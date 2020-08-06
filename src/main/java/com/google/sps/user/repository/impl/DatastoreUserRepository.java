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

package com.google.sps.user.repository.impl;

import com.google.appengine.api.datastore.*;
import com.google.sps.user.User;
import com.google.sps.user.repository.UserRepository;
import org.jetbrains.annotations.Nullable;

/**
 * Handles the storage of comments using the Datastore API.
 */
public class DatastoreUserRepository implements UserRepository {
  public static final String ENTITY_KIND = "User";
  public static final String NAME_PROPERTY = "name";
  public static final String EMAIL_PROPERTY = "email";
  public static final String PUBLIC_PORTFOLIO_PROPERTY = "publicPortfolio";
  public static final String SELF_INTRODUCTION_PROPERTY = "selfIntroduction";
  public static final String IMG_URL_PROPERTY = "imgUrl";

  @Override
  public void saveUser(User user) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(getUserEntity(user));
  }

  private Entity getUserEntity(User user) {
    Entity userEntity = new Entity(ENTITY_KIND, user.getId());
    userEntity.setProperty(NAME_PROPERTY, user.getName());
    userEntity.setProperty(EMAIL_PROPERTY, user.getEmail());
    userEntity.setProperty(PUBLIC_PORTFOLIO_PROPERTY, user.portfolioIsPublic());
    userEntity.setProperty(SELF_INTRODUCTION_PROPERTY, user.getSelfIntroduction());
    userEntity.setProperty(IMG_URL_PROPERTY, user.getImgUrl());
    return userEntity;
  }

  @Override
  @Nullable
  public User getUser(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key userKey = KeyFactory.createKey(ENTITY_KIND, id);
    try {
      return getUserFromUserEntity(datastore.get(userKey));
    } catch (EntityNotFoundException e) {
      return null;
    }
  }

  @Nullable
  private User getUserFromUserEntity(Entity userEntity) {
    if (userEntity == null) {
      return null;
    }
    String id = (String) userEntity.getKey().getName();
    String name = (String) userEntity.getProperty(NAME_PROPERTY);
    String email = (String) userEntity.getProperty(EMAIL_PROPERTY);
    Boolean publicPortfolio = (Boolean) userEntity.getProperty(PUBLIC_PORTFOLIO_PROPERTY);
    String selfIntroduction = (String) userEntity.getProperty(SELF_INTRODUCTION_PROPERTY);
    String imgUrl = (String) userEntity.getProperty(IMG_URL_PROPERTY);
    User.Builder newUserBuilder =
            new User.Builder(id, email).setName(name).addSelfIntroduction(selfIntroduction).setPublicPortfolio(publicPortfolio).addImgUrl(imgUrl);
    return newUserBuilder.build();
  }
}