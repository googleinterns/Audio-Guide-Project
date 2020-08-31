/**
 * This function initialises the map and adds some functionalities to it:
 * Geolocation: to track the user's current location, display it and center the map around it.
 * Searching: lets the user search for places and center the map around it.
 * Display place guides: displays all the public place guides.
 */
var map;
var placeGuideManager;

function initPage() {
  authenticateUser().then(userAuthenticationStatus => {
    if (!userAuthenticationStatus.isLoggedIn) {
      location.replace(userAuthenticationStatus.loginUrl);
    } else {
      const menu = new Menu(Menu.PAGE_NAMES.DISCOVER);
      var mapWidget = new MapWidget();
      mapWidget.addGeolocationFunctionality();
      mapWidget.addSearchingFunctionality();
      map = mapWidget.map;
      var placeGuideRepository =
          new PlaceGuideRepository(
              PlaceGuideRepository.QueryType.ALL_PUBLIC_IN_MAP_AREA);
      placeGuideManager = new PlaceGuideManager(placeGuideRepository);
      google.maps.event.addListener(map, 'idle', function () {
        placeGuideManager.update(map.getBounds(), map.getZoom(), false);
      });
    }
  });
}