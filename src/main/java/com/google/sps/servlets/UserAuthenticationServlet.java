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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** This class is a user-authentication servlet.  */
@WebServlet("/user-authentication")
public class UserAuthenticationServlet extends HttpServlet {

    /**
    * Check if user has already logged in using their Google account. User that has not logged in will
    * be prompted to the Google login form, once logged in, will be redirected to the portfolio page.
    */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (userService.isUserLoggedIn()) {
            response.sendRedirect("/portfolio.html");
        } 
        else {
            // Use portfolio.html as a destination URL when user have logged in.
            String loginURL = userService.createLoginURL("/portfolio.html");
            response.sendRedirect(loginURL);
        }
    }
}