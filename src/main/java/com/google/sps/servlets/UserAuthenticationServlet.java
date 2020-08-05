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

package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.authentication.UserAuthenticationStatus;

/** This class is a user-authentication servlet.  */
@WebServlet("/user-authentication")
public class UserAuthenticationServlet extends HttpServlet {

    private final UserService userService;

    // For testing.
    public UserAuthenticationServlet(UserService userService) {
        this.userService = userService;
    }

    // For production.
    public UserAuthenticationServlet() {
        userService = UserServiceFactory.getUserService();
    }

    /**
    * Check if user has already logged in using their Google account. User that has not logged in will
    * be prompted to the Google login form, once logged in, will be redirected to the current page.
    */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String destinationUrl = request.getParameter("currentUrl");

        if (userService.isUserLoggedIn()) {
            String logoutUrl = userService.createLogoutURL(destinationUrl);
            sendResponse(response, true, logoutUrl);
        } else {
            String loginUrl = userService.createLoginURL(destinationUrl);
            sendResponse(response, false, loginUrl);
        }
    }

    private void sendResponse(HttpServletResponse response, boolean isLoggedIn, String url) throws IOException {
        UserAuthenticationStatus.Builder statusBuilder = UserAuthenticationStatus.Builder(isLoggedIn);
        UserAuthenticationStatus status;
        if (isLoggedIn) {
            status = statusBuilder.setLogoutUrl(url).build();
        } else {
            status = statusBuilder.setLoginUrl(url).build();
        }
        response.setContentType("application/json;");
        response.getWriter().println(convertToJsonUsingGson(status));
    }

    private String convertToJsonUsingGson(UserAuthenticationStatus status) {
        Gson gson = new Gson();
        String json = gson.toJson(status);
        return json;
    }
}