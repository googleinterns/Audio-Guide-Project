package com.google.sps.placeGuide.repository;

import org.jetbrains.annotations.Nullable;
import com.google.sps.placeGuide.PlaceGuide;
import com.google.appengine.api.datastore.GeoPt;
import java.util.List;

/** Interface for handling place guide. */
public interface PlaceGuideRepository {

  public void savePlaceGuide(PlaceGuide placeGuide);

  // @return all public {@code PlaceGuide}.
  public List<PlaceGuide> getAllPublicPlaceGuides();

  // Finds all of the user's created place guides.
  public List<PlaceGuide> getCreatedPlaceGuides(String creatorId);

  // Finds all of the user's created place guides that are public 
  // (available to be viewed by other users).
  public List<PlaceGuide> getCreatedPublicPlaceGuides(String creatorId);

  // Finds all of the user's created place guides that are private
  // (not available to be viewed by other users).
  public List<PlaceGuide> getCreatedPrivatePlaceGuides(String creatorId);

  public void deletePlaceGuide(long placeGuideId);

  // @return all public {@code PlaceGuide} which are within a given map area of rectangular form.
  public List<PlaceGuide> getAllPublicPlaceGuidesInMapArea(GeoPt northEastCorner, GeoPt southWestCorner);

  // Finds all of the user's created place guides which are within a given map area of rectangular form.
  public List<PlaceGuide> getCreatedPlaceGuidesInMapArea(String creatorId, GeoPt northEastCorner, GeoPt southWestCorner);

  // Finds all of the user's created place guides that are public and 
  // which are within a given map area of rectangular form.
  // (available to be viewed by other users).
  public List<PlaceGuide> getCreatedPublicPlaceGuidesInMapArea(String creatorId, GeoPt northEastCorner, GeoPt southWestCorner);

  // Finds all of the user's created place guides that are private and
  // which are within a given map area of rectangular form.
  // (not available to be viewed by other users).
  public List<PlaceGuide> getCreatedPrivatePlaceGuidesInMapArea(String creatorId, GeoPt northEastCorner, GeoPt southWestCorner);
}