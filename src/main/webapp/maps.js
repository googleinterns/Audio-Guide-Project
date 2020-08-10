var currentLocationMarker;

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
    const myLocationControlDiv = createControlDiv("Go to my location", "./img/my_location.svg", null);
    myLocationControlDiv.index = 1;
    map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(myLocationControlDiv);
    myLocationControlDiv.addEventListener("click", () => {
        showCurrentLocation(map);
    });
}

function showCurrentLocation(map) {
    if (navigator.geolocation) {
        var pos = null;
        navigator.geolocation.getCurrentPosition(function(position) {
            currentPos = {
                lat: position.coords.latitude,
                lng: position.coords.longitude
            };
            showCurrentLocationMarker(map, currentPos);
            map.setCenter(currentPos);
            map.setZoom(15);
        },  error => {
            showError(error);
            removeCurrentLocationMarker();
        });
    } else {
        alert("The browser doesn't support geolocation.");
        removeCurrentLocationMarker();
    }
}

function showCurrentLocationMarker(map, currentPos) {
    removeCurrentLocationMarker();
    currentLocationMarker = new google.maps.Marker({
        map: map,
        animation: google.maps.Animation.DROP,
        position: currentPos,
        icon: "./img/blue_dot.png"
    });
    currentLocationMarker.setMap(map);
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

function createControlDiv(title, imgSrc, text) {
  const controlDiv = document.createElement("div");
  controlDiv.setAttribute('class', 'control');
  controlDiv.title = title;
  if (imgSrc != null ) {
    const controlImg = document.createElement("img");
    controlImg.src = imgSrc;
    controlDiv.appendChild(controlImg);
  } else if (text != null) {
      // Add support for text.
  }
  return controlDiv;
}