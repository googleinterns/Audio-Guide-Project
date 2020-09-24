class MapWidget {
  static CHOSEN_LOCATION_CHANGE_EVENT = 'chosenPositionChange';

  constructor() {
    var myMapOptions = {
      zoom: 13,
      // The coordinates of the Google Office in Mountain View
      center: new google.maps.LatLng(37.419857, -122.078827), 
      mapTypeId: 'roadmap',
      restriction: {
        // Set bounds for map so that the user can't pan out.
        // Use the maximum possible latitude and logitude coordinates on Earth for bounding,
        // based on the Mercato-projection.
        latLngBounds: {north: 85, south: -85, west: -180, east: 180},
        strictBounds: true
      }
    };
    this._map = new google.maps.Map(
        document.getElementById('map'), myMapOptions);
    Geolocator.centerMapAtCurrentLocation(this._map);
  }

  get map() {
    return this._map;
  }

  get pickedLocation() {
    return this._locationPicker.pickedLocation;
  }

  addGeolocationFunctionality() {
    var geolocator = new Geolocator(this._map);
    geolocator.init();
  }

  addSearchingFunctionality() {
    var searchResult =
        Place.constructPlaceBasedOnCoordinates(
            this._map, 0, 0,
            "Search Result", PlaceType.SEARCH_RESULT, true);
    searchResult.visible = false;
    var searchBox = new SearchBox(this._map, searchResult, "search-box");
    searchBox.init();
  }

  addLocationChoosingAndSavingFunctionality(placeGuideToEdit) {
    // Choose location functionality.
    // Remark that chosenLocation can be updated
    // from the SearchBox and by clicking as well.
    let chosenLocation;
    if (placeGuideToEdit === null) {
      chosenLocation = this.constructDefaultChosenLocation();
      var searchBox = new SearchBox(
          this._map, chosenLocation, "search-box");
      searchBox.init();
      this._locationPicker = new LocationPicker(this._map, chosenLocation);
      this._locationPicker.init();
      document.getElementById("map")
          .addEventListener(MapWidget.CHOSEN_LOCATION_CHANGE_EVENT, function () {
            chosenLocation.visible = true;
          });
    } else {
      const thisMap = this._map;
      this.constructChosenLocationForPlaceGuide(placeGuideToEdit)
          .then((chosenLocation) => {
            var searchBox = new SearchBox(
                thisMap, chosenLocation, "search-box");
            searchBox.init();
            this._locationPicker = new LocationPicker(this._map, chosenLocation);
            this._locationPicker.init();
          });
    }
  }

  constructDefaultChosenLocation() {
    const thisMap = this._map;
    const chosenLocation =
        Place.constructPlaceBasedOnCoordinates(
            thisMap, 0, 0,
            "Picked Location", PlaceType.SEARCH_RESULT, true);
    chosenLocation.visible = false;
    chosenLocation.draggable = true;
    chosenLocation.triggerChosenLocationChangeEvent = true;
    return chosenLocation;
  }

  constructChosenLocationForPlaceGuide(placeGuide) {
    const thisMap = this._map;
    if (placeGuide.location.placeId != null) {
          return Place.constructPlaceBasedOnPlaceId(thisMap,
              placeGuide.location.placeId,
              "Picked Location",
              PlaceType.SEARCH_RESULT,
              true)
              .then((chosenLocation) => {
                chosenLocation.visible = true;
                chosenLocation.draggable = true;
                chosenLocation.triggerChosenLocationChangeEvent = true;
                return chosenLocation;
              });
    } else {
      const chosenLocation =
          Place.constructPlaceBasedOnCoordinates(thisMap,
              placeGuide.location.position.lat(),
              placeGuide.location.position.lng(),
              "Picked Location",
              PlaceType.SEARCH_RESULT,
              true);
      chosenLocation.visible = true;
      chosenLocation.draggable = true;
      chosenLocation.triggerChosenLocationChangeEvent = true;
      return new Promise((resolve, reject) => {
        resolve(chosenLocation);
      });
    }
  }
}