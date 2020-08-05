// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.authentication;

import org.jetbrains.annotations.Nullable;

/** Class containing user's authentication status. */
public class UserAuthenticationStatus {

    private final boolean isLoggedIn;

    @Nullable
    private final String loginUrl, logoutUrl;

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
            if (this.isLoggedOut) {
                throw new IllegalStateException(
                            "User is already logged out, failed to create a logout url!");
            }
            this.logoutUrl = logoutUrl;
            return this;
        }
        public UserAuthenticationStatus build() {
            return new UserAuthenticationStatus(this);
        }
    }

    public UserAuthenticationStatus(Builder Builder) {
        this.isLoggedIn = isLoggedIn;
        this.loginUrl = loginUrl;
        this.logoutUrl = logoutUrl;
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