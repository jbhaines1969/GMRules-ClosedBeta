/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.web;

import com.gamemaker.gmrules.AtomicElements.Attribute;
import com.gamemaker.gmrules.AtomicElements.AttributeType;
import com.gamemaker.gmrules.AtomicElements.AttributeTypes;
import com.gamemaker.gmrules.AtomicElements.EffectType;
import com.gamemaker.gmrules.AtomicElements.EffectTypes;
import com.gamemaker.gmrules.AtomicElements.RegistryKey;
import com.gamemaker.gmrules.AtomicElements.SkillCategories;
import com.gamemaker.gmrules.AtomicElements.SkillCategory;
import com.gamemaker.gmrules.CharacterElements.CharacterClass;
import com.gamemaker.gmrules.CharacterElements.Skill;
import com.gamemaker.gmrules.CharacterElements.Race;
import com.gamemaker.gmrules.ElementRegistry;
import com.gamemaker.gmrules.ElementRegistryKey;
import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameMechanics.ArmorClassMethod;
import com.gamemaker.gmrules.GameMechanics.AttributeGenerationMethod;
import com.gamemaker.gmrules.GameMechanics.HPMethod;
import com.gamemaker.gmrules.GameMechanics.LevelingMethod;
import com.gamemaker.gmrules.GameSaveIO;
import com.gamemaker.gmrules.GameElements.Armor;
import com.gamemaker.gmrules.GameElements.Currency;
import com.gamemaker.gmrules.GameElements.Deity;
import com.gamemaker.gmrules.GameElements.Equipment;
import com.gamemaker.gmrules.GameElements.Pantheon;
import com.gamemaker.gmrules.GameElements.Species;
import com.gamemaker.gmrules.GameElements.Spell;
import com.gamemaker.gmrules.GameElements.Weapon;
import com.gamemaker.gmrules.SupportElements.AttributeModifiers;
import com.gamemaker.gmrules.SupportElements.Effect;
import com.gamemaker.gmrules.SupportElements.Status;
import com.gamemaker.gmrules.character.CharacterDraft;
import com.gamemaker.gmrules.character.CharacterFile;
import com.gamemaker.gmrules.character.CharacterFileBuilder;
import com.gamemaker.gmrules.character.CharacterFileIO;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registers API routes for the web UI.
 */
public final class ApiRoutes {

    // *** MEMBERS ***
    private static final String STRINGS_BUNDLE = "i18n/strings";
    private static final String NDA_RESOURCE = "legal/nda/nda-v1-en.txt";
    private static final String ARRAY_HYBRID = "hybridStages";
    private static final String CHARACTER_RULE_MODE_PREFIX = "ruleMode.";
    private static final long FEEDBACK_MAX_BODY_BYTES = 16L * 1024;
    private static final int FEEDBACK_TITLE_MAX_LENGTH = 120;
    private static final int FEEDBACK_MESSAGE_MAX_LENGTH = 4000;
    private static final int FEEDBACK_STEPS_MAX_LENGTH = 2000;
    private static final int FEEDBACK_METADATA_MAX_LENGTH = 512;
    private static final int FEEDBACK_RATE_LIMIT_MAX = 5;
    private static final Duration FEEDBACK_RATE_LIMIT_WINDOW = Duration.ofMinutes(10);
    private static final Map<String, Deque<Instant>> FEEDBACK_RATE_LIMITS = new ConcurrentHashMap<>();
    private static final int LOCKED_ACCOUNT_REPORT_RATE_LIMIT_MAX = 3;
    private static final Duration LOCKED_ACCOUNT_REPORT_RATE_LIMIT_WINDOW = Duration.ofMinutes(10);
    private static final Map<String, Deque<Instant>> LOCKED_ACCOUNT_REPORT_RATE_LIMITS = new ConcurrentHashMap<>();
    private static final int PASSWORD_RESET_RATE_LIMIT_MAX = 3;
    private static final Duration PASSWORD_RESET_RATE_LIMIT_WINDOW = Duration.ofMinutes(10);
    private static final Map<String, Deque<Instant>> PASSWORD_RESET_RATE_LIMITS = new ConcurrentHashMap<>();
    private static final int SIGNUP_IP_RATE_LIMIT_MAX = 2;
    private static final Duration SIGNUP_IP_RATE_LIMIT_WINDOW = Duration.ofDays(1);
    private static final int SIGNUP_EMAIL_RATE_LIMIT_MAX = 1;
    private static final Duration SIGNUP_EMAIL_RATE_LIMIT_WINDOW = Duration.ofDays(1);
    private static final Map<String, Deque<Instant>> SIGNUP_RATE_LIMITS = new ConcurrentHashMap<>();
    private static final int IMPORT_RATE_LIMIT_MAX = 2;
    private static final Duration IMPORT_RATE_LIMIT_WINDOW = Duration.ofDays(7);
    private static final Map<String, Deque<Instant>> IMPORT_RATE_LIMITS = new ConcurrentHashMap<>();
    private static final int EXPORT_RATE_LIMIT_MAX = 2;
    private static final Duration EXPORT_RATE_LIMIT_WINDOW = Duration.ofDays(7);
    private static final Map<String, Deque<Instant>> EXPORT_RATE_LIMITS = new ConcurrentHashMap<>();

    // *** CONSTRUCTORS ***
    private ApiRoutes() {
    }

    // *** METHODS ***
    public static void register(Router router) {
        router.add("GET", "/api/health", ApiRoutes::health);
        router.add("GET", "/api/i18n", ApiRoutes::localization);
        router.add("GET", "/api/legal/nda", ApiRoutes::getNdaText);
        router.add("POST", "/api/accounts", ApiRoutes::createAccount);
        router.add("GET", "/api/accounts/verify", ApiRoutes::verifyAccount);
        router.add("POST", "/api/accounts/lookup", ApiRoutes::lookupAccount);
        router.add("POST", "/api/accounts/password", ApiRoutes::setInitialPassword);
        router.add("POST", "/api/accounts/password-reset", ApiRoutes::requestPasswordReset);
        router.add("POST", "/api/accounts/locked-report", ApiRoutes::submitLockedAccountReport);
        router.add("DELETE", "/api/accounts", ApiRoutes::deleteAccount);
        router.add("POST", "/api/login", ApiRoutes::login);
        router.add("POST", "/api/logout", ApiRoutes::logout);
        router.add("GET", "/api/session", ApiRoutes::sessionInfo);
        router.add("GET", "/api/tutorial/visited", ApiRoutes::listTutorialVisitedScreens);
        router.add("POST", "/api/tutorial/visited", ApiRoutes::markTutorialVisitedScreens);
        router.add("POST", "/api/feedback", ApiRoutes::submitFeedback);
        router.add("GET", "/api/admin/accounts", ApiRoutes::adminListAccounts);
        router.add("POST", "/api/admin/accounts/unlock", ApiRoutes::adminUnlockAccount);
        router.add("DELETE", "/api/admin/accounts", ApiRoutes::adminDeleteAccount);
        router.add("GET", "/api/admin/blocks", ApiRoutes::adminListBlocks);
        router.add("POST", "/api/admin/blocks", ApiRoutes::adminBlockAccess);
        router.add("DELETE", "/api/admin/blocks", ApiRoutes::adminUnblockAccess);

        router.add("GET", "/api/drafts", ApiRoutes::listDrafts);
        router.add("POST", "/api/drafts", ApiRoutes::createDraft);
        router.add("POST", "/api/drafts/import", ApiRoutes::importDraft);
        router.add("POST", "/api/drafts/{id}/open", ApiRoutes::openDraft);
        router.add("DELETE", "/api/drafts/{id}", ApiRoutes::deleteDraft);
        router.add("GET", "/api/drafts/{id}/export", ApiRoutes::exportDraft);
        router.add("GET", "/api/drafts/{id}/summary", ApiRoutes::summary);
        router.add("GET", "/api/drafts/{id}/locale", ApiRoutes::getLocale);
        router.add("POST", "/api/drafts/{id}/locale", ApiRoutes::updateLocale);
        router.add("GET", "/api/drafts/{id}/system-names", ApiRoutes::getSystemNames);
        router.add("POST", "/api/drafts/{id}/system-names", ApiRoutes::updateSystemName);
        router.add("GET", "/api/characters", ApiRoutes::listCharacterDrafts);
        router.add("POST", "/api/characters", ApiRoutes::saveCharacterDraft);
        router.add("POST", "/api/characters/import", ApiRoutes::importCharacterFile);
        router.add("POST", "/api/characters/export", ApiRoutes::exportCharacterFile);
        router.add("GET", "/api/characters/{id}", ApiRoutes::openCharacterDraft);
        router.add("DELETE", "/api/characters/{id}", ApiRoutes::deleteCharacterDraft);

        router.add("GET", "/api/drafts/{id}/setup", ApiRoutes::getSetup);
        router.add("POST", "/api/drafts/{id}/setup", ApiRoutes::updateSetup);
        router.add("GET", "/api/drafts/{id}/measurements", ApiRoutes::getMeasurements);
        router.add("POST", "/api/drafts/{id}/measurements", ApiRoutes::updateMeasurements);

        router.add("GET", "/api/drafts/{id}/dice", ApiRoutes::getDice);
        router.add("POST", "/api/drafts/{id}/dice/standard", ApiRoutes::updateStandardDice);
        router.add("POST", "/api/drafts/{id}/dice/custom", ApiRoutes::addCustomDiceRange);
        router.add("DELETE", "/api/drafts/{id}/dice/custom", ApiRoutes::removeCustomDiceRange);

        router.add("GET", "/api/drafts/{id}/attribute-types", ApiRoutes::getAttributeTypes);
        router.add("POST", "/api/drafts/{id}/attribute-types", ApiRoutes::addAttributeType);
        router.add("DELETE", "/api/drafts/{id}/attribute-types", ApiRoutes::removeAttributeType);
        router.add("POST", "/api/drafts/{id}/attribute-types/update", ApiRoutes::updateAttributeTypeDetails);
        router.add("GET", "/api/drafts/{id}/effect-types", ApiRoutes::getEffectTypes);
        router.add("POST", "/api/drafts/{id}/effect-types", ApiRoutes::addEffectType);
        router.add("DELETE", "/api/drafts/{id}/effect-types", ApiRoutes::removeEffectType);
        router.add("POST", "/api/drafts/{id}/effect-types/update", ApiRoutes::updateEffectTypeDetails);
        router.add("GET", "/api/drafts/{id}/skill-categories", ApiRoutes::getSkillCategories);
        router.add("POST", "/api/drafts/{id}/skill-categories", ApiRoutes::addSkillCategory);

        router.add("GET", "/api/drafts/{id}/attributes", ApiRoutes::getAttributes);
        router.add("POST", "/api/drafts/{id}/attributes", ApiRoutes::addAttribute);
        router.add("DELETE", "/api/drafts/{id}/attributes", ApiRoutes::removeAttribute);
        router.add("POST", "/api/drafts/{id}/attributes/type", ApiRoutes::updateAttributeType);
        router.add("POST", "/api/drafts/{id}/attributes/update", ApiRoutes::updateAttributeDetails);

        router.add("GET", "/api/drafts/{id}/attribute-generation", ApiRoutes::getAttributeGeneration);
        router.add("POST", "/api/drafts/{id}/attribute-generation", ApiRoutes::updateAttributeGeneration);
        router.add("GET", "/api/drafts/{id}/chargen/attribute-generation", ApiRoutes::getCharGenAttributeGeneration);

        router.add("GET", "/api/drafts/{id}/standard-array", ApiRoutes::getStandardArrays);
        router.add("POST", "/api/drafts/{id}/standard-array/standard", ApiRoutes::addStandardArray);
        router.add("DELETE", "/api/drafts/{id}/standard-array/standard", ApiRoutes::removeStandardArray);
        router.add("POST", "/api/drafts/{id}/standard-array/elite", ApiRoutes::addEliteArray);
        router.add("DELETE", "/api/drafts/{id}/standard-array/elite", ApiRoutes::removeEliteArray);
        router.add("POST", "/api/drafts/{id}/standard-array/default", ApiRoutes::setDefaultArrayType);

        router.add("GET", "/api/drafts/{id}/dice-rolling", ApiRoutes::getDiceRolling);
        router.add("POST", "/api/drafts/{id}/dice-rolling/sets", ApiRoutes::updateDiceSets);
        router.add("POST", "/api/drafts/{id}/dice-rolling/method", ApiRoutes::updateDiceMethod);
        router.add("POST", "/api/drafts/{id}/dice-rolling/substitution", ApiRoutes::updateDiceSubstitution);
        router.add("POST", "/api/drafts/{id}/dice-rolling/term", ApiRoutes::addDiceTerm);
        router.add("DELETE", "/api/drafts/{id}/dice-rolling/term", ApiRoutes::removeDiceTerm);

        router.add("GET", "/api/drafts/{id}/points-buy", ApiRoutes::getPointsBuy);
        router.add("POST", "/api/drafts/{id}/points-buy", ApiRoutes::updatePointsBuy);
        router.add("GET", "/api/drafts/{id}/hit-points", ApiRoutes::getHitPoints);
        router.add("POST", "/api/drafts/{id}/hit-points", ApiRoutes::updateHitPoints);
        router.add("GET", "/api/drafts/{id}/armor-class", ApiRoutes::getArmorClass);
        router.add("POST", "/api/drafts/{id}/armor-class", ApiRoutes::updateArmorClass);
        router.add("GET", "/api/drafts/{id}/armor", ApiRoutes::getArmor);

        router.add("GET", "/api/drafts/{id}/currencies", ApiRoutes::getCurrencies);
        router.add("POST", "/api/drafts/{id}/currencies", ApiRoutes::addCurrency);
        router.add("DELETE", "/api/drafts/{id}/currencies", ApiRoutes::removeCurrency);
        router.add("POST", "/api/drafts/{id}/currencies/denominations", ApiRoutes::addCurrencyDenomination);
        router.add("DELETE", "/api/drafts/{id}/currencies/denominations", ApiRoutes::removeCurrencyDenomination);
        router.add("POST", "/api/drafts/{id}/currencies/starting-money", ApiRoutes::updateStartingMoney);

        router.add("GET", "/api/drafts/{id}/effects", ApiRoutes::getEffects);
        router.add("POST", "/api/drafts/{id}/effects", ApiRoutes::addEffect);
        router.add("DELETE", "/api/drafts/{id}/effects", ApiRoutes::removeEffect);
        router.add("POST", "/api/drafts/{id}/effects/update", ApiRoutes::updateEffect);

        router.add("GET", "/api/drafts/{id}/statuses", ApiRoutes::getStatuses);
        router.add("POST", "/api/drafts/{id}/statuses", ApiRoutes::addStatus);
        router.add("DELETE", "/api/drafts/{id}/statuses", ApiRoutes::removeStatus);
        router.add("POST", "/api/drafts/{id}/statuses/update", ApiRoutes::updateStatus);

        router.add("GET", "/api/drafts/{id}/equipment", ApiRoutes::getEquipment);
        router.add("POST", "/api/drafts/{id}/equipment", ApiRoutes::addEquipment);
        router.add("DELETE", "/api/drafts/{id}/equipment", ApiRoutes::removeEquipment);
        router.add("POST", "/api/drafts/{id}/equipment/update", ApiRoutes::updateEquipment);

        router.add("GET", "/api/drafts/{id}/weapons", ApiRoutes::getWeapons);
        router.add("POST", "/api/drafts/{id}/weapons", ApiRoutes::addWeapon);
        router.add("DELETE", "/api/drafts/{id}/weapons", ApiRoutes::removeWeapon);
        router.add("POST", "/api/drafts/{id}/weapons/update", ApiRoutes::updateWeapon);

        router.add("GET", "/api/drafts/{id}/classes", ApiRoutes::getClasses);
        router.add("POST", "/api/drafts/{id}/classes", ApiRoutes::addClass);
        router.add("DELETE", "/api/drafts/{id}/classes", ApiRoutes::removeClass);
        router.add("POST", "/api/drafts/{id}/classes/update", ApiRoutes::updateClass);

        router.add("GET", "/api/drafts/{id}/skills", ApiRoutes::getSkills);
        router.add("POST", "/api/drafts/{id}/skills/progression", ApiRoutes::updateSkillProgression);
        router.add("POST", "/api/drafts/{id}/skills", ApiRoutes::addSkill);
        router.add("DELETE", "/api/drafts/{id}/skills", ApiRoutes::removeSkill);
        router.add("POST", "/api/drafts/{id}/skills/update", ApiRoutes::updateSkill);

        router.add("GET", "/api/drafts/{id}/spells", ApiRoutes::getSpells);
        router.add("POST", "/api/drafts/{id}/spells", ApiRoutes::addSpell);
        router.add("DELETE", "/api/drafts/{id}/spells", ApiRoutes::removeSpell);
        router.add("POST", "/api/drafts/{id}/spells/update", ApiRoutes::updateSpell);
        router.add("GET", "/api/drafts/{id}/pantheons", ApiRoutes::getPantheons);
        router.add("POST", "/api/drafts/{id}/pantheons", ApiRoutes::addPantheon);
        router.add("DELETE", "/api/drafts/{id}/pantheons", ApiRoutes::removePantheon);
        router.add("POST", "/api/drafts/{id}/pantheons/update", ApiRoutes::updatePantheon);
        router.add("GET", "/api/drafts/{id}/deities", ApiRoutes::getDeities);
        router.add("POST", "/api/drafts/{id}/deities", ApiRoutes::addDeity);
        router.add("DELETE", "/api/drafts/{id}/deities", ApiRoutes::removeDeity);
        router.add("POST", "/api/drafts/{id}/deities/update", ApiRoutes::updateDeity);

        router.add("GET", "/api/drafts/{id}/races", ApiRoutes::getRaces);
        router.add("POST", "/api/drafts/{id}/races", ApiRoutes::addRace);
        router.add("DELETE", "/api/drafts/{id}/races", ApiRoutes::removeRace);
        router.add("POST", "/api/drafts/{id}/races/update", ApiRoutes::updateRace);
    }

    private static void health(RequestContext ctx) throws IOException {
        List<String> failedChecks = new ArrayList<>();
        WebConfig config = ctx.getConfig();
        probeWritableDirectory("accounts", parentDirectory(config.getAccountsFile()), failedChecks);
        probeWritableDirectory("drafts", config.getDraftsDirectory(), failedChecks);
        probeWritableDirectory("ndaAudit", config.getNdaAuditDirectory(), failedChecks);
        probeWritableDirectory("feedback", config.getFeedbackDirectory(), failedChecks);
        probeWritableDirectory("blockedAccess", parentDirectory(config.getBlockedAccessFile()), failedChecks);
        probeWritableDirectory("requestLogs", config.getRequestLogDirectory(), failedChecks);

        boolean healthy = failedChecks.isEmpty();
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("ok", healthy);
        payload.put("status", healthy ? "healthy" : "unhealthy");
        payload.put("version", "closed-beta");
        payload.put("timestamp", Instant.now().toString());
        if (!healthy) {
            payload.put("failedChecks", failedChecks);
        }
        if (prefersHtml(ctx)) {
            ctx.text(healthy ? 200 : 503, healthHtml(healthy, failedChecks), "text/html");
            return;
        }
        ctx.json(healthy ? 200 : 503, payload);
    }

    private static void localization(RequestContext ctx) throws IOException {
        String rawLanguage = firstQueryParam(ctx, "lang");
        Locale resolvedLocale = resolveLocale(rawLanguage);
        Locale.setDefault(resolvedLocale);
        Map<String, String> strings = new LinkedHashMap<>();
        strings.putAll(loadStrings(resolvedLocale));
        String localeTag = Objects.toString(resolvedLocale.toLanguageTag(), "");
        ctx.json(200, Map.of("locale", localeTag, "strings", strings));
    }

    private static void getNdaText(RequestContext ctx) throws IOException {
        try (InputStream input = ApiRoutes.class.getClassLoader().getResourceAsStream(NDA_RESOURCE)) {
            if (input == null) {
                ctx.json(404, Map.of("error", "NDA text resource was not found."));
                return;
            }
            String text = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            ctx.json(200, Map.of(
                "version",
                NdaAuditStore.CURRENT_NDA_VERSION,
                "text",
                text
            ));
        }
    }

    private static Locale resolveLocale(String rawLanguage) {
        String safeLanguage = Objects.toString(rawLanguage, "").trim().toLowerCase();
        if (safeLanguage.startsWith("fr")) {
            return Locale.FRENCH;
        }
        return Locale.ENGLISH;
    }

    private static Map<String, String> loadStrings(Locale locale) throws IOException {
        Locale resolvedLocale = Objects.requireNonNullElseGet(locale, Locale::getDefault);
        boolean useFrench = Locale.FRENCH.getLanguage().equals(resolvedLocale.getLanguage());
        String resource = useFrench ? STRINGS_BUNDLE + "_fr.properties" : STRINGS_BUNDLE + ".properties";
        Map<String, String> strings = new LinkedHashMap<>();
        if (!loadProperties(resource, strings) && useFrench) {
            loadProperties(STRINGS_BUNDLE + ".properties", strings);
        }
        return strings;
    }

    private static boolean loadProperties(String resource, Map<String, String> target) throws IOException {
        try (InputStream input = ApiRoutes.class.getClassLoader().getResourceAsStream(resource)) {
            if (input == null) {
                return false;
            }
            Properties props = new Properties();
            props.load(input);
            for (String name : props.stringPropertyNames()) {
                target.put(name, Objects.toString(props.getProperty(name), ""));
            }
            return true;
        }
    }

    private static void createAccount(RequestContext ctx) throws IOException {
        Map<String, Object> body = ctx.readJsonMap();
        String fullName = limitLength(getString(body, "fullName").trim(), 120);
        String email = getString(body, "email").trim();
        boolean ndaAccepted = getBoolean(body, "ndaAccepted", false);
        String ndaVersion = getString(body, "ndaVersion").trim();
        String ndaScrollCompletedAt = getString(body, "ndaScrollCompletedAt").trim();
        String ndaAcceptedAt = getString(body, "ndaAcceptedAt").trim();
        if (fullName.length() < 2) {
            ctx.json(400, Map.of("error", "Legal full name is required."));
            return;
        }
        if (!ndaAccepted) {
            ctx.json(400, Map.of("error", "You must agree to the NDA before submitting."));
            return;
        }
        if (!Objects.equals(ndaVersion, NdaAuditStore.CURRENT_NDA_VERSION)) {
            ctx.json(400, Map.of("error", "The NDA version is out of date. Refresh and try again."));
            return;
        }
        if (ndaScrollCompletedAt.isEmpty() || ndaAcceptedAt.isEmpty()) {
            ctx.json(400, Map.of("error", "Review and accept the full NDA before submitting."));
            return;
        }
        if (isBlockedSignup(ctx, email)) {
            return;
        }
        String signupRateLimitMessage = checkSignupRateLimit(ctx, email);
        if (!signupRateLimitMessage.isEmpty()) {
            ctx.json(429, Map.of("error", signupRateLimitMessage));
            return;
        }
        AccountStore.VerificationRequest verificationRequest;
        try {
            verificationRequest = ctx.getAccountStore().createVerificationRequest(email);
        } catch (IllegalArgumentException e) {
            ctx.json(400, Map.of("error", e.getMessage()));
            return;
        }
        new NdaAuditStore(ctx.getConfig()).recordNdaAcceptance(
            fullName,
            verificationRequest.getEmail(),
            ctx.clientIp(),
            ctx.userAgent(),
            ndaScrollCompletedAt,
            ndaAcceptedAt
        );
        boolean emailSent;
        try {
            String verificationUrl = buildVerificationUrl(ctx.getConfig(), verificationRequest.getToken());
            emailSent = new EmailService(ctx.getConfig()).sendClosedBetaEmail(
                verificationRequest.getEmail(),
                verificationUrl
            );
        } catch (IOException e) {
            ctx.json(502, Map.of("error", e.getMessage()));
            return;
        }
        ctx.json(200, Map.of(
            "ok",
            true,
            "email",
            verificationRequest.getEmail(),
            "emailSent",
            emailSent,
            "verificationRequired",
            true
        ));
    }

    private static void verifyAccount(RequestContext ctx) throws IOException {
        String token = firstQueryParam(ctx, "token");
        try {
            AccountStore.VerificationResult verification = ctx.getAccountStore().verifyAccount(token);
            AccountStore.Account account = verification.getAccount();
            if (verification.isPasswordReset()) {
                ctx.redirect(buildResetPasswordUrl(ctx.getConfig(), account.getUsername(), verification.getToken()));
                return;
            }
            NdaAuditStore auditStore = new NdaAuditStore(ctx.getConfig());
            auditStore.recordAccountCreated(
                account.getUsername(),
                ctx.clientIp(),
                ctx.userAgent(),
                account.getId()
            );
            sendNdaAuditCopy(ctx, account, auditStore);
            ctx.redirect(buildVerifiedPasswordUrl(ctx.getConfig(), account.getUsername()));
        } catch (IllegalArgumentException e) {
            String safeMessage = escapeHtml(e.getMessage());
            ctx.text(400, """
                <!doctype html>
                <html lang="en">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>GMRules Verification Failed</title>
                </head>
                <body>
                  <h1>Verification failed</h1>
                  <p>%s</p>
                </body>
                </html>
                """.formatted(safeMessage), "text/html");
        }
    }

