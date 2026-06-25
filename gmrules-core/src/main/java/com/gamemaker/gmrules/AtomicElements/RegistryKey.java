/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.AtomicElements;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Typed key for accessing atomic registries on the Game object.
 */
public final class RegistryKey<T extends AtomicRegistry> implements Serializable {

// *** MEMBERS ***
    private static final long serialVersionUID = 1L;
    private final String name;
    private final Class<T> type;
    private final transient Supplier<T> factory;

    public static final RegistryKey<AttributeTypes> ATTRIBUTE_TYPES =
        new RegistryKey<>("attributeTypes", AttributeTypes.class, AttributeTypes::new);
    public static final RegistryKey<SkillCategories> SKILL_CATEGORIES =
        new RegistryKey<>("skillCategories", SkillCategories.class, SkillCategories::new);
    public static final RegistryKey<MovementTypes> MOVEMENT_TYPES =
        new RegistryKey<>("movementTypes", MovementTypes.class, MovementTypes::new);
    public static final RegistryKey<EffectTypes> EFFECT_TYPES =
        new RegistryKey<>("effectTypes", EffectTypes.class, EffectTypes::new);
    public static final RegistryKey<EquipmentTypes> EQUIPMENT_TYPES =
        new RegistryKey<>("equipmentTypes", EquipmentTypes.class, EquipmentTypes::new);
    public static final RegistryKey<AdvantageTypes> ADVANTAGE_TYPES =
        new RegistryKey<>("advantageTypes", AdvantageTypes.class, AdvantageTypes::new);
    public static final RegistryKey<FlawTypes> FLAW_TYPES =
        new RegistryKey<>("flawTypes", FlawTypes.class, FlawTypes::new);
    public static final RegistryKey<SoftwareTypes> SOFTWARE_TYPES =
        new RegistryKey<>("softwareTypes", SoftwareTypes.class, SoftwareTypes::new);
    public static final RegistryKey<SpellSchools> SPELL_SCHOOLS =
        new RegistryKey<>("spellSchools", SpellSchools.class, SpellSchools::new);
    private static final Map<String, RegistryKey<?>> KEYS_BY_NAME = buildKeyMap();

// *** CONSTRUCTORS ***
    private RegistryKey(String name, Class<T> type, Supplier<T> factory) {
        this.name = Objects.toString(name, "");
        this.type = Objects.requireNonNull(type, "type");
        this.factory = Objects.requireNonNull(factory, "factory");
    }

// *** METHODS ***
    public String getName() {
        return name;
    }

    public Class<T> getType() {
        return type;
    }

    public T createDefault() {
        return factory.get();
    }

    private static Map<String, RegistryKey<?>> buildKeyMap() {
        Map<String, RegistryKey<?>> keys = new HashMap<>();
        registerKey(keys, ATTRIBUTE_TYPES);
        registerKey(keys, SKILL_CATEGORIES);
        registerKey(keys, MOVEMENT_TYPES);
        registerKey(keys, EFFECT_TYPES);
        registerKey(keys, EQUIPMENT_TYPES);
        registerKey(keys, ADVANTAGE_TYPES);
        registerKey(keys, FLAW_TYPES);
        registerKey(keys, SOFTWARE_TYPES);
        registerKey(keys, SPELL_SCHOOLS);
        return keys;
    }

    private static void registerKey(Map<String, RegistryKey<?>> keys, RegistryKey<?> key) {
        keys.put(key.getName(), key);
    }

    private Object readResolve() {
        RegistryKey<?> resolved = KEYS_BY_NAME.get(name);
        return resolved == null ? this : resolved;
    }
}
