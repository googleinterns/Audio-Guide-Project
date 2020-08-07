package com.google.sps.placeGuide.repository;

import org.jetbrains.annotations.Nullable;
import com.google.sps.placeGuide.PlaceGuide;
import com.google.sps.placeGuide.PlaceCoordinate;
import java.util.List;

public interface PlaceGuideRepository {

  public void createAndStorePlaceGuide(PlaceGuide placeGuide);

  // For when user wants to save the user's own place guide or other user's place guide.
  public void savePlaceGuide(String creatorId, String placeId);

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