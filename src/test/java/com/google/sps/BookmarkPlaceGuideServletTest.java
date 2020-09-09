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

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import com.google.appengine.api.datastore.*;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.user.User;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class BookmarkPlaceGuideServletTest {
  private Map<String, Object> attributeToValue = new HashMap<>();
  private LocalServiceTestHelper helper;

  private HttpServletRequest request;
  private HttpServletResponse response;

  private static final String ID_A = "useridA";
  private static final String ID_B = "useridB";
  private static final String EMAIL = "user@gmail.com";
  private static final Set<Long> EMPTY_BOOKMARKED_PLACE_GUIDES_IDS = null;
  private static final Set<Long> BOOKMARKED_PLACE_GUIDES_IDS_A =
      new HashSet<>(
          Arrays.asList(
              (long) 1, (long) 2, (long) 3, (long) 4, (long) 5, (long) 6, (long) 7, (long) 8,
              (long) 9, (long) 10));
  private static final Set<Long> BOOKMARKED_PLACE_GUIDES_IDS_B =
      new HashSet<>(
          Arrays.asList(
              (long) 1, (long) 2, (long) 3, (long) 4, (long) 5, (long) 6, (long) 7, (long) 8,
              (long) 9, (long) 10, (long) 11, (long) 12, (long) 13, (long) 14, (long) 15, (long) 16,
              (long) 17, (long) 18, (long) 19, (long) 20));
  private static final String NAME = "username";
  private static final String SELF_INTRODUCTION = "I am the user";
  private static final String IMG_KEY = "/img.com";

  private final User userA =
      new User.Builder(ID_A, EMAIL)
          .setBookmarkedPlaceGuidesIds(BOOKMARKED_PLACE_GUIDES_IDS_A)
          .setName(NAME)
          .setPublicPortfolio(true)
          .addSelfIntroduction(SELF_INTRODUCTION)
          .addImgKey(IMG_KEY)
          .build();

  private final User userB =
      new User.Builder(ID_B, EMAIL)
          .setBookmarkedPlaceGuidesIds(BOOKMARKED_PLACE_GUIDES_IDS_B)
          .setName(NAME)
          .setPublicPortfolio(true)
          .addSelfIntroduction(SELF_INTRODUCTION)
          .addImgKey(IMG_KEY)
          .build();

  @Before
  public void setup() {
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }
}
