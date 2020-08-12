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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.*;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.datastore.*;
import com.google.sps.placeGuide.repository.impl.DatastorePlaceGuideRepository;

@RunWith(JUnit4.class)
public class PlaceGuideServletTest{

  private final LocalServiceTestHelper helper = new LocalServiceTestHelper();
  private HttpServletRequest request;
  private HttpServletResponse response;
  private PlaceGuideServlet PlaceGuideServlet;

  private static final String NAME_INPUT_STUB = "name";
  private static final String AUDIO_KEY_INPUT_STUB = "audioKey";
  private static final String LATITUDE_INPUT_STUB = "3.14";
  private static final String LONGITUDE_INPUT_STUB = "2.56";
  private static final String ID_INPUT_STUB = "12345";
  private static final String IS_PUBLIC_INPUT_STUB = "public";
  private static final String LENGTH_INPUT_STUB = "2";
  private static final String PLACE_ID_INPUT_STUB = "FWFWEF423423";
  private static final String DESCRIPTION_INPUT_STUB = "desc";

  private static final String USER_ID = "userId";
  private static final String NAME = "name";
  private static final String AUDIO_KEY = "audioKey";
  private static final GeoPt COORDINATE = new GeoPt(3.14, 2,56);
  private static final long ID = 12345;
  private static final boolean IS_PUBLIC = true;
  private static final long LENGTH = 2;
  private static final String PLACE_ID = "FWFWEF423423";
  private static final String DESCRIPTION = "desc";

  private PlaceGuide getTestPlaceGuide() {
    PlaceGuide testPlaceGuide = new PlaceGuide.Builder()
  }

  private Entity getTestPlaceGuideEntity() {
    
  }

  @Before
  public void setUp() {
    helper.setUp();

    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);

    placeGuideServlet = new PlaceGuideServlet(USER_ID);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PlaceGuide placeGuide = getPlaceGuideFromRequest(request);
    placeGuideRepository.savePlaceGuide(placeGuide);
    response.sendRedirect("/createPlaceGuide.html");
  }

  @Test
  public void doPost_databaseContainsSavedPlaceGuide() throws IOException {
    when(request.getParameter(NAME_INPUT)).thenReturn(NAME_INPUT_STUB);
    when(request.getParameter(AUDIO_KEY_INPUT)).thenReturn(AUDIO_KEY_INPUT_STUB);
    when(request.getParameter(LATITUDE_INPUT)).thenReturn(LATITUDE_INPUT_STUB);
    when(request.getParameter(LONGITUDE_INPUT)).thenReturn(LONGITUDE_INPUT_STUB);
    when(request.getParameter(ID_INPUT)).thenReturn(ID_INPUT_STUB);
    when(request.getParameter(IS_PUBLIC_INPUT)).thenReturn(IS_PUBLIC_INPUT_STUB);
    when(request.getParameter(LENGTH_INPUT)).thenReturn(LENGTH_INPUT_STUB);
    when(request.getParameter(PLACE_ID_INPUT)).thenReturn(PLACE_ID_INPUT_STUB);
    when(request.getParameter(DESCRIPTION_INPUT)).thenReturn(DESCRIPTION_INPUT_STUB);
    when(request.getParameter(IMG_KEY_INPUT)).thenReturn(IMG_KEY_INPUT_STUB)
    placeGuideServlet.doPost(request, response);
    try {
      Key placeGuideKey = KeyFactory.createKey(DatastorePlaceGuideRepository.ENTITY_KIND);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Entity placeGuideEntity = datastore.get(placeGuideEntity);
      
    } catch (EntityNotFoundException e) {
      fail("Entity not found: " + e);
    }
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }
}