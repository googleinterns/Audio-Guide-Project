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
            latLngBounds: {north: 85, south: -85, west: -180, east: 180},
            strictBounds: true
        }
    };
    const map = new google.maps.Map(
        document.getElementById('map'), myMapOptions);

    // Geolocation functionality.
    var geolocator = new Geolocator(map);
    geolocator.init();

    // Searching functionality.
    var searchResult =
        Place.constructPlaceBasedOnCoordinates(map, 0, 0, "Search Result", PlaceType.SEARCH_RESULT, true);
    searchResult.visible = false;
    var searchBox = new SearchBox(map, searchResult, "search-box");
    searchBox.init();

    // Discover public place guides functionality.
    var userId = "test";
    PlaceDisplayer.displayPlaceGuidesOfUser(map, userId);
}