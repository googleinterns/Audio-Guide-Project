class MapWidget {
    constructor() {
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
        this._map = new google.maps.Map(
            document.getElementById('map'), myMapOptions);
    }

    get map() {
        return this._map;
    }

    addGeolocationFunctionality() {
        var geolocator = new Geolocator(this._map);
        geolocator.init();
    }

    addSearchingFunctionality() {
        var searchResult =
            Place.constructPlaceBasedOnCoordinates(this._map, 0, 0,
                "Search Result", PlaceType.SEARCH_RESULT, true);
        searchResult.visible = false;
        var searchBox = new SearchBox(this._map, searchResult, "search-box");
        searchBox.init();
    }

    addLocationChoosingAndSavingFunctionality() {
        // Choose location functionality.
        // Remark that chosenLocation can be updated from the SearchBox and by clicking as well.
        var chosenLocation =
            Place.constructPlaceBasedOnCoordinates(this._map, 0, 0,
                "Picked Location", PlaceType.SEARCH_RESULT, true);
        chosenLocation.visible = false;
        chosenLocation.draggable = true;
        chosenLocation.triggerChosenLocationChangeEvent = true;
        var searchBox = new SearchBox(this._map, chosenLocation, "search-box");
        searchBox.init();
        var locationPicker = new LocationPicker(this._map, chosenLocation);
        locationPicker.init();
        // Save location functionality.
        var locationSaver = new LocationSaver(this._map, chosenLocation);
        locationSaver.init();
    }
}