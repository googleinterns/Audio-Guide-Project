/**
 * This class holds a place guide's data, related to its place, creator, image, audio and description.
 * The place, similarly to the Place class, can be specified either by coordinates or through a place ID.
 */


class PlaceGuide {
    constructor(id, name, location, audioKey, audioLength, imgKey, creator, public, createdByCurrentUser, bookmarkedByCurrentUser) {
        this._id = id;
        this._name = name;
        this._location = location;
        this._audioKey = audioKey;
        this._audioLength = audioLength;
        this._imgKey = imgKey;
        this._creator = creator;
        this._public = public;
        this._createdByCurrentuser = createdByCurrentUser;
        this._bookmarkedBuCurrentUser = bookmarkedByCurrentUser;
    }

    get id() {
        return this._id;
    }

    get name() {
        return this._name;
    }

    get location() {
        return this._location;
    }

    get audioKey() {
        return this._audioKey;
    }

    get audioLength() {
        return this._audioLength;
    }

    get imgKey() {
        return this._imgKey;
    }

    get creator() {
        return this._creator;
    }

    get isPublic() {
        return this._public;
    }

    get createdByCurrentUser() {
        return this._createdByCurrentuser;
    }

    get bookmarkedByCurrentUser() {
        return this._bookmarkedBuCurrentUser;
    }

    set bookmarkedByCurrentUser(newValue) {
        this._bookmarkedBuCurrentUser = newValue;
    }
}


// class PlaceGuide {
//   constructor(place, databaseId, description, audioKey, audioLength, imageKey, creatorId, creatorName) {
//     this._place = place;
//     this._databaseId = databaseId;
//     this._description = description;
//     this._audioKey = audioKey;
//     this._audioLength = audioLength;
//     this._imageKey = imageKey;
//     this._creatorId = creatorId;
//     this._creatorName = creatorName;
//     this._place.infoWindowContent = this.getInfoWindowContent();
//   }

//   static constructPlaceGuideBasedOnCoordinates(map, databaseId, name, description, audioKey,
//                                                audioLength, imageKey, positionLat, positionLng,
//                                                creatorId, creatorName, placeType) {
//     var newPlace = Place.constructPlaceBasedOnCoordinates(map, positionLat, positionLng, name,
//         placeType, true);
//     return new PlaceGuide(newPlace, databaseId, description, audioKey,
//         audioLength, imageKey, creatorId, creatorName);
//   }

//   static constructPlaceGuideBasedOnPlaceId(map, databaseId, name, description, audioKey,
//                                            audioLength, imageKey, placeId, creatorId,
//                                            creatorName, placeType) {
//     return Place.constructPlaceBasedOnPlaceId(map, placeId, name, placeType, true)
//         .catch(error => console.log("Failed to construct place guide based on id: " + error))
//         .then(newPlace => {
//               return new PlaceGuide(newPlace, databaseId, description, audioKey,
//                   audioLength, imageKey, creatorId, creatorName);
//             }
//         )
//   }

//   // Specific infoWindowContent for PlaceGuides.
//   getInfoWindowContent() {
//     var content = "<h3>" + this._place._name + "</h3>" +
//         "<h4> Created by: " + this._creatorName + "</h4>" +
//         "<h4> Place: " + this._place.mapsPlaceName + "</h4>" +
//         "<p>" + this._description + "</p>";
//     return content;
//   }
// }