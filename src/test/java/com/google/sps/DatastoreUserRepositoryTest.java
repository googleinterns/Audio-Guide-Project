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
import com.google.sps.placeGuide.repository.impl.DatastorePlaceGuideRepository;
import com.google.sps.placeGuide.PlaceGuide;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

@RunWith(JUnit4.class)
public final class DatastoreUserRepositoryTest {
  private static final String ID = "userid";
  private static final String EMAIL = "user@gmail.com";
  private static final Set<Long> BOOKMARKED_PLACE_GUIDES_IDS = new HashSet<>(Arrays.asList((long) 12345));
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

  private User toGetUser;

  public static final long A_PUBLIC_ID = 12345;
  public static final long B_PUBLIC_ID = 23456;
  public static final long A_PRIVATE_ID = 34567;
  public static final long B_PRIVATE_ID = 45678;
  public static final String PLACE_GUIDE_NAME = "placeGuideName";
  public static final String AUDIO_KEY = "audioKey";
  public static final String CREATOR_A_ID = "creatorA_Id";
  public static final String CREATOR_B_ID = "creatorB_Id";
  public static final String OTHER_USER_ID = "otherUserId";
  public static final String PLACE_ID = "placeId";
  public static final GeoPt COORDINATE = new GeoPt((float) 3.14, (float) 2.56);
  public static final boolean IS_PUBLIC = true;
  public static final long LENGTH = new Long(60);
  public static final String DESCRIPTION = "description";
  public static final String PREVIOUS_DESCRIPTION = "previous description";
  public static final String IMAGE_KEY = "imageKey";
  public static final String PLACE_NAME = "placeName";

  public static final String OTHER_USER_EMAIL = "otherUser@gmail.com";
  public static final Set<Long> OTHER_USER_BOOKMARKED_PLACE_GUIDES_IDS = new HashSet<>();
  public static final String CREATOR_A_EMAIL = "creatorA@gmail.com";
  public static final Set<Long> CREATOR_A_BOOKMARKED_PLACE_GUIDES_IDS = 
      new HashSet<>(Arrays.asList(A_PUBLIC_ID, B_PUBLIC_ID));

  private final User testUser = 
      new User.Builder(OTHER_USER_ID, OTHER_USER_EMAIL)
      .setBookmarkedPlaceGuidesIds(OTHER_USER_BOOKMARKED_PLACE_GUIDES_IDS)
      .build();

  private final User userA = 
      new User.Builder(CREATOR_A_ID, CREATOR_A_EMAIL)
      .setBookmarkedPlaceGuidesIds(CREATOR_A_BOOKMARKED_PLACE_GUIDES_IDS)
      .build();

  private final PlaceGuide testPublicPlaceGuideA = 
      new PlaceGuide.Builder(A_PUBLIC_ID, PLACE_GUIDE_NAME, AUDIO_KEY, CREATOR_A_ID, COORDINATE)
      .setPlaceId(PLACE_ID)
      .setPlaceGuideStatus(IS_PUBLIC)
      .setLength(LENGTH)
      .setDescription(DESCRIPTION)
      .setImageKey(IMAGE_KEY)
      .setPlaceName(PLACE_NAME)
      .build();

  private final PlaceGuide testPrivatePlaceGuideA = 
      new PlaceGuide.Builder(A_PRIVATE_ID, PLACE_GUIDE_NAME, AUDIO_KEY, CREATOR_A_ID, COORDINATE)
      .setPlaceId(PLACE_ID)
      .setLength(LENGTH)
      .setDescription(DESCRIPTION)
      .setImageKey(IMAGE_KEY)
      .setPlaceName(PLACE_NAME)
      .build();

  private final PlaceGuide testPublicPlaceGuideB = 
      new PlaceGuide.Builder(B_PUBLIC_ID, PLACE_GUIDE_NAME, AUDIO_KEY, CREATOR_B_ID, COORDINATE)
      .setPlaceId(PLACE_ID)
      .setPlaceGuideStatus(IS_PUBLIC)
      .setLength(LENGTH)
      .setDescription(DESCRIPTION)
      .setImageKey(IMAGE_KEY)
      .setPlaceName(PLACE_NAME)
      .build();

  private final PlaceGuide testPrivatePlaceGuideB = 
      new PlaceGuide.Builder(B_PRIVATE_ID, PLACE_GUIDE_NAME, AUDIO_KEY, CREATOR_B_ID, COORDINATE)
      .setPlaceId(PLACE_ID)
      .setLength(LENGTH)
      .setDescription(DESCRIPTION)
      .setImageKey(IMAGE_KEY)
      .setPlaceName(PLACE_NAME)
      .build();

  private void saveTestPlaceGuidesEntities(List<PlaceGuide> placeGuides) {
    for (PlaceGuide placeGuide : placeGuides) {
      datastore.put(getEntityFromPlaceGuide(placeGuide));
    }
  }

