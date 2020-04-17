package io.igrant.igrant_org_sdk.models.Organizations;

public class Purpose {

    private String ID;
    private String Name;
    private String Description;
    private Boolean LawfulUsage;
    private String PolicyURL;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Description) {
        this.Name = Description;
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

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
