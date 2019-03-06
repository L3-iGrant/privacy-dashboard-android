package io.igrant.igrant_org_sdk.models;

/**
 * Created by JMAM on 9/5/18.
 */

public class Organization {

    private String ID;
    private String Name;
    private String CoverImageURL = "";
    private String LogoImageURL = "";
    private String TypeID;
    private String Description = "";
    private String Location;
    private String Type;
    private String PolicyURL;
    private Boolean Subscribed = true;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCoverImageURL() {
        return CoverImageURL;
    }

    public void setCoverImageURL(String coverImageURL) {
        CoverImageURL = coverImageURL;
    }

    public String getLogoImageURL() {
        return LogoImageURL;
    }

    public void setLogoImageURL(String logoImageURL) {
        LogoImageURL = logoImageURL;
    }

    public String getTypeID() {
        return TypeID;
    }

    public void setTypeID(String typeID) {
        TypeID = typeID;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getPolicyURL() {
        return PolicyURL;
    }

    public void setPolicyURL(String policyURL) {
        PolicyURL = policyURL;
    }

    public Boolean getSubscribed() {
        return Subscribed;
    }

    public void setSubscribed(Boolean subscribed) {
        Subscribed = subscribed;
    }
}
