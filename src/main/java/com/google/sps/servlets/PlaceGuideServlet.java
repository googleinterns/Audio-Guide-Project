package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.RepositoryType;
import com.google.sps.placeGuide.PlaceGuide;
import com.google.sps.placeGuide.repository.PlaceGuideRepository;
import com.google.sps.placeGuide.repository.PlaceGuideRepositoryFactory;
import com.google.appengine.api.datastore.GeoPt;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.lang.IllegalStateException;
import java.io.IOException;

/**
 * This servlet handles placeguide's data.
 */
@WebServlet("/place-guide-data")
public class PlaceGuideServlet extends HttpServlet {
  public static final String ID_INPUT = "id";
  public static final String NAME_INPUT = "name";
  public static final String AUDIO_KEY_INPUT = "audioKey";
  public static final String PLACE_ID_INPUT = "placeId";
  public static final String IS_PUBLIC_INPUT = "isPublic";
  public static final String IS_PUBLIC_INPUT_VALUE = "public";
  public static final String LATITUDE_INPUT = "latitude";
  public static final String LONGITUDE_INPUT = "longitude";
  public static final String DESCRIPTION_INPUT = "description";
  public static final String LENGTH_INPUT = "length";
  public static final String IMG_KEY_INPUT = "imgKey";
  public static final String PLACE_GUIDE_TYPE_PARAMETER = "placeGuideType";

  private final PlaceGuideRepository placeGuideRepository = PlaceGuideRepositoryFactory
                                                .getPlaceGuideRepository(RepositoryType.DATASTORE);
  private final String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();

  private enum PlaceGuideQueryType {ALL, CREATED_ALL, CREATED_PUBLIC, CREATED_PRIVATE }

  /**
   * Saves the recently submitted place guide data.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PlaceGuide placeGuide = getPlaceGuideFromRequest(request);
    placeGuideRepository.savePlaceGuide(placeGuide);
    response.sendRedirect("/createPlaceGuide.html");
  }

  /**
   * Returns the data of the placeguide(s) asked by the user who is currently logged in.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String placeGuideType = request.getParameter(PLACE_GUIDE_TYPE_PARAMETER);
    PlaceGuideQueryType queryType = PlaceGuideQueryType.valueOf(placeGuideType);
    List<PlaceGuide> placeGuides = getPlaceGuides(queryType);
    response.setContentType("application/json;");
    response.getWriter().println(convertToJsonUsingGson(placeGuides));
  }

  private List<PlaceGuide> getPlaceGuides(PlaceGuideQueryType placeGuideQueryType) {
    List<PlaceGuide> placeGuides;
    switch(placeGuideQueryType) {
      case ALL:
        placeGuides = placeGuideRepository.getAllPublicPlaceGuides();
        break;
      case CREATED_ALL:
        placeGuides = placeGuideRepository.getCreatedPlaceGuides(userId);
        break;
      case CREATED_PUBLIC:
        placeGuides = placeGuideRepository.getCreatedPublicPlaceGuides(userId);
        break;
      case CREATED_PRIVATE:
        placeGuides = placeGuideRepository.getCreatedPrivatePlaceGuides(userId);
        break;
      default:
        throw new IllegalStateException("Place Guide type does not exist!");
    }
    return placeGuides;
  }

  private PlaceGuide getPlaceGuideFromRequest(HttpServletRequest request) {
    String name = request.getParameter(NAME_INPUT);
    String audioKey = request.getParameter(AUDIO_KEY_INPUT); // Get from Blobstore.
    float latitude = Float.parseFloat(request.getParameter(LATITUDE_INPUT));
    float longitude = Float.parseFloat(request.getParameter(LONGITUDE_INPUT));
    GeoPt coordinate = new GeoPt(latitude, longitude);
    PlaceGuide.Builder newPlaceGuideBuilder = new PlaceGuide.Builder(name, audioKey, userId, 
                                                                     coordinate);
    String id = request.getParameter(ID_INPUT);
    if (!id.isEmpty()) {
        newPlaceGuideBuilder.setId(Long.parseLong(id));
    }
    
    String publicPlaceGuideStringValue = request.getParameter(IS_PUBLIC_INPUT);
    if (publicPlaceGuideStringValue.equals(IS_PUBLIC_INPUT_VALUE)) {
      newPlaceGuideBuilder.setPlaceGuideStatus(true);
    }
    String length = request.getParameter(LENGTH_INPUT);
    if (!length.isEmpty()) {
      newPlaceGuideBuilder.setLength(Long.parseLong(length));
    }
    String placeId = request.getParameter(PLACE_ID_INPUT);
    if (!placeId.isEmpty()) {
      newPlaceGuideBuilder.setPlaceId(placeId);
    }
    String description = request.getParameter(DESCRIPTION_INPUT);
    if (!description.isEmpty()) {
      newPlaceGuideBuilder.setDescription(description);
    }
    String imgKey = request.getParameter(IMG_KEY_INPUT);
    if (!imgKey.isEmpty()) {
      newPlaceGuideBuilder.setImageKey
    }
    return newPlaceGuideBuilder.build();
  }

  private String convertToJsonUsingGson(Object o) {
    Gson gson = new Gson();
    String json = gson.toJson(o);
    return json;
  }
}