/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - Empty strings and Optional represent absent UI state.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.combatpoc;

import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameIO;
import com.gamemaker.gmrules.GameMechanics.AttackMethod;
import com.gamemaker.gmrules.GameMechanics.AttackResolution;
import com.gamemaker.gmrules.GameMechanics.DefenseMethod;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

/** Minimal standalone shell for loading a ruleset and running one combat round. */
public final class CombatAutomatorFrame extends JFrame {

    private final GameIO gameIO = new GameIO();
    private final SingleRoundCombatConsumer consumer = new SingleRoundCombatConsumer();
    private final JLabel fileValue = new JLabel("No ruleset loaded");
    private final JLabel gameValue = new JLabel("—");
    private final JLabel attackValue = new JLabel("—");
    private final JLabel defenseValue = new JLabel("—");
    private final JLabel resolutionValue = new JLabel("—");
    private final JLabel supportValue = new JLabel("Choose a .gmrf ruleset file.");
    private final JButton runButton = new JButton("Run Combat Round");
    private final JTextArea resultArea = new JTextArea();
    private Optional<Game> loadedGame = Optional.empty();

    public CombatAutomatorFrame(Optional<Path> initialFile) {
        super("GMRules Combat Automator PoC");
        buildUi();
        initialFile.ifPresent(this::loadRuleset);
    }

