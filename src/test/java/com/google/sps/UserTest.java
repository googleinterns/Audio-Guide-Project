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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class UserTest {
    private static final String ID = "userid";
    private static final String ID_B = "useridB";
    private static final String EMAIL = "user@gmail.com";
    private static final String EMAIL_B = "userB@gmail.com";
    private static final String NAME = "username";
    private static final String SELF_INTRODUCTION = "I am the user";
    private static final String IMG_KEY = "imgkey1234";

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
        assertEquals(null, newUser.getName());
    }

    @Test
    public void getName_nameProvidedNoOtherOptionalParameters_returnsName() {
        // User with preset selfIntroduction.
        User newUser = new User.Builder(ID, EMAIL).setName(NAME).build();
        assertEquals(NAME, newUser.getName());
    }

    @Test
    public void getName_nameProvidedPlusOtherOptionalParameters_returnsName() {
        // User with preset selfIntroduction, and other fields.
        User newUser = new User.Builder(ID, EMAIL).setPublicPortfolio().setName(NAME).addSelfIntroduction(SELF_INTRODUCTION).build();
        assertEquals(NAME, newUser.getName());
    }

    @Test
    public void getSelfIntroduction_noSelfIntroductionProvided_returnsNull() {
        // User with no preset selfIntroduction.
        User newUser = new User.Builder(ID, EMAIL).build();
        assertEquals(null, newUser.getSelfIntroduction());
    }

    @Test 
    public void getSelfIntroduction_selfIntroductionProvidedNoOtherOptionalParameters_returnsSelfIntroduction() {
        // User with preset selfIntroduction.
        User newUser = new User.Builder(ID, EMAIL).addSelfIntroduction(SELF_INTRODUCTION).build();
        assertEquals(SELF_INTRODUCTION, newUser.getSelfIntroduction());
    }

    @Test
    public void getSelfIntroduction_selfIntroductionprovidedPlusOtherOptionalParameters_returnsSelfIntroduction() {
        // User with preset selfIntroduction, and other fields.
        User newUser = new User.Builder(ID, EMAIL).setPublicPortfolio().setName(NAME).addSelfIntroduction(SELF_INTRODUCTION).build();
        assertEquals(SELF_INTRODUCTION, newUser.getSelfIntroduction());
    }

    @Test
    public void getImgKey_noImgKeyProvided_returnsNull() {
        // User with no preset imgKey.
        User newUser = new User.Builder(ID, EMAIL).build();
        assertEquals(null, newUser.getImgKey());
    }

    @Test
    public void getImgKey_imgKeyProvidedNoOtherOptionalParameters_returnsImgKey() {
        // User with preset imgKey.
        User newUser = new User.Builder(ID, EMAIL).addImgKey(IMG_KEY).build();
        assertEquals(IMG_KEY, newUser.getImgKey());
    }

    @Test
    public void getImgKey_imgKeyProvidedPlusOtherOptionalParameters_returnsImgKey() {
        // User with preset imgKey, and other fields.
        User newUser = new User.Builder(ID, EMAIL).addSelfIntroduction(SELF_INTRODUCTION).addImgKey(IMG_KEY).build();
        assertEquals(IMG_KEY, newUser.getImgKey());
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
        User newUser = new User.Builder(ID, EMAIL).setPublicPortfolio().addSelfIntroduction(SELF_INTRODUCTION).build();
        assertEquals(true, newUser.portfolioIsPublic());
    }

    @Test
    public void equals_usersHaveEqualIds_returnsTrue() {
         User newUserA = new User.Builder(ID, EMAIL).build();
         User newUserB = new User.Builder(ID, EMAIL_B).build();
         assertEquals(newUserA, newUserB);
    }

    @Test
    public void equals_usersHaveDifferentIds_returnsFalse() {
         User newUserA = new User.Builder(ID, EMAIL).build();
         User newUserB = new User.Builder(ID_B, EMAIL_B).build();
         assertNotEquals(newUserA, newUserB);
    }
}