class Location {
    constructor(position, mapsPlace) {
        this._mapsPlace = mapsPlace;
        if (this._mapsPlace != null) {
            this._position = this._mapsPlace.geometry.location;
            console.log("construct location with mapsPlace: ");
            console.log(this._mapsPlace);
            console.log("position is: ");
            console.log(this._position);
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
            fields: ['address_components', 'name', 'geometry']
        };
        return new Promise(function (resolve, reject) {
            var service = new google.maps.places.PlacesService(map);
            console.log("PlacesService and map ");
            console.log(service);
            console.log(map);
            service.getDetails(request, (place, status) => {
                if (status === google.maps.places.PlacesServiceStatus.OK) {
                    console.log("PlacesService for " + placeId + " finished with OK");
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