package com.google.sps.servlets;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.RepositoryType;
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
    UNBOOKMARK
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long placeGuideId = Long.parseLong(request.getParameter(PLACE_GUIDE_ID_PARAMETER));
    String bookmarkHandlingType = request.getParameter(BOOKMARK_HANDLING_TYPE_PARAMETER);
    BookmarkPlaceGuideQueryType queryType =
        BookmarkPlaceGuideQueryType.valueOf(bookmarkHandlingType);
    togglePlaceGuideBookmark(queryType, placeGuideId);
  }

  private void togglePlaceGuideBookmark(
      BookmarkPlaceGuideQueryType bookmarkPlaceGuideQueryType, long placeGuideId) {
    String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();
    switch (bookmarkPlaceGuideQueryType) {
      case BOOKMARK:
        userRepository.bookmarkPlaceGuide(placeGuideId, userId);
        break;
      case UNBOOKMARK:
        userRepository.removeBookmarkedPlaceGuide(placeGuideId, userId);
        break;
      default:
        throw new IllegalStateException("Bookmark handling type does not exist!");
    }
  }
}
