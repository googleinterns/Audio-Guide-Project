function initAutocomplete(map) {
    const input = document.getElementById("search-box");
    var autocomplete = new google.maps.places.Autocomplete(input);
    map.controls[google.maps.ControlPosition.TOP_CENTER].push(input);

    var searchedPlaceMarker = new google.maps.Marker({
        map: map,
    });
    searchedPlaceMarker.setVisible(false);
    searchedPlaceMarker.setMap(map);

    autocomplete.setFields(['address_components', 'geometry', 'icon', 'name']);
    autocomplete.addListener('place_changed', result => showNewSearchResult(map, autocomplete, input, searchedPlaceMarker));
}

function showNewSearchResult(map, autocomplete, input, searchedPlaceMarker) {
    searchedPlaceMarker.setVisible(false);
    var place = autocomplete.getPlace();
    if (!place.geometry) {
        // If the user didn't choose a suggestion, but pressed enter, 
        // then "place" may have no geometry. 
        // In this case, search for the "closest" place with geometry and show that.
        showBestSearchResultOnEnter(map, input, autocomplete, searchedPlaceMarker);
        return;
    }
    displaySearchResultOnMap(map, place, searchedPlaceMarker, true);
}

function displaySearchResultOnMap(map, place, placeMarker, centerMap) {
    if (centerMap) {
        centerMapAroundPlace(map, place);
    }
    placeMarker.setPosition(place.geometry.location);
    placeMarker.setVisible(true);
}

function centerMapAroundPlace(map, place) {
    if (place.geometry.viewport) {
        map.fitBounds(place.geometry.viewport);
    } else {
        map.setCenter(place.geometry.location);
        map.setZoom(placeZoom);
    }
}

function showBestSearchResultOnEnter(map, input, autocomplete, searchedPlaceMarker) {
    var service = new google.maps.places.AutocompleteService();
    service.getPlacePredictions({ input:  input.value}, function(predictions, status){
        if (status != google.maps.places.PlacesServiceStatus.OK) {
            alert(status);
            return;
        }
        showPlaceBasedOnId(map, predictions[0].place_id, searchedPlaceMarker);
    });
}

function showPlaceBasedOnId(map, id, placeMarker) {
    const geocoder = new google.maps.Geocoder();
    geocoder.geocode({placeId: id}, (results, status) => {
      if (status !== "OK") {
        window.alert("Geocoder failed due to: " + status);
        return;
      }
      displaySearchResultOnMap(map, results[0], placeMarker, true);
    });
}