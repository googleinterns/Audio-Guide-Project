const placeZoom = 14;

// Get icons from the charts API
function getColoredMarkerIcon(color) {
    var iconBase = 'https://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|';
    var markerIcon = {
        url: iconBase + color,
        scaledSize: new google.maps.Size(30, 46), // scaled size
        origin: new google.maps.Point(0,0), // origin
        anchor: new google.maps.Point(15, 45) // anchor
    }
    return markerIcon;
}

var PlaceType = {
    PUBLIC: {
        // Orange icon
        icon: null,
        iconColor: "de8a0b", 
    },
    PRIVATE: {
        // Yellow icon
        icon: null,
        iconColor: "f7ff05",
    },
    SEARCH_RESULT: {
        // Green icon
        icon: null,
        iconColor: "82d613",
    },
    SAVED_LOCATION: {
        // Blue icon
        icon: null,
        iconColor: "1d2480",
    },
    CURRENT_LOCATION: {
        icon: "./img/blue_dot.png",
    }
};

class Place {
    // Either positionLat, positionLng and name, or mapsPlace will be specified.
    // The others will be null.
    // placeType is a value from PlaceType
    constructor(positionLat, positionLng, name, mapsPlace, placeType, hasInfoWindow) {
        this._mapsPlace = mapsPlace;
        if (this._mapsPlace != null) {
            this._position = this._mapsPlace.geometry.location;
            this.name = this._mapsPlace.name;
        } else {
            this._position = new google.maps.LatLng(positionLat, positionLng);
            this._name = name;
        }
        this._placeType = placeType;
        this._hasInfoWindow = hasInfoWindow;
        this.setupRepresentationOnMap();
    }

    setupRepresentationOnMap() {
        this.setupMarker();
        if(this._hasInfoWindow) {
            this.setupInfoWindow();
            this._marker.addListener('click', () => {
                if (this._infoWindowClosed) {
                    this._infoWindow.open(map, this._marker);
                }
                else {
                    this._infoWindow.close();
                }
                this._infoWindowClosed = !this._infoWindowClosed;
            });
        }
    }

    setupMarker() {
        var markerIcon;
        if (this._placeType.icon != null) {
            markerIcon = this._placeType.icon;
        } else {
            markerIcon = getColoredMarkerIcon(this._placeType.iconColor);
        }
        this._marker = new google.maps.Marker( {
            position: this._position, 
            title: this._name, 
            icon: markerIcon,
        });
    }

    setupInfoWindow() {
        this._infoWindowClosed = true;
        this._infoWindow = new google.maps.InfoWindow({
            content: this.getInfoWindowContent(),
            maxWidth: 200, 
        });
    }

    getInfoWindowContent() {
        var content = "<h3>" + this._name + "</h3>";
        return content;
    }

    set visible(visibility) {
        this._marker.setVisible(visibility);
    }

    set draggable(draggability) {
        this._marker.setDraggable(draggability);
        this._marker.addListener('drag', function() {
            this.position = this.getPosition();
        });
    }

    set map(newMap) {
        this._marker.setMap(newMap);
    }

    removeMarkerFromMap() {
        this._marker.setMap(null);
    }

    get position() {
        return this._position;
    }

    set position(pos) {
        if (this._mapsPlace != null ) {
            this.detachFromPlace();
        } 
        this._position = pos;
        this._marker.setPosition(this._position);
    }

    get place() {
        return this._mapsPlace;
    }

    set place(newPlace) {
        this._mapsPlace = newPlace;
        this._position = this._mapsPlace.geometry.location;
        this._name = this._mapsPlace.name;
        this._marker.setPosition(this._position);
    }

    detachFromPlace() {
        this._mapsPlace = null;
    }

    centerMapAround(map) {
        if (this._mapsPlace != null && this._mapsPlace.geometry.viewport) {
            map.fitBounds(this._mapsPlace.geometry.viewport);
        } else {
            map.setCenter(this._position);
            map.setZoom(placeZoom);
        }
    }

    /**
    * This function relies on Geocoder API to get the place
    * corresponding to an id, provided by the Places API,
    * and then display it as the new search result.
    */
    updatePlaceAndCenterBasedOnId(map, id) {
        const geocoder = new google.maps.Geocoder();
        geocoder.geocode({placeId: id}, (results, status) => {
            if (status !== "OK") {
            window.alert(GEOCODER_FAIL_MSG + status);
            return;
            }
            this.updatePlaceAndCenter(map, results[0]);
        });
    }

    updatePlaceAndCenter(map, newPlace) {
        this.place = newPlace;
        this.centerMapAround(map);
    }
}

class PlaceGuide extends Place {
    constructor(databaseId, name, description, audioKey, audioLength, imgKey, positionLat, positionLng, placeId, creatorId, creatorName, placeType) {
        super(positionLat, positionLng, name, null, placeType, true);
        this._databaseId = databaseId;
        this._name = name;
        this._description = description;
        this._audioKey = audioKey;
        this._imgKey = imgKey;
        this._creatorId = creatorId;
        this._creatorName = creatorName;
        this.setupRepresentationOnMap();
    }

    getInfoWindowContent() {
        var content = "<h3>" + this._name + "</h3>" +
        "<h4> Created by: " + this._creatorName + "</h4>" + 
        "<p>" + this._description + "</p>";
        return content;
    }
}