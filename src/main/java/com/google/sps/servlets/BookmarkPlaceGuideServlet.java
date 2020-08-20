package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.RepositoryType;
import com.google.sps.placeGuide.PlaceGuide;
import com.google.sps.user.repository.UserRepository;
import com.google.sps.user.repository.UserRepositoryFactory;
import com.google.appengine.api.datastore.GeoPt;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.lang.IllegalStateException;
import java.io.IOException;

/**
 * This servlet handles bookmarking place guide and removing place guide id from user's 
 * {@code bookmarkedPlaceGuidesIds}.
 */
@WebServlet("/bookmark-place-guide")
public class BookmarkPlaceGuideServlet extends HttpServlet {
  
  private final String userId;

  // For production.
  public BookmarkPlaceGuideServlet() {
    this(UserServiceFactory.getUserService().getCurrentUser().getUserId());  
  }

  public BookmarkPlaceGuideServlet(String userId) {
    this.userId = userId;
  }

  private final static String PLACE_GUIDE_ID_PARAMETER = "placeGuideId";
  private final static String BOOKMARK_HANDLING_TYPE_PARAMETER = "bookmarkHandlingType";

  private final UserRepository userRepository = 
      UserRepositoryFactory.getUserRepository(RepositoryType.DATASTORE);

  private enum BookmarkPlaceGuideQueryType {
    BOOKMARK, 
    REMOVE
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long placeGuideId = Long.parseLong(request.getParameter(PLACE_GUIDE_ID_PARAMETER));
    String bookmarkHandlingType = request.getParameter(BOOKMARK_HANDLING_TYPE_PARAMETER);
    BookmarkPlaceGuideQueryType queryType = 
        BookmarkPlaceGuideQueryType.valueOf(bookmarkHandlingType);
    handlePlaceGuide(queryType, placeGuideId);
  }

  private void handlePlaceGuide(
      BookmarkPlaceGuideQueryType bookmarkPlaceGuideQueryType, long placeGuideId) {
    switch(bookmarkPlaceGuideQueryType) {
      case BOOKMARK:
        userRepository.bookmarkPlaceGuide();
        break;
      case REMOVE:
        userRepository.removeBookmarkedPlaceGuide();
        break;
      default:
        throw new IllegalStateException("Bookmark handling type does not exist!");
    }
  }
}