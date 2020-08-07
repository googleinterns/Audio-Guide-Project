package com.google.sps.placeGuide.repository.impl;

import com.google.appengine.api.datastore.*;
import com.google.sps.placeGuide.PlaceGuide;
import com.google.sps.placeGuide.PlaceCoordinate;
import java.util.List;
import java.util.ArrayList;
import org.jetbrains.annotations.Nullable;
import com.google.sps.placeGuide.repository.PlaceGuideRepository;

/** Class for handling place guide repository using datastore. */
public class DatastorePlaceGuideRepository implements PlaceGuideRepository{

  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  public static final String PLACEGUIDE_ENTITY_KIND = "PlaceGuide";
  public static final String NAME_PROPERTY = "name";
  public static final String AUDIO_KEY_PROPERTY = "audioKey";
  public static final String CREATOR_ID_PROPERTY = "creatorId";
  public static final String PLACE_ID_PROPERTY = "placeId";
  public static final String IS_PUBLIC_PROPERTY = "isPublic";
  public static final String COORD_PROPERTY = "coord";
  public static final String DESC_PROPERTY = "desc";
  public static final String LENGTH_PROPERTY = "length";
  public static final String IMG_KEY_PROPERTY = "imgKey";
  public static final String CREATED_PLACEGUIDES_LIST_PROPERTY = "createdPlaceGuidesList";
  public static final String SAVED_PLACEGUIDES_LIST_PROPERTY = "savedPlaceGuidesList";
  
  @Override
  public void savePlaceGuide(String creatorId, PlaceGuide placeGuide) {
    Entity placeGuideEntity = getPlaceGuideEntity(placeGuide);
    datastore.put(placeGuideEntity);
  }

  private Entity getPlaceGuideEntity(PlaceGuide placeGuide) {

    // Let the ID be automatically created.
    Entity placeGuideEntity = new Entity(PLACEGUIDE_ENTITY_KIND);
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
  public List<PlaceGuide> getCreatedPlaceGuidesList(String creatorId) {
    List<PlaceGuide>
    Query query = new Query(PLACEGUIDE_ENTITY_KIND)
                  .setFilter(Query.FieldPredicate(CREATOR_ID_PROPERTY, 
                                                  Query.FieldOperator.EQUAL, 
                                                  creatorId));
    
    PreparedQuery results = dataStoreService.prepare(query);
  }

  @Nullable
  public List<PlaceGuide> getCreatedPlaceGuidesList(String creatorId)

  public void deleteSelectedCreatedPlaceGuide(String creatorId, String placeGuideId);
}