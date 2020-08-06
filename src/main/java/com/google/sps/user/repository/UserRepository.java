package com.google.sps.user.repository;

import com.google.sps.user.User;
import org.jetbrains.annotations.Nullable;

/**
 * Handles the storage of User data.
 */
public interface UserRepository {
  /**
   * Saves the user's data in the database.
   */
  void saveUser(User user);

  /**
   * Returns the user with the given id data from the database.
   * If no user exists with the given id, it returns null.
   */
  @Nullable
  User getUser(String id);
}