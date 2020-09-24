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

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

/** Stores the data related to one user. */
public class User {
  public static final int MAX_NO_BOOKMARKED_PLACEGUIDES = 25;
  private final String id;
  private final String email;
  private final boolean publicPortfolio;
  @Nullable private final String name;
  @Nullable private final String selfIntroduction;
  @Nullable private final String imgKey;

  private final Set<Long> bookmarkedPlaceGuidesIds;

  public static class Builder {
    // Required.
    private final String id;
    private final String email;
    // Optional.
    @Nullable private String name;
    @Nullable private String selfIntroduction;
    @Nullable private String imgKey;
    private boolean publicPortfolio = false;
    private Set<Long> bookmarkedPlaceGuidesIds = new HashSet<>();

    public Builder(String id, String email) {
      this.id = id;
      this.email = email;
    }

    public Builder setBookmarkedPlaceGuidesIds(Set<Long> bookmarkedPlaceGuidesIds) {
      this.bookmarkedPlaceGuidesIds = bookmarkedPlaceGuidesIds;
      return this;
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

    public User build() {
      return new User(this);
    }
  }

  public User.Builder toBuilder() {
    User.Builder newUserBuilder =
        new User.Builder(id, email)
            .setBookmarkedPlaceGuidesIds(bookmarkedPlaceGuidesIds)
            .setName(name)
            .addSelfIntroduction(selfIntroduction)
            .setPublicPortfolio(publicPortfolio)
            .addImgKey(imgKey);
    return newUserBuilder;
  }

  private User(Builder builder) {
    this.id = builder.id;
    this.email = builder.email;
    this.name = builder.name;
    this.selfIntroduction = builder.selfIntroduction;
    this.imgKey = builder.imgKey;
    this.publicPortfolio = builder.publicPortfolio;
    this.bookmarkedPlaceGuidesIds = builder.bookmarkedPlaceGuidesIds;
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

  public Set<Long> getBookmarkedPlaceGuidesIds() {
    return Collections.unmodifiableSet(bookmarkedPlaceGuidesIds);
  }

  @Override
  public boolean equals(Object o) {
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
