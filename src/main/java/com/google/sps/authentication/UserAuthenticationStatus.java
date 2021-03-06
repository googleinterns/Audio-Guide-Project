package com.google.sps.authentication;

import org.jetbrains.annotations.Nullable;

/** Class containing user's authentication status. */
public class UserAuthenticationStatus {
  
  private final boolean isLoggedIn;

  @Nullable
  private final String logoutUrl;
  
  @Nullable
  private final String loginUrl;

  /**
  * @param url is a login URL when the user is logged in and logout URL when the user is 
  * logged out.
  */
  public UserAuthenticationStatus(boolean isLoggedIn, String url) {
    this.isLoggedIn = isLoggedIn;
    if (isLoggedIn) {
      logoutUrl = url;
      loginUrl = null;
    } else {
      loginUrl = url;
      logoutUrl = null;
    }
  }

  public String getLogoutUrl() {
    return logoutUrl;
  }

  public String getLoginUrl() {
    return loginUrl;
  }

  public boolean isLoggedIn() {
    return isLoggedIn;
  }
}