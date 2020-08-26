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
import com.google.sps.placeGuide.PlaceGuide;
import com.google.sps.placeGuideInfo.PlaceGuideInfo;
import com.google.sps.user.User;
import com.google.sps.user.repository.impl.DatastoreUserRepository;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class PlaceGuideInfoTest {
  // User parameters.
  private static final String CREATOR_USER_ID = "creatorUserid";
  private static final String CREATOR_EMAIL = "creatoruser@gmail.com";
  private static final String CREATOR_USER_NAME = "creatorUsername";
  private static final String CREATOR_SELF_INTRODUCTION = "I am the creator user";
  private static final String CREATOR_USER_IMG_KEY = "img1234Creator";

  private static final String OTHER_USER_ID = "otherUserid";
  private static final String OTHER_EMAIL = "otherUser@gmail.com";
  private static final String OTHER_USER_NAME = "otherUsername";
  private static final String OTHER_SELF_INTRODUCTION = "I am the other user";
  private static final String OTHER_USER_IMG_KEY = "img1234other";

  private static final long PLACEGUIDE_ID = 12345;
  private static final String PLACEGUIDE_NAME = "name";
  private static final String AUDIO_KEY = "audioKey";
  private static final String CREATOR_ID = CREATOR_USER_ID;
  private static final String PLACE_ID = "placeId";
  private static final GeoPt COORDINATE = new GeoPt((float) 3.14, (float) 2.56);
  private static final boolean IS_PUBLIC = true;
  private static final long LENGTH = 60L;
  private static final String DESCRIPTION = "description";
  private static final String IMAGE_KEY = "imageKey";

  private static final Set<Long> CREATOR_USER_BOOKMARKED_PLACEGUIDE_IDS =
      new HashSet<>(Arrays.asList(1L, 2L, 3L));
  private static final Set<Long> OTHER_USER_BOOKMARKED_PLACEGUIDE_IDS =
      new HashSet<Long>(Arrays.asList(1L, 2L, PLACEGUIDE_ID));

  private final User creatorUser =
      new User.Builder(CREATOR_USER_ID, CREATOR_EMAIL)
          .setName(CREATOR_USER_NAME)
          .addSelfIntroduction(CREATOR_SELF_INTRODUCTION)
          .addImgKey(CREATOR_USER_IMG_KEY)
          .setBookmarkedPlaceGuidesIds(CREATOR_USER_BOOKMARKED_PLACEGUIDE_IDS)
          .build();

  private final User otherUser =
      new User.Builder(OTHER_USER_ID, OTHER_EMAIL)
          .setName(OTHER_USER_NAME)
          .addSelfIntroduction(OTHER_SELF_INTRODUCTION)
          .addImgKey(OTHER_USER_IMG_KEY)
          .setBookmarkedPlaceGuidesIds(OTHER_USER_BOOKMARKED_PLACEGUIDE_IDS)
          .build();

  private final PlaceGuide toMatchPlaceGuide =
      new PlaceGuide.Builder(PLACEGUIDE_ID, PLACEGUIDE_NAME, AUDIO_KEY, CREATOR_ID, COORDINATE)
          .setPlaceId(PLACE_ID)
          .setPlaceGuideStatus(IS_PUBLIC)
          .setLength(LENGTH)
          .setDescription(DESCRIPTION)
          .setImageKey(IMAGE_KEY)
          .build();

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
  public void constructPlaceGuideInfo_matchesPlaceGuideWithUser() {
    saveUser(creatorUser);
    saveUser(otherUser);
    PlaceGuideInfo placeGuideInfo = new PlaceGuideInfo(toMatchPlaceGuide, OTHER_USER_ID);
    assertEquals(creatorUser, placeGuideInfo.getCreator());
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructPlaceGuideInfo_inexistentCreator_throwsIllegalArgumentException() {
    saveUser(otherUser);
    PlaceGuideInfo placeGuideInfo = new PlaceGuideInfo(toMatchPlaceGuide, OTHER_USER_ID);
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructPlaceGuideInfo_inexistentCurrentUser_throwsIllegalArgumentException() {
    saveUser(creatorUser);
    PlaceGuideInfo placeGuideInfo = new PlaceGuideInfo(toMatchPlaceGuide, OTHER_USER_ID);
  }

  @Test
  public void constructPlaceGuideInfo_currentUserIsCreator_createdByCurrentUserIsTrue() {
    saveUser(creatorUser);
    PlaceGuideInfo placeGuideInfo = new PlaceGuideInfo(toMatchPlaceGuide, CREATOR_USER_ID);
    assertEquals(true, placeGuideInfo.isCreatedByCurrentUser());
  }

  @Test
  public void constructPlaceGuideInfo_currentUserIsNotCreator_createdByCurrentUserIsFalse() {
    saveUser(creatorUser);
    saveUser(otherUser);
    PlaceGuideInfo placeGuideInfo = new PlaceGuideInfo(toMatchPlaceGuide, OTHER_USER_ID);
    assertEquals(false, placeGuideInfo.isCreatedByCurrentUser());
  }

  @Test
  public void constructPlaceGuideInfo_bookmarkedByCurrentUser_bookmarkedByCurrentUserIsTrue() {
    saveUser(creatorUser);
    saveUser(otherUser);
    PlaceGuideInfo placeGuideInfo = new PlaceGuideInfo(toMatchPlaceGuide, OTHER_USER_ID);
    assertEquals(true, placeGuideInfo.isBookmarkedByCurrentUser());
  }

  @Test
  public void constructPlaceGuideInfo_notBookmarkedByCurrentUser_bookmarkedByCurrentUserIsFalse() {
    saveUser(creatorUser);
    saveUser(otherUser);
    PlaceGuideInfo placeGuideInfo = new PlaceGuideInfo(toMatchPlaceGuide, CREATOR_USER_ID);
    assertEquals(false, placeGuideInfo.isBookmarkedByCurrentUser());
  }

  private void saveUser(User user) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(getUserEntity(user));
  }

  private Entity getUserEntity(User user) {
    Entity userEntity = new Entity(DatastoreUserRepository.ENTITY_KIND, user.getId());
    userEntity.setProperty(DatastoreUserRepository.NAME_PROPERTY, user.getName());
    userEntity.setProperty(DatastoreUserRepository.EMAIL_PROPERTY, user.getEmail());
    userEntity.setProperty(
        DatastoreUserRepository.BOOKMARKED_PLACE_GUIDES_IDS_PROPERTY,
        user.getBookmarkedPlaceGuidesIds());
    userEntity.setProperty(
        DatastoreUserRepository.PUBLIC_PORTFOLIO_PROPERTY, user.portfolioIsPublic());
    userEntity.setProperty(
        DatastoreUserRepository.SELF_INTRODUCTION_PROPERTY, user.getSelfIntroduction());
    userEntity.setProperty(DatastoreUserRepository.IMG_KEY_PROPERTY, user.getImgKey());
    return userEntity;
  }
}