  private Entity getEntityFromPlaceGuide(PlaceGuide placeGuide) {
    Entity placeGuideEntity = 
        new Entity(DatastorePlaceGuideRepository.ENTITY_KIND, placeGuide.getId());
    placeGuideEntity.setProperty(
        DatastorePlaceGuideRepository.NAME_PROPERTY, placeGuide.getName());
    placeGuideEntity.setProperty(
        DatastorePlaceGuideRepository.AUDIO_KEY_PROPERTY, placeGuide.getAudioKey());
    placeGuideEntity.setProperty(
        DatastorePlaceGuideRepository.CREATOR_ID_PROPERTY, placeGuide.getCreatorId());
    placeGuideEntity.setProperty(
        DatastorePlaceGuideRepository.IS_PUBLIC_PROPERTY, placeGuide.isPublic());
    placeGuideEntity.setProperty(
        DatastorePlaceGuideRepository.PLACE_ID_PROPERTY, placeGuide.getPlaceId());
    placeGuideEntity.setProperty(
        DatastorePlaceGuideRepository.COORDINATE_PROPERTY, placeGuide.getCoordinate());
    placeGuideEntity.setProperty(
        DatastorePlaceGuideRepository.DESCRIPTION_PROPERTY, placeGuide.getDescription());
    placeGuideEntity.setProperty(
        DatastorePlaceGuideRepository.LENGTH_PROPERTY, placeGuide.getLength());
    placeGuideEntity.setProperty(
        DatastorePlaceGuideRepository.IMAGE_KEY_PROPERTY, placeGuide.getImageKey());
    placeGuideEntity.setProperty(
        DatastorePlaceGuideRepository.PLACE_NAME_PROPERTY, placeGuide.getPlaceName());
    return placeGuideEntity;
  }

  private final UserRepository myUserRepository =
      UserRepositoryFactory.getUserRepository(RepositoryType.DATASTORE);

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  
  private DatastoreService datastore;

  @Before
  public void setUp() {
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void getUser_existingUser_returnsUserEqualToSavedUser() {
    // Create entity to save.
    Entity userEntity = new Entity(DatastoreUserRepository.ENTITY_KIND, toSaveUser.getId());
    userEntity.setProperty(
        DatastoreUserRepository.NAME_PROPERTY, toSaveUser.getName());
    userEntity.setProperty(
        DatastoreUserRepository.EMAIL_PROPERTY, toSaveUser.getEmail());
    userEntity.setProperty(
        DatastoreUserRepository.BOOKMARKED_PLACE_GUIDES_IDS_PROPERTY, 
        toSaveUser.getBookmarkedPlaceGuidesIds());
    userEntity.setProperty(
        DatastoreUserRepository.PUBLIC_PORTFOLIO_PROPERTY, toSaveUser.portfolioIsPublic());
    userEntity.setProperty(
        DatastoreUserRepository.SELF_INTRODUCTION_PROPERTY, toSaveUser.getSelfIntroduction());
    userEntity.setProperty(
        DatastoreUserRepository.IMG_KEY_PROPERTY, toSaveUser.getImgKey());

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
      List<Long> resultList = 
          (ArrayList) userEntity.getProperty(
              DatastoreUserRepository.BOOKMARKED_PLACE_GUIDES_IDS_PROPERTY);
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
  public void existingUser_userExists_returnsTrue() {
    // Create entity to save.
    Entity userEntity = new Entity(DatastoreUserRepository.ENTITY_KIND, toSaveUser.getId());
    userEntity.setProperty(DatastoreUserRepository.NAME_PROPERTY, toSaveUser.getName());
    userEntity.setProperty(DatastoreUserRepository.EMAIL_PROPERTY, toSaveUser.getEmail());
    userEntity.setProperty(
        DatastoreUserRepository.BOOKMARKED_PLACE_GUIDES_IDS_PROPERTY, 
        toSaveUser.getBookmarkedPlaceGuidesIds());
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

  @Test
  public void bookmarkPlaceGuide_existingUserAndExistingPlaceGuide_bookmarkedPlaceGuidesContainsPlaceGuideId() {
    // Save testUser to datastore using userRepository.
    myUserRepository.saveUser(testUser);
    
    // Store place guide to datastore.
    List<PlaceGuide> testPlaceGuidesList = Arrays.asList(testPublicPlaceGuideA,
                                                         testPublicPlaceGuideB,
                                                         testPrivatePlaceGuideB,
                                                         testPrivatePlaceGuideA);
    saveTestPlaceGuidesEntities(testPlaceGuidesList);

    // testUser is bookmarking public place guide a. 
    myUserRepository.bookmarkPlaceGuide(A_PUBLIC_ID, OTHER_USER_ID);

    // Check whether the place guide is inside testUser's bookmarkedPlaceGuides list.
    Set<Long> expected = new HashSet<>(Arrays.asList(A_PUBLIC_ID));
    Set<Long> result = myUserRepository.getUser(OTHER_USER_ID).getBookmarkedPlaceGuidesIds();
    assertEquals(expected, result);
  }

  @Test(expected = IllegalStateException.class)
  public void bookmarkPlaceGuide_InexistentUserAndExistingPlaceGuide_throwsError() {
    // Store place guide to datastore.
    List<PlaceGuide> testPlaceGuidesList = Arrays.asList(testPublicPlaceGuideA,
                                                         testPublicPlaceGuideB,
                                                         testPrivatePlaceGuideB,
                                                         testPrivatePlaceGuideA);
    saveTestPlaceGuidesEntities(testPlaceGuidesList);

    // testUser is bookmarking public place guide a. 
    myUserRepository.bookmarkPlaceGuide(A_PUBLIC_ID, OTHER_USER_ID);
  }

  @Test(expected = IllegalStateException.class)
  public void removeBookmarkedPlaceGuide_userDoesntExist_throwsError() {
    myUserRepository.removeBookmarkedPlaceGuide(A_PUBLIC_ID, CREATOR_A_ID);
  }

  @Test
  public void removeBookmarkedPlaceGuide_userHasBookmarkedPlaceGuide_selectedPlaceGuideRemoved() {
    // Store user A.
    myUserRepository.saveUser(userA);

    myUserRepository.removeBookmarkedPlaceGuide(A_PUBLIC_ID, CREATOR_A_ID);
    User testUserA = myUserRepository.getUser(CREATOR_A_ID);
    Set<Long> expected = new HashSet<>(Arrays.asList(B_PUBLIC_ID));
    Set<Long> result = testUserA.getBookmarkedPlaceGuidesIds();
    assertEquals(expected, result);
  }
}
