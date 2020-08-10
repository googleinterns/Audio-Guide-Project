package com.google.sps.placeGuide.repository;

import com.google.sps.data.RepositoryType;
import com.google.sps.placeGuide.repository.impl.DatastorePlaceGuideRepository;

/**
 * Creates a PlaceGuideRepository according to the requested type.
 */
public class PlaceGuideRepositoryFactory {

  public static PlaceGuideRepository getPlaceGuideRepository(RepositoryType repositoryType) {
    if (repositoryType == null) {
      throw new IllegalArgumentException("repositoryType can't be null!");
    }
    switch (repositoryType) {
      case DATASTORE:
        return new DatastorePlaceGuideRepository();
      default:
        throw new IllegalArgumentException("not an existing respository type");
    }
  }
}