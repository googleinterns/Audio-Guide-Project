var currentLocationMarker = null;
var trackUser = false;
var watchPositionId;
var currentLocation;

function addEnableGeolocationControl(map) {
    var imgId = "enableGeolocationIcon";
    var enableTitle = "Enable geolocation";
    var disabledImgSrc = "./img/geolocation.svg"
    var disableTitle = "Disable geolocation";
    var enabledImgSrc = "./img/geolocation_active.svg"
    const geolocationControlDiv = createControlDiv(enableTitle, disabledImgSrc, imgId);
    geolocationControlDiv.index = 1;
    map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(geolocationControlDiv);
    trackUser = false;
    geolocationControlDiv.addEventListener("click", () => {
        var img = document.getElementById(imgId);
        trackUser = !trackUser;
        if (trackUser) {
            geolocationControlDiv.title = disableTitle;
            img.src = enabledImgSrc;
            if (navigator.geolocation) {
                watchPositionId = navigator.geolocation.watchPosition(
                    position => {saveCurrentlocation(position, map); showCurrentLocationMarker(map)},
                    error => watchPositionError(error));
            } else {
                alert("The browser doesn't support geolocation.");
            }
        } else {
            navigator.geolocation.clearWatch(watchPositionId);
            removeCurrentLocationMarker();
            img.src = disabledImgSrc;
            geolocationControlDiv.title = enableTitle;
        }
    });
}

function addGoToMyLocationControl(map) {
    var imgId = "goToMyLocationIcon";
    const myLocationControlDiv = createControlDiv("Go to my location", "./img/my_location.svg", imgId);
    myLocationControlDiv.index = 1;
    map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(myLocationControlDiv);
    trackUser = false;
    myLocationControlDiv.addEventListener("click", () => {
        if (trackUser) {
            if (navigator.geolocation) {
                centerMapToCurrentLocation(map);
            } else {
                alert("The browser doesn't support geolocation.");
            }
        } else {
            alert("Please enable geolocation first!");
        }
    });
}

function centerMapToCurrentLocation(map) {
    map.setCenter(currentLocation);
    map.setZoom(placeZoom);
}

function saveCurrentlocation(position, map) {
    currentLocation = {
        lat: position.coords.latitude,
        lng: position.coords.longitude
    };
}

function watchPositionError(error) {
    showError(error);
    removeCurrentLocationMarker();
}

function showCurrentLocationMarker(map) {
    if (currentLocationMarker === null) {
        currentLocationMarker = new google.maps.Marker({
            map: map,
            position: currentLocation,
            icon: "./img/blue_dot.png"
        });
    } 
    currentLocationMarker.setMap(map);
    currentLocationMarker.setPosition(currentLocation);
}

function removeCurrentLocationMarker(){
    if (currentLocationMarker != null) {
        currentLocationMarker.setMap(null);
    }
}

function showError(error) {
  switch(error.code) {
    case error.PERMISSION_DENIED:
      alert("Please allow location permission!.");
      break;
    case error.POSITION_UNAVAILABLE:
      alert("Location information is unavailable.");
      break;
    case error.TIMEOUT:
      alert("The request to get user location timed out.");
      break;
    case error.UNKNOWN_ERROR:
     alert("An unknown error occurred while geolocating.");
      break;
  }
}

function createControlDiv(title, imgSrc, imgId) {
  const controlDiv = document.createElement("div");
  controlDiv.setAttribute('class', 'control');
  controlDiv.title = title;
  if (imgSrc != null ) {
    const controlImg = document.createElement("img");
    controlImg.setAttribute('id', imgId);
    controlImg.src = imgSrc;
    controlDiv.appendChild(controlImg);
  } 
  return controlDiv;
}