/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - Missing command-line input is represented by Optional.empty().
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.combatpoc;

import java.nio.file.Path;
import java.util.Optional;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Standalone entry point for the combat automator proof of concept. */
public final class CombatAutomatorMain {

    private CombatAutomatorMain() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && "--generate-examples".equals(args[0])) {
            Path outputDirectory = args.length > 1
                ? Path.of(args[1])
                : CombatPocRulesetGenerator.DEFAULT_OUTPUT_DIRECTORY;
            for (Path generatedFile : new CombatPocRulesetGenerator().generate(outputDirectory)) {
                System.out.println(generatedFile);
            }
            return;
        }

        Optional<Path> initialFile = args.length == 0
            ? Optional.empty()
            : Optional.of(Path.of(args[0]).toAbsolutePath().normalize());
        SwingUtilities.invokeLater(() -> {
            useSystemLookAndFeel();
            new CombatAutomatorFrame(initialFile).setVisible(true);
        });
    }

    private static void useSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Swing's cross-platform look and feel remains usable.
        }
    }
}
