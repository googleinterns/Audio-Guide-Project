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

package com.google.sps.user;

import java.util.Objects;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import com.google.appengine.api.datastore.Entity;
import com.google.sps.placeGuide.PlaceGuide;

/**
 * Stores the data related to one user.
 */
public class User {
  private final String id;
  private final String email;
  private final boolean publicPortfolio;
  @Nullable
  private final String name;
  @Nullable
  private final String selfIntroduction;
  @Nullable
  private final String imgKey;
  private final List<PlaceGuide> bookmarkedPlaceGuides;

  public static class Builder {
    // Required.
    private final String id;
    private final String email;
    // Optional.
    @Nullable
    private String name;
    @Nullable
    private String selfIntroduction;
    @Nullable
    private String imgKey;
    private final List<PlaceGuide> bookmarkedPlaceGuides;
    private boolean publicPortfolio = false;

    public Builder(String id, String email) {
      this.id = id;
      this.email = email;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder addImgKey(String imgKey) {
      this.imgKey = imgKey;
      return this;
    }

    public Builder addSelfIntroduction(String selfIntroduction) {
      this.selfIntroduction = selfIntroduction;
      return this;
    }

    public Builder setPublicPortfolio(boolean publicPortfolio) {
      this.publicPortfolio = publicPortfolio;
      return this;
    }

    public Builder setBookmarkedPlaceGuideId(long bookmarkedPlaceGuideId) {
      this.bookmarkedPlaceGuideId = bookmarkedPlaceGuideId;
      return this;
    }

    public User build() {
      return new User(this);
    }
  }

  private User(Builder builder) {
    this.id = builder.id;
    this.email = builder.email;
    this.name = builder.name;
    this.selfIntroduction = builder.selfIntroduction;
    this.imgKey = builder.imgKey;
    this.publicPortfolio = builder.publicPortfolio;
    this.bookmarkedPlaceGuideId = builder.bookmarkedPlaceGuideId;
  }

  public String getId() {
    return id;
  }

  public String getEmail() {
    return this.email;
  }

  public String getName() {
    return name;
  }

  public String getSelfIntroduction() {
    return selfIntroduction;
  }

  public String getImgKey() {
    return imgKey;
  }

  public boolean portfolioIsPublic() {
    return publicPortfolio;
  }

  public long getBookmarkedPlaceGuideId() {
    return bookmarkedPlaceGuideId;
  }

  @Override
  public boolean equals(Object o){
    if (!(o instanceof User)) {
      return false;
    }
    User user = (User) o;
    return user.getId().equals(this.getId());
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
