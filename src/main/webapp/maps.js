function createMap() {
    console.log("create map");
    var myMapOptions = {
        zoom: 2,
        center: new google.maps.LatLng(0, 0),
        mapTypeId: 'roadmap',
    };
    const map = new google.maps.Map(
        document.getElementById('map'), myMapOptions); 
}