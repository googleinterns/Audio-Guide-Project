/**
 * This function initialises the map and adds some functionalities to it:
 * Geolocation: to track the user's current location, display it
 * and center the map around it.
 * Searching: lets the user search for places and center the map around it.
 * Display place guides: displays all the public place guides.
 */
let map;
let placeGuideManager;

function initPage() {
  authenticateUser().then((userAuthenticationStatus) => {
    if (!userAuthenticationStatus.isLoggedIn) {
      location.replace(userAuthenticationStatus.loginUrl);
    } else {
      saveUserInDatabase().then((response) => {
        const menu = new Menu(Menu.PAGE_NAMES.DISCOVER);
        fitContent();
        window.addEventListener('resize', function() {
          fitContent();
        });
        const mapWidget = new MapWidget();
        mapWidget.addGeolocationFunctionality();
        mapWidget.addSearchingFunctionality();
        map = mapWidget.map;
        placeGuideManager = new PlaceGuideManager(
            PlaceGuideManager.PAGE.DISCOVER, map);
      });
    }
  });
}

function fitContent() {
  setMapWidth();
  setContentHeight();
}
