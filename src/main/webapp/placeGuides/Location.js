/**
 * This class holds a location's data, which can be specified by
 * coordinates or by a place_id, which will be decoded with PlacesService.
 */
class Location {
  constructor(position, placeId) {
    this._position = position;
    this._placeId = placeId;
    this._placeName = undefined;
    // if (this._mapsPlace != null) {
    //   this._position = this._mapsPlace.geometry.location;
    // } else {
    //   this._position = position;
    // }
  }

  get position() {
    return this._position;
  }

  // get placeName() {
  //   if (this._placeName !== undefined) {
  //     return this._placeName;
  //   } else {
  //
  //   }
  //   return this._placeName;
  // }

  static constructLocationBasedOnCoordinates(positionLat, positionLng) {
    return new Location(
        new google.maps.LatLng(positionLat, positionLng), null);
  }

  static constructLocationBasedOnPlaceId(placeId) {
    const request = {
      placeId: placeId,
      fields: ['address_components', 'name', 'geometry', 'place_id'],
    };
    return new Promise(function(resolve, reject) {
      const service = new google.maps.places.PlacesService(map);
      service.getDetails(request, (place, status) => {
        if (status === google.maps.places.PlacesServiceStatus.OK) {
          resolve(new Location(null, place));
        } else {
          reject(new Error('Couldn\'t find the place ' + placeId));
        }
      });
    });
  }
}
