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

import com.google.sps.user.User;
import com.google.sps.user.repository.UserRepository;
import com.google.sps.user.repository.UserRepositoryFactory;
import com.google.sps.data.RepositoryType;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;

/** This servlet handles users' data */
@WebServlet("/user-data")
public class UserServlet extends HttpServlet {
    public static final String NAME_INPUT = "name";
    public static final String PUBLIC_PORTFOLIO_INPUT = "publicPortfolio";
    public static final String PUBLIC_PORTFOLIO_INPUT_PUBLIC_VALUE = "public";
    public static final String SELF_INTRODUCTION_INPUT = "selfIntroduction";
    public static final String IMG_URL_INPUT = "imgUrl";

     /** Saves the recently submitted userdata(updates it if the user already has some data saved). */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException { 
        UserRepository myUserRepository = UserRepositoryFactory.getUserRepository(RepositoryType.DATASTORE);
        User user = getUserFromRequest(request);
        myUserRepository.saveUser(user);
        response.sendRedirect("/portfolio.html");
    }

    /** Returns the data of the user who is currently logged in. */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException { 
        UserRepository myUserRepository = UserRepositoryFactory.getUserRepository(RepositoryType.DATASTORE);
        User user = myUserRepository.getUser(getUserIdFromUserService());
        response.setContentType("application/json;");
        response.getWriter().println(convertToJsonUsingGson(user));
    }

    private String getUserIdFromUserService() {
        UserService userService = UserServiceFactory.getUserService();
        return userService.getCurrentUser().getUserId();
    }

    private String getUserEmailFromUserService() {
        UserService userService = UserServiceFactory.getUserService();
        return userService.getCurrentUser().getEmail();
    }

    private User getUserFromRequest(HttpServletRequest request) {
        String id = getUserIdFromUserService();
        String email = getUserEmailFromUserService();
        User.Builder newUserBuilder = new User.Builder(id, email);
        String name = request.getParameter(NAME_INPUT);
        if (!name.equals("")) {
            newUserBuilder.setName(name);
        }
        String selfIntroduction = request.getParameter(SELF_INTRODUCTION_INPUT);
        if (!selfIntroduction.equals("")) {
            newUserBuilder.addSelfIntroduction(selfIntroduction);
        }
        String publicPortfolioStringValue = request.getParameter(PUBLIC_PORTFOLIO_INPUT);
        if ( publicPortfolioStringValue.equals(PUBLIC_PORTFOLIO_INPUT_PUBLIC_VALUE)) {
            newUserBuilder.setPublicPortfolio();
        }
        return newUserBuilder.build();
    }

    private String convertToJsonUsingGson(Object o) {
        Gson gson = new Gson();
        String json = gson.toJson(o);
        return json;
    }
}