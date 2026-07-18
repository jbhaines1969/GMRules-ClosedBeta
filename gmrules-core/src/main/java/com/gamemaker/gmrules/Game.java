/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules;

import com.gamemaker.gmrules.AtomicElements.*;
import com.gamemaker.gmrules.CharacterElements.*;
import com.gamemaker.gmrules.GameElements.*;
import com.gamemaker.gmrules.GameMechanics.*;
import com.gamemaker.gmrules.SupportElements.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * The Game class serves as the root container for an entire tabletop RPG system definition.
 * This class holds all the rules, mechanics, character options, and content that define
 * a complete game system. It provides serialization capabilities for exporting to other
 * applications in the GameMaker suite.
 */
public class Game extends GameElement {

    // *** MEMBERS ***
    private static final long serialVersionUID = 1L;
    private static final String GAME_TYPES_KEY = "game.types";
    private static final String GAME_TYPES_SPLIT_REGEX = "\\s*\\|\\s*";

    // Game-specific metadata (inherits id, name, description from GameElement)
    private String gameType = "";                 // Fantasy, Sci-Fi, Modern, Horror, etc.
    private String weightSystem = "metric";
    private String uiLocale = "";
    private ArrayList<String> completedStages = new ArrayList<>();
    private String version = "";
    private String author = "";
    private LocalDateTime created = LocalDateTime.now();
    private LocalDateTime lastModified = LocalDateTime.now();

    // Array Registry for Generic Management - single source of truth
    private Map<String, ArrayList<?>> arrayRegistry = new HashMap<>();
    private Map<String, Boolean> systemConfig = new HashMap<>();
    private Map<String, String> systemNames = new HashMap<>();
    private Map<String, Integer> timeUnits = new LinkedHashMap<>();
    private String startingMoneyMethod = "base";
    private int baseStartingMoney = 0;
    private String startingMoneyCurrencyId = "";
    private Map<String, Integer> classStartingMoney = new LinkedHashMap<>();
    private Map<String, Integer> raceStartingMoneyModifiers = new LinkedHashMap<>();
    private Map<String, Integer> traitStartingMoneyModifiers = new LinkedHashMap<>();
    private AttributeModifiers attributeModifiers = new AttributeModifiers();
    private boolean applyAttributeModifiersToAllAttributes = false;
    private int defaultAttributeMinScore = 0;
    private int defaultAttributeMaxScore = 0;
    private ArrayHandler arrayHandler = new ArrayHandler(arrayRegistry);
    private final ArrayList<DiceRange> customDiceRanges = new ArrayList<>();

    // Rule Methods
    private AttributeGenerationMethod attributeGenerationMethod = new AttributeGenerationMethod("Attribute Generation");
    private ArrayList<AttributeGenerationOption> attributeGenerationOptions = new ArrayList<>();
    private boolean customAttributeGenerationOptions = false;
    private SaveMethod saveMethod = new SaveMethod("Saves");
    private HPMethod hpMethod = new HPMethod("Hit Points");
    private ArmorClassMethod armorClassMethod = new ArmorClassMethod("Armor Class");
    private CombatMethod combatMethod = new CombatMethod("Combat");
    private LevelingMethod levelingMethod = new LevelingMethod("Leveling");
    private Map<RegistryKey<?>, AtomicRegistry> registries = new HashMap<>();
    private Map<ElementRegistryKey<?>, ElementRegistry<? extends GameElement>> elementRegistries = new HashMap<>();
    private Map<String, ElementRegistryKey<?>> elementRegistryByName = new HashMap<>();

    // *** CONSTRUCTORS ***
    public Game() {
        super("");
        initializeRegistries();
        initializeElementRegistries();
        initializeArrayRegistry();
    }

    public Game(String name) {
        super(name);
        initializeRegistries();
        initializeElementRegistries();
        initializeArrayRegistry();
    }

    public Game(String name, String version, String author) {
        super(name); // calls GameElement constructor with name
        this.version = Objects.toString(version, "");
        this.author = Objects.toString(author, "");
        initializeRegistries();
        initializeElementRegistries();
        initializeArrayRegistry();
    }

    public Game(String name, String description, String version, String author, String gameType) {
        super(name); // calls GameElement constructor with name
        setDescription(description);
        this.version = Objects.toString(version, "");
        this.author = Objects.toString(author, "");
        this.gameType = Objects.toString(gameType, "");
        initializeRegistries();
        initializeElementRegistries();
        initializeArrayRegistry();
    }

    public Game(String name, String description, String version, String author) {
        super(name); // calls GameElement constructor with name
        setDescription(description);
        this.version = Objects.toString(version, "");
        this.author = Objects.toString(author, "");
        initializeRegistries();
        initializeElementRegistries();
        initializeArrayRegistry();
    }

    // *** METHODS ***
    // ===== GENERIC ARRAY MANAGEMENT METHODS =====
    private void initializeArrayRegistry() {
        arrayRegistry.clear();
        systemConfig.clear();
        systemNames.clear();

        // Initialize system configuration with defaults (all true)
        systemConfig.put("usesLevels", true);
        systemConfig.put("usesSkillRanks", true);
        systemConfig.put("usesSpellcasting", true);
        systemConfig.put("usesArchetypes", true);
        systemConfig.put("usesMulticlassing", true);
        systemConfig.put("usesRaces", true);
        systemConfig.put("usesClasses", true);
        systemConfig.put("usesResources", true);
        systemConfig.put("usesProficiencyBonus", true);
        systemConfig.put("usesArmorClass", true);
        systemConfig.put("usesAlignment", true);
        systemConfig.put("usesDeities", true);
        systemConfig.put("usesCurrency", true);
        systemConfig.put("usesNaturalWeapons", true);

        timeUnits.clear();
        timeUnits.put("round", 6);
        timeUnits.put("minute", 60);
        timeUnits.put("turn", 600);
        timeUnits.put("hour", 3600);
        timeUnits.put("day", 86400);

        // GameElement arrays - create strongly-typed ArrayLists
        arrayRegistry.put("attributes", getElementRegistry(ElementRegistryKey.ATTRIBUTES).getMutableItems());
        arrayRegistry.put("difficultySystems", getElementRegistry(ElementRegistryKey.DIFFICULTY_SYSTEMS).getMutableItems());
        arrayRegistry.put("spellComponents", getElementRegistry(ElementRegistryKey.SPELL_COMPONENTS).getMutableItems());
        arrayRegistry.put("skills", getElementRegistry(ElementRegistryKey.SKILLS).getMutableItems());
        arrayRegistry.put("characterClasses", getElementRegistry(ElementRegistryKey.CHARACTER_CLASSES).getMutableItems());
        arrayRegistry.put("races", getElementRegistry(ElementRegistryKey.RACES).getMutableItems());
        arrayRegistry.put("advantages", getElementRegistry(ElementRegistryKey.ADVANTAGES).getMutableItems());
        arrayRegistry.put("flaws", getElementRegistry(ElementRegistryKey.FLAWS).getMutableItems());
        arrayRegistry.put("creatures", getElementRegistry(ElementRegistryKey.CREATURES).getMutableItems());
        arrayRegistry.put("spells", getElementRegistry(ElementRegistryKey.SPELLS).getMutableItems());
        arrayRegistry.put("software", getElementRegistry(ElementRegistryKey.SOFTWARE).getMutableItems());
        arrayRegistry.put("equipment", getElementRegistry(ElementRegistryKey.EQUIPMENT).getMutableItems());
        arrayRegistry.put("weapons", getElementRegistry(ElementRegistryKey.WEAPONS).getMutableItems());
        arrayRegistry.put("armor", getElementRegistry(ElementRegistryKey.ARMOR).getMutableItems());
        arrayRegistry.put("naturalWeapons", getElementRegistry(ElementRegistryKey.NATURAL_WEAPONS).getMutableItems());
        arrayRegistry.put("effects", getElementRegistry(ElementRegistryKey.EFFECTS).getMutableItems());
        arrayRegistry.put("statuses", getElementRegistry(ElementRegistryKey.STATUSES).getMutableItems());
        arrayRegistry.put("pantheons", getElementRegistry(ElementRegistryKey.PANTHEONS).getMutableItems());
        arrayRegistry.put("deities", getElementRegistry(ElementRegistryKey.DEITIES).getMutableItems());
        arrayRegistry.put("currencies", getElementRegistry(ElementRegistryKey.CURRENCIES).getMutableItems());
        arrayRegistry.put("materials", getElementRegistry(ElementRegistryKey.MATERIALS).getMutableItems());

        // String arrays
        arrayHandler.putArray("diceUsed", new ArrayList<Integer>());
        ArrayList<String> weightUnitsMetric = new ArrayList<>();
        weightUnitsMetric.add("g");
        weightUnitsMetric.add("kg");
        weightUnitsMetric.add("tonne");
        arrayHandler.putArray("weightUnitsMetric", weightUnitsMetric);
        ArrayList<String> weightUnitsEnglish = new ArrayList<>();
        weightUnitsEnglish.add("oz");
        weightUnitsEnglish.add("lb");
        weightUnitsEnglish.add("ton");
        arrayHandler.putArray("weightUnitsEnglish", weightUnitsEnglish);
    }

