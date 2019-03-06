package io.igrant.igrant_org_sdk.models.Consent;

import io.igrant.igrant_org_sdk.models.Organizations.PurposeConsent;

import java.util.ArrayList;

public class UpdateConsentStatusResponse {

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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

    public ArrayList<PurposeConsent> getConsents() {
        return ConsentsAndPurposes;
    }

    public void setConsents(ArrayList<PurposeConsent> consents) {
        ConsentsAndPurposes = consents;
    }

    private String ID;
    private String OrgID;
    private String UserID;
    private ArrayList<PurposeConsent> ConsentsAndPurposes;

}
