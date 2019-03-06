package io.igrant.igrant_org_sdk.models.Consent;

public class ConsentStatusRequest {

    private String Consented;

    private int Days;

    public String getConsented() {
        return Consented;
    }

    public void setConsented(String consented) {
        Consented = consented;
    }

    public int getDays() {
        return Days;
    }

    public void setDays(int days) {
        Days = days;
    }
}
