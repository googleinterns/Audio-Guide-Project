var iconBase = 'https://maps.google.com/mapfiles/kml/';
var PlaceType = {
    PUBLIC: {
        icon: iconBase + 'paddle/orange-blank.png'
    },
    PRIVATE: {
        icon: iconBase + "paddle/ylw-blank.png",
    },
    SEARCH_RESULT: {
        icon: iconBase + 'paddle/blu-blank.png'
    },
    SAVED_LOCATION: {
        icon: iconBase + 'paddle/grn-blank.png'
    }
};

class PlaceGuide {
    constructor(id, name, description, audioKey, audioLength, imgKey, positionLat, positionLng, placeId, creatorId, creatorName, placeType) {
        this._id = id;
        this._name = name;
        this._description = description;
        this._audioKey = audioKey;
        this._imgKey = imgKey;
        this._position = new google.maps.LatLng(positionLat, positionLng);
        this._placeId = placeId;
        this._creatorId = creatorId;
        this._creatorName = creatorName;
        this._placeType = placeType;
        this.setupRepresentationOnMap();
    }

    setupRepresentationOnMap() {
        this.setupMarker();
        this.setupInfoWindow();
        this._marker.addListener('click', () => {
            this._infoWindow.open(map, this._marker);
        });
    }

    setupMarker() {
        this._marker = new google.maps.Marker( {
            position: this._position, 
            title: this._name, 
            icon: { 
                url: this._placeType.icon,
            }
        });
    }

    setupInfoWindow() {
        this._infoWindow = new google.maps.InfoWindow({
            content: this.getInfoWindowContent(),
        });
    }

    getInfoWindowContent() {
        var content = "<h3>" + this._name + "</h3>" +
        "<h4> Created by: " + this._creatorName + "</h4>" + 
        "<p>" + this._description + "</p>";
        return content;
    }

    addMarkerToMap(map) {
        this._marker.setMap(map);
    }

    removeMarkerFromMap() {
        this._marker.setMap(null);
    }

    get position() {
        return this._position;
    }

    set position(pos) {
        this._position = pos;
        this._marker.setPosition(this._position);
    }
}