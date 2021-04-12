package io.igrant.igrant_org_sdk.utils;

import io.igrant.igrant_org_sdk.models.Consent.Consent;
import io.igrant.igrant_org_sdk.models.anonymous.AnonymousUser;

public interface IgrantUserResponseHandler {
    void onSuccess(AnonymousUser user);
    void onCreationFailed();
}