    private void initializeRegistries() {
        registries.clear();
        registries.put(RegistryKey.ATTRIBUTE_TYPES, new AttributeTypes());
        registries.put(RegistryKey.SKILL_CATEGORIES, new SkillCategories());
        registries.put(RegistryKey.MOVEMENT_TYPES, new MovementTypes());
        registries.put(RegistryKey.EFFECT_TYPES, new EffectTypes());
        registries.put(RegistryKey.EQUIPMENT_TYPES, new EquipmentTypes());
        registries.put(RegistryKey.ADVANTAGE_TYPES, new AdvantageTypes());
        registries.put(RegistryKey.FLAW_TYPES, new FlawTypes());
        registries.put(RegistryKey.SOFTWARE_TYPES, new SoftwareTypes());
        registries.put(RegistryKey.SPELL_SCHOOLS, new SpellSchools());
    }

    private void initializeElementRegistries() {
        elementRegistries.clear();
        elementRegistryByName.clear();
        registerElementRegistry(ElementRegistryKey.ATTRIBUTES, new ElementRegistry<>());
        registerElementRegistry(ElementRegistryKey.DIFFICULTY_SYSTEMS, new ElementRegistry<>());
        registerElementRegistry(ElementRegistryKey.SPELL_COMPONENTS, new ElementRegistry<>());
        registerElementRegistry(ElementRegistryKey.SKILLS, new ElementRegistry<>());
        registerElementRegistry(ElementRegistryKey.CHARACTER_CLASSES, new ElementRegistry<>());
        registerElementRegistry(ElementRegistryKey.RACES, new ElementRegistry<>());
        registerElementRegistry(ElementRegistryKey.ADVANTAGES, new ElementRegistry<>());
        registerElementRegistry(ElementRegistryKey.FLAWS, new ElementRegistry<>());
        registerElementRegistry(ElementRegistryKey.CREATURES, new ElementRegistry<>());
        registerElementRegistry(ElementRegistryKey.SPELLS, new ElementRegistry<>());
        registerElementRegistry(ElementRegistryKey.SOFTWARE, new ElementRegistry<>());
        registerElementRegistry(ElementRegistryKey.EQUIPMENT, new ElementRegistry<>());
        registerElementRegistry(ElementRegistryKey.WEAPONS, new ElementRegistry<>());
        registerElementRegistry(ElementRegistryKey.ARMOR, new ElementRegistry<>());
        registerElementRegistry(ElementRegistryKey.NATURAL_WEAPONS, new ElementRegistry<>());
        registerElementRegistry(ElementRegistryKey.EFFECTS, new ElementRegistry<>());
        registerElementRegistry(ElementRegistryKey.STATUSES, new ElementRegistry<>());
        registerElementRegistry(ElementRegistryKey.PANTHEONS, new ElementRegistry<>());
        registerElementRegistry(ElementRegistryKey.DEITIES, new ElementRegistry<>());
        registerElementRegistry(ElementRegistryKey.CURRENCIES, new ElementRegistry<>());
        registerElementRegistry(ElementRegistryKey.MATERIALS, new ElementRegistry<>());
    }

    /**
     * Primary access point for atomic registries. Use this instead of per-registry fields.
     * Ensures a registry exists for the requested key.
     */
    public <T extends AtomicRegistry> T getRegistry(RegistryKey<T> key) {
        AtomicRegistry registry = registries.get(key);
        if (registry == null) {
            T created = key.createDefault();
            registries.put(key, created);
            return created;
        }
        return key.getType().cast(registry);
    }

    /**
     * Primary mutator for atomic registries. Use this instead of per-registry fields.
     */
    public <T extends AtomicRegistry> void setRegistry(RegistryKey<T> key, T registry) {
        T safeRegistry = Objects.requireNonNullElseGet(registry, key::createDefault);
        registries.put(key, safeRegistry);
        updateLastModified();
    }

    /**
     * Primary access point for element registries. Use this instead of per-element fields.
     * Ensures a registry exists for the requested key.
     */
    public <T extends GameElement> ElementRegistry<T> getElementRegistry(ElementRegistryKey<T> key) {
        ElementRegistry<? extends GameElement> registry = elementRegistries.get(key);
        if (registry == null) {
            ElementRegistry<T> created = key.createDefault();
            registerElementRegistry(key, created);
            return created;
        }
        @SuppressWarnings("unchecked")
        ElementRegistry<T> cast = (ElementRegistry<T>) registry;
        return cast;
    }

    /**
     * Primary mutator for element registries. Use this instead of per-element fields.
     */
    public <T extends GameElement> void setElementRegistry(ElementRegistryKey<T> key, ElementRegistry<T> registry) {
        ElementRegistry<T> safeRegistry = Objects.requireNonNullElseGet(registry, key::createDefault);
        registerElementRegistry(key, safeRegistry);
        updateLastModified();
    }

    /**
     * Resolves a display name for an element id, returning an empty string when not found.
     */
    public <T extends GameElement> String resolveElementName(ElementRegistryKey<T> key, String id) {
        String safeId = Objects.toString(id, "");
        if (safeId.isEmpty()) {
            return "";
        }
        T element = getElementRegistry(key).getById(safeId);
        if (element == null) {
            return "";
        }
        return element.getName();
    }

    private void registerElementRegistry(ElementRegistryKey<?> key, ElementRegistry<? extends GameElement> registry) {
        elementRegistries.put(key, registry);
        elementRegistryByName.put(key.getName(), key);
    }

    private void ensureElementRegistry(ElementRegistryKey<?> key) {
        ElementRegistry<? extends GameElement> registry = elementRegistries.get(key);
        if (registry == null) {
            registry = key.createDefault();
            elementRegistries.put(key, registry);
        }
        elementRegistryByName.put(key.getName(), key);
        arrayRegistry.put(key.getName(), registry.getMutableItems());
    }

