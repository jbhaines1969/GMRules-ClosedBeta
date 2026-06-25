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
import java.awt.Dimension;
import java.awt.Toolkit;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.gamemaker.gmrules.Game;

/**
 * Base window for main UI screens.
 */
public class MainStage extends JFrame {

    // *** MEMBERS ***
    private static final double SCREEN_SCALE = 0.8;
    private static final MainStage INSTANCE = new MainStage();
    private final JPanel rootPanel = new JPanel(new BorderLayout());
    private final JPanel contentPanel = new JPanel(new BorderLayout());
    private final StageSidebar sidebar = new StageSidebar(this);
    private final Set<StageId> visitedStages = EnumSet.noneOf(StageId.class);
    private final List<StageId> navigationHistory = new ArrayList<>();
    private StageId currentStage = StageId.SPLASH;
    private Game currentGame = new Game("");
    private String currentSavePath = "";
    private boolean historyLocked = false;

    // *** CONSTRUCTORS ***
    private MainStage() {
        super(Localization.get("app.title"));
        rootPanel.add(sidebar, BorderLayout.WEST);
        rootPanel.add(contentPanel, BorderLayout.CENTER);
        applyStandardBounds();
        updateSidebar();
        setContentPane(rootPanel);
    }

    // *** METHODS ***
    public static MainStage getInstance() {
        return INSTANCE;
    }

    public final void applyStandardBounds() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) Math.round(screenSize.getWidth() * SCREEN_SCALE);
        int height = (int) Math.round(screenSize.getHeight() * SCREEN_SCALE);
        setSize(width, height);
        setLocationRelativeTo(null);
    }

    public void showContent(JComponent content) {
        JComponent safeContent = Objects.requireNonNullElseGet(content, JPanel::new);
        applyStageContext(safeContent);
        contentPanel.removeAll();
        contentPanel.add(safeContent, BorderLayout.CENTER);
        if (getContentPane() != rootPanel) {
            setContentPane(rootPanel);
        }
        revalidate();
        repaint();
    }

    public void navigateToStage(StageId stageId) {
        StageId safeStageId = Objects.requireNonNullElse(stageId, StageId.SPLASH);
        JComponent nextStage = createStage(safeStageId, currentGame);
        showContent(nextStage);
    }

    public void navigateBack() {
        if (navigationHistory.isEmpty()) {
            navigateToStage(StageId.SPLASH);
            return;
        }
        StageId previousStage = navigationHistory.remove(navigationHistory.size() - 1);
        historyLocked = true;
        navigateToStage(previousStage);
        historyLocked = false;
    }

    public void refreshSidebar() {
        sidebar.refreshLabels();
    }

    public String resolveStageLabel(StageId stageId) {
        StageId safeStageId = Objects.requireNonNullElse(stageId, StageId.SPLASH);
        String customLabel = currentGame.getSystemName(safeStageId.getSystemNameKey());
        if (!customLabel.isEmpty()) {
            return customLabel;
        }
        return Localization.get(safeStageId.getLabelKey());
    }

    public void setCurrentSavePath(Path path) {
        currentSavePath = Objects.toString(path, "").trim();
    }

    public void clearCurrentSavePath() {
        currentSavePath = "";
    }

    public boolean hasCurrentSavePath() {
        return !currentSavePath.isEmpty();
    }

    public Path getCurrentSavePath() {
        return Paths.get(currentSavePath);
    }

    private void applyStageContext(JComponent content) {
        if (content instanceof StageView) {
            StageView stageView = (StageView) content;
            StageId stageId = Objects.requireNonNullElse(stageView.getStageId(), StageId.SPLASH);
            Game stageGame = Objects.requireNonNullElseGet(stageView.getGame(), () -> new Game(""));
            StageId previousStage = currentStage;
            boolean gameChanged = stageGame != currentGame;
            if (gameChanged) {
                currentGame = stageGame;
                loadCompletedStages(stageGame);
                navigationHistory.clear();
            }
            if (!historyLocked && !gameChanged && stageId != previousStage) {
                recordHistory(previousStage);
            }
            currentStage = stageId;
            if (stageId != StageId.SPLASH) {
                stageGame.markStageCompleted(stageId.getStepKey());
                visitedStages.add(stageId);
            }
        } else {
            currentStage = StageId.SPLASH;
            currentGame = new Game("");
            visitedStages.clear();
            currentSavePath = "";
            navigationHistory.clear();
        }
        updateSidebar();
    }

    private void recordHistory(StageId stageId) {
        StageId safeStageId = Objects.requireNonNullElse(stageId, StageId.SPLASH);
        if (!navigationHistory.isEmpty() && navigationHistory.get(navigationHistory.size() - 1) == safeStageId) {
            return;
        }
        navigationHistory.add(safeStageId);
    }

    private void updateSidebar() {
        sidebar.updateStages(buildStageList(), currentStage);
    }

    private void loadCompletedStages(Game game) {
        visitedStages.clear();
        Game safeGame = Objects.requireNonNullElseGet(game, () -> new Game(""));
        for (String stageKey : safeGame.getCompletedStages()) {
            StageId stageId = StageId.fromStepKey(stageKey);
            if (stageId != StageId.SPLASH) {
                visitedStages.add(stageId);
            }
        }
    }

    private List<StageId> buildStageList() {
        List<StageId> orderedStages = new ArrayList<>();
        for (StageId stageId : StageId.values()) {
            if (stageId == StageId.SPLASH) {
                continue;
            }
            if (visitedStages.contains(stageId)) {
                orderedStages.add(stageId);
            }
        }
        return orderedStages;
    }

    private JComponent createStage(StageId stageId, Game game) {
        StageId safeStageId = Objects.requireNonNullElse(stageId, StageId.SPLASH);
        Game safeGame = Objects.requireNonNullElseGet(game, () -> new Game(""));
        switch (safeStageId) {
            case SETUP:
                return new GameSetupStage(this, safeGame);
            case MEASUREMENTS:
                return new MeasurementsStage(this, safeGame);
            case DICE:
                return new DiceChooserStage(this, safeGame);
            case ATTRIBUTE_TYPES:
                return new AttributeTypesStage(this, safeGame);
            case ATTRIBUTES:
                return new AttributesStage(this, safeGame);
            case ATTRIBUTE_GENERATION:
                return new AttributeGenerationStage(this, safeGame);
            case STANDARD_ARRAY:
                return new StandardArrayStage(this, safeGame);
            case DICE_ROLLING:
                return new DiceRollingStage(this, safeGame);
            case POINTS_BUY:
                return new PointsBuyStage(this, safeGame);
            case HIT_POINTS:
                return new HitPointsStage(this, safeGame);
            case ARMOR_CLASS:
                return new ArmorClassStage(this, safeGame);
            case CURRENCY:
                return new CurrencyStage(this, safeGame);
            case EFFECT_TYPES:
                return new EffectTypesStage(this, safeGame);
            case STATUSES:
                return new StatusesStage(this, safeGame);
            case EFFECTS:
                return new EffectsStage(this, safeGame);
            case EQUIPMENT:
                return new EquipmentStage(this, safeGame);
            case WEAPONS:
                return new WeaponsStage(this, safeGame);
            case CLASSES:
                return new ClassesStage(this, safeGame);
            case SKILLS:
                return new SkillsStage(this, safeGame);
            case SPELLS:
                return new SpellsStage(this, safeGame);
            case RACES:
                return new RacesStage(this, safeGame);
            case SPLASH:
            default:
                return new DisplayStage(this);
        }
    }
}
