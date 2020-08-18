// Copyright 2020 Google LLC

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at

//     https://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.placeGuide;

import com.google.sps.user.User;
import com.google.sps.user.repository.UserRepository;
import com.google.sps.user.repository.UserRepositoryFactory;
import com.google.sps.data.RepositoryType;
import com.google.sps.placeGuide.PlaceGuide;
import org.jetbrains.annotations.Nullable;
import com.google.appengine.api.datastore.GeoPt;

/** 
 * Class which unites a place guide's information with its creators information. 
 * This class is used to return the complete information about a place guide
 * from the servlets, including the user's data, in one object.
 */
public class PlaceGuideWithUser {
    private User creator;
    private PlaceGuide placeGuide;
    private static final UserRepository userRepository =
          UserRepositoryFactory.getUserRepository(RepositoryType.DATASTORE);;

    private PlaceGuideWithUser(User creator, PlaceGuide placeGuide) {
        this.creator = creator;
        this.placeGuide = placeGuide;
    }

    public static PlaceGuideWithUser createPlaceGuideWithUserPair(PlaceGuide placeGuide) {
        User creator = userRepository.getUser(placeGuide.getCreatorId());
        return new PlaceGuideWithUser(creator, placeGuide);
    }

    public User getCreator() {
        return creator;
    }

    public PlaceGuide getPlaceGuide() {
        return placeGuide;
    }
}