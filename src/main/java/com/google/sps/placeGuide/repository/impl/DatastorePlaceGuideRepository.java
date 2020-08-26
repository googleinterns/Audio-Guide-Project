package com.google.sps.placeGuide.repository.impl;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.GeoRegion;
import com.google.appengine.api.datastore.Query.StContainsFilter;
import com.google.sps.data.RepositoryType;
import com.google.sps.placeGuide.PlaceGuide;
import com.google.sps.placeGuide.repository.PlaceGuideRepository;
import com.google.sps.user.User;
import com.google.sps.user.repository.UserRepository;
import com.google.sps.user.repository.UserRepositoryFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

/** Class for handling place guide repository using datastore. */
public class DatastorePlaceGuideRepository implements PlaceGuideRepository {

  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  public static final String ENTITY_KIND = "PlaceGuide";
  public static final String NAME_PROPERTY = "name";
  public static final String AUDIO_KEY_PROPERTY = "audioKey";
  public static final String CREATOR_ID_PROPERTY = "creatorId";
  public static final String PLACE_ID_PROPERTY = "placeId";
  public static final String IS_PUBLIC_PROPERTY = "isPublic";
  public static final String COORDINATE_PROPERTY = "coordinate";
  public static final String DESCRIPTION_PROPERTY = "description";
  public static final String LENGTH_PROPERTY = "length";
  public static final String IMAGE_KEY_PROPERTY = "imageKey";

  @Override
  public long saveAndGeneratePlaceGuideId() {
    Entity placeGuideEntity = new Entity(DatastorePlaceGuideRepository.ENTITY_KIND);
    datastore.put(placeGuideEntity);
    return placeGuideEntity.getKey().getId();
  }

  @Override
  public void savePlaceGuide(PlaceGuide placeGuide) {
    Entity placeGuideEntity = createPlaceGuideEntity(placeGuide);
    datastore.put(placeGuideEntity);
  }

  private Entity createPlaceGuideEntity(PlaceGuide placeGuide) {
    Entity placeGuideEntity = new Entity(ENTITY_KIND, placeGuide.getId());

    placeGuideEntity.setProperty(NAME_PROPERTY, placeGuide.getName());
    placeGuideEntity.setProperty(AUDIO_KEY_PROPERTY, placeGuide.getAudioKey());
    placeGuideEntity.setProperty(CREATOR_ID_PROPERTY, placeGuide.getCreatorId());
    placeGuideEntity.setProperty(PLACE_ID_PROPERTY, placeGuide.getPlaceId());
    placeGuideEntity.setProperty(IS_PUBLIC_PROPERTY, placeGuide.isPublic());
    placeGuideEntity.setProperty(COORDINATE_PROPERTY, placeGuide.getCoordinate());
    placeGuideEntity.setProperty(DESCRIPTION_PROPERTY, placeGuide.getDescription());
    placeGuideEntity.setProperty(LENGTH_PROPERTY, placeGuide.getLength());
    placeGuideEntity.setProperty(IMAGE_KEY_PROPERTY, placeGuide.getImageKey());

    return placeGuideEntity;
  }

  @Nullable
  @Override
  public PlaceGuide getPlaceGuide(long placeGuideId) {
    Key placeGuideEntityKey = KeyFactory.createKey(ENTITY_KIND, placeGuideId);
    try {
      Entity placeGuideEntity = datastore.get(placeGuideEntityKey);
      return getPlaceGuideFromEntity(placeGuideEntity);
    } catch (EntityNotFoundException err) {
      return null;
    }
  }

  @Override
  public List<PlaceGuide> getAllPublicPlaceGuides() {
    Filter queryFilter = new FilterPredicate(IS_PUBLIC_PROPERTY, FilterOperator.EQUAL, true);
    Query query = new Query(ENTITY_KIND).setFilter(queryFilter);
    return getPlaceGuidesList(query);
  }

  @Override
  public List<PlaceGuide> getCreatedPlaceGuides(String creatorId) {
    Filter queryFilter = new FilterPredicate(CREATOR_ID_PROPERTY, FilterOperator.EQUAL, creatorId);
    Query query = new Query(ENTITY_KIND).setFilter(queryFilter);
    return getPlaceGuidesList(query);
  }

