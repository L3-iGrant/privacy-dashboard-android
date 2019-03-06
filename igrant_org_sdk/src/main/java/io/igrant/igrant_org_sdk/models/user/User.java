package io.igrant.igrant_org_sdk.models.user;

/**
 * Created by JMAM on 8/16/18.
 */

public class User {

    private String ID;
    private String Name;
    private String IamID;
    private String Email;
    private String Phone;
    private String ImageID;
    private String ImageURL;
    private String LastVisit;
    private String RegToken;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getIamID() {
        return IamID;
    }

    public void setIamID(String IamID) {
        this.IamID = IamID;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    public String getImageID() {
        return ImageID;
    }

    public void setImageID(String ImageID) {
        this.ImageID = ImageID;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String ImageURL) {
        this.ImageURL = ImageURL;
    }

    public String getLastVisit() {
        return LastVisit;
    }

    public void setLastVisit(String LastVisit) {
        this.LastVisit = LastVisit;
    }

    public String getRegToken() {
        return RegToken;
    }

    public void setRegToken(String RegToken) {
        this.RegToken = RegToken;
    }

}
