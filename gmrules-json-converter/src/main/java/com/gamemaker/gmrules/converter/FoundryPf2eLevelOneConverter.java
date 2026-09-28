package com.gamemaker.gmrules.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamemaker.gmrules.CatalogMetadata;
import com.gamemaker.gmrules.ElementRegistryKey;
import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameDiagnostic;
import com.gamemaker.gmrules.GameElement;
import com.gamemaker.gmrules.GameLicenseNotice;
import com.gamemaker.gmrules.AtomicElements.Attribute;
import com.gamemaker.gmrules.AtomicElements.SkillCategory;
import com.gamemaker.gmrules.AtomicElements.SkillCategories;
import com.gamemaker.gmrules.AtomicElements.RegistryKey;
import com.gamemaker.gmrules.CharacterElements.Background;
import com.gamemaker.gmrules.CharacterElements.CharacterClass;
import com.gamemaker.gmrules.CharacterElements.Heritage;
import com.gamemaker.gmrules.CharacterElements.Race;
import com.gamemaker.gmrules.CharacterElements.Skill;
import com.gamemaker.gmrules.GameElements.Action;
import com.gamemaker.gmrules.GameElements.Armor;
import com.gamemaker.gmrules.GameElements.Deity;
import com.gamemaker.gmrules.GameElements.Equipment;
import com.gamemaker.gmrules.GameElements.Spell;
import com.gamemaker.gmrules.GameElements.Weapon;
import com.gamemaker.gmrules.SupportElements.Status;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/** Translates the explicit ORC subset of Foundry PF2e into a provisional level-one Game. */
public final class FoundryPf2eLevelOneConverter {
    private static final List<Map.Entry<String, String>> ATTRIBUTE_SOURCE_NAMES = List.of(
        Map.entry("str", "Strength"),
        Map.entry("dex", "Dexterity"),
        Map.entry("con", "Constitution"),
        Map.entry("int", "Intelligence"),
        Map.entry("wis", "Wisdom"),
        Map.entry("cha", "Charisma")
    );
    private static final Set<String> DEFINITION_PACKS = Set.of(
        "actions", "ancestries", "ancestry-features", "backgrounds", "classes", "class-features", "deities",
        "equipment", "familiar-abilities", "feats", "heritages", "spells"
    );
    private static final Set<String> DEPENDENCY_PACKS = Set.of(
        "conditions", "feat-effects", "equipment-effects", "spell-effects", "other-effects"
    );
    private static final Map<String, String> COMPENDIUM_PACKS = Map.ofEntries(
        Map.entry("actions", "actions"), Map.entry("actionspf2e", "actions"),
        Map.entry("ancestryfeatures", "ancestry-features"), Map.entry("backgrounds", "backgrounds"),
        Map.entry("classes", "classes"), Map.entry("classfeatures", "class-features"),
        Map.entry("conditionitems", "conditions"), Map.entry("deities", "deities"),
        Map.entry("equipment-srd", "equipment"), Map.entry("feats-srd", "feats"),
        Map.entry("heritages", "heritages"), Map.entry("spell-effects", "spell-effects"),
        Map.entry("spells-srd", "spells")
    );
    private static final Pattern COMPENDIUM_REFERENCE = Pattern.compile(
        "Compendium\\.pf2e\\.([A-Za-z0-9_-]+)\\.Item\\.([A-Za-z0-9]+)"
    );

    private final ObjectMapper mapper = new ObjectMapper();

