package io.igrant.igrant_org_sdk.models.Consent;

import io.igrant.igrant_org_sdk.models.Organizations.Purpose;
import io.igrant.igrant_org_sdk.models.Organizations.Count;
import java.util.ArrayList;

public class Consents {

    private Purpose Purpose;
    private Count Count;
    private ArrayList<Consent> Consents = null;

    public Purpose getPurpose() {
        return Purpose;
    }

    public void setPurpose(Purpose Purpose) {
        this.Purpose = Purpose;
    }

    public Count getCount() {
        return Count;
    }

    public void setCount(Count Count) {
        this.Count = Count;
    }

    public ArrayList<Consent> getConsents() {
        return Consents;
    }

    public void setConsents(ArrayList<Consent> Consents) {
        this.Consents = Consents;
    }
}
