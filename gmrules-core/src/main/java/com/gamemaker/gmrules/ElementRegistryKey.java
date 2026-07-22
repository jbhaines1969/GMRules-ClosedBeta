/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules;

import com.gamemaker.gmrules.AtomicElements.Attribute;
import com.gamemaker.gmrules.AtomicElements.SpellComponents;
import com.gamemaker.gmrules.CharacterElements.Advantage;
import com.gamemaker.gmrules.CharacterElements.CharacterClass;
import com.gamemaker.gmrules.CharacterElements.Flaw;
import com.gamemaker.gmrules.CharacterElements.Race;
import com.gamemaker.gmrules.CharacterElements.Skill;
import com.gamemaker.gmrules.GameElements.Armor;
import com.gamemaker.gmrules.GameElements.Creature;
import com.gamemaker.gmrules.GameElements.Currency;
import com.gamemaker.gmrules.GameElements.DamageType;
import com.gamemaker.gmrules.GameElements.Deity;
import com.gamemaker.gmrules.GameElements.Equipment;
import com.gamemaker.gmrules.GameElements.Material;
import com.gamemaker.gmrules.GameElements.NaturalWeapon;
import com.gamemaker.gmrules.GameElements.Pantheon;
import com.gamemaker.gmrules.GameElements.Software;
import com.gamemaker.gmrules.GameElements.Spell;
import com.gamemaker.gmrules.GameElements.Weapon;
import com.gamemaker.gmrules.GameMechanics.DifficultySystem;
import com.gamemaker.gmrules.SupportElements.Effect;
import com.gamemaker.gmrules.SupportElements.Status;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Typed key for accessing element registries on the Game object.
 */
public final class ElementRegistryKey<T extends GameElement> implements Serializable {

// *** MEMBERS ***
    private static final long serialVersionUID = 1L;
    private final String name;
    private final Class<T> type;
    private final transient Supplier<ElementRegistry<T>> factory;

    public static final ElementRegistryKey<Attribute> ATTRIBUTES =
        new ElementRegistryKey<>("attributes", Attribute.class, ElementRegistry::new);
    public static final ElementRegistryKey<DifficultySystem> DIFFICULTY_SYSTEMS =
        new ElementRegistryKey<>("difficultySystems", DifficultySystem.class, ElementRegistry::new);
    public static final ElementRegistryKey<SpellComponents> SPELL_COMPONENTS =
        new ElementRegistryKey<>("spellComponents", SpellComponents.class, ElementRegistry::new);
    public static final ElementRegistryKey<Skill> SKILLS =
        new ElementRegistryKey<>("skills", Skill.class, ElementRegistry::new);
    public static final ElementRegistryKey<CharacterClass> CHARACTER_CLASSES =
        new ElementRegistryKey<>("characterClasses", CharacterClass.class, ElementRegistry::new);
    public static final ElementRegistryKey<Race> RACES =
        new ElementRegistryKey<>("races", Race.class, ElementRegistry::new);
    public static final ElementRegistryKey<Advantage> ADVANTAGES =
        new ElementRegistryKey<>("advantages", Advantage.class, ElementRegistry::new);
    public static final ElementRegistryKey<Flaw> FLAWS =
        new ElementRegistryKey<>("flaws", Flaw.class, ElementRegistry::new);
    public static final ElementRegistryKey<Creature> CREATURES =
        new ElementRegistryKey<>("creatures", Creature.class, ElementRegistry::new);
    public static final ElementRegistryKey<Spell> SPELLS =
        new ElementRegistryKey<>("spells", Spell.class, ElementRegistry::new);
    public static final ElementRegistryKey<Software> SOFTWARE =
        new ElementRegistryKey<>("software", Software.class, ElementRegistry::new);
    public static final ElementRegistryKey<Equipment> EQUIPMENT =
        new ElementRegistryKey<>("equipment", Equipment.class, ElementRegistry::new);
    public static final ElementRegistryKey<Weapon> WEAPONS =
        new ElementRegistryKey<>("weapons", Weapon.class, ElementRegistry::new);
    public static final ElementRegistryKey<Armor> ARMOR =
        new ElementRegistryKey<>("armor", Armor.class, ElementRegistry::new);
    public static final ElementRegistryKey<NaturalWeapon> NATURAL_WEAPONS =
        new ElementRegistryKey<>("naturalWeapons", NaturalWeapon.class, ElementRegistry::new);
    public static final ElementRegistryKey<Effect> EFFECTS =
        new ElementRegistryKey<>("effects", Effect.class, ElementRegistry::new);
    public static final ElementRegistryKey<Status> STATUSES =
        new ElementRegistryKey<>("statuses", Status.class, ElementRegistry::new);
    public static final ElementRegistryKey<Pantheon> PANTHEONS =
        new ElementRegistryKey<>("pantheons", Pantheon.class, ElementRegistry::new);
    public static final ElementRegistryKey<Deity> DEITIES =
        new ElementRegistryKey<>("deities", Deity.class, ElementRegistry::new);
    public static final ElementRegistryKey<Currency> CURRENCIES =
        new ElementRegistryKey<>("currencies", Currency.class, ElementRegistry::new);
    public static final ElementRegistryKey<DamageType> DAMAGE_TYPES =
        new ElementRegistryKey<>("damageTypes", DamageType.class, ElementRegistry::new);
    public static final ElementRegistryKey<Material> MATERIALS =
        new ElementRegistryKey<>("materials", Material.class, ElementRegistry::new);
    private static final Map<String, ElementRegistryKey<?>> KEYS_BY_NAME = buildKeyMap();

// *** CONSTRUCTORS ***
    private ElementRegistryKey(String name, Class<T> type, Supplier<ElementRegistry<T>> factory) {
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

    public ElementRegistry<T> createDefault() {
        return factory.get();
    }

    private static Map<String, ElementRegistryKey<?>> buildKeyMap() {
        Map<String, ElementRegistryKey<?>> keys = new HashMap<>();
        registerKey(keys, ATTRIBUTES);
        registerKey(keys, DIFFICULTY_SYSTEMS);
        registerKey(keys, SPELL_COMPONENTS);
        registerKey(keys, SKILLS);
        registerKey(keys, CHARACTER_CLASSES);
        registerKey(keys, RACES);
        registerKey(keys, ADVANTAGES);
        registerKey(keys, FLAWS);
        registerKey(keys, CREATURES);
        registerKey(keys, SPELLS);
        registerKey(keys, SOFTWARE);
        registerKey(keys, EQUIPMENT);
        registerKey(keys, WEAPONS);
        registerKey(keys, ARMOR);
        registerKey(keys, NATURAL_WEAPONS);
        registerKey(keys, EFFECTS);
        registerKey(keys, STATUSES);
        registerKey(keys, PANTHEONS);
        registerKey(keys, DEITIES);
        registerKey(keys, CURRENCIES);
        registerKey(keys, DAMAGE_TYPES);
        registerKey(keys, MATERIALS);
        return keys;
    }

    private static void registerKey(Map<String, ElementRegistryKey<?>> keys, ElementRegistryKey<?> key) {
        keys.put(key.getName(), key);
    }

    private Object readResolve() {
        ElementRegistryKey<?> resolved = KEYS_BY_NAME.get(name);
        return resolved == null ? this : resolved;
    }
}
