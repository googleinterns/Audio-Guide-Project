package com.google.sps.authentication;

import org.jetbrains.annotations.Nullable;
import java.lang.IllegalStateException;

/** Class containing user's authentication status. */
public class UserAuthenticationStatus {

    private final boolean isLoggedIn;

    @Nullable
    private final String loginUrl, logoutUrl;

    private UserAuthenticationStatus(boolean isLoggedIn, String loginUrl, String logoutUrl) {
        this.isLoggedIn = isLoggedIn;
        this.loginUrl = loginUrl;
        this.logoutUrl = logoutUrl;
    }

    public static class Builder {
        private final boolean isLoggedIn;
        private String loginUrl, logoutUrl;
        public Builder(boolean isLoggedIn) {
            this.isLoggedIn = isLoggedIn;
        }
        public Builder setLoginUrl(String loginUrl) {
            if (this.isLoggedIn) {
                throw new IllegalStateException(
                            "User is already logged in, failed to create a login url!"); 
            }
            this.loginUrl = loginUrl;
            return this;
        }
        public Builder setLogoutUrl(String logoutUrl) {
            if (!this.isLoggedIn) {
                throw new IllegalStateException(
                            "User is already logged out, failed to create a logout url!");
            }
            this.logoutUrl = logoutUrl;
            return this;
        }
        public UserAuthenticationStatus build() {
            return new UserAuthenticationStatus(isLoggedIn, loginUrl, logoutUrl);
        }
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    @Nullable
    public String getLoginUrl() {
        return loginUrl;
    }

    @Nullable
    public String getLogoutUrl() {
        return logoutUrl;
    }
}