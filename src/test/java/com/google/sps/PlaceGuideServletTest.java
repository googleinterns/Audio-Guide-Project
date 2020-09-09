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

import com.google.appengine.api.blobstore.BlobInfoFactory;
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
  private StringWriter sw;
  private PrintWriter pw;

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

  // General placeguide data
  private static final String NAME = "name";
  private static final String AUDIO_KEY = "audioKey";
  private static final String PLACE_ID = "placeId";
  private static final boolean IS_PUBLIC = true;
  private static final long LENGTH = new Long(60);
  private static final String DESCRIPTION = "description";
  private static final String IMAGE_KEY = "imageKey";

  // PlaceGuides' parameters used for map-related queries, with a region not crossing the IDL.
  // PlaceGudies of user C.
  private static final long C_INNER_PUBLIC_ID = 56789;
  private static final long C_INNER_PRIVATE_ID = 98765;
  private static final long C_OUTER_PUBLIC_ID = 67890;
  private static final long C_OUTER_PRIVATE_ID = 9876;
  private static final String CREATOR_C_ID = ID_USER_C;
  private static final GeoPt C_INNER_PUBLIC_COORDINATE = new GeoPt((float) 10, (float) -5);
  private static final GeoPt C_INNER_PRIVATE_COORDINATE = new GeoPt((float) -14, (float) 14);
  private static final GeoPt C_OUTER_PUBLIC_COORDINATE = new GeoPt((float) 5, (float) -20);
  private static final GeoPt C_OUTER_PRIVATE_COORDINATE = new GeoPt((float) -30, (float) -5);
  // PlaceGudies of user D.
  private static final long D_INNER_PUBLIC_ID = 567890;
  private static final long D_INNER_PRIVATE_ID = 987650;
  private static final long D_OUTER_PUBLIC_ID = 678900;
  private static final long D_OUTER_PRIVATE_ID = 98760;
  private static final String CREATOR_D_ID = ID_USER_D;
  private static final GeoPt D_INNER_PUBLIC_COORDINATE = new GeoPt((float) 10, (float) 5);
  private static final GeoPt D_INNER_PRIVATE_COORDINATE = new GeoPt((float) -14, (float) 14);
  private static final GeoPt D_OUTER_PUBLIC_COORDINATE = new GeoPt((float) 60, (float) 10);
  private static final GeoPt D_OUTER_PRIVATE_COORDINATE = new GeoPt((float) -10, (float) -45);
  // Corners of the rectangle for the queried map area.
  // This is a map area which doesn't cross the IDL.
  private static final GeoPt NORTH_EAST_CORNER = new GeoPt((float) 15, (float) 15);
  private static final GeoPt SOUTH_WEST_CORNER = new GeoPt((float) -15, (float) -15);

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

  private String getRegionCornersString(GeoPt southWestCorner, GeoPt northEastCorner) {
    return String.format(
        "%f,%f,%f,%f",
        southWestCorner.getLatitude(),
        southWestCorner.getLongitude(),
        northEastCorner.getLatitude(),
        northEastCorner.getLongitude());
  }

  private void setupDoGetMockRequest(
      PlaceGuideQueryType queryType, GeoPt southWestCorner, GeoPt northEastCorner)
      throws IOException {
    when(request.getParameter(PlaceGuideServlet.PLACE_GUIDE_QUERY_TYPE_PARAMETER))
        .thenReturn(queryType.toString());
    when(request.getParameter(PlaceGuideServlet.REGION_CORNERS_PARAMETER))
        .thenReturn(getRegionCornersString(southWestCorner, northEastCorner));
    sw = new StringWriter();
    pw = new PrintWriter(sw);
    when(response.getWriter()).thenReturn(pw);
  }

  private boolean placeGuideInfoEquals(PlaceGuideInfo a, PlaceGuideInfo b) {
    return a.getPlaceGuide().getId() == b.getPlaceGuide().getId()
        && a.getCreator().equals(b.getCreator())
        && a.isCreatedByCurrentUser() == b.isCreatedByCurrentUser()
        && a.isBookmarkedByCurrentUser() == b.isBookmarkedByCurrentUser();
  }

  // Find out if the 2 lists of placeguides are equal.
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
  public void doGet_ALL_PUBLIC_IN_MAP_AREA_noExistingPlaceGuides_emptyResult() throws IOException {
    setupDoGetMockRequest(
        PlaceGuideQueryType.ALL_PUBLIC_IN_MAP_AREA, SOUTH_WEST_CORNER, NORTH_EAST_CORNER);
    PlaceGuideServlet placeGuideServlet = new PlaceGuideServlet(blobstoreService, blobInfoFactory);
    placeGuideServlet.doGet(request, response);

    pw.flush();
    Gson gson = new Gson();
    List<PlaceGuideInfo> result =
        Arrays.asList(new GsonBuilder().create().fromJson(sw.toString(), PlaceGuideInfo[].class));
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

    pw.flush();
    Gson gson = new Gson();
    List<PlaceGuideInfo> result =
        Arrays.asList(new GsonBuilder().create().fromJson(sw.toString(), PlaceGuideInfo[].class));
    List<PlaceGuideInfo> expected =
        Arrays.asList(
            new PlaceGuideInfo(testInnerPublicPlaceGuideC, userC, true, false),
            new PlaceGuideInfo(testInnerPublicPlaceGuideD, userD, false, false));
    assertTrue(compare(expected, result));
  }

  @Test
  public void getCreatedPlaceGuidesInMapArea_noExistingPlaceGuides_emptyResult()
      throws IOException {
    setupDoGetMockRequest(
        PlaceGuideQueryType.CREATED_ALL_IN_MAP_AREA, SOUTH_WEST_CORNER, NORTH_EAST_CORNER);
    PlaceGuideServlet placeGuideServlet = new PlaceGuideServlet(blobstoreService, blobInfoFactory);
    placeGuideServlet.doGet(request, response);

    pw.flush();
    Gson gson = new Gson();
    List<PlaceGuideInfo> result =
        Arrays.asList(new GsonBuilder().create().fromJson(sw.toString(), PlaceGuideInfo[].class));
    List<PlaceGuideInfo> expected = Collections.emptyList();
    assertTrue(compare(expected, result));
  }

  @Test
  public void getCreatedPlaceGuidesInMapArea_userDoesntOwnAnyPlaceGuides_emptyResult()
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

    pw.flush();
    Gson gson = new Gson();
    List<PlaceGuideInfo> result =
        Arrays.asList(new GsonBuilder().create().fromJson(sw.toString(), PlaceGuideInfo[].class));
    List<PlaceGuideInfo> expected = Arrays.asList();
    assertTrue(compare(expected, result));
  }

  @Test
  public void getCreatedPlaceGuidesInMapArea_userOwnsPlaceGuides_resultHasInnerPlaceGuidesOfUser()
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

    pw.flush();
    Gson gson = new Gson();
    List<PlaceGuideInfo> result =
        Arrays.asList(new GsonBuilder().create().fromJson(sw.toString(), PlaceGuideInfo[].class));
    List<PlaceGuideInfo> expected =
        Arrays.asList(
            new PlaceGuideInfo(testInnerPublicPlaceGuideC, userC, true, false),
            new PlaceGuideInfo(testInnerPrivatePlaceGuideC, userC, true, false));
    assertTrue(compare(expected, result));
  }

  // @Test
  // public void getCreatedPublicPlaceGuidesInMapArea_noExistingPlaceGuides_emptyResult() {
  //   List<PlaceGuide> result =
  //       placeGuideRepository.getCreatedPublicPlaceGuidesInMapArea(
  //           OTHER_USER_ID, NORTH_EAST_CORNER, SOUTH_WEST_CORNER);
  //   assertTrue(result.isEmpty());
  // }

  //   @Test
  //   public void getCreatedPublicPlaceGuidesInMapArea_userDoesntOwnAnyPlaceGuides_emptyResult() {
  //     List<PlaceGuide> testPlaceGuidesList =
  //         Arrays.asList(
  //             testInnerPrivatePlaceGuideC,
  //             testInnerPublicPlaceGuideC,
  //             testOuterPrivatePlaceGuideC,
  //             testOuterPublicPlaceGuideC,
  //             testInnerPrivatePlaceGuideD,
  //             testInnerPublicPlaceGuideD,
  //             testOuterPrivatePlaceGuideD,
  //             testOuterPublicPlaceGuideD);
  //     saveTestPlaceGuidesEntities(testPlaceGuidesList);
  //     List<PlaceGuide> result =
  //         placeGuideRepository.getCreatedPublicPlaceGuidesInMapArea(
  //             OTHER_USER_ID, NORTH_EAST_CORNER, SOUTH_WEST_CORNER);
  //     assertTrue(result.isEmpty());
  //   }

  //   @Test
  //   public void
  //
  // getCreatedPublicPlaceGuidesInMapArea_userOwnsPlaceGuides_resultHasInnerPublicPlaceGuidesOfUser() {
  //     List<PlaceGuide> testPlaceGuidesList =
  //         Arrays.asList(
  //             testInnerPrivatePlaceGuideC,
  //             testInnerPublicPlaceGuideC,
  //             testOuterPrivatePlaceGuideC,
  //             testOuterPublicPlaceGuideC,
  //             testInnerPrivatePlaceGuideD,
  //             testInnerPublicPlaceGuideD,
  //             testOuterPrivatePlaceGuideD,
  //             testOuterPublicPlaceGuideD);
  //     saveTestPlaceGuidesEntities(testPlaceGuidesList);
  //     List<PlaceGuide> result =
  //         placeGuideRepository.getCreatedPublicPlaceGuidesInMapArea(
  //             CREATOR_C_ID, NORTH_EAST_CORNER, SOUTH_WEST_CORNER);
  //     List<PlaceGuide> expected = Arrays.asList(testInnerPublicPlaceGuideC);
  //     assertTrue(compare(expected, result));
  //   }

  //   @Test
  //   public void getCreatedPrivatePlaceGuidesInMapArea_noExistingPlaceGuides_emptyResult() {
  //     List<PlaceGuide> result =
  //         placeGuideRepository.getCreatedPrivatePlaceGuidesInMapArea(
  //             OTHER_USER_ID, NORTH_EAST_CORNER, SOUTH_WEST_CORNER);
  //     assertTrue(result.isEmpty());
  //   }

  //   @Test
  //   public void getCreatedPrivatePlaceGuidesInMapArea_userDoesntOwnAnyPlaceGuides_emptyResult() {
  //     List<PlaceGuide> testPlaceGuidesList =
  //         Arrays.asList(
  //             testInnerPrivatePlaceGuideC,
  //             testInnerPublicPlaceGuideC,
  //             testOuterPrivatePlaceGuideC,
  //             testOuterPublicPlaceGuideC,
  //             testInnerPrivatePlaceGuideD,
  //             testInnerPublicPlaceGuideD,
  //             testOuterPrivatePlaceGuideD,
  //             testOuterPublicPlaceGuideD);
  //     saveTestPlaceGuidesEntities(testPlaceGuidesList);
  //     List<PlaceGuide> result =
  //         placeGuideRepository.getCreatedPrivatePlaceGuidesInMapArea(
  //             OTHER_USER_ID, NORTH_EAST_CORNER, SOUTH_WEST_CORNER);
  //     assertTrue(result.isEmpty());
  //   }

  //   @Test
  //   public void
  //
  // getCreatedPrivatePlaceGuidesInMapArea_userOwnsPlaceGuides_resultHasInnerPublicPlaceGuidesOfUser() {
  //     List<PlaceGuide> testPlaceGuidesList =
  //         Arrays.asList(
  //             testInnerPrivatePlaceGuideC,
  //             testInnerPublicPlaceGuideC,
  //             testOuterPrivatePlaceGuideC,
  //             testOuterPublicPlaceGuideC,
  //             testInnerPrivatePlaceGuideD,
  //             testInnerPublicPlaceGuideD,
  //             testOuterPrivatePlaceGuideD,
  //             testOuterPublicPlaceGuideD);
  //     saveTestPlaceGuidesEntities(testPlaceGuidesList);
  //     List<PlaceGuide> result =
  //         placeGuideRepository.getCreatedPrivatePlaceGuidesInMapArea(
  //             CREATOR_C_ID, NORTH_EAST_CORNER, SOUTH_WEST_CORNER);
  //     List<PlaceGuide> expected = Arrays.asList(testInnerPrivatePlaceGuideC);
  //     assertTrue(compare(expected, result));
  //   }

  //   @Test
  //   public void getAllPublicPlaceGuidesInMapArea_noExistingPlaceGuides_areaWithIDL_emptyResult()
  // {
  //     List<PlaceGuide> result =
  //         placeGuideRepository.getAllPublicPlaceGuidesInMapArea(
  //             NORTH_EAST_CORNER_WITH_IDL, SOUTH_WEST_CORNER_WITH_IDL);
  //     assertTrue(result.isEmpty());
  //   }

  //   @Test
  //   public void
  //
  // getAllPublicPlaceGuidesInMapArea_placeGuidesExist_areaWithIDL_resultHasInnerPublicPlaceGuides()
  // {
  //     List<PlaceGuide> testPlaceGuidesList =
  //         Arrays.asList(
  //             testInnerPrivatePlaceGuideE,
  //             testInnerPublicPlaceGuideE,
  //             testOuterPrivatePlaceGuideE,
  //             testOuterPublicPlaceGuideE,
  //             testInnerPrivatePlaceGuideF,
  //             testInnerPublicPlaceGuideF,
  //             testOuterPrivatePlaceGuideF,
  //             testOuterPublicPlaceGuideF);
  //     saveTestPlaceGuidesEntities(testPlaceGuidesList);
  //     List<PlaceGuide> result =
  //         placeGuideRepository.getAllPublicPlaceGuidesInMapArea(
  //             NORTH_EAST_CORNER_WITH_IDL, SOUTH_WEST_CORNER_WITH_IDL);
  //     List<PlaceGuide> expected =
  //         Arrays.asList(testInnerPublicPlaceGuideE, testInnerPublicPlaceGuideF);
  //     System.out.println("!!!Result is:");
  //     for (PlaceGuide placeGuide : result) {
  //       System.out.println(
  //           placeGuide.getId() + " with coordinates: " + placeGuide.getCoordinate().toString());
  //     }
  //     assertTrue(compare(expected, result));
  //   }

  //   @Test
  //   public void getCreatedPlaceGuidesInMapArea_noExistingPlaceGuides_areaWithIDL_emptyResult() {
  //     List<PlaceGuide> result =
  //         placeGuideRepository.getCreatedPlaceGuidesInMapArea(
  //             OTHER_USER_ID, NORTH_EAST_CORNER_WITH_IDL, SOUTH_WEST_CORNER_WITH_IDL);
  //     assertTrue(result.isEmpty());
  //   }

  //   @Test
  //   public void
  // getCreatedPlaceGuidesInMapArea_userDoesntOwnAnyPlaceGuides_areaWithIDL_emptyResult() {
  //     List<PlaceGuide> testPlaceGuidesList =
  //         Arrays.asList(
  //             testInnerPrivatePlaceGuideE,
  //             testInnerPublicPlaceGuideE,
  //             testOuterPrivatePlaceGuideE,
  //             testOuterPublicPlaceGuideE,
  //             testInnerPrivatePlaceGuideF,
  //             testInnerPublicPlaceGuideF,
  //             testOuterPrivatePlaceGuideF,
  //             testOuterPublicPlaceGuideF);
  //     saveTestPlaceGuidesEntities(testPlaceGuidesList);
  //     List<PlaceGuide> result =
  //         placeGuideRepository.getCreatedPlaceGuidesInMapArea(
  //             OTHER_USER_ID, NORTH_EAST_CORNER_WITH_IDL, SOUTH_WEST_CORNER_WITH_IDL);
  //     assertTrue(result.isEmpty());
  //   }

  //   @Test
  //   public void
  //
  // getCreatedPlaceGuidesInMapArea_userOwnsPlaceGuides_areaWithIDL_resultHasInnerPlaceGuidesOfUser() {
  //     List<PlaceGuide> testPlaceGuidesList =
  //         Arrays.asList(
  //             testInnerPrivatePlaceGuideE,
  //             testInnerPublicPlaceGuideE,
  //             testOuterPrivatePlaceGuideE,
  //             testOuterPublicPlaceGuideE,
  //             testInnerPrivatePlaceGuideF,
  //             testInnerPublicPlaceGuideF,
  //             testOuterPrivatePlaceGuideF,
  //             testOuterPublicPlaceGuideF);
  //     saveTestPlaceGuidesEntities(testPlaceGuidesList);
  //     List<PlaceGuide> result =
  //         placeGuideRepository.getCreatedPlaceGuidesInMapArea(
  //             CREATOR_E_ID, NORTH_EAST_CORNER_WITH_IDL, SOUTH_WEST_CORNER_WITH_IDL);
  //     List<PlaceGuide> expected =
  //         Arrays.asList(testInnerPublicPlaceGuideE, testInnerPrivatePlaceGuideE);
  //     assertTrue(compare(expected, result));
  //   }

  //   @Test
  //   public void
  // getCreatedPublicPlaceGuidesInMapArea_noExistingPlaceGuides_areaWithIDL_emptyResult() {
  //     List<PlaceGuide> result =
  //         placeGuideRepository.getCreatedPublicPlaceGuidesInMapArea(
  //             OTHER_USER_ID, NORTH_EAST_CORNER_WITH_IDL, SOUTH_WEST_CORNER_WITH_IDL);
  //     assertTrue(result.isEmpty());
  //   }

  //   @Test
  //   public void
  //
  // getCreatedPublicPlaceGuidesInMapArea_userDoesntOwnAnyPlaceGuides_areaWithIDL_emptyResult() {
  //     List<PlaceGuide> testPlaceGuidesList =
  //         Arrays.asList(
  //             testInnerPrivatePlaceGuideE,
  //             testInnerPublicPlaceGuideE,
  //             testOuterPrivatePlaceGuideE,
  //             testOuterPublicPlaceGuideE,
  //             testInnerPrivatePlaceGuideF,
  //             testInnerPublicPlaceGuideF,
  //             testOuterPrivatePlaceGuideF,
  //             testOuterPublicPlaceGuideF);
  //     saveTestPlaceGuidesEntities(testPlaceGuidesList);
  //     List<PlaceGuide> result =
  //         placeGuideRepository.getCreatedPublicPlaceGuidesInMapArea(
  //             OTHER_USER_ID, NORTH_EAST_CORNER_WITH_IDL, SOUTH_WEST_CORNER_WITH_IDL);
  //     assertTrue(result.isEmpty());
  //   }

  //   @Test
  //   public void
  //
  // getCreatedPublicPlaceGuidesInMapArea_userOwnsPlaceGuides_areaWithIDL_resultHasInnerPublicPlaceGuidesOfUser() {
  //     List<PlaceGuide> testPlaceGuidesList =
  //         Arrays.asList(
  //             testInnerPrivatePlaceGuideE,
  //             testInnerPublicPlaceGuideE,
  //             testOuterPrivatePlaceGuideE,
  //             testOuterPublicPlaceGuideE,
  //             testInnerPrivatePlaceGuideF,
  //             testInnerPublicPlaceGuideF,
  //             testOuterPrivatePlaceGuideF,
  //             testOuterPublicPlaceGuideF);
  //     saveTestPlaceGuidesEntities(testPlaceGuidesList);
  //     List<PlaceGuide> result =
  //         placeGuideRepository.getCreatedPublicPlaceGuidesInMapArea(
  //             CREATOR_E_ID, NORTH_EAST_CORNER_WITH_IDL, SOUTH_WEST_CORNER_WITH_IDL);
  //     List<PlaceGuide> expected = Arrays.asList(testInnerPublicPlaceGuideE);
  //     assertTrue(compare(expected, result));
  //   }

  //   @Test
  //   public void
  //       getCreatedPrivatePlaceGuidesInMapArea_noExistingPlaceGuides_areaWithIDL_emptyResult() {
  //     List<PlaceGuide> result =
  //         placeGuideRepository.getCreatedPrivatePlaceGuidesInMapArea(
  //             OTHER_USER_ID, NORTH_EAST_CORNER_WITH_IDL, SOUTH_WEST_CORNER_WITH_IDL);
  //     assertTrue(result.isEmpty());
  //   }

  //   @Test
  //   public void
  //
  // getCreatedPrivatePlaceGuidesInMapArea_userDoesntOwnAnyPlaceGuides_areaWithIDL_emptyResult() {
  //     List<PlaceGuide> testPlaceGuidesList =
  //         Arrays.asList(
  //             testInnerPrivatePlaceGuideE,
  //             testInnerPublicPlaceGuideE,
  //             testOuterPrivatePlaceGuideE,
  //             testOuterPublicPlaceGuideE,
  //             testInnerPrivatePlaceGuideF,
  //             testInnerPublicPlaceGuideF,
  //             testOuterPrivatePlaceGuideF,
  //             testOuterPublicPlaceGuideF);
  //     saveTestPlaceGuidesEntities(testPlaceGuidesList);
  //     List<PlaceGuide> result =
  //         placeGuideRepository.getCreatedPrivatePlaceGuidesInMapArea(
  //             OTHER_USER_ID, NORTH_EAST_CORNER_WITH_IDL, SOUTH_WEST_CORNER_WITH_IDL);
  //     assertTrue(result.isEmpty());
  //   }

  //   @Test
  //   public void
  //
  // getCreatedPrivatePlaceGuidesInMapArea_userOwnsPlaceGuides_areaWithIDL_resultHasInnerPublicPlaceGuidesOfUser() {
  //     List<PlaceGuide> testPlaceGuidesList =
  //         Arrays.asList(
  //             testInnerPrivatePlaceGuideE,
  //             testInnerPublicPlaceGuideE,
  //             testOuterPrivatePlaceGuideE,
  //             testOuterPublicPlaceGuideE,
  //             testInnerPrivatePlaceGuideF,
  //             testInnerPublicPlaceGuideF,
  //             testOuterPrivatePlaceGuideF,
  //             testOuterPublicPlaceGuideF);
  //     saveTestPlaceGuidesEntities(testPlaceGuidesList);
  //     List<PlaceGuide> result =
  //         placeGuideRepository.getCreatedPrivatePlaceGuidesInMapArea(
  //             CREATOR_E_ID, NORTH_EAST_CORNER_WITH_IDL, SOUTH_WEST_CORNER_WITH_IDL);
  //     List<PlaceGuide> expected = Arrays.asList(testInnerPrivatePlaceGuideE);
  //     assertTrue(compare(expected, result));
  //   }

  @After
  public void tearDown() {
    helper.tearDown();
  }
}
