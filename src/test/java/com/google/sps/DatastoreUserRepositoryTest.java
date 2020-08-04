package com.google.sps;

import com.google.sps.user.User;
import com.google.sps.user.repository.UserRepository;
import com.google.sps.user.repository.UserRepositoryFactory;
import com.google.sps.user.repository.impl.DatastoreUserRepository;
import com.google.sps.data.RepositoryType;
import org.junit.Assert;
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
    private static final String ID_B = "useridB";
    private static final String EMAIL = "user@gmail.com";
    private static final String NAME = "username";
    private static final String SELF_INTRODUCTION = "I am the user";
    private static final String IMG_URL = "/img.com";
    private static final User toSaveUser = new User.Builder(ID, EMAIL).setName(NAME).addSelfIntroduction(SELF_INTRODUCTION).addImgUrl(IMG_URL).build();
    private static User toGetUser; 
    private static final UserRepository myUserRepository = UserRepositoryFactory.getUserRepository(RepositoryType.DATASTORE);

    private final LocalServiceTestHelper helper = 
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }
    
    @Test
    public void saveGetUser() {
        myUserRepository.saveUser(toSaveUser);
        toGetUser = myUserRepository.getUser(ID);
        Assert.assertEquals(toSaveUser, toGetUser);
    }

    @Test
    public void getNonExistingUser() {
        toGetUser = myUserRepository.getUser(ID_B);
        Assert.assertEquals(toGetUser, null);
    }
}