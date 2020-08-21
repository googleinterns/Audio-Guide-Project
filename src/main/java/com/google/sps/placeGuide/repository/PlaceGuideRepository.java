package com.google.sps.placeGuide.repository;

import org.jetbrains.annotations.Nullable;
import com.google.sps.placeGuide.PlaceGuide;
import java.util.List;
import com.google.sps.user.User;
import com.google.appengine.api.datastore.Key;

/** Interface for handling place guide. */
public interface PlaceGuideRepository {

  long saveAndGeneratePlaceGuideId();

  void savePlaceGuide(PlaceGuide placeGuide);

  @Nullable
  PlaceGuide getPlaceGuide(long placeGuideId);

  /** @return all public {@code PlaceGuide}. */
  List<PlaceGuide> getAllPublicPlaceGuides();

  /** Finds all of the user's created place guides. */
  List<PlaceGuide> getCreatedPlaceGuides(String creatorId);

  /**
  * Finds all of the user's created place guides that are public 
  * (available to be viewed by other users).
  */
  List<PlaceGuide> getCreatedPublicPlaceGuides(String creatorId);

  /**
  * Finds all of the user's created place guides that are private
  * (not available to be viewed by other users).
  */
  List<PlaceGuide> getCreatedPrivatePlaceGuides(String creatorId);

  List<PlaceGuide> getBookmarkedPlaceGuides(String userId);

  void deletePlaceGuide(long placeGuideId);

  boolean placeGuideExists(Key placeGuideEntityKey);
}