/**
 * This function lets the user pick a location
 * by clicking on any point on the map.
 * The clicked points coordinates will be saved as the chosen location.
 * Additionaly, the user may also click on the marker
 * of a POI(point of interest).
 * In this case, the place corresponding to that POI,
 * as provided by the Places API,
 * will be saved as the chosen location, with all its data.
 */

class LocationPicker {
  constructor(map, chosenPlace) {
    this._map = map;
    this._chosenPlace = chosenPlace;
  }

  // The LocationPicker starts listening to and handling
  // map-click events when init() is called.
  init() {
    const chosenPlace = this._chosenPlace;
    this._map.addListener('click', function(mapsMouseEvent) {
      if (mapsMouseEvent.placeId) {
        chosenPlace.updatePlaceAndCenterBasedOnId(mapsMouseEvent.placeId);
      } else {
        chosenPlace.position = mapsMouseEvent.latLng;
      }
    });
  }
}
