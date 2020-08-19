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
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import org.jetbrains.annotations.Nullable;
import com.google.sps.placeGuide.repository.PlaceGuideRepository;
import com.google.sps.user.repository.impl.DatastoreUserRepository;
import com.google.sps.user.repository.UserRepository;
import com.google.sps.user.repository.UserRepositoryFactory;
import com.google.sps.data.RepositoryType;
import com.google.sps.user.User;

/** Class for handling place guide repository using datastore. */
public class DatastorePlaceGuideRepository implements PlaceGuideRepository {

  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private final UserRepository userRepository =
      UserRepositoryFactory.getUserRepository(RepositoryType.DATASTORE);
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

  @Override
  public void bookmarkPlaceGuide(long placeGuideId, String userId) {
    // Check if the corresponding placeGuide is in the database.
    Key placeGuideEntityKey = KeyFactory.createKey(ENTITY_KIND, placeGuideId);
    try {
      Entity placeGuideEntity = datastore.get(placeGuideEntityKey);
      User user = userRepository.getUser(userId);
      if (user != null) {
        Set<Long> bookmarkedIds = user.getBookmarkedPlaceGuidesIds();
        Set<Long> bookmarkedIdsCopy = new HashSet<>(bookmarkedIds);
        bookmarkedIdsCopy.add(placeGuideId);

      // Update and save user with the updated {@code bookmarkedPlaceGuides}.
        saveUpdatedUser(user, bookmarkedIdsCopy);
      } else {
        throw new IllegalStateException("Non existent user cannot bookmark a place guide!");
      }

    } catch(EntityNotFoundException err) {
      System.out.println("No existing corresponding place guides.");
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

  @Override
  public List<PlaceGuide> getBookmarkedPlaceGuides(String userId) {
    List<PlaceGuide> bookmarkedPlaceGuides = new ArrayList<>();
    User user = userRepository.getUser(userId);
    if (user != null) {
      Set<Long> bookmarkedIds = user.getBookmarkedPlaceGuidesIds();
      List<Long> bookmarkedIdsCopy = new ArrayList<>(bookmarkedIds);
      int bookmarkedIdsCopyIndex = 0;
      while (bookmarkedIdsCopyIndex < bookmarkedIdsCopy.size()) {
        long placeGuideId = bookmarkedIdsCopy.get(bookmarkedIdsCopyIndex);
        Key placeGuideEntityKey = KeyFactory.createKey(ENTITY_KIND, placeGuideId);
        try {
          Entity placeGuideEntity = datastore.get(placeGuideEntityKey);
          bookmarkedPlaceGuides.add(getPlaceGuideFromEntity(placeGuideEntity));
          bookmarkedIdsCopyIndex++;
        } catch(EntityNotFoundException err) {
          System.out.println("PlaceGuide entity does not exist or has already been removed.");
          bookmarkedIdsCopy.remove(bookmarkedIdsCopyIndex);
        }
      }
      Set<Long> updatedBookmarkedIdsCopy = new HashSet<>(bookmarkedIdsCopy);
      // Update and save user with updated {@code bookmarkedPlaceGuides}.
      saveUpdatedUser(user, updatedBookmarkedIdsCopy);
      return bookmarkedPlaceGuides;
    } else {
      throw new IllegalStateException("Cannot get bookmarked place guides for non-existent user!");
    }
  }

  @Override
  public void removeBookmarkedPlaceGuide(long placeGuideId, String userId) {
    User user = userRepository.getUser(userId);
    if (user != null) {
      Set<Long> bookmarkedPlaceGuidesIds = user.getBookmarkedPlaceGuidesIds();
      Set<Long> bookmarkedPlaceGuidesIdsCopy = new HashSet<>(bookmarkedPlaceGuidesIds);
      bookmarkedPlaceGuidesIdsCopy.remove(placeGuideId);
      
      // Update user with the new set.
      saveUpdatedUser(user, bookmarkedPlaceGuidesIdsCopy);
    } else {
      throw new IllegalStateException("Cannot remove bookmarked place guides from non-existent user!");
    }
  }

  private void saveUpdatedUser(User user, Set<Long> updatedBookmarkedPlaceGuidesIds) {
    User updatedUser =
        new User.Builder(user.getId(), user.getEmail(), updatedBookmarkedPlaceGuidesIds)
        .setName(user.getName())
        .addSelfIntroduction(user.getSelfIntroduction())
        .setPublicPortfolio(user.portfolioIsPublic())
        .addImgKey(user.getImgKey())
        .build();
    userRepository.saveUser(updatedUser);
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
}