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
  public static final String ALL = "all";
  public static final String CREATED_ALL = "created-all";
  public static final String CREATED_PUBLIC = "created-public";
  public static final String CREATED_PRIVATE = "created-private";
  public static final String NAME_INPUT = "name";
  public static final String AUDIO_KEY_INPUT = "audioKey";
  public static final String PLACE_ID_INPUT = "placeId";
  public static final String IS_PUBLIC_INPUT = "isPublic";
  public static final String IS_PUBLIC_INPUT_VALUE = "public";
  public static final String LATITUDE_INPUT = "latitude";
  public static final String LONGITUDE_INPUT = "longitude";
  public static final String DESC_INPUT = "desc";
  public static final String LENGTH_INPUT = "length";
  public static final String IMG_KEY_INPUT = "imgKey";
  public static final String PLACE_GUIDE_TYPE_PARAMETER = "placeGuideType";

  private final PlaceGuideRepository placeGuideRepository = PlaceGuideRepositoryFactory
                                                .getPlaceGuideRepository(RepositoryType.DATASTORE);
  private final String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();

  /**
   * Saves the recently submitted place guide data.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PlaceGuide placeGuide = getPlaceGuideFromRequest(request);
    placeGuideRepository.savePlaceGuide(userId, placeGuide);
    response.sendRedirect("/createPlaceGuide.html");
  }

  /**
   * Returns the data of the placeguide(s) asked by the user who is currently logged in.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String placeGuideType = request.getParameter(PLACE_GUIDE_TYPE_PARAMETER);
    List<PlaceGuide> placeGuides = null;
    switch(placeGuideType) {
      case ALL:
        placeGuides = placeGuideRepository.getAllPlaceGuides();
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
    response.setContentType("application/json;");
    response.getWriter().println(convertToJsonUsingGson(placeGuides));
  }

  private PlaceGuide getPlaceGuideFromRequest(HttpServletRequest request) {
    String name = request.getParameter(NAME_INPUT);
    String audioKey = request.getParameter(AUDIO_KEY_INPUT); // Get from Blobstore.
    String placeId = request.getParameter(PLACE_ID_INPUT);
    float latitude = Float.parseFloat(request.getParameter(LATITUDE_INPUT));
    float longitude = Float.parseFloat(request.getParameter(LONGITUDE_INPUT));
    GeoPt coord = new GeoPt(latitude, longitude);
    PlaceGuide.Builder newPlaceGuideBuilder = new PlaceGuide.Builder(name, audioKey, userId, 
                                                                     placeId, coord);
    String publicPlaceGuideStringValue = request.getParameter(IS_PUBLIC_INPUT);
    if (publicPlaceGuideStringValue.equals(IS_PUBLIC_INPUT_VALUE)) {
      newPlaceGuideBuilder.setPlaceGuideToPublic(true);
    }
    String length = request.getParameter(LENGTH_INPUT);
    if (!length.isEmpty()) {
      newPlaceGuideBuilder.setLength(Integer.parseInt(length));
    }
    String description = request.getParameter(DESC_INPUT);
    if (!description.isEmpty()) {
      newPlaceGuideBuilder.setDescription(description);
    }
    return newPlaceGuideBuilder.build();
  }

  private String convertToJsonUsingGson(Object o) {
    Gson gson = new Gson();
    String json = gson.toJson(o);
    return json;
  }
}