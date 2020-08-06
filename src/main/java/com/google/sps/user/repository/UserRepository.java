package com.google.sps.user.repository;

import com.google.sps.user.User;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;

/** Handles the storage of User data. */
public interface UserRepository {
    void saveUser(User user);

    @Nullable
    User getUser(String id);
}