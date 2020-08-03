package com.google.sps.user;

import org.jetbrains.annotations.Nullable;

/** Stores the data related to one user */
public class User {
   private final String id;
   @Nullable 
   private String selfIntroduction, imgUrl;
}