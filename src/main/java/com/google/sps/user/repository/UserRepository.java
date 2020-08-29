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

package com.google.sps.user.repository;

import com.google.sps.user.User;
import org.jetbrains.annotations.Nullable;

/** Handles the storage of User data. */
public interface UserRepository {
  /** Saves the user's data in the database. */
  void saveUser(User user);

  /**
   * Returns the user with the given id data from the database. If no user exists with the given id,
   * it returns null.
   */
  @Nullable
  User getUser(String id);

  boolean existingUser(String id);

  void bookmarkPlaceGuide(long placeGuideId, String userId);

  void removeBookmarkedPlaceGuide(long placeGuideId, String userId);
}
