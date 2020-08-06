package com.google.sps;

import com.google.gson.Gson;
import com.google.sps.authentication.UserAuthenticationStatus;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class UserAuthenticationStatusTest {

  private static final boolean LOGGED_IN = true;
  private static final boolean LOGGED_OUT = false;
  private static final String MOCK_URL = "mock_url";

  @Test
  public void isLoggedIn_userIsLoggedIn_returnTrue() {
    UserAuthenticationStatus status = new UserAuthenticationStatus(LOGGED_IN, MOCK_URL);
    assertEquals(LOGGED_IN, status.isLoggedIn());
  }

  @Test
  public void isLoggedIn_userIsLoggedOut_returnFalse() {
    UserAuthenticationStatus status = new UserAuthenticationStatus(LOGGED_OUT, MOCK_URL);
    assertEquals(LOGGED_OUT, status.isLoggedIn());
  }

  @Test
  public void getLoginUrl_userIsLoggedIn_returnNull() {
    UserAuthenticationStatus status = new UserAuthenticationStatus(LOGGED_IN, MOCK_URL);
    assertEquals(null, status.getLoginUrl());
  }

  @Test
  public void getLoginUrl_userIsLoggedOut_returnMockUrl() {
    UserAuthenticationStatus status = new UserAuthenticationStatus(LOGGED_OUT, MOCK_URL);
    assertEquals(MOCK_URL, status.getLoginUrl());
  }

  @Test
  public void getLogoutUrl_userIsLoggedIn_returnMockUrl() {
    UserAuthenticationStatus status = new UserAuthenticationStatus(LOGGED_IN, MOCK_URL);
    assertEquals(MOCK_URL, status.getLogoutUrl());
  }

  @Test
  public void getLogoutUrl_userIsLoggedOut_returnNull() {
    UserAuthenticationStatus status = new UserAuthenticationStatus(LOGGED_OUT, MOCK_URL);
    assertEquals(null, status.getLogoutUrl());
  }

}