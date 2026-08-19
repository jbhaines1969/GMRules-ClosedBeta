/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All generated rulesets and paths are non-null.
 - Existing ruleset files are never overwritten.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.combatpoc;

import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameMechanics.AttackMethod;
import com.gamemaker.gmrules.GameMechanics.AttackResolution;
import com.gamemaker.gmrules.GameMechanics.DefenseMethod;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/** Generates the compact ruleset matrix used to demonstrate the current combat PoC. */
public final class CombatPocRulesetGenerator {

    public static final List<Integer> DICE_SELECTION = List.of(4, 6, 8, 10, 20, 100);
    public static final Path DEFAULT_OUTPUT_DIRECTORY = Path.of("games", "combat-poc-examples");

    private static final List<ExampleSpec> EXAMPLES = List.of(
        new ExampleSpec("NoMod_D20_PassiveDefense10_Over_DefTies", 20, 0, 10, false, false),
        new ExampleSpec("Plus2_D8_PassiveDefense7_Over_DefTies", 8, 2, 7, false, false),
        new ExampleSpec("Minus1_D10_PassiveDefense6_Over_AttTies", 10, -1, 6, false, true),
        new ExampleSpec("Plus3_D6_PassiveDefense5_Over_AttTies", 6, 3, 5, false, true),
        new ExampleSpec("NoMod_D100_PassiveDefense50_Under_DefTies", 100, 0, 50, true, false),
        new ExampleSpec("Minus2_D20_PassiveDefense12_Under_DefTies", 20, -2, 12, true, false),
        new ExampleSpec("Plus1_D4_PassiveDefense3_Under_AttTies", 4, 1, 3, true, true),
        new ExampleSpec("NoMod_D6_PassiveDefense4_Under_AttTies", 6, 0, 4, true, true)
    );

    /**
     * Writes all example rulesets to {@code outputDirectory}.
     *
     * @throws FileAlreadyExistsException before writing when any destination already exists
     */
    public List<Path> generate(Path outputDirectory) throws IOException {
        Path safeOutputDirectory = outputDirectory.toAbsolutePath().normalize();
        Files.createDirectories(safeOutputDirectory);

        for (ExampleSpec example : EXAMPLES) {
            Path destination = destinationFor(safeOutputDirectory, example);
            if (Files.exists(destination)) {
                throw new FileAlreadyExistsException(destination.toString());
            }
        }

        List<Path> generatedFiles = new ArrayList<>();
        for (ExampleSpec example : EXAMPLES) {
            Path destination = destinationFor(safeOutputDirectory, example);
            writeNewRuleset(destination, createGame(example));
            generatedFiles.add(destination);
        }
        return List.copyOf(generatedFiles);
    }

    public List<String> exampleNames() {
        return EXAMPLES.stream().map(ExampleSpec::name).toList();
    }

    private static Path destinationFor(Path outputDirectory, ExampleSpec example) {
        return outputDirectory.resolve(example.name() + ".gmrf");
    }

    private static Game createGame(ExampleSpec example) {
        Game game = new Game(example.name());
        game.setDiceUsed(DICE_SELECTION);

        AttackMethod attack = game.getAttackMethod();
        attack.setDiceRolled(true);
        attack.setStandardNumberOfDice(true);
        attack.setNumberOfRolls(1);
        attack.setDieSides(example.dieSides());
        attack.setNumberOfDiceRolled(1);
        attack.setSingleRollModifier(example.modifier());

        DefenseMethod defense = game.getDefenseMethod();
        defense.setDefenseMode(DefenseMethod.MODE_PASSIVE_VALUE);
        defense.setStandardDefenseValue(true);
        defense.setPassiveDefenseValue(example.passiveDefense());

        AttackResolution resolution = game.getAttackResolution();
        resolution.setResolutionMode(AttackResolution.MODE_ATTACK_VS_PASSIVE);
        resolution.setAttackRollDirection(
            example.rollUnder()
                ? AttackResolution.ROLL_DIRECTION_UNDER
                : AttackResolution.ROLL_DIRECTION_OVER
        );
        resolution.setAttackerWinsTies(example.attackerWinsTies());
        resolution.setTargetValueIsDefenseValue(true);
        game.updateLastModified();
        return game;
    }

    private static void writeNewRuleset(Path destination, Game game) throws IOException {
        try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(
            destination,
            StandardOpenOption.CREATE_NEW,
            StandardOpenOption.WRITE
        ))) {
            output.writeObject(game);
        }
    }

    private record ExampleSpec(
        String name,
        int dieSides,
        int modifier,
        int passiveDefense,
        boolean rollUnder,
        boolean attackerWinsTies
    ) {
    }
}
