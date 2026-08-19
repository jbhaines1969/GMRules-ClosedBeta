package com.gamemaker.gmrules.combatpoc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameIO;
import com.gamemaker.gmrules.GameMechanics.AttackMethod;
import com.gamemaker.gmrules.GameMechanics.AttackResolution;
import com.gamemaker.gmrules.GameMechanics.DefenseMethod;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SingleRoundCombatConsumerTest {

    @TempDir
    Path tempDirectory;

    @Test
    void loadsRulesetAndRunsSuccessfulRoundThroughCore() throws Exception {
        Path rulesetFile = tempDirectory.resolve("combat.gmrf");
        writeRuleset(rulesetFile, supportedGame(2, 2, false));

        Game loaded = new GameIO().readGame(rulesetFile);
        AttackResolution.AttackResult round = new SingleRoundCombatConsumer().run(loaded);

        assertEquals("", CombatRulesSupport.unsupportedReason(loaded));
        assertEquals(java.util.List.of(java.util.List.of(1)), round.attack().rolls());
        assertEquals(3, round.attack().requireValue());
        assertEquals(2, round.defense().requireValue());
        assertEquals(1, round.resolution().margin().orElseThrow());
        assertEquals(
            AttackResolution.AttackSuccess.SUCCEEDED,
            round.resolution().attackSuccess()
        );
    }

    @Test
    void consumerHandlesFailedEqualityThroughConfiguredResolution() {
        Game game = supportedGame(1, 2, false);

        AttackResolution.AttackResult round = new SingleRoundCombatConsumer().run(game);

        assertEquals(2, round.attack().requireValue());
        assertEquals(
            AttackResolution.AttackSuccess.FAILED,
            round.resolution().attackSuccess()
        );
    }

    @Test
    void coreEntryPointOwnsGenerationAndResolutionSectionSelection() {
        Game game = supportedGame(2, 8, false);
        game.getAttackMethod().setDieSides(20);

        AttackResolution.AttackResult result = game.getAttackResolution().getAttackResult(
            dieSides -> 7,
            dieSides -> 1
        );

        assertEquals(java.util.List.of(java.util.List.of(7)), result.attack().rolls());
        assertEquals(9, result.attack().requireValue());
        assertEquals(8, result.defense().requireValue());
        assertEquals(AttackResolution.AttackSuccess.SUCCEEDED, result.attackSuccess());
    }

    @Test
    void supportBoundaryRejectsAttackPoolsUntilTheirResolutionSectionExists() {
        Game game = supportedGame(0, 2, false);
        game.getAttackMethod().setNumberOfDiceRolled(3);

        String reason = CombatRulesSupport.unsupportedReason(game);

        assertFalse(reason.isBlank());
        assertEquals("This revision supports exactly one attack die rolled once.", reason);
    }

    private static Game supportedGame(
        int modifier,
        int passiveDefense,
        boolean attackerWinsTies
    ) {
        Game game = new Game("Combat PoC Rules");
        AttackMethod attack = game.getAttackMethod();
        attack.setDiceRolled(true);
        attack.setStandardNumberOfDice(true);
        attack.setNumberOfRolls(1);
        attack.setDieSides(1);
        attack.setNumberOfDiceRolled(1);
        attack.setSingleRollModifier(modifier);

        DefenseMethod defense = game.getDefenseMethod();
        defense.setDefenseMode(DefenseMethod.MODE_PASSIVE_VALUE);
        defense.setStandardDefenseValue(true);
        defense.setPassiveDefenseValue(passiveDefense);

        AttackResolution resolution = game.getAttackResolution();
        resolution.setResolutionMode(AttackResolution.MODE_ATTACK_VS_PASSIVE);
        resolution.setAttackRollDirection(AttackResolution.ROLL_DIRECTION_OVER);
        resolution.setAttackerWinsTies(attackerWinsTies);
        resolution.setTargetValueIsDefenseValue(true);
        return game;
    }

    private static void writeRuleset(Path file, Game game) throws Exception {
        try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(file))) {
            output.writeObject(game);
        }
    }
}
