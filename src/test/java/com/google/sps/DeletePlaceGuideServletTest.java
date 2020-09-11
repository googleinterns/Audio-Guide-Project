package com.google.sps;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.appengine.api.datastore.*;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.placeGuide.PlaceGuide;
import com.google.sps.placeGuide.repository.PlaceGuideRepository;
import com.google.sps.placeGuide.repository.impl.DatastorePlaceGuideRepository;
import com.google.sps.servlets.DeletePlaceGuideServlet;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class DeletePlaceGuideServletTest {

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  public static final long A_PUBLIC_ID = 12345;
  public static final long B_PUBLIC_ID = 23456;
  public static final long A_PRIVATE_ID = 34567;
  public static final long B_PRIVATE_ID = 45678;
  public static final String NAME = "name";
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

  private final PlaceGuide testPublicPlaceGuideA =
      new PlaceGuide.Builder(A_PUBLIC_ID, NAME, AUDIO_KEY, CREATOR_A_ID, COORDINATE)
          .setPlaceId(PLACE_ID)
          .setPlaceGuideStatus(IS_PUBLIC)
          .setLength(LENGTH)
          .setDescription(DESCRIPTION)
          .setImageKey(IMAGE_KEY)
          .build();

  private final PlaceGuide previousTestPublicPlaceGuideA =
      new PlaceGuide.Builder(A_PUBLIC_ID, NAME, AUDIO_KEY, CREATOR_A_ID, COORDINATE)
          .setPlaceGuideStatus(IS_PUBLIC)
          .build();

  private final PlaceGuide testPrivatePlaceGuideA =
      new PlaceGuide.Builder(A_PRIVATE_ID, NAME, AUDIO_KEY, CREATOR_A_ID, COORDINATE)
          .setPlaceId(PLACE_ID)
          .setLength(LENGTH)
          .setDescription(DESCRIPTION)
          .setImageKey(IMAGE_KEY)
          .build();

  private final PlaceGuide testPublicPlaceGuideB =
      new PlaceGuide.Builder(B_PUBLIC_ID, NAME, AUDIO_KEY, CREATOR_B_ID, COORDINATE)
          .setPlaceId(PLACE_ID)
          .setPlaceGuideStatus(IS_PUBLIC)
          .setLength(LENGTH)
          .setDescription(DESCRIPTION)
          .setImageKey(IMAGE_KEY)
          .build();

  private final PlaceGuide testPrivatePlaceGuideB =
      new PlaceGuide.Builder(B_PRIVATE_ID, NAME, AUDIO_KEY, CREATOR_B_ID, COORDINATE)
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

  private DatastoreService datastore;
  private PlaceGuideRepository placeGuideRepository;
  private HttpServletRequest request;
  private HttpServletResponse response;

  @Before
  public void setUp() {
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
  }

  @Test(expected = EntityNotFoundException.class)
  public void doGet_deletePlaceGuide() throws EntityNotFoundException, IOException {
    when(request.getParameter("id")).thenReturn(String.valueOf(A_PUBLIC_ID));
    when(request.getParameter("currentUrl")).thenReturn("currentUrl");
    List<PlaceGuide> testPlaceGuidesList =
        Arrays.asList(
            testPublicPlaceGuideA,
            testPublicPlaceGuideB,
            testPrivatePlaceGuideB,
            testPrivatePlaceGuideA);
    saveTestPlaceGuidesEntities(testPlaceGuidesList);
    DeletePlaceGuideServlet deleteServlet = new DeletePlaceGuideServlet();
    deleteServlet.doGet(request, response);
    Key deletedEntityKey =
        KeyFactory.createKey(DatastorePlaceGuideRepository.ENTITY_KIND, A_PUBLIC_ID);
    Entity deletedEntity = datastore.get(deletedEntityKey);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }
}
