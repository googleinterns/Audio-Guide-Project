class PlaceGuideOnMap {
    constructor(map, id, name, position, place, creator, description, placeType) {
        this._map = map;
        this._id;
        this._infoWindowClosed = true;
        this._infoWindow = PlaceGuideOnMap.getInfoWindow(name, position, place, creator, description);
        this._marker = PlaceGuideOnMap.getMarker(map, placeType, name, position);
        this.closeInfoWindowOnMapClick();
        this.toggleInfoWindowOnMarkerClick();
        this.highlightPlaceGuideOnMarkerDoubleClick();
        this.stopAnimationOnMarkerClick();
        this.stopAnimationOnMapClick();
    }

    highlight() {
        this._marker.setAnimation(google.maps.Animation.BOUNCE);
    }

    highlightPlaceGuideOnMarkerDoubleClick() {
        var thisPlaceGuideOnMap = this;
        this._marker.addListener("dblclick", placeGuideManager.highlightPlaceGuide(thisPlaceGuideOnMap._id));
    }

    toggleInfoWindowOnMarkerClick() {
        var thisPlaceGuideOnMap = this;
        this._marker.addListener('click', () => {
            if (thisPlaceGuideOnMap._infoWindowClosed) {
                thisPlaceGuideOnMap.openInfoWindow();
            } else {
                thisPlaceGuideOnMap.closeInfoWindow();
            }
        });
    }

    stopAnimationOnMarkerClick() {
        var thisPlaceGuideOnMap = this;
        this._marker.addListener('click', () => {
            thisPlaceGuideOnMap._marker.setAnimation(null);
        });
    }

    closeInfoWindowOnMapClick() {
        var thisPlaceGuideOnMap = this;
        this._map.addListener('click', function (mapsMouseEvent) {
            if (!thisPlaceGuideOnMap._infoWindowClosed) {
                thisPlaceGuideOnMap.closeInfoWindow();
            }
        });
    }

    stopAnimationOnMapClick() {
        var thisPlaceGuideOnMap = this;
        this._map.addListener('click', function (mapsMouseEvent) {
            thisPlaceGuideOnMap._marker.setAnimation(null);
        });
    }

    static getMarker(map, placeType, name, position) {
        var markerIcon = PlaceGuideOnMap.getMarkerIcon();
        var marker = new google.maps.Marker({
            position: position,
            title: name,
            icon: markerIcon,
            map: map,
        });
        return marker;
    }

    static getMarkerIcon(placeType) {
        var markerIcon;
        if (placeType.icon != null) {
            markerIcon = this._placeType.icon;
        } else {
            markerIcon = getColoredMarkerIcon(this._placeType.iconColor);
        }
        return markerIcon;
    }

    static getInfoWindow(name, position, place, creator, description) {
        return new google.maps.InfoWindow({
            content: PlaceGuideOnMap.getInfoWindowContent(name, position, place, creator, description),
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

    closeInfoWindow() {
        this._infoWindow.close();
        this._infoWindowClosed = true;
    }

    openInfoWindow() {
        this._infoWindow.open(this._map, this._marker);
        this._infoWindowClosed = false;
    }

    remove() {
        this.marker.setMap(null);
    }
}