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

package com.google.sps;

import com.google.gson.Gson;
import com.google.sps.authentication.UserAuthenticationStatus;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.lang.IllegalStateException;

@RunWith(JUnit4.class)
public class UserAuthenticationStatusTest {

    private static final boolean LOGGED_IN = true;
    private static final boolean LOGGED_OUT = false;
    private static final String MOCK_URL = "mock_url";

    @Test
    public void isLoggedIn_userIsLoggedIn_returnTrue() {
        UserAuthenticationStatus status = new UserAuthenticationStatus.Builder(LOGGED_IN).build();
        assertEquals(true, status.isLoggedIn());
    }

    @Test
    public void isLoggedOut_userIsLoggedOut_returnFalse() {
        UserAuthenticationStatus status = new UserAuthenticationStatus.Builder(LOGGED_OUT).build();
        assertEquals(LOGGED_OUT, status.isLoggedIn());
    }

    @Test(expected = IllegalStateException.class)
    public void setLoginUrl_userAlreadyLoggedIn_throwIllegalStateException() {
        UserAuthenticationStatus status = new UserAuthenticationStatus
                                                        .Builder(LOGGED_IN)
                                                        .setLoginUrl(MOCK_URL)
                                                        .build();
    }

    @Test(expected = IllegalStateException.class)
    public void setLogoutUrl_userAlreadyLoggedOut_throwIllegalStateException() {
        UserAuthenticationStatus status = new UserAuthenticationStatus
                                                        .Builder(LOGGED_OUT)
                                                        .setLogoutUrl(MOCK_URL)
                                                        .build();
    }

    @Test
    public void getLoginUrl_userIsLoggedOut_returnMockUrl() {
        UserAuthenticationStatus status = new UserAuthenticationStatus
                                                .Builder(LOGGED_OUT)
                                                .setLoginUrl(MOCK_URL)
                                                .build();
        assertEquals(MOCK_URL, status.getLoginUrl());
    }

    @Test
    public void getLogoutUrl_userIsLoggedIn_returnMockUrl() {
        UserAuthenticationStatus status = new UserAuthenticationStatus
                                                .Builder(LOGGED_IN)
                                                .setLogoutUrl(MOCK_URL)
                                                .build();
        assertEquals(MOCK_URL, status.getLogoutUrl());
    }

}