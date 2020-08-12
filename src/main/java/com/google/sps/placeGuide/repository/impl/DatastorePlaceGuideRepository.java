package com.google.sps.placeGuide.repository.impl;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.sps.placeGuide.PlaceGuide;
import com.google.appengine.api.datastore.GeoPt;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.jetbrains.annotations.Nullable;
import com.google.sps.placeGuide.repository.PlaceGuideRepository;

/** Class for handling place guide repository using datastore. */
public class DatastorePlaceGuideRepository implements PlaceGuideRepository{

  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  public static final String ENTITY_KIND = "PlaceGuide";
  public static final String NAME_PROPERTY = "name";
  public static final String AUDIO_KEY_PROPERTY = "audioKey";
  public static final String CREATOR_ID_PROPERTY = "creatorId";
  public static final String PLACE_ID_PROPERTY = "placeId";
  public static final String IS_PUBLIC_PROPERTY = "isPublic";
  public static final String COORD_PROPERTY = "coord";
  public static final String DESCRIPTION_PROPERTY = "description";
  public static final String LENGTH_PROPERTY = "length";
  public static final String IMG_KEY_PROPERTY = "imgKey";
  
  @Override
  public void savePlaceGuide(PlaceGuide placeGuide) {
    Entity placeGuideEntity = createPlaceGuideEntity(placeGuide);
    datastore.put(placeGuideEntity);
  }

  private Entity createPlaceGuideEntity(PlaceGuide placeGuide) {

    Entity placeGuideEntity;
    long placeGuideId = placeGuide.getId();
    if (Long.valueOf(placeGuideId) != null) {
      // Use the original placeGuide's ID for when user edits a place guide.
      placeGuideEntity = new Entity(ENTITY_KIND, placeGuideId);
    } else {
      // Let the ID be automatically created if the place guide is new.
      placeGuideEntity = new Entity(ENTITY_KIND);

      // Update the current placeGuide with the newly generated id.
      long id = placeGuideEntity.getKey().getId();
      String name = placeGuide.getName();
      String audioKey = placeGuide.getAudioKey();
      String creatorId = placeGuide.getCreatorId();
      String placeId = placeGuide.getPlaceId();
      boolean isPublic = placeGuide.isPublic();
      GeoPt coord = placeGuide.getCoordinate();
      String description = placeGuide.getDescription();
      long length = placeGuide.getLength();
      String imgKey = placeGuide.getImageKey();
      placeGuide = new PlaceGuide
                   .Builder(name, audioKey, creatorId, coord)
                   .setId(id).setPlaceId(placeId).setLength(length)
                   .setDescription(description).setImageKey(imgKey)
                   .setPlaceGuideStatus(isPublic).build();
    }
    placeGuideEntity.setProperty(NAME_PROPERTY, name);
    placeGuideEntity.setProperty(AUDIO_KEY_PROPERTY, audioKey);
    placeGuideEntity.setProperty(CREATOR_ID_PROPERTY, creatorId);
    placeGuideEntity.setProperty(PLACE_ID_PROPERTY, placeId);
    placeGuideEntity.setProperty(IS_PUBLIC_PROPERTY, isPublic);
    placeGuideEntity.setProperty(COORD_PROPERTY, coord);
    placeGuideEntity.setProperty(DESCRIPTION_PROPERTY, description);
    placeGuideEntity.setProperty(LENGTH_PROPERTY, length);
    placeGuideEntity.setProperty(IMG_KEY_PROPERTY, imgKey);

    return placeGuideEntity;
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
    Filter queryFilter = CompositeFilterOperator.and(Arrays.asList(
                        new FilterPredicate(CREATOR_ID_PROPERTY, FilterOperator.EQUAL, creatorId),
                        new FilterPredicate(IS_PUBLIC_PROPERTY, FilterOperator.EQUAL, true)));
    Query query = new Query(ENTITY_KIND).setFilter(queryFilter);
    return getPlaceGuidesList(query);
  }

  @Override
  public List<PlaceGuide> getCreatedPrivatePlaceGuides(String creatorId) {
    Filter queryFilter = CompositeFilterOperator.and(Arrays.asList(
                        new FilterPredicate(CREATOR_ID_PROPERTY, FilterOperator.EQUAL, creatorId),
                        new FilterPredicate(IS_PUBLIC_PROPERTY, FilterOperator.EQUAL, false)));
    Query query = new Query(ENTITY_KIND).setFilter(queryFilter);
    return getPlaceGuidesList(query);
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

  private PlaceGuide getPlaceGuideFromEntity(Entity placeGuideEntity) {
    long id = placeGuideEntity.getKey().getId();
    String name = (String) placeGuideEntity.getProperty(NAME_PROPERTY);
    String audioKey = (String) placeGuideEntity.getProperty(AUDIO_KEY_PROPERTY);
    String creatorId = (String) placeGuideEntity.getProperty(CREATOR_ID_PROPERTY);
    String placeId = (String) placeGuideEntity.getProperty(PLACE_ID_PROPERTY);
    GeoPt coord = (GeoPt) placeGuideEntity.getProperty(COORD_PROPERTY);
    boolean isPublic = (boolean) placeGuideEntity.getProperty(IS_PUBLIC_PROPERTY);
    String description = (String) placeGuideEntity.getProperty(DESCRIPTION_PROPERTY);
    long length = (long) placeGuideEntity.getProperty(LENGTH_PROPERTY);
    String imgKey = (String) placeGuideEntity.getProperty(IMG_KEY_PROPERTY);
    PlaceGuide placeGuide = new PlaceGuide
                            .Builder(name, audioKey, creatorId, coord)
                            .setId(id).setPlaceId(placeId).setLength(length)
                            .setDescription(description).setImageKey(imgKey)
                            .setPlaceGuideStatus(isPublic).build();
    return placeGuide;
  }

  @Override
  public void deletePlaceGuide(long placeGuideId) {
    Key placeGuideEntityKey = KeyFactory.createKey(ENTITY_KIND, placeGuideId);
    datastore.delete(placeGuideEntityKey);
  }
}