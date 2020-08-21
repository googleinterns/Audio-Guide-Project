/**
 * This function initialises the map and adds some functionalities to it:
 * Geolocation: to track the user's current location, display it and center the map around it.
 * Searching: lets the user search for places and center the map around it.
 * Display place guides: displays all the public place guides.
 */
var map;
var placeGuideManager;

function createMap() {
   var map = new MapWidget();
   map.addGeolocationFunctionality();
   map.addSearchingFunctionality();
   var placeGuideRepository = new PlaceGuideRepository(PlaceGuideRepository.QueryType.ALL_PUBLIC_IN_MAP_AREA);
   placeGuideManager = new PlaceGuideManager(map, placeGuideRepository);
}