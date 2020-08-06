// Copyright 2020 Google LLC

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at

//     https://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.RepositoryType;
import com.google.sps.user.User;
import com.google.sps.user.repository.UserRepository;
import com.google.sps.user.repository.UserRepositoryFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * This servlet handles users' data.
 */
@WebServlet("/user-data")
public class UserServlet extends HttpServlet {
  public static final String NAME_INPUT = "name";
  public static final String PUBLIC_PORTFOLIO_INPUT = "publicPortfolio";
  public static final String PUBLIC_PORTFOLIO_INPUT_PUBLIC_VALUE = "public";
  public static final String SELF_INTRODUCTION_INPUT = "selfIntroduction";
  public static final String IMG_URL_INPUT = "imgUrl";

  private final UserRepository userRepository;
  private final UserService userService;

  public UserServlet() {
    userRepository = UserRepositoryFactory.getUserRepository(RepositoryType.DATASTORE);
    userService = UserServiceFactory.getUserService();
  }

  /**
   * Saves the recently submitted userdata(updates it if the user already has some data saved).
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    User user = getUserFromRequest(request);
    userRepository.saveUser(user);
    response.sendRedirect("/index.html");
  }

  /**
   * Returns the data of the user who is currently logged in.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    User user = userRepository.getUser(userService.getCurrentUser().getUserId());
    response.setContentType("application/json;");
    response.getWriter().println(convertToJsonUsingGson(user));
  }

  private User getUserFromRequest(HttpServletRequest request) {
    String id = userService.getCurrentUser().getUserId();
    String email = userService.getCurrentUser().getEmail();
    User.Builder newUserBuilder = new User.Builder(id, email);
    String name = request.getParameter(NAME_INPUT);
    if (!name.isEmpty()) {
      newUserBuilder.setName(name);
    }
    String selfIntroduction = request.getParameter(SELF_INTRODUCTION_INPUT);
    if (!selfIntroduction.isEmpty()) {
      newUserBuilder.addSelfIntroduction(selfIntroduction);
    }
    String publicPortfolioStringValue = request.getParameter(PUBLIC_PORTFOLIO_INPUT);
    if (publicPortfolioStringValue.equals(PUBLIC_PORTFOLIO_INPUT_PUBLIC_VALUE)) {
      newUserBuilder.setPublicPortfolio(true); // False by default.
    }
    return newUserBuilder.build();
  }

  private String convertToJsonUsingGson(Object o) {
    Gson gson = new Gson();
    String json = gson.toJson(o);
    return json;
  }
}