    public FoundryConversionReport convert(Path packsRoot) throws IOException {
        Path root = Objects.requireNonNull(packsRoot, "packsRoot").toAbsolutePath().normalize();
        if (!Files.isDirectory(root)) throw new IOException("Foundry pack root does not exist: " + root);

        LinkedHashMap<String, SourceRecord> records = loadRecords(root);
        LinkedHashSet<String> selected = selectSeeds(records);
        selectDependencies(records, selected);

        Game game = createGame();
        ArrayList<GameDiagnostic> diagnostics = new ArrayList<>();
        LinkedHashMap<String, Integer> counts = new LinkedHashMap<>();
        HashMap<String, String> sourceToCoreId = new HashMap<>();
        registerFoundation(game, sourceToCoreId, counts, diagnostics);

        ArrayList<SourceRecord> ordered = new ArrayList<>();
        for (String key : selected) ordered.add(records.get(key));
        ordered.sort(Comparator.comparingInt(this::importOrder).thenComparing(record -> text(record.node, "name")));
        for (SourceRecord record : ordered) {
            importRecord(game, record, sourceToCoreId, counts, diagnostics);
        }
        resolveHeritageAncestries(game, ordered, sourceToCoreId, diagnostics);
        resolveLevelOneRelationships(game, ordered, sourceToCoreId);
        counts.put("heritages", game.getElementRegistry(ElementRegistryKey.HERITAGES).getAll().size());

        int excludedOgl = (int) records.values().stream().filter(record -> "OGL".equals(record.license())).count();
        int excludedUnknown = (int) records.values().stream()
            .filter(record -> !"ORC".equals(record.license()) && !"OGL".equals(record.license())).count();
        diagnostics.add(new GameDiagnostic(
            "PROVISIONAL_LEVEL_ONE_CATALOG", GameDiagnostic.Severity.WARNING,
            "Catalog includes level-one character options and direct ORC dependencies only. Full legality and combat readiness are not implemented."
        ));
        diagnostics.add(new GameDiagnostic(
            "UNSUPPORTED_FOUNDRY_RULES", GameDiagnostic.Severity.WARNING,
            "Foundry rule elements are recorded by type for diagnostics but are not executed or exposed as a consumer rules language."
        ));
        diagnostics.add(new GameDiagnostic(
            "UNSUPPORTED_ATTRIBUTE_GENERATION", GameDiagnostic.Severity.WARNING,
            "PF2e staged Attribute choices and contributions require the future core generation-session contract; this provisional Game does not advertise dice, array, or point-buy generation."
        ));
        game.setCatalogDiagnostics(diagnostics);
        return new FoundryConversionReport(game, counts, excludedOgl, excludedUnknown);
    }

    private LinkedHashMap<String, SourceRecord> loadRecords(Path root) throws IOException {
        LinkedHashMap<String, SourceRecord> records = new LinkedHashMap<>();
        Set<String> packs = new LinkedHashSet<>(DEFINITION_PACKS);
        packs.addAll(DEPENDENCY_PACKS);
        for (String pack : packs) {
            Path packPath = root.resolve(pack);
            if (!Files.isDirectory(packPath)) continue;
            try (Stream<Path> files = Files.walk(packPath)) {
                for (Path path : files.filter(Files::isRegularFile).filter(p -> p.toString().endsWith(".json")).toList()) {
                    if (path.getFileName().toString().equals("_folders.json")) continue;
                    JsonNode node = mapper.readTree(path.toFile());
                    String sourceId = text(node, "_id");
                    if (!sourceId.isEmpty()) records.put(pack + ":" + sourceId, new SourceRecord(pack, path, node));
                }
            }
        }
        return records;
    }

    private LinkedHashSet<String> selectSeeds(Map<String, SourceRecord> records) {
        LinkedHashSet<String> selected = new LinkedHashSet<>();
        for (Map.Entry<String, SourceRecord> entry : records.entrySet()) {
            SourceRecord record = entry.getValue();
            if (!"ORC".equals(record.license()) || !DEFINITION_PACKS.contains(record.pack)) continue;
            int level = level(record.node);
            if (level <= 1 || level < 0 || Set.of("actions", "ancestries", "backgrounds", "classes", "deities", "heritages", "familiar-abilities").contains(record.pack)) {
                selected.add(entry.getKey());
            }
        }
        return selected;
    }

    private void selectDependencies(Map<String, SourceRecord> records, LinkedHashSet<String> selected) {
        Deque<String> pending = new ArrayDeque<>(selected);
        while (!pending.isEmpty()) {
            SourceRecord record = records.get(pending.removeFirst());
            for (String reference : mechanicalReferences(record.node.path("system"))) {
                Matcher matcher = COMPENDIUM_REFERENCE.matcher(reference);
                if (!matcher.find()) continue;
                String pack = COMPENDIUM_PACKS.getOrDefault(matcher.group(1).toLowerCase(Locale.ROOT), "");
                String key = pack + ":" + matcher.group(2);
                SourceRecord target = records.get(key);
                if (target != null && "ORC".equals(target.license()) && selected.add(key)) pending.addLast(key);
            }
        }
    }

