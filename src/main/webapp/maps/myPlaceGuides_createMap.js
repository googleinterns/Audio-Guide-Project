/**
 * This function initialises the map and adds some functionalities to it:
 * Geolocation: to track the user's current location, display it and center the map around it.
 * Searching: lets the user search for places and center the map around it.
 * Display place guides: displays the place guides created by the currently logged in user.
 */
function createMap() {
  var map = new MapWidget();
   map.addGeolocationFunctionality();
   map.addSearchingFunctionality();
  //PlaceDisplayer.displayPlaceGuidesOfUser(map);
}