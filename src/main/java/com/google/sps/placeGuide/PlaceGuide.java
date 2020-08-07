package com.google.sps.placeGuide;

import org.jetbrains.annotations.Nullable;

/** Class containing place guide's information. */
public class PlaceGuide {

  private final String name;
  private final String audioKey; 
  private final String creatorId;

  // Acquired using Places API.
  private final String placeId;

  private final PlaceCoordinate coord;

  // Specify how long user usually spends to follow this place guide in minutes.
  @Nullable
  private final int length;

  @Nullable
  private final String desc, imgKey;

  private PlaceGuide(String name, String audioKey, String creatorId, 
                                    String placeId, PlaceCoordinate coord,
                                    int length, String desc, String imgKey) {
    this.name = name;
    this.audioKey = audioKey;
    this.creatorId = creatorId;
    this.placeId = placeId;
    this.coord = coord;
    this.length = length;
    this.desc = desc;
    this.imgKey = imgKey;
  }

  public static class Builder {
    private final String name;
    private final String audioKey;
    private final String creatorId; 
    private final String placeId;
    private final PlaceCoordinate coord;
    private int length;
    private String desc, imgKey;
        
    public Builder(String name, String audioKey, String creatorId, String placeId, 
                                                                   PlaceCoordinate coord) {
      this.name = name;
      this.audioKey = audioKey;
      this.creatorId = creatorId;
      this.placeId = placeId;
      this.coord = coord;
    }
    public Builder setLength(int length) {
      this.length = length;
      return this;
    }
    public Builder setDescription(String desc) {
      this.desc = desc;
      return this;
    }
    public Builder setImageUrl(String imgKey) {
      this.imgKey = imgKey;
      return this;
    }
    public UserAuthenticationStatus build() {
      return new PlaceGuide(name, audioKey, creatorId, placeId, coord, length, desc, imgKey);
    }
  }

  public String getName() {
    return name;
  }

  public String getaudioKey() {
    return audioKey;
  }

  public String getCreatorId() {
    return creatorId;
  }

  public String getPlaceId() {
    return placeId;
  }

  public PlaceCoordinate getCoordinate() {
    return coord;
  }

  @Nullable
  public String getLength() {
    return length;
  }

  @Nullable
  public String getDescription() {
    return desc;
  }

  @Nullable
  public String getImageUrl() {
    return imgKey;
  }
}