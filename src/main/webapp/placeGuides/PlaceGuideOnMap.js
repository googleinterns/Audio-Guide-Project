/**
 * This class is responsible of the representation of one single
 * placeGuide on map.
 */
class PlaceGuideOnMap {
  constructor(id, name, position, place, creator, description, placeType) {
    this._id = id;
    this._infoWindowClosed = true;
    this._infoWindow = PlaceGuideOnMap
        .getInfoWindow(name, position, place, creator, description);
    this._marker = PlaceGuideOnMap.getMarker(placeType, name, position);
    this._highlighted = false;
    this.toggleHighlightOnMarkerClick();
    this.unhighlightOnMapClick();
  }

  get marker() {
    return this._marker;
  }

  get id() {
    return this._id;
  }

  static getMarker(placeType, name, position) {
    const markerIcon = PlaceGuideOnMap.getMarkerIcon(placeType);
    const marker = new google.maps.Marker({
      position: position,
      title: name,
      icon: markerIcon,
      map: map,
    });
    return marker;
  }

  static getMarkerIcon(placeType) {
    let markerIcon;
    if (placeType.icon != null) {
      markerIcon = this._placeType.icon;
    } else {
      markerIcon = getColoredMarkerIcon(placeType.iconColor);
    }
    return markerIcon;
  }

  static getInfoWindow(name, position, place, creator, description) {
    return new google.maps.InfoWindow({
      content: PlaceGuideOnMap
          .getInfoWindowContent(name, position, place, creator, description),
      maxWidth: 200,
    });
  }

  static getInfoWindowContent(name, position, place, creator, description) {
    let placeName;
    if (place != null) {
      placeName = place.name;
    } else {
      placeName = position.toString();
    }
    let creatorName = creator.name;
    if (creatorName == undefined) {
      creatorName = creator.email;
    }
    const content = '<h3>' + name + '</h3>' +
        '<h4> Created by: ' + creatorName + '</h4>' +
        '<h4> Place: ' + placeName + '</h4>' +
        '<p>' + description + '</p>';
    return content;
  }

  isHighlighted() {
    return this._highlighted;
  }

  highlight() {
    this._highlighted = true;
    this.openInfoWindow();
  }

  unhighlight() {
    this._highlighted = false;
    this.closeInfoWindow();
  }

  closeInfoWindow() {
    this._infoWindow.close();
    this._infoWindowClosed = true;
  }

  openInfoWindow() {
    this._infoWindow.open(map, this._marker);
    this._infoWindowClosed = false;
  }

  remove() {
    this._marker.setMap(null);
  }

  toggleHighlightOnMarkerClick() {
    const thisPlaceGuideOnMap = this;
    this._marker.addListener('click', () => {
      if (thisPlaceGuideOnMap.isHighlighted()) {
        placeGuideManager.unhighlightPlaceGuide(thisPlaceGuideOnMap.id);
      } else {
        placeGuideManager.highlightPlaceGuide(thisPlaceGuideOnMap.id);
      }
    });
  }

  unhighlightOnMapClick() {
    const thisPlaceGuideOnMap = this;
    map.addListener('click', function(mapsMouseEvent) {
      if (thisPlaceGuideOnMap.isHighlighted()) {
        placeGuideManager.unhighlightPlaceGuide();
      }
    });
  }
}
