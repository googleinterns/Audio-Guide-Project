function addGeolocationFunctionality(map) {
  var geolocator = new Geolocator(map);
  geolocator.init();
}

function addSearchingFunctionality(map) {
  var searchResult =
      Place.constructPlaceBasedOnCoordinates(map, 0, 0,
          "Search Result", PlaceType.SEARCH_RESULT, true);
  searchResult.visible = false;
  var searchBox = new SearchBox(map, searchResult, "search-box");
  searchBox.init();
}

function addLocationChoosingAndSavingFunctionality(map) {
  // Choose location functionality.
  // Remark that chosenLocation can be updated from the SearchBox and by clicking as well.
  var chosenLocation =
      Place.constructPlaceBasedOnCoordinates(map, 0, 0,
          "Picked Location", PlaceType.SEARCH_RESULT, true);
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
}