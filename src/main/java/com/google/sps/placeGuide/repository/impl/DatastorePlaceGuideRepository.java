package com.google.sps.placeGuide.repository.impl;

import com.google.appengine.api.datastore.*;
import com.google.sps.placeGuide.PlaceGuide;
import com.google.sps.placeGuide.PlaceCoordinate;
import java.util.List;
import java.util.ArrayList;
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
  public static final String DESC_PROPERTY = "desc";
  public static final String LENGTH_PROPERTY = "length";
  public static final String IMG_KEY_PROPERTY = "imgKey";
  
  @Override
  public void savePlaceGuide(String creatorId, PlaceGuide placeGuide) {
    Entity placeGuideEntity = createPlaceGuideEntity(placeGuide);
    datastore.put(placeGuideEntity);
  }

  private Entity createPlaceGuideEntity(PlaceGuide placeGuide) {

    // Let the ID be automatically created.
    Entity placeGuideEntity = new Entity(ENTITY_KIND);
    placeGuideEntity.setProperty(NAME_PROPERTY, placeGuide.getName());
    placeGuideEntity.setProperty(AUDIO_KEY_PROPERTY, placeGuide.getAudioKey());
    placeGuideEntity.setProperty(CREATOR_ID_PROPERTY, placeGuide.getCreatorId());
    placeGuideEntity.setProperty(PLACE_ID_PROPERTY, placeGuide.getPlaceId());
    placeGuideEntity.setProperty(IS_PUBLIC_PROPERTY, placeGuide.isPublic());
    placeGuideEntity.setProperty(COORD_PROPERTY, placeGuide.getCoordinate());
    placeGuideEntity.setProperty(DESC_PROPERTY, placeGuide.getDescription());
    placeGuideEntity.setProperty(LENGTH_PROPERTY, placeGuide.getLength());
    placeGuideEntity.setProperty(IMG_KEY_PROPERTY, placeGuide.getImageKey());

    return placeGuideEntity;
  }

  @Nullable
  @Override
  public List<PlaceGuide> getAllPlaceGuides() {
    Query query = new Query(ENTITY_KIND);
    return getPlaceGuidesList(query);
  }

  @Nullable
  @Override
  public List<PlaceGuide> getCreatedPlaceGuides(String creatorId) {
    Query query = new Query(ENTITY_KIND)
                  .setFilter(Query.FieldPredicate(CREATOR_ID_PROPERTY,
                                                  Query.FieldOperator.EQUAL,
                                                  creatorId));
    return getPlaceGuidesList(query);
  }

  @Nullable
  @Override
  public List<PlaceGuide> getCreatedPublicPlaceGuides(String creatorId) {
    Query query = new Query(ENTITY_KIND)
                  .setFilter(Query.FieldPredicate(CREATOR_ID_PROPERTY, 
                                                  Query.FieldOperator.EQUAL, 
                                                  creatorId))   
                  .setFilter(Query.FieldPredicate(IS_PUBLIC_PROPERTY,
                                                  Query.FieldOperator.EQUAL,
                                                  true));
    return getPlaceGuidesList(query);
  }

  @Nullable
  @Override
  public List<PlaceGuide> getCreatedPrivatePlaceGuides(String creatorId) {
    Query query = new Query(ENTITY_KIND)
                  .setFilter(Query.FieldPredicate(CREATOR_ID_PROPERTY, 
                                                  Query.FieldOperator.EQUAL, 
                                                  creatorId))   
                  .setFilter(Query.FieldPredicate(IS_PUBLIC_PROPERTY,
                                                  Query.FieldOperator.EQUAL,
                                                  false));
    return getPlaceGuidesList(query);
  }

  @Nullable 
  private List<PlaceGuide> getPlaceGuidesList(Query query) {
    PreparedQuery results = datastore.prepare(query);
    List<PlaceGuide> createdPlaceGuides = new ArrayList<>();
    for (Entity placeGuideEntity : results.asIterable()) {
      long id = placeGuideEntity.getKey().getId();
      String name = (String) placeGuideEntity.getProperty(NAME_PROPERTY);
      String audioKey = (String) placeGuideEntity.getProperty(AUDIO_KEY_PROPERTY);
      String creatorId = (String) placeGuideEntity.getProperty(CREATOR_ID_PROPERTY);
      String placeId = (String) placeGuideEntity.getProperty(PLACE_ID_PROPERTY);
      boolean isPublic = (boolean) placeGuideEntity.getProperty(IS_PUBLIC_PROPERTY);
      GeoPt coord = (GeoPt) placeGuideEntity.getProperty(COORD_PROPERTY);
      String desc = (String) placeGuideEntity.getProperty(DESC_PROPERTY);
      int length = (Integer) placeGuideEntity.getProperty(LENGTH_PROPERTY);
      String imgKey = (String) placeGuideEntity.getProperty(IMG_KEY_PROPERTY);
      PlaceGuide placeGuide = new PlaceGuide
                              .Builder(name, audioKey, creatorId, placeId, coord)
                              .setId(id).setLength(length).setDescription(desc)
                              .setPlaceGuideToPublic(isPublic).setImageKey(imgKey)
                              .build();
      createdPlaceGuides.add(placeGuide);
    }
    return Collections.unmodifiableList(createdPlaceGuides);
  }

  public void deleteSelectedPlaceGuide(String creatorId, String placeGuideId) {
    Query query = new Query(ENTITY_KIND)
                  .setFilter(Query.FieldPredicate(CREATOR_ID_PROPERTY,
                                                  Query.FieldOperator.EQUAL,
                                                  creatorId))
                  .setFilter(Query.FieldPredicate(ID_PROPERTY,
                                                  Query.FieldOperator.EQUAL,
                                                  placeGuideId));
    PreparedQuery result = datastore.prepare(query);
    datastore.delete(result.asSingleEntity().getKey());
  }
}