  @Override
  public List<PlaceGuide> getCreatedPublicPlaceGuides(String creatorId) {
    Filter queryFilter =
        CompositeFilterOperator.and(
            Arrays.asList(
                new FilterPredicate(CREATOR_ID_PROPERTY, FilterOperator.EQUAL, creatorId),
                new FilterPredicate(IS_PUBLIC_PROPERTY, FilterOperator.EQUAL, true)));
    Query query = new Query(ENTITY_KIND).setFilter(queryFilter);
    return getPlaceGuidesList(query);
  }

  @Override
  public List<PlaceGuide> getCreatedPrivatePlaceGuides(String creatorId) {
    Filter queryFilter =
        CompositeFilterOperator.and(
            Arrays.asList(
                new FilterPredicate(CREATOR_ID_PROPERTY, FilterOperator.EQUAL, creatorId),
                new FilterPredicate(IS_PUBLIC_PROPERTY, FilterOperator.EQUAL, false)));
    Query query = new Query(ENTITY_KIND).setFilter(queryFilter);
    return getPlaceGuidesList(query);
  }

  @Override
  public List<PlaceGuide> getAllPublicPlaceGuidesInMapArea(
      GeoPt northEastCorner, GeoPt southWestCorner) {
    Filter publicityFilter = new FilterPredicate(IS_PUBLIC_PROPERTY, FilterOperator.EQUAL, true);
    Filter southBoundFilter =
        new FilterPredicate(
            COORDINATE_PROPERTY, FilterOperator.GREATER_THAN_OR_EQUAL, southWestCorner);
    Filter northBoundFilter =
        new FilterPredicate(
            COORDINATE_PROPERTY, FilterOperator.LESS_THAN_OR_EQUAL, northEastCorner);
    Filter queryFilter =
        CompositeFilterOperator.and(
            publicityFilter,
            southBoundFilter,
            northBoundFilter); // , mapAreaFilter1, mapAreaFilter2);
    Query query = new Query(ENTITY_KIND).setFilter(queryFilter);
    return getPlaceGuidesList(query);
  }

  @Override
  public List<PlaceGuide> getCreatedPlaceGuidesInMapArea(
      String creatorId, GeoPt northEastCorner, GeoPt southWestCorner) {
    Filter mapAreaFilter =
        new StContainsFilter(
            COORDINATE_PROPERTY, new GeoRegion.Rectangle(southWestCorner, northEastCorner));
    Filter creatorFilter =
        new FilterPredicate(CREATOR_ID_PROPERTY, FilterOperator.EQUAL, creatorId);
    Filter queryFilter = CompositeFilterOperator.and(mapAreaFilter, creatorFilter);
    Query query = new Query(ENTITY_KIND).setFilter(queryFilter);
    return // removePlaceGuidesOutsideLongitudeBounds(
    getPlaceGuidesList(query); // , southWestCorner.getLongitude(), northEastCorner.getLongitude());
  }

  @Override
  public List<PlaceGuide> getCreatedPublicPlaceGuidesInMapArea(
      String creatorId, GeoPt northEastCorner, GeoPt southWestCorner) {
    Filter publicityFilter = new FilterPredicate(IS_PUBLIC_PROPERTY, FilterOperator.EQUAL, true);
    Filter mapAreaFilter =
        new StContainsFilter(
            COORDINATE_PROPERTY, new GeoRegion.Rectangle(southWestCorner, northEastCorner));
    Filter creatorFilter =
        new FilterPredicate(CREATOR_ID_PROPERTY, FilterOperator.EQUAL, creatorId);
    Filter queryFilter = CompositeFilterOperator.and(publicityFilter, mapAreaFilter, creatorFilter);
    Query query = new Query(ENTITY_KIND).setFilter(queryFilter);
    return getPlaceGuidesList(query);
  }

  @Override
  public List<PlaceGuide> getCreatedPrivatePlaceGuidesInMapArea(
      String creatorId, GeoPt northEastCorner, GeoPt southWestCorner) {
    Filter publicityFilter = new FilterPredicate(IS_PUBLIC_PROPERTY, FilterOperator.EQUAL, false);
    Filter mapAreaFilter =
        new StContainsFilter(
            COORDINATE_PROPERTY, new GeoRegion.Rectangle(southWestCorner, northEastCorner));
    Filter creatorFilter =
        new FilterPredicate(CREATOR_ID_PROPERTY, FilterOperator.EQUAL, creatorId);
    Filter queryFilter = CompositeFilterOperator.and(publicityFilter, mapAreaFilter, creatorFilter);
    Query query = new Query(ENTITY_KIND).setFilter(queryFilter);
    return getPlaceGuidesList(query);
  }

