const state = {
  draftId: "",
  step: "splash",
  mode: "home",
  attributeGenerationType: "",
  attributeGenerationStages: [],
  locale: "",
  strings: {},
  sessionToken: "",
  accountName: "",
  legacyGuest: false,
  currencyId: "",
  chargenAttributes: [],
  chargenAttributeScores: {},
  chargenPointBuyBaselineScores: {},
  chargenRaceId: "",
  chargenClassId: "",
  chargenGameId: "",
  chargenGameHash: "",
  chargenGameName: "",
  chargenDraftText: "",
  lastAttributeTypeKey: "",
  lastSkillCategoryKey: "",
  lastEffectTypeKeys: [],
  lastStatusEffectTypeKeys: [],
  applyAttributeModifiersToAllAttributes: false,
  attributeModifiers: [],
  defaultAttributeMinScore: 0,
  defaultAttributeMaxScore: 0,
  systemNames: {},
};

const steps = [
  { id: "setup", labelKey: "setup.title", fallback: "Game Setup" },
  { id: "measurements", labelKey: "measurements.title", fallback: "Measurements" },
  { id: "dice", labelKey: "dice.title", fallback: "Dice Options" },
  { id: "attribute-generation", labelKey: "attrgen.title", fallback: "Attribute Generation" },
  { id: "standard-array", labelKey: "attrgen.standard.title", fallback: "Standard Arrays" },
  { id: "dice-rolling", labelKey: "attrgen.dice.title", fallback: "Dice Rolling" },
  { id: "points-buy", labelKey: "attrgen.point.title", fallback: "Points Buy" },
  { id: "attribute-types", labelKey: "attrtypes.title", fallback: "Attribute Categories" },
  { id: "attributes", labelKey: "attributes.title", fallback: "Attributes" },
  { id: "hit-points", labelKey: "hp.title", fallback: "Hit Points" },
  { id: "armor-class", labelKey: "armorclass.title", fallback: "Armor Class" },
  { id: "currency", labelKey: "currency.title", fallback: "Currency", systemNameKey: "currencies" },
  { id: "effect-types", labelKey: "effecttypes.title", fallback: "Effect Types" },
  { id: "statuses", labelKey: "statuses.title", fallback: "Statuses" },
  { id: "effects", labelKey: "effects.title", fallback: "Effects" },
  { id: "equipment", labelKey: "equipment.title", fallback: "Equipment" },
  { id: "weapons", labelKey: "weapons.title", fallback: "Weapons" },
  { id: "skills", labelKey: "skills.title", fallback: "Skills" },
  { id: "spells", labelKey: "spells.title", fallback: "Spells" },
  { id: "races", labelKey: "races.title", fallback: "Races" },
  { id: "classes", labelKey: "classes.title", fallback: "Classes" },
];

const stepRoutes = {};
const historyRoutes = {};
const visitedSteps = new Set();
const appBackStack = [];
const SESSION_TOKEN_KEY = "gmrules.web.sessionToken";

let historyReady = false;
let historyLocked = false;
let appBackLocked = false;

const view = document.getElementById("view");
const stepIndicator = document.getElementById("stepIndicator");
const saveStatus = document.getElementById("saveStatus");
const feedbackBtn = document.getElementById("feedbackBtn");
const downloadBtn = document.getElementById("downloadBtn");
const logoutBtn = document.getElementById("logoutBtn");
const toast = document.getElementById("toast");
const sidebarTitle = document.getElementById("sidebarTitle");
const sidebarNav = document.getElementById("sidebarNav");

const confirmModal = document.getElementById("confirmModal");
const confirmTitle = document.getElementById("confirmTitle");
const confirmMessage = document.getElementById("confirmMessage");
const confirmCancel = document.getElementById("confirmCancel");
const confirmOk = document.getElementById("confirmOk");

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
const editClassSkillCreate = document.getElementById("editClassSkillCreate");
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
const editTypeCreate = document.getElementById("editTypeCreate");
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
const editRaceTraitCreateTitle = document.getElementById("editRaceTraitCreateTitle");
const editRaceTraitNameLabel = document.getElementById("editRaceTraitNameLabel");
const editRaceTraitName = document.getElementById("editRaceTraitName");
const editRaceTraitDescriptionLabel = document.getElementById("editRaceTraitDescriptionLabel");
const editRaceTraitDescription = document.getElementById("editRaceTraitDescription");
const editRaceTraitCreate = document.getElementById("editRaceTraitCreate");
const editRaceStartingMoneyModifierLabel = document.getElementById("editRaceStartingMoneyModifierLabel");
const editRaceStartingMoneyModifier = document.getElementById("editRaceStartingMoneyModifier");
const editWeaponEffectsSection = document.getElementById("editWeaponEffectsSection");
const editWeaponEffectsTitle = document.getElementById("editWeaponEffectsTitle");
const editWeaponEffectSelectLabel = document.getElementById("editWeaponEffectSelectLabel");
const editWeaponEffectSelect = document.getElementById("editWeaponEffectSelect");
const editWeaponEffectAdd = document.getElementById("editWeaponEffectAdd");
const editWeaponEffectCreate = document.getElementById("editWeaponEffectCreate");
const editWeaponEffectList = document.getElementById("editWeaponEffectList");
const editCancel = document.getElementById("editCancel");
const editOk = document.getElementById("editOk");
const skillModal = document.getElementById("skillModal");
const skillModalTitle = document.getElementById("skillModalTitle");
const skillNameLabel = document.getElementById("skillNameLabel");
const skillNameInput = document.getElementById("skillNameInput");
const skillCategoryLabel = document.getElementById("skillCategoryLabel");
const skillCategorySelect = document.getElementById("skillCategorySelect");
const skillCategoryCreateTitle = document.getElementById("skillCategoryCreateTitle");
const skillCategoryNameLabel = document.getElementById("skillCategoryNameLabel");
const skillCategoryNameInput = document.getElementById("skillCategoryNameInput");
const skillCategoryDescLabel = document.getElementById("skillCategoryDescLabel");
const skillCategoryDescInput = document.getElementById("skillCategoryDescInput");
const skillCategoryCreate = document.getElementById("skillCategoryCreate");
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
const skillEffectCreateTitle = document.getElementById("skillEffectCreateTitle");
const skillEffectNameLabel = document.getElementById("skillEffectNameLabel");
const skillEffectName = document.getElementById("skillEffectName");
const skillEffectDescLabel = document.getElementById("skillEffectDescLabel");
const skillEffectDesc = document.getElementById("skillEffectDesc");
const skillEffectCreate = document.getElementById("skillEffectCreate");
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
const spellEffectTitle = document.getElementById("spellEffectTitle");
const spellEffectSelectLabel = document.getElementById("spellEffectSelectLabel");
const spellEffectSelect = document.getElementById("spellEffectSelect");
const spellEffectAdd = document.getElementById("spellEffectAdd");
const spellEffectList = document.getElementById("spellEffectList");
const spellEffectCreateTitle = document.getElementById("spellEffectCreateTitle");
const spellEffectNameLabel = document.getElementById("spellEffectNameLabel");
const spellEffectName = document.getElementById("spellEffectName");
const spellEffectDescLabel = document.getElementById("spellEffectDescLabel");
const spellEffectDesc = document.getElementById("spellEffectDesc");
const spellEffectCreate = document.getElementById("spellEffectCreate");
const spellCancel = document.getElementById("spellCancel");
const spellSave = document.getElementById("spellSave");

let confirmResolve = null;
let pendingTypeUpdate = null;
let editContext = null;
let editReturnTo = "";
let editModifiers = [];
let editBonuses = [];
let editEffectTypeKeys = [];
let editSuspend = null;
let effectTypeOptions = [];
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
let spellContext = null;
let spellEffects = [];
let spellEffectOptions = [];
state.locale = "en";

