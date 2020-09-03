/**
 * This function initialises the map and adds some functionalities to it:
 * Geolocation: to track the user's current location,
 * display it and center the map around it.
 * Searching: lets the user search for places and center the map around it.
 * Display place guides: displays the place guides created
 * by the currently logged in user.
 */
let map;
let placeGuideManager;
const availableWidth = window.innerWidth - 370;
const PLACE_GUIDE_DISPLAY_TYPE = "My Guides";

function initPage() {
  authenticateUser().then((userAuthenticationStatus) => {
    if (!userAuthenticationStatus.isLoggedIn) {
      location.replace(userAuthenticationStatus.loginUrl);
    } else {
      const menu = new Menu(Menu.PAGE_NAMES.MY_PLACEGUIDES);
      const mapWidget = new MapWidget();
      mapWidget.addGeolocationFunctionality();
      mapWidget.addSearchingFunctionality();
      map = mapWidget.map;
      const placeGuideRepository =
          new PlaceGuideRepository(
              PlaceGuideRepository.QueryType.CREATED_ALL_IN_MAP_AREA);
      placeGuideManager = 
        new PlaceGuideManager(placeGuideRepository, false, PLACE_GUIDE_DISPLAY_TYPE, true);
      google.maps.event.addListener(map, 'idle', function () {
        placeGuideManager.update(map.getBounds(), map.getZoom(), false);
      });
      document.getElementById("mapDisplayer").style.width = availableWidth.toString() + "px";
    }
  });
}
