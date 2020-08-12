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
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet checks if the currently logged in user exists in the database and saves a new user
 * in the database.
 */
@WebServlet("/user-creation-servlet")
public class UserCreationServlet extends HttpServlet {
  private final UserRepository userRepository;
  private final UserService userService;

  public UserCreationServlet() {
    userRepository = UserRepositoryFactory.getUserRepository(RepositoryType.DATASTORE);
    userService = UserServiceFactory.getUserService();
  }

  /** Saves the new user's data(id and email only, provided by the UserService) to the database. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    User user = getLoggedInUser();
    userRepository.saveUser(user);
    response.sendRedirect("/index.html");
  }

  /**
   * @return true if the currently logged in user is already present in the database, false
   *     otherwise.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    boolean existingUser = userRepository.existingUser(userService.getCurrentUser().getUserId());
    response.setContentType("application/json;");
    response.getWriter().println(convertToJsonUsingGson(Boolean.valueOf(existingUser)));
  }

  private User getLoggedInUser() {
    String id = userService.getCurrentUser().getUserId();
    String email = userService.getCurrentUser().getEmail();
    User.Builder newUserBuilder = new User.Builder(id, email);
    return newUserBuilder.build();
  }

  private String convertToJsonUsingGson(Object o) {
    Gson gson = new Gson();
    String json = gson.toJson(o);
    return json;
  }
}