    private List<String> mechanicalReferences(JsonNode node) {
        ArrayList<String> references = new ArrayList<>();
        collectReferences(node, "", references);
        return references;
    }

    private void collectReferences(JsonNode node, String fieldName, List<String> references) {
        if ("description".equals(fieldName)) return;
        if (node.isTextual()) {
            String value = node.asText("");
            if (COMPENDIUM_REFERENCE.matcher(value).find()) references.add(value);
            return;
        }
        if (node.isArray()) node.forEach(child -> collectReferences(child, fieldName, references));
        if (node.isObject()) node.fields().forEachRemaining(entry -> collectReferences(entry.getValue(), entry.getKey(), references));
    }

    private Game createGame() {
        Game game = new Game("Pathfinder 2e Remaster - Level 1 Provisional", "1-provisional", "GMRules JSON Converter");
        game.setDescription("ORC-only provisional catalog for human-guided level-one Pathfinder character creation. Not proof of complete rules legality or combat readiness.");
        game.setGameType("Fantasy");
        game.setSystemName("races", "Ancestries");
        game.setSystemName("heritages", "Heritages");
        game.setSystemName("characterClasses", "Classes");
        game.getAttributeGenerationMethod().setGenerationType("");
        game.setAttributeGenerationOptions(List.of());
        GameLicenseNotice notice = new GameLicenseNotice();
        notice.setLicenseName("Open RPG Creative License (ORC)");
        notice.setNotice("This Game contains rules material selected only from source records explicitly marked ORC. The ORC License is available at www.azoralaw.com/orclicense. All warranties are disclaimed as set forth therein.");
        notice.setUpstreamAttribution("Pathfinder Player Core © 2023 Paizo Inc.; Pathfinder Player Core 2 © 2024 Paizo Inc.; additional included source titles are retained on their individual catalog elements.");
        notice.setDownstreamAttribution("Converted into GMRules format by the GMRules JSON Converter.");
        notice.setReservedMaterial("Trademarks, proper names, artwork, trade dress, story elements, and other Reserved Material are not licensed merely by inclusion in an upstream data source.");
        notice.setLicensedMaterial("Game mechanics and rules text explicitly designated under the ORC License by each included source record.");
        game.setLicenseNotices(List.of(notice));
        return game;
    }

    private void registerFoundation(Game game, Map<String, String> ids, Map<String, Integer> counts, List<GameDiagnostic> diagnostics) {
        ArrayList<String> attributeOrder = new ArrayList<>();
        for (Map.Entry<String, String> attributeSourceName : ATTRIBUTE_SOURCE_NAMES) {
            String name = attributeSourceName.getValue();
            Attribute attribute = new Attribute(name);
            register(game, ElementRegistryKey.ATTRIBUTES, attribute, "attribute:" + slug(name), ids, counts, diagnostics);
            String coreId = ids.get("attribute:" + slug(name));
            if (coreId != null && !coreId.isBlank()) {
                attributeOrder.add(coreId);
            }
        }
        addAttributeSourceAliases(ids, diagnostics);
        if (attributeOrder.size() == ATTRIBUTE_SOURCE_NAMES.size()) {
            game.setAttributeAssignmentOrder(attributeOrder);
        }
        SkillCategories categories = game.getRegistry(RegistryKey.SKILL_CATEGORIES);
        for (String category : List.of("Skill", "Feat", "Class Feature", "Ancestry Feature", "Familiar Ability")) {
            categories.register(new SkillCategory(slug(category), category, "", false));
        }
        for (String name : List.of("Acrobatics", "Arcana", "Athletics", "Crafting", "Deception", "Diplomacy", "Intimidation", "Medicine", "Nature", "Occultism", "Performance", "Religion", "Society", "Stealth", "Survival", "Thievery")) {
            Skill skill = new Skill(name);
            skill.setCategory("skill");
            skill.setType("Skill");
            register(game, ElementRegistryKey.SKILLS, skill, "skill:" + slug(name), ids, counts, new ArrayList<>());
        }
    }