let transientZIndex = 1000;

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
  downloadBtn.textContent = t("web.download", "Download .gmrf");
  logoutBtn.textContent = t("web.logout", "Logout");
  confirmTitle.textContent = t("web.confirm.title", "Confirm");
  confirmCancel.textContent = t("common.cancel", "Cancel");
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
  editNameLabel.textContent = t("common.name", "Name");
  editDescriptionLabel.textContent = t("common.description", "Description");
  if (editWeightValueLabel) {
    editWeightValueLabel.textContent = t("common.weight", "Weight");
  }
  if (editWeightUnitLabel) {
    editWeightUnitLabel.textContent = t("common.weight.unit", "Weight Unit");
  }
  editTypeLabel.textContent = t("attributes.edit.type", "Attribute Category");
  if (editTypeAdd) {
    editTypeAdd.textContent = t("effects.type.add", "Add Type");
  }
  if (editTypeCreate) {
    editTypeCreate.textContent = t("effecttypes.create", "Create Effect Type");
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
  if (editClassSkillCreate) {
    editClassSkillCreate.textContent = t("skills.create", "Create Skill");
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
  if (editWeaponEffectCreate) {
    editWeaponEffectCreate.textContent = t("skills.effects.create", "Create Effect");
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
  if (editRaceTraitCreateTitle) {
    editRaceTraitCreateTitle.textContent = t("skills.create", "Create Skill");
  }
  if (editRaceTraitNameLabel) {
    editRaceTraitNameLabel.textContent = t("skills.name", "Skill Name");
  }
  if (editRaceTraitDescriptionLabel) {
    editRaceTraitDescriptionLabel.textContent = t("common.description", "Description");
  }
  if (editRaceTraitCreate) {
    editRaceTraitCreate.textContent = t("skills.create", "Create Skill");
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
  if (skillCategoryCreateTitle) {
    skillCategoryCreateTitle.textContent = t("skills.category.create", "Create Category");
  }
  if (skillCategoryNameLabel) {
    skillCategoryNameLabel.textContent = t("common.name", "Name");
  }
  if (skillCategoryDescLabel) {
    skillCategoryDescLabel.textContent = t("common.description", "Description");
  }
  if (skillCategoryCreate) {
    skillCategoryCreate.textContent = t("skills.category.create", "Create Category");
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
  if (skillEffectCreateTitle) {
    skillEffectCreateTitle.textContent = t("skills.effects.create", "Create Effect");
  }
  if (skillEffectNameLabel) {
    skillEffectNameLabel.textContent = t("effects.name", "Effect Name");
  }
  if (skillEffectDescLabel) {
    skillEffectDescLabel.textContent = t("effects.description", "Description");
  }
  if (skillEffectCreate) {
    skillEffectCreate.textContent = t("skills.effects.create", "Create Effect");
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
  if (spellEffectTitle) {
    spellEffectTitle.textContent = t("spells.effects", "Effects");
  }
  if (spellEffectSelectLabel) {
    spellEffectSelectLabel.textContent = t("skills.effects.select", "Select Effect");
  }
  if (spellEffectAdd) {
    spellEffectAdd.textContent = t("skills.effects.add", "Add Effect");
  }
  if (spellEffectCreateTitle) {
    spellEffectCreateTitle.textContent = t("skills.effects.create", "Create Effect");
  }
  if (spellEffectNameLabel) {
    spellEffectNameLabel.textContent = t("effects.name", "Effect Name");
  }
  if (spellEffectDescLabel) {
    spellEffectDescLabel.textContent = t("effects.description", "Description");
  }
  if (spellEffectCreate) {
    spellEffectCreate.textContent = t("skills.effects.create", "Create Effect");
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
  const text = await response.text();
  const data = text ? JSON.parse(text) : {};
  if (!response.ok) {
    throw new Error(data.error || t("web.error.request_failed", "Request failed"));
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
  const text = await response.text();
  const data = text ? JSON.parse(text) : {};
  if (!response.ok) {
    throw new Error(data.error || t("web.error.request_failed", "Request failed"));
  }
  return data;
}

function setLoggedIn(isLoggedIn) {
  document.body.classList.toggle("logged-out", !isLoggedIn);
}

function setMode(mode) {
  const safeMode = ["home", "builder", "chargen"].includes(mode) ? mode : "home";
  state.mode = safeMode;
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
  markVisited(safeStep);
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
  renderSidebar();
}

function applyCompletedStages(stageKeys) {
  const safeKeys = Array.isArray(stageKeys) ? stageKeys : [];
  const stageSet = new Set(
    safeKeys
      .map((value) => String(value || "").trim())
      .filter((value) => value.length > 0)
  );
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

function renderSidebar() {
  if (!sidebarNav || !sidebarTitle) {
    return;
  }
  if (state.mode !== "builder") {
    sidebarNav.innerHTML = "";
    return;
  }
  sidebarTitle.textContent = t("common.stages", "Stages");
  let items = steps.filter((entry) => visitedSteps.has(entry.id));
  if (!items.length) {
    const stepIndex = steps.findIndex((entry) => entry.id === state.step);
    if (stepIndex >= 0) {
      items = steps.slice(0, stepIndex + 1);
      items.forEach((entry) => visitedSteps.add(entry.id));
    }
  }
  if (!items.length) {
    sidebarNav.innerHTML = "";
    return;
  }
  sidebarNav.innerHTML = items
    .map((entry) => {
      const isActive = entry.id === state.step;
      const activeClass = isActive ? " active" : "";
      const disabled = isActive ? "disabled" : "";
      const systemNameKey = String(entry.systemNameKey || entry.id || "").trim().toLowerCase();
      const customLabel = systemNameKey ? String(state.systemNames[systemNameKey] || "").trim() : "";
      const label = customLabel ? escapeHtml(customLabel) : t(entry.labelKey, entry.fallback);
      return `
        <button class="btn ghost sidebar-link${activeClass}" type="button" data-step="${entry.id}" ${disabled}>
          ${label}
        </button>
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
  if (feedbackBtn) {
    feedbackBtn.style.display = state.sessionToken ? "" : "none";
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

function showConfirm(message, okLabel) {
  confirmTitle.textContent = t("web.confirm.title", "Confirm");
  confirmMessage.textContent = message;
  confirmOk.textContent = okLabel || t("common.remove", "Remove");
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

feedbackCancel.addEventListener("click", closeFeedbackModal);
feedbackSubmit.addEventListener("click", submitFeedbackReport);
feedbackType.addEventListener("change", () => {
  feedbackSeverity.value = defaultFeedbackSeverity(feedbackType.value);
  updateFeedbackStepsVisibility();
});

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
    effectIds: editWeaponEffectIds.slice(),
  };
}

function resetRaceEditSection() {
  editRaceSkillIds = [];
  editRaceAttributeLimits = [];
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
  if (editRaceTraitName) {
    editRaceTraitName.value = "";
  }
  if (editRaceTraitDescription) {
    editRaceTraitDescription.value = "";
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
  editTitle.textContent = t("effecttypes.edit.title", "Edit Effect Type");
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
  editTypeLabel.textContent = t("effects.type", "Type");
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

async function openStatusCreate() {
  resetClassEditSection();
  resetWeaponEditSection();
  resetRaceEditSection();
  resetEffectTypeSection();
  editContext = { kind: "status-create" };
  editTitle.textContent = t("statuses.edit.title", "Edit Status");
  editName.value = "";
  editDescription.value = "";
  editTypeLabel.textContent = t("effects.type", "Type");
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
  editEffectTypeKeys = Array.isArray(state.lastStatusEffectTypeKeys)
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
  if (editModifierApplyAll) {
    editModifierApplyAll.checked = false;
  }
  renderEditModifiers();
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
  editBonuses = Array.isArray(attribute.scoreBonuses) ? attribute.scoreBonuses.slice() : [];
  if (editModifierApplyAll) {
    editModifierApplyAll.checked = false;
  }
  renderEditModifiers();
  renderEditBonuses();
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
  editContext = { kind: "effect", id: effect.id };
  editTitle.textContent = t("effects.edit.title", "Edit Effect");
  editName.value = effect.name || "";
  editDescription.value = effect.description || "";
  editTypeLabel.textContent = t("effects.type", "Type");
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
  }
  editReturnTo = suspendModalForEdit(safeOrigin);
  resetClassEditSection();
  resetWeaponEditSection();
  resetRaceEditSection();
  resetEffectTypeSection();
  editContext = { kind: "effect-create", origin: safeOrigin };
  editTitle.textContent = t("skills.effects.create", "Create Effect");
  editName.value = String(prefillName || "");
  editDescription.value = String(prefillDescription || "");
  editTypeLabel.textContent = t("effects.type", "Type");
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
  const options = (includeNone ? [`<option value="">${t("effects.type.none", "None")}</option>`] : [])
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
    .filter((entry) => Number.isFinite(entry.score) && Number.isFinite(entry.modifier));
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
  if (editTypeCreate) {
    editTypeCreate.classList.toggle("hidden", !showList);
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
    openEffectCreateModal(suspended.origin, suspended.name, suspended.description, suspended.effectTypeKeys);
    return true;
  }
  if (suspended.kind === "weapon") {
    openWeaponEditor(suspended.weapon);
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
  }
  editContext = { kind: "effect-type-create", origin: safeOrigin };
  editTitle.textContent = t("effecttypes.create.title", "Create Effect Type");
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
      (key, index) => `
        <div class="list-item">
          <span>${escapeHtml(resolveEffectTypeLabel(key))}</span>
          <button class="btn danger small" data-remove-edit-type="${index}">${t("common.remove", "Remove")}</button>
        </div>
      `
    )
    .join("");
  editTypeList.querySelectorAll("[data-remove-edit-type]").forEach((button) => {
    button.addEventListener("click", async () => {
      const index = Number(button.dataset.removeEditType || -1);
      if (Number.isNaN(index) || index < 0 || index >= editEffectTypeKeys.length) {
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
    if (!value) {
      return;
    }
    if (editContext && (editContext.kind === "status" || editContext.kind === "status-create")) {
      const exists = editEffectTypeKeys.some((key) => {
        return String(key || "").trim().toLowerCase() === value.toLowerCase();
      });
      if (exists) {
        editType.value = "";
        return;
      }
    }
    editEffectTypeKeys.push(value);
    editType.value = "";
    renderEditEffectTypeList();
  });
}

if (editTypeCreate) {
  editTypeCreate.addEventListener("click", () => {
    const origin = editContext && editContext.kind === "effect-create" ? editContext.origin : "effects";
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
  const options = [`<option value="">${t("classes.skills.select", "Select Skill")}</option>`]
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
      (skillId, index) => `
        <div class="list-item">
          <span>${escapeHtml(resolveClassSkillLabel(skillId))}</span>
          <button class="btn danger small" data-remove-class-skill="${index}">${t("common.remove", "Remove")}</button>
        </div>
      `
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
      (entry, index) => `
        <div class="list-item">
          <span>${escapeHtml(resolveClassAttributeLabel(entry.attributeId))} : ${escapeHtml(entry.score)}</span>
          <button class="btn danger small" data-remove-class-req="${index}">${t("common.remove", "Remove")}</button>
        </div>
      `
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
      (entry, index) => `
        <div class="list-item">
          <span>${t("classes.skill_points.level.label", `Level ${entry.level}`).replace("{0}", String(entry.level))} : ${entry.points}</span>
          <button class="btn danger small" data-remove-class-skill-points="${index}">${t("common.remove", "Remove")}</button>
        </div>
      `
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
  const options = [`<option value="">${t("races.traits.select", "Select Trait")}</option>`]
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
      return `
        <div class="list-item">
          <span>${label}</span>
          <button class="btn danger small" data-remove-race-trait="${index}">${t("common.remove", "Remove")}</button>
        </div>
      `;
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
      return `
        <div class="list-item">
          <span>${label} (${minLabel}: ${minValue}, ${maxLabel}: ${maxValue})</span>
          <button class="btn danger small" data-remove-race-attribute="${index}">${t("common.remove", "Remove")}</button>
        </div>
      `;
    })
    .join("");
}

function populateWeaponEffectSelect() {
  if (!editWeaponEffectSelect) {
    return;
  }
  const options = [`<option value="">${t("skills.effects.select", "Select Effect")}</option>`]
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
      return `
        <div class="list-item">
          <span>${label}</span>
          <button class="btn danger small" data-remove-weapon-effect="${index}">${t("common.remove", "Remove")}</button>
        </div>
      `;
    })
    .join("");
  document.querySelectorAll("[data-remove-weapon-effect]").forEach((button) => {
    button.addEventListener("click", () => {
      const index = Number(button.dataset.removeWeaponEffect || -1);
      if (index < 0) {
        return;
      }
      editWeaponEffectIds.splice(index, 1);
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
  editModifierList.innerHTML = editModifiers
    .map(
      (entry, index) => `
        <div class="list-item">
          <span>${entry.score} → ${entry.modifier}</span>
          <button class="btn danger small" data-remove-modifier="${index}">${t("common.remove", "Remove")}</button>
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
          <span>${entry.threshold} : ${escapeHtml(entry.effect)}</span>
          <button class="btn danger small" data-remove-bonus="${index}">${t("common.remove", "Remove")}</button>
        </div>
      `
    )
    .join("");
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
  if (skillReturnToEdit && editModal) {
    editModal.classList.remove("hidden");
  }
  skillReturnToEdit = false;
}

function renderSkillEffectOptions() {
  if (!skillEffectSelect) {
    return;
  }
  const options = [`<option value="">${t("skills.effects.select", "Select Effect")}</option>`]
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
  const options = [`<option value="">${t("skills.category.none", "None")}</option>`]
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
          <button class="btn danger small" data-remove-skill-effect="${index}">${t("common.remove", "Remove")}</button>
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
          <button class="btn danger small" data-remove-skill-class-limit="${index}">${t("common.remove", "Remove")}</button>
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
          <button class="btn danger small" data-remove-skill-race-limit="${index}">${t("common.remove", "Remove")}</button>
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
}

function renderSpellEffectOptions(selectedValue) {
  if (!spellEffectSelect) {
    return;
  }
  const options = [`<option value="">${t("skills.effects.select", "Select Effect")}</option>`]
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
          <button class="btn danger small" data-remove-spell-effect="${index}">${t("common.remove", "Remove")}</button>
        </div>
      `
    )
    .join("");
}

function openSpellEditor(spell) {
  if (!spell || !spellModal) {
    return;
  }
  spellContext = { id: spell.id, mode: "edit" };
  spellNameInput.value = spell.name || "";
  spellDescriptionInput.value = spell.description || "";
  spellSchoolInput.value = spell.school || "";
  spellLevelInput.value = Number(spell.level || 0);
  spellCastingInput.value = spell.castingTime || "";
  spellRangeInput.value = spell.range || "";
  spellDurationInput.value = spell.duration || "";
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

function openSpellCreateModal() {
  if (!spellModal) {
    return;
  }
  spellContext = { mode: "create" };
  spellNameInput.value = "";
  spellDescriptionInput.value = "";
  spellSchoolInput.value = "";
  spellLevelInput.value = "0";
  spellCastingInput.value = "";
  spellRangeInput.value = "";
  spellDurationInput.value = "";
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
        (race) => `
          <div class="list-item">
            <div>
              <strong>${escapeHtml(race.name || t("races.untitled", "Untitled"))}</strong>
            </div>
            <div>
              <button class="btn ghost small" data-edit-race="${race.id}">${t("common.edit", "Edit")}</button>
              <button class="btn danger small" data-remove-race="${race.id}">${t("common.remove", "Remove")}</button>
            </div>
          </div>
        `
      )
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${escapeHtml(title)}</h1>
        <p class="field-hint">${t(
          "races.intro",
          "Races define species options and the traits or limits that come with them."
        )}</p>
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
  editModifiers.push({ score, modifier });
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
  const effect = String(editBonusEffect.value || "").trim();
  if (!effect) {
    showToast(t("common.name.required", "Name is required."));
    return;
  }
  editBonuses.push({ threshold, effect });
  editBonusEffect.value = "";
  renderEditBonuses();
});

editModifierList.addEventListener("click", async (event) => {
  const button = event.target.closest("button[data-remove-modifier]");
  if (!button) {
    return;
  }
  const index = Number(button.dataset.removeModifier);
  const confirmed = await showConfirm(
    t("common.remove.confirm", "Remove selected item?"),
    t("common.remove", "Remove")
  );
  if (!confirmed) {
    return;
  }
  editModifiers.splice(index, 1);
  renderEditModifiers();
});

editBonusList.addEventListener("click", async (event) => {
  const button = event.target.closest("button[data-remove-bonus]");
  if (!button) {
    return;
  }
  const index = Number(button.dataset.removeBonus);
  const confirmed = await showConfirm(
    t("common.remove.confirm", "Remove selected item?"),
    t("common.remove", "Remove")
  );
  if (!confirmed) {
    return;
  }
  editBonuses.splice(index, 1);
  renderEditBonuses();
});

if (editRaceTraitAdd) {
  editRaceTraitAdd.addEventListener("click", () => {
    const skillId = String(editRaceTraitSelect.value || "").trim();
    if (!skillId) {
      return;
    }
    if (!editRaceSkillIds.includes(skillId)) {
      editRaceSkillIds.push(skillId);
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
      (limit) => String(limit.attributeId || "") === attributeId
    );
    const entry = { attributeId, min: minValue, max: maxValue };
    if (existingIndex >= 0) {
      editRaceAttributeLimits[existingIndex] = entry;
    } else {
      editRaceAttributeLimits.push(entry);
    }
    renderRaceAttributeList();
  });
}

if (editWeaponEffectAdd) {
  editWeaponEffectAdd.addEventListener("click", () => {
    const effectId = String(editWeaponEffectSelect.value || "").trim();
    if (!effectId) {
      return;
    }
    if (!editWeaponEffectIds.includes(effectId)) {
      editWeaponEffectIds.push(effectId);
      renderWeaponEffectList();
    }
  });
}
if (editWeaponEffectCreate) {
  editWeaponEffectCreate.addEventListener("click", async () => {
    await openEffectCreateModal("weapon", "", "", state.lastEffectTypeKeys);
  });
}

if (editClassSkillAdd) {
  editClassSkillAdd.addEventListener("click", () => {
    const skillId = String(editClassSkillSelect.value || "").trim();
    if (!skillId) {
      return;
    }
    if (!editClassSkillIds.includes(skillId)) {
      editClassSkillIds.push(skillId);
      renderClassSkillList();
    }
  });
}

if (editClassSkillList) {
  editClassSkillList.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-remove-class-skill]");
    if (!button) {
      return;
    }
    const index = Number(button.dataset.removeClassSkill);
    const confirmed = await showConfirm(
      t("common.remove.confirm", "Remove selected item?"),
      t("common.remove", "Remove")
    );
    if (!confirmed) {
      return;
    }
    editClassSkillIds.splice(index, 1);
    renderClassSkillList();
  });
}

if (editClassSkillCreate) {
  editClassSkillCreate.addEventListener("click", async () => {
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
    const existingIndex = editClassSkillPointsByLevel.findIndex((entry) => entry.level === level);
    const entry = { level, points };
    if (existingIndex >= 0) {
      editClassSkillPointsByLevel.splice(existingIndex, 1, entry);
    } else {
      editClassSkillPointsByLevel.push(entry);
    }
    editClassSkillPointsByLevel.sort((left, right) => left.level - right.level);
    renderClassSkillPointsList();
  });
}

if (editClassSkillPointsList) {
  editClassSkillPointsList.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-remove-class-skill-points]");
    if (!button) {
      return;
    }
    if (editClassSkillPointsSame && editClassSkillPointsSame.checked) {
      return;
    }
    const index = Number(button.dataset.removeClassSkillPoints);
    const confirmed = await showConfirm(
      t("common.remove.confirm", "Remove selected item?"),
      t("common.remove", "Remove")
    );
    if (!confirmed) {
      return;
    }
    editClassSkillPointsByLevel.splice(index, 1);
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
      (entry) => String(entry.attributeId || "") === attributeId
    );
    const entry = { attributeId, score: scoreValue };
    if (existingIndex >= 0) {
      editClassRequiredScores.splice(existingIndex, 1, entry);
    } else {
      editClassRequiredScores.push(entry);
    }
    renderClassRequiredList();
  });
}

if (editClassRequiredList) {
  editClassRequiredList.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-remove-class-req]");
    if (!button) {
      return;
    }
    const index = Number(button.dataset.removeClassReq);
    const confirmed = await showConfirm(
      t("common.remove.confirm", "Remove selected item?"),
      t("common.remove", "Remove")
    );
    if (!confirmed) {
      return;
    }
    editClassRequiredScores.splice(index, 1);
    renderClassRequiredList();
  });
}

