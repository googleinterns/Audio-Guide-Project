/**
 * This class is responsible of the representation of one single
 * placeGuide on map.
 */
class PlaceGuideOnMap {
  constructor(id, name, location, creator, description, placeType) {
    this._guideName = name;
    this._creator = creator;
    this._description = description;
    this._id = id;
    this._location = location;
    this._marker = PlaceGuideOnMap
        .getMarker(placeType, name, location.position);
    this._infoWindow = undefined;
    this._highlighted = false;
    this._highlightingStopped = false;
    this.setupHighlightOnMarkerClick();
    this.setupUnhighlightOnMapClick();
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
      markerIcon = getColoredMarkerIcon(placeType.iconColor, placeType.size);
    }
    return markerIcon;
  }

  static getInfoWindow(name, position, placeName, creator, description) {
    return new google.maps.InfoWindow({
      content: PlaceGuideOnMap
          .getInfoWindowContent(
              name, position, placeName, creator, description),
      maxWidth: 200,
    });
  }

  static getInfoWindowContent(name, position, placeName, creator, description) {
    let toDisplayPlaceName;
    if (placeName !== undefined) {
      toDisplayPlaceName = placeName;
    } else {
      toDisplayPlaceName = position.toString();
    }
    let creatorName = creator.name;
    if (creatorName == undefined) {
      creatorName = creator.email;
    }
    let content = `<h3>${name}</h3>
      <h4> Created by: ${creatorName}</h4>
      <h4> Place: ${toDisplayPlaceName}</h4>`;
    if (description != undefined) {
      content += `<p>${description}</p>`;
    }
    return content;
  }

  isHighlighted() {
    return this._highlighted;
  }

  highlight() {
    this._highlightingStopped = false;
    if (this._infoWindow !== undefined) {
      this._infoWindow.open(map, this._marker);
      this._highlighted = true;
    } else {
      this._location.getPlaceName()
          .then((placeName) => {
            this._infoWindow = PlaceGuideOnMap
                .getInfoWindow(this._guideName,
                    this._location.position,
                    placeName,
                    this._creator,
                    this._description);
            if (!this._highlightingStopped) {
              this._infoWindow.open(map, this._marker);
              this._highlighted = true;
            }
          });
    }
  }

  unhighlight() {
    if (this._infoWindow !== undefined) {
      this._highlightingStopped = true;
      this._infoWindow.close();
    }
    this._highlighted = false;
  }

  closeInfoWindow() {
    this._infoWindow.close();
  }

  openInfoWindow() {
    if (this._infoWindow !== undefined) {
      this._infoWindow.open(map, this._marker);
    } else {
      this._location.getPlaceName
          .then((placeName) => {
            this._infoWindow = PlaceGuideOnMap
                .getInfoWindow(this._guideName,
                               this._location.position,
                               placeName,
                               this._creator,
                               this._description);
            this._infoWindow.open(map, this._marker);
          });
    }
  }

  remove() {
    this._marker.setMap(null);
  }

  setupHighlightOnMarkerClick() {
    const thisPlaceGuideOnMap = this;
    this._marker.addListener('click', () => {
      if (thisPlaceGuideOnMap.isHighlighted()) {
        placeGuideManager.unhighlightPlaceGuide();
      } else {
        placeGuideManager.highlightPlaceGuide(thisPlaceGuideOnMap.id);
      }
    });
  }

  setupUnhighlightOnMapClick() {
    const thisPlaceGuideOnMap = this;
    map.addListener('click', function (mapsMouseEvent) {
      if (thisPlaceGuideOnMap.isHighlighted()) {
        placeGuideManager.unhighlightPlaceGuide();
      }
    });
  }
}