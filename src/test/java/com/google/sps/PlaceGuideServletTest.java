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

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.datastore.*;
import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.sps.data.PlaceGuideQueryType;
import com.google.sps.placeGuide.PlaceGuide;
import com.google.sps.placeGuide.repository.impl.DatastorePlaceGuideRepository;
import com.google.sps.placeGuideInfo.PlaceGuideInfo;
import com.google.sps.servlets.PlaceGuideServlet;
import com.google.sps.user.User;
import com.google.sps.user.repository.impl.DatastoreUserRepository;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class PlaceGuideServletTest {
  private HttpServletRequest request;
  private HttpServletResponse response;
  private BlobstoreService blobstoreService;
  private BlobInfoFactory blobInfoFactory;
  private LocalServiceTestHelper helper;
  private DatastoreService datastore;
  private StringWriter stringWriter;
  private PrintWriter printWriter;

  // Creator C data.
  private static final String ID_USER_C = "idUserC";
  private static final String EMAIL_USER_C = "emailUserC";
  private static final String NAME_USER_C = "nameUserC";
  private static final boolean PUBLIC_PORTFOLIO_USER_C = true;
  private static final String SELF_INTRODUCTION_USER_C = "selfIntroductionUserC";
  private static final String IMG_KEY_USER_C = "imgKeyUserC";

  private final User userC =
      new User.Builder(ID_USER_C, EMAIL_USER_C)
          .setBookmarkedPlaceGuidesIds(Collections.emptySet())
          .setName(NAME_USER_C)
          .setPublicPortfolio(true)
          .addSelfIntroduction(SELF_INTRODUCTION_USER_C)
          .addImgKey(IMG_KEY_USER_C)
          .build();

  // Creator D data.
  private static final String ID_USER_D = "idUserD";
  private static final String EMAIL_USER_D = "emailUserD";
  private static final String NAME_USER_D = "nameUserD";
  private static final boolean PUBLIC_PORTFOLIO_USER_D = true;
  private static final String SELF_INTRODUCTION_USER_D = "selfIntroductionUserD";
  private static final String IMG_KEY_USER_D = "imgKeyUserD";

  private final User userD =
      new User.Builder(ID_USER_D, EMAIL_USER_D)
          .setBookmarkedPlaceGuidesIds(Collections.emptySet())
          .setName(NAME_USER_D)
          .setPublicPortfolio(true)
          .addSelfIntroduction(SELF_INTRODUCTION_USER_D)
          .addImgKey(IMG_KEY_USER_D)
          .build();

  // General placeguide data(used for all placeguides).
  private static final String NAME = "name";
  private static final String AUDIO_KEY = "audioKey";
  private static final String PLACE_ID = "placeId";
  private static final boolean IS_PUBLIC = true;
  private static final long LENGTH = new Long(60);
  private static final String DESCRIPTION = "description";
  private static final String IMAGE_KEY = "imageKey";

  // PlaceGuides' parameters used for map-related queries.
  // PlaceGuides of user C.
  private static final long C_INNER_PUBLIC_ID = 56789;
  private static final long C_INNER_PRIVATE_ID = 98765;
  private static final long C_OUTER_PUBLIC_ID = 67890;
  private static final long C_OUTER_PRIVATE_ID = 9876;
  private static final String CREATOR_C_ID = ID_USER_C;
  private static final GeoPt C_INNER_PUBLIC_COORDINATE = new GeoPt(10f, -5f);
  private static final GeoPt C_INNER_PRIVATE_COORDINATE = new GeoPt(-14f, 14f);
  private static final GeoPt C_OUTER_PUBLIC_COORDINATE = new GeoPt(5f, -20f);
  private static final GeoPt C_OUTER_PRIVATE_COORDINATE = new GeoPt(-30f, -5f);
  // PlaceGuides of user D.
  private static final long D_INNER_PUBLIC_ID = 567890;
  private static final long D_INNER_PRIVATE_ID = 987650;
  private static final long D_OUTER_PUBLIC_ID = 678900;
  private static final long D_OUTER_PRIVATE_ID = 98760;
  private static final String CREATOR_D_ID = ID_USER_D;
  private static final GeoPt D_INNER_PUBLIC_COORDINATE = new GeoPt(10f, 5f);
  private static final GeoPt D_INNER_PRIVATE_COORDINATE = new GeoPt(-14f, 14f);
  private static final GeoPt D_OUTER_PUBLIC_COORDINATE = new GeoPt(60f, 10f);
  private static final GeoPt D_OUTER_PRIVATE_COORDINATE = new GeoPt(-10f, -45f);
  // Corners of the rectangle for the queried map area.
  // This is a map area which doesn't cross the IDL.
  private static final GeoPt NORTH_EAST_CORNER = new GeoPt(15f, 15f);
  private static final GeoPt SOUTH_WEST_CORNER = new GeoPt(-15f, -15f);

  private final PlaceGuide testInnerPublicPlaceGuideC =
      new PlaceGuide.Builder(
              C_INNER_PUBLIC_ID, NAME, AUDIO_KEY, CREATOR_C_ID, C_INNER_PUBLIC_COORDINATE)
          .setPlaceId(PLACE_ID)
          .setPlaceGuideStatus(IS_PUBLIC)
          .setLength(LENGTH)
          .setDescription(DESCRIPTION)
          .setImageKey(IMAGE_KEY)
          .build();

  private final PlaceGuide testInnerPrivatePlaceGuideC =
      new PlaceGuide.Builder(
              C_INNER_PRIVATE_ID, NAME, AUDIO_KEY, CREATOR_C_ID, C_INNER_PRIVATE_COORDINATE)
          .setPlaceId(PLACE_ID)
          .setLength(LENGTH)
          .setDescription(DESCRIPTION)
          .setImageKey(IMAGE_KEY)
          .build();

  private final PlaceGuide testOuterPublicPlaceGuideC =
      new PlaceGuide.Builder(
              C_OUTER_PUBLIC_ID, NAME, AUDIO_KEY, CREATOR_C_ID, C_OUTER_PUBLIC_COORDINATE)
          .setPlaceId(PLACE_ID)
          .setPlaceGuideStatus(IS_PUBLIC)
          .setLength(LENGTH)
          .setDescription(DESCRIPTION)
          .setImageKey(IMAGE_KEY)
          .build();

  private final PlaceGuide testOuterPrivatePlaceGuideC =
      new PlaceGuide.Builder(
              C_OUTER_PRIVATE_ID, NAME, AUDIO_KEY, CREATOR_C_ID, C_OUTER_PUBLIC_COORDINATE)
          .setPlaceId(PLACE_ID)
          .setLength(LENGTH)
          .setDescription(DESCRIPTION)
          .setImageKey(IMAGE_KEY)
          .build();

  private final PlaceGuide testInnerPublicPlaceGuideD =
      new PlaceGuide.Builder(
              D_INNER_PUBLIC_ID, NAME, AUDIO_KEY, CREATOR_D_ID, D_INNER_PUBLIC_COORDINATE)
          .setPlaceId(PLACE_ID)
          .setPlaceGuideStatus(IS_PUBLIC)
          .setLength(LENGTH)
          .setDescription(DESCRIPTION)
          .setImageKey(IMAGE_KEY)
          .build();

  private final PlaceGuide testInnerPrivatePlaceGuideD =
      new PlaceGuide.Builder(
              D_INNER_PRIVATE_ID, NAME, AUDIO_KEY, CREATOR_D_ID, D_INNER_PRIVATE_COORDINATE)
          .setPlaceId(PLACE_ID)
          .setLength(LENGTH)
          .setDescription(DESCRIPTION)
          .setImageKey(IMAGE_KEY)
          .build();

  private final PlaceGuide testOuterPublicPlaceGuideD =
      new PlaceGuide.Builder(
              D_OUTER_PUBLIC_ID, NAME, AUDIO_KEY, CREATOR_D_ID, D_OUTER_PUBLIC_COORDINATE)
          .setPlaceId(PLACE_ID)
          .setPlaceGuideStatus(IS_PUBLIC)
          .setLength(LENGTH)
          .setDescription(DESCRIPTION)
          .setImageKey(IMAGE_KEY)
          .build();

  private final PlaceGuide testOuterPrivatePlaceGuideD =
      new PlaceGuide.Builder(
              D_OUTER_PRIVATE_ID, NAME, AUDIO_KEY, CREATOR_D_ID, D_OUTER_PUBLIC_COORDINATE)
          .setPlaceId(PLACE_ID)
          .setLength(LENGTH)
          .setDescription(DESCRIPTION)
          .setImageKey(IMAGE_KEY)
          .build();

  private void saveTestPlaceGuidesEntities(List<PlaceGuide> placeGuides) {
    for (PlaceGuide placeGuide : placeGuides) {
      datastore.put(getEntityFromPlaceGuide(placeGuide));
    }
  }

  private Entity getEntityFromPlaceGuide(PlaceGuide placeGuide) {
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

  private void saveUser(User user) {
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

  /**
   * This string is passed as an http request parameter. The format is the same as for
   * LatLngBound.tourlValue() from the Maps Javascript API.
   */
  private String getRegionCornersString(GeoPt southWestCorner, GeoPt northEastCorner) {
    return String.format(
        "%f,%f,%f,%f",
        southWestCorner.getLatitude(),
        southWestCorner.getLongitude(),
        northEastCorner.getLatitude(),
        northEastCorner.getLongitude());
  }

  /** It sets the values of the request parameters. */
  private void setupDoGetMockRequest(
      PlaceGuideQueryType queryType, GeoPt southWestCorner, GeoPt northEastCorner)
      throws IOException {
    when(request.getParameter(PlaceGuideServlet.PLACE_GUIDE_QUERY_TYPE_PARAMETER))
        .thenReturn(queryType.toString());
    when(request.getParameter(PlaceGuideServlet.REGION_CORNERS_PARAMETER))
        .thenReturn(getRegionCornersString(southWestCorner, northEastCorner));
    stringWriter = new StringWriter();
    printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
  }

  private boolean placeGuideInfoEquals(PlaceGuideInfo a, PlaceGuideInfo b) {
    return a.getPlaceGuide().getId() == b.getPlaceGuide().getId()
        && a.getCreator().equals(b.getCreator())
        && a.isCreatedByCurrentUser() == b.isCreatedByCurrentUser()
        && a.isBookmarkedByCurrentUser() == b.isBookmarkedByCurrentUser();
  }

  // Find out if the 2 lists of placeguideinfos are equal.
  private boolean compare(List<PlaceGuideInfo> a, List<PlaceGuideInfo> b) {
    List<PlaceGuideInfo> b_copy = new ArrayList<>(b);
    if (a.size() != b_copy.size()) {
      return false;
    }
    for (PlaceGuideInfo a_pg_info : a) {
      boolean hasEqual = false;
      int index_b_copy = 0;
      while (index_b_copy < b_copy.size()) {
        if (placeGuideInfoEquals(a_pg_info, b_copy.get(index_b_copy))) {
          hasEqual = true;
          b_copy.remove(index_b_copy);
          break;
        }
        index_b_copy++;
      }
      if (!hasEqual) {
        return false;
      }
    }
    return true;
  }

  @Before
  public void setup() {
    // Set the userdata that the Userservice will return.
    Map<String, Object> attributeToValue = new HashMap<>();
    attributeToValue.put(
        "com.google.appengine.api.users.UserService.user_id_key", (Object) ID_USER_C);
    helper =
        new LocalServiceTestHelper(
                new LocalDatastoreServiceTestConfig(), new LocalBlobstoreServiceTestConfig())
            .setEnvIsLoggedIn(true)
            .setEnvAuthDomain("localhost")
            .setEnvEmail(EMAIL_USER_C)
            .setEnvAttributes(attributeToValue);
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();

    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    blobstoreService = mock(BlobstoreService.class);
    blobInfoFactory = mock(BlobInfoFactory.class);
  }

  @Test
  public void doGet_getPlaceGuide_currentUserOwnsGuide() throws IOException {
    saveUser(userC);
    datastore.put(getEntityFromPlaceGuide(testInnerPublicPlaceGuideC));
    setupDoGetMockRequest(
        PlaceGuideQueryType.PLACE_GUIDE_WITH_ID, SOUTH_WEST_CORNER, NORTH_EAST_CORNER);
    when(request.getParameter(PlaceGuideServlet.PLACE_GUIDE_ID_PARAMETER))
        .thenReturn(String.valueOf(testInnerPublicPlaceGuideC.getId()));
    PlaceGuideServlet placeGuideServlet = new PlaceGuideServlet(blobstoreService, blobInfoFactory);
    placeGuideServlet.doGet(request, response);

    printWriter.flush();
    Gson gson = new Gson();
    PlaceGuideInfo result =
        new GsonBuilder().create().fromJson(stringWriter.toString(), PlaceGuideInfo.class);
    PlaceGuideInfo expected = new PlaceGuideInfo(testInnerPublicPlaceGuideC, userC, true, false);
    assertTrue(placeGuideInfoEquals(expected, result));
  }

  @Test
  public void doGet_getPlaceGuide_currentUserDoesntOwnGuide() throws IOException {
    saveUser(userC);
    saveUser(userD);
    datastore.put(getEntityFromPlaceGuide(testInnerPublicPlaceGuideD));
    setupDoGetMockRequest(
        PlaceGuideQueryType.PLACE_GUIDE_WITH_ID, SOUTH_WEST_CORNER, NORTH_EAST_CORNER);
    when(request.getParameter(PlaceGuideServlet.PLACE_GUIDE_ID_PARAMETER))
        .thenReturn(String.valueOf(testInnerPublicPlaceGuideD.getId()));
    PlaceGuideServlet placeGuideServlet = new PlaceGuideServlet(blobstoreService, blobInfoFactory);
    placeGuideServlet.doGet(request, response);

    printWriter.flush();
    Gson gson = new Gson();
    PlaceGuideInfo result =
        new GsonBuilder().create().fromJson(stringWriter.toString(), PlaceGuideInfo.class);
    PlaceGuideInfo expected = new PlaceGuideInfo(testInnerPublicPlaceGuideD, userD, false, false);
    assertTrue(placeGuideInfoEquals(expected, result));
  }

  @Test
  public void doGet_getAllPublicPlaceGuides_placeGuideExists_returnPlaceGuides()
      throws IOException {
    saveUser(userC);
    List<PlaceGuide> testPlaceGuidesList =
        Arrays.asList(
            testInnerPrivatePlaceGuideC,
            testInnerPublicPlaceGuideC,
            testOuterPrivatePlaceGuideC,
            testOuterPublicPlaceGuideC);
    saveTestPlaceGuidesEntities(testPlaceGuidesList);
    when(request.getParameter(PlaceGuideServlet.PLACE_GUIDE_QUERY_TYPE_PARAMETER))
        .thenReturn(PlaceGuideQueryType.ALL_PUBLIC.toString());
    stringWriter = new StringWriter();
    printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    PlaceGuideServlet placeGuideServlet = new PlaceGuideServlet(blobstoreService, blobInfoFactory);
    placeGuideServlet.doGet(request, response);
    printWriter.flush();
    Gson gson = new Gson();
    List<PlaceGuideInfo> result =
        Arrays.asList(
            new GsonBuilder().create().fromJson(stringWriter.toString(), PlaceGuideInfo[].class));
    List<PlaceGuideInfo> expected =
        Arrays.asList(
            new PlaceGuideInfo(testInnerPublicPlaceGuideC, userC, true, false),
            new PlaceGuideInfo(testOuterPublicPlaceGuideC, userC, true, false));
    assertTrue(compare(expected, result));
  }

  @Test
  public void doGet_getAllPublicPlaceGuides_placeGuideDoesntExists_resultIsEmpty()
      throws IOException {
    when(request.getParameter(PlaceGuideServlet.PLACE_GUIDE_QUERY_TYPE_PARAMETER))
        .thenReturn(PlaceGuideQueryType.ALL_PUBLIC.toString());
    stringWriter = new StringWriter();
    printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    PlaceGuideServlet placeGuideServlet = new PlaceGuideServlet(blobstoreService, blobInfoFactory);
    placeGuideServlet.doGet(request, response);
    printWriter.flush();
    Gson gson = new Gson();
    List<PlaceGuideInfo> result =
        Arrays.asList(
            new GsonBuilder().create().fromJson(stringWriter.toString(), PlaceGuideInfo[].class));
    List<PlaceGuideInfo> expected = Collections.emptyList();
    assertTrue(compare(expected, result));
  }

  @Test
  public void doGet_getCreatedPlaceGuides_UserDoesntOwnAnyPlaceGuides_resultIsEmpty()
      throws IOException {
    saveUser(userC);
    List<PlaceGuide> testPlaceGuidesList =
        Arrays.asList(testOuterPublicPlaceGuideD, testInnerPublicPlaceGuideD);
    saveTestPlaceGuidesEntities(testPlaceGuidesList);
    when(request.getParameter(PlaceGuideServlet.PLACE_GUIDE_QUERY_TYPE_PARAMETER))
        .thenReturn(PlaceGuideQueryType.CREATED_ALL.toString());
    stringWriter = new StringWriter();
    printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    PlaceGuideServlet placeGuideServlet = new PlaceGuideServlet(blobstoreService, blobInfoFactory);
    placeGuideServlet.doGet(request, response);
    printWriter.flush();
    Gson gson = new Gson();
    List<PlaceGuideInfo> result =
        Arrays.asList(
            new GsonBuilder().create().fromJson(stringWriter.toString(), PlaceGuideInfo[].class));
    List<PlaceGuideInfo> expected = Collections.emptyList();
    assertTrue(compare(expected, result));
  }

  @Test
  public void doGet_getCreatedPlaceGuides_UserOwnsPublicAndPrivate_resultContainsPlaceGuide()
      throws IOException {
    saveUser(userC);
    List<PlaceGuide> testPlaceGuidesList =
        Arrays.asList(
            testInnerPublicPlaceGuideC, testOuterPrivatePlaceGuideC, testOuterPublicPlaceGuideD);
    saveTestPlaceGuidesEntities(testPlaceGuidesList);
    when(request.getParameter(PlaceGuideServlet.PLACE_GUIDE_QUERY_TYPE_PARAMETER))
        .thenReturn(PlaceGuideQueryType.CREATED_ALL.toString());
    stringWriter = new StringWriter();
    printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    PlaceGuideServlet placeGuideServlet = new PlaceGuideServlet(blobstoreService, blobInfoFactory);
    placeGuideServlet.doGet(request, response);
    printWriter.flush();
    Gson gson = new Gson();
    List<PlaceGuideInfo> result =
        Arrays.asList(
            new GsonBuilder().create().fromJson(stringWriter.toString(), PlaceGuideInfo[].class));
    List<PlaceGuideInfo> expected =
        Arrays.asList(
            new PlaceGuideInfo(testInnerPublicPlaceGuideC, userC, true, false),
            new PlaceGuideInfo(testOuterPrivatePlaceGuideC, userC, true, false));
    assertTrue(compare(expected, result));
  }

  @Test
  public void doGet_getCreatedPlaceGuides_placeGuideDoesntExist_resultIsEmpty() throws IOException {
    when(request.getParameter(PlaceGuideServlet.PLACE_GUIDE_QUERY_TYPE_PARAMETER))
        .thenReturn(PlaceGuideQueryType.CREATED_ALL.toString());
    stringWriter = new StringWriter();
    printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    PlaceGuideServlet placeGuideServlet = new PlaceGuideServlet(blobstoreService, blobInfoFactory);
    placeGuideServlet.doGet(request, response);
    printWriter.flush();
    Gson gson = new Gson();
    List<PlaceGuideInfo> result =
        Arrays.asList(
            new GsonBuilder().create().fromJson(stringWriter.toString(), PlaceGuideInfo[].class));
    List<PlaceGuideInfo> expected = Collections.emptyList();
    assertTrue(compare(expected, result));
  }

  @Test
  public void doGet_getCreatedPublicPlaceGuides_placeGuideDoesntExist_resultIsEmpty()
      throws IOException {
    when(request.getParameter(PlaceGuideServlet.PLACE_GUIDE_QUERY_TYPE_PARAMETER))
        .thenReturn(PlaceGuideQueryType.CREATED_PUBLIC.toString());
    stringWriter = new StringWriter();
    printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    PlaceGuideServlet placeGuideServlet = new PlaceGuideServlet(blobstoreService, blobInfoFactory);
    placeGuideServlet.doGet(request, response);
    printWriter.flush();
    Gson gson = new Gson();
    List<PlaceGuideInfo> result =
        Arrays.asList(
            new GsonBuilder().create().fromJson(stringWriter.toString(), PlaceGuideInfo[].class));
    List<PlaceGuideInfo> expected = Collections.emptyList();
    assertTrue(compare(expected, result));
  }

  @Test
  public void doGet_getCreatedPublicPlaceGuides_UserHasPublicPlaceGuide_resultHasPlaceGuide()
      throws IOException {
    saveUser(userC);
    List<PlaceGuide> testPlaceGuidesList =
        Arrays.asList(
            testInnerPublicPlaceGuideC, testOuterPublicPlaceGuideD, testOuterPrivatePlaceGuideC);
    saveTestPlaceGuidesEntities(testPlaceGuidesList);
    when(request.getParameter(PlaceGuideServlet.PLACE_GUIDE_QUERY_TYPE_PARAMETER))
        .thenReturn(PlaceGuideQueryType.CREATED_PUBLIC.toString());
    stringWriter = new StringWriter();
    printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    PlaceGuideServlet placeGuideServlet = new PlaceGuideServlet(blobstoreService, blobInfoFactory);
    placeGuideServlet.doGet(request, response);
    printWriter.flush();
    Gson gson = new Gson();
    List<PlaceGuideInfo> result =
        Arrays.asList(
            new GsonBuilder().create().fromJson(stringWriter.toString(), PlaceGuideInfo[].class));
    List<PlaceGuideInfo> expected =
        Arrays.asList(new PlaceGuideInfo(testInnerPublicPlaceGuideC, userC, true, false));
    assertTrue(compare(expected, result));
  }

  @Test
  public void doGet_getCreatedPrivatePlaceGuides_placeGuideDoesntExist_resultIsEmpty()
      throws IOException {
    when(request.getParameter(PlaceGuideServlet.PLACE_GUIDE_QUERY_TYPE_PARAMETER))
        .thenReturn(PlaceGuideQueryType.CREATED_PRIVATE.toString());
    stringWriter = new StringWriter();
    printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    PlaceGuideServlet placeGuideServlet = new PlaceGuideServlet(blobstoreService, blobInfoFactory);
    placeGuideServlet.doGet(request, response);
    printWriter.flush();
    Gson gson = new Gson();
    List<PlaceGuideInfo> result =
        Arrays.asList(
            new GsonBuilder().create().fromJson(stringWriter.toString(), PlaceGuideInfo[].class));
    List<PlaceGuideInfo> expected = Collections.emptyList();
    assertTrue(compare(expected, result));
  }

  @Test
  public void doGet_getCreatedPrivatePlaceGuides_userDoesntOwnAnyPrivatePlaceGuides_resultIsEmpty()
      throws IOException {
    saveUser(userC);
    List<PlaceGuide> testPlaceGuidesList =
        Arrays.asList(
            testInnerPublicPlaceGuideC, testOuterPublicPlaceGuideD, testOuterPublicPlaceGuideC);
    saveTestPlaceGuidesEntities(testPlaceGuidesList);
    when(request.getParameter(PlaceGuideServlet.PLACE_GUIDE_QUERY_TYPE_PARAMETER))
        .thenReturn(PlaceGuideQueryType.CREATED_PRIVATE.toString());
    stringWriter = new StringWriter();
    printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    PlaceGuideServlet placeGuideServlet = new PlaceGuideServlet(blobstoreService, blobInfoFactory);
    placeGuideServlet.doGet(request, response);
    printWriter.flush();
    Gson gson = new Gson();
    List<PlaceGuideInfo> result =
        Arrays.asList(
            new GsonBuilder().create().fromJson(stringWriter.toString(), PlaceGuideInfo[].class));
    List<PlaceGuideInfo> expected = Collections.emptyList();
    assertTrue(compare(expected, result));
  }

  @Test
  public void doGet_getCreatedPrivatePlaceGuides_UserHasPrivatePlaceGuide_resultHasPlaceGuide()
      throws IOException {
    saveUser(userC);
    List<PlaceGuide> testPlaceGuidesList =
        Arrays.asList(
            testInnerPublicPlaceGuideC, testOuterPublicPlaceGuideD, testOuterPrivatePlaceGuideC);
    saveTestPlaceGuidesEntities(testPlaceGuidesList);
    when(request.getParameter(PlaceGuideServlet.PLACE_GUIDE_QUERY_TYPE_PARAMETER))
        .thenReturn(PlaceGuideQueryType.CREATED_PRIVATE.toString());
    stringWriter = new StringWriter();
    printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    PlaceGuideServlet placeGuideServlet = new PlaceGuideServlet(blobstoreService, blobInfoFactory);
    placeGuideServlet.doGet(request, response);
    printWriter.flush();
    Gson gson = new Gson();
    List<PlaceGuideInfo> result =
        Arrays.asList(
            new GsonBuilder().create().fromJson(stringWriter.toString(), PlaceGuideInfo[].class));
    List<PlaceGuideInfo> expected =
        Arrays.asList(new PlaceGuideInfo(testOuterPrivatePlaceGuideC, userC, true, false));
    assertTrue(compare(expected, result));
  }

  @Test
  public void doGet_ALL_PUBLIC_IN_MAP_AREA_noExistingPlaceGuides_emptyResult() throws IOException {
    setupDoGetMockRequest(
        PlaceGuideQueryType.ALL_PUBLIC_IN_MAP_AREA, SOUTH_WEST_CORNER, NORTH_EAST_CORNER);
    PlaceGuideServlet placeGuideServlet = new PlaceGuideServlet(blobstoreService, blobInfoFactory);
    placeGuideServlet.doGet(request, response);

    printWriter.flush();
    Gson gson = new Gson();
    List<PlaceGuideInfo> result =
        Arrays.asList(
            new GsonBuilder().create().fromJson(stringWriter.toString(), PlaceGuideInfo[].class));
    List<PlaceGuideInfo> expected = Collections.emptyList();
    assertTrue(compare(expected, result));
  }

  @Test
  public void doGet_ALL_PUBLIC_IN_MAP_AREA_placeGuidesExist_resultHasInnerPublicPlaceGuides()
      throws IOException {
    saveUser(userC);
    saveUser(userD);
    List<PlaceGuide> testPlaceGuidesList =
        Arrays.asList(
            testInnerPrivatePlaceGuideC,
            testInnerPublicPlaceGuideC,
            testOuterPrivatePlaceGuideC,
            testOuterPublicPlaceGuideC,
            testInnerPrivatePlaceGuideD,
            testInnerPublicPlaceGuideD,
            testOuterPrivatePlaceGuideD,
            testOuterPublicPlaceGuideD);
    saveTestPlaceGuidesEntities(testPlaceGuidesList);

    setupDoGetMockRequest(
        PlaceGuideQueryType.ALL_PUBLIC_IN_MAP_AREA, SOUTH_WEST_CORNER, NORTH_EAST_CORNER);
    PlaceGuideServlet placeGuideServlet = new PlaceGuideServlet(blobstoreService, blobInfoFactory);
    placeGuideServlet.doGet(request, response);

    printWriter.flush();
    Gson gson = new Gson();
    List<PlaceGuideInfo> result =
        Arrays.asList(
            new GsonBuilder().create().fromJson(stringWriter.toString(), PlaceGuideInfo[].class));
    List<PlaceGuideInfo> expected =
        Arrays.asList(
            new PlaceGuideInfo(testInnerPublicPlaceGuideC, userC, true, false),
            new PlaceGuideInfo(testInnerPublicPlaceGuideD, userD, false, false));
    assertTrue(compare(expected, result));
  }

  @Test
  public void doGet_CREATED_ALL_IN_MAP_AREA_noExistingPlaceGuides_emptyResult() throws IOException {
    setupDoGetMockRequest(
        PlaceGuideQueryType.CREATED_ALL_IN_MAP_AREA, SOUTH_WEST_CORNER, NORTH_EAST_CORNER);
    PlaceGuideServlet placeGuideServlet = new PlaceGuideServlet(blobstoreService, blobInfoFactory);
    placeGuideServlet.doGet(request, response);

    printWriter.flush();
    Gson gson = new Gson();
    List<PlaceGuideInfo> result =
        Arrays.asList(
            new GsonBuilder().create().fromJson(stringWriter.toString(), PlaceGuideInfo[].class));
    List<PlaceGuideInfo> expected = Collections.emptyList();
    assertTrue(compare(expected, result));
  }

  @Test
  public void doGet_CREATED_ALL_IN_MAP_AREA_userDoesntOwnAnyPlaceGuides_emptyResult()
      throws IOException {
    saveUser(userC);
    saveUser(userD);
    List<PlaceGuide> testPlaceGuidesList =
        Arrays.asList(
            testOuterPrivatePlaceGuideC,
            testOuterPublicPlaceGuideC,
            testInnerPrivatePlaceGuideD,
            testInnerPublicPlaceGuideD,
            testOuterPrivatePlaceGuideD,
            testOuterPublicPlaceGuideD);
    saveTestPlaceGuidesEntities(testPlaceGuidesList);

    setupDoGetMockRequest(
        PlaceGuideQueryType.CREATED_ALL_IN_MAP_AREA, SOUTH_WEST_CORNER, NORTH_EAST_CORNER);
    PlaceGuideServlet placeGuideServlet = new PlaceGuideServlet(blobstoreService, blobInfoFactory);
    placeGuideServlet.doGet(request, response);

    printWriter.flush();
    Gson gson = new Gson();
    List<PlaceGuideInfo> result =
        Arrays.asList(
            new GsonBuilder().create().fromJson(stringWriter.toString(), PlaceGuideInfo[].class));
    List<PlaceGuideInfo> expected = Arrays.asList();
    assertTrue(compare(expected, result));
  }

  @Test
  public void doGet_CREATED_ALL_IN_MAP_AREA_userOwnsPlaceGuides_resultHasInnerPlaceGuidesOfUser()
      throws IOException {
    saveUser(userC);
    saveUser(userD);
    List<PlaceGuide> testPlaceGuidesList =
        Arrays.asList(
            testInnerPrivatePlaceGuideC,
            testInnerPublicPlaceGuideC,
            testOuterPrivatePlaceGuideC,
            testOuterPublicPlaceGuideC,
            testInnerPrivatePlaceGuideD,
            testInnerPublicPlaceGuideD,
            testOuterPrivatePlaceGuideD,
            testOuterPublicPlaceGuideD);
    saveTestPlaceGuidesEntities(testPlaceGuidesList);

    setupDoGetMockRequest(
        PlaceGuideQueryType.CREATED_ALL_IN_MAP_AREA, SOUTH_WEST_CORNER, NORTH_EAST_CORNER);
    PlaceGuideServlet placeGuideServlet = new PlaceGuideServlet(blobstoreService, blobInfoFactory);
    placeGuideServlet.doGet(request, response);

    printWriter.flush();
    Gson gson = new Gson();
    List<PlaceGuideInfo> result =
        Arrays.asList(
            new GsonBuilder().create().fromJson(stringWriter.toString(), PlaceGuideInfo[].class));
    List<PlaceGuideInfo> expected =
        Arrays.asList(
            new PlaceGuideInfo(testInnerPublicPlaceGuideC, userC, true, false),
            new PlaceGuideInfo(testInnerPrivatePlaceGuideC, userC, true, false));
    assertTrue(compare(expected, result));
  }

  @Test
  public void doGet_CREATED_PUBLIC_IN_MAP_AREA_noExistingPlaceGuides_emptyResult()
      throws IOException {
    setupDoGetMockRequest(
        PlaceGuideQueryType.CREATED_PUBLIC_IN_MAP_AREA, SOUTH_WEST_CORNER, NORTH_EAST_CORNER);
    PlaceGuideServlet placeGuideServlet = new PlaceGuideServlet(blobstoreService, blobInfoFactory);
    placeGuideServlet.doGet(request, response);

    printWriter.flush();
    Gson gson = new Gson();
    List<PlaceGuideInfo> result =
        Arrays.asList(
            new GsonBuilder().create().fromJson(stringWriter.toString(), PlaceGuideInfo[].class));
    List<PlaceGuideInfo> expected = Collections.emptyList();
    assertTrue(compare(expected, result));
  }

  @Test
  public void doGet_CREATED_PUBLIC_IN_MAP_AREA_userDoesntOwnAnyPlaceGuides_emptyResult()
      throws IOException {
    saveUser(userC);
    saveUser(userD);
    List<PlaceGuide> testPlaceGuidesList =
        Arrays.asList(
            testInnerPrivatePlaceGuideC,
            testOuterPrivatePlaceGuideC,
            testOuterPublicPlaceGuideC,
            testInnerPrivatePlaceGuideD,
            testInnerPublicPlaceGuideD,
            testOuterPrivatePlaceGuideD,
            testOuterPublicPlaceGuideD);
    saveTestPlaceGuidesEntities(testPlaceGuidesList);

    setupDoGetMockRequest(
        PlaceGuideQueryType.CREATED_PUBLIC_IN_MAP_AREA, SOUTH_WEST_CORNER, NORTH_EAST_CORNER);
    PlaceGuideServlet placeGuideServlet = new PlaceGuideServlet(blobstoreService, blobInfoFactory);
    placeGuideServlet.doGet(request, response);

    printWriter.flush();
    Gson gson = new Gson();
    List<PlaceGuideInfo> result =
        Arrays.asList(
            new GsonBuilder().create().fromJson(stringWriter.toString(), PlaceGuideInfo[].class));
    List<PlaceGuideInfo> expected = Arrays.asList();
    assertTrue(compare(expected, result));
  }

  @Test
  public void doGet_CREATED_PUBLIC_IN_MAP_AREA_userOwnsPlaceGuides_resultHasInnerPlaceGuidesOfUser()
      throws IOException {
    saveUser(userC);
    saveUser(userD);
    List<PlaceGuide> testPlaceGuidesList =
        Arrays.asList(
            testInnerPrivatePlaceGuideC,
            testInnerPublicPlaceGuideC,
            testOuterPrivatePlaceGuideC,
            testOuterPublicPlaceGuideC,
            testInnerPrivatePlaceGuideD,
            testInnerPublicPlaceGuideD,
            testOuterPrivatePlaceGuideD,
            testOuterPublicPlaceGuideD);
    saveTestPlaceGuidesEntities(testPlaceGuidesList);

    setupDoGetMockRequest(
        PlaceGuideQueryType.CREATED_PUBLIC_IN_MAP_AREA, SOUTH_WEST_CORNER, NORTH_EAST_CORNER);
    PlaceGuideServlet placeGuideServlet = new PlaceGuideServlet(blobstoreService, blobInfoFactory);
    placeGuideServlet.doGet(request, response);

    printWriter.flush();
    Gson gson = new Gson();
    List<PlaceGuideInfo> result =
        Arrays.asList(
            new GsonBuilder().create().fromJson(stringWriter.toString(), PlaceGuideInfo[].class));
    List<PlaceGuideInfo> expected =
        Arrays.asList(new PlaceGuideInfo(testInnerPublicPlaceGuideC, userC, true, false));
    assertTrue(compare(expected, result));
  }

  @Test
  public void doGet_CREATED_PRIVATE_IN_MAP_AREA_noExistingPlaceGuides_emptyResult()
      throws IOException {
    setupDoGetMockRequest(
        PlaceGuideQueryType.CREATED_PRIVATE_IN_MAP_AREA, SOUTH_WEST_CORNER, NORTH_EAST_CORNER);
    PlaceGuideServlet placeGuideServlet = new PlaceGuideServlet(blobstoreService, blobInfoFactory);
    placeGuideServlet.doGet(request, response);

    printWriter.flush();
    Gson gson = new Gson();
    List<PlaceGuideInfo> result =
        Arrays.asList(
            new GsonBuilder().create().fromJson(stringWriter.toString(), PlaceGuideInfo[].class));
    List<PlaceGuideInfo> expected = Collections.emptyList();
    assertTrue(compare(expected, result));
  }

  @Test
  public void doGet_CREATED_PRIVATE_IN_MAP_AREA_userDoesntOwnAnyPlaceGuides_emptyResult()
      throws IOException {
    saveUser(userC);
    saveUser(userD);
    List<PlaceGuide> testPlaceGuidesList =
        Arrays.asList(
            testInnerPublicPlaceGuideC,
            testOuterPrivatePlaceGuideC,
            testOuterPublicPlaceGuideC,
            testInnerPrivatePlaceGuideD,
            testInnerPublicPlaceGuideD,
            testOuterPrivatePlaceGuideD,
            testOuterPublicPlaceGuideD);
    saveTestPlaceGuidesEntities(testPlaceGuidesList);

    setupDoGetMockRequest(
        PlaceGuideQueryType.CREATED_PRIVATE_IN_MAP_AREA, SOUTH_WEST_CORNER, NORTH_EAST_CORNER);
    PlaceGuideServlet placeGuideServlet = new PlaceGuideServlet(blobstoreService, blobInfoFactory);
    placeGuideServlet.doGet(request, response);

    printWriter.flush();
    Gson gson = new Gson();
    List<PlaceGuideInfo> result =
        Arrays.asList(
            new GsonBuilder().create().fromJson(stringWriter.toString(), PlaceGuideInfo[].class));
    List<PlaceGuideInfo> expected = Arrays.asList();
    assertTrue(compare(expected, result));
  }

  @Test
  public void
      doGet_CREATED_PRIVATE_IN_MAP_AREA_userOwnsPlaceGuides_resultHasInnerPlaceGuidesOfUser()
          throws IOException {
    saveUser(userC);
    saveUser(userD);
    List<PlaceGuide> testPlaceGuidesList =
        Arrays.asList(
            testInnerPrivatePlaceGuideC,
            testInnerPublicPlaceGuideC,
            testOuterPrivatePlaceGuideC,
            testOuterPublicPlaceGuideC,
            testInnerPrivatePlaceGuideD,
            testInnerPublicPlaceGuideD,
            testOuterPrivatePlaceGuideD,
            testOuterPublicPlaceGuideD);
    saveTestPlaceGuidesEntities(testPlaceGuidesList);

    setupDoGetMockRequest(
        PlaceGuideQueryType.CREATED_PRIVATE_IN_MAP_AREA, SOUTH_WEST_CORNER, NORTH_EAST_CORNER);
    PlaceGuideServlet placeGuideServlet = new PlaceGuideServlet(blobstoreService, blobInfoFactory);
    placeGuideServlet.doGet(request, response);

    printWriter.flush();
    Gson gson = new Gson();
    List<PlaceGuideInfo> result =
        Arrays.asList(
            new GsonBuilder().create().fromJson(stringWriter.toString(), PlaceGuideInfo[].class));
    List<PlaceGuideInfo> expected =
        Arrays.asList(new PlaceGuideInfo(testInnerPrivatePlaceGuideC, userC, true, false));
    assertTrue(compare(expected, result));
  }

  @Test
  public void doPost() throws IOException {
    // Mock request and response.
    when(request.getParameter(PlaceGuideServlet.NAME_INPUT)).thenReturn(NAME);
    when(request.getParameter(PlaceGuideServlet.ID_INPUT)).thenReturn("123");
    when(request.getParameter(PlaceGuideServlet.LATITUDE_INPUT)).thenReturn("12.8");
    when(request.getParameter(PlaceGuideServlet.LONGITUDE_INPUT)).thenReturn("25.6");
    when(request.getParameter(PlaceGuideServlet.IS_PUBLIC_INPUT)).thenReturn("true");
    when(request.getParameter(PlaceGuideServlet.LENGTH_INPUT)).thenReturn("60");
    when(request.getParameter(PlaceGuideServlet.PLACE_ID_INPUT)).thenReturn(PLACE_ID);
    when(request.getParameter(PlaceGuideServlet.DESCRIPTION_INPUT)).thenReturn(DESCRIPTION);
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);

    // Mock blobstoreService and blobInfoFactory
    Map<String, List<BlobKey>> blobs = new HashMap<>();
    BlobKey imageBlobKey = new BlobKey(IMAGE_KEY);
    BlobKey audioBlobKey = new BlobKey(AUDIO_KEY);
    blobs.put(PlaceGuideServlet.IMAGE_KEY_INPUT, Arrays.asList(imageBlobKey));
    blobs.put(PlaceGuideServlet.AUDIO_KEY_INPUT, Arrays.asList(audioBlobKey));
    when(blobstoreService.getUploads(request)).thenReturn(blobs);
    BlobInfo imageBlobInfo = new BlobInfo(imageBlobKey, "img", new Date(), "file.img", 1);
    BlobInfo audioBlobInfo = new BlobInfo(audioBlobKey, "mp3", new Date(), "file.mp3", 1);
    when(blobInfoFactory.loadBlobInfo(imageBlobKey)).thenReturn(imageBlobInfo);
    when(blobInfoFactory.loadBlobInfo(audioBlobKey)).thenReturn(audioBlobInfo);

    PlaceGuideServlet placeGuideServlet = new PlaceGuideServlet(blobstoreService, blobInfoFactory);
    placeGuideServlet.doPost(request, response);

    Key placeGuideKey = KeyFactory.createKey(DatastorePlaceGuideRepository.ENTITY_KIND, (long) 123);
    try {
      Entity placeGuideEntity = datastore.get(placeGuideKey);
      assertEquals(NAME, placeGuideEntity.getProperty(DatastorePlaceGuideRepository.NAME_PROPERTY));
      assertEquals(
          ID_USER_C,
          placeGuideEntity.getProperty(DatastorePlaceGuideRepository.CREATOR_ID_PROPERTY));
      assertEquals(
          IS_PUBLIC,
          placeGuideEntity.getProperty(DatastorePlaceGuideRepository.IS_PUBLIC_PROPERTY));
      assertEquals(
          new GeoPt((float) 12.8, (float) 25.6),
          placeGuideEntity.getProperty(DatastorePlaceGuideRepository.COORDINATE_PROPERTY));
      assertEquals(
          LENGTH, placeGuideEntity.getProperty(DatastorePlaceGuideRepository.LENGTH_PROPERTY));
      assertEquals(
          DESCRIPTION,
          placeGuideEntity.getProperty(DatastorePlaceGuideRepository.DESCRIPTION_PROPERTY));
      assertEquals(
          IMAGE_KEY,
          placeGuideEntity.getProperty(DatastorePlaceGuideRepository.IMAGE_KEY_PROPERTY));
      assertEquals(
          AUDIO_KEY,
          placeGuideEntity.getProperty(DatastorePlaceGuideRepository.AUDIO_KEY_PROPERTY));
    } catch (EntityNotFoundException e) {
      fail("Entity not found: " + e);
    }
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }
}
