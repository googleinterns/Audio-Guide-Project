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
  authenticateUser().then((userAuthenticationStatus) => {
    if (!userAuthenticationStatus.isLoggedIn) {
      location.replace(userAuthenticationStatus.loginUrl);
    } else {
      const menu = new Menu(Menu.PAGE_NAMES.CREATE_PLACEGUIDE);
      fitContent();
      window.addEventListener('resize', function() {
        fitContent();
      });
      const mapWidget = new MapWidget();
      map = mapWidget.map;
      mapWidget.addGeolocationFunctionality();
      setUpPlaceGuideCreation()
          .then((placeGuideToEdit) => {
            placeGuideManager = new PlaceGuideManager(
                PlaceGuideManager.PAGE.CREATE_PLACE_GUIDE, map);
            if (placeGuideToEdit !== null) {
              placeGuideManager.setEditedPlaceGuide(placeGuideToEdit.id);
            } else {
              mapWidget.centerAtCurrentLocation();
            }
            mapWidget
                .addLocationChoosingAndSavingFunctionality(placeGuideToEdit);
          });
      document.getElementById('map')
          .addEventListener(MapWidget.CHOSEN_LOCATION_CHANGE_EVENT, function() {
            handleChosenLocationChangeEvent(mapWidget);
          });
    }
  });
}

function handleChosenLocationChangeEvent(mapWidget) {
  enableSubmission();
  if (mapWidget.pickedLocation.place != null) {
    updateLocation(mapWidget.pickedLocation.position,
        mapWidget.pickedLocation.place.place_id);
  } else {
    updateLocation(mapWidget.pickedLocation.position, null);
  }
}

function fitContent() {
  setMapWidth();
  setContentHeight();
}
