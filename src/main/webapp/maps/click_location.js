function enableChoosingPlaceByClick(map, chosenPlace) {
    map.addListener('click', function(mapsMouseEvent) {
        if (mapsMouseEvent.placeId) {
            chosenPlace.updatePlaceAndCenterBasedOnId(mapsMouseEvent.placeId)
        } else {
            chosenPlace.position = mapsMouseEvent.latLng;
        }
    });
}