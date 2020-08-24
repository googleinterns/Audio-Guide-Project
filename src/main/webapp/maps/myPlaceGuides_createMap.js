/**
 * This function initialises the map and adds some functionalities to it:
 * Geolocation: to track the user's current location, display it and center the map around it.
 * Searching: lets the user search for places and center the map around it.
 * Display place guides: displays the place guides created by the currently logged in user.
 */
function createMap() {
  var myMapOptions = {
    zoom: 2,
    center: new google.maps.LatLng(0, 0),
    mapTypeId: 'roadmap',
    restriction: {
      // Set bounds for map so that the user can't pan out. 
      // Use the maximum possible latitude and logitude coordinates on Earth for bounding,
      // based on the Mercato-projection. 
      latLngBounds: {north: 85, south: -85, west: -180, east: 180},
      strictBounds: true
    }
  };
  const map = new google.maps.Map(
      document.getElementById('map'), myMapOptions);

  addGeolocationFunctionality(map);
  addSearchingFunctionality(map);
  PlaceDisplayer.displayPlaceGuidesOfUser(map);
}