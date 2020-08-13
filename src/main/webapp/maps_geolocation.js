/**
 * {@code addGoToMyLocationControl} allows the user to center the map
 * at their location by clicking a button.
 * {@code addEnableGeolocationControl} allows the user to turn on/off
 * the geolocation feature, which displays the user's current location continuously.
 */

// var currentLocationMarker = null;
var trackUser = false;
var watchPositionId;
var currentLocation;

const NO_GEOLOCATION_SUPPORT_MSG = "The browser doesn't support geolocation.";
const ENABLE_GEOLOCATION_MSG = "Please enable geolocation first!";
const NO_LOCATION_PERMISSION_MSG = "Please allow location permission!.";
const NO_LOCATION_INFORMATION_MSG = "Location information is unavailable.";
const REQUEST_TIMEOUT_MSG = "The request to get user location timed out.";
const UNKNOWN_ERROR_MSG = "An unknown error occurred while geolocating.";

const GEOLOCATION_IMG_ID = "enableGeolocationIcon";
const ENABLE_GEOLOCATION_TITLE = "Enable geolocation";
const DISABLED_GEOLOCATION_IMG_SRC = "./img/geolocation.svg"
const DISABLE_GEOLOCATION_TITLE = "Disable geolocation";
const ENABLE_GEOLOCATION_IMG_SRC = "./img/geolocation_active.svg";

const GO_TO_MY_LOCATION_IMG_ID = "goToMyLocationIcon";
const GO_TO_MY_LOCATION_TITLE = "Go to my location";

/**
 * Adds a button to the map which turns on/off geolocation.
 * Geolocation can be battery-consuming, and the user should be able
 * to tund it off.
 * Remark that the audio-guide creation process doesn't require the user's location at all.
 */
function addEnableGeolocationControl(map) {
  currentLocation = new Place(0, 0, "My current location", null, PlaceType.CURRENT_LOCATION, false);
  currentLocation.visible = false;
  currentLocation.map = map;
  const geolocationControlDiv =
      createControlDiv(ENABLE_GEOLOCATION_TITLE, DISABLED_GEOLOCATION_IMG_SRC, GEOLOCATION_IMG_ID);
  geolocationControlDiv.index = 1;
  map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(geolocationControlDiv);
  trackUser = false;
  geolocationControlDiv.addEventListener("click",
          event => geolocationControlEvent(map, geolocationControlDiv));
}

/**
 * When the geolocation button gets clicked, its status is toggled.
 * If geolocation is on, then each time the user's loctaion changes,
 * and event will be triggered,
 * and the currentLocationMarker and currentlocation will be reset
 */
function geolocationControlEvent(map, geolocationControlDiv) {
  var img = document.getElementById(GEOLOCATION_IMG_ID);
  trackUser = !trackUser;
  if (trackUser) {
    geolocationControlDiv.title = DISABLE_GEOLOCATION_TITLE;
    img.src = ENABLE_GEOLOCATION_IMG_SRC;
    if (navigator.geolocation) {
      watchPositionId = navigator.geolocation.watchPosition(
          position => {
              currentLocation.position = convertCurrentLocationToLatLng(position);
              currentLocation.visible = true;
          },
          error => {
              currentLocation.visible = false;
              showError(error);
          }
      );
    } else {
      alert(NO_GEOLOCATION_SUPPORT_MSG);
    }
  } else {
    navigator.geolocation.clearWatch(watchPositionId);
    currentLocation.visible = false;
    img.src = DISABLED_GEOLOCATION_IMG_SRC;
    geolocationControlDiv.title = ENABLE_GEOLOCATION_TITLE;
  }
}

/**
 * Adds a button to the map which lets the user center the map around their current location.
 */
function addGoToMyLocationControl(map) {
  const myLocationControlDiv =
      createControlDiv(GO_TO_MY_LOCATION_TITLE,
              "./img/my_location.svg", GO_TO_MY_LOCATION_IMG_ID);
  myLocationControlDiv.index = 1;
  map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(myLocationControlDiv);
  trackUser = false;
  myLocationControlDiv.addEventListener("click",
      event => goToMyLocationControlEvent(map));
}

/**
 * When the goToMyLocation-button is clicked, if the user's location is available,
 * the maps gets centered around it.
 * Otherwise, a message will be displayed to enable geolocation.
 */
function goToMyLocationControlEvent(map) {
  if (trackUser) {
    if (navigator.geolocation) {
      currentLocation.centerMapAround(map);
    } else {
      alert(NO_GEOLOCATION_SUPPORT_MSG);
    }
  } else {
    alert(ENABLE_GEOLOCATION_MSG);
  }
}

function convertCurrentLocationToLatLng(location) {
  locationLatLng = {
    lat: location.coords.latitude,
    lng: location.coords.longitude
  };
  return locationLatLng;
}

/** Displays a message corresponding to the error occured in watchposition */
function showError(error) {
  switch (error.code) {
    case error.PERMISSION_DENIED:
      alert(NO_LOCATION_PERMISSION_MSG);
      break;
    case error.POSITION_UNAVAILABLE:
      alert(NO_LOCATION_INFORMATION_MSG);
      break;
    case error.TIMEOUT:
      alert(REQUEST_TIMEOUT_MSG);
      break;
    case error.UNKNOWN_ERROR:
      alert(UNKNOWN_ERROR_MSG);
      break;
  }
}

/** Creates a div with the style of control elements and with the given image. */
function createControlDiv(title, imgSrc, imgId) {
  const controlDiv = document.createElement("div");
  controlDiv.setAttribute('class', 'control');
  controlDiv.title = title;
  if (imgSrc != null) {
    const controlImg = document.createElement("img");
    controlImg.setAttribute('id', imgId);
    controlImg.src = imgSrc;
    controlDiv.appendChild(controlImg);
  }
  return controlDiv;
}