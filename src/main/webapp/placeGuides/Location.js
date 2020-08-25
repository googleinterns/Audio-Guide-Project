/** 
 * This class holds a location's data, which can be specified by 
 * coordinates or nu a place_id, which will be decoded with PlacesService.
 */
class Location {
    constructor(position, mapsPlace) {
        this._mapsPlace = mapsPlace;
        if (this._mapsPlace != null) {
            this._position = this._mapsPlace.geometry.location;
        } else {
            this._position = position;
        }
    }

    static constructLocationBasedOnCoordinates(positionLat, positionLng) {
        return new Location(new google.maps.LatLng(positionLat, positionLng), null);
    }

    // TODO set map to global or pas it as parameter
    static constructLocationBasedOnPlaceId(placeId) {
        var request = {
            placeId: placeId,
            fields: ['address_components', 'name', 'geometry', 'place_id']
        };
        return new Promise(function (resolve, reject) {
            var service = new google.maps.places.PlacesService(map);
            service.getDetails(request, (place, status) => {
                if (status === google.maps.places.PlacesServiceStatus.OK) {
                    resolve(new Location(null, place));
                } else {
                    reject(new Error('Couldn\'t find the place' + placeId));
                }
            });
        });
    }

    get position() {
        return this._position;
    }

    get mapsPlace() {
        return this._mapsPlace;
    }
}