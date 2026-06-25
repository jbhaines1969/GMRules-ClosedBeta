/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.UI;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Simple localization helper for UI strings.
 */
public final class Localization {

    // *** MEMBERS ***
    private static final String BUNDLE_NAME = "i18n/strings";
    private static Locale currentLocale = Locale.getDefault();
    private static ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, currentLocale);
    private static final String MISSING_PREFIX = "!";
    private static final String MISSING_SUFFIX = "!";

    // *** CONSTRUCTORS ***
    private Localization() {
    }

    // *** METHODS ***
    public static String get(String key) {
        String normalizedKey = key == null ? "" : key;
        try {
            return bundle.getString(normalizedKey);
        } catch (MissingResourceException e) {
            return MISSING_PREFIX + normalizedKey + MISSING_SUFFIX;
        }
    }

    public static String format(String key, Object... args) {
        String pattern = get(key);
        return MessageFormat.format(pattern, args);
    }

    public static void setLocale(Locale locale) {
        Locale resolvedLocale = locale == null ? Locale.getDefault() : locale;
        Locale.setDefault(resolvedLocale);
        currentLocale = resolvedLocale;
        bundle = ResourceBundle.getBundle(BUNDLE_NAME, currentLocale);
    }
}
