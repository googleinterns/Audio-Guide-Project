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

import com.google.appengine.api.datastore.*;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.RepositoryType;
import com.google.sps.user.User;
import com.google.sps.user.repository.UserRepository;
import com.google.sps.user.repository.UserRepositoryFactory;
import com.google.sps.user.repository.impl.DatastoreUserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class DatastoreUserRepositoryTest {
  private static final String ID = "userid";
  private static final String EMAIL = "user@gmail.com";
  private static final String NAME = "username";
  private static final String SELF_INTRODUCTION = "I am the user";
  private static final String IMG_KEY = "img1234";

  private final User toSaveUser =
      new User.Builder(ID, EMAIL)
          .setName(NAME)
          .addSelfIntroduction(SELF_INTRODUCTION)
          .addImgKey(IMG_KEY)
          .build();
  private User toGetUser;

  private final UserRepository myUserRepository =
      UserRepositoryFactory.getUserRepository(RepositoryType.DATASTORE);

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void getUser_existingUser_returnsUserEqualToSavedUser() {
    // Create entity to save.
    Entity userEntity = new Entity(DatastoreUserRepository.ENTITY_KIND, toSaveUser.getId());
    userEntity.setProperty(DatastoreUserRepository.NAME_PROPERTY, toSaveUser.getName());
    userEntity.setProperty(DatastoreUserRepository.EMAIL_PROPERTY, toSaveUser.getEmail());
    userEntity.setProperty(
        DatastoreUserRepository.PUBLIC_PORTFOLIO_PROPERTY, toSaveUser.portfolioIsPublic());
    userEntity.setProperty(
        DatastoreUserRepository.SELF_INTRODUCTION_PROPERTY, toSaveUser.getSelfIntroduction());
    userEntity.setProperty(DatastoreUserRepository.IMG_KEY_PROPERTY, toSaveUser.getImgKey());

    // Save entity to datastore.
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(userEntity);

    // Get user.
    toGetUser = myUserRepository.getUser(ID);
    assertEquals(toSaveUser, toGetUser);
  }

  @Test
  public void getUser_inexistentUser_returnsNull() {
    toGetUser = myUserRepository.getUser(ID);
    assertEquals(null, toGetUser);
  }

  @Test
  public void saveUser() {
    myUserRepository.saveUser(toSaveUser);

    // Get user.
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key userKey = KeyFactory.createKey(DatastoreUserRepository.ENTITY_KIND, ID);
    try {
      Entity userEntity = datastore.get(userKey);
      assertEquals(NAME, userEntity.getProperty(DatastoreUserRepository.NAME_PROPERTY));
      assertEquals(EMAIL, userEntity.getProperty(DatastoreUserRepository.EMAIL_PROPERTY));
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
  public void existingUser_userExists_returnsTrue() {
    // Create entity to save.
    Entity userEntity = new Entity(DatastoreUserRepository.ENTITY_KIND, toSaveUser.getId());
    userEntity.setProperty(DatastoreUserRepository.NAME_PROPERTY, toSaveUser.getName());
    userEntity.setProperty(DatastoreUserRepository.EMAIL_PROPERTY, toSaveUser.getEmail());
    userEntity.setProperty(
        DatastoreUserRepository.PUBLIC_PORTFOLIO_PROPERTY, toSaveUser.portfolioIsPublic());
    userEntity.setProperty(
        DatastoreUserRepository.SELF_INTRODUCTION_PROPERTY, toSaveUser.getSelfIntroduction());
    userEntity.setProperty(DatastoreUserRepository.IMG_KEY_PROPERTY, toSaveUser.getImgKey());

    // Save entity to datastore.
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(userEntity);

    // Check if user exists.
    boolean userExists = myUserRepository.existingUser(ID);
    assertTrue(userExists);
  }

  @Test
  public void existingUser_userDoesntExist_returnsFalse() {
    // Check if user exists.
    boolean userExists = myUserRepository.existingUser(ID);
    assertFalse(userExists);
  }
}
