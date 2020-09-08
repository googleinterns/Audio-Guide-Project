/**
 * The Geolocator class is responsible for finding and dispaying the users location on the map.
 * {@code enableGoToMyLocationControl} allows the user to center the map
 * at their location by clicking a button.
 * {@code enableGeolocationControl} allows the user to turn on/off
 * the geolocation feature, which displays the user's current location continuously.
 */
class Geolocator {
  static NO_GEOLOCATION_SUPPORT_MSG = "The browser doesn't support geolocation.";
  static ENABLE_GEOLOCATION_MSG = "Please enable geolocation first!";
  static NO_LOCATION_PERMISSION_MSG = "Please allow location permission!.";
  static NO_LOCATION_INFORMATION_MSG = "Location information is unavailable.";
  static REQUEST_TIMEOUT_MSG = "The request to get user location timed out.";
  static UNKNOWN_ERROR_MSG = "An unknown error occurred while geolocating.";
  static LOCATION_NOT_FOUND_MSG = "Your location couldn't be found yet.";

  static GEOLOCATION_ICON_ID = "enableGeolocationIcon";
  static ENABLED_GEOLOCATION_TITLE = "Disable geolocation";
  static ENABLED_GEOLOCATION_ICON = "gps_fixed";
  static DISABLED_GEOLOCATION_TITLE = "Enable geolocation";
  static DISABLED_GEOLOCATION_ICON = "gps_off";

  static GO_TO_MY_LOCATION_ICON_ID = "goToMyLocationIcon";
  static GO_TO_MY_LOCATION_ICON = "center_focus_strong";
  static GO_TO_MY_LOCATION_TITLE = "Go to my location";
  static MY_LOCATION_TITLE = "My current location";

  constructor(map) {
    this._map = map;
    this._trackLocation = false;
    this._foundLocation = false;
    this._watchPositionId = -1;
    this._currentLocation =
        Place.constructPlaceBasedOnCoordinates(map,
            0,
            0,
            Geolocator.MY_LOCATION_TITLE,
            PlaceType.CURRENT_LOCATION,
            false);
    this._currentLocation.visible = false;
    this._geolocationControlDiv =
        this.createControlDiv(Geolocator.ENABLE_GEOLOCATION_TITLE,
            Geolocator.DISABLED_GEOLOCATION_ICON,
            Geolocator.GEOLOCATION_ICON_ID);
    this._map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(
        this._geolocationControlDiv);
    this._myLocationControlDiv =
        this.createControlDiv(
            Geolocator.GO_TO_MY_LOCATION_TITLE,
            Geolocator.GO_TO_MY_LOCATION_ICON,
            Geolocator.GO_TO_MY_LOCATION_ICON_ID);
    this._map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(
        this._myLocationControlDiv);
  }

  static convertCurrentLocationToLatLng(location) {
    return new google.maps.LatLng(
        location.coords.latitude, 
        location.coords.longitude);
  }

  // The Geolocator starts listening to and handling "turn on/off geolocation" 
  // and "go to my location" click events when init() is called. 
  init() {
    this.enableGeolocationControl();
    this.enableGoToMyLocationControl();
  }

  /**
   * This function tries to find the user's current location, and
   * if it is found, it centers the map around it and sets 
   * a higher zoom level.
   * If the location cannot be found, nothing happens.
   */
  static centerMapAtCurrentLocation(map) {
    navigator.geolocation.getCurrentPosition(
      position => {
       map.setZoom(10);
       map.setCenter(
        Geolocator.convertCurrentLocationToLatLng(position));
      }
    );
  }

  enableGeolocationControl() {
    this._geolocationControlDiv.addEventListener("click",
        event => this.onGeolocationControlEvent());
  }

  onGeolocationControlEvent() {
    if (this._trackLocation) {
      this.disableLocationTracking();
    } else {
      this.enableLocationTracking();
    }
  }

  enableLocationTracking() {
    if (navigator.geolocation) {
      this._trackLocation = true;
      var icon = document.getElementById(Geolocator.GEOLOCATION_ICON_ID);
      this._geolocationControlDiv.title = Geolocator.ENABLED_GEOLOCATION_TITLE;
      icon.innerText = Geolocator.ENABLED_GEOLOCATION_ICON;
      this._watchPositionId = navigator.geolocation.watchPosition(
          position => {
            this._foundLocation = true;
            this._currentLocation.position =
                Geolocator.convertCurrentLocationToLatLng(position);
            this._currentLocation.visible = true;
          },
          error => {
            this._foundLocation = false;
            this._currentLocation.visible = false;
            showError(error);
          }
      );
    } else {
      alert(Geolocator.NO_GEOLOCATION_SUPPORT_MSG);
    }
  }

  disableLocationTracking() {
    this._trackLocation = false;
    this._foundLocation = false;
    navigator.geolocation.clearWatch(this._watchPositionId);
    this._currentLocation.visible = false;
    var icon = document.getElementById(Geolocator.GEOLOCATION_ICON_ID);
    icon.innerText = Geolocator.DISABLED_GEOLOCATION_ICON;
    this._geolocationControlDiv.title = Geolocator.DISABLED_GEOLOCATION_TITLE;
  }

  enableGoToMyLocationControl() {
    this._myLocationControlDiv.addEventListener("click",
        event => this.onGoToMyLocationControlEvent());
  }

  onGoToMyLocationControlEvent() {
    if (this._trackLocation) {
      if (this._foundLocation) {
        this._currentLocation.centerMapAround();
      } else {
        alert(Geolocator.LOCATION_NOT_FOUND_MSG);
      }
    } else {
      alert(Geolocator.ENABLE_GEOLOCATION_MSG);
    }
  }

  /** Displays a message corresponding to the error occured in watchposition */
  showError(error) {
    switch (error.code) {
      case error.PERMISSION_DENIED:
        alert(Geolocator.NO_LOCATION_PERMISSION_MSG);
        break;
      case error.POSITION_UNAVAILABLE:
        alert(Geolocator.NO_LOCATION_INFORMATION_MSG);
        break;
      case error.TIMEOUT:
        alert(Geolocator.REQUEST_TIMEOUT_MSG);
        break;
      case error.UNKNOWN_ERROR:
        alert(Geolocator.UNKNOWN_ERROR_MSG);
        break;
    }
  }

  /**
   * Creates a div with the style of control elements and with the given image.
   */
  createControlDiv(title, iconName, iconId) {
    const controlDiv = document.createElement("div");
    controlDiv.setAttribute('class', 'control');
    controlDiv.title = title;
    controlDiv.appendChild(Geolocator.createIcon(iconName, iconId));
    return controlDiv;
  }

  static createIcon(iconName, iconId) {
    var icon = document.createElement("i");
    icon.classList.add("mdc-tab__icon", "material-icons");
    icon.setAttribute("aria-hidden", true);
    icon.setAttribute("id", iconId);
    icon.innerText = iconName;
    return icon;
  }
}
