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

const LIST_WIDTH = 370;

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
      placeGuideManager = new PlaceGuideManager(
          PlaceGuideManager.PAGE.MY_GUIDES, map);
      setMapWidth();
      window.addEventListener('resize', setMapWidth);
    }
  });
}

function setMapWidth() {
  const availableWidth = window.innerWidth - LIST_WIDTH;
  document.getElementById('mapDisplayer').style.width = availableWidth.toString() + 'px';
}
