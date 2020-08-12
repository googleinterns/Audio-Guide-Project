package com.google.sps;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.servlets.PlaceGuideServlet;
import com.google.sps.placeGuide.PlaceGuide;
import com.google.sps.placeGuide.repository.impl.DatastorePlaceGuideRepository;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.*;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.datastore.*;

@RunWith(JUnit4.class)
public class PlaceGuideServletTest{

  private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
                                                            new LocalDatastoreServiceTestConfig());
  private HttpServletRequest request;
  private HttpServletResponse response;
  private PlaceGuideServlet placeGuideServlet;
  private DatastoreService datastore;

  private static final String NAME_INPUT_STUB = "name";
  private static final String AUDIO_KEY_INPUT_STUB = "audioKey";
  private static final String LATITUDE_INPUT_STUB = "3.14";
  private static final String LONGITUDE_INPUT_STUB = "2.56";
  private static final String ID_INPUT_STUB = "12345";
  private static final String IS_PUBLIC_INPUT_STUB = "public";
  private static final String LENGTH_INPUT_STUB = "2";
  private static final String PLACE_ID_INPUT_STUB = "FWFWEF423423";
  private static final String DESCRIPTION_INPUT_STUB = "desc";
  private static final String IMAGE_KEY_INPUT_STUB = "imageKey";

  private static final String USER_ID = "userId";
  private static final String NAME = "name";
  private static final String AUDIO_KEY = "audioKey";
  private static final GeoPt COORDINATE = new GeoPt((float) 3.14, (float) 2.56);
  private static final long ID = 12345;
  private static final boolean IS_PUBLIC = true;
  private static final long LENGTH = 2;
  private static final String PLACE_ID = "FWFWEF423423";
  private static final String DESCRIPTION = "desc";
  private static final String IMAGE_KEY = "imageKey";

  private static final String PREVIOUS_USER_ID = "userId";
  private static final String PREVIOUS_NAME = "name";
  private static final String PREVIOUS_AUDIO_KEY = "audioKey";
  private static final GeoPt PREVIOUS_COORDINATE = new GeoPt((float) 3.14, (float) 2.56);
  private static final long PREVIOUS_ID = 12345;
  private static final boolean PREVIOUS_IS_PUBLIC = true;

  private final PlaceGuide testPublicPlaceGuide = new PlaceGuide
                                          .Builder(NAME, AUDIO_KEY, USER_ID, COORDINATE)
                                          .setPlaceId(PLACE_ID)
                                          .setId(ID)
                                          .setPlaceGuideStatus(IS_PUBLIC)
                                          .setLength(LENGTH)
                                          .setDescription(DESCRIPTION)
                                          .setImageKey(IMAGE_KEY)
                                          .build();

  private final PlaceGuide previousTestPublicPlaceGuide = new PlaceGuide
                                          .Builder(NAME, AUDIO_KEY, USER_ID, COORDINATE)
                                          .setId(ID)
                                          .setPlaceGuideStatus(IS_PUBLIC)
                                          .build();

  private Entity getTestPlaceGuideEntity(PlaceGuide testPlaceGuide) {
    Entity testPlaceGuideEntity = new Entity(DatastorePlaceGuideRepository.ENTITY_KIND, testPlaceGuide.getId());
    testPlaceGuideEntity.setProperty(DatastorePlaceGuideRepository.NAME_PROPERTY, testPlaceGuide.getName());
    testPlaceGuideEntity.setProperty(DatastorePlaceGuideRepository.AUDIO_KEY_PROPERTY, testPlaceGuide.getAudioKey());
    testPlaceGuideEntity.setProperty(DatastorePlaceGuideRepository.CREATOR_ID_PROPERTY, testPlaceGuide.getCreatorId());
    testPlaceGuideEntity.setProperty(DatastorePlaceGuideRepository.PLACE_ID_PROPERTY, testPlaceGuide.getPlaceId());
    testPlaceGuideEntity.setProperty(DatastorePlaceGuideRepository.IS_PUBLIC_PROPERTY, testPlaceGuide.isPublic());
    testPlaceGuideEntity.setProperty(DatastorePlaceGuideRepository.COORDINATE_PROPERTY,
                                                                           testPlaceGuide.getCoordinate());
    testPlaceGuideEntity.setProperty(DatastorePlaceGuideRepository.DESCRIPTION_PROPERTY, 
                                                                          testPlaceGuide.getDescription());
    testPlaceGuideEntity.setProperty(DatastorePlaceGuideRepository.LENGTH_PROPERTY, testPlaceGuide.getLength());
    testPlaceGuideEntity.setProperty(DatastorePlaceGuideRepository.IMAGE_KEY_PROPERTY, testPlaceGuide.getImageKey());
    return testPlaceGuideEntity;
  }

  @Before
  public void setUp() {
    helper.setUp();

    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    datastore = DatastoreServiceFactory.getDatastoreService();

    placeGuideServlet = new PlaceGuideServlet(USER_ID);
  }

  @Test
  public void doPost_noPreviousPlaceGuideWithSameId_databaseContainsCreatedPlaceGuide() throws IOException, EntityNotFoundException {
    when(request.getParameter(PlaceGuideServlet.NAME_INPUT)).thenReturn(NAME_INPUT_STUB);
    when(request.getParameter(PlaceGuideServlet.AUDIO_KEY_INPUT)).thenReturn(AUDIO_KEY_INPUT_STUB);
    when(request.getParameter(PlaceGuideServlet.LATITUDE_INPUT)).thenReturn(LATITUDE_INPUT_STUB);
    when(request.getParameter(PlaceGuideServlet.LONGITUDE_INPUT)).thenReturn(LONGITUDE_INPUT_STUB);
    when(request.getParameter(PlaceGuideServlet.ID_INPUT)).thenReturn(ID_INPUT_STUB);
    when(request.getParameter(PlaceGuideServlet.IS_PUBLIC_INPUT)).thenReturn(IS_PUBLIC_INPUT_STUB);
    when(request.getParameter(PlaceGuideServlet.LENGTH_INPUT)).thenReturn(LENGTH_INPUT_STUB);
    when(request.getParameter(PlaceGuideServlet.PLACE_ID_INPUT)).thenReturn(PLACE_ID_INPUT_STUB);
    when(request.getParameter(PlaceGuideServlet.DESCRIPTION_INPUT)).thenReturn(DESCRIPTION_INPUT_STUB);
    when(request.getParameter(PlaceGuideServlet.IMAGE_KEY_INPUT)).thenReturn(IMAGE_KEY_INPUT_STUB);
    placeGuideServlet.doPost(request, response);
    Key placeGuideKey = KeyFactory.createKey(DatastorePlaceGuideRepository.ENTITY_KIND, ID);
    Entity result = datastore.get(placeGuideKey);
    Entity testPlaceGuideEntity = getTestPlaceGuideEntity(testPublicPlaceGuide);
    assertTrue(testPlaceGuideEntity.equals(result));
  }

  @Test
  public void doPost_previousPlaceGuideWithSameIdExists_databaseContainsEditedPlaceGuide() throws IOException, EntityNotFoundException {
    when(request.getParameter(PlaceGuideServlet.NAME_INPUT)).thenReturn(NAME_INPUT_STUB);
    when(request.getParameter(PlaceGuideServlet.AUDIO_KEY_INPUT)).thenReturn(AUDIO_KEY_INPUT_STUB);
    when(request.getParameter(PlaceGuideServlet.LATITUDE_INPUT)).thenReturn(LATITUDE_INPUT_STUB);
    when(request.getParameter(PlaceGuideServlet.LONGITUDE_INPUT)).thenReturn(LONGITUDE_INPUT_STUB);
    when(request.getParameter(PlaceGuideServlet.ID_INPUT)).thenReturn(ID_INPUT_STUB);
    when(request.getParameter(PlaceGuideServlet.IS_PUBLIC_INPUT)).thenReturn(IS_PUBLIC_INPUT_STUB);
    when(request.getParameter(PlaceGuideServlet.LENGTH_INPUT)).thenReturn(LENGTH_INPUT_STUB);
    when(request.getParameter(PlaceGuideServlet.PLACE_ID_INPUT)).thenReturn(PLACE_ID_INPUT_STUB);
    when(request.getParameter(PlaceGuideServlet.DESCRIPTION_INPUT)).thenReturn(DESCRIPTION_INPUT_STUB);
    when(request.getParameter(PlaceGuideServlet.IMAGE_KEY_INPUT)).thenReturn(IMAGE_KEY_INPUT_STUB);

    Entity previousPlaceGuideEntity = getTestPlaceGuideEntity(previousTestPublicPlaceGuide);
    datastore.put(previousPlaceGuideEntity);
    placeGuideServlet.doPost(request, response);
    Key placeGuideKey = KeyFactory.createKey(DatastorePlaceGuideRepository.ENTITY_KIND, ID);
    Entity result = datastore.get(placeGuideKey);
    Entity testPlaceGuideEntity = getTestPlaceGuideEntity(testPublicPlaceGuide);
    assertTrue(testPlaceGuideEntity.equals(result));
  }

  @Test
  public void doGet_queryAllPublicPlaceGuides_returnAllPublicPlaceGuides() throws IOException{
    when(request.getParameter(PLACE_GUIDE_TYPE_PARAMETER)).thenReturn("ALL");
    placeGuideServlet.doGet(request, response);
  }

  @Test
  public void doGet_queryAllCreatedPlaceGuides_returnAllPublicPlaceGuides() throws IOException{
    when(request.getParameter(PLACE_GUIDE_TYPE_PARAMETER)).thenReturn("CREATED_ALL");
  }

  @Test
  public void doGet_queryAllCreatedPublicPlaceGuides_returnAllPublicPlaceGuides() throws IOException{
    when(request.getParameter(PLACE_GUIDE_TYPE_PARAMETER)).thenReturn("CREATED-PUBLIC");
  }

  @Test
  public void doGet_queryAllCreatedPrivatePlaceGuides_returnAllPublicPlaceGuides() throws IOException{
    when(request.getParameter(PLACE_GUIDE_TYPE_PARAMETER)).thenReturn("CREATED-PRIVATE");
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }
}