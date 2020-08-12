var clickedPlaceMarker;
var clickedPlacePosition;

function showClickedPlace(map) {
    clickedPlaceMarker = new google.maps.Marker({
        map: map,
        icon: PlaceType.SEARCH_RESULT.icon,
        draggable:true,
    });
    map.addListener('click', function(mapsMouseEvent) {
        clickedPlacePosition = mapsMouseEvent.latLng;
        clickedPlaceMarker.setPosition(clickedPlacePosition);
    });
    clickedPlaceMarker.addListener('drag', function() {
        clickedPlaceMarker.setTitle(clickedPlaceMarker.getPosition().toString());
    });
}