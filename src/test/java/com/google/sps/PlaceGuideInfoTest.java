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
import com.google.sps.placeGuide.PlaceGuide;
import com.google.sps.placeGuideInfo.PlaceGuideInfo;
import com.google.sps.user.User;
import com.google.sps.user.repository.UserRepository;
import com.google.sps.user.repository.UserRepositoryFactory;
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

  private static final String CURRENT_USER_ID = "currentUserid";
  private static final String CURRENT_EMAIL = "currentUser@gmail.com";
  private static final String CURRENT_USER_NAME = "currentUsername";
  private static final String CURRENT_SELF_INTRODUCTION = "I am the current user";
  private static final String CURRENT_USER_IMG_KEY = "img1234Current";

  public static final long PLACEGUIDE_ID = 12345;
  public static final String PLACEGUIDE_NAME = "name";
  public static final String AUDIO_KEY = "audioKey";
  public static final String CREATOR_ID = CREATOR_USER_ID;
  public static final String PLACE_ID = "placeId";
  public static final GeoPt COORDINATE = new GeoPt((float) 3.14, (float) 2.56);
  public static final boolean IS_PUBLIC = true;
  public static final long LENGTH = 60L;
  public static final String DESCRIPTION = "description";
  public static final String IMAGE_KEY = "imageKey";

  private final User creatorUser =
      new User.Builder(CREATOR_USER_ID, CREATOR_EMAIL)
          .setName(CREATOR_USER_NAME)
          .addSelfIntroduction(CREATOR_SELF_INTRODUCTION)
          .addImgKey(CREATOR_USER_IMG_KEY)
          .build();

  private final User currentUser =
      new User.Builder(CURRENT_USER_ID, CURRENT_EMAIL)
          .setName(CURRENT_USER_NAME)
          .addSelfIntroduction(CURRENT_SELF_INTRODUCTION)
          .addImgKey(CURRENT_USER_IMG_KEY)
          .build();

  private final PlaceGuide toMatchPlaceGuide =
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

  @Test
  public void constructPlaceGuideInfo_existingUser_matchesPlaceGuideWithUser() {
    myUserRepository.saveUser(creatorUser);
    myUserRepository.saveUser(currentUser);
    PlaceGuideInfo placeGuideInfo = new PlaceGuideInfo(toMatchPlaceGuide, CURRENT_USER_ID);
    assertEquals(creatorUser, placeGuideInfo.getCreator());
  }

  // Remark: this scenario should never occur in real-life.
  // The user's data will be saved the very first time when they access the website, so
  // there cannot exist PlaceGuides with inexistent creators.
  @Test
  public void constructPlaceGuideInfo_nonExistingUser_matchesPlaceGuideWithNullUser() {
    myUserRepository.saveUser(currentUser);
    PlaceGuideInfo placeGuideInfo = new PlaceGuideInfo(toMatchPlaceGuide, CURRENT_USER_ID);
    assertEquals(null, placeGuideInfo.getCreator());
  }
}
