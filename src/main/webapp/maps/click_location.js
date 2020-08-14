/**
 * This function lets the user pick a location by clicking on any point on the map.
 * The clicked points coordinates will be saved as the chosen location.
 * Additionaly, the user may also click on the marker of a POI(point of interest).
 * In this case, the place corresponding to that POI, as provided by the Places API,
 * will be saved as the chosen location, with all its data.
 */
function enableChoosingLocationByClick(map, chosenPlace) {
  map.addListener('click', function (mapsMouseEvent) {
    if (mapsMouseEvent.placeId) {
      chosenPlace.updatePlaceAndCenterBasedOnId(mapsMouseEvent.placeId)
    } else {
      chosenPlace.position = mapsMouseEvent.latLng;
    }
  });
}