  private List<PlaceGuide> removePlaceGuidesOutsideLongitudeBounds(
      List<PlaceGuide> placeGuides, float westBound, float eastBound) {
    List<PlaceGuide> placeGuidesCopy = new ArrayList<>(placeGuides);
    placeGuidesCopy.removeIf(
        placeGuide ->
            !isBetweenLongitudeBounds(
                placeGuide.getCoordinate().getLongitude(), westBound, eastBound));
    return placeGuidesCopy;
  }

  private boolean isBetweenLongitudeBounds(float longitude, float westBound, float eastBound) {
    if (westBound < eastBound) {
      return westBound <= longitude && longitude <= eastBound;
    } else {
      // The bounded area crosses the International Date Line(located at longitude 180/-180).
      // In this case, the westBound is positive and the eastBound is negative.
      if (longitude > 0) {
        return longitude > westBound;
      } else {
        return longitude < eastBound;
      }
    }
  }

  private List<PlaceGuide> getPlaceGuidesList(Query query) {
    PreparedQuery results = datastore.prepare(query);
    List<PlaceGuide> createdPlaceGuides = new ArrayList<>();
    for (Entity placeGuideEntity : results.asIterable()) {
      PlaceGuide placeGuide = getPlaceGuideFromEntity(placeGuideEntity);
      createdPlaceGuides.add(placeGuide);
    }
    return Collections.unmodifiableList(createdPlaceGuides);
  }

  @Override
  public List<PlaceGuide> getBookmarkedPlaceGuides(String userId) {
    UserRepository userRepository =
        UserRepositoryFactory.getUserRepository(RepositoryType.DATASTORE);
    List<PlaceGuide> bookmarkedPlaceGuides = new ArrayList<>();
    User user = userRepository.getUser(userId);
    if (user == null) {
      throw new IllegalStateException("Cannot get bookmarked place guides for non-existent user!");
    }
    Set<Long> bookmarkedIds = user.getBookmarkedPlaceGuidesIds();
    List<Long> bookmarkedIdsCopy = new ArrayList<>(bookmarkedIds);
    for (long placeGuideId : bookmarkedIdsCopy) {
      Key placeGuideEntityKey = KeyFactory.createKey(ENTITY_KIND, placeGuideId);
      try {
        Entity placeGuideEntity = datastore.get(placeGuideEntityKey);
        bookmarkedPlaceGuides.add(getPlaceGuideFromEntity(placeGuideEntity));
      } catch (EntityNotFoundException err) {
        System.out.println("Place Guide does not exist anymore.");
      }
    }
    return bookmarkedPlaceGuides;
  }

  private PlaceGuide getPlaceGuideFromEntity(Entity placeGuideEntity) {
    long id = placeGuideEntity.getKey().getId();
    String name = (String) placeGuideEntity.getProperty(NAME_PROPERTY);
    String audioKey = (String) placeGuideEntity.getProperty(AUDIO_KEY_PROPERTY);
    String creatorId = (String) placeGuideEntity.getProperty(CREATOR_ID_PROPERTY);
    String placeId = (String) placeGuideEntity.getProperty(PLACE_ID_PROPERTY);
    GeoPt coordinate = (GeoPt) placeGuideEntity.getProperty(COORDINATE_PROPERTY);
    boolean isPublic = (boolean) placeGuideEntity.getProperty(IS_PUBLIC_PROPERTY);
    String description = (String) placeGuideEntity.getProperty(DESCRIPTION_PROPERTY);
    long length = (long) placeGuideEntity.getProperty(LENGTH_PROPERTY);
    String imageKey = (String) placeGuideEntity.getProperty(IMAGE_KEY_PROPERTY);
    PlaceGuide placeGuide =
        new PlaceGuide.Builder(id, name, audioKey, creatorId, coordinate)
            .setPlaceId(placeId)
            .setLength(length)
            .setDescription(description)
            .setImageKey(imageKey)
            .setPlaceGuideStatus(isPublic)
            .build();
    return placeGuide;
  }

  @Override
  public void deletePlaceGuide(long placeGuideId) {
    Key placeGuideEntityKey = KeyFactory.createKey(ENTITY_KIND, placeGuideId);
    datastore.delete(placeGuideEntityKey);
  }

  @Override
  public boolean placeGuideExists(Key placeGuideEntityKey) {
    try {
      datastore.get(placeGuideEntityKey);
      return true;
    } catch (EntityNotFoundException err) {
      return false;
    }
  }
}
