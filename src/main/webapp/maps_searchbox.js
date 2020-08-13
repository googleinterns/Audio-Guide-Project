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

class SearchBox {
    static GEOCODER_FAIL_MSG = "Geocoder failed due to: ";

    constructor(map, searchResult, elementId) {
        this._map = map;
        this._searchResult = searchResult;
        this._input = document.getElementById(elementId);
        this._autocomplete = new google.maps.places.Autocomplete(this._input);
        this._map.controls[google.maps.ControlPosition.TOP_CENTER].push(this._input);
    }

    init() {
        this._autocomplete.setFields(['address_components', 'geometry', 'name']);
        this._autocomplete.addListener('place_changed', result => this.setNewSearchResult());
    }

    /**
    * Gets the place searched by the user if possible,
    * centers the map around it and adds a marker for it.
    * If the user didn't choose an existing place, display
    * the best suggestion instead. 
    */
    setNewSearchResult() {
        var place = this._autocomplete.getPlace();
        if (!place.geometry) {
            // If the user didn't choose a suggestion, but pressed enter,
            // then "place" may have no geometry.
            // In this case, search for the "closest" place with geometry and show that.
            this.setClosestResult();
            return;
        }
        this._searchResult.updatePlaceAndCenter(this._map, place);
    }

    /**
    * This function gets the best result(prediction) for the submitted search query
    * and displays it as the new search result.
    */
    setClosestResult() {
        var service = new google.maps.places.AutocompleteService();
        var searchResult = this._searchResult;
        var map = this._map;
        service.getPlacePredictions({input: this._input.value}, function (predictions, status) {
            if (status != google.maps.places.PlacesServiceStatus.OK) {
                alert(status);
                return;
            }
            searchResult.updatePlaceAndCenterBasedOnId(map, predictions[0].place_id);
        });
    }
}
