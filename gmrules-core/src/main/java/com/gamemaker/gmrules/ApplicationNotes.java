/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules;

public final class ApplicationNotes {

// *** MEMBERS ***

// *** CONSTRUCTORS ***
    private ApplicationNotes() {
    }

// *** METHODS ***
    // all constructors initialize array registry, array registries can never be null.
    // all array handling is passed to ArrayHandler instances in all classes the use more than two arrayLists
}