    private ElementRegistryKey<?> getElementRegistryKey(String arrayName) {
        return elementRegistryByName.get(Objects.toString(arrayName, ""));
    }

    private ElementRegistry<? extends GameElement> getElementRegistryForArray(String arrayName) {
        ElementRegistryKey<?> key = getElementRegistryKey(arrayName);
        if (key == null) {
            return null;
        }
        return getElementRegistryRaw(key);
    }

    private ElementRegistry<? extends GameElement> getElementRegistryRaw(ElementRegistryKey<?> key) {
        ElementRegistry<? extends GameElement> registry = elementRegistries.get(key);
        if (registry == null) {
            ElementRegistry<? extends GameElement> created = key.createDefault();
            registerElementRegistry(key, created);
            return created;
        }
        return registry;
    }

    /**
     * Get array by name with type safety
     * @param arrayName the name of the array to retrieve
     * @return the array, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T> ArrayList<T> getArray(String arrayName) {
        ElementRegistry<? extends GameElement> registry = getElementRegistryForArray(arrayName);
        if (registry != null) {
            ArrayList<String> names = new ArrayList<>();
            for (GameElement element : registry.getAll()) {
                names.add(element.getName());
            }
            return (ArrayList<T>) names;
        }
        return arrayHandler.getArray(arrayName);
    }

    /**
     * Get the underlying array by name (object list)
     * @param arrayName the name of the array to retrieve
     * @return the array, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T> ArrayList<T> getObjectArray(String arrayName) {
        ElementRegistry<? extends GameElement> registry = getElementRegistryForArray(arrayName);
        if (registry != null) {
            return (ArrayList<T>) new ArrayList<>(registry.getAll());
        }
        return arrayHandler.getObjectArray(arrayName);
    }

    public Set<String> getArrayNames() {
        return arrayHandler.getArrayNames();
    }

    /**
     * Add element to specified array
     * Prevents duplicate names and IDs - checks if an element with the same name or ID already exists
     * @param arrayName the name of the array
     * @param element the element to add
     * @return true if element was added, false if duplicate name/ID found or element is null
     */
    @SuppressWarnings("unchecked")
    public <T extends GameElement> boolean addElement(String arrayName, T element) {
        ElementRegistryKey<?> key = getElementRegistryKey(arrayName);
        if (key != null) {
            if (element == null || !key.getType().isInstance(element)) {
                return false;
            }
            @SuppressWarnings("unchecked")
            ElementRegistry<T> registry = (ElementRegistry<T>) getElementRegistryRaw(key);
            boolean added = registry.add(element);
            if (added) {
                updateLastModified();
            }
            return added;
        }
        boolean added = arrayHandler.addElement(arrayName, element);
        if (added) {
            updateLastModified();
        }
        return added;
    }

