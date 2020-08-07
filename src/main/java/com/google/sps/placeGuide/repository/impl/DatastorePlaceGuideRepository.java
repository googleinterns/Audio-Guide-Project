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
  public static final String USER_ENTITY_KIND = "User";
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
  
  /**
  * This method creates a new place guide {@code Entity} and store it to the list of created 
  * place guides.
  */
  @Override
  public void createAndStorePlaceGuide(String creatorId, PlaceGuide placeGuide) {
    Entity placeGuideEntity = getPlaceGuideEntity(placeGuide);
    datastore.put(placeGuideEntity);
    Key creatorKey = KeyFactory.createKey(USER_ENTITY_KIND, creatorId);
    Entity creatorEntity = datastore.get(creatorKey);
    List<Key> createdPlaceGuidesList = (List<Key>) creatorEntity.getProperty(
                                                            CREATED_PLACEGUIDES_LIST_PROPERTY);
    if (createdPlaceGuidesList == null) {
      createdPlaceGuidesList = new ArrayList<>();
    }
    createdPlaceGuidesList.add(placeGuideEntity.getKey());
    creatorEntity.setProperty(CREATED_PLACEGUIDES_LIST_PROPERTY, createdPlaceGuidesList);
    datastore.put(creatorEntity);
  }

  // Create place guide entity as parent entity and coordinate entity as child entity.
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

  /** 
  * Save public place guide and store it inside the user entity's 
  * savedPlaceGuidesList property.
  */
  public void savePlaceGuide(String saverId, String creatorId, String placeGuideId) {
    Key creatorKey = KeyFactory.createKey(USER_ENTITY_KIND, creatorId);
    Key placeGuideKey = KeyFactory.createKey(creatorKey, PLACEGUIDE_ENTITY_KIND, placeGuideId);
    Key saverKey = KeyFactory.createKey(USER_ENTITY_KIND, saverId);
    Entity saverEntity = datastore.get(saverKey);
    List<Key> savedPlaceGuidesList = (List<Key>) saverEntity.getProperty(
                                                            SAVED_PLACEGUIDES_LIST_PROPERTY);
    if (savedPlaceGuidesList == null) {
      savedPlaceGuidesList = new ArrayList<>();
    }
    savedPlaceGuidesList.add(placeGuideEntity.getKey());
    saverEntity.setProperty(SAVED_PLACEGUIDES_LIST_PROPERTY, savedPlaceGuidesList);
    datastore.put(saverEntity);
  }

  // For when navigating from the map and get all the place guides data in the
  // current window view.
  @Nullable
  public List<PlaceGuide> getPlaceGuidesList(List<PlaceCoordinate>);

  // For when trying to get personal created place guides for the info windows.
  @Nullable
  public List<PlaceGuide> getCreatedPlaceGuidesList(String creatorId);

  // For marking all the place guides that the user created on the current map window.
  @Nullable
  public List<PlaceGuide> getCreatedPlaceGuidesList(String creatorId, 
                                                    List<PlaceCoordinate> coordinatesList);

  // For when trying to get saved place guides for the info windows.
  @Nullable
  public List<PlaceGuide> getSavedPlaceGuidesList(String saverId);

  // For marking all the place guides that the user saved on the current map window.
  @Nullable
  public List<PlaceGuide> getSavedPlaceGuidesList(String saverId, 
                                                    List<PlaceCoordinate> coordinatesList);

  public void deleteSelectedCreatedPlaceGuide(String creatorId, String placeId);

  public void removeSelectedSavedPlaceGuide(String saverId, String placeId);

}