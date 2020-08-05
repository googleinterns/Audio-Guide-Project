package com.google.sps;

import com.google.sps.user.User;
import static org.junit.Assert.assertEquals;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class UserTest {
    private static final String ID = "userid";
    private static final String EMAIL = "user@gmail.com";
    private static final String NAME = "username";
    private static final String SELF_INTRODUCTION = "I am the user";
    private static final String IMG_URL = "/img.com";

    @Test
    public void getId() {
        User newUser = new User.Builder(ID, EMAIL).build();
        assertEquals(ID, newUser.getId());
    }

    @Test
    public void getEmail() {
        User newUser = new User.Builder(ID, EMAIL).build();
        assertEquals(EMAIL, newUser.getEmail());
    }

    @Test
    public void getName() {
        // User with no preset name.
        User newUser = new User.Builder(ID, EMAIL).build();
        assertEquals(null, newUser.getName());

        // User with preset selfIntroduction.
        newUser = new User.Builder(ID, EMAIL).setName(NAME).build();
        assertEquals(NAME, newUser.getName());

        // User with preset selfIntroduction, and other fields.
        newUser = new User.Builder(ID, EMAIL).setPublicPortfolio().setName(NAME).addSelfIntroduction(SELF_INTRODUCTION).build();
        Assert.assertEquals(NAME, newUser.getName());
    }

    @Test
    public void getSelfIntroduction() {
        // User with no preset selfIntroduction.
        User newUser = new User.Builder(ID, EMAIL).build();
        assertEquals(null, newUser.getSelfIntroduction());

        // User with preset selfIntroduction.
        newUser = new User.Builder(ID, EMAIL).addSelfIntroduction(SELF_INTRODUCTION).build();
        assertEquals(SELF_INTRODUCTION, newUser.getSelfIntroduction());

        // User with preset selfIntroduction, and other fields.
        newUser = new User.Builder(ID, EMAIL).setPublicPortfolio().setName(NAME).addSelfIntroduction(SELF_INTRODUCTION).build();
        assertEquals(SELF_INTRODUCTION, newUser.getSelfIntroduction());
    }

    @Test
    public void getImgUrl() {
        // User with no preset imgUrl.
        User newUser = new User.Builder(ID, EMAIL).build();
        assertEquals(null, newUser.getImgUrl());

        // User with preset imgUrl.
        newUser = new User.Builder(ID, EMAIL).addImgUrl(IMG_URL).build();
        assertEquals(IMG_URL, newUser.getImgUrl());

        // User with preset imgUrl, and other fields.
        newUser = new User.Builder(ID, EMAIL).addSelfIntroduction(SELF_INTRODUCTION).addImgUrl(IMG_URL).build();
        assertEquals(IMG_URL, newUser.getImgUrl());
    }

    @Test
    public void portfolioIsPublic() {
        // User with no preset public/private fields.
        User newUser = new User.Builder(ID, EMAIL).build();
        assertEquals(false, newUser.portfolioIsPublic());

        // User with preset public/private fields.
        newUser = new User.Builder(ID, EMAIL).setPublicPortfolio().build();
        assertEquals(true, newUser.portfolioIsPublic());

        // User with preset public/private fields.
        newUser = new User.Builder(ID, EMAIL).setPublicPortfolio().addSelfIntroduction(SELF_INTRODUCTION).build();
        assertEquals(true, newUser.portfolioIsPublic());
    }
}