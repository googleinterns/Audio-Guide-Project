// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.repository;

import org.jetbrains.annotations.Nullable;
import com.google.sps.placeGuide.PlaceGuide;
import com.google.sps.placeGuide.PlaceCoordinate;
import java.util.List;

public interface PlaceGuideRepository {

    public void createAndSavePlaceGuide(PlaceGuide placeGuide);

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
    public List<PlaceGuide> getCreatedPlaceGuidesList(String creatorId, List<PlaceCoordinate> coordinatesList);

    // For when trying to get saved place guides for the info windows.
    @Nullable
    public List<PlaceGuide> getSavedPlaceGuidesList(String saverId);

    // For marking all the place guides that the user saved on the current map window.
    @Nullable
    public List<PlaceGuide> getSavedPlaceGuidesList(String saverId, List<PlaceCoordinate> coordinatesList);

    public void deleteSelectedPlaceGuide(String creatorId, String placeId);
    
}