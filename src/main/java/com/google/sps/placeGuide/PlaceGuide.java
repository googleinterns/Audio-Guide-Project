package com.google.sps.placeGuide;

import org.jetbrains.annotations.Nullable;
import java.util.List;

/** Class containing place guide's information. */
public class PlaceGuide {

  private final String name, audioUrl, creatorId;

  // Acquired using Places API.
  private final String placeId;

  private final PlaceCoordinate coord;

  // Specify how long user usually spends to follow this place guide in minutes.
  @Nullable
  private final int length;

  @Nullable
  private final String desc, imgUrl;

  // List of ids that belonged to users that saved a particular place guide.
  @Nullable
  private final List<String> saverId;

  private PlaceGuide(String name, String audioUrl, String creatorId, 
                                    String placeId, PlaceCoordinate coord,
                                    List<String> saverId, int length, 
                                    String desc, String imgUrl) {
    this.name = name;
    this.audioUrl = audioUrl;
    this.creatorId = creatorId;
    this.placeId = placeId;
    this.coord = coord;
    this.saverId = saverId;
    this.length = length;
    this.desc = desc;
    this.imgUrl = imgUrl;
  }

  public static class Builder {
    private final String name, audioUrl, creatorId, placeId;
    private final PlaceCoordinate coord;
    private List<String> saverId;
    private int length;
    private String desc, imgUrl;
        
    public Builder(String name, String audioUrl, String creatorId, 
                                            String placeId, PlaceCoordinate coord) {
      this.name = name;
      this.audioUrl = audioUrl;
      this.creatorId = creatorId;
      this.placeId = placeId;
      this.coord = coord;
    }
    public Builder setSaverId(List<String> saverId) {
      this.saverId = saverId;
      return this;
    }
    public Builder setLength(int length) {
      this.length = length;
      return this;
    }
    public Builder setDescription(String desc) {
      this.desc = desc;
      return this;
    }
    public Builder setImageUrl(String imgUrl) {
      this.imgUrl = imgUrl;
      return this;
    }
    public UserAuthenticationStatus build() {
      return new PlaceGuide(name, audioUrl, creatorId, placeId, coord, saverId, length, 
                                                                            desc, imgUrl);
    }
  }

    public String getName() {
        return name;
    }

    public String getAudioUrl() {
        return audioUrl;
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

    public List<String> getSaverId() {
        return saverId;
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
        return imgUrl;
    }
}