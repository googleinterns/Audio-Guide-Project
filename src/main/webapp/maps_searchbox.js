/**
 * This module's {@code initAutocomplete} sets up a search-box with auto-suggestions feature,
 * so that the user can search for a specific place.
 *
 * When a search query is submitted,
 * the map is centered at the requested place and a marker is displayed for it.
 *
 * By default, the Autocomplete widget provided by Places API
 * supports only submitting queries for one of the suggetsions.
 * This makes the searching process quite uncomportable, since the user can't press enter
 * if they didn't write the exact name of the place, they had a typo, etc.
 * To avoid this issue, I extended the autocomplete widget's eventlistener
 * so that if the user didn't choose one of the suggestions explicitly,
 * then I programatically choose the first place suggestion when the query is submitted.
 */

const GEOCODER_FAIL_MSG = "Geocoder failed due to: ";
var searchedPlaceMarker;
var searchedPlaceId;

function initAutocomplete(map) {
  const input = document.getElementById("search-box");
  var autocomplete = new google.maps.places.Autocomplete(input);
  map.controls[google.maps.ControlPosition.TOP_CENTER].push(input);

  searchedPlaceMarker = new google.maps.Marker({map: map});
  searchedPlaceMarker.setVisible(false);
  searchedPlaceMarker.setMap(map);

  autocomplete.setFields(['address_components', 'geometry', 'icon', 'name']);
  autocomplete.addListener('place_changed',
      result => showNewSearchResult(map, autocomplete, input));
}

/**
 * Gets the place searched by the user if possible,
 * centers the map around it and adds a marker for it.
 * If the user didn't choose an existing place, display
 * the best suggestion instead. 
 */
function showNewSearchResult(map, autocomplete, input) {
  searchedPlaceMarker.setVisible(false);
  var place = autocomplete.getPlace();
  if (!place.geometry) {
    // If the user didn't choose a suggestion, but pressed enter,
    // then "place" may have no geometry.
    // In this case, search for the "closest" place with geometry and show that.
    showClosestResult(map, input, autocomplete);
    return;
  }
  searchedPlaceId = place.place_id;
  displaySearchResultOnMap(map, place, true);
}

/**
 * This function gets the best result(prediction) for the submitted search query
 * and displays it as the new search result.
 */
function showClosestResult(map, input, autocomplete) {
  var service = new google.maps.places.AutocompleteService();
  service.getPlacePredictions({input: input.value}, function (predictions, status) {
    if (status != google.maps.places.PlacesServiceStatus.OK) {
      alert(status);
      return;
    }
    searchedPlaceId = predictions[0].place_id;
    showPlaceBasedOnId(map, predictions[0].place_id);
  });
}

/**
 * This function relies on Geocoder API to get the place
 * corresponding to an id, provided by the Places API,
 * and then display it as the new search result.
 */
function showPlaceBasedOnId(map, id) {
  const geocoder = new google.maps.Geocoder();
  geocoder.geocode({placeId: id}, (results, status) => {
    if (status !== "OK") {
      window.alert(GEOCODER_FAIL_MSG + status);
      return;
    }
    displaySearchResultOnMap(map, results[0], true);
  });
}

/**
 * Move placeMarker to {@param place} and
 * center the map around it if centerMap is true.
 */
function displaySearchResultOnMap(map, place, centerMap) {
  if (centerMap) {
    centerMapAroundPlace(map, place);
  }
  searchedPlaceMarker.setPosition(place.geometry.location);
  searchedPlaceMarker.setVisible(true);
}

/** Centers the map around {@param place}. */
function centerMapAroundPlace(map, place) {
  if (place.geometry.viewport) {
    map.fitBounds(place.geometry.viewport);
  } else {
    map.setCenter(place.geometry.location);
    map.setZoom(placeZoom);
  }
}