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

package com.google.sps.placeGuideInfo;

import com.google.sps.data.RepositoryType;
import com.google.sps.placeGuide.PlaceGuide;
import com.google.sps.user.User;
import com.google.sps.user.repository.UserRepository;
import com.google.sps.user.repository.UserRepositoryFactory;

/**
 * Class which unites a place guide's information with its creators information, and specifies the
 * placeGuide's relation to the currently logged in user (whether it was created and/or bookmarked
 * by the currently logged in user. This class is used to return the complete information about a
 * place guide from the servlets, in one object.
 */
public class PlaceGuideInfo {
  private User creator;
  private PlaceGuide placeGuide;
  private boolean createdByCurrentUser;
  private boolean bookmarkedByCurrentUser;
  private static final UserRepository userRepository =
      UserRepositoryFactory.getUserRepository(RepositoryType.DATASTORE);;

  /** For production. */
  public PlaceGuideInfo(PlaceGuide placeGuide, String currentUserId) {
    this.placeGuide = placeGuide;
    this.creator = userRepository.getUser(this.placeGuide.getCreatorId());
    if (this.creator == null) {
      throw new IllegalArgumentException("The creator of this PlaceGuide does not exist.");
    }
    User currentUser = userRepository.getUser(currentUserId);
    if (currentUser == null) {
      throw new IllegalArgumentException("The current user does not exist.");
    }
    this.bookmarkedByCurrentUser =
        currentUser.getBookmarkedPlaceGuidesIds().contains(this.placeGuide.getId());
    this.createdByCurrentUser = this.placeGuide.getCreatorId().equals(currentUserId);
  }

  /** For testing. */
  public PlaceGuideInfo(
      PlaceGuide placeGuide,
      User creator,
      boolean createdByCurrentUser,
      boolean bookmarkedByCurrentUser) {
    this.placeGuide = placeGuide;
    this.creator = creator;
    this.createdByCurrentUser = createdByCurrentUser;
    this.bookmarkedByCurrentUser = bookmarkedByCurrentUser;
  }

  public User getCreator() {
    return creator;
  }

  public PlaceGuide getPlaceGuide() {
    return placeGuide;
  }

  public boolean isCreatedByCurrentUser() {
    return this.createdByCurrentUser;
  }

  public boolean isBookmarkedByCurrentUser() {
    return this.bookmarkedByCurrentUser;
  }
}
