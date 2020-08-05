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

package com.google.sps;

import com.google.sps.user.User;
import com.google.sps.user.repository.UserRepository;
import com.google.sps.user.repository.UserRepositoryFactory;
import com.google.sps.data.RepositoryType;
import com.google.sps.user.repository.impl.DatastoreUserRepository;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

@RunWith(JUnit4.class)
public final class DatastoreUserRepositoryTest {
    private static final String ID = "userid";
    private static final String EMAIL = "user@gmail.com";
    private static final String NAME = "username";
    private static final String SELF_INTRODUCTION = "I am the user";
    private static final String IMG_URL = "/img.com";

    private static final String ID_B = "useridB";

    private static final String ID_C = "useridC";
    private static final String EMAIL_C = "user@gmail.com_C";
    private static final String NAME_C = "username_C";
    private static final String SELF_INTRODUCTION_C = "I am the user_C";
    private static final String IMG_URL_C = "/img.com_C";

    private final static User toSaveUser = new User.Builder(ID, EMAIL).setName(NAME).addSelfIntroduction(SELF_INTRODUCTION).addImgUrl(IMG_URL).build(); 
    private final static User toSaveUser_C = new User.Builder(ID_C, EMAIL_C).setName(NAME_C).addSelfIntroduction(SELF_INTRODUCTION_C).addImgUrl(IMG_URL_C).build(); 
    private static User toGetUser; 

    private final static UserRepository myUserRepository = UserRepositoryFactory.getUserRepository(RepositoryType.DATASTORE); 

    private final static LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }
    
    @Test
    public void getUser_existingUser_returnsUserEqualToSavedUser() {
        // Create entity to save.
        Entity userEntity = new Entity(DatastoreUserRepository.ENTITY_KIND, toSaveUser.getId());
        userEntity.setProperty(DatastoreUserRepository.NAME_PROPERTY, toSaveUser.getName());
        userEntity.setProperty(DatastoreUserRepository.EMAIL_PROPERTY, toSaveUser.getEmail());
        userEntity.setProperty(DatastoreUserRepository.PUBLIC_PORTFOLIO_PROPERTY, toSaveUser.portfolioIsPublic());
        userEntity.setProperty(DatastoreUserRepository.SELF_INTRODUCTION_PROPERTY, toSaveUser.getSelfIntroduction());
        userEntity.setProperty(DatastoreUserRepository.IMG_URL_PROPERTY, toSaveUser.getImgUrl());

        // Save entity to datastore.
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(userEntity);

        // Get user.
        toGetUser = myUserRepository.getUser(ID);
        assertEquals(toSaveUser, toGetUser);
    }

    @Test
    public void getUser_inexistentUser_returnsNull() {
        toGetUser = myUserRepository.getUser(ID_B);
        assertEquals(null, toGetUser);
    }

    @Test
    public void saveUser() {
        myUserRepository.saveUser(toSaveUser_C);

        // Get user.
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key userKey = KeyFactory.createKey(DatastoreUserRepository.ENTITY_KIND, ID_C);
        try {
            Entity userEntity = datastore.get(userKey);
            assertEquals(userEntity.getProperty(DatastoreUserRepository.NAME_PROPERTY), NAME_C);
            assertEquals(userEntity.getProperty(DatastoreUserRepository.EMAIL_PROPERTY), EMAIL_C);
            assertEquals(userEntity.getProperty(DatastoreUserRepository.SELF_INTRODUCTION_PROPERTY), SELF_INTRODUCTION_C);
            assertEquals(userEntity.getProperty(DatastoreUserRepository.PUBLIC_PORTFOLIO_PROPERTY), false);
            assertEquals(userEntity.getProperty(DatastoreUserRepository.IMG_URL_PROPERTY), IMG_URL_C);
        } catch (EntityNotFoundException e) {
            fail("Entity not found: " + e);
        }
    }
}