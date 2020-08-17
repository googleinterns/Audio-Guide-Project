/**
 * This function initialises the map and adds some functionalities to it:
 * Geolocation: to track the user's current location, display it and center the map around it.
 * Choose location: the user can choose  alocation through the searchbox,
 * by clicking on any point on the map, by clicnking on a POI
 * or by moving the marker of the chosen location.
 * Save location: lets the user save the currently chosen location for the place guide
 * creation.
 * Display place guides: displays the place guides created by the currently logged in user.
 */
function createMap() {
  var myMapOptions = {
    zoom: 2,
    center: new google.maps.LatLng(0, 0),
    mapTypeId: 'roadmap',
    restriction: {
      latLngBounds: {north: 85, south: -85, west: -180, east: 180},
      strictBounds: true
    }
  };
  const map = new google.maps.Map(
      document.getElementById('map'), myMapOptions);

  // Geolocation functionality.
  var geolocator = new Geolocator(map);
  geolocator.init();

  // Choose location functionality.
  // Remark that chosenLocation can be updated from the SearchBox and by clicking as well.
  var chosenLocation =
      Place.constructPlaceBasedOnCoordinates(map, 0, 0, "Picked Location", PlaceType.SEARCH_RESULT, true);
  chosenLocation.visible = false;
  chosenLocation.draggable = true;
  chosenLocation.triggerChosenLocationChangeEvent = true;
  document.getElementById("map")
      .addEventListener(CHOSEN_LOCATION_CHANGE_EVENT, function () {
        chosenLocation.visible = true;
      });
  var searchBox = new SearchBox(map, chosenLocation, "search-box");
  searchBox.init();
  enableChoosingLocationByClick(map, chosenLocation);

  // Save location functionality.
  var locationSaver = new LocationSaver(map, chosenLocation);
  locationSaver.init();

  // Display user's place guides functionality.
  // The user's ID is hardcoded for now,
  // but it will be fetched from the server in the future.
  var userId = "test";
  PlaceDisplayer.displayPlaceGuidesOfUser(map, userId);
}