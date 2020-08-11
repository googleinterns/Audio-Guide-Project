package com.google.sps.placeGuide;

import org.jetbrains.annotations.Nullable;
import com.google.appengine.api.datastore.GeoPt;

/** Class containing place guide's information. */
public class PlaceGuide {

  // Unique identifier for a {@code PlaceGuide} automatically given 
  // when a {@code PlaceGuide} entity is created and will be used to delete
  // or edit {@code PlaceGuide}.
  @Nullable
  private final long id;

  private final String name;
  private final String audioKey; 
  private final String creatorId;
  private final boolean isPublic;

  // This is not the unique identifier of a place guide.
  @Nullable
  private String placeId;

  private final GeoPt coord;

  // Specify how long user usually spends to follow this place guide in minutes.
  @Nullable
  private final long length;

  @Nullable
  private final String description, imgKey;

  private PlaceGuide(long id, String name, String audioKey, String creatorId, 
                                            String placeId, boolean isPublic, 
                                            GeoPt coord, long length, 
                                            String description, String imgKey) {
    this.id = id;
    this.name = name;
    this.audioKey = audioKey;
    this.creatorId = creatorId;
    this.placeId = placeId;
    this.isPublic = isPublic;
    this.coord = coord;
    this.length = length;
    this.description = description;
    this.imgKey = imgKey;
  }

  public static class Builder {
    private long id;
    private final String name;
    private final String audioKey;
    private final String creatorId; 
    private boolean isPublic = false;
    private String placeId;
    private final GeoPt coord;
    private long length;
    private String description, imgKey;
        
    public Builder(String name, String audioKey, String creatorId, GeoPt coord) {
      this.name = name;
      this.audioKey = audioKey;
      this.creatorId = creatorId;
      this.placeId = placeId;
      this.coord = coord;
    }
    public Builder setPlaceId(String placeId) {
      this.placeId = placeId;
      return this;
    }
    public Builder setId(long id) { 
      this.id = id;
      return this;
    }
    public Builder setPlaceGuideStatus(boolean setToPublic) {
      this.isPublic = setToPublic;
      return this;
    }
    public Builder setLength(long length) {
      this.length = length;
      return this;
    }
    public Builder setDescription(String description) {
      this.description = description;
      return this;
    }
    public Builder setImageKey(String imgKey) {
      this.imgKey = imgKey;
      return this;
    }
    public PlaceGuide build() {
      return new PlaceGuide(id, name, audioKey, creatorId, placeId, isPublic, 
                                                coord, length, description, imgKey);
    }
  }
  
  @Override
  public boolean equals(Object o){
    if(o == null) { 
      return false; 
    }
    if(!(o instanceof PlaceGuide)) { 
      return false; 
    }
    PlaceGuide placeGuide = (PlaceGuide) o;

    return (this.name.equals(placeGuide.getName()) &&
           this.id == placeGuide.getId() &&
           this.audioKey.equals(placeGuide.getAudioKey()) &&
           this.creatorId.equals(placeGuide.getCreatorId()) &&
           this.placeId.equals(placeGuide.getPlaceId()) &&
           this.isPublic == placeGuide.isPublic() &&
           this.coord.equals(placeGuide.getCoordinate()) &&
           this.length == placeGuide.getLength() &&
           this.description.equals(placeGuide.getDescription()) &&
           this.imgKey.equals(placeGuide.getImageKey()));
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getAudioKey() {
    return audioKey;
  }

  public String getCreatorId() {
    return creatorId;
  }

  public String getPlaceId() {
    return placeId;
  }

  public GeoPt getCoordinate() {
    return coord;
  }

  public boolean isPublic() {
    return isPublic;
  }

  @Nullable
  public long getLength() {
    return length;
  }

  @Nullable
  public String getDescription() {
    return description;
  }

  @Nullable
  public String getImageKey() {
    return imgKey;
  }
}