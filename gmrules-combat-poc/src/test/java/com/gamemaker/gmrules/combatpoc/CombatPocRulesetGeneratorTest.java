package com.gamemaker.gmrules.combatpoc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameIO;
import com.gamemaker.gmrules.GameMechanics.AttackResolution;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CombatPocRulesetGeneratorTest {

    @TempDir
    Path tempDirectory;

    @Test
    void generatesTwoUsableRulesetsForEveryCurrentComparisonCombination() throws Exception {
        CombatPocRulesetGenerator generator = new CombatPocRulesetGenerator();
        List<Path> generatedFiles = generator.generate(tempDirectory.resolve("examples"));
        Map<String, Integer> combinationCounts = new HashMap<>();
        Map<String, Set<String>> combinationValues = new HashMap<>();
        GameIO gameIO = new GameIO();

        assertEquals(8, generatedFiles.size());
        assertEquals(8, generatedFiles.stream().distinct().count());

        for (Path generatedFile : generatedFiles) {
            Game game = gameIO.readGame(generatedFile);
            AttackResolution resolution = game.getAttackResolution();

            assertEquals(
                generatedFile.getFileName().toString().replace(".gmrf", ""),
                game.getName()
            );
            assertEquals(CombatPocRulesetGenerator.DICE_SELECTION, game.getDiceUsed());
            assertEquals("", CombatRulesSupport.unsupportedReason(game));
            assertEquals(AttackResolution.MODE_ATTACK_VS_PASSIVE, resolution.getResolutionMode());
            assertTrue(resolution.isTargetValueDefenseValue());

            String combination = resolution.getAttackRollDirection()
                + ":"
                + resolution.isAttackerWinsTies();
            combinationCounts.merge(combination, 1, Integer::sum);
            combinationValues.computeIfAbsent(combination, ignored -> new HashSet<>()).add(
                game.getAttackMethod().getDieSides()
                    + ":"
                    + game.getAttackMethod().getSingleRollModifier()
                    + ":"
                    + game.getDefenseMethod().getPassiveDefenseValue()
            );
        }

        assertEquals(Map.of(
            AttackResolution.ROLL_DIRECTION_OVER + ":false", 2,
            AttackResolution.ROLL_DIRECTION_OVER + ":true", 2,
            AttackResolution.ROLL_DIRECTION_UNDER + ":false", 2,
            AttackResolution.ROLL_DIRECTION_UNDER + ":true", 2
        ), combinationCounts);
        assertTrue(combinationValues.values().stream().allMatch(values -> values.size() == 2));
    }

    @Test
    void refusesToOverwriteExistingRulesets() throws Exception {
        CombatPocRulesetGenerator generator = new CombatPocRulesetGenerator();
        Path outputDirectory = tempDirectory.resolve("examples");
        generator.generate(outputDirectory);

        assertThrows(FileAlreadyExistsException.class, () -> generator.generate(outputDirectory));
    }
}
