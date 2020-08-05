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
import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.servlets.UserAuthenticationServlet;
import com.google.sps.authentication.UserAuthenticationStatus;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.*;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@RunWith(JUnit4.class)
public class UserAuthenticationServletTest extends UserAuthenticationServlet{

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper();
    private HttpServletRequest request;
    private HttpServletResponse response;
    private UserService userService;
    private UserAuthenticationServlet userAuthenticationServlet;
    
    public void login(String username, String domain, boolean isAdmin) {
        helper.setEnvAuthDomain(domain);
        helper.setEnvEmail(username + "@" + domain);
        helper.setEnvIsLoggedIn(true);
        helper.setEnvIsAdmin(isAdmin);
    }

    @Before
    public void setUp() {
        helper.setUp();

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);

        when(request.getParameter("currentUrl")).thenReturn("/index.html");

        userService = UserServiceFactory.getUserService();
        userAuthenticationServlet = new UserAuthenticationServlet(userService);
    }

    @Test
    public void doGet_userLoggedOut_returnsFalseAndLoginUrl() throws IOException {

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        userAuthenticationServlet.doGet(request, response);
        String resultJson = stringWriter.toString();

        Gson gson = new Gson();
        UserAuthenticationStatus result = gson.fromJson(resultJson, UserAuthenticationStatus.class);
        String expectedLoginUrl = userService.createLoginURL("/index.html");
        String expectedLogoutUrl = null;
        assertEquals(false, result.isLoggedIn());
        assertEquals(expectedLoginUrl, result.getLoginUrl());
        assertEquals(expectedLogoutUrl, result.getLogoutUrl());
    }

    @Test
    public void doGet_userLoggedIn_returnsTrueAndLogoutUrl() throws IOException {

        login("denniswillie", "google.com", true);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        userAuthenticationServlet.doGet(request, response);
        String resultJson = stringWriter.toString();

        Gson gson = new Gson();
        UserAuthenticationStatus result = gson.fromJson(resultJson, UserAuthenticationStatus.class);
        String expectedLogoutUrl = userService.createLogoutURL("/index.html");
        String expectedLoginUrl = null;
        assertEquals(true, result.isLoggedIn());
        assertEquals(expectedLogoutUrl, result.getLogoutUrl());
        assertEquals(expectedLoginUrl, result.getLoginUrl());
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }
}