if (editRaceTraitList) {
  editRaceTraitList.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-remove-race-trait]");
    if (!button) {
      return;
    }
    const index = Number(button.dataset.removeRaceTrait);
    const confirmed = await showConfirm(
      t("common.remove.confirm", "Remove selected item?"),
      t("common.remove", "Remove")
    );
    if (!confirmed) {
      return;
    }
    editRaceSkillIds.splice(index, 1);
    renderRaceTraitList();
  });
}

if (editRaceAttributeList) {
  editRaceAttributeList.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-remove-race-attribute]");
    if (!button) {
      return;
    }
    const index = Number(button.dataset.removeRaceAttribute);
    const confirmed = await showConfirm(
      t("common.remove.confirm", "Remove selected item?"),
      t("common.remove", "Remove")
    );
    if (!confirmed) {
      return;
    }
    editRaceAttributeLimits.splice(index, 1);
    renderRaceAttributeList();
  });
}

if (editRaceTraitCreate) {
  editRaceTraitCreate.addEventListener("click", async () => {
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
      markSaved(t("web.toast.effect_type_updated", "Effect type updated"));
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
      markSaved(t("web.toast.effect_type_added", "Effect type added"));
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
      if (hadSuspend && Array.isArray(editSuspend.effectTypeKeys)) {
        const alreadyAdded = editSuspend.effectTypeKeys.some(
          (key) => String(key || "").toLowerCase() === createdName.toLowerCase()
        );
        if (!alreadyAdded) {
          editSuspend.effectTypeKeys.push(createdName);
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
      });
      markSaved(t("web.toast.effect_added", "Effect added"));
      state.lastEffectTypeKeys = effectTypeKeys.slice();
      const effectEntry = { id: result.id || "", name, description, effectTypeKeys };
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
        if (skillEffectName) {
          skillEffectName.value = "";
        }
        if (skillEffectDesc) {
          skillEffectDesc.value = "";
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
        if (spellEffectName) {
          spellEffectName.value = "";
        }
        if (spellEffectDesc) {
          spellEffectDesc.value = "";
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
    if (!name) {
      return;
    }
    if (!skillEffects.includes(name)) {
      skillEffects.push(name);
      renderSkillEffectList();
    }
  });
}

if (skillEffectList) {
  skillEffectList.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-remove-skill-effect]");
    if (!button) {
      return;
    }
    const index = Number(button.dataset.removeSkillEffect);
    if (Number.isNaN(index) || index < 0) {
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
    renderSkillEffectList();
  });
}

if (skillClassLimitAdd) {
  skillClassLimitAdd.addEventListener("click", () => {
    const classId = String(skillClassLimitSelect ? skillClassLimitSelect.value : "").trim();
    if (!classId) {
      return;
    }
    if (!skillLimitedClassIds.includes(classId)) {
      skillLimitedClassIds.push(classId);
      renderSkillClassLimitList();
    }
  });
}

if (skillClassLimitList) {
  skillClassLimitList.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-remove-skill-class-limit]");
    if (!button) {
      return;
    }
    const index = Number(button.dataset.removeSkillClassLimit);
    if (Number.isNaN(index) || index < 0) {
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
    renderSkillClassLimitList();
  });
}

if (skillRaceLimitAdd) {
  skillRaceLimitAdd.addEventListener("click", () => {
    const raceId = String(skillRaceLimitSelect ? skillRaceLimitSelect.value : "").trim();
    if (!raceId) {
      return;
    }
    if (!skillLimitedRaceIds.includes(raceId)) {
      skillLimitedRaceIds.push(raceId);
      renderSkillRaceLimitList();
    }
  });
}

if (skillRaceLimitList) {
  skillRaceLimitList.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-remove-skill-race-limit]");
    if (!button) {
      return;
    }
    const index = Number(button.dataset.removeSkillRaceLimit);
    if (Number.isNaN(index) || index < 0) {
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
    renderSkillRaceLimitList();
  });
}

if (spellEffectAdd) {
  spellEffectAdd.addEventListener("click", () => {
    const name = String(spellEffectSelect ? spellEffectSelect.value : "").trim();
    if (!name) {
      return;
    }
    if (!spellEffects.includes(name)) {
      spellEffects.push(name);
      renderSpellEffectList();
    }
  });
}

if (spellEffectList) {
  spellEffectList.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-remove-spell-effect]");
    if (!button) {
      return;
    }
    const index = Number(button.dataset.removeSpellEffect);
    if (Number.isNaN(index) || index < 0) {
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
    renderSpellEffectList();
  });
}

if (skillEffectCreate) {
  skillEffectCreate.addEventListener("click", async () => {
    await openEffectCreateModal("skill", "", "", state.lastEffectTypeKeys);
  });
}

