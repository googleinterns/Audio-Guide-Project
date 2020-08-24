package com.google.sps.placeGuide.repository;

import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Key;
import com.google.sps.placeGuide.PlaceGuide;
import java.util.List;
import org.jetbrains.annotations.Nullable;

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
   * Finds all of the user's created place guides that are public (available to be viewed by other
   * users).
   */
  List<PlaceGuide> getCreatedPublicPlaceGuides(String creatorId);

  /**
   * Finds all of the user's created place guides that are private (not available to be viewed by
   * other users).
   */
  List<PlaceGuide> getCreatedPrivatePlaceGuides(String creatorId);

  List<PlaceGuide> getBookmarkedPlaceGuides(String userId);

  void deletePlaceGuide(long placeGuideId);

  boolean placeGuideExists(Key placeGuideEntityKey);

  /**
   * @return all public {@code PlaceGuide} which are within a given map area of rectangular form.
   */
  List<PlaceGuide> getAllPublicPlaceGuidesInMapArea(GeoPt northEastCorner, GeoPt southWestCorner);

  /**
   * Finds all of the user's created place guides which are within a given map area of rectangular
   * form.
   */
  List<PlaceGuide> getCreatedPlaceGuidesInMapArea(
      String creatorId, GeoPt northEastCorner, GeoPt southWestCorner);

  /**
   * Finds all of the user's created place guides that are public and which are within a given map
   * area of rectangular form. (available to be viewed by other users).
   */
  List<PlaceGuide> getCreatedPublicPlaceGuidesInMapArea(
      String creatorId, GeoPt northEastCorner, GeoPt southWestCorner);

  /**
   * Finds all of the user's created place guides that are private and which are within a given map
   * area of rectangular form. (not available to be viewed by other users).
   */
  List<PlaceGuide> getCreatedPrivatePlaceGuidesInMapArea(
      String creatorId, GeoPt northEastCorner, GeoPt southWestCorner);
}
