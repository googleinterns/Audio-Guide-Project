package com.google.sps.servlets;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.RepositoryType;
import com.google.sps.user.User;
import com.google.sps.user.repository.UserRepository;
import com.google.sps.user.repository.UserRepositoryFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet handles bookmarking place guide and removing place guide id from user's {@code
 * bookmarkedPlaceGuidesIds}.
 */
@WebServlet("/bookmark-place-guide")
public class BookmarkPlaceGuideServlet extends HttpServlet {

  private static final String PLACE_GUIDE_ID_PARAMETER = "placeGuideId";
  private static final String BOOKMARK_HANDLING_TYPE_PARAMETER = "bookmarkHandlingType";

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
    togglePlaceGuideBookmark(queryType, placeGuideId);
  }

  /**
   * This functions handles the bookmarking/unbookmarking of the placeguide with id = @param
   * placeGuideId by the cuurent user. If the user unbookmarked a placeguide, it will for sure
   * succeed. if the user bookmarked a placeguide, then the operation will succeed only if the user
   * didn't reach the limit for the maximum number of bookmarked placeguides before. The function
   * returns true if the operation succeded, and false otherwise.
   */
  private boolean togglePlaceGuideBookmark(
      BookmarkPlaceGuideQueryType bookmarkPlaceGuideQueryType, long placeGuideId) {
    String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();
    switch (bookmarkPlaceGuideQueryType) {
      case BOOKMARK:
        User user = userRepository.getUser(userId);
        if (user == null) {
          throw new IllegalStateException("The current user does not exist in the database!");
        } else {
          if (user.canBookmarkAnotherPlaceGuide()) {
            userRepository.bookmarkPlaceGuide(placeGuideId, userId);
            return true;
          } else {
            return false;
          }
        }
      case REMOVE:
        userRepository.removeBookmarkedPlaceGuide(placeGuideId, userId);
        return true;
      default:
        throw new IllegalStateException("Bookmark handling type does not exist!");
    }
  }
}