    /**
     * Remove element from specified array
     * @param arrayName the name of the array
     * @param element the element to remove
     * @return true if removed, false if not found
     */
    @SuppressWarnings("unchecked")
    public <T extends GameElement> boolean removeElement(String arrayName, T element) {
        ElementRegistryKey<?> key = getElementRegistryKey(arrayName);
        if (key != null) {
            if (element == null || !key.getType().isInstance(element)) {
                return false;
            }
            @SuppressWarnings("unchecked")
            ElementRegistry<T> registry = (ElementRegistry<T>) getElementRegistryRaw(key);
            boolean removed = registry.remove(element);
            if (removed) {
                updateLastModified();
            }
            return removed;
        }
        boolean removed = arrayHandler.removeElement(arrayName, element);
        if (removed) {
            updateLastModified();
        }
        return removed;
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
    public <T extends GameElement> int addAllElements(String arrayName, Collection<T> elements) {
        ElementRegistryKey<?> key = getElementRegistryKey(arrayName);
        if (key != null) {
            int addedCount = 0;
            Collection<T> safeElements = Objects.requireNonNullElse(elements, List.of());
            for (T element : safeElements) {
                if (element != null && key.getType().isInstance(element)) {
                    @SuppressWarnings("unchecked")
                    ElementRegistry<T> registry = (ElementRegistry<T>) getElementRegistryRaw(key);
                    if (registry.add(element)) {
                        addedCount++;
                    }
                }
            }
            if (addedCount > 0) {
                updateLastModified();
            }
            return addedCount;
        }
        int addedCount = arrayHandler.addAllElements(arrayName, elements);
        if (addedCount > 0) {
            updateLastModified();
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
    public <T extends GameElement> int removeAllElements(String arrayName, Collection<T> elements) {
        ElementRegistryKey<?> key = getElementRegistryKey(arrayName);
        if (key != null) {
            int removedCount = 0;
            Collection<T> safeElements = Objects.requireNonNullElse(elements, List.of());
            for (T element : safeElements) {
                if (element != null && key.getType().isInstance(element)) {
                    @SuppressWarnings("unchecked")
                    ElementRegistry<T> registry = (ElementRegistry<T>) getElementRegistryRaw(key);
                    if (registry.remove(element)) {
                        removedCount++;
                    }
                }
            }
            if (removedCount > 0) {
                updateLastModified();
            }
            return removedCount;
        }
        int removedCount = arrayHandler.removeAllElements(arrayName, elements);
        if (removedCount > 0) {
            updateLastModified();
        }
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
    public <T extends GameElement> void replaceArray(String arrayName, Collection<T> newElements) {
        ElementRegistryKey<?> key = getElementRegistryKey(arrayName);
        if (key != null) {
            Collection<T> safeElements = Objects.requireNonNullElse(newElements, List.of());
            @SuppressWarnings("unchecked")
            ElementRegistry<T> registry = (ElementRegistry<T>) getElementRegistryRaw(key);
            registry.clear();
            for (T element : safeElements) {
                if (element != null && key.getType().isInstance(element)) {
                    registry.add(element);
                }
            }
            updateLastModified();
            return;
        }
        arrayHandler.replaceArray(arrayName, newElements);
        updateLastModified();
    }

    /**
     * Get single element by ID from specified array
     * @param arrayName the name of the array
     * @param id the ID of the element to find
     * @return the element or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T extends GameElement> T getElement(String arrayName, String id) {
        ElementRegistryKey<?> key = getElementRegistryKey(arrayName);
        if (key != null) {
            return (T) getElementRegistryRaw(key).getById(id);
        }
        return arrayHandler.getElement(arrayName, id);
    }

    /**
     * Get an element by its name from the specified array
     * @param arrayName the name of the array
     * @param name the name to search for (case-insensitive)
     * @return the element with matching name, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T extends GameElement> T getElementByName(String arrayName, String name) {
        ElementRegistryKey<?> key = getElementRegistryKey(arrayName);
        if (key != null) {
            return (T) getElementRegistryRaw(key).getByName(name);
        }
        return arrayHandler.getElementByName(arrayName, name);
    }

    /**
     * Check if an element with the given name already exists in the specified array
     * @param arrayName the name of the array
     * @param name the name to check for (case-insensitive)
     * @return true if an element with this name exists, false otherwise
     */
    @SuppressWarnings("unchecked")
    public <T extends GameElement> boolean hasElementWithName(String arrayName, String name) {
        ElementRegistryKey<?> key = getElementRegistryKey(arrayName);
        if (key != null) {
            return getElementRegistryRaw(key).hasName(name);
        }
        return arrayHandler.hasElementWithName(arrayName, name);
    }

    /**
     * Clear all elements from specified array
     * @param arrayName the name of the array to clear
     */
    public void clearArray(String arrayName) {
        ElementRegistryKey<?> key = getElementRegistryKey(arrayName);
        if (key != null) {
            getElementRegistryRaw(key).clear();
            updateLastModified();
            return;
        }
        arrayHandler.clearArray(arrayName);
        updateLastModified();
    }

    // Special methods for String arrays (since String doesn't extend GameElement)

    /**
     * Add string to specified string array
     * Prevents duplicate strings - checks if string already exists before adding
     * @param arrayName the name of the string array
     * @param value the string to add
     */
    public void addString(String arrayName, String value) {
        String safeValue = Objects.toString(value, "").trim();
        if (safeValue.isEmpty()) {
            return;
        }
        boolean added = arrayHandler.addElement(arrayName, safeValue);
        if (added) {
            updateLastModified();
        }
    }

    /**
     * Remove string from specified string array
     * @param arrayName the name of the string array
     * @param value the string to remove
     * @return true if removed, false if not found
     */
    public boolean removeString(String arrayName, String value) {
        boolean removed = arrayHandler.removeElement(arrayName, value);
        if (removed) {
            updateLastModified();
        }
        return removed;
    }

    // ===== SYSTEM CONFIGURATION METHODS =====

    /**
     * Get system configuration flag
     * @param configName the name of the configuration flag
     * @return the boolean value, or false if not found
     */
    public boolean getSystemConfig(String configName) {
        String safeConfigName = Objects.toString(configName, "").trim();
        if (safeConfigName.isEmpty()) {
            return false;
        }
        return systemConfig.getOrDefault(safeConfigName, false);
    }

    /**
     * Set system configuration flag
     * @param configName the name of the configuration flag
     * @param value the boolean value to set
     */
    public void setSystemConfig(String configName, boolean value) {
        String safeConfigName = Objects.toString(configName, "").trim();
        if (safeConfigName.isEmpty()) {
            return;
        }
        systemConfig.put(safeConfigName, value);
        updateLastModified();
    }

    // ===== SYSTEM NAME METHODS =====
    public String getSystemName(String key) {
        String safeKey = normalizeSystemNameKey(key);
        if (safeKey.isEmpty()) {
            return "";
        }
        return Objects.toString(systemNames.get(safeKey), "");
    }

    public Map<String, String> getSystemNames() {
        return new LinkedHashMap<>(systemNames);
    }

    public void setSystemName(String key, String value) {
        String safeKey = normalizeSystemNameKey(key);
        if (safeKey.isEmpty()) {
            return;
        }
        String safeValue = Objects.toString(value, "").trim();
        if (safeValue.isEmpty()) {
            if (systemNames.remove(safeKey) != null) {
                updateLastModified();
            }
            return;
        }
        systemNames.put(safeKey, safeValue);
        updateLastModified();
    }

    private String normalizeSystemNameKey(String key) {
        return Objects.toString(key, "").trim().toLowerCase(Locale.ROOT);
    }

    // ===== TIME UNIT METHODS =====
    public Map<String, Integer> getTimeUnits() {
        return new LinkedHashMap<>(timeUnits);
    }

    public void setTimeUnits(Map<String, Integer> timeUnits) {
        this.timeUnits.clear();
        Map<String, Integer> safeUnits = Objects.requireNonNullElse(timeUnits, Map.of());
        for (Map.Entry<String, Integer> entry : safeUnits.entrySet()) {
            String name = Objects.toString(entry.getKey(), "").trim();
            Integer duration = entry.getValue();
            if (name.isEmpty() || duration == null || duration <= 0) {
                continue;
            }
            this.timeUnits.put(name, duration);
        }
        updateLastModified();
    }

    public void addTimeUnit(String name, int duration) {
        String safeName = Objects.toString(name, "").trim();
        if (safeName.isEmpty() || duration <= 0) {
            return;
        }
        timeUnits.put(safeName, duration);
        updateLastModified();
    }

    public void addTimeUnit(String name, int duration, String baseUnit) {
        String safeName = Objects.toString(name, "").trim();
        if (safeName.isEmpty()) {
            return;
        }
        Integer resolved = resolveTimeUnitSeconds(duration, baseUnit);
        if (resolved == null || resolved <= 0) {
            return;
        }
        timeUnits.put(safeName, resolved);
        updateLastModified();
    }

    public Integer resolveTimeUnitSeconds(int duration, String baseUnit) {
        if (duration <= 0) {
            return null;
        }
        String unit = Objects.toString(baseUnit, "").trim().toLowerCase();
        if (unit.isEmpty() || unit.equals("second") || unit.equals("seconds")) {
            return duration;
        }
        if (unit.equals("minute") || unit.equals("minutes")) {
            try {
                return Math.multiplyExact(duration, 60);
            } catch (ArithmeticException ex) {
                return null;
            }
        }
        if (unit.equals("hour") || unit.equals("hours")) {
            try {
                return Math.multiplyExact(duration, 3600);
            } catch (ArithmeticException ex) {
                return null;
            }
        }
        return null;
    }

    public boolean removeTimeUnit(String name) {
        String safeName = Objects.toString(name, "").trim();
        if (safeName.isEmpty()) {
            return false;
        }
        Integer removed = timeUnits.remove(safeName);
        if (removed != null) {
            updateLastModified();
            return true;
        }
        return false;
    }

    // ===== STARTING MONEY METHODS =====
    public String getStartingMoneyMethod() {
        return Objects.toString(startingMoneyMethod, "base");
    }

    public void setStartingMoneyMethod(String startingMoneyMethod) {
        String safe = Objects.toString(startingMoneyMethod, "").trim().toLowerCase(Locale.ROOT);
        if (!safe.equals("base") && !safe.equals("class") && !safe.equals("trait") && !safe.equals("hybrid")) {
            safe = "base";
        }
        this.startingMoneyMethod = safe;
        updateLastModified();
    }

    public int getBaseStartingMoney() {
        return Math.max(0, baseStartingMoney);
    }

    public void setBaseStartingMoney(int baseStartingMoney) {
        this.baseStartingMoney = Math.max(0, baseStartingMoney);
        updateLastModified();
    }

    public String getStartingMoneyCurrencyId() {
        return Objects.toString(startingMoneyCurrencyId, "");
    }

    public void setStartingMoneyCurrencyId(String startingMoneyCurrencyId) {
        this.startingMoneyCurrencyId = Objects.toString(startingMoneyCurrencyId, "").trim();
        updateLastModified();
    }

    public Map<String, Integer> getClassStartingMoney() {
        return new LinkedHashMap<>(classStartingMoney);
    }

    public int getClassStartingMoney(String classId) {
        String safeId = Objects.toString(classId, "").trim();
        if (safeId.isEmpty()) {
            return 0;
        }
        return Math.max(0, classStartingMoney.getOrDefault(safeId, 0));
    }

    public void setClassStartingMoney(String classId, int amount) {
        String safeId = Objects.toString(classId, "").trim();
        if (safeId.isEmpty()) {
            return;
        }
        int safeAmount = Math.max(0, amount);
        if (safeAmount <= 0) {
            classStartingMoney.remove(safeId);
        } else {
            classStartingMoney.put(safeId, safeAmount);
        }
        updateLastModified();
    }

    public void setClassStartingMoneyMap(Map<String, Integer> values) {
        classStartingMoney.clear();
        Map<String, Integer> safeValues = Objects.requireNonNullElse(values, Map.of());
        for (Map.Entry<String, Integer> entry : safeValues.entrySet()) {
            String id = Objects.toString(entry.getKey(), "").trim();
            int amount = Objects.requireNonNullElse(entry.getValue(), 0);
            if (!id.isEmpty() && amount > 0) {
                classStartingMoney.put(id, amount);
            }
        }
        updateLastModified();
    }

    public Map<String, Integer> getRaceStartingMoneyModifiers() {
        return new LinkedHashMap<>(raceStartingMoneyModifiers);
    }

    public int getRaceStartingMoneyModifier(String raceId) {
        String safeId = Objects.toString(raceId, "").trim();
        if (safeId.isEmpty()) {
            return 0;
        }
        return raceStartingMoneyModifiers.getOrDefault(safeId, 0);
    }

    public void setRaceStartingMoneyModifier(String raceId, int amount) {
        String safeId = Objects.toString(raceId, "").trim();
        if (safeId.isEmpty()) {
            return;
        }
        if (amount == 0) {
            raceStartingMoneyModifiers.remove(safeId);
        } else {
            raceStartingMoneyModifiers.put(safeId, amount);
        }
        updateLastModified();
    }

    public void setRaceStartingMoneyModifierMap(Map<String, Integer> values) {
        raceStartingMoneyModifiers.clear();
        Map<String, Integer> safeValues = Objects.requireNonNullElse(values, Map.of());
        for (Map.Entry<String, Integer> entry : safeValues.entrySet()) {
            String id = Objects.toString(entry.getKey(), "").trim();
            int amount = Objects.requireNonNullElse(entry.getValue(), 0);
            if (!id.isEmpty() && amount != 0) {
                raceStartingMoneyModifiers.put(id, amount);
            }
        }
        updateLastModified();
    }

    public Map<String, Integer> getTraitStartingMoneyModifiers() {
        return new LinkedHashMap<>(traitStartingMoneyModifiers);
    }

    public int getTraitStartingMoneyModifier(String skillId) {
        String safeId = Objects.toString(skillId, "").trim();
        if (safeId.isEmpty()) {
            return 0;
        }
        return traitStartingMoneyModifiers.getOrDefault(safeId, 0);
    }

    public void setTraitStartingMoneyModifier(String skillId, int amount) {
        String safeId = Objects.toString(skillId, "").trim();
        if (safeId.isEmpty()) {
            return;
        }
        if (amount == 0) {
            traitStartingMoneyModifiers.remove(safeId);
        } else {
            traitStartingMoneyModifiers.put(safeId, amount);
        }
        updateLastModified();
    }

    public void setTraitStartingMoneyModifierMap(Map<String, Integer> values) {
        traitStartingMoneyModifiers.clear();
        Map<String, Integer> safeValues = Objects.requireNonNullElse(values, Map.of());
        for (Map.Entry<String, Integer> entry : safeValues.entrySet()) {
            String id = Objects.toString(entry.getKey(), "").trim();
            int amount = Objects.requireNonNullElse(entry.getValue(), 0);
            if (!id.isEmpty() && amount != 0) {
                traitStartingMoneyModifiers.put(id, amount);
            }
        }
        updateLastModified();
    }

    public int resolveStartingMoney(String classId, String raceId, Collection<String> skillIds) {
        String method = getStartingMoneyMethod();
        int resolved = getBaseStartingMoney();
        if (method.equals("class") || method.equals("hybrid")) {
            int classValue = getClassStartingMoney(classId);
            if (classValue > 0) {
                resolved = classValue;
            }
        }
        if (method.equals("trait") || method.equals("hybrid")) {
            resolved += getRaceStartingMoneyModifier(raceId);
            Collection<String> safeSkillIds = Objects.requireNonNullElse(skillIds, List.of());
            for (String skillId : safeSkillIds) {
                resolved += getTraitStartingMoneyModifier(skillId);
            }
        }
        return Math.max(0, resolved);
    }

    public int cleanupStartingMoneyReferences(
        Set<String> validClassIds,
        Set<String> validRaceIds,
        Set<String> validSkillIds
    ) {
        int removed = 0;
        List<String> invalidClassIds = new ArrayList<>();
        for (String id : classStartingMoney.keySet()) {
            if (!validClassIds.contains(id)) {
                invalidClassIds.add(id);
            }
        }
        for (String id : invalidClassIds) {
            classStartingMoney.remove(id);
            removed++;
        }
        List<String> invalidRaceIds = new ArrayList<>();
        for (String id : raceStartingMoneyModifiers.keySet()) {
            if (!validRaceIds.contains(id)) {
                invalidRaceIds.add(id);
            }
        }
        for (String id : invalidRaceIds) {
            raceStartingMoneyModifiers.remove(id);
            removed++;
        }
        List<String> invalidSkillIds = new ArrayList<>();
        for (String id : traitStartingMoneyModifiers.keySet()) {
            if (!validSkillIds.contains(id)) {
                invalidSkillIds.add(id);
            }
        }
        for (String id : invalidSkillIds) {
            traitStartingMoneyModifiers.remove(id);
            removed++;
        }
        return removed;
    }

    public AttributeModifiers getAttributeModifiers() {
        return attributeModifiers;
    }

    public void setAttributeModifiers(AttributeModifiers attributeModifiers) {
        this.attributeModifiers = Objects.requireNonNullElseGet(attributeModifiers, AttributeModifiers::new);
        updateLastModified();
    }

    public boolean isApplyAttributeModifiersToAllAttributes() {
        return applyAttributeModifiersToAllAttributes;
    }

    public void setApplyAttributeModifiersToAllAttributes(boolean applyAttributeModifiersToAllAttributes) {
        this.applyAttributeModifiersToAllAttributes = applyAttributeModifiersToAllAttributes;
        updateLastModified();
    }

    public int getDefaultAttributeMinScore() {
        return defaultAttributeMinScore;
    }

    public int getDefaultAttributeMaxScore() {
        return defaultAttributeMaxScore;
    }

    public void setDefaultAttributeScoreRange(int minScore, int maxScore) {
        defaultAttributeMinScore = minScore;
        defaultAttributeMaxScore = maxScore;
        updateLastModified();
    }

    // ===== DICE CONFIGURATION METHODS =====
    public ArrayList<Integer> getDiceUsed() {
        ArrayList<Integer> diceValues = arrayHandler.getObjectArray("diceUsed");
        if (diceValues == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(diceValues);
    }

    public void setDiceUsed(Collection<Integer> diceUsed) {
        arrayHandler.clearArray("diceUsed");
        Collection<Integer> safeValues = Objects.requireNonNullElse(diceUsed, List.of());
        boolean updated = false;
        for (Integer value : safeValues) {
            if (value != null && value > 0) {
                if (arrayHandler.addElement("diceUsed", value)) {
                    updated = true;
                }
            }
        }
        if (updated) {
            updateLastModified();
        }
    }

    public void addDiceUsed(int diceSides) {
        if (diceSides <= 0) {
            return;
        }
        boolean added = arrayHandler.addElement("diceUsed", diceSides);
        if (added) {
            updateLastModified();
        }
    }

    public boolean removeDiceUsed(int diceSides) {
        boolean removed = arrayHandler.removeElement("diceUsed", Integer.valueOf(diceSides));
        if (removed) {
            updateLastModified();
        }
        return removed;
    }

    public void clearDiceUsed() {
        arrayHandler.clearArray("diceUsed");
        updateLastModified();
    }

    public List<DiceRange> getCustomDiceRanges() {
        return new ArrayList<>(customDiceRanges);
    }

    public void addCustomDiceRange(int minValue, int maxValue) {
        if (minValue <= 0 || maxValue <= 0) {
            return;
        }
        int safeMin = Math.min(minValue, maxValue);
        int safeMax = Math.max(minValue, maxValue);
        DiceRange range = new DiceRange(safeMin, safeMax);
        if (!customDiceRanges.contains(range)) {
            customDiceRanges.add(range);
            updateLastModified();
        }
    }

    public boolean removeCustomDiceRange(int minValue, int maxValue) {
        int safeMin = Math.min(minValue, maxValue);
        int safeMax = Math.max(minValue, maxValue);
        boolean removed = customDiceRanges.remove(new DiceRange(safeMin, safeMax));
        if (removed) {
            updateLastModified();
        }
        return removed;
    }

    public void clearCustomDiceRanges() {
        if (!customDiceRanges.isEmpty()) {
            customDiceRanges.clear();
            updateLastModified();
        }
    }

    // Game-specific metadata getters and setters (name, description, id inherited from GameElement)
    @Override
    public void setName(String name) {
        super.setName(name);
        updateLastModified();
    }

    @Override
    public void setDescription(String description) {
        super.setDescription(description);
        updateLastModified();
    }

    public String getVersion() { return version; }
    public void setVersion(String version) {
        this.version = Objects.toString(version, "");
        updateLastModified();
    }

    public String getAuthor() { return author; }
    public void setAuthor(String author) {
        this.author = Objects.toString(author, "");
        updateLastModified();
    }

    public String getGameType() { return gameType; }
    public void setGameType(String gameType) {
        this.gameType = Objects.toString(gameType, "");
        updateLastModified();
    }

    public String getWeightSystem() { return weightSystem; }
    public void setWeightSystem(String weightSystem) {
        this.weightSystem = Objects.toString(weightSystem, "");
        updateLastModified();
    }

    public String getUiLocale() { return uiLocale; }
    public void setUiLocale(String uiLocale) {
        this.uiLocale = Objects.toString(uiLocale, "");
        updateLastModified();
    }

    public List<String> getCompletedStages() {
        return new ArrayList<>(completedStages);
    }

    public void setCompletedStages(Collection<String> stages) {
        Collection<String> safeStages = Objects.requireNonNullElseGet(stages, List::of);
        completedStages.clear();
        for (String stage : safeStages) {
            String safeStage = Objects.toString(stage, "").trim();
            if (safeStage.isEmpty() || completedStages.contains(safeStage)) {
                continue;
            }
            completedStages.add(safeStage);
        }
        updateLastModified();
    }

    public boolean markStageCompleted(String stageKey) {
        String safeStage = Objects.toString(stageKey, "").trim();
        if (safeStage.isEmpty() || completedStages.contains(safeStage)) {
            return false;
        }
        completedStages.add(safeStage);
        updateLastModified();
        return true;
    }

    public static String[] getGameTypes() {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("i18n/strings", Locale.getDefault());
            String rawTypes = bundle.getString(GAME_TYPES_KEY);
            if (rawTypes.isBlank()) {
                return new String[0];
            }
            return rawTypes.split(GAME_TYPES_SPLIT_REGEX);
        } catch (MissingResourceException e) {
            return new String[0];
        }
    }

    public LocalDateTime getCreated() { return created; }
    public LocalDateTime getLastModified() { return lastModified; }

    // Individual array getters/setters removed - use generic getArray/addElement/removeElement instead

    // Rule Methods Getters and Setters
    public AttributeGenerationMethod getAttributeGenerationMethod() { return attributeGenerationMethod; }
    public void setAttributeGenerationMethod(AttributeGenerationMethod attributeGenerationMethod) {
        this.attributeGenerationMethod = Objects.requireNonNullElseGet(
            attributeGenerationMethod,
            () -> new AttributeGenerationMethod("Attribute Generation")
        );
        ensureAttributeGenerationOptions();
        updateLastModified();
    }

    public List<AttributeGenerationOption> getAttributeGenerationOptions() {
        ensureAttributeGenerationOptions();
        ArrayList<AttributeGenerationOption> copy = new ArrayList<>();
        for (AttributeGenerationOption option : attributeGenerationOptions) {
            copy.add(new AttributeGenerationOption(option));
        }
        return copy;
    }

    public void setAttributeGenerationOptions(Collection<AttributeGenerationOption> options) {
        attributeGenerationOptions = copyAttributeGenerationOptions(options);
        customAttributeGenerationOptions = true;
        ensureAttributeGenerationOptions();
        updateLastModified();
    }

    public void addAttributeGenerationOption(AttributeGenerationOption option) {
        AttributeGenerationOption safeOption = new AttributeGenerationOption(option);
        if (safeOption.getSteps().isEmpty()) {
            return;
        }
        ensureAttributeGenerationOptions();
        customAttributeGenerationOptions = true;
        attributeGenerationOptions.add(safeOption);
        updateLastModified();
    }

    public void clearAttributeGenerationOptions() {
        attributeGenerationOptions.clear();
        customAttributeGenerationOptions = false;
        ensureAttributeGenerationOptions();
        updateLastModified();
    }

    public boolean hasCustomAttributeGenerationOptions() {
        return customAttributeGenerationOptions;
    }

    public SaveMethod getSaveMethod() { return saveMethod; }
    public void setSaveMethod(SaveMethod saveMethod) {
        this.saveMethod = Objects.requireNonNullElseGet(saveMethod, () -> new SaveMethod("Saves"));
        updateLastModified();
    }

    public HPMethod getHpMethod() { return hpMethod; }
    public void setHpMethod(HPMethod hpMethod) {
        this.hpMethod = Objects.requireNonNullElseGet(hpMethod, () -> new HPMethod("Hit Points"));
        updateLastModified();
    }

    public ArmorClassMethod getArmorClassMethod() { return armorClassMethod; }
    public void setArmorClassMethod(ArmorClassMethod armorClassMethod) {
        this.armorClassMethod = Objects.requireNonNullElseGet(
            armorClassMethod,
            () -> new ArmorClassMethod("Armor Class")
        );
        updateLastModified();
    }

    public CombatMethod getCombatMethod() { return combatMethod; }
    public void setCombatMethod(CombatMethod combatMethod) {
        this.combatMethod = Objects.requireNonNullElseGet(combatMethod, () -> new CombatMethod("Combat"));
        updateLastModified();
    }

    public LevelingMethod getLevelingMethod() { return levelingMethod; }
    public void setLevelingMethod(LevelingMethod levelingMethod) {
        this.levelingMethod = Objects.requireNonNullElseGet(levelingMethod, () -> new LevelingMethod("Leveling"));
        updateLastModified();
    }

    public void updateLastModified() {
        this.lastModified = LocalDateTime.now();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        if (arrayRegistry == null) {
            arrayRegistry = new HashMap<>();
        }
        if (systemConfig == null) {
            systemConfig = new HashMap<>();
        }
        if (systemNames == null) {
            systemNames = new HashMap<>();
        }
        if (timeUnits == null) {
            timeUnits = new LinkedHashMap<>();
        }
        if (registries == null) {
            registries = new HashMap<>();
        }
        if (elementRegistries == null) {
            elementRegistries = new HashMap<>();
        }
        if (elementRegistryByName == null) {
            elementRegistryByName = new HashMap<>();
        }
        arrayHandler = new ArrayHandler(arrayRegistry);
        ensureElementRegistry(ElementRegistryKey.ATTRIBUTES);
        ensureElementRegistry(ElementRegistryKey.DIFFICULTY_SYSTEMS);
        ensureElementRegistry(ElementRegistryKey.SPELL_COMPONENTS);
        ensureElementRegistry(ElementRegistryKey.SKILLS);
        ensureElementRegistry(ElementRegistryKey.CHARACTER_CLASSES);
        ensureElementRegistry(ElementRegistryKey.RACES);
        ensureElementRegistry(ElementRegistryKey.ADVANTAGES);
        ensureElementRegistry(ElementRegistryKey.FLAWS);
        ensureElementRegistry(ElementRegistryKey.CREATURES);
        ensureElementRegistry(ElementRegistryKey.SPELLS);
        ensureElementRegistry(ElementRegistryKey.SOFTWARE);
        ensureElementRegistry(ElementRegistryKey.EQUIPMENT);
        ensureElementRegistry(ElementRegistryKey.WEAPONS);
        ensureElementRegistry(ElementRegistryKey.ARMOR);
        ensureElementRegistry(ElementRegistryKey.NATURAL_WEAPONS);
        ensureElementRegistry(ElementRegistryKey.EFFECTS);
        ensureElementRegistry(ElementRegistryKey.STATUSES);
        ensureElementRegistry(ElementRegistryKey.PANTHEONS);
        ensureElementRegistry(ElementRegistryKey.DEITIES);
        ensureElementRegistry(ElementRegistryKey.CURRENCIES);
        ensureElementRegistry(ElementRegistryKey.MATERIALS);
        if (completedStages == null) {
            completedStages = new ArrayList<>();
        }
        if (classStartingMoney == null) {
            classStartingMoney = new LinkedHashMap<>();
        }
        if (raceStartingMoneyModifiers == null) {
            raceStartingMoneyModifiers = new LinkedHashMap<>();
        }
        if (traitStartingMoneyModifiers == null) {
            traitStartingMoneyModifiers = new LinkedHashMap<>();
        }
        if (attributeModifiers == null) {
            attributeModifiers = new AttributeModifiers();
        }
        if (startingMoneyMethod == null) {
            startingMoneyMethod = "base";
        }
        if (startingMoneyCurrencyId == null) {
            startingMoneyCurrencyId = "";
        }
        attributeGenerationMethod = Objects.requireNonNullElseGet(
            attributeGenerationMethod,
            () -> new AttributeGenerationMethod("Attribute Generation")
        );
        if (!customAttributeGenerationOptions) {
            attributeGenerationOptions = new ArrayList<>();
        }
        attributeGenerationOptions = copyAttributeGenerationOptions(attributeGenerationOptions);
        ensureAttributeGenerationOptions();
        saveMethod = Objects.requireNonNullElseGet(saveMethod, () -> new SaveMethod("Saves"));
        hpMethod = Objects.requireNonNullElseGet(hpMethod, () -> new HPMethod("Hit Points"));
        armorClassMethod = Objects.requireNonNullElseGet(armorClassMethod, () -> new ArmorClassMethod("Armor Class"));
        combatMethod = Objects.requireNonNullElseGet(combatMethod, () -> new CombatMethod("Combat"));
        levelingMethod = Objects.requireNonNullElseGet(levelingMethod, () -> new LevelingMethod("Leveling"));
    }

    private void ensureAttributeGenerationOptions() {
        if (attributeGenerationOptions == null) {
            attributeGenerationOptions = new ArrayList<>();
        }
        if (!customAttributeGenerationOptions) {
            attributeGenerationOptions = buildDefaultAttributeGenerationOptions(attributeGenerationMethod);
            return;
        }
        if (!attributeGenerationOptions.isEmpty()) {
            attributeGenerationOptions = copyAttributeGenerationOptions(attributeGenerationOptions);
            return;
        }
        attributeGenerationOptions = buildDefaultAttributeGenerationOptions(attributeGenerationMethod);
    }

    private ArrayList<AttributeGenerationOption> copyAttributeGenerationOptions(
        Collection<AttributeGenerationOption> options
    ) {
        Collection<AttributeGenerationOption> safeOptions = Objects.requireNonNullElse(options, List.of());
        ArrayList<AttributeGenerationOption> copy = new ArrayList<>();
        for (AttributeGenerationOption option : safeOptions) {
            AttributeGenerationOption safeOption = new AttributeGenerationOption(option);
            if (!safeOption.getSteps().isEmpty()) {
                copy.add(safeOption);
            }
        }
        return copy;
    }

    private ArrayList<AttributeGenerationOption> buildDefaultAttributeGenerationOptions(
        AttributeGenerationMethod method
    ) {
        AttributeGenerationMethod safeMethod = Objects.requireNonNullElseGet(
            method,
            () -> new AttributeGenerationMethod("Attribute Generation")
        );
        ArrayList<String> methodTypes = resolveAttributeGenerationMethodTypes(safeMethod);
        ArrayList<AttributeGenerationOption> options = new ArrayList<>();
        for (String methodType : methodTypes) {
            String safeType = AttributeGenerationStep.normalizeMethodType(methodType);
            if (safeType.isEmpty()) {
                continue;
            }
            AttributeGenerationOption option = new AttributeGenerationOption(
                defaultAttributeGenerationOptionId(safeType),
                defaultAttributeGenerationOptionName(safeType)
            );
            option.addStep(new AttributeGenerationStep(safeType, AttributeGenerationStep.APPLICATION_SET));
            options.add(option);
        }
        return options;
    }

    private ArrayList<String> resolveAttributeGenerationMethodTypes(AttributeGenerationMethod method) {
        String generationType = AttributeGenerationStep.normalizeMethodType(method.getGenerationType());
        ArrayList<String> methodTypes = new ArrayList<>();
        if (!generationType.isEmpty()) {
            methodTypes.add(generationType);
            return methodTypes;
        }
        String rawType = Objects.toString(method.getGenerationType(), "").trim().toLowerCase();
        if (!rawType.equals("hybrid")) {
            return methodTypes;
        }
        List<String> stages = Objects.requireNonNullElse(method.<String>getArray("hybridStages"), new ArrayList<>());
        if (stages.isEmpty()) {
            stages = List.of(
                AttributeGenerationStep.METHOD_STANDARD_ARRAY,
                AttributeGenerationStep.METHOD_DICE,
                AttributeGenerationStep.METHOD_POINT_BUY
            );
        }
        for (String stage : stages) {
            String safeStage = AttributeGenerationStep.normalizeMethodType(stage);
            if (!safeStage.isEmpty() && !methodTypes.contains(safeStage)) {
                methodTypes.add(safeStage);
            }
        }
        return methodTypes;
    }

    private String defaultAttributeGenerationOptionId(String methodType) {
        String safeType = AttributeGenerationStep.normalizeMethodType(methodType);
        return safeType.isEmpty() ? "" : "option-" + safeType;
    }

    private String defaultAttributeGenerationOptionName(String methodType) {
        String safeType = AttributeGenerationStep.normalizeMethodType(methodType);
        if (safeType.equals(AttributeGenerationStep.METHOD_STANDARD_ARRAY)) {
            return "Standard Array";
        }
        if (safeType.equals(AttributeGenerationStep.METHOD_DICE)) {
            return "Dice Rolling";
        }
        if (safeType.equals(AttributeGenerationStep.METHOD_POINT_BUY)) {
            return "Point Buy";
        }
        return "";
    }

    /**
     * Validates the game system for completeness and consistency.
     * @return true if valid, false otherwise
     */
    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }
        return !version.trim().isEmpty();
    }

    /**
     * Gets a summary of the game system content
     * @return formatted summary string
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(String.format("Game: %s v%s by %s\n", getName(), version, author));

        // Only show systems with content (data-driven)
        ArrayList<?> attributes = getArray("attributes");
        ArrayList<?> difficultySystems = getArray("difficultySystems");
        ArrayList<?> skills = getArray("skills");
        ArrayList<?> characterClasses = getArray("characterClasses");
        ArrayList<?> races = getArray("races");
        ArrayList<?> spells = getArray("spells");
        ArrayList<?> equipment = getArray("equipment");
        ArrayList<?> weapons = getArray("weapons");
        ArrayList<?> armor = getArray("armor");
        ArrayList<?> naturalWeapons = getArray("naturalWeapons");
        ArrayList<?> pantheons = getArray("pantheons");
        ArrayList<?> deities = getArray("deities");
        ArrayList<?> currencies = getArray("currencies");
        List<MovementType> movementTypes = getRegistry(RegistryKey.MOVEMENT_TYPES).getAll();

        if (!attributes.isEmpty()) summary.append(String.format("Attributes: %d, ", attributes.size()));
        if (!difficultySystems.isEmpty()) summary.append(String.format("Difficulty Systems: %d, ", difficultySystems.size()));
        if (!skills.isEmpty()) summary.append(String.format("Skills: %d, ", skills.size()));
        if (!characterClasses.isEmpty()) summary.append(String.format("Classes: %d, ", characterClasses.size()));
        if (!races.isEmpty()) summary.append(String.format("Races: %d, ", races.size()));
        if (!spells.isEmpty()) summary.append(String.format("Spells: %d\n", spells.size()));

        if (!equipment.isEmpty()) summary.append(String.format("Equipment: %d, ", equipment.size()));
        if (!weapons.isEmpty()) summary.append(String.format("Weapons: %d, ", weapons.size()));
        if (!armor.isEmpty()) summary.append(String.format("Armor: %d, ", armor.size()));
        if (!naturalWeapons.isEmpty()) summary.append(String.format("Natural Weapons: %d\n", naturalWeapons.size()));

        if (!pantheons.isEmpty()) summary.append(String.format("Pantheons: %d, ", pantheons.size()));
        if (!deities.isEmpty()) summary.append(String.format("Deities: %d, ", deities.size()));
        if (!currencies.isEmpty()) summary.append(String.format("Currencies: %d\n", currencies.size()));

        if (!movementTypes.isEmpty()) summary.append(String.format("Movement Types: %d\n", movementTypes.size()));

        summary.append(String.format("Created: %s, Last Modified: %s", created, lastModified));
        return summary.toString();
    }

    @Override
    public String toString() {
        return String.format("Game{name='%s', version='%s', author='%s'}", getName(), version, author);
    }

    public static final class DiceRange implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private final int minValue;
        private final int maxValue;

        public DiceRange(int minValue, int maxValue) {
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public int getMinValue() {
            return minValue;
        }

        public int getMaxValue() {
            return maxValue;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof DiceRange)) {
                return false;
            }
            DiceRange range = (DiceRange) other;
            return minValue == range.minValue && maxValue == range.maxValue;
        }

        @Override
        public int hashCode() {
            return Objects.hash(minValue, maxValue);
        }

        @Override
        public String toString() {
            return minValue + "-" + maxValue;
        }
    }

    public static final class AttributeGenerationOption implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private String id = "";
        private String name = "";
        private ArrayList<AttributeGenerationStep> steps = new ArrayList<>();

        public AttributeGenerationOption() {
        }

        public AttributeGenerationOption(String id, String name) {
            setId(id);
            setName(name);
        }

        public AttributeGenerationOption(AttributeGenerationOption source) {
            AttributeGenerationOption safeSource = Objects.requireNonNullElseGet(
                source,
                AttributeGenerationOption::new
            );
            setId(safeSource.id);
            setName(safeSource.name);
            setSteps(safeSource.steps);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = Objects.toString(id, "").trim();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = Objects.toString(name, "").trim();
        }

        public List<AttributeGenerationStep> getSteps() {
            ArrayList<AttributeGenerationStep> copy = new ArrayList<>();
            for (AttributeGenerationStep step : steps) {
                copy.add(new AttributeGenerationStep(step));
            }
            return copy;
        }

        public void setSteps(Collection<AttributeGenerationStep> steps) {
            Collection<AttributeGenerationStep> safeSteps = Objects.requireNonNullElse(steps, List.of());
            ArrayList<AttributeGenerationStep> copy = new ArrayList<>();
            for (AttributeGenerationStep step : safeSteps) {
                AttributeGenerationStep safeStep = new AttributeGenerationStep(step);
                if (!safeStep.getMethodType().isEmpty()) {
                    copy.add(safeStep);
                }
            }
            this.steps = copy;
        }

        public void addStep(AttributeGenerationStep step) {
            AttributeGenerationStep safeStep = new AttributeGenerationStep(step);
            if (!safeStep.getMethodType().isEmpty()) {
                steps.add(safeStep);
            }
        }

        private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
            stream.defaultReadObject();
            if (id == null) {
                id = "";
            }
            if (name == null) {
                name = "";
            }
            setSteps(steps);
        }
    }

    public static final class AttributeGenerationStep implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        public static final String METHOD_STANDARD_ARRAY = "standard_array";
        public static final String METHOD_DICE = "dice";
        public static final String METHOD_POINT_BUY = "point_buy";
        public static final String APPLICATION_SET = "set";
        public static final String APPLICATION_ADD = "add";
        public static final String APPLICATION_SPEND = "spend";

        private String methodType = "";
        private String applicationMode = APPLICATION_SET;

        public AttributeGenerationStep() {
        }

        public AttributeGenerationStep(String methodType, String applicationMode) {
            setMethodType(methodType);
            setApplicationMode(applicationMode);
        }

        public AttributeGenerationStep(AttributeGenerationStep source) {
            AttributeGenerationStep safeSource = Objects.requireNonNullElseGet(
                source,
                AttributeGenerationStep::new
            );
            setMethodType(safeSource.methodType);
            setApplicationMode(safeSource.applicationMode);
        }

        public String getMethodType() {
            return methodType;
        }

        public void setMethodType(String methodType) {
            this.methodType = normalizeMethodType(methodType);
        }

        public String getApplicationMode() {
            return applicationMode;
        }

        public void setApplicationMode(String applicationMode) {
            this.applicationMode = normalizeApplicationMode(applicationMode);
        }

        public static String normalizeMethodType(String methodType) {
            String safeType = Objects.toString(methodType, "").trim().toLowerCase();
            if (safeType.equals(METHOD_STANDARD_ARRAY) || safeType.equals(METHOD_DICE) || safeType.equals(METHOD_POINT_BUY)) {
                return safeType;
            }
            return "";
        }

        public static String normalizeApplicationMode(String applicationMode) {
            String safeMode = Objects.toString(applicationMode, "").trim().toLowerCase();
            if (safeMode.equals(APPLICATION_ADD) || safeMode.equals(APPLICATION_SPEND)) {
                return safeMode;
            }
            return APPLICATION_SET;
        }

        private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
            stream.defaultReadObject();
            setMethodType(methodType);
            setApplicationMode(applicationMode);
        }
    }
}
