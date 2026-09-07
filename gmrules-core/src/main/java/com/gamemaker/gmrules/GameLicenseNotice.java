/*
 FILE CONTRACT (Non-Null): all fields remain non-null.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules;

import java.io.Serializable;
import java.util.Objects;

/** Structured license and attribution notice distributed with a Game. */
public final class GameLicenseNotice implements Serializable {
    private static final long serialVersionUID = 1L;

    private String licenseName = "";
    private String notice = "";
    private String upstreamAttribution = "";
    private String downstreamAttribution = "";
    private String reservedMaterial = "";
    private String licensedMaterial = "";

    public GameLicenseNotice() {
    }

    public GameLicenseNotice(GameLicenseNotice source) {
        GameLicenseNotice safe = Objects.requireNonNullElseGet(source, GameLicenseNotice::new);
        licenseName = safe.licenseName;
        notice = safe.notice;
        upstreamAttribution = safe.upstreamAttribution;
        downstreamAttribution = safe.downstreamAttribution;
        reservedMaterial = safe.reservedMaterial;
        licensedMaterial = safe.licensedMaterial;
    }

    public String getLicenseName() { return licenseName; }
    public void setLicenseName(String value) { licenseName = text(value); }
    public String getNotice() { return notice; }
    public void setNotice(String value) { notice = text(value); }
    public String getUpstreamAttribution() { return upstreamAttribution; }
    public void setUpstreamAttribution(String value) { upstreamAttribution = text(value); }
    public String getDownstreamAttribution() { return downstreamAttribution; }
    public void setDownstreamAttribution(String value) { downstreamAttribution = text(value); }
    public String getReservedMaterial() { return reservedMaterial; }
    public void setReservedMaterial(String value) { reservedMaterial = text(value); }
    public String getLicensedMaterial() { return licensedMaterial; }
    public void setLicensedMaterial(String value) { licensedMaterial = text(value); }

    private static String text(String value) { return Objects.toString(value, "").trim(); }
}
