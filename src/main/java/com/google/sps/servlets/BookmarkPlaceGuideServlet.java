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

package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.RepositoryType;
import com.google.sps.user.User;
import com.google.sps.user.repository.UserRepository;
import com.google.sps.user.repository.UserRepositoryFactory;
import com.google.sps.placeGuide.PlaceGuide;
import com.google.sps.placeGuide.repository.PlaceGuideRepository;
import com.google.sps.placeGuide.repository.PlaceGuideRepositoryFactory;
import org.jetbrains.annotations.Nullable;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * This servlet handles bookmarking place guide.
 */
@WebServlet("/bookmark-place-guide-servlet")
public class BookmarkPlaceGuideServlet extends HttpServlet {

  private final UserRepository userRepository =
      UserRepositoryFactory.getUserRepository(RepositoryType.DATASTORE);
  private final PlaceGuideRepository placeGuideRepository = 
      PlaceGuideRepositoryFactory.getPlaceGuideRepository(RepositoryType.DATASTORE);
  private final UserService userService = UserServiceFactory.getUserService();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    User user = getUserFromRequest(request);
    User prevUserData = userRepository.getUser(userService.getCurrentUser().getUserId());
    if (prevUserData != null
            && prevUserData.getImgKey() != null
            && !prevUserData.getImgKey().equals(user.getImgKey())) {
      // Delete previous image blob from blobstore, because it was overwritten or deleted.
      deleteBlobWithGivenKeyValue(prevUserData.getImgKey());
    }
    userRepository.saveUser(user);
    response.sendRedirect("/index.html");
  }

  private void deleteBlobWithGivenKeyValue(String keyValue) {
    BlobKey blobKey = new BlobKey(keyValue);
    blobstoreService.delete(blobKey);
  }

  private User getUserBookmarkedPlaceGuides(HttpServletRequest request) {
    String id = userService.getCurrentUser().getUserId();
    String email = userService.getCurrentUser().getEmail();
    User.Builder newUserBuilder = new User.Builder(id, email);
    String name = request.getParameter(NAME_INPUT);
    if (!name.isEmpty()) {
      newUserBuilder.setName(name);
    }
    String selfIntroduction = request.getParameter(SELF_INTRODUCTION_INPUT);
    if (!selfIntroduction.isEmpty()) {
      newUserBuilder.addSelfIntroduction(selfIntroduction);
    }
    String publicPortfolioStringValue = request.getParameter(PUBLIC_PORTFOLIO_INPUT);
    if (publicPortfolioStringValue.equals(PUBLIC_PORTFOLIO_INPUT_PUBLIC_VALUE)) {
      newUserBuilder.setPublicPortfolio(true); // False by default.
    }
    String imgKey = getUploadedFileBlobKey(request, IMG_KEY_INPUT);
    if (imgKey != null) {
      // The user submitted a new photo, save it in the database, later overwrite the old one.
      newUserBuilder.addImgKey(imgKey);
    } else if (request.getParameterValues(DELETE_IMG_INPUT) == null) {
      // The user didn't submit a new photo, but they didn't choose to delete the old one either.
      // Keep old photo in the database.
      imgKey = userRepository.getUser(userService.getCurrentUser().getUserId()).getImgKey();
      newUserBuilder.addImgKey(imgKey);
    }
    return newUserBuilder.build();
  }

  @Nullable
  private String getUploadedFileBlobKey(HttpServletRequest request, String formInputElementName) {
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formInputElementName);
    // User submitted form without selecting a file, so we can't get a URL. (dev server)
    if (blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }
    BlobKey blobKey = blobKeys.get(0);
    // User submitted form without selecting a file, so we can't get a URL. (live server)
    BlobInfo blobInfo = blobInfoFactory.loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }
    // Return the blobKey as a string.
    return blobKey.getKeyString();
  }

  private String convertToJsonUsingGson(Object o) {
    Gson gson = new Gson();
    String json = gson.toJson(o);
    return json;
  }
}
