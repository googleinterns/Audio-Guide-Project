package com.google.sps;

import com.google.sps.user.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class UserTest {
    private static final String ID = "userid";
    private static final String SELF_INTRODUCTION = "I am the user";
    private static final String IMG_URL = "/img.com";

    @Test
    public void getId(){
        User newUser = new User.Builder(ID).build();
        Assert.assertEquals(newUser.getId(), ID);
    }

    @Test
    public void getSelfIntroduction(){
        //user with preset selfIntroduction
        User newUser = new User.Builder(ID).build();
        Assert.assertEquals(newUser.getSelfIntroduction(), null);

        //user with preset selfIntroduction
        newUser = new User.Builder(ID).addSelfIntroduction(SELF_INTRODUCTION).build();
        Assert.assertEquals(newUser.getSelfIntroduction(), SELF_INTRODUCTION);
    }

    @Test
    public void getImgUrl(){
        //user with preset imgUrl
        User newUser = new User.Builder(ID).build();
        Assert.assertEquals(newUser.getImgUrl(), null);

        //user with preset selfIntroduction
        newUser = new User.Builder(ID).addImgUrl(IMG_URL).build();
        Assert.assertEquals(newUser.getImgUrl(), IMG_URL);
    }
}