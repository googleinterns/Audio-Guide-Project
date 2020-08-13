function enableChoosingPlaceByClick(map, chosenPlace) {
    map.addListener('click', function(mapsMouseEvent) {
        if (mapsMouseEvent.placeId) {
            chosenPlace.updatePlaceAndCenterBasedOnId(map, mapsMouseEvent.placeId)
        } else {
            chosenPlace.position = mapsMouseEvent.latLng;
        }
    });
}