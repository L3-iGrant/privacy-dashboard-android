package io.igrant.igrant_org_sdk.models.Login;

import io.igrant.igrant_org_sdk.models.user.Token;
import io.igrant.igrant_org_sdk.models.user.User;
/**
 * Created by JMAM on 8/16/18.
 */

public class LoginResponse {

    private User User;
    private Token Token;

    public User getUser() {
        return this.User;
    }

    public void setUser(User user) {
        this.User = user;
    }

    public Token getToken() {
        return this.Token;
    }

    public void setToken(Token token) {
        this.Token = token;
    }
}
