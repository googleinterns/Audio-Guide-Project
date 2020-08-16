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

import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.RepositoryType;
import com.google.sps.user.User;
import com.google.sps.user.repository.UserRepository;
import com.google.sps.user.repository.UserRepositoryFactory;
import com.google.sps.user.repository.impl.DatastoreUserRepository;
import org.jetbrains.annotations.Nullable;
import com.google.appengine.api.datastore.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * This servlet handles users' data.
 */
@WebServlet("/user-data-servlet")
public class UserDataServlet extends HttpServlet {
  public static final String NAME_INPUT = "name";
  public static final String PUBLIC_PORTFOLIO_INPUT = "publicPortfolio";
  public static final String PUBLIC_PORTFOLIO_INPUT_PUBLIC_VALUE = "public";
  public static final String SELF_INTRODUCTION_INPUT = "selfIntroduction";
  public static final String IMG_KEY_INPUT = "imgKey";
  public static final String DELETE_IMG_INPUT = "deleteImg";

  private final UserRepository userRepository =
          UserRepositoryFactory.getUserRepository(RepositoryType.DATASTORE);;
  private final UserService userService = UserServiceFactory.getUserService();;

  private final BlobstoreService blobstoreService;
  private final BlobInfoFactory blobInfoFactory;

  /**
   * For production.
   */
  public UserDataServlet() {
    this(BlobstoreServiceFactory.getBlobstoreService(), new BlobInfoFactory());
  }

  /**
   * For testing purposes.
   */
  public UserDataServlet(BlobstoreService blobstoreService, BlobInfoFactory blobInfoFactory) {
    this.blobstoreService = blobstoreService;
    this.blobInfoFactory = blobInfoFactory;
  }

  /**
   * Saves the recently submitted userdata (updates it if the user already has some data saved) in
   * the database. Note: the user's name, self-introduction and portfolio status will be rewritten
   * with the new data, whatewer it is. (even if the new data is empty and previously the user had
   * some data saved) However, the user's photo is kept if they didn't submit a new one, unless the
   * user specifically exressed their preference to drop the photo from their profile.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    User prevUserData = userRepository.getUser(userService.getCurrentUser().getUserId());
    boolean prevUserDataExists = (Boolean) (prevUserData != null);
    if (prevUserDataExists
            && prevUserData.getImgKey() != null
            && !prevUserData.getImgKey().equals(user.getImgKey())) {
      // Delete previous image blob from blobstore, because it was overwritten or deleted.
      deleteBlobWithGivenKeyValue(prevUserData.getImgKey());
    }
    User user = getUserFromRequest(request, prevUserDataExists);
    userRepository.saveUser(user);
    response.sendRedirect("/index.html");
  }

  /**
   * Returns the data of the user who is currently logged in.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    User user = userRepository.getUser(userService.getCurrentUser().getUserId());
    response.setContentType("application/json;");
    response.getWriter().println(convertToJsonUsingGson(user));
  }

  private void deleteBlobWithGivenKeyValue(String keyValue) {
    BlobKey blobKey = new BlobKey(keyValue);
    blobstoreService.delete(blobKey);
  }

  private User getUserFromRequest(HttpServletRequest request, boolean prevUserDataExists) {
    String id = userService.getCurrentUser().getUserId();
    String email = userService.getCurrentUser().getEmail();
    List<long> bookmarkedPlaceGuides;
    if (prevUserDataExists) {
      Key prevUserKey = KeyFactory.createKey(DatastoreUserRepository.ENTITY_KIND, id);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Entity prevUserEntity = datastore.get(prevUserKey);
      // Get user's previous bookmarkedPlaceGuides.
      bookmarkedPlaceGuides = 
          (ArrayList) prevUserEntity
          .getProperty(DatastoreUserRepository.BOOKMARKED_PLACE_GUIDES_PROPERTY);
    } else {
      bookmarkedPlaceGuides = new ArrayList<>();
    }
    User.Builder newUserBuilder = new User.Builder(id, email, bookmarkedPlaceGuides);
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
