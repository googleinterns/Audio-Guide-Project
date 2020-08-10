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
        centerMapAtUsersLocation(map);
    });
}

function centerMapAtUsersLocation(map) {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(position) {
            var pos = {
                lat: position.coords.latitude,
                lng: position.coords.longitude
            };
            if (currentLocationMarker != null) {
                currentLocationMarker.setMap(null);
            }
            currentLocationMarker = new google.maps.Marker({
                map: map,
                animation: google.maps.Animation.DROP,
                position: pos,
                icon: "./img/blue_dot.png"
            });
            currentLocationMarker.setMap(map);
            map.setCenter(pos);
            map.setZoom(15);
        }, function() {
            alert("The geolocation service failed.");
        });
    } else {
        alert("The browser doesn't support geolocation.");
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
      // add support for text;
  }
  return controlDiv;
}