class PlaceGuideOnMap {
    constructor(map, id, name, position, place, creator, description, placeType) {
        this.marker = setupMarker(map, placeType, name, position);
        this._infoWindowClosed = true;
        this._infoWindow = getInfoWindow(name, position, place, creator, description);
    }

    static getMarker(map, placeType, name, position) {
        var markerIcon;
        if (placeType.icon != null) {
            markerIcon = this._placeType.icon;
        } else {
            markerIcon = getColoredMarkerIcon(this._placeType.iconColor);
        }
        var marker = new google.maps.Marker({
            position: position,
            title: name,
            icon: markerIcon,
            map: map,
        });
        return marker;
    }

    static getInfoWindow(name, position, place, creator, description) {
        return new google.maps.InfoWindow({
            content: this.getInfoWindowContent(name, position, place, creator, description),
            maxWidth: 200,
        });
    }

    static getInfoWindowContent(name, position, place, creator, description) {
        var placeName;
        if (place != null) {
            placeName = place.name;
        } else {
            placeName = position.toString();
        }
        var content = "<h3>" + name + "</h3>" +
            "<h4> Created by: " + creator.name + "</h4>" +
            "<h4> Place: " + placeName + "</h4>" +
            "<p>" + description + "</p>";
        return content;
    }

    remove() {
        this.marker.setMap(null);
    }
}