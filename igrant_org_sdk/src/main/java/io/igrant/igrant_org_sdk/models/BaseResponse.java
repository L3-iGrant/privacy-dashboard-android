package io.igrant.igrant_org_sdk.models;

/**
 * Created by JMAM on 8/18/18.
 */

public class BaseResponse {
    private String msg;

    private String error_description;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }
}