    private static void sendNdaAuditCopy(
            RequestContext ctx,
            AccountStore.Account account,
            NdaAuditStore auditStore
    ) {
        try {
            String auditCsv = auditStore.readAuditCsv(account.getUsername());
            new EmailService(ctx.getConfig()).sendNdaAuditEmail(account.getUsername(), auditCsv);
        } catch (IOException e) {
            System.err.println("NDA audit email failed: " + e.getMessage());
        }
    }

    private static void lookupAccount(RequestContext ctx) throws IOException {
        Map<String, Object> body = ctx.readJsonMap();
        String email = getString(body, "email").trim();
        if (isBlockedLogin(ctx, email)) {
            return;
        }
        AccountStore.AccountLookup lookup = ctx.getAccountStore().lookupAccount(email);
        ctx.json(200, Map.of(
            "exists",
            lookup.exists(),
            "passwordSet",
            lookup.isPasswordSet(),
            "locked",
            lookup.isLocked(),
            "email",
            lookup.getEmail()
        ));
    }

    private static void requestPasswordReset(RequestContext ctx) throws IOException {
        Map<String, Object> body = ctx.readJsonMap();
        String email = getString(body, "email").trim();
        if (isBlockedLogin(ctx, email)) {
            return;
        }
        if (!consumePasswordResetRateLimit(ctx, email)) {
            ctx.json(429, Map.of("error", "Too many password reset requests. Try again later."));
            return;
        }
        boolean emailSent = false;
        try {
            AccountStore.VerificationRequest resetRequest = ctx.getAccountStore().createPasswordResetRequest(email);
            String resetUrl = buildVerificationUrl(ctx.getConfig(), resetRequest.getToken());
            emailSent = new EmailService(ctx.getConfig()).sendPasswordResetEmail(
                resetRequest.getEmail(),
                resetUrl
            );
        } catch (IllegalArgumentException ignored) {
            emailSent = false;
        } catch (IOException e) {
            ctx.json(502, Map.of("error", e.getMessage()));
            return;
        }
        ctx.json(200, Map.of(
            "ok",
            true,
            "emailSent",
            emailSent,
            "message",
            "If this email has a GMRules Closed Beta account, a password reset link has been sent."
        ));
    }

    private static void setInitialPassword(RequestContext ctx) throws IOException {
        Map<String, Object> body = ctx.readJsonMap();
        String email = getString(body, "email").trim();
        String password = getString(body, "password");
        String confirmPassword = getString(body, "confirmPassword");
        if (isBlockedLogin(ctx, email)) {
            return;
        }
        if (!Objects.equals(password, confirmPassword)) {
            ctx.json(400, Map.of("error", "Passwords do not match."));
            return;
        }
        try {
            String resetToken = getString(body, "resetToken").trim();
            AccountStore.Account account = resetToken.isEmpty()
                ? ctx.getAccountStore().setInitialPassword(email, password)
                : ctx.getAccountStore().resetPassword(email, resetToken, password);
            ctx.getAccountStore().recordSuccessfulLogin(account.getUsername(), ctx.clientIp());
            boolean admin = ctx.getConfig().isAdminEmail(account.getUsername());
            SessionStore.Session session = ctx.getSessionStore().createSession(
                account.getId(),
                account.getUsername(),
                account.isLegacyGuest()
            );
            ctx.json(200, Map.of(
                "ok",
                true,
                "token",
                session.getId(),
                "username",
                account.getUsername(),
                "legacyGuest",
                account.isLegacyGuest(),
                "admin",
                admin,
                "tutorialVisitedScreens",
                ctx.getAccountStore().listTutorialVisitedScreens(account.getId())
            ));
        } catch (IllegalArgumentException e) {
            ctx.json(400, Map.of("error", e.getMessage()));
        }
    }

    private static void deleteAccount(RequestContext ctx) throws IOException {
        Map<String, Object> body = ctx.readJsonMap();
        String username = getString(body, "username").trim();
        String password = getString(body, "password");
        try {
            AccountStore.AccountDeletion deletion = ctx.getAccountStore().deleteAccount(username, password);
            for (String draftId : deletion.getDraftIds()) {
                ctx.getDraftStore().deleteDraft(draftId);
            }
            CharacterDraftStore characterDraftStore = new CharacterDraftStore(ctx.getConfig());
            for (String characterDraftId : deletion.getCharacterDraftIds()) {
                characterDraftStore.deleteCharacterDraft(characterDraftId);
            }
            ctx.getSessionStore().invalidateUser(deletion.getAccount().getId());
            ctx.json(200, Map.of(
                "ok",
                true,
                "deletedDrafts",
                deletion.getDraftIds().size(),
                "deletedCharacterDrafts",
                deletion.getCharacterDraftIds().size()
            ));
        } catch (IllegalArgumentException e) {
            ctx.json(401, Map.of("error", e.getMessage()));
        }
    }

    private static void login(RequestContext ctx) throws IOException {
        Map<String, Object> body = ctx.readJsonMap();
        String username = getString(body, "username").trim();
        String password = getString(body, "password");
        if (username.isEmpty() || password.isEmpty()) {
            ctx.json(401, Map.of("error", "Email and password are required"));
            return;
        }
        if (isBlockedLogin(ctx, username)) {
            return;
        }
        AccountStore.AuthenticationResult result = ctx.getAccountStore().authenticate(username, password);
        if (result.isLocked()) {
            ctx.json(423, accountLockedPayload());
            return;
        }
        if (!result.isAuthenticated()) {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("error", "Invalid email or password");
            if (result.getFailedAttempts() > 0) {
                payload.put("remainingAttempts", result.getRemainingAttempts());
            }
            ctx.json(401, payload);
            return;
        }
        AccountStore.Account account = result.getAccount();
        ctx.getAccountStore().recordSuccessfulLogin(account.getUsername(), ctx.clientIp());
        boolean admin = ctx.getConfig().isAdminEmail(account.getUsername());
        SessionStore.Session session = ctx.getSessionStore().createSession(
            account.getId(),
            account.getUsername(),
            account.isLegacyGuest()
        );
        ctx.json(200, Map.of(
            "ok",
            true,
            "token",
            session.getId(),
            "username",
            account.getUsername(),
            "legacyGuest",
            account.isLegacyGuest(),
            "admin",
            admin,
            "tutorialVisitedScreens",
            ctx.getAccountStore().listTutorialVisitedScreens(account.getId())
        ));
    }

    private static void logout(RequestContext ctx) throws IOException {
        String sessionId = resolveToken(ctx);
        ctx.getSessionStore().invalidate(sessionId);
        ctx.json(200, Map.of("ok", true));
    }

    private static void sessionInfo(RequestContext ctx) throws IOException {
        SessionStore.Session session = ctx.getSessionStore().getSession(resolveToken(ctx));
        if (session == null) {
            ctx.json(200, Map.of("authenticated", false));
            return;
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("authenticated", true);
        payload.put("username", session.getUsername());
        payload.put("legacyGuest", session.isLegacyGuest());
        payload.put("admin", ctx.getConfig().isAdminEmail(session.getUsername()));
        payload.put("draftId", session.getDraftId());
        payload.put("draftLocale", resolveDraftLocale(ctx, session.getDraftId()));
        payload.put("completedStages", resolveCompletedStages(ctx, session.getDraftId()));
        payload.put("tutorialVisitedScreens", session.isLegacyGuest()
            ? List.of()
            : ctx.getAccountStore().listTutorialVisitedScreens(session.getUserId()));
        ctx.json(200, payload);
    }

    private static void listTutorialVisitedScreens(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        List<String> visitedScreens = session.isLegacyGuest()
            ? List.of()
            : ctx.getAccountStore().listTutorialVisitedScreens(session.getUserId());
        ctx.json(200, Map.of(
            "ok",
            true,
            "tutorialVisitedScreens",
            visitedScreens
        ));
    }

    private static void markTutorialVisitedScreens(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        Map<String, Object> body = ctx.readJsonMap(4096);
        ArrayList<String> screens = new ArrayList<>();
        String screen = getString(body, "screen").trim();
        if (!screen.isEmpty()) {
            screens.add(screen);
        }
        screens.addAll(getStringList(body, "screens"));
        List<String> visitedScreens = session.isLegacyGuest()
            ? List.of()
            : ctx.getAccountStore().markTutorialScreensVisited(session.getUserId(), screens);
        ctx.json(200, Map.of(
            "ok",
            true,
            "tutorialVisitedScreens",
            visitedScreens
        ));
    }

    private static void adminListAccounts(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireAdminSession(ctx);
        if (session == null) {
            return;
        }
        BlockedAccessStore blockedAccessStore = new BlockedAccessStore(ctx.getConfig());
        List<SessionStore.Session> activeSessions = ctx.getSessionStore().listActiveSessions();
        Map<String, Integer> activeSessionsByUserId = new LinkedHashMap<>();
        for (SessionStore.Session activeSession : activeSessions) {
            String userId = Objects.toString(activeSession.getUserId(), "");
            if (!userId.isEmpty()) {
                activeSessionsByUserId.put(userId, activeSessionsByUserId.getOrDefault(userId, 0) + 1);
            }
        }
        List<Map<String, Object>> accounts = new ArrayList<>();
        List<Map<String, Object>> sessions = new ArrayList<>();
        int draftCount = 0;
        int lockedCount = 0;
        for (AccountStore.AccountSummary account : ctx.getAccountStore().listAccounts()) {
            List<String> draftIds = account.getDraftIds();
            draftCount += draftIds.size();
            if (account.isLocked()) {
                lockedCount++;
            }
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("id", account.getId());
            entry.put("email", account.getEmail());
            entry.put("passwordSet", account.isPasswordSet());
            entry.put("locked", account.isLocked());
            entry.put("failedLoginAttempts", account.getFailedLoginAttempts());
            entry.put("createdAt", account.getCreatedAt());
            entry.put("verifiedAt", account.getVerifiedAt());
            entry.put("lastLoginAt", account.getLastLoginAt());
            entry.put("lastLoginIp", account.getLastLoginIp());
            entry.put("draftCount", draftIds.size());
            entry.put("draftIds", draftIds);
            entry.put("admin", ctx.getConfig().isAdminEmail(account.getEmail()));
            entry.put("emailBlocked", blockedAccessStore.isEmailBlocked(account.getEmail()));
            entry.put("lastLoginIpBlocked", blockedAccessStore.isIpBlocked(account.getLastLoginIp()));
            entry.put("activeSessionCount", activeSessionsByUserId.getOrDefault(account.getId(), 0));
            accounts.add(entry);
        }
        for (SessionStore.Session activeSession : activeSessions) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("userId", activeSession.getUserId());
            entry.put("username", activeSession.getUsername());
            entry.put("legacyGuest", activeSession.isLegacyGuest());
            entry.put("createdAt", activeSession.getCreatedAt().toString());
            entry.put("lastAccessAt", activeSession.getLastAccessAt().toString());
            entry.put("draftId", activeSession.getDraftId());
            entry.put("admin", ctx.getConfig().isAdminEmail(activeSession.getUsername()));
            sessions.add(entry);
        }
        ctx.json(200, Map.of(
            "ok",
            true,
            "accounts",
            accounts,
            "activeSessions",
            sessions,
            "activeSessionCount",
            sessions.size(),
            "accountCount",
            accounts.size(),
            "draftCount",
            draftCount,
            "lockedCount",
            lockedCount,
            "adminEmailCount",
            ctx.getConfig().getAdminEmails().size()
        ));
    }

