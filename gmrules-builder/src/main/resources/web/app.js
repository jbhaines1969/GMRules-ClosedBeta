const DEFAULT_FONT_SIZE_LEVEL = 1;

const state = {
  draftId: "",
  step: "splash",
  mode: "home",
  setupComplete: false,
  attributeGenerationType: "",
  attributeGenerationStages: [],
  attributeGenerationOptions: [],
  locale: "",
  strings: {},
  sessionToken: "",
  accountName: "",
  legacyGuest: false,
  admin: false,
  localMode: false,
  currencyId: "",
  chargenAttributes: [],
  chargenAttributeScores: {},
  chargenAttributeStepResults: {},
  chargenAttributeResultChoice: "",
  chargenPointBuyBaselineScores: {},
  chargenRaceId: "",
  chargenClassId: "",
  chargenClassSkillRanks: {},
  chargenSelectedSkillRanks: {},
  chargenSelectedSpellIds: [],
  chargenSelectedEquipmentIds: [],
  chargenSelectedWeaponIds: [],
  chargenSelectedArmorIds: [],
  chargenStartingMoneyMethod: "base",
  chargenStartingMoneyAmount: 0,
  chargenStartingMoneyCurrencyId: "",
  chargenResolvedArmorClass: 0,
  chargenGameId: "",
  chargenGameHash: "",
  chargenGameName: "",
  chargenCharacterName: "",
  chargenAttributeGenerationChoice: "",
  chargenRolledAttributeValues: [],
  chargenDiceRollAssignments: {},
  chargenSelectedArrayType: "",
  chargenArrayAssignments: {},
  chargenRuleModeSelections: {},
  chargenDiceSubstitutionsUsed: 0,
  chargenDraftText: "",
  chargenCharacterDraftId: "",
  chargenServerSaveInFlight: false,
  chargenServerSaveQueued: false,
  canCreateCharacterDraft: true,
  lastAttributeTypeKey: "",
  lastSkillCategoryKey: "",
  lastEffectTypeKeys: [],
  lastStatusEffectTypeKeys: [],
  applyAttributeModifiersToAllAttributes: false,
  attributeModifiers: [],
  defaultAttributeMinScore: 0,
  defaultAttributeMaxScore: 0,
  systemNames: {},
  fontSizeAllScreens: false,
  fontSizeGlobalLevel: DEFAULT_FONT_SIZE_LEVEL,
  fontSizeLevelsByScreen: {},
  mechanicDescriptions: {},
  mechanicDescriptionsDraftId: "",
  mechanicDescriptionsLoadingDraftId: "",
  mechanicDescriptionsLoadPromise: null,
};

const steps = [
  {
    id: "setup",
    labelKey: "setup.title",
    fallback: "Game Setup",
    tutorialKey: "web.download.note",
    tutorialFallback: "Two Game rule files are saved to your account at a time for easy access. Use the Download .gmrf button in the top bar anytime if you'd like to save the file to your computer.",
  },
  {
    id: "measurements",
    labelKey: "measurements.title",
    fallback: "Measurements",
    tutorialParagraphs: [
      {
        key: "measurements.intro",
        fallback: "Choose which system of measurement your game will use by default, Metric or Imperial (US Customary).",
      },
      {
        key: "measurements.intro_time_units",
        fallback: "On this screen you will also enter time units for different game actions and events. The standard set is included, and you can remove any you don't use and add any that your game uses that aren't included. A round is usually the length of time for one simple action to complete, a turn is generally the length of time for more complex operations. Include as many as you'd like, if it isn't needed later it can always be removed or ignored.",
      },
    ],
  },
  {
    id: "dice",
    labelKey: "dice.title",
    fallback: "Dice Options",
    tutorialKey: "dice.intro",
    tutorialFallback: "In RPGs, dice rolls are often used to create characters, randomize selections, or resolve outcomes. Did your attack succeed? How much damage did that spell do? What treasures are contained in the dragon's hoard, or the corporate CEO's safe? Select which dice your game system uses, and enter any custom ranges you need.",
  },
  {
    id: "attribute-types",
    labelKey: "attrtypes.title",
    fallback: "Attribute Categories",
    tutorialKey: "attrtypes.intro",
    tutorialFallback: "Attribute categories are optional groups, not the attributes themselves. For example, Vampire: The Masquerade groups attributes into Physical, Social, and Mental, while many versions of D&D skip categories entirely.",
  },
  {
    id: "attributes",
    labelKey: "attributes.title",
    fallback: "Attributes",
    tutorialKey: "attributes.intro",
    tutorialFallback: "Attributes are the core stats for characters, such as Strength or Intelligence. Add the attributes your game uses, and optionally assign them to a category.",
  },
  {
    id: "attribute-generation",
    labelKey: "attrgen.title",
    fallback: "Attribute Generation",
    tutorialPages: [
      {
        titleKey: "attrgen.info.overview.title",
        titleFallback: "Understanding Attributes",
        paragraphs: [
          {
            key: "attrgen.intro",
            fallback: "Attributes describe a character's fundamental capabilities—the broad qualities that help define what they are naturally good or bad at. Common examples include Strength, Intelligence, Agility, or Charisma, but your game can use any qualities that fit its setting and rules. A political drama might emphasize Influence and Reputation, while a survival game might focus on Endurance, Awareness, and Resourcefulness.",
          },
          {
            key: "attrgen.intro_values",
            fallback: "An attribute usually has a score or value that represents the character's capability. That value can affect dice rolls, determine whether the character qualifies for an option, modify another statistic, or establish limits within the game. Higher does not have to mean better, and attributes do not have to work identically; the important part is deciding what each attribute represents and how its value affects play.",
          },
          {
            key: "attrgen.intro_scope",
            fallback: "Attributes should describe broad capabilities rather than individual actions. For example, Agility might influence many activities involving speed or coordination, while Lockpicking would usually be modeled as a more specific skill. Your game may use only a few broad attributes, many specialized attributes, or no attributes at all. Every part of this section is optional.",
          },
          {
            key: "attrgen.intro_flow",
            fallback: "Attribute setup is divided across the next six screens. First, Attribute Generation defines how players receive or choose their starting attribute scores, using methods such as dice rolling, point buy, standard arrays, or combinations of those methods. Next, Attribute Categories lets you create optional groups for organizing related attributes. Then, Attributes is where you define the attributes themselves, explain what they represent, assign them to categories, and configure their allowed values and mechanical effects.",
          },
          {
            key: "attrgen.intro_detail_screens",
            fallback: "The final three screens are data-entry screens for the generation methods used by your player options. Use them to define the actual standard arrays, dice procedures, and point-buy rules that characters will use. They come after Attributes because some entries may depend on the completed attribute list. Methods your game does not use can be left alone.",
          },
        ],
      },
      {
        titleKey: "attrgen.info.specifics.title",
        titleFallback: "Using Attribute Generation",
        paragraphs: [
          {
            key: "attrgen.intro_specifics_options",
            fallback: "On this screen, you will define the ways players can generate their characters' Attribute scores. You are choosing the options players will see during character creation; the detailed rules for Standard Array/Base Scores, Dice Rolling, and Point Buy are entered on their later screens.",
          },
          {
            key: "attrgen.intro_specifics_player_options",
            fallback: "Player Options are alternative generation methods offered to the player. Give each option a clear name, such as 'Roll for Attributes,' 'Standard Array/Base Scores,' or 'Base Scores with Point Buy.' If you create more than one option, the player will choose between them during character creation. If there is only one, it will be used automatically.",
          },
          {
            key: "attrgen.intro_specifics_first_step",
            fallback: "The First Step establishes the character's initial Attribute scores. Standard Array/Base Scores gives the player prepared values to assign or a base for a hybrid method. Dice Rolling generates values using the dice rules you define. Point Buy gives the player a budget that can be spent to build their scores.",
          },
          {
            key: "attrgen.intro_specifics_second_step",
            fallback: "The optional Second Step combines another generation method with the first result. Add increases the first scores, Spend uses the first scores as a point-buy baseline, and Choose generates both results before the player selects which complete result to use. Standard Array/Base Scores is available only as the first step.",
          },
          {
            key: "attrgen.intro_specifics_score_limits",
            fallback: "Score Limits define the lowest and highest scores allowed for Attributes. Select 'All attributes use the same score limits' when every Attribute follows the same range. Leave it unselected when individual Attributes need different limits; those limits can be entered while creating each Attribute.",
          },
          {
            key: "attrgen.intro_specifics_modifiers",
            fallback: "Default Modifiers translate Attribute scores into the bonuses or penalties used during play. Select 'All attributes use the same modifier list' when every Attribute uses the same score-to-modifier progression. If different Attributes calculate modifiers differently, leave it unselected and define their modifier lists individually on the Attributes screen.",
          },
        ],
      },
    ],
  },
  {
    id: "standard-array",
    labelKey: "attrgen.standard.title",
    fallback: "Standard Array/Base Scores",
    tutorialKey: "attrgen.standard.intro",
    tutorialFallback: "Define fixed attribute arrays and choose which one is the default option.",
  },
  {
    id: "dice-rolling",
    labelKey: "attrgen.dice.title",
    fallback: "Dice Rolling",
    tutorialKey: "attrgen.dice.intro",
    tutorialFallback: "Set how many attribute sets are rolled and define the dice terms used for each roll.",
  },
  {
    id: "points-buy",
    labelKey: "attrgen.point.title",
    fallback: "Points Buy",
    tutorialKey: "attrgen.point.intro",
    tutorialFallback: "Set the starting points and bounds for point-buy attribute generation.",
  },
  {
    id: "hit-points",
    labelKey: "hp.title",
    fallback: "Hit Points",
    tutorialParagraphs: [
      {
        key: "hp.intro",
        fallback: "Hit Points are a numerical measure of how much harm, strain, or danger a character can endure before suffering serious consequences. Depending on the game, they may represent physical health alone or a combination of injury, stamina, luck, resolve, and the ability to avoid a decisive blow.",
      },
      {
        key: "hp.intro.play",
        fallback: "Damage usually reduces Hit Points, while healing, rest, or other recovery restores them. Reaching zero commonly triggers an important game state such as unconsciousness, critical injury, dying, or death, but your rules determine the exact consequence. Hit Points can also be used for creatures, vehicles, objects, or any other element that can be damaged.",
      },
      {
        key: "hp.intro.design",
        fallback: "The amount of Hit Points available, how quickly they increase, and how easily they return strongly shape the feel of play. Large or rapidly growing totals support durable, heroic characters; smaller or mostly fixed totals make danger more immediate. This screen defines how characters begin and gain Hit Points so the descendant applications can calculate those totals consistently.",
      },
    ],
  },
  {
    id: "armor-class",
    labelKey: "armorclass.title",
    fallback: "Armor Class",
    tutorialKey: "armorclass.intro",
    tutorialFallback: "Set the starting armor class value and optional attribute modifier used for armor class.",
  },
  {
    id: "currency",
    labelKey: "currency.title",
    fallback: "Currency",
    systemNameKey: "currencies",
    tutorialKey: "currency.intro",
    tutorialFallback: "A currency system is the overall economy (like gold standard or credits). Denominations are the specific coins or bills within it, each with a value.",
  },
  {
    id: "effect-types",
    labelKey: "effecttypes.title",
    fallback: "Affected Systems",
    tutorialKey: "effecttypes.intro",
    tutorialFallback: "Affected Systems identify parts of the game, or recurring rule interactions, that actions, events, Effects, and Statuses can change or invoke. Use short, reusable names such as Damage, Resistance, Immunity, Armor, Speed, Movement, or Apply Status.",
  },
  {
    id: "damage-types",
    labelKey: "damagetypes.title",
    fallback: "Damage Types",
    systemNameKey: "damage-types",
    tutorialKey: "damagetypes.intro",
    tutorialFallback: "Define reusable damage types before adding damaging effects, weapons, equipment, and spells.",
  },
  {
    id: "statuses",
    labelKey: "statuses.title",
    fallback: "Statuses",
    tutorialKey: "statuses.intro",
    tutorialFallback: "Statuses are reusable conditions that effects can apply to characters.",
  },
  {
    id: "effects",
    labelKey: "effects.title",
    fallback: "Effects",
    tutorialKey: "effects.intro",
    tutorialFallback: "Effects are reusable rules snippets that can be referenced by skills, spells, and equipment.",
  },
  {
    id: "equipment",
    labelKey: "equipment.title",
    fallback: "Equipment",
    tutorialKey: "equipment.intro",
    tutorialFallback: "List the gear available in your game and describe what each item does.",
  },
  {
    id: "weapons",
    labelKey: "weapons.title",
    fallback: "Weapons",
    tutorialKey: "weapons.intro",
    tutorialFallback: "Define the weapons available in your game along with their damage and effects.",
  },
  {
    id: "skills",
    labelKey: "skills.title",
    fallback: "Skills",
    tutorialKey: "skills.intro",
    tutorialFallback: "Skills describe what characters can do and can reference effects for automation.",
  },
  {
    id: "spells",
    labelKey: "spells.title",
    fallback: "Spells",
    tutorialKey: "spells.intro",
    tutorialFallback: "Spells define magic options, including level, school, and linked effects.",
  },
  {
    id: "pantheons",
    labelKey: "pantheons.title",
    fallback: "Pantheons",
    systemNameKey: "pantheons",
    tutorialKey: "pantheons.intro",
    tutorialFallback: "Pantheons organize related deities by culture, region, or theme.",
  },
  {
    id: "deities",
    labelKey: "deities.title",
    fallback: "Deities",
    systemNameKey: "deities",
    tutorialKey: "deities.intro",
    tutorialFallback: "Deities define divine figures, their portfolios, symbols, spell access, and pantheon membership.",
  },
  {
    id: "races",
    labelKey: "races.title",
    fallback: "Races",
    tutorialKey: "races.intro",
    tutorialFallback: "Races define species options and the traits or limits that come with them.",
  },
  {
    id: "classes",
    labelKey: "classes.title",
    fallback: "Classes",
    tutorialKey: "classes.intro",
    tutorialFallback: "Classes bundle progression rules, requirements, and starting packages.",
  },
];

const tutorialScreens = steps.concat([
  {
    id: "home",
    labelKey: "web.home.title",
    fallback: "Welcome to GMRules",
    tutorialParagraphs: [
      {
        key: "web.home.intro_ttrpg",
        fallback: "A tabletop RPG is a shared story game where players create characters, explore imagined worlds, and use rules and dice to decide what happens.",
      },
      {
        key: "web.home.intro_purpose",
        fallback: "GMRules helps you turn your custom game's rules, options, and content into a portable file that can power a digital ecosystem for your creation.",
      },
      {
        key: "web.home.intro_optional_sections",
        fallback: "In the following screens, you will enter the details and descriptions for your game rules. Every section is optional, if your game doesn't use them or if you just don't want that level of complexity from this specific tool.",
      },
      {
        key: "web.home.intro_application_suite",
        fallback: "The description you enter here will be presented to users of the suite of applications designed to bring your game to life, from character generation to world building and campaign management. You make the rules, we make the tools.",
      },
    ],
  },
]);

const tutorialSpecificPages = {
  setup: {
    titleKey: "setup.info.specifics.title",
    titleFallback: "Using Game Setup",
    paragraphs: [
      {
        key: "setup.info.specifics.name",
        fallback: "Game Name is the required identity for this ruleset. Saving a nonblank name completes Game Setup, unlocks the Rules Builder navigation, and gives exported .gmrf files a recognizable filename.",
      },
      {
        key: "setup.info.specifics.description",
        fallback: "Game Description is the broad introduction to your game. Use it to explain the setting, premise, intended style of play, and anything a new player should understand before creating a character. Descendant applications can present this creator-written text when introducing the ruleset.",
      },
      {
        key: "setup.info.specifics.type",
        fallback: "Game Type classifies the ruleset by its general genre or style. Choose the closest available option; it is descriptive metadata and does not force the game to use particular mechanics.",
      },
      {
        key: "setup.info.specifics.save",
        fallback: "Continue saves all three fields to the active draft before moving to Weights & Measures. You can return and revise them later, and you can download the current .gmrf from the top bar whenever you want a portable copy.",
      },
    ],
  },
  measurements: {
    titleKey: "measurements.info.specifics.title",
    titleFallback: "Using Weights & Measures",
    paragraphs: [
      {
        key: "measurements.info.specifics.weight",
        fallback: "Weight System selects the default convention used when the suite presents weights and measurements. Choose Metric or Imperial (US Customary) according to the language of your rules; individual descriptions can still mention whatever units your game requires.",
      },
      {
        key: "measurements.info.specifics.time_unit",
        fallback: "Each Time Unit gives a game term a duration. Enter its name, the number in Amount, and the existing Unit that amount is measured in. For example, a round could be 6 seconds, while a turn could be 10 rounds.",
      },
      {
        key: "measurements.info.specifics.relationships",
        fallback: "Time units may be built from smaller units, allowing the ruleset to preserve relationships between seconds, rounds, turns, watches, days, or setting-specific periods. Add smaller base units before defining larger units that depend on them.",
      },
      {
        key: "measurements.info.specifics.manage",
        fallback: "The list shows the units currently available to the ruleset. Add Time Unit opens a blank editor, Edit opens that editor for an existing unit, and Remove deletes one you do not use. Include only the units that help descendant applications describe durations or schedule game events.",
      },
    ],
  },
  dice: {
    titleKey: "dice.info.specifics.title",
    titleFallback: "Using Dice Options",
    paragraphs: [
      {
        key: "dice.info.specifics.standard",
        fallback: "Standard Dice identifies the physical or digital dice used anywhere in your game. Select every die size that later rules may reference. These choices make dice available to the ruleset; they do not by themselves create a roll or determine when a die is used.",
      },
      {
        key: "dice.info.specifics.custom",
        fallback: "Custom Ranges represent random values that are not covered by the standard dice list. Add Range opens an editor for the inclusive minimum and maximum result. This can support unusual dice, tables, cards, spinners, or digital randomizers whose results follow a custom numeric span.",
      },
      {
        key: "dice.info.specifics.use",
        fallback: "Later mechanics can combine the available dice and ranges into procedures such as Attribute generation, damage, healing, or random selections. Select a die or range here because the rules use it somewhere, not merely because it is common in other games.",
      },
      {
        key: "dice.info.specifics.manage",
        fallback: "Checkbox changes are saved immediately. Use Edit to revise a custom range and Remove when it should no longer be offered for future rule configuration; review any mechanics that depended on it.",
      },
    ],
  },
  "attribute-types": {
    titleKey: "attrtypes.info.specifics.title",
    titleFallback: "Using Attribute Categories",
    paragraphs: [
      {
        key: "attrtypes.info.specifics.optional",
        fallback: "Attribute Categories are optional organizational groups. Create them only when the grouping communicates something useful about the rules, such as Physical, Mental, and Social Attributes. A game whose Attributes stand on their own can leave this list empty.",
      },
      {
        key: "attrtypes.info.specifics.entries",
        fallback: "Add Category opens an editor for the category's name and description. The name is shown while organizing Attributes, and the description can explain what the group represents in your game. Edit revises either value; Remove deletes the category.",
      },
      {
        key: "attrtypes.info.specifics.assignment",
        fallback: "Categories do not contain scores or generation rules. After creating them, assign each relevant Attribute to a category on the Attributes screen. Attributes may also remain uncategorized.",
      },
      {
        key: "attrtypes.info.specifics.system_name",
        fallback: "The optional system name changes what this collection is called throughout the builder and ruleset. Use it when your game uses a more fitting term than Attribute Categories, while keeping the underlying data compatible with the rest of the suite.",
      },
    ],
  },
  attributes: {
    titleKey: "attributes.info.specifics.title",
    titleFallback: "Using Attributes",
    paragraphs: [
      {
        key: "attributes.info.specifics.entries",
        fallback: "Add Attribute creates one of the broad character capabilities used by your game. Give it a clear name and a creator-written description explaining what the Attribute represents, what kinds of actions it influences, and how players should interpret its score.",
      },
      {
        key: "attributes.info.specifics.category",
        fallback: "Attribute Category assigns the Attribute to one of the optional groups created on the previous screen. You can change that assignment directly from the list without recreating the Attribute, or leave it set to None.",
      },
      {
        key: "attributes.info.specifics.range",
        fallback: "Minimum and Maximum define the legal score range for this Attribute when the game does not use the shared limits from Attribute Generation. When shared limits are enabled, those common values are used instead and the individual range fields are hidden.",
      },
      {
        key: "attributes.info.specifics.modifiers",
        fallback: "Modifiers translate particular scores into bonuses or penalties used by other rules. Define an individual score-to-modifier list when this Attribute follows its own progression. If Attribute Generation applies one modifier list to every Attribute, the shared list is used instead.",
      },
      {
        key: "attributes.info.specifics.bonuses",
        fallback: "Score Bonuses grant reusable Effects when this Attribute reaches a specified threshold. Select an existing Effect or create one here without leaving the Attribute editor; the created Effect is also added to the main Effects collection.",
      },
      {
        key: "attributes.info.specifics.manage",
        fallback: "Edit reopens the complete Attribute definition, Change Category only changes its organizational group, and Remove deletes it. Because later rules may refer to an Attribute, review those relationships before removing one from an established ruleset.",
      },
    ],
  },
  "standard-array": {
    titleKey: "attrgen.standard.info.specifics.title",
    titleFallback: "Using Standard Array/Base Scores",
    paragraphs: [
      {
        key: "attrgen.standard.info.specifics.enabled",
        fallback: "This screen is active when at least one Player Option uses Standard Array. If no option uses it, the controls remain locked so unused array data cannot accidentally affect character generation.",
      },
      {
        key: "attrgen.standard.info.specifics.assignment",
        fallback: "Array Assignment determines how values reach Attributes. Auto Assigned pairs every value with a specific Attribute, producing a fixed distribution. Player Assigned stores only the numbers and lets each player decide which Attribute receives each value.",
      },
      {
        key: "attrgen.standard.info.specifics.standard",
        fallback: "Standard Arrays contain the ordinary set of starting values. In assigned mode, select an Attribute and add its value; in open mode, add one value for each score the player will assign. The completed array should supply every Attribute score required by character creation.",
      },
      {
        key: "attrgen.standard.info.specifics.elite",
        fallback: "Elite Arrays provide a second, usually stronger or more exceptional, set of values. They use the same assignment mode as the Standard Array. You may leave this list empty if your game has only one fixed array.",
      },
      {
        key: "attrgen.standard.info.specifics.default",
        fallback: "Default Array Type identifies which completed array character generation should offer first. Choose Standard or Elite after entering the values, use Edit for corrections, and Remove when rebuilding a set.",
      },
    ],
  },
  "dice-rolling": {
    titleKey: "attrgen.dice.info.specifics.title",
    titleFallback: "Using Dice Rolling",
    paragraphs: [
      {
        key: "attrgen.dice.info.specifics.enabled",
        fallback: "This screen is active when at least one Player Option uses Dice Rolling. It defines the complete procedure used to generate Attribute values; the general Dice Options screen only determines which die sizes are available here.",
      },
      {
        key: "attrgen.dice.info.specifics.sets",
        fallback: "Number of Sets controls how many complete groups of Attribute results are generated. During character creation, the player chooses which generated set to use.",
      },
      {
        key: "attrgen.dice.info.specifics.assignment",
        fallback: "Roll Assignment determines whether generated values are locked to Attributes in their rolled order or assigned by the player. When rolls are assigned in order, Set Attribute Order defines the canonical first-to-last association. Player assignment lets each rolled value be used once and returned to the available set when cleared.",
      },
      {
        key: "attrgen.dice.info.specifics.substitution",
        fallback: "Dice Substitution optionally allows a player to replace a rolled Attribute result with a fixed value. Substitution Value is the replacement score and must fall within the shared Attribute Score Limits when those limits are configured. Max Substitutions limits how many times the player may use it during character creation.",
      },
      {
        key: "attrgen.dice.info.specifics.terms",
        fallback: "Dice Terms build the roll used for an Attribute result. Number of Rolls is the quantity of the selected die, Reroll Below repeats results lower than the threshold, and Drop lowest roll removes the lowest die before totaling the term. Add multiple terms when the procedure combines different groups of dice.",
      },
      {
        key: "attrgen.dice.info.specifics.manage",
        fallback: "Number of Sets and Dice Substitution settings save automatically when changed. Add Dice Term opens a focused form. Use Edit to revise a term and Remove when it should not participate in the final roll.",
      },
    ],
  },
  "points-buy": {
    titleKey: "attrgen.point.info.specifics.title",
    titleFallback: "Using Point Buy",
    paragraphs: [
      {
        key: "attrgen.point.info.specifics.enabled",
        fallback: "This screen is active when at least one Player Option uses Point Buy. Point Buy gives players a controlled budget for shaping Attribute scores instead of accepting only predetermined or random results.",
      },
      {
        key: "attrgen.point.info.specifics.budget",
        fallback: "Base Points is the budget available during character creation. Minimum Points to Spend can require players to commit part or all of that budget before continuing, while a value of zero allows unused points to remain.",
      },
      {
        key: "attrgen.point.info.specifics.categories",
        fallback: "Point Buy can use one shared Base Points pool or separate pools for each Attribute Category. Category pools can be attached by the creator, or created as named slots that players assign one-to-one during character generation.",
      },
      {
        key: "attrgen.point.info.specifics.bounds",
        fallback: "Minimum Value and Maximum Value define the score range available during the purchase process. These bounds keep players from lowering or raising an Attribute beyond what the Point Buy method permits.",
      },
      {
        key: "attrgen.point.info.specifics.post_racial",
        fallback: "Max Value Post-Racial is the final cap after ancestry, species, race, or similar character-option adjustments are applied. Set it higher than the purchase maximum when those later bonuses are allowed to exceed the normal Point Buy limit.",
      },
      {
        key: "attrgen.point.info.specifics.negative",
        fallback: "Allow Negative Attributes determines whether Point Buy may produce scores below zero. Enable it only when negative scores have a defined meaning in your game and the rest of the rules can interpret them correctly.",
      },
      {
        key: "attrgen.point.info.specifics.apply",
        fallback: "Point Buy fields save automatically when changed. When Point Buy is a second Player Option step set to Spend from existing scores, the earlier step supplies the starting scores and this budget is used to adjust them.",
      },
    ],
  },
  "hit-points": {
    titleKey: "hp.info.specifics.title",
    titleFallback: "Using Hit Points",
    paragraphs: [
      {
        key: "hp.info.specifics.method",
        fallback: "Choose Independent Hit Points when characters have a separate starting total and may gain more through advancement. Starting Hit Points and Hit Point Gain are configured in separate sections. Gain can be Rolled, Fixed, or disabled.",
      },
      {
        key: "hp.info.specifics.attribute_derived",
        fallback: "Choose Attribute Derived when Maximum Hit Points are the result of character Attributes rather than a separate total. This replaces both starting Hit Points and per-level gain, and descendant applications recalculate the value whenever the referenced Attribute scores change.",
      },
      {
        key: "hp.info.specifics.attribute_formula",
        fallback: "Direct Attribute uses one complete Attribute score. Single-Attribute Formula applies a base value, multiplier, divisor, and rounding to one Attribute. Multi-Attribute Formula combines two or more unique Attribute terms through the same structured calculation.",
      },
      {
        key: "hp.info.specifics.minimum",
        fallback: "Minimum HP per Level is the floor for a level's gain after the selected method and relevant modifiers are considered. Use it to prevent poor rolls or penalties from reducing advancement below the minimum your game allows.",
      },
      {
        key: "hp.info.specifics.dice",
        fallback: "Select All characters use the same Hit Point dice when one expression applies to everyone, then choose its Die, Rolls, and single total Modifier. When it is not selected, assign the appropriate dice later in whichever character-defining sections provide them.",
      },
      {
        key: "hp.info.specifics.first_level",
        fallback: "Max HP at First Level gives a new character the maximum result instead of rolling the first Hit Point dice. First Level Bonus HP adds a separate fixed amount to the starting total.",
      },
      {
        key: "hp.info.specifics.static",
        fallback: "For a game with a static health track, select No Hit Point Gain and enter the complete starting total as Base Hit Points. GMRules stores this as the equivalent zero-gain progression so existing rulesets and descendant calculations remain compatible. Each change on this screen saves immediately.",
      },
    ],
  },
  "armor-class": {
    titleKey: "armorclass.info.specifics.title",
    titleFallback: "Using Armor Class",
    paragraphs: [
      {
        key: "armorclass.info.specifics.base",
        fallback: "Base Armor Class is the unmodified defensive value from which a character begins. Set it to the number an unarmored character would use before Attribute modifiers, worn armor, effects, or other bonuses are applied.",
      },
      {
        key: "armorclass.info.specifics.attribute",
        fallback: "AC Attribute optionally identifies the Attribute whose modifier contributes to Armor Class. Choose None for a fixed base with no inherent Attribute contribution, or select the capability your game uses for avoidance, reflexes, defense, or a similar concept.",
      },
      {
        key: "armorclass.info.specifics.use",
        fallback: "Character generation combines this rule with the selected Attribute's modifier and any Armor Class changes supplied by equipment. Both fields save when changed, so you can return later if the Attribute list or defensive model evolves.",
      },
    ],
  },
  currency: {
    titleKey: "currency.info.specifics.title",
    titleFallback: "Using Currency",
    paragraphs: [
      {
        key: "currency.info.specifics.system",
        fallback: "A Currency entry represents one economic system used by the game. Currency Name identifies that system, while Base Denomination creates its value-one unit. Add separate currencies when the setting contains economies that do not share denominations or exchange values.",
      },
      {
        key: "currency.info.specifics.denominations",
        fallback: "Denominations are the named units within the selected currency. Their Value is measured relative to that currency's base denomination: a value of 1 equals one base unit, while larger or fractional values represent more or less purchasing value.",
      },
      {
        key: "currency.info.specifics.selection",
        fallback: "Select a currency from the list before adding, editing, or removing its denominations. The badge shows how many denominations it contains. Removing a currency also removes the economic structure that later character and equipment rules may reference.",
      },
      {
        key: "currency.info.specifics.starting",
        fallback: "Starting Money declares where a new character's funds come from. Base uses the shared Base Amount; Class uses values defined by character class; Trait uses applicable character-option adjustments; Hybrid allows the ruleset to combine shared and option-based sources.",
      },
      {
        key: "currency.info.specifics.starting_currency",
        fallback: "Starting Money Currency identifies which currency receives the starting amount. Changes to the method, amount, and currency save automatically. Later Class and Race editors provide their own starting-money values or modifiers when those methods are part of the game.",
      },
      {
        key: "currency.info.specifics.system_name",
        fallback: "The optional system name changes what this collection is called throughout the builder and ruleset. Use it when your game calls money, wealth, credits, resources, or other economic units by a different collective term.",
      },
    ],
  },
  "effect-types": {
    titleKey: "effecttypes.info.specifics.title",
    titleFallback: "Using Affected Systems",
    paragraphs: [
      {
        key: "effecttypes.info.specifics.purpose",
        fallback: "Affected Systems identify the parts of your game, or recurring rule interactions, that actions, events, Effects, and Statuses can change or invoke. The built-in examples are Damage, Resistance, Immunity, Armor, Speed, Movement, and Apply Status.",
      },
      {
        key: "effecttypes.info.specifics.entries",
        fallback: "Add Affected System opens an editor for the system's name and description. Use a short, reusable name that identifies the rules area or response involved; use the description to explain when that label applies.",
      },
      {
        key: "effecttypes.info.specifics.multiple",
        fallback: "An Effect or Status may interact with more than one Affected System, so these labels may overlap. A slowing fire effect could involve both Damage and Speed, for example. Select each system that communicates a real rule relationship.",
      },
      {
        key: "effecttypes.info.specifics.manage",
        fallback: "Edit revises a system label, Remove deletes it, and the optional system name changes what this collection is called throughout the ruleset. Define the major affected systems before Effects and Statuses so they are available while those entries are being created.",
      },
    ],
  },
  "damage-types": {
    titleKey: "damagetypes.info.specifics.title",
    titleFallback: "Using Damage Types",
    paragraphs: [
      {
        key: "damagetypes.info.specifics.purpose",
        fallback: "Damage Types identify the nature of harm in your game, such as Fire, Cold, Ballistic, Psychic, Radiant, or Poison. They provide a shared reference that later rules can use for descriptions, resistances, vulnerabilities, immunities, and automation.",
      },
      {
        key: "damagetypes.info.specifics.entries",
        fallback: "Add Damage Type opens an editor for its name and creator-written description. Explain what produces this damage, how it is understood in the setting, and any general behavior that applies whenever the type appears.",
      },
      {
        key: "damagetypes.info.specifics.references",
        fallback: "Effects, equipment, weapons, armor, and spells can reference these entries instead of repeating free-form damage labels. Define a type once, then select it wherever the same kind of damage is used.",
      },
      {
        key: "damagetypes.info.specifics.manage",
        fallback: "Edit revises the shared entry and Remove deletes it. Review dependent content before removing a type from an established ruleset. The optional system name lets your game use another collective term for this list.",
      },
    ],
  },
  statuses: {
    titleKey: "statuses.info.specifics.title",
    titleFallback: "Using Statuses",
    paragraphs: [
      {
        key: "statuses.info.specifics.purpose",
        fallback: "Statuses are persistent or reusable conditions that can be applied to characters, creatures, or other game subjects. Examples include Stunned, Hidden, Burning, Inspired, Injured, or Exhausted.",
      },
      {
        key: "statuses.info.specifics.entries",
        fallback: "Add Status opens an editor for its name, description, and Affected Systems. The description should tell users what the condition means and how it changes play; the selected systems identify the rules areas or responses involved for organization and later automation.",
      },
      {
        key: "statuses.info.specifics.relationships",
        fallback: "A Status defines the condition itself, while an Effect describes a rule event or result that may apply that condition. Keeping the Status reusable allows many spells, skills, items, or other rules to refer to the same condition.",
      },
      {
        key: "statuses.info.specifics.manage",
        fallback: "Use Edit to change the shared definition and Remove only when the condition is no longer part of the game. The optional system name changes how this collection is labeled without changing its underlying role.",
      },
    ],
  },
  effects: {
    titleKey: "effects.info.specifics.title",
    titleFallback: "Using Effects",
    paragraphs: [
      {
        key: "effects.info.specifics.purpose",
        fallback: "Effects are reusable pieces of rules content that describe a result, change, or consequence. Create an Effect once when the same behavior may be granted by several skills, spells, weapons, or other game elements.",
      },
      {
        key: "effects.info.specifics.entries",
        fallback: "Add Effect opens an editor for its name and creator-written description. Write the actionable rule in the description, including triggers, targets, values, duration, limits, or other details needed to apply it during play.",
      },
      {
        key: "effects.info.specifics.types",
        fallback: "Assign one or more Affected Systems to identify the rules areas or responses the Effect changes or invokes. Select an optional Damage Type when the Effect causes a defined kind of harm. These references let descendant applications connect effects to other rules without relying only on wording.",
      },
      {
        key: "effects.info.specifics.references",
        fallback: "Skills, spells, and weapons can attach existing Effects, and their editors can create a new Effect without leaving the current workflow. Editing the shared Effect updates the definition those elements reference.",
      },
      {
        key: "effects.info.specifics.manage",
        fallback: "Edit revises the reusable Effect. Remove deletes it, so review elements that may depend on it first. The optional system name lets your game use a different term for this collection.",
      },
    ],
  },
  equipment: {
    titleKey: "equipment.info.specifics.title",
    titleFallback: "Using Equipment",
    paragraphs: [
      {
        key: "equipment.info.specifics.purpose",
        fallback: "Equipment contains general carried, worn, consumed, or used items that are not better represented by a more specialized rules section. Examples include tools, supplies, kits, devices, clothing, and adventuring gear.",
      },
      {
        key: "equipment.info.specifics.entries",
        fallback: "Add Equipment opens an editor for the item's name and creator-written description. Explain what the item is, how it is used, and any limitations or rules that players need when selecting or carrying it.",
      },
      {
        key: "equipment.info.specifics.weight",
        fallback: "Weight records the item's encumbrance using the units defined in Weights & Measures. Use zero or leave the measurement minimal when your game does not track equipment weight.",
      },
      {
        key: "equipment.info.specifics.damage",
        fallback: "Damage Type is optional and should be selected only when the item's rules directly involve a defined kind of damage. General equipment without a damage relationship can leave it set to None.",
      },
      {
        key: "equipment.info.specifics.manage",
        fallback: "Edit revises the complete item and Remove deletes it from the ruleset. The optional system name changes the collective label used for this equipment list.",
      },
    ],
  },
  weapons: {
    titleKey: "weapons.info.specifics.title",
    titleFallback: "Using Weapons",
    paragraphs: [
      {
        key: "weapons.info.specifics.entries",
        fallback: "Add Weapon creates a selectable weapon definition. Give it a name and creator-written description covering its form, use, range or handling rules, restrictions, special qualities, and any other details not represented by the structured fields.",
      },
      {
        key: "weapons.info.specifics.damage",
        fallback: "Number of Rolls, Dice Sides, and Modifier form the weapon's basic damage expression. For example, 2 rolls of a 6-sided die with a +1 modifier represents 2d6+1. Select the Damage Type that classifies this harm.",
      },
      {
        key: "weapons.info.specifics.weight",
        fallback: "Weight uses the measurement units defined earlier and allows inventory or encumbrance systems to account for the weapon. A zero value can represent negligible weight or a game that does not track it.",
      },
      {
        key: "weapons.info.specifics.effects",
        fallback: "Effects attach reusable rules behavior to the weapon, such as applying a condition, moving a target, or triggering an additional consequence. Select existing Effects or create one from the editor when the weapon needs a new shared rule.",
      },
      {
        key: "weapons.info.specifics.manage",
        fallback: "Edit revises the complete weapon and Remove deletes it. Review character options and other rules that may grant or reference a weapon before removing it from an established ruleset.",
      },
    ],
  },
  skills: {
    titleKey: "skills.info.specifics.title",
    titleFallback: "Using Skills",
    paragraphs: [
      {
        key: "skills.info.specifics.progression",
        fallback: "Skill Point Progression determines how many points characters receive as they advance. By Class uses the values defined on each Class; Fixed uses the shared Base Points per Level; Intelligence Modified applies the relevant Attribute modifier; Custom allows level-specific values.",
      },
      {
        key: "skills.info.specifics.progression_controls",
        fallback: "Minimum per Level places a floor on the award. Apply Intelligence Modifier controls whether that modifier participates, and Same at all levels uses one value throughout progression. Clear Same at all levels to enter different point awards for selected levels; progression changes save automatically.",
      },
      {
        key: "skills.info.specifics.entries",
        fallback: "Add Skill opens the full Skill editor. Name and Description identify what the Skill covers, Category organizes related Skills, and Related Attribute connects checks to the broad capability that supports them.",
      },
      {
        key: "skills.info.specifics.rules",
        fallback: "Trained Only marks a Skill that cannot be used normally without training. Armor Check Penalty records how armor interferes with the Skill. Trait Starting Money Modifier lets a Skill or trait-like selection adjust starting funds when the chosen Starting Money method uses those adjustments.",
      },
      {
        key: "skills.info.specifics.restrictions",
        fallback: "Class Restrictions and Race Restrictions limit which character options may access the Skill. Leave both lists empty for general availability. Effects attach reusable outcomes or rules behavior, and a new Effect can be created directly from the Skill editor.",
      },
      {
        key: "skills.info.specifics.manage",
        fallback: "Edit revises the Skill and Remove deletes it from the shared list. The optional system name changes what Skills are called throughout the ruleset.",
      },
    ],
  },
  spells: {
    titleKey: "spells.info.specifics.title",
    titleFallback: "Using Spells",
    paragraphs: [
      {
        key: "spells.info.specifics.entries",
        fallback: "Add Spell opens an editor for a magical, supernatural, technological, or otherwise special ability. Name and creator-written Description explain what it does and provide any casting rules not captured by the structured fields.",
      },
      {
        key: "spells.info.specifics.classification",
        fallback: "Level represents the Spell's relative tier or access requirement. School is a creator-defined classification such as Evocation, Healing, Psionics, or Hacking; it remains flexible so the terminology can match your setting.",
      },
      {
        key: "spells.info.specifics.timing",
        fallback: "Casting Time states how long activation takes, Range states how far or where it can reach, and Duration states how long its result lasts. These are creator-written values so they can use the time and measurement vocabulary of your game.",
      },
      {
        key: "spells.info.specifics.damage",
        fallback: "Damage Type is optional and classifies the Spell's harm when applicable. Leave it set to None for utility, healing, defensive, or other Spells that do not cause a defined damage type.",
      },
      {
        key: "spells.info.specifics.effects",
        fallback: "Effects attach reusable rule results to the Spell. Add every Effect the Spell produces, or create a new Effect from the editor when the required behavior does not exist yet.",
      },
      {
        key: "spells.info.specifics.manage",
        fallback: "Edit revises the complete Spell and Remove deletes it. The optional system name changes what this collection is called throughout the ruleset.",
      },
    ],
  },
  pantheons: {
    titleKey: "pantheons.info.specifics.title",
    titleFallback: "Using Pantheons",
    paragraphs: [
      {
        key: "pantheons.info.specifics.purpose",
        fallback: "Pantheons organize related Deities into the religious traditions, divine families, alliances, or rival groups used by your setting. A Deity can belong to more than one Pantheon when your mythology overlaps.",
      },
      {
        key: "pantheons.info.specifics.entries",
        fallback: "Enter a Pantheon Name and use Description to explain its origin, shared beliefs, relationships, and place in the world. Save creates the Pantheon; Edit loads an existing one back into these fields.",
      },
      {
        key: "pantheons.info.specifics.membership",
        fallback: "The Deities list assigns existing Deities to this Pantheon. Because the Deities screen comes next, you may save a Pantheon without members, create the Deities afterward, and then return here to complete its membership.",
      },
      {
        key: "pantheons.info.specifics.manage",
        fallback: "Cancel clears the current form without removing saved entries. Edit revises a Pantheon, Remove deletes it, and the optional system name changes what this collection is called throughout the ruleset.",
      },
    ],
  },
  deities: {
    titleKey: "deities.info.specifics.title",
    titleFallback: "Using Deities",
    paragraphs: [
      {
        key: "deities.info.specifics.identity",
        fallback: "Deity Name identifies the divine figure. Deity Type and Divine Rank let you record classifications such as ancestral spirit, demigod, greater deity, or any hierarchy used by your game.",
      },
      {
        key: "deities.info.specifics.portfolio",
        fallback: "Primary Portfolio states the Deity's principal area of influence, while Alignment records any moral, ethical, elemental, or factional outlook your rules use. Holy Symbol and Worship Style describe how followers recognize and honor the Deity.",
      },
      {
        key: "deities.info.specifics.pantheons",
        fallback: "Pantheons assigns the Deity to one or more groups created on the previous screen. Select every applicable Pantheon, or leave the list empty for an independent Deity.",
      },
      {
        key: "deities.info.specifics.spells",
        fallback: "Can grant spells determines whether worship of this Deity can provide spellcasting. Max Spell Level places an upper limit on the Spell levels the Deity may grant; set it to match the power and role you intend.",
      },
      {
        key: "deities.info.specifics.description",
        fallback: "Use Description for the Deity's mythology, goals, commandments, relationships, followers, or any rules that are not represented by the structured fields.",
      },
      {
        key: "deities.info.specifics.manage",
        fallback: "Save adds or updates the Deity, Cancel clears the current form, Edit reopens an entry, and Remove deletes it. The optional system name changes what this collection is called throughout the ruleset.",
      },
    ],
  },
  races: {
    titleKey: "races.info.specifics.title",
    titleFallback: "Using Races",
    paragraphs: [
      {
        key: "races.info.specifics.entries",
        fallback: "Add Race opens the Race editor. Give the option a Name and use Description to explain its people, species, ancestry, culture, physiology, or whatever character origin this element represents in your game.",
      },
      {
        key: "races.info.specifics.traits",
        fallback: "Racial Traits assigns Skills that characters receive from this Race. Select an existing Skill and add it, or create a new Skill from the editor when the required trait is not yet part of the ruleset.",
      },
      {
        key: "races.info.specifics.attributes",
        fallback: "Attribute Limits restrict the scores available to members of this Race. Select an Attribute and enter its minimum and maximum; add only the limits your rules require.",
      },
      {
        key: "races.info.specifics.money",
        fallback: "Race Starting Money Modifier adjusts the character's starting funds because of this Race. Use zero when Race has no effect, a positive value for additional money, or a negative value for a reduction.",
      },
      {
        key: "races.info.specifics.manage",
        fallback: "Save creates or updates the Race. Edit revises an existing entry, Remove deletes it, and the optional system name changes what this collection is called throughout the ruleset.",
      },
    ],
  },
  classes: {
    titleKey: "classes.info.specifics.title",
    titleFallback: "Using Classes",
    paragraphs: [
      {
        key: "classes.info.specifics.entries",
        fallback: "Add Class opens the Class editor. Name and creator-written Description establish the Class's role, theme, abilities, advancement, and any rules not represented by the structured fields.",
      },
      {
        key: "classes.info.specifics.core",
        fallback: "Primary Attribute identifies the score most important to the Class. Hit Die and its Modifier define the die expression associated with Class-based health when your Hit Points rules use it.",
      },
      {
        key: "classes.info.specifics.skills",
        fallback: "Class Skills lists the Skills associated with this Class. Select existing Skills to add them, or create a new Skill from the editor when the Class requires one that does not yet exist.",
      },
      {
        key: "classes.info.specifics.skill_points",
        fallback: "Skill Points per Level controls how many points this Class provides for Skill advancement. Keep Same at all levels selected for one repeating value, or clear it to enter different Skill Point values for individual levels.",
      },
      {
        key: "classes.info.specifics.requirements",
        fallback: "Required Attributes set minimum scores a character must meet to select the Class. Choose an Attribute, enter the minimum score, and add each prerequisite your rules require.",
      },
      {
        key: "classes.info.specifics.money",
        fallback: "Class Starting Money overrides the general starting-money value for characters of this Class. Leave it at zero when your rules do not use a Class-specific amount.",
      },
      {
        key: "classes.info.specifics.manage",
        fallback: "Save creates or updates the Class. Edit revises an entry, Remove deletes it, and the optional system name changes what this collection is called. Done offers to download the completed ruleset.",
      },
    ],
  },
};

const stepRoutes = {};
const historyRoutes = {};
const visitedSteps = new Set();
const tutorialVisitedScreens = new Set();
const appBackStack = [];
const SESSION_TOKEN_KEY = "gmrules.web.sessionToken";
const LOCAL_SESSION_FRAGMENT_KEY = "local-session";
const TUTORIAL_SCREEN_KEY_PATTERN = /^[a-z0-9][a-z0-9._:-]{0,79}$/;
const TUTORIAL_CONTENT_VERSION = "builder-detailed-guidance-20260728";
const TUTORIAL_CONTENT_VERSION_BY_SCREEN = {
  home: "home-intro-expanded-20260728",
  setup: "setup-detailed-guidance-20260728",
  measurements: "measurements-detailed-guidance-20260728",
  "attribute-generation": "attribute-generation-specific-guidance-20260728",
  "hit-points": "hit-points-attribute-derived-20260805",
};

let historyReady = false;
let historyLocked = false;
let appBackLocked = false;
let activeInformationAction = null;

const view = document.getElementById("view");
const stepIndicator = document.getElementById("stepIndicator");
const saveStatus = document.getElementById("saveStatus");
const homeBtn = document.getElementById("homeBtn");
const tutorialInfoBtn = document.getElementById("tutorialInfoBtn");
const fontSizeSlider = document.getElementById("fontSizeSlider");
const fontSizeSliderLabel = document.getElementById("fontSizeSliderLabel");
const fontSizeAllScreens = document.getElementById("fontSizeAllScreens");
const fontSizeAllScreensLabel = document.getElementById("fontSizeAllScreensLabel");
const adminBtn = document.getElementById("adminBtn");
const feedbackBtn = document.getElementById("feedbackBtn");
const downloadBtn = document.getElementById("downloadBtn");
const logoutBtn = document.getElementById("logoutBtn");
const toast = document.getElementById("toast");
const sidebar = document.getElementById("sidebar");
const sidebarTitle = document.getElementById("sidebarTitle");
const sidebarNav = document.getElementById("sidebarNav");

const confirmModal = document.getElementById("confirmModal");
const confirmTitle = document.getElementById("confirmTitle");
const confirmMessage = document.getElementById("confirmMessage");
const confirmCancel = document.getElementById("confirmCancel");
const confirmOk = document.getElementById("confirmOk");

const tutorialModal = document.getElementById("tutorialModal");
const tutorialTitle = document.getElementById("tutorialTitle");
const tutorialMessage = document.getElementById("tutorialMessage");
const tutorialBack = document.getElementById("tutorialBack");
const tutorialNext = document.getElementById("tutorialNext");
const tutorialOk = document.getElementById("tutorialOk");

const FONT_SIZE_STEP_PIXELS = 3;

function clampFontSizeLevel(value) {
  const numericValue = Number(value);
  if (!Number.isFinite(numericValue)) {
    return DEFAULT_FONT_SIZE_LEVEL;
  }
  return Math.max(0, Math.min(2, Math.round(numericValue)));
}

function currentFontSizeScreenKey() {
  const mode = String(state.mode || "home").trim() || "home";
  const step = String(state.step || "home").trim() || "home";
  return `${mode}:${step}`;
}

function currentFontSizeLevel() {
  if (state.fontSizeAllScreens) {
    return clampFontSizeLevel(state.fontSizeGlobalLevel);
  }
  return clampFontSizeLevel(
    state.fontSizeLevelsByScreen[currentFontSizeScreenKey()] ?? DEFAULT_FONT_SIZE_LEVEL
  );
}

function fontSizeLevelLabel(level) {
  const labels = [
    t("web.font_size.current", "Current"),
    t("web.font_size.larger", "Larger"),
    t("web.font_size.largest", "Largest"),
  ];
  return labels[clampFontSizeLevel(level)];
}

function applyFontSizePreference() {
  const level = currentFontSizeLevel();
  const offset = `${level * FONT_SIZE_STEP_PIXELS}px`;
  const universalOffset = state.fontSizeAllScreens ? offset : "0px";
  document.body.style.setProperty("--font-size-adjust", universalOffset);

  const screenOffset = state.fontSizeAllScreens ? "" : offset;
  [view, ...document.querySelectorAll(".modal"), toast].forEach((element) => {
    if (!element) {
      return;
    }
    if (screenOffset) {
      element.style.setProperty("--font-size-adjust", screenOffset);
    } else {
      element.style.removeProperty("--font-size-adjust");
    }
  });

  if (fontSizeSlider) {
    fontSizeSlider.value = String(level);
    fontSizeSlider.setAttribute("aria-valuetext", fontSizeLevelLabel(level));
  }
  if (fontSizeAllScreens) {
    fontSizeAllScreens.checked = state.fontSizeAllScreens;
  }
}

const timeUnitEditModal = document.getElementById("timeUnitEditModal");
const timeUnitEditTitle = document.getElementById("timeUnitEditTitle");
const timeUnitEditNameLabel = document.getElementById("timeUnitEditNameLabel");
const timeUnitEditName = document.getElementById("timeUnitEditName");
const timeUnitEditAmountLabel = document.getElementById("timeUnitEditAmountLabel");
const timeUnitEditAmount = document.getElementById("timeUnitEditAmount");
const timeUnitEditBaseLabel = document.getElementById("timeUnitEditBaseLabel");
const timeUnitEditBase = document.getElementById("timeUnitEditBase");
const timeUnitEditCancel = document.getElementById("timeUnitEditCancel");
const timeUnitEditSave = document.getElementById("timeUnitEditSave");

const diceRangeModal = document.getElementById("diceRangeModal");
const diceRangeTitle = document.getElementById("diceRangeTitle");
const diceRangeMinLabel = document.getElementById("diceRangeMinLabel");
const diceRangeMin = document.getElementById("diceRangeMin");
const diceRangeMaxLabel = document.getElementById("diceRangeMaxLabel");
const diceRangeMax = document.getElementById("diceRangeMax");
const diceRangeCancel = document.getElementById("diceRangeCancel");
const diceRangeSave = document.getElementById("diceRangeSave");

const generationOptionModal = document.getElementById("generationOptionModal");
const generationOptionModalTitle = document.getElementById("generationOptionModalTitle");
const generationOptionModalNameLabel = document.getElementById("generationOptionModalNameLabel");
const generationOptionModalName = document.getElementById("generationOptionModalName");
const generationOptionModalStep1Label = document.getElementById("generationOptionModalStep1Label");
const generationOptionModalStep1 = document.getElementById("generationOptionModalStep1");
const generationOptionModalStep2Label = document.getElementById("generationOptionModalStep2Label");
const generationOptionModalStep2 = document.getElementById("generationOptionModalStep2");
const generationOptionModalStep2ModeField = document.getElementById("generationOptionModalStep2ModeField");
const generationOptionModalStep2ModeLabel = document.getElementById("generationOptionModalStep2ModeLabel");
const generationOptionModalStep2Mode = document.getElementById("generationOptionModalStep2Mode");
const generationOptionModalCancel = document.getElementById("generationOptionModalCancel");
const generationOptionModalSave = document.getElementById("generationOptionModalSave");

const defaultModifierModal = document.getElementById("defaultModifierModal");
const defaultModifierModalTitle = document.getElementById("defaultModifierModalTitle");
const defaultModifierModalScoreLabel = document.getElementById("defaultModifierModalScoreLabel");
const defaultModifierModalScore = document.getElementById("defaultModifierModalScore");
const defaultModifierModalValueLabel = document.getElementById("defaultModifierModalValueLabel");
const defaultModifierModalValue = document.getElementById("defaultModifierModalValue");
const defaultModifierModalCancel = document.getElementById("defaultModifierModalCancel");
const defaultModifierModalSave = document.getElementById("defaultModifierModalSave");

const arrayValueModal = document.getElementById("arrayValueModal");
const arrayValueModalCard = document.getElementById("arrayValueModalCard");
const arrayValueModalTitle = document.getElementById("arrayValueModalTitle");
const arrayValueModalSingleFields = document.getElementById("arrayValueModalSingleFields");
const arrayValueModalEntries = document.getElementById("arrayValueModalEntries");
const arrayValueModalAttributeField = document.getElementById("arrayValueModalAttributeField");
const arrayValueModalAttributeLabel = document.getElementById("arrayValueModalAttributeLabel");
const arrayValueModalAttribute = document.getElementById("arrayValueModalAttribute");
const arrayValueModalValueLabel = document.getElementById("arrayValueModalValueLabel");
const arrayValueModalValue = document.getElementById("arrayValueModalValue");
const arrayValueModalCancel = document.getElementById("arrayValueModalCancel");
const arrayValueModalSave = document.getElementById("arrayValueModalSave");

const diceTermModal = document.getElementById("diceTermModal");
const diceTermModalTitle = document.getElementById("diceTermModalTitle");
const diceTermModalCountLabel = document.getElementById("diceTermModalCountLabel");
const diceTermModalCount = document.getElementById("diceTermModalCount");
const diceTermModalSidesLabel = document.getElementById("diceTermModalSidesLabel");
const diceTermModalSides = document.getElementById("diceTermModalSides");
const diceTermModalRerollLabel = document.getElementById("diceTermModalRerollLabel");
const diceTermModalReroll = document.getElementById("diceTermModalReroll");
const diceTermModalDropLowestLabel = document.getElementById("diceTermModalDropLowestLabel");
const diceTermModalDropLowest = document.getElementById("diceTermModalDropLowest");
const diceTermModalCancel = document.getElementById("diceTermModalCancel");
const diceTermModalSave = document.getElementById("diceTermModalSave");

const attributeOrderModal = document.getElementById("attributeOrderModal");
const attributeOrderModalTitle = document.getElementById("attributeOrderModalTitle");
const attributeOrderModalDescription = document.getElementById("attributeOrderModalDescription");
const attributeOrderModalList = document.getElementById("attributeOrderModalList");
const attributeOrderModalCancel = document.getElementById("attributeOrderModalCancel");
const attributeOrderModalSave = document.getElementById("attributeOrderModalSave");

const pointCategoryBudgetModal = document.getElementById("pointCategoryBudgetModal");
const pointCategoryBudgetModalTitle = document.getElementById("pointCategoryBudgetModalTitle");
const pointCategoryBudgetCategoryField = document.getElementById("pointCategoryBudgetCategoryField");
const pointCategoryBudgetCategoryLabel = document.getElementById("pointCategoryBudgetCategoryLabel");
const pointCategoryBudgetCategory = document.getElementById("pointCategoryBudgetCategory");
const pointCategoryBudgetNameField = document.getElementById("pointCategoryBudgetNameField");
const pointCategoryBudgetNameLabel = document.getElementById("pointCategoryBudgetNameLabel");
const pointCategoryBudgetName = document.getElementById("pointCategoryBudgetName");
const pointCategoryBudgetPointsLabel = document.getElementById("pointCategoryBudgetPointsLabel");
const pointCategoryBudgetPoints = document.getElementById("pointCategoryBudgetPoints");
const pointCategoryBudgetModalCancel = document.getElementById("pointCategoryBudgetModalCancel");
const pointCategoryBudgetModalSave = document.getElementById("pointCategoryBudgetModalSave");

const currencyCreateModal = document.getElementById("currencyCreateModal");
const currencyCreateModalTitle = document.getElementById("currencyCreateModalTitle");
const currencyCreateModalNameLabel = document.getElementById("currencyCreateModalNameLabel");
const currencyCreateModalName = document.getElementById("currencyCreateModalName");
const currencyCreateModalBaseLabel = document.getElementById("currencyCreateModalBaseLabel");
const currencyCreateModalBase = document.getElementById("currencyCreateModalBase");
const currencyCreateModalCancel = document.getElementById("currencyCreateModalCancel");
const currencyCreateModalSave = document.getElementById("currencyCreateModalSave");

const denominationCreateModal = document.getElementById("denominationCreateModal");
const denominationCreateModalTitle = document.getElementById("denominationCreateModalTitle");
const denominationCreateModalNameLabel = document.getElementById("denominationCreateModalNameLabel");
const denominationCreateModalName = document.getElementById("denominationCreateModalName");
const denominationCreateModalValueLabel = document.getElementById("denominationCreateModalValueLabel");
const denominationCreateModalValue = document.getElementById("denominationCreateModalValue");
const denominationCreateModalCancel = document.getElementById("denominationCreateModalCancel");
const denominationCreateModalSave = document.getElementById("denominationCreateModalSave");

const typeModal = document.getElementById("typeModal");
const typeTitle = document.getElementById("typeTitle");
const typeLabel = document.getElementById("typeLabel");
const typeSelector = document.getElementById("typeSelector");
const typeCancel = document.getElementById("typeCancel");
const typeOk = document.getElementById("typeOk");

const deleteAccountModal = document.getElementById("deleteAccountModal");
const deleteAccountTitle = document.getElementById("deleteAccountTitle");
const deleteAccountMessage = document.getElementById("deleteAccountMessage");
const deleteAccountUsernameLabel = document.getElementById("deleteAccountUsernameLabel");
const deleteAccountUsername = document.getElementById("deleteAccountUsername");
const deleteAccountPasswordLabel = document.getElementById("deleteAccountPasswordLabel");
const deleteAccountPassword = document.getElementById("deleteAccountPassword");
const deleteAccountCancel = document.getElementById("deleteAccountCancel");
const deleteAccountOk = document.getElementById("deleteAccountOk");

const feedbackModal = document.getElementById("feedbackModal");
const feedbackTitle = document.getElementById("feedbackTitle");
const feedbackType = document.getElementById("feedbackType");
const feedbackTypeLabel = document.getElementById("feedbackTypeLabel");
const feedbackSeverity = document.getElementById("feedbackSeverity");
const feedbackSeverityLabel = document.getElementById("feedbackSeverityLabel");
const feedbackShortTitle = document.getElementById("feedbackShortTitle");
const feedbackShortTitleLabel = document.getElementById("feedbackShortTitleLabel");
const feedbackMessage = document.getElementById("feedbackMessage");
const feedbackMessageLabel = document.getElementById("feedbackMessageLabel");
const feedbackStepsField = document.getElementById("feedbackStepsField");
const feedbackSteps = document.getElementById("feedbackSteps");
const feedbackStepsLabel = document.getElementById("feedbackStepsLabel");
const feedbackPrivacyHint = document.getElementById("feedbackPrivacyHint");
const feedbackStatus = document.getElementById("feedbackStatus");
const feedbackCancel = document.getElementById("feedbackCancel");
const feedbackSubmit = document.getElementById("feedbackSubmit");

const lockedAccountModal = document.getElementById("lockedAccountModal");
const lockedAccountTitle = document.getElementById("lockedAccountTitle");
const lockedAccountMessage = document.getElementById("lockedAccountMessage");
const lockedAccountDetailsLabel = document.getElementById("lockedAccountDetailsLabel");
const lockedAccountDetails = document.getElementById("lockedAccountDetails");
const lockedAccountStatus = document.getElementById("lockedAccountStatus");
const lockedAccountCancel = document.getElementById("lockedAccountCancel");
const lockedAccountSubmit = document.getElementById("lockedAccountSubmit");

const editModal = document.getElementById("editModal");
const editTitle = document.getElementById("editTitle");
const editNameLabel = document.getElementById("editNameLabel");
const editName = document.getElementById("editName");
const editDescriptionLabel = document.getElementById("editDescriptionLabel");
const editDescription = document.getElementById("editDescription");
const editWeightGrid = document.getElementById("editWeightGrid");
const editWeightValueLabel = document.getElementById("editWeightValueLabel");
const editWeightValue = document.getElementById("editWeightValue");
const editWeightUnitLabel = document.getElementById("editWeightUnitLabel");
const editWeightUnit = document.getElementById("editWeightUnit");
const editDamageTypeField = document.getElementById("editDamageTypeField");
const editDamageTypeLabel = document.getElementById("editDamageTypeLabel");
const editDamageType = document.getElementById("editDamageType");
const editWeaponDamageField = document.getElementById("editWeaponDamageField");
const editWeaponDamageCountLabel = document.getElementById("editWeaponDamageCountLabel");
const editWeaponDamageCount = document.getElementById("editWeaponDamageCount");
const editWeaponDamageSidesLabel = document.getElementById("editWeaponDamageSidesLabel");
const editWeaponDamageSides = document.getElementById("editWeaponDamageSides");
const editWeaponDamageModifierLabel = document.getElementById("editWeaponDamageModifierLabel");
const editWeaponDamageModifier = document.getElementById("editWeaponDamageModifier");
const editClassSection = document.getElementById("editClassSection");
const editClassTitle = document.getElementById("editClassTitle");
const editClassPrimaryLabel = document.getElementById("editClassPrimaryLabel");
const editClassPrimary = document.getElementById("editClassPrimary");
const editClassHitDieLabel = document.getElementById("editClassHitDieLabel");
const editClassHitDieSelect = document.getElementById("editClassHitDieSelect");
const editClassHitDieModifierLabel = document.getElementById("editClassHitDieModifierLabel");
const editClassHitDieModifier = document.getElementById("editClassHitDieModifier");
const editClassSkillPointsLabel = document.getElementById("editClassSkillPointsLabel");
const editClassSkillPoints = document.getElementById("editClassSkillPoints");
const editClassStartingMoneyLabel = document.getElementById("editClassStartingMoneyLabel");
const editClassStartingMoney = document.getElementById("editClassStartingMoney");
const editClassSkillPointsSameLabel = document.getElementById("editClassSkillPointsSameLabel");
const editClassSkillPointsSame = document.getElementById("editClassSkillPointsSame");
const editClassSkillPointsLevelLabel = document.getElementById("editClassSkillPointsLevelLabel");
const editClassSkillPointsLevel = document.getElementById("editClassSkillPointsLevel");
const editClassSkillPointsValueLabel = document.getElementById("editClassSkillPointsValueLabel");
const editClassSkillPointsValue = document.getElementById("editClassSkillPointsValue");
const editClassSkillPointsAdd = document.getElementById("editClassSkillPointsAdd");
const editClassSkillPointsList = document.getElementById("editClassSkillPointsList");
const editClassSkillsTitle = document.getElementById("editClassSkillsTitle");
const editClassSkillSelectLabel = document.getElementById("editClassSkillSelectLabel");
const editClassSkillSelect = document.getElementById("editClassSkillSelect");
const editClassSkillAdd = document.getElementById("editClassSkillAdd");
const editClassSkillList = document.getElementById("editClassSkillList");
const editClassRequiredTitle = document.getElementById("editClassRequiredTitle");
const editClassRequiredSelectLabel = document.getElementById("editClassRequiredSelectLabel");
const editClassRequiredSelect = document.getElementById("editClassRequiredSelect");
const editClassRequiredScoreLabel = document.getElementById("editClassRequiredScoreLabel");
const editClassRequiredScore = document.getElementById("editClassRequiredScore");
const editClassRequiredAdd = document.getElementById("editClassRequiredAdd");
const editClassRequiredList = document.getElementById("editClassRequiredList");
const editTypeField = document.getElementById("editTypeField");
const editTypeLabel = document.getElementById("editTypeLabel");
const editType = document.getElementById("editType");
const editTypeAdd = document.getElementById("editTypeAdd");
const editTypeList = document.getElementById("editTypeList");
const editRangeFields = document.getElementById("editRangeFields");
const editMinLabel = document.getElementById("editMinLabel");
const editMinValue = document.getElementById("editMinValue");
const editMaxLabel = document.getElementById("editMaxLabel");
const editMaxValue = document.getElementById("editMaxValue");
const editModifierSection = document.getElementById("editModifierSection");
const editModifierTitle = document.getElementById("editModifierTitle");
const editModifierScoreLabel = document.getElementById("editModifierScoreLabel");
const editModifierScore = document.getElementById("editModifierScore");
const editModifierValueLabel = document.getElementById("editModifierValueLabel");
const editModifierValue = document.getElementById("editModifierValue");
const editModifierAdd = document.getElementById("editModifierAdd");
const editModifierApplyAll = document.getElementById("editModifierApplyAll");
const editModifierApplyAllLabel = document.getElementById("editModifierApplyAllLabel");
const editModifierList = document.getElementById("editModifierList");
const editBonusSection = document.getElementById("editBonusSection");
const editBonusTitle = document.getElementById("editBonusTitle");
const editBonusThresholdLabel = document.getElementById("editBonusThresholdLabel");
const editBonusThreshold = document.getElementById("editBonusThreshold");
const editBonusEffectLabel = document.getElementById("editBonusEffectLabel");
const editBonusEffect = document.getElementById("editBonusEffect");
const editBonusAdd = document.getElementById("editBonusAdd");
const editBonusList = document.getElementById("editBonusList");
const editRaceSection = document.getElementById("editRaceSection");
const editRaceTraitsTitle = document.getElementById("editRaceTraitsTitle");
const editRaceTraitLabel = document.getElementById("editRaceTraitLabel");
const editRaceTraitSelect = document.getElementById("editRaceTraitSelect");
const editRaceTraitAdd = document.getElementById("editRaceTraitAdd");
const editRaceTraitList = document.getElementById("editRaceTraitList");
const editRaceAttributesTitle = document.getElementById("editRaceAttributesTitle");
const editRaceAttributeLabel = document.getElementById("editRaceAttributeLabel");
const editRaceAttributeSelect = document.getElementById("editRaceAttributeSelect");
const editRaceAttributeMinLabel = document.getElementById("editRaceAttributeMinLabel");
const editRaceAttributeMin = document.getElementById("editRaceAttributeMin");
const editRaceAttributeMaxLabel = document.getElementById("editRaceAttributeMaxLabel");
const editRaceAttributeMax = document.getElementById("editRaceAttributeMax");
const editRaceAttributeAdd = document.getElementById("editRaceAttributeAdd");
const editRaceAttributeList = document.getElementById("editRaceAttributeList");
const editRaceStartingMoneyModifierLabel = document.getElementById("editRaceStartingMoneyModifierLabel");
const editRaceStartingMoneyModifier = document.getElementById("editRaceStartingMoneyModifier");
const editWeaponEffectsSection = document.getElementById("editWeaponEffectsSection");
const editWeaponEffectsTitle = document.getElementById("editWeaponEffectsTitle");
const editWeaponEffectSelectLabel = document.getElementById("editWeaponEffectSelectLabel");
const editWeaponEffectSelect = document.getElementById("editWeaponEffectSelect");
const editWeaponEffectAdd = document.getElementById("editWeaponEffectAdd");
const editWeaponEffectList = document.getElementById("editWeaponEffectList");
const editCancel = document.getElementById("editCancel");
const editOk = document.getElementById("editOk");
const skillModal = document.getElementById("skillModal");
const skillModalTitle = document.getElementById("skillModalTitle");
const skillNameLabel = document.getElementById("skillNameLabel");
const skillNameInput = document.getElementById("skillNameInput");
const skillCategoryLabel = document.getElementById("skillCategoryLabel");
const skillCategorySelect = document.getElementById("skillCategorySelect");
const skillDescriptionLabel = document.getElementById("skillDescriptionLabel");
const skillDescriptionInput = document.getElementById("skillDescriptionInput");
const skillAbilityLabel = document.getElementById("skillAbilityLabel");
const skillAbilitySelect = document.getElementById("skillAbilitySelect");
const skillTrainedLabel = document.getElementById("skillTrainedLabel");
const skillTrainedOnly = document.getElementById("skillTrainedOnly");
const skillArmorLabel = document.getElementById("skillArmorLabel");
const skillArmorPenalty = document.getElementById("skillArmorPenalty");
const skillStartingMoneyModifierLabel = document.getElementById("skillStartingMoneyModifierLabel");
const skillStartingMoneyModifier = document.getElementById("skillStartingMoneyModifier");
const skillClassLimitsTitle = document.getElementById("skillClassLimitsTitle");
const skillClassLimitSelectLabel = document.getElementById("skillClassLimitSelectLabel");
const skillClassLimitSelect = document.getElementById("skillClassLimitSelect");
const skillClassLimitAdd = document.getElementById("skillClassLimitAdd");
const skillClassLimitList = document.getElementById("skillClassLimitList");
const skillRaceLimitsTitle = document.getElementById("skillRaceLimitsTitle");
const skillRaceLimitSelectLabel = document.getElementById("skillRaceLimitSelectLabel");
const skillRaceLimitSelect = document.getElementById("skillRaceLimitSelect");
const skillRaceLimitAdd = document.getElementById("skillRaceLimitAdd");
const skillRaceLimitList = document.getElementById("skillRaceLimitList");
const skillEffectsTitle = document.getElementById("skillEffectsTitle");
const skillEffectSelectLabel = document.getElementById("skillEffectSelectLabel");
const skillEffectSelect = document.getElementById("skillEffectSelect");
const skillEffectAdd = document.getElementById("skillEffectAdd");
const skillEffectList = document.getElementById("skillEffectList");
const skillCancel = document.getElementById("skillCancel");
const skillSave = document.getElementById("skillSave");
const spellModal = document.getElementById("spellModal");
const spellModalTitle = document.getElementById("spellModalTitle");
const spellNameLabel = document.getElementById("spellNameLabel");
const spellNameInput = document.getElementById("spellNameInput");
const spellDescriptionLabel = document.getElementById("spellDescriptionLabel");
const spellDescriptionInput = document.getElementById("spellDescriptionInput");
const spellSchoolLabel = document.getElementById("spellSchoolLabel");
const spellSchoolInput = document.getElementById("spellSchoolInput");
const spellLevelLabel = document.getElementById("spellLevelLabel");
const spellLevelInput = document.getElementById("spellLevelInput");
const spellCastingLabel = document.getElementById("spellCastingLabel");
const spellCastingInput = document.getElementById("spellCastingInput");
const spellRangeLabel = document.getElementById("spellRangeLabel");
const spellRangeInput = document.getElementById("spellRangeInput");
const spellDurationLabel = document.getElementById("spellDurationLabel");
const spellDurationInput = document.getElementById("spellDurationInput");
const spellDamageTypeLabel = document.getElementById("spellDamageTypeLabel");
const spellDamageTypeSelect = document.getElementById("spellDamageTypeSelect");
const spellEffectTitle = document.getElementById("spellEffectTitle");
const spellEffectSelectLabel = document.getElementById("spellEffectSelectLabel");
const spellEffectSelect = document.getElementById("spellEffectSelect");
const spellEffectAdd = document.getElementById("spellEffectAdd");
const spellEffectList = document.getElementById("spellEffectList");
const spellCancel = document.getElementById("spellCancel");
const spellSave = document.getElementById("spellSave");

const NEW_INLINE_OPTION = "__gmrules_new_inline__";

let confirmResolve = null;
let activeTutorialPages = [];
let activeTutorialPageIndex = 0;
let pendingTypeUpdate = null;
let editContext = null;
let editReturnTo = "";
let editModifiers = [];
let editBonuses = [];
let editEffectTypeKeys = [];
let editSuspend = null;
let attributeTypeOptions = [];
let attributeEffectOptions = [];
let effectTypeOptions = [];
let damageTypeOptions = [];
let weightUnitOptions = [];
let weightSystem = "";
let weaponEffectOptions = [];
let raceSkillOptions = [];
let raceAttributeOptions = [];
let classSkillOptions = [];
let classAttributeOptions = [];
let classHitDieOptions = [];
let editClassSkillPointsByLevel = [];
let editWeaponEffectIds = [];
let editRaceSkillIds = [];
let editRaceAttributeLimits = [];
let editClassSkillIds = [];
let editClassRequiredScores = [];
let editEffectTypeIndex = -1;
let editModifierIndex = -1;
let editBonusIndex = -1;
let editWeaponEffectIndex = -1;
let editRaceTraitIndex = -1;
let editRaceAttributeIndex = -1;
let editClassSkillIndex = -1;
let editClassRequiredIndex = -1;
let editClassSkillPointsIndex = -1;
let skillContext = null;
let skillReturnToEdit = false;
let skillEffects = [];
let skillEffectOptions = [];
let skillAbilityOptions = [];
let skillCategoryOptions = [];
let skillClassOptions = [];
let skillRaceOptions = [];
let skillLimitedClassIds = [];
let skillLimitedRaceIds = [];
let skillEffectIndex = -1;
let skillClassLimitIndex = -1;
let skillRaceLimitIndex = -1;
let spellContext = null;
let spellEffects = [];
let spellEffectOptions = [];
let spellEffectIndex = -1;
state.locale = "en";

let transientZIndex = 1000;
let lockedAccountEmail = "";

function bringTransientToFront(element) {
  transientZIndex += 1;
  element.style.zIndex = String(transientZIndex);
}

function trackTransientStacking() {
  document.querySelectorAll(".modal").forEach((modal) => {
    const updateModalStack = () => {
      if (modal.classList.contains("hidden")) {
        modal.style.zIndex = "";
        return;
      }
      bringTransientToFront(modal);
    };
    new MutationObserver(updateModalStack).observe(modal, {
      attributes: true,
      attributeFilter: ["class"],
    });
    updateModalStack();
  });
  if (toast) {
    const updateToastStack = () => {
      if (!toast.classList.contains("show")) {
        toast.style.zIndex = "";
        return;
      }
      bringTransientToFront(toast);
    };
    new MutationObserver(updateToastStack).observe(toast, {
      attributes: true,
      attributeFilter: ["class"],
    });
    updateToastStack();
  }
}

function t(key, fallback) {
  if (key in state.strings) {
    return state.strings[key];
  }
  return fallback || key;
}

function isMechanicDescriptionScreen(screenKey) {
  return screenKey !== "setup" && steps.some((entry) => entry.id === screenKey);
}

async function loadMechanicDescriptions() {
  const draftId = String(state.draftId || "").trim();
  if (!draftId) {
    return {};
  }
  if (state.mechanicDescriptionsDraftId === draftId) {
    return state.mechanicDescriptions;
  }
  if (
    state.mechanicDescriptionsLoadingDraftId === draftId
    && state.mechanicDescriptionsLoadPromise
  ) {
    return state.mechanicDescriptionsLoadPromise;
  }

  state.mechanicDescriptionsLoadingDraftId = draftId;
  const loadPromise = (async () => {
    const data = await api("GET", `/api/drafts/${draftId}/mechanic-descriptions`);
    const rawDescriptions = data && typeof data.descriptions === "object"
      ? data.descriptions
      : {};
    const descriptions = {};
    Object.entries(rawDescriptions || {}).forEach(([key, value]) => {
      const safeKey = String(key || "").trim().toLowerCase();
      if (isMechanicDescriptionScreen(safeKey)) {
        descriptions[safeKey] = String(value || "");
      }
    });
    if (state.draftId === draftId) {
      state.mechanicDescriptions = descriptions;
      state.mechanicDescriptionsDraftId = draftId;
    }
    return descriptions;
  })();
  state.mechanicDescriptionsLoadPromise = loadPromise;

  try {
    return await loadPromise;
  } finally {
    if (state.mechanicDescriptionsLoadPromise === loadPromise) {
      state.mechanicDescriptionsLoadPromise = null;
      state.mechanicDescriptionsLoadingDraftId = "";
    }
  }
}

async function ensureMechanicDescriptionSection() {
  const screenKey = String(state.step || "").trim();
  if (
    state.mode !== "builder"
    || !state.draftId
    || !isMechanicDescriptionScreen(screenKey)
  ) {
    return;
  }

  const title = view.querySelector(".panel > h1");
  if (!title) {
    return;
  }
  const existing = view.querySelector(".mechanic-description-section");
  if (existing && existing.dataset.mechanicDescriptionScreen === screenKey) {
    return;
  }
  if (existing) {
    existing.remove();
  }

  const section = document.createElement("details");
  section.className = "mechanic-description-section";
  section.dataset.mechanicDescriptionScreen = screenKey;
  section.innerHTML = `
    <summary>
      <span>${escapeHtml(t("web.mechanic_description.title", "Describe This Section"))}</span>
      <span class="mechanic-description-summary-note">${escapeHtml(
        t("web.mechanic_description.optional", "Creator description")
      )}</span>
    </summary>
    <div class="mechanic-description-content">
      <p>${escapeHtml(t(
        "web.mechanic_description.help",
        "Describe this part of your game in your own words. This creator-written description, not the GMRules guidance, will be available to players in other applications."
      ))}</p>
      <textarea
        class="mechanic-description-input"
        rows="8"
        maxlength="20000"
        disabled
        aria-label="${escapeHtml(t("web.mechanic_description.input_label", "Mechanic description"))}"
        placeholder="${escapeHtml(t("web.loading", "Loading..."))}"
      ></textarea>
      <div class="mechanic-description-actions">
        <span class="mechanic-description-status" aria-live="polite"></span>
      </div>
    </div>
  `;
  title.insertAdjacentElement("afterend", section);

  const input = section.querySelector(".mechanic-description-input");
  const status = section.querySelector(".mechanic-description-status");
  try {
    const descriptions = await loadMechanicDescriptions();
    if (
      !section.isConnected
      || state.step !== screenKey
      || state.mode !== "builder"
    ) {
      return;
    }
    input.value = String(descriptions[screenKey] || "");
    input.placeholder = t(
      "web.mechanic_description.placeholder",
      "Enter the description players will see for this mechanic."
    );
    input.disabled = false;
  } catch (error) {
    if (!section.isConnected) {
      return;
    }
    status.textContent = error.message || t("common.error", "Something went wrong.");
    input.placeholder = t(
      "web.mechanic_description.load_failed",
      "Description could not be loaded."
    );
  }

  section.addEventListener("toggle", () => {
    if (!section.open) {
      void saveMechanicDescriptionSection(section);
    }
  });
}

async function saveMechanicDescriptionSection(section) {
  const input = section ? section.querySelector(".mechanic-description-input") : null;
  const status = section ? section.querySelector(".mechanic-description-status") : null;
  const screenKey = section
    ? String(section.dataset.mechanicDescriptionScreen || "").trim()
    : "";
  if (!input || !status || !isMechanicDescriptionScreen(screenKey)) {
    return true;
  }
  if (section.mechanicDescriptionSavePromise) {
    return section.mechanicDescriptionSavePromise;
  }
  const description = String(input.value || "");
  if (description === String(state.mechanicDescriptions[screenKey] || "")) {
    return true;
  }

  const draftId = state.draftId;
  const savePromise = (async () => {
    input.disabled = true;
    status.textContent = t("web.mechanic_description.saving", "Saving...");
    try {
      await api("POST", `/api/drafts/${draftId}/mechanic-descriptions`, {
        screenKey,
        description,
      });
      state.mechanicDescriptions[screenKey] = description;
      status.textContent = t("web.mechanic_description.saved", "Description saved.");
      markSaved(t("web.mechanic_description.saved", "Description saved."));
      return true;
    } catch (error) {
      status.textContent = error.message || t("common.error", "Something went wrong.");
      return false;
    } finally {
      if (section.isConnected) {
        input.disabled = false;
      }
    }
  })();
  section.mechanicDescriptionSavePromise = savePromise;
  try {
    return await savePromise;
  } finally {
    if (section.mechanicDescriptionSavePromise === savePromise) {
      delete section.mechanicDescriptionSavePromise;
    }
  }
}

new MutationObserver(() => {
  void ensureMechanicDescriptionSection();
}).observe(view, {
  childList: true,
  subtree: true,
});

view.addEventListener("click", async (event) => {
  const continueButton = event.target.closest("button[id$='Continue']");
  const section = view.querySelector(".mechanic-description-section");
  if (!continueButton || !section || continueButton.dataset.descriptionSaveBypass === "true") {
    return;
  }
  event.preventDefault();
  event.stopImmediatePropagation();
  if (await saveMechanicDescriptionSection(section)) {
    continueButton.dataset.descriptionSaveBypass = "true";
    continueButton.click();
    delete continueButton.dataset.descriptionSaveBypass;
  }
}, true);

function normalizeLocale(value) {
  const raw = String(value || "").toLowerCase();
  if (raw.startsWith("fr")) {
    return "fr";
  }
  if (raw.startsWith("en")) {
    return "en";
  }
  return "en";
}

function emptyFallback(value) {
  const safeValue = String(value || "").trim();
  return safeValue.length ? safeValue : t("common.none", "None");
}

function readStoredSessionToken() {
  try {
    return String(window.sessionStorage.getItem(SESSION_TOKEN_KEY) || "").trim();
  } catch (error) {
    return "";
  }
}

function storeSessionToken(token) {
  const safeToken = String(token || "").trim();
  try {
    if (safeToken) {
      window.sessionStorage.setItem(SESSION_TOKEN_KEY, safeToken);
    } else {
      window.sessionStorage.removeItem(SESSION_TOKEN_KEY);
    }
  } catch (error) {
    // Session storage may be unavailable in private or locked-down browser modes.
  }
}

function clearStoredSessionToken() {
  storeSessionToken("");
}

function consumeLocalSessionTokenFromUrl() {
  const rawHash = String(window.location.hash || "");
  if (!rawHash.startsWith("#")) {
    return "";
  }
  const params = new URLSearchParams(rawHash.slice(1));
  const localSessionToken = String(params.get(LOCAL_SESSION_FRAGMENT_KEY) || "").trim();
  if (!localSessionToken) {
    return "";
  }
  const cleanUrl = new URL(window.location.href);
  cleanUrl.hash = "";
  window.history.replaceState(window.history.state, "", `${cleanUrl.pathname}${cleanUrl.search}`);
  return localSessionToken;
}

function applyStaticLabels() {
  document.title = t("web.title", "GMRules Web");
  const brandName = document.getElementById("brandName");
  if (brandName) {
    brandName.textContent = t("web.brand", "GMRules Web");
  }
  saveStatus.textContent = t("web.save.empty", "Not saved yet");
  if (feedbackBtn) {
    feedbackBtn.textContent = t("web.feedback.button", "Report");
  }
  if (homeBtn) {
    homeBtn.textContent = t("web.home.button", "Home");
  }
  if (tutorialInfoBtn) {
    tutorialInfoBtn.textContent = t("web.tutorial.info_button", "Info");
  }
  if (fontSizeSliderLabel) {
    fontSizeSliderLabel.textContent = t("web.font_size.label", "Text size");
  }
  if (fontSizeAllScreensLabel) {
    fontSizeAllScreensLabel.textContent = t("web.font_size.all_screens", "Change all screens");
  }
  applyFontSizePreference();
  if (adminBtn) {
    adminBtn.textContent = t("web.admin.button", "Admin");
  }
  downloadBtn.textContent = t("web.download", "Download .gmrf");
  logoutBtn.textContent = t("web.logout", "Logout");
  confirmTitle.textContent = t("web.confirm.title", "Confirm");
  confirmCancel.textContent = t("common.cancel", "Cancel");
  if (tutorialTitle) {
    tutorialTitle.textContent = t("web.tutorial.title", "Tutorial");
  }
  if (tutorialMessage) {
    tutorialMessage.textContent = t(
      "web.tutorial.placeholder",
      "The tutorial explanations are triggering this popup"
    );
  }
  if (tutorialOk) {
    tutorialOk.textContent = t("common.ok", "OK");
  }
  typeTitle.textContent = t("web.type_modal.title", "Change Attribute Category");
  typeLabel.textContent = t("web.type_modal.type", "Type");
  typeCancel.textContent = t("common.cancel", "Cancel");
  typeOk.textContent = t("web.type_modal.apply", "Apply");
  deleteAccountTitle.textContent = t("web.account_delete.title", "Permanently Delete Account");
  deleteAccountMessage.textContent = t(
    "web.account_delete.message",
    "This permanently deletes the account and all saved rulesets for that account. Enter the email and password if one is set."
  );
  deleteAccountUsernameLabel.textContent = t("web.login.email", "Email");
  deleteAccountPasswordLabel.textContent = t("web.login.password", "Password");
  deleteAccountCancel.textContent = t("common.cancel", "Cancel");
  deleteAccountOk.textContent = t("web.account_delete.confirm", "Permanently Delete Account");
  if (feedbackTitle) {
    feedbackTitle.textContent = t("web.feedback.title", "Report Feedback");
  }
  if (feedbackTypeLabel) {
    feedbackTypeLabel.textContent = t("web.feedback.type", "Type");
  }
  if (feedbackType) {
    const typeLabels = {
      feedback: t("web.feedback.type.feedback", "Feedback"),
      bug: t("web.feedback.type.bug", "Bug Report"),
      blocker: t("web.feedback.type.blocker", "Blocker / Crash"),
    };
    Array.from(feedbackType.options).forEach((option) => {
      option.textContent = typeLabels[option.value] || option.textContent;
    });
  }
  if (feedbackSeverityLabel) {
    feedbackSeverityLabel.textContent = t("web.feedback.severity", "Severity");
  }
  if (feedbackSeverity) {
    const severityLabels = {
      low: t("web.feedback.severity.low", "Low"),
      medium: t("web.feedback.severity.medium", "Medium"),
      high: t("web.feedback.severity.high", "High"),
      critical: t("web.feedback.severity.critical", "Critical"),
    };
    Array.from(feedbackSeverity.options).forEach((option) => {
      option.textContent = severityLabels[option.value] || option.textContent;
    });
  }
  if (feedbackShortTitleLabel) {
    feedbackShortTitleLabel.textContent = t("web.feedback.short_title", "Short Title");
  }
  if (feedbackMessageLabel) {
    feedbackMessageLabel.textContent = t("web.feedback.details", "Details");
  }
  if (feedbackStepsLabel) {
    feedbackStepsLabel.textContent = t("web.feedback.steps", "Steps to Reproduce");
  }
  if (feedbackPrivacyHint) {
    feedbackPrivacyHint.textContent = t(
      "web.feedback.privacy",
      "Reports include account and page metadata, but not your ruleset file or full draft content."
    );
  }
  if (feedbackCancel) {
    feedbackCancel.textContent = t("common.cancel", "Cancel");
  }
  if (feedbackSubmit) {
    feedbackSubmit.textContent = t("web.feedback.submit", "Submit Report");
  }
  if (lockedAccountTitle) {
    lockedAccountTitle.textContent = t("web.login.locked_password_title", "Locked After Failed Password Attempts");
  }
  if (lockedAccountMessage) {
    lockedAccountMessage.textContent = t(
      "web.login.locked_password_message",
      "This account was locked after repeated password failures. Use Forgot Password to verify by email and choose a new password."
    );
  }
  if (lockedAccountDetailsLabel) {
    lockedAccountDetailsLabel.textContent = t("web.login.locked_details", "Message");
  }
  if (lockedAccountCancel) {
    lockedAccountCancel.textContent = t("common.cancel", "Cancel");
  }
  if (lockedAccountSubmit) {
    lockedAccountSubmit.textContent = t("web.login.forgot_password", "Forgot Password?");
  }
  editNameLabel.textContent = t("common.name", "Name");
  editDescriptionLabel.textContent = t("common.description", "Description");
  if (editWeightValueLabel) {
    editWeightValueLabel.textContent = t("common.weight", "Weight");
  }
  if (editWeightUnitLabel) {
    editWeightUnitLabel.textContent = t("common.weight.unit", "Weight Unit");
  }
  if (editDamageTypeLabel) {
    editDamageTypeLabel.textContent = t("damagetypes.field", "Damage Type");
  }
  editTypeLabel.textContent = t("attributes.edit.type", "Attribute Category");
  if (editTypeAdd) {
    editTypeAdd.textContent = t("effects.type.add", "Add System");
  }
  editMinLabel.textContent = t("attributes.edit.min_value", "Minimum Value");
  editMaxLabel.textContent = t("attributes.edit.max_value", "Maximum Value");
  editModifierTitle.textContent = t("attributes.edit.modifiers", "Modifiers");
  editModifierScoreLabel.textContent = t("attributes.edit.modifier.score", "Score");
  editModifierValueLabel.textContent = t("attributes.edit.modifier.value", "Modifier");
  editModifierAdd.textContent = t("attributes.edit.modifier.add", "Add Modifier");
  if (editModifierApplyAllLabel) {
    editModifierApplyAllLabel.textContent = t(
      "attributes.edit.modifier.apply_all",
      "Apply modifiers to all attributes"
    );
    editModifierApplyAllLabel.closest("label").classList.add("hidden");
  }
  editBonusTitle.textContent = t("attributes.edit.bonuses", "Score Bonuses");
  editBonusThresholdLabel.textContent = t("attributes.edit.bonus.threshold", "Threshold");
  editBonusEffectLabel.textContent = t("attributes.edit.bonus.effect", "Effect");
  editBonusAdd.textContent = t("attributes.edit.bonus.add", "Add Bonus");
  if (editWeaponEffectsTitle) {
    editWeaponEffectsTitle.textContent = t("skills.effects", "Effects");
  }
  if (editWeaponDamageCountLabel) {
    editWeaponDamageCountLabel.textContent = t("attrgen.dice.count", "Number of Rolls");
  }
  if (editWeaponDamageSidesLabel) {
    editWeaponDamageSidesLabel.textContent = t("attrgen.dice.sides", "Dice Sides");
  }
  if (editWeaponDamageModifierLabel) {
    editWeaponDamageModifierLabel.textContent = t("common.modifier", "Modifier");
  }
  if (editClassTitle) {
    editClassTitle.textContent = t("classes.title", "Classes");
  }
  if (editClassPrimaryLabel) {
    editClassPrimaryLabel.textContent = t("classes.primary_attribute", "Primary Attribute");
  }
  if (editClassHitDieLabel) {
    editClassHitDieLabel.textContent = t("classes.hit_die", "Hit Die");
  }
  if (editClassHitDieModifierLabel) {
    editClassHitDieModifierLabel.textContent = t("common.modifier", "Modifier");
  }
  if (editClassSkillPointsLabel) {
    editClassSkillPointsLabel.textContent = t("classes.skill_points", "Skill Points per Level");
  }
  if (editClassStartingMoneyLabel) {
    editClassStartingMoneyLabel.textContent = t("money.class.starting", "Class Starting Money (override)");
  }
  if (editClassSkillPointsSameLabel) {
    editClassSkillPointsSameLabel.textContent = t("classes.skill_points.same_all", "Same at all levels");
  }
  if (editClassSkillPointsLevelLabel) {
    editClassSkillPointsLevelLabel.textContent = t("classes.skill_points.level", "Level");
  }
  if (editClassSkillPointsValueLabel) {
    editClassSkillPointsValueLabel.textContent = t("classes.skill_points.value", "Points");
  }
  if (editClassSkillPointsAdd) {
    editClassSkillPointsAdd.textContent = t("classes.skill_points.add", "Add Level");
  }
  if (editClassSkillsTitle) {
    editClassSkillsTitle.textContent = t("classes.skills", "Class Skills");
  }
  if (editClassSkillSelectLabel) {
    editClassSkillSelectLabel.textContent = t("classes.skills.select", "Select Skill");
  }
  if (editClassSkillAdd) {
    editClassSkillAdd.textContent = t("classes.skills.add", "Add Skill");
  }
  if (editClassRequiredTitle) {
    editClassRequiredTitle.textContent = t("classes.required_attributes", "Required Attributes");
  }
  if (editClassRequiredSelectLabel) {
    editClassRequiredSelectLabel.textContent = t("classes.required_attributes.select", "Select Attribute");
  }
  if (editClassRequiredScoreLabel) {
    editClassRequiredScoreLabel.textContent = t("classes.required_attributes.score", "Score");
  }
  if (editClassRequiredAdd) {
    editClassRequiredAdd.textContent = t("classes.required_attributes.add", "Add Requirement");
  }
  if (editWeaponEffectSelectLabel) {
    editWeaponEffectSelectLabel.textContent = t("skills.effects.select", "Select Effect");
  }
  if (editWeaponEffectAdd) {
    editWeaponEffectAdd.textContent = t("skills.effects.add", "Add Effect");
  }
  if (editRaceTraitsTitle) {
    editRaceTraitsTitle.textContent = t("races.traits.title", "Racial Traits");
  }
  if (editRaceTraitLabel) {
    editRaceTraitLabel.textContent = t("races.traits.default", "Default Skills");
  }
  if (editRaceTraitAdd) {
    editRaceTraitAdd.textContent = t("races.traits.add", "Add Trait");
  }
  if (editRaceAttributesTitle) {
    editRaceAttributesTitle.textContent = t("races.attributes.title", "Attribute Limits");
  }
  if (editRaceAttributeLabel) {
    editRaceAttributeLabel.textContent = t("races.attributes.select", "Select Attribute");
  }
  if (editRaceAttributeMinLabel) {
    editRaceAttributeMinLabel.textContent = t("attributes.edit.min_value", "Minimum Value");
  }
  if (editRaceAttributeMaxLabel) {
    editRaceAttributeMaxLabel.textContent = t("attributes.edit.max_value", "Maximum Value");
  }
  if (editRaceAttributeAdd) {
    editRaceAttributeAdd.textContent = t("races.attributes.add", "Add Limit");
  }
  if (editRaceStartingMoneyModifierLabel) {
    editRaceStartingMoneyModifierLabel.textContent = t("money.race.modifier", "Race Starting Money Modifier");
  }
  editCancel.textContent = t("common.cancel", "Cancel");
  editOk.textContent = t("common.ok", "OK");
  if (skillModalTitle) {
    skillModalTitle.textContent = t("skills.edit.title", "Edit Skill");
  }
  if (skillNameLabel) {
    skillNameLabel.textContent = t("common.name", "Name");
  }
  if (skillCategoryLabel) {
    skillCategoryLabel.textContent = t("skills.category", "Category");
  }
  if (skillDescriptionLabel) {
    skillDescriptionLabel.textContent = t("common.description", "Description");
  }
  if (skillAbilityLabel) {
    skillAbilityLabel.textContent = t("skills.ability", "Related Attribute");
  }
  if (skillTrainedLabel) {
    skillTrainedLabel.textContent = t("skills.trained_only", "Trained Only");
  }
  if (skillArmorLabel) {
    skillArmorLabel.textContent = t("skills.armor_penalty", "Armor Check Penalty");
  }
  if (skillStartingMoneyModifierLabel) {
    skillStartingMoneyModifierLabel.textContent = t("money.trait.modifier", "Trait Starting Money Modifier");
  }
  if (skillClassLimitsTitle) {
    skillClassLimitsTitle.textContent = t("skills.limits.classes", "Class Restrictions");
  }
  if (skillClassLimitSelectLabel) {
    skillClassLimitSelectLabel.textContent = t("skills.limits.class_select", "Select Class");
  }
  if (skillClassLimitAdd) {
    skillClassLimitAdd.textContent = t("skills.limits.add_class", "Add Class");
  }
  if (skillRaceLimitsTitle) {
    skillRaceLimitsTitle.textContent = t("skills.limits.races", "Race Restrictions");
  }
  if (skillRaceLimitSelectLabel) {
    skillRaceLimitSelectLabel.textContent = t("skills.limits.race_select", "Select Race");
  }
  if (skillRaceLimitAdd) {
    skillRaceLimitAdd.textContent = t("skills.limits.add_race", "Add Race");
  }
  if (skillEffectsTitle) {
    skillEffectsTitle.textContent = t("skills.effects", "Effects");
  }
  if (skillEffectSelectLabel) {
    skillEffectSelectLabel.textContent = t("skills.effects.select", "Select Effect");
  }
  if (skillEffectAdd) {
    skillEffectAdd.textContent = t("skills.effects.add", "Add Effect");
  }
  if (skillCancel) {
    skillCancel.textContent = t("common.cancel", "Cancel");
  }
  if (skillSave) {
    skillSave.textContent = t("common.ok", "OK");
  }
  if (spellModalTitle) {
    spellModalTitle.textContent = t("spells.edit.title", "Edit Spell");
  }
  if (spellNameLabel) {
    spellNameLabel.textContent = t("common.name", "Name");
  }
  if (spellDescriptionLabel) {
    spellDescriptionLabel.textContent = t("common.description", "Description");
  }
  if (spellSchoolLabel) {
    spellSchoolLabel.textContent = t("spells.school", "School");
  }
  if (spellLevelLabel) {
    spellLevelLabel.textContent = t("spells.level", "Level");
  }
  if (spellCastingLabel) {
    spellCastingLabel.textContent = t("spells.casting_time", "Casting Time");
  }
  if (spellRangeLabel) {
    spellRangeLabel.textContent = t("spells.range", "Range");
  }
  if (spellDurationLabel) {
    spellDurationLabel.textContent = t("spells.duration", "Duration");
  }
  if (spellDamageTypeLabel) {
    spellDamageTypeLabel.textContent = t("damagetypes.field", "Damage Type");
  }
  if (spellEffectTitle) {
    spellEffectTitle.textContent = t("spells.effects", "Effects");
  }
  if (spellEffectSelectLabel) {
    spellEffectSelectLabel.textContent = t("skills.effects.select", "Select Effect");
  }
  if (spellEffectAdd) {
    spellEffectAdd.textContent = t("skills.effects.add", "Add Effect");
  }
  if (spellCancel) {
    spellCancel.textContent = t("common.cancel", "Cancel");
  }
  if (spellSave) {
    spellSave.textContent = t("common.ok", "OK");
  }
  renderSidebar();
}

function showToast(message) {
  toast.textContent = message;
  toast.classList.add("show");
  setTimeout(() => toast.classList.remove("show"), 8000);
}

async function loadLocalization(language) {
  const target = normalizeLocale(language || "en");
  try {
    const cacheStamp = Date.now();
    const response = await api(
      "GET",
      `/api/i18n?lang=${encodeURIComponent(target)}&ts=${cacheStamp}`
    );
    state.strings = response.strings || {};
    state.locale = normalizeLocale(response.locale || target);
  } catch (error) {
    state.strings = {};
    state.locale = target;
  }
  document.documentElement.lang = state.locale || "en";
  applyStaticLabels();
}

async function loadSystemNames() {
  if (!state.draftId) {
    state.systemNames = {};
    renderSidebar();
    return;
  }
  try {
    const cacheStamp = Date.now();
    const response = await api(
      "GET",
      `/api/drafts/${state.draftId}/system-names?ts=${cacheStamp}`
    );
    const rawNames = response.systemNames || {};
    const normalized = {};
    Object.keys(rawNames).forEach((key) => {
      const safeKey = String(key || "").trim().toLowerCase();
      if (!safeKey) {
        return;
      }
      const value = String(rawNames[key] || "").trim();
      if (value) {
        normalized[safeKey] = value;
      }
    });
    state.systemNames = normalized;
  } catch (error) {
    state.systemNames = {};
  }
  renderSidebar();
}

async function api(method, path, body) {
  const options = { method, headers: {}, cache: "no-store" };
  if (state.sessionToken) {
    options.headers.Authorization = `Bearer ${state.sessionToken}`;
  }
  if (body !== undefined && body !== null) {
    options.headers["Content-Type"] = "application/json";
    options.body = JSON.stringify(body);
  }
  const response = await fetch(path, options);
  const data = await readApiJson(response, path);
  if (!response.ok) {
    const error = new Error(data.error || t("web.error.request_failed", "Request failed"));
    error.status = response.status;
    error.code = data.code || "";
    error.data = data;
    throw error;
  }
  return data;
}

async function apiBinary(method, path, buffer) {
  const headers = { "Content-Type": "application/octet-stream" };
  if (state.sessionToken) {
    headers.Authorization = `Bearer ${state.sessionToken}`;
  }
  const response = await fetch(path, {
    method,
    headers,
    cache: "no-store",
    body: buffer,
  });
  const data = await readApiJson(response, path);
  if (!response.ok) {
    const error = new Error(data.error || t("web.error.request_failed", "Request failed"));
    error.status = response.status;
    error.code = data.code || "";
    error.data = data;
    throw error;
  }
  return data;
}

async function readApiJson(response, path) {
  const text = await response.text();
  if (!text) {
    return {};
  }
  try {
    return JSON.parse(text);
  } catch (parseError) {
    const error = new Error(buildNonJsonApiMessage(response, path, text));
    error.status = response.status;
    error.code = "non_json_response";
    error.data = {};
    throw error;
  }
}

function buildNonJsonApiMessage(response, path, text) {
  const status = response.status ? ` (${response.status})` : "";
  const contentType = String(response.headers.get("Content-Type") || "").toLowerCase();
  const trimmed = String(text || "").trim();
  if (contentType.includes("html") || trimmed.toLowerCase().startsWith("<!doctype") || trimmed.toLowerCase().startsWith("<html")) {
    return t(
      "web.error.non_json_html",
      "Server returned an HTML page for {path}{status}. Refresh and try again; if it repeats, the deployed server or proxy may be serving the wrong route."
    )
      .replace("{path}", path)
      .replace("{status}", status);
  }
  return t("web.error.non_json", "Server returned an unexpected response for {path}{status}.")
    .replace("{path}", path)
    .replace("{status}", status);
}

async function apiBinaryCharacterImport(file) {
  const buffer = await file.arrayBuffer();
  const headers = { "Content-Type": "application/octet-stream" };
  if (state.sessionToken) {
    headers.Authorization = `Bearer ${state.sessionToken}`;
  }
  const response = await fetch("/api/characters/import", {
    method: "POST",
    headers,
    cache: "no-store",
    body: buffer,
  });
  const text = await response.text();
  let data = {};
  try {
    data = text ? JSON.parse(text) : {};
  } catch (parseError) {
    data = { error: text };
  }
  if (!response.ok) {
    let message = data.error || t("web.error.request_failed", "Request failed");
    if (response.status === 404 && String(message || "").trim().toLowerCase() === "not found") {
      message = t(
        "web.chargen.import_endpoint_missing",
        "Character import is not available on this server yet. Redeploy the latest server build."
      );
    }
    const error = new Error(message);
    error.status = response.status;
    error.code = data.code || "";
    error.data = data;
    throw error;
  }
  return data;
}

function setLoggedIn(isLoggedIn) {
  document.body.classList.toggle("logged-out", !isLoggedIn);
  logoutBtn.classList.toggle("hidden", isLoggedIn && state.localMode);
}

function setMode(mode) {
  const safeMode = ["home", "builder", "chargen"].includes(mode) ? mode : "home";
  state.mode = safeMode;
  applyFontSizePreference();
  document.body.classList.toggle("mode-home", safeMode === "home");
  document.body.classList.toggle("mode-builder", safeMode === "builder");
  document.body.classList.toggle("mode-chargen", safeMode === "chargen");
  if (safeMode !== "builder") {
    resetVisited();
    appBackStack.length = 0;
  }
  updateStepIndicator();
  renderSidebar();
  updateActions();
}

function updateStepIndicator() {
  if (state.mode !== "builder") {
    stepIndicator.textContent = "";
    return;
  }
  const step = steps.find((entry) => entry.id === state.step);
  if (!step) {
    stepIndicator.textContent = "";
    return;
  }
  const prefix = t("web.step.prefix", "Step:");
  stepIndicator.textContent = `${prefix} ${t(step.labelKey, step.fallback)}`;
}

function buildHistoryHash(stepId) {
  const safeStep = String(stepId || "").trim();
  if (!safeStep) {
    return "";
  }
  return `#/${encodeURIComponent(safeStep)}`;
}

function syncHistory(stepId, replace) {
  if (!window.history || typeof window.history.pushState !== "function") {
    return;
  }
  const safeStep = String(stepId || "").trim();
  if (!safeStep) {
    return;
  }
  const payload = { step: safeStep };
  const url = buildHistoryHash(safeStep);
  if (replace) {
    window.history.replaceState(payload, "", url);
  } else {
    window.history.pushState(payload, "", url);
  }
}

function ensureHistoryReady() {
  if (historyReady) {
    return;
  }
  syncHistory(state.step, true);
  historyReady = true;
}

function recordAppBack(stepId) {
  const safeStep = String(stepId || "").trim();
  if (!safeStep) {
    return;
  }
  const last = appBackStack[appBackStack.length - 1];
  if (last === safeStep) {
    return;
  }
  appBackStack.push(safeStep);
}

function setStep(stepId) {
  const safeStep = String(stepId || "").trim();
  const previousStep = state.step;
  const isSameStep = previousStep === safeStep;
  if (!isSameStep && !appBackLocked) {
    recordAppBack(previousStep);
  }
  state.step = safeStep;
  applyFontSizePreference();
  if (!isSameStep) {
    window.scrollTo({ top: 0, left: 0, behavior: "auto" });
  }
  markVisited(safeStep);
  markTutorialScreenVisited(safeStep);
  updateStepIndicator();
  renderSidebar();
  if (isSameStep || historyLocked || !historyReady) {
    return;
  }
  syncHistory(safeStep, false);
}

function navigateBackInApp() {
  if (!appBackStack.length) {
    if (state.mode === "builder") {
      renderBuilderSplash();
    }
    return;
  }
  const previousStep = appBackStack.pop();
  appBackLocked = true;
  navigateToHistoryStep(previousStep);
  appBackLocked = false;
}

function markVisited(stepId) {
  const index = steps.findIndex((entry) => entry.id === stepId);
  if (index < 0) {
    return;
  }
  for (let i = 0; i <= index; i += 1) {
    visitedSteps.add(steps[i].id);
  }
}

function resetVisited() {
  visitedSteps.clear();
  state.setupComplete = false;
  renderSidebar();
}

function applyCompletedStages(stageKeys) {
  const safeKeys = Array.isArray(stageKeys) ? stageKeys : [];
  const stageSet = new Set(
    safeKeys
      .map((value) => String(value || "").trim())
      .filter((value) => value.length > 0)
  );
  state.setupComplete = stageSet.has("setup");
  visitedSteps.clear();
  let maxIndex = -1;
  steps.forEach((entry, index) => {
    if (stageSet.has(entry.id) && index > maxIndex) {
      maxIndex = index;
    }
  });
  if (maxIndex >= 0) {
    for (let i = 0; i <= maxIndex; i += 1) {
      visitedSteps.add(steps[i].id);
    }
  }
  renderSidebar();
}

function normalizeTutorialScreenKey(value) {
  const safeValue = String(value || "").trim().toLowerCase();
  if (!TUTORIAL_SCREEN_KEY_PATTERN.test(safeValue)) {
    return "";
  }
  return safeValue;
}

function tutorialSeenKey(screenKey) {
  const safeKey = normalizeTutorialScreenKey(screenKey);
  if (!safeKey) {
    return "";
  }
  const contentVersion = TUTORIAL_CONTENT_VERSION_BY_SCREEN[safeKey] || TUTORIAL_CONTENT_VERSION;
  return `${contentVersion}:${safeKey}`;
}

function applyTutorialVisitedScreens(screenKeys) {
  tutorialVisitedScreens.clear();
  const safeKeys = Array.isArray(screenKeys) ? screenKeys : [];
  safeKeys.forEach((screenKey) => {
    const safeKey = normalizeTutorialScreenKey(screenKey);
    if (safeKey) {
      tutorialVisitedScreens.add(safeKey);
    }
  });
}

function markTutorialScreenVisited(screenKey) {
  const safeKey = normalizeTutorialScreenKey(screenKey);
  if (!safeKey || safeKey === "admin") {
    return;
  }
  const shouldPersistTutorial = Boolean(state.sessionToken) && !state.legacyGuest;
  const safeSeenKey = tutorialSeenKey(safeKey);
  const alreadyVisited = tutorialVisitedScreens.has(safeKey);
  const alreadySawTutorial = safeSeenKey ? tutorialVisitedScreens.has(safeSeenKey) : true;
  if (alreadyVisited && (!shouldPersistTutorial || alreadySawTutorial)) {
    return;
  }
  const screensToRecord = [];
  if (!alreadyVisited) {
    tutorialVisitedScreens.add(safeKey);
    screensToRecord.push(safeKey);
  }
  if (shouldPersistTutorial && !alreadySawTutorial && safeSeenKey) {
    tutorialVisitedScreens.add(safeSeenKey);
    screensToRecord.push(safeSeenKey);
    openTutorialPopup(safeKey);
  }
  if (!shouldPersistTutorial || !screensToRecord.length) {
    return;
  }
  api("POST", "/api/tutorial/visited", { screens: screensToRecord })
    .then((result) => {
      const visited = result.tutorialVisitedScreens;
      if (!Array.isArray(visited)) {
        return;
      }
      visited.forEach((entry) => {
        const normalized = normalizeTutorialScreenKey(entry);
        if (normalized) {
          tutorialVisitedScreens.add(normalized);
        }
      });
    })
    .catch(() => {
      // Tutorial tracking must not interrupt the builder or character flow.
    });
}

function buildTutorialPages(tutorialStep) {
  if (!tutorialStep) {
    return [{
      titleKey: "web.tutorial.title",
      titleFallback: "Tutorial",
      paragraphs: [],
    }];
  }
  if (Array.isArray(tutorialStep.tutorialPages) && tutorialStep.tutorialPages.length) {
    return tutorialStep.tutorialPages;
  }
  const paragraphs = Array.isArray(tutorialStep.tutorialParagraphs)
    ? tutorialStep.tutorialParagraphs
    : [{ key: tutorialStep.tutorialKey, fallback: tutorialStep.tutorialFallback }];
  const pages = [{
    titleKey: tutorialStep.labelKey,
    titleFallback: tutorialStep.fallback,
    paragraphs,
  }];
  const specificPage = tutorialSpecificPages[tutorialStep.id];
  if (specificPage) {
    pages.push(specificPage);
  }
  return pages;
}

function renderActiveTutorialPage() {
  const pageCount = activeTutorialPages.length;
  const safeIndex = Math.max(0, Math.min(activeTutorialPageIndex, Math.max(0, pageCount - 1)));
  activeTutorialPageIndex = safeIndex;
  const page = activeTutorialPages[safeIndex] || {};
  const paragraphs = Array.isArray(page.paragraphs) ? page.paragraphs : [];
  if (tutorialTitle) {
    tutorialTitle.textContent = page.titleText || t(
      page.titleKey || "web.tutorial.title",
      page.titleFallback || "Tutorial"
    );
  }
  if (tutorialMessage) {
    tutorialMessage.textContent = paragraphs.length
      ? paragraphs.map((paragraph) => {
        return paragraph.text || t(paragraph.key, paragraph.fallback);
      }).join("\n\n")
      : t("web.tutorial.placeholder", "Tutorial guidance for this screen is coming soon.");
  }
  const hasMultiplePages = pageCount > 1;
  const isLastPage = safeIndex >= pageCount - 1;
  if (tutorialBack) {
    tutorialBack.textContent = t("common.back", "Back");
    tutorialBack.classList.toggle("hidden", !hasMultiplePages || safeIndex === 0);
  }
  if (tutorialNext) {
    tutorialNext.textContent = t("common.next", "Next");
    tutorialNext.classList.toggle("hidden", !hasMultiplePages || isLastPage);
  }
  if (tutorialOk) {
    tutorialOk.textContent = hasMultiplePages ? t("common.done", "Done") : t("common.ok", "OK");
    tutorialOk.classList.toggle("hidden", hasMultiplePages && !isLastPage);
  }
}

function openTutorialPopup(screenKey = state.step) {
  if (!tutorialModal) {
    return;
  }
  activeInformationAction = null;
  const safeKey = normalizeTutorialScreenKey(screenKey);
  const tutorialStep = tutorialScreens.find((entry) => entry.id === safeKey);
  activeTutorialPages = buildTutorialPages(tutorialStep);
  activeTutorialPageIndex = 0;
  renderActiveTutorialPage();
  tutorialModal.classList.remove("hidden");
  window.requestAnimationFrame(() => {
    const focusTarget = activeTutorialPages.length > 1 ? tutorialNext : tutorialOk;
    if (focusTarget) {
      focusTarget.focus();
    }
  });
}

function closeTutorialPopup() {
  if (tutorialModal) {
    tutorialModal.classList.add("hidden");
  }
  activeTutorialPages = [];
  activeTutorialPageIndex = 0;
  activeInformationAction = null;
}

function openInformationPopup(title, message, actionLabel = "", action = null) {
  if (!tutorialModal) {
    return;
  }
  activeInformationAction = typeof action === "function" ? action : null;
  activeTutorialPages = [{
    titleText: title,
    paragraphs: [{ text: message }],
  }];
  activeTutorialPageIndex = 0;
  renderActiveTutorialPage();
  if (tutorialOk && actionLabel) {
    tutorialOk.textContent = actionLabel;
  }
  tutorialModal.classList.remove("hidden");
  window.requestAnimationFrame(() => {
    if (tutorialOk) {
      tutorialOk.focus();
    }
  });
}

function openRulesetLimitPopup(rulesetListVisible, revealRulesets) {
  const listVisible = Boolean(rulesetListVisible);
  openInformationPopup(
    t("web.home.ruleset_limit_title", "Ruleset Limit"),
    t(
      "web.home.ruleset_limit_message",
      "Closed beta accounts can store up to two rulesets online. To start or import another, download any ruleset you want to keep, then delete that online save."
    ),
    listVisible
      ? t("common.close", "Close")
      : t("web.home.manage_saved_rulesets", "Manage saved rulesets"),
    listVisible ? null : revealRulesets
  );
}

function openCharacterRulesetRequiredPopup() {
  openInformationPopup(
    t("web.home.character_ruleset_required_title", "Ruleset Required"),
    t(
      "web.home.character_ruleset_required_message",
      "Ruleset required for character creation, please upload or create a ruleset."
    )
  );
}

function renderSidebar() {
  if (!sidebarNav || !sidebarTitle) {
    return;
  }
  const sidebarVisible = state.mode === "builder"
    && Boolean(state.draftId)
    && state.step !== "splash"
    && state.setupComplete;
  if (sidebar) {
    sidebar.classList.toggle("hidden", !sidebarVisible);
  }
  document.body.classList.toggle("sidebar-hidden", !sidebarVisible);
  if (!sidebarVisible) {
    sidebarNav.innerHTML = "";
    return;
  }
  sidebarTitle.textContent = t("common.stages", "Stages");
  sidebarNav.innerHTML = steps
    .map((entry) => {
      const isActive = entry.id === state.step;
      const activeClass = isActive ? " active" : "";
      const disabled = isActive ? "disabled" : "";
      const systemNameKey = String(entry.systemNameKey || entry.id || "").trim().toLowerCase();
      const customLabel = systemNameKey ? String(state.systemNames[systemNameKey] || "").trim() : "";
      const rawLabel = customLabel || t(entry.labelKey, entry.fallback);
      const label = escapeHtml(rawLabel);
      const infoLabel = t("web.tutorial.info_button", "Info");
      return `
        <div class="sidebar-row">
          <button class="btn ghost sidebar-link${activeClass}" type="button" data-step="${entry.id}" ${disabled}>
            ${label}
          </button>
          <button
            class="btn ghost sidebar-info"
            type="button"
            data-tutorial-screen="${entry.id}"
            aria-label="${escapeHtml(`${infoLabel}: ${rawLabel}`)}"
          >
            ${escapeHtml(infoLabel)}
          </button>
        </div>
      `;
    })
    .join("");
}

function navigateToStep(stepId) {
  const route = stepRoutes[stepId];
  if (!route) {
    return;
  }
  if (state.mode !== "builder") {
    setMode("builder");
  }
  route();
}

function resolveHistoryStep(event) {
  const stateStep = event && event.state ? String(event.state.step || "").trim() : "";
  if (stateStep) {
    return stateStep;
  }
  const hash = String(window.location.hash || "");
  if (hash.startsWith("#/")) {
    return decodeURIComponent(hash.slice(2));
  }
  if (hash.startsWith("#")) {
    return decodeURIComponent(hash.slice(1));
  }
  return "";
}

function navigateToHistoryStep(stepId) {
  const safeStep = String(stepId || "").trim();
  if (!safeStep) {
    return;
  }
  if (stepRoutes[safeStep]) {
    if (!state.draftId) {
      renderBuilderSplash();
      return;
    }
    if (state.mode !== "builder") {
      setMode("builder");
    }
    stepRoutes[safeStep]();
    return;
  }
  const route = historyRoutes[safeStep];
  if (route) {
    route();
  }
}

function markSaved(message) {
  const stamp = new Date().toLocaleTimeString();
  const savedLabel = t("web.save.saved", "Saved");
  saveStatus.textContent = message ? `${message} - ${stamp}` : `${savedLabel} ${stamp}`;
}

function updateActions() {
  if (!downloadBtn) {
    return;
  }
  if (homeBtn) {
    homeBtn.style.display = state.sessionToken && state.mode !== "home" ? "" : "none";
  }
  if (tutorialInfoBtn) {
    tutorialInfoBtn.style.display = state.sessionToken ? "" : "none";
  }
  if (feedbackBtn) {
    feedbackBtn.style.display = state.sessionToken ? "" : "none";
  }
  if (adminBtn) {
    adminBtn.style.display = state.sessionToken && state.admin ? "" : "none";
  }
  const showDownload = state.mode === "builder" || state.mode === "chargen";
  downloadBtn.style.display = showDownload ? "" : "none";
  downloadBtn.disabled = state.mode === "builder" ? !state.draftId : false;
  if (state.mode === "chargen") {
    downloadBtn.textContent = t("web.download.character", "Download .gmcf");
  } else {
    downloadBtn.textContent = t("web.download", "Download .gmrf");
  }
  if (state.mode !== "builder") {
    saveStatus.textContent = "";
  }
}

function ensureDraft() {
  if (!state.draftId) {
    showToast(t("web.toast.need_draft", "Start or import a draft first."));
    return false;
  }
  return true;
}

function showConfirm(message, okLabel, cancelLabel = "", title = "", danger = true, showCancel = true) {
  confirmTitle.textContent = title || t("web.confirm.title", "Confirm");
  confirmMessage.textContent = message;
  confirmCancel.textContent = cancelLabel || t("common.cancel", "Cancel");
  confirmCancel.classList.toggle("hidden", !showCancel);
  confirmOk.textContent = okLabel || t("common.remove", "Remove");
  confirmOk.classList.toggle("danger", danger);
  confirmModal.classList.remove("hidden");
  return new Promise((resolve) => {
    confirmResolve = resolve;
  });
}

function openDeleteAccountModal(username) {
  deleteAccountUsername.value = username || "";
  deleteAccountPassword.value = "";
  deleteAccountModal.classList.remove("hidden");
  window.requestAnimationFrame(() => {
    if (deleteAccountUsername.value) {
      deleteAccountPassword.focus();
      return;
    }
    deleteAccountUsername.focus();
  });
}

function closeDeleteAccountModal() {
  deleteAccountModal.classList.add("hidden");
  deleteAccountPassword.value = "";
}

function defaultFeedbackSeverity(type) {
  const safeType = String(type || "").trim().toLowerCase();
  if (safeType === "blocker") {
    return "high";
  }
  if (safeType === "bug") {
    return "medium";
  }
  return "low";
}

function updateFeedbackStepsVisibility() {
  if (!feedbackType || !feedbackStepsField) {
    return;
  }
  const type = String(feedbackType.value || "").trim().toLowerCase();
  feedbackStepsField.classList.toggle("hidden", type === "feedback");
}

function openFeedbackModal(defaultType = "feedback") {
  if (!state.sessionToken) {
    showToast(t("web.feedback.login_required", "Log in before submitting a report."));
    return;
  }
  const safeType = ["feedback", "bug", "blocker"].includes(defaultType) ? defaultType : "feedback";
  feedbackType.value = safeType;
  feedbackSeverity.value = defaultFeedbackSeverity(safeType);
  feedbackShortTitle.value = "";
  feedbackMessage.value = "";
  feedbackSteps.value = "";
  feedbackStatus.textContent = "";
  feedbackSubmit.disabled = false;
  updateFeedbackStepsVisibility();
  feedbackModal.classList.remove("hidden");
  window.requestAnimationFrame(() => feedbackShortTitle.focus());
}

function closeFeedbackModal() {
  feedbackModal.classList.add("hidden");
  feedbackStatus.textContent = "";
}

function openLockedAccountModal(email) {
  lockedAccountEmail = String(email || "").trim();
  lockedAccountTitle.textContent = t("web.login.locked_password_title", "Locked After Failed Password Attempts");
  lockedAccountMessage.textContent = t(
    "web.login.locked_password_message",
    "This account was locked after repeated password failures. Use Forgot Password to verify by email and choose a new password."
  );
  lockedAccountDetails.value = "";
  const detailsField = lockedAccountDetails.closest(".field");
  if (detailsField) {
    detailsField.classList.add("hidden");
  }
  lockedAccountSubmit.textContent = t("web.login.forgot_password", "Forgot Password?");
  lockedAccountStatus.textContent = "";
  lockedAccountSubmit.disabled = false;
  lockedAccountCancel.disabled = false;
  lockedAccountModal.classList.remove("hidden");
  window.requestAnimationFrame(() => lockedAccountSubmit.focus());
}

function closeLockedAccountModal() {
  lockedAccountModal.classList.add("hidden");
  lockedAccountStatus.textContent = "";
}

async function requestPasswordResetEmail(email, button) {
  const safeEmail = String(email || "").trim();
  if (!safeEmail) {
    showToast(t("web.login.email_required", "Email is required."));
    return false;
  }
  if (button) {
    button.disabled = true;
  }
  try {
    await api("POST", "/api/accounts/password-reset", { email: safeEmail });
    showToast(t("web.login.reset_email_sent", "If this email has a GMRules Closed Beta account, a password reset link has been sent."));
    return true;
  } catch (error) {
    showToast(error.message);
    return false;
  } finally {
    if (button) {
      button.disabled = false;
    }
  }
}

function currentRouteForFeedback() {
  const path = String(window.location.pathname || "");
  const search = String(window.location.search || "");
  const hash = String(window.location.hash || "");
  return `${path}${search}${hash}`;
}

function buildFeedbackPayload() {
  return {
    type: feedbackType.value,
    severity: feedbackSeverity.value,
    title: feedbackShortTitle.value,
    message: feedbackMessage.value,
    steps: feedbackStepsField.classList.contains("hidden") ? "" : feedbackSteps.value,
    route: currentRouteForFeedback(),
    page: state.mode,
    stage: state.mode === "builder" ? state.step : "",
    draftId: state.draftId,
    userAgent: String(navigator.userAgent || ""),
    clientTimestamp: new Date().toISOString(),
  };
}

async function submitFeedbackReport() {
  const title = feedbackShortTitle.value.trim();
  const message = feedbackMessage.value.trim();
  if (!title || !message) {
    feedbackStatus.textContent = t("web.feedback.required", "Add a short title and details before submitting.");
    return;
  }
  feedbackSubmit.disabled = true;
  feedbackCancel.disabled = true;
  feedbackStatus.textContent = t("web.feedback.submitting", "Submitting...");
  try {
    await api("POST", "/api/feedback", buildFeedbackPayload());
    closeFeedbackModal();
    showToast(t("web.feedback.submitted", "Report submitted. Thank you."));
  } catch (error) {
    feedbackStatus.textContent = error.message;
    feedbackSubmit.disabled = false;
  } finally {
    feedbackCancel.disabled = false;
  }
}

async function submitLockedAccountReport() {
  const email = String(lockedAccountEmail || "").trim();
  if (!email) {
    lockedAccountStatus.textContent = t("web.login.email_required", "Email is required.");
    return;
  }
  lockedAccountCancel.disabled = true;
  lockedAccountStatus.textContent = t("web.login.reset_sending", "Sending password reset email...");
  const sent = await requestPasswordResetEmail(email, lockedAccountSubmit);
  if (sent) {
    closeLockedAccountModal();
    return;
  }
  lockedAccountStatus.textContent = "";
  lockedAccountCancel.disabled = false;
}

async function submitDeleteAccount() {
  try {
    const result = await api("DELETE", "/api/accounts", {
      username: deleteAccountUsername.value,
      password: deleteAccountPassword.value,
    });
    closeDeleteAccountModal();
    state.sessionToken = "";
    clearStoredSessionToken();
    state.accountName = "";
    state.legacyGuest = false;
    state.admin = false;
    state.draftId = "";
    state.systemNames = {};
    setLoggedIn(false);
    renderClosedBetaApplication();
    showToast(t(
      "web.account_delete.deleted",
      "Account deleted. Removed {count} saved rulesets."
    ).replace("{count}", String(result.deletedDrafts || 0)));
  } catch (error) {
    showToast(error.message);
  }
}

confirmCancel.addEventListener("click", () => {
  confirmModal.classList.add("hidden");
  if (confirmResolve) {
    confirmResolve(false);
    confirmResolve = null;
  }
});

confirmOk.addEventListener("click", () => {
  confirmModal.classList.add("hidden");
  if (confirmResolve) {
    confirmResolve(true);
    confirmResolve = null;
  }
});

if (tutorialOk) {
  tutorialOk.addEventListener("click", () => {
    const informationAction = activeInformationAction;
    closeTutorialPopup();
    if (informationAction) {
      informationAction();
    }
  });
}
if (tutorialBack) {
  tutorialBack.addEventListener("click", () => {
    if (activeTutorialPageIndex <= 0) {
      return;
    }
    activeTutorialPageIndex -= 1;
    renderActiveTutorialPage();
    window.requestAnimationFrame(() => tutorialNext?.focus());
  });
}
if (tutorialNext) {
  tutorialNext.addEventListener("click", () => {
    if (activeTutorialPageIndex >= activeTutorialPages.length - 1) {
      return;
    }
    activeTutorialPageIndex += 1;
    renderActiveTutorialPage();
    window.requestAnimationFrame(() => {
      const focusTarget = activeTutorialPageIndex >= activeTutorialPages.length - 1
        ? tutorialOk
        : tutorialNext;
      focusTarget?.focus();
    });
  });
}

deleteAccountCancel.addEventListener("click", closeDeleteAccountModal);
deleteAccountOk.addEventListener("click", submitDeleteAccount);
deleteAccountPassword.addEventListener("keypress", (event) => {
  if (event.key === "Enter") {
    submitDeleteAccount();
  }
});

if (feedbackBtn) {
  feedbackBtn.addEventListener("click", () => openFeedbackModal("feedback"));
}
if (adminBtn) {
  adminBtn.addEventListener("click", renderAdmin);
}
if (homeBtn) {
  homeBtn.addEventListener("click", renderHome);
}
if (tutorialInfoBtn) {
  tutorialInfoBtn.addEventListener("click", () => openTutorialPopup());
}
if (fontSizeSlider) {
  fontSizeSlider.addEventListener("input", () => {
    const level = clampFontSizeLevel(fontSizeSlider.value);
    if (state.fontSizeAllScreens) {
      state.fontSizeGlobalLevel = level;
    } else {
      state.fontSizeLevelsByScreen[currentFontSizeScreenKey()] = level;
    }
    applyFontSizePreference();
  });
}
if (fontSizeAllScreens) {
  fontSizeAllScreens.addEventListener("change", () => {
    const level = clampFontSizeLevel(fontSizeSlider ? fontSizeSlider.value : currentFontSizeLevel());
    state.fontSizeAllScreens = fontSizeAllScreens.checked;
    if (state.fontSizeAllScreens) {
      state.fontSizeGlobalLevel = level;
    } else {
      state.fontSizeLevelsByScreen[currentFontSizeScreenKey()] = level;
    }
    applyFontSizePreference();
  });
}

feedbackCancel.addEventListener("click", closeFeedbackModal);
feedbackSubmit.addEventListener("click", submitFeedbackReport);
feedbackType.addEventListener("change", () => {
  feedbackSeverity.value = defaultFeedbackSeverity(feedbackType.value);
  updateFeedbackStepsVisibility();
});

lockedAccountCancel.addEventListener("click", closeLockedAccountModal);
lockedAccountSubmit.addEventListener("click", submitLockedAccountReport);

typeCancel.addEventListener("click", () => {
  typeModal.classList.add("hidden");
  pendingTypeUpdate = null;
});

typeOk.addEventListener("click", async () => {
  if (!pendingTypeUpdate) {
    typeModal.classList.add("hidden");
    return;
  }
  const { attributeId } = pendingTypeUpdate;
  const typeKey = typeSelector.value;
  try {
    await api("POST", `/api/drafts/${state.draftId}/attributes/type`, {
      id: attributeId,
      typeKey,
    });
    markSaved(t("web.toast.type_updated", "Category updated"));
    state.lastAttributeTypeKey = typeKey;
    typeModal.classList.add("hidden");
    pendingTypeUpdate = null;
    renderAttributes();
  } catch (error) {
    showToast(error.message);
  }
});

function closeEditModal() {
  if (restoreSuspendedEdit()) {
    return;
  }
  const returnTo = editReturnTo;
  editModal.classList.add("hidden");
  editContext = null;
  editReturnTo = "";
  editModifiers = [];
  editBonuses = [];
  editModifierIndex = -1;
  editBonusIndex = -1;
  resetEffectTypeSection();
  resetWeightSection();
  resetClassEditSection();
  resetWeaponEditSection();
  resetRaceEditSection();
  resetEffectTypeSection();
  resetWeightSection();
  if (returnTo === "skill" && skillModal) {
    skillModal.classList.remove("hidden");
  }
  if (returnTo === "spell" && spellModal) {
    spellModal.classList.remove("hidden");
  }
}

function suspendModalForEdit(origin) {
  if (origin === "skill" && skillModal && !skillModal.classList.contains("hidden")) {
    skillModal.classList.add("hidden");
    return "skill";
  }
  if (origin === "spell" && spellModal && !spellModal.classList.contains("hidden")) {
    spellModal.classList.add("hidden");
    return "spell";
  }
  return "";
}

function resetClassEditSection() {
  editClassSkillIds = [];
  editClassRequiredScores = [];
  editClassSkillPointsByLevel = [];
  editClassSkillIndex = -1;
  editClassRequiredIndex = -1;
  editClassSkillPointsIndex = -1;
  setCollectionCommitMode(editClassSkillAdd, false);
  setCollectionCommitMode(editClassRequiredAdd, false);
  setCollectionCommitMode(editClassSkillPointsAdd, false);
  if (editClassSection) {
    editClassSection.classList.add("hidden");
  }
  if (editClassPrimary) {
    editClassPrimary.innerHTML = "";
  }
  if (editClassHitDieSelect) {
    editClassHitDieSelect.innerHTML = "";
  }
  if (editClassHitDieModifier) {
    editClassHitDieModifier.value = "0";
  }
  if (editClassSkillPoints) {
    editClassSkillPoints.value = "";
  }
  if (editClassStartingMoney) {
    editClassStartingMoney.value = "0";
  }
  if (editClassSkillPointsSame) {
    editClassSkillPointsSame.checked = true;
  }
  if (editClassSkillPointsLevel) {
    editClassSkillPointsLevel.innerHTML = "";
  }
  if (editClassSkillPointsValue) {
    editClassSkillPointsValue.value = "0";
  }
  if (editClassSkillPointsList) {
    editClassSkillPointsList.innerHTML = "";
  }
  if (editClassSkillSelect) {
    editClassSkillSelect.innerHTML = "";
  }
  if (editClassSkillList) {
    editClassSkillList.innerHTML = "";
  }
  if (editClassRequiredSelect) {
    editClassRequiredSelect.innerHTML = "";
  }
  if (editClassRequiredScore) {
    editClassRequiredScore.value = "1";
  }
  if (editClassRequiredList) {
    editClassRequiredList.innerHTML = "";
  }
}

function resetWeaponEditSection() {
  editWeaponEffectIds = [];
  editWeaponEffectIndex = -1;
  setCollectionCommitMode(editWeaponEffectAdd, false);
  if (editWeaponDamageField) {
    editWeaponDamageField.classList.add("hidden");
  }
  if (editWeaponDamageCount) {
    editWeaponDamageCount.value = "0";
  }
  if (editWeaponDamageSides) {
    editWeaponDamageSides.value = "0";
  }
  if (editWeaponDamageModifier) {
    editWeaponDamageModifier.value = "0";
  }
  if (editWeaponEffectsSection) {
    editWeaponEffectsSection.classList.add("hidden");
  }
  if (editWeaponEffectSelect) {
    editWeaponEffectSelect.innerHTML = "";
  }
  if (editWeaponEffectList) {
    editWeaponEffectList.innerHTML = "";
  }
}

function snapshotWeaponEdit() {
  return {
    id: editContext ? editContext.id : "",
    name: String(editName.value || ""),
    description: String(editDescription.value || ""),
    damageDiceCount: editWeaponDamageCount ? Number(editWeaponDamageCount.value || 0) : 0,
    damageDiceSides: editWeaponDamageSides ? Number(editWeaponDamageSides.value || 0) : 0,
    damageDiceModifier: editWeaponDamageModifier ? Number(editWeaponDamageModifier.value || 0) : 0,
    weightValue: editWeightValue ? Number(editWeightValue.value || 0) : 0,
    weightUnit: editWeightUnit ? String(editWeightUnit.value || "").trim() : "",
    damageTypeId: editDamageType ? String(editDamageType.value || "").trim() : "",
    effectIds: editWeaponEffectIds.slice(),
  };
}

function resetRaceEditSection() {
  editRaceSkillIds = [];
  editRaceAttributeLimits = [];
  editRaceTraitIndex = -1;
  editRaceAttributeIndex = -1;
  setCollectionCommitMode(editRaceTraitAdd, false);
  setCollectionCommitMode(editRaceAttributeAdd, false);
  if (editRaceSection) {
    editRaceSection.classList.add("hidden");
  }
  if (editRaceTraitSelect) {
    editRaceTraitSelect.innerHTML = "";
  }
  if (editRaceTraitList) {
    editRaceTraitList.innerHTML = "";
  }
  if (editRaceAttributeSelect) {
    editRaceAttributeSelect.innerHTML = "";
  }
  if (editRaceAttributeList) {
    editRaceAttributeList.innerHTML = "";
  }
  if (editRaceAttributeMin) {
    editRaceAttributeMin.value = "0";
  }
  if (editRaceAttributeMax) {
    editRaceAttributeMax.value = "0";
  }
  if (editRaceStartingMoneyModifier) {
    editRaceStartingMoneyModifier.value = "0";
  }
}

function openAttributeTypeEditor(type) {
  if (!type) {
    return;
  }
  resetClassEditSection();
  resetWeaponEditSection();
  resetRaceEditSection();
  resetEffectTypeSection();
  resetWeightSection();
  resetWeightSection();
  resetWeightSection();
  if (editType) {
    editType.multiple = false;
    editType.size = 1;
  }
  editContext = { kind: "attribute-type", key: type.key };
  editTitle.textContent = t("attrtypes.edit.title", "Edit Attribute Category");
  editName.value = type.name || "";
  editDescription.value = type.description || "";
  editTypeField.classList.add("hidden");
  editRangeFields.classList.add("hidden");
  editModifierSection.classList.add("hidden");
  editBonusSection.classList.add("hidden");
  editModal.classList.remove("hidden");
}

function openAttributeTypeCreate() {
  resetClassEditSection();
  resetWeaponEditSection();
  resetRaceEditSection();
  resetEffectTypeSection();
  resetWeightSection();
  if (editType) {
    editType.multiple = false;
    editType.size = 1;
  }
  editContext = { kind: "attribute-type-create" };
  editTitle.textContent = t("attrtypes.edit.title", "Edit Attribute Category");
  editName.value = "";
  editDescription.value = "";
  editTypeField.classList.add("hidden");
  editRangeFields.classList.add("hidden");
  editModifierSection.classList.add("hidden");
  editBonusSection.classList.add("hidden");
  editModal.classList.remove("hidden");
}

function openEffectTypeEditor(type) {
  if (!type) {
    return;
  }
  resetClassEditSection();
  resetWeaponEditSection();
  resetRaceEditSection();
  resetEffectTypeSection();
  resetWeightSection();
  resetWeightSection();
  resetWeightSection();
  if (editType) {
    editType.multiple = false;
    editType.size = 1;
  }
  editContext = { kind: "effect-type", key: type.key };
  editTitle.textContent = t("effecttypes.edit.title", "Edit Affected System");
  editName.value = type.name || "";
  editDescription.value = type.description || "";
  editTypeField.classList.add("hidden");
  editRangeFields.classList.add("hidden");
  editModifierSection.classList.add("hidden");
  editBonusSection.classList.add("hidden");
  editModal.classList.remove("hidden");
}

async function openStatusEditor(status) {
  if (!status) {
    return;
  }
  resetClassEditSection();
  resetWeaponEditSection();
  resetRaceEditSection();
  resetEffectTypeSection();
  editContext = { kind: "status", id: status.id };
  editTitle.textContent = t("statuses.edit.title", "Edit Status");
  editName.value = status.name || "";
  editDescription.value = status.description || "";
  editTypeLabel.textContent = t("effects.type", "Affected Systems");
  editTypeField.classList.remove("hidden");
  editRangeFields.classList.add("hidden");
  editModifierSection.classList.add("hidden");
  editBonusSection.classList.add("hidden");
  try {
    await ensureEffectTypeOptions();
  } catch (error) {
    showToast(error.message);
  }
  setEditTypeListMode("add-list");
  editEffectTypeKeys = Array.isArray(status.effectTypeKeys) ? status.effectTypeKeys.slice() : [];
  applyEffectTypeOptions(editType, "");
  renderEditEffectTypeList();
  editModal.classList.remove("hidden");
}

async function openStatusCreate(prefill = null) {
  resetClassEditSection();
  resetWeaponEditSection();
  resetRaceEditSection();
  resetEffectTypeSection();
  editContext = { kind: "status-create" };
  editTitle.textContent = t("statuses.edit.title", "Edit Status");
  editName.value = prefill ? String(prefill.name || "") : "";
  editDescription.value = prefill ? String(prefill.description || "") : "";
  editTypeLabel.textContent = t("effects.type", "Affected Systems");
  editTypeField.classList.remove("hidden");
  editRangeFields.classList.add("hidden");
  editModifierSection.classList.add("hidden");
  editBonusSection.classList.add("hidden");
  try {
    await ensureEffectTypeOptions();
  } catch (error) {
    showToast(error.message);
  }
  setEditTypeListMode("add-list");
  editEffectTypeKeys = prefill && Array.isArray(prefill.effectTypeKeys)
    ? prefill.effectTypeKeys.slice()
    : Array.isArray(state.lastStatusEffectTypeKeys)
      ? state.lastStatusEffectTypeKeys.slice()
      : [];
  applyEffectTypeOptions(editType, "");
  renderEditEffectTypeList();
  editModal.classList.remove("hidden");
}

function openAttributeCreate(types) {
  resetClassEditSection();
  resetWeaponEditSection();
  resetRaceEditSection();
  resetEffectTypeSection();
  if (editType) {
    editType.multiple = false;
    editType.size = 1;
  }
  editContext = { kind: "attribute-create" };
  editTitle.textContent = t("attributes.edit.title", "Edit Attribute");
  editName.value = "";
  editDescription.value = "";
  editTypeLabel.textContent = t("attributes.edit.type", "Attribute Category");
  editTypeField.classList.remove("hidden");
  editRangeFields.classList.toggle("hidden", usesDefaultAttributeScoreRange());
  editModifierSection.classList.toggle("hidden", usesDefaultAttributeModifiers());
  editBonusSection.classList.remove("hidden");

  const options = [`<option value="">${t("attrtypes.none", "None")}</option>`]
    .concat(
      (types || []).map(
        (type) => `<option value="${type.key}">${escapeHtml(type.displayName || type.key)}</option>`
      )
    )
    .join("");
  editType.innerHTML = options;
  const lastType = String(state.lastAttributeTypeKey || "");
  if (lastType && Array.from(editType.options || []).some((option) => option.value === lastType)) {
    editType.value = lastType;
  } else {
    editType.value = "";
  }

  editMinValue.value = 0;
  editMaxValue.value = 0;
  editModifiers = usesDefaultAttributeModifiers() ? getStandardAttributeModifiers() : [];
  editBonuses = [];
  editModifierIndex = -1;
  editBonusIndex = -1;
  setCollectionCommitMode(editModifierAdd, false);
  setCollectionCommitMode(editBonusAdd, false);
  if (editModifierApplyAll) {
    editModifierApplyAll.checked = false;
  }
  renderEditModifiers();
  populateAttributeBonusEffectSelect();
  renderEditBonuses();
  editModal.classList.remove("hidden");
}

function openAttributeEditor(attribute, types) {
  if (!attribute) {
    return;
  }
  resetClassEditSection();
  resetWeaponEditSection();
  resetRaceEditSection();
  resetEffectTypeSection();
  if (editType) {
    editType.multiple = false;
    editType.size = 1;
  }
  editContext = { kind: "attribute", id: attribute.id };
  editTitle.textContent = t("attributes.edit.title", "Edit Attribute");
  editName.value = attribute.name || "";
  editDescription.value = attribute.description || "";
  editTypeLabel.textContent = t("attributes.edit.type", "Attribute Category");
  editTypeField.classList.remove("hidden");
  editRangeFields.classList.toggle("hidden", usesDefaultAttributeScoreRange());
  editModifierSection.classList.toggle("hidden", usesDefaultAttributeModifiers());
  editBonusSection.classList.remove("hidden");

  const options = [`<option value="">${t("attrtypes.none", "None")}</option>`]
    .concat(
      (types || []).map(
        (type) => `<option value="${type.key}">${escapeHtml(type.displayName || type.key)}</option>`
      )
    )
    .join("");
  editType.innerHTML = options;
  editType.value = attribute.typeKey || "";

  editMinValue.value = Number(attribute.minValue || 0);
  editMaxValue.value = Number(attribute.maxValue || 0);
  editModifiers = usesDefaultAttributeModifiers()
    ? getStandardAttributeModifiers()
    : normalizeModifierEntries(attribute.modifiers);
  editBonuses = Array.isArray(attribute.scoreBonuses)
    ? attribute.scoreBonuses.map((entry) => ({
      threshold: Number(entry.threshold || 0),
      effectId: String(entry.effectId || entry.effect || ""),
    })).filter((entry) => entry.effectId)
    : [];
  editModifierIndex = -1;
  editBonusIndex = -1;
  setCollectionCommitMode(editModifierAdd, false);
  setCollectionCommitMode(editBonusAdd, false);
  if (editModifierApplyAll) {
    editModifierApplyAll.checked = false;
  }
  renderEditModifiers();
  populateAttributeBonusEffectSelect();
  renderEditBonuses();
  editModal.classList.remove("hidden");
}

function openDamageTypeEditor(damageType) {
  if (!damageType) {
    return;
  }
  resetClassEditSection();
  resetWeaponEditSection();
  resetRaceEditSection();
  resetEffectTypeSection();
  resetWeightSection();
  editContext = { kind: "damage-type", id: damageType.id };
  editTitle.textContent = t("damagetypes.edit.title", "Edit Damage Type");
  editName.value = damageType.name || "";
  editDescription.value = damageType.description || "";
  editTypeField.classList.add("hidden");
  editRangeFields.classList.add("hidden");
  editModifierSection.classList.add("hidden");
  editBonusSection.classList.add("hidden");
  editModal.classList.remove("hidden");
}

function openDamageTypeCreate() {
  resetClassEditSection();
  resetWeaponEditSection();
  resetRaceEditSection();
  resetEffectTypeSection();
  resetWeightSection();
  editContext = { kind: "damage-type-create" };
  editTitle.textContent = t("damagetypes.edit.title", "Edit Damage Type");
  editName.value = "";
  editDescription.value = "";
  editTypeField.classList.add("hidden");
  editRangeFields.classList.add("hidden");
  editModifierSection.classList.add("hidden");
  editBonusSection.classList.add("hidden");
  editModal.classList.remove("hidden");
}

async function openEffectEditor(effect) {
  if (!effect) {
    return;
  }
  resetClassEditSection();
  resetWeaponEditSection();
  resetRaceEditSection();
  resetEffectTypeSection();
  resetWeightSection();
  editContext = { kind: "effect", id: effect.id };
  editTitle.textContent = t("effects.edit.title", "Edit Effect");
  editName.value = effect.name || "";
  editDescription.value = effect.description || "";
  editTypeLabel.textContent = t("effects.type", "Affected Systems");
  editTypeField.classList.remove("hidden");
  editRangeFields.classList.add("hidden");
  editModifierSection.classList.add("hidden");
  editBonusSection.classList.add("hidden");
  try {
    await ensureDamageTypeOptions();
  } catch (error) {
    showToast(error.message);
  }
  showDamageTypeField(effect.damageTypeId || "");
  try {
    await ensureEffectTypeOptions();
  } catch (error) {
    showToast(error.message);
  }
  setEditTypeListMode("add-list");
  editEffectTypeKeys = Array.isArray(effect.effectTypeKeys) ? effect.effectTypeKeys.slice() : [];
  applyEffectTypeOptions(editType, "");
  renderEditEffectTypeList();
  editModal.classList.remove("hidden");
}

async function openEffectCreateModal(origin, prefillName, prefillDescription, prefillTypes) {
  const safeOrigin = String(origin || "");
  if (editContext && editContext.kind === "weapon") {
    editSuspend = {
      kind: "weapon",
      weapon: snapshotWeaponEdit(),
      parent: editSuspend,
    };
  } else if (editContext && ["attribute", "attribute-create"].includes(editContext.kind)) {
    editSuspend = {
      kind: "attribute",
      attribute: snapshotAttributeEdit(),
      parent: editSuspend,
    };
  }
  editReturnTo = suspendModalForEdit(safeOrigin);
  resetClassEditSection();
  resetWeaponEditSection();
  resetRaceEditSection();
  resetEffectTypeSection();
  resetWeightSection();
  editContext = { kind: "effect-create", origin: safeOrigin };
  editTitle.textContent = t("skills.effects.create", "Create Effect");
  editName.value = String(prefillName || "");
  editDescription.value = String(prefillDescription || "");
  editTypeLabel.textContent = t("effects.type", "Affected Systems");
  editTypeField.classList.remove("hidden");
  editRangeFields.classList.add("hidden");
  editModifierSection.classList.add("hidden");
  editBonusSection.classList.add("hidden");
  try {
    await ensureDamageTypeOptions();
  } catch (error) {
    showToast(error.message);
  }
  showDamageTypeField("");
  try {
    await ensureEffectTypeOptions();
  } catch (error) {
    showToast(error.message);
  }
  setEditTypeListMode("add-list");
  editEffectTypeKeys = Array.isArray(prefillTypes) ? prefillTypes.slice() : [];
  applyEffectTypeOptions(editType, "");
  renderEditEffectTypeList();
  editModal.classList.remove("hidden");
}

function openSkillCategoryCreateModal(origin) {
  const safeOrigin = String(origin || "");
  editReturnTo = suspendModalForEdit(safeOrigin);
  resetClassEditSection();
  resetWeaponEditSection();
  resetRaceEditSection();
  resetEffectTypeSection();
  resetWeightSection();
  editContext = { kind: "skill-category-create", origin: safeOrigin };
  editTitle.textContent = t("skills.category.create", "Create Category");
  editName.value = "";
  editDescription.value = "";
  editTypeField.classList.add("hidden");
  editRangeFields.classList.add("hidden");
  editModifierSection.classList.add("hidden");
  editBonusSection.classList.add("hidden");
  editModal.classList.remove("hidden");
}

async function ensureEffectTypeOptions() {
  if (!ensureDraft()) {
    return [];
  }
  if (effectTypeOptions.length) {
    return effectTypeOptions;
  }
  const data = await api("GET", `/api/drafts/${state.draftId}/effect-types`);
  effectTypeOptions = sortByLabel(data.types || [], (type) => type.displayName || type.name || type.key || "");
  return effectTypeOptions;
}

function applyEffectTypeOptions(select, selectedValue) {
  if (!select) {
    return;
  }
  const selectedValues = Array.isArray(selectedValue)
    ? selectedValue.filter(Boolean).map((value) => String(value))
    : [String(selectedValue || "")].filter(Boolean);
  const selectedLower = selectedValues.map((value) => value.toLowerCase());
  const includeNone = !select.multiple;
  const options = (includeNone
    ? [
      `<option value="">${t("effects.type.none", "None")}</option>`,
      `<option value="${NEW_INLINE_OPTION}">${t("effecttypes.new", "New Affected System")}</option>`,
    ]
    : [])
    .concat(
      (effectTypeOptions || []).map((type) => {
        const label = escapeHtml(type.name || type.displayName || "");
        const value = escapeHtml(type.name || type.displayName || "");
        return `<option value="${value}">${label}</option>`;
      })
    )
    .join("");
  select.innerHTML = options;
  const selectOptions = Array.from(select.options || []);
  selectedValues.forEach((value) => {
    const lowerValue = value.toLowerCase();
    const match = (effectTypeOptions || []).find((type) => {
      return (
        String(type.name || "").toLowerCase() === lowerValue ||
        String(type.displayName || "").toLowerCase() === lowerValue ||
        String(type.key || "").toLowerCase() === lowerValue
      );
    });
    const resolved = match ? String(match.name || match.displayName || "") : value;
    if (!selectOptions.some((option) => option.value === resolved)) {
      const extra = document.createElement("option");
      extra.value = resolved;
      extra.textContent = resolveEffectTypeLabel(resolved);
      select.appendChild(extra);
      selectOptions.push(extra);
    }
  });
  if (select.multiple) {
    selectOptions.forEach((option) => {
      option.selected = selectedLower.includes(String(option.value || "").toLowerCase());
    });
    if (select.size < 2) {
      select.size = Math.min(6, selectOptions.length || 4);
    }
  } else {
    const safeValue = selectedValues[0] || "";
    select.value = safeValue;
  }
}

function getSelectValues(select) {
  if (!select) {
    return [];
  }
  if (!select.multiple) {
    const value = String(select.value || "").trim();
    return value ? [value] : [];
  }
  return Array.from(select.selectedOptions || [])
    .map((option) => String(option.value || "").trim())
    .filter(Boolean);
}

function normalizeModifierEntries(entries) {
  const safeEntries = Array.isArray(entries) ? entries : [];
  return safeEntries
    .map((entry) => ({
      score: Number(entry.score || 0),
      modifier: Number(entry.modifier || 0),
    }))
    .filter((entry) => Number.isFinite(entry.score) && Number.isFinite(entry.modifier))
    .sort((left, right) => left.score - right.score);
}

function getStandardAttributeModifiers() {
  return normalizeModifierEntries(state.attributeModifiers).map((entry) => ({ ...entry }));
}

function usesDefaultAttributeScoreRange() {
  return Number(state.defaultAttributeMinScore || 0) !== 0 || Number(state.defaultAttributeMaxScore || 0) !== 0;
}

function usesDefaultAttributeModifiers() {
  return usesDefaultAttributeScoreRange() && Boolean(state.applyAttributeModifiersToAllAttributes);
}

function setEditTypeListMode(mode) {
  if (!editType) {
    return;
  }
  const showList = mode === "add-list";
  if (editTypeAdd) {
    editTypeAdd.classList.toggle("hidden", !showList);
  }
  if (editTypeList) {
    editTypeList.classList.toggle("hidden", !showList);
    if (!showList) {
      editTypeList.innerHTML = "";
    }
  }
  if (mode === "multi") {
    editType.multiple = true;
  } else {
    editType.multiple = false;
    editType.size = 1;
  }
}

function resetEffectTypeSection() {
  editEffectTypeKeys = [];
  editEffectTypeIndex = -1;
  setCollectionCommitMode(editTypeAdd, false);
  setEditTypeListMode("single");
}

function restoreSuspendedEdit() {
  if (!editSuspend) {
    return false;
  }
  const suspended = editSuspend;
  editSuspend = suspended.parent || null;
  if (suspended.kind === "effect") {
    openEffectEditor(suspended.effect);
    return true;
  }
  if (suspended.kind === "effect-create") {
    const returnTo = editReturnTo;
    openEffectCreateModal(suspended.origin, suspended.name, suspended.description, suspended.effectTypeKeys);
    editReturnTo = editReturnTo || returnTo;
    return true;
  }
  if (suspended.kind === "status") {
    openStatusEditor(suspended.status);
    return true;
  }
  if (suspended.kind === "status-create") {
    openStatusCreate(suspended.status);
    return true;
  }
  if (suspended.kind === "weapon") {
    openWeaponEditor(suspended.weapon);
    return true;
  }
  if (suspended.kind === "attribute") {
    restoreAttributeEdit(suspended.attribute);
    return true;
  }
  return false;
}

function openEffectTypeCreateModal(origin) {
  const safeOrigin = String(origin || "");
  if (editContext && editContext.kind === "effect") {
    editSuspend = {
      kind: "effect",
      effect: {
        id: editContext.id,
        name: String(editName.value || ""),
        description: String(editDescription.value || ""),
        effectTypeKeys: editEffectTypeKeys.slice(),
      },
      parent: editSuspend,
    };
  } else if (editContext && editContext.kind === "effect-create") {
    editSuspend = {
      kind: "effect-create",
      origin: String(editContext.origin || ""),
      name: String(editName.value || ""),
      description: String(editDescription.value || ""),
      effectTypeKeys: editEffectTypeKeys.slice(),
      parent: editSuspend,
    };
  } else if (editContext && editContext.kind === "status") {
    editSuspend = {
      kind: "status",
      status: {
        id: editContext.id,
        name: String(editName.value || ""),
        description: String(editDescription.value || ""),
        effectTypeKeys: editEffectTypeKeys.slice(),
      },
      parent: editSuspend,
    };
  } else if (editContext && editContext.kind === "status-create") {
    editSuspend = {
      kind: "status-create",
      status: {
        name: String(editName.value || ""),
        description: String(editDescription.value || ""),
        effectTypeKeys: editEffectTypeKeys.slice(),
      },
      parent: editSuspend,
    };
  }
  editContext = { kind: "effect-type-create", origin: safeOrigin };
  editTitle.textContent = t("effecttypes.create.title", "Create Affected System");
  editName.value = "";
  editDescription.value = "";
  editTypeField.classList.add("hidden");
  editRangeFields.classList.add("hidden");
  editModifierSection.classList.add("hidden");
  editBonusSection.classList.add("hidden");
  resetEffectTypeSection();
  resetWeightSection();
  editModal.classList.remove("hidden");
}

function resetWeightSection() {
  if (editWeightGrid) {
    editWeightGrid.classList.add("hidden");
  }
  if (editWeightValue) {
    editWeightValue.value = "0";
  }
  if (editWeightUnit) {
    editWeightUnit.innerHTML = "";
  }
  if (editDamageTypeField) {
    editDamageTypeField.classList.add("hidden");
  }
  if (editDamageType) {
    editDamageType.innerHTML = "";
  }
}

async function ensureDamageTypeOptions() {
  if (!ensureDraft()) {
    return [];
  }
  if (damageTypeOptions.length) {
    return damageTypeOptions;
  }
  const data = await api("GET", `/api/drafts/${state.draftId}/damage-types`);
  damageTypeOptions = sortByLabel(data.damageTypes || [], (type) => type.displayName || type.name || "");
  return damageTypeOptions;
}

function renderWeightUnitOptions(select, selectedValue) {
  if (!select) {
    return;
  }
  const options = [`<option value="">${t("common.none", "None")}</option>`]
    .concat((weightUnitOptions || []).map((unit) => `<option value="${escapeHtml(unit)}">${escapeHtml(unit)}</option>`))
    .join("");
  select.innerHTML = options;
  const safeValue = String(selectedValue || "").trim();
  if (safeValue && !Array.from(select.options || []).some((option) => option.value === safeValue)) {
    const extra = document.createElement("option");
    extra.value = safeValue;
    extra.textContent = safeValue;
    select.appendChild(extra);
  }
  select.value = safeValue;
}

function showWeightSection(value, unit) {
  if (!editWeightGrid) {
    return;
  }
  editWeightGrid.classList.remove("hidden");
  if (editWeightValue) {
    const safeValue = Number.isFinite(Number(value)) ? Number(value) : 0;
    editWeightValue.value = String(Math.max(0, Math.trunc(safeValue)));
  }
  renderWeightUnitOptions(editWeightUnit, unit);
}

function renderDamageTypeOptions(select, selectedValue) {
  if (!select) {
    return;
  }
  const options = [`<option value="">${t("common.none", "None")}</option>`]
    .concat(
      (damageTypeOptions || []).map((type) => {
        const value = escapeHtml(type.id || "");
        const label = escapeHtml(type.displayName || type.name || "");
        return `<option value="${value}">${label}</option>`;
      })
    )
    .join("");
  select.innerHTML = options;
  const safeValue = String(selectedValue || "").trim();
  select.value = Array.from(select.options || []).some((option) => option.value === safeValue) ? safeValue : "";
}

function showDamageTypeField(selectedValue) {
  if (editDamageTypeField) {
    editDamageTypeField.classList.remove("hidden");
  }
  renderDamageTypeOptions(editDamageType, selectedValue);
}

function resolveDamageTypeLabel(damageTypeId) {
  const safeId = String(damageTypeId || "").trim();
  if (!safeId) {
    return "";
  }
  const match = (damageTypeOptions || []).find((type) => String(type.id || "") === safeId);
  return match ? match.displayName || match.name || safeId : safeId;
}

function resolveEffectTypeLabel(typeKey) {
  const safeKey = String(typeKey || "").trim();
  if (!safeKey) {
    return "";
  }
  let match = (effectTypeOptions || []).find((type) => String(type.name || type.key || "") === safeKey);
  if (!match) {
    const lowerValue = safeKey.toLowerCase();
    match = (effectTypeOptions || []).find((type) => {
      return (
        String(type.name || "").toLowerCase() === lowerValue ||
        String(type.displayName || "").toLowerCase() === lowerValue ||
        String(type.key || "").toLowerCase() === lowerValue
      );
    });
  }
  if (match) {
    return match.displayName || match.name || match.key || safeKey;
  }
  if (editType) {
    const optionMatch = Array.from(editType.options || []).find(
      (option) => String(option.value || "").toLowerCase() === safeKey.toLowerCase()
    );
    if (optionMatch) {
      const label = String(optionMatch.textContent || optionMatch.label || "").trim();
      if (label) {
        return label;
      }
    }
  }
  return safeKey;
}

function renderEffectTypeBadges(typeKeys) {
  return (Array.isArray(typeKeys) ? typeKeys : [])
    .map((key) => resolveEffectTypeLabel(key))
    .filter(Boolean)
    .map((label) => ` <span class="badge">${escapeHtml(label)}</span>`)
    .join("");
}

function renderEditEffectTypeList() {
  if (!editTypeList) {
    return;
  }
  if (!editEffectTypeKeys.length) {
    editTypeList.innerHTML = "";
    return;
  }
  editTypeList.innerHTML = editEffectTypeKeys
    .map(
      (key, index) => renderCollectionRow(
        `<span>${escapeHtml(resolveEffectTypeLabel(key))}</span>`,
        [
          collectionEditAction("edit-edit-type", index),
          collectionRemoveAction("remove-edit-type", index),
        ]
      )
    )
    .join("");
  editTypeList.querySelectorAll("[data-edit-edit-type], [data-remove-edit-type]").forEach((button) => {
    button.addEventListener("click", async () => {
      const index = Number(button.dataset.editEditType ?? button.dataset.removeEditType ?? -1);
      if (Number.isNaN(index) || index < 0 || index >= editEffectTypeKeys.length) {
        return;
      }
      if (button.hasAttribute("data-edit-edit-type")) {
        editEffectTypeIndex = index;
        editType.value = editEffectTypeKeys[index];
        setCollectionCommitMode(editTypeAdd, true);
        editType.focus();
        return;
      }
      const confirmed = await showConfirm(
        t("common.remove.confirm", "Remove selected item?"),
        t("common.remove", "Remove")
      );
      if (!confirmed) {
        return;
      }
      editEffectTypeKeys.splice(index, 1);
      if (editEffectTypeIndex === index) {
        editEffectTypeIndex = -1;
        editType.value = "";
        setCollectionCommitMode(editTypeAdd, false);
      }
      renderEditEffectTypeList();
    });
  });
}

if (editTypeAdd) {
  editTypeAdd.addEventListener("click", () => {
    if (!editType || !editTypeList || editTypeList.classList.contains("hidden")) {
      return;
    }
    const value = String(editType.value || "").trim();
    if (!value || value === NEW_INLINE_OPTION) {
      return;
    }
    if (editContext && (editContext.kind === "status" || editContext.kind === "status-create")) {
      const exists = editEffectTypeKeys.some((key, index) => {
        if (index === editEffectTypeIndex) {
          return false;
        }
        return String(key || "").trim().toLowerCase() === value.toLowerCase();
      });
      if (exists) {
        editType.value = "";
        return;
      }
    }
    replaceOrAppendCollectionItem(editEffectTypeKeys, editEffectTypeIndex, value);
    editEffectTypeIndex = -1;
    editType.value = "";
    setCollectionCommitMode(editTypeAdd, false);
    renderEditEffectTypeList();
  });
}

if (editType) {
  editType.addEventListener("change", () => {
    if (editType.value !== NEW_INLINE_OPTION) {
      return;
    }
    editType.value = "";
    const origin = editContext && editContext.kind === "effect-create"
      ? editContext.origin
      : editContext && (editContext.kind === "status" || editContext.kind === "status-create")
        ? "statuses"
        : "effects";
    openEffectTypeCreateModal(origin);
  });
}

function openEquipmentEditor(item) {
  if (!item) {
    return;
  }
  resetWeaponEditSection();
  resetRaceEditSection();
  resetEffectTypeSection();
  resetWeightSection();
  editContext = { kind: "equipment", id: item.id };
  editTitle.textContent = t("equipment.edit.title", "Edit Equipment");
  editName.value = item.name || "";
  editDescription.value = item.description || "";
  showWeightSection(item.weightValue || 0, item.weightUnit || "");
  showDamageTypeField(item.damageTypeId || "");
  editTypeField.classList.add("hidden");
  editRangeFields.classList.add("hidden");
  editModifierSection.classList.add("hidden");
  editBonusSection.classList.add("hidden");
  editModal.classList.remove("hidden");
}

function openEquipmentCreate() {
  resetWeaponEditSection();
  resetRaceEditSection();
  resetEffectTypeSection();
  resetWeightSection();
  editContext = { kind: "equipment-create" };
  editTitle.textContent = t("equipment.edit.title", "Edit Equipment");
  editName.value = "";
  editDescription.value = "";
  showWeightSection(0, "");
  showDamageTypeField("");
  editTypeField.classList.add("hidden");
  editRangeFields.classList.add("hidden");
  editModifierSection.classList.add("hidden");
  editBonusSection.classList.add("hidden");
  editModal.classList.remove("hidden");
}

function openWeaponEditor(weapon) {
  if (!weapon) {
    return;
  }
  resetClassEditSection();
  resetWeaponEditSection();
  resetRaceEditSection();
  resetEffectTypeSection();
  resetWeightSection();
  editContext = { kind: "weapon", id: weapon.id };
  editTitle.textContent = t("weapons.edit.title", "Edit Weapon");
  editName.value = weapon.name || "";
  editDescription.value = weapon.description || "";
  showWeightSection(weapon.weightValue || 0, weapon.weightUnit || "");
  showDamageTypeField(weapon.damageTypeId || "");
  editTypeField.classList.add("hidden");
  editRangeFields.classList.add("hidden");
  editModifierSection.classList.add("hidden");
  editBonusSection.classList.add("hidden");
  if (editWeaponDamageField) {
    editWeaponDamageField.classList.remove("hidden");
  }
  if (editWeaponDamageCount) {
    editWeaponDamageCount.value = String(Number(weapon.damageDiceCount || 0));
  }
  if (editWeaponDamageSides) {
    editWeaponDamageSides.value = String(Number(weapon.damageDiceSides || 0));
  }
  if (editWeaponDamageModifier) {
    editWeaponDamageModifier.value = String(Number(weapon.damageDiceModifier || 0));
  }
  if (editWeaponEffectsSection) {
    editWeaponEffectsSection.classList.remove("hidden");
  }
  editWeaponEffectIds = Array.isArray(weapon.effectIds) ? weapon.effectIds.slice() : [];
  populateWeaponEffectSelect();
  renderWeaponEffectList();
  editModal.classList.remove("hidden");
}

function openWeaponCreate() {
  resetClassEditSection();
  resetWeaponEditSection();
  resetRaceEditSection();
  resetEffectTypeSection();
  resetWeightSection();
  editContext = { kind: "weapon-create" };
  editTitle.textContent = t("weapons.edit.title", "Edit Weapon");
  editName.value = "";
  editDescription.value = "";
  showWeightSection(0, "");
  showDamageTypeField("");
  editTypeField.classList.add("hidden");
  editRangeFields.classList.add("hidden");
  editModifierSection.classList.add("hidden");
  editBonusSection.classList.add("hidden");
  if (editWeaponDamageField) {
    editWeaponDamageField.classList.remove("hidden");
  }
  if (editWeaponDamageCount) {
    editWeaponDamageCount.value = "0";
  }
  if (editWeaponDamageSides) {
    editWeaponDamageSides.value = "0";
  }
  if (editWeaponDamageModifier) {
    editWeaponDamageModifier.value = "0";
  }
  if (editWeaponEffectsSection) {
    editWeaponEffectsSection.classList.remove("hidden");
  }
  editWeaponEffectIds = [];
  populateWeaponEffectSelect();
  renderWeaponEffectList();
  editModal.classList.remove("hidden");
}

function openClassEditor(characterClass) {
  if (!characterClass) {
    return;
  }
  resetClassEditSection();
  resetWeaponEditSection();
  resetRaceEditSection();
  editContext = { kind: "class", id: characterClass.id };
  editTitle.textContent = t("classes.edit.title", "Edit Class");
  editName.value = characterClass.name || "";
  editDescription.value = characterClass.description || "";
  if (editClassSection) {
    editClassSection.classList.remove("hidden");
  }
  editClassSkillIds = Array.isArray(characterClass.classSkillIds)
    ? characterClass.classSkillIds.slice()
    : [];
  editClassRequiredScores = Array.isArray(characterClass.requiredAttributeScores)
    ? characterClass.requiredAttributeScores.map((entry) => ({
        attributeId: String(entry.attributeId || ""),
        score: Number(entry.score || 0),
      }))
    : [];
  editClassSkillPointsByLevel = Array.isArray(characterClass.skillPointsByLevel)
    ? characterClass.skillPointsByLevel
        .map((entry) => ({
          level: Number(entry.level || 0),
          points: Number(entry.points || 0),
        }))
        .filter((entry) => entry.level > 0)
    : [];
  editClassSkillPointsByLevel.sort((left, right) => left.level - right.level);
  const hitDie = parseHitDie(characterClass.hitDie);
  populateClassPrimarySelect();
  populateClassHitDieSelect(hitDie.sides);
  populateClassSkillPointsLevelSelect(characterClass.maxLevel);
  populateClassSkillSelect();
  populateClassRequiredSelect();
  if (editClassPrimary) {
    editClassPrimary.value = characterClass.primaryAttribute || "";
  }
  if (editClassHitDieSelect) {
    editClassHitDieSelect.value = hitDie.sides ? String(hitDie.sides) : "";
  }
  if (editClassHitDieModifier) {
    editClassHitDieModifier.value = String(hitDie.modifier || 0);
  }
  if (editClassSkillPoints) {
    editClassSkillPoints.value = Number(characterClass.skillPointsPerLevel || 0);
  }
  if (editClassStartingMoney) {
    editClassStartingMoney.value = Number(characterClass.startingMoney || 0);
  }
  if (editClassSkillPointsSame) {
    editClassSkillPointsSame.checked = characterClass.skillPointsSameAllLevels !== false;
  }
  if (editClassSkillPointsValue) {
    editClassSkillPointsValue.value = "0";
  }
  renderClassSkillList();
  renderClassRequiredList();
  renderClassSkillPointsList();
  updateClassSkillPointsModeUI();
  editTypeField.classList.add("hidden");
  editRangeFields.classList.add("hidden");
  editModifierSection.classList.add("hidden");
  editBonusSection.classList.add("hidden");
  editModal.classList.remove("hidden");
}

function openClassCreate() {
  resetClassEditSection();
  resetWeaponEditSection();
  resetRaceEditSection();
  editContext = { kind: "class-create" };
  editTitle.textContent = t("classes.edit.title", "Edit Class");
  editName.value = "";
  editDescription.value = "";
  if (editClassSection) {
    editClassSection.classList.remove("hidden");
  }
  editClassSkillIds = [];
  editClassRequiredScores = [];
  editClassSkillPointsByLevel = [];
  populateClassPrimarySelect();
  populateClassHitDieSelect(0);
  populateClassSkillPointsLevelSelect(0);
  populateClassSkillSelect();
  populateClassRequiredSelect();
  if (editClassPrimary) {
    editClassPrimary.value = "";
  }
  if (editClassHitDieSelect) {
    editClassHitDieSelect.value = "";
  }
  if (editClassHitDieModifier) {
    editClassHitDieModifier.value = "0";
  }
  if (editClassSkillPoints) {
    editClassSkillPoints.value = "0";
  }
  if (editClassStartingMoney) {
    editClassStartingMoney.value = "0";
  }
  if (editClassSkillPointsSame) {
    editClassSkillPointsSame.checked = true;
  }
  if (editClassSkillPointsValue) {
    editClassSkillPointsValue.value = "0";
  }
  renderClassSkillList();
  renderClassRequiredList();
  renderClassSkillPointsList();
  updateClassSkillPointsModeUI();
  editTypeField.classList.add("hidden");
  editRangeFields.classList.add("hidden");
  editModifierSection.classList.add("hidden");
  editBonusSection.classList.add("hidden");
  editModal.classList.remove("hidden");
}

function populateClassPrimarySelect() {
  if (!editClassPrimary) {
    return;
  }
  const options = [`<option value="">${t("classes.primary_attribute.none", "None")}</option>`]
    .concat(
      (classAttributeOptions || []).map((attribute) => {
        const label = attribute.displayName || attribute.name || "";
        return `<option value="${escapeHtml(attribute.id || "")}">${escapeHtml(label)}</option>`;
      })
    )
    .join("");
  editClassPrimary.innerHTML = options;
}

function populateClassHitDieSelect(selectedSides = 0) {
  if (!editClassHitDieSelect) {
    return;
  }
  const safeSelected = Number(selectedSides || 0);
  const optionsList = (classHitDieOptions || []).slice();
  if (safeSelected && !optionsList.includes(safeSelected)) {
    optionsList.push(safeSelected);
    optionsList.sort((left, right) => left - right);
  }
  const options = [`<option value="">${t("common.die.select", "Select Die")}</option>`]
    .concat(
      optionsList.map((sides) => {
        const safeSides = Number(sides || 0);
        if (!safeSides) {
          return "";
        }
        return `<option value="${safeSides}">d${safeSides}</option>`;
      })
    )
    .filter(Boolean)
    .join("");
  editClassHitDieSelect.innerHTML = options;
}

function populateClassSkillPointsLevelSelect(maxLevel) {
  if (!editClassSkillPointsLevel) {
    return;
  }
  const resolvedMax = Number(maxLevel || 0);
  const limit = resolvedMax > 0 ? resolvedMax : 20;
  const options = [`<option value="">${t("classes.skill_points.level.select", "Select Level")}</option>`]
    .concat(
      Array.from({ length: limit }, (_, index) => {
        const level = index + 1;
        return `<option value="${level}">${t("classes.skill_points.level.label", `Level ${level}`).replace(
          "{0}",
          String(level)
        )}</option>`;
      })
    )
    .join("");
  editClassSkillPointsLevel.innerHTML = options;
}

function populateClassSkillSelect() {
  if (!editClassSkillSelect) {
    return;
  }
  const options = [
    `<option value="">${t("classes.skills.select", "Select Skill")}</option>`,
    `<option value="${NEW_INLINE_OPTION}">${t("skills.new", "New Skill")}</option>`,
  ]
    .concat(
      (classSkillOptions || []).map((skill) => {
        const label = skill.displayName || skill.name || "";
        return `<option value="${escapeHtml(skill.id || "")}">${escapeHtml(label)}</option>`;
      })
    )
    .join("");
  editClassSkillSelect.innerHTML = options;
}

function populateClassRequiredSelect() {
  if (!editClassRequiredSelect) {
    return;
  }
  const options = [`<option value="">${t("classes.required_attributes.select", "Select Attribute")}</option>`]
    .concat(
      (classAttributeOptions || []).map((attribute) => {
        const label = attribute.displayName || attribute.name || "";
        return `<option value="${escapeHtml(attribute.id || "")}">${escapeHtml(label)}</option>`;
      })
    )
    .join("");
  editClassRequiredSelect.innerHTML = options;
}

function parseHitDie(value) {
  const safeValue = String(value || "").trim();
  if (!safeValue) {
    return { sides: 0, modifier: 0 };
  }
  const compact = safeValue.replace(/\s+/g, "");
  let modifier = 0;
  let modIndex = -1;
  for (let index = 1; index < compact.length; index += 1) {
    const ch = compact.charAt(index);
    if (ch === "+" || ch === "-") {
      modIndex = index;
      break;
    }
  }
  let dicePart = compact;
  if (modIndex > 0) {
    dicePart = compact.slice(0, modIndex);
    const modPart = compact.slice(modIndex);
    const parsed = Number(modPart);
    if (!Number.isNaN(parsed)) {
      modifier = parsed;
    }
  }
  const lower = dicePart.toLowerCase();
  const dIndex = lower.indexOf("d");
  let sidesPart = dIndex >= 0 ? lower.slice(dIndex + 1) : lower;
  sidesPart = sidesPart.replace(/[^0-9]/g, "");
  if (!sidesPart) {
    return { sides: 0, modifier };
  }
  const sides = Number(sidesPart);
  if (Number.isNaN(sides)) {
    return { sides: 0, modifier };
  }
  return { sides, modifier };
}

function buildHitDie(sides, modifier) {
  const safeSides = Math.trunc(Number(sides || 0));
  const safeModifier = Math.trunc(Number(modifier || 0));
  if (!safeSides) {
    return "";
  }
  let text = `d${safeSides}`;
  if (safeModifier > 0) {
    text += `+${safeModifier}`;
  } else if (safeModifier < 0) {
    text += `${safeModifier}`;
  }
  return text;
}

function resolveClassSkillLabel(skillId) {
  const safeId = String(skillId || "").trim();
  if (!safeId) {
    return "";
  }
  const match = (classSkillOptions || []).find((skill) => String(skill.id || "") === safeId);
  return match ? match.displayName || match.name || safeId : safeId;
}

function resolveClassAttributeLabel(attributeId) {
  const safeId = String(attributeId || "").trim();
  if (!safeId) {
    return "";
  }
  const match = (classAttributeOptions || []).find((attribute) => String(attribute.id || "") === safeId);
  return match ? match.displayName || match.name || safeId : safeId;
}

function renderClassSkillList() {
  if (!editClassSkillList) {
    return;
  }
  if (!editClassSkillIds.length) {
    editClassSkillList.innerHTML = `<div class="list-item">${t("classes.skills.none", "No class skills assigned.")}</div>`;
    return;
  }
  editClassSkillList.innerHTML = editClassSkillIds
    .map(
      (skillId, index) => renderCollectionRow(
        `<span>${escapeHtml(resolveClassSkillLabel(skillId))}</span>`,
        [
          collectionEditAction("edit-class-skill", index),
          collectionRemoveAction("remove-class-skill", index),
        ]
      )
    )
    .join("");
}

function renderClassRequiredList() {
  if (!editClassRequiredList) {
    return;
  }
  if (!editClassRequiredScores.length) {
    editClassRequiredList.innerHTML = `<div class="list-item">${t(
      "classes.required_attributes.none",
      "No requirements set."
    )}</div>`;
    return;
  }
  editClassRequiredList.innerHTML = editClassRequiredScores
    .map(
      (entry, index) => renderCollectionRow(
        `<span>${escapeHtml(resolveClassAttributeLabel(entry.attributeId))} : ${escapeHtml(entry.score)}</span>`,
        [
          collectionEditAction("edit-class-req", index),
          collectionRemoveAction("remove-class-req", index),
        ]
      )
    )
    .join("");
}

function renderClassSkillPointsList() {
  if (!editClassSkillPointsList) {
    return;
  }
  if (!editClassSkillPointsByLevel.length) {
    editClassSkillPointsList.innerHTML = `<div class="list-item">${t(
      "classes.skill_points.none",
      "No level-specific values."
    )}</div>`;
    return;
  }
  editClassSkillPointsList.innerHTML = editClassSkillPointsByLevel
    .map(
      (entry, index) => renderCollectionRow(
        `<span>${t("classes.skill_points.level.label", `Level ${entry.level}`).replace("{0}", String(entry.level))} : ${entry.points}</span>`,
        [
          collectionEditAction("edit-class-skill-points", index),
          collectionRemoveAction("remove-class-skill-points", index),
        ]
      )
    )
    .join("");
}

function updateClassSkillPointsModeUI() {
  const sameAll = editClassSkillPointsSame ? editClassSkillPointsSame.checked : true;
  if (editClassSkillPoints) {
    editClassSkillPoints.disabled = !sameAll;
  }
  if (editClassSkillPointsLevel) {
    editClassSkillPointsLevel.disabled = sameAll;
  }
  if (editClassSkillPointsValue) {
    editClassSkillPointsValue.disabled = sameAll;
  }
  if (editClassSkillPointsAdd) {
    editClassSkillPointsAdd.disabled = sameAll;
  }
  if (editClassSkillPointsList) {
    editClassSkillPointsList.classList.toggle("disabled", sameAll);
  }
}

function populateRaceTraitSelect() {
  if (!editRaceTraitSelect) {
    return;
  }
  const options = [
    `<option value="">${t("races.traits.select", "Select Trait")}</option>`,
    `<option value="${NEW_INLINE_OPTION}">${t("skills.new", "New Skill")}</option>`,
  ]
    .concat(
      (raceSkillOptions || []).map((skill) => {
        const label = escapeHtml(skill.displayName || skill.name || "");
        const value = escapeHtml(skill.id || "");
        return `<option value="${value}">${label}</option>`;
      })
    )
    .join("");
  editRaceTraitSelect.innerHTML = options;
}

function resolveRaceTraitLabel(skillId) {
  const safeId = String(skillId || "").trim();
  if (!safeId) {
    return "";
  }
  const match = (raceSkillOptions || []).find((skill) => String(skill.id || "") === safeId);
  return match ? match.displayName || match.name || safeId : safeId;
}

function renderRaceTraitList() {
  if (!editRaceTraitList) {
    return;
  }
  if (!editRaceSkillIds.length) {
    editRaceTraitList.innerHTML = `<div class="list-item">${t("races.traits.none", "No traits assigned.")}</div>`;
    return;
  }
  editRaceTraitList.innerHTML = editRaceSkillIds
    .map((skillId, index) => {
      const label = escapeHtml(resolveRaceTraitLabel(skillId));
      return renderCollectionRow(`<span>${label}</span>`, [
        collectionEditAction("edit-race-trait", index),
        collectionRemoveAction("remove-race-trait", index),
      ]);
    })
    .join("");
}

function populateRaceAttributeSelect() {
  if (!editRaceAttributeSelect) {
    return;
  }
  const options = [`<option value="">${t("races.attributes.select", "Select Attribute")}</option>`]
    .concat(
      (raceAttributeOptions || []).map((attribute) => {
        const label = escapeHtml(attribute.displayName || attribute.name || "");
        const value = escapeHtml(attribute.id || "");
        return `<option value="${value}">${label}</option>`;
      })
    )
    .join("");
  editRaceAttributeSelect.innerHTML = options;
}

function resolveRaceAttributeLabel(attributeId) {
  const safeId = String(attributeId || "").trim();
  if (!safeId) {
    return "";
  }
  const match = (raceAttributeOptions || []).find((attribute) => String(attribute.id || "") === safeId);
  return match ? match.displayName || match.name || safeId : safeId;
}

function renderRaceAttributeList() {
  if (!editRaceAttributeList) {
    return;
  }
  if (!editRaceAttributeLimits.length) {
    editRaceAttributeList.innerHTML = `<div class="list-item">${t("races.attributes.none", "No attribute limits set.")}</div>`;
    return;
  }
  const minLabel = t("attributes.edit.min_value", "Minimum Value");
  const maxLabel = t("attributes.edit.max_value", "Maximum Value");
  editRaceAttributeList.innerHTML = editRaceAttributeLimits
    .map((limit, index) => {
      const label = escapeHtml(resolveRaceAttributeLabel(limit.attributeId));
      const minValue = Number(limit.min || 0);
      const maxValue = Number(limit.max || 0);
      return renderCollectionRow(
        `<span>${label} (${minLabel}: ${minValue}, ${maxLabel}: ${maxValue})</span>`,
        [
          collectionEditAction("edit-race-attribute", index),
          collectionRemoveAction("remove-race-attribute", index),
        ]
      );
    })
    .join("");
}

function populateWeaponEffectSelect() {
  if (!editWeaponEffectSelect) {
    return;
  }
  const options = [
    `<option value="">${t("skills.effects.select", "Select Effect")}</option>`,
    `<option value="${NEW_INLINE_OPTION}">${t("skills.effects.new", "New Effect")}</option>`,
  ]
    .concat(
      (weaponEffectOptions || []).map((effect) => {
        const label = escapeHtml(effect.name || effect.displayName || "");
        const value = escapeHtml(effect.id || "");
        return `<option value="${value}">${label}</option>`;
      })
    )
    .join("");
  editWeaponEffectSelect.innerHTML = options;
}

function resolveWeaponEffectLabel(effectId) {
  const safeId = String(effectId || "").trim();
  if (!safeId) {
    return "";
  }
  const match = (weaponEffectOptions || []).find((effect) => String(effect.id || "") === safeId);
  return match ? match.displayName || match.name || safeId : safeId;
}

function renderWeaponEffectList() {
  if (!editWeaponEffectList) {
    return;
  }
  if (!editWeaponEffectIds.length) {
    editWeaponEffectList.innerHTML = `<div class="list-item">${t("skills.effects.none", "No effects assigned.")}</div>`;
    return;
  }
  editWeaponEffectList.innerHTML = editWeaponEffectIds
    .map((effectId, index) => {
      const label = escapeHtml(resolveWeaponEffectLabel(effectId));
      return renderCollectionRow(`<span>${label}</span>`, [
        collectionEditAction("edit-weapon-effect", index),
        collectionRemoveAction("remove-weapon-effect", index),
      ]);
    })
    .join("");
  editWeaponEffectList.querySelectorAll("[data-edit-weapon-effect], [data-remove-weapon-effect]").forEach((button) => {
    button.addEventListener("click", async () => {
      const index = Number(button.dataset.editWeaponEffect ?? button.dataset.removeWeaponEffect ?? -1);
      if (index < 0) {
        return;
      }
      if (button.hasAttribute("data-edit-weapon-effect")) {
        editWeaponEffectIndex = index;
        editWeaponEffectSelect.value = editWeaponEffectIds[index] || "";
        setCollectionCommitMode(editWeaponEffectAdd, true);
        editWeaponEffectSelect.focus();
        return;
      }
      const confirmed = await showConfirm(
        t("common.remove.confirm", "Remove selected item?"),
        t("common.remove", "Remove")
      );
      if (!confirmed) {
        return;
      }
      editWeaponEffectIds.splice(index, 1);
      editWeaponEffectIndex = -1;
      setCollectionCommitMode(editWeaponEffectAdd, false);
      renderWeaponEffectList();
    });
  });
}

function renderEditModifiers() {
  if (!editModifierList) {
    return;
  }
  if (!editModifiers.length) {
    editModifierList.innerHTML = `<div class="list-item">${t("attributes.edit.modifiers.none", "No modifiers yet.")}</div>`;
    return;
  }
  editModifiers = normalizeModifierEntries(editModifiers);
  editModifierList.innerHTML = editModifiers
    .map(
      (entry, index) => `
        <div class="list-item">
          <span>${entry.score} → ${entry.modifier}</span>
          <div class="actions">
            ${collectionEditAction("edit-modifier", index)}
            ${collectionRemoveAction("remove-modifier", index)}
          </div>
        </div>
      `
    )
    .join("");
}

function renderEditBonuses() {
  if (!editBonusList) {
    return;
  }
  if (!editBonuses.length) {
    editBonusList.innerHTML = `<div class="list-item">${t("attributes.edit.bonuses.none", "No bonuses yet.")}</div>`;
    return;
  }
  editBonusList.innerHTML = editBonuses
    .map(
      (entry, index) => `
        <div class="list-item">
          <span>${entry.threshold} : ${escapeHtml(resolveAttributeBonusEffectLabel(entry.effectId))}</span>
          <div class="actions">
            ${collectionEditAction("edit-bonus", index)}
            ${collectionRemoveAction("remove-bonus", index)}
          </div>
        </div>
      `
    )
    .join("");
}

function populateAttributeBonusEffectSelect(selectedValue = "") {
  if (!editBonusEffect) {
    return;
  }
  const safeSelected = String(selectedValue || "");
  editBonusEffect.innerHTML = [
    `<option value="">${t("attributes.edit.bonus.select", "Select Effect")}</option>`,
    `<option value="${NEW_INLINE_OPTION}">${t("skills.effects.new", "New Effect")}</option>`,
    ...(attributeEffectOptions || []).map((effect) => {
      const effectId = String(effect.id || "");
      const label = String(effect.displayName || effect.name || effectId);
      return `<option value="${escapeHtml(effectId)}"${effectId === safeSelected ? " selected" : ""}>${escapeHtml(label)}</option>`;
    }),
  ].join("");
}

function resolveAttributeBonusEffectLabel(effectId) {
  const safeId = String(effectId || "").trim();
  const effect = (attributeEffectOptions || []).find((entry) => String(entry.id || "") === safeId);
  return effect ? String(effect.displayName || effect.name || safeId) : safeId;
}

function closeSkillModal() {
  if (!skillModal) {
    return;
  }
  skillModal.classList.add("hidden");
  skillContext = null;
  skillEffects = [];
  skillLimitedClassIds = [];
  skillLimitedRaceIds = [];
  skillEffectIndex = -1;
  skillClassLimitIndex = -1;
  skillRaceLimitIndex = -1;
  setCollectionCommitMode(skillEffectAdd, false);
  setCollectionCommitMode(skillClassLimitAdd, false);
  setCollectionCommitMode(skillRaceLimitAdd, false);
  if (skillReturnToEdit && editModal) {
    editModal.classList.remove("hidden");
  }
  skillReturnToEdit = false;
}

function renderSkillEffectOptions() {
  if (!skillEffectSelect) {
    return;
  }
  const options = [
    `<option value="">${t("skills.effects.select", "Select Effect")}</option>`,
    `<option value="${NEW_INLINE_OPTION}">${t("skills.effects.new", "New Effect")}</option>`,
  ]
    .concat(
      (skillEffectOptions || []).map(
        (effect) => `<option value="${escapeHtml(effect.name || "")}">${escapeHtml(effect.name || "")}</option>`
      )
    )
    .join("");
  skillEffectSelect.innerHTML = options;
}

function renderSkillCategoryOptions(selectedValue) {
  if (!skillCategorySelect) {
    return;
  }
  let safeValue = String(selectedValue || "");
  const options = [
    `<option value="">${t("skills.category.none", "None")}</option>`,
    `<option value="${NEW_INLINE_OPTION}">${t("skills.category.new", "New Category")}</option>`,
  ]
    .concat(
      (skillCategoryOptions || []).map((category) => {
        const label = category.displayName || category.name || category.key || "";
        return `<option value="${escapeHtml(category.key || "")}">${escapeHtml(label)}</option>`;
      })
    )
    .join("");
  skillCategorySelect.innerHTML = options;
  if (safeValue) {
    let match = (skillCategoryOptions || []).find((category) => String(category.key || "") === safeValue);
    if (!match) {
      const lowerValue = safeValue.toLowerCase();
      match = (skillCategoryOptions || []).find((category) => {
        return (
          String(category.name || "").toLowerCase() === lowerValue ||
          String(category.displayName || "").toLowerCase() === lowerValue
        );
      });
    }
    if (match) {
      safeValue = String(match.key || "");
    }
  }
  skillCategorySelect.value = safeValue;
  skillCategorySelect.dataset.previousValue = safeValue;
}

function resolveSkillCategoryLabel(categoryKey) {
  const safeKey = String(categoryKey || "").trim();
  if (!safeKey) {
    return "";
  }
  let match = (skillCategoryOptions || []).find((category) => String(category.key || "") === safeKey);
  if (!match) {
    const lowerValue = safeKey.toLowerCase();
    match = (skillCategoryOptions || []).find((category) => {
      return (
        String(category.name || "").toLowerCase() === lowerValue ||
        String(category.displayName || "").toLowerCase() === lowerValue
      );
    });
  }
  if (match) {
    return match.displayName || match.name || match.key || safeKey;
  }
  return safeKey;
}

function renderSkillAbilityOptions(selectedValue) {
  if (!skillAbilitySelect) {
    return;
  }
  let safeValue = String(selectedValue || "");
  const options = [`<option value="">${t("skills.ability.none", "None")}</option>`]
    .concat(
      (skillAbilityOptions || []).map((attribute) => {
        const label = attribute.displayName || attribute.name || "";
        return `<option value="${escapeHtml(attribute.id || "")}">${escapeHtml(label)}</option>`;
      })
    )
    .join("");
  skillAbilitySelect.innerHTML = options;
  if (safeValue) {
    let hasMatch = (skillAbilityOptions || []).some((attribute) => String(attribute.id || "") === safeValue);
    if (!hasMatch) {
      const legacyMatch = (skillAbilityOptions || []).find((attribute) => {
        return (
          String(attribute.typeKey || "") === safeValue ||
          String(attribute.displayName || "").toLowerCase() === safeValue.toLowerCase() ||
          String(attribute.name || "").toLowerCase() === safeValue.toLowerCase()
        );
      });
      if (legacyMatch) {
        safeValue = String(legacyMatch.id || "");
        hasMatch = Boolean(safeValue);
      }
    }
    if (!hasMatch) {
      const extra = document.createElement("option");
      extra.value = safeValue;
      extra.textContent = safeValue;
      skillAbilitySelect.appendChild(extra);
    }
  }
  skillAbilitySelect.value = safeValue;
}

function renderSkillEffectList() {
  if (!skillEffectList) {
    return;
  }
  if (!skillEffects.length) {
    skillEffectList.innerHTML = `<div class="list-item">${t("skills.effects.none", "No effects assigned.")}</div>`;
    return;
  }
  skillEffectList.innerHTML = skillEffects
    .map(
      (effectName, index) => `
        <div class="list-item">
          <span>${escapeHtml(effectName)}</span>
          <div class="actions">
            ${collectionEditAction("edit-skill-effect", index)}
            ${collectionRemoveAction("remove-skill-effect", index)}
          </div>
        </div>
      `
    )
    .join("");
}

function renderSkillClassLimitOptions(selectedValue = "") {
  if (!skillClassLimitSelect) {
    return;
  }
  const options = [`<option value="">${t("skills.limits.class_select", "Select Class")}</option>`]
    .concat(
      (skillClassOptions || []).map((entry) => {
        const label = entry.displayName || entry.name || "";
        return `<option value="${escapeHtml(entry.id || "")}">${escapeHtml(label)}</option>`;
      })
    )
    .join("");
  skillClassLimitSelect.innerHTML = options;
  skillClassLimitSelect.value = selectedValue || "";
}

function renderSkillRaceLimitOptions(selectedValue = "") {
  if (!skillRaceLimitSelect) {
    return;
  }
  const options = [`<option value="">${t("skills.limits.race_select", "Select Race")}</option>`]
    .concat(
      (skillRaceOptions || []).map((entry) => {
        const label = entry.displayName || entry.name || "";
        return `<option value="${escapeHtml(entry.id || "")}">${escapeHtml(label)}</option>`;
      })
    )
    .join("");
  skillRaceLimitSelect.innerHTML = options;
  skillRaceLimitSelect.value = selectedValue || "";
}

function resolveSkillClassName(classId) {
  const safeId = String(classId || "").trim();
  if (!safeId) {
    return t("common.none", "None");
  }
  const match = (skillClassOptions || []).find((entry) => String(entry.id || "") === safeId);
  if (!match) {
    return safeId;
  }
  return String(match.displayName || match.name || safeId);
}

function resolveSkillRaceName(raceId) {
  const safeId = String(raceId || "").trim();
  if (!safeId) {
    return t("common.none", "None");
  }
  const match = (skillRaceOptions || []).find((entry) => String(entry.id || "") === safeId);
  if (!match) {
    return safeId;
  }
  return String(match.displayName || match.name || safeId);
}

function renderSkillClassLimitList() {
  if (!skillClassLimitList) {
    return;
  }
  if (!skillLimitedClassIds.length) {
    skillClassLimitList.innerHTML = `<div class="list-item">${t("common.none", "None")}</div>`;
    return;
  }
  skillClassLimitList.innerHTML = skillLimitedClassIds
    .map(
      (classId, index) => `
        <div class="list-item">
          <span>${escapeHtml(resolveSkillClassName(classId))}</span>
          <div class="actions">
            ${collectionEditAction("edit-skill-class-limit", index)}
            ${collectionRemoveAction("remove-skill-class-limit", index)}
          </div>
        </div>
      `
    )
    .join("");
}

function renderSkillRaceLimitList() {
  if (!skillRaceLimitList) {
    return;
  }
  if (!skillLimitedRaceIds.length) {
    skillRaceLimitList.innerHTML = `<div class="list-item">${t("common.none", "None")}</div>`;
    return;
  }
  skillRaceLimitList.innerHTML = skillLimitedRaceIds
    .map(
      (raceId, index) => `
        <div class="list-item">
          <span>${escapeHtml(resolveSkillRaceName(raceId))}</span>
          <div class="actions">
            ${collectionEditAction("edit-skill-race-limit", index)}
            ${collectionRemoveAction("remove-skill-race-limit", index)}
          </div>
        </div>
      `
    )
    .join("");
}

async function ensureSkillEditorOptions() {
  if (!ensureDraft()) {
    return;
  }
  const [effectsData, attributesData, categoriesData, classesData, racesData] = await Promise.all([
    api("GET", `/api/drafts/${state.draftId}/effects`),
    api("GET", `/api/drafts/${state.draftId}/attributes`),
    api("GET", `/api/drafts/${state.draftId}/skill-categories`),
    api("GET", `/api/drafts/${state.draftId}/classes`),
    api("GET", `/api/drafts/${state.draftId}/races`),
  ]);
  skillEffectOptions = sortByLabel(
    effectsData.effects || [],
    (effect) => effect.name || effect.displayName || ""
  );
  skillAbilityOptions = attributesData.attributes || [];
  skillCategoryOptions = sortByLabel(
    categoriesData.categories || [],
    (category) => category.displayName || category.name || category.key || ""
  );
  skillClassOptions = sortByLabel(classesData.classes || [], (entry) => entry.displayName || entry.name || "");
  skillRaceOptions = sortByLabel(racesData.races || [], (entry) => entry.displayName || entry.name || "");
}

async function openSkillEditor(skill) {
  if (!skill || !skillModal) {
    return;
  }
  try {
    await ensureSkillEditorOptions();
  } catch (error) {
    showToast(error.message);
    return;
  }
  skillContext = { id: skill.id, mode: "edit", origin: "skills" };
  skillEffectIndex = -1;
  skillClassLimitIndex = -1;
  skillRaceLimitIndex = -1;
  setCollectionCommitMode(skillEffectAdd, false);
  setCollectionCommitMode(skillClassLimitAdd, false);
  setCollectionCommitMode(skillRaceLimitAdd, false);
  skillNameInput.value = skill.name || "";
  skillDescriptionInput.value = skill.description || "";
  renderSkillCategoryOptions(skill.category || "");
  skillTrainedOnly.checked = Boolean(skill.trainedOnly);
  skillArmorPenalty.value = Number(skill.armorCheckPenalty || 0);
  if (skillStartingMoneyModifier) {
    skillStartingMoneyModifier.value = Number(skill.startingMoneyModifier || 0);
  }
  skillEffects = Array.isArray(skill.effectNames) ? Array.from(new Set(skill.effectNames)) : [];
  skillLimitedClassIds = Array.isArray(skill.limitedToClasses)
    ? Array.from(new Set(skill.limitedToClasses.map((id) => String(id || "").trim()).filter(Boolean)))
    : [];
  skillLimitedRaceIds = Array.isArray(skill.limitedToRaces)
    ? Array.from(new Set(skill.limitedToRaces.map((id) => String(id || "").trim()).filter(Boolean)))
    : [];
  renderSkillAbilityOptions(skill.relatedAbility || "");
  renderSkillEffectOptions();
  renderSkillEffectList();
  renderSkillClassLimitOptions("");
  renderSkillRaceLimitOptions("");
  renderSkillClassLimitList();
  renderSkillRaceLimitList();
  skillModal.classList.remove("hidden");
}

async function openSkillCreateModal(origin, prefillName, prefillDescription) {
  if (!skillModal) {
    return;
  }
  try {
    await ensureSkillEditorOptions();
  } catch (error) {
    showToast(error.message);
    return;
  }
  skillContext = { mode: "create", origin: String(origin || "") };
  skillEffectIndex = -1;
  skillClassLimitIndex = -1;
  skillRaceLimitIndex = -1;
  setCollectionCommitMode(skillEffectAdd, false);
  setCollectionCommitMode(skillClassLimitAdd, false);
  setCollectionCommitMode(skillRaceLimitAdd, false);
  skillNameInput.value = String(prefillName || "");
  skillDescriptionInput.value = String(prefillDescription || "");
  skillTrainedOnly.checked = false;
  skillArmorPenalty.value = "0";
  if (skillStartingMoneyModifier) {
    skillStartingMoneyModifier.value = "0";
  }
  skillEffects = [];
  skillLimitedClassIds = [];
  skillLimitedRaceIds = [];
  renderSkillCategoryOptions(state.lastSkillCategoryKey || "");
  renderSkillAbilityOptions("");
  renderSkillEffectOptions();
  renderSkillEffectList();
  renderSkillClassLimitOptions("");
  renderSkillRaceLimitOptions("");
  renderSkillClassLimitList();
  renderSkillRaceLimitList();
  if ((skillContext.origin === "race" || skillContext.origin === "class") && editModal) {
    editModal.classList.add("hidden");
    skillReturnToEdit = true;
  }
  skillModal.classList.remove("hidden");
}

function closeSpellModal() {
  if (!spellModal) {
    return;
  }
  spellModal.classList.add("hidden");
  spellContext = null;
  spellEffects = [];
  spellEffectIndex = -1;
  setCollectionCommitMode(spellEffectAdd, false);
  if (spellDamageTypeSelect) {
    spellDamageTypeSelect.innerHTML = "";
  }
}

function renderSpellEffectOptions(selectedValue) {
  if (!spellEffectSelect) {
    return;
  }
  const options = [
    `<option value="">${t("skills.effects.select", "Select Effect")}</option>`,
    `<option value="${NEW_INLINE_OPTION}">${t("skills.effects.new", "New Effect")}</option>`,
  ]
    .concat(
      (spellEffectOptions || []).map(
        (effect) => `<option value="${escapeHtml(effect.name || "")}">${escapeHtml(effect.name || "")}</option>`
      )
    )
    .join("");
  spellEffectSelect.innerHTML = options;
  spellEffectSelect.value = selectedValue || "";
}

function renderSpellEffectList() {
  if (!spellEffectList) {
    return;
  }
  if (!spellEffects.length) {
    spellEffectList.innerHTML = `<div class="list-item">${t("skills.effects.none", "No effects assigned.")}</div>`;
    return;
  }
  spellEffectList.innerHTML = spellEffects
    .map(
      (effectName, index) => `
        <div class="list-item">
          <span>${escapeHtml(effectName)}</span>
          <div class="actions">
            ${collectionEditAction("edit-spell-effect", index)}
            ${collectionRemoveAction("remove-spell-effect", index)}
          </div>
        </div>
      `
    )
    .join("");
}

async function openSpellEditor(spell) {
  if (!spell || !spellModal) {
    return;
  }
  spellContext = { id: spell.id, mode: "edit" };
  spellEffectIndex = -1;
  setCollectionCommitMode(spellEffectAdd, false);
  spellNameInput.value = spell.name || "";
  spellDescriptionInput.value = spell.description || "";
  spellSchoolInput.value = spell.school || "";
  spellLevelInput.value = Number(spell.level || 0);
  spellCastingInput.value = spell.castingTime || "";
  spellRangeInput.value = spell.range || "";
  spellDurationInput.value = spell.duration || "";
  try {
    await ensureDamageTypeOptions();
  } catch (error) {
    showToast(error.message);
  }
  renderDamageTypeOptions(spellDamageTypeSelect, spell.damageTypeId || "");
  if (Array.isArray(spell.effectNames) && spell.effectNames.length) {
    spellEffects = Array.from(new Set(spell.effectNames.filter(Boolean)));
  } else {
    spellEffects = [];
    const primary = String(spell.effect || "").trim();
    const secondary = String(spell.secondaryEffect || "").trim();
    if (primary) {
      spellEffects.push(primary);
    }
    if (secondary && !spellEffects.includes(secondary)) {
      spellEffects.push(secondary);
    }
  }
  renderSpellEffectOptions();
  renderSpellEffectList();
  spellModal.classList.remove("hidden");
}

async function openSpellCreateModal() {
  if (!spellModal) {
    return;
  }
  spellContext = { mode: "create" };
  spellEffectIndex = -1;
  setCollectionCommitMode(spellEffectAdd, false);
  spellNameInput.value = "";
  spellDescriptionInput.value = "";
  spellSchoolInput.value = "";
  spellLevelInput.value = "0";
  spellCastingInput.value = "";
  spellRangeInput.value = "";
  spellDurationInput.value = "";
  try {
    await ensureDamageTypeOptions();
  } catch (error) {
    showToast(error.message);
  }
  renderDamageTypeOptions(spellDamageTypeSelect, "");
  spellEffects = [];
  renderSpellEffectOptions();
  renderSpellEffectList();
  spellModal.classList.remove("hidden");
}

function openRaceEditor(race) {
  if (!race) {
    return;
  }
  resetClassEditSection();
  resetWeaponEditSection();
  resetRaceEditSection();
  editContext = { kind: "race", id: race.id };
  editTitle.textContent = t("races.edit.title", "Edit Race");
  editName.value = race.name || "";
  editDescription.value = race.description || "";
  editTypeField.classList.add("hidden");
  editRangeFields.classList.add("hidden");
  editModifierSection.classList.add("hidden");
  editBonusSection.classList.add("hidden");
  if (editRaceSection) {
    editRaceSection.classList.remove("hidden");
  }
  editRaceSkillIds = Array.isArray(race.racialSkillIds)
    ? race.racialSkillIds.slice()
    : Array.isArray(race.racialSkills)
      ? race.racialSkills.slice()
      : [];
  editRaceAttributeLimits = Array.isArray(race.attributeScoreLimits)
    ? race.attributeScoreLimits.map((limit) => ({
        attributeId: String(limit.attributeId || "").trim(),
        min: Number(limit.min || 0),
        max: Number(limit.max || 0),
      }))
    : [];
  if (editRaceStartingMoneyModifier) {
    editRaceStartingMoneyModifier.value = Number(race.startingMoneyModifier || 0);
  }
  populateRaceTraitSelect();
  renderRaceTraitList();
  populateRaceAttributeSelect();
  renderRaceAttributeList();
  editModal.classList.remove("hidden");
}

function openRaceCreate() {
  resetClassEditSection();
  resetWeaponEditSection();
  resetRaceEditSection();
  editContext = { kind: "race-create" };
  editTitle.textContent = t("races.edit.title", "Edit Race");
  editName.value = "";
  editDescription.value = "";
  editTypeField.classList.add("hidden");
  editRangeFields.classList.add("hidden");
  editModifierSection.classList.add("hidden");
  editBonusSection.classList.add("hidden");
  if (editRaceSection) {
    editRaceSection.classList.remove("hidden");
  }
  editRaceSkillIds = [];
  editRaceAttributeLimits = [];
  if (editRaceStartingMoneyModifier) {
    editRaceStartingMoneyModifier.value = "0";
  }
  populateRaceTraitSelect();
  populateRaceAttributeSelect();
  renderRaceTraitList();
  renderRaceAttributeList();
  editModal.classList.remove("hidden");
}

async function renderRaces(openRaceId = "") {
  if (!ensureDraft()) {
    return;
  }
  setStep("races");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const [data, skillsData, attributesData] = await Promise.all([
      api("GET", `/api/drafts/${state.draftId}/races`),
      api("GET", `/api/drafts/${state.draftId}/skills`),
      api("GET", `/api/drafts/${state.draftId}/attributes`),
    ]);
    const systemName = String(data.systemName || "");
    const title = systemNameTitle(systemName, t("races.title", "Races"));
    const races = sortByLabel(data.races || [], (race) => race.name || race.displayName || "");
    raceSkillOptions = sortByLabel(skillsData.skills || [], (skill) => skill.displayName || skill.name || "");
    raceAttributeOptions = attributesData.attributes || [];

    const raceList = races
      .map(
        (race) => renderCollectionRow(
          `<div><strong>${escapeHtml(race.name || t("races.untitled", "Untitled"))}</strong></div>`,
          [
            collectionEditAction("edit-race", race.id),
            collectionRemoveAction("remove-race", race.id),
          ]
        )
      )
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${escapeHtml(title)}</h1>
        ${renderSystemNameControls(systemName, t("races.title", "Races"))}
        <button class="btn" id="addRace" type="button">${t("races.add", "Add Race")}</button>
        <div class="list" id="raceList">
          ${raceList || `<div class="list-item">${t("races.none", "No races yet.")}</div>`}
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToSpells" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn ghost" id="racesContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    document.getElementById("addRace").addEventListener("click", () => {
      openRaceCreate();
    });

    document.querySelectorAll("[data-edit-race]").forEach((button) => {
      button.addEventListener("click", () => {
        const raceId = button.dataset.editRace;
        const race = races.find((item) => item.id === raceId);
        openRaceEditor(race);
      });
    });

    document.querySelectorAll("[data-remove-race]").forEach((button) => {
      button.addEventListener("click", async () => {
        const id = button.dataset.removeRace;
        const confirmed = await showConfirm(
          t("common.remove.confirm", "Remove selected item?"),
          t("common.remove", "Remove")
        );
        if (!confirmed) {
          return;
        }
        try {
          await api("DELETE", `/api/drafts/${state.draftId}/races`, { id });
          markSaved(t("web.toast.race_removed", "Race removed"));
          renderRaces();
        } catch (error) {
          showToast(error.message);
        }
      });
    });

    document.getElementById("backToSpells").addEventListener("click", navigateBackInApp);
    document.getElementById("racesContinue").addEventListener("click", () => {
      markSaved(t("web.toast.races_saved", "Races saved"));
      renderClasses();
    });
    wireSystemNameSave("races", () => renderRaces());
    if (openRaceId) {
      const race = races.find((item) => item.id === openRaceId);
      if (race) {
        openRaceEditor(race);
      }
    }
  } catch (error) {
    showToast(error.message);
  }
}

editCancel.addEventListener("click", closeEditModal);

editModifierAdd.addEventListener("click", () => {
  const score = Number(editModifierScore.value || 0);
  const modifier = Number(editModifierValue.value || 0);
  replaceOrAppendCollectionItem(editModifiers, editModifierIndex, { score, modifier });
  editModifierIndex = -1;
  editModifierScore.value = "0";
  editModifierValue.value = "0";
  setCollectionCommitMode(editModifierAdd, false);
  renderEditModifiers();
});

if (editModifierApplyAll) {
  editModifierApplyAll.addEventListener("change", () => {
    if (!editModifierApplyAll.checked || editModifiers.length) {
      return;
    }
    editModifiers = getStandardAttributeModifiers();
    renderEditModifiers();
  });
}

editBonusAdd.addEventListener("click", () => {
  const threshold = Number(editBonusThreshold.value || 0);
  const effectId = String(editBonusEffect.value || "").trim();
  if (!effectId || effectId === NEW_INLINE_OPTION) {
    showToast(t("attributes.edit.bonus.effect.required", "Select an Effect."));
    return;
  }
  const duplicate = editBonuses.some(
    (entry, index) => index !== editBonusIndex
      && Number(entry.threshold || 0) === threshold
      && String(entry.effectId || "") === effectId
  );
  if (!duplicate) {
    replaceOrAppendCollectionItem(editBonuses, editBonusIndex, { threshold, effectId });
  }
  editBonusIndex = -1;
  editBonusEffect.value = "";
  setCollectionCommitMode(editBonusAdd, false);
  populateAttributeBonusEffectSelect();
  renderEditBonuses();
});

editBonusEffect.addEventListener("change", () => {
  if (editBonusEffect.value !== NEW_INLINE_OPTION) {
    return;
  }
  editBonusEffect.value = "";
  openEffectCreateModal("attribute");
});

editModifierList.addEventListener("click", async (event) => {
  const button = event.target.closest("button[data-edit-modifier], button[data-remove-modifier]");
  if (!button) {
    return;
  }
  const index = Number(button.dataset.editModifier ?? button.dataset.removeModifier);
  if (button.hasAttribute("data-edit-modifier")) {
    const entry = editModifiers[index];
    if (!entry) {
      return;
    }
    editModifierIndex = index;
    editModifierScore.value = String(entry.score);
    editModifierValue.value = String(entry.modifier);
    setCollectionCommitMode(editModifierAdd, true);
    editModifierScore.focus();
    return;
  }
  const confirmed = await showConfirm(
    t("common.remove.confirm", "Remove selected item?"),
    t("common.remove", "Remove")
  );
  if (!confirmed) {
    return;
  }
  editModifiers.splice(index, 1);
  editModifierIndex = -1;
  setCollectionCommitMode(editModifierAdd, false);
  renderEditModifiers();
});

editBonusList.addEventListener("click", async (event) => {
  const button = event.target.closest("button[data-edit-bonus], button[data-remove-bonus]");
  if (!button) {
    return;
  }
  const index = Number(button.dataset.editBonus ?? button.dataset.removeBonus);
  if (button.hasAttribute("data-edit-bonus")) {
    const entry = editBonuses[index];
    if (!entry) {
      return;
    }
    editBonusIndex = index;
    editBonusThreshold.value = String(entry.threshold);
    populateAttributeBonusEffectSelect(entry.effectId);
    setCollectionCommitMode(editBonusAdd, true);
    editBonusThreshold.focus();
    return;
  }
  const confirmed = await showConfirm(
    t("common.remove.confirm", "Remove selected item?"),
    t("common.remove", "Remove")
  );
  if (!confirmed) {
    return;
  }
  editBonuses.splice(index, 1);
  editBonusIndex = -1;
  setCollectionCommitMode(editBonusAdd, false);
  renderEditBonuses();
});

if (editRaceTraitAdd) {
  editRaceTraitAdd.addEventListener("click", () => {
    const skillId = String(editRaceTraitSelect.value || "").trim();
    if (!skillId || skillId === NEW_INLINE_OPTION) {
      return;
    }
    const duplicate = editRaceSkillIds.some((entry, index) => entry === skillId && index !== editRaceTraitIndex);
    if (!duplicate) {
      replaceOrAppendCollectionItem(editRaceSkillIds, editRaceTraitIndex, skillId);
      editRaceTraitIndex = -1;
      editRaceTraitSelect.value = "";
      setCollectionCommitMode(editRaceTraitAdd, false);
      renderRaceTraitList();
    }
  });
}

if (editRaceAttributeAdd) {
  editRaceAttributeAdd.addEventListener("click", () => {
    const attributeId = String(editRaceAttributeSelect.value || "").trim();
    if (!attributeId) {
      return;
    }
    const minValue = Number(editRaceAttributeMin.value || 0);
    const maxValue = Number(editRaceAttributeMax.value || 0);
    if (minValue > maxValue) {
      showToast(t("attributes.edit.range.invalid", "Minimum value cannot exceed maximum."));
      return;
    }
    const existingIndex = editRaceAttributeLimits.findIndex(
      (limit, index) => String(limit.attributeId || "") === attributeId && index !== editRaceAttributeIndex
    );
    const entry = { attributeId, min: minValue, max: maxValue };
    if (existingIndex >= 0) {
      editRaceAttributeLimits[existingIndex] = entry;
      if (editRaceAttributeIndex >= 0 && editRaceAttributeIndex !== existingIndex) {
        editRaceAttributeLimits.splice(editRaceAttributeIndex, 1);
      }
    } else {
      replaceOrAppendCollectionItem(editRaceAttributeLimits, editRaceAttributeIndex, entry);
    }
    editRaceAttributeIndex = -1;
    editRaceAttributeSelect.value = "";
    setCollectionCommitMode(editRaceAttributeAdd, false);
    renderRaceAttributeList();
  });
}

if (editWeaponEffectAdd) {
  editWeaponEffectAdd.addEventListener("click", () => {
    const effectId = String(editWeaponEffectSelect.value || "").trim();
    if (!effectId || effectId === NEW_INLINE_OPTION) {
      return;
    }
    const duplicate = editWeaponEffectIds.some((entry, index) => entry === effectId && index !== editWeaponEffectIndex);
    if (!duplicate) {
      replaceOrAppendCollectionItem(editWeaponEffectIds, editWeaponEffectIndex, effectId);
      editWeaponEffectIndex = -1;
      editWeaponEffectSelect.value = "";
      setCollectionCommitMode(editWeaponEffectAdd, false);
      renderWeaponEffectList();
    }
  });
}
if (editWeaponEffectSelect) {
  editWeaponEffectSelect.addEventListener("change", async () => {
    if (editWeaponEffectSelect.value !== NEW_INLINE_OPTION) {
      return;
    }
    editWeaponEffectSelect.value = "";
    await openEffectCreateModal("weapon", "", "", state.lastEffectTypeKeys);
  });
}

if (editClassSkillAdd) {
  editClassSkillAdd.addEventListener("click", () => {
    const skillId = String(editClassSkillSelect.value || "").trim();
    if (!skillId || skillId === NEW_INLINE_OPTION) {
      return;
    }
    const duplicate = editClassSkillIds.some((entry, index) => entry === skillId && index !== editClassSkillIndex);
    if (!duplicate) {
      replaceOrAppendCollectionItem(editClassSkillIds, editClassSkillIndex, skillId);
      editClassSkillIndex = -1;
      editClassSkillSelect.value = "";
      setCollectionCommitMode(editClassSkillAdd, false);
      renderClassSkillList();
    }
  });
}

if (editClassSkillList) {
  editClassSkillList.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-edit-class-skill], button[data-remove-class-skill]");
    if (!button) {
      return;
    }
    const index = Number(button.dataset.editClassSkill ?? button.dataset.removeClassSkill);
    if (button.hasAttribute("data-edit-class-skill")) {
      editClassSkillIndex = index;
      editClassSkillSelect.value = editClassSkillIds[index] || "";
      setCollectionCommitMode(editClassSkillAdd, true);
      editClassSkillSelect.focus();
      return;
    }
    const confirmed = await showConfirm(
      t("common.remove.confirm", "Remove selected item?"),
      t("common.remove", "Remove")
    );
    if (!confirmed) {
      return;
    }
    editClassSkillIds.splice(index, 1);
    editClassSkillIndex = -1;
    setCollectionCommitMode(editClassSkillAdd, false);
    renderClassSkillList();
  });
}

if (editClassSkillSelect) {
  editClassSkillSelect.addEventListener("change", async () => {
    if (editClassSkillSelect.value !== NEW_INLINE_OPTION) {
      return;
    }
    editClassSkillSelect.value = "";
    await openSkillCreateModal("class", "", "");
  });
}

if (editClassSkillPointsSame) {
  editClassSkillPointsSame.addEventListener("change", () => {
    updateClassSkillPointsModeUI();
  });
}

if (editClassSkillPointsAdd) {
  editClassSkillPointsAdd.addEventListener("click", () => {
    if (editClassSkillPointsSame && editClassSkillPointsSame.checked) {
      return;
    }
    const level = Number(editClassSkillPointsLevel ? editClassSkillPointsLevel.value : 0);
    if (!level) {
      return;
    }
    const points = Math.max(0, Number(editClassSkillPointsValue ? editClassSkillPointsValue.value : 0));
    if (editClassSkillPointsIndex >= 0 && editClassSkillPointsIndex < editClassSkillPointsByLevel.length) {
      editClassSkillPointsByLevel.splice(editClassSkillPointsIndex, 1);
    }
    const existingIndex = editClassSkillPointsByLevel.findIndex((entry) => entry.level === level);
    const entry = { level, points };
    if (existingIndex >= 0) {
      editClassSkillPointsByLevel.splice(existingIndex, 1, entry);
    } else {
      editClassSkillPointsByLevel.push(entry);
    }
    editClassSkillPointsByLevel.sort((left, right) => left.level - right.level);
    editClassSkillPointsIndex = -1;
    editClassSkillPointsLevel.value = "";
    setCollectionCommitMode(editClassSkillPointsAdd, false);
    renderClassSkillPointsList();
  });
}

if (editClassSkillPointsList) {
  editClassSkillPointsList.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-edit-class-skill-points], button[data-remove-class-skill-points]");
    if (!button) {
      return;
    }
    if (editClassSkillPointsSame && editClassSkillPointsSame.checked) {
      return;
    }
    const index = Number(button.dataset.editClassSkillPoints ?? button.dataset.removeClassSkillPoints);
    if (button.hasAttribute("data-edit-class-skill-points")) {
      const entry = editClassSkillPointsByLevel[index];
      if (!entry) {
        return;
      }
      editClassSkillPointsIndex = index;
      editClassSkillPointsLevel.value = String(entry.level);
      editClassSkillPointsValue.value = String(entry.points);
      setCollectionCommitMode(editClassSkillPointsAdd, true);
      editClassSkillPointsLevel.focus();
      return;
    }
    const confirmed = await showConfirm(
      t("common.remove.confirm", "Remove selected item?"),
      t("common.remove", "Remove")
    );
    if (!confirmed) {
      return;
    }
    editClassSkillPointsByLevel.splice(index, 1);
    editClassSkillPointsIndex = -1;
    setCollectionCommitMode(editClassSkillPointsAdd, false);
    renderClassSkillPointsList();
  });
}

if (editClassRequiredAdd) {
  editClassRequiredAdd.addEventListener("click", () => {
    const attributeId = String(editClassRequiredSelect.value || "").trim();
    const scoreValue = Number(editClassRequiredScore.value || 0);
    if (!attributeId || scoreValue <= 0) {
      return;
    }
    const existingIndex = editClassRequiredScores.findIndex(
      (entry, index) => String(entry.attributeId || "") === attributeId && index !== editClassRequiredIndex
    );
    const entry = { attributeId, score: scoreValue };
    if (existingIndex >= 0) {
      editClassRequiredScores.splice(existingIndex, 1, entry);
      if (editClassRequiredIndex >= 0 && editClassRequiredIndex !== existingIndex) {
        editClassRequiredScores.splice(editClassRequiredIndex, 1);
      }
    } else {
      replaceOrAppendCollectionItem(editClassRequiredScores, editClassRequiredIndex, entry);
    }
    editClassRequiredIndex = -1;
    editClassRequiredSelect.value = "";
    setCollectionCommitMode(editClassRequiredAdd, false);
    renderClassRequiredList();
  });
}

if (editClassRequiredList) {
  editClassRequiredList.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-edit-class-req], button[data-remove-class-req]");
    if (!button) {
      return;
    }
    const index = Number(button.dataset.editClassReq ?? button.dataset.removeClassReq);
    if (button.hasAttribute("data-edit-class-req")) {
      const entry = editClassRequiredScores[index];
      if (!entry) {
        return;
      }
      editClassRequiredIndex = index;
      editClassRequiredSelect.value = entry.attributeId || "";
      editClassRequiredScore.value = String(entry.score);
      setCollectionCommitMode(editClassRequiredAdd, true);
      editClassRequiredSelect.focus();
      return;
    }
    const confirmed = await showConfirm(
      t("common.remove.confirm", "Remove selected item?"),
      t("common.remove", "Remove")
    );
    if (!confirmed) {
      return;
    }
    editClassRequiredScores.splice(index, 1);
    editClassRequiredIndex = -1;
    setCollectionCommitMode(editClassRequiredAdd, false);
    renderClassRequiredList();
  });
}

if (editRaceTraitList) {
  editRaceTraitList.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-edit-race-trait], button[data-remove-race-trait]");
    if (!button) {
      return;
    }
    const index = Number(button.dataset.editRaceTrait ?? button.dataset.removeRaceTrait);
    if (button.hasAttribute("data-edit-race-trait")) {
      editRaceTraitIndex = index;
      editRaceTraitSelect.value = editRaceSkillIds[index] || "";
      setCollectionCommitMode(editRaceTraitAdd, true);
      editRaceTraitSelect.focus();
      return;
    }
    const confirmed = await showConfirm(
      t("common.remove.confirm", "Remove selected item?"),
      t("common.remove", "Remove")
    );
    if (!confirmed) {
      return;
    }
    editRaceSkillIds.splice(index, 1);
    editRaceTraitIndex = -1;
    setCollectionCommitMode(editRaceTraitAdd, false);
    renderRaceTraitList();
  });
}

if (editRaceAttributeList) {
  editRaceAttributeList.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-edit-race-attribute], button[data-remove-race-attribute]");
    if (!button) {
      return;
    }
    const index = Number(button.dataset.editRaceAttribute ?? button.dataset.removeRaceAttribute);
    if (button.hasAttribute("data-edit-race-attribute")) {
      const entry = editRaceAttributeLimits[index];
      if (!entry) {
        return;
      }
      editRaceAttributeIndex = index;
      editRaceAttributeSelect.value = entry.attributeId || "";
      editRaceAttributeMin.value = String(entry.min);
      editRaceAttributeMax.value = String(entry.max);
      setCollectionCommitMode(editRaceAttributeAdd, true);
      editRaceAttributeSelect.focus();
      return;
    }
    const confirmed = await showConfirm(
      t("common.remove.confirm", "Remove selected item?"),
      t("common.remove", "Remove")
    );
    if (!confirmed) {
      return;
    }
    editRaceAttributeLimits.splice(index, 1);
    editRaceAttributeIndex = -1;
    setCollectionCommitMode(editRaceAttributeAdd, false);
    renderRaceAttributeList();
  });
}

if (editRaceTraitSelect) {
  editRaceTraitSelect.addEventListener("change", async () => {
    if (editRaceTraitSelect.value !== NEW_INLINE_OPTION) {
      return;
    }
    editRaceTraitSelect.value = "";
    await openSkillCreateModal("race", "", "");
  });
}

editOk.addEventListener("click", async () => {
  if (!editContext) {
    closeEditModal();
    return;
  }
  const name = String(editName.value || "").trim();
  if (!name) {
    showToast(t("common.name.required", "Name is required."));
    return;
  }
  const description = String(editDescription.value || "").trim();
  if (editContext.kind === "attribute-type") {
    try {
      await api("POST", `/api/drafts/${state.draftId}/attribute-types/update`, {
        key: editContext.key,
        name,
        description,
      });
      markSaved(t("web.toast.type_updated", "Category updated"));
      closeEditModal();
      renderAttributeTypes();
    } catch (error) {
      showToast(error.message);
    }
    return;
  }
  if (editContext.kind === "attribute-type-create") {
    try {
      await api("POST", `/api/drafts/${state.draftId}/attribute-types`, { name, description });
      markSaved(t("web.toast.type_added", "Category added"));
      closeEditModal();
      renderAttributeTypes();
    } catch (error) {
      showToast(error.message);
    }
    return;
  }
  if (editContext.kind === "effect-type") {
    try {
      await api("POST", `/api/drafts/${state.draftId}/effect-types/update`, {
        key: editContext.key,
        name,
        description,
      });
      markSaved(t("web.toast.effect_type_updated", "Affected system updated"));
      closeEditModal();
      renderEffectTypes();
    } catch (error) {
      showToast(error.message);
    }
    return;
  }
  if (editContext.kind === "status-create") {
    try {
      const effectTypeKeys = editEffectTypeKeys
        .map((key) => String(key || "").trim())
        .filter(Boolean);
      await api("POST", `/api/drafts/${state.draftId}/statuses`, { name, description, effectTypeKeys });
      markSaved(t("web.toast.status_added", "Status added"));
      state.lastStatusEffectTypeKeys = effectTypeKeys.slice();
      closeEditModal();
      renderStatuses();
    } catch (error) {
      showToast(error.message);
    }
    return;
  }
  if (editContext.kind === "attribute-create") {
    const payload = {
      name,
      description,
      typeKey: editType.value || "",
      minValue: Number(editMinValue.value || 0),
      maxValue: Number(editMaxValue.value || 0),
      modifiers: editModifiers,
      scoreBonuses: editBonuses,
    };
    if (payload.minValue > payload.maxValue) {
      showToast(t("attributes.edit.range.invalid", "Minimum value cannot exceed maximum."));
      return;
    }
    try {
      const result = await api("POST", `/api/drafts/${state.draftId}/attributes`, {
        name,
        typeKey: payload.typeKey,
      });
      const attributeId = String(result.id || "");
      if (!attributeId) {
        showToast(t("common.error", "Something went wrong."));
        return;
      }
      await api("POST", `/api/drafts/${state.draftId}/attributes/update`, {
        id: attributeId,
        name: payload.name,
        description: payload.description,
        typeKey: payload.typeKey,
        minValue: payload.minValue,
        maxValue: payload.maxValue,
        modifiers: payload.modifiers,
        scoreBonuses: payload.scoreBonuses,
      });
      markSaved(t("web.toast.attribute_added", "Attribute added"));
      state.lastAttributeTypeKey = payload.typeKey;
      closeEditModal();
      renderAttributes();
    } catch (error) {
      showToast(error.message);
    }
    return;
  }
  if (editContext.kind === "effect-type-create") {
    const origin = String(editContext.origin || "");
    try {
      const result = await api("POST", `/api/drafts/${state.draftId}/effect-types`, { name, description });
      markSaved(t("web.toast.effect_type_added", "Affected system added"));
      const createdName = String(result.name || name);
      const createdKey = String(result.key || createdName);
      const entry = {
        key: createdKey,
        name: createdName,
        description: String(result.description || description || ""),
        displayName: String(result.displayName || createdName),
      };
      const existingIndex = effectTypeOptions.findIndex((type) => {
        const typeKey = String(type.key || type.name || "");
        const typeName = String(type.name || type.displayName || "");
        return (
          typeKey.toLowerCase() === createdKey.toLowerCase() ||
          typeName.toLowerCase() === createdName.toLowerCase()
        );
      });
      if (existingIndex >= 0) {
        effectTypeOptions.splice(existingIndex, 1, entry);
      } else {
        effectTypeOptions.push(entry);
      }
      effectTypeOptions = sortByLabel(
        effectTypeOptions,
        (type) => type.displayName || type.name || type.key || ""
      );
      const hadSuspend = Boolean(editSuspend);
      const suspendedTypeKeys = editSuspend && editSuspend.kind === "effect"
        ? editSuspend.effect.effectTypeKeys
        : editSuspend && editSuspend.kind === "status"
          ? editSuspend.status.effectTypeKeys
          : editSuspend && editSuspend.kind === "status-create"
            ? editSuspend.status.effectTypeKeys
            : editSuspend
              ? editSuspend.effectTypeKeys
              : null;
      if (hadSuspend && Array.isArray(suspendedTypeKeys)) {
        const alreadyAdded = suspendedTypeKeys.some(
          (key) => String(key || "").toLowerCase() === createdName.toLowerCase()
        );
        if (!alreadyAdded) {
          suspendedTypeKeys.push(createdName);
        }
      }
      closeEditModal();
      if (hadSuspend) {
        return;
      }
      if (origin === "effect-types") {
        renderEffectTypes(createdKey);
        return;
      }
      renderEffects();
    } catch (error) {
      showToast(error.message);
    }
    return;
  }
  if (editContext.kind === "damage-type-create") {
    try {
      const result = await api("POST", `/api/drafts/${state.draftId}/damage-types`, { name, description });
      markSaved(t("web.toast.damage_type_added", "Damage type added"));
      damageTypeOptions = [];
      closeEditModal();
      renderDamageTypes(String(result.id || ""));
    } catch (error) {
      showToast(error.message);
    }
    return;
  }
  if (editContext.kind === "damage-type") {
    try {
      const damageTypeId = editContext.id;
      await api("POST", `/api/drafts/${state.draftId}/damage-types/update`, {
        id: damageTypeId,
        name,
        description,
      });
      markSaved(t("web.toast.damage_type_updated", "Damage type updated"));
      damageTypeOptions = [];
      closeEditModal();
      renderDamageTypes(damageTypeId);
    } catch (error) {
      showToast(error.message);
    }
    return;
  }
  if (editContext.kind === "skill-category-create") {
    try {
      const result = await api("POST", `/api/drafts/${state.draftId}/skill-categories`, { name, description });
      markSaved(t("web.toast.category_added", "Category added"));
      const entry = {
        key: result.key || "",
        name: result.name || name,
        description: result.description || description,
        displayName: result.displayName || result.name || name,
      };
      if (!skillCategoryOptions.some((category) => category.key === entry.key)) {
        skillCategoryOptions.push(entry);
      }
      skillCategoryOptions = sortByLabel(
        skillCategoryOptions,
        (category) => category.displayName || category.name || category.key || ""
      );
      renderSkillCategoryOptions(entry.key);
      closeEditModal();
    } catch (error) {
      showToast(error.message);
    }
    return;
  }
  if (editContext.kind === "status") {
    try {
      const effectTypeKeys = editEffectTypeKeys
        .map((key) => String(key || "").trim())
        .filter(Boolean);
      await api("POST", `/api/drafts/${state.draftId}/statuses/update`, {
        id: editContext.id,
        name,
        description,
        effectTypeKeys,
      });
      markSaved(t("web.toast.status_updated", "Status updated"));
      state.lastStatusEffectTypeKeys = effectTypeKeys.slice();
      closeEditModal();
      renderStatuses();
    } catch (error) {
      showToast(error.message);
    }
    return;
  }
  if (editContext.kind === "effect") {
    try {
      const effectTypeKeys = editEffectTypeKeys.slice();
      await api("POST", `/api/drafts/${state.draftId}/effects/update`, {
        id: editContext.id,
        name,
        description,
        effectTypeKeys,
        damageTypeId: editDamageType ? String(editDamageType.value || "").trim() : "",
      });
      markSaved(t("web.toast.effect_updated", "Effect updated"));
      state.lastEffectTypeKeys = effectTypeKeys.slice();
      closeEditModal();
      renderEffects();
    } catch (error) {
      showToast(error.message);
    }
    return;
  }
  if (editContext.kind === "effect-create") {
    try {
      const effectTypeKeys = editEffectTypeKeys.slice();
      const result = await api("POST", `/api/drafts/${state.draftId}/effects`, {
        name,
        description,
        effectTypeKeys,
        damageTypeId: editDamageType ? String(editDamageType.value || "").trim() : "",
      });
      markSaved(t("web.toast.effect_added", "Effect added"));
      state.lastEffectTypeKeys = effectTypeKeys.slice();
      const effectEntry = {
        id: result.id || "",
        name,
        description,
        effectTypeKeys,
        damageTypeId: editDamageType ? String(editDamageType.value || "").trim() : "",
      };
      if (editContext.origin === "skill") {
        if (!skillEffectOptions.some((effect) => effect.id === effectEntry.id)) {
          skillEffectOptions.push(effectEntry);
        }
        skillEffectOptions = sortByLabel(
          skillEffectOptions,
          (effect) => effect.name || effect.displayName || ""
        );
        renderSkillEffectOptions();
        if (!skillEffects.includes(name)) {
          skillEffects.push(name);
          renderSkillEffectList();
        }
        closeEditModal();
        return;
      }
      if (editContext.origin === "spell") {
        if (!spellEffectOptions.some((effect) => effect.id === effectEntry.id)) {
          spellEffectOptions.push(effectEntry);
        }
        spellEffectOptions = sortByLabel(
          spellEffectOptions,
          (effect) => effect.name || effect.displayName || ""
        );
        renderSpellEffectOptions();
        if (!spellEffects.includes(name)) {
          spellEffects.push(name);
          renderSpellEffectList();
        }
        closeEditModal();
        return;
      }
      if (editContext.origin === "weapon") {
        if (!weaponEffectOptions.some((effect) => effect.id === effectEntry.id)) {
          weaponEffectOptions.push(effectEntry);
        }
        weaponEffectOptions = sortByLabel(
          weaponEffectOptions,
          (effect) => effect.name || effect.displayName || ""
        );
        if (editSuspend && editSuspend.kind === "weapon") {
          if (!Array.isArray(editSuspend.weapon.effectIds)) {
            editSuspend.weapon.effectIds = [];
          }
          if (!editSuspend.weapon.effectIds.includes(effectEntry.id)) {
            editSuspend.weapon.effectIds.push(effectEntry.id);
          }
        } else if (!editWeaponEffectIds.includes(effectEntry.id)) {
          editWeaponEffectIds.push(effectEntry.id);
        }
        closeEditModal();
        return;
      }
      if (editContext.origin === "attribute") {
        if (!attributeEffectOptions.some((effect) => effect.id === effectEntry.id)) {
          attributeEffectOptions.push(effectEntry);
        }
        attributeEffectOptions = sortByLabel(
          attributeEffectOptions,
          (effect) => effect.displayName || effect.name || ""
        );
        if (editSuspend && editSuspend.kind === "attribute") {
          const threshold = Number(editSuspend.attribute.pendingThreshold || 0);
          const bonuses = editSuspend.attribute.scoreBonuses;
          const bonusEditIndex = Number(editSuspend.attribute.bonusEditIndex ?? -1);
          const duplicate = bonuses.some(
            (entry, index) => index !== bonusEditIndex
              && Number(entry.threshold || 0) === threshold
              && String(entry.effectId || "") === String(effectEntry.id || "")
          );
          if (!duplicate) {
            replaceOrAppendCollectionItem(bonuses, bonusEditIndex, { threshold, effectId: effectEntry.id });
          }
          editSuspend.attribute.bonusEditIndex = -1;
          editSuspend.attribute.selectedEffectId = "";
        }
        closeEditModal();
        return;
      }
      closeEditModal();
      renderEffects();
    } catch (error) {
      showToast(error.message);
    }
    return;
  }
  if (editContext.kind === "equipment-create") {
    try {
      const weightValue = editWeightValue ? Number(editWeightValue.value || 0) : 0;
      const weightUnit = editWeightUnit ? String(editWeightUnit.value || "").trim() : "";
      await api("POST", `/api/drafts/${state.draftId}/equipment`, {
        name,
        description,
        weightValue: Math.max(0, Math.trunc(weightValue)),
        weightUnit,
        damageTypeId: editDamageType ? String(editDamageType.value || "").trim() : "",
      });
      markSaved(t("web.toast.equipment_added", "Equipment added"));
      closeEditModal();
      renderEquipment();
    } catch (error) {
      showToast(error.message);
    }
    return;
  }
  if (editContext.kind === "weapon-create") {
    try {
      const damageDiceCount = editWeaponDamageCount ? Number(editWeaponDamageCount.value || 0) : 0;
      const damageDiceSides = editWeaponDamageSides ? Number(editWeaponDamageSides.value || 0) : 0;
      const damageDiceModifier = editWeaponDamageModifier ? Number(editWeaponDamageModifier.value || 0) : 0;
      const weightValue = editWeightValue ? Number(editWeightValue.value || 0) : 0;
      const weightUnit = editWeightUnit ? String(editWeightUnit.value || "").trim() : "";
      await api("POST", `/api/drafts/${state.draftId}/weapons`, {
        name,
        description,
        damageDiceCount,
        damageDiceSides,
        damageDiceModifier,
        weightValue: Math.max(0, Math.trunc(weightValue)),
        weightUnit,
        damageTypeId: editDamageType ? String(editDamageType.value || "").trim() : "",
        effectIds: editWeaponEffectIds.slice(),
      });
      markSaved(t("web.toast.weapon_added", "Weapon added"));
      closeEditModal();
      renderWeapons();
    } catch (error) {
      showToast(error.message);
    }
    return;
  }
  if (editContext.kind === "class-create") {
    try {
      const hitDieSides = editClassHitDieSelect ? Number(editClassHitDieSelect.value || 0) : 0;
      const hitDieModifier = editClassHitDieModifier ? Number(editClassHitDieModifier.value || 0) : 0;
      const hitDie = buildHitDie(hitDieSides, hitDieModifier);
      const skillPointsSameAllLevels = editClassSkillPointsSame ? editClassSkillPointsSame.checked : true;
      await api("POST", `/api/drafts/${state.draftId}/classes`, {
        name,
        description,
        primaryAttribute: editClassPrimary ? String(editClassPrimary.value || "").trim() : "",
        hitDie,
        skillPointsPerLevel: editClassSkillPoints ? Number(editClassSkillPoints.value || 0) : 0,
        startingMoney: editClassStartingMoney ? Number(editClassStartingMoney.value || 0) : 0,
        skillPointsSameAllLevels,
        skillPointsByLevel: editClassSkillPointsByLevel.slice(),
        classSkillIds: editClassSkillIds.slice(),
        requiredAttributeScores: editClassRequiredScores.slice(),
      });
      markSaved(t("web.toast.class_added", "Class added"));
      closeEditModal();
      renderClasses();
    } catch (error) {
      showToast(error.message);
    }
    return;
  }
  if (editContext.kind === "race-create") {
    try {
      const result = await api("POST", `/api/drafts/${state.draftId}/races`, { name });
      const raceId = String(result.id || "");
      if (!raceId) {
        showToast(t("common.error", "Something went wrong."));
        return;
      }
      await api("POST", `/api/drafts/${state.draftId}/races/update`, {
        id: raceId,
        name,
        description,
        racialSkillIds: editRaceSkillIds.slice(),
        attributeScoreLimits: editRaceAttributeLimits.slice(),
        startingMoneyModifier: editRaceStartingMoneyModifier
          ? Number(editRaceStartingMoneyModifier.value || 0)
          : 0,
      });
      markSaved(t("web.toast.race_added", "Race added"));
      closeEditModal();
      renderRaces();
    } catch (error) {
      showToast(error.message);
    }
    return;
  }
  if (editContext.kind === "equipment") {
    try {
      const weightValue = editWeightValue ? Number(editWeightValue.value || 0) : 0;
      const weightUnit = editWeightUnit ? String(editWeightUnit.value || "").trim() : "";
      await api("POST", `/api/drafts/${state.draftId}/equipment/update`, {
        id: editContext.id,
        name,
        description,
        weightValue: Math.max(0, Math.trunc(weightValue)),
        weightUnit,
        damageTypeId: editDamageType ? String(editDamageType.value || "").trim() : "",
      });
      markSaved(t("web.toast.equipment_updated", "Equipment updated"));
      closeEditModal();
      renderEquipment();
    } catch (error) {
      showToast(error.message);
    }
    return;
  }
  if (editContext.kind === "weapon") {
    try {
      const damageDiceCount = editWeaponDamageCount ? Number(editWeaponDamageCount.value || 0) : 0;
      const damageDiceSides = editWeaponDamageSides ? Number(editWeaponDamageSides.value || 0) : 0;
      const damageDiceModifier = editWeaponDamageModifier ? Number(editWeaponDamageModifier.value || 0) : 0;
      const weightValue = editWeightValue ? Number(editWeightValue.value || 0) : 0;
      const weightUnit = editWeightUnit ? String(editWeightUnit.value || "").trim() : "";
      await api("POST", `/api/drafts/${state.draftId}/weapons/update`, {
        id: editContext.id,
        name,
        description,
        damageDiceCount,
        damageDiceSides,
        damageDiceModifier,
        weightValue: Math.max(0, Math.trunc(weightValue)),
        weightUnit,
        damageTypeId: editDamageType ? String(editDamageType.value || "").trim() : "",
        effectIds: editWeaponEffectIds.slice(),
      });
      markSaved(t("web.toast.weapon_updated", "Weapon updated"));
      closeEditModal();
      renderWeapons();
    } catch (error) {
      showToast(error.message);
    }
    return;
  }
  if (editContext.kind === "class") {
    try {
      const hitDieSides = editClassHitDieSelect ? Number(editClassHitDieSelect.value || 0) : 0;
      const hitDieModifier = editClassHitDieModifier ? Number(editClassHitDieModifier.value || 0) : 0;
      const hitDie = buildHitDie(hitDieSides, hitDieModifier);
      const skillPointsSameAllLevels = editClassSkillPointsSame ? editClassSkillPointsSame.checked : true;
      await api("POST", `/api/drafts/${state.draftId}/classes/update`, {
        id: editContext.id,
        name,
        description,
        primaryAttribute: editClassPrimary ? String(editClassPrimary.value || "").trim() : "",
        hitDie,
        skillPointsPerLevel: editClassSkillPoints ? Number(editClassSkillPoints.value || 0) : 0,
        startingMoney: editClassStartingMoney ? Number(editClassStartingMoney.value || 0) : 0,
        skillPointsSameAllLevels,
        skillPointsByLevel: editClassSkillPointsByLevel.slice(),
        classSkillIds: editClassSkillIds.slice(),
        requiredAttributeScores: editClassRequiredScores.slice(),
      });
      markSaved(t("web.toast.class_updated", "Class updated"));
      closeEditModal();
      renderClasses();
    } catch (error) {
      showToast(error.message);
    }
    return;
  }
  if (editContext.kind === "race") {
    try {
      await api("POST", `/api/drafts/${state.draftId}/races/update`, {
        id: editContext.id,
        name,
        description,
        racialSkillIds: editRaceSkillIds.slice(),
        attributeScoreLimits: editRaceAttributeLimits.slice(),
        startingMoneyModifier: editRaceStartingMoneyModifier
          ? Number(editRaceStartingMoneyModifier.value || 0)
          : 0,
      });
      markSaved(t("web.toast.race_updated", "Race updated"));
      closeEditModal();
      renderRaces();
    } catch (error) {
      showToast(error.message);
    }
    return;
  }

  const payload = {
    id: editContext.id,
    name,
    description,
    typeKey: editType.value || "",
    minValue: Number(editMinValue.value || 0),
    maxValue: Number(editMaxValue.value || 0),
    modifiers: editModifiers,
    scoreBonuses: editBonuses,
  };
  if (payload.minValue > payload.maxValue) {
    showToast(t("attributes.edit.range.invalid", "Minimum value cannot exceed maximum."));
    return;
  }
  try {
    await api("POST", `/api/drafts/${state.draftId}/attributes/update`, payload);
    markSaved(t("web.toast.attribute_updated", "Attribute updated"));
    state.lastAttributeTypeKey = payload.typeKey || "";
    closeEditModal();
    renderAttributes();
  } catch (error) {
    showToast(error.message);
  }
});

if (skillCancel) {
  skillCancel.addEventListener("click", () => {
    closeSkillModal();
  });
}

if (skillEffectAdd) {
  skillEffectAdd.addEventListener("click", () => {
    const name = String(skillEffectSelect.value || "").trim();
    if (!name || name === NEW_INLINE_OPTION) {
      return;
    }
    const duplicate = skillEffects.some((entry, index) => entry === name && index !== skillEffectIndex);
    if (!duplicate) {
      replaceOrAppendCollectionItem(skillEffects, skillEffectIndex, name);
      skillEffectIndex = -1;
      skillEffectSelect.value = "";
      setCollectionCommitMode(skillEffectAdd, false);
      renderSkillEffectList();
    }
  });
}

if (skillEffectList) {
  skillEffectList.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-edit-skill-effect], button[data-remove-skill-effect]");
    if (!button) {
      return;
    }
    const index = Number(button.dataset.editSkillEffect ?? button.dataset.removeSkillEffect);
    if (Number.isNaN(index) || index < 0) {
      return;
    }
    if (button.hasAttribute("data-edit-skill-effect")) {
      skillEffectIndex = index;
      skillEffectSelect.value = skillEffects[index] || "";
      setCollectionCommitMode(skillEffectAdd, true);
      skillEffectSelect.focus();
      return;
    }
    const confirmed = await showConfirm(
      t("common.remove.confirm", "Remove selected item?"),
      t("common.remove", "Remove")
    );
    if (!confirmed) {
      return;
    }
    skillEffects.splice(index, 1);
    skillEffectIndex = -1;
    setCollectionCommitMode(skillEffectAdd, false);
    renderSkillEffectList();
  });
}

if (skillClassLimitAdd) {
  skillClassLimitAdd.addEventListener("click", () => {
    const classId = String(skillClassLimitSelect ? skillClassLimitSelect.value : "").trim();
    if (!classId) {
      return;
    }
    const duplicate = skillLimitedClassIds.some((entry, index) => entry === classId && index !== skillClassLimitIndex);
    if (!duplicate) {
      replaceOrAppendCollectionItem(skillLimitedClassIds, skillClassLimitIndex, classId);
      skillClassLimitIndex = -1;
      skillClassLimitSelect.value = "";
      setCollectionCommitMode(skillClassLimitAdd, false);
      renderSkillClassLimitList();
    }
  });
}

if (skillClassLimitList) {
  skillClassLimitList.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-edit-skill-class-limit], button[data-remove-skill-class-limit]");
    if (!button) {
      return;
    }
    const index = Number(button.dataset.editSkillClassLimit ?? button.dataset.removeSkillClassLimit);
    if (Number.isNaN(index) || index < 0) {
      return;
    }
    if (button.hasAttribute("data-edit-skill-class-limit")) {
      skillClassLimitIndex = index;
      skillClassLimitSelect.value = skillLimitedClassIds[index] || "";
      setCollectionCommitMode(skillClassLimitAdd, true);
      skillClassLimitSelect.focus();
      return;
    }
    const confirmed = await showConfirm(
      t("common.remove.confirm", "Remove selected item?"),
      t("common.remove", "Remove")
    );
    if (!confirmed) {
      return;
    }
    skillLimitedClassIds.splice(index, 1);
    skillClassLimitIndex = -1;
    setCollectionCommitMode(skillClassLimitAdd, false);
    renderSkillClassLimitList();
  });
}

if (skillRaceLimitAdd) {
  skillRaceLimitAdd.addEventListener("click", () => {
    const raceId = String(skillRaceLimitSelect ? skillRaceLimitSelect.value : "").trim();
    if (!raceId) {
      return;
    }
    const duplicate = skillLimitedRaceIds.some((entry, index) => entry === raceId && index !== skillRaceLimitIndex);
    if (!duplicate) {
      replaceOrAppendCollectionItem(skillLimitedRaceIds, skillRaceLimitIndex, raceId);
      skillRaceLimitIndex = -1;
      skillRaceLimitSelect.value = "";
      setCollectionCommitMode(skillRaceLimitAdd, false);
      renderSkillRaceLimitList();
    }
  });
}

if (skillRaceLimitList) {
  skillRaceLimitList.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-edit-skill-race-limit], button[data-remove-skill-race-limit]");
    if (!button) {
      return;
    }
    const index = Number(button.dataset.editSkillRaceLimit ?? button.dataset.removeSkillRaceLimit);
    if (Number.isNaN(index) || index < 0) {
      return;
    }
    if (button.hasAttribute("data-edit-skill-race-limit")) {
      skillRaceLimitIndex = index;
      skillRaceLimitSelect.value = skillLimitedRaceIds[index] || "";
      setCollectionCommitMode(skillRaceLimitAdd, true);
      skillRaceLimitSelect.focus();
      return;
    }
    const confirmed = await showConfirm(
      t("common.remove.confirm", "Remove selected item?"),
      t("common.remove", "Remove")
    );
    if (!confirmed) {
      return;
    }
    skillLimitedRaceIds.splice(index, 1);
    skillRaceLimitIndex = -1;
    setCollectionCommitMode(skillRaceLimitAdd, false);
    renderSkillRaceLimitList();
  });
}

if (spellEffectAdd) {
  spellEffectAdd.addEventListener("click", () => {
    const name = String(spellEffectSelect ? spellEffectSelect.value : "").trim();
    if (!name || name === NEW_INLINE_OPTION) {
      return;
    }
    const duplicate = spellEffects.some((entry, index) => entry === name && index !== spellEffectIndex);
    if (!duplicate) {
      replaceOrAppendCollectionItem(spellEffects, spellEffectIndex, name);
      spellEffectIndex = -1;
      spellEffectSelect.value = "";
      setCollectionCommitMode(spellEffectAdd, false);
      renderSpellEffectList();
    }
  });
}

if (spellEffectList) {
  spellEffectList.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-edit-spell-effect], button[data-remove-spell-effect]");
    if (!button) {
      return;
    }
    const index = Number(button.dataset.editSpellEffect ?? button.dataset.removeSpellEffect);
    if (Number.isNaN(index) || index < 0) {
      return;
    }
    if (button.hasAttribute("data-edit-spell-effect")) {
      spellEffectIndex = index;
      spellEffectSelect.value = spellEffects[index] || "";
      setCollectionCommitMode(spellEffectAdd, true);
      spellEffectSelect.focus();
      return;
    }
    const confirmed = await showConfirm(
      t("common.remove.confirm", "Remove selected item?"),
      t("common.remove", "Remove")
    );
    if (!confirmed) {
      return;
    }
    spellEffects.splice(index, 1);
    spellEffectIndex = -1;
    setCollectionCommitMode(spellEffectAdd, false);
    renderSpellEffectList();
  });
}

if (skillEffectSelect) {
  skillEffectSelect.addEventListener("change", async () => {
    if (skillEffectSelect.value !== NEW_INLINE_OPTION) {
      return;
    }
    skillEffectSelect.value = "";
    await openEffectCreateModal("skill", "", "", state.lastEffectTypeKeys);
  });
}

if (skillCategorySelect) {
  skillCategorySelect.addEventListener("change", () => {
    if (skillCategorySelect.value !== NEW_INLINE_OPTION) {
      skillCategorySelect.dataset.previousValue = skillCategorySelect.value;
      return;
    }
    skillCategorySelect.value = skillCategorySelect.dataset.previousValue || "";
    openSkillCategoryCreateModal("skill");
  });
}

if (skillSave) {
  skillSave.addEventListener("click", async () => {
    if (!skillContext) {
      closeSkillModal();
      return;
    }
    const name = String(skillNameInput.value || "").trim();
    if (!name) {
      showToast(t("common.name.required", "Name is required."));
      return;
    }
    try {
      const payload = {
        id: skillContext.id,
        name,
        description: String(skillDescriptionInput.value || "").trim(),
        category: String(skillCategorySelect ? skillCategorySelect.value : "").trim(),
        relatedAbility: String(skillAbilitySelect.value || "").trim(),
        trainedOnly: Boolean(skillTrainedOnly.checked),
        armorCheckPenalty: Number(skillArmorPenalty.value || 0),
        startingMoneyModifier: skillStartingMoneyModifier
          ? Number(skillStartingMoneyModifier.value || 0)
          : 0,
        effectNames: skillEffects.slice(),
        limitedToClasses: skillLimitedClassIds.slice(),
        limitedToRaces: skillLimitedRaceIds.slice(),
      };
      if (skillContext.mode === "create") {
        const createResult = await api("POST", `/api/drafts/${state.draftId}/skills`, {
          name,
          description: payload.description,
        });
        const skillId = String(createResult.id || "");
        if (!skillId) {
          showToast(t("common.error", "Something went wrong."));
          return;
        }
        payload.id = skillId;
        await api("POST", `/api/drafts/${state.draftId}/skills/update`, payload);
        markSaved(t("web.toast.skill_added", "Skill added"));
        state.lastSkillCategoryKey = payload.category || "";
        const entry = {
          id: skillId,
          name: payload.name,
          description: payload.description,
          category: payload.category,
          relatedAbility: payload.relatedAbility,
          trainedOnly: payload.trainedOnly,
          armorCheckPenalty: payload.armorCheckPenalty,
          startingMoneyModifier: payload.startingMoneyModifier,
          effectNames: payload.effectNames.slice(),
          limitedToClasses: payload.limitedToClasses.slice(),
          limitedToRaces: payload.limitedToRaces.slice(),
        };
      if (skillContext.origin === "race") {
        if (!raceSkillOptions.some((skill) => skill.id === entry.id)) {
          raceSkillOptions.push(entry);
        }
          raceSkillOptions = sortByLabel(
            raceSkillOptions,
            (skill) => skill.displayName || skill.name || ""
          );
          populateRaceTraitSelect();
          if (entry.id && !editRaceSkillIds.includes(entry.id)) {
            editRaceSkillIds.push(entry.id);
            renderRaceTraitList();
          }
        closeSkillModal();
        return;
      }
      if (skillContext.origin === "class") {
        if (!classSkillOptions.some((skill) => skill.id === entry.id)) {
          classSkillOptions.push(entry);
        }
        classSkillOptions = sortByLabel(
          classSkillOptions,
          (skill) => skill.displayName || skill.name || ""
        );
        populateClassSkillSelect();
        if (entry.id && !editClassSkillIds.includes(entry.id)) {
          editClassSkillIds.push(entry.id);
          renderClassSkillList();
        }
        closeSkillModal();
        return;
      }
      closeSkillModal();
      renderSkills();
      return;
      }
      await api("POST", `/api/drafts/${state.draftId}/skills/update`, payload);
      markSaved(t("web.toast.skill_updated", "Skill updated"));
      state.lastSkillCategoryKey = payload.category || "";
      closeSkillModal();
      renderSkills();
    } catch (error) {
      showToast(error.message);
    }
  });
}

if (spellCancel) {
  spellCancel.addEventListener("click", () => {
    closeSpellModal();
  });
}

if (spellEffectSelect) {
  spellEffectSelect.addEventListener("change", async () => {
    if (spellEffectSelect.value !== NEW_INLINE_OPTION) {
      return;
    }
    spellEffectSelect.value = "";
    await openEffectCreateModal("spell", "", "", state.lastEffectTypeKeys);
  });
}

if (spellSave) {
  spellSave.addEventListener("click", async () => {
    if (!spellContext) {
      closeSpellModal();
      return;
    }
    const name = String(spellNameInput.value || "").trim();
    if (!name) {
      showToast(t("common.name.required", "Name is required."));
      return;
    }
    const payload = {
      name,
      description: String(spellDescriptionInput.value || "").trim(),
      school: String(spellSchoolInput.value || "").trim(),
      level: Number(spellLevelInput.value || 0),
      castingTime: String(spellCastingInput.value || "").trim(),
      range: String(spellRangeInput.value || "").trim(),
      duration: String(spellDurationInput.value || "").trim(),
      damageTypeId: spellDamageTypeSelect ? String(spellDamageTypeSelect.value || "").trim() : "",
      effectNames: spellEffects.slice(),
    };
    try {
      if (spellContext.mode === "create") {
        const createResult = await api("POST", `/api/drafts/${state.draftId}/spells`, { name });
        const spellId = String(createResult.id || "");
        if (!spellId) {
          showToast(t("common.error", "Something went wrong."));
          return;
        }
        await api("POST", `/api/drafts/${state.draftId}/spells/update`, { id: spellId, ...payload });
        markSaved(t("web.toast.spell_added", "Spell added"));
        closeSpellModal();
        renderSpells();
        return;
      }
      payload.id = spellContext.id;
      await api("POST", `/api/drafts/${state.draftId}/spells/update`, payload);
      markSaved(t("web.toast.spell_updated", "Spell updated"));
      closeSpellModal();
      renderSpells();
    } catch (error) {
      showToast(error.message);
    }
  });
}

logoutBtn.addEventListener("click", async () => {
  try {
    await api("POST", "/api/logout");
  } catch (error) {
    showToast(error.message);
  }
  state.sessionToken = "";
  clearStoredSessionToken();
  state.accountName = "";
  state.legacyGuest = false;
  state.admin = false;
  state.draftId = "";
  state.systemNames = {};
  applyTutorialVisitedScreens([]);
  resetVisited();
  setStep("beta-application");
  setLoggedIn(false);
  renderClosedBetaApplication();
});

if (sidebarNav) {
  sidebarNav.addEventListener("click", (event) => {
    const tutorialButton = event.target.closest("button[data-tutorial-screen]");
    if (tutorialButton) {
      openTutorialPopup(tutorialButton.dataset.tutorialScreen);
      return;
    }
    const button = event.target.closest("button[data-step]");
    if (!button) {
      return;
    }
    navigateToStep(button.dataset.step);
  });
}

window.addEventListener("popstate", (event) => {
  if (!historyReady) {
    return;
  }
  const target = resolveHistoryStep(event);
  if (!target || target === state.step) {
    return;
  }
  historyLocked = true;
  navigateToHistoryStep(target);
  historyLocked = false;
});

downloadBtn.addEventListener("click", async () => {
  if (state.mode === "chargen") {
    await downloadCharGenDraft();
    return;
  }
  await downloadDraft();
});

async function downloadDraft() {
  if (!ensureDraft()) {
    return;
  }
  try {
    const headers = {};
    if (state.sessionToken) {
      headers.Authorization = `Bearer ${state.sessionToken}`;
    }
    const response = await fetch(`/api/drafts/${state.draftId}/export`, {
      headers,
      cache: "no-store",
    });
    if (!response.ok) {
      const payload = await readApiJson(response, `/api/drafts/${state.draftId}/export`);
      throw new Error(payload.error || t("web.error.download_failed", "Download failed"));
    }
    const blob = await response.blob();
    const disposition = response.headers.get("Content-Disposition") || "";
    const match = disposition.match(/filename=\"([^\"]+)\"/);
    const filename = match ? match[1] : "ruleset.gmrf";
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
    markSaved(t("web.toast.downloaded", "Downloaded"));
  } catch (error) {
    showToast(error.message);
  }
}

async function boot() {
  setupSelectAllOnFocus();
  trackTransientStacking();
  const localSessionToken = consumeLocalSessionTokenFromUrl();
  let localModeDetected = Boolean(localSessionToken);
  state.sessionToken = localSessionToken || readStoredSessionToken();
  await loadLocalization(state.locale);
  const verifiedEmail = readVerifiedEmailFromUrl();
  const passwordReset = readPasswordResetFromUrl();
  if (passwordReset.email && passwordReset.token) {
    clearPasswordResetFromUrl();
    state.sessionToken = "";
    clearStoredSessionToken();
    state.accountName = "";
    state.legacyGuest = false;
    state.admin = false;
    applyTutorialVisitedScreens([]);
    setLoggedIn(false);
    renderCreatePassword(passwordReset.email, passwordReset.token);
    showToast(t("web.login.reset_verified", "Password reset verified. Create a new password to finish."));
    ensureHistoryReady();
    return;
  }
  try {
    let session = await api("GET", "/api/session");
    localModeDetected = localModeDetected || !!session.localMode;
    if (!session.authenticated && session.localMode) {
      state.sessionToken = "";
      clearStoredSessionToken();
      state.accountName = "";
      state.legacyGuest = false;
      state.admin = false;
      state.localMode = true;
      applyTutorialVisitedScreens([]);
      setLoggedIn(false);
      renderLocalAccessRequired();
      ensureHistoryReady();
      return;
    }
    if (session.authenticated) {
      state.sessionToken = session.token || state.sessionToken;
      storeSessionToken(state.sessionToken);
      state.localMode = !!session.localMode;
      setLoggedIn(true);
      state.accountName = session.username || "";
      state.legacyGuest = !!session.legacyGuest;
      state.admin = !!session.admin;
      applyTutorialVisitedScreens(session.tutorialVisitedScreens || []);
      if (session.draftLocale) {
        state.locale = normalizeLocale(session.draftLocale);
        await loadLocalization(state.locale);
      }
      state.draftId = session.draftId || "";
      applyCompletedStages(session.completedStages || []);
      await loadSystemNames();
      const targetStep = resolveHistoryStep();
      if (targetStep && targetStep !== "login") {
        navigateToHistoryStep(targetStep);
      } else {
        state.step = "splash";
        renderHome();
      }
      ensureHistoryReady();
    } else {
      state.sessionToken = "";
      clearStoredSessionToken();
      state.accountName = "";
      state.legacyGuest = false;
      state.admin = false;
      state.localMode = false;
      applyTutorialVisitedScreens([]);
      setLoggedIn(false);
      if (verifiedEmail) {
        clearVerifiedEmailFromUrl();
        renderCreatePassword(verifiedEmail);
        showToast(t("web.login.email_verified", "Email verified. Create your password to finish account setup."));
      } else {
        renderClosedBetaApplication();
      }
      ensureHistoryReady();
    }
  } catch (error) {
    state.sessionToken = "";
    clearStoredSessionToken();
    state.accountName = "";
    state.legacyGuest = false;
    state.admin = false;
    state.localMode = false;
    applyTutorialVisitedScreens([]);
    setLoggedIn(false);
    if (localModeDetected) {
      state.localMode = true;
      renderLocalAccessRequired(error.message);
    } else if (verifiedEmail) {
      clearVerifiedEmailFromUrl();
      renderCreatePassword(verifiedEmail);
      showToast(t("web.login.email_verified", "Email verified. Create your password to finish account setup."));
    } else {
      renderClosedBetaApplication();
    }
    ensureHistoryReady();
  }
}

function renderLocalAccessRequired(message = "") {
  setMode("home");
  resetVisited();
  setStep("login");
  const safeMessage = String(message || "").trim();
  view.innerHTML = `
    <section class="panel">
      <h1>Local Development Access</h1>
      <p>Close this tab and start the site with <code>.\\run-local.ps1</code>. The launcher verifies the private local key and supplies a browser session.</p>
      ${safeMessage ? `<p class="field-hint">${escapeHtml(safeMessage)}</p>` : ""}
      <div class="actions-row">
        <div class="right">
          <button class="btn" id="localAccessReload" type="button">Try Again</button>
        </div>
      </div>
    </section>
  `;
  document.getElementById("localAccessReload").addEventListener("click", () => window.location.reload());
}

function readVerifiedEmailFromUrl() {
  const params = new URLSearchParams(window.location.search || "");
  return String(params.get("verifiedEmail") || "").trim();
}

function clearVerifiedEmailFromUrl() {
  const url = new URL(window.location.href);
  if (!url.searchParams.has("verifiedEmail")) {
    return;
  }
  url.searchParams.delete("verifiedEmail");
  window.history.replaceState({}, "", `${url.pathname}${url.search}${url.hash}`);
}

function readPasswordResetFromUrl() {
  const params = new URLSearchParams(window.location.search || "");
  return {
    email: String(params.get("resetEmail") || "").trim(),
    token: String(params.get("resetToken") || "").trim(),
  };
}

function clearPasswordResetFromUrl() {
  const url = new URL(window.location.href);
  if (!url.searchParams.has("resetEmail") && !url.searchParams.has("resetToken")) {
    return;
  }
  url.searchParams.delete("resetEmail");
  url.searchParams.delete("resetToken");
  window.history.replaceState({}, "", `${url.pathname}${url.search}${url.hash}`);
}

function renderClosedBetaApplication() {
  setMode("home");
  resetVisited();
  setStep("beta-application");
  view.innerHTML = `
    <section class="panel">
      <h1>${t("web.beta.title", "Apply for the Closed Beta")}</h1>
      <p>${t(
        "web.beta.description",
        "Review the NDA, enter your email, and submit your closed beta application."
      )}</p>
      <p class="field-hint">${t(
        "web.beta.limits",
        "Closed beta access is limited to 10 accepted testers. To support shared households, this form accepts up to two applications from the same connection per day."
      )}</p>
      <div class="field">
        <label for="closedBetaNda">${t("web.beta.nda", "NDA")}</label>
        <p class="field-hint">${t("web.beta.review_full", "Read and review the full Agreement before continuing.")}</p>
        <div class="nda-scroll-box">
          <textarea id="closedBetaNda" readonly></textarea>
        </div>
      </div>
      <div class="grid three">
        <div class="field checkbox-field">
          <label class="checkbox-label" for="closedBetaAgree">
            <input type="checkbox" id="closedBetaAgree" disabled>
            <span>${t("web.beta.agree", "I have reviewed and agree to the Beta Access NDA v1")}</span>
          </label>
        </div>
        <div class="field">
          <label for="closedBetaFullName">${t("web.beta.full_name", "Legal Full Name")}</label>
          <input type="text" id="closedBetaFullName" autocomplete="name" maxlength="120" required>
        </div>
        <div class="field">
          <label for="closedBetaEmail">${t("web.beta.email", "Email")}</label>
          <input type="email" id="closedBetaEmail" autocomplete="email" required>
        </div>
      </div>
      <div class="actions-row">
        <div class="left">
          <button class="btn ghost" id="closedBetaLogin" type="button">
            ${t("web.beta.proceed_login", "Already registered? Proceed to login")}
          </button>
        </div>
        <div class="right">
          <button class="btn" id="closedBetaSubmit" type="button">${t("web.beta.submit", "Submit")}</button>
        </div>
      </div>
    </section>
  `;
  const betaFullNameInput = document.getElementById("closedBetaFullName");
  const betaEmailInput = document.getElementById("closedBetaEmail");
  const betaAgreeInput = document.getElementById("closedBetaAgree");
  const betaNdaInput = document.getElementById("closedBetaNda");
  const betaSubmitButton = document.getElementById("closedBetaSubmit");
  let ndaScrollCompletedAt = "";
  let ndaAcceptedAt = "";
  betaNdaInput.value = t("web.loading", "Loading...");
  const hasFullName = () => String(betaFullNameInput.value || "").trim().length >= 2;
  const hasValidEmail = () => Boolean(String(betaEmailInput.value || "").trim()) && betaEmailInput.checkValidity();
  const syncBetaSubmitState = () => {
    betaSubmitButton.disabled = !betaAgreeInput.checked || !hasFullName() || !hasValidEmail();
  };
  const syncNdaScrollState = () => {
    const atBottom = betaNdaInput.scrollTop + betaNdaInput.clientHeight >= betaNdaInput.scrollHeight - 4;
    if (atBottom) {
      if (!ndaScrollCompletedAt) {
        ndaScrollCompletedAt = new Date().toISOString();
      }
      betaAgreeInput.disabled = false;
      betaAgreeInput.title = "";
    } else {
      betaAgreeInput.disabled = true;
      betaAgreeInput.title = t("web.beta.scroll_required", "Read and review the full Agreement before checking this box.");
    }
    syncBetaSubmitState();
  };
  const loadClosedBetaNda = async () => {
    try {
      const result = await api("GET", "/api/legal/nda");
      betaNdaInput.value = String(result.text || "");
      betaNdaInput.scrollTop = 0;
      betaAgreeInput.checked = false;
      betaAgreeInput.disabled = true;
      ndaScrollCompletedAt = "";
      ndaAcceptedAt = "";
      syncNdaScrollState();
    } catch (error) {
      betaNdaInput.value = t("web.beta.nda_load_failed", "NDA text could not be loaded. Refresh and try again.");
      betaAgreeInput.checked = false;
      betaAgreeInput.disabled = true;
      ndaScrollCompletedAt = "";
      ndaAcceptedAt = "";
      syncBetaSubmitState();
      showToast(error.message);
    }
  };
  document.getElementById("closedBetaLogin").addEventListener("click", () => renderLogin());
  betaNdaInput.addEventListener("scroll", syncNdaScrollState);
  betaFullNameInput.addEventListener("input", syncBetaSubmitState);
  betaEmailInput.addEventListener("input", syncBetaSubmitState);
  betaAgreeInput.addEventListener("change", () => {
    ndaAcceptedAt = betaAgreeInput.checked ? new Date().toISOString() : "";
    syncBetaSubmitState();
  });
  syncBetaSubmitState();
  loadClosedBetaNda();
  betaSubmitButton.addEventListener("click", async () => {
    if (!betaAgreeInput.checked) {
      showToast(t("web.beta.must_agree", "Step 1 failed: you must agree to the NDA before submitting."));
      return;
    }
    const fullName = String(betaFullNameInput.value || "").trim();
    if (!fullName) {
      showToast(t("web.beta.full_name_required", "Step 2 failed: legal full name is required."));
      return;
    }
    const email = String(betaEmailInput.value || "").trim();
    if (!email) {
      showToast(t("web.login.email_required", "Step 2 failed: email is required."));
      return;
    }
    betaSubmitButton.disabled = true;
    betaSubmitButton.textContent = t("web.beta.submitting", "Submitting...");
    showToast(t("web.beta.submit_start", "Step 3: creating beta account and sending email."));
    try {
      const result = await api("POST", "/api/accounts", {
        fullName,
        email,
        ndaAccepted: betaAgreeInput.checked,
        ndaVersion: "v1",
        ndaScrollCompletedAt,
        ndaAcceptedAt,
      });
      if (result.emailSent) {
        showToast(t("web.beta.email_sent", "Step 4 complete: verification email sent. Click the email link to create your account."));
      } else {
        showToast(t("web.beta.email_skipped", "Step 4 skipped: email API key is not configured, so no account was created."));
      }
      syncBetaSubmitState();
      betaSubmitButton.textContent = t("web.beta.submit", "Submit");
    } catch (error) {
      showToast(`${t("web.beta.submit_failed", "Beta application failed")}: ${error.message}`);
      syncBetaSubmitState();
      betaSubmitButton.textContent = t("web.beta.submit", "Submit");
    }
  });
}

function renderLogin(email = "") {
  setMode("home");
  resetVisited();
  setStep("login");
  view.innerHTML = `
    <section class="panel">
      <h1>${t("web.login.title", "Account Access")}</h1>
      <p>${t(
        "web.login.email_description",
        "Enter your closed beta email to continue."
      )}</p>
      <div class="field">
        <label for="loginEmail">${t("web.login.email", "Email")}</label>
        <input type="email" id="loginEmail" autocomplete="email" value="${escapeHtml(email)}">
      </div>
      <div class="actions-row">
        <div class="left">
          <button class="btn danger ghost" id="loginDeleteAccount" type="button">${t("web.account_delete.opt_out_button", "Delete Account / Opt Out")}</button>
          <button class="btn ghost" id="loginBackToBeta" type="button">${t("setup.back", "Back")}</button>
        </div>
        <div class="right">
          <button class="btn" id="loginContinue" type="button">${t("common.continue", "Continue")}</button>
        </div>
      </div>
    </section>
  `;

  const emailInput = document.getElementById("loginEmail");
  const continueButton = document.getElementById("loginContinue");
  const continueLogin = async () => {
    const safeEmail = String(emailInput.value || "").trim();
    if (!safeEmail) {
      showToast(t("web.login.email_required", "Email is required."));
      return;
    }
    try {
      const result = await api("POST", "/api/accounts/lookup", { email: safeEmail });
      if (!result.exists) {
        showToast(t("web.login.not_beta", "This email is not on the closed beta list."));
        return;
      }
      if (result.locked) {
        renderPasswordLogin(result.email || safeEmail, true);
        return;
      }
      if (result.passwordSet) {
        renderPasswordLogin(result.email || safeEmail);
        return;
      }
      renderCreatePassword(result.email || safeEmail);
    } catch (error) {
      showToast(error.message);
    }
  };

  continueButton.addEventListener("click", continueLogin);
  document.getElementById("loginDeleteAccount").addEventListener("click", () => {
    openDeleteAccountModal(emailInput.value.trim());
  });
  document.getElementById("loginBackToBeta").addEventListener("click", renderClosedBetaApplication);
  emailInput.addEventListener("keypress", (event) => {
    if (event.key === "Enter") {
      continueLogin();
    }
  });
  window.requestAnimationFrame(() => {
    emailInput.focus();
  });
}

function renderCreatePassword(email, resetToken = "") {
  setMode("home");
  resetVisited();
  setStep("login");
  const safeEmail = String(email || "").trim();
  const safeResetToken = String(resetToken || "").trim();
  const isReset = Boolean(safeResetToken);
  view.innerHTML = `
    <section class="panel">
      <h1>${isReset ? t("web.login.reset_password_title", "Reset Password") : t("web.login.create_password_title", "Create Password")}</h1>
      <p>${escapeHtml(safeEmail)}</p>
      ${isReset ? `<p class="field-hint">${t("web.login.reset_password_hint", "Choose a new password for this account.")}</p>` : ""}
      <div class="grid two">
        <div class="field">
          <label for="newPassword">${t("web.login.password", "Password")}</label>
          <input type="password" id="newPassword" autocomplete="new-password">
        </div>
        <div class="field">
          <label for="confirmPassword">${t("web.login.confirm_password", "Confirm Password")}</label>
          <input type="password" id="confirmPassword" autocomplete="new-password">
        </div>
      </div>
      <p class="field-hint">${t("web.login.password_requirement", "Password must be at least 8 characters.")}</p>
      <div class="actions-row">
        <div class="left">
          <button class="btn danger ghost" id="createPasswordDeleteAccount" type="button">${t("web.account_delete.opt_out_button", "Delete Account / Opt Out")}</button>
          <button class="btn ghost" id="createPasswordBack" type="button">${t("setup.back", "Back")}</button>
        </div>
        <div class="right">
          <button class="btn" id="createPasswordSubmit" type="button">${isReset ? t("web.login.reset_password", "Reset Password") : t("web.login.create_password", "Create Password")}</button>
        </div>
      </div>
    </section>
  `;

  const passwordInput = document.getElementById("newPassword");
  const confirmInput = document.getElementById("confirmPassword");
  const createPassword = async () => {
    try {
      const result = await api("POST", "/api/accounts/password", {
        email: safeEmail,
        password: passwordInput.value,
        confirmPassword: confirmInput.value,
        resetToken: safeResetToken,
      });
      finishLogin(result);
    } catch (error) {
      showToast(error.message);
    }
  };
  document.getElementById("createPasswordSubmit").addEventListener("click", createPassword);
  document.getElementById("createPasswordDeleteAccount").addEventListener("click", () => openDeleteAccountModal(safeEmail));
  document.getElementById("createPasswordBack").addEventListener("click", () => {
    if (isReset) {
      renderPasswordLogin(safeEmail);
      return;
    }
    renderLogin(safeEmail);
  });
  confirmInput.addEventListener("keypress", (event) => {
    if (event.key === "Enter") {
      createPassword();
    }
  });
  window.requestAnimationFrame(() => {
    passwordInput.focus();
  });
}

function renderPasswordLogin(email, locked = false) {
  setMode("home");
  resetVisited();
  setStep("login");
  const safeEmail = String(email || "").trim();
  const isLocked = Boolean(locked);
  view.innerHTML = `
    <section class="panel">
      <h1>${t("web.login.password_title", "Enter Password")}</h1>
      <p>${escapeHtml(safeEmail)}</p>
      ${isLocked ? `<p class="field-hint">${t("web.login.locked_reset_hint", "This account is locked after repeated failed login attempts. Use Forgot Password to verify by email and choose a new password.")}</p>` : ""}
      <div class="field">
        <label for="password">${t("web.login.password", "Password")}</label>
        <input type="password" id="password" autocomplete="current-password">
      </div>
      <div class="actions-row">
        <div class="left">
          <button class="btn danger ghost" id="deleteAccountBtn" type="button">${t("web.account_delete.permanent_button", "Permanently Delete Account")}</button>
          <button class="btn ghost" id="forgotPasswordBtn" type="button">${t("web.login.forgot_password", "Forgot Password?")}</button>
          <button class="btn ghost" id="passwordBack" type="button">${t("setup.back", "Back")}</button>
        </div>
        <div class="right">
          <button class="btn" id="loginBtn" type="button">${t("web.login.sign_in", "Sign In")}</button>
        </div>
      </div>
    </section>
  `;

  const passwordInput = document.getElementById("password");
  const submit = async () => {
    try {
      const result = await api("POST", "/api/login", {
        username: safeEmail,
        password: passwordInput.value,
      });
      finishLogin(result);
    } catch (error) {
      if (error.code === "account_locked") {
        openLockedAccountModal(safeEmail);
        return;
      }
      showToast(error.message);
    }
  };

  document.getElementById("loginBtn").addEventListener("click", submit);
  document.getElementById("passwordBack").addEventListener("click", () => renderLogin(safeEmail));
  document.getElementById("deleteAccountBtn").addEventListener("click", () => openDeleteAccountModal(safeEmail));
  document.getElementById("forgotPasswordBtn").addEventListener("click", async () => {
    const resetButton = document.getElementById("forgotPasswordBtn");
    await requestPasswordResetEmail(safeEmail, resetButton);
  });
  passwordInput.addEventListener("keypress", (event) => {
    if (event.key === "Enter") {
      submit();
    }
  });
  window.requestAnimationFrame(() => {
    passwordInput.focus();
  });
}

function finishLogin(result) {
  state.sessionToken = result.token || "";
  storeSessionToken(state.sessionToken);
  state.accountName = result.username || "";
  state.legacyGuest = !!result.legacyGuest;
  state.admin = !!result.admin;
  state.localMode = !!result.localMode;
  applyTutorialVisitedScreens(result.tutorialVisitedScreens || []);
  setLoggedIn(true);
  state.step = "splash";
  renderHome();
}

async function renderSavedDraftList() {
  const panel = document.getElementById("savedDraftsPanel");
  const list = document.getElementById("savedDraftsList");
  const meta = document.getElementById("savedDraftsMeta");
  if (!panel || !list || !meta) {
    return;
  }
  try {
    const data = await api("GET", "/api/drafts");
    const drafts = data.drafts || [];
    const maxDrafts = data.maxDrafts || 2;
    const canCreate = !!data.canCreate;
    const transientGuest = !!data.transientGuest;
    const capacityMessage = t(
      "web.home.save_capacity",
      "{used} of {limit} online save slots used"
    )
      .replace("{used}", String(drafts.length))
      .replace("{limit}", String(maxDrafts));
    meta.textContent = transientGuest
      ? `${t("web.home.guest_badge", "Guest")} · ${capacityMessage}`
      : capacityMessage;
    const newDraftButton = document.getElementById("homeNewDraft");
    const uploadRuleFileButton = document.getElementById("homeUploadRuleFile");
    const characterButton = document.getElementById("homeCharacterActions");
    if (newDraftButton) {
      newDraftButton.classList.toggle("is-limit-reached", !canCreate);
      newDraftButton.setAttribute(
        "aria-label",
        !canCreate
          ? `${t("web.home.limit_reached", "Online save limit reached")}: ${t("web.splash.start", "Start new ruleset")}`
          : t("web.splash.start", "Start new ruleset")
      );
      newDraftButton.dataset.rulesetSlotsFull = String(!canCreate);
    }
    if (uploadRuleFileButton) {
      uploadRuleFileButton.classList.toggle("is-limit-reached", !canCreate);
      uploadRuleFileButton.setAttribute(
        "aria-label",
        !canCreate
          ? `${t("web.home.limit_reached", "Online save limit reached")}: ${t("web.home.import_rule_file", "Import rule file")}`
          : t("web.home.import_rule_file", "Import rule file")
      );
      uploadRuleFileButton.dataset.rulesetSlotsFull = String(!canCreate);
    }
    if (characterButton) {
      const hasRulesets = drafts.length > 0;
      characterButton.classList.toggle("is-disabled", !hasRulesets);
      characterButton.setAttribute("aria-disabled", String(!hasRulesets));
      characterButton.dataset.rulesetsAvailable = String(hasRulesets);
    }
    if (transientGuest) {
      list.innerHTML = `<div class="field-hint">${t(
        "web.home.guest_transient",
        "Guest work is temporary and is not saved to a server account. Create an account for server saves, or use Download .gmrf to keep a local file."
      )}</div>`;
      return;
    }
    if (!drafts.length) {
      list.innerHTML = `<div class="field-hint">${t(
        "web.home.no_saved_games",
        "No rulesets are saved to this account yet."
      )}</div>`;
      return;
    }
    list.innerHTML = drafts
      .map((draft) => {
        const id = escapeHtml(draft.id || "");
        const name = escapeHtml(draft.name || t("web.home.untitled_ruleset", "Untitled Ruleset"));
        const savedAt = escapeHtml(formatSavedDate(draft.lastSaved));
        return `
          <div class="list-item saved-draft-item">
            <div class="saved-draft-top-row">
              <strong class="saved-draft-name">${name}</strong>
              <div class="saved-draft-actions">
                <button class="btn small" type="button" data-open-draft="${id}" data-ruleset-management-action>${t("web.home.open_saved", "Open")}</button>
                <button class="btn ghost small" type="button" data-create-character="${id}">${t("web.home.create_character_from_saved", "Create character")}</button>
                <button class="btn danger small" type="button" data-delete-draft="${id}" data-delete-draft-name="${name}" data-ruleset-management-action>${t("web.home.delete_saved", "Delete save")}</button>
              </div>
            </div>
            <div class="field-hint">${t("web.home.last_saved", "Last saved")}: ${savedAt}</div>
          </div>
        `;
      })
      .join("");
    updateSavedDraftActionVisibility();
    updateSavedDraftCharacterButtons();
  } catch (error) {
    list.innerHTML = `<div class="field-hint">${escapeHtml(error.message)}</div>`;
  }
}

function updateSavedDraftActionVisibility() {
  const characterButton = document.getElementById("homeCharacterActions");
  const characterMode = characterButton && characterButton.getAttribute("aria-expanded") === "true";
  document.querySelectorAll("[data-ruleset-management-action]").forEach((button) => {
    button.classList.toggle("hidden", Boolean(characterMode));
  });
}

function updateSavedDraftCharacterButtons() {
  document.querySelectorAll("button[data-create-character]").forEach((button) => {
    button.disabled = !state.canCreateCharacterDraft;
  });
}

async function renderSavedCharacterList() {
  const panel = document.getElementById("savedCharactersPanel");
  const list = document.getElementById("savedCharactersList");
  const meta = document.getElementById("savedCharactersMeta");
  if (!panel || !list || !meta) {
    return;
  }
  try {
    const data = await api("GET", "/api/characters");
    const characters = data.characters || [];
    const maxCharacters = data.maxCharacters || 4;
    const canCreate = data.canCreate !== false;
    const transientGuest = !!data.transientGuest;
    state.canCreateCharacterDraft = canCreate;
    meta.textContent = `${characters.length}/${maxCharacters} ${t("web.home.character_save_slots", "character save slots used")}`;
    if (transientGuest) {
      meta.textContent = t("web.home.guest_badge", "Guest");
    }
    const characterChooseButton = document.getElementById("homeCharacterChoose");
    if (characterChooseButton) {
      characterChooseButton.disabled = !canCreate;
    }
    updateSavedDraftCharacterButtons();
    if (transientGuest) {
      list.innerHTML = `<div class="field-hint">${t(
        "web.home.character_guest_transient",
        "Guest character work is temporary. Create an account for server saves, or use Download .gmcf to keep a local file."
      )}</div>`;
      return;
    }
    if (!characters.length) {
      list.innerHTML = `<div class="field-hint">${t(
        "web.home.no_saved_characters",
        "No characters are saved yet. Choose a saved ruleset on the left to create one, or upload a character file to continue."
      )}</div>`;
      return;
    }
    list.innerHTML = characters
      .map((character) => {
        const id = escapeHtml(character.id || "");
        const name = escapeHtml(character.name || t("web.home.untitled_character", "Character Draft"));
        const gameName = escapeHtml(character.gameName || t("web.home.saved_unknown", "Unknown"));
        const savedAt = escapeHtml(formatSavedDate(character.lastSaved));
        return `
          <div class="list-item saved-character-item">
            <div class="saved-draft-copy">
              <strong>${name}</strong>
              <div class="field-hint">${t("web.home.character_game", "Game")}: ${gameName}</div>
              <div class="field-hint">${t("web.home.last_saved", "Last saved")}: ${savedAt}</div>
            </div>
            <div class="saved-draft-actions">
              <button class="btn small" type="button" data-open-character="${id}">${t("web.home.open_saved", "Open")}</button>
                <button class="btn danger small" type="button" data-delete-character="${id}" data-delete-character-name="${name}">${t("web.home.delete_saved", "Delete save")}</button>
            </div>
          </div>
        `;
      })
      .join("");
    list.querySelectorAll("button[data-open-character]").forEach((button) => {
      button.addEventListener("click", async () => {
        await openSavedCharacter(button.dataset.openCharacter || "");
      });
    });
    list.querySelectorAll("button[data-delete-character]").forEach((button) => {
      button.addEventListener("click", async () => {
        const id = button.dataset.deleteCharacter || "";
        const name = button.dataset.deleteCharacterName || t("web.home.untitled_character", "Character Draft");
        const confirmed = await showConfirm(
          t("web.home.delete_character_confirm", "Permanently delete saved character \"{name}\"?")
            .replace("{name}", name),
          t("web.home.delete_saved", "Delete Save")
        );
        if (!confirmed) {
          return;
        }
        button.disabled = true;
        try {
          await api("DELETE", `/api/characters/${encodeURIComponent(id)}`);
          showToast(t("web.toast.character_deleted", "Saved character deleted"));
          await renderSavedCharacterList();
        } catch (error) {
          showToast(error.message);
          button.disabled = false;
        }
      });
    });
  } catch (error) {
    list.innerHTML = `<div class="field-hint">${escapeHtml(error.message)}</div>`;
  }
}

function formatSavedDate(value) {
  const date = new Date(String(value || ""));
  if (Number.isNaN(date.getTime())) {
    return t("web.home.saved_unknown", "Unknown");
  }
  return date.toLocaleString();
}

async function renderAdmin() {
  if (!state.admin) {
    showToast(t("web.admin.forbidden", "Admin access required."));
    return;
  }
  setMode("home");
  setStep("admin");
  view.innerHTML = `
    <h1>${t("web.admin.title", "Admin")}</h1>
    <div class="admin-summary" id="adminSummary">
      <div class="stat"><strong>-</strong><span>${t("web.admin.accounts", "Accounts")}</span></div>
      <div class="stat"><strong>-</strong><span>${t("web.admin.saved_drafts", "Saved Drafts")}</span></div>
      <div class="stat"><strong>-</strong><span>${t("web.admin.locked", "Locked")}</span></div>
      <div class="stat"><strong>-</strong><span>${t("web.admin.active_sessions", "Active Sessions")}</span></div>
    </div>
    <div class="saved-drafts">
      <div class="saved-drafts-header">
        <h2>${t("web.admin.active_users", "Active Users")}</h2>
      </div>
      <div class="list admin-session-list" id="adminSessionList">
        <div class="field-hint">${t("web.loading", "Loading...")}</div>
      </div>
    </div>
    <div class="saved-drafts">
      <div class="saved-drafts-header">
        <h2>${t("web.admin.account_list", "User Accounts")}</h2>
        <button class="btn ghost" id="adminRefresh" type="button">${t("web.admin.refresh", "Refresh")}</button>
      </div>
      <div class="list admin-account-list" id="adminAccountList">
        <div class="field-hint">${t("web.loading", "Loading...")}</div>
      </div>
    </div>
    <div class="saved-drafts">
      <div class="saved-drafts-header">
        <h2>${t("web.admin.block_list", "Blocked Access")}</h2>
      </div>
      <div class="grid three admin-block-form">
        <div class="field">
          <label for="adminBlockType">${t("web.admin.block_type", "Type")}</label>
          <select id="adminBlockType">
            <option value="email">${t("web.admin.email", "Email")}</option>
            <option value="ip">${t("web.admin.ip", "IP")}</option>
          </select>
        </div>
        <div class="field">
          <label for="adminBlockValue">${t("web.admin.block_value", "Value")}</label>
          <input type="text" id="adminBlockValue">
        </div>
        <div class="field">
          <label for="adminBlockReason">${t("web.admin.block_reason", "Reason")}</label>
          <input type="text" id="adminBlockReason" maxlength="500">
        </div>
      </div>
      <button class="btn" id="adminAddBlock" type="button">${t("web.admin.add_block", "Add Block")}</button>
      <div class="list admin-block-list" id="adminBlockList">
        <div class="field-hint">${t("web.loading", "Loading...")}</div>
      </div>
    </div>
  `;
  updateActions();
  document.getElementById("adminRefresh").addEventListener("click", loadAdminAccounts);
  document.getElementById("adminAddBlock").addEventListener("click", addManualAdminBlock);
  await loadAdminAccounts();
  await loadAdminBlocks();
}

async function openSavedCharacter(characterDraftId) {
  const safeId = String(characterDraftId || "").trim();
  if (!safeId) {
    return;
  }
  try {
    const result = await api("GET", `/api/characters/${encodeURIComponent(safeId)}`);
    const text = String(result.text || "");
    const draft = parseCharGenDraft(text);
    const gameDraftId = String(draft.gameDraftId || "").trim();
    if (!gameDraftId) {
      showToast(t("web.chargen.missing", "Upload a ruleset to save this character."));
      return;
    }
    const openResult = await api("POST", `/api/drafts/${encodeURIComponent(gameDraftId)}/open`, {});
    state.draftId = openResult.draftId || gameDraftId;
    state.chargenCharacterDraftId = result.id || safeId;
    applyCharGenDraft(draft);
    state.chargenDraftText = text;
    updateActions();
    if (openResult.locale) {
      state.locale = normalizeLocale(openResult.locale);
      await loadLocalization(state.locale);
    }
    if (!hasValidCharGenName()) {
      renderCharGenName();
      return;
    }
    const target = resolveCharGenResumeStage();
    if (target === "classes") {
      renderCharGenClasses();
    } else if (target === "races") {
      renderCharGenRaces();
    } else if (target === "attribute-result-choice") {
      renderCharGenAttributeResultChoice();
    } else if (target === "attributes") {
      renderCharGenAttributeAssignment();
    } else {
      renderCharGenIntro();
    }
  } catch (error) {
    showToast(error.message);
  }
}

async function importServerCharacterFile(file) {
  if (!file) {
    return;
  }
  const result = await apiBinaryCharacterImport(file);
  const text = String(result.text || "");
  const draft = parseCharGenDraft(text);
  state.draftId = result.draftId || draft.gameDraftId || "";
  state.chargenCharacterDraftId = "";
  applyCharGenDraft(draft);
  state.chargenDraftText = text;
  updateActions();
  if (result.locale) {
    state.locale = normalizeLocale(result.locale);
    await loadLocalization(state.locale);
  }
  applyCompletedStages(result.completedStages || []);
  await loadSystemNames();
  if (!hasValidCharGenName()) {
    renderCharGenName();
    return;
  }
  const target = resolveCharGenResumeStage();
  if (target === "classes") {
    renderCharGenClasses();
  } else if (target === "races") {
    renderCharGenRaces();
  } else if (target === "attribute-result-choice") {
    renderCharGenAttributeResultChoice();
  } else if (target === "attributes") {
    renderCharGenAttributeAssignment();
  } else {
    renderCharGenIntro();
  }
}

async function startCharacterFromSavedDraft(draftId) {
  const safeId = String(draftId || "").trim();
  if (!safeId) {
    return;
  }
  try {
    resetCharGenState();
    const result = await api("POST", `/api/drafts/${encodeURIComponent(safeId)}/open`, {});
    state.draftId = result.draftId || safeId;
    updateActions();
    if (result.locale) {
      state.locale = normalizeLocale(result.locale);
      await loadLocalization(state.locale);
    }
    renderCharGenIntro();
  } catch (error) {
    showToast(error.message);
  }
}

async function loadAdminAccounts() {
  const summary = document.getElementById("adminSummary");
  const list = document.getElementById("adminAccountList");
  const sessionList = document.getElementById("adminSessionList");
  if (!summary || !list || !sessionList) {
    return;
  }
  try {
    const data = await api("GET", "/api/admin/accounts");
    summary.innerHTML = `
      <div class="stat"><strong>${escapeHtml(data.accountCount || 0)}</strong><span>${t("web.admin.accounts", "Accounts")}</span></div>
      <div class="stat"><strong>${escapeHtml(data.draftCount || 0)}</strong><span>${t("web.admin.saved_drafts", "Saved Drafts")}</span></div>
      <div class="stat"><strong>${escapeHtml(data.lockedCount || 0)}</strong><span>${t("web.admin.locked", "Locked")}</span></div>
      <div class="stat"><strong>${escapeHtml(data.activeSessionCount || 0)}</strong><span>${t("web.admin.active_sessions", "Active Sessions")}</span></div>
    `;
    const sessions = data.activeSessions || [];
    if (!sessions.length) {
      sessionList.innerHTML = `<div class="field-hint">${t("web.admin.no_active_sessions", "No users are currently logged in.")}</div>`;
    } else {
      sessionList.innerHTML = sessions.map((session) => renderAdminSession(session)).join("");
    }
    const accounts = data.accounts || [];
    if (!accounts.length) {
      list.innerHTML = `<div class="field-hint">${t("web.admin.no_accounts", "No accounts found.")}</div>`;
      return;
    }
    list.innerHTML = accounts.map((account) => renderAdminAccount(account)).join("");
    list.querySelectorAll("button[data-admin-unlock]").forEach((button) => {
      button.addEventListener("click", async () => {
        const email = button.dataset.adminUnlock || "";
        button.disabled = true;
        try {
          await api("POST", "/api/admin/accounts/unlock", { email });
          showToast(t("web.admin.unlocked", "Account unlocked."));
          await loadAdminAccounts();
        } catch (error) {
          showToast(error.message);
          button.disabled = false;
        }
      });
    });
    list.querySelectorAll("button[data-admin-block-email]").forEach((button) => {
      button.addEventListener("click", () => blockAdminValue("email", button.dataset.adminBlockEmail || "", button.dataset.adminSourceAccount || ""));
    });
    list.querySelectorAll("button[data-admin-unblock-email]").forEach((button) => {
      button.addEventListener("click", () => unblockAdminValue("email", button.dataset.adminUnblockEmail || ""));
    });
    list.querySelectorAll("button[data-admin-block-ip]").forEach((button) => {
      button.addEventListener("click", () => blockAdminValue("ip", button.dataset.adminBlockIp || "", button.dataset.adminSourceAccount || ""));
    });
    list.querySelectorAll("button[data-admin-unblock-ip]").forEach((button) => {
      button.addEventListener("click", () => unblockAdminValue("ip", button.dataset.adminUnblockIp || ""));
    });
    list.querySelectorAll("button[data-admin-delete]").forEach((button) => {
      button.addEventListener("click", async () => {
        const email = button.dataset.adminDelete || "";
        const confirmed = await showConfirm(
          t("web.admin.delete_confirm", "Permanently delete {email} and all saved rulesets for that account?")
            .replace("{email}", email),
          t("web.admin.delete", "Delete Account")
        );
        if (!confirmed) {
          return;
        }
        button.disabled = true;
        try {
          const result = await api("DELETE", "/api/admin/accounts", { email });
          showToast(t("web.admin.deleted", "Account deleted. Removed {count} saved rulesets.")
            .replace("{count}", String(result.deletedDrafts || 0)));
          await loadAdminAccounts();
        } catch (error) {
          showToast(error.message);
          button.disabled = false;
        }
      });
    });
  } catch (error) {
    list.innerHTML = `<div class="field-hint">${escapeHtml(error.message)}</div>`;
  }
}

function renderAdminSession(session) {
  const username = escapeHtml(session.username || "");
  const userId = escapeHtml(session.userId || "");
  const createdAt = escapeHtml(formatSavedDate(session.createdAt));
  const lastAccessAt = escapeHtml(formatSavedDate(session.lastAccessAt));
  const draftId = escapeHtml(session.draftId || "");
  const badges = [
    session.admin ? t("web.admin.badge_admin", "Admin") : "",
    session.legacyGuest ? t("web.admin.badge_legacy_guest", "Legacy guest") : "",
  ].filter(Boolean).map((label) => `<span class="badge">${escapeHtml(label)}</span>`).join(" ");
  return `
    <div class="list-item admin-account-item">
      <div class="saved-draft-copy">
        <strong>${username || t("web.home.saved_unknown", "Unknown")}</strong>
        ${badges ? `<div class="field-hint">${badges}</div>` : ""}
        <div class="field-hint">${t("web.admin.account_id", "Account ID")}: ${userId}</div>
        <div class="field-hint">${t("web.admin.session_started", "Session Started")}: ${createdAt}</div>
        <div class="field-hint">${t("web.admin.last_active", "Last Active")}: ${lastAccessAt}</div>
        <div class="field-hint">${t("web.admin.current_draft", "Current Draft")}: ${draftId || t("common.none", "None")}</div>
      </div>
    </div>
  `;
}

async function loadAdminBlocks() {
  const list = document.getElementById("adminBlockList");
  if (!list) {
    return;
  }
  try {
    const data = await api("GET", "/api/admin/blocks");
    const blocks = data.blocks || [];
    if (!blocks.length) {
      list.innerHTML = `<div class="field-hint">${t("web.admin.no_blocks", "No blocked emails or IPs.")}</div>`;
      return;
    }
    list.innerHTML = blocks.map((block) => renderAdminBlock(block)).join("");
    list.querySelectorAll("button[data-admin-unblock-type]").forEach((button) => {
      button.addEventListener("click", () => unblockAdminValue(
        button.dataset.adminUnblockType || "",
        button.dataset.adminUnblockValue || ""
      ));
    });
  } catch (error) {
    list.innerHTML = `<div class="field-hint">${escapeHtml(error.message)}</div>`;
  }
}

function renderAdminBlock(block) {
  const type = escapeHtml(block.type || "");
  const value = escapeHtml(block.value || "");
  const reason = escapeHtml(block.reason || "");
  const createdAt = escapeHtml(formatSavedDate(block.createdAt));
  const createdBy = escapeHtml(block.createdBy || "");
  const sourceAccountId = escapeHtml(block.sourceAccountId || "");
  return `
    <div class="list-item admin-account-item">
      <div class="saved-draft-copy">
        <strong>${type}: ${value}</strong>
        ${reason ? `<div class="field-hint">${t("web.admin.block_reason", "Reason")}: ${reason}</div>` : ""}
        <div class="field-hint">${t("web.admin.created", "Created")}: ${createdAt}</div>
        ${createdBy ? `<div class="field-hint">${t("web.admin.created_by", "Created by")}: ${createdBy}</div>` : ""}
        ${sourceAccountId ? `<div class="field-hint">${t("web.admin.source_account", "Source Account")}: ${sourceAccountId}</div>` : ""}
      </div>
      <div class="saved-draft-actions">
        <button class="btn small" type="button" data-admin-unblock-type="${type}" data-admin-unblock-value="${value}">
          ${t("web.admin.unblock", "Unblock")}
        </button>
      </div>
    </div>
  `;
}

async function addManualAdminBlock() {
  const typeInput = document.getElementById("adminBlockType");
  const valueInput = document.getElementById("adminBlockValue");
  const reasonInput = document.getElementById("adminBlockReason");
  const type = typeInput ? typeInput.value : "email";
  const value = valueInput ? valueInput.value.trim() : "";
  const reason = reasonInput ? reasonInput.value.trim() : "";
  if (!value) {
    showToast(t("web.admin.block_value_required", "Enter a value to block."));
    return;
  }
  await blockAdminValue(type, value, "", reason);
  if (valueInput) {
    valueInput.value = "";
  }
  if (reasonInput) {
    reasonInput.value = "";
  }
}

async function blockAdminValue(type, value, sourceAccountId = "", reason = "") {
  const safeValue = String(value || "").trim();
  if (!safeValue) {
    showToast(t("web.admin.block_value_required", "Enter a value to block."));
    return;
  }
  const safeReason = reason || window.prompt(t("web.admin.block_reason_prompt", "Reason for block?"), "") || "";
  try {
    await api("POST", "/api/admin/blocks", {
      type,
      value: safeValue,
      reason: safeReason,
      sourceAccountId,
    });
    showToast(t("web.admin.blocked", "Access block saved."));
    await loadAdminAccounts();
    await loadAdminBlocks();
  } catch (error) {
    showToast(error.message);
  }
}

async function unblockAdminValue(type, value) {
  const safeValue = String(value || "").trim();
  if (!safeValue) {
    return;
  }
  try {
    await api("DELETE", "/api/admin/blocks", { type, value: safeValue });
    showToast(t("web.admin.unblocked", "Access block removed."));
    await loadAdminAccounts();
    await loadAdminBlocks();
  } catch (error) {
    showToast(error.message);
  }
}

function renderAdminAccount(account) {
  const email = escapeHtml(account.email || "");
  const id = escapeHtml(account.id || "");
  const createdAt = escapeHtml(formatSavedDate(account.createdAt));
  const verifiedAt = escapeHtml(formatSavedDate(account.verifiedAt));
  const lastLoginAt = escapeHtml(formatSavedDate(account.lastLoginAt));
  const lastLoginIp = escapeHtml(account.lastLoginIp || "");
  const draftCount = Number(account.draftCount || 0);
  const draftIds = (account.draftIds || []).map((draftId) => escapeHtml(draftId)).join(", ");
  const badges = [
    account.admin ? t("web.admin.badge_admin", "Admin") : "",
    account.passwordSet ? t("web.admin.badge_password", "Password set") : t("web.admin.badge_no_password", "No password"),
    account.locked ? t("web.admin.badge_locked", "Locked") : "",
    account.emailBlocked ? t("web.admin.badge_email_blocked", "Email blocked") : "",
    account.lastLoginIpBlocked ? t("web.admin.badge_ip_blocked", "IP blocked") : "",
  ].filter(Boolean).map((label) => `<span class="badge">${escapeHtml(label)}</span>`).join(" ");
  return `
    <div class="list-item admin-account-item">
      <div class="saved-draft-copy">
        <strong>${email}</strong>
        <div class="field-hint">${badges}</div>
        <div class="field-hint">${t("web.admin.account_id", "Account ID")}: ${id}</div>
        <div class="field-hint">${t("web.admin.created", "Created")}: ${createdAt}</div>
        <div class="field-hint">${t("web.admin.verified", "Verified")}: ${verifiedAt}</div>
        <div class="field-hint">${t("web.admin.last_login", "Last Login")}: ${lastLoginAt}</div>
        <div class="field-hint">${t("web.admin.last_login_ip", "Last Login IP")}: ${lastLoginIp || t("web.home.saved_unknown", "Unknown")}</div>
        <div class="field-hint">${t("web.admin.drafts", "Drafts")}: ${draftCount}${draftIds ? ` (${draftIds})` : ""}</div>
        <div class="field-hint">${t("web.admin.failed_attempts", "Failed login attempts")}: ${escapeHtml(account.failedLoginAttempts || 0)}</div>
      </div>
      <div class="saved-draft-actions">
        <button class="btn small" type="button" data-admin-unlock="${email}" ${account.locked ? "" : "disabled"}>${t("web.admin.unlock", "Unlock")}</button>
        <button class="btn small" type="button" data-admin-block-email="${email}" data-admin-source-account="${id}" ${account.admin || account.emailBlocked ? "disabled" : ""}>${t("web.admin.block_email", "Block Email")}</button>
        <button class="btn small" type="button" data-admin-unblock-email="${email}" ${account.emailBlocked ? "" : "disabled"}>${t("web.admin.unblock_email", "Unblock Email")}</button>
        <button class="btn small" type="button" data-admin-block-ip="${lastLoginIp}" data-admin-source-account="${id}" ${lastLoginIp && !account.lastLoginIpBlocked ? "" : "disabled"}>${t("web.admin.block_ip", "Block IP")}</button>
        <button class="btn small" type="button" data-admin-unblock-ip="${lastLoginIp}" ${account.lastLoginIpBlocked ? "" : "disabled"}>${t("web.admin.unblock_ip", "Unblock IP")}</button>
        <button class="btn danger small" type="button" data-admin-delete="${email}" ${account.admin ? "disabled" : ""}>${t("web.admin.delete", "Delete Account")}</button>
      </div>
    </div>
  `;
}

function renderHome() {
  setMode("home");
  setStep("home");
  view.innerHTML = `
    <section class="panel home-panel">
      <h1>${t("web.home.title", "Welcome to GMRules")}</h1>
      <div class="home-action-groups">
        <section class="home-action-group" aria-labelledby="homeRulesetsTitle">
          <div class="home-action-group-heading">
            <h2 id="homeRulesetsTitle">${t("web.home.rulesets_title", "Rulesets")}</h2>
            <p>${t(
              "web.home.rulesets_description",
              "Create a game or continue building an existing one."
            )}</p>
          </div>
          <div class="home-primary-actions">
            <button class="btn" id="homeNewDraft" type="button" aria-describedby="savedDraftsMeta">${t("web.splash.start", "Start new ruleset")}</button>
            <button
              class="btn ghost"
              id="homeOpenSavedRulesets"
              type="button"
              aria-controls="savedDraftsPanel"
              aria-expanded="false"
            >${t("web.home.manage_saved_rulesets", "Manage saved rulesets")}</button>
            <button class="btn ghost" id="homeUploadRuleFile" type="button" aria-describedby="savedDraftsMeta">${t("web.home.import_rule_file", "Import rule file")}</button>
          </div>
          <div class="home-save-capacity" id="savedDraftsMeta" aria-live="polite">${t("web.loading", "Loading...")}</div>
          <input class="hidden" type="file" id="homeEditFile" accept=".gmrf">
          <div class="saved-drafts hidden" id="savedDraftsPanel">
            <div class="saved-drafts-header">
              <h3>${t("web.home.saved_title", "Saved rulesets")}</h3>
            </div>
            <div class="list saved-drafts-list" id="savedDraftsList">
              <div class="field-hint">${t("web.loading", "Loading...")}</div>
            </div>
          </div>
        </section>
        <section class="home-action-group" aria-labelledby="homeCharactersTitle">
          <div class="home-action-group-heading">
            <h2 id="homeCharactersTitle">${t("web.home.characters_title", "Characters")}</h2>
            <p>${t(
              "web.home.characters_description",
              "Create or continue a character using one of your rulesets."
            )}</p>
          </div>
          <div class="home-primary-actions">
            <button
              class="btn ghost is-disabled"
              id="homeCharacterActions"
              type="button"
              aria-controls="savedDraftsPanel savedCharactersPanel homeCharacterUploadActions"
              aria-expanded="false"
              aria-disabled="true"
              data-rulesets-available="false"
            >${t("web.home.create_edit_character", "Create or edit character")}</button>
          </div>
          <div class="saved-drafts hidden" id="savedCharactersPanel">
            <div class="saved-drafts-header saved-characters-header">
              <h3>${t("web.home.saved_characters_title", "Saved characters")}</h3>
              <span class="badge" id="savedCharactersMeta">${t("web.loading", "Loading...")}</span>
            </div>
            <div class="list saved-character-list" id="savedCharactersList">
              <div class="field-hint">${t("web.loading", "Loading...")}</div>
            </div>
          </div>
          <div class="home-character-upload-actions hidden" id="homeCharacterUploadActions">
            <input class="hidden" type="file" id="homeCharacterFile" accept=".gmcf">
            <h3>${t("web.home.character_file_title", "Continue from a file")}</h3>
            <button class="btn" id="homeCharacterChoose" type="button">${t("web.home.create_character", "Upload character file")}</button>
          </div>
        </section>
      </div>
      <details class="account-settings">
        <summary>${t("web.account_delete.settings_title", "Account settings")}</summary>
        <div class="account-settings-content">
          <p class="field-hint">${t(
            "web.account_delete.zone_hint",
            "This deletes your account and every saved ruleset for it. To keep work offline, log in, open each ruleset you want to keep, and use Download .gmrf before deleting the account. To delete only one save, use the Delete save buttons in the saved-ruleset list."
          )}</p>
          <button class="btn danger" id="homeDeleteAccount" type="button">${t("web.account_delete.permanent_button", "Permanently delete account")}</button>
        </div>
      </details>
    </section>
  `;

  const openHomeFilePicker = async (fallbackInput, pickerId, extensions) => {
    if (!window.showOpenFilePicker) {
      fallbackInput.click();
      return null;
    }
    try {
      const handles = await window.showOpenFilePicker({
        id: pickerId,
        startIn: "documents",
        multiple: false,
        types: [
          {
            description: t("web.home.file_type", "GMRules Files"),
            accept: {
              "application/octet-stream": extensions,
            },
          },
        ],
      });
      if (!handles.length) {
        return null;
      }
      return await handles[0].getFile();
    } catch (error) {
      if (error && error.name === "AbortError") {
        return null;
      }
      fallbackInput.click();
      return null;
    }
  };

  const importGameForEditing = async (file) => {
    if (!file) {
      return;
    }
    try {
      const buffer = await file.arrayBuffer();
      const result = await apiBinary("POST", "/api/drafts/import", buffer);
      state.draftId = result.draftId;
      updateActions();
      if (result.locale) {
        state.locale = normalizeLocale(result.locale);
        await loadLocalization(state.locale);
      }
      applyCompletedStages(result.completedStages || []);
      await loadSystemNames();
      markSaved(t("web.toast.draft_imported", "Draft imported"));
      renderSetup();
    } catch (error) {
      showToast(error.message);
    }
  };

  const importCharacterSource = async (file) => {
    if (!file) {
      return;
    }
    try {
      const filename = String(file.name || "").toLowerCase();
      if (filename.endsWith(".gmcf")) {
        const text = await file.text();
        try {
          const draft = parseCharGenDraft(text);
          applyCharGenDraft(draft);
          state.chargenCharacterDraftId = "";
          state.chargenDraftText = text;
          renderCharGenResume();
        } catch (parseError) {
          await importServerCharacterFile(file);
        }
        return;
      }
      resetCharGenState();
      const buffer = await file.arrayBuffer();
      state.chargenGameHash = await hashBuffer(buffer);
      const result = await apiBinary("POST", "/api/drafts/import", buffer);
      state.draftId = result.draftId;
      updateActions();
      if (result.locale) {
        state.locale = normalizeLocale(result.locale);
        await loadLocalization(state.locale);
      }
      renderCharGenIntro();
    } catch (error) {
      showToast(error.message);
    }
  };

  document.getElementById("homeNewDraft").addEventListener("click", async (event) => {
    if (event.currentTarget.dataset.rulesetSlotsFull === "true") {
      openRulesetLimitPopup(
        !savedDraftsPanel.classList.contains("hidden"),
        () => setHomePanelMode("rulesets")
      );
      return;
    }
    try {
      const result = await api("POST", "/api/drafts", { locale: state.locale });
      state.draftId = result.draftId;
      updateActions();
      if (result.locale && result.locale !== state.locale) {
        state.locale = normalizeLocale(result.locale);
        await loadLocalization(state.locale);
      }
      applyCompletedStages(result.completedStages || []);
      await loadSystemNames();
      markSaved(t("web.toast.draft_created", "Draft created"));
      renderSetup();
    } catch (error) {
      showToast(error.message);
    }
  });

  const homeEditFile = document.getElementById("homeEditFile");
  const homeCharacterFile = document.getElementById("homeCharacterFile");
  const savedDraftsPanel = document.getElementById("savedDraftsPanel");
  const savedCharactersPanel = document.getElementById("savedCharactersPanel");
  const homeCharacterUploadActions = document.getElementById("homeCharacterUploadActions");
  const homeOpenSavedRulesets = document.getElementById("homeOpenSavedRulesets");
  const homeCharacterActions = document.getElementById("homeCharacterActions");
  let homePanelMode = "";

  const setHomePanelMode = (mode) => {
    homePanelMode = mode;
    const showRulesets = mode === "rulesets" || mode === "characters";
    const showCharacters = mode === "characters";
    savedDraftsPanel.classList.toggle("hidden", !showRulesets);
    savedCharactersPanel.classList.toggle("hidden", !showCharacters);
    homeCharacterUploadActions.classList.toggle("hidden", !showCharacters);
    homeOpenSavedRulesets.setAttribute("aria-expanded", String(mode === "rulesets"));
    homeCharacterActions.setAttribute("aria-expanded", String(showCharacters));
    updateSavedDraftActionVisibility();
  };

  document.getElementById("homeDeleteAccount").addEventListener("click", () => {
    openDeleteAccountModal(state.accountName);
  });

  homeOpenSavedRulesets.addEventListener("click", () => {
    setHomePanelMode("rulesets");
  });

  document.getElementById("homeUploadRuleFile").addEventListener("click", async (event) => {
    if (event.currentTarget.dataset.rulesetSlotsFull === "true") {
      openRulesetLimitPopup(
        !savedDraftsPanel.classList.contains("hidden"),
        () => setHomePanelMode("rulesets")
      );
      return;
    }
    const file = await openHomeFilePicker(homeEditFile, "gmrules-edit-game", [".gmrf"]);
    await importGameForEditing(file);
  });

  homeCharacterActions.addEventListener("click", () => {
    if (homeCharacterActions.dataset.rulesetsAvailable !== "true") {
      openCharacterRulesetRequiredPopup();
      return;
    }
    setHomePanelMode("characters");
  });

  document.getElementById("homeCharacterChoose").addEventListener("click", async () => {
    const file = await openHomeFilePicker(homeCharacterFile, "gmrules-create-character", [".gmcf"]);
    await importCharacterSource(file);
  });

  document.getElementById("savedDraftsList").addEventListener("click", async (event) => {
    const deleteButton = event.target.closest("button[data-delete-draft]");
    if (deleteButton) {
      const draftId = deleteButton.dataset.deleteDraft || "";
      const draftName = deleteButton.dataset.deleteDraftName || t("web.home.untitled_ruleset", "Untitled Ruleset");
      const confirmed = await showConfirm(
        t(
          "web.home.delete_saved_confirm",
          "Permanently delete saved ruleset \"{name}\"? This deletes only this saved file, not your account."
        ).replace("{name}", draftName),
        t("web.home.delete_saved", "Delete Save")
      );
      if (!confirmed) {
        return;
      }
      try {
        await api("DELETE", `/api/drafts/${draftId}`);
        if (state.draftId === draftId) {
          state.draftId = "";
          updateActions();
        }
        await renderSavedDraftList();
        await renderSavedCharacterList();
        showToast(t("web.toast.draft_deleted", "Saved ruleset deleted"));
      } catch (error) {
        showToast(error.message);
      }
      return;
    }
    const button = event.target.closest("button[data-open-draft]");
    if (button) {
      try {
        const result = await api("POST", `/api/drafts/${button.dataset.openDraft}/open`);
        state.draftId = result.draftId;
        updateActions();
        if (result.locale) {
          state.locale = normalizeLocale(result.locale);
          await loadLocalization(state.locale);
        }
        applyCompletedStages(result.completedStages || []);
        await loadSystemNames();
        markSaved(t("web.toast.draft_opened", "Draft opened"));
        renderSetup();
      } catch (error) {
        showToast(error.message);
      }
      return;
    }

    const characterButton = event.target.closest("button[data-create-character]");
    if (!characterButton) {
      return;
    }
    if (!state.canCreateCharacterDraft) {
      showToast(t("web.home.character_slots_full", "Character save slots are full."));
      return;
    }
    await startCharacterFromSavedDraft(characterButton.dataset.createCharacter || "");
  });

  homeEditFile.addEventListener("change", async (event) => {
    const file = event.target.files[0];
    await importGameForEditing(file);
  });

  homeCharacterFile.addEventListener("change", async (event) => {
    const file = event.target.files[0];
    await importCharacterSource(file);
  });

  renderSavedDraftList();
  renderSavedCharacterList();
}

function renderBuilderSplash() {
  setMode("builder");
  setStep("splash");
  view.innerHTML = `
    <section class="panel">
      <h1>${t("splash.title", "Welcome to GMRules")}</h1>
      <p>${t("splash.description", "GMRules lets you define and manage tabletop RPG rulesets, mechanics, and game elements.")}</p>
      <div class="grid two">
        <div class="field">
          <label for="language">${t("splash.language", "Language")}</label>
          <select id="language">
            <option value="en">${t("web.language.en", "English")}</option>
            <option value="fr">${t("web.language.fr", "French")}</option>
          </select>
        </div>
        <div class="field">
          <label for="importFile">${t("web.splash.import", "Import .gmrf file")}</label>
          <input type="file" id="importFile" accept=".gmrf">
        </div>
      </div>
      <div class="actions-row">
        <div class="left">
          <button class="btn ghost" id="backToHome" type="button">${t("setup.back", "Back")}</button>
        </div>
        <div class="right">
          <button class="btn" id="newDraft" type="button">${t("web.splash.start", "Start New Ruleset")}</button>
        </div>
      </div>
    </section>
  `;

  const newDraft = document.getElementById("newDraft");
  const importFile = document.getElementById("importFile");
  const languageSelect = document.getElementById("language");

  document.getElementById("backToHome").addEventListener("click", renderHome);

  languageSelect.value = state.locale || "en";
  languageSelect.addEventListener("change", async (event) => {
    state.locale = normalizeLocale(event.target.value || "en");
    await loadLocalization(state.locale);
    if (state.draftId) {
      try {
        await api("POST", `/api/drafts/${state.draftId}/locale`, { locale: state.locale });
        markSaved(t("web.toast.language_updated", "Language updated"));
      } catch (error) {
        showToast(error.message);
      }
    }
    renderBuilderSplash();
  });

  newDraft.addEventListener("click", async () => {
    try {
      const result = await api("POST", "/api/drafts", { locale: state.locale });
      state.draftId = result.draftId;
      updateActions();
      if (result.locale && result.locale !== state.locale) {
        state.locale = normalizeLocale(result.locale);
        await loadLocalization(state.locale);
      }
      applyCompletedStages(result.completedStages || []);
      await loadSystemNames();
      markSaved(t("web.toast.draft_created", "Draft created"));
      renderSetup();
    } catch (error) {
      showToast(error.message);
    }
  });

  importFile.addEventListener("change", async (event) => {
    const file = event.target.files[0];
    if (!file) {
      return;
    }
    try {
      const buffer = await file.arrayBuffer();
      const result = await apiBinary("POST", "/api/drafts/import", buffer);
      state.draftId = result.draftId;
      updateActions();
      if (result.locale) {
        state.locale = normalizeLocale(result.locale);
        await loadLocalization(state.locale);
      }
      applyCompletedStages(result.completedStages || []);
      await loadSystemNames();
      markSaved(t("web.toast.draft_imported", "Draft imported"));
      renderSetup();
    } catch (error) {
      showToast(error.message);
    }
  });
}

function renderCharGenUpload() {
  setMode("chargen");
  setStep("chargen-upload");
  view.innerHTML = `
    <section class="panel">
      <h1>${t("web.chargen.title", "Character Generator")}</h1>
      <p>${t("web.chargen.description", "Upload a .gmrf or .gmcf file to start creating a character.")}</p>
      <div class="grid">
        <div class="field">
          <label for="chargenFile">${t("web.chargen.upload", "Import .gmrf or .gmcf file")}</label>
          <input type="file" id="chargenFile" accept=".gmrf,.gmcf">
        </div>
      </div>
      <div class="actions-row">
        <div class="left">
          <button class="btn ghost" id="chargenBack" type="button">${t("setup.back", "Back")}</button>
        </div>
      </div>
    </section>
  `;

  document.getElementById("chargenBack").addEventListener("click", renderHome);
  const chargenFile = document.getElementById("chargenFile");
  chargenFile.addEventListener("change", async (event) => {
    const file = event.target.files[0];
    if (!file) {
      return;
    }
    try {
      const filename = String(file.name || "").toLowerCase();
      if (filename.endsWith(".gmcf")) {
        const text = await file.text();
        try {
          const draft = parseCharGenDraft(text);
          applyCharGenDraft(draft);
          state.chargenDraftText = text;
          renderCharGenResume();
        } catch (parseError) {
          await importServerCharacterFile(file);
        }
        return;
      }
      resetCharGenState();
      const buffer = await file.arrayBuffer();
      state.chargenGameHash = await hashBuffer(buffer);
      const result = await apiBinary("POST", "/api/drafts/import", buffer);
      state.draftId = result.draftId;
      updateActions();
      if (result.locale) {
        state.locale = normalizeLocale(result.locale);
        await loadLocalization(state.locale);
      }
      renderCharGenIntro();
    } catch (error) {
      showToast(error.message);
    }
  });
}

function renderCharGenResume() {
  setMode("chargen");
  setStep("chargen-resume");
  view.innerHTML = `
    <section class="panel">
      <h1>${t("web.chargen.resume.title", "Resume Character")}</h1>
      <p>${t("web.chargen.resume.body", "Upload the matching .gmrf ruleset to continue.")}</p>
      <div class="grid">
        <div class="field">
          <label for="chargenResumeFile">${t("web.chargen.upload.rules", "Import .gmrf file")}</label>
          <input type="file" id="chargenResumeFile" accept=".gmrf">
        </div>
      </div>
      <div class="actions-row">
        <div class="left">
          <button class="btn ghost" id="chargenResumeBack" type="button">${t("setup.back", "Back")}</button>
        </div>
      </div>
    </section>
  `;

  document.getElementById("chargenResumeBack").addEventListener("click", renderCharGenUpload);
  const resumeFile = document.getElementById("chargenResumeFile");
  resumeFile.addEventListener("change", async (event) => {
    const file = event.target.files[0];
    if (!file) {
      return;
    }
    try {
      const buffer = await file.arrayBuffer();
      const hash = await hashBuffer(buffer);
      if (state.chargenGameHash && hash !== state.chargenGameHash) {
        showToast(t("web.chargen.hash.mismatch", "Selected file does not match this character."));
        return;
      }
      state.chargenGameHash = hash;
      const result = await apiBinary("POST", "/api/drafts/import", buffer);
      state.draftId = result.draftId;
      updateActions();
      if (result.locale) {
        state.locale = normalizeLocale(result.locale);
        await loadLocalization(state.locale);
      }
      const setup = await api("GET", `/api/drafts/${state.draftId}/setup`);
      const gameId = String(setup.id || "");
      if (state.chargenGameId && gameId !== state.chargenGameId) {
        showToast(t("web.chargen.id.mismatch", "Selected file does not match this character."));
        return;
      }
      state.chargenGameId = gameId;
      state.chargenGameName = String(setup.name || "");
      if (!hasValidCharGenName()) {
        renderCharGenName();
        return;
      }
      const target = resolveCharGenResumeStage();
      if (target === "armor") {
        renderCharGenArmor();
      } else if (target === "weapons") {
        renderCharGenWeapons();
      } else if (target === "equipment") {
        renderCharGenEquipment();
      } else if (target === "spells") {
        renderCharGenSpells();
      } else if (target === "skills") {
        renderCharGenSkills();
      } else if (target === "classes") {
        renderCharGenClasses();
      } else if (target === "races") {
        renderCharGenRaces();
      } else if (target === "attribute-result-choice") {
        renderCharGenAttributeResultChoice();
      } else if (target === "attributes") {
        renderCharGenAttributeAssignment();
      } else {
        renderCharGenIntro();
      }
    } catch (error) {
      showToast(error.message);
    }
  });
}

function hasValidCharGenName() {
  return String(state.chargenCharacterName || "").trim().length > 0;
}

function renderCharGenLoadError(error, retry) {
  const message = String(error && error.message ? error.message : error || "").trim()
    || t("web.error.request_failed", "Request failed");
  view.innerHTML = `
    <section class="panel">
      <h1>${t("web.chargen.load_failed.title", "Character Generator")}</h1>
      <p>${escapeHtml(message)}</p>
      <div class="actions-row">
        <div class="left">
          <button class="btn ghost" id="chargenErrorHome" type="button">${t("web.home.button", "Home")}</button>
        </div>
        <div class="right">
          <button class="btn" id="chargenErrorRetry" type="button">${t("common.retry", "Try Again")}</button>
        </div>
      </div>
    </section>
  `;
  document.getElementById("chargenErrorHome").addEventListener("click", renderHome);
  document.getElementById("chargenErrorRetry").addEventListener("click", () => {
    if (typeof retry === "function") {
      retry();
    }
  });
  showToast(message);
}

async function renderCharGenName() {
  if (!state.draftId) {
    renderCharGenUpload();
    return;
  }
  setMode("chargen");
  setStep("chargen-name");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const data = await api("GET", `/api/drafts/${state.draftId}/setup`);
    state.chargenGameId = String(data.id || "");
    state.chargenGameName = String(data.name || "");
    const currentName = String(state.chargenCharacterName || "").trim();
    view.innerHTML = `
      <section class="panel">
        <h1>${t("web.chargen.name.title", "Character Name")}</h1>
        <p>${t("web.chargen.name.body", "Enter a character name before starting this character.")}</p>
        <div class="field">
          <label for="chargenCharacterName">${t("web.chargen.name.label", "Character Name")}</label>
          <input type="text" id="chargenCharacterName" maxlength="80" value="${escapeHtml(currentName)}">
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="chargenNameBack" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn" id="chargenNameContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;
    const nameInput = document.getElementById("chargenCharacterName");
    const submit = () => {
      const name = String(nameInput.value || "").trim();
      if (!name) {
        showToast(t("web.chargen.name.required", "Enter a character name."));
        nameInput.focus();
        return;
      }
      state.chargenCharacterName = name;
      saveCharGenDraftLocal({ server: false });
      renderCharGenAttributes();
    };
    document.getElementById("chargenNameBack").addEventListener("click", renderCharGenIntro);
    document.getElementById("chargenNameContinue").addEventListener("click", submit);
    nameInput.addEventListener("keydown", (event) => {
      if (event.key === "Enter") {
        submit();
      }
    });
    window.requestAnimationFrame(() => {
      nameInput.focus();
      nameInput.select();
    });
  } catch (error) {
    renderCharGenLoadError(error, renderCharGenName);
  }
}

async function renderCharGenIntro() {
  if (!state.draftId) {
    renderCharGenUpload();
    return;
  }
  setMode("chargen");
  setStep("chargen-intro");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const data = await api("GET", `/api/drafts/${state.draftId}/setup`);
    const description = String(data.description || "").trim();
    const safeDescription = description || t("common.no_data", "No data available.");
    state.chargenGameId = String(data.id || "");
    state.chargenGameName = String(data.name || "");
    saveCharGenDraftLocal({ server: false });
    view.innerHTML = `
      <section class="panel">
        <h1>${t("setup.title", "Game Setup")}</h1>
        <div class="field">
          <label for="chargenDescription">${t("setup.game.description", "Game Description")}</label>
          <textarea id="chargenDescription" readonly>${escapeHtml(safeDescription)}</textarea>
        </div>
        <p>${t("setup.prompt", "Do you want to create a character for this game?")}</p>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="chargenCancel" type="button">${t("common.cancel", "Cancel")}</button>
          </div>
          <div class="right">
            <button class="btn" id="chargenContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    document.getElementById("chargenCancel").addEventListener("click", renderHome);
    document.getElementById("chargenContinue").addEventListener("click", () => {
      if (!hasValidCharGenName()) {
        renderCharGenName();
        return;
      }
      renderCharGenAttributes();
    });
  } catch (error) {
    renderCharGenLoadError(error, renderCharGenIntro);
  }
}

async function renderCharGenAttributes() {
  if (!state.draftId) {
    renderCharGenUpload();
    return;
  }
  setMode("chargen");
  setStep("chargen-attrgen");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const data = await api("GET", `/api/drafts/${state.draftId}/chargen/attribute-generation`);
    const attributes = Array.isArray(data.attributes) ? data.attributes : [];
    const method = data || {};
    const generationChoices = getCharGenGenerationChoices(method);
    const previousChoice = normalizeCharGenGenerationChoice(state.chargenAttributeGenerationChoice);
    if (generationChoices.length === 1) {
      state.chargenAttributeGenerationChoice = generationChoices[0].key;
      if (previousChoice && previousChoice !== generationChoices[0].key) {
        state.chargenRolledAttributeValues = [];
        state.chargenDiceRollAssignments = {};
        state.chargenDiceSubstitutionsUsed = 0;
        clearCharGenAttributeStepResults();
        state.chargenSelectedArrayType = "";
        state.chargenArrayAssignments = {};
      }
    }
    let activeGenerationChoice = resolveCharGenGenerationChoice(method);
    const generationChoiceOptions = [`<option value="">${t("attrgen.choice.placeholder", "Choose a method")}</option>`]
      .concat(
        generationChoices.map((choice) => {
          const selected = choice.key === activeGenerationChoice ? " selected" : "";
          return `<option value="${choice.key}"${selected}>${escapeHtml(choice.label)}</option>`;
        })
      )
      .join("");
    const description = String(method.description || "").trim()
      || t(
        "attrgen.description.empty",
        "The ruleset creator did not provide a description for Attribute Generation."
      );
    state.chargenAttributes = attributes.slice();

    view.innerHTML = `
      <section class="panel">
        <h1>${t("attrgen.title", "Attribute Generation")}</h1>
        <details class="mechanic-description-section" id="chargenMethodDescription" open>
          <summary>
            <span>${t("attrgen.description.drawer", "About Attribute Generation")}</span>
          </summary>
          <div class="mechanic-description-content">
            <p class="chargen-method-description">${escapeHtml(description)}</p>
            <div class="mechanic-description-actions">
              <button class="btn ghost" id="chargenDescriptionClose" type="button">${t("common.close", "Close")}</button>
            </div>
          </div>
        </details>
        ${generationChoices.length > 1 ? `
          <div class="field">
            <label for="chargenGenerationChoice">${t("attrgen.choice", "Choose Attribute Method")}</label>
            <select id="chargenGenerationChoice">${generationChoiceOptions}</select>
          </div>
        ` : ""}
        <div class="field hidden" id="chargenDiceSection">
          <label>${t("attrgen.rolls", "Rolled Sets")}</label>
          <div class="actions-row">
            <div class="left">
              <button class="btn" id="chargenRollBtn" type="button">${t("attrgen.roll.all", "Roll All Sets")}</button>
            </div>
            <div class="right">
              <button class="btn ghost" id="chargenChooseRollBtn" type="button">${t("attrgen.roll.choose", "Choose Set")}</button>
            </div>
          </div>
          <div class="list" id="chargenRollList"></div>
          <p id="chargenRollEmpty">${t("attrgen.rolls.empty", "No rolls yet.")}</p>
          <p class="field-hint" id="chargenRollStatus" aria-live="polite"></p>
          <div class="grid two" id="chargenSubstitutionSection">
            <div class="field">
              <label for="chargenSubstitutionIndex">${t("attrgen.dice.substitution.replace", "Replace Roll")}</label>
              <select id="chargenSubstitutionIndex"></select>
            </div>
            <div class="field">
              <label>&nbsp;</label>
              <button class="btn ghost" id="chargenSubstituteBtn" type="button">${t(
                "attrgen.dice.substitution.use",
                "Use Substitution"
              )}</button>
            </div>
          </div>
          <p class="field-hint" id="chargenSubstitutionHint"></p>
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="chargenBackToIntro" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn" id="chargenAttrContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    const descriptionDrawer = document.getElementById("chargenMethodDescription");
    const generationChoiceSelect = document.getElementById("chargenGenerationChoice");
    const diceSection = document.getElementById("chargenDiceSection");
    const rollList = document.getElementById("chargenRollList");
    const rollEmpty = document.getElementById("chargenRollEmpty");
    const rollStatus = document.getElementById("chargenRollStatus");
    const rollBtn = document.getElementById("chargenRollBtn");
    const chooseRollBtn = document.getElementById("chargenChooseRollBtn");
    const substitutionSection = document.getElementById("chargenSubstitutionSection");
    const substitutionSelect = document.getElementById("chargenSubstitutionIndex");
    const substituteBtn = document.getElementById("chargenSubstituteBtn");
    const substitutionHint = document.getElementById("chargenSubstitutionHint");
    const savedRoll = normalizeCharGenAttributeValues(state.chargenRolledAttributeValues);
    const rolls = savedRoll.length ? [savedRoll.slice()] : [];
    let selectedIndex = savedRoll.length ? 0 : -1;
    let chosenIndex = savedRoll.length ? 0 : -1;
    const substitutionValue = Math.trunc(Number(method.diceSubstitutionValue || 0));
    const maxSubstitutions = Math.max(0, Math.trunc(Number(method.maxDiceSubstitutions || 0)));

    const renderRolls = () => {
      const diceActive = isCharGenGenerationChoiceActive(method, activeGenerationChoice, "dice");
      const substitutionEnabled = diceActive && isCharGenDiceSubstitutionEnabled(method);
      const selectedRoll = selectedIndex >= 0 && selectedIndex < rolls.length ? rolls[selectedIndex] : [];
      const usedSubstitutions = Math.max(0, Math.trunc(Number(state.chargenDiceSubstitutionsUsed || 0)));
      const remainingSubstitutions = Math.max(0, maxSubstitutions - usedSubstitutions);
      diceSection.classList.toggle("hidden", !diceActive);
      rollList.innerHTML = rolls
        .map((values, index) => {
          const checked = index === selectedIndex ? "checked" : "";
          return `
            <div class="list-item">
              <label>
                <input type="radio" name="chargenRollSelect" value="${index}" ${checked}>
                ${escapeHtml(formatRollSet(index, values))}
              </label>
            </div>
          `;
        })
        .join("");
      rollEmpty.style.display = rolls.length ? "none" : "";
      rollBtn.disabled = !diceActive || !isCharGenDiceEnabled(method, attributes);
      chooseRollBtn.disabled = !diceActive || selectedIndex < 0;
      rollStatus.textContent = chosenIndex >= 0
        ? t("attrgen.roll.chosen", "Set {index} chosen for assignment.").replace("{index}", String(chosenIndex + 1))
        : "";
      substitutionSection.style.display = substitutionEnabled ? "" : "none";
      substitutionHint.style.display = substitutionEnabled ? "" : "none";
      if (substitutionEnabled) {
        substitutionSelect.innerHTML = selectedRoll
          .map((value, index) => {
            const label = t("attrgen.dice.substitution.option", "Roll {number}: {value}")
              .replace("{number}", String(index + 1))
              .replace("{value}", String(value));
            return `<option value="${index}">${escapeHtml(label)}</option>`;
          })
          .join("");
        substituteBtn.disabled = selectedIndex < 0 || !selectedRoll.length || remainingSubstitutions <= 0;
        substitutionHint.textContent = t(
          "attrgen.dice.substitution.remaining",
          "Substitution value: {value}. Remaining: {remaining} of {max}."
        )
          .replace("{value}", String(substitutionValue))
          .replace("{remaining}", String(remainingSubstitutions))
          .replace("{max}", String(maxSubstitutions));
      }
    };

    document.getElementById("chargenDescriptionClose").addEventListener("click", () => {
      descriptionDrawer.open = false;
    });
    if (generationChoiceSelect) {
      generationChoiceSelect.addEventListener("change", () => {
        state.chargenAttributeGenerationChoice = normalizeCharGenGenerationChoice(generationChoiceSelect.value);
        activeGenerationChoice = resolveCharGenGenerationChoice(method);
        state.chargenRolledAttributeValues = [];
        state.chargenDiceRollAssignments = {};
        state.chargenAttributeScores = {};
        state.chargenPointBuyBaselineScores = {};
        clearCharGenAttributeStepResults();
        state.chargenSelectedArrayType = "";
        state.chargenArrayAssignments = {};
        state.chargenDiceSubstitutionsUsed = 0;
        rolls.length = 0;
        selectedIndex = -1;
        chosenIndex = -1;
        renderRolls();
        saveCharGenDraftLocal();
      });
    }
    rollBtn.addEventListener("click", () => {
      if (!isCharGenGenerationChoiceActive(method, activeGenerationChoice, "dice") || !isCharGenDiceEnabled(method, attributes)) {
        return;
      }
      const setCount = Math.max(1, Math.trunc(Number(method.numberOfSets || 0)));
      rolls.length = 0;
      for (let index = 0; index < setCount; index += 1) {
        rolls.push(rollCharGenSet(attributes, method));
      }
      selectedIndex = rolls.length === 1 ? 0 : -1;
      chosenIndex = -1;
      state.chargenRolledAttributeValues = [];
      state.chargenDiceRollAssignments = {};
      state.chargenAttributeScores = {};
      state.chargenPointBuyBaselineScores = {};
      clearCharGenAttributeStepResults();
      state.chargenDiceSubstitutionsUsed = 0;
      saveCharGenDraftLocal();
      renderRolls();
    });
    rollList.addEventListener("change", (event) => {
      const target = event.target;
      if (!target || target.name !== "chargenRollSelect") {
        return;
      }
      selectedIndex = Number(target.value);
      renderRolls();
    });
    chooseRollBtn.addEventListener("click", () => {
      if (selectedIndex < 0 || selectedIndex >= rolls.length) {
        return;
      }
      chosenIndex = selectedIndex;
      state.chargenRolledAttributeValues = normalizeCharGenAttributeValues(rolls[selectedIndex]);
      state.chargenDiceRollAssignments = {};
      state.chargenAttributeScores = {};
      state.chargenPointBuyBaselineScores = {};
      clearCharGenAttributeStepResults();
      saveCharGenDraftLocal();
      renderRolls();
    });
    substituteBtn.addEventListener("click", () => {
      if (selectedIndex < 0 || selectedIndex >= rolls.length) {
        return;
      }
      const usedSubstitutions = Math.max(0, Math.trunc(Number(state.chargenDiceSubstitutionsUsed || 0)));
      if (usedSubstitutions >= maxSubstitutions) {
        showToast(t("attrgen.dice.substitution.none_remaining", "No substitutions remain."));
        return;
      }
      const values = rolls[selectedIndex];
      const replacementIndex = Number(substitutionSelect.value);
      if (!Number.isInteger(replacementIndex) || replacementIndex < 0 || replacementIndex >= values.length) {
        return;
      }
      values[replacementIndex] = substitutionValue;
      state.chargenDiceSubstitutionsUsed = usedSubstitutions + 1;
      state.chargenRolledAttributeValues = [];
      state.chargenDiceRollAssignments = {};
      chosenIndex = -1;
      saveCharGenDraftLocal();
      renderRolls();
    });
    document.getElementById("chargenBackToIntro").addEventListener("click", () => {
      saveCharGenDraftLocal();
      renderCharGenIntro();
    });
    document.getElementById("chargenAttrContinue").addEventListener("click", () => {
      if (generationChoices.length > 1 && !resolveCharGenGenerationChoice(method)) {
        showToast(t("attrgen.choice.required", "Choose an attribute generation method."));
        return;
      }
      if (
        isCharGenGenerationChoiceActive(method, resolveCharGenGenerationChoice(method), "dice")
        && normalizeCharGenAttributeValues(state.chargenRolledAttributeValues).length !== attributes.length
      ) {
        showToast(t("attrgen.roll.required", "Roll and choose a set before continuing."));
        return;
      }
      saveCharGenDraftLocal();
      renderCharGenAttributeAssignment();
    });

    renderRolls();
    saveCharGenDraftLocal();
  } catch (error) {
    renderCharGenLoadError(error, renderCharGenAttributes);
  }
}

async function renderCharGenAttributeAssignment() {
  if (!state.draftId) {
    renderCharGenUpload();
    return;
  }
  setMode("chargen");
  setStep("chargen-attribute-assignment");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const data = await api("GET", `/api/drafts/${state.draftId}/chargen/attribute-generation`);
    const attributes = Array.isArray(data.attributes) ? data.attributes : [];
    const method = data || {};
    const initialGenerationChoice = resolveCharGenGenerationChoice(method);
    const generationChoices = getCharGenGenerationChoices(method);
    if (generationChoices.length > 1 && !initialGenerationChoice) {
      renderCharGenAttributes();
      return;
    }
    if (initialGenerationChoice) {
      state.chargenAttributeGenerationChoice = initialGenerationChoice;
    }
    const description = String(method.description || "").trim()
      || t(
        "attrgen.description.empty",
        "The ruleset creator did not provide a description for Attribute Generation."
      );
    state.chargenAttributes = attributes.slice();

    view.innerHTML = `
      <section class="panel">
        <h1>${t("attrgen.assignment.title", "Assign Attribute Values")}</h1>
        <details class="mechanic-description-section" id="chargenAssignmentDescription">
          <summary>
            <span>${t("attrgen.description.drawer", "About Attribute Generation")}</span>
          </summary>
          <div class="mechanic-description-content">
            <p class="chargen-method-description">${escapeHtml(description)}</p>
            <div class="mechanic-description-actions">
              <button class="btn ghost" id="chargenAssignmentDescriptionClose" type="button">${t("common.close", "Close")}</button>
            </div>
          </div>
        </details>
        <div class="field" id="chargenArraySection">
          <label>${t("attrgen.type.standard_array", "Standard Array/Base Scores")}</label>
          <select id="chargenArraySelect"></select>
          <p class="field-hint" id="chargenArrayEmpty">${t("common.none", "None")}</p>
        </div>
        <div class="field hidden" id="chargenArrayAssignmentSection">
          <label>${t("attrgen.array.available", "Available Array Values")}</label>
          <p class="field-hint" id="chargenArrayAssignmentHint"></p>
          <div class="list" id="chargenAvailableArrayValues"></div>
        </div>
        <div id="chargenSecondStepStage" class="hidden">
          <div class="field hidden" id="chargenDiceAssignmentSection">
            <label id="chargenDiceAssignmentTitle">${t("attrgen.roll.available", "Available Rolls")}</label>
            <p class="field-hint" id="chargenDiceAssignmentHint"></p>
            <div class="list" id="chargenAvailableRolls"></div>
          </div>
          <div class="field hidden" id="chargenDiceSection" aria-hidden="true">
            <label>${t("attrgen.rolls", "Rolled Sets")}</label>
            <div class="actions-row">
              <div class="left">
                <button class="btn" id="chargenRollBtn" type="button">${t("attrgen.roll", "Roll Dice")}</button>
              </div>
              <div class="right">
                <button class="btn ghost" id="chargenApplyBtn" type="button">${t("attrgen.apply", "Apply Set")}</button>
              </div>
            </div>
            <div class="list" id="chargenRollList"></div>
            <p id="chargenRollEmpty">${t("attrgen.rolls.empty", "No rolls yet.")}</p>
            <div class="grid two" id="chargenSubstitutionSection">
              <div class="field">
                <label for="chargenSubstitutionIndex">${t("attrgen.dice.substitution.replace", "Replace Roll")}</label>
                <select id="chargenSubstitutionIndex"></select>
              </div>
              <div class="field">
                <label>&nbsp;</label>
                <button class="btn ghost" id="chargenSubstituteBtn" type="button">${t(
                  "attrgen.dice.substitution.use",
                  "Use Substitution"
                )}</button>
              </div>
            </div>
            <p class="field-hint" id="chargenSubstitutionHint"></p>
          </div>
        </div>
        <div class="field">
          <label>${t("attrgen.attributes", "Attributes")}</label>
          <div class="grid two" id="chargenAttributes"></div>
          <p id="chargenAttributesEmpty">${t("attrgen.attributes.empty", "No attributes available.")}</p>
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="chargenBackToGeneration" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn" id="chargenAttrContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    const rolls = [];
    let selectedIndex = -1;
    const rollList = document.getElementById("chargenRollList");
    const rollEmpty = document.getElementById("chargenRollEmpty");
    const rollBtn = document.getElementById("chargenRollBtn");
    const applyBtn = document.getElementById("chargenApplyBtn");
    const substitutionSection = document.getElementById("chargenSubstitutionSection");
    const substitutionSelect = document.getElementById("chargenSubstitutionIndex");
    const substituteBtn = document.getElementById("chargenSubstituteBtn");
    const substitutionHint = document.getElementById("chargenSubstitutionHint");
    const arraySection = document.getElementById("chargenArraySection");
    const arraySelect = document.getElementById("chargenArraySelect");
    const arrayEmpty = document.getElementById("chargenArrayEmpty");
    const arrayAssignmentSection = document.getElementById("chargenArrayAssignmentSection");
    const arrayAssignmentHint = document.getElementById("chargenArrayAssignmentHint");
    const availableArrayValues = document.getElementById("chargenAvailableArrayValues");
    const secondStepStage = document.getElementById("chargenSecondStepStage");
    const diceAssignmentSection = document.getElementById("chargenDiceAssignmentSection");
    const diceAssignmentTitle = document.getElementById("chargenDiceAssignmentTitle");
    const diceAssignmentHint = document.getElementById("chargenDiceAssignmentHint");
    const availableRolls = document.getElementById("chargenAvailableRolls");
    const diceSection = document.getElementById("chargenDiceSection");
    const generationChoiceSelect = document.getElementById("chargenGenerationChoice");
    const attributesGrid = document.getElementById("chargenAttributes");
    const attributesEmpty = document.getElementById("chargenAttributesEmpty");
    const continueBtn = document.getElementById("chargenAttrContinue");

    const attributeInputs = buildCharGenAttributes(attributes, method, attributesGrid);
    const hasSavedScores = Object.keys(state.chargenAttributeScores || {}).length > 0;
    let activeGenerationChoice = initialGenerationChoice;
    const standardActive = isCharGenGenerationChoiceActive(method, activeGenerationChoice, "standard_array");
    const diceActive = isCharGenGenerationChoiceActive(method, activeGenerationChoice, "dice");
    let standardComplete = !standardActive;
    let arrayController = null;
    let diceAssignmentController = null;
    applyCharGenSavedScores(attributeInputs);
    attributeInputs.forEach((entry) => {
      entry.input.readOnly = true;
    });
    attributesEmpty.style.display = attributes.length ? "none" : "";

    const setDiceAssignmentControlsVisible = (visible) => {
      document.querySelectorAll(".dice-assignment-controls").forEach((controls) => {
        controls.classList.toggle("hidden", !visible);
      });
    };

    const initializeDiceAssignment = () => {
      if (!diceActive || !standardComplete || diceAssignmentController) {
        return;
      }
      diceAssignmentController = wireCharGenDiceAssignmentUI({
        method,
        attributes,
        inputs: attributeInputs,
        section: diceAssignmentSection,
        title: diceAssignmentTitle,
        hint: diceAssignmentHint,
        availableList: availableRolls,
        continueButton: continueBtn,
        hasSavedScores: standardActive ? false : hasSavedScores,
      });
      diceAssignmentController.render();
    };

    const updateStageVisibility = (refreshDiceBaseline = false) => {
      const revealDice = diceActive && standardComplete;
      secondStepStage.classList.toggle("hidden", !revealDice);
      if (revealDice) {
        initializeDiceAssignment();
        if (refreshDiceBaseline && diceAssignmentController) {
          diceAssignmentController.resetBaseline();
          diceAssignmentController.render();
        }
      }
      setDiceAssignmentControlsVisible(revealDice);
      continueBtn.disabled = !standardComplete
        || (diceActive && (!diceAssignmentController || !diceAssignmentController.isComplete()));
    };

    if (standardActive) {
      arrayController = wireCharGenArrayUI({
        method,
        attributes,
        inputs: attributeInputs,
        section: arraySection,
        select: arraySelect,
        emptyLabel: arrayEmpty,
        assignmentSection: arrayAssignmentSection,
        assignmentHint: arrayAssignmentHint,
        availableList: availableArrayValues,
        onStatusChange: (complete, scores) => {
          standardComplete = complete;
          if (complete) {
            recordCharGenStepResult(method, "standard_array", scores);
          }
          updateStageVisibility(complete && Boolean(diceAssignmentController));
        },
      });
    } else {
      arraySection.classList.add("hidden");
      arrayAssignmentSection.classList.add("hidden");
    }

    const substitutionValue = Math.trunc(Number(method.diceSubstitutionValue || 0));
    const maxSubstitutions = Math.max(0, Math.trunc(Number(method.maxDiceSubstitutions || 0)));
    applyBtn.disabled = true;

    let rollBaselineValues = captureCharGenAttributeValues(attributeInputs);
    attributeInputs.forEach((entry) => {
      entry.input.addEventListener("change", () => {
        if (!rolls.length) {
          rollBaselineValues = captureCharGenAttributeValues(attributeInputs);
        }
      });
    });
    const renderRolls = () => {
      const diceActive = isCharGenGenerationChoiceActive(method, activeGenerationChoice, "dice");
      const substitutionEnabled = diceActive && isCharGenDiceSubstitutionEnabled(method);
      const selectedRoll = selectedIndex >= 0 && selectedIndex < rolls.length ? rolls[selectedIndex] : [];
      const usedSubstitutions = Math.max(0, Math.trunc(Number(state.chargenDiceSubstitutionsUsed || 0)));
      const remainingSubstitutions = Math.max(0, maxSubstitutions - usedSubstitutions);
      rollList.innerHTML = rolls
        .map((values, index) => {
          const label = formatRollSet(index, values);
          const checked = index === selectedIndex ? "checked" : "";
          return `
            <div class="list-item">
              <label>
                <input type="radio" name="chargenRollSelect" value="${index}" ${checked}>
                ${label}
              </label>
            </div>
          `;
        })
        .join("");
      rollEmpty.style.display = rolls.length ? "none" : "";
      rollBtn.disabled = !diceActive || !isCharGenDiceEnabled(method, attributes);
      applyBtn.disabled = !diceActive || selectedIndex < 0;
      substitutionSection.style.display = substitutionEnabled ? "" : "none";
      substitutionHint.style.display = substitutionEnabled ? "" : "none";
      if (substitutionEnabled) {
        substitutionSelect.innerHTML = selectedRoll
          .map((value, index) => {
            const label = t("attrgen.dice.substitution.option", "Roll {number}: {value}")
              .replace("{number}", String(index + 1))
              .replace("{value}", String(value));
            return `<option value="${index}">${escapeHtml(label)}</option>`;
          })
          .join("");
        substituteBtn.disabled = selectedIndex < 0 || !selectedRoll.length || remainingSubstitutions <= 0;
        substitutionHint.textContent = t(
          "attrgen.dice.substitution.remaining",
          "Substitution value: {value}. Remaining: {remaining} of {max}."
        )
          .replace("{value}", String(substitutionValue))
          .replace("{remaining}", String(remainingSubstitutions))
          .replace("{max}", String(maxSubstitutions));
      }
    };

    const updateGenerationChoiceUi = () => {
      activeGenerationChoice = resolveCharGenGenerationChoice(method);
      arraySection.classList.toggle("hidden", !standardActive);
      diceSection.classList.add("hidden");
      renderRolls();
      updateStageVisibility();
    };

    if (generationChoiceSelect) {
      generationChoiceSelect.addEventListener("change", () => {
        state.chargenAttributeGenerationChoice = normalizeCharGenGenerationChoice(generationChoiceSelect.value);
        state.chargenAttributeScores = {};
        clearCharGenAttributeStepResults();
        state.chargenSelectedArrayType = "";
        state.chargenArrayAssignments = {};
        state.chargenDiceSubstitutionsUsed = 0;
        rolls.length = 0;
        selectedIndex = -1;
        resetCharGenAttributeInputs(attributeInputs, method);
        rollBaselineValues = captureCharGenAttributeValues(attributeInputs);
        updateGenerationChoiceUi();
        saveCharGenDraftLocal();
      });
    }

    rollBtn.addEventListener("click", () => {
      if (!isCharGenGenerationChoiceActive(method, activeGenerationChoice, "dice") || !isCharGenDiceEnabled(method, attributes)) {
        return;
      }
      const maxSets = Number(method.numberOfSets || 0);
      if (maxSets > 0 && rolls.length >= maxSets) {
        rolls.length = 0;
        selectedIndex = -1;
        rollBaselineValues = captureCharGenAttributeValues(attributeInputs);
      }
      if (!rolls.length) {
        rollBaselineValues = captureCharGenAttributeValues(attributeInputs);
      }
      const values = rollCharGenSet(attributes, method);
      rolls.push(values);
      selectedIndex = rolls.length - 1;
      renderRolls();
    });

    rollList.addEventListener("change", (event) => {
      const target = event.target;
      if (!target || target.name !== "chargenRollSelect") {
        return;
      }
      selectedIndex = Number(target.value);
      renderRolls();
    });

    applyBtn.addEventListener("click", () => {
      if (selectedIndex < 0 || selectedIndex >= rolls.length) {
        return;
      }
      const values = rolls[selectedIndex];
      const addToBase = shouldAddCharGenRollToBase(method);
      applyCharGenValues(attributeInputs, values, addToBase ? rollBaselineValues : null);
    });

    substituteBtn.addEventListener("click", () => {
      if (!isCharGenDiceSubstitutionEnabled(method) || selectedIndex < 0 || selectedIndex >= rolls.length) {
        return;
      }
      const usedSubstitutions = Math.max(0, Math.trunc(Number(state.chargenDiceSubstitutionsUsed || 0)));
      if (usedSubstitutions >= maxSubstitutions) {
        showToast(t("attrgen.dice.substitution.none_remaining", "No substitutions remain."));
        return;
      }
      const values = rolls[selectedIndex];
      const replacementIndex = Number(substitutionSelect.value);
      if (!Array.isArray(values) || !Number.isInteger(replacementIndex) || replacementIndex < 0 || replacementIndex >= values.length) {
        return;
      }
      values[replacementIndex] = substitutionValue;
      state.chargenDiceSubstitutionsUsed = usedSubstitutions + 1;
      state.chargenAttributeScores = collectCharGenAttributeScores(attributeInputs);
      state.chargenAttributes = attributes.slice();
      saveCharGenDraftLocal();
      renderRolls();
    });

    document.getElementById("chargenAssignmentDescriptionClose").addEventListener("click", () => {
      document.getElementById("chargenAssignmentDescription").open = false;
    });
    document.getElementById("chargenBackToGeneration").addEventListener("click", () => {
      const arrayValid = !arrayController || arrayController.isComplete();
      const diceValid = !diceAssignmentController || diceAssignmentController.isComplete();
      if (arrayValid && diceValid) {
        state.chargenAttributeScores = collectCharGenAttributeScores(attributeInputs);
      }
      state.chargenAttributes = attributes.slice();
      saveCharGenDraftLocal();
      renderCharGenAttributes();
    });
    continueBtn.addEventListener("click", () => {
      if (arrayController && !arrayController.isComplete()) {
        showToast(t("attrgen.array.assignment.required", "Assign every array value before continuing."));
        return;
      }
      if (diceAssignmentController && !diceAssignmentController.isComplete()) {
        showToast(t("attrgen.roll.assignment.required", "Assign every roll before continuing."));
        return;
      }
      const scores = collectCharGenAttributeScores(attributeInputs);
      const activeChoice = resolveCharGenGenerationChoice(method);
      const diceActive = isCharGenGenerationChoiceActive(method, activeChoice, "dice");
      if (diceActive) {
        recordCharGenStepResult(method, "dice", scores);
      }
      state.chargenAttributeScores = scores;
      state.chargenAttributes = attributes.slice();
      state.chargenPointBuyBaselineScores = { ...scores };
      saveCharGenDraftLocal();
      if (isCharGenGenerationChoiceActive(method, resolveCharGenGenerationChoice(method), "point_buy")) {
        renderCharGenPointsBuy();
        return;
      }
      if (getCharGenChooseStep(method)) {
        renderCharGenAttributeResultChoice();
        return;
      }
      renderCharGenRaces();
    });

    updateGenerationChoiceUi();
    renderRolls();
    saveCharGenDraftLocal();
  } catch (error) {
    renderCharGenLoadError(error, renderCharGenAttributeAssignment);
  }
}

async function renderCharGenPointsBuy() {
  if (!state.draftId) {
    renderCharGenUpload();
    return;
  }
  setMode("chargen");
  setStep("chargen-points-buy");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const data = await api("GET", `/api/drafts/${state.draftId}/chargen/attribute-generation`);
    const attributes = Array.isArray(data.attributes) ? data.attributes : [];
    const method = data || {};

    const choosingBetweenResults = Boolean(getCharGenChooseStep(method));
    const pointStepIndex = getCharGenGenerationStepIndex(method, "point_buy");
    const savedStepResults = normalizeCharGenAttributeStepResults(state.chargenAttributeStepResults);
    const scores = choosingBetweenResults
      ? savedStepResults[String(pointStepIndex)] || {}
      : state.chargenAttributeScores || {};
    const savedBaseline = pointStepIndex > 0 ? savedStepResults[String(pointStepIndex - 1)] || {} : {};
    const transientBaseline = state.chargenPointBuyBaselineScores || {};
    const baseline = Object.keys(transientBaseline).length ? transientBaseline : savedBaseline;
    const applicationMode = resolveCharGenPointBuyApplicationMode(method);
    const usesBaseline = shouldAddCharGenPointBuyToBaseScores(method);

    view.innerHTML = `
      <section class="panel">
        <h1>${t("attrgen.point.title", "Point Buy")}</h1>
        <p class="field-hint">${t(
          "attrgen.point.intro",
          "Set the starting points and bounds for point-buy attribute generation."
        )}</p>
        <div class="grid two">
          <div class="field">
            <label>${t("attrgen.point.base_points", "Base Points")}</label>
            <input type="number" id="chargenPointBudget" value="${Number(method.basePoints || 0)}" readonly>
          </div>
          <div class="field">
            <label>${t("attrgen.point.base_value", "Base Attribute Value")}</label>
            <input type="number" id="chargenPointBaseValue" value="${Number(method.baseAttributeValue || 0)}" readonly>
          </div>
        </div>
        <div class="field">
          <label>${t("attrgen.point.summary", "Point Buy")}</label>
          <div class="list" id="chargenPointSummary"></div>
        </div>
        <div class="field">
          <label>${t("attrgen.attributes", "Attributes")}</label>
          <div class="list" id="chargenPointList"></div>
          <p id="chargenPointEmpty">${t("attrgen.attributes.empty", "No attributes available.")}</p>
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="chargenPointBack" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn" id="chargenPointContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    const list = document.getElementById("chargenPointList");
    const empty = document.getElementById("chargenPointEmpty");
    const summary = document.getElementById("chargenPointSummary");
    const continueBtn = document.getElementById("chargenPointContinue");

    empty.style.display = attributes.length ? "none" : "";

    const rows = [];
    const initialPointScores = {};
    list.innerHTML = attributes
      .map((attribute, index) => {
        const attributeId = String(attribute.id || "").trim();
        const label = attribute.displayName || attribute.name || `Attribute ${index + 1}`;
        const id = `chargenPointAttr${index}`;
        const minusId = `chargenPointMinus${index}`;
        const plusId = `chargenPointPlus${index}`;
        const resetId = `chargenPointReset${index}`;
        const min = resolveCharGenMin(attribute, method);
        const max = resolveCharGenMax(attribute, method);
        const base = resolveCharGenBase(method);
        const current = clampCharGen(Number(scores[attributeId] ?? base), min, max);
        initialPointScores[attributeId] = current;
        const baselineValue = clampCharGen(Number(baseline[attributeId] ?? base), min, max);
        rows.push({ attributeId, inputId: id, minusId, plusId, resetId, min, max, baselineValue });
        const baselineBadge = usesBaseline ? `<span class="badge">${t("attrgen.point.baseline", "Baseline")}: ${baselineValue}</span>` : "";
        return `
          <div class="list-item">
            <div class="row split">
              <div class="stack">
                <strong>${escapeHtml(label)}</strong>
                ${baselineBadge}
              </div>
              <div class="row">
                <button class="btn ghost small" id="${minusId}" type="button">-</button>
                <input type="number" id="${id}" min="${min}" max="${max}" step="1" value="${current}">
                <button class="btn ghost small" id="${plusId}" type="button">+</button>
                <button class="btn ghost small" id="${resetId}" type="button">${t("common.reset", "Reset")}</button>
              </div>
            </div>
          </div>
        `;
      })
      .join("");
    state.chargenAttributeScores = { ...initialPointScores };

    const pointCostMap = buildCharGenPointCostMap(method.pointCosts);
    const budget = Math.max(0, Number(method.basePoints || 0));
    const minSpend = Math.max(0, Number(method.minimumPointsToSpend || 0));

    const resolveCost = (value) => resolveCharGenPointCost(value, method, pointCostMap);

    const computeSpent = () => {
      let spent = 0;
      rows.forEach((row) => {
        const input = document.getElementById(row.inputId);
        const value = clampCharGen(Number(input.value || 0), row.min, row.max);
        if (applicationMode === "add") {
          const addedValue = value - row.baselineValue;
          spent += resolveCost(addedValue) - resolveCost(0);
        } else if (applicationMode === "spend") {
          spent += resolveCost(value) - resolveCost(row.baselineValue);
        } else {
          spent += resolveCost(value);
        }
      });
      return spent;
    };

    const renderSummary = () => {
      const spent = computeSpent();
      const remaining = budget - spent;
      const meetsMinimum = spent >= minSpend;
      const allowed = remaining >= 0 && meetsMinimum;
      summary.innerHTML = `
        <div class="list-item">
          ${t("attrgen.point.spent", "Spent")}: <strong>${spent}</strong>
          , ${t("attrgen.point.remaining", "Remaining")}: <strong>${remaining}</strong>
          , ${t("attrgen.point.minimum", "Minimum Spend")}: <strong>${minSpend}</strong>
        </div>
      `;
      continueBtn.disabled = !allowed;
    };

    const updateScore = (attributeId, value) => {
      const safeId = String(attributeId || "").trim();
      if (!safeId) {
        return;
      }
      state.chargenAttributeScores = state.chargenAttributeScores || {};
      state.chargenAttributeScores[safeId] = Number(value || 0);
      if (choosingBetweenResults) {
        recordCharGenStepResult(method, "point_buy", state.chargenAttributeScores);
      }
      saveCharGenDraftLocal();
    };

    rows.forEach((row) => {
      const input = document.getElementById(row.inputId);
      const minus = document.getElementById(row.minusId);
      const plus = document.getElementById(row.plusId);
      const reset = document.getElementById(row.resetId);

      const clampInput = () => {
        const next = clampCharGen(Number(input.value || 0), row.min, row.max);
        input.value = String(next);
        updateScore(row.attributeId, next);
        renderSummary();
      };

      input.addEventListener("change", clampInput);
      minus.addEventListener("click", () => {
        input.value = String(clampCharGen(Number(input.value || 0) - 1, row.min, row.max));
        clampInput();
      });
      plus.addEventListener("click", () => {
        input.value = String(clampCharGen(Number(input.value || 0) + 1, row.min, row.max));
        clampInput();
      });
      reset.addEventListener("click", () => {
        const next = usesBaseline ? row.baselineValue : resolveCharGenBase(method);
        input.value = String(clampCharGen(Number(next || 0), row.min, row.max));
        clampInput();
      });
    });

    renderSummary();

    document.getElementById("chargenPointBack").addEventListener("click", () => {
      saveCharGenDraftLocal();
      renderCharGenAttributeAssignment();
    });
    continueBtn.addEventListener("click", () => {
      if (choosingBetweenResults) {
        recordCharGenStepResult(method, "point_buy", state.chargenAttributeScores);
      }
      saveCharGenDraftLocal();
      if (choosingBetweenResults) {
        renderCharGenAttributeResultChoice();
        return;
      }
      renderCharGenRaces();
    });
  } catch (error) {
    renderCharGenLoadError(error, renderCharGenPointsBuy);
  }
}

async function renderCharGenAttributeResultChoice() {
  if (!state.draftId) {
    renderCharGenUpload();
    return;
  }
  setMode("chargen");
  setStep("chargen-attribute-result-choice");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const method = await api("GET", `/api/drafts/${state.draftId}/chargen/attribute-generation`);
    const attributes = Array.isArray(method.attributes) ? method.attributes : [];
    const entry = getCharGenSelectedGenerationEntry(method);
    const chooseStep = getCharGenChooseStep(method);
    if (!entry || !chooseStep || entry.steps.length < 2) {
      renderCharGenRaces();
      return;
    }
    const stepResults = normalizeCharGenAttributeStepResults(state.chargenAttributeStepResults);
    const requiredAttributeIds = attributes
      .map((attribute) => String(attribute.id || "").trim())
      .filter(Boolean);
    const completedSteps = entry.steps.slice(0, 2).every((step, index) => {
      const scores = stepResults[String(index)] || {};
      return requiredAttributeIds.every((attributeId) => Number.isFinite(Number(scores[attributeId])));
    });
    if (!completedSteps) {
      showToast(t("attrgen.result_choice.incomplete", "Complete both generation results before choosing."));
      if (isCharGenGenerationChoiceActive(method, resolveCharGenGenerationChoice(method), "point_buy")) {
        renderCharGenPointsBuy();
      } else {
        renderCharGenAttributeAssignment();
      }
      return;
    }

    const selectedChoice = String(state.chargenAttributeResultChoice || "");
    const resultCards = entry.steps.slice(0, 2).map((step, stepIndex) => {
      const scores = stepResults[String(stepIndex)] || {};
      const checked = selectedChoice === String(stepIndex) ? " checked" : "";
      const scoreRows = attributes.map((attribute, attributeIndex) => {
        const attributeId = String(attribute.id || "").trim();
        const label = attribute.displayName || attribute.name || `Attribute ${attributeIndex + 1}`;
        return `<div class="list-item"><span>${escapeHtml(label)}</span><strong>${escapeHtml(scores[attributeId])}</strong></div>`;
      }).join("");
      const methodLabel = formatCharGenType(step.methodType);
      return `
        <label class="edit-section attribute-result-choice-card">
          <span class="row">
            <input type="radio" name="chargenAttributeResultChoice" value="${stepIndex}"${checked}>
            <strong>${escapeHtml(t("attrgen.result_choice.step", "Step {number}: {method}")
              .replace("{number}", String(stepIndex + 1))
              .replace("{method}", methodLabel))}</strong>
          </span>
          <div class="list">${scoreRows}</div>
        </label>
      `;
    }).join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${t("attrgen.result_choice.title", "Choose Attribute Results")}</h1>
        <p class="field-hint">${t(
          "attrgen.result_choice.help",
          "Both results are complete. Choose the set of scores this character will use."
        )}</p>
        <div class="grid two">${resultCards}</div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="chargenResultChoiceBack" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn" id="chargenResultChoiceContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    document.querySelectorAll('input[name="chargenAttributeResultChoice"]').forEach((input) => {
      input.addEventListener("change", () => {
        state.chargenAttributeResultChoice = String(input.value || "");
        saveCharGenDraftLocal();
      });
    });
    document.getElementById("chargenResultChoiceBack").addEventListener("click", () => {
      saveCharGenDraftLocal();
      if (isCharGenGenerationChoiceActive(method, resolveCharGenGenerationChoice(method), "point_buy")) {
        renderCharGenPointsBuy();
      } else {
        renderCharGenAttributeAssignment();
      }
    });
    document.getElementById("chargenResultChoiceContinue").addEventListener("click", () => {
      const choice = String(state.chargenAttributeResultChoice || "");
      const scores = stepResults[choice];
      if (!scores) {
        showToast(t("attrgen.result_choice.required", "Choose one result before continuing."));
        return;
      }
      state.chargenAttributeScores = { ...scores };
      saveCharGenDraftLocal();
      renderCharGenRaces();
    });
  } catch (error) {
    renderCharGenLoadError(error, renderCharGenAttributeResultChoice);
  }
}

async function renderCharGenRaces() {
  if (!state.draftId) {
    renderCharGenUpload();
    return;
  }
  setMode("chargen");
  setStep("chargen-races");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const data = await api("GET", `/api/drafts/${state.draftId}/races`);
    const races = Array.isArray(data.races) ? data.races : [];
    const hasRaces = races.length > 0;
    const emptyNotice = t(
      "web.chargen.races.empty_system",
      "This game system does not use races, proceed to next screen."
    );

    view.innerHTML = `
      <section class="panel">
        <h1>${t("races.title", "Races")}</h1>
        ${hasRaces ? "" : `<p class="field-hint">${emptyNotice}</p>`}
        <div class="field">
          <label for="chargenRaceSelect">${t("races.select", "Select Race")}</label>
          <select id="chargenRaceSelect" ${hasRaces ? "" : "disabled"}></select>
        </div>
        <div class="field">
          <label for="chargenRaceDescription">${t("common.description", "Description")}</label>
          <textarea id="chargenRaceDescription" readonly>${t("common.no_data", "No data available.")}</textarea>
        </div>
        <div class="field">
          <label for="chargenRaceTraits">${t("races.traits.title", "Racial Traits")}</label>
          <textarea id="chargenRaceTraits" readonly>${t("races.traits.none", "No traits assigned.")}</textarea>
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="chargenBackToAttributes" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn" id="chargenRaceContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    const select = document.getElementById("chargenRaceSelect");
    const description = document.getElementById("chargenRaceDescription");
    const traits = document.getElementById("chargenRaceTraits");
    const options = [`<option value="">${t("races.select", "Select Race")}</option>`]
      .concat(
        races.map((race) => {
          const value = escapeHtml(race.id || "");
          const label = escapeHtml(race.name || t("races.untitled", "Untitled"));
          return `<option value="${value}">${label}</option>`;
        })
      )
      .join("");
    select.innerHTML = options;

    if (state.chargenRaceId) {
      select.value = state.chargenRaceId;
    }

    const updateDescription = () => {
      const id = String(select.value || "");
      const match = races.find((race) => String(race.id || "") === id);
      const text = match && match.description ? match.description : t("common.no_data", "No data available.");
      description.value = text;
      const entries = match && Array.isArray(match.racialSkillNames) ? match.racialSkillNames : match && match.racialSkillIds;
      const lines = Array.isArray(entries) ? entries.map((entry) => String(entry || "").trim()).filter(Boolean) : [];
      traits.value = lines.length ? lines.join("\n") : t("races.traits.none", "No traits assigned.");
    };
    updateDescription();
    select.addEventListener("change", updateDescription);

    document.getElementById("chargenBackToAttributes").addEventListener("click", () => {
      state.chargenRaceId = String(select.value || "");
      saveCharGenDraftLocal();
      if (String(state.chargenAttributeResultChoice || "")) {
        renderCharGenAttributeResultChoice();
        return;
      }
      renderCharGenAttributeAssignment();
    });
    document.getElementById("chargenRaceContinue").addEventListener("click", () => {
      const id = String(select.value || "");
      const selected = races.find((race) => String(race.id || "") === id);
      if (selected) {
        const message = buildCharGenRaceRequirementMessage(selected);
        if (message) {
          alert(message);
          return;
        }
      }
      state.chargenRaceId = id;
      saveCharGenDraftLocal();
      renderCharGenClasses();
    });
  } catch (error) {
    renderCharGenLoadError(error, renderCharGenRaces);
  }
}

async function renderCharGenClasses() {
  if (!state.draftId) {
    renderCharGenUpload();
    return;
  }
  setMode("chargen");
  setStep("chargen-classes");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const data = await api("GET", `/api/drafts/${state.draftId}/classes`);
    const classes = Array.isArray(data.classes) ? data.classes : [];
    const hasClasses = classes.length > 0;
    const emptyNotice = t(
      "web.chargen.classes.empty_system",
      "This game system does not use classes, proceed to next screen."
    );

    view.innerHTML = `
      <section class="panel">
        <h1>${t("classes.title", "Classes")}</h1>
        ${hasClasses ? "" : `<p class="field-hint">${emptyNotice}</p>`}
        <div class="field">
          <label for="chargenClassSelect">${t("classes.select", "Select Class")}</label>
          <select id="chargenClassSelect" ${hasClasses ? "" : "disabled"}></select>
        </div>
        <div class="field">
          <label for="chargenClassDescription">${t("common.description", "Description")}</label>
          <textarea id="chargenClassDescription" readonly>${t("common.no_data", "No data available.")}</textarea>
        </div>
        <div class="field">
          <label for="chargenClassSkills">${t("classes.skills", "Class Skills")}</label>
          <textarea id="chargenClassSkills" readonly>${t("common.none", "None")}</textarea>
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="chargenBackToRaces" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn" id="chargenClassContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    const select = document.getElementById("chargenClassSelect");
    const description = document.getElementById("chargenClassDescription");
    const skills = document.getElementById("chargenClassSkills");
    const options = [`<option value="">${t("classes.select", "Select Class")}</option>`]
      .concat(
        classes.map((entry) => {
          const value = escapeHtml(entry.id || "");
          const label = escapeHtml(entry.name || t("classes.untitled", "Untitled"));
          return `<option value="${value}">${label}</option>`;
        })
      )
      .join("");
    select.innerHTML = options;

    if (state.chargenClassId) {
      select.value = state.chargenClassId;
    }

    const updateDescription = () => {
      const id = String(select.value || "");
      const match = classes.find((entry) => String(entry.id || "") === id);
      const text = match && match.description ? match.description : t("common.no_data", "No data available.");
      description.value = text;
      const entries = match && Array.isArray(match.classSkillNames) ? match.classSkillNames : match && match.classSkillIds;
      const lines = Array.isArray(entries) ? entries.map((entry) => String(entry || "").trim()).filter(Boolean) : [];
      skills.value = lines.length ? lines.join("\n") : t("common.none", "None");
    };
    updateDescription();
    select.addEventListener("change", updateDescription);

    document.getElementById("chargenBackToRaces").addEventListener("click", () => {
      state.chargenClassId = String(select.value || "");
      saveCharGenDraftLocal();
      renderCharGenRaces();
    });
    document.getElementById("chargenClassContinue").addEventListener("click", () => {
      state.chargenClassId = String(select.value || "");
      const characterClass = classes.find((entry) => String(entry.id || "") === state.chargenClassId);
      state.chargenClassSkillRanks = buildCharGenClassSkillRanks(characterClass);
      saveCharGenDraftLocal();
      renderCharGenSkills();
    });
  } catch (error) {
    renderCharGenLoadError(error, renderCharGenClasses);
  }
}

async function renderCharGenSkills() {
  if (!state.draftId) {
    renderCharGenUpload();
    return;
  }
  setMode("chargen");
  setStep("chargen-skills");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const [skillData, classData] = await Promise.all([
      api("GET", `/api/drafts/${state.draftId}/skills`),
      api("GET", `/api/drafts/${state.draftId}/classes`),
    ]);
    const skills = sortByLabel(skillData.skills || [], (skill) => skill.displayName || skill.name || "");
    const classes = Array.isArray(classData.classes) ? classData.classes : [];
    const characterClass = classes.find((entry) => String(entry.id || "") === String(state.chargenClassId || ""));
    if (!Object.keys(state.chargenClassSkillRanks || {}).length) {
      state.chargenClassSkillRanks = buildCharGenClassSkillRanks(characterClass);
    }
    const classSkillRanks = state.chargenClassSkillRanks || {};
    const selectedRanks = state.chargenSelectedSkillRanks || {};
    const progression = skillData.progression || {};
    const skillPointText = buildCharGenSkillPointSummary(characterClass, progression);
    const list = skills
      .map((skill, index) => {
        const id = String(skill.id || "").trim();
        const label = skill.displayName || skill.name || t("skills.untitled", "Untitled");
        const classBadge = classSkillRanks[id] !== undefined
          ? ` <span class="badge">${t("classes.skills", "Class Skills")}</span>`
          : "";
        const category = skill.category ? ` <span class="badge">${escapeHtml(skill.category)}</span>` : "";
        const rank = Math.max(0, Number(selectedRanks[id] || 0));
        return `
          <div class="list-item">
            <div>
              <strong>${escapeHtml(label)}</strong>${classBadge}${category}
            </div>
            <div class="field inline-field">
              <label for="chargenSkillRank${index}">${t("skills.rank", "Rank")}</label>
              <input type="number" id="chargenSkillRank${index}" min="0" max="99" step="1"
                value="${rank}" data-chargen-skill-rank="${escapeHtml(id)}">
            </div>
          </div>
        `;
      })
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${t("skills.title", "Skills")}</h1>
        <p class="field-hint">${escapeHtml(skillPointText)}</p>
        <div class="list">
          ${list || `<div class="list-item">${t("skills.none", "No skills yet.")}</div>`}
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="chargenSkillsBack" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn" id="chargenSkillsContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    const save = () => {
      state.chargenSelectedSkillRanks = collectCharGenSkillRanks();
      state.chargenClassSkillRanks = classSkillRanks;
      saveCharGenDraftLocal();
    };
    document.querySelectorAll("[data-chargen-skill-rank]").forEach((input) => {
      input.addEventListener("change", save);
    });
    document.getElementById("chargenSkillsBack").addEventListener("click", () => {
      save();
      renderCharGenClasses();
    });
    document.getElementById("chargenSkillsContinue").addEventListener("click", () => {
      save();
      renderCharGenSpells();
    });
  } catch (error) {
    renderCharGenLoadError(error, renderCharGenSkills);
  }
}

async function renderCharGenSpells() {
  if (!state.draftId) {
    renderCharGenUpload();
    return;
  }
  setMode("chargen");
  setStep("chargen-spells");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const [spellData, damageTypeData] = await Promise.all([
      api("GET", `/api/drafts/${state.draftId}/spells`),
      api("GET", `/api/drafts/${state.draftId}/damage-types`),
    ]);
    const spells = sortByLabel(spellData.spells || [], (spell) => spell.displayName || spell.name || "");
    damageTypeOptions = sortByLabel(damageTypeData.damageTypes || [], (type) => type.displayName || type.name || "");
    const hasSpells = spells.length > 0;
    const emptyNotice = t(
      "web.chargen.spells.empty_system",
      "This game system does not use spells, proceed to next screen."
    );
    const selected = new Set(normalizeCharGenIdList(state.chargenSelectedSpellIds));
    const list = spells
      .map((spell) => {
        const id = String(spell.id || "").trim();
        const level = Number(spell.level || 0);
        const school = spell.school ? ` <span class="badge">${escapeHtml(spell.school)}</span>` : "";
        const damageTypeLabel = resolveDamageTypeLabel(spell.damageTypeId);
        const damageType = damageTypeLabel ? ` <span class="badge">${escapeHtml(damageTypeLabel)}</span>` : "";
        const checked = selected.has(id) ? "checked" : "";
        return `
          <div class="list-item">
            <label>
              <input type="checkbox" data-chargen-spell="${escapeHtml(id)}" ${checked}>
              <strong>${escapeHtml(spell.name || t("spells.untitled", "Untitled"))}</strong>
              <span class="badge">L${level}</span>${school}${damageType}
            </label>
          </div>
        `;
      })
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${t("spells.title", "Spells")}</h1>
        <div class="list">
          ${list || `<div class="list-item">${emptyNotice}</div>`}
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="chargenSpellsBack" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn" id="chargenSpellsContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    const save = () => {
      state.chargenSelectedSpellIds = collectCheckedIds("[data-chargen-spell]");
      saveCharGenDraftLocal();
    };
    document.querySelectorAll("[data-chargen-spell]").forEach((input) => input.addEventListener("change", save));
    document.getElementById("chargenSpellsBack").addEventListener("click", () => {
      save();
      renderCharGenSkills();
    });
    document.getElementById("chargenSpellsContinue").addEventListener("click", () => {
      save();
      renderCharGenEquipment();
    });
  } catch (error) {
    renderCharGenLoadError(error, renderCharGenSpells);
  }
}

async function renderCharGenEquipment() {
  if (!state.draftId) {
    renderCharGenUpload();
    return;
  }
  setMode("chargen");
  setStep("chargen-equipment");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const [equipmentData, currencyData, classData, damageTypeData] = await Promise.all([
      api("GET", `/api/drafts/${state.draftId}/equipment`),
      api("GET", `/api/drafts/${state.draftId}/currency`),
      api("GET", `/api/drafts/${state.draftId}/classes`),
      api("GET", `/api/drafts/${state.draftId}/damage-types`),
    ]);
    const equipment = sortByLabel(equipmentData.equipment || [], (item) => item.displayName || item.name || "");
    damageTypeOptions = sortByLabel(damageTypeData.damageTypes || [], (type) => type.displayName || type.name || "");
    const currencies = sortByLabel(currencyData.currencies || [], (currency) => currency.name || "");
    const classes = Array.isArray(classData.classes) ? classData.classes : [];
    const characterClass = classes.find((entry) => String(entry.id || "") === String(state.chargenClassId || ""));
    applyCharGenStartingMoneyDefaults(currencyData.startingMoney || {}, characterClass);
    const selected = new Set(normalizeCharGenIdList(state.chargenSelectedEquipmentIds));
    const currencyOptions = [`<option value="">${t("common.none", "None")}</option>`]
      .concat(
        currencies.map((currency) => {
          const id = String(currency.id || "").trim();
          const selectedAttr = id === String(state.chargenStartingMoneyCurrencyId || "") ? "selected" : "";
          return `<option value="${escapeHtml(id)}" ${selectedAttr}>${escapeHtml(currency.name || id)}</option>`;
        })
      )
      .join("");
    const list = equipment
      .map((item) => {
        const id = String(item.id || "").trim();
        const checked = selected.has(id) ? "checked" : "";
        const weight = Number(item.weightValue || 0) > 0
          ? ` <span class="badge">${escapeHtml(String(item.weightValue))} ${escapeHtml(item.weightUnit || "")}</span>`
          : "";
        const damageTypeLabel = resolveDamageTypeLabel(item.damageTypeId);
        const damageType = damageTypeLabel ? ` <span class="badge">${escapeHtml(damageTypeLabel)}</span>` : "";
        return `
          <div class="list-item">
            <label>
              <input type="checkbox" data-chargen-equipment="${escapeHtml(id)}" ${checked}>
              <strong>${escapeHtml(item.name || t("equipment.untitled", "Untitled"))}</strong>${weight}${damageType}
            </label>
          </div>
        `;
      })
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${t("equipment.title", "Equipment")}</h1>
        <div class="grid two">
          <div class="field">
            <label for="chargenStartingMoneyAmount">${t("money.starting.amount", "Starting Money")}</label>
            <input type="number" id="chargenStartingMoneyAmount" min="0" step="1"
              value="${Math.max(0, Number(state.chargenStartingMoneyAmount || 0))}">
          </div>
          <div class="field">
            <label for="chargenStartingMoneyCurrency">${t("money.currency", "Currency")}</label>
            <select id="chargenStartingMoneyCurrency">${currencyOptions}</select>
          </div>
        </div>
        <div class="list">
          ${list || `<div class="list-item">${t("equipment.none", "No equipment yet.")}</div>`}
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="chargenEquipmentBack" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn" id="chargenEquipmentContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    const save = () => {
      state.chargenSelectedEquipmentIds = collectCheckedIds("[data-chargen-equipment]");
      state.chargenStartingMoneyAmount = Math.max(0, Math.trunc(Number(document.getElementById("chargenStartingMoneyAmount").value || 0)));
      state.chargenStartingMoneyCurrencyId = String(document.getElementById("chargenStartingMoneyCurrency").value || "");
      saveCharGenDraftLocal();
    };
    document.querySelectorAll("[data-chargen-equipment]").forEach((input) => input.addEventListener("change", save));
    document.getElementById("chargenStartingMoneyAmount").addEventListener("change", save);
    document.getElementById("chargenStartingMoneyCurrency").addEventListener("change", save);
    document.getElementById("chargenEquipmentBack").addEventListener("click", () => {
      save();
      renderCharGenSpells();
    });
    document.getElementById("chargenEquipmentContinue").addEventListener("click", () => {
      save();
      renderCharGenWeapons();
    });
  } catch (error) {
    renderCharGenLoadError(error, renderCharGenEquipment);
  }
}

async function renderCharGenWeapons() {
  if (!state.draftId) {
    renderCharGenUpload();
    return;
  }
  setMode("chargen");
  setStep("chargen-weapons");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const [weaponData, damageTypeData] = await Promise.all([
      api("GET", `/api/drafts/${state.draftId}/weapons`),
      api("GET", `/api/drafts/${state.draftId}/damage-types`),
    ]);
    const weapons = sortByLabel(weaponData.weapons || [], (weapon) => weapon.displayName || weapon.name || "");
    damageTypeOptions = sortByLabel(damageTypeData.damageTypes || [], (type) => type.displayName || type.name || "");
    const selected = new Set(normalizeCharGenIdList(state.chargenSelectedWeaponIds));
    const list = weapons
      .map((weapon) => {
        const id = String(weapon.id || "").trim();
        const checked = selected.has(id) ? "checked" : "";
        const damage = weapon.damageRoll ? ` <span class="badge">${escapeHtml(weapon.damageRoll)}</span>` : "";
        const damageTypeLabel = resolveDamageTypeLabel(weapon.damageTypeId);
        const damageType = damageTypeLabel ? ` <span class="badge">${escapeHtml(damageTypeLabel)}</span>` : "";
        return `
          <div class="list-item">
            <label>
              <input type="checkbox" data-chargen-weapon="${escapeHtml(id)}" ${checked}>
              <strong>${escapeHtml(weapon.name || t("weapons.untitled", "Untitled"))}</strong>${damage}${damageType}
            </label>
          </div>
        `;
      })
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${t("weapons.title", "Weapons")}</h1>
        <div class="list">
          ${list || `<div class="list-item">${t("weapons.none", "No weapons yet.")}</div>`}
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="chargenWeaponsBack" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn" id="chargenWeaponsContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    const save = () => {
      state.chargenSelectedWeaponIds = collectCheckedIds("[data-chargen-weapon]");
      saveCharGenDraftLocal();
    };
    document.querySelectorAll("[data-chargen-weapon]").forEach((input) => input.addEventListener("change", save));
    document.getElementById("chargenWeaponsBack").addEventListener("click", () => {
      save();
      renderCharGenEquipment();
    });
    document.getElementById("chargenWeaponsContinue").addEventListener("click", () => {
      save();
      renderCharGenArmor();
    });
  } catch (error) {
    renderCharGenLoadError(error, renderCharGenWeapons);
  }
}

async function renderCharGenArmor() {
  if (!state.draftId) {
    renderCharGenUpload();
    return;
  }
  setMode("chargen");
  setStep("chargen-armor");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const [armorData, armorClassData, damageTypeData] = await Promise.all([
      api("GET", `/api/drafts/${state.draftId}/armor`),
      api("GET", `/api/drafts/${state.draftId}/armor-class`),
      api("GET", `/api/drafts/${state.draftId}/damage-types`),
    ]);
    const armor = sortByLabel(armorData.armor || [], (item) => item.displayName || item.name || "");
    damageTypeOptions = sortByLabel(damageTypeData.damageTypes || [], (type) => type.displayName || type.name || "");
    const selected = new Set(normalizeCharGenIdList(state.chargenSelectedArmorIds));
    const initialAc = state.chargenResolvedArmorClass > 0
      ? state.chargenResolvedArmorClass
      : calculateCharGenArmorClass(armorClassData, armor, selected);
    const list = armor
      .map((item) => {
        const id = String(item.id || "").trim();
        const checked = selected.has(id) ? "checked" : "";
        const acParts = [];
        if (Number(item.armorClass || 0) > 0) {
          acParts.push(`${t("armorclass.title", "Armor Class")} ${Number(item.armorClass || 0)}`);
        }
        if (Number(item.armorBonus || 0) > 0) {
          acParts.push(`+${Number(item.armorBonus || 0)}`);
        }
        if (Number(item.shieldBonus || 0) > 0) {
          acParts.push(`+${Number(item.shieldBonus || 0)} ${t("armor.shield", "Shield")}`);
        }
        const damageTypeLabel = resolveDamageTypeLabel(item.damageTypeId);
        if (damageTypeLabel) {
          acParts.push(damageTypeLabel);
        }
        const detail = acParts.length ? ` <span class="badge">${escapeHtml(acParts.join(" "))}</span>` : "";
        return `
          <div class="list-item">
            <label>
              <input type="checkbox" data-chargen-armor="${escapeHtml(id)}" ${checked}>
              <strong>${escapeHtml(item.name || t("armor.untitled", "Untitled"))}</strong>${detail}
            </label>
          </div>
        `;
      })
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${t("armor.title", "Armor")}</h1>
        <div class="field">
          <label for="chargenResolvedArmorClass">${t("armorclass.title", "Armor Class")}</label>
          <input type="number" id="chargenResolvedArmorClass" min="0" step="1" value="${Math.max(0, Number(initialAc || 0))}">
        </div>
        <div class="list">
          ${list || `<div class="list-item">${t("armor.none", "No armor yet.")}</div>`}
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="chargenArmorBack" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn ghost" id="chargenArmorSave" type="button">${t("common.save", "Save")}</button>
            <button class="btn" id="chargenArmorDownload" type="button">${t("web.download.character", "Download .gmcf")}</button>
          </div>
        </div>
      </section>
    `;

    const acInput = document.getElementById("chargenResolvedArmorClass");
    const save = () => {
      state.chargenSelectedArmorIds = collectCheckedIds("[data-chargen-armor]");
      state.chargenResolvedArmorClass = Math.max(0, Math.trunc(Number(acInput.value || 0)));
      saveCharGenDraftLocal();
    };
    const recalculate = () => {
      const ids = new Set(collectCheckedIds("[data-chargen-armor]"));
      acInput.value = String(calculateCharGenArmorClass(armorClassData, armor, ids));
      save();
    };
    document.querySelectorAll("[data-chargen-armor]").forEach((input) => input.addEventListener("change", recalculate));
    acInput.addEventListener("change", save);
    document.getElementById("chargenArmorBack").addEventListener("click", () => {
      save();
      renderCharGenWeapons();
    });
    document.getElementById("chargenArmorSave").addEventListener("click", () => {
      save();
      markSaved(t("web.toast.character_saved", "Character saved"));
    });
    document.getElementById("chargenArmorDownload").addEventListener("click", () => {
      save();
      downloadCharGenDraft();
    });
  } catch (error) {
    renderCharGenLoadError(error, renderCharGenArmor);
  }
}

function buildCharGenRules(method) {
  const type = formatCharGenType(method.generationType);
  const sets = Number(method.numberOfSets || 0);
  const assignInOrder = method.assignInOrder ? t("common.yes", "Yes") : t("common.no", "No");
  const terms = Array.isArray(method.diceTerms) ? method.diceTerms : [];
  const diceText = terms.length
    ? terms.map((term) => term.notation || formatCharGenDiceTerm(term)).join(", ")
    : t("common.none", "None");
  const arrayMode = isCharGenOpenStandardArray(method)
      ? t("attrgen.arrays.mode.open", "Player Assigned")
      : t("attrgen.arrays.mode.assigned", "Auto Assigned");
  const substitutionText = method.allowDiceSubstitution
    ? t("attrgen.dice.substitution.summary", "{value}, up to {count}")
        .replace("{value}", String(Math.trunc(Number(method.diceSubstitutionValue || 0))))
        .replace("{count}", String(Math.max(0, Number(method.maxDiceSubstitutions || 0))))
    : t("common.no", "No");
  return [
    `${t("attrgen.type", "Generation Type")}: ${type}`,
    `${t("attrgen.sets", "Attribute Sets")}: ${sets}`,
    `${t("attrgen.assign", "Assign In Order")}: ${assignInOrder}`,
    `${t("attrgen.arrays.mode", "Array Assignment")}: ${arrayMode}`,
    `${t("attrgen.dice.substitution.enable", "Allow substitution")}: ${substitutionText}`,
    `${t("attrgen.dice", "Dice Terms")}: ${diceText}`,
  ].join("\n");
}

function normalizeCharGenGenerationChoice(value) {
  return String(value || "")
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9_-]/g, "")
    .slice(0, 80);
}

function normalizeCharGenAttributeValues(values) {
  if (!Array.isArray(values)) {
    return [];
  }
  return values
    .map((value) => Number(value))
    .filter((value) => Number.isFinite(value))
    .map((value) => Math.trunc(value));
}

function normalizeCharGenRollAssignments(assignments) {
  const normalized = {};
  const safeAssignments = assignments && typeof assignments === "object" ? assignments : {};
  Object.keys(safeAssignments).forEach((attributeId) => {
    const safeAttributeId = String(attributeId || "").trim();
    const rollIndex = Number(safeAssignments[attributeId]);
    if (safeAttributeId && Number.isInteger(rollIndex) && rollIndex >= 0) {
      normalized[safeAttributeId] = rollIndex;
    }
  });
  return normalized;
}

function normalizeCharGenArrayType(value) {
  const safeValue = String(value || "").trim().toLowerCase();
  return ["standard", "elite"].includes(safeValue) ? safeValue : "";
}

function normalizeCharGenArrayAssignments(assignments) {
  return normalizeCharGenRollAssignments(assignments);
}

function normalizeCharGenAttributeStepResults(results) {
  const normalized = {};
  const safeResults = results && typeof results === "object" ? results : {};
  Object.keys(safeResults).forEach((stepKey) => {
    const stepIndex = Number(stepKey);
    if (!Number.isInteger(stepIndex) || stepIndex < 0) {
      return;
    }
    const scores = safeResults[stepKey] && typeof safeResults[stepKey] === "object"
      ? safeResults[stepKey]
      : {};
    const safeScores = {};
    Object.keys(scores).forEach((attributeId) => {
      const safeId = String(attributeId || "").trim();
      const score = Number(scores[attributeId]);
      if (safeId && Number.isFinite(score)) {
        safeScores[safeId] = score;
      }
    });
    if (Object.keys(safeScores).length) {
      normalized[String(stepIndex)] = safeScores;
    }
  });
  return normalized;
}

function getCharGenGenerationChoiceEntries(method) {
  const safeMethod = method || {};
  const options = normalizeAttributeGenerationOptions(safeMethod.attributeGenerationOptions || []);
  if (options.length) {
    return options.map((option) => ({
      key: option.id,
      label: option.name || t("attrgen.options.default_name", "Option"),
      steps: option.steps,
    }));
  }
  const type = normalizeCharGenGenerationChoice(safeMethod.generationType);
  if (["standard_array", "dice", "point_buy"].includes(type)) {
    return [{
      key: type,
      label: formatCharGenType(type),
      steps: [{ methodType: type, applicationMode: "set" }],
    }];
  }
  if (String(safeMethod.generationType || "").trim().toLowerCase() !== "hybrid") {
    return [];
  }
  const stages = normalizeHybridStages(safeMethod.hybridStages)
    .map((stage) => normalizeCharGenGenerationChoice(stage))
    .filter((stage) => stage.length > 0);
  const resolved = stages.length ? stages : ["standard_array", "dice", "point_buy"];
  const choices = [];
  resolved.forEach((stage) => {
    if (!choices.includes(stage)) {
      choices.push(stage);
    }
  });
  return choices.map((key) => ({
    key,
    label: formatCharGenType(key),
    steps: [{ methodType: key, applicationMode: "set" }],
  }));
}

function getCharGenGenerationChoiceKeys(method) {
  return getCharGenGenerationChoiceEntries(method).map((entry) => entry.key);
}

function getCharGenGenerationChoices(method) {
  return getCharGenGenerationChoiceEntries(method).map((entry) => ({
    key: entry.key,
    label: entry.label,
  }));
}

function resolveCharGenGenerationChoice(method) {
  const choices = getCharGenGenerationChoiceKeys(method);
  const selected = normalizeCharGenGenerationChoice(state.chargenAttributeGenerationChoice);
  if (selected && choices.includes(selected)) {
    return selected;
  }
  return choices.length === 1 ? choices[0] : "";
}

function isCharGenGenerationChoiceActive(method, selectedChoice, methodKey) {
  const target = normalizeCharGenGenerationChoice(methodKey);
  if (!target) {
    return false;
  }
  const choices = getCharGenGenerationChoiceEntries(method);
  const selected = normalizeCharGenGenerationChoice(selectedChoice);
  const selectedEntry = choices.length === 1
    ? choices[0]
    : choices.find((entry) => normalizeCharGenGenerationChoice(entry.key) === selected);
  if (!selectedEntry) {
    return false;
  }
  return selectedEntry.steps.some((step) => normalizeAttributeGenerationMethodType(step.methodType) === target);
}

function getCharGenSelectedGenerationStep(method, methodType) {
  const target = normalizeAttributeGenerationMethodType(methodType);
  if (!target) {
    return null;
  }
  const choices = getCharGenGenerationChoiceEntries(method);
  const selected = normalizeCharGenGenerationChoice(resolveCharGenGenerationChoice(method));
  const selectedEntry = choices.length === 1
    ? choices[0]
    : choices.find((entry) => normalizeCharGenGenerationChoice(entry.key) === selected);
  if (!selectedEntry) {
    return null;
  }
  return selectedEntry.steps.find((step) => normalizeAttributeGenerationMethodType(step.methodType) === target) || null;
}

function getCharGenSelectedGenerationEntry(method) {
  const choices = getCharGenGenerationChoiceEntries(method);
  const selected = normalizeCharGenGenerationChoice(resolveCharGenGenerationChoice(method));
  return choices.length === 1
    ? choices[0]
    : choices.find((entry) => normalizeCharGenGenerationChoice(entry.key) === selected) || null;
}

function getCharGenGenerationStepIndex(method, methodType) {
  const entry = getCharGenSelectedGenerationEntry(method);
  const target = normalizeAttributeGenerationMethodType(methodType);
  if (!entry || !target) {
    return -1;
  }
  return entry.steps.findIndex((step) => normalizeAttributeGenerationMethodType(step.methodType) === target);
}

function getCharGenChooseStep(method) {
  const entry = getCharGenSelectedGenerationEntry(method);
  if (!entry || entry.steps.length < 2) {
    return null;
  }
  const step = entry.steps[1];
  return normalizeAttributeGenerationApplicationMode(step.applicationMode) === "choose" ? step : null;
}

function recordCharGenStepResult(method, methodType, scores) {
  const stepIndex = getCharGenGenerationStepIndex(method, methodType);
  if (stepIndex < 0) {
    return;
  }
  const safeScores = normalizeCharGenAttributeStepResults({ [stepIndex]: scores });
  if (!safeScores[String(stepIndex)]) {
    return;
  }
  state.chargenAttributeStepResults = normalizeCharGenAttributeStepResults(state.chargenAttributeStepResults);
  state.chargenAttributeStepResults[String(stepIndex)] = safeScores[String(stepIndex)];
  state.chargenAttributeResultChoice = "";
}

function clearCharGenAttributeStepResults() {
  state.chargenAttributeStepResults = {};
  state.chargenAttributeResultChoice = "";
}

function buildCharGenAttributes(attributes, method, container) {
  const inputs = [];
  container.innerHTML = "";
  attributes.forEach((attribute, index) => {
    const min = resolveCharGenMin(attribute, method);
    const max = resolveCharGenMax(attribute, method);
    const base = resolveCharGenBase(method);
    const value = clampCharGen(base, min, max);
    const id = `chargenAttr${index}`;
    const label = attribute.displayName || attribute.name || `Attribute ${index + 1}`;
    const attributeId = String(attribute.id || "").trim();
    container.insertAdjacentHTML(
      "beforeend",
      `
        <div class="field">
          <label for="${id}">${escapeHtml(label)}</label>
          <input type="number" id="${id}" min="${min}" max="${max}" value="${value}">
        </div>
      `
    );
    const input = document.getElementById(id);
    inputs.push({ input, min, max, attributeId });
  });
  return inputs;
}

function resetCharGenAttributeInputs(inputs, method) {
  const base = resolveCharGenBase(method);
  inputs.forEach((entry) => {
    entry.input.value = String(clampCharGen(base, entry.min, entry.max));
  });
}

function applyCharGenValues(inputs, values, baseValues = null) {
  inputs.forEach((entry, index) => {
    const rolled = Number(values[index] || 0);
    const base = Array.isArray(baseValues) ? Number(baseValues[index] || 0) : 0;
    const value = clampCharGen(base + rolled, entry.min, entry.max);
    entry.input.value = value;
  });
}

function captureCharGenAttributeValues(inputs) {
  return (inputs || []).map((entry) => Number(entry.input.value || 0));
}

function shouldAddCharGenRollToBase(method) {
  const selectedStep = getCharGenSelectedGenerationStep(method, "dice");
  if (selectedStep) {
    return normalizeAttributeGenerationApplicationMode(selectedStep.applicationMode) === "add";
  }
  const safeMethod = method || {};
  const generationType = String(safeMethod.generationType || "").trim().toLowerCase();
  if (generationType !== "hybrid") {
    return false;
  }
  const hybridStages = normalizeHybridStages(safeMethod.hybridStages);
  if (!hybridStages.length) {
    return true;
  }
  const standardIndex = hybridStages.indexOf("standard_array");
  const diceIndex = hybridStages.indexOf("dice");
  return standardIndex >= 0 && diceIndex > standardIndex;
}

function applyCharGenSavedScores(inputs) {
  const scores = state.chargenAttributeScores || {};
  inputs.forEach((entry) => {
    const id = String(entry.attributeId || "").trim();
    if (!id || !(id in scores)) {
      return;
    }
    const value = clampCharGen(Number(scores[id] || 0), entry.min, entry.max);
    entry.input.value = value;
  });
}

function buildCharGenRaceRequirementMessage(race) {
  if (!race) {
    return "";
  }
  const limits = Array.isArray(race.attributeScoreLimits) ? race.attributeScoreLimits : [];
  const scores = state.chargenAttributeScores || {};
  for (const limit of limits) {
    const attributeId = String(limit.attributeId || "").trim();
    if (!attributeId) {
      continue;
    }
    const min = Number(limit.min || 0);
    if (min <= 0) {
      continue;
    }
    const score = Number(scores[attributeId] || 0);
    if (score < min) {
      const raceName = String(race.name || "");
      const attributeName = resolveCharGenAttributeName(attributeId);
      return t("races.requirement.blocked", "You cannot select {race} because {attribute} must be at least {value}")
        .replace("{race}", raceName)
        .replace("{attribute}", attributeName)
        .replace("{value}", String(min));
    }
  }
  return "";
}

function resolveCharGenAttributeName(attributeId) {
  const safeId = String(attributeId || "").trim();
  if (!safeId) {
    return "";
  }
  const match = (state.chargenAttributes || []).find((attribute) => String(attribute.id || "") === safeId);
  if (match) {
    return match.displayName || match.name || safeId;
  }
  return safeId;
}

function buildCharGenClassSkillRanks(characterClass) {
  const ranks = {};
  const ids = characterClass && Array.isArray(characterClass.classSkillIds) ? characterClass.classSkillIds : [];
  ids.forEach((id) => {
    const safeId = String(id || "").trim();
    if (safeId) {
      ranks[safeId] = 0;
    }
  });
  return ranks;
}

function buildCharGenSkillPointSummary(characterClass, progression) {
  const className = characterClass && characterClass.name ? characterClass.name : t("classes.select", "Select Class");
  const classPoints = characterClass ? Math.max(0, Number(characterClass.skillPointsPerLevel || 0)) : 0;
  const progressionType = String(progression && progression.skillPointProgression ? progression.skillPointProgression : "byClass");
  if (progressionType === "byClass") {
    return t("web.chargen.skills.points.class", "{class}: {points} skill points per level.")
      .replace("{class}", className)
      .replace("{points}", String(classPoints));
  }
  const base = Math.max(0, Number(progression && progression.baseSkillPointsPerLevel || 0));
  const minimum = Math.max(0, Number(progression && progression.minimumSkillPointsPerLevel || 0));
  return t("web.chargen.skills.points.global", "Skill points: {points} per level, minimum {minimum}.")
    .replace("{points}", String(base))
    .replace("{minimum}", String(minimum));
}

function collectCharGenSkillRanks() {
  const ranks = {};
  document.querySelectorAll("[data-chargen-skill-rank]").forEach((input) => {
    const id = String(input.dataset.chargenSkillRank || "").trim();
    const rank = Math.max(0, Math.trunc(Number(input.value || 0)));
    if (id && rank > 0) {
      ranks[id] = rank;
    }
  });
  return ranks;
}

function normalizeCharGenIdList(values) {
  const list = Array.isArray(values) ? values : [];
  const results = [];
  list.forEach((value) => {
    const safe = String(value || "").trim();
    if (safe && !results.includes(safe)) {
      results.push(safe);
    }
  });
  return results;
}

function normalizeCharGenRankMap(values) {
  const source = values && typeof values === "object" ? values : {};
  const ranks = {};
  Object.keys(source).forEach((key) => {
    const safeKey = String(key || "").trim();
    const rank = Math.max(0, Math.trunc(Number(source[key] || 0)));
    if (safeKey) {
      ranks[safeKey] = rank;
    }
  });
  return ranks;
}

function putCharGenRank(target, value) {
  const safeValue = String(value || "").trim();
  if (!safeValue) {
    return;
  }
  const separator = safeValue.lastIndexOf("|");
  const id = separator >= 0 ? safeValue.slice(0, separator).trim() : safeValue;
  const rankText = separator >= 0 ? safeValue.slice(separator + 1).trim() : "0";
  const rank = Math.max(0, Math.trunc(Number(rankText || 0)));
  if (id) {
    target[id] = rank;
  }
}

function pushCharGenId(target, value) {
  const safeValue = String(value || "").trim();
  if (safeValue && !target.includes(safeValue)) {
    target.push(safeValue);
  }
}

function collectCheckedIds(selector) {
  const ids = [];
  document.querySelectorAll(selector).forEach((input) => {
    if (!input.checked) {
      return;
    }
    const value = String(input.dataset.chargenSpell || input.dataset.chargenEquipment || input.dataset.chargenWeapon || input.dataset.chargenArmor || "").trim();
    if (value && !ids.includes(value)) {
      ids.push(value);
    }
  });
  return ids;
}

function applyCharGenStartingMoneyDefaults(startingMoney, characterClass) {
  const method = String(startingMoney.method || "base").trim() || "base";
  state.chargenStartingMoneyMethod = method;
  if (!state.chargenStartingMoneyCurrencyId) {
    state.chargenStartingMoneyCurrencyId = String(startingMoney.currencyId || "");
  }
  if (Number(state.chargenStartingMoneyAmount || 0) > 0) {
    return;
  }
  if (method === "class" && characterClass) {
    state.chargenStartingMoneyAmount = Math.max(0, Number(characterClass.startingMoney || 0));
    return;
  }
  state.chargenStartingMoneyAmount = Math.max(0, Number(startingMoney.baseAmount || 0));
}

function calculateCharGenArmorClass(armorClassData, armor, selectedIds) {
  const data = armorClassData || {};
  const selected = selectedIds instanceof Set ? selectedIds : new Set(normalizeCharGenIdList(selectedIds));
  const base = Math.max(0, Math.trunc(Number(data.baseArmorClass || 0)));
  let replacementBase = base;
  let modifierTotal = 0;
  (armor || []).forEach((item) => {
    const id = String(item.id || "").trim();
    if (!selected.has(id)) {
      return;
    }
    const armorClass = Math.max(0, Math.trunc(Number(item.armorClass || 0)));
    const armorBonus = Math.max(0, Math.trunc(Number(item.armorBonus || 0)));
    const shieldBonus = Math.max(0, Math.trunc(Number(item.shieldBonus || 0)));
    if (armorClass > 0) {
      replacementBase = Math.max(replacementBase, armorClass);
    }
    modifierTotal += armorBonus + shieldBonus;
  });
  let total = replacementBase + modifierTotal;
  if (String(data.acAbilityAttributeId || "").trim()) {
    total += resolveCharGenAbilityModifier(data.acAbilityAttributeId);
  }
  return Math.max(0, Math.trunc(total));
}

function resolveCharGenAbilityModifier(attributeId) {
  const safeId = String(attributeId || "").trim();
  if (!safeId) {
    return 0;
  }
  const score = Number((state.chargenAttributeScores || {})[safeId] || 0);
  if (!Number.isFinite(score)) {
    return 0;
  }
  return Math.floor((score - 10) / 2);
}

async function hashBuffer(buffer) {
  if (crypto && crypto.subtle && crypto.subtle.digest) {
    const digest = await crypto.subtle.digest("SHA-256", buffer);
    return bufferToHex(digest);
  }
  return sha256Hex(buffer);
}

function bufferToHex(buffer) {
  const bytes = new Uint8Array(buffer);
  return Array.from(bytes)
    .map((value) => value.toString(16).padStart(2, "0"))
    .join("");
}

function sha256Hex(buffer) {
  const bytes = buffer instanceof ArrayBuffer ? new Uint8Array(buffer) : new Uint8Array(buffer.buffer || buffer);
  const padded = sha256Pad(bytes);
  const h = [
    0x6a09e667,
    0xbb67ae85,
    0x3c6ef372,
    0xa54ff53a,
    0x510e527f,
    0x9b05688c,
    0x1f83d9ab,
    0x5be0cd19,
  ];
  const k = [
    0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
    0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
    0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
    0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
    0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
    0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
    0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
    0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2,
  ];

  for (let offset = 0; offset < padded.length; offset += 64) {
    const w = new Uint32Array(64);
    for (let i = 0; i < 16; i++) {
      const base = offset + i * 4;
      w[i] = (
        (padded[base] << 24) |
        (padded[base + 1] << 16) |
        (padded[base + 2] << 8) |
        (padded[base + 3])
      ) >>> 0;
    }
    for (let i = 16; i < 64; i++) {
      const s0 = rotr(w[i - 15], 7) ^ rotr(w[i - 15], 18) ^ (w[i - 15] >>> 3);
      const s1 = rotr(w[i - 2], 17) ^ rotr(w[i - 2], 19) ^ (w[i - 2] >>> 10);
      w[i] = (w[i - 16] + s0 + w[i - 7] + s1) >>> 0;
    }

    let a = h[0];
    let b = h[1];
    let c = h[2];
    let d = h[3];
    let e = h[4];
    let f = h[5];
    let g = h[6];
    let hh = h[7];

    for (let i = 0; i < 64; i++) {
      const s1 = rotr(e, 6) ^ rotr(e, 11) ^ rotr(e, 25);
      const ch = (e & f) ^ (~e & g);
      const temp1 = (hh + s1 + ch + k[i] + w[i]) >>> 0;
      const s0 = rotr(a, 2) ^ rotr(a, 13) ^ rotr(a, 22);
      const maj = (a & b) ^ (a & c) ^ (b & c);
      const temp2 = (s0 + maj) >>> 0;

      hh = g;
      g = f;
      f = e;
      e = (d + temp1) >>> 0;
      d = c;
      c = b;
      b = a;
      a = (temp1 + temp2) >>> 0;
    }

    h[0] = (h[0] + a) >>> 0;
    h[1] = (h[1] + b) >>> 0;
    h[2] = (h[2] + c) >>> 0;
    h[3] = (h[3] + d) >>> 0;
    h[4] = (h[4] + e) >>> 0;
    h[5] = (h[5] + f) >>> 0;
    h[6] = (h[6] + g) >>> 0;
    h[7] = (h[7] + hh) >>> 0;
  }

  return h.map((value) => value.toString(16).padStart(8, "0")).join("");
}

function sha256Pad(bytes) {
  const bitLen = bytes.length * 8;
  const withOne = bytes.length + 1;
  const mod = withOne % 64;
  const padLen = mod <= 56 ? (56 - mod) : (56 + 64 - mod);
  const totalLen = bytes.length + 1 + padLen + 8;
  const padded = new Uint8Array(totalLen);
  padded.set(bytes);
  padded[bytes.length] = 0x80;
  const bitLenHi = Math.floor(bitLen / 0x100000000);
  const bitLenLo = bitLen >>> 0;
  padded[totalLen - 8] = (bitLenHi >>> 24) & 0xff;
  padded[totalLen - 7] = (bitLenHi >>> 16) & 0xff;
  padded[totalLen - 6] = (bitLenHi >>> 8) & 0xff;
  padded[totalLen - 5] = (bitLenHi) & 0xff;
  padded[totalLen - 4] = (bitLenLo >>> 24) & 0xff;
  padded[totalLen - 3] = (bitLenLo >>> 16) & 0xff;
  padded[totalLen - 2] = (bitLenLo >>> 8) & 0xff;
  padded[totalLen - 1] = (bitLenLo) & 0xff;
  return padded;
}

function rotr(value, bits) {
  return (value >>> bits) | (value << (32 - bits));
}

function buildCharGenDraft() {
  return {
    gameDraftId: String(state.draftId || ""),
    gameId: String(state.chargenGameId || ""),
    gameHash: String(state.chargenGameHash || ""),
    gameName: String(state.chargenGameName || ""),
    characterName: String(state.chargenCharacterName || ""),
    attributeGenerationChoice: normalizeCharGenGenerationChoice(state.chargenAttributeGenerationChoice),
    rolledAttributeValues: normalizeCharGenAttributeValues(state.chargenRolledAttributeValues),
    diceRollAssignments: normalizeCharGenRollAssignments(state.chargenDiceRollAssignments),
    selectedArrayType: normalizeCharGenArrayType(state.chargenSelectedArrayType),
    arrayAssignments: normalizeCharGenArrayAssignments(state.chargenArrayAssignments),
    attributeStepResults: normalizeCharGenAttributeStepResults(state.chargenAttributeStepResults),
    attributeResultChoice: String(state.chargenAttributeResultChoice || ""),
    ruleModeSelections: state.chargenRuleModeSelections || {},
    diceSubstitutionsUsed: Number(state.chargenDiceSubstitutionsUsed || 0),
    raceId: String(state.chargenRaceId || ""),
    classId: String(state.chargenClassId || ""),
    classSkillRanks: normalizeCharGenRankMap(state.chargenClassSkillRanks),
    selectedSkillRanks: normalizeCharGenRankMap(state.chargenSelectedSkillRanks),
    selectedSpellIds: normalizeCharGenIdList(state.chargenSelectedSpellIds),
    selectedEquipmentIds: normalizeCharGenIdList(state.chargenSelectedEquipmentIds),
    selectedWeaponIds: normalizeCharGenIdList(state.chargenSelectedWeaponIds),
    selectedArmorIds: normalizeCharGenIdList(state.chargenSelectedArmorIds),
    startingMoneyMethod: String(state.chargenStartingMoneyMethod || "base"),
    startingMoneyAmount: Math.max(0, Math.trunc(Number(state.chargenStartingMoneyAmount || 0))),
    startingMoneyCurrencyId: String(state.chargenStartingMoneyCurrencyId || ""),
    resolvedArmorClass: Math.max(0, Math.trunc(Number(state.chargenResolvedArmorClass || 0))),
    attributeScores: state.chargenAttributeScores || {},
  };
}

function serializeCharGenDraft(draft) {
  const safeDraft = draft || {};
  const lines = ["GMRulesCharacterFile v1"];
  lines.push(`gameDraftId=${safeDraft.gameDraftId || ""}`);
  lines.push(`gameId=${safeDraft.gameId || ""}`);
  lines.push(`gameHash=${safeDraft.gameHash || ""}`);
  lines.push(`gameName=${safeDraft.gameName || ""}`);
  lines.push(`characterName=${safeDraft.characterName || ""}`);
  const attributeGenerationChoice = normalizeCharGenGenerationChoice(safeDraft.attributeGenerationChoice);
  if (attributeGenerationChoice) {
    lines.push(`attributeGenerationChoice=${attributeGenerationChoice}`);
  }
  normalizeCharGenAttributeValues(safeDraft.rolledAttributeValues).forEach((value, index) => {
    lines.push(`attributeGenerationValue.${index}=${value}`);
  });
  const diceRollAssignments = normalizeCharGenRollAssignments(safeDraft.diceRollAssignments);
  Object.keys(diceRollAssignments)
    .sort()
    .forEach((attributeId) => {
      lines.push(`attributeRollAssignment.${attributeId}=${diceRollAssignments[attributeId]}`);
    });
  const selectedArrayType = normalizeCharGenArrayType(safeDraft.selectedArrayType);
  if (selectedArrayType) {
    lines.push(`attributeArrayType=${selectedArrayType}`);
  }
  const arrayAssignments = normalizeCharGenArrayAssignments(safeDraft.arrayAssignments);
  Object.keys(arrayAssignments)
    .sort()
    .forEach((attributeId) => {
      lines.push(`attributeArrayAssignment.${attributeId}=${arrayAssignments[attributeId]}`);
    });
  const stepResults = normalizeCharGenAttributeStepResults(safeDraft.attributeStepResults);
  Object.keys(stepResults)
    .sort((left, right) => Number(left) - Number(right))
    .forEach((stepIndex) => {
      Object.keys(stepResults[stepIndex]).sort().forEach((attributeId) => {
        lines.push(`attributeStepResult.${stepIndex}.${attributeId}=${stepResults[stepIndex][attributeId]}`);
      });
    });
  if (String(safeDraft.attributeResultChoice || "")) {
    lines.push(`attributeResultChoice=${String(safeDraft.attributeResultChoice)}`);
  }
  lines.push(`diceSubstitutionsUsed=${Math.max(0, Number(safeDraft.diceSubstitutionsUsed || 0))}`);
  const ruleModes = safeDraft.ruleModeSelections || {};
  Object.keys(ruleModes)
    .sort()
    .forEach((key) => {
      const safeKey = String(key || "").trim();
      if (safeKey) {
        lines.push(`ruleMode.${safeKey}=${String(ruleModes[key] || "").trim()}`);
      }
    });
  if (safeDraft.raceId) {
    lines.push(`raceId=${safeDraft.raceId}`);
  }
  if (safeDraft.classId) {
    lines.push(`classId=${safeDraft.classId}`);
  }
  const classSkillRanks = normalizeCharGenRankMap(safeDraft.classSkillRanks);
  Object.keys(classSkillRanks)
    .sort()
    .forEach((key, index) => {
      lines.push(`classSkill.${index}=${key}|${classSkillRanks[key]}`);
    });
  const selectedSkillRanks = normalizeCharGenRankMap(safeDraft.selectedSkillRanks);
  Object.keys(selectedSkillRanks)
    .sort()
    .forEach((key, index) => {
      lines.push(`selectedSkill.${index}=${key}|${selectedSkillRanks[key]}`);
    });
  normalizeCharGenIdList(safeDraft.selectedSpellIds)
    .sort()
    .forEach((id, index) => {
      lines.push(`selectedSpell.${index}=${id}`);
    });
  normalizeCharGenIdList(safeDraft.selectedEquipmentIds)
    .sort()
    .forEach((id, index) => {
      lines.push(`selectedEquipment.${index}=${id}`);
    });
  normalizeCharGenIdList(safeDraft.selectedWeaponIds)
    .sort()
    .forEach((id, index) => {
      lines.push(`selectedWeapon.${index}=${id}`);
    });
  normalizeCharGenIdList(safeDraft.selectedArmorIds)
    .sort()
    .forEach((id, index) => {
      lines.push(`selectedArmor.${index}=${id}`);
    });
  lines.push(`startingMoneyMethod=${safeDraft.startingMoneyMethod || "base"}`);
  lines.push(`startingMoneyAmount=${Math.max(0, Number(safeDraft.startingMoneyAmount || 0))}`);
  if (safeDraft.startingMoneyCurrencyId) {
    lines.push(`startingMoneyCurrencyId=${safeDraft.startingMoneyCurrencyId}`);
  }
  lines.push(`resolvedArmorClass=${Math.max(0, Number(safeDraft.resolvedArmorClass || 0))}`);
  const scores = safeDraft.attributeScores || {};
  Object.keys(scores)
    .sort()
    .forEach((key) => {
      const value = Number(scores[key] || 0);
      lines.push(`attr.${key}=${value}`);
    });
  return lines.join("\n");
}

function parseCharGenDraft(text) {
  const lines = String(text || "").split(/\r?\n/);
  if (!lines.length || !lines[0].startsWith("GMRulesCharacterFile")) {
    throw new Error(t("web.chargen.invalid", "Invalid character file."));
  }
  const draft = {
    gameDraftId: "",
    gameId: "",
    gameHash: "",
    gameName: "",
    characterName: "",
    attributeGenerationChoice: "",
    rolledAttributeValues: [],
    diceRollAssignments: {},
    selectedArrayType: "",
    arrayAssignments: {},
    attributeStepResults: {},
    attributeResultChoice: "",
    ruleModeSelections: {},
    diceSubstitutionsUsed: 0,
    raceId: "",
    classId: "",
    classSkillRanks: {},
    selectedSkillRanks: {},
    selectedSpellIds: [],
    selectedEquipmentIds: [],
    selectedWeaponIds: [],
    selectedArmorIds: [],
    startingMoneyMethod: "base",
    startingMoneyAmount: 0,
    startingMoneyCurrencyId: "",
    resolvedArmorClass: 0,
    attributeScores: {},
  };
  for (let i = 1; i < lines.length; i += 1) {
    const line = String(lines[i] || "").trim();
    if (!line || line.startsWith("#")) {
      continue;
    }
    const separator = line.indexOf("=");
    if (separator <= 0) {
      continue;
    }
    const key = line.slice(0, separator).trim();
    const value = line.slice(separator + 1).trim();
    if (key === "gameDraftId") {
      draft.gameDraftId = value;
    } else if (key === "gameId") {
      draft.gameId = value;
    } else if (key === "gameHash") {
      draft.gameHash = value;
    } else if (key === "gameName") {
      draft.gameName = value;
    } else if (key === "characterName") {
      draft.characterName = value;
    } else if (key === "attributeGenerationChoice") {
      draft.attributeGenerationChoice = normalizeCharGenGenerationChoice(value);
    } else if (key.startsWith("attributeGenerationValue.")) {
      const rolledValue = Number(value);
      if (Number.isFinite(rolledValue)) {
        draft.rolledAttributeValues.push(Math.trunc(rolledValue));
      }
    } else if (key.startsWith("attributeRollAssignment.")) {
      const attributeId = key.slice("attributeRollAssignment.".length).trim();
      const rollIndex = Number(value);
      if (attributeId && Number.isInteger(rollIndex) && rollIndex >= 0) {
        draft.diceRollAssignments[attributeId] = rollIndex;
      }
    } else if (key === "attributeArrayType") {
      draft.selectedArrayType = normalizeCharGenArrayType(value);
    } else if (key.startsWith("attributeArrayAssignment.")) {
      const attributeId = key.slice("attributeArrayAssignment.".length).trim();
      const valueIndex = Number(value);
      if (attributeId && Number.isInteger(valueIndex) && valueIndex >= 0) {
        draft.arrayAssignments[attributeId] = valueIndex;
      }
    } else if (key.startsWith("attributeStepResult.")) {
      const resultKey = key.slice("attributeStepResult.".length);
      const separatorIndex = resultKey.indexOf(".");
      const stepIndex = Number(resultKey.slice(0, separatorIndex));
      const attributeId = resultKey.slice(separatorIndex + 1).trim();
      const score = Number(value);
      if (separatorIndex > 0 && Number.isInteger(stepIndex) && stepIndex >= 0 && attributeId && Number.isFinite(score)) {
        draft.attributeStepResults[String(stepIndex)] = draft.attributeStepResults[String(stepIndex)] || {};
        draft.attributeStepResults[String(stepIndex)][attributeId] = score;
      }
    } else if (key === "attributeResultChoice") {
      const choice = Number(value);
      draft.attributeResultChoice = Number.isInteger(choice) && choice >= 0 ? String(choice) : "";
    } else if (key === "diceSubstitutionsUsed") {
      const used = Number(value);
      draft.diceSubstitutionsUsed = Number.isFinite(used) ? Math.max(0, Math.trunc(used)) : 0;
    } else if (key.startsWith("ruleMode.")) {
      const modeKey = key.slice("ruleMode.".length).trim();
      if (modeKey) {
        draft.ruleModeSelections[modeKey] = value;
      }
    } else if (key === "raceId") {
      draft.raceId = value;
    } else if (key === "classId") {
      draft.classId = value;
    } else if (key.startsWith("classSkill.")) {
      putCharGenRank(draft.classSkillRanks, value);
    } else if (key.startsWith("selectedSkill.")) {
      putCharGenRank(draft.selectedSkillRanks, value);
    } else if (key.startsWith("selectedSpell.")) {
      pushCharGenId(draft.selectedSpellIds, value);
    } else if (key.startsWith("selectedEquipment.")) {
      pushCharGenId(draft.selectedEquipmentIds, value);
    } else if (key.startsWith("selectedWeapon.")) {
      pushCharGenId(draft.selectedWeaponIds, value);
    } else if (key.startsWith("selectedArmor.")) {
      pushCharGenId(draft.selectedArmorIds, value);
    } else if (key === "startingMoneyMethod") {
      draft.startingMoneyMethod = value || "base";
    } else if (key === "startingMoneyAmount") {
      const amount = Number(value);
      draft.startingMoneyAmount = Number.isFinite(amount) ? Math.max(0, Math.trunc(amount)) : 0;
    } else if (key === "startingMoneyCurrencyId") {
      draft.startingMoneyCurrencyId = value;
    } else if (key === "resolvedArmorClass") {
      const armorClass = Number(value);
      draft.resolvedArmorClass = Number.isFinite(armorClass) ? Math.max(0, Math.trunc(armorClass)) : 0;
    } else if (key.startsWith("attr.")) {
      const attrId = key.slice(5).trim();
      if (!attrId) {
        continue;
      }
      const score = Number(value);
      if (!Number.isNaN(score)) {
        draft.attributeScores[attrId] = score;
      }
    }
  }
  return draft;
}

function applyCharGenDraft(draft) {
  const safeDraft = draft || {};
  if (safeDraft.gameDraftId) {
    state.draftId = String(safeDraft.gameDraftId || "");
  }
  state.chargenGameId = String(safeDraft.gameId || "");
  state.chargenGameHash = String(safeDraft.gameHash || "");
  state.chargenGameName = String(safeDraft.gameName || "");
  state.chargenCharacterName = String(safeDraft.characterName || "");
  state.chargenAttributeGenerationChoice = normalizeCharGenGenerationChoice(safeDraft.attributeGenerationChoice);
  state.chargenRolledAttributeValues = normalizeCharGenAttributeValues(safeDraft.rolledAttributeValues);
  state.chargenDiceRollAssignments = normalizeCharGenRollAssignments(safeDraft.diceRollAssignments);
  state.chargenSelectedArrayType = normalizeCharGenArrayType(safeDraft.selectedArrayType);
  state.chargenArrayAssignments = normalizeCharGenArrayAssignments(safeDraft.arrayAssignments);
  state.chargenAttributeStepResults = normalizeCharGenAttributeStepResults(safeDraft.attributeStepResults);
  state.chargenAttributeResultChoice = String(safeDraft.attributeResultChoice || "");
  state.chargenRuleModeSelections = safeDraft.ruleModeSelections || {};
  state.chargenDiceSubstitutionsUsed = Math.max(0, Number(safeDraft.diceSubstitutionsUsed || 0));
  state.chargenRaceId = String(safeDraft.raceId || "");
  state.chargenClassId = String(safeDraft.classId || "");
  state.chargenClassSkillRanks = normalizeCharGenRankMap(safeDraft.classSkillRanks);
  state.chargenSelectedSkillRanks = normalizeCharGenRankMap(safeDraft.selectedSkillRanks);
  state.chargenSelectedSpellIds = normalizeCharGenIdList(safeDraft.selectedSpellIds);
  state.chargenSelectedEquipmentIds = normalizeCharGenIdList(safeDraft.selectedEquipmentIds);
  state.chargenSelectedWeaponIds = normalizeCharGenIdList(safeDraft.selectedWeaponIds);
  state.chargenSelectedArmorIds = normalizeCharGenIdList(safeDraft.selectedArmorIds);
  state.chargenStartingMoneyMethod = String(safeDraft.startingMoneyMethod || "base");
  state.chargenStartingMoneyAmount = Math.max(0, Math.trunc(Number(safeDraft.startingMoneyAmount || 0)));
  state.chargenStartingMoneyCurrencyId = String(safeDraft.startingMoneyCurrencyId || "");
  state.chargenResolvedArmorClass = Math.max(0, Math.trunc(Number(safeDraft.resolvedArmorClass || 0)));
  state.chargenAttributeScores = safeDraft.attributeScores || {};
}

function resetCharGenState() {
  state.chargenAttributes = [];
  state.chargenAttributeScores = {};
  state.chargenAttributeStepResults = {};
  state.chargenAttributeResultChoice = "";
  state.chargenPointBuyBaselineScores = {};
  state.chargenSelectedArrayType = "";
  state.chargenArrayAssignments = {};
  state.chargenRaceId = "";
  state.chargenClassId = "";
  state.chargenClassSkillRanks = {};
  state.chargenSelectedSkillRanks = {};
  state.chargenSelectedSpellIds = [];
  state.chargenSelectedEquipmentIds = [];
  state.chargenSelectedWeaponIds = [];
  state.chargenSelectedArmorIds = [];
  state.chargenStartingMoneyMethod = "base";
  state.chargenStartingMoneyAmount = 0;
  state.chargenStartingMoneyCurrencyId = "";
  state.chargenResolvedArmorClass = 0;
  state.chargenGameId = "";
  state.chargenGameHash = "";
  state.chargenGameName = "";
  state.chargenCharacterName = "";
  state.chargenAttributeGenerationChoice = "";
  state.chargenRolledAttributeValues = [];
  state.chargenDiceRollAssignments = {};
  state.chargenRuleModeSelections = {};
  state.chargenDiceSubstitutionsUsed = 0;
  state.chargenDraftText = "";
  state.chargenCharacterDraftId = "";
  state.chargenServerSaveInFlight = false;
  state.chargenServerSaveQueued = false;
}

function saveCharGenDraftLocal(options = {}) {
  const draft = buildCharGenDraft();
  const text = serializeCharGenDraft(draft);
  state.chargenDraftText = text;
  try {
    localStorage.setItem("gmrules.chargen.draft", text);
  } catch (error) {
    // Ignore storage failures.
  }
  if (options.server !== false) {
    saveCharGenDraftServer(text);
  }
}

function saveCharGenDraftServer(text) {
  const safeText = String(text || "");
  if (!state.sessionToken || state.legacyGuest || !state.draftId || !state.chargenGameId || !hasValidCharGenName()) {
    return;
  }
  if (state.chargenServerSaveInFlight) {
    state.chargenServerSaveQueued = true;
    return;
  }
  state.chargenServerSaveInFlight = true;
  state.chargenServerSaveQueued = false;
  api("POST", "/api/characters", {
    id: state.chargenCharacterDraftId,
    gameDraftId: state.draftId,
    text: safeText,
  })
    .then((result) => {
      if (result.id) {
        state.chargenCharacterDraftId = result.id;
      }
      if (result.lastSaved) {
        markSaved(t("web.toast.character_saved", "Character saved"));
      }
    })
    .catch((error) => {
      showToast(error.message);
    })
    .finally(() => {
      state.chargenServerSaveInFlight = false;
      if (state.chargenServerSaveQueued) {
        saveCharGenDraftServer(state.chargenDraftText);
      }
    });
}

async function downloadCharGenDraft() {
  if (!hasValidCharGenName()) {
    showToast(t("web.chargen.name.required", "Enter a character name."));
    renderCharGenName();
    return;
  }
  if (!state.chargenGameId) {
    showToast(t("web.chargen.missing", "Choose a ruleset before saving this character."));
    return;
  }
  if (!state.draftId) {
    showToast(t("web.chargen.missing", "Choose a ruleset before saving this character."));
    return;
  }
  const text = serializeCharGenDraft(buildCharGenDraft());
  state.chargenDraftText = text;
  try {
    const headers = { "Content-Type": "application/json" };
    if (state.sessionToken) {
      headers.Authorization = `Bearer ${state.sessionToken}`;
    }
    const response = await fetch("/api/characters/export", {
      method: "POST",
      headers,
      cache: "no-store",
      body: JSON.stringify({
        gameDraftId: state.draftId,
        text,
      }),
    });
    if (!response.ok) {
      const errorText = await response.text();
      let message = errorText.trim();
      try {
        const payload = message ? JSON.parse(message) : {};
        message = payload.error || message;
      } catch (parseError) {
        // Plain-text server errors are shown as-is.
      }
      if (response.status === 404 && message.toLowerCase() === "not found") {
        message = t(
          "web.chargen.export_endpoint_missing",
          "Character export is not available on this server yet. Restart or redeploy the latest server build."
        );
      }
      throw new Error(message || t("web.error.download_failed", "Download failed"));
    }
    const blob = await response.blob();
    const disposition = response.headers.get("Content-Disposition") || "";
    const match = disposition.match(/filename=\"([^\"]+)\"/);
    const filename = match ? match[1] : buildCharGenFilename();
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
    markSaved(t("web.toast.downloaded", "Downloaded"));
  } catch (error) {
    showToast(error.message);
  }
}

function buildCharGenFilename() {
  const game = String(state.chargenGameName || "").trim();
  const character = String(state.chargenCharacterName || "").trim();
  const base = game && character ? `${game}-${character}` : character || game || "character";
  const safe = sanitizeFilename(base);
  return safe.toLowerCase().endsWith(".gmcf") ? safe : `${safe}.gmcf`;
}

function sanitizeFilename(value) {
  return String(value || "")
    .replace(/[\\/:*?"<>|]/g, "_")
    .trim();
}

function resolveCharGenResumeStage() {
  if (state.chargenResolvedArmorClass || normalizeCharGenIdList(state.chargenSelectedArmorIds).length) {
    return "armor";
  }
  if (normalizeCharGenIdList(state.chargenSelectedWeaponIds).length) {
    return "weapons";
  }
  if (
    normalizeCharGenIdList(state.chargenSelectedEquipmentIds).length
    || Number(state.chargenStartingMoneyAmount || 0) > 0
  ) {
    return "equipment";
  }
  if (normalizeCharGenIdList(state.chargenSelectedSpellIds).length) {
    return "spells";
  }
  if (Object.keys(state.chargenSelectedSkillRanks || {}).length) {
    return "skills";
  }
  if (state.chargenClassId) {
    return "classes";
  }
  if (state.chargenRaceId) {
    return "races";
  }
  const stepResults = normalizeCharGenAttributeStepResults(state.chargenAttributeStepResults);
  if (Object.keys(stepResults).length >= 2 && !String(state.chargenAttributeResultChoice || "")) {
    return "attribute-result-choice";
  }
  if (Object.keys(state.chargenAttributeScores || {}).length) {
    return "attributes";
  }
  return "intro";
}

function collectCharGenAttributeScores(inputs) {
  const scores = {};
  inputs.forEach((entry) => {
    const id = String(entry.attributeId || "").trim();
    if (!id) {
      return;
    }
    const value = Number(entry.input.value || 0);
    scores[id] = value;
  });
  return scores;
}

function rollCharGenSet(attributes, method) {
  const terms = Array.isArray(method.diceTerms) ? method.diceTerms : [];
  return attributes.map(() => {
    let total = 0;
    terms.forEach((term) => {
      total += rollCharGenTerm(term);
    });
    return total;
  });
}

function rollCharGenTerm(term) {
  const count = Math.max(0, Number(term.count || 0));
  const sides = Math.max(0, Number(term.sides || 0));
  if (count <= 0 || sides <= 0) {
    return 0;
  }
  const rolls = [];
  for (let i = 0; i < count; i += 1) {
    rolls.push(rollCharGenDie(term, sides));
  }
  rolls.sort((a, b) => a - b);
  const dropLowest = Math.min(Number(term.dropLowest || 0), rolls.length);
  const dropHighest = Math.min(Number(term.dropHighest || 0), rolls.length - dropLowest);
  let total = 0;
  for (let i = dropLowest; i < rolls.length - dropHighest; i += 1) {
    total += rolls[i];
  }
  total += Number(term.flatModifier || 0);
  return total;
}

function rollCharGenDie(term, sides) {
  const threshold = Number(term.explodeThreshold || 0) > 0 ? Number(term.explodeThreshold) : sides;
  let total = rollCharGenFace(term, sides);
  if (!term.exploding) {
    return total;
  }
  let current = total;
  while (current >= threshold && threshold > 0) {
    const extra = rollCharGenFace(term, sides);
    if (extra === 0) {
      break;
    }
    total += extra;
    current = extra;
  }
  return total;
}

function rollCharGenFace(term, sides) {
  const ignored = Array.isArray(term.ignoredFaces) ? term.ignoredFaces : [];
  const maxAttempts = sides * 2;
  for (let attempt = 0; attempt < maxAttempts; attempt += 1) {
    const value = Math.floor(Math.random() * sides) + 1;
    if (!ignored.includes(value)) {
      return value;
    }
  }
  return 0;
}

function formatRollSet(index, values) {
  const label = Array.isArray(values) ? values.join(", ") : "";
  return t("attrgen.roll.set", "Set {index}: {values}")
    .replace("{index}", String(index + 1))
    .replace("{values}", label);
}

function formatCharGenType(value) {
  const safeValue = String(value || "").trim().toLowerCase();
  if (safeValue === "dice") {
    return t("attrgen.type.dice", "Dice Rolling");
  }
  if (safeValue === "point_buy") {
    return t("attrgen.type.point_buy", "Point Buy");
  }
  if (safeValue === "standard_array") {
    return t("attrgen.type.standard_array", "Standard Array/Base Scores");
  }
  if (safeValue === "hybrid") {
    return t("attrgen.type.hybrid", "Hybrid");
  }
  return emptyFallback(value);
}

function formatCharGenDiceTerm(term) {
  const count = Number(term.count || 0);
  const sides = Number(term.sides || 0);
  if (!count || !sides) {
    return t("common.none", "None");
  }
  return `${count}d${sides}`;
}

function resolveCharGenMin(attribute, method) {
  const attrMin = Number(attribute.minValue || 0);
  if (attrMin > 0) {
    return attrMin;
  }
  const methodMin = Number(method.minAttributeValue || 0);
  return methodMin > 0 ? methodMin : 0;
}

function resolveCharGenMax(attribute, method) {
  const attrMax = Number(attribute.maxValue || 0);
  if (attrMax > 0) {
    return attrMax;
  }
  const methodMax = Number(method.maxAttributeValue || 0);
  return methodMax > 0 ? methodMax : 100;
}

function resolveCharGenBase(method) {
  const base = Number(method.baseAttributeValue || 0);
  return base > 0 ? base : 0;
}

function clampCharGen(value, min, max) {
  if (value < min) {
    return min;
  }
  if (value > max) {
    return max;
  }
  return value;
}

function isCharGenPointBuySelected(method) {
  const selected = normalizeCharGenGenerationChoice(state.chargenAttributeGenerationChoice);
  if (["standard_array", "dice", "point_buy"].includes(selected)) {
    return selected === "point_buy";
  }
  const safeMethod = method || {};
  const type = String(safeMethod.generationType || "").trim().toLowerCase();
  if (type === "point_buy") {
    return true;
  }
  if (type !== "hybrid") {
    return false;
  }
  const stages = normalizeHybridStages(safeMethod.hybridStages);
  return !stages.length || stages.includes("point_buy");
}

function resolveCharGenPointBuyApplicationMode(method) {
  const selectedStep = getCharGenSelectedGenerationStep(method, "point_buy");
  if (selectedStep) {
    return normalizeAttributeGenerationApplicationMode(selectedStep.applicationMode);
  }
  const safeMethod = method || {};
  const type = String(safeMethod.generationType || "").trim().toLowerCase();
  if (type !== "hybrid") {
    return "set";
  }
  const hybridStages = normalizeHybridStages(safeMethod.hybridStages);
  if (!hybridStages.length) {
    return "spend";
  }
  let pointIndex = -1;
  let baselineIndex = -1;
  for (let i = 0; i < hybridStages.length; i += 1) {
    const stage = String(hybridStages[i] || "").trim().toLowerCase();
    if (pointIndex < 0 && stage === "point_buy") {
      pointIndex = i;
    }
    if (baselineIndex < 0 && (stage === "standard_array" || stage === "dice")) {
      baselineIndex = i;
    }
  }
  return baselineIndex >= 0 && pointIndex > baselineIndex ? "spend" : "set";
}

function shouldAddCharGenPointBuyToBaseScores(method) {
  return ["add", "spend"].includes(resolveCharGenPointBuyApplicationMode(method));
}

function buildCharGenPointCostMap(entries) {
  const map = {};
  if (!Array.isArray(entries)) {
    return map;
  }
  entries.forEach((entry) => {
    if (!entry) {
      return;
    }
    const value = Number(entry.value);
    const cost = Number(entry.cost);
    if (Number.isFinite(value) && Number.isFinite(cost)) {
      map[value] = cost;
    }
  });
  return map;
}

function resolveCharGenPointCost(value, method, pointCostMap) {
  const safeMethod = method || {};
  const costs = pointCostMap || {};
  const score = Number(value || 0);
  if (Object.prototype.hasOwnProperty.call(costs, score)) {
    return Math.max(0, Number(costs[score] || 0));
  }
  const baseValue = Number(safeMethod.baseAttributeValue || 0);
  const delta = score - baseValue;
  if (delta < 0 && !safeMethod.allowNegativeAttributes) {
    return 0;
  }
  return delta;
}

function buildCharGenArrayMap(entries) {
  const map = {};
  const safeEntries = Array.isArray(entries) ? entries : [];
  safeEntries.forEach((raw) => {
    const entry = String(raw || "").trim();
    const idx = entry.indexOf("=");
    if (idx <= 0 || idx >= entry.length - 1) {
      return;
    }
    const key = entry.slice(0, idx).trim().toLowerCase();
    const valueText = entry.slice(idx + 1).trim();
    if (!key || !valueText) {
      return;
    }
    const value = Number(valueText);
    if (!Number.isFinite(value)) {
      return;
    }
    map[key] = Math.trunc(value);
  });
  return map;
}

function buildCharGenArrayValues(entries) {
  const values = [];
  const safeEntries = Array.isArray(entries) ? entries : [];
  safeEntries.forEach((raw) => {
    const entry = String(raw || "").trim();
    if (!entry) {
      return;
    }
    const idx = entry.indexOf("=");
    const valueText = idx >= 0 ? entry.slice(idx + 1).trim() : entry;
    const value = Number(valueText);
    if (Number.isFinite(value)) {
      values.push(Math.trunc(value));
    }
  });
  return values;
}

function isCharGenOpenStandardArray(method) {
  return String((method || {}).standardArrayAssignmentMode || "").trim().toLowerCase() === "open";
}

function resolveCharGenArrayValue(values, attribute) {
  const safeValues = values || {};
  const safeAttr = attribute || {};
  const id = String(safeAttr.id || "").trim().toLowerCase();
  const name = String(safeAttr.name || "").trim().toLowerCase();
  const display = String(safeAttr.displayName || "").trim().toLowerCase();
  if (name && Object.prototype.hasOwnProperty.call(safeValues, name)) {
    return safeValues[name];
  }
  if (display && Object.prototype.hasOwnProperty.call(safeValues, display)) {
    return safeValues[display];
  }
  if (id && Object.prototype.hasOwnProperty.call(safeValues, id)) {
    return safeValues[id];
  }
  return null;
}

function applyCharGenArrayPreset(attributes, method, inputs, values) {
  const safeAttributes = Array.isArray(attributes) ? attributes : [];
  inputs.forEach((entry, index) => {
    const attribute = safeAttributes[index] || {};
    const resolved = resolveCharGenArrayValue(values, attribute);
    if (resolved == null) {
      return;
    }
    entry.input.value = String(clampCharGen(Number(resolved || 0), entry.min, entry.max));
  });
}

function wireCharGenArrayUI(config) {
  const safeConfig = config || {};
  const method = safeConfig.method || {};
  const attributes = Array.isArray(safeConfig.attributes) ? safeConfig.attributes : [];
  const inputs = Array.isArray(safeConfig.inputs) ? safeConfig.inputs : [];
  const section = safeConfig.section;
  const select = safeConfig.select;
  const emptyLabel = safeConfig.emptyLabel;
  const assignmentSection = safeConfig.assignmentSection;
  const assignmentHint = safeConfig.assignmentHint;
  const availableList = safeConfig.availableList;
  const onStatusChange = safeConfig.onStatusChange;
  const openArray = isCharGenOpenStandardArray(method);
  const standardMap = buildCharGenArrayMap(method.standardArray);
  const eliteMap = buildCharGenArrayMap(method.eliteArray);
  const standardValues = buildCharGenArrayValues(method.standardArray);
  const eliteValues = buildCharGenArrayValues(method.eliteArray);
  const hasStandard = openArray ? standardValues.length > 0 : Object.keys(standardMap).length > 0;
  const hasElite = openArray ? eliteValues.length > 0 : Object.keys(eliteMap).length > 0;
  const validAttributeIds = new Set(inputs.map((entry) => String(entry.attributeId || "").trim()).filter(Boolean));
  const controlsByAttributeId = {};
  let assignments = normalizeCharGenArrayAssignments(state.chargenArrayAssignments);

  const selectedValues = () => select.value === "elite" ? eliteValues : standardValues;
  const selectedMap = () => select.value === "elite" ? eliteMap : standardMap;
  const valueLabel = (valueIndex, values) => t("attrgen.array.option", "Value {number}: {value}")
    .replace("{number}", String(valueIndex + 1))
    .replace("{value}", String(values[valueIndex]));

  const isComplete = () => {
    const key = normalizeCharGenArrayType(select.value);
    if (!key || !inputs.length) {
      return false;
    }
    if (!openArray) {
      const values = selectedMap();
      return inputs.every((entry, index) => resolveCharGenArrayValue(values, attributes[index] || {}) != null);
    }
    const values = selectedValues();
    if (values.length !== inputs.length) {
      return false;
    }
    const usedIndices = new Set();
    for (const entry of inputs) {
      const attributeId = String(entry.attributeId || "").trim();
      const valueIndex = assignments[attributeId];
      if (!Number.isInteger(valueIndex) || valueIndex < 0 || valueIndex >= values.length || usedIndices.has(valueIndex)) {
        return false;
      }
      usedIndices.add(valueIndex);
    }
    return usedIndices.size === values.length;
  };

  const render = () => {
    const key = normalizeCharGenArrayType(select.value);
    const values = selectedValues();
    state.chargenSelectedArrayType = key;
    emptyLabel.textContent = key
      ? openArray
        ? t("attrgen.arrays.open.values", "Values: {values}").replace("{values}", values.join(", "))
        : key === "elite"
          ? t("attrgen.arrays.elite.section", "Elite Arrays")
          : t("attrgen.type.standard_array", "Standard Array/Base Scores")
      : t("common.none", "None");

    inputs.forEach((entry) => {
      entry.input.readOnly = true;
    });

    if (!key) {
      assignments = {};
      state.chargenArrayAssignments = {};
      assignmentSection.classList.add("hidden");
      availableList.innerHTML = "";
      inputs.forEach((entry) => {
        entry.input.value = "";
      });
      if (typeof onStatusChange === "function") {
        onStatusChange(false, {});
      }
      return;
    }

    if (!openArray) {
      assignmentSection.classList.add("hidden");
      if (key) {
        applyCharGenArrayPreset(attributes, method, inputs, selectedMap());
      }
    } else {
      assignmentSection.classList.remove("hidden");
      const sanitized = {};
      const usedIndices = new Set();
      Object.keys(assignments).forEach((attributeId) => {
        const valueIndex = assignments[attributeId];
        if (validAttributeIds.has(attributeId)
          && Number.isInteger(valueIndex)
          && valueIndex >= 0
          && valueIndex < values.length
          && !usedIndices.has(valueIndex)) {
          sanitized[attributeId] = valueIndex;
          usedIndices.add(valueIndex);
        }
      });
      assignments = sanitized;
      const availableIndices = values.map((value, index) => index).filter((index) => !usedIndices.has(index));
      availableList.innerHTML = availableIndices.length
        ? availableIndices.map((index) => `<div class="list-item">${escapeHtml(valueLabel(index, values))}</div>`).join("")
        : `<div class="field-hint">${t("attrgen.array.assignment.all_used", "All array values are assigned.")}</div>`;
      assignmentHint.textContent = values.length === inputs.length
        ? t(
          "attrgen.array.assignment.player",
          "Choose one available array value for each Attribute. Clear an assignment to return that value to the pool."
        )
        : t(
          "attrgen.array.assignment.count",
          "This array must contain exactly one value for each Attribute before it can be assigned."
        );

      inputs.forEach((entry) => {
        const attributeId = String(entry.attributeId || "").trim();
        const valueIndex = assignments[attributeId];
        const hasAssignment = Number.isInteger(valueIndex) && valueIndex >= 0 && valueIndex < values.length;
        entry.input.value = hasAssignment ? String(clampCharGen(values[valueIndex], entry.min, entry.max)) : "";
        const controls = controlsByAttributeId[attributeId];
        if (!controls) {
          return;
        }
        const usedByOthers = new Set(
          Object.entries(assignments)
            .filter(([assignedAttributeId]) => assignedAttributeId !== attributeId)
            .map(([, assignedValueIndex]) => assignedValueIndex)
        );
        const options = [`<option value="">${t("attrgen.array.assignment.choose", "Choose a value")}</option>`];
        values.forEach((value, index) => {
          if (usedByOthers.has(index)) {
            return;
          }
          const selected = index === valueIndex ? " selected" : "";
          options.push(`<option value="${index}"${selected}>${escapeHtml(valueLabel(index, values))}</option>`);
        });
        controls.innerHTML = `
          <select aria-label="${escapeHtml(t(
            "attrgen.array.assignment.for_attribute",
            "Array value for {attribute}"
          ).replace("{attribute}", resolveCharGenAttributeName(attributeId)))}">${options.join("")}</select>
          <button class="btn ghost" type="button" ${hasAssignment ? "" : "disabled"}>${t("attrgen.array.assignment.clear", "Clear")}</button>
        `;
        const assignmentSelect = controls.querySelector("select");
        const clearButton = controls.querySelector("button");
        assignmentSelect.addEventListener("change", () => {
          const nextIndex = Number(assignmentSelect.value);
          if (!assignmentSelect.value || !Number.isInteger(nextIndex)) {
            delete assignments[attributeId];
          } else {
            Object.keys(assignments).forEach((assignedAttributeId) => {
              if (assignedAttributeId !== attributeId && assignments[assignedAttributeId] === nextIndex) {
                delete assignments[assignedAttributeId];
              }
            });
            assignments[attributeId] = nextIndex;
          }
          state.chargenArrayAssignments = { ...assignments };
          state.chargenAttributeResultChoice = "";
          render();
          saveCharGenDraftLocal();
        });
        clearButton.addEventListener("click", () => {
          delete assignments[attributeId];
          state.chargenArrayAssignments = { ...assignments };
          state.chargenAttributeResultChoice = "";
          render();
          saveCharGenDraftLocal();
        });
      });
    }

    state.chargenArrayAssignments = openArray ? { ...assignments } : {};
    const complete = isComplete();
    const scores = complete ? collectCharGenAttributeScores(inputs) : {};
    if (complete) {
      state.chargenAttributeScores = { ...scores };
    }
    if (typeof onStatusChange === "function") {
      onStatusChange(complete, scores);
    }
  };

  if (!section || !select || !emptyLabel || !assignmentSection || !assignmentHint || !availableList) {
    return { isComplete: () => false, render: () => {} };
  }
  if (!hasStandard && !hasElite) {
    section.classList.add("hidden");
    assignmentSection.classList.add("hidden");
    inputs.forEach((entry) => {
      entry.input.readOnly = true;
    });
    return { isComplete: () => false, render: () => {} };
  }

  section.classList.remove("hidden");
  const options = [`<option value="">${t("common.none", "None")}</option>`];
  if (hasStandard) {
    options.push(`<option value="standard">${t("attrgen.type.standard_array", "Standard Array/Base Scores")}</option>`);
  }
  if (hasElite) {
    options.push(`<option value="elite">${t("attrgen.arrays.elite.section", "Elite Arrays")}</option>`);
  }
  select.innerHTML = options.join("");
  const savedType = normalizeCharGenArrayType(state.chargenSelectedArrayType);
  const preferred = normalizeCharGenArrayType(method.defaultArrayType);
  if ((savedType === "elite" && hasElite) || (savedType === "standard" && hasStandard)) {
    select.value = savedType;
  } else if (preferred === "elite" && hasElite) {
    select.value = "elite";
  } else if (hasStandard) {
    select.value = "standard";
  } else if (hasElite) {
    select.value = "elite";
  }

  if (openArray) {
    inputs.forEach((entry) => {
      const attributeId = String(entry.attributeId || "").trim();
      const field = entry.input.closest(".field");
      if (!field || !attributeId) {
        return;
      }
      const controls = document.createElement("div");
      controls.className = "array-assignment-controls";
      controls.dataset.attributeId = attributeId;
      field.appendChild(controls);
      controlsByAttributeId[attributeId] = controls;
    });
  }

  select.addEventListener("change", () => {
    assignments = {};
    state.chargenArrayAssignments = {};
    state.chargenSelectedArrayType = normalizeCharGenArrayType(select.value);
    clearCharGenAttributeStepResults();
    render();
    saveCharGenDraftLocal();
  });
  render();
  return { isComplete, render };
}

function maybeApplyCharGenDefaultArray(method, attributes, inputs) {
  if (Object.keys(state.chargenAttributeScores || {}).length) {
    return;
  }
  const safeMethod = method || {};
  if (isCharGenOpenStandardArray(safeMethod)) {
    return;
  }
  const type = String(safeMethod.generationType || "").trim().toLowerCase();
  if (type !== "standard_array" && type !== "hybrid") {
    return;
  }
  const standardMap = buildCharGenArrayMap(safeMethod.standardArray);
  const eliteMap = buildCharGenArrayMap(safeMethod.eliteArray);
  const hasStandard = Object.keys(standardMap).length > 0;
  const hasElite = Object.keys(eliteMap).length > 0;
  if (!hasStandard && !hasElite) {
    return;
  }
  const preferred = String(safeMethod.defaultArrayType || "").trim().toLowerCase();
  const key = preferred === "elite" && hasElite ? "elite" : hasStandard ? "standard" : hasElite ? "elite" : "";
  if (!key) {
    return;
  }
  const values = key === "elite" ? eliteMap : standardMap;
  applyCharGenArrayPreset(attributes, method, inputs, values);
}

function isCharGenDiceEnabled(method, attributes) {
  const type = String(method.generationType || "").trim().toLowerCase();
  const isDice = type === "dice" || type === "hybrid";
  const terms = Array.isArray(method.diceTerms) ? method.diceTerms : [];
  return isDice && terms.length > 0 && attributes.length > 0;
}

function isCharGenDiceSubstitutionEnabled(method) {
  const safeMethod = method || {};
  const type = String(safeMethod.generationType || "").trim().toLowerCase();
  const isDice = type === "dice" || type === "hybrid";
  const maxSubstitutions = Number(safeMethod.maxDiceSubstitutions || 0);
  return isDice && Boolean(safeMethod.allowDiceSubstitution) && maxSubstitutions > 0;
}

async function renderSetup() {
  if (!ensureDraft()) {
    return;
  }
  if (state.mode !== "builder") {
    setMode("builder");
  }
  setStep("setup");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const data = await api("GET", `/api/drafts/${state.draftId}/setup`);
    state.setupComplete = Boolean(String(data.name || "").trim());
    renderSidebar();
    const gameTypes = data.gameTypes || [];
    const options = [""].concat(gameTypes).map((type) => {
      const label = type || t("setup.game.type.placeholder", "Select a type");
      const selected = type === data.gameType ? "selected" : "";
      return `<option value="${type}" ${selected}>${label}</option>`;
    });

    view.innerHTML = `
      <section class="panel">
        <h1>${t("setup.title", "Game Setup")}</h1>
        <div class="grid">
          <div class="field">
            <label for="gameName">${t("setup.game.name", "Game Name")}</label>
            <input type="text" id="gameName" value="${escapeHtml(data.name)}">
            <div class="field-hint">${t(
              "setup.game.name.hint",
              "The game name becomes the filename when you download this ruleset as a .gmrf file."
            )}</div>
          </div>
          <div class="field">
            <label for="gameDescription">${t("setup.game.description", "Game Description")}</label>
            <textarea id="gameDescription" class="game-description-input" rows="15">${escapeHtml(data.description)}</textarea>
          </div>
          <div class="field">
            <label for="gameType">${t("setup.game.type", "Game Type")}</label>
            <select id="gameType">${options.join("")}</select>
          </div>
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToSplash" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn" id="setupContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    document.getElementById("backToSplash").addEventListener("click", navigateBackInApp);
    let setupSavePromise = Promise.resolve(true);
    const saveSetup = (showNameError = false) => {
      const name = document.getElementById("gameName").value.trim();
      if (!name) {
        if (showNameError) {
          showToast(t("common.name.required", "Name is required."));
        }
        return Promise.resolve(false);
      }
      const description = document.getElementById("gameDescription").value;
      const gameType = document.getElementById("gameType").value;
      const payload = { name, description, gameType };
      setupSavePromise = setupSavePromise.catch(() => false).then(async () => {
        try {
          await api("POST", `/api/drafts/${state.draftId}/setup`, payload);
          state.setupComplete = true;
          renderSidebar();
          markSaved(t("web.toast.setup_saved", "Setup saved"));
          return true;
        } catch (error) {
          showToast(error.message);
          return false;
        }
      });
      return setupSavePromise;
    };
    ["gameName", "gameDescription", "gameType"].forEach((id) => {
      document.getElementById(id).addEventListener("change", () => saveSetup(false));
    });
    document.getElementById("setupContinue").addEventListener("click", async () => {
      if (await saveSetup(true)) {
        renderMeasurements();
      }
    });
  } catch (error) {
    showToast(error.message);
  }
}

async function renderMeasurements() {
  if (!ensureDraft()) {
    return;
  }
  setStep("measurements");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const data = await api("GET", `/api/drafts/${state.draftId}/measurements`);
    let timeUnits = Array.isArray(data.timeUnits) ? data.timeUnits : [];
    const secondsLabel = t("measurements.timeUnits.seconds", "seconds");
    const displayTimeUnits = buildDisplayTimeUnits(timeUnits, secondsLabel);
    const baseTimeUnits = buildBaseTimeUnits(timeUnits, secondsLabel);
    const selectedSystem = String(data.weightSystem || "metric");
    const weightOptions = [
      { value: "metric", label: t("measurements.weight.metric", "Metric") },
      { value: "english", label: t("measurements.weight.english", "English") },
    ]
      .map((option) => {
        const selected = option.value === selectedSystem ? "selected" : "";
        return `<option value="${option.value}" ${selected}>${escapeHtml(option.label)}</option>`;
      })
      .join("");

    const baseUnitOptions = baseTimeUnits
      .map((unit) => {
        const label = escapeHtml(pluralizeTimeUnit(unit.name, 2));
        return `<option value="${unit.duration}">${label}</option>`;
      })
      .join("");

    const timeUnitRows = displayTimeUnits
      .map((unit) => {
        const rawName = String(unit.name || "");
        const name = escapeHtml(rawName);
        const key = escapeHtml(rawName.trim().toLowerCase());
        const displayAmount = Number(unit.displayAmount || 0);
        const displayUnit = escapeHtml(pluralizeTimeUnit(String(unit.displayUnit || secondsLabel), displayAmount));
        return renderCollectionRow(
          `<div><strong>${name}</strong> ${displayAmount} ${displayUnit}</div>`,
          [
            collectionEditAction("edit-time-unit", key),
            collectionRemoveAction("remove-time-unit", key),
          ]
        );
      })
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${t("measurements.title", "Weights & Measures")}</h1>
        <div class="field">
          <label for="weightSystem">${t("measurements.weight.label", "Weight System")}</label>
          <select id="weightSystem">${weightOptions}</select>
        </div>
        <h2 class="time-units-title">${t("measurements.timeUnits.title", "Time Units")}</h2>
        <button class="btn time-unit-add-button" id="addTimeUnit" type="button">${t("measurements.timeUnits.add", "Add Time Unit")}</button>
        <div class="list" id="timeUnitList">
          ${timeUnitRows || `<div class="list-item">${t("measurements.timeUnits.none", "No time units yet.")}</div>`}
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToSetup" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn ghost" id="measurementsContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    const saveMeasurements = async (units, toastKey, fallback) => {
      const weightSystem = document.getElementById("weightSystem").value;
      await api("POST", `/api/drafts/${state.draftId}/measurements`, {
        weightSystem,
        timeUnits: units,
      });
      if (toastKey || fallback) {
        markSaved(t(toastKey, fallback));
      }
    };

    const openTimeUnitEditor = (unit = null) => {
      const editing = Boolean(unit);
      const originalKey = editing
        ? String(unit.name || "").trim().toLowerCase()
        : "";
      const displayBase = editing
        ? baseTimeUnits.find((entry) => {
          return String(entry.name || "").trim().toLowerCase()
            === String(unit.displayUnit || secondsLabel).trim().toLowerCase();
        })
        : baseTimeUnits[0];

      timeUnitEditTitle.textContent = editing
        ? t("measurements.timeUnits.edit", "Edit Time Unit")
        : t("measurements.timeUnits.add", "Add Time Unit");
      timeUnitEditNameLabel.textContent = t("measurements.timeUnits.name", "Unit Name");
      timeUnitEditAmountLabel.textContent = t("measurements.timeUnits.amount", "Amount");
      timeUnitEditBaseLabel.textContent = t("measurements.timeUnits.base", "Unit");
      timeUnitEditCancel.textContent = t("common.cancel", "Cancel");
      timeUnitEditSave.textContent = editing
        ? t("common.save", "Save")
        : t("measurements.timeUnits.add", "Add Time Unit");
      timeUnitEditName.placeholder = t("measurements.timeUnits.name.placeholder", "e.g., round");
      timeUnitEditAmount.placeholder = t("measurements.timeUnits.duration.placeholder", "e.g., 6");
      timeUnitEditName.value = editing ? String(unit.name || "") : "";
      timeUnitEditAmount.value = editing ? String(unit.displayAmount || "") : "";
      timeUnitEditBase.innerHTML = baseUnitOptions;
      timeUnitEditBase.value = String(displayBase ? displayBase.duration : 1);
      timeUnitEditSave.disabled = false;
      timeUnitEditModal.classList.remove("hidden");
      window.requestAnimationFrame(() => timeUnitEditName.focus());

      timeUnitEditCancel.onclick = () => {
        timeUnitEditModal.classList.add("hidden");
      };
      timeUnitEditSave.onclick = async () => {
        const name = timeUnitEditName.value.trim();
        const nameKey = name.toLowerCase();
        const amount = Number.parseInt(timeUnitEditAmount.value.trim(), 10);
        const baseDuration = Number.parseInt(timeUnitEditBase.value, 10);
        if (!name) {
          showToast(t("common.name.required", "Name is required."));
          return;
        }
        if (timeUnits.some((entry) => {
          const entryKey = String(entry.name || "").trim().toLowerCase();
          return entryKey === nameKey && entryKey !== originalKey;
        })) {
          showToast(t("measurements.timeUnits.name.exists", "A time unit with that name already exists."));
          return;
        }
        if (!Number.isFinite(amount) || amount <= 0) {
          showToast(t("web.toast.time_unit_invalid", "Duration must be a positive number."));
          return;
        }
        if (!Number.isFinite(baseDuration) || baseDuration <= 0) {
          showToast(t("measurements.timeUnits.base.invalid", "Select a valid base unit."));
          return;
        }
        let duration = 0;
        if (amount <= Math.floor(Number.MAX_SAFE_INTEGER / baseDuration)) {
          duration = amount * baseDuration;
        }
        if (!Number.isSafeInteger(duration) || duration <= 0) {
          showToast(t("measurements.timeUnits.duration.invalid", "Duration must be a positive whole number."));
          return;
        }
        const updated = editing
          ? timeUnits.map((entry) => {
            const entryKey = String(entry.name || "").trim().toLowerCase();
            return entryKey === originalKey ? { name, duration } : entry;
          })
          : [...timeUnits, { name, duration }];
        timeUnitEditSave.disabled = true;
        try {
          await saveMeasurements(
            updated,
            editing ? "web.toast.time_unit_updated" : "web.toast.time_unit_added",
            editing ? "Time unit updated" : "Time unit added"
          );
          timeUnitEditModal.classList.add("hidden");
          renderMeasurements();
        } catch (error) {
          showToast(error.message);
          timeUnitEditSave.disabled = false;
        }
      };
    };

    document.getElementById("addTimeUnit").addEventListener("click", () => {
      openTimeUnitEditor();
    });

    document.querySelectorAll("[data-edit-time-unit]").forEach((button) => {
      button.addEventListener("click", () => {
        const originalKey = String(button.dataset.editTimeUnit || "").trim().toLowerCase();
        const unit = displayTimeUnits.find((entry) => {
          return String(entry.name || "").trim().toLowerCase() === originalKey;
        });
        if (unit) {
          openTimeUnitEditor(unit);
        }
      });
    });

    document.querySelectorAll("[data-remove-time-unit]").forEach((button) => {
      button.addEventListener("click", async () => {
        const key = String(button.dataset.removeTimeUnit || "").trim().toLowerCase();
        if (!key) {
          return;
        }
        const confirmed = await showConfirm(
          t("measurements.timeUnits.remove.confirm", "Remove this time unit?"),
          t("common.remove", "Remove")
        );
        if (!confirmed) {
          return;
        }
        const updated = timeUnits.filter((unit) => {
          return String(unit.name || "").trim().toLowerCase() !== key;
        });
        try {
          await saveMeasurements(updated, "web.toast.time_unit_removed", "Time unit removed");
          renderMeasurements();
        } catch (error) {
          showToast(error.message);
        }
      });
    });

    document.getElementById("weightSystem").addEventListener("change", async () => {
      try {
        await saveMeasurements(timeUnits, "web.toast.measurements_saved", "Measurements saved");
      } catch (error) {
        showToast(error.message);
      }
    });

    document.getElementById("backToSetup").addEventListener("click", navigateBackInApp);
    document.getElementById("measurementsContinue").addEventListener("click", async () => {
      try {
        await saveMeasurements(timeUnits, "web.toast.measurements_saved", "Measurements saved");
        renderDice();
      } catch (error) {
        showToast(error.message);
      }
    });
  } catch (error) {
    showToast(error.message);
  }
}

function buildDisplayTimeUnits(units, secondsLabel) {
  const normalized = (Array.isArray(units) ? units : [])
    .map((unit) => ({
      name: String(unit.name || "").trim(),
      duration: Number(unit.duration || 0),
    }))
    .filter((unit) => unit.name && Number.isFinite(unit.duration) && unit.duration > 0);
  normalized.sort((left, right) => {
    if (left.duration !== right.duration) {
      return left.duration - right.duration;
    }
    return left.name.toLowerCase().localeCompare(right.name.toLowerCase());
  });
  return normalized.map((unit, index) => {
    let displayAmount = unit.duration;
    let displayUnit = secondsLabel;
    for (let lowerIndex = index - 1; lowerIndex >= 0; lowerIndex -= 1) {
      const lower = normalized[lowerIndex];
      if (lower.duration <= 0 || lower.duration >= unit.duration) {
        continue;
      }
      if (unit.duration % lower.duration === 0) {
        displayAmount = unit.duration / lower.duration;
        displayUnit = lower.name;
        break;
      }
    }
    return {
      ...unit,
      displayAmount,
      displayUnit,
    };
  });
}

function buildBaseTimeUnits(units, secondsLabel) {
  const baseUnits = [{ name: secondsLabel, duration: 1 }];
  const seenNames = new Set([String(secondsLabel || "").trim().toLowerCase()]);
  const normalized = (Array.isArray(units) ? units : [])
    .map((unit) => ({
      name: String(unit.name || "").trim(),
      duration: Number(unit.duration || 0),
    }))
    .filter((unit) => unit.name && Number.isFinite(unit.duration) && unit.duration > 0)
    .sort((left, right) => {
      if (left.duration !== right.duration) {
        return left.duration - right.duration;
      }
      return left.name.toLowerCase().localeCompare(right.name.toLowerCase());
    });
  normalized.forEach((unit) => {
    const key = unit.name.toLowerCase();
    if (seenNames.has(key)) {
      return;
    }
    seenNames.add(key);
    baseUnits.push(unit);
  });
  return baseUnits;
}

function pluralizeTimeUnit(unit, amount) {
  const safeUnit = String(unit || "").trim();
  if (!safeUnit || Number(amount) === 1) {
    return safeUnit;
  }
  const lower = safeUnit.toLowerCase();
  if (lower.endsWith("s")) {
    return safeUnit;
  }
  if (lower.endsWith("y") && safeUnit.length > 1 && !"aeiou".includes(lower.charAt(lower.length - 2))) {
    return `${safeUnit.slice(0, -1)}ies`;
  }
  return `${safeUnit}s`;
}

async function renderDice() {
  if (!ensureDraft()) {
    return;
  }
  setStep("dice");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const data = await api("GET", `/api/drafts/${state.draftId}/dice`);
    const diceOptions = [2, 4, 6, 8, 10, 12, 20, 100];
    const toggles = diceOptions
      .map((sides) => {
        const checked = data.diceUsed.includes(sides) ? "checked" : "";
        return `
          <label class="toggle">
            <input type="checkbox" data-sides="${sides}" ${checked}>
            d${sides}
          </label>
        `;
      })
      .join("");

    const ranges = (data.customRanges || [])
      .map(
        (range, index) => renderCollectionRow(
          `<span>${range.min} - ${range.max}</span>`,
          [
            collectionEditAction("edit-range", index),
            collectionRemoveAction("remove-range", index),
          ]
        )
      )
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${t("dice.title", "Dice Options")}</h1>
        <h2>${t("dice.standard", "Standard Dice")}</h2>
        <div class="toggle-group" id="standardDice">${toggles}</div>
        <h2>${t("dice.custom", "Custom Ranges")}</h2>
        <button class="btn custom-range-add-button" id="addRange" type="button">${t("dice.add", "Add Range")}</button>
        <div class="list" id="rangeList">${ranges || `<div class="list-item">${t("web.dice.custom.none", "No custom ranges yet.")}</div>`}</div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToSetup" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn" id="diceContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    document.querySelectorAll("#standardDice input").forEach((checkbox) => {
      checkbox.addEventListener("change", async (event) => {
        const sides = Number(event.target.dataset.sides);
        const selected = event.target.checked;
        try {
          await api("POST", `/api/drafts/${state.draftId}/dice/standard`, { sides, selected });
          markSaved(t("web.toast.dice_updated", "Dice updated"));
        } catch (error) {
          showToast(error.message);
        }
      });
    });

    const customRanges = Array.isArray(data.customRanges) ? data.customRanges : [];
    const openDiceRangeEditor = (index = -1) => {
      const editing = Number.isInteger(index) && index >= 0 && index < customRanges.length;
      const range = editing ? customRanges[index] : { min: 1, max: 6 };
      diceRangeTitle.textContent = editing ? t("dice.edit", "Edit Range") : t("dice.add", "Add Range");
      diceRangeMinLabel.textContent = t("dice.min", "Min");
      diceRangeMaxLabel.textContent = t("dice.max", "Max");
      diceRangeCancel.textContent = t("common.cancel", "Cancel");
      diceRangeSave.textContent = editing ? t("common.save", "Save") : t("dice.add", "Add Range");
      diceRangeMin.value = String(range.min);
      diceRangeMax.value = String(range.max);
      diceRangeSave.disabled = false;
      diceRangeModal.classList.remove("hidden");
      window.requestAnimationFrame(() => diceRangeMin.focus());

      diceRangeCancel.onclick = () => {
        diceRangeModal.classList.add("hidden");
      };
      diceRangeSave.onclick = async () => {
        const min = Number(diceRangeMin.value);
        const max = Number(diceRangeMax.value);
        diceRangeSave.disabled = true;
        try {
          await api(
            "POST",
            `/api/drafts/${state.draftId}/dice/custom${editing ? "/update" : ""}`,
            editing ? { originalMin: range.min, originalMax: range.max, min, max } : { min, max }
          );
          markSaved(t(editing ? "web.toast.range_updated" : "web.toast.range_added", editing ? "Range updated" : "Range added"));
          diceRangeModal.classList.add("hidden");
          renderDice();
        } catch (error) {
          showToast(error.message);
          diceRangeSave.disabled = false;
        }
      };
    };

    document.getElementById("addRange").addEventListener("click", () => openDiceRangeEditor());

    document.querySelectorAll("#rangeList button").forEach((button) => {
      button.addEventListener("click", async () => {
        const index = Number(button.dataset.editRange ?? button.dataset.removeRange);
        const range = customRanges[index];
        if (!range) {
          return;
        }
        if (button.hasAttribute("data-edit-range")) {
          openDiceRangeEditor(index);
          return;
        }
        const min = Number(range.min);
        const max = Number(range.max);
        const confirmed = await showConfirm(
          t("web.confirm.remove.custom_range", "Remove this custom range?"),
          t("common.remove", "Remove")
        );
        if (!confirmed) {
          return;
        }
        try {
          await api("DELETE", `/api/drafts/${state.draftId}/dice/custom`, { min, max });
          markSaved(t("web.toast.range_removed", "Range removed"));
          renderDice();
        } catch (error) {
          showToast(error.message);
        }
      });
    });

    document.getElementById("backToSetup").addEventListener("click", navigateBackInApp);
    document.getElementById("diceContinue").addEventListener("click", renderAttributeTypes);
  } catch (error) {
    showToast(error.message);
  }
}

async function renderAttributeTypes(openKey = "") {
  if (!ensureDraft()) {
    return;
  }
  setStep("attribute-types");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const data = await api("GET", `/api/drafts/${state.draftId}/attribute-types`);
    const systemName = String(data.systemName || "");
    const title = systemNameTitle(systemName, t("attrtypes.title", "Attribute Categories"));
    const types = sortByLabel(data.types || [], (type) => type.displayName || type.name || type.key || "");
    const typeMap = {};
    const list = types
      .map((type) => {
        typeMap[type.key] = type;
        return renderCollectionRow(
          `<span>${escapeHtml(type.displayName)}</span>`,
          [
            collectionEditAction("edit-type", type.key),
            collectionRemoveAction("remove-type", type.key),
          ]
        );
      })
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${escapeHtml(title)}</h1>
        ${renderSystemNameControls(systemName, t("attrtypes.title", "Attribute Categories"))}
        <button class="btn" id="addType" type="button">${t("attrtypes.add", "Add Category")}</button>
        <div class="list" id="typeList">${list || `<div class="list-item">${t("web.attrtypes.none", "No categories yet.")}</div>`}</div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToDice" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn" id="typesContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    document.getElementById("addType").addEventListener("click", () => {
      openAttributeTypeCreate();
    });

    document.querySelectorAll("#typeList [data-remove-type]").forEach((button) => {
      button.addEventListener("click", async () => {
        const key = button.dataset.removeType;
        const confirmed = await showConfirm(
          t("web.confirm.remove.attribute_type", "Remove this attribute category?"),
          t("common.remove", "Remove")
        );
        if (!confirmed) {
          return;
        }
        try {
          await api("DELETE", `/api/drafts/${state.draftId}/attribute-types`, { key });
          markSaved(t("web.toast.type_removed", "Category removed"));
          renderAttributeTypes();
        } catch (error) {
          showToast(error.message);
        }
      });
    });

    document.querySelectorAll("#typeList [data-edit-type]").forEach((button) => {
      button.addEventListener("click", () => {
        const key = button.dataset.editType;
        openAttributeTypeEditor(typeMap[key]);
      });
    });

    document.getElementById("backToDice").addEventListener("click", navigateBackInApp);
    document.getElementById("typesContinue").addEventListener("click", renderAttributes);
    wireSystemNameSave("attribute-types", () => renderAttributeTypes());
    if (openKey) {
      const target = typeMap[openKey];
      if (target) {
        openAttributeTypeEditor(target);
      }
    }
  } catch (error) {
    showToast(error.message);
  }
}

async function renderAttributes(openId = "") {
  if (!ensureDraft()) {
    return;
  }
  setStep("attributes");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const data = await api("GET", `/api/drafts/${state.draftId}/attributes`);
    attributeTypeOptions = Array.isArray(data.types) ? data.types.slice() : [];
    attributeEffectOptions = sortByLabel(
      Array.isArray(data.effects) ? data.effects : [],
      (effect) => effect.displayName || effect.name || ""
    );
    state.applyAttributeModifiersToAllAttributes = Boolean(data.applyAttributeModifiersToAllAttributes);
    state.attributeModifiers = normalizeModifierEntries(data.attributeModifiers || []);
    state.defaultAttributeMinScore = Number(data.defaultAttributeMinScore || 0);
    state.defaultAttributeMaxScore = Number(data.defaultAttributeMaxScore || 0);
    const systemName = String(data.systemName || "");
    const title = systemNameTitle(systemName, t("attributes.title", "Attributes"));
    const typeOptions = [`<option value="">${t("attrtypes.none", "None")}</option>`]
      .concat(attributeTypeOptions.map((type) => `<option value="${type.key}">${escapeHtml(type.displayName)}</option>`))
      .join("");

    const attributeMap = {};
    const list = (data.attributes || [])
      .map((attr) => {
        attributeMap[attr.id] = attr;
        return renderCollectionRow(
          `<div>
            <div>${escapeHtml(attr.displayName)}</div>
            <div class="badge">${escapeHtml(attr.typeName || t("attrtypes.none", "None"))}</div>
          </div>`,
          [
            collectionEditAction("edit-attr", attr.id),
            collectionAction("edit-type", attr.id, t("web.attributes.change_type", "Change Category")),
            collectionRemoveAction("remove-attr", attr.id),
          ]
        );
      })
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${escapeHtml(title)}</h1>
        ${renderSystemNameControls(systemName, t("attributes.title", "Attributes"))}
        <button class="btn" id="addAttribute" type="button">${t("attributes.add", "Add Attribute")}</button>
        <div class="list" id="attributeList">${list || `<div class="list-item">${t("web.attributes.none", "No attributes yet.")}</div>`}</div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToTypes" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn" id="attributesContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    document.getElementById("addAttribute").addEventListener("click", () => {
      openAttributeCreate(attributeTypeOptions);
    });

    document.querySelectorAll("[data-remove-attr]").forEach((button) => {
      button.addEventListener("click", async () => {
        const id = button.dataset.removeAttr;
        const confirmed = await showConfirm(
          t("web.confirm.remove.attribute", "Remove this attribute?"),
          t("common.remove", "Remove")
        );
        if (!confirmed) {
          return;
        }
        try {
          await api("DELETE", `/api/drafts/${state.draftId}/attributes`, { id });
          markSaved(t("web.toast.attribute_removed", "Attribute removed"));
          renderAttributes();
        } catch (error) {
          showToast(error.message);
        }
      });
    });

    document.querySelectorAll("[data-edit-type]").forEach((button) => {
      button.addEventListener("click", () => {
        const id = button.dataset.editType;
        typeSelector.innerHTML = typeOptions;
        const attribute = attributeMap[id];
        if (attribute) {
          typeSelector.value = attribute.typeKey || "";
        }
        typeModal.classList.remove("hidden");
        pendingTypeUpdate = { attributeId: id };
      });
    });

    document.querySelectorAll("[data-edit-attr]").forEach((button) => {
      button.addEventListener("click", () => {
        const id = button.dataset.editAttr;
        openAttributeEditor(attributeMap[id], attributeTypeOptions);
      });
    });

    document.getElementById("backToTypes").addEventListener("click", navigateBackInApp);
    document.getElementById("attributesContinue").addEventListener("click", renderAttributeGeneration);
    wireSystemNameSave("attributes", () => renderAttributes());
    if (openId) {
      const target = attributeMap[openId];
      if (target) {
        openAttributeEditor(target, attributeTypeOptions);
      }
    }
  } catch (error) {
    showToast(error.message);
  }
}

function normalizeHybridStages(stages) {
  if (!Array.isArray(stages)) {
    return [];
  }
  const resolved = [];
  const seen = new Set();
  stages.forEach((entry) => {
    const key = String(entry || "").trim().toLowerCase();
    if (!key) {
      return;
    }
    if (seen.has(key)) {
      return;
    }
    seen.add(key);
    resolved.push(key);
  });
  return resolved;
}

function normalizeAttributeGenerationMethodType(value) {
  const safeValue = String(value || "").trim().toLowerCase();
  if (["standard_array", "dice", "point_buy"].includes(safeValue)) {
    return safeValue;
  }
  return "";
}

function normalizeAttributeGenerationApplicationMode(value) {
  const safeValue = String(value || "").trim().toLowerCase();
  if (["add", "spend", "choose"].includes(safeValue)) {
    return safeValue;
  }
  return "set";
}

function formatAttributeGenerationApplicationMode(value) {
  const safeValue = normalizeAttributeGenerationApplicationMode(value);
  if (safeValue === "add") {
    return t("attrgen.options.mode.add.short", "add");
  }
  if (safeValue === "spend") {
    return t("attrgen.options.mode.spend.short", "spend");
  }
  if (safeValue === "choose") {
    return t("attrgen.options.mode.choose.short", "choose");
  }
  return t("attrgen.options.mode.set.short", "initial");
}

function normalizeAttributeGenerationOptions(options) {
  const safeOptions = Array.isArray(options) ? options : [];
  const normalized = [];
  safeOptions.forEach((option, index) => {
    const steps = Array.isArray(option.steps) ? option.steps : [];
    const normalizedSteps = [];
    steps.forEach((step) => {
      const methodType = normalizeAttributeGenerationMethodType(step.methodType);
      if (!methodType || (normalizedSteps.length > 0 && methodType === "standard_array")) {
        return;
      }
      let applicationMode = normalizeAttributeGenerationApplicationMode(step.applicationMode);
      if (!normalizedSteps.length) {
        applicationMode = "set";
      } else if (applicationMode === "set") {
        applicationMode = "choose";
      }
      normalizedSteps.push({ methodType, applicationMode });
    });
    if (!normalizedSteps.length) {
      return;
    }
    normalized.push({
      id: String(option.id || `option-${index + 1}`).trim() || `option-${index + 1}`,
      name: String(option.name || "").trim() || formatCharGenType(normalizedSteps[0].methodType),
      steps: normalizedSteps,
    });
  });
  return normalized;
}

function getAttributeGenerationOrder(generationType, hybridStages) {
  const safeType = String(generationType || "").toLowerCase();
  const order = [];
  const resolvedStages = normalizeHybridStages(hybridStages);
  const includeHybrid = safeType === "hybrid";
  const includeAll = includeHybrid && !resolvedStages.length;
  const includes = (key) => includeAll || resolvedStages.includes(key);
  if (safeType === "standard_array" || (includeHybrid && includes("standard_array"))) {
    order.push("standard-array");
  }
  if (safeType === "dice" || (includeHybrid && includes("dice"))) {
    order.push("dice-rolling");
  }
  if (safeType === "point_buy" || (includeHybrid && includes("point_buy"))) {
    order.push("points-buy");
  }
  return order;
}

function usesAttributeGenerationStage(stageKey) {
  const safeStage = String(stageKey || "").trim().toLowerCase();
  if (!safeStage) {
    return false;
  }
  const safeType = String(state.attributeGenerationType || "").trim().toLowerCase();
  if (safeType === "standard_array") {
    return safeStage === "standard_array";
  }
  if (safeType === "dice") {
    return safeStage === "dice";
  }
  if (safeType === "point_buy") {
    return safeStage === "point_buy";
  }
  if (safeType !== "hybrid") {
    return false;
  }
  const stages = normalizeHybridStages(state.attributeGenerationStages);
  return !stages.length || stages.includes(safeStage);
}

async function ensureAttributeGenerationType() {
  if (state.attributeGenerationType) {
    return;
  }
  const data = await api("GET", `/api/drafts/${state.draftId}/attribute-generation`);
  state.attributeGenerationType = data.generationType || "";
  state.attributeGenerationStages = normalizeHybridStages(data.hybridStages);
  state.defaultAttributeMinScore = Number(data.defaultAttributeMinScore || 0);
  state.defaultAttributeMaxScore = Number(data.defaultAttributeMaxScore || 0);
}

function getNextAttributeGenerationStep(currentStep) {
  const order = getAttributeGenerationOrder(state.attributeGenerationType, state.attributeGenerationStages);
  if (!order.length) {
    return "hit-points";
  }
  const safeStep = String(currentStep || "");
  const currentIndex = order.indexOf(safeStep);
  if (currentIndex < 0) {
    return order[0];
  }
  return order[currentIndex + 1] || "hit-points";
}

function getPreviousAttributeGenerationStep(currentStep) {
  const order = getAttributeGenerationOrder(state.attributeGenerationType, state.attributeGenerationStages);
  if (!order.length) {
    return "attribute-generation";
  }
  const safeStep = String(currentStep || "");
  if (safeStep === "currency") {
    return "armor-class";
  }
  if (safeStep === "hit-points") {
    return order[order.length - 1] || "attribute-generation";
  }
  const currentIndex = order.indexOf(safeStep);
  if (currentIndex <= 0) {
    return "attribute-generation";
  }
  return order[currentIndex - 1];
}

function getNextAvailableAttributeGenerationStep(currentStep) {
  const fixedOrder = ["standard-array", "dice-rolling", "points-buy"];
  const order = getAttributeGenerationOrder(state.attributeGenerationType, state.attributeGenerationStages);
  const currentIndex = fixedOrder.indexOf(String(currentStep || ""));
  if (currentIndex < 0) {
    return getNextAttributeGenerationStep(currentStep);
  }
  for (let i = currentIndex + 1; i < fixedOrder.length; i += 1) {
    if (order.includes(fixedOrder[i])) {
      return fixedOrder[i];
    }
  }
  return "hit-points";
}

async function renderAttributeGeneration() {
  if (!ensureDraft()) {
    return;
  }
  setStep("attribute-generation");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const data = await api("GET", `/api/drafts/${state.draftId}/attribute-generation`);
    state.attributeGenerationType = data.generationType || "";
    state.attributeGenerationStages = normalizeHybridStages(data.hybridStages);
    state.defaultAttributeMinScore = Number(data.defaultAttributeMinScore || 0);
    state.defaultAttributeMaxScore = Number(data.defaultAttributeMaxScore || 0);
    state.applyAttributeModifiersToAllAttributes = Boolean(data.applyAttributeModifiersToAllAttributes);
    state.attributeModifiers = normalizeModifierEntries(data.attributeModifiers || []);

    const defaultScoreRangeChecked = usesDefaultAttributeScoreRange();
    let defaultModifiers = getStandardAttributeModifiers();
    let generationOptions = normalizeAttributeGenerationOptions(data.attributeGenerationOptions || []);

    view.innerHTML = `
      <section class="panel">
        <h1>${t("attrgen.title", "Attribute Generation")}</h1>
        <div class="edit-section">
          <h2 class="collection-editor-heading">${t("attrgen.options.title", "Player Options")}</h2>
          <p class="field-hint">${t(
            "attrgen.options.help",
            "Each option is a player-facing choice. Later steps can add to or spend from earlier scores, or generate a second result for the player to choose between."
          )}</p>
          <button class="btn ghost collection-add-button" id="addGenerationOption" type="button">${t("attrgen.options.add", "Add Option")}</button>
          <div class="list" id="generationOptionList"></div>
        </div>
        <div class="edit-section">
          <h2>${t("attrgen.score_limits.title", "Score Limits")}</h2>
          <label class="toggle">
            <input type="checkbox" id="defaultScoreRange" ${defaultScoreRangeChecked ? "checked" : ""}>
            ${t("attrgen.score_limits.same_all", "All attributes use the same score limits")}
          </label>
          <div class="grid two" id="defaultScoreRangeFields">
            <div class="field">
              <label for="defaultScoreMin">${t("attrgen.score_limits.min", "Minimum Score")}</label>
              <input type="number" id="defaultScoreMin" step="1" value="${state.defaultAttributeMinScore}">
            </div>
            <div class="field">
              <label for="defaultScoreMax">${t("attrgen.score_limits.max", "Maximum Score")}</label>
              <input type="number" id="defaultScoreMax" step="1" value="${state.defaultAttributeMaxScore}">
            </div>
          </div>
        </div>
        <div class="edit-section" id="defaultModifierSection">
          <h2 class="collection-editor-heading">${t("attrgen.modifiers.title", "Default Modifiers")}</h2>
          <label class="toggle">
            <input type="checkbox" id="defaultModifiersEnabled" ${state.applyAttributeModifiersToAllAttributes ? "checked" : ""}>
            ${t("attrgen.modifiers.same_all", "All attributes use the same modifier list")}
          </label>
          <button class="btn ghost collection-add-button" id="addDefaultModifier" type="button">${t("attributes.edit.modifier.add", "Add Modifier")}</button>
          <div class="list" id="defaultModifierList"></div>
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToAttributes" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn" id="generationContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    const updateDefaultScoreControls = () => {
      const enabled = document.getElementById("defaultScoreRange").checked;
      document.getElementById("defaultScoreMin").disabled = !enabled;
      document.getElementById("defaultScoreMax").disabled = !enabled;
      document.getElementById("defaultModifierSection").classList.toggle("hidden", !enabled);
      const modifiersEnabled = enabled && document.getElementById("defaultModifiersEnabled").checked;
      document.getElementById("addDefaultModifier").disabled = !modifiersEnabled;
    };

    const renderDefaultModifiers = () => {
      const list = document.getElementById("defaultModifierList");
      if (!defaultModifiers.length) {
        list.innerHTML = `<div class="list-item">${t("attributes.edit.modifiers.none", "No modifiers yet.")}</div>`;
        return;
      }
      defaultModifiers = normalizeModifierEntries(defaultModifiers);
      const configuredMaximum = Number.parseInt(document.getElementById("defaultScoreMax").value, 10);
      const maximumScore = Number.isFinite(configuredMaximum)
        ? configuredMaximum
        : Number(state.defaultAttributeMaxScore || 0);
      const formatModifierValue = (value) => {
        if (value > 0) {
          return `+${value}`;
        }
        if (value < 0) {
          return `−${Math.abs(value)}`;
        }
        return "0";
      };
      list.innerHTML = defaultModifiers
        .map(
          (entry, index) => {
            const nextEntry = defaultModifiers[index + 1];
            const derivedEnd = nextEntry ? nextEntry.score - 1 : maximumScore;
            const rangeLabel = derivedEnd > entry.score
              ? `${entry.score}–${derivedEnd}`
              : String(entry.score);
            return `
              <div class="list-item">
                <span>${rangeLabel}: ${formatModifierValue(entry.modifier)}</span>
                <div>
                  ${collectionEditAction("edit-default-modifier", index)}
                  ${collectionRemoveAction("remove-default-modifier", index)}
                </div>
              </div>
            `;
          }
        )
        .join("");
    };

    const getSelectedGenerationMethods = () => {
      const selected = [];
      generationOptions.forEach((option) => {
        option.steps.forEach((step) => {
          const methodType = normalizeAttributeGenerationMethodType(step.methodType);
          if (methodType && !selected.includes(methodType)) {
            selected.push(methodType);
          }
        });
      });
      return selected;
    };

    const buildMethodSelectOptions = (includeEmpty = false, excludeStandardArray = false) => {
      const options = includeEmpty
        ? [`<option value="">${t("common.none", "None")}</option>`]
        : [];
      ["standard_array", "dice", "point_buy"].forEach((methodType) => {
        if (excludeStandardArray && methodType === "standard_array") {
          return;
        }
        options.push(`<option value="${methodType}">${escapeHtml(formatCharGenType(methodType))}</option>`);
      });
      return options.join("");
    };

    const renderGenerationOptions = () => {
      state.attributeGenerationOptions = generationOptions.slice();
      const list = document.getElementById("generationOptionList");
      if (!generationOptions.length) {
        list.innerHTML = `<div class="list-item">${t("attrgen.options.none", "No player options yet.")}</div>`;
        return;
      }
      list.innerHTML = generationOptions
        .map((option, index) => {
          const steps = option.steps
            .map((step) => `${formatCharGenType(step.methodType)} (${formatAttributeGenerationApplicationMode(step.applicationMode)})`)
            .join(" -> ");
          return renderCollectionRow(
            `<div>
              <div>${escapeHtml(option.name || t("attrgen.options.default_name", "Option"))}</div>
              <div class="badge">${escapeHtml(steps)}</div>
            </div>`,
            [
              collectionEditAction("edit-generation-option", index),
              collectionRemoveAction("remove-generation-option", index),
            ]
          );
        })
        .join("");
    };

    const validateDefaultScoreRange = () => {
      const enabled = document.getElementById("defaultScoreRange").checked;
      if (!enabled) {
        return {
          range: { defaultAttributeMinScore: 0, defaultAttributeMaxScore: 0 },
          error: "",
        };
      }
      const minScore = Number.parseInt(document.getElementById("defaultScoreMin").value, 10);
      const maxScore = Number.parseInt(document.getElementById("defaultScoreMax").value, 10);
      if (!Number.isFinite(minScore) || !Number.isFinite(maxScore)) {
        return { range: null, error: t("attrgen.score_limits.invalid", "Enter a valid score range.") };
      }
      if (minScore > maxScore) {
        return { range: null, error: t("attrgen.score_limits.order.invalid", "Minimum score cannot exceed maximum.") };
      }
      if (minScore === 0 && maxScore === 0) {
        return { range: null, error: t("attrgen.score_limits.zero.invalid", "Use non-zero limits or clear the checkbox.") };
      }
      return {
        range: { defaultAttributeMinScore: minScore, defaultAttributeMaxScore: maxScore },
        error: "",
      };
    };

    const readDefaultScoreRange = (showError = true) => {
      const validation = validateDefaultScoreRange();
      if (validation.error && showError) {
        showToast(validation.error);
      }
      return validation.range;
    };

    let selectionSavePromise = Promise.resolve(true);
    const syncSelection = (showValidationError = true) => {
      const defaultScoreRange = readDefaultScoreRange(showValidationError);
      if (!defaultScoreRange) {
        return Promise.resolve(false);
      }
      const hasDefaultScoreRange = defaultScoreRange.defaultAttributeMinScore !== 0
        || defaultScoreRange.defaultAttributeMaxScore !== 0;
      const applyAttributeModifiers = document.getElementById("defaultModifiersEnabled").checked
        && hasDefaultScoreRange;
      const selected = getSelectedGenerationMethods();
      let generationType = "";
      let hybridStages = [];
      if (selected.length === 1) {
        generationType = selected[0];
      } else {
        generationType = "hybrid";
        hybridStages = selected;
      }
      const payload = {
          generationType,
          hybridStages,
          defaultAttributeMinScore: defaultScoreRange.defaultAttributeMinScore,
          defaultAttributeMaxScore: defaultScoreRange.defaultAttributeMaxScore,
          applyAttributeModifiersToAllAttributes: applyAttributeModifiers,
          attributeModifiers: defaultModifiers.map((entry) => ({ ...entry })),
          attributeGenerationOptions: generationOptions.map((option) => ({
            ...option,
            steps: option.steps.map((step) => ({ ...step })),
          })),
      };
      selectionSavePromise = selectionSavePromise.catch(() => false).then(async () => {
        try {
        await api("POST", `/api/drafts/${state.draftId}/attribute-generation`, payload);
        state.attributeGenerationType = generationType;
        state.attributeGenerationStages = normalizeHybridStages(hybridStages);
        state.attributeGenerationOptions = payload.attributeGenerationOptions;
        state.defaultAttributeMinScore = defaultScoreRange.defaultAttributeMinScore;
        state.defaultAttributeMaxScore = defaultScoreRange.defaultAttributeMaxScore;
        state.applyAttributeModifiersToAllAttributes = applyAttributeModifiers;
        state.attributeModifiers = normalizeModifierEntries(payload.attributeModifiers);
        markSaved(t("web.toast.generation_updated", "Generation updated"));
        return true;
        } catch (error) {
          showToast(error.message);
          return false;
        }
      });
      return selectionSavePromise;
    };

    updateDefaultScoreControls();
    renderDefaultModifiers();
    renderGenerationOptions();
    const openGenerationOptionEditor = (index = -1) => {
      const editing = Number.isInteger(index) && index >= 0 && index < generationOptions.length;
      const option = editing ? generationOptions[index] : { name: "", steps: [] };
      const firstStep = option.steps[0] || { methodType: "standard_array", applicationMode: "set" };
      const secondStep = option.steps[1] || { methodType: "", applicationMode: "add" };
      generationOptionModalTitle.textContent = editing
        ? t("attrgen.options.edit", "Edit Option")
        : t("attrgen.options.add", "Add Option");
      generationOptionModalNameLabel.textContent = t("attrgen.options.name", "Option Name");
      generationOptionModalStep1Label.textContent = t("attrgen.options.step1", "First Step");
      generationOptionModalStep2Label.textContent = t("attrgen.options.step2", "Second Step");
      generationOptionModalStep2ModeLabel.textContent = t("attrgen.options.step2_mode", "Second Step Applies");
      generationOptionModalCancel.textContent = t("common.cancel", "Cancel");
      generationOptionModalSave.textContent = editing ? t("common.save", "Save") : t("attrgen.options.add", "Add Option");
      generationOptionModalName.value = option.name || "";
      generationOptionModalStep1.innerHTML = buildMethodSelectOptions(false);
      generationOptionModalStep2.innerHTML = buildMethodSelectOptions(true, true);
      generationOptionModalStep1.value = firstStep.methodType || "standard_array";
      generationOptionModalStep2.value = secondStep.methodType || "";
      generationOptionModalStep2Mode.innerHTML = `
        <option value="add">${t("attrgen.options.mode.add", "Add to existing scores")}</option>
        <option value="spend">${t("attrgen.options.mode.spend", "Spend from existing scores")}</option>
        <option value="choose">${t("attrgen.options.mode.choose", "Choose between both results")}</option>
      `;
      generationOptionModalStep2Mode.value = secondStep.applicationMode || "add";
      const updateSecondStepModeVisibility = () => {
        generationOptionModalStep2ModeField.classList.toggle("hidden", !generationOptionModalStep2.value);
      };
      generationOptionModalStep2.onchange = updateSecondStepModeVisibility;
      updateSecondStepModeVisibility();
      generationOptionModalSave.disabled = false;
      generationOptionModal.classList.remove("hidden");
      window.requestAnimationFrame(() => generationOptionModalName.focus());

      generationOptionModalCancel.onclick = () => {
        generationOptionModal.classList.add("hidden");
      };
      generationOptionModalSave.onclick = async () => {
        const name = String(generationOptionModalName.value || "").trim();
        const firstStep = normalizeAttributeGenerationMethodType(generationOptionModalStep1.value);
        const secondStep = normalizeAttributeGenerationMethodType(generationOptionModalStep2.value);
        const secondMode = normalizeAttributeGenerationApplicationMode(generationOptionModalStep2Mode.value);
        if (!firstStep) {
          showToast(t("attrgen.options.step_required", "Choose at least one option step."));
          return;
        }
        if (secondStep === "standard_array") {
          showToast(t(
            "attrgen.options.standard_first_only",
            "Standard Array/Base Scores can only be the first step."
          ));
          return;
        }
        const optionSteps = [{ methodType: firstStep, applicationMode: "set" }];
        if (secondStep) {
          optionSteps.push({ methodType: secondStep, applicationMode: secondMode });
        }
        const updatedOption = {
          id: editing ? option.id : `option-${Date.now()}`,
          name: name || t("attrgen.options.default_name", "Option"),
          steps: optionSteps,
        };
        replaceOrAppendCollectionItem(generationOptions, index, updatedOption);
        renderGenerationOptions();
        generationOptionModal.classList.add("hidden");
        await syncSelection();
      };
    };
    document.getElementById("addGenerationOption").addEventListener("click", () => openGenerationOptionEditor());
    document.getElementById("generationOptionList").addEventListener("click", async (event) => {
      const button = event.target.closest("button[data-edit-generation-option], button[data-remove-generation-option]");
      if (!button) {
        return;
      }
      const index = Number(button.dataset.editGenerationOption ?? button.dataset.removeGenerationOption);
      if (!Number.isInteger(index) || index < 0 || index >= generationOptions.length) {
        return;
      }
      if (button.hasAttribute("data-edit-generation-option")) {
        openGenerationOptionEditor(index);
        return;
      }
      generationOptions.splice(index, 1);
      renderGenerationOptions();
      await syncSelection();
    });
    ["defaultScoreMin", "defaultScoreMax"].forEach((id) => {
      document.getElementById(id).addEventListener("change", async () => {
        if (await syncSelection(false)) {
          renderDefaultModifiers();
        }
      });
    });
    document.getElementById("defaultScoreRange").addEventListener("change", async () => {
      if (!document.getElementById("defaultScoreRange").checked) {
        document.getElementById("defaultModifiersEnabled").checked = false;
      }
      updateDefaultScoreControls();
      await syncSelection(false);
    });
    document.getElementById("defaultModifiersEnabled").addEventListener("change", async () => {
      updateDefaultScoreControls();
      await syncSelection();
    });
    const openDefaultModifierEditor = (index = -1) => {
      const isEditing = Number.isInteger(index) && index >= 0 && index < defaultModifiers.length;
      const entry = isEditing ? defaultModifiers[index] : { score: "", modifier: "" };
      defaultModifierModalTitle.textContent = isEditing
        ? t("attrgen.modifiers.edit", "Edit Modifier")
        : t("attributes.edit.modifier.add", "Add Modifier");
      defaultModifierModalScoreLabel.textContent = t("attrgen.modifiers.starting_score", "Starting Score");
      defaultModifierModalValueLabel.textContent = t("attributes.edit.modifier.value", "Modifier");
      defaultModifierModalCancel.textContent = t("common.cancel", "Cancel");
      defaultModifierModalSave.textContent = isEditing
        ? t("attrgen.modifiers.save", "Save Modifier")
        : t("attributes.edit.modifier.add", "Add Modifier");
      const configuredMinimum = Number.parseInt(document.getElementById("defaultScoreMin").value, 10);
      const configuredMaximum = Number.parseInt(document.getElementById("defaultScoreMax").value, 10);
      defaultModifierModalScore.min = Number.isFinite(configuredMinimum) ? String(configuredMinimum) : "";
      defaultModifierModalScore.max = Number.isFinite(configuredMaximum) ? String(configuredMaximum) : "";
      defaultModifierModalScore.value = String(entry.score);
      defaultModifierModalValue.value = String(entry.modifier);
      defaultModifierModalSave.disabled = false;
      defaultModifierModal.classList.remove("hidden");
      window.requestAnimationFrame(() => defaultModifierModalScore.focus());

      defaultModifierModalCancel.onclick = () => {
        defaultModifierModal.classList.add("hidden");
      };
      defaultModifierModalSave.onclick = async () => {
        const score = Number(defaultModifierModalScore.value || 0);
        const modifier = Number(defaultModifierModalValue.value || 0);
        if (!Number.isFinite(score) || !Number.isFinite(modifier)) {
          showToast(t("attrgen.modifiers.invalid", "Enter a valid modifier."));
          return;
        }
        const scoreRange = readDefaultScoreRange();
        if (!scoreRange) {
          return;
        }
        const minimumScore = scoreRange.defaultAttributeMinScore;
        const maximumScore = scoreRange.defaultAttributeMaxScore;
        if (score < minimumScore || score > maximumScore) {
          showToast(
            t("attrgen.modifiers.score_range", "Starting Score must be between {min} and {max}.")
              .replace("{min}", String(minimumScore))
              .replace("{max}", String(maximumScore))
          );
          return;
        }
        const previousModifiers = defaultModifiers.map((item) => ({ ...item }));
        if (isEditing) {
          defaultModifiers[index] = { score, modifier };
        } else {
          defaultModifiers.push({ score, modifier });
        }
        defaultModifiers = normalizeModifierEntries(defaultModifiers);
        defaultModifierModalSave.disabled = true;
        if (await syncSelection()) {
          renderDefaultModifiers();
          defaultModifierModal.classList.add("hidden");
        } else {
          defaultModifiers = previousModifiers;
          defaultModifierModalSave.disabled = false;
        }
      };
    };
    document.getElementById("addDefaultModifier").addEventListener("click", () => {
      openDefaultModifierEditor();
    });
    document.getElementById("defaultModifierList").addEventListener("click", async (event) => {
      const button = event.target.closest("button[data-edit-default-modifier], button[data-remove-default-modifier]");
      if (!button) {
        return;
      }
      if (button.hasAttribute("data-edit-default-modifier")) {
        const index = Number(button.dataset.editDefaultModifier);
        if (Number.isInteger(index) && index >= 0 && index < defaultModifiers.length) {
          openDefaultModifierEditor(index);
        }
        return;
      }
      const confirmed = await showConfirm(
        t("common.remove.confirm", "Remove selected item?"),
        t("common.remove", "Remove")
      );
      if (!confirmed) {
        return;
      }
      const index = Number(button.dataset.removeDefaultModifier);
      if (!Number.isInteger(index) || index < 0 || index >= defaultModifiers.length) {
        return;
      }
      defaultModifiers.splice(index, 1);
      renderDefaultModifiers();
      await syncSelection();
    });
    document.getElementById("backToAttributes").addEventListener("click", navigateBackInApp);
    document.getElementById("generationContinue").addEventListener("click", async () => {
      const validation = validateDefaultScoreRange();
      if (validation.error) {
        openInformationPopup(
          t("attrgen.score_limits.invalid.title", "Check Score Limits"),
          validation.error
        );
        return;
      }
      const saved = await syncSelection();
      if (saved) {
        navigateToStep(getNextAttributeGenerationStep("attribute-generation"));
      }
    });
  } catch (error) {
    showToast(error.message);
  }
}

async function renderStandardArray() {
  if (!ensureDraft()) {
    return;
  }
  setStep("standard-array");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    await ensureAttributeGenerationType();
    if (!usesAttributeGenerationStage("standard_array")) {
      view.innerHTML = `
        <section class="panel">
          <h1>${t("attrgen.standard.title", "Standard Array/Base Scores")}</h1>
          <p class="field-hint">${t(
            "attrgen.standard.disabled",
            "Standard Array/Base Scores is not selected in Attribute Generation, so this screen is locked to prevent unused array data from being edited."
          )}</p>
          <div class="actions-row">
            <div class="left">
              <button class="btn ghost" id="backToGeneration" type="button">${t("setup.back", "Back")}</button>
            </div>
            <div class="right">
              <button class="btn" id="standardContinue" type="button">${t("common.continue", "Continue")}</button>
            </div>
          </div>
        </section>
      `;
      document.getElementById("backToGeneration").addEventListener("click", navigateBackInApp);
      document.getElementById("standardContinue").addEventListener("click", () => {
        navigateToStep(getNextAvailableAttributeGenerationStep("standard-array"));
      });
      return;
    }
    const data = await api("GET", `/api/drafts/${state.draftId}/standard-array`);
    const attributes = data.attributes || [];
    const assignmentMode = String(data.standardArrayAssignmentMode || "assigned").toLowerCase() === "open"
      ? "open"
      : "assigned";
    const isOpenArray = assignmentMode === "open";
    const standardUsesSharedScore = Boolean(data.allAttributesUseSameStandardScore);
    const standardSharedScore = Number(data.standardSharedScore || 0);
    const eliteUsesSharedScore = Boolean(data.allAttributesUseSameEliteScore);
    const eliteSharedScore = Number(data.eliteSharedScore || 0);
    const attributeOptions = attributes
      .map((attr) => `<option value="${attr.id}">${escapeHtml(attr.displayName)}</option>`)
      .join("");
    const assignmentHelp = isOpenArray
      ? t(
          "attrgen.arrays.mode.open.help",
          "Open arrays store values only. Players assign those values to whichever attributes they choose."
        )
      : t(
          "attrgen.arrays.mode.assigned.help",
          "Assigned arrays pair each value with a specific attribute."
        );

    const formatArrayEntry = (entry) => {
      const text = String(entry || "").trim();
      const idx = text.indexOf("=");
      if (idx > 0 && idx < text.length - 1) {
        return `${text.slice(0, idx).trim()}: ${text.slice(idx + 1).trim()}`;
      }
      return text;
    };

    const isAssignedArrayEntry = (entry) => {
      const text = String(entry || "").trim();
      const separator = text.indexOf("=");
      return separator > 0 && separator < text.length - 1;
    };

    const buildAssignedArrayValues = (items) => {
      const values = {};
      (items || []).forEach((entry) => {
        const text = String(entry || "").trim();
        const separator = text.indexOf("=");
        if (separator <= 0 || separator >= text.length - 1) {
          return;
        }
        const name = text.slice(0, separator).trim().toLowerCase();
        const value = Number(text.slice(separator + 1).trim());
        if (name && Number.isFinite(value)) {
          values[name] = Math.trunc(value);
        }
      });
      return values;
    };
    const standardAssignedValues = buildAssignedArrayValues(data.standardArray);
    const eliteAssignedValues = buildAssignedArrayValues(data.eliteArray);
    const assignedArrayIsComplete = (items, values, usesSharedScore) => usesSharedScore
      || !(items || []).length
      || attributes.every((attribute) => Object.prototype.hasOwnProperty.call(
        values,
        String(attribute.name || "").trim().toLowerCase()
      ));
    const incompleteAssignedTargets = isOpenArray
      ? []
      : [
          !assignedArrayIsComplete(data.standardArray, standardAssignedValues, standardUsesSharedScore) ? "standard" : "",
          !assignedArrayIsComplete(data.eliteArray, eliteAssignedValues, eliteUsesSharedScore) ? "elite" : "",
        ].filter(Boolean);

    const renderList = (items, target) =>
      (items || [])
        .map(
          (entry) => {
            const needsAssignment = !isOpenArray && !isAssignedArrayEntry(entry);
            const content = `<span class="${needsAssignment ? "array-entry-warning" : ""}">${escapeHtml(
            needsAssignment
              ? `${t("attrgen.arrays.assignment.required", "Needs Attribute assignment")}: ${formatArrayEntry(entry)}`
              : formatArrayEntry(entry)
          )}</span>`;
            return renderCollectionRow(
              content,
              isOpenArray
                ? [
                    collectionEditAction(`edit-${target}`, entry),
                    collectionRemoveAction(target, entry),
                  ]
                : []
            );
          }
        )
        .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${t("attrgen.standard.title", "Standard Array/Base Scores")}</h1>
        <div class="field">
          <label>${t("attrgen.arrays.mode", "Array Assignment")}</label>
          <select id="standardAssignmentMode">
            <option value="assigned" ${assignmentMode === "assigned" ? "selected" : ""}>${t(
              "attrgen.arrays.mode.assigned",
              "Auto Assigned"
            )}</option>
            <option value="open" ${assignmentMode === "open" ? "selected" : ""}>${t(
              "attrgen.arrays.mode.open",
              "Player Assigned"
            )}</option>
          </select>
          <p class="field-hint">${escapeHtml(assignmentHelp)}</p>
        </div>
        ${!isOpenArray && incompleteAssignedTargets.length ? `
          <div class="array-assignment-notice" role="status">
            ${escapeHtml(t(
              "attrgen.arrays.assignment.complete_notice",
              "Each non-normalized Auto Assigned array must include one score for every Attribute. Use Set Scores to complete it."
            ))}
          </div>
        ` : ""}
        <h2 class="collection-editor-heading">${t("attrgen.arrays.section", "Standard Array/Base Scores")}</h2>
        ${!isOpenArray ? `
          <div class="array-normalization-controls">
            <label class="checkbox-label" for="standardUsesSharedScore">
              <input type="checkbox" id="standardUsesSharedScore" ${standardUsesSharedScore ? "checked" : ""}>
              <span>${t("attrgen.arrays.standard.shared", "All Attributes use the same base score")}</span>
            </label>
            <div class="field ${standardUsesSharedScore ? "" : "hidden"}" id="standardSharedScoreField">
              <label for="standardSharedScore">${t("attrgen.arrays.standard.shared_value", "Shared base score")}</label>
              <input type="number" id="standardSharedScore" value="${standardSharedScore}">
            </div>
          </div>
        ` : ""}
        ${isOpenArray || !standardUsesSharedScore ? `
          <button class="btn collection-add-button" id="addStandard" type="button">${isOpenArray
            ? t("attrgen.arrays.add", "Add Value")
            : t("attrgen.arrays.set_scores", "Set Scores")}</button>
          <div class="list" id="standardList">${renderList(data.standardArray, "standard") || `<div class="list-item">${t("web.standard_array.none", "No entries yet.")}</div>`}</div>
        ` : `
          <p class="field-hint">${escapeHtml(t(
            "attrgen.arrays.standard.shared_help",
            "This score is assigned to every Attribute, including Attributes added later."
          ))}</p>
        `}

        <h2 class="collection-editor-heading">${t("attrgen.arrays.elite.section", "Elite Arrays")}</h2>
        ${!isOpenArray ? `
          <div class="array-normalization-controls">
            <label class="checkbox-label" for="eliteUsesSharedScore">
              <input type="checkbox" id="eliteUsesSharedScore" ${eliteUsesSharedScore ? "checked" : ""}>
              <span>${t("attrgen.arrays.elite.shared", "All Attributes use the same elite score")}</span>
            </label>
            <div class="field ${eliteUsesSharedScore ? "" : "hidden"}" id="eliteSharedScoreField">
              <label for="eliteSharedScore">${t("attrgen.arrays.elite.shared_value", "Shared elite score")}</label>
              <input type="number" id="eliteSharedScore" value="${eliteSharedScore}">
            </div>
          </div>
        ` : ""}
        ${isOpenArray || !eliteUsesSharedScore ? `
          <button class="btn collection-add-button" id="addElite" type="button">${isOpenArray
            ? t("attrgen.arrays.add", "Add Value")
            : t("attrgen.arrays.set_scores", "Set Scores")}</button>
          <div class="list" id="eliteList">${renderList(data.eliteArray, "elite") || `<div class="list-item">${t("web.standard_array.none", "No entries yet.")}</div>`}</div>
        ` : `
          <p class="field-hint">${escapeHtml(t(
            "attrgen.arrays.elite.shared_help",
            "This score is assigned to every Attribute, including Attributes added later."
          ))}</p>
        `}

        <div class="field">
          <label>${t("attrgen.default_array", "Default Array Type")}</label>
          <select id="defaultArray">
            <option value="standard" ${data.defaultArrayType === "standard" ? "selected" : ""}>${t("attrgen.default_array.standard", "Standard")}</option>
            <option value="elite" ${data.defaultArrayType === "elite" ? "selected" : ""}>${t("attrgen.default_array.elite", "Elite")}</option>
          </select>
        </div>

        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToGeneration" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn" id="standardContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    const openArrayValueEditor = (target, originalEntry = "") => {
      const isElite = target === "elite";
      const editing = String(originalEntry || "") !== "";
      const sectionTitle = isElite
        ? t("attrgen.arrays.elite.section", "Elite Arrays")
        : t("attrgen.arrays.section", "Standard Array/Base Scores");
      arrayValueModalTitle.textContent = `${editing ? t("attrgen.arrays.edit", "Edit Value") : t("attrgen.arrays.add", "Add Value")} - ${sectionTitle}`;
      arrayValueModalAttributeLabel.textContent = t("attrgen.arrays.attribute", "Attribute");
      arrayValueModalValueLabel.textContent = t("attrgen.arrays.value", "Array Value");
      arrayValueModalCancel.textContent = t("common.cancel", "Cancel");
      arrayValueModalSave.textContent = editing ? t("common.save", "Save") : t("attrgen.arrays.add", "Add Value");
      arrayValueModalCard.classList.remove("wide");
      arrayValueModalSingleFields.classList.remove("hidden");
      arrayValueModalEntries.classList.add("hidden");
      arrayValueModalAttributeField.classList.add("hidden");
      arrayValueModalAttribute.innerHTML = attributeOptions;
      arrayValueModalValue.value = editing ? String(Number(originalEntry)) : "0";
      arrayValueModalValue.disabled = false;
      arrayValueModalSave.disabled = false;
      arrayValueModal.classList.remove("hidden");
      window.requestAnimationFrame(() => arrayValueModalValue.focus());

      arrayValueModalCancel.onclick = () => {
        arrayValueModal.classList.add("hidden");
      };
      arrayValueModalSave.onclick = async () => {
        const value = Number(arrayValueModalValue.value);
        arrayValueModalSave.disabled = true;
        try {
          await api("POST", `/api/drafts/${state.draftId}/standard-array/${target}`, {
            value,
            originalEntry: editing ? String(originalEntry) : "",
          });
          markSaved(t(
            isElite ? "web.toast.elite_array_updated" : "web.toast.standard_array_updated",
            isElite ? "Elite array updated" : "Standard array updated"
          ));
          arrayValueModal.classList.add("hidden");
          renderStandardArray();
        } catch (error) {
          showToast(error.message);
          arrayValueModalSave.disabled = false;
        }
      };
    };

    const openAssignedArrayEditor = (target) => {
      if (!attributes.length) {
        openInformationPopup(
          t("attrgen.arrays.assignment.attributes_required.title", "Attributes Required"),
          t(
            "attrgen.arrays.assignment.attributes_required",
            "Create at least one Attribute before assigning array values."
          )
        );
        return;
      }
      const isElite = target === "elite";
      const sectionTitle = isElite
        ? t("attrgen.arrays.elite.section", "Elite Arrays")
        : t("attrgen.arrays.section", "Standard Array/Base Scores");
      const savedValues = isElite ? eliteAssignedValues : standardAssignedValues;
      arrayValueModalTitle.textContent = `${t("attrgen.arrays.set_scores", "Set Scores")} - ${sectionTitle}`;
      arrayValueModalCancel.textContent = t("common.cancel", "Cancel");
      arrayValueModalSave.textContent = t("attrgen.arrays.save_scores", "Save Scores");
      arrayValueModalCard.classList.add("wide");
      arrayValueModalSingleFields.classList.add("hidden");
      arrayValueModalEntries.innerHTML = attributes.map((attribute, index) => {
        const attributeName = String(attribute.name || "").trim().toLowerCase();
        const savedValue = Object.prototype.hasOwnProperty.call(savedValues, attributeName)
          ? savedValues[attributeName]
          : 0;
        const inputId = `arrayScore-${target}-${index}`;
        return `
          <div class="array-score-row">
            <label for="${inputId}">${escapeHtml(attribute.displayName || attribute.name || "")}</label>
            <input
              type="number"
              id="${inputId}"
              data-array-score-attribute="${escapeHtml(attribute.id)}"
              value="${savedValue}"
            >
          </div>
        `;
      }).join("");
      arrayValueModalEntries.classList.remove("hidden");
      arrayValueModalSave.disabled = false;
      arrayValueModal.classList.remove("hidden");
      window.requestAnimationFrame(() => {
        const firstInput = arrayValueModalEntries.querySelector("[data-array-score-attribute]");
        if (firstInput) {
          firstInput.focus();
        }
      });

      arrayValueModalCancel.onclick = () => {
        arrayValueModal.classList.add("hidden");
      };
      arrayValueModalSave.onclick = async () => {
        const attributeValues = [...arrayValueModalEntries.querySelectorAll("[data-array-score-attribute]")]
          .map((input) => ({
            attributeId: input.dataset.arrayScoreAttribute,
            value: String(input.value || "").trim() ? Number(input.value) : Number.NaN,
          }));
        if (attributeValues.some((entry) => !Number.isFinite(entry.value))) {
          showToast(t("attrgen.arrays.score_required", "Enter a score for every Attribute."));
          return;
        }
        arrayValueModalSave.disabled = true;
        try {
          await api("POST", `/api/drafts/${state.draftId}/standard-array/${target}`, { attributeValues });
          markSaved(t(
            isElite ? "web.toast.elite_array_updated" : "web.toast.standard_array_updated",
            isElite ? "Elite array updated" : "Standard array updated"
          ));
          arrayValueModal.classList.add("hidden");
          renderStandardArray();
        } catch (error) {
          showToast(error.message);
          arrayValueModalSave.disabled = false;
        }
      };
    };

    const addStandard = document.getElementById("addStandard");
    if (addStandard) {
      addStandard.addEventListener("click", () => {
        if (isOpenArray) {
          openArrayValueEditor("standard");
        } else {
          openAssignedArrayEditor("standard");
        }
      });
    }

    const addElite = document.getElementById("addElite");
    if (addElite) {
      addElite.addEventListener("click", () => {
        if (isOpenArray) {
          openArrayValueEditor("elite");
        } else {
          openAssignedArrayEditor("elite");
        }
      });
    }

    document.querySelectorAll("[data-edit-standard]").forEach((button) => {
      button.addEventListener("click", () => openArrayValueEditor("standard", button.dataset.editStandard));
    });

    document.querySelectorAll("[data-edit-elite]").forEach((button) => {
      button.addEventListener("click", () => openArrayValueEditor("elite", button.dataset.editElite));
    });

    document.querySelectorAll("[data-standard]").forEach((button) => {
      button.addEventListener("click", async () => {
        const entry = button.dataset.standard;
        const confirmed = await showConfirm(
          t("web.confirm.remove.standard_array", "Remove this standard array entry?"),
          t("common.remove", "Remove")
        );
        if (!confirmed) {
          return;
        }
        try {
          await api("DELETE", `/api/drafts/${state.draftId}/standard-array/standard`, { entry });
          markSaved(t("web.toast.standard_array_updated", "Standard array updated"));
          renderStandardArray();
        } catch (error) {
          showToast(error.message);
        }
      });
    });

    document.querySelectorAll("[data-elite]").forEach((button) => {
      button.addEventListener("click", async () => {
        const entry = button.dataset.elite;
        const confirmed = await showConfirm(
          t("web.confirm.remove.elite_array", "Remove this elite array entry?"),
          t("common.remove", "Remove")
        );
        if (!confirmed) {
          return;
        }
        try {
          await api("DELETE", `/api/drafts/${state.draftId}/standard-array/elite`, { entry });
          markSaved(t("web.toast.elite_array_updated", "Elite array updated"));
          renderStandardArray();
        } catch (error) {
          showToast(error.message);
        }
      });
    });

    document.getElementById("standardAssignmentMode").addEventListener("change", async (event) => {
      try {
        await api("POST", `/api/drafts/${state.draftId}/standard-array/default`, {
          standardArrayAssignmentMode: event.target.value,
        });
        markSaved(t("web.toast.standard_array_updated", "Standard array updated"));
        renderStandardArray();
      } catch (error) {
        showToast(error.message);
      }
    });

    const wireSharedScoreControl = (config) => {
      const checkbox = document.getElementById(config.checkboxId);
      const input = document.getElementById(config.inputId);
      if (!checkbox || !input) {
        return;
      }
      checkbox.addEventListener("change", async () => {
        checkbox.disabled = true;
        try {
          await api("POST", `/api/drafts/${state.draftId}/standard-array/default`, {
            [config.modeKey]: checkbox.checked,
          });
          markSaved(t(config.toastKey, config.toastFallback));
          renderStandardArray();
        } catch (error) {
          showToast(error.message);
          checkbox.disabled = false;
        }
      });
      input.addEventListener("change", async () => {
        const score = String(input.value || "").trim() ? Number(input.value) : Number.NaN;
        if (!Number.isFinite(score)) {
          showToast(t("attrgen.arrays.score_required", "Enter a score for every Attribute."));
          return;
        }
        input.disabled = true;
        try {
          await api("POST", `/api/drafts/${state.draftId}/standard-array/default`, {
            [config.scoreKey]: score,
          });
          markSaved(t(config.toastKey, config.toastFallback));
        } catch (error) {
          showToast(error.message);
        } finally {
          input.disabled = false;
        }
      });
    };

    wireSharedScoreControl({
      checkboxId: "standardUsesSharedScore",
      inputId: "standardSharedScore",
      modeKey: "allAttributesUseSameStandardScore",
      scoreKey: "standardSharedScore",
      toastKey: "web.toast.standard_array_updated",
      toastFallback: "Standard array updated",
    });
    wireSharedScoreControl({
      checkboxId: "eliteUsesSharedScore",
      inputId: "eliteSharedScore",
      modeKey: "allAttributesUseSameEliteScore",
      scoreKey: "eliteSharedScore",
      toastKey: "web.toast.elite_array_updated",
      toastFallback: "Elite array updated",
    });

    document.getElementById("defaultArray").addEventListener("change", async (event) => {
      try {
        await api("POST", `/api/drafts/${state.draftId}/standard-array/default`, {
          defaultArrayType: event.target.value,
        });
        markSaved(t("web.toast.default_array_set", "Default array set"));
      } catch (error) {
        showToast(error.message);
      }
    });

    const confirmUnassignedNavigation = async (navigate) => {
      if (!isOpenArray && incompleteAssignedTargets.length) {
        const leavePage = await showConfirm(
          t(
            "attrgen.arrays.assignment.leave.message",
            "Some Auto Assigned arrays do not yet include a score for every Attribute. You can leave them incomplete and return later."
          ),
          t("common.okay", "Okay"),
          t("attrgen.arrays.assignment.continue_editing", "Continue Editing"),
          t("attrgen.arrays.assignment.leave.title", "Unassigned Array Values"),
          false
        );
        if (!leavePage) {
          return;
        }
      }
      navigate();
    };
    document.getElementById("backToGeneration").addEventListener("click", () => {
      confirmUnassignedNavigation(navigateBackInApp);
    });
    document.getElementById("standardContinue").addEventListener("click", () => {
      confirmUnassignedNavigation(() => {
        navigateToStep(getNextAttributeGenerationStep("standard-array"));
      });
    });
  } catch (error) {
    showToast(error.message);
  }
}

async function renderDiceRolling() {
  if (!ensureDraft()) {
    return;
  }
  setStep("dice-rolling");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    await ensureAttributeGenerationType();
    if (!usesAttributeGenerationStage("dice")) {
      view.innerHTML = `
        <section class="panel">
          <h1>${t("attrgen.dice.title", "Dice Rolling")}</h1>
          <p class="field-hint">${t(
            "attrgen.dice.disabled",
            "Dice Rolling is not selected in Attribute Generation, so this screen is locked to prevent unused dice data from being edited."
          )}</p>
          <div class="actions-row">
            <div class="left">
              <button class="btn ghost" id="backToStandard" type="button">${t("setup.back", "Back")}</button>
            </div>
            <div class="right">
              <button class="btn" id="diceContinue" type="button">${t("common.continue", "Continue")}</button>
            </div>
          </div>
        </section>
      `;
      document.getElementById("backToStandard").addEventListener("click", navigateBackInApp);
      document.getElementById("diceContinue").addEventListener("click", () => {
        navigateToStep(getNextAvailableAttributeGenerationStep("dice-rolling"));
      });
      return;
    }
    const data = await api("GET", `/api/drafts/${state.draftId}/dice-rolling`);
    const minimumAttributeScore = Math.trunc(Number(data.defaultAttributeMinScore || 0));
    const maximumAttributeScore = Math.trunc(Number(data.defaultAttributeMaxScore || 0));
    const hasDefaultAttributeScoreRange = minimumAttributeScore !== 0 || maximumAttributeScore !== 0;
    const substitutionRangeMessage = hasDefaultAttributeScoreRange
      ? t(
          "attrgen.dice.substitution.range",
          "Must be between {min} and {max}, matching the Attribute Score Limits."
        )
          .replace("{min}", String(minimumAttributeScore))
          .replace("{max}", String(maximumAttributeScore))
      : "";
    const substitutionRangeAttributes = hasDefaultAttributeScoreRange
      ? ` min="${minimumAttributeScore}" max="${maximumAttributeScore}"`
      : "";
    const diceUsed = (data.diceUsed || [])
      .map((value) => Number(value || 0))
      .filter((value) => value > 0)
      .sort((left, right) => left - right);
    const defaultSides = diceUsed.includes(6) ? 6 : diceUsed[0] || 0;
    const diceSideOptions = [`<option value="">${t("common.die.select", "Select Die")}</option>`]
      .concat(
        diceUsed.map((sides) => {
          const selected = sides === defaultSides ? " selected" : "";
          return `<option value="${sides}"${selected}>d${sides}</option>`;
        })
      )
      .join("");
    const diceTerms = Array.isArray(data.terms) ? data.terms : [];
    const terms = diceTerms
      .map(
        (term, index) => renderCollectionRow(
          `<span>${escapeHtml(term.notation)}</span>`,
          [
            collectionEditAction("edit-term", index),
            collectionRemoveAction("remove-term", index),
          ]
        )
      )
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${t("attrgen.dice.title", "Dice Rolling")}</h1>
        <div class="grid two">
          <div class="field">
            <label for="setCount">${t("attrgen.sets.count", "Number of Sets")}</label>
            <input type="number" id="setCount" min="0" step="1" value="${Math.max(0, Number(data.numberOfSets || 0))}">
          </div>
          <div class="field">
            <label for="diceAssignmentMethod">${t("attrgen.dice.assignment.method", "Roll Assignment")}</label>
            <select id="diceAssignmentMethod">
              <option value="player" ${data.assignInOrder ? "" : "selected"}>${t(
                "attrgen.dice.assignment.player",
                "Player assigns rolls"
              )}</option>
              <option value="in_order" ${data.assignInOrder ? "selected" : ""}>${t(
                "attrgen.dice.assignment.in_order",
                "Assign in Attribute order"
              )}</option>
            </select>
            <div class="dice-assignment-order-controls ${data.assignInOrder ? "" : "hidden"}" id="diceAssignmentOrderControls">
              <button class="btn ghost small" id="editDiceAttributeOrder" type="button" ${(data.attributes || []).length ? "" : "disabled"}>${t(
                "attrgen.dice.assignment.order.button",
                "Set Attribute Order"
              )}</button>
              ${
                (data.attributes || []).length
                  ? `<p class="field-hint">${t(
                      "attrgen.dice.assignment.order.hint",
                      "This order controls which Attribute receives each roll."
                    )}</p>`
                  : `<p class="field-hint">${t(
                      "attrgen.dice.assignment.order.empty",
                      "Add Attributes before setting their assignment order."
                    )}</p>`
              }
            </div>
          </div>
        </div>

        <h2>${t("attrgen.dice.substitution.section", "Dice Substitution")}</h2>
        <div class="grid two">
          <div class="field">
            <label for="allowDiceSubstitution">${t("attrgen.dice.substitution.enable", "Allow substitution")}</label>
            <input type="checkbox" id="allowDiceSubstitution" ${data.allowDiceSubstitution ? "checked" : ""}>
          </div>
          <div class="field">
            <label for="diceSubstitutionValue">${t("attrgen.dice.substitution.value", "Substitution value")}</label>
            <input type="number" id="diceSubstitutionValue"${substitutionRangeAttributes} step="1" value="${escapeHtml(
              String(Math.trunc(Number(data.diceSubstitutionValue ?? 14)))
            )}">
            ${hasDefaultAttributeScoreRange ? `<p class="field-hint">${escapeHtml(substitutionRangeMessage)}</p>` : ""}
          </div>
          <div class="field">
            <label for="maxDiceSubstitutions">${t("attrgen.dice.substitution.count", "Max substitutions")}</label>
            <input type="number" id="maxDiceSubstitutions" min="0" step="1" value="${escapeHtml(
              String(Math.max(0, Number(data.maxDiceSubstitutions ?? 1)))
            )}">
          </div>
        </div>

        <h2 class="collection-editor-heading">${t("attrgen.dice.section", "Dice Terms")}</h2>
        <button class="btn collection-add-button" id="addTerm" type="button">${t("attrgen.dice.add", "Add Dice Term")}</button>
        <div class="list" id="termList">${terms || `<div class="list-item">${t("web.dice.terms.none", "No terms yet.")}</div>`}</div>

        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToStandard" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn" id="diceContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    document.getElementById("setCount").addEventListener("change", async (event) => {
      const numberOfSets = Math.max(0, Math.trunc(Number(event.target.value || 0)));
      event.target.value = String(numberOfSets);
      try {
        await api("POST", `/api/drafts/${state.draftId}/dice-rolling/sets`, { numberOfSets });
        markSaved(t("web.toast.sets_updated", "Sets updated"));
      } catch (error) {
        showToast(error.message);
      }
    });

    document.getElementById("diceAssignmentMethod").addEventListener("change", async (event) => {
      const assignInOrder = event.target.value === "in_order";
      const orderControls = document.getElementById("diceAssignmentOrderControls");
      orderControls.classList.toggle("hidden", !assignInOrder);
      try {
        await api("POST", `/api/drafts/${state.draftId}/dice-rolling/assignment`, { assignInOrder });
        markSaved(t("web.toast.dice_assignment_updated", "Roll assignment updated"));
      } catch (error) {
        showToast(error.message);
        event.target.value = assignInOrder ? "player" : "in_order";
        orderControls.classList.toggle("hidden", assignInOrder);
      }
    });

    const attributes = Array.isArray(data.attributes) ? data.attributes : [];
    const attributeById = new Map(attributes.map((attribute) => [String(attribute.id || ""), attribute]));
    const storedOrder = Array.isArray(data.attributeOrder) ? data.attributeOrder.map(String) : [];
    const attributeOrder = storedOrder.filter((attributeId) => attributeById.has(attributeId));
    attributes.forEach((attribute) => {
      const attributeId = String(attribute.id || "");
      if (attributeId && !attributeOrder.includes(attributeId)) {
        attributeOrder.push(attributeId);
      }
    });
    const editAttributeOrder = document.getElementById("editDiceAttributeOrder");
    if (editAttributeOrder) {
      editAttributeOrder.addEventListener("click", () => {
        let workingOrder = [...attributeOrder];
        attributeOrderModalTitle.textContent = t(
          "attrgen.dice.assignment.order.title",
          "Attribute Assignment Order"
        );
        attributeOrderModalDescription.textContent = t(
          "attrgen.dice.assignment.order.description",
          "Choose which Attribute receives each roll, from first to last."
        );
        attributeOrderModalCancel.textContent = t("common.cancel", "Cancel");
        attributeOrderModalSave.textContent = t("attrgen.dice.assignment.order.save", "Save Order");

        const renderOrderRows = () => {
          attributeOrderModalList.innerHTML = workingOrder
            .map((selectedId, index) => {
              const options = [
                `<option value="">${t(
                  "attrgen.dice.assignment.order.choose",
                  "Choose an Attribute"
                )}</option>`,
              ].concat(
                attributes.map((attribute) => {
                  const attributeId = String(attribute.id || "");
                  const label = String(attribute.displayName || attribute.name || attributeId);
                  return `<option value="${escapeHtml(attributeId)}" ${attributeId === selectedId ? "selected" : ""}>${escapeHtml(label)}</option>`;
                })
              ).join("");
              const rowLabel = t("attrgen.dice.assignment.order.row", "Roll {position} is assigned to").replace(
                "{position}",
                String(index + 1)
              );
              return `
                <div class="field">
                  <label for="attributeOrderPosition${index}">${escapeHtml(rowLabel)}</label>
                  <select id="attributeOrderPosition${index}" data-order-position="${index}">${options}</select>
                </div>
              `;
            })
            .join("");
          attributeOrderModalList.querySelectorAll("select").forEach((select) => {
            select.addEventListener("change", (event) => {
              const index = Number(event.target.dataset.orderPosition);
              workingOrder[index] = event.target.value;
            });
          });
        };

        renderOrderRows();
        attributeOrderModalSave.disabled = false;
        attributeOrderModal.classList.remove("hidden");
        window.requestAnimationFrame(() => attributeOrderModalList.querySelector("select")?.focus());
        attributeOrderModalCancel.onclick = () => attributeOrderModal.classList.add("hidden");
        attributeOrderModalSave.onclick = async () => {
          attributeOrderModalSave.disabled = true;
          const expectedAttributeIds = attributes
            .map((attribute) => String(attribute.id || "").trim())
            .filter(Boolean);
          const selectedAttributeIds = workingOrder.map((attributeId) => String(attributeId || "").trim());
          const uniqueSelectedAttributeIds = new Set(selectedAttributeIds.filter(Boolean));
          const orderIsComplete = selectedAttributeIds.length === expectedAttributeIds.length
            && selectedAttributeIds.every(Boolean)
            && uniqueSelectedAttributeIds.size === expectedAttributeIds.length
            && expectedAttributeIds.every((attributeId) => uniqueSelectedAttributeIds.has(attributeId));
          if (!orderIsComplete) {
            await showConfirm(
              t(
                "attrgen.dice.assignment.order.incomplete.message",
                "The order was not saved. Assign every Attribute once and only once, then try again."
              ),
              t("common.okay", "Okay"),
              "",
              t("attrgen.dice.assignment.order.incomplete.title", "Attribute Order Incomplete"),
              false,
              false
            );
            attributeOrderModalSave.disabled = false;
            return;
          }
          try {
            await api("POST", `/api/drafts/${state.draftId}/dice-rolling/attribute-order`, {
              attributeOrder: workingOrder,
            });
            markSaved(t("web.toast.dice_attribute_order_updated", "Attribute order updated"));
            attributeOrderModal.classList.add("hidden");
            renderDiceRolling();
          } catch (error) {
            showToast(error.message);
            attributeOrderModalSave.disabled = false;
          }
        };
      });
    }

    let diceSubstitutionSavePromise = Promise.resolve();
    const queueDiceSubstitutionSave = () => {
      const allowDiceSubstitution = document.getElementById("allowDiceSubstitution").checked;
      const diceSubstitutionValueInput = document.getElementById("diceSubstitutionValue");
      const maxDiceSubstitutionsInput = document.getElementById("maxDiceSubstitutions");
      const diceSubstitutionValue = Math.trunc(Number(diceSubstitutionValueInput.value || 0));
      const maxDiceSubstitutions = Math.max(0, Math.trunc(Number(maxDiceSubstitutionsInput.value || 0)));
      diceSubstitutionValueInput.value = String(diceSubstitutionValue);
      maxDiceSubstitutionsInput.value = String(maxDiceSubstitutions);
      if (hasDefaultAttributeScoreRange
          && (diceSubstitutionValue < minimumAttributeScore || diceSubstitutionValue > maximumAttributeScore)) {
        diceSubstitutionValueInput.setCustomValidity(substitutionRangeMessage);
        diceSubstitutionValueInput.reportValidity();
        showToast(substitutionRangeMessage);
        return;
      }
      diceSubstitutionValueInput.setCustomValidity("");
      diceSubstitutionSavePromise = diceSubstitutionSavePromise
        .catch(() => {})
        .then(async () => {
          try {
            await api("POST", `/api/drafts/${state.draftId}/dice-rolling/substitution`, {
              allowDiceSubstitution,
              diceSubstitutionValue,
              maxDiceSubstitutions,
            });
            markSaved(t("web.toast.dice_substitution_updated", "Dice substitution updated"));
          } catch (error) {
            showToast(error.message);
          }
        });
    };
    ["allowDiceSubstitution", "diceSubstitutionValue", "maxDiceSubstitutions"].forEach((id) => {
      document.getElementById(id).addEventListener("change", queueDiceSubstitutionSave);
    });

    const updateRerollMax = () => {
      const sides = Number(diceTermModalSides.value || 0);
      diceTermModalReroll.max = sides > 0 ? String(sides) : "1000";
      if (sides > 0 && Number(diceTermModalReroll.value || 0) > sides) {
        diceTermModalReroll.value = String(sides);
      }
    };

    const openDiceTermEditor = (index = -1) => {
      const editing = Number.isInteger(index) && index >= 0 && index < diceTerms.length;
      const term = editing ? diceTerms[index] : { count: 3, sides: defaultSides, ignoredFaces: [], dropLowest: 0 };
      const ignoredFaces = Array.isArray(term.ignoredFaces) ? term.ignoredFaces.map(Number) : [];
      const rerollResult = ignoredFaces.length ? Math.max(...ignoredFaces) + 1 : 0;
      diceTermModalTitle.textContent = editing ? t("attrgen.dice.edit", "Edit Dice Term") : t("attrgen.dice.add", "Add Dice Term");
      diceTermModalCountLabel.textContent = t("attrgen.dice.count", "Number of Rolls");
      diceTermModalSidesLabel.textContent = t("attrgen.dice.sides", "Dice Sides");
      diceTermModalRerollLabel.textContent = t("attrgen.dice.reroll", "Reroll Below");
      diceTermModalDropLowestLabel.textContent = t("attrgen.dice.drop_lowest", "Drop lowest roll");
      diceTermModalCancel.textContent = t("common.cancel", "Cancel");
      diceTermModalSave.textContent = editing ? t("common.save", "Save") : t("attrgen.dice.add", "Add Dice Term");
      diceTermModalCount.value = String(term.count || 0);
      diceTermModalSides.innerHTML = diceSideOptions;
      diceTermModalSides.value = String(term.sides || defaultSides || "");
      diceTermModalReroll.value = String(rerollResult);
      diceTermModalDropLowest.checked = Number(term.dropLowest || 0) > 0;
      diceTermModalSave.disabled = false;
      diceTermModalSides.onchange = updateRerollMax;
      updateRerollMax();
      diceTermModal.classList.remove("hidden");
      window.requestAnimationFrame(() => diceTermModalCount.focus());

      diceTermModalCancel.onclick = () => {
        diceTermModal.classList.add("hidden");
      };
      diceTermModalSave.onclick = async () => {
        const count = Number(diceTermModalCount.value);
        const sides = Number(diceTermModalSides.value);
        if (!sides) {
          showToast(t("common.die.required", "Select a die size."));
          return;
        }
        const rerollResult = Number(diceTermModalReroll.value);
        const dropLowest = diceTermModalDropLowest.checked;
        diceTermModalSave.disabled = true;
        try {
          await api("POST", `/api/drafts/${state.draftId}/dice-rolling/term${editing ? "/update" : ""}`, {
            index,
            count,
            sides,
            rerollResult,
            dropLowest,
          });
          markSaved(t(editing ? "web.toast.dice_term_updated" : "web.toast.dice_term_added", editing ? "Dice term updated" : "Dice term added"));
          diceTermModal.classList.add("hidden");
          renderDiceRolling();
        } catch (error) {
          showToast(error.message);
          diceTermModalSave.disabled = false;
        }
      };
    };

    document.getElementById("addTerm").addEventListener("click", () => openDiceTermEditor());

    document.querySelectorAll("#termList button").forEach((button) => {
      button.addEventListener("click", async () => {
        const index = Number(button.dataset.editTerm ?? button.dataset.removeTerm);
        if (button.hasAttribute("data-edit-term")) {
          openDiceTermEditor(index);
          return;
        }
        const confirmed = await showConfirm(
          t("web.confirm.remove.dice_term", "Remove this dice term?"),
          t("common.remove", "Remove")
        );
        if (!confirmed) {
          return;
        }
        try {
          await api("DELETE", `/api/drafts/${state.draftId}/dice-rolling/term`, { index });
          markSaved(t("web.toast.dice_term_removed", "Dice term removed"));
          renderDiceRolling();
        } catch (error) {
          showToast(error.message);
        }
      });
    });

    document.getElementById("backToStandard").addEventListener("click", navigateBackInApp);
    document.getElementById("diceContinue").addEventListener("click", () => {
      navigateToStep(getNextAttributeGenerationStep("dice-rolling"));
    });
  } catch (error) {
    showToast(error.message);
  }
}

async function renderPointsBuy() {
  if (!ensureDraft()) {
    return;
  }
  setStep("points-buy");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    await ensureAttributeGenerationType();
    if (!usesAttributeGenerationStage("point_buy")) {
      view.innerHTML = `
        <section class="panel">
          <h1>${t("attrgen.point.title", "Point Buy")}</h1>
          <p class="field-hint">${t(
            "attrgen.point.disabled",
            "Point Buy is not selected in Attribute Generation, so this screen is locked to prevent unused point-buy data from being edited."
          )}</p>
          <div class="actions-row">
            <div class="left">
              <button class="btn ghost" id="backToDiceRolling" type="button">${t("setup.back", "Back")}</button>
            </div>
            <div class="right">
              <button class="btn" id="pointsContinue" type="button">${t("common.continue", "Continue")}</button>
            </div>
          </div>
        </section>
      `;
      document.getElementById("backToDiceRolling").addEventListener("click", navigateBackInApp);
      document.getElementById("pointsContinue").addEventListener("click", () => {
        navigateToStep(getNextAvailableAttributeGenerationStep("points-buy"));
      });
      return;
    }
    const data = await api("GET", `/api/drafts/${state.draftId}/points-buy`);
    const attributeCategories = Array.isArray(data.attributeCategories) ? data.attributeCategories : [];
    let categoryPointRules = Array.isArray(data.categoryPointRules) ? data.categoryPointRules : [];
    let categoryPointSlots = Array.isArray(data.categoryPointSlots) ? data.categoryPointSlots : [];
    const assignByCategory = Boolean(data.assignByCategory);
    const categoryAssignmentMode = data.categoryAssignmentMode === "player" ? "player" : "creator";
    const categoryCount = attributeCategories.length;
    const categoryNameByKey = new Map(
      attributeCategories.map((category) => [
        String(category.key || ""),
        String(category.displayName || category.name || category.key || ""),
      ])
    );
    const formatProgress = (key, fallback, count) =>
      t(key, fallback)
        .replace("{count}", String(count))
        .replace("{total}", String(categoryCount));
    const fixedRulesComplete = categoryCount > 0 && categoryPointRules.length === categoryCount;
    const playerSlotsComplete = categoryCount > 0 && categoryPointSlots.length === categoryCount;
    const fixedRuleList = categoryPointRules.length
      ? categoryPointRules.map((rule, index) => renderCollectionRow(
          `<span><strong>${escapeHtml(
            rule.attributeCategoryName || categoryNameByKey.get(String(rule.attributeCategoryKey || "")) || rule.attributeCategoryKey
          )}</strong>: ${Number(rule.availablePoints || 0)} ${t("attrgen.point.points", "points")}</span>`,
          [
            collectionEditAction("edit-category-point-rule", index),
            collectionRemoveAction("remove-category-point-rule", index),
          ]
        )).join("")
      : `<div class="list-item">${t("attrgen.point.fixed.none", "No category budgets yet.")}</div>`;
    const playerSlotList = categoryPointSlots.length
      ? categoryPointSlots.map((slot, index) => renderCollectionRow(
          `<span><strong>${escapeHtml(slot.name || "")}</strong>: ${Number(slot.availablePoints || 0)} ${t("attrgen.point.points", "points")}</span>`,
          [
            collectionEditAction("edit-category-point-slot", index),
            collectionRemoveAction("remove-category-point-slot", index),
          ]
        )).join("")
      : `<div class="list-item">${t("attrgen.point.slot.none", "No point slots yet.")}</div>`;
    view.innerHTML = `
      <section class="panel">
        <h1>${t("attrgen.point.title", "Point Buy")}</h1>
        <p class="field-hint">${t(
          "attrgen.point.intro",
          "Set the available points and bounds for point-buy attribute generation."
        )}</p>
        <div class="grid two">
          <div class="field">
            <label>${t("attrgen.point.assign_by_category", "Assign available points by Attribute Category")}</label>
            <select id="assignByCategory">
              <option value="false" ${assignByCategory ? "" : "selected"}>${t("common.no", "No")}</option>
              <option value="true" ${assignByCategory ? "selected" : ""} ${categoryCount === 0 ? "disabled" : ""}>${t("common.yes", "Yes")}</option>
            </select>
            <span class="field-hint">${t(
              "attrgen.point.assign_by_category.help",
              "Choose No for one shared point pool, or Yes for a separate pool for each Attribute Category."
            )}</span>
          </div>
          <div class="field ${assignByCategory ? "hidden" : ""}" id="globalPointBudgetField">
            <label>${t("attrgen.point.base_points", "Base Points")}</label>
            <input type="number" id="basePoints" value="${data.basePoints}">
            <span class="field-hint">${t("attrgen.point.global_budget.help", "This shared budget applies across all Attributes.")}</span>
          </div>
          <div class="field">
            <label>${t("attrgen.point.min_value", "Minimum Value")}</label>
            <input type="number" id="minValue" value="${data.minValue}">
          </div>
          <div class="field">
            <label>${t("attrgen.point.max_value", "Maximum Value")}</label>
            <input type="number" id="maxValue" value="${data.maxValue}">
          </div>
          <div class="field">
            <label>${t("attrgen.point.max_post_racial", "Max Value Post-Racial")}</label>
            <input type="number" id="maxPostRacial" value="${data.maxPostRacial}">
          </div>
          <div class="field">
            <label>${t("attrgen.point.min_points_spend", "Minimum Points to Spend")}</label>
            <input type="number" id="minPoints" value="${data.minPointsToSpend}">
          </div>
          <div class="field">
            <label>${t("attrgen.point.allow_negative", "Allow Negative Attributes")}</label>
            <select id="allowNegative">
              <option value="false" ${data.allowNegative ? "" : "selected"}>${t("common.no", "No")}</option>
              <option value="true" ${data.allowNegative ? "selected" : ""}>${t("common.yes", "Yes")}</option>
            </select>
          </div>
        </div>
        ${categoryCount === 0 ? `<div class="point-buy-configuration-notice">${t(
          "attrgen.point.no_categories",
          "Create Attribute Categories before assigning Point Buy budgets by category."
        )}</div>` : ""}
        <div class="edit-section point-buy-category-settings ${assignByCategory ? "" : "hidden"}" id="pointBuyCategorySettings">
          <div class="system-name-collection-break" aria-hidden="true"></div>
          <div class="field point-buy-assignment-mode">
            <label>${t("attrgen.point.assignment_mode", "Who assigns categories to point pools?")}</label>
            <select id="categoryAssignmentMode">
              <option value="creator" ${categoryAssignmentMode === "creator" ? "selected" : ""}>${t(
                "attrgen.point.assignment.creator",
                "Creator assigns categories now"
              )}</option>
              <option value="player" ${categoryAssignmentMode === "player" ? "selected" : ""}>${t(
                "attrgen.point.assignment.player",
                "Player assigns categories during character generation"
              )}</option>
            </select>
          </div>
          <div id="fixedCategoryPointEditor" class="edit-section ${categoryAssignmentMode === "creator" ? "" : "hidden"}">
            <h3 class="collection-editor-heading">${t("attrgen.point.fixed.title", "Category Budgets")}</h3>
            <p class="field-hint">${t(
              "attrgen.point.fixed.help",
              "Set the available points for each Attribute Category now."
            )}</p>
            <div class="point-buy-configuration-notice ${fixedRulesComplete ? "complete" : ""}">${formatProgress(
              "attrgen.point.fixed.progress",
              "{count} of {total} Attribute Categories configured.",
              categoryPointRules.length
            )}</div>
            <button class="btn secondary collection-add-button" id="addCategoryPointRule" type="button" ${categoryPointRules.length >= categoryCount ? "disabled" : ""}>${t(
              "attrgen.point.fixed.add",
              "Add Category Budget"
            )}</button>
            <div class="list" id="categoryPointRuleList">${fixedRuleList}</div>
          </div>
          <div id="playerCategoryPointEditor" class="edit-section ${categoryAssignmentMode === "player" ? "" : "hidden"}">
            <h3 class="collection-editor-heading">${t("attrgen.point.slot.title", "Player-Assigned Point Slots")}</h3>
            <p class="field-hint">${t(
              "attrgen.point.slot.help",
              "Create one named point slot for each Attribute Category. Players attach each slot to a different category during character generation."
            )}</p>
            <div class="point-buy-configuration-notice ${playerSlotsComplete ? "complete" : ""}">${formatProgress(
              "attrgen.point.slot.progress",
              "{count} of {total} point slots defined.",
              categoryPointSlots.length
            )}</div>
            <button class="btn secondary collection-add-button" id="addCategoryPointSlot" type="button" ${categoryPointSlots.length >= categoryCount ? "disabled" : ""}>${t(
              "attrgen.point.slot.add",
              "Add Point Slot"
            )}</button>
            <div class="list" id="categoryPointSlotList">${playerSlotList}</div>
          </div>
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToDiceRolling" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn ghost" id="pointsContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    let pointBuySavePromise = Promise.resolve(true);
    const queuePointBuySave = (payload, rerender = false) => {
      pointBuySavePromise = pointBuySavePromise.catch(() => false).then(async () => {
        try {
          await api("POST", `/api/drafts/${state.draftId}/points-buy`, payload);
          markSaved(t("web.toast.points_buy_updated", "Point Buy updated"));
          if (rerender) {
            await renderPointsBuy();
          }
          return true;
        } catch (error) {
          showToast(error.message);
          return false;
        }
      });
      return pointBuySavePromise;
    };
    const savePointBuyScalars = () =>
      queuePointBuySave({
        basePoints: Number(document.getElementById("basePoints").value),
        minValue: Number(document.getElementById("minValue").value),
        maxValue: Number(document.getElementById("maxValue").value),
        maxPostRacial: Number(document.getElementById("maxPostRacial").value),
        minPointsToSpend: Number(document.getElementById("minPoints").value),
        allowNegative: document.getElementById("allowNegative").value === "true",
      });
    ["basePoints", "minValue", "maxValue", "maxPostRacial", "minPoints", "allowNegative"].forEach((id) => {
      document.getElementById(id).addEventListener("change", savePointBuyScalars);
    });

    document.getElementById("assignByCategory").addEventListener("change", (event) => {
      queuePointBuySave({ assignByCategory: event.target.value === "true" }, true);
    });
    const assignmentModeSelect = document.getElementById("categoryAssignmentMode");
    if (assignmentModeSelect) {
      assignmentModeSelect.addEventListener("change", (event) => {
        queuePointBuySave({ categoryAssignmentMode: event.target.value }, true);
      });
    }

    const closePointCategoryBudgetModal = () => pointCategoryBudgetModal.classList.add("hidden");
    pointCategoryBudgetModalCancel.onclick = closePointCategoryBudgetModal;
    const openPointCategoryBudgetModal = (kind, index = -1) => {
      const editing = index >= 0;
      const isPlayerSlot = kind === "player";
      const currentEntry = editing
        ? (isPlayerSlot ? categoryPointSlots[index] : categoryPointRules[index])
        : null;
      pointCategoryBudgetCategoryField.classList.toggle("hidden", isPlayerSlot);
      pointCategoryBudgetNameField.classList.toggle("hidden", !isPlayerSlot);
      pointCategoryBudgetCategoryLabel.textContent = t("attrgen.point.modal.category", "Attribute Category");
      pointCategoryBudgetNameLabel.textContent = t("attrgen.point.modal.slot_name", "Slot Name");
      pointCategoryBudgetPointsLabel.textContent = t("attrgen.point.modal.points", "Available Points");
      pointCategoryBudgetModalCancel.textContent = t("common.cancel", "Cancel");
      pointCategoryBudgetModalSave.textContent = editing ? t("common.save", "Save") : t("common.add", "Add");
      pointCategoryBudgetModalTitle.textContent = isPlayerSlot
        ? t(editing ? "attrgen.point.slot.edit" : "attrgen.point.slot.add", editing ? "Edit Point Slot" : "Add Point Slot")
        : t(editing ? "attrgen.point.fixed.edit" : "attrgen.point.fixed.add", editing ? "Edit Category Budget" : "Add Category Budget");
      if (isPlayerSlot) {
        pointCategoryBudgetName.value = currentEntry ? String(currentEntry.name || "") : "";
      } else {
        const currentKey = currentEntry ? String(currentEntry.attributeCategoryKey || "") : "";
        const usedKeys = new Set(categoryPointRules.map((rule, ruleIndex) =>
          ruleIndex === index ? "" : String(rule.attributeCategoryKey || "")
        ));
        pointCategoryBudgetCategory.innerHTML = attributeCategories
          .filter((category) => !usedKeys.has(String(category.key || "")))
          .map((category) => {
            const key = String(category.key || "");
            const selected = key === currentKey ? " selected" : "";
            return `<option value="${escapeHtml(key)}"${selected}>${escapeHtml(
              category.displayName || category.name || key
            )}</option>`;
          })
          .join("");
      }
      pointCategoryBudgetPoints.value = String(currentEntry ? Number(currentEntry.availablePoints || 0) : 0);
      pointCategoryBudgetModalSave.disabled = false;
      pointCategoryBudgetModal.classList.remove("hidden");
      window.requestAnimationFrame(() => {
        (isPlayerSlot ? pointCategoryBudgetName : pointCategoryBudgetCategory).focus();
      });
      pointCategoryBudgetModalSave.onclick = async () => {
        const availablePoints = Number(pointCategoryBudgetPoints.value);
        if (!Number.isInteger(availablePoints) || availablePoints < 0) {
          showToast(t("attrgen.point.validation.points", "Available Points must be a whole number of zero or more."));
          return;
        }
        pointCategoryBudgetModalSave.disabled = true;
        let saved;
        if (isPlayerSlot) {
          const name = pointCategoryBudgetName.value.trim();
          if (!name) {
            showToast(t("attrgen.point.validation.slot_name", "Enter a name for this point slot."));
            pointCategoryBudgetModalSave.disabled = false;
            return;
          }
          const duplicateName = categoryPointSlots.some((slot, slotIndex) =>
            slotIndex !== index && String(slot.name || "").trim().toLocaleLowerCase() === name.toLocaleLowerCase()
          );
          if (duplicateName) {
            showToast(t("attrgen.point.validation.unique_slot_name", "Each point slot needs a unique name."));
            pointCategoryBudgetModalSave.disabled = false;
            return;
          }
          const nextSlots = categoryPointSlots.map((slot) => ({
            id: String(slot.id || ""),
            name: String(slot.name || ""),
            availablePoints: Number(slot.availablePoints || 0),
          }));
          const nextSlot = {
            id: currentEntry ? String(currentEntry.id || "") : "",
            name,
            availablePoints,
          };
          if (editing) {
            nextSlots[index] = nextSlot;
          } else {
            nextSlots.push(nextSlot);
          }
          saved = await queuePointBuySave({ categoryPointSlots: nextSlots }, true);
        } else {
          const categoryKey = pointCategoryBudgetCategory.value;
          if (!categoryKey) {
            showToast(t("attrgen.point.validation.category", "Choose an Attribute Category."));
            pointCategoryBudgetModalSave.disabled = false;
            return;
          }
          const nextRules = categoryPointRules.map((rule) => ({
            attributeCategoryKey: String(rule.attributeCategoryKey || ""),
            availablePoints: Number(rule.availablePoints || 0),
          }));
          const nextRule = { attributeCategoryKey: categoryKey, availablePoints };
          if (editing) {
            nextRules[index] = nextRule;
          } else {
            nextRules.push(nextRule);
          }
          saved = await queuePointBuySave({ categoryPointRules: nextRules }, true);
        }
        if (saved) {
          closePointCategoryBudgetModal();
        } else {
          pointCategoryBudgetModalSave.disabled = false;
        }
      };
    };

    const addCategoryPointRule = document.getElementById("addCategoryPointRule");
    if (addCategoryPointRule) {
      addCategoryPointRule.addEventListener("click", () => openPointCategoryBudgetModal("creator"));
    }
    const addCategoryPointSlot = document.getElementById("addCategoryPointSlot");
    if (addCategoryPointSlot) {
      addCategoryPointSlot.addEventListener("click", () => openPointCategoryBudgetModal("player"));
    }
    document.querySelectorAll("[data-edit-category-point-rule]").forEach((button) => {
      button.addEventListener("click", () => openPointCategoryBudgetModal("creator", Number(button.dataset.editCategoryPointRule)));
    });
    document.querySelectorAll("[data-edit-category-point-slot]").forEach((button) => {
      button.addEventListener("click", () => openPointCategoryBudgetModal("player", Number(button.dataset.editCategoryPointSlot)));
    });
    document.querySelectorAll("[data-remove-category-point-rule]").forEach((button) => {
      button.addEventListener("click", async () => {
        const index = Number(button.dataset.removeCategoryPointRule);
        const confirmed = await showConfirm(
          t("attrgen.point.fixed.remove_confirm", "Remove this category budget?"),
          t("common.remove", "Remove")
        );
        if (!confirmed) {
          return;
        }
        const nextRules = categoryPointRules
          .filter((unused, ruleIndex) => ruleIndex !== index)
          .map((rule) => ({
            attributeCategoryKey: String(rule.attributeCategoryKey || ""),
            availablePoints: Number(rule.availablePoints || 0),
          }));
        await queuePointBuySave({ categoryPointRules: nextRules }, true);
      });
    });
    document.querySelectorAll("[data-remove-category-point-slot]").forEach((button) => {
      button.addEventListener("click", async () => {
        const index = Number(button.dataset.removeCategoryPointSlot);
        const confirmed = await showConfirm(
          t("attrgen.point.slot.remove_confirm", "Remove this point slot?"),
          t("common.remove", "Remove")
        );
        if (!confirmed) {
          return;
        }
        const nextSlots = categoryPointSlots
          .filter((unused, slotIndex) => slotIndex !== index)
          .map((slot) => ({
            id: String(slot.id || ""),
            name: String(slot.name || ""),
            availablePoints: Number(slot.availablePoints || 0),
          }));
        await queuePointBuySave({ categoryPointSlots: nextSlots }, true);
      });
    });

    document.getElementById("backToDiceRolling").addEventListener("click", navigateBackInApp);
    document.getElementById("pointsContinue").addEventListener("click", async () => {
      if (!(await savePointBuyScalars())) {
        return;
      }
      const categoryConfigurationComplete = categoryAssignmentMode === "player"
        ? playerSlotsComplete
        : fixedRulesComplete;
      if (assignByCategory && !categoryConfigurationComplete) {
        showToast(t(
          "attrgen.point.configuration_incomplete",
          "Complete one point budget for each Attribute Category before continuing."
        ));
        return;
      }
      navigateToStep(getNextAttributeGenerationStep("points-buy"));
    });
  } catch (error) {
    showToast(error.message);
  }
}

async function renderHitPoints() {
  if (!ensureDraft()) {
    return;
  }
  setStep("hit-points");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const data = await api("GET", `/api/drafts/${state.draftId}/hit-points`);
    const hpGainMethod = String(data.hpGainMethod || "");
    const attributeDerived = hpGainMethod === "attribute_derived";
    const independentGainMethod = hpGainMethod === "fixed" ? "fixed" : "rolled";
    const allCharactersUseSameFixedGain = data.allCharactersUseSameFixedGain !== false;
    const allCharactersUseSameHitDice = data.allCharactersUseSameHitDice !== false;
    const hitDieSides = Math.max(0, Math.trunc(Number(data.hitDieSides || 0)));
    const hitDieCount = Math.max(1, Math.trunc(Number(data.hitDieCount || 1)));
    const hitDieModifier = Math.trunc(Number(data.hitDieModifier || 0));
    const noHitPointGain = !attributeDerived && hpGainMethod === "fixed"
      && allCharactersUseSameFixedGain
      && Number(data.fixedHPPerLevel || 0) === 0
      && Number(data.minimumHPPerLevel || 0) === 0;
    const hpAttributes = Array.isArray(data.attributes) ? data.attributes : [];
    const attributeDerivationMode = ["direct", "single_formula", "multi_formula"].includes(data.attributeDerivationMode)
      ? data.attributeDerivationMode
      : "direct";
    const attributeDerivedDirectAttributeId = String(data.attributeDerivedDirectAttributeId || "");
    const attributeDerivedBaseValue = Number(data.attributeDerivedBaseValue || 0);
    const attributeDerivedDivisor = Number(data.attributeDerivedDivisor || 1) || 1;
    const attributeDerivedRoundingMethod = ["up", "down", "nearest"].includes(data.attributeDerivedRoundingMethod)
      ? data.attributeDerivedRoundingMethod
      : "nearest";
    let attributeDerivedTerms = (Array.isArray(data.attributeDerivedTerms) ? data.attributeDerivedTerms : [])
      .map((term) => ({
        attributeId: String(term.attributeId || ""),
        multiplier: Number.isFinite(Number(term.multiplier)) ? Number(term.multiplier) : 1,
      }));

    const buildOptions = (options, selected) =>
      options
        .map((entry) => {
          const value = entry.value;
          const label = entry.label;
          const isSelected = value === selected ? " selected" : "";
          return `<option value="${value}"${isSelected}>${label}</option>`;
        })
        .join("");

    const methodOptions = buildOptions(
      [
        { value: "rolled", label: t("hp.method.rolled", "Rolled") },
        { value: "fixed", label: t("hp.method.fixed", "Fixed") },
      ],
      independentGainMethod
    );
    const roundingOptions = buildOptions(
      [
        { value: "up", label: t("hp.attribute.rounding.up", "Round Up") },
        { value: "down", label: t("hp.attribute.rounding.down", "Round Down") },
        { value: "nearest", label: t("hp.attribute.rounding.nearest", "Round to Nearest") },
      ],
      attributeDerivedRoundingMethod
    );
    const hitDice = Array.from(new Set(
      (Array.isArray(data.diceUsed) ? data.diceUsed : [])
        .map((value) => Math.trunc(Number(value || 0)))
        .filter((value) => value > 0)
        .concat(hitDieSides > 0 ? [hitDieSides] : [])
    )).sort((left, right) => left - right);
    const hitDieOptions = [
      `<option value="">${t("hp.dice.choose", "Choose a die")}</option>`,
      ...hitDice.map((sides) => `<option value="${sides}"${sides === hitDieSides ? " selected" : ""}>d${sides}</option>`),
    ].join("");
    const attributeOptions = (selected = "") => [
      `<option value="">${t("hp.attribute.choose", "Choose an Attribute")}</option>`,
      ...hpAttributes.map((attribute) => {
        const id = String(attribute.id || "");
        const label = String(attribute.displayName || attribute.name || id);
        return `<option value="${escapeHtml(id)}"${id === selected ? " selected" : ""}>${escapeHtml(label)}</option>`;
      }),
    ].join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${t("hp.title", "Hit Points")}</h1>
        <div class="hp-system-choice">
          <h2>${t("hp.system.section", "How are Hit Points determined?")}</h2>
          <label class="hp-radio-option" for="hpSystemIndependent">
            <input type="radio" name="hpSystem" id="hpSystemIndependent" value="independent" ${attributeDerived ? "" : "checked"}>
            <span>
              <strong>${t("hp.system.independent", "Independent Hit Points")}</strong>
              <span class="field-hint">${t("hp.system.independent.help", "Characters have starting Hit Points and may gain more through advancement.")}</span>
            </span>
          </label>
          <label class="hp-radio-option" for="hpSystemAttributeDerived">
            <input type="radio" name="hpSystem" id="hpSystemAttributeDerived" value="attribute_derived" ${attributeDerived ? "checked" : ""}>
            <span>
              <strong>${t("hp.system.attribute_derived", "Attribute Derived")}</strong>
              <span class="field-hint">${t("hp.system.attribute_derived.help", "Maximum Hit Points are recalculated from character Attributes instead of using separate starting and advancement values.")}</span>
            </span>
          </label>
        </div>

        <div id="hpIndependentSettings" class="${attributeDerived ? "hidden" : ""}">
          <section class="hp-settings-section">
            <h2>${t("hp.starting.section", "Starting Hit Points")}</h2>
            <div id="hpNoGainSettings" class="grid two ${noHitPointGain ? "" : "hidden"}">
              <div class="field">
                <label for="hpBaseHitPoints">${t("hp.base", "Base Hit Points")}</label>
                <input type="number" id="hpBaseHitPoints" value="${Number(data.firstLevelBonusHP || 0)}">
                <span class="field-hint">${t("hp.base.help", "The character's complete starting Hit Point pool.")}</span>
              </div>
            </div>
            <div id="hpProgressiveStartingSettings" class="grid two ${noHitPointGain ? "hidden" : ""}">
              <div class="field" id="hpFirstLevelMaxField">
                <label>${t("hp.first_level.max", "Max HP at First Level")}</label>
                <select id="hpFirstLevelMax">
                  <option value="true" ${data.firstLevelMaxHP ? "selected" : ""}>${t("common.yes", "Yes")}</option>
                  <option value="false" ${data.firstLevelMaxHP ? "" : "selected"}>${t("common.no", "No")}</option>
                </select>
              </div>
              <div class="field">
                <label>${t("hp.first_level.bonus", "First Level Bonus HP")}</label>
                <input type="number" id="hpFirstLevelBonus" value="${Number(data.firstLevelBonusHP || 0)}">
                <span class="field-hint">${t("hp.first_level.bonus.help", "A one-time bonus added only when the character is created.")}</span>
              </div>
            </div>
          </section>

          <section class="hp-settings-section">
            <h2>${t("hp.gain.section", "Hit Point Gain")}</h2>
            <div class="field hp-no-gain-choice">
              <label class="checkbox-label" for="hpNoGain">
                <input type="checkbox" id="hpNoGain" ${noHitPointGain ? "checked" : ""}>
                <span>${t("hp.no_gain", "No Hit Point Gain")}</span>
              </label>
              <span class="field-hint">${t(
                "hp.no_gain.help",
                "Use one starting Hit Point pool that does not increase through normal character advancement."
              )}</span>
            </div>
            <div id="hpGainSettings" class="${noHitPointGain ? "hidden" : ""}">
              <div class="grid two">
                <div class="field">
                  <label>${t("hp.method.label", "HP Gain Method")}</label>
                  <select id="hpGainMethod">${methodOptions}</select>
                </div>
              </div>
              <div id="hpDiceSourceSettings">
                <div class="field">
                  <label class="checkbox-label" for="hpAllCharactersSameDice">
                    <input type="checkbox" id="hpAllCharactersSameDice" ${allCharactersUseSameHitDice ? "checked" : ""}>
                    <span>${t("hp.dice.same_for_all", "All characters use the same Hit Point dice")}</span>
                  </label>
                  <span class="field-hint">${t(
                    "hp.dice.same_for_all.help",
                    "When not selected, you will have to assign dice later, in the appropriate section."
                  )}</span>
                </div>
                <div id="hpCommonDiceSettings">
                  <div class="grid three">
                    <div class="field">
                      <label for="hpHitDieSides">${t("hp.dice.die", "Die")}</label>
                      <select id="hpHitDieSides">${hitDieOptions}</select>
                    </div>
                    <div class="field">
                      <label for="hpHitDieCount">${t("hp.dice.rolls", "Rolls")}</label>
                      <input type="number" id="hpHitDieCount" min="1" step="1" value="${hitDieCount}">
                    </div>
                    <div class="field">
                      <label for="hpHitDieModifier">${t("hp.dice.modifier", "Modifier")}</label>
                      <input type="number" id="hpHitDieModifier" step="1" value="${hitDieModifier}">
                    </div>
                  </div>
                  <p class="field-hint" id="hpDiceExpressionPreview"></p>
                  <p class="field-hint">${t(
                    "hp.dice.modifier.help",
                    "The Modifier is added once to the complete dice total. If the same fixed amount is the entire advancement gain, use Fixed HP per Level instead."
                  )}</p>
                </div>
              </div>
              <div id="hpFixedSettings">
                <div class="field">
                  <label class="checkbox-label" for="hpAllCharactersSameFixedGain">
                    <input type="checkbox" id="hpAllCharactersSameFixedGain" ${allCharactersUseSameFixedGain ? "checked" : ""}>
                    <span>${t("hp.fixed.same_for_all", "All characters gain the same fixed Hit Points")}</span>
                  </label>
                  <span class="field-hint">${t(
                    "hp.fixed.same_for_all.help",
                    "When not selected, you will have to assign fixed gains later, in the appropriate section."
                  )}</span>
                </div>
                <div id="hpCommonFixedSettings" class="grid two">
                  <div class="field">
                    <label>${t("hp.fixed_per_level", "Fixed HP per Level")}</label>
                    <input type="number" id="hpFixedPerLevel" value="${Number(data.fixedHPPerLevel || 0)}">
                  </div>
                </div>
              </div>
              <div class="grid two">
                <div class="field">
                  <label>${t("hp.minimum_per_level", "Minimum HP per Level")}</label>
                  <input type="number" id="hpMinimumPerLevel" value="${Number(data.minimumHPPerLevel || 0)}">
                </div>
              </div>
            </div>
          </section>
        </div>

        <section id="hpAttributeDerivedSettings" class="hp-settings-section ${attributeDerived ? "" : "hidden"}">
          <h2>${t("hp.attribute.section", "Attribute-Derived Hit Points")}</h2>
          <p class="field-hint">${t("hp.attribute.help", "Choose one calculation. Maximum Hit Points update whenever the referenced Attribute scores change.")}</p>
          <div class="hp-derived-mode-list">
            <label class="hp-radio-option">
              <input type="radio" name="hpDerivationMode" value="direct" ${attributeDerivationMode === "direct" ? "checked" : ""}>
              <span><strong>${t("hp.attribute.mode.direct", "Direct Attribute")}</strong><span class="field-hint">${t("hp.attribute.mode.direct.help", "Maximum Hit Points equal one Attribute's full score.")}</span></span>
            </label>
            <label class="hp-radio-option">
              <input type="radio" name="hpDerivationMode" value="single_formula" ${attributeDerivationMode === "single_formula" ? "checked" : ""}>
              <span><strong>${t("hp.attribute.mode.single", "Single-Attribute Formula")}</strong><span class="field-hint">${t("hp.attribute.mode.single.help", "Calculate Maximum Hit Points from one Attribute, a multiplier, and optional fixed values.")}</span></span>
            </label>
            <label class="hp-radio-option">
              <input type="radio" name="hpDerivationMode" value="multi_formula" ${attributeDerivationMode === "multi_formula" ? "checked" : ""}>
              <span><strong>${t("hp.attribute.mode.multi", "Multi-Attribute Formula")}</strong><span class="field-hint">${t("hp.attribute.mode.multi.help", "Combine two or more Attributes into one Maximum Hit Point value.")}</span></span>
            </label>
          </div>
          <div id="hpDirectAttributeSettings" class="grid two">
            <div class="field">
              <label for="hpDirectAttribute">${t("hp.attribute.label", "HP Attribute")}</label>
              <select id="hpDirectAttribute">${attributeOptions(attributeDerivedDirectAttributeId)}</select>
            </div>
          </div>
          <div id="hpAttributeFormulaSettings">
            <div class="grid three">
              <div class="field">
                <label for="hpAttributeBaseValue">${t("hp.attribute.base", "Base Value")}</label>
                <input type="number" id="hpAttributeBaseValue" step="any" value="${attributeDerivedBaseValue}">
              </div>
              <div class="field">
                <label for="hpAttributeDivisor">${t("hp.attribute.divisor", "Divide Total By")}</label>
                <input type="number" id="hpAttributeDivisor" step="any" value="${attributeDerivedDivisor}">
              </div>
              <div class="field">
                <label for="hpAttributeRounding">${t("hp.attribute.rounding", "Rounding")}</label>
                <select id="hpAttributeRounding">${roundingOptions}</select>
              </div>
            </div>
            <div class="hp-derived-terms-heading">
              <h3>${t("hp.attribute.terms", "Attribute Terms")}</h3>
              <button class="btn secondary" id="hpAddAttributeTerm" type="button">${t("hp.attribute.term.add", "Add Attribute")}</button>
            </div>
            <div id="hpAttributeTerms" class="hp-derived-terms"></div>
            <p id="hpAttributeFormulaPreview" class="field-hint"></p>
          </div>
        </section>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToAttributes" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn" id="hpContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    const updateDiceExpressionPreview = () => {
      const sides = Math.max(0, Math.trunc(Number(document.getElementById("hpHitDieSides").value || 0)));
      const count = Math.max(1, Math.trunc(Number(document.getElementById("hpHitDieCount").value || 1)));
      const modifier = Math.trunc(Number(document.getElementById("hpHitDieModifier").value || 0));
      const expression = sides > 0
        ? `${count}d${sides}${modifier > 0 ? `+${modifier}` : modifier < 0 ? modifier : ""}`
        : t("hp.dice.unset", "No die selected");
      const preview = document.getElementById("hpDiceExpressionPreview");
      preview.textContent = t("hp.dice.preview", "Expression: {expression}")
        .replace("{expression}", expression);
    };

    const updateMethodControls = () => {
      const method = document.getElementById("hpGainMethod").value;
      const dieBased = method === "rolled";
      const allSameDice = document.getElementById("hpAllCharactersSameDice").checked;
      const allSameFixedGain = document.getElementById("hpAllCharactersSameFixedGain").checked;
      document.getElementById("hpDiceSourceSettings").classList.toggle("hidden", !dieBased);
      document.getElementById("hpCommonDiceSettings").classList.toggle("hidden", !dieBased || !allSameDice);
      document.getElementById("hpFixedSettings").classList.toggle("hidden", method !== "fixed");
      document.getElementById("hpCommonFixedSettings").classList.toggle("hidden", method !== "fixed" || !allSameFixedGain);
      document.getElementById("hpFirstLevelMaxField").classList.toggle("hidden", !dieBased);
      updateDiceExpressionPreview();
    };

    const selectedDerivationMode = () => {
      const selected = document.querySelector('input[name="hpDerivationMode"]:checked');
      return selected ? selected.value : "direct";
    };

    const readAttributeTermInputs = () => {
      document.querySelectorAll("[data-hp-term-index]").forEach((select) => {
        const index = Number(select.dataset.hpTermIndex);
        if (attributeDerivedTerms[index]) {
          attributeDerivedTerms[index].attributeId = select.value;
        }
      });
      document.querySelectorAll("[data-hp-term-multiplier]").forEach((input) => {
        const index = Number(input.dataset.hpTermMultiplier);
        if (attributeDerivedTerms[index]) {
          const multiplier = Number(input.value);
          attributeDerivedTerms[index].multiplier = Number.isFinite(multiplier) ? multiplier : 1;
        }
      });
    };

    const updateAttributeFormulaPreview = () => {
      const preview = document.getElementById("hpAttributeFormulaPreview");
      const mode = selectedDerivationMode();
      if (mode === "direct") {
        preview.textContent = "";
        return;
      }
      readAttributeTermInputs();
      const limit = mode === "single_formula" ? 1 : attributeDerivedTerms.length;
      const names = new Map(hpAttributes.map((attribute) => [
        String(attribute.id || ""),
        String(attribute.displayName || attribute.name || t("hp.attribute.placeholder", "Attribute")),
      ]));
      const pieces = [];
      const baseValue = Number(document.getElementById("hpAttributeBaseValue").value || 0);
      if (baseValue !== 0) {
        pieces.push(String(baseValue));
      }
      attributeDerivedTerms.slice(0, limit).forEach((term) => {
        const name = names.get(term.attributeId) || t("hp.attribute.placeholder", "Attribute");
        pieces.push(`${term.multiplier} * ${name}`);
      });
      if (!pieces.length) {
        pieces.push("0");
      }
      const divisor = Number(document.getElementById("hpAttributeDivisor").value || 1) || 1;
      const expression = divisor === 1 ? pieces.join(" + ") : `(${pieces.join(" + ")}) / ${divisor}`;
      const rounding = document.getElementById("hpAttributeRounding").selectedOptions[0]?.textContent || "";
      preview.textContent = t("hp.attribute.preview", "Formula: {expression}. Rounding: {rounding}.")
        .replace("{expression}", expression)
        .replace("{rounding}", rounding);
    };

    let hitPointSavePromise = Promise.resolve(true);
    let saveHitPoints = () => Promise.resolve(true);

    const renderAttributeDerivedTerms = () => {
      const mode = selectedDerivationMode();
      const direct = mode === "direct";
      const single = mode === "single_formula";
      document.getElementById("hpDirectAttributeSettings").classList.toggle("hidden", !direct);
      document.getElementById("hpAttributeFormulaSettings").classList.toggle("hidden", direct);
      document.getElementById("hpAddAttributeTerm").classList.toggle("hidden", mode !== "multi_formula");
      if (direct) {
        updateAttributeFormulaPreview();
        return;
      }
      const requiredTerms = single ? 1 : 2;
      while (attributeDerivedTerms.length < requiredTerms) {
        attributeDerivedTerms.push({ attributeId: "", multiplier: 1 });
      }
      const visibleTerms = single ? attributeDerivedTerms.slice(0, 1) : attributeDerivedTerms;
      document.getElementById("hpAttributeTerms").innerHTML = visibleTerms.map((term, index) => `
        <div class="hp-derived-term-row">
          <div class="field">
            <label for="hpAttributeTerm${index}">${t("hp.attribute.label", "HP Attribute")}</label>
            <select id="hpAttributeTerm${index}" data-hp-term-index="${index}">${attributeOptions(term.attributeId)}</select>
          </div>
          <div class="field">
            <label for="hpAttributeMultiplier${index}">${t("hp.attribute.multiplier", "Multiplier")}</label>
            <input type="number" id="hpAttributeMultiplier${index}" step="any" value="${term.multiplier}" data-hp-term-multiplier="${index}">
          </div>
          ${single || visibleTerms.length <= 2 ? "" : `<button class="btn danger small hp-remove-derived-term" type="button" data-hp-remove-term="${index}">${t("common.remove", "Remove")}</button>`}
        </div>
      `).join("");
      document.querySelectorAll("[data-hp-term-index], [data-hp-term-multiplier]").forEach((control) => {
        control.addEventListener("change", () => {
          readAttributeTermInputs();
          updateAttributeFormulaPreview();
          saveHitPoints();
        });
      });
      document.querySelectorAll("[data-hp-remove-term]").forEach((button) => {
        button.addEventListener("click", () => {
          readAttributeTermInputs();
          attributeDerivedTerms.splice(Number(button.dataset.hpRemoveTerm), 1);
          renderAttributeDerivedTerms();
          saveHitPoints();
        });
      });
      updateAttributeFormulaPreview();
    };

    const updateHPSystemControls = () => {
      const derived = document.getElementById("hpSystemAttributeDerived").checked;
      document.getElementById("hpIndependentSettings").classList.toggle("hidden", derived);
      document.getElementById("hpAttributeDerivedSettings").classList.toggle("hidden", !derived);
      if (derived) {
        renderAttributeDerivedTerms();
      } else {
        updateMethodControls();
      }
    };

    saveHitPoints = () => {
      readAttributeTermInputs();
      const attributeSystem = document.getElementById("hpSystemAttributeDerived").checked;
      const noGain = document.getElementById("hpNoGain").checked;
      const payload = {
        hpGainMethod: attributeSystem
          ? "attribute_derived"
          : noGain ? "fixed" : document.getElementById("hpGainMethod").value,
        fixedHPPerLevel: noGain ? 0 : Number(document.getElementById("hpFixedPerLevel").value),
        allCharactersUseSameFixedGain: noGain || document.getElementById("hpAllCharactersSameFixedGain").checked,
        allCharactersUseSameHitDice: document.getElementById("hpAllCharactersSameDice").checked,
        hitDieSides: Number(document.getElementById("hpHitDieSides").value || 0),
        hitDieCount: Math.max(1, Number(document.getElementById("hpHitDieCount").value || 1)),
        hitDieModifier: Number(document.getElementById("hpHitDieModifier").value || 0),
        minimumHPPerLevel: noGain ? 0 : Number(document.getElementById("hpMinimumPerLevel").value),
        firstLevelMaxHP: !attributeSystem && !noGain
          && document.getElementById("hpGainMethod").value === "rolled"
          && document.getElementById("hpFirstLevelMax").value === "true",
        firstLevelBonusHP: Number(document.getElementById(noGain ? "hpBaseHitPoints" : "hpFirstLevelBonus").value),
        attributeDerivationMode: selectedDerivationMode(),
        attributeDerivedDirectAttributeId: document.getElementById("hpDirectAttribute").value,
        attributeDerivedBaseValue: Number(document.getElementById("hpAttributeBaseValue").value || 0),
        attributeDerivedDivisor: Number(document.getElementById("hpAttributeDivisor").value || 1),
        attributeDerivedRoundingMethod: document.getElementById("hpAttributeRounding").value,
        attributeDerivedTerms: attributeDerivedTerms.map((term) => ({
          attributeId: term.attributeId,
          multiplier: term.multiplier,
        })),
      };
      hitPointSavePromise = hitPointSavePromise.catch(() => false).then(async () => {
        try {
          await api("POST", `/api/drafts/${state.draftId}/hit-points`, payload);
          markSaved(t("web.toast.hp_updated", "Hit points updated"));
          return true;
        } catch (error) {
          showToast(error.message);
          return false;
        }
      });
      return hitPointSavePromise;
    };

    updateHPSystemControls();
    document.querySelectorAll('input[name="hpSystem"]').forEach((radio) => {
      radio.addEventListener("change", () => {
        updateHPSystemControls();
        saveHitPoints();
      });
    });
    document.getElementById("hpNoGain").addEventListener("change", (event) => {
      const noGain = event.target.checked;
      document.getElementById("hpNoGainSettings").classList.toggle("hidden", !noGain);
      document.getElementById("hpProgressiveStartingSettings").classList.toggle("hidden", noGain);
      document.getElementById("hpGainSettings").classList.toggle("hidden", noGain);
      if (noGain) {
        document.getElementById("hpAllCharactersSameFixedGain").checked = true;
        document.getElementById("hpBaseHitPoints").value = document.getElementById("hpFirstLevelBonus").value;
      } else {
        document.getElementById("hpFirstLevelBonus").value = document.getElementById("hpBaseHitPoints").value;
        if (document.getElementById("hpGainMethod").value === "fixed"
            && Number(document.getElementById("hpFixedPerLevel").value) === 0) {
          document.getElementById("hpFixedPerLevel").value = "1";
        }
        updateMethodControls();
      }
      saveHitPoints();
    });
    document.getElementById("hpBaseHitPoints").addEventListener("change", saveHitPoints);
    document.getElementById("hpGainMethod").addEventListener("change", () => {
      updateMethodControls();
      saveHitPoints();
    });
    document.getElementById("hpFixedPerLevel").addEventListener("change", saveHitPoints);
    document.getElementById("hpAllCharactersSameFixedGain").addEventListener("change", () => {
      updateMethodControls();
      saveHitPoints();
    });
    document.getElementById("hpAllCharactersSameDice").addEventListener("change", () => {
      updateMethodControls();
      saveHitPoints();
    });
    document.getElementById("hpHitDieSides").addEventListener("change", () => {
      updateDiceExpressionPreview();
      saveHitPoints();
    });
    document.getElementById("hpHitDieCount").addEventListener("change", () => {
      const input = document.getElementById("hpHitDieCount");
      input.value = String(Math.max(1, Math.trunc(Number(input.value || 1))));
      updateDiceExpressionPreview();
      saveHitPoints();
    });
    document.getElementById("hpHitDieModifier").addEventListener("change", () => {
      updateDiceExpressionPreview();
      saveHitPoints();
    });
    document.getElementById("hpMinimumPerLevel").addEventListener("change", saveHitPoints);
    document.getElementById("hpFirstLevelMax").addEventListener("change", saveHitPoints);
    document.getElementById("hpFirstLevelBonus").addEventListener("change", saveHitPoints);
    document.getElementById("hpDirectAttribute").addEventListener("change", saveHitPoints);
    document.querySelectorAll('input[name="hpDerivationMode"]').forEach((radio) => {
      radio.addEventListener("change", () => {
        readAttributeTermInputs();
        renderAttributeDerivedTerms();
        saveHitPoints();
      });
    });
    ["hpAttributeBaseValue", "hpAttributeDivisor", "hpAttributeRounding"].forEach((id) => {
      document.getElementById(id).addEventListener("change", () => {
        updateAttributeFormulaPreview();
        saveHitPoints();
      });
    });
    document.getElementById("hpAddAttributeTerm").addEventListener("click", () => {
      readAttributeTermInputs();
      attributeDerivedTerms.push({ attributeId: "", multiplier: 1 });
      renderAttributeDerivedTerms();
    });

    document.getElementById("backToAttributes").addEventListener("click", navigateBackInApp);
    document.getElementById("hpContinue").addEventListener("click", async () => {
      if (document.getElementById("hpSystemAttributeDerived").checked) {
        readAttributeTermInputs();
        const mode = selectedDerivationMode();
        const selectedTerms = mode === "single_formula" ? attributeDerivedTerms.slice(0, 1) : attributeDerivedTerms;
        const selectedIds = selectedTerms.map((term) => term.attributeId).filter(Boolean);
        const valid = mode === "direct"
          ? document.getElementById("hpDirectAttribute").value !== ""
          : mode === "single_formula"
            ? selectedIds.length === 1
            : selectedIds.length >= 2 && new Set(selectedIds).size === selectedIds.length;
        const invalidDivisor = mode !== "direct"
          && Number(document.getElementById("hpAttributeDivisor").value) === 0;
        if (!valid || invalidDivisor) {
          showToast(t("hp.attribute.validation", "Complete the selected Attribute-derived HP formula before continuing."));
          return;
        }
      }
      await saveHitPoints();
      navigateToStep("armor-class");
    });
  } catch (error) {
    showToast(error.message);
  }
}

async function renderArmorClass() {
  if (!ensureDraft()) {
    return;
  }
  setStep("armor-class");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const data = await api("GET", `/api/drafts/${state.draftId}/armor-class`);
    const baseArmorClass = Number(data.baseArmorClass || 10);
    const acAbilityAttributeId = String(data.acAbilityAttributeId || "");
    const attributes = Array.isArray(data.attributes) ? data.attributes : [];

    view.innerHTML = `
      <section class="panel">
        <h1>${t("armorclass.title", "Armor Class")}</h1>
        <div class="grid two">
          <div class="field">
            <label for="acBaseValue">${t("armorclass.base", "Base Armor Class")}</label>
            <input type="number" id="acBaseValue" min="0" step="1" value="${Number.isFinite(baseArmorClass) ? baseArmorClass : 10}">
          </div>
          <div class="field">
            <label for="acAbilityAttr">${t("armorclass.ability.attribute", "AC Attribute")}</label>
            <select id="acAbilityAttr"></select>
          </div>
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToHP" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn" id="acContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    const populateAbilityAttributes = () => {
      const select = document.getElementById("acAbilityAttr");
      if (!select) {
        return;
      }
      const options = [`<option value="">${t("common.none", "None")}</option>`];
      attributes.forEach((attr) => {
        const id = String(attr.id || "");
        const label = String(attr.displayName || attr.name || id || "");
        options.push(`<option value="${escapeHtml(id)}">${escapeHtml(label)}</option>`);
      });
      select.innerHTML = options.join("");
      select.value = acAbilityAttributeId;
    };

    const saveSelection = async () => {
      const selected = {
        baseArmorClass: Number(document.getElementById("acBaseValue").value || 0),
        acAbilityAttributeId: String(document.getElementById("acAbilityAttr").value || "").trim(),
      };
      try {
        await api("POST", `/api/drafts/${state.draftId}/armor-class`, selected);
        markSaved(t("web.toast.armor_class_updated", "Armor class updated"));
      } catch (error) {
        showToast(error.message);
      }
    };

    document.getElementById("acBaseValue").addEventListener("change", saveSelection);
    document.getElementById("acAbilityAttr").addEventListener("change", saveSelection);
    document.getElementById("backToHP").addEventListener("click", navigateBackInApp);
    document.getElementById("acContinue").addEventListener("click", () => {
      navigateToStep("currency");
    });

    populateAbilityAttributes();
  } catch (error) {
    showToast(error.message);
  }
}

async function renderCurrency() {
  if (!ensureDraft()) {
    return;
  }
  setStep("currency");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const data = await api("GET", `/api/drafts/${state.draftId}/currencies`);
    const systemName = String(data.systemName || "");
    const title = systemNameTitle(systemName, t("currency.title", "Currency"));
    const currencies = sortByLabel(data.currencies || [], (currency) => currency.name || currency.displayName || "");
    const startingMoney = data.startingMoney || {};
    const startingMoneyMethod = String(startingMoney.method || "base");
    const startingMoneyBaseAmount = Number(startingMoney.baseAmount || 0);
    const startingMoneyCurrencyId = String(startingMoney.currencyId || "");
    let selectedId = state.currencyId;
    if (!currencies.some((currency) => currency.id === selectedId)) {
      selectedId = currencies.length ? currencies[0].id : "";
    }
    state.currencyId = selectedId;
    const selectedCurrency = currencies.find((currency) => currency.id === selectedId);

    const currencyList = currencies
      .map((currency) => {
        const name = currency.name || t("currency.untitled", "Untitled");
        const count = (currency.denominations || []).length;
        return renderCollectionRow(
          `<button class="btn ghost small" type="button" data-select-currency="${escapeHtml(currency.id)}">${escapeHtml(name)}</button>
           <span class="badge">${count}</span>`,
          [
            collectionEditAction("edit-currency", currency.id),
            collectionRemoveAction("remove-currency", currency.id),
          ]
        );
      })
      .join("");

    const denominations = (selectedCurrency && selectedCurrency.denominations) || [];
    const denomList = denominations
      .map(
        (denom, index) => renderCollectionRow(
          `<span>${escapeHtml(denom.name)} <span class="badge">${denom.value}</span></span>`,
          [
            collectionEditAction("edit-denom", index),
            collectionRemoveAction("remove-denom", index),
          ]
        )
      )
      .join("");

    const startingCurrencyOptions = [`<option value="">${t("common.none", "None")}</option>`]
      .concat(
        currencies.map((currency) => {
          const selected = currency.id === startingMoneyCurrencyId ? " selected" : "";
          return `<option value="${escapeHtml(currency.id || "")}"${selected}>${escapeHtml(
            currency.name || t("currency.untitled", "Untitled")
          )}</option>`;
        })
      )
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${escapeHtml(title)}</h1>
        ${renderSystemNameControls(systemName, t("currency.title", "Currency"))}
        <button class="btn collection-add-button" id="addCurrency" type="button">${t("currency.add", "Add Currency")}</button>
        <div class="list" id="currencyList">
          ${currencyList || `<div class="list-item">${t("currency.none", "No currencies yet.")}</div>`}
        </div>

        <h2 class="collection-editor-heading">${t("currency.denom.section", "Denominations")}</h2>
        <button class="btn collection-add-button" id="addDenom" type="button">${t("currency.denom.add", "Add Denomination")}</button>
        <div class="list" id="denomList">
          ${denomList || `<div class="list-item">${t("currency.denom.none", "No denominations yet.")}</div>`}
        </div>

        <div class="edit-section">
          <h4>${t("money.starting.title", "Starting Money")}</h4>
          <div class="grid two">
            <div class="field">
              <label for="startingMoneyMethod">${t("money.starting.method", "Method")}</label>
              <select id="startingMoneyMethod">
                <option value="base" ${startingMoneyMethod === "base" ? "selected" : ""}>
                  ${t("money.starting.method.base", "Base")}
                </option>
                <option value="class" ${startingMoneyMethod === "class" ? "selected" : ""}>
                  ${t("money.starting.method.class", "Class")}
                </option>
                <option value="trait" ${startingMoneyMethod === "trait" ? "selected" : ""}>
                  ${t("money.starting.method.trait", "Trait")}
                </option>
                <option value="hybrid" ${startingMoneyMethod === "hybrid" ? "selected" : ""}>
                  ${t("money.starting.method.hybrid", "Hybrid")}
                </option>
              </select>
            </div>
            <div class="field">
              <label for="startingMoneyBaseAmount">${t("money.starting.base", "Base Amount")}</label>
              <input type="number" id="startingMoneyBaseAmount" min="0" step="1" value="${escapeHtml(
                String(Math.max(0, startingMoneyBaseAmount))
              )}">
            </div>
          </div>
          <div class="grid two">
            <div class="field">
              <label for="startingMoneyCurrency">${t("money.starting.currency", "Currency")}</label>
              <select id="startingMoneyCurrency">
                ${startingCurrencyOptions}
              </select>
            </div>
          </div>
        </div>

        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToPoints" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn ghost" id="currencyContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    const openCurrencyEditor = (currency = null) => {
      const editing = Boolean(currency);
      const currencyDenominations = editing && Array.isArray(currency.denominations) ? currency.denominations : [];
      const baseEntry = currencyDenominations.find((denom) => Number(denom.value) === 1) || currencyDenominations[0];
      const originalBaseDenomination = baseEntry ? String(baseEntry.name || "") : "";
      currencyCreateModalTitle.textContent = editing ? t("currency.edit", "Edit Currency") : t("currency.add", "Add Currency");
      currencyCreateModalNameLabel.textContent = t("currency.name", "Currency Name");
      currencyCreateModalName.placeholder = t("currency.name.placeholder", "e.g., Gold Standard");
      currencyCreateModalName.value = editing ? String(currency.name || "") : "";
      currencyCreateModalBaseLabel.textContent = t("currency.base_denom", "Base Denomination");
      currencyCreateModalBase.placeholder = t("currency.base_denom.placeholder", "e.g., Copper");
      currencyCreateModalBase.value = originalBaseDenomination;
      currencyCreateModalCancel.textContent = t("common.cancel", "Cancel");
      currencyCreateModalSave.textContent = editing ? t("common.save", "Save") : t("currency.add", "Add Currency");
      currencyCreateModalSave.disabled = false;
      currencyCreateModal.classList.remove("hidden");
      currencyCreateModalName.focus();

      currencyCreateModalCancel.onclick = () => currencyCreateModal.classList.add("hidden");
      currencyCreateModalSave.onclick = async () => {
        const name = currencyCreateModalName.value.trim();
        const baseDenomination = currencyCreateModalBase.value.trim();
        if (!name) {
          showToast(t("common.name.required", "Name is required."));
          return;
        }
        currencyCreateModalSave.disabled = true;
        try {
          const result = await api("POST", `/api/drafts/${state.draftId}/currencies${editing ? "/update" : ""}`, {
            id: editing ? currency.id : "",
            name,
            baseDenomination,
            originalBaseDenomination,
          });
          state.currencyId = editing ? currency.id : result.id || "";
          markSaved(t(editing ? "web.toast.currency_updated" : "web.toast.currency_added", editing ? "Currency updated" : "Currency added"));
          currencyCreateModal.classList.add("hidden");
          renderCurrency();
        } catch (error) {
          showToast(error.message);
          currencyCreateModalSave.disabled = false;
        }
      };
    };

    document.getElementById("addCurrency").addEventListener("click", () => openCurrencyEditor());

    document.querySelectorAll("[data-edit-currency]").forEach((button) => {
      button.addEventListener("click", () => {
        const currency = currencies.find((entry) => entry.id === button.dataset.editCurrency);
        if (currency) {
          openCurrencyEditor(currency);
        }
      });
    });

    document.querySelectorAll("[data-select-currency]").forEach((button) => {
      button.addEventListener("click", () => {
        state.currencyId = button.dataset.selectCurrency;
        renderCurrency();
      });
    });

    document.querySelectorAll("[data-remove-currency]").forEach((button) => {
      button.addEventListener("click", async () => {
        const id = button.dataset.removeCurrency;
        const confirmed = await showConfirm(
          t("currency.remove.confirm", "Remove this currency?"),
          t("common.remove", "Remove")
        );
        if (!confirmed) {
          return;
        }
        try {
          await api("DELETE", `/api/drafts/${state.draftId}/currencies`, { id });
          if (state.currencyId === id) {
            state.currencyId = "";
          }
          markSaved(t("web.toast.currency_removed", "Currency removed"));
          renderCurrency();
        } catch (error) {
          showToast(error.message);
        }
      });
    });

    const openDenominationEditor = (index = -1) => {
      if (!state.currencyId) {
        showToast(t("web.toast.currency_select_required", "Select a currency first."));
        return;
      }
      const editing = Number.isInteger(index) && index >= 0 && index < denominations.length;
      const denomination = editing ? denominations[index] : { name: "", value: 1 };
      denominationCreateModalTitle.textContent = editing
        ? t("currency.denom.edit", "Edit Denomination")
        : t("currency.denom.add", "Add Denomination");
      denominationCreateModalNameLabel.textContent = t("currency.denom.name", "Denomination");
      denominationCreateModalName.placeholder = t("currency.denom.placeholder", "e.g., Silver");
      denominationCreateModalName.value = denomination.name || "";
      denominationCreateModalValueLabel.textContent = t("currency.denom.value", "Value");
      denominationCreateModalValue.value = String(denomination.value || 1);
      denominationCreateModalCancel.textContent = t("common.cancel", "Cancel");
      denominationCreateModalSave.textContent = editing ? t("common.save", "Save") : t("currency.denom.add", "Add Denomination");
      denominationCreateModalSave.disabled = false;
      denominationCreateModal.classList.remove("hidden");
      denominationCreateModalName.focus();

      denominationCreateModalCancel.onclick = () => denominationCreateModal.classList.add("hidden");
      denominationCreateModalSave.onclick = async () => {
        const name = denominationCreateModalName.value.trim();
        const value = Number(denominationCreateModalValue.value);
        if (!name) {
          showToast(t("common.name.required", "Name is required."));
          return;
        }
        if (!(value > 0)) {
          showToast(t("web.toast.denom_value_positive", "Denomination value must be positive."));
          return;
        }
        denominationCreateModalSave.disabled = true;
        try {
          await api("POST", `/api/drafts/${state.draftId}/currencies/denominations${editing ? "/update" : ""}`, {
            currencyId: state.currencyId,
            originalName: editing ? denomination.name : "",
            name,
            value,
          });
          markSaved(t(editing ? "web.toast.denom_updated" : "web.toast.denom_added", editing ? "Denomination updated" : "Denomination added"));
          denominationCreateModal.classList.add("hidden");
          renderCurrency();
        } catch (error) {
          showToast(error.message);
          denominationCreateModalSave.disabled = false;
        }
      };
    };

    document.getElementById("addDenom").addEventListener("click", () => openDenominationEditor());

    document.querySelectorAll("[data-edit-denom]").forEach((button) => {
      button.addEventListener("click", () => openDenominationEditor(Number(button.dataset.editDenom)));
    });

    document.querySelectorAll("[data-remove-denom]").forEach((button) => {
      button.addEventListener("click", async () => {
        if (!state.currencyId) {
          return;
        }
        const denomination = denominations[Number(button.dataset.removeDenom)];
        const name = denomination ? denomination.name : "";
        if (!name) {
          return;
        }
        const confirmed = await showConfirm(
          t("currency.denom.remove.confirm", "Remove this denomination?"),
          t("common.remove", "Remove")
        );
        if (!confirmed) {
          return;
        }
        try {
          await api("DELETE", `/api/drafts/${state.draftId}/currencies/denominations`, {
            currencyId: state.currencyId,
            name,
          });
          markSaved(t("web.toast.denom_removed", "Denomination removed"));
          renderCurrency();
        } catch (error) {
          showToast(error.message);
        }
      });
    });

    let startingMoneySavePromise = Promise.resolve(true);
    const saveStartingMoney = () => {
      const methodElement = document.getElementById("startingMoneyMethod");
      const baseAmountElement = document.getElementById("startingMoneyBaseAmount");
      const currencyElement = document.getElementById("startingMoneyCurrency");
      const payload = {
        method: methodElement ? String(methodElement.value || "base") : "base",
        baseAmount: baseAmountElement ? Number(baseAmountElement.value || 0) : 0,
        currencyId: currencyElement ? String(currencyElement.value || "").trim() : "",
      };
      payload.baseAmount = Math.max(0, Math.trunc(payload.baseAmount));
      if (baseAmountElement) {
        baseAmountElement.value = String(payload.baseAmount);
      }
      startingMoneySavePromise = startingMoneySavePromise.catch(() => false).then(async () => {
        try {
          await api("POST", `/api/drafts/${state.draftId}/currencies/starting-money`, payload);
          markSaved(t("web.toast.currency_saved", "Currency saved"));
          return true;
        } catch (error) {
          showToast(error.message);
          return false;
        }
      });
      return startingMoneySavePromise;
    };

    ["startingMoneyMethod", "startingMoneyBaseAmount", "startingMoneyCurrency"].forEach((id) => {
      document.getElementById(id).addEventListener("change", saveStartingMoney);
    });

    document.getElementById("backToPoints").addEventListener("click", navigateBackInApp);
    document.getElementById("currencyContinue").addEventListener("click", async () => {
      if (await saveStartingMoney()) {
        renderEffectTypes();
      }
    });

    wireSystemNameSave("currencies", () => renderCurrency());
  } catch (error) {
    showToast(error.message);
  }
}

async function renderEffectTypes(openKey = "") {
  if (!ensureDraft()) {
    return;
  }
  setStep("effect-types");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const data = await api("GET", `/api/drafts/${state.draftId}/effect-types`);
    const systemName = String(data.systemName || "");
    const title = systemNameTitle(systemName, t("effecttypes.title", "Affected Systems"));
    const types = sortByLabel(data.types || [], (type) => type.displayName || type.name || type.key || "");
    effectTypeOptions = types;

    const typeMap = {};
    const list = types
      .map((type) => {
        const key = type.key || type.name || "";
        typeMap[key] = type;
        return `
          <div class="list-item">
            <div>
              <strong>${escapeHtml(type.name || t("effects.untitled", "Untitled"))}</strong>
            </div>
            <div>
              ${collectionEditAction("edit-effect-type", key)}
              ${collectionRemoveAction("remove-effect-type", key)}
            </div>
          </div>
        `;
      })
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${escapeHtml(title)}</h1>
        ${renderSystemNameControls(systemName, t("effecttypes.title", "Affected Systems"))}
        <button class="btn" id="addEffectType" type="button">${t("effecttypes.add", "Add Affected System")}</button>
        <div class="list" id="effectTypeList">
          ${list || `<div class="list-item">${t("effecttypes.none", "No affected systems yet.")}</div>`}
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToCurrency" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn ghost" id="effectTypesContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    document.getElementById("addEffectType").addEventListener("click", () => {
      openEffectTypeCreateModal("effect-types");
    });

    document.querySelectorAll("[data-edit-effect-type]").forEach((button) => {
      button.addEventListener("click", () => {
        const key = button.dataset.editEffectType;
        openEffectTypeEditor(typeMap[key]);
      });
    });

    document.querySelectorAll("[data-remove-effect-type]").forEach((button) => {
      button.addEventListener("click", async () => {
        const key = button.dataset.removeEffectType;
        const confirmed = await showConfirm(
          t("effecttypes.remove.confirm", "Remove this affected system?"),
          t("common.remove", "Remove")
        );
        if (!confirmed) {
          return;
        }
        try {
          await api("DELETE", `/api/drafts/${state.draftId}/effect-types`, { key });
          markSaved(t("web.toast.effect_type_removed", "Affected system removed"));
          renderEffectTypes();
        } catch (error) {
          showToast(error.message);
        }
      });
    });

    document.getElementById("backToCurrency").addEventListener("click", navigateBackInApp);
    document.getElementById("effectTypesContinue").addEventListener("click", () => {
      markSaved(t("web.toast.effect_types_saved", "Affected systems saved"));
      renderDamageTypes();
    });
    wireSystemNameSave("effect-types", () => renderEffectTypes());
    if (openKey) {
      const target = typeMap[openKey];
      if (target) {
        openEffectTypeEditor(target);
      }
    }
  } catch (error) {
    showToast(error.message);
  }
}

async function renderStatuses(openId = "") {
  if (!ensureDraft()) {
    return;
  }
  setStep("statuses");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const [data, typeData] = await Promise.all([
      api("GET", `/api/drafts/${state.draftId}/statuses`),
      api("GET", `/api/drafts/${state.draftId}/effect-types`),
    ]);
    const systemName = String(data.systemName || "");
    const title = systemNameTitle(systemName, t("statuses.title", "Statuses"));
    const statuses = sortByLabel(data.statuses || [], (status) => status.name || status.displayName || "");
    effectTypeOptions = sortByLabel(typeData.types || [], (type) => type.displayName || type.name || type.key || "");

    const statusList = statuses
      .map((status) => {
        const affectedSystems = renderEffectTypeBadges(status.effectTypeKeys);
        return `
          <div class="list-item">
            <div>
              <strong>${escapeHtml(status.name || t("statuses.untitled", "Untitled"))}</strong>${affectedSystems}
            </div>
            <div>
              ${collectionEditAction("edit-status", status.id)}
              ${collectionRemoveAction("remove-status", status.id)}
            </div>
          </div>
        `;
      })
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${escapeHtml(title)}</h1>
        ${renderSystemNameControls(systemName, t("statuses.title", "Statuses"))}
        <button class="btn" id="addStatus" type="button">${t("statuses.add", "Add Status")}</button>
        <div class="list" id="statusList">
          ${statusList || `<div class="list-item">${t("statuses.none", "No statuses yet.")}</div>`}
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToDamageTypes" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn ghost" id="statusesContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    document.getElementById("addStatus").addEventListener("click", () => {
      openStatusCreate();
    });

    document.querySelectorAll("[data-edit-status]").forEach((button) => {
      button.addEventListener("click", () => {
        const statusId = button.dataset.editStatus;
        const status = statuses.find((item) => item.id === statusId);
        openStatusEditor(status);
      });
    });

    document.querySelectorAll("[data-remove-status]").forEach((button) => {
      button.addEventListener("click", async () => {
        const id = button.dataset.removeStatus;
        const confirmed = await showConfirm(
          t("statuses.remove.confirm", "Remove this status?"),
          t("common.remove", "Remove")
        );
        if (!confirmed) {
          return;
        }
        try {
          await api("DELETE", `/api/drafts/${state.draftId}/statuses`, { id });
          markSaved(t("web.toast.status_removed", "Status removed"));
          renderStatuses();
        } catch (error) {
          showToast(error.message);
        }
      });
    });

    document.getElementById("backToDamageTypes").addEventListener("click", navigateBackInApp);
    document.getElementById("statusesContinue").addEventListener("click", () => {
      markSaved(t("web.toast.statuses_saved", "Statuses saved"));
      renderEffects();
    });
    wireSystemNameSave("statuses", () => renderStatuses());
    if (openId) {
      const target = statuses.find((item) => item.id === openId);
      openStatusEditor(target);
    }
  } catch (error) {
    showToast(error.message);
  }
}

async function renderDamageTypes(openId = "") {
  if (!ensureDraft()) {
    return;
  }
  setStep("damage-types");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const data = await api("GET", `/api/drafts/${state.draftId}/damage-types`);
    const systemName = String(data.systemName || "");
    const title = systemNameTitle(systemName, t("damagetypes.title", "Damage Types"));
    const damageTypes = sortByLabel(data.damageTypes || [], (type) => type.name || type.displayName || "");
    damageTypeOptions = damageTypes.slice();
    const damageTypeList = damageTypes
      .map(
        (damageType) => `
          <div class="list-item">
            <div>
              <strong>${escapeHtml(damageType.name || t("damagetypes.untitled", "Untitled"))}</strong>
            </div>
            <div>
              ${collectionEditAction("edit-damage-type", damageType.id)}
              ${collectionRemoveAction("remove-damage-type", damageType.id)}
            </div>
          </div>
        `
      )
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${escapeHtml(title)}</h1>
        ${renderSystemNameControls(systemName, t("damagetypes.title", "Damage Types"))}
        <button class="btn" id="addDamageType" type="button">${t("damagetypes.add", "Add Damage Type")}</button>
        <div class="list" id="damageTypeList">
          ${damageTypeList || `<div class="list-item">${t("damagetypes.none", "No damage types yet.")}</div>`}
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToEffectTypes" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn ghost" id="damageTypesContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    document.getElementById("addDamageType").addEventListener("click", () => {
      openDamageTypeCreate();
    });
    document.querySelectorAll("[data-edit-damage-type]").forEach((button) => {
      button.addEventListener("click", () => {
        const damageTypeId = button.dataset.editDamageType;
        const damageType = damageTypes.find((item) => item.id === damageTypeId);
        openDamageTypeEditor(damageType);
      });
    });
    document.querySelectorAll("[data-remove-damage-type]").forEach((button) => {
      button.addEventListener("click", async () => {
        const id = button.dataset.removeDamageType;
        const confirmed = await showConfirm(
          t("damagetypes.remove.confirm", "Remove this damage type?"),
          t("common.remove", "Remove")
        );
        if (!confirmed) {
          return;
        }
        try {
          await api("DELETE", `/api/drafts/${state.draftId}/damage-types`, { id });
          damageTypeOptions = [];
          markSaved(t("web.toast.damage_type_removed", "Damage type removed"));
          renderDamageTypes();
        } catch (error) {
          showToast(error.message);
        }
      });
    });
    document.getElementById("backToEffectTypes").addEventListener("click", navigateBackInApp);
    document.getElementById("damageTypesContinue").addEventListener("click", () => {
      markSaved(t("web.toast.damage_types_saved", "Damage types saved"));
      renderStatuses();
    });
    wireSystemNameSave("damage-types", () => renderDamageTypes());
    if (openId) {
      const target = damageTypes.find((item) => item.id === openId);
      openDamageTypeEditor(target);
    }
  } catch (error) {
    showToast(error.message);
  }
}

async function renderEffects(openId = "") {
  if (!ensureDraft()) {
    return;
  }
  setStep("effects");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const [data, typeData, damageTypeData] = await Promise.all([
      api("GET", `/api/drafts/${state.draftId}/effects`),
      api("GET", `/api/drafts/${state.draftId}/effect-types`),
      api("GET", `/api/drafts/${state.draftId}/damage-types`),
    ]);
    const systemName = String(data.systemName || "");
    const title = systemNameTitle(systemName, t("effects.title", "Effects"));
    const effects = sortByLabel(data.effects || [], (effect) => effect.name || effect.displayName || "");
    effectTypeOptions = sortByLabel(typeData.types || [], (type) => type.displayName || type.name || type.key || "");
    damageTypeOptions = sortByLabel(damageTypeData.damageTypes || [], (type) => type.displayName || type.name || "");
    const effectList = effects
      .map(
        (effect) => {
          const damageTypeLabel = resolveDamageTypeLabel(effect.damageTypeId);
          const damageType = damageTypeLabel ? ` <span class="badge">${escapeHtml(damageTypeLabel)}</span>` : "";
          const affectedSystems = renderEffectTypeBadges(effect.effectTypeKeys);
          return `
            <div class="list-item">
              <div>
                <strong>${escapeHtml(effect.name || t("effects.untitled", "Untitled"))}</strong>${affectedSystems}${damageType}
              </div>
              <div>
                ${collectionEditAction("edit-effect", effect.id)}
                ${collectionRemoveAction("remove-effect", effect.id)}
              </div>
            </div>
          `;
        }
      )
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${escapeHtml(title)}</h1>
        ${renderSystemNameControls(systemName, t("effects.title", "Effects"))}
        <button class="btn" id="addEffect" type="button">${t("effects.add", "Add Effect")}</button>
        <div class="list" id="effectList">
          ${effectList || `<div class="list-item">${t("effects.none", "No effects yet.")}</div>`}
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToStatuses" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn ghost" id="effectsContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    document.getElementById("addEffect").addEventListener("click", () => {
      openEffectCreateModal("effects", "", "", state.lastEffectTypeKeys);
    });

    document.querySelectorAll("[data-edit-effect]").forEach((button) => {
      button.addEventListener("click", () => {
        const effectId = button.dataset.editEffect;
        const effect = effects.find((item) => item.id === effectId);
        openEffectEditor(effect);
      });
    });

    document.querySelectorAll("[data-remove-effect]").forEach((button) => {
      button.addEventListener("click", async () => {
        const id = button.dataset.removeEffect;
        const confirmed = await showConfirm(
          t("effects.remove.confirm", "Remove this effect?"),
          t("common.remove", "Remove")
        );
        if (!confirmed) {
          return;
        }
        try {
          await api("DELETE", `/api/drafts/${state.draftId}/effects`, { id });
          markSaved(t("web.toast.effect_removed", "Effect removed"));
          renderEffects();
        } catch (error) {
          showToast(error.message);
        }
      });
    });

    document.getElementById("backToStatuses").addEventListener("click", navigateBackInApp);
    document.getElementById("effectsContinue").addEventListener("click", () => {
      markSaved(t("web.toast.effects_saved", "Effects saved"));
      renderEquipment();
    });
    wireSystemNameSave("effects", () => renderEffects());
    if (openId) {
      const target = effects.find((item) => item.id === openId);
      openEffectEditor(target);
    }
  } catch (error) {
    showToast(error.message);
  }
}

async function renderEquipment(openId = "") {
  if (!ensureDraft()) {
    return;
  }
  setStep("equipment");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const [data, damageTypeData] = await Promise.all([
      api("GET", `/api/drafts/${state.draftId}/equipment`),
      api("GET", `/api/drafts/${state.draftId}/damage-types`),
    ]);
    const systemName = String(data.systemName || "");
    const title = systemNameTitle(systemName, t("equipment.title", "Equipment"));
    const equipment = sortByLabel(data.equipment || [], (item) => item.name || item.displayName || "");
    damageTypeOptions = sortByLabel(damageTypeData.damageTypes || [], (type) => type.displayName || type.name || "");
    weightUnitOptions = Array.isArray(data.weightUnits) ? data.weightUnits.slice() : [];
    weightSystem = String(data.weightSystem || "");

    const equipmentList = equipment
      .map(
        (item) => {
          const damageTypeLabel = resolveDamageTypeLabel(item.damageTypeId);
          const damageType = damageTypeLabel ? ` <span class="badge">${escapeHtml(damageTypeLabel)}</span>` : "";
          return `
            <div class="list-item">
              <div>
                <strong>${escapeHtml(item.name || t("equipment.untitled", "Untitled"))}</strong>${damageType}
              </div>
              <div>
                ${collectionEditAction("edit-equipment", item.id)}
                ${collectionRemoveAction("remove-equipment", item.id)}
              </div>
            </div>
          `;
        }
      )
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${escapeHtml(title)}</h1>
        ${renderSystemNameControls(systemName, t("equipment.title", "Equipment"))}
        <button class="btn" id="addEquipment" type="button">${t("equipment.add", "Add Equipment")}</button>
        <div class="list" id="equipmentList">
          ${equipmentList || `<div class="list-item">${t("equipment.none", "No equipment yet.")}</div>`}
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToEffects" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn ghost" id="equipmentContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    document.getElementById("addEquipment").addEventListener("click", () => {
      openEquipmentCreate();
    });

    document.querySelectorAll("[data-edit-equipment]").forEach((button) => {
      button.addEventListener("click", () => {
        const id = button.dataset.editEquipment;
        const item = equipment.find((entry) => entry.id === id);
        openEquipmentEditor(item);
      });
    });

    document.querySelectorAll("[data-remove-equipment]").forEach((button) => {
      button.addEventListener("click", async () => {
        const id = button.dataset.removeEquipment;
        const confirmed = await showConfirm(
          t("equipment.remove.confirm", "Remove this equipment?"),
          t("common.remove", "Remove")
        );
        if (!confirmed) {
          return;
        }
        try {
          await api("DELETE", `/api/drafts/${state.draftId}/equipment`, { id });
          markSaved(t("web.toast.equipment_removed", "Equipment removed"));
          renderEquipment();
        } catch (error) {
          showToast(error.message);
        }
      });
    });

    document.getElementById("backToEffects").addEventListener("click", navigateBackInApp);
    document.getElementById("equipmentContinue").addEventListener("click", () => {
      markSaved(t("web.toast.equipment_saved", "Equipment saved"));
      renderWeapons();
    });
    wireSystemNameSave("equipment", () => renderEquipment());
    if (openId) {
      const target = equipment.find((item) => item.id === openId);
      openEquipmentEditor(target);
    }
  } catch (error) {
    showToast(error.message);
  }
}

async function renderWeapons(openId = "") {
  if (!ensureDraft()) {
    return;
  }
  setStep("weapons");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const [data, effectsData, damageTypeData] = await Promise.all([
      api("GET", `/api/drafts/${state.draftId}/weapons`),
      api("GET", `/api/drafts/${state.draftId}/effects`),
      api("GET", `/api/drafts/${state.draftId}/damage-types`),
    ]);
    const weapons = sortByLabel(data.weapons || [], (weapon) => weapon.name || weapon.displayName || "");
    weightUnitOptions = Array.isArray(data.weightUnits) ? data.weightUnits.slice() : [];
    weightSystem = String(data.weightSystem || "");
    weaponEffectOptions = sortByLabel(effectsData.effects || [], (effect) => effect.name || effect.displayName || "");
    damageTypeOptions = sortByLabel(damageTypeData.damageTypes || [], (type) => type.displayName || type.name || "");

    const weaponList = weapons
      .map(
        (weapon) => {
          const damageTypeLabel = resolveDamageTypeLabel(weapon.damageTypeId);
          const damageType = damageTypeLabel ? ` <span class="badge">${escapeHtml(damageTypeLabel)}</span>` : "";
          return `
          <div class="list-item">
            <div>
              <strong>${escapeHtml(weapon.name || t("weapons.untitled", "Untitled"))}</strong>${damageType}
            </div>
            <div>
              ${collectionEditAction("edit-weapon", weapon.id)}
              ${collectionRemoveAction("remove-weapon", weapon.id)}
            </div>
          </div>
        `;
        }
      )
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${t("weapons.title", "Weapons")}</h1>
        <button class="btn" id="addWeapon" type="button">${t("weapons.add", "Add Weapon")}</button>
        <div class="list" id="weaponList">
          ${weaponList || `<div class="list-item">${t("weapons.none", "No weapons yet.")}</div>`}
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToEquipment" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn ghost" id="weaponsContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    document.getElementById("addWeapon").addEventListener("click", () => {
      openWeaponCreate();
    });

    document.querySelectorAll("[data-edit-weapon]").forEach((button) => {
      button.addEventListener("click", () => {
        const id = button.dataset.editWeapon;
        const item = weapons.find((entry) => entry.id === id);
        if (!item) {
          return;
        }
        openWeaponEditor(item);
      });
    });

    document.querySelectorAll("[data-remove-weapon]").forEach((button) => {
      button.addEventListener("click", async () => {
        const id = button.dataset.removeWeapon;
        const confirmed = await showConfirm(
          t("weapons.remove.confirm", "Remove this weapon?"),
          t("common.remove", "Remove")
        );
        if (!confirmed) {
          return;
        }
        try {
          await api("DELETE", `/api/drafts/${state.draftId}/weapons`, { id });
          markSaved(t("web.toast.weapon_removed", "Weapon removed"));
          renderWeapons();
        } catch (error) {
          showToast(error.message);
        }
      });
    });

    document.getElementById("backToEquipment").addEventListener("click", navigateBackInApp);
    document.getElementById("weaponsContinue").addEventListener("click", () => {
      markSaved(t("web.toast.weapons_saved", "Weapons saved"));
      renderSkills();
    });
    if (openId) {
      const target = weapons.find((item) => item.id === openId);
      openWeaponEditor(target);
    }
  } catch (error) {
    showToast(error.message);
  }
}

async function renderClasses(openId = "") {
  if (!ensureDraft()) {
    return;
  }
  setStep("classes");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const [data, skillsData, attributesData] = await Promise.all([
      api("GET", `/api/drafts/${state.draftId}/classes`),
      api("GET", `/api/drafts/${state.draftId}/skills`),
      api("GET", `/api/drafts/${state.draftId}/attributes`),
    ]);
    const systemName = String(data.systemName || "");
    const title = systemNameTitle(systemName, t("classes.title", "Classes"));
    const classes = sortByLabel(data.classes || [], (entry) => entry.name || entry.displayName || "");
    classSkillOptions = sortByLabel(skillsData.skills || [], (skill) => skill.displayName || skill.name || "");
    classAttributeOptions = attributesData.attributes || [];
    classHitDieOptions = (data.diceUsed || [])
      .map((value) => Number(value || 0))
      .filter((value) => value > 0)
      .sort((left, right) => left - right);

    const classList = classes
      .map(
        (characterClass) => `
          <div class="list-item">
            <div>
              <strong>${escapeHtml(characterClass.name || t("classes.untitled", "Untitled"))}</strong>
            </div>
            <div>
              ${collectionEditAction("edit-class", characterClass.id)}
              ${collectionRemoveAction("remove-class", characterClass.id)}
            </div>
          </div>
        `
      )
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${escapeHtml(title)}</h1>
        ${renderSystemNameControls(systemName, t("classes.title", "Classes"))}
        <button class="btn" id="addClass" type="button">${t("classes.add", "Add Class")}</button>
        <div class="list" id="classList">
          ${classList || `<div class="list-item">${t("classes.none", "No classes yet.")}</div>`}
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToRaces" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn ghost" id="classesContinue" type="button">${t("common.done", "Done")}</button>
          </div>
        </div>
      </section>
    `;

    document.getElementById("addClass").addEventListener("click", () => {
      openClassCreate();
    });

    document.querySelectorAll("[data-edit-class]").forEach((button) => {
      button.addEventListener("click", () => {
        const id = button.dataset.editClass;
        const item = classes.find((entry) => entry.id === id);
        if (!item) {
          return;
        }
        openClassEditor(item);
      });
    });

    document.querySelectorAll("[data-remove-class]").forEach((button) => {
      button.addEventListener("click", async () => {
        const id = button.dataset.removeClass;
        const confirmed = await showConfirm(
          t("classes.remove.confirm", "Remove this class?"),
          t("common.remove", "Remove")
        );
        if (!confirmed) {
          return;
        }
        try {
          await api("DELETE", `/api/drafts/${state.draftId}/classes`, { id });
          markSaved(t("web.toast.class_removed", "Class removed"));
          renderClasses();
        } catch (error) {
          showToast(error.message);
        }
      });
    });

    document.getElementById("backToRaces").addEventListener("click", navigateBackInApp);
    document.getElementById("classesContinue").addEventListener("click", async () => {
      const confirmed = await showConfirm(
        t("web.download.confirm", "Download your ruleset now?"),
        t("web.download.cta", "Download")
      );
      if (!confirmed) {
        return;
      }
      await downloadDraft();
    });
    wireSystemNameSave("classes", () => renderClasses());
    if (openId) {
      const target = classes.find((item) => item.id === openId);
      openClassEditor(target);
    }
  } catch (error) {
    showToast(error.message);
  }
}

async function renderSkills(openId = "") {
  if (!ensureDraft()) {
    return;
  }
  setStep("skills");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const [skillData, effectsData, attributesData, categoriesData] = await Promise.all([
      api("GET", `/api/drafts/${state.draftId}/skills`),
      api("GET", `/api/drafts/${state.draftId}/effects`),
      api("GET", `/api/drafts/${state.draftId}/attributes`),
      api("GET", `/api/drafts/${state.draftId}/skill-categories`),
    ]);
    const systemName = String(skillData.systemName || "");
    const title = systemNameTitle(systemName, t("skills.title", "Skills"));
    const skills = sortByLabel(skillData.skills || [], (skill) => skill.name || skill.displayName || "");
    const progression = skillData.progression || {};
    let skillPointsByLevel = Array.isArray(progression.skillPointsByLevel)
      ? progression.skillPointsByLevel
          .map((entry) => ({
            level: Number(entry.level || 0),
            points: Number(entry.points || 0),
          }))
          .filter((entry) => entry.level > 0)
      : [];
    skillPointsByLevel.sort((left, right) => left.level - right.level);
    const progressionType = String(progression.skillPointProgression || "byClass");
    const progressionBase = Number(progression.baseSkillPointsPerLevel || 0);
    const progressionMin = Number(progression.minimumSkillPointsPerLevel || 0);
    const progressionModInt = progression.skillPointsModifiedByInt !== false;
    const progressionSameAll = progression.skillPointsSameAllLevels !== false;
    skillEffectOptions = sortByLabel(effectsData.effects || [], (effect) => effect.name || effect.displayName || "");
    skillAbilityOptions = attributesData.attributes || [];
    skillCategoryOptions = sortByLabel(
      categoriesData.categories || [],
      (category) => category.displayName || category.name || category.key || ""
    );

    const skillList = skills
      .map((skill) => {
        const categoryLabel = resolveSkillCategoryLabel(skill.category);
        const category = categoryLabel ? ` <span class="badge">${escapeHtml(categoryLabel)}</span>` : "";
        return `
          <div class="list-item">
            <div>
              <strong>${escapeHtml(skill.name || t("skills.untitled", "Untitled"))}</strong>${category}
            </div>
            <div>
              ${collectionEditAction("edit-skill", skill.id)}
              ${collectionRemoveAction("remove-skill", skill.id)}
            </div>
          </div>
        `;
      })
      .join("");

    const skillPointRows = skillPointsByLevel
      .map((entry, index) => {
        const levelLabel = t("classes.skill_points.level.label", "Level {0}").replace("{0}", String(entry.level));
        return renderCollectionRow(
          `<div><strong>${escapeHtml(levelLabel)}</strong>: ${escapeHtml(String(entry.points))}</div>`,
          [
            collectionEditAction("edit-skill-points-level", index),
            collectionRemoveAction("remove-skill-points-level", index),
          ]
        );
      })
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${escapeHtml(title)}</h1>
        ${renderSystemNameControls(systemName, t("skills.title", "Skills"))}
        <div class="edit-section">
          <h4>${t("skills.progression.type", "Skill Point Progression")}</h4>
          <div class="grid two">
            <div class="field">
              <label for="skillPointProgressionType">${t("skills.progression.type", "Skill Point Progression")}</label>
              <select id="skillPointProgressionType">
                <option value="byClass">${t("skills.progression.by_class", "By Class")}</option>
                <option value="fixed">${t("skills.progression.fixed", "Fixed")}</option>
                <option value="intelligence">${t("skills.progression.intelligence", "Intelligence Modified")}</option>
                <option value="custom">${t("skills.progression.custom", "Custom")}</option>
              </select>
            </div>
            <div class="field">
              <label for="skillPointBase">${t("skills.progression.base", "Base Points per Level")}</label>
              <input type="number" id="skillPointBase" min="0" max="100" step="1" value="${escapeHtml(
                String(Math.max(0, progressionBase))
              )}">
            </div>
          </div>
          <div class="grid two">
            <div class="field">
              <label for="skillPointMinimum">${t("skills.progression.minimum", "Minimum per Level")}</label>
              <input type="number" id="skillPointMinimum" min="0" max="100" step="1" value="${escapeHtml(
                String(Math.max(0, progressionMin))
              )}">
            </div>
            <div class="field">
              <label for="skillPointModInt">${t("skills.progression.mod_int", "Apply Intelligence Modifier")}</label>
              <input type="checkbox" id="skillPointModInt" ${progressionModInt ? "checked" : ""}>
            </div>
          </div>
          <div class="field">
            <label for="skillPointSameAll">${t("classes.skill_points.same_all", "Same at all levels")}</label>
            <input type="checkbox" id="skillPointSameAll" ${progressionSameAll ? "checked" : ""}>
          </div>
          <div class="grid two" id="skillPointLevelGrid">
            <div class="field">
              <label for="skillPointLevelSelect">${t("classes.skill_points.level", "Level")}</label>
              <select id="skillPointLevelSelect"></select>
            </div>
            <div class="field">
              <label for="skillPointLevelValue">${t("classes.skill_points.value", "Points")}</label>
              <input type="number" id="skillPointLevelValue" min="0" max="100" step="1" value="0">
            </div>
            <div class="field">
              <label>&nbsp;</label>
              <button class="btn" id="skillPointLevelAdd" type="button">${t("classes.skill_points.add", "Add Level")}</button>
            </div>
          </div>
          <div class="list" id="skillPointLevelList">
            ${skillPointRows || `<div class="list-item">${t("classes.skill_points.none", "No level-specific values.")}</div>`}
          </div>
        </div>
        <button class="btn" id="addSkill" type="button">${t("skills.add", "Add Skill")}</button>
        <div class="list" id="skillList">
          ${skillList || `<div class="list-item">${t("skills.none", "No skills yet.")}</div>`}
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToWeapons" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn ghost" id="skillsContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    document.getElementById("addSkill").addEventListener("click", async () => {
      await openSkillCreateModal("skills", "", "");
    });

    const skillPointProgressionType = document.getElementById("skillPointProgressionType");
    const skillPointBase = document.getElementById("skillPointBase");
    const skillPointMinimum = document.getElementById("skillPointMinimum");
    const skillPointModInt = document.getElementById("skillPointModInt");
    const skillPointSameAll = document.getElementById("skillPointSameAll");
    const skillPointLevelGrid = document.getElementById("skillPointLevelGrid");
    const skillPointLevelSelect = document.getElementById("skillPointLevelSelect");
    const skillPointLevelValue = document.getElementById("skillPointLevelValue");
    const skillPointLevelAdd = document.getElementById("skillPointLevelAdd");

    let skillPointSavePromise = Promise.resolve();
    const saveSkillProgressionData = () => {
      const payload = {
        skillPointProgression: String(skillPointProgressionType.value || "byClass"),
        baseSkillPointsPerLevel: Math.max(0, Math.trunc(Number(skillPointBase.value || 0))),
        skillPointsModifiedByInt: Boolean(skillPointModInt.checked),
        minimumSkillPointsPerLevel: Math.max(0, Math.trunc(Number(skillPointMinimum.value || 0))),
        skillPointsSameAllLevels: Boolean(skillPointSameAll.checked),
        skillPointsByLevel: skillPointsByLevel.slice(),
      };
      skillPointSavePromise = skillPointSavePromise
        .catch(() => undefined)
        .then(() => api("POST", `/api/drafts/${state.draftId}/skills/progression`, payload));
      return skillPointSavePromise;
    };

    const renderSkillPointLevelOptions = () => {
      const levelCap = 20;
      const options = [`<option value="">${t("classes.skill_points.level.select", "Select Level")}</option>`]
        .concat(
          Array.from({ length: levelCap }, (_, index) => {
            const level = index + 1;
            return `<option value="${level}">${t("classes.skill_points.level.label", "Level {0}").replace(
              "{0}",
              String(level)
            )}</option>`;
          })
        )
        .join("");
      skillPointLevelSelect.innerHTML = options;
    };

    const renderSkillPointLevels = () => {
      const rows = skillPointsByLevel
        .map((entry, index) => {
          const levelLabel = t("classes.skill_points.level.label", "Level {0}").replace("{0}", String(entry.level));
          return renderCollectionRow(
            `<div><strong>${escapeHtml(levelLabel)}</strong>: ${escapeHtml(String(entry.points))}</div>`,
            [
              collectionEditAction("edit-skill-points-level", index),
              collectionRemoveAction("remove-skill-points-level", index),
            ]
          );
        })
        .join("");
      const list = document.getElementById("skillPointLevelList");
      list.innerHTML = rows || `<div class="list-item">${t("classes.skill_points.none", "No level-specific values.")}</div>`;
      list.querySelectorAll("[data-edit-skill-points-level], [data-remove-skill-points-level]").forEach((button) => {
        button.addEventListener("click", async () => {
          const index = Number(button.dataset.editSkillPointsLevel ?? button.dataset.removeSkillPointsLevel);
          if (Number.isNaN(index) || index < 0) {
            return;
          }
          if (button.hasAttribute("data-edit-skill-points-level")) {
            const entry = skillPointsByLevel[index];
            if (!entry) {
              return;
            }
            skillPointLevelSelect.value = String(entry.level);
            skillPointLevelValue.value = String(entry.points);
            skillPointLevelSelect.focus();
            return;
          }
          const confirmed = await showConfirm(
            t("common.remove.confirm", "Remove selected item?"),
            t("common.remove", "Remove")
          );
          if (!confirmed) {
            return;
          }
          skillPointsByLevel.splice(index, 1);
          renderSkillPointLevels();
          try {
            await saveSkillProgressionData();
            markSaved(t("web.toast.skills_saved", "Skills saved"));
          } catch (error) {
            showToast(error.message);
          }
        });
      });
    };

    const updateSkillPointControls = () => {
      const type = String(skillPointProgressionType.value || "byClass");
      const classMode = type === "byClass";
      const sameAll = Boolean(skillPointSameAll.checked);
      const levelMode = !classMode && !sameAll;
      skillPointBase.disabled = classMode;
      skillPointMinimum.disabled = classMode;
      skillPointModInt.disabled = classMode;
      skillPointSameAll.disabled = classMode;
      skillPointLevelGrid.classList.toggle("hidden", !levelMode);
      document.getElementById("skillPointLevelList").classList.toggle("hidden", !levelMode);
    };

    renderSkillPointLevelOptions();
    renderSkillPointLevels();
    skillPointProgressionType.value = progressionType || "byClass";
    updateSkillPointControls();

    skillPointProgressionType.addEventListener("change", async () => {
      updateSkillPointControls();
      try {
        await saveSkillProgressionData();
        markSaved(t("web.toast.skills_saved", "Skills saved"));
      } catch (error) {
        showToast(error.message);
      }
    });
    skillPointModInt.addEventListener("change", async () => {
      try {
        await saveSkillProgressionData();
        markSaved(t("web.toast.skills_saved", "Skills saved"));
      } catch (error) {
        showToast(error.message);
      }
    });
    [skillPointBase, skillPointMinimum].forEach((input) => {
      input.addEventListener("change", async () => {
        try {
          await saveSkillProgressionData();
          markSaved(t("web.toast.skills_saved", "Skills saved"));
        } catch (error) {
          showToast(error.message);
        }
      });
    });
    skillPointSameAll.addEventListener("change", async () => {
      updateSkillPointControls();
      try {
        await saveSkillProgressionData();
        markSaved(t("web.toast.skills_saved", "Skills saved"));
      } catch (error) {
        showToast(error.message);
      }
    });
    skillPointLevelAdd.addEventListener("click", () => {
      const level = Number(skillPointLevelSelect.value || 0);
      const points = Math.max(0, Number(skillPointLevelValue.value || 0));
      if (!level) {
        return;
      }
      const existing = skillPointsByLevel.find((entry) => entry.level === level);
      if (existing) {
        existing.points = points;
      } else {
        skillPointsByLevel.push({ level, points });
      }
      skillPointsByLevel.sort((left, right) => left.level - right.level);
      renderSkillPointLevels();
      saveSkillProgressionData()
        .then(() => markSaved(t("web.toast.skills_saved", "Skills saved")))
        .catch((error) => showToast(error.message));
    });
    document.querySelectorAll("[data-edit-skill]").forEach((button) => {
      button.addEventListener("click", () => {
        const skillId = button.dataset.editSkill;
        const skill = skills.find((item) => item.id === skillId);
        openSkillEditor(skill);
      });
    });

    document.querySelectorAll("[data-remove-skill]").forEach((button) => {
      button.addEventListener("click", async () => {
        const id = button.dataset.removeSkill;
        const confirmed = await showConfirm(
          t("common.remove.confirm", "Remove selected item?"),
          t("common.remove", "Remove")
        );
        if (!confirmed) {
          return;
        }
        try {
          await api("DELETE", `/api/drafts/${state.draftId}/skills`, { id });
          markSaved(t("web.toast.skill_removed", "Skill removed"));
          renderSkills();
        } catch (error) {
          showToast(error.message);
        }
      });
    });

    document.getElementById("backToWeapons").addEventListener("click", navigateBackInApp);
    document.getElementById("skillsContinue").addEventListener("click", async () => {
      try {
        await saveSkillProgressionData();
        markSaved(t("web.toast.skills_saved", "Skills saved"));
        renderSpells();
      } catch (error) {
        showToast(error.message);
      }
    });
    wireSystemNameSave("skills", () => renderSkills());
    if (openId) {
      const target = skills.find((item) => item.id === openId);
      openSkillEditor(target);
    }
  } catch (error) {
    showToast(error.message);
  }
}

async function renderSpells(openId = "") {
  if (!ensureDraft()) {
    return;
  }
  setStep("spells");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const [spellData, effectsData, damageTypeData] = await Promise.all([
      api("GET", `/api/drafts/${state.draftId}/spells`),
      api("GET", `/api/drafts/${state.draftId}/effects`),
      api("GET", `/api/drafts/${state.draftId}/damage-types`),
    ]);
    const systemName = String(spellData.systemName || "");
    const title = systemNameTitle(systemName, t("spells.title", "Spells"));
    const spells = sortByLabel(spellData.spells || [], (spell) => spell.name || spell.displayName || "");
    spellEffectOptions = sortByLabel(effectsData.effects || [], (effect) => effect.name || effect.displayName || "");
    damageTypeOptions = sortByLabel(damageTypeData.damageTypes || [], (type) => type.displayName || type.name || "");

    const spellList = spells
      .map((spell) => {
        const level = Number(spell.level || 0);
        const school = spell.school ? ` <span class="badge">${escapeHtml(spell.school)}</span>` : "";
        const damageTypeLabel = resolveDamageTypeLabel(spell.damageTypeId);
        const damageType = damageTypeLabel ? ` <span class="badge">${escapeHtml(damageTypeLabel)}</span>` : "";
        return `
          <div class="list-item">
            <div>
              <strong>${escapeHtml(spell.name || t("spells.untitled", "Untitled"))}</strong>
              <span class="badge">L${level}</span>${school}${damageType}
            </div>
            <div>
              ${collectionEditAction("edit-spell", spell.id)}
              ${collectionRemoveAction("remove-spell", spell.id)}
            </div>
          </div>
        `;
      })
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${escapeHtml(title)}</h1>
        ${renderSystemNameControls(systemName, t("spells.title", "Spells"))}
        <button class="btn" id="addSpell" type="button">${t("spells.add", "Add Spell")}</button>
        <div class="list" id="spellList">
          ${spellList || `<div class="list-item">${t("spells.none", "No spells yet.")}</div>`}
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToSkills" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn ghost" id="spellsContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    document.getElementById("addSpell").addEventListener("click", async () => {
      await openSpellCreateModal();
    });

    document.querySelectorAll("[data-edit-spell]").forEach((button) => {
      button.addEventListener("click", async () => {
        const spellId = button.dataset.editSpell;
        const spell = spells.find((item) => item.id === spellId);
        await openSpellEditor(spell);
      });
    });

    document.querySelectorAll("[data-remove-spell]").forEach((button) => {
      button.addEventListener("click", async () => {
        const id = button.dataset.removeSpell;
        const confirmed = await showConfirm(
          t("common.remove.confirm", "Remove selected item?"),
          t("common.remove", "Remove")
        );
        if (!confirmed) {
          return;
        }
        try {
          await api("DELETE", `/api/drafts/${state.draftId}/spells`, { id });
          markSaved(t("web.toast.spell_removed", "Spell removed"));
          renderSpells();
        } catch (error) {
          showToast(error.message);
        }
      });
    });

    document.getElementById("backToSkills").addEventListener("click", navigateBackInApp);
    document.getElementById("spellsContinue").addEventListener("click", () => {
      markSaved(t("web.toast.spells_saved", "Spells saved"));
      renderPantheons();
    });
    wireSystemNameSave("spells", () => renderSpells());
    if (openId) {
      const target = spells.find((item) => item.id === openId);
      await openSpellEditor(target);
    }
  } catch (error) {
    showToast(error.message);
  }
}

function getSelectedValues(select) {
  return Array.from((select && select.selectedOptions) || []).map((option) => String(option.value || ""));
}

async function renderPantheons(openId = "") {
  if (!ensureDraft()) {
    return;
  }
  setStep("pantheons");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const data = await api("GET", `/api/drafts/${state.draftId}/pantheons`);
    const systemName = String(data.systemName || "");
    const title = systemNameTitle(systemName, t("pantheons.title", "Pantheons"));
    const pantheons = sortByLabel(data.pantheons || [], (pantheon) => pantheon.name || "");
    const deities = sortByLabel(data.deities || [], (deity) => deity.name || "");
    const pantheonMap = {};
    const deityOptions = deities
      .map((deity) => `<option value="${escapeHtml(deity.id)}">${escapeHtml(deity.name)}</option>`)
      .join("");
    const list = pantheons
      .map((pantheon) => {
        pantheonMap[pantheon.id] = pantheon;
        const deityNames = deities
          .filter((deity) => (pantheon.deityIds || []).includes(deity.id))
          .map((deity) => deity.name)
          .join(", ");
        return `
          <div class="list-item">
            <div>
              <strong>${escapeHtml(pantheon.name || t("pantheons.untitled", "Untitled Pantheon"))}</strong>
              <div class="badge">${escapeHtml(deityNames || t("common.none", "None"))}</div>
            </div>
            <div class="actions">
              ${collectionEditAction("edit-pantheon", pantheon.id)}
              ${collectionRemoveAction("remove-pantheon", pantheon.id)}
            </div>
          </div>
        `;
      })
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${escapeHtml(title)}</h1>
        ${renderSystemNameControls(systemName, t("pantheons.title", "Pantheons"))}
        <div class="grid two">
          <div class="field">
            <label>${t("pantheons.name", "Pantheon Name")}</label>
            <input type="text" id="pantheonName" maxlength="80">
          </div>
          <div class="field">
            <label>${t("pantheons.deities", "Deities")}</label>
            <select id="pantheonDeities" multiple size="5">${deityOptions}</select>
          </div>
        </div>
        <div class="field">
          <label>${t("common.description", "Description")}</label>
          <textarea id="pantheonDescription"></textarea>
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="pantheonClear" type="button">${t("common.cancel", "Cancel")}</button>
          </div>
          <div class="right">
            <button class="btn" id="pantheonSave" type="button">${t("common.save", "Save")}</button>
          </div>
        </div>
        <div class="list" id="pantheonList">${list || `<div class="list-item">${t("pantheons.none", "No pantheons yet.")}</div>`}</div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToSpells" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn" id="pantheonsContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    const nameInput = document.getElementById("pantheonName");
    const descriptionInput = document.getElementById("pantheonDescription");
    const deitiesSelect = document.getElementById("pantheonDeities");
    let editingId = "";
    const clearForm = () => {
      editingId = "";
      nameInput.value = "";
      descriptionInput.value = "";
      Array.from(deitiesSelect.options || []).forEach((option) => {
        option.selected = false;
      });
    };
    const loadForm = (pantheon) => {
      if (!pantheon) {
        clearForm();
        return;
      }
      editingId = String(pantheon.id || "");
      nameInput.value = String(pantheon.name || "");
      descriptionInput.value = String(pantheon.description || "");
      const deityIds = new Set((pantheon.deityIds || []).map((id) => String(id || "")));
      Array.from(deitiesSelect.options || []).forEach((option) => {
        option.selected = deityIds.has(option.value);
      });
      nameInput.focus();
    };

    document.getElementById("pantheonClear").addEventListener("click", clearForm);
    document.getElementById("pantheonSave").addEventListener("click", async () => {
      const name = nameInput.value.trim();
      if (!name) {
        showToast(t("common.name.required", "Name is required."));
        return;
      }
      try {
        if (!editingId) {
          const result = await api("POST", `/api/drafts/${state.draftId}/pantheons`, {
            name,
            description: descriptionInput.value.trim(),
          });
          editingId = String(result.id || "");
        }
        await api("POST", `/api/drafts/${state.draftId}/pantheons/update`, {
          id: editingId,
          name,
          description: descriptionInput.value.trim(),
          deityIds: getSelectedValues(deitiesSelect),
        });
        markSaved(t("web.toast.pantheon_saved", "Pantheon saved"));
        renderPantheons(editingId);
      } catch (error) {
        showToast(error.message);
      }
    });
    document.querySelectorAll("[data-edit-pantheon]").forEach((button) => {
      button.addEventListener("click", () => loadForm(pantheonMap[button.dataset.editPantheon]));
    });
    document.querySelectorAll("[data-remove-pantheon]").forEach((button) => {
      button.addEventListener("click", async () => {
        const confirmed = await showConfirm(
          t("common.remove.confirm", "Remove selected item?"),
          t("common.remove", "Remove")
        );
        if (!confirmed) {
          return;
        }
        try {
          await api("DELETE", `/api/drafts/${state.draftId}/pantheons`, { id: button.dataset.removePantheon });
          markSaved(t("web.toast.pantheon_removed", "Pantheon removed"));
          renderPantheons();
        } catch (error) {
          showToast(error.message);
        }
      });
    });
    document.getElementById("backToSpells").addEventListener("click", navigateBackInApp);
    document.getElementById("pantheonsContinue").addEventListener("click", renderDeities);
    wireSystemNameSave("pantheons", () => renderPantheons());
    if (openId) {
      loadForm(pantheonMap[openId]);
    }
  } catch (error) {
    showToast(error.message);
  }
}

async function renderDeities(openId = "") {
  if (!ensureDraft()) {
    return;
  }
  setStep("deities");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const data = await api("GET", `/api/drafts/${state.draftId}/deities`);
    const systemName = String(data.systemName || "");
    const title = systemNameTitle(systemName, t("deities.title", "Deities"));
    const deities = sortByLabel(data.deities || [], (deity) => deity.name || "");
    const pantheons = sortByLabel(data.pantheons || [], (pantheon) => pantheon.name || "");
    const deityMap = {};
    const pantheonOptions = pantheons
      .map((pantheon) => `<option value="${escapeHtml(pantheon.id)}">${escapeHtml(pantheon.name)}</option>`)
      .join("");
    const list = deities
      .map((deity) => {
        deityMap[deity.id] = deity;
        const pantheonNames = pantheons
          .filter((pantheon) => (deity.pantheonIds || []).includes(pantheon.id))
          .map((pantheon) => pantheon.name)
          .join(", ");
        return `
          <div class="list-item">
            <div>
              <strong>${escapeHtml(deity.name || t("deities.untitled", "Untitled Deity"))}</strong>
              <span class="badge">${escapeHtml(deity.primaryPortfolio || t("common.none", "None"))}</span>
              <div class="badge">${escapeHtml(pantheonNames || t("common.none", "None"))}</div>
            </div>
            <div class="actions">
              ${collectionEditAction("edit-deity", deity.id)}
              ${collectionRemoveAction("remove-deity", deity.id)}
            </div>
          </div>
        `;
      })
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${escapeHtml(title)}</h1>
        ${renderSystemNameControls(systemName, t("deities.title", "Deities"))}
        <div class="grid two">
          <div class="field">
            <label>${t("deities.name", "Deity Name")}</label>
            <input type="text" id="deityName" maxlength="80">
          </div>
          <div class="field">
            <label>${t("deities.type", "Deity Type")}</label>
            <input type="text" id="deityType" maxlength="80">
          </div>
          <div class="field">
            <label>${t("deities.rank", "Divine Rank")}</label>
            <input type="text" id="deityRank" maxlength="80">
          </div>
          <div class="field">
            <label>${t("deities.portfolio", "Primary Portfolio")}</label>
            <input type="text" id="deityPortfolio" maxlength="120">
          </div>
          <div class="field">
            <label>${t("deities.alignment", "Alignment")}</label>
            <input type="text" id="deityAlignment" maxlength="80">
          </div>
          <div class="field">
            <label>${t("deities.symbol", "Holy Symbol")}</label>
            <input type="text" id="deitySymbol" maxlength="120">
          </div>
          <div class="field">
            <label>${t("deities.worship", "Worship Style")}</label>
            <input type="text" id="deityWorship" maxlength="160">
          </div>
          <div class="field">
            <label>${t("deities.pantheons", "Pantheons")}</label>
            <select id="deityPantheons" multiple size="5">${pantheonOptions}</select>
          </div>
          <label class="toggle">
            <input type="checkbox" id="deityCanGrantSpells" checked>
            ${t("deities.can_grant_spells", "Can grant spells")}
          </label>
          <div class="field">
            <label>${t("deities.max_spell_level", "Max Spell Level")}</label>
            <input type="number" id="deityMaxSpellLevel" min="0" max="9" value="9">
          </div>
        </div>
        <div class="field">
          <label>${t("common.description", "Description")}</label>
          <textarea id="deityDescription"></textarea>
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="deityClear" type="button">${t("common.cancel", "Cancel")}</button>
          </div>
          <div class="right">
            <button class="btn" id="deitySave" type="button">${t("common.save", "Save")}</button>
          </div>
        </div>
        <div class="list" id="deityList">${list || `<div class="list-item">${t("deities.none", "No deities yet.")}</div>`}</div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToPantheons" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn" id="deitiesContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    const fields = {
      name: document.getElementById("deityName"),
      description: document.getElementById("deityDescription"),
      rank: document.getElementById("deityRank"),
      type: document.getElementById("deityType"),
      portfolio: document.getElementById("deityPortfolio"),
      symbol: document.getElementById("deitySymbol"),
      alignment: document.getElementById("deityAlignment"),
      worship: document.getElementById("deityWorship"),
      canGrantSpells: document.getElementById("deityCanGrantSpells"),
      maxSpellLevel: document.getElementById("deityMaxSpellLevel"),
      pantheons: document.getElementById("deityPantheons"),
    };
    let editingId = "";
    const clearForm = () => {
      editingId = "";
      fields.name.value = "";
      fields.description.value = "";
      fields.rank.value = "";
      fields.type.value = "";
      fields.portfolio.value = "";
      fields.symbol.value = "";
      fields.alignment.value = "";
      fields.worship.value = "";
      fields.canGrantSpells.checked = true;
      fields.maxSpellLevel.value = "9";
      Array.from(fields.pantheons.options || []).forEach((option) => {
        option.selected = false;
      });
    };
    const loadForm = (deity) => {
      if (!deity) {
        clearForm();
        return;
      }
      editingId = String(deity.id || "");
      fields.name.value = String(deity.name || "");
      fields.description.value = String(deity.description || "");
      fields.rank.value = String(deity.divineRank || "");
      fields.type.value = String(deity.deityType || "");
      fields.portfolio.value = String(deity.primaryPortfolio || "");
      fields.symbol.value = String(deity.holySymbol || "");
      fields.alignment.value = String(deity.alignment || "");
      fields.worship.value = String(deity.worshipStyle || "");
      fields.canGrantSpells.checked = deity.canGrantSpells !== false;
      fields.maxSpellLevel.value = String(Number(deity.maxSpellLevel ?? 9));
      const pantheonIds = new Set((deity.pantheonIds || []).map((id) => String(id || "")));
      Array.from(fields.pantheons.options || []).forEach((option) => {
        option.selected = pantheonIds.has(option.value);
      });
      fields.name.focus();
    };

    document.getElementById("deityClear").addEventListener("click", clearForm);
    document.getElementById("deitySave").addEventListener("click", async () => {
      const name = fields.name.value.trim();
      if (!name) {
        showToast(t("common.name.required", "Name is required."));
        return;
      }
      try {
        if (!editingId) {
          const result = await api("POST", `/api/drafts/${state.draftId}/deities`, {
            name,
            description: fields.description.value.trim(),
          });
          editingId = String(result.id || "");
        }
        await api("POST", `/api/drafts/${state.draftId}/deities/update`, {
          id: editingId,
          name,
          description: fields.description.value.trim(),
          divineRank: fields.rank.value.trim(),
          deityType: fields.type.value.trim(),
          primaryPortfolio: fields.portfolio.value.trim(),
          holySymbol: fields.symbol.value.trim(),
          alignment: fields.alignment.value.trim(),
          worshipStyle: fields.worship.value.trim(),
          canGrantSpells: fields.canGrantSpells.checked,
          maxSpellLevel: Number(fields.maxSpellLevel.value || 0),
          pantheonIds: getSelectedValues(fields.pantheons),
        });
        markSaved(t("web.toast.deity_saved", "Deity saved"));
        renderDeities(editingId);
      } catch (error) {
        showToast(error.message);
      }
    });
    document.querySelectorAll("[data-edit-deity]").forEach((button) => {
      button.addEventListener("click", () => loadForm(deityMap[button.dataset.editDeity]));
    });
    document.querySelectorAll("[data-remove-deity]").forEach((button) => {
      button.addEventListener("click", async () => {
        const confirmed = await showConfirm(
          t("common.remove.confirm", "Remove selected item?"),
          t("common.remove", "Remove")
        );
        if (!confirmed) {
          return;
        }
        try {
          await api("DELETE", `/api/drafts/${state.draftId}/deities`, { id: button.dataset.removeDeity });
          markSaved(t("web.toast.deity_removed", "Deity removed"));
          renderDeities();
        } catch (error) {
          showToast(error.message);
        }
      });
    });
    document.getElementById("backToPantheons").addEventListener("click", navigateBackInApp);
    document.getElementById("deitiesContinue").addEventListener("click", renderRaces);
    wireSystemNameSave("deities", () => renderDeities());
    if (openId) {
      loadForm(deityMap[openId]);
    }
  } catch (error) {
    showToast(error.message);
  }
}

Object.assign(stepRoutes, {
  setup: renderSetup,
  measurements: renderMeasurements,
  dice: renderDice,
  "attribute-types": renderAttributeTypes,
  attributes: renderAttributes,
  "attribute-generation": renderAttributeGeneration,
  "standard-array": renderStandardArray,
  "dice-rolling": renderDiceRolling,
  "points-buy": renderPointsBuy,
  "hit-points": renderHitPoints,
  "armor-class": renderArmorClass,
  currency: renderCurrency,
  "effect-types": renderEffectTypes,
  "damage-types": renderDamageTypes,
  statuses: renderStatuses,
  effects: renderEffects,
  equipment: renderEquipment,
  weapons: renderWeapons,
  skills: renderSkills,
  spells: renderSpells,
  pantheons: renderPantheons,
  deities: renderDeities,
  races: renderRaces,
  classes: renderClasses,
});

Object.assign(historyRoutes, {
  "beta-application": renderClosedBetaApplication,
  login: renderLogin,
  home: renderHome,
  admin: renderAdmin,
  splash: renderBuilderSplash,
  "chargen-upload": renderCharGenUpload,
  "chargen-resume": renderCharGenResume,
  "chargen-name": renderCharGenName,
  "chargen-intro": renderCharGenIntro,
  "chargen-attrgen": renderCharGenAttributes,
  "chargen-points-buy": renderCharGenPointsBuy,
  "chargen-races": renderCharGenRaces,
  "chargen-classes": renderCharGenClasses,
  "chargen-skills": renderCharGenSkills,
  "chargen-spells": renderCharGenSpells,
  "chargen-equipment": renderCharGenEquipment,
  "chargen-weapons": renderCharGenWeapons,
  "chargen-armor": renderCharGenArmor,
});

function systemNameTitle(systemName, fallback) {
  const safe = String(systemName || "").trim();
  return safe ? safe : fallback;
}

function renderSystemNameControls(systemName, placeholder) {
  return `
    <div class="grid system-name-controls">
      <div class="field">
        <label for="systemNameInput">${t("common.system_name.label", "Section Label")}</label>
        <input type="text" id="systemNameInput" value="${escapeHtml(systemName)}"
          data-default-section-name="${escapeHtml(placeholder)}"
          placeholder="${escapeHtml(placeholder)}">
      </div>
    </div>
    <button class="btn system-name-rename-button" id="renameSystemName" type="button">${t("common.rename", "Rename")}</button>
    <div class="system-name-collection-break" aria-hidden="true"></div>
  `;
}

function wireSystemNameSave(key) {
  const input = document.getElementById("systemNameInput");
  const renameButton = document.getElementById("renameSystemName");
  if (!input || !renameButton) {
    return;
  }
  const saveSystemName = async () => {
    if (renameButton.disabled) {
      return;
    }
    const name = input.value.trim();
    renameButton.disabled = true;
    try {
      await api("POST", `/api/drafts/${state.draftId}/system-names`, { key, name });
      markSaved(t("common.system_name.saved", "Label saved."));
      const safeKey = String(key || "").trim().toLowerCase();
      if (safeKey) {
        if (name) {
          state.systemNames[safeKey] = name;
        } else {
          delete state.systemNames[safeKey];
        }
      }
      renderSidebar();
      const heading = view.querySelector(".panel > h1");
      if (heading) {
        heading.textContent = name || String(input.dataset.defaultSectionName || "");
      }
    } catch (error) {
      showToast(error.message);
    } finally {
      renameButton.disabled = false;
    }
  };
  renameButton.addEventListener("click", saveSystemName);
  input.addEventListener("change", saveSystemName);
  input.addEventListener("keydown", (event) => {
    if (event.key !== "Enter") {
      return;
    }
    event.preventDefault();
    saveSystemName();
  });
}

function wireCharGenDiceAssignmentUI(config) {
  const safeConfig = config || {};
  const method = safeConfig.method || {};
  const attributes = Array.isArray(safeConfig.attributes) ? safeConfig.attributes : [];
  const inputs = Array.isArray(safeConfig.inputs) ? safeConfig.inputs : [];
  const section = safeConfig.section;
  const title = safeConfig.title;
  const hint = safeConfig.hint;
  const availableList = safeConfig.availableList;
  const continueButton = safeConfig.continueButton;
  const selectedChoice = resolveCharGenGenerationChoice(method);
  const diceActive = isCharGenGenerationChoiceActive(method, selectedChoice, "dice");
  const assignInOrder = Boolean(method.assignInOrder);
  const additive = shouldAddCharGenRollToBase(method);
  const rolledValues = normalizeCharGenAttributeValues(state.chargenRolledAttributeValues);
  const validAttributeIds = new Set(inputs.map((entry) => String(entry.attributeId || "").trim()).filter(Boolean));
  let assignments = normalizeCharGenRollAssignments(state.chargenDiceRollAssignments);
  const usedRolls = new Set();
  const sanitizedAssignments = {};

  if (assignInOrder) {
    inputs.forEach((entry, index) => {
      const attributeId = String(entry.attributeId || "").trim();
      if (attributeId && index < rolledValues.length) {
        sanitizedAssignments[attributeId] = index;
      }
    });
  } else {
    Object.keys(assignments).forEach((attributeId) => {
      const rollIndex = assignments[attributeId];
      if (
        validAttributeIds.has(attributeId)
        && rollIndex >= 0
        && rollIndex < rolledValues.length
        && !usedRolls.has(rollIndex)
      ) {
        sanitizedAssignments[attributeId] = rollIndex;
        usedRolls.add(rollIndex);
      }
    });
  }
  assignments = sanitizedAssignments;
  state.chargenDiceRollAssignments = { ...assignments };

  const baselineScores = {};
  const controlsByAttributeId = {};

  const captureBaseline = (recoverFromSavedScores) => {
    inputs.forEach((entry) => {
      const attributeId = String(entry.attributeId || "").trim();
      let value = Number(entry.input.value || 0);
      const rollIndex = assignments[attributeId];
      if (
        recoverFromSavedScores
        && additive
        && Number.isInteger(rollIndex)
        && rollIndex >= 0
        && rollIndex < rolledValues.length
      ) {
        value -= rolledValues[rollIndex];
      }
      baselineScores[attributeId] = clampCharGen(value, entry.min, entry.max);
    });
  };

  captureBaseline(Boolean(safeConfig.hasSavedScores));

  inputs.forEach((entry) => {
    const attributeId = String(entry.attributeId || "").trim();
    const field = entry.input.closest(".field");
    if (!field) {
      return;
    }
    const controls = document.createElement("div");
    controls.className = "dice-assignment-controls";
    controls.dataset.attributeId = attributeId;
    field.appendChild(controls);
    controlsByAttributeId[attributeId] = controls;
  });

  const rollLabel = (rollIndex) => t("attrgen.roll.option", "Roll {number}: {value}")
    .replace("{number}", String(rollIndex + 1))
    .replace("{value}", String(rolledValues[rollIndex]));

  const isComplete = () => {
    if (!diceActive) {
      return true;
    }
    if (rolledValues.length !== inputs.length) {
      return false;
    }
    const assignedRolls = new Set();
    for (const entry of inputs) {
      const attributeId = String(entry.attributeId || "").trim();
      const rollIndex = assignments[attributeId];
      if (
        !Number.isInteger(rollIndex)
        || rollIndex < 0
        || rollIndex >= rolledValues.length
        || assignedRolls.has(rollIndex)
      ) {
        return false;
      }
      assignedRolls.add(rollIndex);
    }
    return assignedRolls.size === rolledValues.length;
  };

  const render = () => {
    if (!diceActive) {
      section.classList.add("hidden");
      inputs.forEach((entry) => {
        entry.input.readOnly = true;
      });
      if (continueButton) {
        continueButton.disabled = false;
      }
      return;
    }

    section.classList.remove("hidden");
    const assignedRolls = new Set(Object.values(assignments));
    if (assignInOrder) {
      title.textContent = t("attrgen.roll.assignments", "Roll Assignments");
      hint.textContent = t(
        "attrgen.roll.assignment.in_order",
        "This ruleset assigns rolls to Attributes in order. These values cannot be rearranged."
      );
      availableList.innerHTML = "";
      availableList.classList.add("hidden");
    } else {
      title.textContent = t("attrgen.roll.available", "Available Rolls");
      hint.textContent = t(
        "attrgen.roll.assignment.player",
        "Choose one available roll for each Attribute. Clear an assignment to return that roll to this set."
      );
      availableList.classList.remove("hidden");
      const availableIndices = rolledValues
        .map((value, index) => index)
        .filter((index) => !assignedRolls.has(index));
      availableList.innerHTML = availableIndices.length
        ? availableIndices
            .map((index) => `<div class="list-item">${escapeHtml(rollLabel(index))}</div>`)
            .join("")
        : `<div class="field-hint">${t("attrgen.roll.assignment.all_used", "All rolls are assigned.")}</div>`;
    }

    inputs.forEach((entry, attributeIndex) => {
      const attributeId = String(entry.attributeId || "").trim();
      const rollIndex = assignments[attributeId];
      const hasAssignment = Number.isInteger(rollIndex) && rollIndex >= 0 && rollIndex < rolledValues.length;
      const baseline = Number(baselineScores[attributeId] || 0);
      const score = hasAssignment
        ? additive ? baseline + rolledValues[rollIndex] : rolledValues[rollIndex]
        : baseline;
      entry.input.value = String(clampCharGen(score, entry.min, entry.max));
      entry.input.readOnly = true;

      const controls = controlsByAttributeId[attributeId];
      if (!controls) {
        return;
      }
      if (assignInOrder) {
        controls.innerHTML = hasAssignment
          ? `<span class="field-hint">${escapeHtml(rollLabel(rollIndex))}</span>`
          : "";
        return;
      }

      const usedByOthers = new Set(
        Object.entries(assignments)
          .filter(([assignedAttributeId]) => assignedAttributeId !== attributeId)
          .map(([, assignedRollIndex]) => assignedRollIndex)
      );
      const options = [
        `<option value="">${t("attrgen.roll.assignment.choose", "Choose a roll")}</option>`,
      ];
      rolledValues.forEach((value, index) => {
        if (usedByOthers.has(index)) {
          return;
        }
        const selected = index === rollIndex ? " selected" : "";
        options.push(`<option value="${index}"${selected}>${escapeHtml(rollLabel(index))}</option>`);
      });
      const attribute = attributes[attributeIndex] || {};
      const attributeName = attribute.displayName || attribute.name || `Attribute ${attributeIndex + 1}`;
      controls.innerHTML = `
        <select aria-label="${escapeHtml(t("attrgen.roll.assignment.for_attribute", "Roll for {attribute}").replace("{attribute}", attributeName))}">
          ${options.join("")}
        </select>
        <button class="btn ghost" type="button" ${hasAssignment ? "" : "disabled"}>${t("attrgen.roll.assignment.clear", "Clear")}</button>
      `;
      const select = controls.querySelector("select");
      const clearButton = controls.querySelector("button");
      select.addEventListener("change", () => {
        const nextRollIndex = Number(select.value);
        if (!select.value || !Number.isInteger(nextRollIndex)) {
          delete assignments[attributeId];
        } else {
          Object.keys(assignments).forEach((assignedAttributeId) => {
            if (assignedAttributeId !== attributeId && assignments[assignedAttributeId] === nextRollIndex) {
              delete assignments[assignedAttributeId];
            }
          });
          assignments[attributeId] = nextRollIndex;
        }
        state.chargenDiceRollAssignments = { ...assignments };
        render();
        saveCharGenDraftLocal();
      });
      clearButton.addEventListener("click", () => {
        delete assignments[attributeId];
        state.chargenDiceRollAssignments = { ...assignments };
        render();
        saveCharGenDraftLocal();
      });
    });

    state.chargenDiceRollAssignments = { ...assignments };
    state.chargenAttributeScores = collectCharGenAttributeScores(inputs);
    state.chargenAttributes = attributes.slice();
    if (continueButton) {
      continueButton.disabled = !isComplete();
    }
  };

  return {
    isComplete,
    render,
    resetBaseline: () => captureBaseline(false),
  };
}

function snapshotAttributeEdit() {
  return {
    kind: editContext && editContext.kind === "attribute" ? "attribute" : "attribute-create",
    id: editContext ? String(editContext.id || "") : "",
    name: String(editName.value || ""),
    description: String(editDescription.value || ""),
    typeKey: editType ? String(editType.value || "") : "",
    minValue: Number(editMinValue.value || 0),
    maxValue: Number(editMaxValue.value || 0),
    modifiers: normalizeModifierEntries(editModifiers).map((entry) => ({ ...entry })),
    scoreBonuses: editBonuses.map((entry) => ({
      threshold: Number(entry.threshold || 0),
      effectId: String(entry.effectId || entry.effect || ""),
    })),
    pendingThreshold: Number(editBonusThreshold.value || 0),
    selectedEffectId: editBonusEffect ? String(editBonusEffect.value || "") : "",
    bonusEditIndex: editBonusIndex,
  };
}

function restoreAttributeEdit(snapshot) {
  const safeSnapshot = snapshot || {};
  const attribute = {
    id: String(safeSnapshot.id || ""),
    name: String(safeSnapshot.name || ""),
    description: String(safeSnapshot.description || ""),
    typeKey: String(safeSnapshot.typeKey || ""),
    minValue: Number(safeSnapshot.minValue || 0),
    maxValue: Number(safeSnapshot.maxValue || 0),
    modifiers: normalizeModifierEntries(safeSnapshot.modifiers || []),
    scoreBonuses: Array.isArray(safeSnapshot.scoreBonuses)
      ? safeSnapshot.scoreBonuses.map((entry) => ({ ...entry }))
      : [],
  };
  if (safeSnapshot.kind === "attribute" && attribute.id) {
    openAttributeEditor(attribute, attributeTypeOptions);
  } else {
    openAttributeCreate(attributeTypeOptions);
    editName.value = attribute.name;
    editDescription.value = attribute.description;
    if (editType && Array.from(editType.options || []).some((option) => option.value === attribute.typeKey)) {
      editType.value = attribute.typeKey;
    }
    editMinValue.value = String(attribute.minValue);
    editMaxValue.value = String(attribute.maxValue);
    editModifiers = attribute.modifiers.map((entry) => ({ ...entry }));
    editBonuses = attribute.scoreBonuses.map((entry) => ({ ...entry }));
    renderEditModifiers();
    renderEditBonuses();
  }
  editBonusThreshold.value = String(Number(safeSnapshot.pendingThreshold || 0));
  populateAttributeBonusEffectSelect(String(safeSnapshot.selectedEffectId || ""));
  editBonusIndex = Number.isInteger(Number(safeSnapshot.bonusEditIndex))
    ? Number(safeSnapshot.bonusEditIndex)
    : -1;
  setCollectionCommitMode(editBonusAdd, editBonusIndex >= 0);
}

function sortByLabel(items, labelFn) {
  const list = Array.isArray(items) ? items.slice() : [];
  if (typeof labelFn !== "function") {
    return list;
  }
  list.sort((left, right) => {
    const leftLabel = String(labelFn(left) || "").toLowerCase();
    const rightLabel = String(labelFn(right) || "").toLowerCase();
    return leftLabel.localeCompare(rightLabel);
  });
  return list;
}

function escapeHtml(value) {
  return String(value)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/\"/g, "&quot;")
    .replace(/'/g, "&#039;");
}

function collectionAction(attribute, value, label, tone = "edit") {
  const safeAttribute = String(attribute || "").trim().toLowerCase();
  if (!/^[a-z][a-z0-9-]*$/.test(safeAttribute)) {
    return "";
  }
  const className = tone === "remove" ? "btn danger small" : "btn ghost small";
  return `<button class="${className}" type="button" data-${safeAttribute}="${escapeHtml(value)}">${escapeHtml(label)}</button>`;
}

function collectionEditAction(attribute, value, label = "") {
  return collectionAction(attribute, value, label || t("common.edit", "Edit"), "edit");
}

function collectionRemoveAction(attribute, value, label = "") {
  return collectionAction(attribute, value, label || t("common.remove", "Remove"), "remove");
}

function renderCollectionRow(contentHtml, actions = []) {
  const actionHtml = (Array.isArray(actions) ? actions : []).filter(Boolean).join("");
  return `
    <div class="list-item collection-row">
      <div class="collection-row-content">${contentHtml}</div>
      ${actionHtml ? `<div class="actions collection-row-actions">${actionHtml}</div>` : ""}
    </div>
  `;
}

function setCollectionCommitMode(button, editing) {
  if (!button) {
    return;
  }
  if (!button.dataset.addLabel) {
    button.dataset.addLabel = String(button.textContent || "").trim();
  }
  button.textContent = editing ? t("common.save", "Save") : button.dataset.addLabel;
}

function replaceOrAppendCollectionItem(items, index, value) {
  if (Number.isInteger(index) && index >= 0 && index < items.length) {
    items.splice(index, 1, value);
  } else {
    items.push(value);
  }
}

function setupSelectAllOnFocus() {
  document.addEventListener("focusin", (event) => {
    const target = event.target;
    if (!target) {
      return;
    }
    const tag = String(target.tagName || "").toLowerCase();
    if (tag !== "input" && tag !== "textarea") {
      return;
    }
    if (target.disabled || target.readOnly) {
      return;
    }
    if (tag === "input") {
      const type = String(target.type || "text").toLowerCase();
      if (["checkbox", "radio", "button", "submit", "reset", "file", "range", "color", "hidden"].includes(type)) {
        return;
      }
    }
    const value = String(target.value || "");
    if (!value) {
      return;
    }
    window.setTimeout(() => {
      if (document.activeElement === target) {
        target.select();
      }
    }, 0);
  });
}

boot();
