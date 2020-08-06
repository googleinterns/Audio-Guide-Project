package com.google.sps.user.repository;

import com.google.sps.data.RepositoryType;
import com.google.sps.user.repository.impl.DatastoreUserRepository;

/**
 * Creates a UserRepository according to the requested type.
 */
public class UserRepositoryFactory {

  public static UserRepository getUserRepository(RepositoryType repositoryType) {
    if (repositoryType == null) {
      throw new IllegalArgumentException("repositoryType can't be null!");
    }
    switch (repositoryType) {
      case DATASTORE:
        return new DatastoreUserRepository();
      default:
        throw new IllegalArgumentException("not an existing respository type");
    }
  }
}