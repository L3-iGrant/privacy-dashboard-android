package io.igrant.igrant_org_sdk.models.user;

/**
 * Created by JMAM on 8/16/18.
 */

public class Token {
    private String access_token;
    private Integer expires_in;
    private Integer refresh_expires_in;
    private String refresh_token;
    private long token_saved_time;
    private String token_type;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public Integer getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Integer expires_in) {
        this.expires_in = expires_in;
    }

    public Integer getRefresh_expires_in() {
        return refresh_expires_in;
    }

    public void setRefresh_expires_in(Integer refresh_expires_in) {
        this.refresh_expires_in = refresh_expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public long getToken_saved_time() {
        return token_saved_time;
    }

    public void setToken_saved_time(long token_saved_time) {
        this.token_saved_time = token_saved_time;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }
}
