/**
 * This module's {@code initAutocomplete} sets up a search-box with auto-suggestions feature,
 * so that the user can search for a specific place.
 *
 * When a search query is submitted, @param searchResult is updated with the new place.
 *
 * By default, the Autocomplete widget provided by Places API
 * supports only submitting queries for one of the suggetsions.
 * This makes the searching process quite uncomfortable, since the user can't press enter
 * if they didn't write the exact name of the place, they had a typo, etc.
 * To avoid this issue, the autocomplete widget's eventlistener was extended
 * so that if the user didn't choose one of the suggestions explicitly,
 * then the first place suggestion is chosen programatically when the query is submitted.
 */

class SearchBox {
  static GEOCODER_FAIL_MSG = "Geocoder failed due to: ";

  constructor(map, searchResult, elementId) {
    this._map = map;
    this._searchResult = searchResult;
    this._input = document.getElementById(elementId);
    this._autocomplete = new google.maps.places.Autocomplete(this._input);
    this._map.controls[google.maps.ControlPosition.TOP_CENTER].push(this._input);
  }

  // The Searchbox starts listening to and handling 
  // search-related events when init() is called. 
  init() {
    this._autocomplete.setFields(['address_components', 'geometry',
                                  'name', 'place_id']);
    this._autocomplete.addListener('place_changed',
                                  result => this.setNewSearchResult());
  }

  /**
   * Gets the place searched by the user if possible.
   * If the user didn't choose an existing place, display
   * the best suggestion instead.
   */
  setNewSearchResult() {
    var place = this._autocomplete.getPlace();
    if (!place.geometry) {
      // If the user didn't choose a suggestion, but pressed enter,
      // then "place" may have no geometry.
      // In this case, search for the "closest" place.
      this.setClosestResult();
      return;
    }
    this._searchResult.place = place;
    this._searchResult.centerMapAround();
  }

  /**
   * This function gets the best result(prediction) for the submitted search query
   * and updates searchresult.
   */
  setClosestResult() {
    var service = new google.maps.places.AutocompleteService();
    var searchResult = this._searchResult;
    var map = this._map;
    service.getPlacePredictions({input: this._input.value},
        function (predictions, status) {
      if (status != google.maps.places.PlacesServiceStatus.OK) {
        Modal.show(status, 3000);
        return;
      }
      searchResult.updatePlaceAndCenterBasedOnId(predictions[0].place_id);
    });
  }
}
