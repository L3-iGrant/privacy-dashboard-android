package io.igrant.igrant_org_sdk.models.Login;

/**
 * Created by JMAM on 8/20/18.
 */

public class RefreshTokenRequest {


    private String clientId;
    private String refreshToken;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
