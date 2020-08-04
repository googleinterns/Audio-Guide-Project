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
    private static final String EMAIL = "user@gmail.com";
    private static final String NAME = "username";
    private static final String SELF_INTRODUCTION = "I am the user";
    private static final String IMG_URL = "/img.com";

    @Test
    public void getId() {
        User newUser = new User.Builder(ID, EMAIL).build();
        Assert.assertEquals(newUser.getId(), ID);
    }

    @Test
    public void getEmail() {
        User newUser = new User.Builder(ID, EMAIL).build();
        Assert.assertEquals(newUser.getEmail(), EMAIL);
    }

    @Test
    public void getName() {
        // User with no preset name.
        User newUser = new User.Builder(ID, EMAIL).build();
        Assert.assertEquals(newUser.getName(), null);

        // User with preset selfIntroduction.
        newUser = new User.Builder(ID, EMAIL).setName(NAME).build();
        Assert.assertEquals(newUser.getName(), NAME);

        // User with preset selfIntroduction, and other fields.
        newUser = new User.Builder(ID, EMAIL).setPublicPortfolio().setName(NAME).addSelfIntroduction(SELF_INTRODUCTION).build();
        Assert.assertEquals(newUser.getName(), NAME);
    }

    @Test
    public void getSelfIntroduction() {
        // User with no preset selfIntroduction.
        User newUser = new User.Builder(ID, EMAIL).build();
        Assert.assertEquals(newUser.getSelfIntroduction(), null);

        // User with preset selfIntroduction.
        newUser = new User.Builder(ID, EMAIL).addSelfIntroduction(SELF_INTRODUCTION).build();
        Assert.assertEquals(newUser.getSelfIntroduction(), SELF_INTRODUCTION);

        // User with preset selfIntroduction, and other fields.
        newUser = new User.Builder(ID, EMAIL).setPublicPortfolio().setName(NAME).addSelfIntroduction(SELF_INTRODUCTION).build();
        Assert.assertEquals(newUser.getSelfIntroduction(), SELF_INTRODUCTION);
    }

    @Test
    public void getImgUrl() {
        // User with no preset imgUrl.
        User newUser = new User.Builder(ID, EMAIL).build();
        Assert.assertEquals(newUser.getImgUrl(), null);

        // User with preset imgUrl.
        newUser = new User.Builder(ID, EMAIL).addImgUrl(IMG_URL).build();
        Assert.assertEquals(newUser.getImgUrl(), IMG_URL);

        // User with preset imgUrl, and other fields.
        newUser = new User.Builder(ID, EMAIL).addSelfIntroduction(SELF_INTRODUCTION).addImgUrl(IMG_URL).build();
        Assert.assertEquals(newUser.getImgUrl(), IMG_URL);
    }

    @Test
    public void portfolioIsPublic() {
        // User with no preset public/private fields.
        User newUser = new User.Builder(ID, EMAIL).build();
        Assert.assertEquals(newUser.portfolioIsPublic(), false);

        // User with preset public/private fields.
        newUser = new User.Builder(ID, EMAIL).setPublicPortfolio().build();
        Assert.assertEquals(newUser.portfolioIsPublic(), true);

        // User with preset public/private fields.
        newUser = new User.Builder(ID, EMAIL).setPublicPortfolio().addSelfIntroduction(SELF_INTRODUCTION).build();
        Assert.assertEquals(newUser.portfolioIsPublic(), true);
    }
}