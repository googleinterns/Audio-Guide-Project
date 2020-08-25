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

var map;
var placeGuideManager;

function initPage() {
   var mapWidget = new MapWidget();
   mapWidget.addGeolocationFunctionality();
   mapWidget.addLocationChoosingAndSavingFunctionality();
   map = mapWidget.map;
   var placeGuideRepository = new PlaceGuideRepository(PlaceGuideRepository.QueryType.CREATED_ALL_IN_MAP_AREA);
   placeGuideManager = new PlaceGuideManager(placeGuideRepository);
   google.maps.event.addListener(map, 'idle', function() {
        placeGuideManager.update(map.getBounds(), map.getZoom());
   });
   document.getElementById("map")
        .addEventListener(MapWidget.SAVE_LOCATION_EVENT, function () {
            if (mapWidget.savedLocation.place != null) {
                console.log("found place id = " + mapWidget.savedLocation.place.place_id)
                updateLocation(mapWidget.savedLocation.position, mapWidget.savedLocation.place.place_id, mapWidget.savedLocation.place.name);
            } else {
                updateLocation(mapWidget.savedLocation.position, null, null);
            }
            
        });
}