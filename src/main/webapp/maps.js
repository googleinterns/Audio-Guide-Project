var currentLocationMarker = null;
var trackUser;
var watchPositionId;

function createMap() {
    var myMapOptions = {
        zoom: 2,
        center: new google.maps.LatLng(0, 0),
        mapTypeId: 'roadmap',
    };
    const map = new google.maps.Map(
        document.getElementById('map'), myMapOptions); 
    addGoToMyLocationControl(map);
}

function addGoToMyLocationControl(map) {
    var imgId = "myLocationIcon";
    const myLocationControlDiv = createControlDiv("Go to my location", "./img/my_location.svg", imgId);
    myLocationControlDiv.index = 1;
    map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(myLocationControlDiv);
    trackUser = false;
    myLocationControlDiv.addEventListener("click", () => {
        var img = document.getElementById(imgId);
        trackUser = !trackUser;
        console.log("trackuser=" + trackUser);
        if (trackUser) {
            img.src = "./img/my_location_active.svg";
            if (navigator.geolocation) {
                console.log("start watchPosition");
                watchPositionId = navigator.geolocation.watchPosition(
                        position => {showCurrentlocation(position, map); centerMapToCurrentLocation(position, map)},
                        error => watchPositionError(error));
            } else {
                alert("The browser doesn't support geolocation.");
            }
        } else {
            console.log("stop watchPosition");
            navigator.geolocation.clearWatch(watchPositionId);
            removeCurrentLocationMarker();
            img.src = "./img/my_location.svg";
        }
    });
}

function centerMapToCurrentLocation(position, map) {
    currentPos = {
        lat: position.coords.latitude,
        lng: position.coords.longitude
    };
    map.setCenter(currentPos);
    map.setZoom(14);
}

function showCurrentlocation(position, map) {
    console.log("new position displayed");
    currentPos = {
        lat: position.coords.latitude,
        lng: position.coords.longitude
    };
    showCurrentLocationMarker(map, currentPos);
}

function watchPositionError(error) {
    showError(error);
    removeCurrentLocationMarker();
}

function showCurrentLocationMarker(map, currentPos) {
    if (currentLocationMarker === null) {
        currentLocationMarker = new google.maps.Marker({
            map: map,
            position: currentPos,
            icon: "./img/blue_dot.png"
        });
    } 
    console.log("currentLocationMarker: " + currentLocationMarker);
    currentLocationMarker.setMap(map);
    currentLocationMarker.setPosition(currentPos);
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