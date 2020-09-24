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
import static org.mockito.Mockito.when;

import com.google.appengine.api.datastore.*;
import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import com.google.sps.placeGuide.PlaceGuide;
import com.google.sps.placeGuide.repository.impl.DatastorePlaceGuideRepository;
import com.google.sps.servlets.BookmarkPlaceGuideServlet;
import com.google.sps.user.User;
import com.google.sps.user.repository.impl.DatastoreUserRepository;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.Nullable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class BookmarkPlaceGuideServletTest {
  private Map<String, Object> attributeToValue = new HashMap<>();
  private LocalServiceTestHelper helper;

  private HttpServletRequest request;
  private HttpServletResponse response;
  private StringWriter sw;
  private PrintWriter pw;

  @Before
  public void setup() {
    helper =
        new LocalServiceTestHelper(
                new LocalDatastoreServiceTestConfig(), new LocalBlobstoreServiceTestConfig())
            .setEnvIsLoggedIn(true)
            .setEnvAuthDomain("localhost")
            .setEnvEmail(EMAIL);

    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
  }

  @Test
  public void doGet_bookmark_bookmarkingLimitNotExceeded_succesfulBookmarking()
      throws IOException, EntityNotFoundException {
    setupCurrentUserIdInHelper(ID_A);
    saveUser(userA);
    savePlaceGuidesWithIds(userA.getBookmarkedPlaceGuidesIds());
    savePlaceGuide(toBookmarkGuide);
    setupRequestandResponse("Bookmark", toBookmarkGuide.getId());
    BookmarkPlaceGuideServlet boookmarkPlaceGuideServlet = new BookmarkPlaceGuideServlet();
    boookmarkPlaceGuideServlet.doGet(request, response);
    pw.flush();
    Gson gson = new Gson();
    Boolean successfulBookmark = gson.fromJson(sw.toString(), Boolean.class);
    assertTrue(successfulBookmark);
    assertTrue(userHasBookmarkedThePlaceGuide(ID_A, toBookmarkGuide.getId()));
  }

  @Test
  public void doGet_bookmark_bookmarkingLimitExceeded_unsuccesfulBookmarking()
      throws IOException, EntityNotFoundException {
    setupCurrentUserIdInHelper(ID_B);
    saveUser(userB);
    savePlaceGuidesWithIds(userB.getBookmarkedPlaceGuidesIds());
    savePlaceGuide(toBookmarkGuide);
    setupRequestandResponse("Bookmark", toBookmarkGuide.getId());
    BookmarkPlaceGuideServlet boookmarkPlaceGuideServlet = new BookmarkPlaceGuideServlet();
    boookmarkPlaceGuideServlet.doGet(request, response);
    pw.flush();
    Gson gson = new Gson();
    Boolean successfulBookmark = gson.fromJson(sw.toString(), Boolean.class);
    assertFalse(successfulBookmark);
    assertFalse(userHasBookmarkedThePlaceGuide(ID_B, toBookmarkGuide.getId()));
  }

  /**
   * This test is for the case when the user had reached the limit before, but one of the guides
   * does not exist anymore.
   */
  @Test
  public void doGet_bookmark_bookmarkingLimitNotExceeded_succesfulBookmarking_2()
      throws IOException, EntityNotFoundException {
    setupCurrentUserIdInHelper(ID_D);
    saveUser(userD);
    savePlaceGuidesWithIds(userD.getBookmarkedPlaceGuidesIds());
    savePlaceGuide(toBookmarkGuide);
    deletePlaceGuide(25);
    setupRequestandResponse("Bookmark", toBookmarkGuide.getId());
    BookmarkPlaceGuideServlet boookmarkPlaceGuideServlet = new BookmarkPlaceGuideServlet();
    boookmarkPlaceGuideServlet.doGet(request, response);
    pw.flush();
    Gson gson = new Gson();
    Boolean successfulBookmark = gson.fromJson(sw.toString(), Boolean.class);
    assertTrue(successfulBookmark);
    assertTrue(userHasBookmarkedThePlaceGuide(ID_D, toBookmarkGuide.getId()));
  }

  @Test
  public void doGet_unbookmark_succesfulUnbookmarking()
      throws IOException, EntityNotFoundException {
    setupCurrentUserIdInHelper(ID_C);
    savePlaceGuidesWithIds(userC.getBookmarkedPlaceGuidesIds());
    saveUser(userC);
    savePlaceGuide(toBookmarkGuide);
    setupRequestandResponse("Unbookmark", toBookmarkGuide.getId());
    BookmarkPlaceGuideServlet boookmarkPlaceGuideServlet = new BookmarkPlaceGuideServlet();
    boookmarkPlaceGuideServlet.doGet(request, response);
    pw.flush();
    Gson gson = new Gson();
    Boolean successfulUnbookmark = gson.fromJson(sw.toString(), Boolean.class);
    assertTrue(successfulUnbookmark);
    assertFalse(userHasBookmarkedThePlaceGuide(ID_C, toBookmarkGuide.getId()));
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  private void setupCurrentUserIdInHelper(String userId) {
    attributeToValue.clear();
    attributeToValue.put("com.google.appengine.api.users.UserService.user_id_key", (Object) userId);
    helper.setEnvAttributes(attributeToValue);
    helper.setUp();
  }

  private void savePlaceGuidesWithIds(Set<Long> IDs) {
    for (Long id : IDs) {
      savePlaceGuide(new PlaceGuide.Builder(id, "", "", "", null).build());
    }
  }

  // Users data.
  private static final String ID_A = "useridA";
  private static final String ID_B = "useridB";
  private static final String ID_C = "useridC";
  private static final String ID_D = "useridD";
  private static final String EMAIL = "user@gmail.com";
  private static final Set<Long> EMPTY_BOOKMARKED_PLACE_GUIDES_IDS = null;
  private static final Set<Long> BOOKMARKED_PLACE_GUIDES_IDS_A =
      new HashSet<>(Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l, 10l));
  private static final Set<Long> BOOKMARKED_PLACE_GUIDES_IDS_B =
      new HashSet<>(
          Arrays.asList(
              1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l, 10l, 11l, 12l, 13l, 14l, 15l, 16l, 17l, 18l, 19l,
              20l, 21l, 22l, 23l, 24l, 25l));
  private static final Set<Long> BOOKMARKED_PLACE_GUIDES_IDS_C =
      new HashSet<>(
          Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l, 10l, 11l, 12l, 13l, 14l, 15l, 26l));
  private static final Set<Long> BOOKMARKED_PLACE_GUIDES_IDS_D =
      new HashSet<>(
          Arrays.asList(
              1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l, 10l, 11l, 12l, 13l, 14l, 15l, 16l, 17l, 18l, 19l,
              20l, 21l, 22l, 23l, 24l, 25l));
  private static final String NAME = "username";
  private static final String SELF_INTRODUCTION = "I am the user";
  private static final String IMG_KEY = "/img.com";

  private final User userA =
      new User.Builder(ID_A, EMAIL)
          .setBookmarkedPlaceGuidesIds(BOOKMARKED_PLACE_GUIDES_IDS_A)
          .setName(NAME)
          .setPublicPortfolio(true)
          .addSelfIntroduction(SELF_INTRODUCTION)
          .addImgKey(IMG_KEY)
          .build();

  private final User userB =
      new User.Builder(ID_B, EMAIL)
          .setBookmarkedPlaceGuidesIds(BOOKMARKED_PLACE_GUIDES_IDS_B)
          .setName(NAME)
          .setPublicPortfolio(true)
          .addSelfIntroduction(SELF_INTRODUCTION)
          .addImgKey(IMG_KEY)
          .build();

  private final User userC =
      new User.Builder(ID_C, EMAIL)
          .setBookmarkedPlaceGuidesIds(BOOKMARKED_PLACE_GUIDES_IDS_C)
          .setName(NAME)
          .setPublicPortfolio(true)
          .addSelfIntroduction(SELF_INTRODUCTION)
          .addImgKey(IMG_KEY)
          .build();

  private final User userD =
      new User.Builder(ID_D, EMAIL)
          .setBookmarkedPlaceGuidesIds(BOOKMARKED_PLACE_GUIDES_IDS_D)
          .setName(NAME)
          .setPublicPortfolio(true)
          .addSelfIntroduction(SELF_INTRODUCTION)
          .addImgKey(IMG_KEY)
          .build();

  // PlaceGuides data
  private static final long PLACEGUIDE_ID = 26;
  private static final String PLACEGUIDE_NAME = "name";
  private static final String AUDIO_KEY = "audioKey";
  private static final String PLACE_ID = "placeId";
  private static final GeoPt COORDINATE = new GeoPt((float) 3.14, (float) 2.56);
  private static final boolean IS_PUBLIC = true;
  private static final long LENGTH = 60L;
  private static final String DESCRIPTION = "description";
  private static final String IMAGE_KEY = "imageKey";

  private final PlaceGuide toBookmarkGuide =
      new PlaceGuide.Builder(PLACEGUIDE_ID, PLACEGUIDE_NAME, AUDIO_KEY, ID_A, COORDINATE)
          .setPlaceId(PLACE_ID)
          .setPlaceGuideStatus(IS_PUBLIC)
          .setLength(LENGTH)
          .setDescription(DESCRIPTION)
          .setImageKey(IMAGE_KEY)
          .build();

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

  private void savePlaceGuide(PlaceGuide placeGuide) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(getPlaceGuideEntity(placeGuide));
  }

  private void deletePlaceGuide(long placeGuideId) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key placeGuideEntityKey =
        KeyFactory.createKey(DatastorePlaceGuideRepository.ENTITY_KIND, placeGuideId);
    datastore.delete(placeGuideEntityKey);
  }

  private Entity getPlaceGuideEntity(PlaceGuide placeGuide) {
    Entity placeGuideEntity =
        new Entity(DatastorePlaceGuideRepository.ENTITY_KIND, placeGuide.getId());
    placeGuideEntity.setProperty(DatastorePlaceGuideRepository.NAME_PROPERTY, placeGuide.getName());
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
    return placeGuideEntity;
  }

  private void setupRequestandResponse(String requstType, long toBookmarkGuideId)
      throws IOException {
    when(request.getParameter(BookmarkPlaceGuideServlet.BOOKMARK_HANDLING_TYPE_PARAMETER))
        .thenReturn("BOOKMARK");
    when(request.getParameter(BookmarkPlaceGuideServlet.PLACE_GUIDE_ID_PARAMETER))
        .thenReturn(String.valueOf(toBookmarkGuideId));
    sw = new StringWriter();
    pw = new PrintWriter(sw);
    when(response.getWriter()).thenReturn(pw);
  }

  private boolean userHasBookmarkedThePlaceGuide(String userId, long placeGuideId)
      throws EntityNotFoundException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key userKey = KeyFactory.createKey(DatastoreUserRepository.ENTITY_KIND, userId);
    try {
      User user = getUserFromUserEntity(datastore.get(userKey));
      return user.getBookmarkedPlaceGuidesIds().contains(placeGuideId);
    } catch (EntityNotFoundException e) {
      throw new EntityNotFoundException(userKey);
    }
  }

  @Nullable
  private User getUserFromUserEntity(Entity userEntity) {
    if (userEntity == null) {
      return null;
    }
    String id = (String) userEntity.getKey().getName();
    String name = (String) userEntity.getProperty(DatastoreUserRepository.NAME_PROPERTY);
    String email = (String) userEntity.getProperty(DatastoreUserRepository.EMAIL_PROPERTY);
    List<Long> bookmarkedPlaceGuidesIdsList =
        (ArrayList)
            userEntity.getProperty(DatastoreUserRepository.BOOKMARKED_PLACE_GUIDES_IDS_PROPERTY);
    Set<Long> bookmarkedPlaceGuidesIds;
    if (bookmarkedPlaceGuidesIdsList == null) {
      bookmarkedPlaceGuidesIds = new HashSet<>();
    } else {
      bookmarkedPlaceGuidesIds = new HashSet<>(bookmarkedPlaceGuidesIdsList);
    }
    Boolean publicPortfolio =
        (Boolean) userEntity.getProperty(DatastoreUserRepository.PUBLIC_PORTFOLIO_PROPERTY);
    String selfIntroduction =
        (String) userEntity.getProperty(DatastoreUserRepository.SELF_INTRODUCTION_PROPERTY);
    String imgKey = (String) userEntity.getProperty(DatastoreUserRepository.IMG_KEY_PROPERTY);
    User.Builder newUserBuilder =
        new User.Builder(id, email)
            .setBookmarkedPlaceGuidesIds(bookmarkedPlaceGuidesIds)
            .setName(name)
            .addSelfIntroduction(selfIntroduction)
            .setPublicPortfolio(publicPortfolio)
            .addImgKey(imgKey);
    return newUserBuilder.build();
  }
}