    static void addAttributeSourceAliases(Map<String, String> ids, List<GameDiagnostic> diagnostics) {
        ATTRIBUTE_SOURCE_NAMES.forEach(attributeSourceName -> {
            String sourceSlug = attributeSourceName.getKey();
            String name = attributeSourceName.getValue();
            String coreId = ids.get("attribute:" + slug(name));
            if (coreId == null || coreId.isBlank()) {
                diagnostics.add(new GameDiagnostic(
                    "ATTRIBUTE_ALIAS_TARGET_MISSING",
                    GameDiagnostic.Severity.ERROR,
                    "The PF2e ability abbreviation could not be linked to its core Attribute definition.",
                    "attributes",
                    sourceSlug
                ));
                return;
            }
            ids.put("attribute:" + sourceSlug, coreId);
        });
    }

    private void importRecord(Game game, SourceRecord record, Map<String, String> ids, Map<String, Integer> counts, List<GameDiagnostic> diagnostics) {
        JsonNode node = record.node;
        String name = text(node, "name");
        String description = node.path("system").path("description").path("value").asText("");
        GameElement element;
        ElementRegistryKey<? extends GameElement> key;
        switch (record.pack) {
            case "ancestries" -> { element = new Race(name); key = ElementRegistryKey.RACES; }
            case "heritages" -> { element = new Heritage(name, description); key = ElementRegistryKey.HERITAGES; }
            case "backgrounds" -> { element = new Background(name, description); key = ElementRegistryKey.BACKGROUNDS; }
            case "classes" -> {
                CharacterClass characterClass = new CharacterClass(name, description);
                characterClass.setBaseHitPoints(node.path("system").path("hp").asInt(0));
                characterClass.setMaxLevel(20);
                element = characterClass;
                key = ElementRegistryKey.CHARACTER_CLASSES;
            }
            case "ancestry-features", "class-features", "feats", "familiar-abilities" -> {
                Skill skill = new Skill(name, description);
                String type = switch (record.pack) {
                    case "ancestry-features" -> "Ancestry Feature";
                    case "class-features" -> "Class Feature";
                    case "familiar-abilities" -> "Familiar Ability";
                    default -> "Feat";
                };
                skill.setCategory(slug(type));
                skill.setType(type);
                element = skill;
                key = ElementRegistryKey.SKILLS;
            }
            case "actions" -> {
                Action action = new Action(name, description);
                action.setActionType(text(node.path("system"), "actionType", "value"));
                action.setCategory(text(node.path("system"), "category"));
                action.setActionCost(node.path("system").path("actions").path("value").asInt(0));
                action.setFrequency(node.path("system").path("frequency").toString());
                element = action;
                key = ElementRegistryKey.ACTIONS;
            }
            case "conditions" -> { element = new Status(name, description); key = ElementRegistryKey.STATUSES; }
            case "spells" -> {
                Spell spell = new Spell(name, description);
                spell.setLevel(Math.max(0, level(node)));
                spell.setCastingTime(node.path("system").path("time").path("value").asText(""));
                spell.setRange(node.path("system").path("range").path("value").asText(""));
                spell.setDuration(node.path("system").path("duration").path("value").asText(""));
                spell.setTarget(node.path("system").path("target").path("value").asText(""));
                element = spell;
                key = ElementRegistryKey.SPELLS;
            }
            case "deities" -> { element = new Deity(name, description); key = ElementRegistryKey.DEITIES; }
            case "equipment" -> {
                String type = text(node, "type");
                if ("weapon".equals(type)) { element = new Weapon(name, description); key = ElementRegistryKey.WEAPONS; }
                else if ("armor".equals(type) || "shield".equals(type)) { element = new Armor(name, description, type); key = ElementRegistryKey.ARMOR; }
                else { element = new Equipment(name, description); key = ElementRegistryKey.EQUIPMENT; }
            }
            default -> {
                diagnostics.add(new GameDiagnostic("UNSUPPORTED_ELEMENT_TYPE", GameDiagnostic.Severity.WARNING, "No core catalog mapping exists for this dependency.", record.pack, name));
                return;
            }
        }
        element.setCatalogMetadata(metadata(record));
        registerUntyped(game, key, element, record.key(), ids, counts, diagnostics);
        if (!ids.containsKey(record.key()) && "class-features".equals(record.pack)
            && record.node.path("system").path("rules").toString().contains("GrantItem")) {
            Skill existing = game.getElementRegistry(ElementRegistryKey.SKILLS).getByName(name);
            if (existing != null) {
                ids.put(record.key(), existing.getId());
                ids.put("class-features:name:" + name.toLowerCase(Locale.ROOT), existing.getId());
            }
        }
    }

