class PlaceGuideOnMap {
    constructor(map, id, name, position, place, creator, description, placeType) {
        this.marker = setupMarker(map, placeType, name, position);
    }

    static setupMarker(map, placeType, name, position) {
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

    remove() {
        this.marker.setMap(null);
    }
}