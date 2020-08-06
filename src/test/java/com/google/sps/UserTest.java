package com.google.sps;

import static org.junit.Assert.assertEquals;

import com.google.sps.user.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.util.Optional;

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
  public void getName_noNameProvided_returnsNull() {
    // User with no preset name.
    User newUser = new User.Builder(ID, EMAIL).build();
    assertEquals(Optional.empty(), newUser.getName());
  }

  @Test
  public void getName_nameProvidedNoOtherOptionalParameters_returnsName() {
    // User with preset selfIntroduction.
    User newUser = new User.Builder(ID, EMAIL).setName(NAME).build();
    assertEquals(Optional.of(NAME), newUser.getName());
  }

  @Test
  public void getName_nameProvidedPlusOtherOptionalParameters_returnsName() {
    // User with preset selfIntroduction, and other fields.
    User newUser =
        new User.Builder(ID, EMAIL)
            .setPublicPortfolio()
            .setName(NAME)
            .addSelfIntroduction(SELF_INTRODUCTION)
            .build();
    assertEquals(Optional.of(NAME), newUser.getName());
  }

  @Test
  public void getSelfIntroduction_noSelfIntroductionProvided_returnsNull() {
    // User with no preset selfIntroduction.
    User newUser = new User.Builder(ID, EMAIL).build();
    assertEquals(Optional.empty(), newUser.getSelfIntroduction());
  }

  @Test
  public void
      getSelfIntroduction_selfIntroductionProvidedNoOtherOptionalParameters_returnsSelfIntroduction() {
    // User with preset selfIntroduction.
    User newUser = new User.Builder(ID, EMAIL).addSelfIntroduction(SELF_INTRODUCTION).build();
    assertEquals(Optional.of(SELF_INTRODUCTION), newUser.getSelfIntroduction());
  }

  @Test
  public void
      getSelfIntroduction_selfIntroductionprovidedPlusOtherOptionalParameters_returnsSelfIntroduction() {
    // User with preset selfIntroduction, and other fields.
    User newUser =
        new User.Builder(ID, EMAIL)
            .setPublicPortfolio()
            .setName(NAME)
            .addSelfIntroduction(SELF_INTRODUCTION)
            .build();
    assertEquals(Optional.of(SELF_INTRODUCTION), newUser.getSelfIntroduction());
  }

  @Test
  public void getImgUrl_noImgUrlProvided_returnsNull() {
    // User with no preset imgUrl.
    User newUser = new User.Builder(ID, EMAIL).build();
    assertEquals(Optional.empty(), newUser.getImgUrl());
  }

  @Test
  public void getImgUrl_imgUrlProvidedNoOtherOptionalParameters_returnsImgUrl() {
    // User with preset imgUrl.
    User newUser = new User.Builder(ID, EMAIL).addImgUrl(IMG_URL).build();
    assertEquals(Optional.of(IMG_URL), newUser.getImgUrl());
  }

  @Test
  public void getImgUrl_imgUrlProvidedPlusOtherOptionalParameters_returnsImgUrl() {
    // User with preset imgUrl, and other fields.
    User newUser =
        new User.Builder(ID, EMAIL)
            .addSelfIntroduction(SELF_INTRODUCTION)
            .addImgUrl(IMG_URL)
            .build();
    assertEquals(Optional.of(IMG_URL), newUser.getImgUrl());
  }

  @Test
  public void portfolioIsPublic_portfolioNotSetToPublic_returnsFalse() {
    // User with no preset public/private fields.
    // Portfolio is private by default.
    User newUser = new User.Builder(ID, EMAIL).build();
    assertEquals(false, newUser.portfolioIsPublic());
  }

  @Test
  public void portfolioIsPublic_portfolioSetToPublic_returnsTrue() {
    // User with preset public/private fields.
    // User's portfolio is set to public.
    User newUser = new User.Builder(ID, EMAIL).setPublicPortfolio().build();
    assertEquals(true, newUser.portfolioIsPublic());
  }

  @Test
  public void portfolioIsPublic_portfolioSetToPublicPlusOtherOptionalFields_returnsTrue() {
    // User with preset public/private fields.
    // User's portfolio is set to public.
    User newUser =
        new User.Builder(ID, EMAIL)
            .setPublicPortfolio()
            .addSelfIntroduction(SELF_INTRODUCTION)
            .build();
    assertEquals(true, newUser.portfolioIsPublic());
  }
}
