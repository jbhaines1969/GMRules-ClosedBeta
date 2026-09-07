/*
 FILE CONTRACT (Non-Null): all fields remain non-null.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules;

import java.io.Serializable;
import java.util.Objects;

/** Core-owned diagnostic describing an incomplete or unsupported Game capability. */
public final class GameDiagnostic implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Severity { INFO, WARNING, ERROR }

    private final String code;
    private final Severity severity;
    private final String message;
    private final String elementType;
    private final String elementName;

    public GameDiagnostic(String code, Severity severity, String message) {
        this(code, severity, message, "", "");
    }

    public GameDiagnostic(String code, Severity severity, String message, String elementType, String elementName) {
        this.code = text(code);
        this.severity = Objects.requireNonNullElse(severity, Severity.WARNING);
        this.message = text(message);
        this.elementType = text(elementType);
        this.elementName = text(elementName);
    }

    public String getCode() { return code; }
    public Severity getSeverity() { return severity; }
    public String getMessage() { return message; }
    public String getElementType() { return elementType; }
    public String getElementName() { return elementName; }

    private static String text(String value) { return Objects.toString(value, "").trim(); }
}
