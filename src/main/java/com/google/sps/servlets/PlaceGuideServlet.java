package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.RepositoryType;
import com.google.sps.placeGuide.PlaceGuide;
import com.google.sps.placeGuide.repository.PlaceGuideRepository;
import com.google.sps.placeGuide.repository.PlaceGuideRepositoryFactory;
import com.google.sps.placeGuide.repository.impl.DatastorePlaceGuideRepository;
import com.google.appengine.api.datastore.GeoPt;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.lang.IllegalStateException;
import java.io.IOException;
import com.google.appengine.api.blobstore.*;
import org.jetbrains.annotations.Nullable;

/**
 * This servlet handles placeguide's data.
 */
@WebServlet("/place-guide-data")
public class PlaceGuideServlet extends HttpServlet {
  
  private final String userId;
  private final BlobstoreService blobstoreService;
  private final BlobInfoFactory blobInfoFactory;
  private final DatastoreService datastore;

  // For production.
  public PlaceGuideServlet() {
    this(UserServiceFactory.getUserService().getCurrentUser().getUserId(), 
         BlobstoreServiceFactory.getBlobstoreService(),
         new BlobInfoFactory(),
         DatastoreServiceFactory.getDatastoreService());  
  }

  // For testing.
  public PlaceGuideServlet(String userId, 
                           BlobstoreService blobstoreService, 
                           BlobInfoFactory blobInfoFactory, 
                           DatastoreService datastore) {
    this.userId = userId;
    this.blobstoreService = blobstoreService;
    this.blobInfoFactory = blobInfoFactory;
    this.datastore = datastore;
  }

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
  public static final String IMAGE_KEY_INPUT = "imageKey";
  public static final String DELETE_IMAGE_INPUT = "deleteImage";
  public static final String PLACE_NAME_INPUT = "placeName";
  public static final String PLACE_GUIDE_TYPE_PARAMETER = "placeGuideType";

  private final PlaceGuideRepository placeGuideRepository = 
      PlaceGuideRepositoryFactory.getPlaceGuideRepository(RepositoryType.DATASTORE);

  private enum PlaceGuideQueryType {
    ALL_PUBLIC, 
    CREATED_ALL, 
    CREATED_PUBLIC, 
    CREATED_PRIVATE,
    BOOKMARKED
  }

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
      case ALL_PUBLIC:
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
      case BOOKMARKED:
        placeGuides = placeGuideRepository.getBookmarkedPlaceGuides(userId);
      default:
        throw new IllegalStateException("Place Guide type does not exist!");
    }
    return placeGuides;
  }

  private PlaceGuide getPlaceGuideFromRequest(HttpServletRequest request) {
    String name = request.getParameter(NAME_INPUT);
    String audioKey = getUploadedFileBlobKey(request, AUDIO_KEY_INPUT);

    // The audioKey can be null if the user did not upload any audio file. Hence, it will
    // get the previous audioKey.
    if (audioKey == null) {
      audioKey
    }
    float latitude = Float.parseFloat(request.getParameter(LATITUDE_INPUT));
    float longitude = Float.parseFloat(request.getParameter(LONGITUDE_INPUT));
    GeoPt coordinate = new GeoPt(latitude, longitude);
    String idStringValue = request.getParameter(ID_INPUT);
    long id;
    if (!idStringValue.isEmpty()) {
      id = Long.parseLong(idStringValue);
    } else {
      // Create PlaceGuide entity id.
      Entity placeGuideEntity = new Entity(DatastorePlaceGuideRepository.ENTITY_KIND);
      datastore.put(placeGuideEntity);
      id = placeGuideEntity.getKey().getId();
    }
    PlaceGuide.Builder newPlaceGuideBuilder = new PlaceGuide.Builder(id, name, audioKey, userId, 
                                                                     coordinate);

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
    String imageKey = getUploadedFileBlobKey(request, IMAGE_KEY_INPUT);
    if (imageKey != null) {
      newPlaceGuideBuilder.setImageKey(imageKey);
    }
    String placeName = request.getParameter(PLACE_NAME_INPUT);
    if (!placeName.isEmpty()) {
      newPlaceGuideBuilder.setPlaceName(placeName);
    }
    return newPlaceGuideBuilder.build();
  }

  @Nullable
  private String getUploadedFileBlobKey(HttpServletRequest request, String formInputElementName) {
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formInputElementName);
    // User submitted form without selecting a file, so we can't get a URL. (dev server)
    if (blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }
    BlobKey blobKey = blobKeys.get(0);
    // User submitted form without selecting a file, so we can't get a URL. (live server)
    BlobInfo blobInfo = blobInfoFactory.loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }
    // Return the blobKey as a string.
    return blobKey.getKeyString();
  }

  private String convertToJsonUsingGson(Object o) {
    Gson gson = new Gson();
    String json = gson.toJson(o);
    return json;
  }
}