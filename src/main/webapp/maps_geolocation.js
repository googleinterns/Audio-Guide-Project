/**
 * {@code addGoToMyLocationControl} allows the user to center the map
 * at their location by clicking a button.
 * {@code addEnableGeolocationControl} allows the user to turn on/off
 * the geolocation feature, which displays the user's current location continuously.
 */
class Geolocator {
    static NO_GEOLOCATION_SUPPORT_MSG = "The browser doesn't support geolocation.";
    static ENABLE_GEOLOCATION_MSG = "Please enable geolocation first!";
    static NO_LOCATION_PERMISSION_MSG = "Please allow location permission!.";
    static NO_LOCATION_INFORMATION_MSG = "Location information is unavailable.";
    static REQUEST_TIMEOUT_MSG = "The request to get user location timed out.";
    static UNKNOWN_ERROR_MSG = "An unknown error occurred while geolocating.";

    static GEOLOCATION_IMG_ID = "enableGeolocationIcon";
    static ENABLE_GEOLOCATION_TITLE = "Enable geolocation";
    static DISABLED_GEOLOCATION_IMG_SRC = "./img/geolocation.svg"
    static DISABLE_GEOLOCATION_TITLE = "Disable geolocation";
    static ENABLE_GEOLOCATION_IMG_SRC = "./img/geolocation_active.svg";

    static GO_TO_MY_LOCATION_IMG_ID = "goToMyLocationIcon";
    static GO_TO_MY_LOCATION_TITLE = "Go to my location";
    static MY_LOCATION_TITLE = "My current location";

    constructor(map){
        this._map = map;
        this._trackUser = false;
        this._watchPositionId = -1;
        this._currentLocation = new Place(0, 0, Geolocator.MY_LOCATION_TITLE, null, PlaceType.CURRENT_LOCATION, false);
        this._currentLocation.visible = false;
        this._currentLocation.map = map;
        this._geolocationControlDiv = 
            this.createControlDiv(Geolocator.ENABLE_GEOLOCATION_TITLE, 
                    Geolocator.DISABLED_GEOLOCATION_IMG_SRC, Geolocator.GEOLOCATION_IMG_ID);
        this._map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(this._geolocationControlDiv);
        //this._geolocationControlDiv.index = 1;
        this._myLocationControlDiv =
            this.createControlDiv(Geolocator.GO_TO_MY_LOCATION_TITLE,
                    "./img/my_location.svg", Geolocator.GO_TO_MY_LOCATION_IMG_ID);
        this._map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(this._myLocationControlDiv);
    }

    init() {
        this.enableGeolocationControl();
        this.enableGoToMyLocationControl();
    }

    /**
    * Adds a button to the map which turns on/off geolocation.
    * Geolocation can be battery-consuming, and the user should be able
    * to tund it off.
    * Remark that the audio-guide creation process doesn't require the user's location at all.
    */
    enableGeolocationControl() {
        this._trackUser = false;
        this._geolocationControlDiv.addEventListener("click",
                event => this.geolocationControlEvent());
    }

    /**
    * When the geolocation button gets clicked, its status is toggled.
    * If geolocation is on, then each time the user's loctaion changes,
    * and event will be triggered,
    * and the currentLocationMarker and currentlocation will be reset
    */
    geolocationControlEvent() {
        var img = document.getElementById(Geolocator.GEOLOCATION_IMG_ID);
        this._trackUser = !this._trackUser;
        if (this._trackUser) {
            this._geolocationControlDiv.title = Geolocator.DISABLE_GEOLOCATION_TITLE;
            img.src = Geolocator.ENABLE_GEOLOCATION_IMG_SRC;
            if (navigator.geolocation) {
            this._watchPositionId = navigator.geolocation.watchPosition(
                position => {
                    this._currentLocation.position = Geolocator.convertCurrentLocationToLatLng(position);
                    this._currentLocation.visible = true;
                },
                error => {
                    this._currentLocation.visible = false;
                    showError(error);
                }
            );
            } else {
            alert(Geolocator.NO_GEOLOCATION_SUPPORT_MSG);
            }
        } else {
            navigator.geolocation.clearWatch(this._watchPositionId);
            this._currentLocation.visible = false;
            img.src = Geolocator.DISABLED_GEOLOCATION_IMG_SRC;
            this._geolocationControlDiv.title = Geolocator.ENABLE_GEOLOCATION_TITLE;
        }
    }

    /**
    * Adds a button to the map which lets the user center the map around their current location.
    */
    enableGoToMyLocationControl() {
        this._trackUser = false;
        this._myLocationControlDiv.addEventListener("click",
            event => this.goToMyLocationControlEvent());
    }

    /**
    * When the goToMyLocation-button is clicked, if the user's location is available,
    * the maps gets centered around it.
    * Otherwise, a message will be displayed to enable geolocation.
    */
    goToMyLocationControlEvent() {
        if (this._trackUser) {
            if (navigator.geolocation) {
                this._currentLocation.centerMapAround(this._map);
            } else {
                alert(Geolocator.NO_GEOLOCATION_SUPPORT_MSG);
            }
        } else {
            alert(Geolocator.ENABLE_GEOLOCATION_MSG);
        }
    }

    static convertCurrentLocationToLatLng(location) {
        var locationLatLng = {
            lat: location.coords.latitude,
            lng: location.coords.longitude
        };
        return locationLatLng;
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
