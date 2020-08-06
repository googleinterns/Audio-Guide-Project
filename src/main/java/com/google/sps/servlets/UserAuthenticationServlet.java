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
  * Check if user has already logged in using their Google account. User that has not logged in
  * will be prompted to the Google login form, once logged in, will be redirected to the current
  * page.
  */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String destinationUrl = request.getParameter("currentUrl");
    UserAuthenticationStatus status;
        
    if (userService.isUserLoggedIn()) {
      String logoutUrl = userService.createLogoutURL(destinationUrl);
      status = new UserAuthenticationStatus(true, logoutUrl);
    } else {
      String loginUrl = userService.createLoginURL(destinationUrl);
      status = new UserAuthenticationStatus(false, loginUrl);
    }
    sendResponse(response, status);
  }

  private void sendResponse(HttpServletResponse response, 
                            UserAuthenticationStatus status) throws IOException {
    response.setContentType("application/json;");
    response.getWriter().println(convertToJsonUsingGson(status));
  }

  private String convertToJsonUsingGson(UserAuthenticationStatus status) {
    Gson gson = new Gson();
    String json = gson.toJson(status);
    return json;
  }
}