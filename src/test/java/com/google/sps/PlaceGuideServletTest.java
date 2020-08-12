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

  private Entity getTestPlaceGuideEntity() {
    Entity testPlaceGuideEntity = new Entity(DatastorePlaceGuideRepository.ENTITY_KIND, ID);
    testPlaceGuideEntity.setProperty(DatastorePlaceGuideRepository.NAME_PROPERTY, NAME);
    testPlaceGuideEntity.setProperty(DatastorePlaceGuideRepository.AUDIO_KEY_PROPERTY, AUDIO_KEY);
    testPlaceGuideEntity.setProperty(DatastorePlaceGuideRepository.CREATOR_ID_PROPERTY, USER_ID);
    testPlaceGuideEntity.setProperty(DatastorePlaceGuideRepository.PLACE_ID_PROPERTY, PLACE_ID);
    testPlaceGuideEntity.setProperty(DatastorePlaceGuideRepository.IS_PUBLIC_PROPERTY, IS_PUBLIC);
    testPlaceGuideEntity.setProperty(DatastorePlaceGuideRepository.COORDINATE_PROPERTY,
                                                                           COORDINATE);
    testPlaceGuideEntity.setProperty(DatastorePlaceGuideRepository.DESCRIPTION_PROPERTY, 
                                                                          DESCRIPTION);
    testPlaceGuideEntity.setProperty(DatastorePlaceGuideRepository.LENGTH_PROPERTY, LENGTH);
    testPlaceGuideEntity.setProperty(DatastorePlaceGuideRepository.IMAGE_KEY_PROPERTY, IMAGE_KEY);
    return testPlaceGuideEntity;
  }

  private PlaceGuide getTestPlaceGuide() {
    PlaceGuide testPlaceGuide = new PlaceGuide
                        .Builder(NAME, AUDIO_KEY, USER_ID, COORDINATE)
                        .setId(ID).setPlaceId(PLACE_ID).setLength(LENGTH)
                        .setDescription(DESCRIPTION).setImageKey(IMAGE_KEY)
                        .setPlaceGuideStatus(IS_PUBLIC).build();
    return testPlaceGuide;
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
  public void doPost_databaseContainsCreatedPlaceGuide() throws IOException, EntityNotFoundException {
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
    Entity testPlaceGuideEntity = getTestPlaceGuideEntity();
    assertTrue(testPlaceGuideEntity.equals(result));
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }
}