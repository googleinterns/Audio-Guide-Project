/**
 * This class holds a place guide's data, related to its place, creator, image, audio and description. 
 * The place, similarly to the Place class, can be specified either by coordinates or through a place ID.
 */
class PlaceGuide {
  constructor(place, databaseId, description, audioKey, audioLength, imgKey, creatorId, creatorName) {
      this._place = place;
      this._databaseId = databaseId;
      this._description = description;
      this._audioKey = audioKey;
      this._audioLength = audioLength;
      this._imgKey = imgKey;
      this._creatorId = creatorId;
      this._creatorName = creatorName;
      this._place.infoWindowContent = this.getInfoWindowContent();
  }

  static constructPlaceGuideBasedOnCoordinates(map, databaseId, name, description, audioKey, audioLength, imgKey, positionLat, positionLng, creatorId, creatorName, placeType){
        var newPlace = Place.constructPlaceBasedOnCoordinates(map, positionLat, positionLng, name, placeType, true);
        return new PlaceGuide(newPlace, databaseId, description, audioKey, audioLength, imgKey, creatorId, creatorName);
  }

  static constructPlaceGuideBasedOnPlaceId(map, databaseId, name, description, audioKey, audioLength, imgKey, placeId, creatorId, creatorName, placeType){
      return Place.constructPlaceBasedOnPlaceId(map, placeId, name, placeType, true)
        .catch(error => console.log("Failed to construct place guide based on id: " + error))
        .then(newPlace => {
                return new PlaceGuide(newPlace, databaseId, description, audioKey, audioLength, imgKey, creatorId, creatorName);
            }
        )
  }

  // Specific infoWindowContent for PlaceGuides.
  getInfoWindowContent() {
    var content = "<h3>" + this._place._name + "</h3>" +
        "<h4> Created by: " + this._creatorName + "</h4>" +
        "<h4> Place: " + this._place.mapsPlaceName + "</h4>" +
        "<p>" + this._description + "</p>";
    return content;
  }
}