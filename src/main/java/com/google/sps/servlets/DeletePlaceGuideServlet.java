package com.google.sps.servlets;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
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

  /**
  * Delete the key of the selected place guide from datastore.
  */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String placeGuideId = request.getParameter("id");
    Key placeGuideEntityKey = KeyFactory.createKey("PlaceGuide", id);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.delete(placeGuideEntityKey);
    response.sendRedirect("/myPlaceGuides.html");
  }
}