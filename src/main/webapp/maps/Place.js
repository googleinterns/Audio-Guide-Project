const PLACE_ZOOM = 14;
const MAX_ZOOM = 19;
const CHOSEN_LOCATION_CHANGE_EVENT = 'chosenPositionChange';

/**
 * This class holds a place's data and handles its representation on the map.
 * The place is represented by a marker corresponding to placeType,
 * and it may also have an infoWindow, containing the name.
 * The place may be defined by specifying
 * its position(@param positionLat, @param positionLng),
 * using @code constructPlaceBasedOnCoordinates factory method,
 * or by specifying a place ID from the Google Place database(@param mapsPlace),
 * using @code constructPlaceBasedOnPlaceId factory method.
 * Remark that @code constructPlaceBasedOnPlaceId returns a promise!
 * Remark that mapsPlace has the higher priority(because it contains more information).
 * Whenever a new position is set, the mapsPlace is discarded, and vice versa.
 */
class Place {
  constructor(map, positionLat, positionLng, name,
      mapsPlace, placeType, hasInfoWindow) {
    this._map = map;
    this._mapsPlace = mapsPlace;
    this._name = name;
    if (this._mapsPlace != null) {
      this._position = this._mapsPlace.geometry.location;
    } else {
      this._position = new google.maps.LatLng(positionLat, positionLng);
    }
    this._placeType = placeType;
    this._hasInfoWindow = hasInfoWindow;
    this._positionChangeEvent = new Event(CHOSEN_LOCATION_CHANGE_EVENT);
    this.setupRepresentationOnMap();
  }

  // Some objects' behaviour is conditioned
  // by the chosenLocation's positionChanges.
  // To enable listening to this event, this flag must be set to true.
  // Other Places won't trigger the event.
  set triggerChosenLocationChangeEvent(trigger) {
    this._triggerChosenLocationChangeEvent = trigger;
  }

  set visible(visibility) {
    this._marker.setVisible(visibility);
  }

  set draggable(draggability) {
    this._marker.setDraggable(draggability);
    const thisPlace = this;
    this._marker.addListener('drag', function() {
      thisPlace.position = this.getPosition();
    });
  }

  get position() {
    return this._position;
  }

  set position(pos) {
    // Overwrite mapsPlace when position changes.
    if (this._mapsPlace != null) {
      this.detachFromPlace();
    }
    this._position = pos;
    this._marker.setPosition(this._position);
    this.onPositionChange();
  }

  get place() {
    return this._mapsPlace;
  }

  set place(newPlace) {
    this._mapsPlace = newPlace;
    // Overwrite position when mapsPlace changes.
    // use the position of the new mapsPlace.
    this._position = this._mapsPlace.geometry.location;
    this._marker.setPosition(this._position);
    this.onPositionChange();
  }

  static constructPlaceBasedOnCoordinates(map, positionLat, positionLng,
      name, placeType, hasInfoWindow) {
    return new Place(map, positionLat, positionLng, name,
        null, placeType, hasInfoWindow);
  }

  static constructPlaceBasedOnPlaceId(map, placeId, name,
      placeType, hasInfoWindow) {
    const request = {
      placeId: placeId,
      fields: ['address_components', 'name', 'geometry', 'place_id'],
    };
    return new Promise(function(resolve, reject) {
      const service = new google.maps.places.PlacesService(map);
      service.getDetails(request, (place, status) => {
        if (status === google.maps.places.PlacesServiceStatus.OK) {
          resolve(new Place(map, 0, 0,
              name, place, placeType, hasInfoWindow));
        } else {
          reject(new Error('Couldnt\'t find the place' + placeId));
        }
      });
    });
  }

  setupRepresentationOnMap() {
    this.setupMarker();
    if (this._hasInfoWindow) {
      this.setupInfoWindow();
      // The infowindow can be opened and closed by clicking the marker.
      this._marker.addListener('click', () => {
        if (this._infoWindowClosed) {
          this.openInfoWindow();
        } else {
          this.closeInfoWindow();
        }
      });
      // Close the open infowindow if the user clicks anywhere else on the map.
      const thisPlace = this;
      this._map.addListener('click', function(mapsMouseEvent) {
        if (!thisPlace._infoWindowClosed) {
          thisPlace.closeInfoWindow();
        }
      });
    }
  }

  closeInfoWindow() {
    this._infoWindow.close();
    this._infoWindowClosed = true;
  }

  openInfoWindow() {
    this._infoWindow.open(this._map, this._marker);
    this._infoWindowClosed = false;
  }

  setupMarker() {
    let markerIcon;
    if (this._placeType.icon != null) {
      markerIcon = this._placeType.icon;
    } else {
      markerIcon = getColoredMarkerIcon(this._placeType.iconColor);
    }
    this._marker = new google.maps.Marker({
      position: this._position,
      title: this._name,
      icon: markerIcon,
      map: this._map,
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
    const content = '<h3>' + this._name + '</h3>';
    return content;
  }

  detachFromPlace() {
    this._mapsPlace = null;
  }

  centerMapAround() {
    if (this._mapsPlace != null && this._mapsPlace.geometry.viewport) {
      this._map.setZoom(MAX_ZOOM);
      this._map.fitBounds(this._mapsPlace.geometry.viewport);
    } else {
      this._map.setZoom(PLACE_ZOOM);
      this._map.setCenter(this._position);
    }
  }

  /**
   * This function relies on Places API to get the place
   * corresponding to an id, then update the mapsPlace and
   * center the map around it.
   */
  updatePlaceAndCenterBasedOnId(id) {
    const request = {
      placeId: id,
      fields: ['address_components', 'name', 'geometry', 'place_id'],
    };
    const service = new google.maps.places.PlacesService(this._map);
    service.getDetails(request, (place, status) => {
      if (status === google.maps.places.PlacesServiceStatus.OK) {
        this.place = place;
        this.centerMapAround();
      } else {
        window.alert(Place.GEOCODER_FAIL_MSG + status);
      }
    });
  }

  onPositionChange() {
    if (this._triggerChosenLocationChangeEvent) {
      document.getElementById('map')
          .dispatchEvent(this._positionChangeEvent);
    }
  }
}

// Specify different icons/colors for dynamically
// generated icons for each place type.
var PlaceType = {
  PUBLIC: {
    // Orange icon
    icon: null,
    iconColor: 'de8a0b',
  },
  PUBLIC_OWN: {
    // Lighter orange icon
    icon: null,
    iconColor: 'f5cb42'
  },
  PRIVATE: {
    // Yellow icon
    icon: null,
    iconColor: 'f7ff05',
  },
  SEARCH_RESULT: {
    // Green icon
    icon: null,
    iconColor: '82d613',
  },
  SAVED_LOCATION: {
    // Blue icon
    icon: null,
    iconColor: '1d2480',
  },
  CURRENT_LOCATION: {
    icon: './img/blue_dot.png',
  },
};

// Get icons from the charts API.
function getColoredMarkerIcon(color) {
  const iconBase =
      'https://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|';
  const markerIcon = {
    url: iconBase + color,
    scaledSize: new google.maps.Size(30, 46),
    origin: new google.maps.Point(0, 0),
    anchor: new google.maps.Point(15, 45),
  };
  return markerIcon;
}