    private void buildUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(680, 520));
        setLocationByPlatform(true);

        JLabel title = new JLabel("Single-Round Combat Automator", SwingConstants.LEFT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        JButton loadButton = new JButton("Open Ruleset…");
        loadButton.addActionListener(event -> chooseRuleset());
        runButton.setEnabled(false);
        runButton.addActionListener(event -> runCombatRound());

        JPanel heading = new JPanel(new BorderLayout(12, 12));
        heading.add(title, BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.add(loadButton);
        actions.add(runButton);
        heading.add(actions, BorderLayout.EAST);

        JPanel summary = new JPanel(new GridLayout(0, 2, 12, 8));
        addSummaryRow(summary, "File", fileValue);
        addSummaryRow(summary, "Game", gameValue);
        addSummaryRow(summary, "Attack Method", attackValue);
        addSummaryRow(summary, "Defense", defenseValue);
        addSummaryRow(summary, "Resolution", resolutionValue);
        addSummaryRow(summary, "PoC Support", supportValue);

        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        resultArea.setText("Load a ruleset, then run one combat round.");
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel content = new JPanel(new BorderLayout(16, 16));
        content.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        content.add(heading, BorderLayout.NORTH);
        content.add(summary, BorderLayout.CENTER);

        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setPreferredSize(new Dimension(640, 220));
        content.add(resultScroll, BorderLayout.SOUTH);
        setContentPane(content);
        pack();
    }

    private static void addSummaryRow(JPanel panel, String name, JLabel value) {
        JLabel label = new JLabel(name + ":");
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        panel.add(label);
        panel.add(value);
    }

    private void chooseRuleset() {
        JFileChooser chooser = new JFileChooser(defaultRulesetDirectory());
        chooser.setDialogTitle("Open GMRules Ruleset");
        chooser.setFileFilter(new FileNameExtensionFilter("GMRules ruleset (*.gmrf)", "gmrf"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            loadRuleset(chooser.getSelectedFile().toPath());
        }
    }

    private static File defaultRulesetDirectory() {
        Path exampleDirectory = CombatPocRulesetGenerator.DEFAULT_OUTPUT_DIRECTORY
            .toAbsolutePath()
            .normalize();
        if (Files.isDirectory(exampleDirectory)) {
            return exampleDirectory.toFile();
        }

        Path gamesDirectory = Path.of("games").toAbsolutePath().normalize();
        return Files.isDirectory(gamesDirectory)
            ? gamesDirectory.toFile()
            : Path.of("").toAbsolutePath().normalize().toFile();
    }

    private void loadRuleset(Path path) {
        try {
            Game game = gameIO.readGame(path);
            loadedGame = Optional.of(game);
            showRuleset(path.toFile(), game);
        } catch (Exception exception) {
            loadedGame = Optional.empty();
            runButton.setEnabled(false);
            resultArea.setText("Could not load ruleset:\n" + exception.getMessage());
            JOptionPane.showMessageDialog(
                this,
                exception.getMessage(),
                "Could Not Open Ruleset",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showRuleset(File file, Game game) {
        AttackMethod attack = game.getAttackMethod();
        DefenseMethod defense = game.getDefenseMethod();
        AttackResolution resolution = game.getAttackResolution();
        String modifier = attack.getSingleRollModifier() > 0
            ? "+" + attack.getSingleRollModifier()
            : String.valueOf(attack.getSingleRollModifier());
        String direction = AttackResolution.ROLL_DIRECTION_UNDER.equals(
            resolution.getAttackRollDirection()
        ) ? "roll under" : "roll over";
        String equality = resolution.isAttackerWinsTies()
            ? "attacker wins ties"
            : "defender wins ties";
        boolean usesAttackPool = attack.getNumberOfDiceRolled() > 1;
        String poolSummary = switch (resolution.getAttackPoolResolutionMethod()) {
            case AttackResolution.POOL_RESOLUTION_HIGHEST_DIE -> "; use highest die";
            case AttackResolution.POOL_RESOLUTION_LOWEST_DIE -> "; use lowest die";
            case AttackResolution.POOL_RESOLUTION_SUM -> "; sum all dice";
            default -> "; successes on " + resolution.getAttackPoolSuccessThreshold()
                + (AttackResolution.ROLL_DIRECTION_UNDER.equals(
                    resolution.getAttackRollDirection()
                ) ? "-" : "+");
        };

        fileValue.setText(file.getAbsolutePath());
        gameValue.setText(game.getName().isBlank() ? "Unnamed ruleset" : game.getName());
        attackValue.setText(
            attack.getNumberOfRolls() + " × "
                + attack.getNumberOfDiceRolled() + "d" + attack.getDieSides()
                + (usesAttackPool
                    ? poolSummary
                    : " " + modifier)
        );
        defenseValue.setText(
            defense.usesPassiveValue()
                ? "Passive " + defense.getPassiveDefenseValue()
                : defense.getDefenseMode()
        );
        resolutionValue.setText(
            usesAttackPool
                ? AttackResolution.POOL_RESOLUTION_SUCCESS_COUNT.equals(
                    resolution.getAttackPoolResolutionMethod()
                )
                    ? "success count versus passive Defense; " + equality
                    : direction + " pool value versus passive Defense; " + equality
                : direction + "; " + equality
        );

        String unsupportedReason = CombatRulesSupport.unsupportedReason(game);
        boolean supported = unsupportedReason.isEmpty();
        supportValue.setText(supported ? "Ready" : unsupportedReason);
        runButton.setEnabled(supported);
        resultArea.setText(
            supported
                ? "Ruleset loaded. Click Run Combat Round."
                : "This ruleset loaded successfully, but this PoC revision cannot run it yet.\n\n"
                    + unsupportedReason
        );
    }

    private void runCombatRound() {
        try {
            Game game = loadedGame.orElseThrow();
            AttackResolution.AttackResult round = consumer.run(game);
            AttackResolution.AttackSuccess success = round.attackSuccess();
            boolean usedAttackPool = round.attack().rolls().size() == 1
                && round.attack().rolls().get(0).size() > 1;
            boolean usedSuccessCountPool = usedAttackPool
                && AttackResolution.POOL_RESOLUTION_SUCCESS_COUNT.equals(
                    game.getAttackResolution().getAttackPoolResolutionMethod()
                );
            resultArea.setText(
                "RESULT: " + resultLabel(success) + "\n\n"
                    + "Raw attack roll: " + round.attack().rolls() + "\n"
                    + (usedSuccessCountPool ? "Attack successes: " : "Attack value:    ")
                    + round.attack().requireValue() + "\n"
                    + "Defense value:   " + round.defense().requireValue() + "\n"
                    + "Margin:          " + round.resolution().margin().orElse(0) + "\n"
                    + "Core result:     " + success
            );
        } catch (RuntimeException exception) {
            resultArea.setText("Combat round could not be resolved:\n" + exception.getMessage());
        }
    }

    private static String resultLabel(AttackResolution.AttackSuccess success) {
        return switch (success) {
            case SUCCEEDED -> "SUCCESS";
            case FAILED -> "FAIL";
            case INDETERMINATE -> "INDETERMINATE";
        };
    }
}
