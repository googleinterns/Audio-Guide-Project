/**
 * This function initialises the map and adds some functionalities to it:
 * Geolocation: to track the user's current location, display it and center
 * the map around it.
 * Searching: lets the user search for places and center the map around it.
 * Display place guides: displays the place guides created
 * by the currently logged in user.
 */
let map;
let placeGuideManager;
const PLACE_GUIDE_DISPLAY_TYPE = "Bookmarked Guides";

function initPage() {
  authenticateUser().then((userAuthenticationStatus) => {
    if (!userAuthenticationStatus.isLoggedIn) {
      location.replace(userAuthenticationStatus.loginUrl);
    } else {
        saveUserInDatabase().then(response => {
            const menu = new Menu(Menu.PAGE_NAMES.BOOKMARKED_PLACEGUIDES);
            const mapWidget = new MapWidget();
            mapWidget.addGeolocationFunctionality();
            mapWidget.addSearchingFunctionality();
            map = mapWidget.map;
            const placeGuideRepository =
                new PlaceGuideRepository(
                    PlaceGuideRepository.QueryType.BOOKMARKED);
            placeGuideManager = 
                new PlaceGuideManager(placeGuideRepository, true, PLACE_GUIDE_DISPLAY_TYPE, false);
            placeGuideManager.update(map.getBounds(), map.getZoom(), true);
        });
    }
  });
}
