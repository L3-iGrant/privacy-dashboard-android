package io.igrant.igrant_org_sdk.listener;


import io.igrant.igrant_org_sdk.models.OrgData.DataRequest;

public interface OnUserRequestClickListener {
    void onRequestClick(DataRequest request);
    void onRequestCancel(DataRequest request);
}