    private void resolveHeritageAncestries(Game game, List<SourceRecord> records, Map<String, String> ids, List<GameDiagnostic> diagnostics) {
        for (SourceRecord record : records) {
            if (!"heritages".equals(record.pack)) continue;
            String heritageId = ids.get(record.key());
            Heritage heritage = game.getElementRegistry(ElementRegistryKey.HERITAGES).getById(heritageId);
            if (heritage == null) continue;
            String ancestryUuid = record.node.path("system").path("ancestry").path("uuid").asText("");
            String ancestryName = record.node.path("system").path("ancestry").path("name").asText("");
            if (ancestryUuid.isBlank() || "any".equalsIgnoreCase(ancestryName)) {
                heritage.setUnrestrictedAncestry(true);
                continue;
            }
            Matcher matcher = COMPENDIUM_REFERENCE.matcher(ancestryUuid);
            String raceId = matcher.find() ? ids.get("ancestries:" + matcher.group(2)) : "";
            if (raceId == null || raceId.isBlank()) {
                game.getElementRegistry(ElementRegistryKey.HERITAGES).removeById(heritage.getId());
                ids.remove(record.key());
                diagnostics.add(new GameDiagnostic("OMITTED_HERITAGE_ANCESTRY", GameDiagnostic.Severity.WARNING, "Heritage was omitted because its required ancestry is not in the ORC-only catalog.", "heritage", heritage.getName()));
            } else {
                heritage.setAncestryIds(List.of(raceId));
            }
        }
    }

    private void resolveLevelOneRelationships(Game game, List<SourceRecord> records, Map<String, String> ids) {
        for (SourceRecord record : records) {
            String coreId = ids.get(record.key());
            if (coreId == null) continue;
            if ("classes".equals(record.pack)) {
                CharacterClass characterClass = game.getElementRegistry(ElementRegistryKey.CHARACTER_CLASSES).getById(coreId);
                characterClass.setAutomaticSkillsPerLevel(Map.of(1, resolvedItemIds(record.node.path("system").path("items"), 1, ids)));
            } else if ("backgrounds".equals(record.pack)) {
                Background background = game.getElementRegistry(ElementRegistryKey.BACKGROUNDS).getById(coreId);
                ArrayList<String> grants = resolvedItemIds(record.node.path("system").path("items"), 1, ids);
                record.node.path("system").path("trainedSkills").path("value").forEach(skill -> {
                    String skillId = ids.get("skill:" + slug(skill.asText("")));
                    if (skillId != null && !grants.contains(skillId)) grants.add(skillId);
                });
                background.setBackgroundSkillIds(grants);
            } else if ("ancestries".equals(record.pack)) {
                Race race = game.getElementRegistry(ElementRegistryKey.RACES).getById(coreId);
                for (String skillId : resolvedItemIds(record.node.path("system").path("items"), 1, ids)) {
                    race.addToArray("racialSkills", skillId);
                }
            } else if ("heritages".equals(record.pack)) {
                Heritage heritage = game.getElementRegistry(ElementRegistryKey.HERITAGES).getById(coreId);
                if (heritage != null) heritage.setGrantedSkillIds(resolvedRuleIds(record.node.path("system").path("rules"), ids));
            }
        }
    }

    private ArrayList<String> resolvedItemIds(JsonNode items, int maximumLevel, Map<String, String> ids) {
        ArrayList<String> result = new ArrayList<>();
        items.elements().forEachRemaining(item -> {
            if (item.path("level").asInt(0) > maximumLevel) return;
            String id = resolveReference(item.path("uuid").asText(""), ids);
            if (!id.isBlank() && !result.contains(id)) result.add(id);
        });
        return result;
    }

