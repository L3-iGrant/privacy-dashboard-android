package io.igrant.igrant_org_sdk.models;

/**
 * Created by JMAM on 8/30/18.
 */

public class ResultResponse {

    private Boolean Result;
    private String Message;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public Boolean getResult() {
        return Result;
    }

    public void setResult(Boolean result) {
        Result = result;
    }
}
