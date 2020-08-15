package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.RepositoryType;
import com.google.sps.placeGuide.PlaceGuide;
import com.google.sps.placeGuide.repository.PlaceGuideRepository;
import com.google.sps.placeGuide.repository.PlaceGuideRepositoryFactory;
import com.google.sps.placeGuide.repository.impl.DatastorePlaceGuideRepository;
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
 * This servlet handles bookmarking place guide.
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

  private final PlaceGuideRepository placeGuideRepository = 
      PlaceGuideRepositoryFactory.getPlaceGuideRepository(RepositoryType.DATASTORE);

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String currentUrl = request.getParameter("currentUrl");
    long placeGuideId = Long.parseLong(request.getParameter("placeGuideId"));
    placeGuideRepository.bookmarkPlaceGuide(placeGuideId, userId);
    response.sendRedirect(currentUrl);
  }
}