package io.igrant.igrant_org_sdk.models.OrgData;

public class DataRequest {

    private String ID;
    private String UserID;
    private String UserName;
    private String OrgID;
    private Integer Type;
    private String TypeStr;
    private Integer State;
    private String RequestedDate;
    private String ClosedDate;
    private String StateStr;
    private String Comment;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getOrgID() {
        return OrgID;
    }

    public void setOrgID(String orgID) {
        OrgID = orgID;
    }

    public Integer getType() {
        return Type;
    }

    public void setType(Integer type) {
        Type = type;
    }

    public String getTypeStr() {
        return TypeStr;
    }

    public void setTypeStr(String typeStr) {
        TypeStr = typeStr;
    }

    public Integer getState() {
        return State;
    }

    public void setState(Integer State) {
        this.State = State;
    }

    public String getRequestedDate() {
        return RequestedDate;
    }

    public void setRequestedDate(String requestedDate) {
        RequestedDate = requestedDate;
    }

    public String getClosedDate() {
        return ClosedDate;
    }

    public void setClosedDate(String closedDate) {
        ClosedDate = closedDate;
    }

    public String getStateStr() {
        return StateStr;
    }

    public void setStateStr(String stateStr) {
        StateStr = stateStr;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    private Boolean isOnGoing = false;

    public Boolean getOnGoing() {
        return isOnGoing;
    }

    public void setOnGoing(Boolean onGoing) {
        isOnGoing = onGoing;
    }
}
