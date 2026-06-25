/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Registry for GameElement instances keyed by id with name-based lookups.
 */
public class ElementRegistry<T extends GameElement> implements Serializable {

// *** MEMBERS ***
    private static final long serialVersionUID = 1L;
    private ArrayList<T> items = new ArrayList<>();
    private Map<String, T> byId = new LinkedHashMap<>();
    private Map<String, T> byName = new LinkedHashMap<>();

// *** CONSTRUCTORS ***
    public ElementRegistry() {
    }

// *** METHODS ***
    public boolean add(T element) {
        if (element == null) {
            return false;
        }
        String id = Objects.toString(element.getId(), "");
        if (id.isEmpty() || byId.containsKey(id)) {
            return false;
        }
        String nameKey = normalizeName(element.getName());
        if (byName.containsKey(nameKey)) {
            return false;
        }
        items.add(element);
        byId.put(id, element);
        byName.put(nameKey, element);
        return true;
    }

    public int addAll(Collection<T> elements) {
        Collection<T> safeElements = Objects.requireNonNullElse(elements, List.of());
        int added = 0;
        for (T element : safeElements) {
            if (add(element)) {
                added++;
            }
        }
        return added;
    }

    public boolean remove(T element) {
        if (element == null) {
            return false;
        }
        String id = Objects.toString(element.getId(), "");
        T existing = byId.remove(id);
        if (existing == null) {
            return false;
        }
        items.remove(existing);
        byName.remove(normalizeName(existing.getName()));
        return true;
    }

    public boolean removeById(String id) {
        String safeId = Objects.toString(id, "");
        T existing = byId.remove(safeId);
        if (existing == null) {
            return false;
        }
        items.remove(existing);
        byName.remove(normalizeName(existing.getName()));
        return true;
    }

    public void clear() {
        items.clear();
        byId.clear();
        byName.clear();
    }

    public void replaceAll(Collection<T> elements) {
        clear();
        addAll(elements);
    }

    public T getById(String id) {
        String safeId = Objects.toString(id, "");
        return byId.get(safeId);
    }

    public T getByName(String name) {
        String nameKey = normalizeName(name);
        return byName.get(nameKey);
    }

    public boolean hasName(String name) {
        return byName.containsKey(normalizeName(name));
    }

    public List<T> getAll() {
        return new ArrayList<>(items);
    }

    public List<T> getAllReadOnly() {
        return java.util.Collections.unmodifiableList(items);
    }

    ArrayList<T> getMutableItems() {
        return items;
    }

    private String normalizeName(String name) {
        return Objects.toString(name, "").trim().toLowerCase();
    }
}
