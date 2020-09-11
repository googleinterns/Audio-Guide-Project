/**
 * This function initialises the map and adds some functionalities to it:
 * Geolocation: to track the user's current location,
 * display it and center the map around it.
 * Searching: lets the user search for places and center the map around it.
 * Display place guides: displays the place guides created
 * by the user with the given creatorId.
 */
let map;
let placeGuideManager;

function initPage() {
  authenticateUser().then((userAuthenticationStatus) => {
    if (!userAuthenticationStatus.isLoggedIn) {
      location.replace(userAuthenticationStatus.loginUrl);
    } else {
      const menu = new Menu(undefined);
      const mapWidget = new MapWidget();
      mapWidget.addGeolocationFunctionality();
      mapWidget.addSearchingFunctionality();
      map = mapWidget.map;
      const creatorId = "167176955777861719774";
      placeGuideManager = new PlaceGuideManager(
          PlaceGuideManager.PAGE.USERS_PORTFOLIO, map, creatorId);
    }
  });
}
