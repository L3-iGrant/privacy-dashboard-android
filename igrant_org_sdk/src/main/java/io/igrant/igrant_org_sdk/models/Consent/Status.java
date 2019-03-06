package io.igrant.igrant_org_sdk.models.Consent;

public class Status {

    private String Consented;
    private String TimeStamp;
    private Integer Days;
    private Integer Remaining = 0;

    public String getConsented() {
        return Consented;
    }

    public void setConsented(String consented) {
        Consented = consented;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public Integer getDays() {
        return Days;
    }

    public void setDays(Integer days) {
        Days = days;
    }

    public Integer getRemaining() {
        return Remaining;
    }

    public void setRemaining(Integer remaining) {
        Remaining = remaining;
    }
}
