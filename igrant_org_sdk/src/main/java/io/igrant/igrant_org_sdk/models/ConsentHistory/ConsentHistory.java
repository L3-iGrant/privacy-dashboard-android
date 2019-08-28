package io.igrant.igrant_org_sdk.models.ConsentHistory;

public class ConsentHistory {

    private String ID;
    private String OrgID;
    private String PurposeID;
    private String Log;
    private String TimeStamp;

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

    public String getPurposeIDl() {
        return PurposeID;
    }

    public void setPurposeIDl(String purposeID) {
        PurposeID = purposeID;
    }

    public String getLog() {
        return Log;
    }

    public void setLog(String log) {
        Log = log;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }
}
