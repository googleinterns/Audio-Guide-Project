package com.google.sps.placeGuide.repository;

import org.jetbrains.annotations.Nullable;
import com.google.sps.placeGuide.PlaceGuide;
import java.util.List;

/** Interface for handling place guide. */
public interface PlaceGuideRepository {

  public void savePlaceGuide(PlaceGuide placeGuide);

  public void bookmarkPlaceGuide(long placeGuideId, String userId);

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

  public List<PlaceGuide> getBookmarkedPlaceGuides(String userId);

  public void deletePlaceGuide(long placeGuideId);
}