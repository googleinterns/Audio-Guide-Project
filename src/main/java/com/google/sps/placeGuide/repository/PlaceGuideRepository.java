package com.google.sps.placeGuide.repository;

import org.jetbrains.annotations.Nullable;
import com.google.sps.placeGuide.PlaceGuide;
import java.util.List;

/** Interface for handling place guide. */
public interface PlaceGuideRepository {

  public void savePlaceGuide(PlaceGuide placeGuide);

  // @return all public {@code PlaceGuide}.
  @Nullable
  public List<PlaceGuide> getAllPlaceGuides();

  // Finds all of the user's created place guides.
  @Nullable
  public List<PlaceGuide> getCreatedPlaceGuides(String creatorId);

  // Finds all of the user's created place guides that are public 
  // (available to be viewed by other users).
  @Nullable
  public List<PlaceGuide> getCreatedPublicPlaceGuides(String creatorId);

  // Finds all of the user's created place guides that are private
  // (not available to be viewed by other users).
  @Nullable
  public List<PlaceGuide> getCreatedPrivatePlaceGuides(String creatorId);

  public void deleteSelectedPlaceGuide(long placeGuideId);
}