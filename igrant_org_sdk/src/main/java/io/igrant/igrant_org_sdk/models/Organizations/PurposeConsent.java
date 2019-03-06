package io.igrant.igrant_org_sdk.models.Organizations;

public class PurposeConsent {

    private Purpose Purpose;
//    private Boolean isChecked = false;
    private Count Count;

    public Purpose getPurpose() {
        return Purpose;
    }

    public void setPurpose(Purpose Purpose) {
        this.Purpose = Purpose;
    }

//    public Boolean getChecked() {
//        return isChecked;
//    }
//
//    public void setChecked(Boolean checked) {
//        isChecked = checked;
//    }

    public Count getCount() {
        return Count;
    }

    public void setCount(Count Count) {
        this.Count = Count;
    }

}
