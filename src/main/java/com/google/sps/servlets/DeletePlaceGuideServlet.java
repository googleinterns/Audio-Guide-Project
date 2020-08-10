package com.google.sps.servlets;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.placeGuide.repository.PlaceGuideRepository;
import com.google.sps.placeGuide.repository.PlaceGuideRepositoryFactory;
import com.google.sps.data.RepositoryType;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet handles deleting placeguide's data.
 */
@WebServlet("/delete-place-guide-data")
public class DeletePlaceGuideServlet extends HttpServlet {

  private final UserService userService = UserServiceFactory.getUserService();
  private final PlaceGuideRepository placeGuideRepository = PlaceGuideRepositoryFactory
                                                .getPlaceGuideRepository(RepositoryType.DATASTORE);

  /**
  * Delete the key of the selected place guide from datastore.
  */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String id = request.getParameter("id");
    long parsedId = Long.parseLong(id);
    placeGuideRepository.deleteSelectedPlaceGuide(parsedId);
    response.sendRedirect("/myPlaceGuides.html");
  }
}