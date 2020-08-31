package com.google.sps.servlets;

import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.PlaceGuideQueryType;
import com.google.sps.data.RepositoryType;
import com.google.sps.placeGuide.PlaceGuide;
import com.google.sps.placeGuide.repository.PlaceGuideRepository;
import com.google.sps.placeGuide.repository.PlaceGuideRepositoryFactory;
import com.google.sps.placeGuideInfo.PlaceGuideInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.Nullable;

/** This servlet handles placeguide's data. */
@WebServlet("/place-guide-data")
public class PlaceGuideServlet extends HttpServlet {

  private final BlobstoreService blobstoreService;
  private final BlobInfoFactory blobInfoFactory;
  private final DatastoreService datastore;

  // For production.
  public PlaceGuideServlet() {
    this(
        BlobstoreServiceFactory.getBlobstoreService(),
        new BlobInfoFactory(),
        DatastoreServiceFactory.getDatastoreService());
  }

  // For testing.
  public PlaceGuideServlet(
      BlobstoreService blobstoreService,
      BlobInfoFactory blobInfoFactory,
      DatastoreService datastore) {
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
  public static final String PLACE_GUIDE_QUERY_TYPE_PARAMETER = "placeGuideType";
  public static final String REGION_CORNERS_PARAMETER = "regionCorners";

  private final PlaceGuideRepository placeGuideRepository =
      PlaceGuideRepositoryFactory.getPlaceGuideRepository(RepositoryType.DATASTORE);

  /** Saves the recently submitted place guide data. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PlaceGuide placeGuide = getPlaceGuideFromRequest(request);
    placeGuideRepository.savePlaceGuide(placeGuide);
    response.sendRedirect("/createPlaceGuide.html");
  }

  /** Returns the data of the placeguide(s) asked by the user who is currently logged in. */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String placeGuideQueryTypeString = request.getParameter(PLACE_GUIDE_QUERY_TYPE_PARAMETER);
    PlaceGuideQueryType placeGuideQueryType =
        PlaceGuideQueryType.valueOf(placeGuideQueryTypeString);
    GeoPt northEastCorner = null;
    GeoPt southWestCorner = null;
    if (placeGuideQueryType.requiresCoordinates()) {
      String regionCornersString = request.getParameter(REGION_CORNERS_PARAMETER);
      // On the client-side, the LatLngBound class's toUrlValue function will generate a string with
      // the values being comma-separated. The string is parsed here.
      String[] cornerCoordinates = regionCornersString.split(",");
      southWestCorner =
          new GeoPt(Float.parseFloat(cornerCoordinates[0]), Float.parseFloat(cornerCoordinates[1]));
      northEastCorner =
          new GeoPt(Float.parseFloat(cornerCoordinates[2]), Float.parseFloat(cornerCoordinates[3]));
    }
    List<PlaceGuide> placeGuides =
        getPlaceGuides(placeGuideQueryType, northEastCorner, southWestCorner);
    List<PlaceGuideInfo> placeGuideInfos = getPlaceGuideInfos(placeGuides);
    response.setContentType("application/json;");
    response.getWriter().println(convertToJsonUsingGson(placeGuideInfos));
  }

  private List<PlaceGuideInfo> getPlaceGuideInfos(List<PlaceGuide> placeGuides) {
    List<PlaceGuideInfo> placeGuideInfos = new ArrayList<>();
    for (PlaceGuide placeGuide : placeGuides) {
      placeGuideInfos.add(new PlaceGuideInfo(placeGuide, userId));
    }
    return placeGuideInfos;
  }

  private List<PlaceGuide> getPlaceGuides(
      PlaceGuideQueryType placeGuideQueryType, GeoPt northEastCorner, GeoPt southWestCorner) {
    List<PlaceGuide> placeGuides;
    String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();
    switch (placeGuideQueryType) {
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
        break;
      case ALL_PUBLIC_IN_MAP_AREA:
        placeGuides =
            placeGuideRepository.getAllPublicPlaceGuidesInMapArea(northEastCorner, southWestCorner);
        break;
      case CREATED_ALL_IN_MAP_AREA:
        placeGuides =
            placeGuideRepository.getCreatedPlaceGuidesInMapArea(
                userId, northEastCorner, southWestCorner);
        break;
      case CREATED_PUBLIC_IN_MAP_AREA:
        placeGuides =
            placeGuideRepository.getCreatedPublicPlaceGuidesInMapArea(
                userId, northEastCorner, southWestCorner);
        break;
      case CREATED_PRIVATE_IN_MAP_AREA:
        placeGuides =
            placeGuideRepository.getCreatedPrivatePlaceGuidesInMapArea(
                userId, northEastCorner, southWestCorner);
        break;
      default:
        throw new IllegalStateException("Place Guide type does not exist!");
    }
    return placeGuides;
  }

  private PlaceGuide getPlaceGuideFromRequest(HttpServletRequest request) {
    String name = request.getParameter(NAME_INPUT);
    long id;
    String idStringValue = request.getParameter(ID_INPUT);
    if (!idStringValue.isEmpty()) {
      id = Long.parseLong(idStringValue);
    } else {
      id = placeGuideRepository.saveAndGeneratePlaceGuideId();
    }
    String audioKey = getUploadedFileBlobKey(request, AUDIO_KEY_INPUT);
    // The audioKey can be null if the user did not upload any audio file. Hence, it will
    // get the previous audioKey.
    if (audioKey == null) {
      audioKey = placeGuideRepository.getPlaceGuide(id).getAudioKey();
    }

    String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();
    float latitude = Float.parseFloat(request.getParameter(LATITUDE_INPUT));
    float longitude = Float.parseFloat(request.getParameter(LONGITUDE_INPUT));
    GeoPt coordinate = new GeoPt(latitude, longitude);
    PlaceGuide.Builder newPlaceGuideBuilder =
        new PlaceGuide.Builder(id, name, audioKey, userId, coordinate);

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
    } else if (request.getParameterValues(DELETE_IMAGE_INPUT) == null) {
      imageKey = placeGuideRepository.getPlaceGuide(id).getImageKey();
      newPlaceGuideBuilder.setImageKey(imageKey);
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
