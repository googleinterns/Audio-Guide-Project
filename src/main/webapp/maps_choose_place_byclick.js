var chosenPlaceMarker;
var chosenPlacePosition;

function enableChoosingPlaceByClick(map) {
    chosenPlaceMarker = new google.maps.Marker({
        map: map,
        icon: getMarkerIcon(PlaceType.SEARCH_RESULT.iconColor),
        draggable:true,
    });
    map.addListener('click', function(mapsMouseEvent) {
        if (mapsMouseEvent.placeId) {
            console.log("you clicked on place with id " + mapsMouseEvent.placeId);
        } else {
            chosenPlacePosition = mapsMouseEvent.latLng;
            chosenPlaceMarker.setPosition(chosenPlacePosition);
        }
    });
    chosenPlaceMarker.addListener('drag', function() {
        chosenPlaceMarker.setTitle(chosenPlaceMarker.getPosition().toString());
    });
}