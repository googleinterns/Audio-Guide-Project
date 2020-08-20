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

  static GEOLOCATION_IMG_ID = "enableGeolocationIcon";
  static ENABLE_GEOLOCATION_TITLE = "Enable geolocation";
  static DISABLED_GEOLOCATION_IMG_SRC = "./img/geolocation.svg";
  static DISABLE_GEOLOCATION_TITLE = "Disable geolocation";
  static ENABLE_GEOLOCATION_IMG_SRC = "./img/geolocation_active.svg";

  static GO_TO_MY_LOCATION_IMG_ID = "goToMyLocationIcon";
  static GO_TO_MY_LOCATION_TITLE = "Go to my location";
  static MY_LOCATION_TITLE = "My current location";

  constructor(map) {
    this._map = map;
    this._trackLocation = false;
    this._foundLocation = false;
    this._watchPositionId = -1;
    this._currentLocation =
        Place.constructPlaceBasedOnCoordinates(map, 0, 0, Geolocator.MY_LOCATION_TITLE, PlaceType.CURRENT_LOCATION, true);
    this._currentLocation.visible = false;
    this._geolocationControlDiv =
        this.createControlDiv(Geolocator.ENABLE_GEOLOCATION_TITLE,
            Geolocator.DISABLED_GEOLOCATION_IMG_SRC, Geolocator.GEOLOCATION_IMG_ID);
    this._map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(this._geolocationControlDiv);
    this._myLocationControlDiv =
        this.createControlDiv(Geolocator.GO_TO_MY_LOCATION_TITLE,
            "./img/my_location.svg", Geolocator.GO_TO_MY_LOCATION_IMG_ID);
    this._map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(this._myLocationControlDiv);
  }

  static convertCurrentLocationToLatLng(location) {
    var locationLatLng = {
      lat: location.coords.latitude,
      lng: location.coords.longitude
    };
    return locationLatLng;
  }

  // The Geolocator starts listening to and handling "turn on/off geolocation" 
  // and "go to my location" click events when init() is called. 
  init() {
    this.enableGeolocationControl();
    this.enableGoToMyLocationControl();
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

  enableLocationTracking(img) {
    if (navigator.geolocation) {
      this._trackLocation = true;
      var img = document.getElementById(Geolocator.GEOLOCATION_IMG_ID);
      this._geolocationControlDiv.title = Geolocator.DISABLE_GEOLOCATION_TITLE;
      img.src = Geolocator.ENABLE_GEOLOCATION_IMG_SRC;
      this._watchPositionId = navigator.geolocation.watchPosition(
          position => {
            this._foundLocation = true;
            this._currentLocation.position = Geolocator.convertCurrentLocationToLatLng(position);
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

  disableLocationTracking(geolocationControlDiv, img) {
    this._trackLocation = false;
    this._foundLocation = false;
    navigator.geolocation.clearWatch(this._watchPositionId);
    this._currentLocation.visible = false;
    var img = document.getElementById(Geolocator.GEOLOCATION_IMG_ID);
    img.src = Geolocator.DISABLED_GEOLOCATION_IMG_SRC;
    this._geolocationControlDiv.title = Geolocator.ENABLE_GEOLOCATION_TITLE;
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

  /** Creates a div with the style of control elements and with the given image. */
  createControlDiv(title, imgSrc, imgId) {
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
}