    private static void adminListBlocks(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireAdminSession(ctx);
        if (session == null) {
            return;
        }
        List<Map<String, Object>> blocks = new ArrayList<>();
        for (BlockedAccessStore.BlockEntry block : new BlockedAccessStore(ctx.getConfig()).listBlocks()) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("type", block.getType());
            entry.put("value", block.getValue());
            entry.put("reason", block.getReason());
            entry.put("createdBy", block.getCreatedBy());
            entry.put("sourceAccountId", block.getSourceAccountId());
            entry.put("createdAt", block.getCreatedAt());
            blocks.add(entry);
        }
        ctx.json(200, Map.of("ok", true, "blocks", blocks, "blockCount", blocks.size()));
    }

    private static void adminBlockAccess(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireAdminSession(ctx);
        if (session == null) {
            return;
        }
        Map<String, Object> body = ctx.readJsonMap();
        String type = getString(body, "type").trim().toLowerCase(Locale.ROOT);
        String value = getString(body, "value").trim();
        String reason = limitLength(getString(body, "reason"), 500);
        String sourceAccountId = getString(body, "sourceAccountId").trim();
        BlockedAccessStore blockedAccessStore = new BlockedAccessStore(ctx.getConfig());
        try {
            if (Objects.equals(type, "email")) {
                if (ctx.getConfig().isAdminEmail(value)) {
                    ctx.json(400, Map.of("error", "Admin emails cannot be blocked from this panel."));
                    return;
                }
                blockedAccessStore.blockEmail(value, reason, session.getUsername(), sourceAccountId);
            } else if (Objects.equals(type, "ip")) {
                if (Objects.equals(value, ctx.clientIp())) {
                    ctx.json(400, Map.of("error", "The current admin IP cannot be blocked from this panel."));
                    return;
                }
                blockedAccessStore.blockIp(value, reason, session.getUsername(), sourceAccountId);
            } else {
                ctx.json(400, Map.of("error", "Block type must be email or ip."));
                return;
            }
            ctx.json(200, Map.of("ok", true));
        } catch (IllegalArgumentException e) {
            ctx.json(400, Map.of("error", e.getMessage()));
        }
    }

    private static void adminUnblockAccess(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireAdminSession(ctx);
        if (session == null) {
            return;
        }
        Map<String, Object> body = ctx.readJsonMap();
        String type = getString(body, "type").trim().toLowerCase(Locale.ROOT);
        String value = getString(body, "value").trim();
        BlockedAccessStore blockedAccessStore = new BlockedAccessStore(ctx.getConfig());
        try {
            if (Objects.equals(type, "email")) {
                blockedAccessStore.unblockEmail(value);
            } else if (Objects.equals(type, "ip")) {
                blockedAccessStore.unblockIp(value);
            } else {
                ctx.json(400, Map.of("error", "Block type must be email or ip."));
                return;
            }
            ctx.json(200, Map.of("ok", true));
        } catch (IllegalArgumentException e) {
            ctx.json(400, Map.of("error", e.getMessage()));
        }
    }

    private static void adminUnlockAccount(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireAdminSession(ctx);
        if (session == null) {
            return;
        }
        Map<String, Object> body = ctx.readJsonMap();
        String email = getString(body, "email").trim();
        try {
            ctx.getAccountStore().unlockAccount(email);
            ctx.json(200, Map.of("ok", true));
        } catch (IllegalArgumentException e) {
            ctx.json(400, Map.of("error", e.getMessage()));
        }
    }

    private static void adminDeleteAccount(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireAdminSession(ctx);
        if (session == null) {
            return;
        }
        Map<String, Object> body = ctx.readJsonMap();
        String email = getString(body, "email").trim();
        if (ctx.getConfig().isAdminEmail(email)) {
            ctx.json(400, Map.of("error", "Admin accounts cannot be deleted from this panel."));
            return;
        }
        try {
            AccountStore.AccountDeletion deletion = ctx.getAccountStore().deleteAccountAsAdmin(email);
            for (String draftId : deletion.getDraftIds()) {
                ctx.getDraftStore().deleteDraft(draftId);
            }
            CharacterDraftStore characterDraftStore = new CharacterDraftStore(ctx.getConfig());
            for (String characterDraftId : deletion.getCharacterDraftIds()) {
                characterDraftStore.deleteCharacterDraft(characterDraftId);
            }
            ctx.getSessionStore().invalidateUser(deletion.getAccount().getId());
            ctx.json(200, Map.of(
                "ok",
                true,
                "deletedDrafts",
                deletion.getDraftIds().size(),
                "deletedCharacterDrafts",
                deletion.getCharacterDraftIds().size()
            ));
        } catch (IllegalArgumentException e) {
            ctx.json(400, Map.of("error", e.getMessage()));
        }
    }

    private static void submitFeedback(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        if (!consumeFeedbackRateLimit(ctx, session)) {
            ctx.json(429, Map.of("error", "Too many reports submitted. Wait a few minutes and try again."));
            return;
        }
        Map<String, Object> body;
        try {
            body = ctx.readJsonMap(FEEDBACK_MAX_BODY_BYTES);
        } catch (IOException e) {
            String message = Objects.toString(e.getMessage(), "");
            if (message.toLowerCase(Locale.ROOT).contains("too large")) {
                ctx.json(413, Map.of("error", "Feedback report is too large."));
                return;
            }
            ctx.json(400, Map.of("error", "Feedback report must be valid JSON."));
            return;
        }

        String type = normalizeFeedbackType(getString(body, "type"));
        if (type.isEmpty()) {
            ctx.json(400, Map.of("error", "Choose a valid report type."));
            return;
        }
        String severity = normalizeFeedbackSeverity(getString(body, "severity"));
        String title = limitLength(getString(body, "title"), FEEDBACK_TITLE_MAX_LENGTH).trim();
        String reportMessage = limitLength(getString(body, "message"), FEEDBACK_MESSAGE_MAX_LENGTH).trim();
        String steps = limitLength(getString(body, "steps"), FEEDBACK_STEPS_MAX_LENGTH).trim();
        if (title.isEmpty() || reportMessage.isEmpty()) {
            ctx.json(400, Map.of("error", "Add a short title and details before submitting."));
            return;
        }

        String serverTimestamp = Instant.now().toString();
        FeedbackStore.Report report = new FeedbackStore.Report(
            UUID.randomUUID().toString(),
            type,
            severity,
            title,
            reportMessage,
            steps,
            session.getUserId(),
            session.getUsername(),
            session.isLegacyGuest(),
            limitLength(getString(body, "route"), FEEDBACK_METADATA_MAX_LENGTH),
            limitLength(getString(body, "page"), FEEDBACK_METADATA_MAX_LENGTH),
            limitLength(getString(body, "stage"), FEEDBACK_METADATA_MAX_LENGTH),
            limitLength(getString(body, "draftId"), FEEDBACK_METADATA_MAX_LENGTH),
            limitLength(getString(body, "userAgent"), FEEDBACK_METADATA_MAX_LENGTH),
            limitLength(ctx.userAgent(), FEEDBACK_METADATA_MAX_LENGTH),
            limitLength(ctx.clientIp(), 128),
            limitLength(getString(body, "clientTimestamp"), FEEDBACK_METADATA_MAX_LENGTH),
            serverTimestamp
        );

        try {
            new FeedbackStore(ctx.getConfig()).save(report);
        } catch (IOException e) {
            System.err.println("Feedback local save failed: " + e.getMessage());
            ctx.json(500, Map.of("error", "Feedback could not be saved."));
            return;
        }

        boolean discordDelivered = false;
        try {
            discordDelivered = new DiscordWebhookService(ctx.getConfig()).deliver(report);
        } catch (IOException e) {
            System.err.println("Discord feedback delivery failed for report " + report.getId() + ": " + e.getMessage());
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("ok", true);
        payload.put("reportId", report.getId());
        payload.put("discordDelivered", discordDelivered);
        ctx.json(200, payload);
    }

    private static void submitLockedAccountReport(RequestContext ctx) throws IOException {
        Map<String, Object> body;
        try {
            body = ctx.readJsonMap(FEEDBACK_MAX_BODY_BYTES);
        } catch (IOException e) {
            String message = Objects.toString(e.getMessage(), "");
            if (message.toLowerCase(Locale.ROOT).contains("too large")) {
                ctx.json(413, Map.of("error", "Account recovery report is too large."));
                return;
            }
            ctx.json(400, Map.of("error", "Account recovery report must be valid JSON."));
            return;
        }

        AccountStore.Account account;
        try {
            account = ctx.getAccountStore().lockedAccount(getString(body, "email"));
        } catch (IllegalArgumentException e) {
            ctx.json(400, Map.of("error", e.getMessage()));
            return;
        }
        if (!consumeLockedAccountReportRateLimit(ctx, account.getUsername())) {
            ctx.json(429, Map.of("error", "Too many account recovery reports submitted. Wait a few minutes and try again."));
            return;
        }

        String userMessage = limitLength(getString(body, "message"), FEEDBACK_MESSAGE_MAX_LENGTH).trim();
        String reportMessage = userMessage.isEmpty()
            ? "User requested review for a locked closed-beta account."
            : userMessage;
        FeedbackStore.Report report = new FeedbackStore.Report(
            UUID.randomUUID().toString(),
            "blocker",
            "high",
            "Locked account recovery request",
            reportMessage,
            "Login locked after three failed password attempts. User submitted this request from the locked-account prompt.",
            account.getId(),
            account.getUsername(),
            account.isLegacyGuest(),
            limitLength(getString(body, "route"), FEEDBACK_METADATA_MAX_LENGTH),
            "login",
            "",
            "",
            limitLength(getString(body, "userAgent"), FEEDBACK_METADATA_MAX_LENGTH),
            limitLength(ctx.userAgent(), FEEDBACK_METADATA_MAX_LENGTH),
            limitLength(ctx.clientIp(), 128),
            limitLength(getString(body, "clientTimestamp"), FEEDBACK_METADATA_MAX_LENGTH),
            Instant.now().toString()
        );

        try {
            new FeedbackStore(ctx.getConfig()).save(report);
        } catch (IOException e) {
            System.err.println("Locked account report local save failed: " + e.getMessage());
            ctx.json(500, Map.of("error", "Account recovery report could not be saved."));
            return;
        }

        boolean discordDelivered = false;
        try {
            discordDelivered = new DiscordWebhookService(ctx.getConfig()).deliver(report);
        } catch (IOException e) {
            System.err.println("Discord locked account report delivery failed for report " + report.getId() + ": " + e.getMessage());
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("ok", true);
        payload.put("reportId", report.getId());
        payload.put("discordDelivered", discordDelivered);
        ctx.json(200, payload);
    }

    private static void listDrafts(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        if (session.isLegacyGuest()) {
            ctx.json(200, Map.of(
                "drafts",
                List.of(),
                "maxDrafts",
                0,
                "canCreate",
                true,
                "transientGuest",
                true
            ));
            return;
        }
        List<Map<String, Object>> drafts = new ArrayList<>();
        for (String draftId : ctx.getAccountStore().listDraftIds(session.getUserId())) {
            try {
                drafts.add(buildDraftEntry(ctx, draftId));
            } catch (IOException ignored) {
                // Missing or invalid draft files are skipped from the account list.
            }
        }
        drafts.sort(Comparator.comparing(entry -> Objects.toString(entry.get("lastSaved"), ""), Comparator.reverseOrder()));
        ctx.json(200, Map.of(
            "drafts",
            drafts,
            "maxDrafts",
            2,
            "canCreate",
            ctx.getAccountStore().canAddDraft(session.getUserId())
        ));
    }

    private static void createDraft(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        if (!ensureCanCreateDraft(ctx, session)) {
            return;
        }
        Map<String, Object> body = ctx.readJsonMap();
        String locale = getString(body, "locale");
        Game game = new Game("");
        game.setUiLocale(locale);
        DraftStore.Draft draft = ctx.getDraftStore().createDraft(game);
        if (!session.isLegacyGuest()) {
            ctx.getAccountStore().addDraft(session.getUserId(), draft.getId());
        }
        session.setDraftId(draft.getId());
        ctx.json(200, Map.of(
            "draftId",
            draft.getId(),
            "locale",
            locale,
            "completedStages",
            game.getCompletedStages()
        ));
    }

    private static void importDraft(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        if (!ensureCanCreateDraft(ctx, session)) {
            return;
        }
        if (!consumeImportRateLimit(ctx, session)) {
            ctx.json(429, Map.of("error", "Each account can import up to two ruleset files per week during the closed beta."));
            return;
        }
        byte[] payload = ctx.readBody();
        try {
            DraftStore.Draft draft = ctx.getDraftStore().importDraft(payload);
            if (!session.isLegacyGuest()) {
                ctx.getAccountStore().addDraft(session.getUserId(), draft.getId());
            }
            session.setDraftId(draft.getId());
            String locale = Objects.toString(draft.getGame().getUiLocale(), "");
            ctx.json(200, Map.of(
                "draftId",
                draft.getId(),
                "locale",
                locale,
                "completedStages",
                draft.getGame().getCompletedStages()
            ));
        } catch (ClassNotFoundException e) {
            ctx.json(400, Map.of("error", "Invalid game file"));
        }
    }

    private static void openDraft(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        DraftStore.Draft draft = ctx.getDraftStore().openDraft(draftId);
        session.setDraftId(draft.getId());
        String locale = Objects.toString(draft.getGame().getUiLocale(), "");
        ctx.json(200, Map.of(
            "draftId",
            draft.getId(),
            "locale",
            locale,
            "completedStages",
            draft.getGame().getCompletedStages()
        ));
    }

    private static void deleteDraft(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        ctx.getDraftStore().deleteDraft(draftId);
        if (!session.isLegacyGuest()) {
            CharacterDraftStore characterDraftStore = new CharacterDraftStore(ctx.getConfig());
            for (String characterDraftId : ctx.getAccountStore().listCharacterDraftIdsForGameDraft(session.getUserId(), draftId)) {
                characterDraftStore.deleteCharacterDraft(characterDraftId);
                ctx.getAccountStore().removeCharacterDraft(session.getUserId(), characterDraftId);
            }
            ctx.getAccountStore().removeDraft(session.getUserId(), draftId);
        }
        if (Objects.equals(session.getDraftId(), draftId)) {
            session.setDraftId("");
        }
        ctx.json(200, Map.of("ok", true));
    }

    private static void exportDraft(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        if (!consumeExportRateLimit(ctx, session)) {
            ctx.json(429, Map.of("error", "Each account can download up to two ruleset files per week during the closed beta."));
            return;
        }
        String draftId = ctx.pathParam("id");
        byte[] data = ctx.getDraftStore().exportDraft(draftId);
        String filename = ctx.getDraftStore().readDraft(draftId, game -> new GameSaveIO().buildFilename(game));
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        ctx.bytes(200, data, "application/octet-stream", headers);
    }

    private static void listCharacterDrafts(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        if (session.isLegacyGuest()) {
            ctx.json(200, Map.of(
                "characters",
                List.of(),
                "maxCharacters",
                0,
                "maxCharactersPerDraft",
                0,
                "transientGuest",
                true
            ));
            return;
        }
        CharacterDraftStore characterDraftStore = new CharacterDraftStore(ctx.getConfig());
        List<Map<String, Object>> characters = new ArrayList<>();
        for (String characterDraftId : ctx.getAccountStore().listCharacterDraftIds(session.getUserId())) {
            try {
                CharacterDraftStore.CharacterDraftSummary summary = characterDraftStore.summarize(characterDraftId);
                characters.add(characterDraftEntry(summary));
            } catch (IOException ignored) {
                // Missing or invalid character draft files are skipped from the account list.
            }
        }
        characters.sort(Comparator.comparing(entry -> Objects.toString(entry.get("lastSaved"), ""), Comparator.reverseOrder()));
        ctx.json(200, Map.of(
            "characters",
            characters,
            "maxCharacters",
            4,
            "maxCharactersPerDraft",
            2,
            "canCreate",
            characters.size() < 4
        ));
    }

    private static void saveCharacterDraft(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        if (session.isLegacyGuest()) {
            ctx.json(400, Map.of("error", "Log in before saving character drafts to this server."));
            return;
        }
        Map<String, Object> body = ctx.readJsonMap();
        String characterDraftId = getString(body, "id").trim();
        String gameDraftId = getString(body, "gameDraftId").trim();
        String text = getString(body, "text");
        if (gameDraftId.isEmpty()) {
            ctx.json(400, Map.of("error", "Character draft must be linked to a saved ruleset."));
            return;
        }
        if (parseCharacterDraftField(text, "characterName").isEmpty()) {
            ctx.json(400, Map.of("error", "Enter a character name before saving this character."));
            return;
        }
        if (!ctx.getAccountStore().userOwnsDraft(session.getUserId(), gameDraftId)) {
            ctx.json(403, Map.of("error", "Character draft must be linked to one of your saved rulesets."));
            return;
        }
        text = withCurrentCharacterRuleModes(ctx, gameDraftId, text);
        boolean existing = !characterDraftId.isEmpty()
            && ctx.getAccountStore().userOwnsCharacterDraft(session.getUserId(), characterDraftId);
        if (!existing && !ctx.getAccountStore().canAddCharacterDraft(session.getUserId(), gameDraftId)) {
            ctx.json(400, Map.of("error", "Each account can save up to four characters, with up to two characters per saved ruleset."));
            return;
        }
        try {
            CharacterDraftStore.CharacterDraft draft = new CharacterDraftStore(ctx.getConfig()).saveCharacterDraft(characterDraftId, text);
            ctx.getAccountStore().addCharacterDraft(session.getUserId(), draft.getId(), gameDraftId);
            ctx.json(200, Map.of(
                "ok",
                true,
                "id",
                draft.getId(),
                "lastSaved",
                draft.getLastSaved().toString()
            ));
        } catch (IllegalArgumentException e) {
            ctx.json(400, Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            ctx.json(400, Map.of("error", e.getMessage()));
        }
    }

    private static void importCharacterFile(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        byte[] payload = ctx.readBody(96L * 1024);
        Path tempFile = Files.createTempFile("gmrules-character-import-", ".gmcf");
        try {
            Files.write(tempFile, payload);
            CharacterFile characterFile = new CharacterFileIO().readCharacterFile(tempFile);
            String gameDraftId = resolveCharacterFileDraftId(ctx, session, characterFile);
            if (gameDraftId.isEmpty()) {
                ctx.json(400, Map.of("error", "Open or save the matching ruleset before importing this character."));
                return;
            }
            if (!canAccessCharacterExportDraft(ctx, session, gameDraftId)) {
                ctx.json(403, Map.of("error", "Character must be linked to one of your saved rulesets."));
                return;
            }
            DraftStore.Draft draft = ctx.getDraftStore().openDraft(gameDraftId);
            session.setDraftId(draft.getId());
            String text = serializeCharacterFileDraft(characterFile, gameDraftId);
            ctx.json(200, Map.of(
                "draftId",
                draft.getId(),
                "text",
                text,
                "locale",
                Objects.toString(draft.getGame().getUiLocale(), ""),
                "completedStages",
                draft.getGame().getCompletedStages()
            ));
        } catch (IOException | RuntimeException e) {
            ctx.json(400, Map.of("error", "Invalid character file."));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    private static void exportCharacterFile(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        Map<String, Object> body = ctx.readJsonMap(96L * 1024);
        String gameDraftId = getString(body, "gameDraftId").trim();
        String text = getString(body, "text");
        List<String> candidateDraftIds = characterExportDraftCandidates(gameDraftId, text, session);
        CharacterDraft characterDraft;
        try {
            characterDraft = parseCharacterDraft(text);
            validateCharacterDraft(characterDraft, !candidateDraftIds.isEmpty());
        } catch (IOException | IllegalArgumentException e) {
            ctx.json(400, Map.of("error", e.getMessage()));
            return;
        }
        if (candidateDraftIds.isEmpty()) {
            ctx.json(400, Map.of("error", "Character must be linked to a saved ruleset."));
            return;
        }
        if (!canAccessAnyCharacterExportDraft(ctx, session, candidateDraftIds)) {
            ctx.json(403, Map.of("error", "Character must be linked to one of your saved rulesets."));
            return;
        }

        CharacterFileIO characterFileIO = new CharacterFileIO();
        try {
            for (String candidateDraftId : candidateDraftIds) {
                if (!canAccessCharacterExportDraft(ctx, session, candidateDraftId)) {
                    continue;
                }
                try {
                    CharacterExport export = ctx.getDraftStore().readDraft(candidateDraftId, game -> {
                        try {
                            byte[] data = writeCharacterFileBytes(characterFileIO, game, characterDraft);
                            String filename = characterFileIO.normalizeFilename(buildCharacterExportFilename(game, characterDraft));
                            return new CharacterExport(data, filename);
                        } catch (IOException e) {
                            throw new CharacterExportException(e);
                        }
                    });
                    Map<String, String> headers = new LinkedHashMap<>();
                    headers.put("Content-Disposition", "attachment; filename=\"" + export.getFilename() + "\"");
                    ctx.bytes(200, export.getData(), "application/octet-stream", headers);
                    return;
                } catch (IOException ignored) {
                    // Try the next known source draft id before reporting a missing linked ruleset.
                }
            }
            ctx.json(404, Map.of("error", "Linked ruleset not found."));
        } catch (CharacterExportException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IllegalArgumentException) {
                ctx.json(400, Map.of("error", cause.getMessage()));
            } else if (cause instanceof IOException) {
                ctx.json(400, Map.of("error", "Character file could not be exported."));
            } else {
                ctx.json(400, Map.of("error", "Character file could not be exported."));
            }
        }
    }

    private static void openCharacterDraft(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String characterDraftId = ctx.pathParam("id");
        if (session.isLegacyGuest() || !ctx.getAccountStore().userOwnsCharacterDraft(session.getUserId(), characterDraftId)) {
            ctx.json(404, Map.of("error", "Character draft not found."));
            return;
        }
        try {
            CharacterDraftStore.CharacterDraft draft = new CharacterDraftStore(ctx.getConfig()).readCharacterDraft(characterDraftId);
            ctx.json(200, Map.of(
                "id",
                draft.getId(),
                "text",
                draft.getText(),
                "lastSaved",
                draft.getLastSaved().toString()
            ));
        } catch (IOException e) {
            ctx.json(404, Map.of("error", "Character draft not found."));
        }
    }

    private static void deleteCharacterDraft(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String characterDraftId = ctx.pathParam("id");
        if (session.isLegacyGuest() || !ctx.getAccountStore().userOwnsCharacterDraft(session.getUserId(), characterDraftId)) {
            ctx.json(404, Map.of("error", "Character draft not found."));
            return;
        }
        new CharacterDraftStore(ctx.getConfig()).deleteCharacterDraft(characterDraftId);
        ctx.getAccountStore().removeCharacterDraft(session.getUserId(), characterDraftId);
        ctx.json(200, Map.of("ok", true));
    }

    private static void summary(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        String summary = ctx.getDraftStore().readDraft(draftId, Game::getSummary);
        ctx.json(200, Map.of("summary", summary));
    }

    private static void getLocale(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        String locale = ctx.getDraftStore().readDraft(draftId, Game::getUiLocale);
        ctx.json(200, Map.of("locale", locale));
    }

    private static void updateLocale(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String locale = getString(body, "locale");
        ctx.getDraftStore().updateDraft(draftId, game -> game.setUiLocale(locale));
        ctx.json(200, Map.of("ok", true));
    }

    private static void getSystemNames(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("systemNames", game.getSystemNames());
            return response;
        });
        ctx.json(200, payload);
    }

    private static void updateSystemName(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String key = getString(body, "key").trim();
        String name = getString(body, "name").trim();
        if (key.isEmpty()) {
            ctx.json(400, Map.of("error", "Key is required"));
            return;
        }
        ctx.getDraftStore().updateDraft(draftId, game -> game.setSystemName(key, name));
        ctx.json(200, Map.of("ok", true));
    }

    private static void getSetup(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        markStageCompleted(ctx, draftId, "setup");
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("id", Objects.toString(game.getId(), ""));
            response.put("name", Objects.toString(game.getName(), ""));
            response.put("description", Objects.toString(game.getDescription(), ""));
            response.put("gameType", Objects.toString(game.getGameType(), ""));
            response.put("gameTypes", Game.getGameTypes());
            return response;
        });
        ctx.json(200, payload);
    }

    private static void updateSetup(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String name = getString(body, "name");
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        String description = getString(body, "description");
        String gameType = getString(body, "gameType");
        ctx.getDraftStore().updateDraft(draftId, game -> {
            game.setName(name);
            game.setDescription(description);
            game.setGameType(gameType);
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void getMeasurements(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        markStageCompleted(ctx, draftId, "measurements");
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("weightSystem", Objects.toString(game.getWeightSystem(), ""));
            List<Map<String, Object>> timeUnits = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : game.getTimeUnits().entrySet()) {
                Map<String, Object> unit = new LinkedHashMap<>();
                unit.put("name", Objects.toString(entry.getKey(), ""));
                unit.put("duration", entry.getValue());
                timeUnits.add(unit);
            }
            response.put("timeUnits", timeUnits);
            return response;
        });
        ctx.json(200, payload);
    }

    private static void updateMeasurements(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String weightSystem = getString(body, "weightSystem");
        List<Map<String, Object>> timeUnits = getMapList(body, "timeUnits");
        ctx.getDraftStore().updateDraft(draftId, game -> {
            game.setWeightSystem(weightSystem);
            Map<String, Integer> units = new LinkedHashMap<>();
            for (Map<String, Object> unit : timeUnits) {
                String name = getString(unit, "name");
                int duration = getInt(unit, "duration", 0);
                if (name.isEmpty() || duration <= 0) {
                    continue;
                }
                units.put(name, duration);
            }
            game.setTimeUnits(units);
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void getDice(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        markStageCompleted(ctx, draftId, "dice");
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("diceUsed", game.getDiceUsed());
            List<Map<String, Object>> ranges = new ArrayList<>();
            for (Game.DiceRange range : game.getCustomDiceRanges()) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("min", range.getMinValue());
                entry.put("max", range.getMaxValue());
                ranges.add(entry);
            }
            response.put("customRanges", ranges);
            return response;
        });
        ctx.json(200, payload);
    }

    private static void updateStandardDice(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        int sides = getInt(body, "sides", 0);
        boolean selected = getBoolean(body, "selected", false);
        ctx.getDraftStore().updateDraft(draftId, game -> {
            if (selected) {
                game.addDiceUsed(sides);
            } else {
                game.removeDiceUsed(sides);
            }
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void addCustomDiceRange(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        int min = getInt(body, "min", 0);
        int max = getInt(body, "max", 0);
        ctx.getDraftStore().updateDraft(draftId, game -> game.addCustomDiceRange(min, max));
        ctx.json(200, Map.of("ok", true));
    }

    private static void removeCustomDiceRange(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        int min = getInt(body, "min", 0);
        int max = getInt(body, "max", 0);
        ctx.getDraftStore().updateDraft(draftId, game -> game.removeCustomDiceRange(min, max));
        ctx.json(200, Map.of("ok", true));
    }

    private static void getAttributeTypes(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        markStageCompleted(ctx, draftId, "attribute-types");
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            AttributeTypes registry = game.getRegistry(RegistryKey.ATTRIBUTE_TYPES);
            List<Map<String, Object>> types = new ArrayList<>();
            for (AttributeType type : registry.getAll()) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("key", Objects.toString(type.getKey(), ""));
                entry.put("name", Objects.toString(type.getName(), ""));
                entry.put("description", Objects.toString(type.getDescription(), ""));
                entry.put("displayName", Objects.toString(type.getDisplayName(), ""));
                types.add(entry);
            }
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("systemName", game.getSystemName("attribute-types"));
            response.put("types", types);
            return response;
        });
        ctx.json(200, payload);
    }

    private static void addAttributeType(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String name = getString(body, "name").trim();
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        AttributeType attributeType = new AttributeType(name, name, "", true);
        boolean[] added = new boolean[] { false };
        ctx.getDraftStore().updateDraft(draftId, game -> {
            AttributeTypes registry = game.getRegistry(RegistryKey.ATTRIBUTE_TYPES);
            if (registry.contains(name)) {
                return;
            }
            registry.register(attributeType);
            added[0] = true;
        });
        if (!added[0]) {
            ctx.json(400, Map.of("error", "Attribute type already exists"));
            return;
        }
        ctx.json(200, Map.of(
            "ok", true,
            "key", Objects.toString(attributeType.getKey(), ""),
            "name", Objects.toString(attributeType.getName(), ""),
            "description", Objects.toString(attributeType.getDescription(), ""),
            "displayName", Objects.toString(attributeType.getDisplayName(), "")
        ));
    }

    private static void removeAttributeType(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String key = getString(body, "key");
        ctx.getDraftStore().updateDraft(draftId, game -> {
            AttributeTypes registry = game.getRegistry(RegistryKey.ATTRIBUTE_TYPES);
            registry.remove(key);
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void updateAttributeTypeDetails(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String key = getString(body, "key");
        String name = getString(body, "name");
        String description = getString(body, "description");
        if (key.isEmpty()) {
            ctx.json(400, Map.of("error", "Key is required"));
            return;
        }
        if (name.trim().isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        ctx.getDraftStore().updateDraft(draftId, game -> {
            AttributeTypes registry = game.getRegistry(RegistryKey.ATTRIBUTE_TYPES);
            AttributeType type = registry.get(key);
            if (type == null) {
                return;
            }
            type.setName(name);
            type.setDescription(description);
            game.updateLastModified();
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void getEffectTypes(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        markStageCompleted(ctx, draftId, "effect-types");
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            EffectTypes registry = game.getRegistry(RegistryKey.EFFECT_TYPES);
            List<Map<String, Object>> types = new ArrayList<>();
            for (EffectType type : registry.getAll()) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("key", normalizeEffectTypeKey(type.getName()));
                entry.put("name", Objects.toString(type.getName(), ""));
                entry.put("description", Objects.toString(type.getDescription(), ""));
                entry.put("displayName", Objects.toString(type.getDisplayName(), ""));
                types.add(entry);
            }
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("systemName", game.getSystemName("effect-types"));
            response.put("types", types);
            return response;
        });
        ctx.json(200, payload);
    }

    private static void addEffectType(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String name = getString(body, "name").trim();
        String description = getString(body, "description").trim();
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        EffectType type = new EffectType(name, description);
        boolean[] added = new boolean[] { false };
        ctx.getDraftStore().updateDraft(draftId, game -> {
            EffectTypes registry = game.getRegistry(RegistryKey.EFFECT_TYPES);
            if (registry.contains(name)) {
                return;
            }
            registry.register(type);
            added[0] = true;
        });
        if (!added[0]) {
            ctx.json(400, Map.of("error", "Effect type already exists"));
            return;
        }
        ctx.json(200, Map.of(
            "ok", true,
            "key", normalizeEffectTypeKey(type.getName()),
            "name", Objects.toString(type.getName(), ""),
            "description", Objects.toString(type.getDescription(), ""),
            "displayName", Objects.toString(type.getDisplayName(), "")
        ));
    }

    private static void removeEffectType(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String key = getString(body, "key");
        ctx.getDraftStore().updateDraft(draftId, game -> {
            EffectTypes registry = game.getRegistry(RegistryKey.EFFECT_TYPES);
            EffectType type = registry.get(key);
            if (type == null) {
                return;
            }
            registry.remove(type.getName());
            updateEffectTypeReferences(game, type.getName(), "");
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void updateEffectTypeDetails(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String key = getString(body, "key");
        String name = getString(body, "name").trim();
        String description = getString(body, "description").trim();
        if (key.isEmpty()) {
            ctx.json(400, Map.of("error", "Key is required"));
            return;
        }
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        boolean[] duplicate = new boolean[] { false };
        boolean[] updated = new boolean[] { false };
        ctx.getDraftStore().updateDraft(draftId, game -> {
            EffectTypes registry = game.getRegistry(RegistryKey.EFFECT_TYPES);
            EffectType type = registry.get(key);
            if (type == null) {
                return;
            }
            String previousName = type.getName();
            if (!previousName.equalsIgnoreCase(name) && registry.contains(name)) {
                duplicate[0] = true;
                return;
            }
            if (!previousName.equalsIgnoreCase(name)) {
                registry.remove(previousName);
                type.setName(name);
                registry.register(type);
                updateEffectTypeReferences(game, previousName, name);
            }
            type.setDescription(description);
            updated[0] = true;
        });
        if (duplicate[0]) {
            ctx.json(400, Map.of("error", "Effect type already exists"));
            return;
        }
        if (!updated[0]) {
            ctx.json(404, Map.of("error", "Effect type not found"));
            return;
        }
        ctx.json(200, Map.of("ok", true));
    }

    private static void getAttributes(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        markStageCompleted(ctx, draftId, "attributes");
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("systemName", game.getSystemName("attributes"));
            List<Map<String, Object>> attributes = new ArrayList<>();
            AttributeTypes registry = game.getRegistry(RegistryKey.ATTRIBUTE_TYPES);
            for (Attribute attribute : getAttributes(game)) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("id", Objects.toString(attribute.getId(), ""));
                entry.put("name", Objects.toString(attribute.getName(), ""));
                entry.put("displayName", Objects.toString(attribute.getDisplayName(), ""));
                entry.put("description", Objects.toString(attribute.getDescription(), ""));
                entry.put("minValue", attribute.getMinValue());
                entry.put("maxValue", attribute.getMaxValue());
                String typeKey = Objects.toString(attribute.getType(), "");
                entry.put("typeKey", typeKey);
                AttributeType type = registry.get(typeKey);
                entry.put("typeName", type == null ? "" : Objects.toString(type.getDisplayName(), ""));

                List<Map<String, Object>> modifiers = new ArrayList<>();
                for (Map.Entry<Float, Float> modEntry : attribute.getModifierMap().entrySet()) {
                    Map<String, Object> mod = new LinkedHashMap<>();
                    mod.put("score", modEntry.getKey());
                    mod.put("modifier", modEntry.getValue());
                    modifiers.add(mod);
                }
                entry.put("modifiers", modifiers);

                List<Map<String, Object>> bonuses = new ArrayList<>();
                for (Map.Entry<Integer, ArrayList<String>> bonusEntry : attribute.getAllScoreBonuses().entrySet()) {
                    int threshold = bonusEntry.getKey();
                    for (String effect : bonusEntry.getValue()) {
                        Map<String, Object> bonus = new LinkedHashMap<>();
                        bonus.put("threshold", threshold);
                        bonus.put("effect", Objects.toString(effect, ""));
                        bonuses.add(bonus);
                    }
                }
                entry.put("scoreBonuses", bonuses);
                attributes.add(entry);
            }
            response.put("attributes", attributes);
            response.put("attributeModifiers", serializeAttributeModifiers(game.getAttributeModifiers()));
            response.put(
                "applyAttributeModifiersToAllAttributes",
                game.isApplyAttributeModifiersToAllAttributes()
            );
            response.put("defaultAttributeMinScore", game.getDefaultAttributeMinScore());
            response.put("defaultAttributeMaxScore", game.getDefaultAttributeMaxScore());

            List<Map<String, Object>> types = new ArrayList<>();
            for (AttributeType type : registry.getAll()) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("key", Objects.toString(type.getKey(), ""));
                entry.put("displayName", Objects.toString(type.getDisplayName(), ""));
                types.add(entry);
            }
            response.put("types", types);
            return response;
        });
        ctx.json(200, payload);
    }

    private static void getSkillCategories(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            SkillCategories registry = game.getRegistry(RegistryKey.SKILL_CATEGORIES);
            List<Map<String, Object>> categories = new ArrayList<>();
            for (SkillCategory category : registry.getAll()) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("key", Objects.toString(category.getKey(), ""));
                entry.put("name", Objects.toString(category.getName(), ""));
                entry.put("description", Objects.toString(category.getDescription(), ""));
                entry.put("displayName", Objects.toString(category.getDisplayName(), ""));
                categories.add(entry);
            }
            return Map.of("categories", categories);
        });
        ctx.json(200, payload);
    }

    private static void addSkillCategory(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String name = getString(body, "name").trim();
        String description = getString(body, "description").trim();
        String key = normalizeSkillCategoryKey(name);
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        if (key.isEmpty()) {
            ctx.json(400, Map.of("error", "Category key is required"));
            return;
        }
        SkillCategory category = new SkillCategory(key, name, description, true);
        boolean[] added = new boolean[] { false };
        ctx.getDraftStore().updateDraft(draftId, game -> {
            SkillCategories registry = game.getRegistry(RegistryKey.SKILL_CATEGORIES);
            if (registry.contains(key)) {
                return;
            }
            registry.register(category);
            added[0] = true;
        });
        if (!added[0]) {
            ctx.json(400, Map.of("error", "Category already exists"));
            return;
        }
        ctx.json(200, Map.of(
            "ok", true,
            "key", Objects.toString(category.getKey(), ""),
            "name", Objects.toString(category.getName(), ""),
            "description", Objects.toString(category.getDescription(), ""),
            "displayName", Objects.toString(category.getDisplayName(), "")
        ));
    }

    private static void addAttribute(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String name = getString(body, "name").trim();
        String typeKey = getString(body, "typeKey");
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        Attribute attribute = new Attribute(name);
        if (!typeKey.isEmpty()) {
            attribute.setType(typeKey);
        }
        boolean[] added = new boolean[] { false };
        ctx.getDraftStore().updateDraft(draftId, game -> {
            if (game.addElement("attributes", attribute)) {
                added[0] = true;
            }
        });
        if (!added[0]) {
            ctx.json(400, Map.of("error", "Attribute already exists"));
            return;
        }
        ctx.json(200, Map.of("ok", true, "id", Objects.toString(attribute.getId(), "")));
    }

    private static void removeAttribute(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String attributeId = getString(body, "id");
        ctx.getDraftStore().updateDraft(draftId, game -> {
            Attribute attribute = game.getElement("attributes", attributeId);
            if (attribute != null) {
                game.removeElement("attributes", attribute);
            }
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void updateAttributeType(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String attributeId = getString(body, "id");
        String typeKey = getString(body, "typeKey");
        ctx.getDraftStore().updateDraft(draftId, game -> {
            Attribute attribute = game.getElement("attributes", attributeId);
            if (attribute != null) {
                attribute.setType(typeKey);
                game.updateLastModified();
            }
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void updateAttributeDetails(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String attributeId = getString(body, "id");
        String name = getString(body, "name");
        String description = getString(body, "description");
        String typeKey = getString(body, "typeKey");
        int minValue = getInt(body, "minValue", 0);
        int maxValue = getInt(body, "maxValue", 0);
        List<Map<String, Object>> modifiers = getMapList(body, "modifiers");
        List<Map<String, Object>> bonuses = getMapList(body, "scoreBonuses");

        if (attributeId.isEmpty()) {
            ctx.json(400, Map.of("error", "Attribute id is required"));
            return;
        }
        if (name.trim().isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        if (minValue > maxValue) {
            ctx.json(400, Map.of("error", "Minimum cannot exceed maximum"));
            return;
        }

        ctx.getDraftStore().updateDraft(draftId, game -> {
            Attribute attribute = game.getElement("attributes", attributeId);
            if (attribute == null) {
                return;
            }
            attribute.setName(name);
            attribute.setDescription(description);
            attribute.setType(typeKey);
            attribute.setMinValue(minValue);
            attribute.setMaxValue(maxValue);

            Map<Float, Float> modifierMap = new LinkedHashMap<>();
            for (Map<String, Object> entry : modifiers) {
                double score = getDouble(entry, "score", 0.0);
                double modifier = getDouble(entry, "modifier", 0.0);
                modifierMap.put((float) score, (float) modifier);
            }
            attribute.setModifierMap(modifierMap);

            Map<Integer, ArrayList<String>> bonusMap = new LinkedHashMap<>();
            for (Map<String, Object> entry : bonuses) {
                int threshold = getInt(entry, "threshold", 0);
                String effect = getString(entry, "effect").trim();
                if (effect.isEmpty()) {
                    continue;
                }
                bonusMap.computeIfAbsent(threshold, key -> new ArrayList<>()).add(effect);
            }
            attribute.setScoreBonuses(bonusMap);
            game.updateLastModified();
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static List<Map<String, Object>> serializeAttributeModifiers(AttributeModifiers attributeModifiers) {
        List<Map<String, Object>> modifiers = new ArrayList<>();
        for (Map.Entry<Float, Float> entry : attributeModifiers.getModifierMap().entrySet()) {
            Map<String, Object> modifierEntry = new LinkedHashMap<>();
            modifierEntry.put("score", entry.getKey());
            modifierEntry.put("modifier", entry.getValue());
            modifiers.add(modifierEntry);
        }
        modifiers.sort(Comparator.comparingDouble(entry -> ((Number) entry.get("score")).doubleValue()));
        return modifiers;
    }

    private static void getAttributeGeneration(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        markStageCompleted(ctx, draftId, "attribute-generation");
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            AttributeGenerationMethod method = game.getAttributeGenerationMethod();
            List<String> hybridStages = method.getArray(ARRAY_HYBRID);
            return Map.of(
                "generationType",
                Objects.toString(method.getGenerationType(), ""),
                "hybridStages",
                hybridStages,
                "defaultAttributeMinScore",
                game.getDefaultAttributeMinScore(),
                "defaultAttributeMaxScore",
                game.getDefaultAttributeMaxScore(),
                "applyAttributeModifiersToAllAttributes",
                game.isApplyAttributeModifiersToAllAttributes(),
                "attributeModifiers",
                serializeAttributeModifiers(game.getAttributeModifiers())
            );
        });
        ctx.json(200, payload);
    }

    private static void updateAttributeGeneration(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String generationType = getString(body, "generationType");
        boolean hasHybridStages = body.get(ARRAY_HYBRID) instanceof List<?>;
        List<String> hybridStages = getStringList(body, ARRAY_HYBRID);
        int defaultMinScore = getInt(body, "defaultAttributeMinScore", 0);
        int defaultMaxScore = getInt(body, "defaultAttributeMaxScore", 0);
        boolean useDefaultScoreRange = defaultMinScore != 0 || defaultMaxScore != 0;
        boolean applyModifiersToAllAttributes = useDefaultScoreRange
            && getBoolean(body, "applyAttributeModifiersToAllAttributes", false);
        List<Map<String, Object>> modifiers = getMapList(body, "attributeModifiers");
        if (defaultMinScore > defaultMaxScore) {
            ctx.json(400, Map.of("error", "Minimum score cannot exceed maximum"));
            return;
        }
        ctx.getDraftStore().updateDraft(draftId, game -> {
            AttributeGenerationMethod method = game.getAttributeGenerationMethod();
            method.setGenerationType(generationType);
            game.setDefaultAttributeScoreRange(defaultMinScore, defaultMaxScore);
            game.setApplyAttributeModifiersToAllAttributes(applyModifiersToAllAttributes);
            if (applyModifiersToAllAttributes) {
                Map<Float, Float> modifierMap = new LinkedHashMap<>();
                for (Map<String, Object> entry : modifiers) {
                    double score = getDouble(entry, "score", 0.0);
                    double modifier = getDouble(entry, "modifier", 0.0);
                    modifierMap.put((float) score, (float) modifier);
                }
                game.getAttributeModifiers().setModifierMap(modifierMap);
                for (Attribute target : game.getElementRegistry(ElementRegistryKey.ATTRIBUTES).getAll()) {
                    target.setModifierMap(game.getAttributeModifiers().getModifierMap());
                }
            }
            if (hasHybridStages) {
                method.clearArray(ARRAY_HYBRID);
                for (String stage : hybridStages) {
                    method.addToArray(ARRAY_HYBRID, stage);
                }
            }
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void getCharGenAttributeGeneration(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            AttributeGenerationMethod method = game.getAttributeGenerationMethod();
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("generationType", Objects.toString(method.getGenerationType(), ""));
            response.put("hybridStages", safeList(method.getArray(ARRAY_HYBRID)));
            response.put("numberOfSets", method.getNumberOfSets());
            response.put("setSelectionMethod", Objects.toString(method.getSetSelectionMethod(), ""));
            response.put("assignInOrder", method.isAssignInOrder());
            response.put("baseAttributeValue", method.getBaseAttributeValue());
            response.put("minAttributeValue", method.getMinAttributeValue());
            response.put("maxAttributeValue", method.getMaxAttributeValue());
            response.put("allowDiceSubstitution", method.isAllowDiceSubstitution());
            response.put("diceSubstitutionValue", method.getDiceSubstitutionValue());
            response.put("maxDiceSubstitutions", method.getMaxDiceSubstitutions());
            response.put("standardArray", safeList(method.getArray("standardArrays")));
            response.put("eliteArray", safeList(method.getArray("eliteArrays")));
            response.put("defaultArrayType", Objects.toString(method.getDefaultArrayType(), ""));
            response.put(
                "standardArrayAssignmentMode",
                Objects.toString(method.getStandardArrayAssignmentMode(), "assigned")
            );
            response.put("defaultAttributeMinScore", game.getDefaultAttributeMinScore());
            response.put("defaultAttributeMaxScore", game.getDefaultAttributeMaxScore());
            response.put(
                "applyAttributeModifiersToAllAttributes",
                game.isApplyAttributeModifiersToAllAttributes()
            );
            response.put("attributeModifiers", serializeAttributeModifiers(game.getAttributeModifiers()));

            response.put("basePoints", method.getBasePoints());
            response.put("minimumPointsToSpend", method.getMinimumPointsToSpend());
            response.put("allowNegativeAttributes", method.isAllowNegativeAttributes());
            Map<Integer, Integer> pointCosts = method.getPointCosts();
            List<Map<String, Object>> costs = new ArrayList<>();
            pointCosts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    Map<String, Object> costEntry = new LinkedHashMap<>();
                    costEntry.put("value", Objects.requireNonNullElse(entry.getKey(), 0));
                    costEntry.put("cost", Objects.requireNonNullElse(entry.getValue(), 0));
                    costs.add(costEntry);
                });
            response.put("pointCosts", costs);

            List<Map<String, Object>> terms = new ArrayList<>();
            for (AttributeGenerationMethod.DiceTerm term : method.getDiceTerms()) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("count", term.getCount());
                entry.put("sides", term.getSides());
                entry.put("dropLowest", term.getDropLowest());
                entry.put("dropHighest", term.getDropHighest());
                entry.put("flatModifier", term.getFlatModifier());
                entry.put("exploding", term.isExploding());
                entry.put("explodeThreshold", term.getExplodeThreshold());
                entry.put("ignoredFaces", term.getIgnoredFaces());
                entry.put("notation", term.getNotation());
                terms.add(entry);
            }
            response.put("diceTerms", terms);

            List<Map<String, Object>> attributes = new ArrayList<>();
            for (Attribute attribute : getAttributes(game)) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("id", Objects.toString(attribute.getId(), ""));
                entry.put("name", Objects.toString(attribute.getName(), ""));
                entry.put("displayName", Objects.toString(attribute.getDisplayName(), ""));
                entry.put("minValue", attribute.getMinValue());
                entry.put("maxValue", attribute.getMaxValue());
                attributes.add(entry);
            }
            response.put("attributes", attributes);
            return response;
        });
        ctx.json(200, payload);
    }

    private static void getStandardArrays(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        boolean enabled = ctx.getDraftStore().readDraft(
            draftId,
            game -> isStandardArrayEnabled(game.getAttributeGenerationMethod())
        );
        if (enabled) {
            markStageCompleted(ctx, draftId, "standard-array");
        }
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            AttributeGenerationMethod method = game.getAttributeGenerationMethod();
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("standardArrayEnabled", isStandardArrayEnabled(method));
            response.put("standardArray", safeList(method.getArray("standardArrays")));
            response.put("eliteArray", safeList(method.getArray("eliteArrays")));
            response.put("defaultArrayType", Objects.toString(method.getDefaultArrayType(), ""));
            response.put(
                "standardArrayAssignmentMode",
                Objects.toString(method.getStandardArrayAssignmentMode(), "assigned")
            );

            List<Map<String, Object>> attributes = new ArrayList<>();
            for (Attribute attribute : getAttributes(game)) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("id", Objects.toString(attribute.getId(), ""));
                entry.put("name", Objects.toString(attribute.getName(), ""));
                entry.put("displayName", Objects.toString(attribute.getDisplayName(), ""));
                attributes.add(entry);
            }
            response.put("attributes", attributes);
            return response;
        });
        ctx.json(200, payload);
    }

    private static void addStandardArray(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        if (!ensureStandardArrayEnabled(ctx, draftId)) {
            return;
        }
        Map<String, Object> body = ctx.readJsonMap();
        String attributeId = getString(body, "attributeId");
        int value = getInt(body, "value", 0);
        ctx.getDraftStore().updateDraft(draftId, game -> {
            AttributeGenerationMethod method = game.getAttributeGenerationMethod();
            if (isOpenStandardArray(method)) {
                method.addToArray("standardArrays", Integer.toString(value));
                return;
            }
            Attribute attribute = game.getElement("attributes", attributeId);
            if (attribute != null) {
                String name = Objects.toString(attribute.getName(), "").trim();
                if (!name.isEmpty()) {
                    method.addToArray("standardArrays", name + "=" + value);
                }
            }
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void removeStandardArray(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        if (!ensureStandardArrayEnabled(ctx, draftId)) {
            return;
        }
        Map<String, Object> body = ctx.readJsonMap();
        String entry = getString(body, "entry");
        ctx.getDraftStore().updateDraft(draftId, game -> {
            AttributeGenerationMethod method = game.getAttributeGenerationMethod();
            method.removeFromArray("standardArrays", entry);
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void addEliteArray(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        if (!ensureStandardArrayEnabled(ctx, draftId)) {
            return;
        }
        Map<String, Object> body = ctx.readJsonMap();
        String attributeId = getString(body, "attributeId");
        int value = getInt(body, "value", 0);
        ctx.getDraftStore().updateDraft(draftId, game -> {
            AttributeGenerationMethod method = game.getAttributeGenerationMethod();
            if (isOpenStandardArray(method)) {
                method.addToArray("eliteArrays", Integer.toString(value));
                return;
            }
            Attribute attribute = game.getElement("attributes", attributeId);
            if (attribute != null) {
                String name = Objects.toString(attribute.getName(), "").trim();
                if (!name.isEmpty()) {
                    method.addToArray("eliteArrays", name + "=" + value);
                }
            }
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void removeEliteArray(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        if (!ensureStandardArrayEnabled(ctx, draftId)) {
            return;
        }
        Map<String, Object> body = ctx.readJsonMap();
        String entry = getString(body, "entry");
        ctx.getDraftStore().updateDraft(draftId, game -> {
            AttributeGenerationMethod method = game.getAttributeGenerationMethod();
            method.removeFromArray("eliteArrays", entry);
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void setDefaultArrayType(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        if (!ensureStandardArrayEnabled(ctx, draftId)) {
            return;
        }
        Map<String, Object> body = ctx.readJsonMap();
        String defaultType = getString(body, "defaultArrayType");
        String assignmentMode = getString(body, "standardArrayAssignmentMode");
        ctx.getDraftStore().updateDraft(draftId, game -> {
            AttributeGenerationMethod method = game.getAttributeGenerationMethod();
            if (!defaultType.isEmpty()) {
                method.setDefaultArrayType(defaultType);
            }
            if (!assignmentMode.isEmpty()) {
                method.setStandardArrayAssignmentMode(assignmentMode);
            }
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void getDiceRolling(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        boolean enabled = ctx.getDraftStore().readDraft(
            draftId,
            game -> isDiceRollingEnabled(game.getAttributeGenerationMethod())
        );
        if (enabled) {
            markStageCompleted(ctx, draftId, "dice-rolling");
        }
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            AttributeGenerationMethod method = game.getAttributeGenerationMethod();
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("diceRollingEnabled", isDiceRollingEnabled(method));
            response.put("numberOfSets", method.getNumberOfSets());
            response.put("setSelectionMethod", Objects.toString(method.getSetSelectionMethod(), ""));
            response.put("allowDiceSubstitution", method.isAllowDiceSubstitution());
            response.put("diceSubstitutionValue", method.getDiceSubstitutionValue());
            response.put("maxDiceSubstitutions", method.getMaxDiceSubstitutions());
            response.put("diceUsed", new ArrayList<>(game.getDiceUsed()));
            List<Map<String, Object>> terms = new ArrayList<>();
            for (AttributeGenerationMethod.DiceTerm term : method.getDiceTerms()) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("count", term.getCount());
                entry.put("sides", term.getSides());
                entry.put("dropLowest", term.getDropLowest());
                entry.put("ignoredFaces", term.getIgnoredFaces());
                entry.put("notation", term.getNotation());
                terms.add(entry);
            }
            response.put("terms", terms);
            return response;
        });
        ctx.json(200, payload);
    }

    private static void updateDiceSets(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        if (!ensureDiceRollingEnabled(ctx, draftId)) {
            return;
        }
        Map<String, Object> body = ctx.readJsonMap();
        int sets = getInt(body, "numberOfSets", 0);
        ctx.getDraftStore().updateDraft(draftId, game -> {
            AttributeGenerationMethod method = game.getAttributeGenerationMethod();
            method.setNumberOfSets(sets);
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void updateDiceMethod(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        if (!ensureDiceRollingEnabled(ctx, draftId)) {
            return;
        }
        Map<String, Object> body = ctx.readJsonMap();
        String selectionMethod = getString(body, "setSelectionMethod");
        ctx.getDraftStore().updateDraft(draftId, game -> {
            AttributeGenerationMethod method = game.getAttributeGenerationMethod();
            method.setSetSelectionMethod(selectionMethod.trim());
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void updateDiceSubstitution(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        if (!ensureDiceRollingEnabled(ctx, draftId)) {
            return;
        }
        Map<String, Object> body = ctx.readJsonMap();
        boolean allowDiceSubstitution = getBoolean(body, "allowDiceSubstitution", false);
        int diceSubstitutionValue = Math.max(0, getInt(body, "diceSubstitutionValue", 14));
        int maxDiceSubstitutions = Math.max(0, getInt(body, "maxDiceSubstitutions", 1));
        ctx.getDraftStore().updateDraft(draftId, game -> {
            AttributeGenerationMethod method = game.getAttributeGenerationMethod();
            method.setAllowDiceSubstitution(allowDiceSubstitution);
            method.setDiceSubstitutionValue(diceSubstitutionValue);
            method.setMaxDiceSubstitutions(maxDiceSubstitutions);
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void addDiceTerm(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        if (!ensureDiceRollingEnabled(ctx, draftId)) {
            return;
        }
        Map<String, Object> body = ctx.readJsonMap();
        int count = getInt(body, "count", 0);
        int sides = getInt(body, "sides", 0);
        int rerollResult = getInt(body, "rerollResult", 0);
        boolean dropLowest = getBoolean(body, "dropLowest", false);
        ctx.getDraftStore().updateDraft(draftId, game -> {
            AttributeGenerationMethod method = game.getAttributeGenerationMethod();
            AttributeGenerationMethod.DiceTerm term = new AttributeGenerationMethod.DiceTerm(count, sides);
            if (dropLowest) {
                term.setDropLowest(1);
            }
            int maxFace = Math.min(rerollResult - 1, sides);
            if (maxFace > 0) {
                ArrayList<Integer> ignoredFaces = new ArrayList<>();
                for (int face = 1; face <= maxFace; face++) {
                    ignoredFaces.add(face);
                }
                term.setIgnoredFaces(ignoredFaces);
            }
            method.addDiceTerm(term);
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void removeDiceTerm(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        if (!ensureDiceRollingEnabled(ctx, draftId)) {
            return;
        }
        Map<String, Object> body = ctx.readJsonMap();
        int index = getInt(body, "index", -1);
        ctx.getDraftStore().updateDraft(draftId, game -> {
            AttributeGenerationMethod method = game.getAttributeGenerationMethod();
            List<AttributeGenerationMethod.DiceTerm> terms = method.getDiceTerms();
            if (index < 0 || index >= terms.size()) {
                return;
            }
            terms.remove(index);
            method.setDiceTerms(terms);
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void getPointsBuy(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        boolean enabled = ctx.getDraftStore().readDraft(
            draftId,
            game -> isPointBuyEnabled(game.getAttributeGenerationMethod())
        );
        if (enabled) {
            markStageCompleted(ctx, draftId, "points-buy");
        }
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            AttributeGenerationMethod method = game.getAttributeGenerationMethod();
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("pointBuyEnabled", isPointBuyEnabled(method));
            response.put("basePoints", method.getBasePoints());
            response.put("minValue", method.getMinAttributeValue());
            response.put("maxValue", method.getMaxAttributeValue());
            response.put("maxPostRacial", method.getMaxAttributeValuePostRacial());
            response.put("minPointsToSpend", method.getMinimumPointsToSpend());
            response.put("allowNegative", method.isAllowNegativeAttributes());
            return response;
        });
        ctx.json(200, payload);
    }

    private static void updatePointsBuy(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        if (!ensurePointBuyEnabled(ctx, draftId)) {
            return;
        }
        Map<String, Object> body = ctx.readJsonMap();
        int basePoints = getInt(body, "basePoints", 0);
        int minValue = getInt(body, "minValue", 0);
        int maxValue = getInt(body, "maxValue", 0);
        int maxPostRacial = getInt(body, "maxPostRacial", 0);
        int minPointsToSpend = getInt(body, "minPointsToSpend", 0);
        boolean allowNegative = getBoolean(body, "allowNegative", false);

        ctx.getDraftStore().updateDraft(draftId, game -> {
            AttributeGenerationMethod method = game.getAttributeGenerationMethod();
            method.setBasePoints(basePoints);
            method.setMinAttributeValue(minValue);
            method.setMaxAttributeValue(maxValue);
            method.setMaxAttributeValuePostRacial(maxPostRacial);
            method.setMinimumPointsToSpend(minPointsToSpend);
            method.setAllowNegativeAttributes(allowNegative);
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void getHitPoints(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        markStageCompleted(ctx, draftId, "hit-points");
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            HPMethod method = game.getHpMethod();
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("hpGainMethod", Objects.toString(method.getHpGainMethod(), ""));
            response.put("fixedHPPerLevel", method.getFixedHPPerLevel());
            response.put("averageRoundingMethod", Objects.toString(method.getAverageRoundingMethod(), ""));
            response.put("appliesConstitutionModifier", method.isAppliesConstitutionModifier());
            response.put("allowNegativeConModifier", method.isAllowNegativeConModifier());
            response.put("minimumHPPerLevel", method.getMinimumHPPerLevel());
            response.put("firstLevelMaxHP", method.isFirstLevelMaxHP());
            response.put("firstLevelBonusHP", method.getFirstLevelBonusHP());
            return response;
        });
        ctx.json(200, payload);
    }

    private static void updateHitPoints(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String hpGainMethod = getString(body, "hpGainMethod");
        int fixedHPPerLevel = getInt(body, "fixedHPPerLevel", 0);
        String averageRoundingMethod = getString(body, "averageRoundingMethod");
        boolean appliesConstitutionModifier = getBoolean(body, "appliesConstitutionModifier", false);
        boolean allowNegativeConModifier = getBoolean(body, "allowNegativeConModifier", false);
        int minimumHPPerLevel = getInt(body, "minimumHPPerLevel", 0);
        boolean firstLevelMaxHP = getBoolean(body, "firstLevelMaxHP", false);
        int firstLevelBonusHP = getInt(body, "firstLevelBonusHP", 0);

        ctx.getDraftStore().updateDraft(draftId, game -> {
            HPMethod method = game.getHpMethod();
            method.setHpGainMethod(hpGainMethod);
            method.setFixedHPPerLevel(fixedHPPerLevel);
            method.setAverageRoundingMethod(averageRoundingMethod);
            method.setAppliesConstitutionModifier(appliesConstitutionModifier);
            method.setAllowNegativeConModifier(allowNegativeConModifier);
            method.setMinimumHPPerLevel(minimumHPPerLevel);
            method.setFirstLevelMaxHP(firstLevelMaxHP);
            method.setFirstLevelBonusHP(firstLevelBonusHP);
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void getArmorClass(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        markStageCompleted(ctx, draftId, "armor-class");
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            Map<String, Object> response = new LinkedHashMap<>();
            ArmorClassMethod method = game.getArmorClassMethod();
            response.put("baseArmorClass", Math.max(0, method.getBaseArmorClass()));
            response.put("acAbilityAttributeId", Objects.toString(method.getAcAbilityAttributeId(), ""));
            response.put("attributes", getAttributesForSelect(game));
            response.put("gearBased", method.isGearBased());
            response.put("basePlusModifier", method.isBasePlusModifier());
            response.put("abilityBased", method.isAbilityBased());
            return response;
        });
        ctx.json(200, payload);
    }

    private static void updateArmorClass(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        int baseArmorClass = getInt(body, "baseArmorClass", 10);
        String acAbilityAttributeId = getString(body, "acAbilityAttributeId").trim();
        boolean gearBased = getBoolean(body, "gearBased", false);
        boolean basePlusModifier = getBoolean(body, "basePlusModifier", false);
        boolean abilityBased = !acAbilityAttributeId.isEmpty();
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ArmorClassMethod method = game.getArmorClassMethod();
            method.setBaseArmorClass(Math.max(0, baseArmorClass));
            method.setAcAbilityAttributeId(acAbilityAttributeId);
            method.setGearBased(gearBased);
            method.setBasePlusModifier(basePlusModifier);
            method.setAbilityBased(abilityBased);
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void getArmor(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            Map<String, Object> response = new LinkedHashMap<>();
            List<Map<String, Object>> armor = new ArrayList<>();
            for (Armor item : getArmor(game)) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("id", Objects.toString(item.getId(), ""));
                entry.put("name", Objects.toString(item.getName(), ""));
                entry.put("description", Objects.toString(item.getDescription(), ""));
                entry.put("armorType", Objects.toString(item.getArmorType(), ""));
                entry.put("armorClass", Math.max(0, item.getArmorClass()));
                entry.put("armorBonus", Math.max(0, item.getArmorBonus()));
                entry.put("shieldBonus", Math.max(0, item.getShieldBonus()));
                entry.put("armorCheckPenalty", item.getArmorCheckPenalty());
                armor.add(entry);
            }
            response.put("armor", armor);
            return response;
        });
        ctx.json(200, payload);
    }

    private static List<Map<String, Object>> getAttributesForSelect(Game game) {
        List<Map<String, Object>> attributes = new ArrayList<>();
        for (Attribute attribute : game.getElementRegistry(ElementRegistryKey.ATTRIBUTES).getAll()) {
            if (attribute == null) {
                continue;
            }
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("id", Objects.toString(attribute.getId(), ""));
            entry.put("name", Objects.toString(attribute.getName(), ""));
            entry.put("displayName", Objects.toString(attribute.getDisplayName(), ""));
            attributes.add(entry);
        }
        attributes.sort(Comparator.comparing(
            entry -> Objects.toString(entry.get("displayName"), ""),
            String.CASE_INSENSITIVE_ORDER
        ));
        return attributes;
    }

    private static void getCurrencies(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        markStageCompleted(ctx, draftId, "currency");
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            Map<String, Object> response = new LinkedHashMap<>();
            List<Map<String, Object>> currencies = new ArrayList<>();
            for (Currency currency : getCurrencies(game)) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("id", Objects.toString(currency.getId(), ""));
                entry.put("name", Objects.toString(currency.getName(), ""));
                List<Map<String, Object>> denominations = new ArrayList<>();
                for (Map.Entry<String, Float> denom : currency.getDenominations().entrySet()) {
                    Map<String, Object> denomEntry = new LinkedHashMap<>();
                    denomEntry.put("name", Objects.toString(denom.getKey(), ""));
                    denomEntry.put("value", denom.getValue());
                    denominations.add(denomEntry);
                }
                entry.put("denominations", denominations);
                currencies.add(entry);
            }
            response.put("systemName", game.getSystemName("currencies"));
            Map<String, Object> startingMoney = new LinkedHashMap<>();
            startingMoney.put("method", game.getStartingMoneyMethod());
            startingMoney.put("baseAmount", game.getBaseStartingMoney());
            startingMoney.put("currencyId", game.getStartingMoneyCurrencyId());
            response.put("startingMoney", startingMoney);
            response.put("currencies", currencies);
            return response;
        });
        ctx.json(200, payload);
    }

    private static void addCurrency(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String name = getString(body, "name").trim();
        String baseDenomination = getString(body, "baseDenomination").trim();
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        Currency currency = new Currency(name);
        if (!baseDenomination.isEmpty()) {
            currency.addDenomination(baseDenomination, 1.0f);
        }
        ctx.getDraftStore().updateDraft(draftId, game -> game.addElement("currencies", currency));
        ctx.json(200, Map.of("ok", true, "id", Objects.toString(currency.getId(), "")));
    }

    private static void removeCurrency(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String currencyId = getString(body, "id");
        ctx.getDraftStore().updateDraft(draftId, game -> {
            Currency currency = game.getElement("currencies", currencyId);
            if (currency != null) {
                game.removeElement("currencies", currency);
            }
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void addCurrencyDenomination(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String currencyId = getString(body, "currencyId");
        String name = getString(body, "name").trim();
        double value = getDouble(body, "value", 0.0);
        if (currencyId.isEmpty() || name.isEmpty()) {
            ctx.json(400, Map.of("error", "Currency and name are required"));
            return;
        }
        if (value <= 0.0) {
            ctx.json(400, Map.of("error", "Value must be positive"));
            return;
        }
        ctx.getDraftStore().updateDraft(draftId, game -> {
            Currency currency = game.getElement("currencies", currencyId);
            if (currency != null) {
                currency.addDenomination(name, (float) value);
            }
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void removeCurrencyDenomination(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String currencyId = getString(body, "currencyId");
        String name = getString(body, "name");
        ctx.getDraftStore().updateDraft(draftId, game -> {
            Currency currency = game.getElement("currencies", currencyId);
            if (currency != null) {
                currency.getDenominations().remove(name);
            }
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void updateStartingMoney(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String method = getString(body, "method").trim();
        int baseAmount = Math.max(0, getInt(body, "baseAmount", 0));
        String currencyId = getString(body, "currencyId").trim();
        ctx.getDraftStore().updateDraft(draftId, game -> {
            game.setStartingMoneyMethod(method);
            game.setBaseStartingMoney(baseAmount);
            if (currencyId.isEmpty() || game.getElement("currencies", currencyId) == null) {
                game.setStartingMoneyCurrencyId("");
            } else {
                game.setStartingMoneyCurrencyId(currencyId);
            }
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void getEffects(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        markStageCompleted(ctx, draftId, "effects");
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            Map<String, Object> response = new LinkedHashMap<>();
            List<Map<String, Object>> effects = new ArrayList<>();
            for (Effect effect : getEffects(game)) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("id", Objects.toString(effect.getId(), ""));
                entry.put("name", Objects.toString(effect.getName(), ""));
                entry.put("description", Objects.toString(effect.getDescription(), ""));
                List<String> typeKeys = effect.getEffectTypeKeys();
                entry.put("effectTypeKeys", typeKeys == null ? List.of() : new ArrayList<>(typeKeys));
                effects.add(entry);
            }
            response.put("systemName", game.getSystemName("effects"));
            response.put("effects", effects);
            return response;
        });
        ctx.json(200, payload);
    }

    private static void addEffect(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String name = getString(body, "name").trim();
        String description = getString(body, "description").trim();
        List<String> effectTypeKeys = getStringList(body, "effectTypeKeys");
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        Effect effect = new Effect(name, description);
        effect.setEffectTypeKeys(effectTypeKeys);
        boolean[] added = new boolean[] { false };
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Effect> registry = game.getElementRegistry(ElementRegistryKey.EFFECTS);
            if (registry.hasName(name)) {
                return;
            }
            if (registry.add(effect)) {
                added[0] = true;
            }
        });
        if (!added[0]) {
            ctx.json(400, Map.of("error", "Effect already exists"));
            return;
        }
        ctx.json(200, Map.of("ok", true, "id", Objects.toString(effect.getId(), "")));
    }

    private static void removeEffect(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String effectId = getString(body, "id");
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Effect> registry = game.getElementRegistry(ElementRegistryKey.EFFECTS);
            Effect effect = registry.getById(effectId);
            if (effect == null) {
                return;
            }
            registry.remove(effect);
            updateEffectReferences(game, effect.getName(), "");
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void updateEffect(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String effectId = getString(body, "id");
        String name = getString(body, "name").trim();
        String description = getString(body, "description").trim();
        List<String> effectTypeKeys = getStringList(body, "effectTypeKeys");
        if (effectId.isEmpty()) {
            ctx.json(400, Map.of("error", "Effect id is required"));
            return;
        }
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        boolean[] duplicate = new boolean[] { false };
        boolean[] updated = new boolean[] { false };
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Effect> registry = game.getElementRegistry(ElementRegistryKey.EFFECTS);
            Effect effect = registry.getById(effectId);
            if (effect == null) {
                return;
            }
            String previousName = effect.getName();
            if (!previousName.equalsIgnoreCase(name) && registry.hasName(name)) {
                duplicate[0] = true;
                return;
            }
            if (!previousName.equalsIgnoreCase(name)) {
                registry.remove(effect);
                effect.setName(name);
                registry.add(effect);
                updateEffectReferences(game, previousName, name);
            }
            effect.setDescription(description);
            effect.setEffectTypeKeys(effectTypeKeys);
            updated[0] = true;
        });
        if (duplicate[0]) {
            ctx.json(400, Map.of("error", "Effect already exists"));
            return;
        }
        if (!updated[0]) {
            ctx.json(404, Map.of("error", "Effect not found"));
            return;
        }
        ctx.json(200, Map.of("ok", true));
    }

    private static void getStatuses(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        markStageCompleted(ctx, draftId, "statuses");
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            Map<String, Object> response = new LinkedHashMap<>();
            List<Map<String, Object>> statuses = new ArrayList<>();
            for (Status status : getStatuses(game)) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("id", Objects.toString(status.getId(), ""));
                entry.put("name", Objects.toString(status.getName(), ""));
                entry.put("description", Objects.toString(status.getDescription(), ""));
                List<String> typeKeys = status.getEffectTypeKeys();
                entry.put("effectTypeKeys", typeKeys == null ? List.of() : new ArrayList<>(typeKeys));
                statuses.add(entry);
            }
            response.put("systemName", game.getSystemName("statuses"));
            response.put("statuses", statuses);
            return response;
        });
        ctx.json(200, payload);
    }

    private static void addStatus(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String name = getString(body, "name").trim();
        String description = getString(body, "description").trim();
        List<String> effectTypeKeys = getStringList(body, "effectTypeKeys");
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        Status status = new Status(name, description);
        status.setEffectTypeKeys(effectTypeKeys);
        boolean[] added = new boolean[] { false };
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Status> registry = game.getElementRegistry(ElementRegistryKey.STATUSES);
            if (registry.hasName(name)) {
                return;
            }
            if (registry.add(status)) {
                added[0] = true;
            }
        });
        if (!added[0]) {
            ctx.json(400, Map.of("error", "Status already exists"));
            return;
        }
        ctx.json(200, Map.of("ok", true, "id", Objects.toString(status.getId(), "")));
    }

    private static void removeStatus(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String statusId = getString(body, "id");
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Status> registry = game.getElementRegistry(ElementRegistryKey.STATUSES);
            Status status = registry.getById(statusId);
            if (status != null) {
                registry.remove(status);
            }
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void updateStatus(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String statusId = getString(body, "id");
        String name = getString(body, "name").trim();
        String description = getString(body, "description").trim();
        List<String> effectTypeKeys = getStringList(body, "effectTypeKeys");
        if (statusId.isEmpty()) {
            ctx.json(400, Map.of("error", "Status id is required"));
            return;
        }
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        boolean[] duplicate = new boolean[] { false };
        boolean[] updated = new boolean[] { false };
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Status> registry = game.getElementRegistry(ElementRegistryKey.STATUSES);
            Status status = registry.getById(statusId);
            if (status == null) {
                return;
            }
            String previousName = status.getName();
            if (!previousName.equalsIgnoreCase(name) && registry.hasName(name)) {
                duplicate[0] = true;
                return;
            }
            if (!previousName.equalsIgnoreCase(name)) {
                registry.remove(status);
                status.setName(name);
                registry.add(status);
            }
            status.setDescription(description);
            status.setEffectTypeKeys(effectTypeKeys);
            updated[0] = true;
        });
        if (duplicate[0]) {
            ctx.json(400, Map.of("error", "Status already exists"));
            return;
        }
        if (!updated[0]) {
            ctx.json(404, Map.of("error", "Status not found"));
            return;
        }
        ctx.json(200, Map.of("ok", true));
    }

    private static void getEquipment(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        markStageCompleted(ctx, draftId, "equipment");
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            Map<String, Object> response = new LinkedHashMap<>();
            List<Map<String, Object>> equipment = new ArrayList<>();
            for (Equipment item : getEquipment(game)) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("id", Objects.toString(item.getId(), ""));
                entry.put("name", Objects.toString(item.getName(), ""));
                entry.put("description", Objects.toString(item.getDescription(), ""));
                entry.put("weightValue", Math.max(0, (int) Math.round(item.getWeight())));
                entry.put("weightUnit", Objects.toString(item.getWeightUnit(), ""));
                equipment.add(entry);
            }
            response.put("systemName", game.getSystemName("equipment"));
            response.put("weightSystem", Objects.toString(game.getWeightSystem(), ""));
            response.put("weightUnits", getWeightUnits(game));
            response.put("equipment", equipment);
            return response;
        });
        ctx.json(200, payload);
    }

    private static void addEquipment(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String name = getString(body, "name").trim();
        String description = getString(body, "description").trim();
        int weightValue = getInt(body, "weightValue", 0);
        String weightUnit = getString(body, "weightUnit").trim();
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        Equipment item = new Equipment(name, description);
        item.setWeight(Math.max(0, weightValue));
        item.setWeightUnit(weightUnit);
        boolean[] added = new boolean[] { false };
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Equipment> registry = game.getElementRegistry(ElementRegistryKey.EQUIPMENT);
            if (registry.hasName(name)) {
                return;
            }
            if (registry.add(item)) {
                added[0] = true;
            }
        });
        if (!added[0]) {
            ctx.json(400, Map.of("error", "Equipment already exists"));
            return;
        }
        ctx.json(200, Map.of("ok", true, "id", Objects.toString(item.getId(), "")));
    }

    private static void removeEquipment(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String itemId = getString(body, "id");
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Equipment> registry = game.getElementRegistry(ElementRegistryKey.EQUIPMENT);
            Equipment item = registry.getById(itemId);
            if (item != null) {
                registry.remove(item);
            }
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void updateEquipment(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String itemId = getString(body, "id");
        String name = getString(body, "name").trim();
        String description = getString(body, "description").trim();
        int weightValue = getInt(body, "weightValue", 0);
        String weightUnit = getString(body, "weightUnit").trim();
        if (itemId.isEmpty()) {
            ctx.json(400, Map.of("error", "Equipment id is required"));
            return;
        }
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        boolean[] duplicate = new boolean[] { false };
        boolean[] updated = new boolean[] { false };
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Equipment> registry = game.getElementRegistry(ElementRegistryKey.EQUIPMENT);
            Equipment item = registry.getById(itemId);
            if (item == null) {
                return;
            }
            String previousName = item.getName();
            if (!previousName.equalsIgnoreCase(name) && registry.hasName(name)) {
                duplicate[0] = true;
                return;
            }
            if (!previousName.equalsIgnoreCase(name)) {
                registry.remove(item);
                item.setName(name);
                registry.add(item);
            }
            item.setDescription(description);
            item.setWeight(Math.max(0, weightValue));
            item.setWeightUnit(weightUnit);
            updated[0] = true;
        });
        if (duplicate[0]) {
            ctx.json(400, Map.of("error", "Equipment already exists"));
            return;
        }
        if (!updated[0]) {
            ctx.json(404, Map.of("error", "Equipment not found"));
            return;
        }
        ctx.json(200, Map.of("ok", true));
    }

    private static void getWeapons(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        markStageCompleted(ctx, draftId, "weapons");
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            List<Map<String, Object>> weapons = new ArrayList<>();
            for (Weapon weapon : getWeapons(game)) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("id", Objects.toString(weapon.getId(), ""));
                entry.put("name", Objects.toString(weapon.getName(), ""));
                entry.put("description", Objects.toString(weapon.getDescription(), ""));
                entry.put("damageRoll", Objects.toString(weapon.getDamageRoll(), ""));
                entry.put("damageDiceCount", Math.max(0, weapon.getDamageDiceCount()));
                entry.put("damageDiceSides", Math.max(0, weapon.getDamageDiceSides()));
                entry.put("damageDiceModifier", weapon.getDamageDiceModifier());
                entry.put("weightValue", Math.max(0, (int) Math.round(weapon.getWeight())));
                entry.put("weightUnit", Objects.toString(weapon.getWeightUnit(), ""));
                List<Effect> effects = weapon.getObjectArray("effects");
                List<String> effectIds = new ArrayList<>();
                if (effects != null) {
                    for (Effect effect : effects) {
                        if (effect != null) {
                            String id = Objects.toString(effect.getId(), "").trim();
                            if (!id.isEmpty()) {
                                effectIds.add(id);
                            }
                        }
                    }
                }
                entry.put("effectIds", effectIds);
                weapons.add(entry);
            }
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("weightSystem", Objects.toString(game.getWeightSystem(), ""));
            response.put("weightUnits", getWeightUnits(game));
            response.put("weapons", weapons);
            return response;
        });
        ctx.json(200, payload);
    }

    private static void addWeapon(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String name = getString(body, "name").trim();
        String description = getString(body, "description").trim();
        int damageDiceCount = getInt(body, "damageDiceCount", 0);
        int damageDiceSides = getInt(body, "damageDiceSides", 0);
        int damageDiceModifier = getInt(body, "damageDiceModifier", 0);
        String damageRoll = getString(body, "damageRoll").trim();
        int weightValue = getInt(body, "weightValue", 0);
        String weightUnit = getString(body, "weightUnit").trim();
        List<String> effectIds = getStringList(body, "effectIds");
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        Weapon weapon = new Weapon(name, description);
        if (damageDiceCount > 0 && damageDiceSides > 0) {
            weapon.setDamageDiceCount(damageDiceCount);
            weapon.setDamageDiceSides(damageDiceSides);
            weapon.setDamageDiceModifier(damageDiceModifier);
        } else {
            weapon.setDamageRoll(damageRoll);
        }
        weapon.setWeight(Math.max(0, weightValue));
        weapon.setWeightUnit(weightUnit);
        boolean[] added = new boolean[] { false };
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Weapon> registry = game.getElementRegistry(ElementRegistryKey.WEAPONS);
            if (registry.hasName(name)) {
                return;
            }
            applyWeaponEffects(game, weapon, effectIds);
            if (registry.add(weapon)) {
                added[0] = true;
            }
        });
        if (!added[0]) {
            ctx.json(400, Map.of("error", "Weapon already exists"));
            return;
        }
        ctx.json(200, Map.of("ok", true, "id", Objects.toString(weapon.getId(), "")));
    }

    private static void removeWeapon(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String weaponId = getString(body, "id");
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Weapon> registry = game.getElementRegistry(ElementRegistryKey.WEAPONS);
            Weapon weapon = registry.getById(weaponId);
            if (weapon != null) {
                registry.remove(weapon);
            }
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void updateWeapon(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String weaponId = getString(body, "id");
        String name = getString(body, "name").trim();
        String description = getString(body, "description").trim();
        int damageDiceCount = getInt(body, "damageDiceCount", 0);
        int damageDiceSides = getInt(body, "damageDiceSides", 0);
        int damageDiceModifier = getInt(body, "damageDiceModifier", 0);
        String damageRoll = getString(body, "damageRoll").trim();
        int weightValue = getInt(body, "weightValue", 0);
        String weightUnit = getString(body, "weightUnit").trim();
        List<String> effectIds = getStringList(body, "effectIds");
        if (weaponId.isEmpty()) {
            ctx.json(400, Map.of("error", "Weapon id is required"));
            return;
        }
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        boolean[] duplicate = new boolean[] { false };
        boolean[] updated = new boolean[] { false };
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Weapon> registry = game.getElementRegistry(ElementRegistryKey.WEAPONS);
            Weapon weapon = registry.getById(weaponId);
            if (weapon == null) {
                return;
            }
            String previousName = weapon.getName();
            if (!previousName.equalsIgnoreCase(name) && registry.hasName(name)) {
                duplicate[0] = true;
                return;
            }
            if (!previousName.equalsIgnoreCase(name)) {
                registry.remove(weapon);
                weapon.setName(name);
                registry.add(weapon);
            }
            weapon.setDescription(description);
            if (damageDiceCount > 0 && damageDiceSides > 0) {
                weapon.setDamageDiceCount(damageDiceCount);
                weapon.setDamageDiceSides(damageDiceSides);
                weapon.setDamageDiceModifier(damageDiceModifier);
            } else {
                weapon.setDamageRoll(damageRoll);
            }
            weapon.setWeight(Math.max(0, weightValue));
            weapon.setWeightUnit(weightUnit);
            applyWeaponEffects(game, weapon, effectIds);
            updated[0] = true;
        });
        if (duplicate[0]) {
            ctx.json(400, Map.of("error", "Weapon already exists"));
            return;
        }
        if (!updated[0]) {
            ctx.json(404, Map.of("error", "Weapon not found"));
            return;
        }
        ctx.json(200, Map.of("ok", true));
    }

    private static void applyWeaponEffects(Game game, Weapon weapon, List<String> effectIds) {
        Weapon safeWeapon = Objects.requireNonNullElse(weapon, new Weapon(""));
        safeWeapon.clearArray("effects");
        if (effectIds == null || effectIds.isEmpty()) {
            return;
        }
        ElementRegistry<Effect> registry = game.getElementRegistry(ElementRegistryKey.EFFECTS);
        for (String effectId : effectIds) {
            String id = Objects.toString(effectId, "").trim();
            if (id.isEmpty()) {
                continue;
            }
            Effect effect = registry.getById(id);
            if (effect != null) {
                safeWeapon.addToArray("effects", effect);
            }
        }
    }

    private static void applyClassDetails(
        Game game,
        CharacterClass characterClass,
        String primaryAttribute,
        String hitDie,
        int startingMoney,
        int skillPointsPerLevel,
        boolean skillPointsSameAllLevels,
        List<Map<String, Object>> skillPointsByLevel,
        List<String> classSkillIds,
        List<Map<String, Object>> requiredScores
    ) {
        CharacterClass safeClass = Objects.requireNonNullElse(characterClass, new CharacterClass(""));
        safeClass.setPrimaryAttribute(primaryAttribute);
        safeClass.setHitDie(hitDie);
        game.setClassStartingMoney(Objects.toString(safeClass.getId(), ""), Math.max(0, startingMoney));
        safeClass.setSkillPointsPerLevel(skillPointsPerLevel);
        safeClass.setSkillPointsSameAllLevels(skillPointsSameAllLevels);
        Map<Integer, Integer> pointsByLevel = new LinkedHashMap<>();
        for (Map<String, Object> entry : skillPointsByLevel) {
            int level = getInt(entry, "level", 0);
            int points = getInt(entry, "points", 0);
            if (level > 0) {
                pointsByLevel.put(level, Math.max(0, points));
            }
        }
        safeClass.setSkillPointsByLevel(pointsByLevel);

        safeClass.clearArray("classSkills");
        for (String skillId : safeList(classSkillIds)) {
            String id = Objects.toString(skillId, "").trim();
            if (id.isEmpty()) {
                continue;
            }
            if (game.getElement("skills", id) != null) {
                safeClass.addToArray("classSkills", id);
            }
        }

        Map<String, Integer> requiredAttributeScores = new LinkedHashMap<>();
        for (Map<String, Object> entry : requiredScores) {
            String attributeId = Objects.toString(entry.get("attributeId"), "").trim();
            int score = getInt(entry, "score", 0);
            if (attributeId.isEmpty() || score <= 0) {
                continue;
            }
            if (game.getElement("attributes", attributeId) != null) {
                requiredAttributeScores.put(attributeId, score);
            }
        }
        safeClass.setRequiredAttributeScores(requiredAttributeScores);
    }

    private static void getClasses(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        markStageCompleted(ctx, draftId, "classes");
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            Map<String, String> skillNamesById = new LinkedHashMap<>();
            for (Skill skill : getSkills(game)) {
                String id = Objects.toString(skill.getId(), "").trim();
                if (id.isEmpty()) {
                    continue;
                }
                skillNamesById.put(id, Objects.toString(skill.getName(), ""));
            }
            Map<String, Object> response = new LinkedHashMap<>();
            List<Map<String, Object>> classes = new ArrayList<>();
            for (CharacterClass characterClass : getClasses(game)) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("id", Objects.toString(characterClass.getId(), ""));
                entry.put("name", Objects.toString(characterClass.getName(), ""));
                entry.put("description", Objects.toString(characterClass.getDescription(), ""));
                entry.put("primaryAttribute", Objects.toString(characterClass.getPrimaryAttribute(), ""));
                entry.put("hitDie", Objects.toString(characterClass.getHitDie(), ""));
                entry.put("startingMoney", game.getClassStartingMoney(Objects.toString(characterClass.getId(), "")));
                entry.put("skillPointsPerLevel", characterClass.getSkillPointsPerLevel());
                entry.put("skillPointsSameAllLevels", characterClass.isSkillPointsSameAllLevels());
                entry.put("maxLevel", characterClass.getMaxLevel());
                List<Map<String, Object>> skillPointsByLevel = new ArrayList<>();
                for (Map.Entry<Integer, Integer> pointsEntry : characterClass.getSkillPointsByLevel().entrySet()) {
                    Map<String, Object> levelEntry = new LinkedHashMap<>();
                    levelEntry.put("level", Objects.requireNonNullElse(pointsEntry.getKey(), 0));
                    levelEntry.put("points", Objects.requireNonNullElse(pointsEntry.getValue(), 0));
                    skillPointsByLevel.add(levelEntry);
                }
                skillPointsByLevel.sort(Comparator.comparingInt(entryMap -> getInt(entryMap, "level", 0)));
                entry.put("skillPointsByLevel", skillPointsByLevel);
                List<String> classSkills = characterClass.getObjectArray("classSkills");
                List<String> classSkillIds = classSkills == null ? List.of() : new ArrayList<>(classSkills);
                entry.put("classSkillIds", classSkillIds);
                List<String> classSkillNames = new ArrayList<>();
                for (String skillId : classSkillIds) {
                    String safeId = Objects.toString(skillId, "").trim();
                    if (safeId.isEmpty()) {
                        continue;
                    }
                    classSkillNames.add(Objects.toString(skillNamesById.getOrDefault(safeId, safeId), ""));
                }
                entry.put("classSkillNames", classSkillNames);
                List<Map<String, Object>> requiredScores = new ArrayList<>();
                for (Map.Entry<String, Integer> reqEntry : characterClass.getRequiredAttributeScores().entrySet()) {
                    Map<String, Object> req = new LinkedHashMap<>();
                    req.put("attributeId", Objects.toString(reqEntry.getKey(), ""));
                    req.put("score", Objects.requireNonNullElse(reqEntry.getValue(), 0));
                    requiredScores.add(req);
                }
                entry.put("requiredAttributeScores", requiredScores);
                classes.add(entry);
            }
            response.put("systemName", game.getSystemName("classes"));
            response.put("diceUsed", new ArrayList<>(game.getDiceUsed()));
            response.put("classes", classes);
            return response;
        });
        ctx.json(200, payload);
    }

    private static void addClass(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String name = getString(body, "name").trim();
        String description = getString(body, "description").trim();
        String primaryAttribute = getString(body, "primaryAttribute").trim();
        String hitDie = getString(body, "hitDie").trim();
        int startingMoney = Math.max(0, getInt(body, "startingMoney", 0));
        int skillPointsPerLevel = getInt(body, "skillPointsPerLevel", 0);
        boolean skillPointsSameAllLevels = getBoolean(body, "skillPointsSameAllLevels", true);
        List<Map<String, Object>> skillPointsByLevel = getMapList(body, "skillPointsByLevel");
        List<String> classSkillIds = getStringList(body, "classSkillIds");
        List<Map<String, Object>> requiredScores = getMapList(body, "requiredAttributeScores");
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        CharacterClass characterClass = new CharacterClass(name, description);
        boolean[] added = new boolean[] { false };
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<CharacterClass> registry = game.getElementRegistry(ElementRegistryKey.CHARACTER_CLASSES);
            if (registry.hasName(name)) {
                return;
            }
            applyClassDetails(
                game,
                characterClass,
                primaryAttribute,
                hitDie,
                startingMoney,
                skillPointsPerLevel,
                skillPointsSameAllLevels,
                skillPointsByLevel,
                classSkillIds,
                requiredScores
            );
            if (registry.add(characterClass)) {
                added[0] = true;
            }
        });
        if (!added[0]) {
            ctx.json(400, Map.of("error", "Class already exists"));
            return;
        }
        ctx.json(200, Map.of("ok", true, "id", Objects.toString(characterClass.getId(), "")));
    }

    private static void removeClass(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String classId = getString(body, "id");
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<CharacterClass> registry = game.getElementRegistry(ElementRegistryKey.CHARACTER_CLASSES);
            CharacterClass characterClass = registry.getById(classId);
            if (characterClass != null) {
                game.setClassStartingMoney(Objects.toString(characterClass.getId(), ""), 0);
                registry.remove(characterClass);
            }
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void updateClass(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String classId = getString(body, "id");
        String name = getString(body, "name").trim();
        String description = getString(body, "description").trim();
        String primaryAttribute = getString(body, "primaryAttribute").trim();
        String hitDie = getString(body, "hitDie").trim();
        int startingMoney = Math.max(0, getInt(body, "startingMoney", 0));
        int skillPointsPerLevel = getInt(body, "skillPointsPerLevel", 0);
        boolean skillPointsSameAllLevels = getBoolean(body, "skillPointsSameAllLevels", true);
        List<Map<String, Object>> skillPointsByLevel = getMapList(body, "skillPointsByLevel");
        List<String> classSkillIds = getStringList(body, "classSkillIds");
        List<Map<String, Object>> requiredScores = getMapList(body, "requiredAttributeScores");
        if (classId.isEmpty()) {
            ctx.json(400, Map.of("error", "Class id is required"));
            return;
        }
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        boolean[] duplicate = new boolean[] { false };
        boolean[] updated = new boolean[] { false };
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<CharacterClass> registry = game.getElementRegistry(ElementRegistryKey.CHARACTER_CLASSES);
            CharacterClass characterClass = registry.getById(classId);
            if (characterClass == null) {
                return;
            }
            String previousName = characterClass.getName();
            if (!previousName.equalsIgnoreCase(name) && registry.hasName(name)) {
                duplicate[0] = true;
                return;
            }
            if (!previousName.equalsIgnoreCase(name)) {
                registry.remove(characterClass);
                characterClass.setName(name);
                registry.add(characterClass);
            }
            characterClass.setDescription(description);
            applyClassDetails(
                game,
                characterClass,
                primaryAttribute,
                hitDie,
                startingMoney,
                skillPointsPerLevel,
                skillPointsSameAllLevels,
                skillPointsByLevel,
                classSkillIds,
                requiredScores
            );
            updated[0] = true;
        });
        if (duplicate[0]) {
            ctx.json(400, Map.of("error", "Class already exists"));
            return;
        }
        if (!updated[0]) {
            ctx.json(404, Map.of("error", "Class not found"));
            return;
        }
        ctx.json(200, Map.of("ok", true));
    }

    private static void getSkills(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        markStageCompleted(ctx, draftId, "skills");
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            Map<String, Object> response = new LinkedHashMap<>();
            List<Map<String, Object>> skills = new ArrayList<>();
            for (Skill skill : getSkills(game)) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("id", Objects.toString(skill.getId(), ""));
                entry.put("name", Objects.toString(skill.getName(), ""));
                entry.put("description", Objects.toString(skill.getDescription(), ""));
                entry.put("category", Objects.toString(skill.getCategory(), ""));
                entry.put("relatedAbility", Objects.toString(skill.getRelatedAbility(), ""));
                entry.put("trainedOnly", skill.isTrainedOnly());
                entry.put("armorCheckPenalty", skill.getArmorCheckPenalty());
                entry.put("startingMoneyModifier", game.getTraitStartingMoneyModifier(Objects.toString(skill.getId(), "")));
                List<String> effectNames = skill.getArray("effectNames");
                entry.put("effectNames", effectNames == null ? List.of() : new ArrayList<>(effectNames));
                List<String> limitedToClasses = skill.getArray("limitedToClasses");
                entry.put("limitedToClasses", limitedToClasses == null ? List.of() : new ArrayList<>(limitedToClasses));
                List<String> limitedToRaces = skill.getArray("limitedToRaces");
                entry.put("limitedToRaces", limitedToRaces == null ? List.of() : new ArrayList<>(limitedToRaces));
                skills.add(entry);
            }
            LevelingMethod levelingMethod = game.getLevelingMethod();
            Map<String, Object> progression = new LinkedHashMap<>();
            progression.put("skillPointProgression", Objects.toString(levelingMethod.getSkillPointProgression(), ""));
            progression.put("baseSkillPointsPerLevel", levelingMethod.getBaseSkillPointsPerLevel());
            progression.put("skillPointsModifiedByInt", levelingMethod.isSkillPointsModifiedByInt());
            progression.put("minimumSkillPointsPerLevel", levelingMethod.getMinimumSkillPointsPerLevel());
            progression.put("skillPointsSameAllLevels", levelingMethod.isSkillPointsSameAllLevels());
            List<Map<String, Object>> skillPointsByLevel = new ArrayList<>();
            for (Map.Entry<Integer, Integer> pointsEntry : levelingMethod.getSkillPointsByLevel().entrySet()) {
                Map<String, Object> levelEntry = new LinkedHashMap<>();
                levelEntry.put("level", Objects.requireNonNullElse(pointsEntry.getKey(), 0));
                levelEntry.put("points", Objects.requireNonNullElse(pointsEntry.getValue(), 0));
                skillPointsByLevel.add(levelEntry);
            }
            skillPointsByLevel.sort(Comparator.comparingInt(entryMap -> getInt(entryMap, "level", 0)));
            progression.put("skillPointsByLevel", skillPointsByLevel);
            response.put("systemName", game.getSystemName("skills"));
            response.put("progression", progression);
            response.put("skills", skills);
            return response;
        });
        ctx.json(200, payload);
    }

    private static void updateSkillProgression(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String progressionType = getString(body, "skillPointProgression").trim();
        int baseSkillPointsPerLevel = getInt(body, "baseSkillPointsPerLevel", 0);
        boolean skillPointsModifiedByInt = getBoolean(body, "skillPointsModifiedByInt", true);
        int minimumSkillPointsPerLevel = getInt(body, "minimumSkillPointsPerLevel", 0);
        boolean skillPointsSameAllLevels = getBoolean(body, "skillPointsSameAllLevels", true);
        List<Map<String, Object>> skillPointsByLevel = getMapList(body, "skillPointsByLevel");
        ctx.getDraftStore().updateDraft(draftId, game -> {
            LevelingMethod levelingMethod = game.getLevelingMethod();
            levelingMethod.setSkillPointProgression(progressionType);
            levelingMethod.setBaseSkillPointsPerLevel(Math.max(0, baseSkillPointsPerLevel));
            levelingMethod.setSkillPointsModifiedByInt(skillPointsModifiedByInt);
            levelingMethod.setMinimumSkillPointsPerLevel(Math.max(0, minimumSkillPointsPerLevel));
            levelingMethod.setSkillPointsSameAllLevels(skillPointsSameAllLevels);
            Map<Integer, Integer> byLevel = new LinkedHashMap<>();
            for (Map<String, Object> entry : skillPointsByLevel) {
                int level = getInt(entry, "level", 0);
                int points = getInt(entry, "points", 0);
                if (level > 0) {
                    byLevel.put(level, Math.max(0, points));
                }
            }
            levelingMethod.setSkillPointsByLevel(byLevel);
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void addSkill(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String name = getString(body, "name").trim();
        String description = getString(body, "description").trim();
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        Skill skill = description.isEmpty() ? new Skill(name) : new Skill(name, description);
        boolean[] added = new boolean[] { false };
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Skill> registry = game.getElementRegistry(ElementRegistryKey.SKILLS);
            if (registry.hasName(name)) {
                return;
            }
            if (registry.add(skill)) {
                added[0] = true;
            }
        });
        if (!added[0]) {
            ctx.json(400, Map.of("error", "Skill already exists"));
            return;
        }
        ctx.json(200, Map.of("ok", true, "id", Objects.toString(skill.getId(), "")));
    }

    private static void removeSkill(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String skillId = getString(body, "id");
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Skill> registry = game.getElementRegistry(ElementRegistryKey.SKILLS);
            Skill skill = registry.getById(skillId);
            if (skill != null) {
                game.setTraitStartingMoneyModifier(Objects.toString(skill.getId(), ""), 0);
                registry.remove(skill);
            }
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void updateSkill(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String skillId = getString(body, "id");
        String name = getString(body, "name").trim();
        String description = getString(body, "description").trim();
        String category = getString(body, "category").trim();
        String relatedAbility = getString(body, "relatedAbility").trim();
        boolean trainedOnly = getBoolean(body, "trainedOnly", false);
        int armorCheckPenalty = getInt(body, "armorCheckPenalty", 0);
        int startingMoneyModifier = getInt(body, "startingMoneyModifier", 0);
        List<String> effectNames = getStringList(body, "effectNames");
        List<String> limitedToClasses = getStringList(body, "limitedToClasses");
        List<String> limitedToRaces = getStringList(body, "limitedToRaces");
        if (skillId.isEmpty()) {
            ctx.json(400, Map.of("error", "Skill id is required"));
            return;
        }
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        boolean[] duplicate = new boolean[] { false };
        boolean[] updated = new boolean[] { false };
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Skill> registry = game.getElementRegistry(ElementRegistryKey.SKILLS);
            Skill skill = registry.getById(skillId);
            if (skill == null) {
                return;
            }
            String previousName = skill.getName();
            if (!previousName.equalsIgnoreCase(name) && registry.hasName(name)) {
                duplicate[0] = true;
                return;
            }
            if (!previousName.equalsIgnoreCase(name)) {
                registry.remove(skill);
                skill.setName(name);
                registry.add(skill);
            }
            skill.setDescription(description);
            skill.setCategory(category);
            skill.setRelatedAbility(relatedAbility);
            skill.setTrainedOnly(trainedOnly);
            skill.setArmorCheckPenalty(armorCheckPenalty);
            game.setTraitStartingMoneyModifier(Objects.toString(skill.getId(), ""), startingMoneyModifier);
            skill.clearArray("effectNames");
            for (String effectName : effectNames) {
                skill.addToArray("effectNames", effectName);
            }
            skill.clearArray("limitedToClasses");
            for (String classId : limitedToClasses) {
                String id = Objects.toString(classId, "").trim();
                if (!id.isEmpty() && game.getElementRegistry(ElementRegistryKey.CHARACTER_CLASSES).getById(id) != null) {
                    skill.addToArray("limitedToClasses", id);
                }
            }
            skill.clearArray("limitedToRaces");
            for (String raceId : limitedToRaces) {
                String id = Objects.toString(raceId, "").trim();
                if (!id.isEmpty() && game.getElementRegistry(ElementRegistryKey.RACES).getById(id) != null) {
                    skill.addToArray("limitedToRaces", id);
                }
            }
            updated[0] = true;
        });
        if (duplicate[0]) {
            ctx.json(400, Map.of("error", "Skill already exists"));
            return;
        }
        if (!updated[0]) {
            ctx.json(404, Map.of("error", "Skill not found"));
            return;
        }
        ctx.json(200, Map.of("ok", true));
    }

    private static void getSpells(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        markStageCompleted(ctx, draftId, "spells");
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            Map<String, Object> response = new LinkedHashMap<>();
            List<Map<String, Object>> spells = new ArrayList<>();
            for (Spell spell : getSpells(game)) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("id", Objects.toString(spell.getId(), ""));
                entry.put("name", Objects.toString(spell.getName(), ""));
                entry.put("description", Objects.toString(spell.getDescription(), ""));
                entry.put("school", Objects.toString(spell.getSchool(), ""));
                entry.put("level", spell.getLevel());
                entry.put("castingTime", Objects.toString(spell.getCastingTime(), ""));
                entry.put("range", Objects.toString(spell.getRange(), ""));
                entry.put("duration", Objects.toString(spell.getDuration(), ""));
                entry.put("effectNames", getSpellEffectNames(spell));
                entry.put("effect", Objects.toString(spell.getEffect(), ""));
                entry.put("secondaryEffect", Objects.toString(spell.getSecondaryEffect(), ""));
                spells.add(entry);
            }
            response.put("systemName", game.getSystemName("spells"));
            response.put("spells", spells);
            return response;
        });
        ctx.json(200, payload);
    }

    private static void addSpell(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String name = getString(body, "name").trim();
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        Spell spell = new Spell(name);
        boolean[] added = new boolean[] { false };
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Spell> registry = game.getElementRegistry(ElementRegistryKey.SPELLS);
            if (registry.hasName(name)) {
                return;
            }
            if (registry.add(spell)) {
                added[0] = true;
            }
        });
        if (!added[0]) {
            ctx.json(400, Map.of("error", "Spell already exists"));
            return;
        }
        ctx.json(200, Map.of("ok", true, "id", Objects.toString(spell.getId(), "")));
    }

    private static void removeSpell(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String spellId = getString(body, "id");
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Spell> registry = game.getElementRegistry(ElementRegistryKey.SPELLS);
            Spell spell = registry.getById(spellId);
            if (spell != null) {
                registry.remove(spell);
            }
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void updateSpell(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String spellId = getString(body, "id");
        String name = getString(body, "name").trim();
        String description = getString(body, "description").trim();
        String school = getString(body, "school").trim();
        int level = Math.max(0, getInt(body, "level", 0));
        String castingTime = getString(body, "castingTime").trim();
        String range = getString(body, "range").trim();
        String duration = getString(body, "duration").trim();
        String effect = getString(body, "effect").trim();
        String secondaryEffect = getString(body, "secondaryEffect").trim();
        List<String> effectNames = getStringList(body, "effectNames");
        if (effectNames.isEmpty()) {
            if (!effect.isEmpty()) {
                effectNames.add(effect);
            }
            if (!secondaryEffect.isEmpty() && !effectNames.contains(secondaryEffect)) {
                effectNames.add(secondaryEffect);
            }
        }
        if (spellId.isEmpty()) {
            ctx.json(400, Map.of("error", "Spell id is required"));
            return;
        }
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        boolean[] duplicate = new boolean[] { false };
        boolean[] updated = new boolean[] { false };
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Spell> registry = game.getElementRegistry(ElementRegistryKey.SPELLS);
            Spell spell = registry.getById(spellId);
            if (spell == null) {
                return;
            }
            String previousName = spell.getName();
            if (!previousName.equalsIgnoreCase(name) && registry.hasName(name)) {
                duplicate[0] = true;
                return;
            }
            if (!previousName.equalsIgnoreCase(name)) {
                registry.remove(spell);
                spell.setName(name);
                registry.add(spell);
            }
            spell.setDescription(description);
            spell.setSchool(school);
            spell.setLevel(level);
            spell.setCastingTime(castingTime);
            spell.setRange(range);
            spell.setDuration(duration);
            spell.clearArray("effectNames");
            for (String effectName : effectNames) {
                spell.addToArray("effectNames", effectName);
            }
            spell.setEffect(effectNames.isEmpty() ? effect : effectNames.get(0));
            spell.setSecondaryEffect(effectNames.size() > 1 ? effectNames.get(1) : "");
            updated[0] = true;
        });
        if (duplicate[0]) {
            ctx.json(400, Map.of("error", "Spell already exists"));
            return;
        }
        if (!updated[0]) {
            ctx.json(404, Map.of("error", "Spell not found"));
            return;
        }
        ctx.json(200, Map.of("ok", true));
    }

    private static void getPantheons(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        markStageCompleted(ctx, draftId, "pantheons");
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            Map<String, Object> response = new LinkedHashMap<>();
            List<Map<String, Object>> deities = new ArrayList<>();
            for (Deity deity : getDeities(game)) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("id", Objects.toString(deity.getId(), ""));
                entry.put("name", Objects.toString(deity.getName(), ""));
                deities.add(entry);
            }
            List<Map<String, Object>> pantheons = new ArrayList<>();
            for (Pantheon pantheon : getPantheons(game)) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("id", Objects.toString(pantheon.getId(), ""));
                entry.put("name", Objects.toString(pantheon.getName(), ""));
                entry.put("description", Objects.toString(pantheon.getDescription(), ""));
                entry.put("deityIds", safeList(pantheon.getObjectArray("deities")));
                pantheons.add(entry);
            }
            response.put("systemName", game.getSystemName("pantheons"));
            response.put("pantheons", pantheons);
            response.put("deities", deities);
            return response;
        });
        ctx.json(200, payload);
    }

    private static void addPantheon(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String name = getString(body, "name").trim();
        String description = getString(body, "description").trim();
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        Pantheon pantheon = new Pantheon(name, description);
        boolean[] added = new boolean[] { false };
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Pantheon> registry = game.getElementRegistry(ElementRegistryKey.PANTHEONS);
            if (!registry.hasName(name) && registry.add(pantheon)) {
                added[0] = true;
            }
        });
        if (!added[0]) {
            ctx.json(400, Map.of("error", "Pantheon already exists"));
            return;
        }
        ctx.json(200, Map.of("ok", true, "id", Objects.toString(pantheon.getId(), "")));
    }

    private static void removePantheon(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String pantheonId = getString(body, "id");
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Pantheon> registry = game.getElementRegistry(ElementRegistryKey.PANTHEONS);
            Pantheon pantheon = registry.getById(pantheonId);
            if (pantheon != null) {
                registry.remove(pantheon);
            }
            for (Deity deity : getDeities(game)) {
                deity.removeFromArray("pantheon", pantheonId);
            }
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void updatePantheon(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String pantheonId = getString(body, "id");
        String name = getString(body, "name").trim();
        String description = getString(body, "description").trim();
        List<String> deityIds = getStringList(body, "deityIds");
        if (pantheonId.isEmpty()) {
            ctx.json(400, Map.of("error", "Pantheon id is required"));
            return;
        }
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        boolean[] duplicate = new boolean[] { false };
        boolean[] updated = new boolean[] { false };
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Pantheon> registry = game.getElementRegistry(ElementRegistryKey.PANTHEONS);
            Pantheon pantheon = registry.getById(pantheonId);
            if (pantheon == null) {
                return;
            }
            String previousName = pantheon.getName();
            if (!previousName.equalsIgnoreCase(name) && registry.hasName(name)) {
                duplicate[0] = true;
                return;
            }
            if (!previousName.equalsIgnoreCase(name)) {
                registry.remove(pantheon);
                pantheon.setName(name);
                registry.add(pantheon);
            }
            pantheon.setDescription(description);
            pantheon.clearArray("deities");
            for (String deityId : deityIds) {
                if (game.getElementRegistry(ElementRegistryKey.DEITIES).getById(deityId) != null) {
                    pantheon.addToArray("deities", deityId);
                }
            }
            for (Deity deity : getDeities(game)) {
                deity.removeFromArray("pantheon", pantheonId);
                if (deityIds.contains(Objects.toString(deity.getId(), ""))) {
                    deity.addToArray("pantheon", pantheonId);
                }
            }
            updated[0] = true;
        });
        if (duplicate[0]) {
            ctx.json(400, Map.of("error", "Pantheon already exists"));
            return;
        }
        if (!updated[0]) {
            ctx.json(404, Map.of("error", "Pantheon not found"));
            return;
        }
        ctx.json(200, Map.of("ok", true));
    }

    private static void getDeities(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        markStageCompleted(ctx, draftId, "deities");
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            Map<String, Object> response = new LinkedHashMap<>();
            List<Map<String, Object>> pantheons = new ArrayList<>();
            for (Pantheon pantheon : getPantheons(game)) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("id", Objects.toString(pantheon.getId(), ""));
                entry.put("name", Objects.toString(pantheon.getName(), ""));
                pantheons.add(entry);
            }
            List<Map<String, Object>> deities = new ArrayList<>();
            for (Deity deity : getDeities(game)) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("id", Objects.toString(deity.getId(), ""));
                entry.put("name", Objects.toString(deity.getName(), ""));
                entry.put("description", Objects.toString(deity.getDescription(), ""));
                entry.put("divineRank", Objects.toString(deity.getDivineRank(), ""));
                entry.put("deityType", Objects.toString(deity.getDeityType(), ""));
                entry.put("primaryPortfolio", Objects.toString(deity.getPrimaryPortfolio(), ""));
                entry.put("holySymbol", Objects.toString(deity.getHolySymbol(), ""));
                entry.put("alignment", Objects.toString(deity.getAlignment(), ""));
                entry.put("worshipStyle", Objects.toString(deity.getWorshipStyle(), ""));
                entry.put("canGrantSpells", deity.canGrantSpells());
                entry.put("maxSpellLevel", deity.getMaxSpellLevel());
                entry.put("pantheonIds", safeList(deity.getObjectArray("pantheon")));
                deities.add(entry);
            }
            response.put("systemName", game.getSystemName("deities"));
            response.put("deities", deities);
            response.put("pantheons", pantheons);
            return response;
        });
        ctx.json(200, payload);
    }

    private static void addDeity(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String name = getString(body, "name").trim();
        String description = getString(body, "description").trim();
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        Deity deity = new Deity(name, description);
        boolean[] added = new boolean[] { false };
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Deity> registry = game.getElementRegistry(ElementRegistryKey.DEITIES);
            if (!registry.hasName(name) && registry.add(deity)) {
                added[0] = true;
            }
        });
        if (!added[0]) {
            ctx.json(400, Map.of("error", "Deity already exists"));
            return;
        }
        ctx.json(200, Map.of("ok", true, "id", Objects.toString(deity.getId(), "")));
    }

    private static void removeDeity(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String deityId = getString(body, "id");
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Deity> registry = game.getElementRegistry(ElementRegistryKey.DEITIES);
            Deity deity = registry.getById(deityId);
            if (deity != null) {
                registry.remove(deity);
            }
            for (Pantheon pantheon : getPantheons(game)) {
                pantheon.removeFromArray("deities", deityId);
            }
            for (Deity other : getDeities(game)) {
                other.removeFromArray("alliedDeities", deityId);
                other.removeFromArray("enemyDeities", deityId);
            }
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void updateDeity(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String deityId = getString(body, "id");
        String name = getString(body, "name").trim();
        String description = getString(body, "description").trim();
        String divineRank = getString(body, "divineRank").trim();
        String deityType = getString(body, "deityType").trim();
        String primaryPortfolio = getString(body, "primaryPortfolio").trim();
        String holySymbol = getString(body, "holySymbol").trim();
        String alignment = getString(body, "alignment").trim();
        String worshipStyle = getString(body, "worshipStyle").trim();
        boolean canGrantSpells = getBoolean(body, "canGrantSpells", true);
        int maxSpellLevel = getInt(body, "maxSpellLevel", 9);
        List<String> pantheonIds = getStringList(body, "pantheonIds");
        if (deityId.isEmpty()) {
            ctx.json(400, Map.of("error", "Deity id is required"));
            return;
        }
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        boolean[] duplicate = new boolean[] { false };
        boolean[] updated = new boolean[] { false };
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Deity> registry = game.getElementRegistry(ElementRegistryKey.DEITIES);
            Deity deity = registry.getById(deityId);
            if (deity == null) {
                return;
            }
            String previousName = deity.getName();
            if (!previousName.equalsIgnoreCase(name) && registry.hasName(name)) {
                duplicate[0] = true;
                return;
            }
            if (!previousName.equalsIgnoreCase(name)) {
                registry.remove(deity);
                deity.setName(name);
                registry.add(deity);
            }
            deity.setDescription(description);
            deity.setDivineRank(divineRank);
            deity.setDeityType(deityType);
            deity.setPrimaryPortfolio(primaryPortfolio);
            deity.setHolySymbol(holySymbol);
            deity.setAlignment(alignment);
            deity.setWorshipStyle(worshipStyle);
            deity.setCanGrantSpells(canGrantSpells);
            deity.setMaxSpellLevel(maxSpellLevel);
            deity.clearArray("pantheon");
            for (String pantheonId : pantheonIds) {
                if (game.getElementRegistry(ElementRegistryKey.PANTHEONS).getById(pantheonId) != null) {
                    deity.addToArray("pantheon", pantheonId);
                }
            }
            for (Pantheon pantheon : getPantheons(game)) {
                pantheon.removeFromArray("deities", deityId);
                if (pantheonIds.contains(Objects.toString(pantheon.getId(), ""))) {
                    pantheon.addToArray("deities", deityId);
                }
            }
            updated[0] = true;
        });
        if (duplicate[0]) {
            ctx.json(400, Map.of("error", "Deity already exists"));
            return;
        }
        if (!updated[0]) {
            ctx.json(404, Map.of("error", "Deity not found"));
            return;
        }
        ctx.json(200, Map.of("ok", true));
    }

    private static void getRaces(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        markStageCompleted(ctx, draftId, "races");
        Map<String, Object> payload = ctx.getDraftStore().readDraft(draftId, game -> {
            Map<String, String> skillNamesById = new LinkedHashMap<>();
            for (Skill skill : getSkills(game)) {
                String id = Objects.toString(skill.getId(), "").trim();
                if (id.isEmpty()) {
                    continue;
                }
                skillNamesById.put(id, Objects.toString(skill.getName(), ""));
            }
            Map<String, Object> response = new LinkedHashMap<>();
            List<Map<String, Object>> races = new ArrayList<>();
            for (Race race : getRaces(game)) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("id", Objects.toString(race.getId(), ""));
                entry.put("name", Objects.toString(race.getName(), ""));
                entry.put("description", Objects.toString(race.getDescription(), ""));
                entry.put("playable", race.isPlayable());
                entry.put("parentRace", Objects.toString(race.getParentRace(), ""));
                entry.put("society", Objects.toString(race.getSociety(), ""));
                entry.put("culture", Objects.toString(race.getCulture(), ""));
                entry.put("startingMoneyModifier", game.getRaceStartingMoneyModifier(Objects.toString(race.getId(), "")));
                List<String> racialSkills = race.getArray("racialSkills");
                List<String> racialSkillIds = racialSkills == null ? List.of() : new ArrayList<>(racialSkills);
                entry.put("racialSkillIds", racialSkillIds);
                List<String> racialSkillNames = new ArrayList<>();
                for (String skillId : racialSkillIds) {
                    String safeId = Objects.toString(skillId, "").trim();
                    if (safeId.isEmpty()) {
                        continue;
                    }
                    racialSkillNames.add(Objects.toString(skillNamesById.getOrDefault(safeId, safeId), ""));
                }
                entry.put("racialSkillNames", racialSkillNames);
                List<Map<String, Object>> attributeScoreLimits = new ArrayList<>();
                for (Species.AttributeScoreLimit limit : race.getAttributeScoreLimits()) {
                    Map<String, Object> limitEntry = new LinkedHashMap<>();
                    limitEntry.put("attributeId", Objects.toString(limit.getAttributeId(), ""));
                    limitEntry.put("min", limit.getMin());
                    limitEntry.put("max", limit.getMax());
                    attributeScoreLimits.add(limitEntry);
                }
                entry.put("attributeScoreLimits", attributeScoreLimits);
                races.add(entry);
            }
            response.put("systemName", game.getSystemName("races"));
            response.put("races", races);
            return response;
        });
        ctx.json(200, payload);
    }

    private static void addRace(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String name = getString(body, "name").trim();
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        Race race = new Race(name);
        boolean[] added = new boolean[] { false };
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Race> registry = game.getElementRegistry(ElementRegistryKey.RACES);
            if (registry.hasName(name)) {
                return;
            }
            if (registry.add(race)) {
                added[0] = true;
            }
        });
        if (!added[0]) {
            ctx.json(400, Map.of("error", "Race already exists"));
            return;
        }
        ctx.json(200, Map.of("ok", true, "id", Objects.toString(race.getId(), "")));
    }

    private static void removeRace(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String raceId = getString(body, "id");
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Race> registry = game.getElementRegistry(ElementRegistryKey.RACES);
            Race race = registry.getById(raceId);
            if (race != null) {
                game.setRaceStartingMoneyModifier(Objects.toString(race.getId(), ""), 0);
                registry.remove(race);
            }
        });
        ctx.json(200, Map.of("ok", true));
    }

    private static void updateRace(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return;
        }
        String draftId = ctx.pathParam("id");
        Map<String, Object> body = ctx.readJsonMap();
        String raceId = getString(body, "id");
        String name = getString(body, "name").trim();
        String description = getString(body, "description").trim();
        boolean playable = getBoolean(body, "playable", true);
        String parentRace = getString(body, "parentRace").trim();
        String society = getString(body, "society").trim();
        String culture = getString(body, "culture").trim();
        int startingMoneyModifier = getInt(body, "startingMoneyModifier", 0);
        List<String> racialSkillIds = getStringList(body, "racialSkillIds");
        List<Map<String, Object>> attributeScoreLimits = getMapList(body, "attributeScoreLimits");
        boolean hasPlayable = body.containsKey("playable");
        boolean hasParentRace = body.containsKey("parentRace");
        boolean hasSociety = body.containsKey("society");
        boolean hasCulture = body.containsKey("culture");
        boolean hasRacialSkills = body.containsKey("racialSkillIds");
        boolean hasAttributeScoreLimits = body.containsKey("attributeScoreLimits");
        if (raceId.isEmpty()) {
            ctx.json(400, Map.of("error", "Race id is required"));
            return;
        }
        if (name.isEmpty()) {
            ctx.json(400, Map.of("error", "Name is required"));
            return;
        }
        if (hasAttributeScoreLimits) {
            for (Map<String, Object> entry : attributeScoreLimits) {
                int min = getInt(entry, "min", 0);
                int max = getInt(entry, "max", 0);
                if (min > max) {
                    ctx.json(400, Map.of("error", "Minimum cannot exceed maximum"));
                    return;
                }
            }
        }
        boolean[] duplicate = new boolean[] { false };
        boolean[] updated = new boolean[] { false };
        ctx.getDraftStore().updateDraft(draftId, game -> {
            ElementRegistry<Race> registry = game.getElementRegistry(ElementRegistryKey.RACES);
            Race race = registry.getById(raceId);
            if (race == null) {
                return;
            }
            String previousName = race.getName();
            if (!previousName.equalsIgnoreCase(name) && registry.hasName(name)) {
                duplicate[0] = true;
                return;
            }
            if (!previousName.equalsIgnoreCase(name)) {
                registry.remove(race);
                race.setName(name);
                registry.add(race);
            }
            race.setDescription(description);
            if (hasPlayable) {
                race.setPlayable(playable);
            }
            if (hasParentRace) {
                race.setParentRace(parentRace);
            }
            if (hasSociety) {
                race.setSociety(society);
            }
            if (hasCulture) {
                race.setCulture(culture);
            }
            game.setRaceStartingMoneyModifier(Objects.toString(race.getId(), ""), startingMoneyModifier);
            if (hasRacialSkills) {
                ElementRegistry<Skill> skillRegistry = game.getElementRegistry(ElementRegistryKey.SKILLS);
                race.clearArray("racialSkills");
                for (String skillId : racialSkillIds) {
                    if (skillRegistry.getById(skillId) != null) {
                        race.addToArray("racialSkills", skillId);
                    }
                }
            }
            if (hasAttributeScoreLimits) {
                race.clearAttributeScoreLimits();
                java.util.HashSet<String> seen = new java.util.HashSet<>();
                for (Map<String, Object> entry : attributeScoreLimits) {
                    String attributeId = getString(entry, "attributeId").trim();
                    if (attributeId.isEmpty() || seen.contains(attributeId)) {
                        continue;
                    }
                    Attribute attribute = game.getElement("attributes", attributeId);
                    if (attribute == null) {
                        continue;
                    }
                    int min = getInt(entry, "min", 0);
                    int max = getInt(entry, "max", 0);
                    race.addAttributeScoreLimit(attributeId, min, max);
                    seen.add(attributeId);
                }
            }
            updated[0] = true;
        });
        if (duplicate[0]) {
            ctx.json(400, Map.of("error", "Race already exists"));
            return;
        }
        if (!updated[0]) {
            ctx.json(404, Map.of("error", "Race not found"));
            return;
        }
        ctx.json(200, Map.of("ok", true));
    }

    private static void updateEffectReferences(Game game, String oldName, String newName) {
        String safeOldName = Objects.toString(oldName, "").trim();
        String safeNewName = Objects.toString(newName, "").trim();
        if (safeOldName.isEmpty() || safeOldName.equalsIgnoreCase(safeNewName)) {
            return;
        }
        ElementRegistry<Skill> registry = game.getElementRegistry(ElementRegistryKey.SKILLS);
        List<Skill> skills = registry.getAll();
        for (Skill skill : skills) {
            List<String> effectNames = skill.getArray("effectNames");
            if (effectNames == null || effectNames.isEmpty()) {
                continue;
            }
            boolean removed = false;
            for (int index = effectNames.size() - 1; index >= 0; index--) {
                if (safeOldName.equals(effectNames.get(index))) {
                    effectNames.remove(index);
                    removed = true;
                }
            }
            if (removed && !safeNewName.isEmpty() && !effectNames.contains(safeNewName)) {
                effectNames.add(safeNewName);
            }
        }

        ElementRegistry<Spell> spellRegistry = game.getElementRegistry(ElementRegistryKey.SPELLS);
        List<Spell> spells = spellRegistry.getAll();
        for (Spell spell : spells) {
            String effect = Objects.toString(spell.getEffect(), "");
            String secondary = Objects.toString(spell.getSecondaryEffect(), "");
            if (effect.equalsIgnoreCase(safeOldName)) {
                spell.setEffect(safeNewName);
            }
            if (secondary.equalsIgnoreCase(safeOldName)) {
                spell.setSecondaryEffect(safeNewName);
            }
        }
    }

    private static void updateEffectTypeReferences(Game game, String oldName, String newName) {
        String safeOldName = Objects.toString(oldName, "").trim();
        String safeNewName = Objects.toString(newName, "").trim();
        if (safeOldName.isEmpty() || safeOldName.equalsIgnoreCase(safeNewName)) {
            return;
        }
        ElementRegistry<Effect> registry = game.getElementRegistry(ElementRegistryKey.EFFECTS);
        List<Effect> effects = registry.getAll();
        for (Effect effect : effects) {
            List<String> typeKeys = effect.getEffectTypeKeys();
            if (typeKeys == null || typeKeys.isEmpty()) {
                continue;
            }
            for (int index = typeKeys.size() - 1; index >= 0; index--) {
                String value = Objects.toString(typeKeys.get(index), "").trim();
                if (value.equalsIgnoreCase(safeOldName)) {
                    if (safeNewName.isEmpty()) {
                        typeKeys.remove(index);
                    } else {
                        typeKeys.set(index, safeNewName);
                    }
                }
            }
        }

        ElementRegistry<Status> statusRegistry = game.getElementRegistry(ElementRegistryKey.STATUSES);
        List<Status> statuses = statusRegistry.getAll();
        for (Status status : statuses) {
            List<String> typeKeys = status.getEffectTypeKeys();
            if (typeKeys == null || typeKeys.isEmpty()) {
                continue;
            }
            for (int index = typeKeys.size() - 1; index >= 0; index--) {
                String value = Objects.toString(typeKeys.get(index), "").trim();
                if (value.equalsIgnoreCase(safeOldName)) {
                    if (safeNewName.isEmpty()) {
                        typeKeys.remove(index);
                    } else {
                        typeKeys.set(index, safeNewName);
                    }
                }
            }
        }
    }

    private static SessionStore.Session requireSession(RequestContext ctx) throws IOException {
        String sessionId = resolveToken(ctx);
        SessionStore.Session session = ctx.getSessionStore().getSession(sessionId);
        if (session == null) {
            ctx.json(401, Map.of("error", "Unauthorized"));
            return null;
        }
        return session;
    }

    private static SessionStore.Session requireAdminSession(RequestContext ctx) throws IOException {
        SessionStore.Session session = requireSession(ctx);
        if (session == null) {
            return null;
        }
        if (!ctx.getConfig().isAdminEmail(session.getUsername())) {
            ctx.json(403, Map.of("error", "Admin access required."));
            return null;
        }
        return session;
    }

    private static boolean isBlockedSignup(RequestContext ctx, String email) throws IOException {
        BlockedAccessStore blockedAccessStore = new BlockedAccessStore(ctx.getConfig());
        if (blockedAccessStore.isIpBlocked(ctx.clientIp()) || blockedAccessStore.isEmailBlocked(email)) {
            ctx.json(403, Map.of("error", "Closed beta access is not available for this request."));
            return true;
        }
        return false;
    }

    private static boolean isBlockedLogin(RequestContext ctx, String email) throws IOException {
        BlockedAccessStore blockedAccessStore = new BlockedAccessStore(ctx.getConfig());
        if (blockedAccessStore.isIpBlocked(ctx.clientIp()) || blockedAccessStore.isEmailBlocked(email)) {
            ctx.json(403, Map.of("error", "This account is blocked from closed beta access."));
            return true;
        }
        return false;
    }

    private static boolean ensureCanCreateDraft(RequestContext ctx, SessionStore.Session session) throws IOException {
        if (session.isLegacyGuest()) {
            return true;
        }
        if (ctx.getAccountStore().canAddDraft(session.getUserId())) {
            return true;
        }
        ctx.json(400, Map.of("error", "Each account can save up to two rulesets for this PoC."));
        return false;
    }

    private static boolean ensureStandardArrayEnabled(RequestContext ctx, String draftId) throws IOException {
        boolean enabled = ctx.getDraftStore().readDraft(
            draftId,
            game -> isStandardArrayEnabled(game.getAttributeGenerationMethod())
        );
        if (enabled) {
            return true;
        }
        ctx.json(400, Map.of("error", "Standard Array is not selected in Attribute Generation."));
        return false;
    }

    private static boolean ensureDiceRollingEnabled(RequestContext ctx, String draftId) throws IOException {
        boolean enabled = ctx.getDraftStore().readDraft(
            draftId,
            game -> isDiceRollingEnabled(game.getAttributeGenerationMethod())
        );
        if (enabled) {
            return true;
        }
        ctx.json(400, Map.of("error", "Dice Rolling is not selected in Attribute Generation."));
        return false;
    }

    private static boolean ensurePointBuyEnabled(RequestContext ctx, String draftId) throws IOException {
        boolean enabled = ctx.getDraftStore().readDraft(
            draftId,
            game -> isPointBuyEnabled(game.getAttributeGenerationMethod())
        );
        if (enabled) {
            return true;
        }
        ctx.json(400, Map.of("error", "Point Buy is not selected in Attribute Generation."));
        return false;
    }

    private static boolean isStandardArrayEnabled(AttributeGenerationMethod method) {
        return isAttributeGenerationStageEnabled(method, "standard_array");
    }

    private static boolean isOpenStandardArray(AttributeGenerationMethod method) {
        AttributeGenerationMethod safeMethod = Objects.requireNonNullElseGet(
            method,
            () -> new AttributeGenerationMethod("")
        );
        return "open".equals(Objects.toString(safeMethod.getStandardArrayAssignmentMode(), "").trim().toLowerCase());
    }

    private static boolean isDiceRollingEnabled(AttributeGenerationMethod method) {
        return isAttributeGenerationStageEnabled(method, "dice");
    }

    private static boolean isPointBuyEnabled(AttributeGenerationMethod method) {
        return isAttributeGenerationStageEnabled(method, "point_buy");
    }

    private static boolean isAttributeGenerationStageEnabled(AttributeGenerationMethod method, String stageKey) {
        AttributeGenerationMethod safeMethod = Objects.requireNonNullElseGet(
            method,
            () -> new AttributeGenerationMethod("")
        );
        String safeStage = Objects.toString(stageKey, "").trim().toLowerCase();
        if (safeStage.isEmpty()) {
            return false;
        }
        String type = Objects.toString(safeMethod.getGenerationType(), "").trim().toLowerCase();
        if (safeStage.equals(type)) {
            return true;
        }
        if (!"hybrid".equals(type)) {
            return false;
        }
        List<String> stages = safeList(safeMethod.getArray(ARRAY_HYBRID));
        if (stages.isEmpty()) {
            return true;
        }
        for (String stage : stages) {
            if (safeStage.equals(Objects.toString(stage, "").trim().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private static Map<String, Object> buildDraftEntry(RequestContext ctx, String draftId) throws IOException {
        Map<String, Object> entry = ctx.getDraftStore().readDraft(draftId, game -> {
            Map<String, Object> payload = new LinkedHashMap<>();
            String name = Objects.toString(game.getName(), "").trim();
            payload.put("id", draftId);
            payload.put("name", name.isEmpty() ? "Untitled Ruleset" : name);
            payload.put("description", Objects.toString(game.getDescription(), ""));
            payload.put("locale", Objects.toString(game.getUiLocale(), ""));
            payload.put("completedStages", game.getCompletedStages());
            return payload;
        });
        Instant lastSaved = ctx.getDraftStore().getLastSaved(draftId);
        entry.put("lastSaved", lastSaved.toString());
        return entry;
    }

    private static Map<String, Object> characterDraftEntry(CharacterDraftStore.CharacterDraftSummary summary) {
        Map<String, Object> entry = new LinkedHashMap<>();
        String gameName = Objects.toString(summary.getGameName(), "").trim();
        String characterName = Objects.toString(summary.getCharacterName(), "").trim();
        entry.put("id", summary.getId());
        entry.put("gameDraftId", summary.getGameDraftId());
        entry.put("gameId", summary.getGameId());
        entry.put("gameHash", summary.getGameHash());
        entry.put("characterName", characterName);
        entry.put("gameName", gameName);
        entry.put("name", characterName.isEmpty() ? "Character Draft" : characterName);
        entry.put("raceId", summary.getRaceId());
        entry.put("classId", summary.getClassId());
        entry.put("lastSaved", summary.getLastSaved().toString());
        return entry;
    }

    private static String resolveCharacterFileDraftId(
            RequestContext ctx,
            SessionStore.Session session,
            CharacterFile characterFile
    ) throws IOException {
        CharacterFile safeCharacterFile = Objects.requireNonNullElseGet(characterFile, CharacterFile::new);
        String sourceGameId = Objects.toString(safeCharacterFile.getSourceGameId(), "").trim();
        String sessionDraftId = Objects.toString(session.getDraftId(), "").trim();
        if (!sessionDraftId.isEmpty()
                && canAccessCharacterExportDraft(ctx, session, sessionDraftId)
                && characterFileMatchesDraft(ctx, sessionDraftId, sourceGameId)) {
            return sessionDraftId;
        }
        if (session.isLegacyGuest()) {
            return "";
        }
        for (String draftId : ctx.getAccountStore().listDraftIds(session.getUserId())) {
            if (characterFileMatchesDraft(ctx, draftId, sourceGameId)) {
                return draftId;
            }
        }
        return "";
    }

    private static boolean characterFileMatchesDraft(RequestContext ctx, String draftId, String sourceGameId) throws IOException {
        String safeSourceGameId = Objects.toString(sourceGameId, "").trim();
        if (safeSourceGameId.isEmpty()) {
            return false;
        }
        try {
            return ctx.getDraftStore().readDraft(draftId, game -> safeSourceGameId.equals(Objects.toString(game.getId(), "")));
        } catch (IOException e) {
            return false;
        }
    }

    private static String serializeCharacterFileDraft(CharacterFile characterFile, String gameDraftId) {
        CharacterFile safeCharacterFile = Objects.requireNonNullElseGet(characterFile, CharacterFile::new);
        List<String> lines = new ArrayList<>();
        lines.add("GMRulesCharacterFile v1");
        lines.add("gameDraftId=" + Objects.toString(gameDraftId, "").trim());
        lines.add("gameId=" + Objects.toString(safeCharacterFile.getSourceGameId(), "").trim());
        lines.add("gameHash=" + Objects.toString(safeCharacterFile.getSourceGameHash(), "").trim());
        lines.add("characterName=" + Objects.toString(safeCharacterFile.getCharacterName(), "").trim());
        lines.add("gameName=" + Objects.toString(safeCharacterFile.getSourceGameName(), "").trim());
        lines.add("diceSubstitutionsUsed=" + safeCharacterFile.getDiceSubstitutionsUsed());
        safeCharacterFile.getRuleModeSelections().entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> lines.add(
                CHARACTER_RULE_MODE_PREFIX
                    + Objects.toString(entry.getKey(), "").trim()
                    + "="
                    + Objects.toString(entry.getValue(), "").trim()
            ));
        String raceId = characterRaceId(safeCharacterFile);
        if (!raceId.isEmpty()) {
            lines.add("raceId=" + raceId);
        }
        String classId = characterClassId(safeCharacterFile);
        if (!classId.isEmpty()) {
            lines.add("classId=" + classId);
        }
        safeCharacterFile.getAttributeScores().entrySet().stream()
            .sorted(Comparator.comparing(entry -> Objects.toString(entry.getKey().getId(), "")))
            .forEach(entry -> {
                String attributeId = Objects.toString(entry.getKey().getId(), "").trim();
                if (!attributeId.isEmpty()) {
                    lines.add("attr." + attributeId + "=" + Objects.requireNonNullElse(entry.getValue(), 0));
                }
            });
        safeCharacterFile.getClassSkills().entrySet().stream()
            .sorted(Comparator.comparing(entry -> Objects.toString(entry.getKey().getId(), "")))
            .forEach(entry -> {
                String skillId = Objects.toString(entry.getKey().getId(), "").trim();
                if (!skillId.isEmpty()) {
                    lines.add("classSkill." + skillId + "=" + skillId + "|" + Math.max(0, Objects.requireNonNullElse(entry.getValue(), 0)));
                }
            });
        safeCharacterFile.getSelectedSkills().entrySet().stream()
            .sorted(Comparator.comparing(entry -> Objects.toString(entry.getKey().getId(), "")))
            .forEach(entry -> {
                String skillId = Objects.toString(entry.getKey().getId(), "").trim();
                if (!skillId.isEmpty()) {
                    lines.add("selectedSkill." + skillId + "=" + skillId + "|" + Math.max(0, Objects.requireNonNullElse(entry.getValue(), 0)));
                }
            });
        List<String> selectedSpellIds = safeCharacterFile.getSelectedSpells().stream()
            .map(spell -> characterSpellId(spell))
            .filter(id -> !id.isEmpty())
            .sorted()
            .toList();
        for (int index = 0; index < selectedSpellIds.size(); index++) {
            lines.add("selectedSpell." + index + "=" + selectedSpellIds.get(index));
        }
        List<String> selectedEquipmentIds = safeCharacterFile.getSelectedEquipment().stream()
            .map(item -> characterEquipmentId(item))
            .filter(id -> !id.isEmpty())
            .sorted()
            .toList();
        for (int index = 0; index < selectedEquipmentIds.size(); index++) {
            lines.add("selectedEquipment." + index + "=" + selectedEquipmentIds.get(index));
        }
        List<String> selectedWeaponIds = safeCharacterFile.getSelectedWeapons().stream()
            .map(weapon -> characterWeaponId(weapon))
            .filter(id -> !id.isEmpty())
            .sorted()
            .toList();
        for (int index = 0; index < selectedWeaponIds.size(); index++) {
            lines.add("selectedWeapon." + index + "=" + selectedWeaponIds.get(index));
        }
        List<String> selectedArmorIds = safeCharacterFile.getSelectedArmor().stream()
            .map(item -> characterArmorId(item))
            .filter(id -> !id.isEmpty())
            .sorted()
            .toList();
        for (int index = 0; index < selectedArmorIds.size(); index++) {
            lines.add("selectedArmor." + index + "=" + selectedArmorIds.get(index));
        }
        String currencyId = characterCurrencyId(safeCharacterFile);
        lines.add("startingMoneyAmount=" + safeCharacterFile.getStartingMoneyAmount());
        if (!currencyId.isEmpty()) {
            lines.add("startingMoneyCurrencyId=" + currencyId);
        }
        lines.add("resolvedArmorClass=" + safeCharacterFile.getResolvedArmorClass());
        return String.join("\n", lines);
    }

    private static String buildCharacterExportFilename(Game game, CharacterDraft characterDraft) {
        String gameName = Objects.toString(Objects.requireNonNullElseGet(game, () -> new Game("")).getName(), "").trim();
        String characterName = Objects.toString(
            Objects.requireNonNullElseGet(characterDraft, CharacterDraft::new).getCharacterName(),
            ""
        ).trim();
        if (gameName.isEmpty() && characterName.isEmpty()) {
            return "character";
        }
        if (gameName.isEmpty()) {
            return characterName;
        }
        if (characterName.isEmpty()) {
            return gameName + "-character";
        }
        return gameName + "-" + characterName;
    }

    private static String characterRaceId(CharacterFile characterFile) {
        Race race = Objects.requireNonNullElseGet(characterFile, CharacterFile::new).getRace();
        return Objects.toString(race.getName(), "").trim().isEmpty()
            ? ""
            : Objects.toString(race.getId(), "").trim();
    }

    private static String characterClassId(CharacterFile characterFile) {
        CharacterClass characterClass = Objects.requireNonNullElseGet(characterFile, CharacterFile::new).getCharacterClass();
        return Objects.toString(characterClass.getName(), "").trim().isEmpty()
            ? ""
            : Objects.toString(characterClass.getId(), "").trim();
    }

    private static String characterSpellId(Spell spell) {
        Spell safeSpell = Objects.requireNonNullElseGet(spell, () -> new Spell(""));
        return Objects.toString(safeSpell.getName(), "").trim().isEmpty()
            ? ""
            : Objects.toString(safeSpell.getId(), "").trim();
    }

    private static String characterEquipmentId(Equipment equipment) {
        Equipment safeEquipment = Objects.requireNonNullElseGet(equipment, () -> new Equipment(""));
        return Objects.toString(safeEquipment.getName(), "").trim().isEmpty()
            ? ""
            : Objects.toString(safeEquipment.getId(), "").trim();
    }

    private static String characterWeaponId(Weapon weapon) {
        Weapon safeWeapon = Objects.requireNonNullElseGet(weapon, () -> new Weapon(""));
        return Objects.toString(safeWeapon.getName(), "").trim().isEmpty()
            ? ""
            : Objects.toString(safeWeapon.getId(), "").trim();
    }

    private static String characterArmorId(Armor armor) {
        Armor safeArmor = Objects.requireNonNullElseGet(armor, () -> new Armor(""));
        return Objects.toString(safeArmor.getName(), "").trim().isEmpty()
            ? ""
            : Objects.toString(safeArmor.getId(), "").trim();
    }

    private static String characterCurrencyId(CharacterFile characterFile) {
        Currency currency = Objects.requireNonNullElseGet(characterFile, CharacterFile::new).getStartingMoneyCurrency();
        return Objects.toString(currency.getName(), "").trim().isEmpty()
            ? ""
            : Objects.toString(currency.getId(), "").trim();
    }

    private static CharacterDraft parseCharacterDraft(String text) throws IOException {
        Path tempFile = Files.createTempFile("gmrules-character-draft-", ".gmcf");
        try {
            Files.writeString(tempFile, Objects.toString(text, ""), StandardCharsets.UTF_8);
            return new CharacterFileIO().read(tempFile);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    private static String withCurrentCharacterRuleModes(RequestContext ctx, String gameDraftId, String text) throws IOException {
        if (hasCharacterRuleModeLines(text)) {
            return Objects.toString(text, "");
        }
        Map<String, String> ruleModes = ctx.getDraftStore().readDraft(
            gameDraftId,
            CharacterFileBuilder::buildRuleModeSelections
        );
        return mergeCharacterRuleModeLines(text, ruleModes);
    }

    private static boolean hasCharacterRuleModeLines(String text) {
        String[] rawLines = Objects.toString(text, "").split("\\R");
        for (String rawLine : rawLines) {
            if (Objects.toString(rawLine, "").trim().startsWith(CHARACTER_RULE_MODE_PREFIX)) {
                return true;
            }
        }
        return false;
    }

    private static String mergeCharacterRuleModeLines(String text, Map<String, String> ruleModes) {
        String[] rawLines = Objects.toString(text, "").split("\\R");
        List<String> lines = new ArrayList<>();
        for (String rawLine : rawLines) {
            String safeLine = Objects.toString(rawLine, "");
            String trimmed = safeLine.trim();
            if (!trimmed.startsWith(CHARACTER_RULE_MODE_PREFIX)) {
                lines.add(safeLine);
            }
        }
        Map<String, String> safeModes = Objects.requireNonNullElse(ruleModes, Map.of());
        safeModes.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                String key = Objects.toString(entry.getKey(), "").trim();
                if (!key.isEmpty()) {
                    lines.add(CHARACTER_RULE_MODE_PREFIX + key + "=" + Objects.toString(entry.getValue(), "").trim());
                }
            });
        return String.join("\n", lines);
    }

    private static List<String> characterExportDraftCandidates(String bodyGameDraftId, String text, SessionStore.Session session) {
        List<String> candidateDraftIds = new ArrayList<>();
        addCharacterExportDraftCandidate(candidateDraftIds, bodyGameDraftId);
        addCharacterExportDraftCandidate(candidateDraftIds, parseCharacterDraftField(text, "gameDraftId"));
        addCharacterExportDraftCandidate(candidateDraftIds, session.getDraftId());
        return candidateDraftIds;
    }

    private static void addCharacterExportDraftCandidate(List<String> candidateDraftIds, String draftId) {
        String safeDraftId = Objects.toString(draftId, "").trim();
        if (!safeDraftId.isEmpty() && !candidateDraftIds.contains(safeDraftId)) {
            candidateDraftIds.add(safeDraftId);
        }
    }

    private static String parseCharacterDraftField(String text, String fieldName) {
        String safeFieldName = Objects.toString(fieldName, "").trim();
        if (safeFieldName.isEmpty()) {
            return "";
        }
        String prefix = safeFieldName + "=";
        String[] lines = Objects.toString(text, "").split("\\R");
        for (String line : lines) {
            String safeLine = Objects.toString(line, "").trim();
            if (safeLine.startsWith(prefix)) {
                return safeLine.substring(prefix.length()).trim();
            }
        }
        return "";
    }

    private static boolean canAccessAnyCharacterExportDraft(
            RequestContext ctx,
            SessionStore.Session session,
            List<String> candidateDraftIds
    ) throws IOException {
        for (String candidateDraftId : candidateDraftIds) {
            if (canAccessCharacterExportDraft(ctx, session, candidateDraftId)) {
                return true;
            }
        }
        return false;
    }

    private static boolean canAccessCharacterExportDraft(RequestContext ctx, SessionStore.Session session, String draftId)
            throws IOException {
        String safeDraftId = Objects.toString(draftId, "").trim();
        if (safeDraftId.isEmpty()) {
            return false;
        }
        if (session.isLegacyGuest()) {
            return Objects.equals(session.getDraftId(), safeDraftId);
        }
        return ctx.getAccountStore().userOwnsDraft(session.getUserId(), safeDraftId);
    }

    private static void validateCharacterDraft(CharacterDraft draft, boolean hasServerDraftLink) {
        CharacterDraft safeDraft = Objects.requireNonNullElseGet(draft, CharacterDraft::new);
        if (safeDraft.getGameId().isEmpty() || (!hasServerDraftLink && safeDraft.getGameHash().isEmpty())) {
            throw new IllegalArgumentException("Character must be linked to a ruleset file.");
        }
        if (safeDraft.getCharacterName().isEmpty()) {
            throw new IllegalArgumentException("Enter a character name before exporting this character.");
        }
    }

    private static byte[] writeCharacterFileBytes(CharacterFileIO characterFileIO, Game game, CharacterDraft draft)
            throws IOException {
        Path tempFile = Files.createTempFile("gmrules-character-export-", ".gmcf");
        try {
            characterFileIO.write(game, draft, tempFile);
            return Files.readAllBytes(tempFile);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    private static String checkSignupRateLimit(RequestContext ctx, String email) {
        Instant now = Instant.now();
        String ipKey = "ip:" + Objects.toString(ctx.clientIp(), "").trim();
        String emailKey = "email:" + Objects.toString(email, "").trim().toLowerCase(Locale.ROOT);
        if (!isRollingRateLimitAvailable(
                SIGNUP_RATE_LIMITS,
                ipKey,
                SIGNUP_IP_RATE_LIMIT_MAX,
                SIGNUP_IP_RATE_LIMIT_WINDOW,
                now
        )) {
            return "A beta application was already submitted from this connection today.";
        }
        if (!emailKey.equals("email:") && !isRollingRateLimitAvailable(
                SIGNUP_RATE_LIMITS,
                emailKey,
                SIGNUP_EMAIL_RATE_LIMIT_MAX,
                SIGNUP_EMAIL_RATE_LIMIT_WINDOW,
                now
        )) {
            return "A beta application was already submitted for this email today. Check your email for the verification link.";
        }

        recordRollingRateLimit(SIGNUP_RATE_LIMITS, ipKey, SIGNUP_IP_RATE_LIMIT_WINDOW, now);
        if (!emailKey.equals("email:")) {
            recordRollingRateLimit(SIGNUP_RATE_LIMITS, emailKey, SIGNUP_EMAIL_RATE_LIMIT_WINDOW, now);
        }
        return "";
    }

    private static boolean consumeFeedbackRateLimit(RequestContext ctx, SessionStore.Session session) {
        String accountKey = Objects.toString(session.getUserId(), "").trim();
        String key = accountKey.isEmpty() ? "ip:" + ctx.clientIp() : "account:" + accountKey;
        Instant now = Instant.now();
        if (!isRollingRateLimitAvailable(
                FEEDBACK_RATE_LIMITS,
                key,
                FEEDBACK_RATE_LIMIT_MAX,
                FEEDBACK_RATE_LIMIT_WINDOW,
                now
        )) {
            return false;
        }
        recordRollingRateLimit(FEEDBACK_RATE_LIMITS, key, FEEDBACK_RATE_LIMIT_WINDOW, now);
        return true;
    }

    private static boolean consumeLockedAccountReportRateLimit(RequestContext ctx, String email) {
        String emailKey = "locked-email:" + Objects.toString(email, "").trim().toLowerCase(Locale.ROOT);
        String ipKey = "locked-ip:" + ctx.clientIp();
        Instant now = Instant.now();
        if (!isRollingRateLimitAvailable(
                LOCKED_ACCOUNT_REPORT_RATE_LIMITS,
                emailKey,
                LOCKED_ACCOUNT_REPORT_RATE_LIMIT_MAX,
                LOCKED_ACCOUNT_REPORT_RATE_LIMIT_WINDOW,
                now
        )) {
            return false;
        }
        if (!isRollingRateLimitAvailable(
                LOCKED_ACCOUNT_REPORT_RATE_LIMITS,
                ipKey,
                LOCKED_ACCOUNT_REPORT_RATE_LIMIT_MAX,
                LOCKED_ACCOUNT_REPORT_RATE_LIMIT_WINDOW,
                now
        )) {
            return false;
        }
        recordRollingRateLimit(LOCKED_ACCOUNT_REPORT_RATE_LIMITS, emailKey, LOCKED_ACCOUNT_REPORT_RATE_LIMIT_WINDOW, now);
        recordRollingRateLimit(LOCKED_ACCOUNT_REPORT_RATE_LIMITS, ipKey, LOCKED_ACCOUNT_REPORT_RATE_LIMIT_WINDOW, now);
        return true;
    }

    private static boolean consumePasswordResetRateLimit(RequestContext ctx, String email) {
        String emailKey = "password-reset-email:" + Objects.toString(email, "").trim().toLowerCase(Locale.ROOT);
        String ipKey = "password-reset-ip:" + ctx.clientIp();
        Instant now = Instant.now();
        if (!isRollingRateLimitAvailable(
                PASSWORD_RESET_RATE_LIMITS,
                emailKey,
                PASSWORD_RESET_RATE_LIMIT_MAX,
                PASSWORD_RESET_RATE_LIMIT_WINDOW,
                now
        )) {
            return false;
        }
        if (!isRollingRateLimitAvailable(
                PASSWORD_RESET_RATE_LIMITS,
                ipKey,
                PASSWORD_RESET_RATE_LIMIT_MAX,
                PASSWORD_RESET_RATE_LIMIT_WINDOW,
                now
        )) {
            return false;
        }
        recordRollingRateLimit(PASSWORD_RESET_RATE_LIMITS, emailKey, PASSWORD_RESET_RATE_LIMIT_WINDOW, now);
        recordRollingRateLimit(PASSWORD_RESET_RATE_LIMITS, ipKey, PASSWORD_RESET_RATE_LIMIT_WINDOW, now);
        return true;
    }

    private static boolean consumeImportRateLimit(RequestContext ctx, SessionStore.Session session) {
        String accountKey = Objects.toString(session.getUserId(), "").trim();
        String key = accountKey.isEmpty() ? "ip:" + ctx.clientIp() : "account:" + accountKey;
        Instant now = Instant.now();
        if (!isRollingRateLimitAvailable(
                IMPORT_RATE_LIMITS,
                key,
                IMPORT_RATE_LIMIT_MAX,
                IMPORT_RATE_LIMIT_WINDOW,
                now
        )) {
            return false;
        }
        recordRollingRateLimit(IMPORT_RATE_LIMITS, key, IMPORT_RATE_LIMIT_WINDOW, now);
        return true;
    }

    private static boolean consumeExportRateLimit(RequestContext ctx, SessionStore.Session session) {
        String accountKey = Objects.toString(session.getUserId(), "").trim();
        String key = accountKey.isEmpty() ? "ip:" + ctx.clientIp() : "account:" + accountKey;
        Instant now = Instant.now();
        if (!isRollingRateLimitAvailable(
                EXPORT_RATE_LIMITS,
                key,
                EXPORT_RATE_LIMIT_MAX,
                EXPORT_RATE_LIMIT_WINDOW,
                now
        )) {
            return false;
        }
        recordRollingRateLimit(EXPORT_RATE_LIMITS, key, EXPORT_RATE_LIMIT_WINDOW, now);
        return true;
    }

    private static boolean isRollingRateLimitAvailable(
            Map<String, Deque<Instant>> limits,
            String key,
            int maxAttempts,
            Duration window,
            Instant now
    ) {
        String safeKey = Objects.toString(key, "").trim();
        if (safeKey.isEmpty()) {
            return false;
        }
        Deque<Instant> attempts = limits.computeIfAbsent(safeKey, ignored -> new ArrayDeque<>());
        synchronized (attempts) {
            removeExpiredAttempts(attempts, window, now);
            return attempts.size() < maxAttempts;
        }
    }

    private static void recordRollingRateLimit(
            Map<String, Deque<Instant>> limits,
            String key,
            Duration window,
            Instant now
    ) {
        String safeKey = Objects.toString(key, "").trim();
        if (safeKey.isEmpty()) {
            return;
        }
        Deque<Instant> attempts = limits.computeIfAbsent(safeKey, ignored -> new ArrayDeque<>());
        synchronized (attempts) {
            removeExpiredAttempts(attempts, window, now);
            attempts.addLast(now);
        }
    }

    private static void removeExpiredAttempts(Deque<Instant> attempts, Duration window, Instant now) {
        Instant cutoff = now.minus(window);
        while (!attempts.isEmpty() && attempts.peekFirst().isBefore(cutoff)) {
            attempts.removeFirst();
        }
    }

    private static Map<String, Object> accountLockedPayload() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("error", "This account is locked after repeated failed login attempts.");
        payload.put("code", "account_locked");
        return payload;
    }

    private static String normalizeFeedbackType(String type) {
        String safeType = Objects.toString(type, "").trim().toLowerCase(Locale.ROOT);
        if ("bug-report".equals(safeType) || "bug_report".equals(safeType)) {
            return "bug";
        }
        if ("blocker/crash".equals(safeType) || "blocker-crash".equals(safeType) || "crash".equals(safeType)) {
            return "blocker";
        }
        if ("feedback".equals(safeType) || "bug".equals(safeType) || "blocker".equals(safeType)) {
            return safeType;
        }
        return "";
    }

    private static String normalizeFeedbackSeverity(String severity) {
        String safeSeverity = Objects.toString(severity, "").trim().toLowerCase(Locale.ROOT);
        if ("low".equals(safeSeverity)
                || "medium".equals(safeSeverity)
                || "high".equals(safeSeverity)
                || "critical".equals(safeSeverity)) {
            return safeSeverity;
        }
        return "medium";
    }

    private static String limitLength(String value, int maxLength) {
        String safeValue = Objects.toString(value, "");
        int safeLimit = Math.max(0, maxLength);
        if (safeValue.length() <= safeLimit) {
            return safeValue;
        }
        return safeValue.substring(0, safeLimit);
    }

    private static Path parentDirectory(Path path) {
        Path safePath = Objects.requireNonNullElseGet(path, () -> Path.of(""));
        Path parent = safePath.getParent();
        if (parent == null) {
            return Path.of(".");
        }
        return parent;
    }

    private static void probeWritableDirectory(String checkName, Path directory, List<String> failedChecks) {
        Path safeDirectory = Objects.requireNonNullElseGet(directory, () -> Path.of("."));
        try {
            Files.createDirectories(safeDirectory);
            Path probe = Files.createTempFile(safeDirectory, ".health-", ".tmp");
            Files.writeString(probe, "ok", StandardCharsets.UTF_8);
            Files.deleteIfExists(probe);
        } catch (IOException | RuntimeException e) {
            failedChecks.add(Objects.toString(checkName, "storage"));
        }
    }

    private static boolean prefersHtml(RequestContext ctx) {
        String accept = Objects.toString(ctx.header("Accept"), "").toLowerCase(Locale.ROOT);
        return accept.contains("text/html") && !accept.contains("application/json");
    }

    private static String healthHtml(boolean healthy, List<String> failedChecks) {
        String status = healthy ? "Healthy" : "Unhealthy";
        String statusClass = healthy ? "healthy" : "unhealthy";
        String checks = healthy
            ? "<p>Runtime storage checks passed.</p>"
            : "<p>Failed checks: " + escapeHtml(String.join(", ", failedChecks)) + "</p>";
        return """
            <!doctype html>
            <html lang="en">
            <head>
              <meta charset="utf-8">
              <meta name="viewport" content="width=device-width, initial-scale=1">
              <title>GMRules Health</title>
              <style>
                body { font-family: system-ui, sans-serif; margin: 40px; color: #1f2933; }
                .badge { display: inline-block; padding: 8px 12px; border-radius: 8px; font-weight: 700; }
                .healthy { background: #e7f6ec; color: #176b35; }
                .unhealthy { background: #fdecec; color: #9f1d1d; }
                code { background: #f3f4f6; padding: 2px 5px; border-radius: 4px; }
              </style>
            </head>
            <body>
              <h1>GMRules Health</h1>
              <p><span class="badge %s">%s</span></p>
              %s
              <p>Version: <code>closed-beta</code></p>
              <p>Timestamp: <code>%s</code></p>
            </body>
            </html>
            """.formatted(statusClass, status, checks, Instant.now().toString());
    }

    private static String getString(Map<String, Object> body, String key) {
        Object value = body.get(key);
        return Objects.toString(value, "");
    }

    private static int getInt(Map<String, Object> body, String key, int fallback) {
        Object value = body.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt(((String) value).trim());
            } catch (NumberFormatException ignored) {
                return fallback;
            }
        }
        return fallback;
    }

    private static boolean getBoolean(Map<String, Object> body, String key, boolean fallback) {
        Object value = body.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean(((String) value).trim());
        }
        return fallback;
    }

    private static double getDouble(Map<String, Object> body, String key, double fallback) {
        Object value = body.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                return Double.parseDouble(((String) value).trim());
            } catch (NumberFormatException ignored) {
                return fallback;
            }
        }
        return fallback;
    }

    private static List<String> getStringList(Map<String, Object> body, String key) {
        Object value = body.get(key);
        if (!(value instanceof List<?>)) {
            return List.of();
        }
        List<?> raw = (List<?>) value;
        List<String> result = new ArrayList<>();
        for (Object entry : raw) {
            String item = Objects.toString(entry, "").trim();
            if (!item.isEmpty()) {
                result.add(item);
            }
        }
        return result;
    }

    private static List<String> getSpellEffectNames(Spell spell) {
        List<String> resolved = new ArrayList<>();
        List<String> names = spell.getArray("effectNames");
        if (names != null && !names.isEmpty()) {
            for (String name : names) {
                String safeName = Objects.toString(name, "").trim();
                if (!safeName.isEmpty()) {
                    resolved.add(safeName);
                }
            }
            return resolved;
        }
        String primary = Objects.toString(spell.getEffect(), "").trim();
        if (!primary.isEmpty()) {
            resolved.add(primary);
        }
        String secondary = Objects.toString(spell.getSecondaryEffect(), "").trim();
        if (!secondary.isEmpty() && !resolved.contains(secondary)) {
            resolved.add(secondary);
        }
        return resolved;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> getMapList(Map<String, Object> body, String key) {
        Object value = body.get(key);
        if (!(value instanceof List)) {
            return List.of();
        }
        List<?> rawList = (List<?>) value;
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object entry : rawList) {
            if (entry instanceof Map) {
                result.add((Map<String, Object>) entry);
            }
        }
        return result;
    }

    private static List<String> safeList(List<String> values) {
        return values == null ? List.of() : values;
    }

    private static String resolveToken(RequestContext ctx) {
        String auth = ctx.header("Authorization");
        if (auth.isEmpty()) {
            return "";
        }
        String trimmed = auth.trim();
        if (trimmed.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return trimmed.substring(7).trim();
        }
        return trimmed;
    }

    private static String resolveDraftLocale(RequestContext ctx, String draftId) {
        String safeId = Objects.toString(draftId, "").trim();
        if (safeId.isEmpty()) {
            return "";
        }
        try {
            return ctx.getDraftStore().readDraft(safeId, Game::getUiLocale);
        } catch (IOException e) {
            return "";
        }
    }

    private static List<String> resolveCompletedStages(RequestContext ctx, String draftId) {
        String safeId = Objects.toString(draftId, "").trim();
        if (safeId.isEmpty()) {
            return List.of();
        }
        try {
            return ctx.getDraftStore().readDraft(safeId, Game::getCompletedStages);
        } catch (IOException e) {
            return List.of();
        }
    }

    private static void markStageCompleted(RequestContext ctx, String draftId, String stageKey) throws IOException {
        String safeId = Objects.toString(draftId, "").trim();
        if (safeId.isEmpty()) {
            return;
        }
        boolean alreadyCompleted = ctx.getDraftStore().readDraft(safeId, game -> {
            List<String> completed = game.getCompletedStages();
            return completed.contains(stageKey);
        });
        if (alreadyCompleted) {
            return;
        }
        ctx.getDraftStore().updateDraft(safeId, game -> game.markStageCompleted(stageKey));
    }

    private static String firstQueryParam(RequestContext ctx, String key) {
        List<String> values = ctx.queryParam(key);
        if (values.isEmpty()) {
            return "";
        }
        return Objects.toString(values.get(0), "");
    }

    private static String buildVerificationUrl(WebConfig config, String token) {
        String baseUrl = trimTrailingSlash(config.getPublicBaseUrl());
        String safeToken = URLEncoder.encode(Objects.toString(token, ""), StandardCharsets.UTF_8);
        return baseUrl + "/api/accounts/verify?token=" + safeToken;
    }

    private static String buildVerifiedPasswordUrl(WebConfig config, String email) {
        String baseUrl = trimTrailingSlash(config.getPublicBaseUrl());
        String safeEmail = URLEncoder.encode(Objects.toString(email, ""), StandardCharsets.UTF_8);
        return baseUrl + "/?verifiedEmail=" + safeEmail;
    }

    private static String buildResetPasswordUrl(WebConfig config, String email, String token) {
        String baseUrl = trimTrailingSlash(config.getPublicBaseUrl());
        String safeEmail = URLEncoder.encode(Objects.toString(email, ""), StandardCharsets.UTF_8);
        String safeToken = URLEncoder.encode(Objects.toString(token, ""), StandardCharsets.UTF_8);
        return baseUrl + "/?resetEmail=" + safeEmail + "&resetToken=" + safeToken;
    }

    private static String trimTrailingSlash(String value) {
        String safeValue = Objects.toString(value, "").trim();
        while (safeValue.endsWith("/")) {
            safeValue = safeValue.substring(0, safeValue.length() - 1);
        }
        return safeValue;
    }

    private static String escapeHtml(String value) {
        return Objects.toString(value, "")
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;");
    }

    private static String normalizeSkillCategoryKey(String value) {
        return Objects.toString(value, "").trim().toLowerCase();
    }

    private static String normalizeEffectTypeKey(String value) {
        return Objects.toString(value, "").trim().toLowerCase();
    }

    private static List<String> getWeightUnits(Game game) {
        String system = Objects.toString(game.getWeightSystem(), "").trim().toLowerCase();
        String arrayName = system.equals("english") ? "weightUnitsEnglish" : "weightUnitsMetric";
        List<String> units = game.getArray(arrayName);
        return units == null ? List.of() : new ArrayList<>(units);
    }

    @SuppressWarnings("unchecked")
    private static List<Attribute> getAttributes(Game game) {
        List<Attribute> attributes = game.<Attribute>getObjectArray("attributes");
        return attributes == null ? List.of() : attributes;
    }

    @SuppressWarnings("unchecked")
    private static List<Currency> getCurrencies(Game game) {
        List<Currency> currencies = game.<Currency>getObjectArray("currencies");
        return currencies == null ? List.of() : currencies;
    }

    @SuppressWarnings("unchecked")
    private static List<Effect> getEffects(Game game) {
        List<Effect> effects = game.<Effect>getObjectArray("effects");
        return effects == null ? List.of() : effects;
    }

    @SuppressWarnings("unchecked")
    private static List<Status> getStatuses(Game game) {
        List<Status> statuses = game.<Status>getObjectArray("statuses");
        return statuses == null ? List.of() : statuses;
    }

    @SuppressWarnings("unchecked")
    private static List<Equipment> getEquipment(Game game) {
        List<Equipment> equipment = game.<Equipment>getObjectArray("equipment");
        return equipment == null ? List.of() : equipment;
    }

    @SuppressWarnings("unchecked")
    private static List<Weapon> getWeapons(Game game) {
        List<Weapon> weapons = game.<Weapon>getObjectArray("weapons");
        return weapons == null ? List.of() : weapons;
    }

    @SuppressWarnings("unchecked")
    private static List<Armor> getArmor(Game game) {
        List<Armor> armor = game.<Armor>getObjectArray("armor");
        return armor == null ? List.of() : armor;
    }

    @SuppressWarnings("unchecked")
    private static List<CharacterClass> getClasses(Game game) {
        List<CharacterClass> classes = game.<CharacterClass>getObjectArray("characterClasses");
        return classes == null ? List.of() : classes;
    }

    @SuppressWarnings("unchecked")
    private static List<Skill> getSkills(Game game) {
        List<Skill> skills = game.<Skill>getObjectArray("skills");
        return skills == null ? List.of() : skills;
    }

    @SuppressWarnings("unchecked")
    private static List<Spell> getSpells(Game game) {
        List<Spell> spells = game.<Spell>getObjectArray("spells");
        return spells == null ? List.of() : spells;
    }

    @SuppressWarnings("unchecked")
    private static List<Pantheon> getPantheons(Game game) {
        List<Pantheon> pantheons = game.<Pantheon>getObjectArray("pantheons");
        return pantheons == null ? List.of() : pantheons;
    }

    @SuppressWarnings("unchecked")
    private static List<Deity> getDeities(Game game) {
        List<Deity> deities = game.<Deity>getObjectArray("deities");
        return deities == null ? List.of() : deities;
    }

    @SuppressWarnings("unchecked")
    private static List<Race> getRaces(Game game) {
        List<Race> races = game.<Race>getObjectArray("races");
        return races == null ? List.of() : races;
    }

    private static final class CharacterExport {

        // *** MEMBERS ***
        private final byte[] data;
        private final String filename;

        // *** CONSTRUCTORS ***
        private CharacterExport(byte[] data, String filename) {
            this.data = Objects.requireNonNullElseGet(data, () -> new byte[0]);
            this.filename = Objects.toString(filename, "character.gmcf");
        }

        // *** METHODS ***
        private byte[] getData() {
            return data.clone();
        }

        private String getFilename() {
            return filename;
        }
    }

    private static final class CharacterExportException extends RuntimeException {

        // *** MEMBERS ***
        private static final long serialVersionUID = 1L;

        // *** CONSTRUCTORS ***
        private CharacterExportException(Throwable cause) {
            super(cause);
        }
    }
}
