/*
 FILE CONTRACT (Non-Null): all fields remain non-null.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.GameElements;

import com.gamemaker.gmrules.GameElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/** A discrete action exposed by a Game; execution remains a core responsibility. */
public class Action extends GameElement {
    private static final long serialVersionUID = 1L;

    private String actionType = "";
    private String category = "";
    private int actionCost = 0;
    private String frequency = "";
    private ArrayList<String> affectedMechanicKeys = new ArrayList<>();

    public Action(String name) { super(name); }
    public Action(String name, String description) { super(name, description); }

    public String getActionType() { return actionType; }
    public void setActionType(String value) { actionType = text(value); }
    public String getCategory() { return category; }
    public void setCategory(String value) { category = text(value); }
    public int getActionCost() { return actionCost; }
    public void setActionCost(int value) { actionCost = Math.max(0, value); }
    public String getFrequency() { return frequency; }
    public void setFrequency(String value) { frequency = text(value); }
    public List<String> getAffectedMechanicKeys() { return List.copyOf(affectedMechanicKeys); }
    public void setAffectedMechanicKeys(Collection<String> values) {
        affectedMechanicKeys = new ArrayList<>();
        for (String value : Objects.requireNonNullElse(values, List.<String>of())) {
            String safe = text(value);
            if (!safe.isEmpty() && !affectedMechanicKeys.contains(safe)) affectedMechanicKeys.add(safe);
        }
    }

    private static String text(String value) { return Objects.toString(value, "").trim(); }
}
