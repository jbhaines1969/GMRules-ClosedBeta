/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ArrayHandler implements Serializable {

    // *** MEMBERS ***
    private static final long serialVersionUID = 1L;
    private Map<String, ArrayList<?>> arrayRegistry = new HashMap<>();

    // *** CONSTRUCTORS ***
    public ArrayHandler() {
    }

    @SuppressWarnings("unchecked")
    public ArrayHandler(Map<String, ? extends ArrayList<?>> arrayRegistry) {
        this.arrayRegistry = (Map<String, ArrayList<?>>) Objects.requireNonNullElseGet(arrayRegistry, HashMap::new);
    }

    // *** METHODS ***
    /**
     * Get array by name with type safety
     * @param arrayName the name of the array to retrieve
     * @return the array, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T> ArrayList<T> getArray(String arrayName) {
        ArrayList<?> array = arrayRegistry.get(arrayName);
        if (array == null) {
            return null;
        }
        if (array.isEmpty()) {
            return (ArrayList<T>) array;
        }
        Object first = array.get(0);
        if (first instanceof GameElement) {
            ArrayList<String> names = new ArrayList<>();
            for (Object item : array) {
                if (item instanceof GameElement) {
                    names.add(((GameElement) item).getName());
                }
            }
            return (ArrayList<T>) names;
        }
        return (ArrayList<T>) array;
    }

    /**
     * Get the underlying array by name (object list)
     * @param arrayName the name of the array to retrieve
     * @return the array, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T> ArrayList<T> getObjectArray(String arrayName) {
        ArrayList<?> array = arrayRegistry.get(arrayName);
        if (array == null) {
            return null;
        }
        return (ArrayList<T>) array;
    }

    public void putArray(String arrayName, ArrayList<?> array) {
        arrayRegistry.put(arrayName, Objects.requireNonNullElseGet(array, ArrayList::new));
    }

    public java.util.Set<String> getArrayNames() {
        return java.util.Collections.unmodifiableSet(arrayRegistry.keySet());
    }

    /**
     * Add element to specified array
     * Prevents duplicate names and IDs - checks if an element with the same name or ID already exists
     * @param arrayName the name of the array
     * @param element the element to add
     * @return true if element was added, false if duplicate name/ID found or element is null
     */
    @SuppressWarnings("unchecked")
    public <T> boolean addElement(String arrayName, T element) {
        if (!arrayRegistry.containsKey(arrayName)) {
            return false;
        }
        if (element == null) {
            return false;
        }
        ArrayList<T> array = getObjectArray(arrayName);
        if (element instanceof GameElement) {
            GameElement safeElement = (GameElement) element;
            if (hasElementWithName(arrayName, safeElement.getName())) {
                return false; // Duplicate name found, don't add
            }
            if (getElement(arrayName, safeElement.getId()) != null) {
                return false; // Duplicate ID found, don't add
            }
            array.add(element);
            return true;
        }
        if (array.contains(element)) {
            return false;
        }
        array.add(element);
        return true;
    }

    /**
     * Remove element from specified array
     * @param arrayName the name of the array
     * @param element the element to remove
     * @return true if removed, false if not found
     */
    @SuppressWarnings("unchecked")
    public <T> boolean removeElement(String arrayName, T element) {
        if (!arrayRegistry.containsKey(arrayName)) {
            return false;
        }
        if (element == null) {
            return false;
        }
        ArrayList<T> array = getObjectArray(arrayName);
        return array.remove(element);
    }

    /**
     * Adds multiple elements to the specified array in a single operation.
     * Validates each element and skips duplicates (by name or ID).
     * Useful for importing data from external sources like CSV files.
     *
     * @param arrayName the name of the array
     * @param elements collection of elements to add
     * @return number of elements successfully added (excludes duplicates)
     */
    public <T> int addAllElements(String arrayName, Collection<T> elements) {
        if (!arrayRegistry.containsKey(arrayName)) {
            return 0;
        }
        Collection<T> safeElements = Objects.requireNonNullElse(elements, Collections.emptyList());
        if (safeElements.isEmpty()) {
            return 0;
        }

        int addedCount = 0;
        for (T element : safeElements) {
            if (addElement(arrayName, element)) {
                addedCount++;
            }
        }

        return addedCount;
    }

    /**
     * Removes multiple elements from the specified array in a single operation.
     *
     * @param arrayName the name of the array
     * @param elements collection of elements to remove
     * @return number of elements successfully removed
     */
    @SuppressWarnings("unchecked")
    public <T> int removeAllElements(String arrayName, Collection<T> elements) {
        if (!arrayRegistry.containsKey(arrayName)) {
            return 0;
        }
        Collection<T> safeElements = Objects.requireNonNullElse(elements, Collections.emptyList());
        if (safeElements.isEmpty()) {
            return 0;
        }

        ArrayList<T> array = getObjectArray(arrayName);
        int beforeSize = array.size();
        array.removeAll(safeElements);

        int removedCount = beforeSize - array.size();
        return removedCount;
    }

    /**
     * Replaces the entire array with new elements.
     * Useful for CSV imports that define complete datasets.
     *
     * @param arrayName the name of the array
     * @param newElements new collection of elements
     */
    @SuppressWarnings("unchecked")
    public <T> void replaceArray(String arrayName, Collection<T> newElements) {
        if (!arrayRegistry.containsKey(arrayName)) {
            return;
        }
        ArrayList<T> array = getObjectArray(arrayName);
        array.clear();

        Collection<T> safeElements = Objects.requireNonNullElse(newElements, Collections.emptyList());
        if (!safeElements.isEmpty()) {
            array.addAll(safeElements);
        }

    }

    /**
     * Get single element by ID from specified array
     * @param arrayName the name of the array
     * @param id the ID of the element to find
     * @return the element or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T extends GameElement> T getElement(String arrayName, String id) {
        String safeId = Objects.toString(id, "");
        if (!arrayRegistry.containsKey(arrayName)) {
            return null;
        }
        ArrayList<T> array = getObjectArray(arrayName);
        return array.stream()
                .filter(element -> safeId.equals(element.getId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get an element by its name from the specified array
     * @param arrayName the name of the array
     * @param name the name to search for (case-insensitive)
     * @return the element with matching name, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T extends GameElement> T getElementByName(String arrayName, String name) {
        String safeName = Objects.toString(name, "").trim();
        if (safeName.isEmpty()) {
            return null;
        }
        if (!arrayRegistry.containsKey(arrayName)) {
            return null;
        }
        ArrayList<T> array = getObjectArray(arrayName);
        return array.stream()
                .filter(element -> safeName.equalsIgnoreCase(element.getName().trim()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Check if an element with the given name already exists in the specified array
     * @param arrayName the name of the array
     * @param name the name to check for (case-insensitive)
     * @return true if an element with this name exists, false otherwise
     */
    @SuppressWarnings("unchecked")
    public <T extends GameElement> boolean hasElementWithName(String arrayName, String name) {
        String safeName = Objects.toString(name, "").trim();
        if (safeName.isEmpty()) {
            return false;
        }
        if (!arrayRegistry.containsKey(arrayName)) {
            return false;
        }
        ArrayList<T> array = getObjectArray(arrayName);
        return array.stream()
                .anyMatch(element -> safeName.equalsIgnoreCase(element.getName().trim()));
    }

    /**
     * Clear all elements from specified array
     * @param arrayName the name of the array to clear
     */
    public void clearArray(String arrayName) {
        ArrayList<?> array = arrayRegistry.get(arrayName);
        if (array == null) {
            return;
        }
        array.clear();
    }

}
