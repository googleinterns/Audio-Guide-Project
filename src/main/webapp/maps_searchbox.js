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

function initAutocomplete(map, searchResult) {
  const input = document.getElementById("search-box");
  var autocomplete = new google.maps.places.Autocomplete(input);
  map.controls[google.maps.ControlPosition.TOP_CENTER].push(input);

  autocomplete.setFields(['address_components', 'geometry', 'name']);
  autocomplete.addListener('place_changed',
      result => setNewSearchResult(map, autocomplete, input, searchResult));
}

/**
 * Gets the place searched by the user if possible,
 * centers the map around it and adds a marker for it.
 * If the user didn't choose an existing place, display
 * the best suggestion instead. 
 */
function setNewSearchResult(map, autocomplete, input, searchResult) {
  var place = autocomplete.getPlace();
  if (!place.geometry) {
    // If the user didn't choose a suggestion, but pressed enter,
    // then "place" may have no geometry.
    // In this case, search for the "closest" place with geometry and show that.
    setClosestResult(map, input, autocomplete, searchResult);
    return;
  }
  searchResult.updatePlaceAndCenter(map, place);
}

/**
 * This function gets the best result(prediction) for the submitted search query
 * and displays it as the new search result.
 */
function setClosestResult(map, input, autocomplete, searchResult) {
  var service = new google.maps.places.AutocompleteService();
  service.getPlacePredictions({input: input.value}, function (predictions, status) {
    if (status != google.maps.places.PlacesServiceStatus.OK) {
      alert(status);
      return;
    }
    searchResult.updatePlaceAndCenterBasedOnId(map, predictions[0].place_id);
  });
}