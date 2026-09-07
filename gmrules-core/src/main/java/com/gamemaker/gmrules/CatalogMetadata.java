/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized and remain non-null.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/** Presentation and provenance data retained for an imported catalog element. */
public final class CatalogMetadata implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sourceType = "";
    private String sourceTitle = "";
    private String license = "";
    private String rarity = "";
    private int level = -1;
    private boolean remastered = false;
    private ArrayList<String> traits = new ArrayList<>();
    private ArrayList<String> unsupportedRuleTypes = new ArrayList<>();

    public CatalogMetadata() {
    }

    public CatalogMetadata(CatalogMetadata source) {
        CatalogMetadata safe = Objects.requireNonNullElseGet(source, CatalogMetadata::new);
        sourceType = safe.sourceType;
        sourceTitle = safe.sourceTitle;
        license = safe.license;
        rarity = safe.rarity;
        level = safe.level;
        remastered = safe.remastered;
        traits = new ArrayList<>(safe.traits);
        unsupportedRuleTypes = new ArrayList<>(safe.unsupportedRuleTypes);
    }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String value) { sourceType = text(value); }
    public String getSourceTitle() { return sourceTitle; }
    public void setSourceTitle(String value) { sourceTitle = text(value); }
    public String getLicense() { return license; }
    public void setLicense(String value) { license = text(value); }
    public String getRarity() { return rarity; }
    public void setRarity(String value) { rarity = text(value); }
    public int getLevel() { return level; }
    public void setLevel(int value) { level = Math.max(-1, value); }
    public boolean isRemastered() { return remastered; }
    public void setRemastered(boolean value) { remastered = value; }
    public List<String> getTraits() { return List.copyOf(traits); }
    public void setTraits(Collection<String> values) { traits = strings(values); }
    public List<String> getUnsupportedRuleTypes() { return List.copyOf(unsupportedRuleTypes); }
    public void setUnsupportedRuleTypes(Collection<String> values) { unsupportedRuleTypes = strings(values); }

    private static ArrayList<String> strings(Collection<String> values) {
        ArrayList<String> result = new ArrayList<>();
        for (String value : Objects.requireNonNullElse(values, List.<String>of())) {
            String safe = text(value);
            if (!safe.isEmpty() && !result.contains(safe)) {
                result.add(safe);
            }
        }
        return result;
    }

    private static String text(String value) {
        return Objects.toString(value, "").trim();
    }
}
