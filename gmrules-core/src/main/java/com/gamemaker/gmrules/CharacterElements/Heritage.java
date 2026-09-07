/*
 FILE CONTRACT (Non-Null): all fields remain non-null.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.CharacterElements;

import com.gamemaker.gmrules.GameElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** A character heritage, distinct from ancestry/Race. */
public class Heritage extends GameElement {
    private static final long serialVersionUID = 1L;

    private boolean unrestrictedAncestry = false;
    private ArrayList<String> ancestryIds = new ArrayList<>();
    private ArrayList<String> grantedSkillIds = new ArrayList<>();

    public Heritage(String name) { super(name); }
    public Heritage(String name, String description) { super(name, description); }

    public boolean isUnrestrictedAncestry() { return unrestrictedAncestry; }
    public void setUnrestrictedAncestry(boolean value) { unrestrictedAncestry = value; }
    public List<String> getAncestryIds() { return List.copyOf(ancestryIds); }
    public void setAncestryIds(Collection<String> values) { ancestryIds = ids(values); }
    public List<String> getGrantedSkillIds() { return List.copyOf(grantedSkillIds); }
    public void setGrantedSkillIds(Collection<String> values) { grantedSkillIds = ids(values); }

    public int cleanupOrphanedReferences(Set<String> validRaceIds, Set<String> validSkillIds) {
        int before = ancestryIds.size() + grantedSkillIds.size();
        ancestryIds.removeIf(id -> !validRaceIds.contains(id));
        grantedSkillIds.removeIf(id -> !validSkillIds.contains(id));
        return before - ancestryIds.size() - grantedSkillIds.size();
    }

    private static ArrayList<String> ids(Collection<String> values) {
        ArrayList<String> result = new ArrayList<>();
        for (String value : Objects.requireNonNullElse(values, List.<String>of())) {
            String id = Objects.toString(value, "").trim();
            if (!id.isEmpty() && !result.contains(id)) result.add(id);
        }
        return result;
    }
}
