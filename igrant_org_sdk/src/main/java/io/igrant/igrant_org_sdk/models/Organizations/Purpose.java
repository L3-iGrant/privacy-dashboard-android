package io.igrant.igrant_org_sdk.models.Organizations;

public class Purpose {

    private String ID;
    private String Description;
    private Boolean LawfulUsage;
    private String PolicyURL;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public Boolean getLawfulUsage() {
        return LawfulUsage;
    }

    public void setLawfulUsage(Boolean LawfulUsage) {
        this.LawfulUsage = LawfulUsage;
    }

    public String getPolicyURL() {
        return PolicyURL;
    }

    public void setPolicyURL(String PolicyURL) {
        this.PolicyURL = PolicyURL;
    }


}
