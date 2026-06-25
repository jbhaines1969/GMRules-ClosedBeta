/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.UI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Sidebar navigation for visited stages.
 */
public class StageSidebar extends JPanel {

    // *** MEMBERS ***
    private static final int SIDEBAR_WIDTH = 220;
    private static final int BUTTON_GAP = 6;

    private final JLabel titleLabel = new JLabel();
    private final JPanel buttonPanel = new JPanel();
    private final MainStage mainStage;
    private final List<StageId> stages = new ArrayList<>();
    private StageId currentStage = StageId.SPLASH;

    // *** CONSTRUCTORS ***
    public StageSidebar(MainStage mainStage) {
        this.mainStage = Objects.requireNonNullElseGet(mainStage, MainStage::getInstance);
        configureLayout();
        refreshLabels();
    }

    // *** METHODS ***
    public void updateStages(List<StageId> stageIds, StageId currentStage) {
        List<StageId> safeStages = Objects.requireNonNullElseGet(stageIds, List::of);
        stages.clear();
        stages.addAll(safeStages);
        this.currentStage = Objects.requireNonNullElse(currentStage, StageId.SPLASH);
        rebuildButtons();
    }

    public void refreshLabels() {
        titleLabel.setText(Localization.get("common.stages"));
        rebuildButtons();
    }

    private void configureLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(24, 16, 24, 16));
        setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));

        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(titleLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    private void rebuildButtons() {
        buttonPanel.removeAll();
        for (StageId stageId : stages) {
            JButton button = buildStageButton(stageId);
            buttonPanel.add(button);
            buttonPanel.add(Box.createVerticalStrut(BUTTON_GAP));
        }
        revalidate();
        repaint();
    }

    private JButton buildStageButton(StageId stageId) {
        JButton button = new JButton(mainStage.resolveStageLabel(stageId));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setEnabled(stageId != currentStage);
        button.addActionListener(event -> mainStage.navigateToStage(stageId));
        return button;
    }
}
