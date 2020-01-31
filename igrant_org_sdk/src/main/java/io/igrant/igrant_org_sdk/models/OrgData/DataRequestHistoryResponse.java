package io.igrant.igrant_org_sdk.models.OrgData;

import java.util.ArrayList;

public class DataRequestHistoryResponse {

    private ArrayList<DataRequest> DataRequests;

    public ArrayList<DataRequest> getDataRequests() {
        return DataRequests;
    }

    public void setDataRequests(ArrayList<DataRequest> dataRequests) {
        DataRequests = dataRequests;
    }

    private Boolean IsRequestsOngoing;

    public Boolean getRequestsOngoing() {
        return IsRequestsOngoing;
    }

    public void setRequestsOngoing(Boolean requestsOngoing) {
        IsRequestsOngoing = requestsOngoing;
    }

    private Boolean IsDataDownloadRequestOngoing;
    private Boolean IsDataDeleteRequestOngoing;

    public Boolean getDataDownloadRequestOngoing() {
        return IsDataDownloadRequestOngoing;
    }

    public void setDataDownloadRequestOngoing(Boolean dataDownloadRequestOngoing) {
        IsDataDownloadRequestOngoing = dataDownloadRequestOngoing;
    }

    public Boolean getDataDeleteRequestOngoing() {
        return IsDataDeleteRequestOngoing;
    }

    public void setDataDeleteRequestOngoing(Boolean dataDeleteRequestOngoing) {
        IsDataDeleteRequestOngoing = dataDeleteRequestOngoing;
    }
}
