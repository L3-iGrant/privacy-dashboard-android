package io.igrant.igrant_org_sdk.models.OrgData;

public class DataRequestStatus {
    private Boolean RequestOngoing;
    private String ID;
    private int State;
    private String StateStr;
    private String RequestedDate;

    public Boolean getRequestOngoing() {
        return RequestOngoing;
    }

    public void setRequestOngoing(Boolean requestOngoing) {
        RequestOngoing = requestOngoing;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }

    public String getStateStr() {
        return StateStr;
    }

    public void setStateStr(String stateStr) {
        StateStr = stateStr;
    }

    public String getRequestedDate() {
        return RequestedDate;
    }

    public void setRequestedDate(String requestedDate) {
        RequestedDate = requestedDate;
    }
}
