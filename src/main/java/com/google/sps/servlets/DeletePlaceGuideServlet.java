package com.google.sps.servlets;

import com.google.sps.data.RepositoryType;
import com.google.sps.placeGuide.repository.PlaceGuideRepository;
import com.google.sps.placeGuide.repository.PlaceGuideRepositoryFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** This servlet handles deleting placeguide's data. */
@WebServlet("/delete-place-guide-data")
public class DeletePlaceGuideServlet extends HttpServlet {

  private final PlaceGuideRepository placeGuideRepository =
      PlaceGuideRepositoryFactory.getPlaceGuideRepository(RepositoryType.DATASTORE);

  /** Delete the entity corresponding to the specified key. */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String id = request.getParameter("id");
    long parsedId = Long.parseLong(id);
    placeGuideRepository.deletePlaceGuide(parsedId);
    response.sendRedirect("/myPlaceGuides.html");
  }
}
