/**
 * This file's createMap function handles the set-up of the map.
 * Since not each functionality will be needed on each page,
 * each page shoul'd have a seperate createMap() function for setup,
 * which should be used to "personalise" the current page.
 *
 * At this stage, the modules are included rather for demonstrational purposes.
 */

/** This function initialises the map and adds some functionalities to it:
 * {@code addGoToMyLocationControl} allows the user to center the map
 * at their location by clicking a button.
 * {@code addEnableGeolocationControl} allows the user to turn on/off
 * the geolocation feature, which displays the user's current location continuously.
 * {@code initAutocomplete} sets up a search-box with auto-suggestions feature,
 * so that the user can search for a specific place. When a search query is submitted,
 * the map is centered at the requested place and a marker is displayed for it.
 */
function createMap() {
  var myMapOptions = {
    zoom: 2,
    center: new google.maps.LatLng(0, 0),
    mapTypeId: 'roadmap',
  };
  const map = new google.maps.Map(
      document.getElementById('map'), myMapOptions);
  addGoToMyLocationControl(map);
  addEnableGeolocationControl(map);

  var searchResult = new Place(0, 0, "Picked Location", null, PlaceType.SEARCH_RESULT, true);
  searchResult.draggable = true;
  searchResult.map = map;
  initAutocomplete(map, searchResult);
  // displayPublicPlaceGuides(map);
  enableChoosingPlaceByClick(map, searchResult);
}