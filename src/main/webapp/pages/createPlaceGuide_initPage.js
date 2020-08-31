/**
 * This function initialises the map and adds some functionalities to it:
 * Geolocation: to track the user's current location,
 * display it and center the map around it.
 * Choose location: the user can choose  alocation through the searchbox,
 * by clicking on any point on the map, by clicking on a POI
 * or by moving the marker of the chosen location.
 * Save location: lets the user save the currently chosen location
 * for the place guide creation.
 * Display place guides: displays the place guides created
 * by the currently logged in user.
 */

let map;
let placeGuideManager;

function initPage() {
  authenticateUser().then(userAuthenticationStatus => {
    if (!userAuthenticationStatus.isLoggedIn) {
      location.replace(userAuthenticationStatus.loginUrl);
    } else {
      const menu = new Menu(Menu.PAGE_NAMES.CREATE_PLACEGUIDE);
      addLinktoLogoutButton(userAuthenticationStatus.logoutUrl);
      setUpCreatePlaceGuideForm();
      let mapWidget = new MapWidget();
      mapWidget.addGeolocationFunctionality();
      mapWidget.addLocationChoosingAndSavingFunctionality();
      map = mapWidget.map;
      let placeGuideRepository =
          new PlaceGuideRepository(
              PlaceGuideRepository.QueryType.CREATED_ALL_IN_MAP_AREA);
      placeGuideManager = new PlaceGuideManager(placeGuideRepository);
      google.maps.event.addListener(map, 'idle', function () {
        placeGuideManager.update(map.getBounds(), map.getZoom(), false);
      });
      document.getElementById("map")
          .addEventListener(MapWidget.SAVE_LOCATION_EVENT, function () {
            handleSaveLocationEvent(mapWidget);
          });
    }
  });
}

function handleSaveLocationEvent(mapWidget) {
  if (mapWidget.savedLocation.place != null) {
    updateLocation(mapWidget.savedLocation.position,
                  mapWidget.savedLocation.place.place_id,
                  mapWidget.savedLocation.place.name);
  } else {
    updateLocation(mapWidget.savedLocation.position, null, null);
  }
}
