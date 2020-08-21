class Map {
    constructor() {
        var myMapOptions = {
            zoom: 2,
            center: new google.maps.LatLng(0, 0),
            mapTypeId: 'roadmap',
            restriction: {
            // Set bounds for map so that the user can't pan out. 
            // Use the maximum possible latitude and logitude coordinates on Earth for bounding, 
            // based on the Mercato-projection.
            latLngBounds: {north: 85, south: -85, west: -180, east: 180},
            strictBounds: true
            }
        };
        this._map = new google.maps.Map(
            document.getElementById('map'), myMapOptions);
    }

    get map() {
        return this._map;
    }
}