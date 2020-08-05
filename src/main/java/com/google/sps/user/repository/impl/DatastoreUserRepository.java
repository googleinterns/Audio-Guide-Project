package com.google.sps.user.repository.impl;

import com.google.sps.user.User;
import com.google.sps.user.repository.UserRepository;
import org.jetbrains.annotations.Nullable;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;

/** Handles the storage of comments using the Datastore API. */ 
public class DatastoreUserRepository implements UserRepository {
    public static final String ENTITY_NAME = "User";
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
        Entity userEntity = new Entity(ENTITY_NAME, user.getId());
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
        Key userKey = KeyFactory.createKey(ENTITY_NAME, id);
        try {
            return getUserFromUserEntity(datastore.get(userKey));
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    @Nullable
    private User getUserFromUserEntity(Entity userEntity) {
        if (userEntity==null) {
            return null;
        }
        String id = (String) userEntity.getKey().getName();
        String name = (String) userEntity.getProperty(NAME_PROPERTY);
        String email = (String) userEntity.getProperty(EMAIL_PROPERTY);
        Boolean publicPortfolio = (Boolean) userEntity.getProperty(PUBLIC_PORTFOLIO_PROPERTY);
        String selfIntroduction = (String) userEntity.getProperty(SELF_INTRODUCTION_PROPERTY);
        String imgUrl = (String) userEntity.getProperty(IMG_URL_PROPERTY);
        User.Builder newUserBuilder = new User.Builder(id, email).setName(name).addSelfIntroduction(selfIntroduction).addImgUrl(imgUrl);
        if (publicPortfolio) {
            newUserBuilder.setPublicPortfolio();
        }
        return newUserBuilder.build();
    }
}