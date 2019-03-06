package io.igrant.igrant_org_sdk.models.Organizations;

import io.igrant.igrant_org_sdk.models.Organization;

import java.util.ArrayList;

public class OrganizationDetailResponse {

    private Organization Organization;
    private String ConsentID;
    private ArrayList<PurposeConsent> PurposeConsents = null;

    public Organization getOrganization() {
        return Organization;
    }

    public void setOrganization(Organization Organization) {
        this.Organization = Organization;
    }

    public String getConsentID() {
        return ConsentID;
    }

    public void setConsentID(String ConsentID) {
        this.ConsentID = ConsentID;
    }

    public ArrayList<PurposeConsent> getPurposeConsents() {
        return PurposeConsents;
    }

    public void setPurposeConsents(ArrayList<PurposeConsent> PurposeConsents) {
        this.PurposeConsents = PurposeConsents;
    }

}