    private ArrayList<String> resolvedRuleIds(JsonNode rules, Map<String, String> ids) {
        ArrayList<String> result = new ArrayList<>();
        rules.forEach(rule -> {
            String id = resolveReference(rule.path("uuid").asText(""), ids);
            if (!id.isBlank() && !result.contains(id)) result.add(id);
        });
        return result;
    }

    private String resolveReference(String reference, Map<String, String> ids) {
        String safe = Objects.toString(reference, "").trim();
        String prefix = "Compendium.pf2e.";
        int itemMarker = safe.indexOf(".Item.");
        if (!safe.startsWith(prefix) || itemMarker < 0) return "";
        String sourcePack = safe.substring(prefix.length(), itemMarker).toLowerCase(Locale.ROOT);
        String pack = COMPENDIUM_PACKS.getOrDefault(sourcePack, "");
        String token = safe.substring(itemMarker + ".Item.".length()).split("#", 2)[0].trim();
        return Objects.toString(ids.getOrDefault(pack + ":" + token, ids.get(pack + ":name:" + token.toLowerCase(Locale.ROOT))), "");
    }

    private CatalogMetadata metadata(SourceRecord record) {
        JsonNode system = record.node.path("system");
        CatalogMetadata metadata = new CatalogMetadata();
        metadata.setSourceType(record.pack);
        metadata.setSourceTitle(system.path("publication").path("title").asText(""));
        metadata.setLicense(record.license());
        metadata.setRemastered(system.path("publication").path("remaster").asBoolean(false));
        metadata.setRarity(system.path("traits").path("rarity").asText(""));
        metadata.setLevel(level(record.node));
        ArrayList<String> traits = new ArrayList<>();
        system.path("traits").path("value").forEach(value -> traits.add(value.asText("")));
        metadata.setTraits(traits);
        ArrayList<String> rules = new ArrayList<>();
        system.path("rules").forEach(rule -> {
            String key = rule.path("key").asText("");
            if (!key.isBlank()) rules.add(key);
        });
        metadata.setUnsupportedRuleTypes(rules);
        return metadata;
    }

    private int importOrder(SourceRecord record) {
        return switch (record.pack) {
            case "ancestries" -> 10;
            case "feats" -> 20;
            case "ancestry-features" -> 21;
            case "class-features" -> 22;
            case "familiar-abilities" -> 23;
            case "heritages" -> 30;
            case "backgrounds" -> 40;
            case "classes" -> 50;
            case "actions" -> 60;
            case "spells" -> 70;
            case "equipment" -> 80;
            case "deities" -> 90;
            case "conditions" -> 100;
            default -> 1000;
        };
    }

    private static int level(JsonNode node) {
        JsonNode level = node.path("system").path("level");
        if (level.isInt()) return level.asInt();
        if (level.path("value").isNumber()) return level.path("value").asInt();
        return -1;
    }

    private static String text(JsonNode node, String... path) {
        JsonNode current = node;
        for (String part : path) current = current.path(part);
        return current.asText("").trim();
    }

    private static String slug(String value) {
        return Objects.toString(value, "").trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }

    private <T extends GameElement> void register(Game game, ElementRegistryKey<T> key, T element, String sourceKey, Map<String, String> ids, Map<String, Integer> counts, List<GameDiagnostic> diagnostics) {
        if (game.registerElement(key, element)) {
            ids.put(sourceKey, element.getId());
            String pack = sourceKey.substring(0, sourceKey.indexOf(':'));
            ids.put(pack + ":name:" + element.getName().toLowerCase(Locale.ROOT), element.getId());
            counts.merge(key.getName(), 1, Integer::sum);
        } else {
            diagnostics.add(new GameDiagnostic("DUPLICATE_NAME", GameDiagnostic.Severity.WARNING, "Core rejected a duplicate element name; the later record was omitted.", key.getName(), element.getName()));
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void registerUntyped(Game game, ElementRegistryKey key, GameElement element, String sourceKey, Map<String, String> ids, Map<String, Integer> counts, List<GameDiagnostic> diagnostics) {
        register(game, key, element, sourceKey, ids, counts, diagnostics);
    }

    private record SourceRecord(String pack, Path path, JsonNode node) {
        String key() { return pack + ":" + node.path("_id").asText(""); }
        String license() { return node.path("system").path("publication").path("license").asText("").trim().toUpperCase(Locale.ROOT); }
    }
}
