function createMap() {
    console.log("create map");
    var myMapOptions = {
        zoom: 2,
        center: new google.maps.LatLng(0, 0),
        mapTypeId: 'roadmap',
    };
    const map = new google.maps.Map(
        document.getElementById('map'), myMapOptions); 
    centerMapAtUsersLocation(map);
}

function centerMapAtUsersLocation(map) {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(position) {
            var pos = {
                lat: position.coords.latitude,
                lng: position.coords.longitude
            };
            map.setCenter(pos);
            map.setZoom(7);
        }, function() {
            alert("The geolocation service failed.");
        });
    } else {
        alert("The browser doesn't support geolocation.");
    }
}