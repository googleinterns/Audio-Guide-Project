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
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class PlaceGuideWithUserPairTest {
  // User parameters.
  private static final String USER_ID = "userid";
  private static final String EMAIL = "user@gmail.com";
  private static final String USER_NAME = "username";
  private static final String SELF_INTRODUCTION = "I am the user";
  private static final String USER_IMG_KEY = "img1234";

  public static final long PLACEGUIDE_ID = 12345;
  public static final String PLACEGUIDE_NAME = "name";
  public static final String AUDIO_KEY = "audioKey";
  public static final String CREATOR_ID = USER_ID;
  public static final String PLACE_ID = "placeId";
  public static final GeoPt COORDINATE = new GeoPt((float) 3.14, (float) 2.56);
  public static final boolean IS_PUBLIC = true;
  public static final long LENGTH = new Long(60);
  public static final String DESCRIPTION = "description";
  public static final String IMAGE_KEY = "imageKey";

  private final User toSaveUser =
      new User.Builder(USER_ID, EMAIL)
          .setName(NAME)
          .addSelfIntroduction(SELF_INTRODUCTION)
          .addImgKey(USER_IMG_KEY)
          .build();

  private final PlaceGuide testPublicPlaceGuideA =
      new PlaceGuide.Builder(PLACEGUIDE_ID, PLACEGUIDE_NAME, AUDIO_KEY, CREATOR_ID, COORDINATE)
          .setPlaceId(PLACE_ID)
          .setPlaceGuideStatus(IS_PUBLIC)
          .setLength(LENGTH)
          .setDescription(DESCRIPTION)
          .setImageKey(IMAGE_KEY)
          .build();

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

  //   @Test
  //   public void saveUser() {
  //     myUserRepository.saveUser(toSaveUser);

  //     // Get user.
  //     DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  //     Key userKey = KeyFactory.createKey(DatastoreUserRepository.ENTITY_KIND, ID);
  //     try {
  //       Entity userEntity = datastore.get(userKey);
  //       assertEquals(NAME, userEntity.getProperty(DatastoreUserRepository.NAME_PROPERTY));
  //       assertEquals(EMAIL, userEntity.getProperty(DatastoreUserRepository.EMAIL_PROPERTY));
  //       assertEquals(
  //           SELF_INTRODUCTION,
  //           userEntity.getProperty(DatastoreUserRepository.SELF_INTRODUCTION_PROPERTY));
  //       assertEquals(
  //           false, userEntity.getProperty(DatastoreUserRepository.PUBLIC_PORTFOLIO_PROPERTY));
  //       assertEquals(IMG_KEY, userEntity.getProperty(DatastoreUserRepository.IMG_KEY_PROPERTY));
  //     } catch (EntityNotFoundException e) {
  //       fail("Entity not found: " + e);
  //     }
  //   }
}