if (skillCategoryCreate) {
  skillCategoryCreate.addEventListener("click", async () => {
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
          if (editRaceTraitName) {
            editRaceTraitName.value = "";
          }
          if (editRaceTraitDescription) {
            editRaceTraitDescription.value = "";
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

if (spellEffectCreate) {
  spellEffectCreate.addEventListener("click", async () => {
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
  state.draftId = "";
  state.systemNames = {};
  resetVisited();
  setStep("beta-application");
  setLoggedIn(false);
  renderClosedBetaApplication();
});

if (sidebarNav) {
  sidebarNav.addEventListener("click", (event) => {
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
    downloadCharGenDraft();
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
      const payload = await response.json();
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
  state.sessionToken = readStoredSessionToken();
  await loadLocalization(state.locale);
  const verifiedEmail = readVerifiedEmailFromUrl();
  try {
    const session = await api("GET", "/api/session");
    if (session.authenticated) {
      setLoggedIn(true);
      state.accountName = session.username || "";
      state.legacyGuest = !!session.legacyGuest;
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
      <div class="field">
        <label for="closedBetaNda">${t("web.beta.nda", "NDA")}</label>
        <p class="field-hint">${t("web.beta.review_full", "Read and review the full Agreement before continuing.")}</p>
        <div class="nda-scroll-box">
          <textarea id="closedBetaNda" readonly></textarea>
        </div>
      </div>
      <div class="grid two">
        <div class="field checkbox-field">
          <label class="checkbox-label" for="closedBetaAgree">
            <input type="checkbox" id="closedBetaAgree" disabled>
            <span>${t("web.beta.agree", "I have reviewed and agree to the Beta Access NDA v1")}</span>
          </label>
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
  const betaEmailInput = document.getElementById("closedBetaEmail");
  const betaAgreeInput = document.getElementById("closedBetaAgree");
  const betaNdaInput = document.getElementById("closedBetaNda");
  const betaSubmitButton = document.getElementById("closedBetaSubmit");
  let ndaScrollCompletedAt = "";
  let ndaAcceptedAt = "";
  betaNdaInput.value = t("web.loading", "Loading...");
  const hasValidEmail = () => Boolean(String(betaEmailInput.value || "").trim()) && betaEmailInput.checkValidity();
  const syncBetaSubmitState = () => {
    betaSubmitButton.disabled = !betaAgreeInput.checked || !hasValidEmail();
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

function renderCreatePassword(email) {
  setMode("home");
  resetVisited();
  setStep("login");
  const safeEmail = String(email || "").trim();
  view.innerHTML = `
    <section class="panel">
      <h1>${t("web.login.create_password_title", "Create Password")}</h1>
      <p>${escapeHtml(safeEmail)}</p>
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
      <div class="actions-row">
        <div class="left">
          <button class="btn danger ghost" id="createPasswordDeleteAccount" type="button">${t("web.account_delete.opt_out_button", "Delete Account / Opt Out")}</button>
          <button class="btn ghost" id="createPasswordBack" type="button">${t("setup.back", "Back")}</button>
        </div>
        <div class="right">
          <button class="btn" id="createPasswordSubmit" type="button">${t("web.login.create_password", "Create Password")}</button>
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
      });
      finishLogin(result);
    } catch (error) {
      showToast(error.message);
    }
  };
  document.getElementById("createPasswordSubmit").addEventListener("click", createPassword);
  document.getElementById("createPasswordDeleteAccount").addEventListener("click", () => openDeleteAccountModal(safeEmail));
  document.getElementById("createPasswordBack").addEventListener("click", () => renderLogin(safeEmail));
  confirmInput.addEventListener("keypress", (event) => {
    if (event.key === "Enter") {
      createPassword();
    }
  });
  window.requestAnimationFrame(() => {
    passwordInput.focus();
  });
}

function renderPasswordLogin(email) {
  setMode("home");
  resetVisited();
  setStep("login");
  const safeEmail = String(email || "").trim();
  view.innerHTML = `
    <section class="panel">
      <h1>${t("web.login.password_title", "Enter Password")}</h1>
      <p>${escapeHtml(safeEmail)}</p>
      <div class="field">
        <label for="password">${t("web.login.password", "Password")}</label>
        <input type="password" id="password" autocomplete="current-password">
      </div>
      <div class="actions-row">
        <div class="left">
          <button class="btn danger ghost" id="deleteAccountBtn" type="button">${t("web.account_delete.permanent_button", "Permanently Delete Account")}</button>
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
      showToast(error.message);
    }
  };

  document.getElementById("loginBtn").addEventListener("click", submit);
  document.getElementById("passwordBack").addEventListener("click", () => renderLogin(safeEmail));
  document.getElementById("deleteAccountBtn").addEventListener("click", () => openDeleteAccountModal(safeEmail));
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
    meta.textContent = `${drafts.length}/${maxDrafts} ${t("web.home.save_slots", "server save slots used")}`;
    if (transientGuest) {
      meta.textContent = t("web.home.guest_badge", "Guest");
    }
    const newDraftButton = document.getElementById("homeNewDraft");
    const editChooseButton = document.getElementById("homeEditChoose");
    if (newDraftButton) {
      newDraftButton.disabled = !canCreate;
    }
    if (editChooseButton) {
      editChooseButton.disabled = !canCreate;
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
        const description = String(draft.description || "").trim();
        const savedAt = escapeHtml(formatSavedDate(draft.lastSaved));
        return `
          <div class="list-item saved-draft-item">
            <div class="saved-draft-copy">
              <strong>${name}</strong>
              ${description ? `<div class="field-hint">${escapeHtml(description)}</div>` : ""}
              <div class="field-hint">${t("web.home.last_saved", "Last saved")}: ${savedAt}</div>
            </div>
            <div class="saved-draft-actions">
              <button class="btn small" type="button" data-open-draft="${id}">${t("web.home.open_saved", "Open")}</button>
              <button class="btn danger small" type="button" data-delete-draft="${id}" data-delete-draft-name="${name}">${t("web.home.delete_saved", "Delete Save")}</button>
            </div>
          </div>
        `;
      })
      .join("");
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

function renderHome() {
  setMode("home");
  setStep("home");
  view.innerHTML = `
    <section class="panel">
      <h1>${t("web.home.title", "Welcome to GMRules")}</h1>
      <div class="home-intro">
        <p>${t(
          "web.home.intro_ttrpg",
          "A tabletop RPG is a shared story game where players create characters, explore imagined worlds, and use rules and dice to decide what happens."
        )}</p>
        <p>${t(
          "web.home.intro_purpose",
          "GMRules helps you turn your custom game's rules, options, and content into a portable file that can power a digital ecosystem for your creation."
        )}</p>
      </div>
      <p class="home-choice-prompt">${t(
        "web.home.question",
        "Choose whether to create a ruleset, edit an existing ruleset, or create a character."
      )}</p>
      <div class="grid three">
        <div class="field">
          <label>${t("web.home.create_game", "Create New Game")}</label>
          <button class="btn" id="homeNewDraft" type="button">${t("web.splash.start", "Start New Ruleset")}</button>
        </div>
        <div class="field">
          <label for="homeEditFile">${t("web.home.edit_game", "Edit Existing Game")}</label>
          <input class="hidden" type="file" id="homeEditFile" accept=".gmrf">
          <button class="btn ghost" id="homeEditChoose" type="button">${t("web.home.choose_game", "Choose Game File")}</button>
        </div>
        <div class="field">
          <label for="homeCharacterFile">${t("web.home.create_character", "Create Character")}</label>
          <input class="hidden" type="file" id="homeCharacterFile" accept=".gmrf,.gmcf">
          <button class="btn ghost" id="homeCharacterChoose" type="button">${t("web.home.choose_character", "Choose Ruleset or Character File")}</button>
        </div>
      </div>
      <div class="saved-drafts" id="savedDraftsPanel">
        <div class="saved-drafts-header">
          <h2>${t("web.home.saved_title", "Saved on This Server")}</h2>
          <span class="badge" id="savedDraftsMeta">${t("web.loading", "Loading...")}</span>
        </div>
        <div class="list saved-drafts-list" id="savedDraftsList">
          <div class="field-hint">${t("web.loading", "Loading...")}</div>
        </div>
      </div>
      <div class="account-danger-zone">
        <h2>${t("web.account_delete.zone_title", "Account Danger Zone")}</h2>
        <p class="field-hint">${t(
          "web.account_delete.zone_hint",
          "This deletes your account and every saved ruleset for it. To keep work offline, log in, open each ruleset you want to keep, and use Download .gmrf before deleting the account. To delete only one save, use the Delete Save buttons in the saved-ruleset list."
        )}</p>
        <button class="btn danger" id="homeDeleteAccount" type="button">${t("web.account_delete.permanent_button", "Permanently Delete Account")}</button>
      </div>
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
        const draft = parseCharGenDraft(text);
        applyCharGenDraft(draft);
        state.chargenDraftText = text;
        renderCharGenResume();
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

  document.getElementById("homeNewDraft").addEventListener("click", async () => {
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

  document.getElementById("homeDeleteAccount").addEventListener("click", () => {
    openDeleteAccountModal(state.accountName);
  });

  document.getElementById("homeEditChoose").addEventListener("click", async () => {
    const file = await openHomeFilePicker(homeEditFile, "gmrules-edit-game", [".gmrf"]);
    await importGameForEditing(file);
  });

  document.getElementById("homeCharacterChoose").addEventListener("click", async () => {
    const file = await openHomeFilePicker(homeCharacterFile, "gmrules-create-character", [".gmrf", ".gmcf"]);
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
        showToast(t("web.toast.draft_deleted", "Saved ruleset deleted"));
      } catch (error) {
        showToast(error.message);
      }
      return;
    }
    const button = event.target.closest("button[data-open-draft]");
    if (!button) {
      return;
    }
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
        const draft = parseCharGenDraft(text);
        applyCharGenDraft(draft);
        state.chargenDraftText = text;
        renderCharGenResume();
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
      const target = resolveCharGenResumeStage();
      if (target === "classes") {
        renderCharGenClasses();
      } else if (target === "races") {
        renderCharGenRaces();
      } else if (target === "attributes") {
        renderCharGenAttributes();
      } else {
        renderCharGenIntro();
      }
    } catch (error) {
      showToast(error.message);
    }
  });
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
    saveCharGenDraftLocal();
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
    document.getElementById("chargenContinue").addEventListener("click", renderCharGenAttributes);
  } catch (error) {
    showToast(error.message);
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
    const rulesText = buildCharGenRules(method);
    state.chargenAttributes = attributes.slice();

    view.innerHTML = `
      <section class="panel">
        <h1>${t("attrgen.title", "Attribute Generation")}</h1>
        <div class="field">
          <label for="chargenRules">${t("attrgen.rules", "Rules")}</label>
          <textarea id="chargenRules" readonly>${escapeHtml(rulesText)}</textarea>
        </div>
        <div class="field" id="chargenArraySection">
          <label>${t("attrgen.type.standard_array", "Standard Array")}</label>
          <select id="chargenArraySelect"></select>
          <p class="field-hint" id="chargenArrayEmpty">${t("common.none", "None")}</p>
        </div>
        <div class="field">
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
        </div>
        <div class="field">
          <label>${t("attrgen.attributes", "Attributes")}</label>
          <div class="grid two" id="chargenAttributes"></div>
          <p id="chargenAttributesEmpty">${t("attrgen.attributes.empty", "No attributes available.")}</p>
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

    const rolls = [];
    let selectedIndex = -1;
    const rollList = document.getElementById("chargenRollList");
    const rollEmpty = document.getElementById("chargenRollEmpty");
    const rollBtn = document.getElementById("chargenRollBtn");
    const applyBtn = document.getElementById("chargenApplyBtn");
    const arraySection = document.getElementById("chargenArraySection");
    const arraySelect = document.getElementById("chargenArraySelect");
    const arrayEmpty = document.getElementById("chargenArrayEmpty");
    const attributesGrid = document.getElementById("chargenAttributes");
    const attributesEmpty = document.getElementById("chargenAttributesEmpty");

    const attributeInputs = buildCharGenAttributes(attributes, method, attributesGrid);
    applyCharGenSavedScores(attributeInputs);
    wireCharGenArrayUI(method, attributes, attributeInputs, arraySection, arraySelect, arrayEmpty, () => {
      rolls.length = 0;
      selectedIndex = -1;
      rollBaselineValues = captureCharGenAttributeValues(attributeInputs);
      renderRolls();
    });
    maybeApplyCharGenDefaultArray(method, attributes, attributeInputs);
    const baseAttributeValues = captureCharGenAttributeValues(attributeInputs);
    attributesEmpty.style.display = attributes.length ? "none" : "";

    const diceEnabled = isCharGenDiceEnabled(method, attributes);
    rollBtn.disabled = !diceEnabled;
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
      applyBtn.disabled = selectedIndex < 0;
    };

    rollBtn.addEventListener("click", () => {
      if (!diceEnabled) {
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

    document.getElementById("chargenBackToIntro").addEventListener("click", () => {
      state.chargenAttributeScores = collectCharGenAttributeScores(attributeInputs);
      state.chargenAttributes = attributes.slice();
      saveCharGenDraftLocal();
      renderCharGenIntro();
    });
    document.getElementById("chargenAttrContinue").addEventListener("click", () => {
      const scores = collectCharGenAttributeScores(attributeInputs);
      state.chargenAttributeScores = scores;
      state.chargenAttributes = attributes.slice();
      state.chargenPointBuyBaselineScores = { ...scores };
      saveCharGenDraftLocal();
      if (isCharGenPointBuySelected(method)) {
        renderCharGenPointsBuy();
        return;
      }
      renderCharGenRaces();
    });

    renderRolls();
  } catch (error) {
    showToast(error.message);
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

    const scores = state.chargenAttributeScores || {};
    const baseline = state.chargenPointBuyBaselineScores || {};
    const additive = shouldAddCharGenPointBuyToBaseScores(method);

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
        const baselineValue = clampCharGen(Number(baseline[attributeId] ?? base), min, max);
        rows.push({ attributeId, inputId: id, minusId, plusId, resetId, min, max, baselineValue });
        const baselineBadge = additive ? `<span class="badge">${t("attrgen.point.baseline", "Baseline")}: ${baselineValue}</span>` : "";
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

    const pointCostMap = buildCharGenPointCostMap(method.pointCosts);
    const budget = Math.max(0, Number(method.basePoints || 0));
    const minSpend = Math.max(0, Number(method.minimumPointsToSpend || 0));

    const resolveCost = (value) => resolveCharGenPointCost(value, method, pointCostMap);

    const computeSpent = () => {
      let spent = 0;
      rows.forEach((row) => {
        const input = document.getElementById(row.inputId);
        const value = clampCharGen(Number(input.value || 0), row.min, row.max);
        const baselineValue = additive ? row.baselineValue : 0;
        if (!additive) {
          spent += resolveCost(value);
        } else {
          spent += resolveCost(value) - resolveCost(baselineValue);
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
        const next = additive ? row.baselineValue : resolveCharGenBase(method);
        input.value = String(clampCharGen(Number(next || 0), row.min, row.max));
        clampInput();
      });
    });

    renderSummary();

    document.getElementById("chargenPointBack").addEventListener("click", () => {
      saveCharGenDraftLocal();
      renderCharGenAttributes();
    });
    continueBtn.addEventListener("click", () => {
      saveCharGenDraftLocal();
      renderCharGenRaces();
    });
  } catch (error) {
    showToast(error.message);
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

    view.innerHTML = `
      <section class="panel">
        <h1>${t("races.title", "Races")}</h1>
        <div class="field">
          <label for="chargenRaceSelect">${t("races.select", "Select Race")}</label>
          <select id="chargenRaceSelect"></select>
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
      renderCharGenAttributes();
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
    showToast(error.message);
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

    view.innerHTML = `
      <section class="panel">
        <h1>${t("classes.title", "Classes")}</h1>
        <div class="field">
          <label for="chargenClassSelect">${t("classes.select", "Select Class")}</label>
          <select id="chargenClassSelect"></select>
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
      saveCharGenDraftLocal();
      showToast(t("web.chargen.next", "Character creation screens coming next."));
    });
  } catch (error) {
    showToast(error.message);
  }
}

function buildCharGenRules(method) {
  const type = formatCharGenType(method.generationType);
  const sets = Number(method.numberOfSets || 0);
  const selection = emptyFallback(method.setSelectionMethod);
  const assignInOrder = method.assignInOrder ? t("common.yes", "Yes") : t("common.no", "No");
  const terms = Array.isArray(method.diceTerms) ? method.diceTerms : [];
  const diceText = terms.length
    ? terms.map((term) => term.notation || formatCharGenDiceTerm(term)).join(", ")
    : t("common.none", "None");
  return [
    `${t("attrgen.type", "Generation Type")}: ${type}`,
    `${t("attrgen.sets", "Attribute Sets")}: ${sets}`,
    `${t("attrgen.selection", "Set Selection")}: ${selection}`,
    `${t("attrgen.assign", "Assign In Order")}: ${assignInOrder}`,
    `${t("attrgen.dice", "Dice Terms")}: ${diceText}`,
  ].join("\n");
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
    gameId: String(state.chargenGameId || ""),
    gameHash: String(state.chargenGameHash || ""),
    raceId: String(state.chargenRaceId || ""),
    classId: String(state.chargenClassId || ""),
    attributeScores: state.chargenAttributeScores || {},
  };
}

function serializeCharGenDraft(draft) {
  const safeDraft = draft || {};
  const lines = ["GMRulesCharacterFile v1"];
  lines.push(`gameId=${safeDraft.gameId || ""}`);
  lines.push(`gameHash=${safeDraft.gameHash || ""}`);
  if (safeDraft.raceId) {
    lines.push(`raceId=${safeDraft.raceId}`);
  }
  if (safeDraft.classId) {
    lines.push(`classId=${safeDraft.classId}`);
  }
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
    gameId: "",
    gameHash: "",
    raceId: "",
    classId: "",
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
    if (key === "gameId") {
      draft.gameId = value;
    } else if (key === "gameHash") {
      draft.gameHash = value;
    } else if (key === "raceId") {
      draft.raceId = value;
    } else if (key === "classId") {
      draft.classId = value;
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
  state.chargenGameId = String(safeDraft.gameId || "");
  state.chargenGameHash = String(safeDraft.gameHash || "");
  state.chargenRaceId = String(safeDraft.raceId || "");
  state.chargenClassId = String(safeDraft.classId || "");
  state.chargenAttributeScores = safeDraft.attributeScores || {};
}

function resetCharGenState() {
  state.chargenAttributes = [];
  state.chargenAttributeScores = {};
  state.chargenPointBuyBaselineScores = {};
  state.chargenRaceId = "";
  state.chargenClassId = "";
  state.chargenGameId = "";
  state.chargenGameHash = "";
  state.chargenGameName = "";
  state.chargenDraftText = "";
}

function saveCharGenDraftLocal() {
  const draft = buildCharGenDraft();
  const text = serializeCharGenDraft(draft);
  state.chargenDraftText = text;
  try {
    localStorage.setItem("gmrules.chargen.draft", text);
  } catch (error) {
    // Ignore storage failures.
  }
}

function downloadCharGenDraft() {
  if (!state.chargenGameId || !state.chargenGameHash) {
    showToast(t("web.chargen.missing", "Upload a ruleset to save this character."));
    return;
  }
  const text = serializeCharGenDraft(buildCharGenDraft());
  const blob = new Blob([text], { type: "text/plain" });
  const filename = buildCharGenFilename();
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
  showToast(t("web.toast.downloaded", "Downloaded"));
}

function buildCharGenFilename() {
  const base = state.chargenGameName || "character";
  const safe = sanitizeFilename(`${base}_character`);
  return safe.toLowerCase().endsWith(".gmcf") ? safe : `${safe}.gmcf`;
}

function sanitizeFilename(value) {
  return String(value || "")
    .replace(/[\\/:*?"<>|]/g, "_")
    .trim();
}

function resolveCharGenResumeStage() {
  if (state.chargenClassId) {
    return "classes";
  }
  if (state.chargenRaceId) {
    return "races";
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
    return t("attrgen.type.standard_array", "Standard Array");
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

function shouldAddCharGenPointBuyToBaseScores(method) {
  const safeMethod = method || {};
  const type = String(safeMethod.generationType || "").trim().toLowerCase();
  if (type !== "hybrid") {
    return false;
  }
  const hybridStages = normalizeHybridStages(safeMethod.hybridStages);
  if (!hybridStages.length) {
    return true;
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
  return baselineIndex >= 0 && pointIndex > baselineIndex;
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

function wireCharGenArrayUI(method, attributes, inputs, section, select, emptyLabel, onApplied) {
  if (!section || !select || !emptyLabel) {
    return;
  }
  const standardMap = buildCharGenArrayMap(method.standardArray);
  const eliteMap = buildCharGenArrayMap(method.eliteArray);
  const hasStandard = Object.keys(standardMap).length > 0;
  const hasElite = Object.keys(eliteMap).length > 0;
  if (!hasStandard && !hasElite) {
    section.style.display = "none";
    return;
  }
  section.style.display = "";
  const options = [`<option value="">${t("common.none", "None")}</option>`];
  if (hasStandard) {
    options.push(`<option value="standard">${t("attrgen.type.standard_array", "Standard Array")}</option>`);
  }
  if (hasElite) {
    options.push(`<option value="elite">${t("attrgen.arrays.elite.section", "Elite Arrays")}</option>`);
  }
  select.innerHTML = options.join("");
  const preferred = String(method.defaultArrayType || "").trim().toLowerCase();
  if (preferred === "elite" && hasElite) {
    select.value = "elite";
  } else if (preferred === "standard" && hasStandard) {
    select.value = "standard";
  } else if (hasStandard) {
    select.value = "standard";
  } else if (hasElite) {
    select.value = "elite";
  } else {
    select.value = "";
  }

  const updateHint = () => {
    const key = String(select.value || "");
    if (key === "standard") {
      emptyLabel.textContent = t("attrgen.type.standard_array", "Standard Array");
    } else if (key === "elite") {
      emptyLabel.textContent = t("attrgen.arrays.elite.section", "Elite Arrays");
    } else {
      emptyLabel.textContent = t("common.none", "None");
    }
  };
  updateHint();
  select.addEventListener("change", () => {
    updateHint();
    const key = String(select.value || "");
    if (!key) {
      return;
    }
    const values = key === "elite" ? eliteMap : standardMap;
    applyCharGenArrayPreset(attributes, method, inputs, values);
    state.chargenAttributeScores = collectCharGenAttributeScores(inputs);
    saveCharGenDraftLocal();
    if (typeof onApplied === "function") {
      onApplied();
    }
  });
}

function maybeApplyCharGenDefaultArray(method, attributes, inputs) {
  if (Object.keys(state.chargenAttributeScores || {}).length) {
    return;
  }
  const safeMethod = method || {};
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
    const gameTypes = data.gameTypes || [];
    const options = [""].concat(gameTypes).map((type) => {
      const label = type || t("setup.game.type.placeholder", "Select a type");
      const selected = type === data.gameType ? "selected" : "";
      return `<option value="${type}" ${selected}>${label}</option>`;
    });

    view.innerHTML = `
      <section class="panel">
        <h1>${t("setup.title", "Game Setup")}</h1>
        <p class="field-hint">${t(
          "web.download.note",
          "Game rules are automatically saved to your account for easy access. Use the Download .gmrf button in the top bar anytime if you'd like to save the file to your computer."
        )}</p>
        <div class="grid">
          <div class="field">
            <label for="gameName">${t("setup.game.name", "Game Name")}</label>
            <input type="text" id="gameName" value="${escapeHtml(data.name)}">
            <div class="field-hint">${t(
              "setup.game.name.hint",
              "The game name becomes the filename (.gmrf). Files are saved in your home folder under GameMakerFiles."
            )}</div>
          </div>
          <div class="field">
            <label for="gameDescription">${t("setup.game.description", "Game Description")}</label>
            <textarea id="gameDescription">${escapeHtml(data.description)}</textarea>
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
    document.getElementById("setupContinue").addEventListener("click", async () => {
      const name = document.getElementById("gameName").value.trim();
      if (!name) {
        showToast(t("common.name.required", "Name is required."));
        return;
      }
      const description = document.getElementById("gameDescription").value;
      const gameType = document.getElementById("gameType").value;
      try {
        await api("POST", `/api/drafts/${state.draftId}/setup`, { name, description, gameType });
        markSaved(t("web.toast.setup_saved", "Setup saved"));
        renderMeasurements();
      } catch (error) {
        showToast(error.message);
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
        return `
          <div class="list-item">
            <div><strong>${name}</strong> ${displayAmount} ${displayUnit}</div>
            <div>
              <button class="btn danger small" data-remove-time-unit="${key}">${t("common.remove", "Remove")}</button>
            </div>
          </div>
        `;
      })
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${t("measurements.title", "Weights & Measures")}</h1>
        <p class="field-hint">${t(
          "measurements.intro",
          "Time units help define how long things take in your game, from combat order like rounds and turns to longer actions such as travel, rest, or crafting."
        )}</p>
        <div class="field">
          <label for="weightSystem">${t("measurements.weight.label", "Weight System")}</label>
          <select id="weightSystem">${weightOptions}</select>
        </div>
        <h2>${t("measurements.timeUnits.title", "Time Units")}</h2>
        <div class="grid three">
          <div class="field">
            <label for="timeUnitName">${t("measurements.timeUnits.name", "Unit Name")}</label>
            <input type="text" id="timeUnitName" placeholder="${t("measurements.timeUnits.name.placeholder", "e.g., round")}">
          </div>
          <div class="field">
            <label for="timeUnitAmount">${t("measurements.timeUnits.amount", "Amount")}</label>
            <input type="number" id="timeUnitAmount" min="1" step="1" placeholder="${t("measurements.timeUnits.duration.placeholder", "e.g., 6")}">
          </div>
          <div class="field">
            <label for="timeUnitBase">${t("measurements.timeUnits.base", "Unit")}</label>
            <select id="timeUnitBase">${baseUnitOptions}</select>
          </div>
        </div>
        <button class="btn" id="addTimeUnit" type="button">${t("measurements.timeUnits.add", "Add Time Unit")}</button>
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

    document.getElementById("addTimeUnit").addEventListener("click", async () => {
      const name = document.getElementById("timeUnitName").value.trim();
      const amountRaw = document.getElementById("timeUnitAmount").value.trim();
      const amount = Number.parseInt(amountRaw, 10);
      const baseDuration = Number.parseInt(document.getElementById("timeUnitBase").value, 10);
      if (!name) {
        showToast(t("common.name.required", "Name is required."));
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
      const updated = [];
      let replaced = false;
      timeUnits.forEach((unit) => {
        if (String(unit.name || "").trim().toLowerCase() === name.toLowerCase()) {
          updated.push({ name, duration });
          replaced = true;
        } else {
          updated.push(unit);
        }
      });
      if (!replaced) {
        updated.push({ name, duration });
      }
      try {
        await saveMeasurements(updated, "web.toast.time_unit_added", "Time unit added");
        renderMeasurements();
      } catch (error) {
        showToast(error.message);
      }
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
        (range) => `
          <div class="list-item">
            <span>${range.min} - ${range.max}</span>
            <button class="btn danger small" data-min="${range.min}" data-max="${range.max}">${t("common.remove", "Remove")}</button>
          </div>
        `
      )
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${t("dice.title", "Dice Options")}</h1>
        <p class="field-hint">${t(
          "dice.intro",
          "In RPGs, dice rolls are often used to create characters, randomize selections, or resolve outcomes. Did your attack succeed? How much damage did that spell do? What treasures are contained in the dragon's hoard, or the corporate CEO's safe? Select which dice your game system uses, and enter any custom ranges you need."
        )}</p>
        <h2>${t("dice.standard", "Standard Dice")}</h2>
        <div class="toggle-group" id="standardDice">${toggles}</div>
        <h2>${t("dice.custom", "Custom Ranges")}</h2>
        <div class="grid two">
          <div class="field">
            <label for="customMin">${t("dice.min", "Min")}</label>
            <input type="number" id="customMin" value="1">
          </div>
          <div class="field">
            <label for="customMax">${t("dice.max", "Max")}</label>
            <input type="number" id="customMax" value="6">
          </div>
        </div>
        <button class="btn" id="addRange" type="button">${t("dice.add", "Add Range")}</button>
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

    document.getElementById("addRange").addEventListener("click", async () => {
      const min = Number(document.getElementById("customMin").value);
      const max = Number(document.getElementById("customMax").value);
      try {
        await api("POST", `/api/drafts/${state.draftId}/dice/custom`, { min, max });
        markSaved(t("web.toast.range_added", "Range added"));
        renderDice();
      } catch (error) {
        showToast(error.message);
      }
    });

    document.querySelectorAll("#rangeList button").forEach((button) => {
      button.addEventListener("click", async () => {
        const min = Number(button.dataset.min);
        const max = Number(button.dataset.max);
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
    document.getElementById("diceContinue").addEventListener("click", renderAttributeGeneration);
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
        return `
          <div class="list-item">
            <span>${escapeHtml(type.displayName)}</span>
            <div class="actions">
              <button class="btn ghost small" data-edit-type="${type.key}">${t("common.edit", "Edit")}</button>
              <button class="btn danger small" data-key="${type.key}">${t("common.remove", "Remove")}</button>
            </div>
          </div>
        `;
      })
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${escapeHtml(title)}</h1>
        <p class="field-hint">${t(
          "attrtypes.intro",
          "Attribute categories are optional groups, not the attributes themselves. For example, Vampire: The Masquerade groups attributes into Physical, Social, and Mental, while many versions of D&D skip categories entirely."
        )}</p>
        ${renderSystemNameControls(systemName, t("attrtypes.title", "Attribute Categories"))}
        <button class="btn" id="addType" type="button">${t("attrtypes.add", "Add Category")}</button>
        <div class="list" id="typeList">${list || `<div class="list-item">${t("web.attrtypes.none", "No categories yet.")}</div>`}</div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToGeneration" type="button">${t("setup.back", "Back")}</button>
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

    document.querySelectorAll("#typeList [data-key]").forEach((button) => {
      button.addEventListener("click", async () => {
        const key = button.dataset.key;
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

    document.getElementById("backToGeneration").addEventListener("click", navigateBackInApp);
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
    state.applyAttributeModifiersToAllAttributes = Boolean(data.applyAttributeModifiersToAllAttributes);
    state.attributeModifiers = normalizeModifierEntries(data.attributeModifiers || []);
    state.defaultAttributeMinScore = Number(data.defaultAttributeMinScore || 0);
    state.defaultAttributeMaxScore = Number(data.defaultAttributeMaxScore || 0);
    const systemName = String(data.systemName || "");
    const title = systemNameTitle(systemName, t("attributes.title", "Attributes"));
    const typeOptions = [`<option value="">${t("attrtypes.none", "None")}</option>`]
      .concat((data.types || []).map((type) => `<option value="${type.key}">${escapeHtml(type.displayName)}</option>`))
      .join("");

    const attributeMap = {};
    const list = (data.attributes || [])
      .map((attr) => {
        attributeMap[attr.id] = attr;
        return `
          <div class="list-item">
            <div>
              <div>${escapeHtml(attr.displayName)}</div>
              <div class="badge">${escapeHtml(attr.typeName || t("attrtypes.none", "None"))}</div>
            </div>
            <div class="actions">
              <button class="btn ghost small" data-edit-attr="${attr.id}">${t("common.edit", "Edit")}</button>
              <button class="btn ghost small" data-edit-type="${attr.id}">${t("web.attributes.change_type", "Change Category")}</button>
              <button class="btn danger small" data-remove="${attr.id}">${t("common.remove", "Remove")}</button>
            </div>
          </div>
        `;
      })
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${escapeHtml(title)}</h1>
        ${renderSystemNameControls(systemName, t("attributes.title", "Attributes"))}
        <p class="field-hint">${t(
          "attributes.intro",
          "Attributes are the core stats for characters, such as Strength or Intelligence. Add the attributes your game uses, and optionally assign them to a category."
        )}</p>
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
      openAttributeCreate(data.types || []);
    });

    document.querySelectorAll("[data-remove]").forEach((button) => {
      button.addEventListener("click", async () => {
        const id = button.dataset.remove;
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
        openAttributeEditor(attributeMap[id], data.types || []);
      });
    });

    document.getElementById("backToTypes").addEventListener("click", navigateBackInApp);
    document.getElementById("attributesContinue").addEventListener("click", renderHitPoints);
    wireSystemNameSave("attributes", () => renderAttributes());
    if (openId) {
      const target = attributeMap[openId];
      if (target) {
        openAttributeEditor(target, data.types || []);
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
    return "attribute-types";
  }
  const safeStep = String(currentStep || "");
  const currentIndex = order.indexOf(safeStep);
  if (currentIndex < 0) {
    return order[0];
  }
  return order[currentIndex + 1] || "attribute-types";
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
  if (safeStep === "attribute-types") {
    return order[order.length - 1] || "attribute-generation";
  }
  if (safeStep === "hit-points") {
    return "attributes";
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
  return "attribute-types";
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

    const resolveSelection = () => {
      const isHybrid = state.attributeGenerationType === "hybrid";
      const hybridStages = state.attributeGenerationStages;
      const includeAll = isHybrid && !hybridStages.length;
      return {
        dice: isHybrid ? includeAll || hybridStages.includes("dice") : state.attributeGenerationType === "dice",
        point: isHybrid ? includeAll || hybridStages.includes("point_buy") : state.attributeGenerationType === "point_buy",
        standard: isHybrid
          ? includeAll || hybridStages.includes("standard_array")
          : state.attributeGenerationType === "standard_array",
      };
    };

    const selection = resolveSelection();
    const diceChecked = selection.dice;
    const pointChecked = selection.point;
    const standardChecked = selection.standard;
    const defaultScoreRangeChecked = usesDefaultAttributeScoreRange();
    let defaultModifiers = getStandardAttributeModifiers();

    view.innerHTML = `
      <section class="panel">
        <h1>${t("attrgen.title", "Attribute Generation")}</h1>
        <p class="field-hint">${t(
          "attrgen.intro",
          "Choose how players generate attribute scores: dice rolling for randomness, point buy for controlled balance, or a standard array for a fixed spread. You can select more than one to support hybrid systems."
        )}</p>
        <div class="toggle-group" id="generationOptions">
          <label class="toggle"><input type="checkbox" id="genStandard" ${standardChecked ? "checked" : ""}> ${t("attrgen.type.standard_array", "Standard Array")}</label>
          <label class="toggle"><input type="checkbox" id="genDice" ${diceChecked ? "checked" : ""}> ${t("attrgen.type.dice", "Dice Rolling")}</label>
          <label class="toggle"><input type="checkbox" id="genPoint" ${pointChecked ? "checked" : ""}> ${t("attrgen.type.point_buy", "Point Buy")}</label>
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
          <button class="btn ghost" id="saveDefaultScoreRange" type="button">${t("attrgen.score_limits.save", "Save Score Limits")}</button>
        </div>
        <div class="edit-section" id="defaultModifierSection">
          <h2>${t("attrgen.modifiers.title", "Default Modifiers")}</h2>
          <label class="toggle">
            <input type="checkbox" id="defaultModifiersEnabled" ${state.applyAttributeModifiersToAllAttributes ? "checked" : ""}>
            ${t("attrgen.modifiers.same_all", "All attributes use the same modifier list")}
          </label>
          <div class="grid two" id="defaultModifierFields">
            <div class="field">
              <label for="defaultModifierScore">${t("attributes.edit.modifier.score", "Score")}</label>
              <input type="number" id="defaultModifierScore" step="0.1">
            </div>
            <div class="field">
              <label for="defaultModifierValue">${t("attributes.edit.modifier.value", "Modifier")}</label>
              <input type="number" id="defaultModifierValue" step="0.1">
            </div>
          </div>
          <button class="btn ghost" id="addDefaultModifier" type="button">${t("attributes.edit.modifier.add", "Add Modifier")}</button>
          <div class="list" id="defaultModifierList"></div>
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToDice" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn" id="generationContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    const applySelection = () => {
      const current = resolveSelection();
      document.getElementById("genDice").checked = current.dice;
      document.getElementById("genPoint").checked = current.point;
      document.getElementById("genStandard").checked = current.standard;
    };

    const updateDefaultScoreControls = () => {
      const enabled = document.getElementById("defaultScoreRange").checked;
      document.getElementById("defaultScoreMin").disabled = !enabled;
      document.getElementById("defaultScoreMax").disabled = !enabled;
      document.getElementById("saveDefaultScoreRange").disabled = !enabled;
      document.getElementById("defaultModifierSection").classList.toggle("hidden", !enabled);
      const modifiersEnabled = enabled && document.getElementById("defaultModifiersEnabled").checked;
      document.getElementById("defaultModifierScore").disabled = !modifiersEnabled;
      document.getElementById("defaultModifierValue").disabled = !modifiersEnabled;
      document.getElementById("addDefaultModifier").disabled = !modifiersEnabled;
    };

    const renderDefaultModifiers = () => {
      const list = document.getElementById("defaultModifierList");
      if (!defaultModifiers.length) {
        list.innerHTML = `<div class="list-item">${t("attributes.edit.modifiers.none", "No modifiers yet.")}</div>`;
        return;
      }
      list.innerHTML = defaultModifiers
        .map(
          (entry, index) => `
            <div class="list-item">
              <span>${entry.score} -> ${entry.modifier}</span>
              <button class="btn danger small" data-remove-default-modifier="${index}">${t("common.remove", "Remove")}</button>
            </div>
          `
        )
        .join("");
    };

    const readDefaultScoreRange = () => {
      const enabled = document.getElementById("defaultScoreRange").checked;
      if (!enabled) {
        return {
          defaultAttributeMinScore: 0,
          defaultAttributeMaxScore: 0,
        };
      }
      const minScore = Number.parseInt(document.getElementById("defaultScoreMin").value, 10);
      const maxScore = Number.parseInt(document.getElementById("defaultScoreMax").value, 10);
      if (!Number.isFinite(minScore) || !Number.isFinite(maxScore)) {
        showToast(t("attrgen.score_limits.invalid", "Enter a valid score range."));
        return null;
      }
      if (minScore > maxScore) {
        showToast(t("attrgen.score_limits.order.invalid", "Minimum score cannot exceed maximum."));
        return null;
      }
      if (minScore === 0 && maxScore === 0) {
        showToast(t("attrgen.score_limits.zero.invalid", "Use non-zero limits or clear the checkbox."));
        return null;
      }
      return {
        defaultAttributeMinScore: minScore,
        defaultAttributeMaxScore: maxScore,
      };
    };

    const syncSelection = async () => {
      const dice = document.getElementById("genDice").checked;
      const point = document.getElementById("genPoint").checked;
      const standard = document.getElementById("genStandard").checked;
      const defaultScoreRange = readDefaultScoreRange();
      if (!defaultScoreRange) {
        return false;
      }
      const hasDefaultScoreRange = defaultScoreRange.defaultAttributeMinScore !== 0
        || defaultScoreRange.defaultAttributeMaxScore !== 0;
      const applyAttributeModifiers = document.getElementById("defaultModifiersEnabled").checked
        && hasDefaultScoreRange;
      const selected = [];
      if (standard) {
        selected.push("standard_array");
      }
      if (dice) {
        selected.push("dice");
      }
      if (point) {
        selected.push("point_buy");
      }
      if (!selected.length) {
        applySelection();
        return false;
      }
      let generationType = "";
      let hybridStages = [];
      if (selected.length === 1) {
        generationType = selected[0];
      } else {
        generationType = "hybrid";
        hybridStages = selected;
      }
      try {
        await api("POST", `/api/drafts/${state.draftId}/attribute-generation`, {
          generationType,
          hybridStages,
          defaultAttributeMinScore: defaultScoreRange.defaultAttributeMinScore,
          defaultAttributeMaxScore: defaultScoreRange.defaultAttributeMaxScore,
          applyAttributeModifiersToAllAttributes: applyAttributeModifiers,
          attributeModifiers: defaultModifiers,
        });
        state.attributeGenerationType = generationType;
        state.attributeGenerationStages = normalizeHybridStages(hybridStages);
        state.defaultAttributeMinScore = defaultScoreRange.defaultAttributeMinScore;
        state.defaultAttributeMaxScore = defaultScoreRange.defaultAttributeMaxScore;
        state.applyAttributeModifiersToAllAttributes = applyAttributeModifiers;
        state.attributeModifiers = normalizeModifierEntries(defaultModifiers);
        markSaved(t("web.toast.generation_updated", "Generation updated"));
        return true;
      } catch (error) {
        showToast(error.message);
      }
      return false;
    };

    updateDefaultScoreControls();
    renderDefaultModifiers();
    document.getElementById("genDice").addEventListener("change", syncSelection);
    document.getElementById("genPoint").addEventListener("change", syncSelection);
    document.getElementById("genStandard").addEventListener("change", syncSelection);
    document.getElementById("defaultScoreRange").addEventListener("change", async () => {
      if (!document.getElementById("defaultScoreRange").checked) {
        document.getElementById("defaultModifiersEnabled").checked = false;
      }
      updateDefaultScoreControls();
      await syncSelection();
    });
    document.getElementById("defaultModifiersEnabled").addEventListener("change", async () => {
      updateDefaultScoreControls();
      await syncSelection();
    });
    document.getElementById("addDefaultModifier").addEventListener("click", async () => {
      const score = Number(document.getElementById("defaultModifierScore").value || 0);
      const modifier = Number(document.getElementById("defaultModifierValue").value || 0);
      if (!Number.isFinite(score) || !Number.isFinite(modifier)) {
        showToast(t("attrgen.modifiers.invalid", "Enter a valid modifier."));
        return;
      }
      defaultModifiers.push({ score, modifier });
      defaultModifiers = normalizeModifierEntries(defaultModifiers);
      renderDefaultModifiers();
      await syncSelection();
    });
    document.getElementById("defaultModifierList").addEventListener("click", async (event) => {
      const button = event.target.closest("button[data-remove-default-modifier]");
      if (!button) {
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
    document.getElementById("saveDefaultScoreRange").addEventListener("click", syncSelection);

    document.getElementById("backToDice").addEventListener("click", navigateBackInApp);
    document.getElementById("generationContinue").addEventListener("click", async () => {
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
          <h1>${t("attrgen.standard.title", "Standard Arrays")}</h1>
          <p class="field-hint">${t(
            "attrgen.standard.disabled",
            "Standard Array is not selected in Attribute Generation, so this screen is locked to prevent unused array data from being edited."
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
    const attributeOptions = attributes
      .map((attr) => `<option value="${attr.id}">${escapeHtml(attr.displayName)}</option>`)
      .join("");

    const renderList = (items, target) =>
      (items || [])
        .map(
          (entry) => `
        <div class="list-item">
          <span>${escapeHtml(entry)}</span>
          <button class="btn danger small" data-${target}="${escapeHtml(entry)}">${t("common.remove", "Remove")}</button>
        </div>
      `
        )
        .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${t("attrgen.standard.title", "Standard Arrays")}</h1>
        <p class="field-hint">${t(
          "attrgen.standard.intro",
          "Define fixed attribute arrays and choose which one is the default option."
        )}</p>
        <h2>${t("attrgen.arrays.section", "Standard Arrays")}</h2>
        <div class="grid two">
          <div class="field">
            <label>${t("attrgen.arrays.attribute", "Attribute")}</label>
            <select id="standardAttribute">${attributeOptions}</select>
          </div>
          <div class="field">
            <label>${t("attrgen.arrays.value", "Array Value")}</label>
            <input type="number" id="standardValue" value="0">
          </div>
        </div>
        <button class="btn" id="addStandard" type="button">${t("attrgen.arrays.add", "Add Value")}</button>
        <div class="list" id="standardList">${renderList(data.standardArray, "standard") || `<div class="list-item">${t("web.standard_array.none", "No entries yet.")}</div>`}</div>

        <h2>${t("attrgen.arrays.elite.section", "Elite Arrays")}</h2>
        <div class="grid two">
          <div class="field">
            <label>${t("attrgen.arrays.attribute", "Attribute")}</label>
            <select id="eliteAttribute">${attributeOptions}</select>
          </div>
          <div class="field">
            <label>${t("attrgen.arrays.value", "Array Value")}</label>
            <input type="number" id="eliteValue" value="0">
          </div>
        </div>
        <button class="btn" id="addElite" type="button">${t("attrgen.arrays.add", "Add Value")}</button>
        <div class="list" id="eliteList">${renderList(data.eliteArray, "elite") || `<div class="list-item">${t("web.standard_array.none", "No entries yet.")}</div>`}</div>

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

    document.getElementById("addStandard").addEventListener("click", async () => {
      const attributeId = document.getElementById("standardAttribute").value;
      const value = Number(document.getElementById("standardValue").value);
      try {
        await api("POST", `/api/drafts/${state.draftId}/standard-array/standard`, { attributeId, value });
        markSaved(t("web.toast.standard_array_updated", "Standard array updated"));
        renderStandardArray();
      } catch (error) {
        showToast(error.message);
      }
    });

    document.getElementById("addElite").addEventListener("click", async () => {
      const attributeId = document.getElementById("eliteAttribute").value;
      const value = Number(document.getElementById("eliteValue").value);
      try {
        await api("POST", `/api/drafts/${state.draftId}/standard-array/elite`, { attributeId, value });
        markSaved(t("web.toast.elite_array_updated", "Elite array updated"));
        renderStandardArray();
      } catch (error) {
        showToast(error.message);
      }
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

    document.getElementById("backToGeneration").addEventListener("click", navigateBackInApp);
    document.getElementById("standardContinue").addEventListener("click", () => {
      navigateToStep(getNextAttributeGenerationStep("standard-array"));
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
    const terms = (data.terms || [])
      .map(
        (term, index) => `
          <div class="list-item">
            <span>${escapeHtml(term.notation)}</span>
            <button class="btn danger small" data-index="${index}">${t("common.remove", "Remove")}</button>
          </div>
        `
      )
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${t("attrgen.dice.title", "Dice Rolling")}</h1>
        <p class="field-hint">${t(
          "attrgen.dice.intro",
          "Set how many attribute sets are rolled and define the dice terms used for each roll."
        )}</p>
        <div class="grid two">
          <div class="field">
            <label>${t("attrgen.sets.count", "Number of Sets")}</label>
            <input type="number" id="setCount" value="${data.numberOfSets}">
          </div>
          <div class="field">
            <label>${t("attrgen.sets.method", "Selection Method")}</label>
            <input type="text" id="setMethod" value="${escapeHtml(data.setSelectionMethod)}">
          </div>
        </div>
        <button class="btn" id="applyMethod" type="button">${t("attrgen.sets.apply", "Apply")}</button>

        <h2>${t("attrgen.dice.substitution.section", "Dice Substitution")}</h2>
        <div class="grid two">
          <div class="field">
            <label for="allowDiceSubstitution">${t("attrgen.dice.substitution.enable", "Allow substitution")}</label>
            <input type="checkbox" id="allowDiceSubstitution" ${data.allowDiceSubstitution ? "checked" : ""}>
          </div>
          <div class="field">
            <label for="diceSubstitutionValue">${t("attrgen.dice.substitution.value", "Substitution value")}</label>
            <input type="number" id="diceSubstitutionValue" min="0" step="1" value="${escapeHtml(
              String(Math.max(0, Number(data.diceSubstitutionValue || 14)))
            )}">
          </div>
        </div>
        <div class="grid two">
          <div class="field">
            <label for="maxDiceSubstitutions">${t("attrgen.dice.substitution.count", "Max substitutions")}</label>
            <input type="number" id="maxDiceSubstitutions" min="0" step="1" value="${escapeHtml(
              String(Math.max(0, Number(data.maxDiceSubstitutions || 1)))
            )}">
          </div>
          <div class="field">
            <label>&nbsp;</label>
            <button class="btn ghost" id="saveDiceSubstitution" type="button">${t("common.save", "Save")}</button>
          </div>
        </div>

        <h2>${t("attrgen.dice.section", "Dice Terms")}</h2>
        <div class="grid two">
          <div class="field">
            <label>${t("attrgen.dice.count", "Number of Rolls")}</label>
            <input type="number" id="diceCount" value="3">
          </div>
          <div class="field">
            <label>${t("attrgen.dice.sides", "Dice Sides")}</label>
            <select id="diceSides">${diceSideOptions}</select>
          </div>
        </div>
        <div class="grid two">
          <div class="field">
            <label for="diceReroll">${t("attrgen.dice.reroll", "Reroll Below")}</label>
            <input type="number" id="diceReroll" min="0" step="1" value="0">
          </div>
          <div class="field">
            <label for="diceDropLowest">${t("attrgen.dice.drop_lowest", "Drop lowest roll")}</label>
            <input type="checkbox" id="diceDropLowest">
          </div>
        </div>
        <button class="btn" id="addTerm" type="button">${t("attrgen.dice.add", "Add Dice Term")}</button>
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
      const numberOfSets = Number(event.target.value);
      try {
        await api("POST", `/api/drafts/${state.draftId}/dice-rolling/sets`, { numberOfSets });
        markSaved(t("web.toast.sets_updated", "Sets updated"));
      } catch (error) {
        showToast(error.message);
      }
    });

    document.getElementById("applyMethod").addEventListener("click", async () => {
      const setSelectionMethod = document.getElementById("setMethod").value;
      try {
        await api("POST", `/api/drafts/${state.draftId}/dice-rolling/method`, { setSelectionMethod });
        markSaved(t("web.toast.method_updated", "Method updated"));
      } catch (error) {
        showToast(error.message);
      }
    });

    document.getElementById("saveDiceSubstitution").addEventListener("click", async () => {
      const allowDiceSubstitution = document.getElementById("allowDiceSubstitution").checked;
      const diceSubstitutionValue = Number(document.getElementById("diceSubstitutionValue").value || 0);
      const maxDiceSubstitutions = Number(document.getElementById("maxDiceSubstitutions").value || 0);
      try {
        await api("POST", `/api/drafts/${state.draftId}/dice-rolling/substitution`, {
          allowDiceSubstitution,
          diceSubstitutionValue,
          maxDiceSubstitutions,
        });
        markSaved(t("web.toast.dice_substitution_updated", "Dice substitution updated"));
        renderDiceRolling();
      } catch (error) {
        showToast(error.message);
      }
    });

    const diceSidesSelect = document.getElementById("diceSides");
    const diceReroll = document.getElementById("diceReroll");
    const updateRerollMax = () => {
      const sides = Number(diceSidesSelect.value || 0);
      diceReroll.max = sides > 0 ? String(sides) : "1000";
      if (sides > 0 && Number(diceReroll.value || 0) > sides) {
        diceReroll.value = String(sides);
      }
    };
    updateRerollMax();
    diceSidesSelect.addEventListener("change", updateRerollMax);

    document.getElementById("addTerm").addEventListener("click", async () => {
      const count = Number(document.getElementById("diceCount").value);
      const sides = Number(diceSidesSelect.value);
      if (!sides) {
        showToast(t("common.die.required", "Select a die size."));
        return;
      }
      const rerollResult = Number(diceReroll.value);
      const dropLowest = document.getElementById("diceDropLowest").checked;
      try {
        await api("POST", `/api/drafts/${state.draftId}/dice-rolling/term`, {
          count,
          sides,
          rerollResult,
          dropLowest,
        });
        markSaved(t("web.toast.dice_term_added", "Dice term added"));
        renderDiceRolling();
      } catch (error) {
        showToast(error.message);
      }
    });

    document.querySelectorAll("#termList button").forEach((button) => {
      button.addEventListener("click", async () => {
        const index = Number(button.dataset.index);
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
            <input type="number" id="basePoints" value="${data.basePoints}">
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
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToDiceRolling" type="button">${t("setup.back", "Back")}</button>
          </div>
          <div class="right">
            <button class="btn" id="applyPoints" type="button">${t("attrgen.sets.apply", "Apply")}</button>
            <button class="btn ghost" id="pointsContinue" type="button">${t("common.continue", "Continue")}</button>
          </div>
        </div>
      </section>
    `;

    document.getElementById("applyPoints").addEventListener("click", async () => {
      const payload = {
        basePoints: Number(document.getElementById("basePoints").value),
        minValue: Number(document.getElementById("minValue").value),
        maxValue: Number(document.getElementById("maxValue").value),
        maxPostRacial: Number(document.getElementById("maxPostRacial").value),
        minPointsToSpend: Number(document.getElementById("minPoints").value),
        allowNegative: document.getElementById("allowNegative").value === "true",
      };
      try {
        await api("POST", `/api/drafts/${state.draftId}/points-buy`, payload);
        markSaved(t("web.toast.points_buy_updated", "Points buy updated"));
      } catch (error) {
        showToast(error.message);
      }
    });

    document.getElementById("backToDiceRolling").addEventListener("click", navigateBackInApp);
    document.getElementById("pointsContinue").addEventListener("click", () => {
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
    const averageRounding = String(data.averageRoundingMethod || "");

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
        { value: "average", label: t("hp.method.average", "Average") },
        { value: "fixed", label: t("hp.method.fixed", "Fixed") },
      ],
      hpGainMethod
    );
    const roundingOptions = buildOptions(
      [
        { value: "up", label: t("hp.average.rounding.up", "Round Up") },
        { value: "down", label: t("hp.average.rounding.down", "Round Down") },
        { value: "nearest", label: t("hp.average.rounding.nearest", "Round to Nearest") },
      ],
      averageRounding
    );

    view.innerHTML = `
      <section class="panel">
        <h1>${t("hp.title", "Hit Points")}</h1>
        <p class="field-hint">${t(
          "hp.intro",
          "Set how characters gain hit points each level and how first level is handled."
        )}</p>
        <div class="grid two">
          <div class="field">
            <label>${t("hp.method.label", "HP Gain Method")}</label>
            <select id="hpGainMethod">${methodOptions}</select>
          </div>
          <div class="field">
            <label>${t("hp.fixed_per_level", "Fixed HP per Level")}</label>
            <input type="number" id="hpFixedPerLevel" value="${Number(data.fixedHPPerLevel || 0)}">
          </div>
          <div class="field">
            <label>${t("hp.average.rounding", "Average Rounding")}</label>
            <select id="hpAverageRounding">${roundingOptions}</select>
          </div>
          <div class="field">
            <label>${t("hp.minimum_per_level", "Minimum HP per Level")}</label>
            <input type="number" id="hpMinimumPerLevel" value="${Number(data.minimumHPPerLevel || 0)}">
          </div>
        </div>
        <div class="grid two">
          <div class="field">
            <label>${t("hp.modifier.apply_con", "Apply Constitution Modifier")}</label>
            <select id="hpApplyCon">
              <option value="true" ${data.appliesConstitutionModifier ? "selected" : ""}>${t("common.yes", "Yes")}</option>
              <option value="false" ${data.appliesConstitutionModifier ? "" : "selected"}>${t("common.no", "No")}</option>
            </select>
          </div>
          <div class="field">
            <label>${t("hp.modifier.allow_negative_con", "Allow Negative Constitution Modifier")}</label>
            <select id="hpAllowNegativeCon">
              <option value="true" ${data.allowNegativeConModifier ? "selected" : ""}>${t("common.yes", "Yes")}</option>
              <option value="false" ${data.allowNegativeConModifier ? "" : "selected"}>${t("common.no", "No")}</option>
            </select>
          </div>
        </div>
        <div class="grid two">
          <div class="field">
            <label>${t("hp.first_level.max", "Max HP at First Level")}</label>
            <select id="hpFirstLevelMax">
              <option value="true" ${data.firstLevelMaxHP ? "selected" : ""}>${t("common.yes", "Yes")}</option>
              <option value="false" ${data.firstLevelMaxHP ? "" : "selected"}>${t("common.no", "No")}</option>
            </select>
          </div>
          <div class="field">
            <label>${t("hp.first_level.bonus", "First Level Bonus HP")}</label>
            <input type="number" id="hpFirstLevelBonus" value="${Number(data.firstLevelBonusHP || 0)}">
          </div>
        </div>
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

    const updateMethodControls = () => {
      const method = document.getElementById("hpGainMethod").value;
      const fixedInput = document.getElementById("hpFixedPerLevel");
      const roundingSelect = document.getElementById("hpAverageRounding");
      const fixedEnabled = method === "fixed";
      const averageEnabled = method === "average";
      fixedInput.disabled = !fixedEnabled;
      roundingSelect.disabled = !averageEnabled;
    };

    const saveHitPoints = async () => {
      const payload = {
        hpGainMethod: document.getElementById("hpGainMethod").value,
        fixedHPPerLevel: Number(document.getElementById("hpFixedPerLevel").value),
        averageRoundingMethod: document.getElementById("hpAverageRounding").value,
        minimumHPPerLevel: Number(document.getElementById("hpMinimumPerLevel").value),
        appliesConstitutionModifier: document.getElementById("hpApplyCon").value === "true",
        allowNegativeConModifier: document.getElementById("hpAllowNegativeCon").value === "true",
        firstLevelMaxHP: document.getElementById("hpFirstLevelMax").value === "true",
        firstLevelBonusHP: Number(document.getElementById("hpFirstLevelBonus").value),
      };
      try {
        await api("POST", `/api/drafts/${state.draftId}/hit-points`, payload);
        markSaved(t("web.toast.hp_updated", "Hit points updated"));
      } catch (error) {
        showToast(error.message);
      }
    };

    updateMethodControls();
    document.getElementById("hpGainMethod").addEventListener("change", () => {
      updateMethodControls();
      saveHitPoints();
    });
    document.getElementById("hpFixedPerLevel").addEventListener("change", saveHitPoints);
    document.getElementById("hpAverageRounding").addEventListener("change", saveHitPoints);
    document.getElementById("hpMinimumPerLevel").addEventListener("change", saveHitPoints);
    document.getElementById("hpApplyCon").addEventListener("change", saveHitPoints);
    document.getElementById("hpAllowNegativeCon").addEventListener("change", saveHitPoints);
    document.getElementById("hpFirstLevelMax").addEventListener("change", saveHitPoints);
    document.getElementById("hpFirstLevelBonus").addEventListener("change", saveHitPoints);

    document.getElementById("backToAttributes").addEventListener("click", navigateBackInApp);
    document.getElementById("hpContinue").addEventListener("click", () => {
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
    const gearBased = !!data.gearBased;
    const basePlusModifier = !!data.basePlusModifier;

    view.innerHTML = `
      <section class="panel">
        <h1>${t("armorclass.title", "Armor Class")}</h1>
        <p class="field-hint">${t(
          "armorclass.intro",
          "Choose how armor class is calculated. You can enable more than one method for hybrid systems."
        )}</p>
        <div class="toggle-group">
          <label class="toggle"><input type="checkbox" id="acGear" ${gearBased ? "checked" : ""}> ${t(
            "armorclass.method.gear",
            "Gear-Based AC"
          )}</label>
          <label class="toggle"><input type="checkbox" id="acBase" ${basePlusModifier ? "checked" : ""}> ${t(
            "armorclass.method.base_modifier",
            "Base + Modifier AC"
          )}</label>
        </div>
        <div class="grid two" style="margin-top: 16px;">
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

    const applySelection = () => {
      document.getElementById("acBaseValue").value = String(baseArmorClass);
      document.getElementById("acGear").checked = gearBased;
      document.getElementById("acBase").checked = basePlusModifier;
    };

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
        gearBased: document.getElementById("acGear").checked,
        basePlusModifier: document.getElementById("acBase").checked,
      };
      if (!selected.gearBased && !selected.basePlusModifier && !selected.acAbilityAttributeId) {
        applySelection();
        return;
      }
      try {
        await api("POST", `/api/drafts/${state.draftId}/armor-class`, selected);
        markSaved(t("web.toast.armor_class_updated", "Armor class updated"));
      } catch (error) {
        showToast(error.message);
      }
    };

    document.getElementById("acGear").addEventListener("change", saveSelection);
    document.getElementById("acBase").addEventListener("change", saveSelection);
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
        return `
          <div class="list-item">
            <button class="btn ghost small" data-select-currency="${currency.id}">${escapeHtml(name)}</button>
            <span class="badge">${count}</span>
            <button class="btn danger small" data-remove-currency="${currency.id}">${t("common.remove", "Remove")}</button>
          </div>
        `;
      })
      .join("");

    const denominations = (selectedCurrency && selectedCurrency.denominations) || [];
    const denomList = denominations
      .map(
        (denom) => `
          <div class="list-item">
            <span>${escapeHtml(denom.name)} <span class="badge">${denom.value}</span></span>
            <button class="btn danger small" data-remove-denom="${escapeHtml(denom.name)}">${t("common.remove", "Remove")}</button>
          </div>
        `
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
        <p class="field-hint">${t(
          "currency.intro",
          "A currency system is the overall economy (like gold standard or credits). Denominations are the specific coins or bills within it, each with a value."
        )}</p>
        <div class="grid two">
          <div class="field">
            <label for="currencyName">${t("currency.name", "Currency Name")}</label>
            <input type="text" id="currencyName" placeholder="${t("currency.name.placeholder", "e.g., Gold Standard")}">
          </div>
          <div class="field">
            <label for="baseDenomination">${t("currency.base_denom", "Base Denomination")}</label>
            <input type="text" id="baseDenomination" placeholder="${t("currency.base_denom.placeholder", "e.g., Copper")}" value="">
          </div>
        </div>
        <button class="btn" id="addCurrency" type="button">${t("currency.add", "Add Currency")}</button>
        <div class="list" id="currencyList">
          ${currencyList || `<div class="list-item">${t("currency.none", "No currencies yet.")}</div>`}
        </div>

        <h2>${t("currency.denom.section", "Denominations")}</h2>
        <div class="grid two">
          <div class="field">
            <label for="denomName">${t("currency.denom.name", "Denomination")}</label>
            <input type="text" id="denomName" placeholder="${t("currency.denom.placeholder", "e.g., Silver")}">
          </div>
          <div class="field">
            <label for="denomValue">${t("currency.denom.value", "Value")}</label>
            <input type="number" id="denomValue" value="1" step="0.01" min="0.01">
          </div>
        </div>
        <button class="btn" id="addDenom" type="button">${t("currency.denom.add", "Add Denomination")}</button>
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
            <div class="field">
              <label>&nbsp;</label>
              <button class="btn ghost" id="saveStartingMoney" type="button">${t("common.save", "Save")}</button>
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

    document.getElementById("addCurrency").addEventListener("click", async () => {
      const name = document.getElementById("currencyName").value.trim();
      const baseDenomination = document.getElementById("baseDenomination").value.trim();
      if (!name) {
        showToast(t("common.name.required", "Name is required."));
        return;
      }
      try {
        const result = await api("POST", `/api/drafts/${state.draftId}/currencies`, {
          name,
          baseDenomination,
        });
        state.currencyId = result.id || "";
        markSaved(t("web.toast.currency_added", "Currency added"));
        renderCurrency();
      } catch (error) {
        showToast(error.message);
      }
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

    document.getElementById("addDenom").addEventListener("click", async () => {
      if (!state.currencyId) {
        showToast(t("web.toast.currency_select_required", "Select a currency first."));
        return;
      }
      const name = document.getElementById("denomName").value.trim();
      const value = Number(document.getElementById("denomValue").value);
      if (!name) {
        showToast(t("common.name.required", "Name is required."));
        return;
      }
      if (!(value > 0)) {
        showToast(t("web.toast.denom_value_positive", "Denomination value must be positive."));
        return;
      }
      try {
        await api("POST", `/api/drafts/${state.draftId}/currencies/denominations`, {
          currencyId: state.currencyId,
          name,
          value,
        });
        markSaved(t("web.toast.denom_added", "Denomination added"));
        renderCurrency();
      } catch (error) {
        showToast(error.message);
      }
    });

    document.querySelectorAll("[data-remove-denom]").forEach((button) => {
      button.addEventListener("click", async () => {
        if (!state.currencyId) {
          return;
        }
        const name = button.dataset.removeDenom;
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

    const saveStartingMoney = async () => {
      const methodElement = document.getElementById("startingMoneyMethod");
      const baseAmountElement = document.getElementById("startingMoneyBaseAmount");
      const currencyElement = document.getElementById("startingMoneyCurrency");
      const payload = {
        method: methodElement ? String(methodElement.value || "base") : "base",
        baseAmount: baseAmountElement ? Number(baseAmountElement.value || 0) : 0,
        currencyId: currencyElement ? String(currencyElement.value || "").trim() : "",
      };
      payload.baseAmount = Math.max(0, Math.trunc(payload.baseAmount));
      try {
        await api("POST", `/api/drafts/${state.draftId}/currencies/starting-money`, payload);
        markSaved(t("web.toast.currency_saved", "Currency saved"));
        renderCurrency();
      } catch (error) {
        showToast(error.message);
      }
    };

    const saveStartingMoneyButton = document.getElementById("saveStartingMoney");
    if (saveStartingMoneyButton) {
      saveStartingMoneyButton.addEventListener("click", saveStartingMoney);
    }

    document.getElementById("backToPoints").addEventListener("click", navigateBackInApp);
    document.getElementById("currencyContinue").addEventListener("click", () => {
      markSaved(t("web.toast.currency_saved", "Currency saved"));
      renderEffectTypes();
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
    const title = systemNameTitle(systemName, t("effecttypes.title", "Effect Types"));
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
              ${type.description ? `<div>${escapeHtml(type.description)}</div>` : ""}
            </div>
            <div>
              <button class="btn ghost small" data-edit-effect-type="${escapeHtml(key)}">${t("common.edit", "Edit")}</button>
              <button class="btn danger small" data-remove-effect-type="${escapeHtml(key)}">${t("common.remove", "Remove")}</button>
            </div>
          </div>
        `;
      })
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${escapeHtml(title)}</h1>
        ${renderSystemNameControls(systemName, t("effecttypes.title", "Effect Types"))}
        <p class="field-hint">${t(
          "effecttypes.intro",
          "Effect types are tags that classify effects (for example: damage, condition, or movement). They help organize effects and power automation later."
        )}</p>
        <button class="btn" id="addEffectType" type="button">${t("effecttypes.add", "Add Effect Type")}</button>
        <div class="list" id="effectTypeList">
          ${list || `<div class="list-item">${t("effecttypes.none", "No effect types yet.")}</div>`}
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
          t("effecttypes.remove.confirm", "Remove this effect type?"),
          t("common.remove", "Remove")
        );
        if (!confirmed) {
          return;
        }
        try {
          await api("DELETE", `/api/drafts/${state.draftId}/effect-types`, { key });
          markSaved(t("web.toast.effect_type_removed", "Effect type removed"));
          renderEffectTypes();
        } catch (error) {
          showToast(error.message);
        }
      });
    });

    document.getElementById("backToCurrency").addEventListener("click", navigateBackInApp);
    document.getElementById("effectTypesContinue").addEventListener("click", () => {
      markSaved(t("web.toast.effect_types_saved", "Effect types saved"));
      renderStatuses();
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
      .map(
        (status) => `
          <div class="list-item">
            <div>
              <strong>${escapeHtml(status.name || t("statuses.untitled", "Untitled"))}</strong>
              ${status.description ? `<div>${escapeHtml(status.description)}</div>` : ""}
            </div>
            <div>
              <button class="btn ghost small" data-edit-status="${status.id}">${t("common.edit", "Edit")}</button>
              <button class="btn danger small" data-remove-status="${status.id}">${t("common.remove", "Remove")}</button>
            </div>
          </div>
        `
      )
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${escapeHtml(title)}</h1>
        <p class="field-hint">${t(
          "statuses.intro",
          "Statuses are reusable conditions that effects can apply to characters."
        )}</p>
        ${renderSystemNameControls(systemName, t("statuses.title", "Statuses"))}
        <button class="btn" id="addStatus" type="button">${t("statuses.add", "Add Status")}</button>
        <div class="list" id="statusList">
          ${statusList || `<div class="list-item">${t("statuses.none", "No statuses yet.")}</div>`}
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToEffectTypes" type="button">${t("setup.back", "Back")}</button>
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

    document.getElementById("backToEffectTypes").addEventListener("click", navigateBackInApp);
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

async function renderEffects(openId = "") {
  if (!ensureDraft()) {
    return;
  }
  setStep("effects");
  view.innerHTML = `<section class="panel"><p>${t("web.loading", "Loading...")}</p></section>`;
  try {
    const [data, typeData] = await Promise.all([
      api("GET", `/api/drafts/${state.draftId}/effects`),
      api("GET", `/api/drafts/${state.draftId}/effect-types`),
    ]);
    const systemName = String(data.systemName || "");
    const title = systemNameTitle(systemName, t("effects.title", "Effects"));
    const effects = sortByLabel(data.effects || [], (effect) => effect.name || effect.displayName || "");
    effectTypeOptions = sortByLabel(typeData.types || [], (type) => type.displayName || type.name || type.key || "");
    const effectList = effects
      .map(
        (effect) => `
          <div class="list-item">
            <div>
              <strong>${escapeHtml(effect.name || t("effects.untitled", "Untitled"))}</strong>
              ${effect.description ? `<div>${escapeHtml(effect.description)}</div>` : ""}
            </div>
            <div>
              <button class="btn ghost small" data-edit-effect="${effect.id}">${t("common.edit", "Edit")}</button>
              <button class="btn danger small" data-remove-effect="${effect.id}">${t("common.remove", "Remove")}</button>
            </div>
          </div>
        `
      )
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${escapeHtml(title)}</h1>
        <p class="field-hint">${t(
          "effects.intro",
          "Effects are reusable rules snippets that can be referenced by skills, spells, and equipment."
        )}</p>
        ${renderSystemNameControls(systemName, t("effects.title", "Effects"))}
        <button class="btn" id="addEffect" type="button">${t("effects.add", "Add Effect")}</button>
        <div class="list" id="effectList">
          ${effectList || `<div class="list-item">${t("effects.none", "No effects yet.")}</div>`}
        </div>
        <div class="actions-row">
          <div class="left">
            <button class="btn ghost" id="backToEffectTypes" type="button">${t("setup.back", "Back")}</button>
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

    document.getElementById("backToEffectTypes").addEventListener("click", navigateBackInApp);
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
    const data = await api("GET", `/api/drafts/${state.draftId}/equipment`);
    const systemName = String(data.systemName || "");
    const title = systemNameTitle(systemName, t("equipment.title", "Equipment"));
    const equipment = sortByLabel(data.equipment || [], (item) => item.name || item.displayName || "");
    weightUnitOptions = Array.isArray(data.weightUnits) ? data.weightUnits.slice() : [];
    weightSystem = String(data.weightSystem || "");

    const equipmentList = equipment
      .map(
        (item) => `
          <div class="list-item">
            <div>
              <strong>${escapeHtml(item.name || t("equipment.untitled", "Untitled"))}</strong>
              ${item.description ? `<div>${escapeHtml(item.description)}</div>` : ""}
            </div>
            <div>
              <button class="btn ghost small" data-edit-equipment="${item.id}">${t("common.edit", "Edit")}</button>
              <button class="btn danger small" data-remove-equipment="${item.id}">${t("common.remove", "Remove")}</button>
            </div>
          </div>
        `
      )
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${escapeHtml(title)}</h1>
        <p class="field-hint">${t(
          "equipment.intro",
          "List the gear available in your game and describe what each item does."
        )}</p>
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
    const [data, effectsData] = await Promise.all([
      api("GET", `/api/drafts/${state.draftId}/weapons`),
      api("GET", `/api/drafts/${state.draftId}/effects`),
    ]);
    const weapons = sortByLabel(data.weapons || [], (weapon) => weapon.name || weapon.displayName || "");
    weightUnitOptions = Array.isArray(data.weightUnits) ? data.weightUnits.slice() : [];
    weightSystem = String(data.weightSystem || "");
    weaponEffectOptions = sortByLabel(effectsData.effects || [], (effect) => effect.name || effect.displayName || "");

    const weaponList = weapons
      .map(
        (weapon) => `
          <div class="list-item">
            <div>
              <strong>${escapeHtml(weapon.name || t("weapons.untitled", "Untitled"))}</strong>
              ${weapon.description ? `<div>${escapeHtml(weapon.description)}</div>` : ""}
            </div>
            <div>
              <button class="btn ghost small" data-edit-weapon="${weapon.id}">${t("common.edit", "Edit")}</button>
              <button class="btn danger small" data-remove-weapon="${weapon.id}">${t("common.remove", "Remove")}</button>
            </div>
          </div>
        `
      )
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${t("weapons.title", "Weapons")}</h1>
        <p class="field-hint">${t(
          "weapons.intro",
          "Define the weapons available in your game along with their damage and effects."
        )}</p>
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
              ${characterClass.description ? `<div>${escapeHtml(characterClass.description)}</div>` : ""}
            </div>
            <div>
              <button class="btn ghost small" data-edit-class="${characterClass.id}">${t("common.edit", "Edit")}</button>
              <button class="btn danger small" data-remove-class="${characterClass.id}">${t("common.remove", "Remove")}</button>
            </div>
          </div>
        `
      )
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${escapeHtml(title)}</h1>
        <p class="field-hint">${t(
          "classes.intro",
          "Classes bundle progression rules, requirements, and starting packages."
        )}</p>
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
              <button class="btn ghost small" data-edit-skill="${skill.id}">${t("common.edit", "Edit")}</button>
              <button class="btn danger small" data-remove-skill="${skill.id}">${t("common.remove", "Remove")}</button>
            </div>
          </div>
        `;
      })
      .join("");

    const skillPointRows = skillPointsByLevel
      .map((entry, index) => {
        const levelLabel = t("classes.skill_points.level.label", "Level {0}").replace("{0}", String(entry.level));
        return `
          <div class="list-item">
            <div><strong>${escapeHtml(levelLabel)}</strong>: ${escapeHtml(String(entry.points))}</div>
            <div>
              <button class="btn danger small" data-remove-skill-points-level="${index}">
                ${t("common.remove", "Remove")}
              </button>
            </div>
          </div>
        `;
      })
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${escapeHtml(title)}</h1>
        <p class="field-hint">${t(
          "skills.intro",
          "Skills describe what characters can do and can reference effects for automation."
        )}</p>
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
          <div class="actions-row">
            <div class="left"></div>
            <div class="right">
              <button class="btn ghost" id="saveSkillProgression" type="button">${t("common.save", "Save")}</button>
            </div>
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
    const saveSkillProgression = document.getElementById("saveSkillProgression");

    const saveSkillProgressionData = async () => {
      await api("POST", `/api/drafts/${state.draftId}/skills/progression`, {
        skillPointProgression: String(skillPointProgressionType.value || "byClass"),
        baseSkillPointsPerLevel: Math.max(0, Math.trunc(Number(skillPointBase.value || 0))),
        skillPointsModifiedByInt: Boolean(skillPointModInt.checked),
        minimumSkillPointsPerLevel: Math.max(0, Math.trunc(Number(skillPointMinimum.value || 0))),
        skillPointsSameAllLevels: Boolean(skillPointSameAll.checked),
        skillPointsByLevel: skillPointsByLevel.slice(),
      });
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
          return `
            <div class="list-item">
              <div><strong>${escapeHtml(levelLabel)}</strong>: ${escapeHtml(String(entry.points))}</div>
              <div>
                <button class="btn danger small" data-remove-skill-points-level="${index}">
                  ${t("common.remove", "Remove")}
                </button>
              </div>
            </div>
          `;
        })
        .join("");
      const list = document.getElementById("skillPointLevelList");
      list.innerHTML = rows || `<div class="list-item">${t("classes.skill_points.none", "No level-specific values.")}</div>`;
      list.querySelectorAll("[data-remove-skill-points-level]").forEach((button) => {
        button.addEventListener("click", async () => {
          const index = Number(button.dataset.removeSkillPointsLevel);
          if (Number.isNaN(index) || index < 0) {
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
    saveSkillProgression.addEventListener("click", async () => {
      try {
        await saveSkillProgressionData();
        markSaved(t("web.toast.skills_saved", "Skills saved"));
      } catch (error) {
        showToast(error.message);
      }
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
    document.getElementById("skillsContinue").addEventListener("click", () => {
      markSaved(t("web.toast.skills_saved", "Skills saved"));
      renderSpells();
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
    const [spellData, effectsData] = await Promise.all([
      api("GET", `/api/drafts/${state.draftId}/spells`),
      api("GET", `/api/drafts/${state.draftId}/effects`),
    ]);
    const systemName = String(spellData.systemName || "");
    const title = systemNameTitle(systemName, t("spells.title", "Spells"));
    const spells = sortByLabel(spellData.spells || [], (spell) => spell.name || spell.displayName || "");
    spellEffectOptions = sortByLabel(effectsData.effects || [], (effect) => effect.name || effect.displayName || "");

    const spellList = spells
      .map((spell) => {
        const level = Number(spell.level || 0);
        const school = spell.school ? ` <span class="badge">${escapeHtml(spell.school)}</span>` : "";
        return `
          <div class="list-item">
            <div>
              <strong>${escapeHtml(spell.name || t("spells.untitled", "Untitled"))}</strong>
              <span class="badge">L${level}</span>${school}
            </div>
            <div>
              <button class="btn ghost small" data-edit-spell="${spell.id}">${t("common.edit", "Edit")}</button>
              <button class="btn danger small" data-remove-spell="${spell.id}">${t("common.remove", "Remove")}</button>
            </div>
          </div>
        `;
      })
      .join("");

    view.innerHTML = `
      <section class="panel">
        <h1>${escapeHtml(title)}</h1>
        <p class="field-hint">${t(
          "spells.intro",
          "Spells define magic options, including level, school, and linked effects."
        )}</p>
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

    document.getElementById("addSpell").addEventListener("click", () => {
      openSpellCreateModal();
    });

    document.querySelectorAll("[data-edit-spell]").forEach((button) => {
      button.addEventListener("click", () => {
        const spellId = button.dataset.editSpell;
        const spell = spells.find((item) => item.id === spellId);
        openSpellEditor(spell);
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
      renderRaces();
    });
    wireSystemNameSave("spells", () => renderSpells());
    if (openId) {
      const target = spells.find((item) => item.id === openId);
      openSpellEditor(target);
    }
  } catch (error) {
    showToast(error.message);
  }
}

Object.assign(stepRoutes, {
  setup: renderSetup,
  measurements: renderMeasurements,
  dice: renderDice,
  "attribute-generation": renderAttributeGeneration,
  "standard-array": renderStandardArray,
  "dice-rolling": renderDiceRolling,
  "points-buy": renderPointsBuy,
  "attribute-types": renderAttributeTypes,
  attributes: renderAttributes,
  "hit-points": renderHitPoints,
  "armor-class": renderArmorClass,
  currency: renderCurrency,
  "effect-types": renderEffectTypes,
  statuses: renderStatuses,
  effects: renderEffects,
  equipment: renderEquipment,
  weapons: renderWeapons,
  skills: renderSkills,
  spells: renderSpells,
  races: renderRaces,
  classes: renderClasses,
});

Object.assign(historyRoutes, {
  "beta-application": renderClosedBetaApplication,
  login: renderLogin,
  home: renderHome,
  splash: renderBuilderSplash,
  "chargen-upload": renderCharGenUpload,
  "chargen-resume": renderCharGenResume,
  "chargen-intro": renderCharGenIntro,
  "chargen-attrgen": renderCharGenAttributes,
  "chargen-points-buy": renderCharGenPointsBuy,
  "chargen-races": renderCharGenRaces,
  "chargen-classes": renderCharGenClasses,
});

function systemNameTitle(systemName, fallback) {
  const safe = String(systemName || "").trim();
  return safe ? safe : fallback;
}

function renderSystemNameControls(systemName, placeholder) {
  return `
    <div class="grid">
      <div class="field">
        <label for="systemNameInput">${t("common.system_name.label", "Section Label")}</label>
        <input type="text" id="systemNameInput" value="${escapeHtml(systemName)}"
          placeholder="${escapeHtml(placeholder)}">
      </div>
      <button class="btn ghost" id="saveSystemName" type="button">
        ${t("common.system_name.save", "Save Label")}
      </button>
    </div>
  `;
}

function wireSystemNameSave(key, onSaved) {
  const button = document.getElementById("saveSystemName");
  if (!button) {
    return;
  }
  button.addEventListener("click", async () => {
    const name = document.getElementById("systemNameInput").value.trim();
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
      if (typeof onSaved === "function") {
        onSaved();
      }
    } catch (error) {
      showToast(error.message);
    }
  });
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
