/**
 * This function initialises the map and adds some functionalities to it:
 * Geolocation: to track the user's current location, display it and center the map around it.
 * Searching: lets the user search for places and center the map around it.
 * Display place guides: displays the place guides created by the currently logged in user.
 */
var map;
var placeGuideManager;

function initPage() {
  authenticateUser().then(userAuthenticationStatus => {
    if (!userAuthenticationStatus.isLoggedIn) {
      location.replace(userAuthenticationStatus.loginUrl);
    } else {
      const menu = new Menu(Menu.PAGE_NAMES.BOOKMARKED_PLACEGUIDES);
      var mapWidget = new MapWidget();
      mapWidget.addGeolocationFunctionality();
      mapWidget.addSearchingFunctionality();
      map = mapWidget.map;
      var placeGuideRepository =
          new PlaceGuideRepository(
              PlaceGuideRepository.QueryType.BOOKMARKED);
      placeGuideManager = new PlaceGuideManager(placeGuideRepository);
      placeGuideManager.update(map.getBounds(), map.getZoom(), true);
    }
  });
}