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

package com.google.sps;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import com.google.appengine.api.datastore.*;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.servlets.UserCreationServlet;
import com.google.sps.user.User;
import com.google.sps.user.repository.impl.DatastoreUserRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class UserCreationServletTest {
  private static final String ID = "userid";
  private static final String EMAIL = "user@gmail.com";
  private static final Set<Long> BOOKMARKED_PLACE_GUIDES_IDS =
      new HashSet<>(Arrays.asList((long) 12345));
  private static final Set<Long> EMPTY_BOOKMARKED_PLACE_GUIDES_IDS = null;
  private static final String NAME = "username";
  private static final String SELF_INTRODUCTION = "I am the user";
  private static final String IMG_KEY = "img1234";

  private final User toSaveUser =
      new User.Builder(ID, EMAIL)
          .setBookmarkedPlaceGuidesIds(BOOKMARKED_PLACE_GUIDES_IDS)
          .setName(NAME)
          .addSelfIntroduction(SELF_INTRODUCTION)
          .addImgKey(IMG_KEY)
          .build();

  private UserCreationServlet userCreationServlet;
  private Map<String, Object> attributeToValue = new HashMap<>();
  private LocalServiceTestHelper helper;

  private HttpServletRequest request;
  private HttpServletResponse response;

  @Before
  public void setup() {
    // Set the userdata that the Userservice will return.
    attributeToValue.put("com.google.appengine.api.users.UserService.user_id_key", (Object) ID);
    helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig())
            .setEnvIsLoggedIn(true)
            .setEnvAuthDomain("localhost")
            .setEnvEmail(EMAIL)
            .setEnvAttributes(attributeToValue);
    helper.setUp();

    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void doPost_exitingUser_doesntUpdate() throws IOException, ServletException {
    // Create entity to save.
    Entity userEntity = new Entity(DatastoreUserRepository.ENTITY_KIND, toSaveUser.getId());
    userEntity.setProperty(DatastoreUserRepository.EMAIL_PROPERTY, toSaveUser.getEmail());
    userEntity.setProperty(
        DatastoreUserRepository.BOOKMARKED_PLACE_GUIDES_IDS_PROPERTY,
        toSaveUser.getBookmarkedPlaceGuidesIds());
    userEntity.setProperty(DatastoreUserRepository.NAME_PROPERTY, toSaveUser.getName());
    userEntity.setProperty(
        DatastoreUserRepository.PUBLIC_PORTFOLIO_PROPERTY, toSaveUser.portfolioIsPublic());
    userEntity.setProperty(
        DatastoreUserRepository.SELF_INTRODUCTION_PROPERTY, toSaveUser.getSelfIntroduction());
    userEntity.setProperty(DatastoreUserRepository.IMG_KEY_PROPERTY, toSaveUser.getImgKey());
    // Save entity to datastore.
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(userEntity);

    // Post to servlet the same user, who is already present in the database.
    userCreationServlet = new UserCreationServlet();
    userCreationServlet.doPost(request, response);

    // Get the currenly logged in user's saved data.
    // Check that the previously saved userdata is not rewritten by the servlet.
    Key userKey = KeyFactory.createKey(DatastoreUserRepository.ENTITY_KIND, ID);
    try {
      userEntity = datastore.get(userKey);
      assertEquals(NAME, userEntity.getProperty(DatastoreUserRepository.NAME_PROPERTY));
      assertEquals(EMAIL, userEntity.getProperty(DatastoreUserRepository.EMAIL_PROPERTY));
      List<Long> resultList =
          (ArrayList)
              userEntity.getProperty(DatastoreUserRepository.BOOKMARKED_PLACE_GUIDES_IDS_PROPERTY);
      Set<Long> resultSet = new HashSet<>(resultList);
      assertEquals(BOOKMARKED_PLACE_GUIDES_IDS, resultSet);
      assertEquals(
          SELF_INTRODUCTION,
          userEntity.getProperty(DatastoreUserRepository.SELF_INTRODUCTION_PROPERTY));
      assertEquals(
          false, userEntity.getProperty(DatastoreUserRepository.PUBLIC_PORTFOLIO_PROPERTY));
      assertEquals(IMG_KEY, userEntity.getProperty(DatastoreUserRepository.IMG_KEY_PROPERTY));
    } catch (EntityNotFoundException e) {
      fail("Entity not found: " + e);
    }
  }

  @Test
  public void doPost_inexistentUser_savesNewUser() throws IOException, ServletException {
    // Save the new user.
    userCreationServlet = new UserCreationServlet();
    userCreationServlet.doPost(request, response);

    // Get the currenly logged in user's previously saved data
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key userKey = KeyFactory.createKey(DatastoreUserRepository.ENTITY_KIND, ID);
    try {
      Entity userEntity = datastore.get(userKey);
      assertEquals(EMAIL, userEntity.getProperty(DatastoreUserRepository.EMAIL_PROPERTY));
      assertEquals(
          EMPTY_BOOKMARKED_PLACE_GUIDES_IDS,
          userEntity.getProperty(DatastoreUserRepository.BOOKMARKED_PLACE_GUIDES_IDS_PROPERTY));
    } catch (EntityNotFoundException e) {
      fail("Entity not found: " + e);
    }
  }
}
