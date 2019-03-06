package io.igrant.igrant_org_sdk.listener;

import io.igrant.igrant_org_sdk.models.Organizations.PurposeConsent;

public interface UsagePurposeClickListener {

    void onItemClick(PurposeConsent consent);

    void onSetStatus(PurposeConsent consent, Boolean isChecked);
}
