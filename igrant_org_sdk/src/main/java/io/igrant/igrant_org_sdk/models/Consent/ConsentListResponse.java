package io.igrant.igrant_org_sdk.models.Consent;

public class ConsentListResponse {
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getConsentID() {
        return ConsentID;
    }

    public void setConsentID(String consentID) {
        ConsentID = consentID;
    }

    public String getOrgID() {
        return OrgID;
    }

    public void setOrgID(String orgID) {
        OrgID = orgID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public Consents getConsents() {
        return Consents;
    }

    public void setConsents(Consents consents) {
        Consents = consents;
    }

    private String ID;
    private String ConsentID;
    private String OrgID;
    private String UserID;
    private Consents Consents;
}
