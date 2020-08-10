package com.google.sps.placeGuide.repository;

import org.jetbrains.annotations.Nullable;
import com.google.sps.placeGuide.PlaceGuide;
import java.util.List;

/** Interface for handling place guide. */
public interface PlaceGuideRepository {

  public void savePlaceGuide(String creatorId, PlaceGuide placeGuide);

  // @return all public {@code PlaceGuide}.
  @Nullable
  public List<PlaceGuide> getAllPlaceGuides();

  @Nullable
  public List<PlaceGuide> getCreatedPlaceGuides(String creatorId);

  @Nullable
  public List<PlaceGuide> getCreatedPublicPlaceGuides(String creatorId);

  @Nullable
  public List<PlaceGuide> getCreatedPrivatePlaceGuides(String creatorId);

  public void deleteSelectedPlaceGuide(long placeGuideId);
}