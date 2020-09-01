/**
 * This class is responsible for representing the placeGuides on the map.
 */
class MapPlaceGuideDisplayer {
  constructor() {
    this._placeGuidesOnMap = {};
    this._markerClusterer = new MarkerClusterer(map, [],
        {imagePath: './img/m'});
  }

  update(placeGuides) {
    this.removePreviousPlaceGuidesFromMap(placeGuides);
    this.addNewPlaceGuidesToMap(placeGuides);
  }

  remove(placeGuideId) {
    this._placeGuidesOnMap[placeGuideId].remove();
    delete this._placeGuidesOnMap[placeGuideId];
  }

  highlight(placeGuideId) {
    this._placeGuidesOnMap[placeGuideId].highlight();
  }

  unhighlight(placeGuideId) {
    this._placeGuidesOnMap[placeGuideId].unhighlight();
  }

  // This function sets the map bound to the minimum one
  // which contains all the PlaceGuides.
  adjustMapToShowAll() {
    let mapBounds = new google.maps.LatLngBounds();
    let guidesExist = false;
    for (const placeGuideId in this._placeGuidesOnMap) {
      if (this._placeGuidesOnMap.hasOwnProperty(placeGuideId)) {
        const position =
          this._placeGuidesOnMap[placeGuideId].marker.getPosition();
        mapBounds.extend(position);
        guidesExist = true;
      }
    }
    if (guidesExist) {
      // Unless the zoom is increased manually, the fitBounds()
      // method is not guaranteed to set the highest possible
      // zoom, which is expected in our case.
      map.setZoom(MAX_ZOOM);
      map.setCenter(mapBounds.getCenter());
      map.fitBounds(mapBounds);
    }
  }

  removePreviousPlaceGuidesFromMap(placeGuides) {
    for (const placeGuideId in this._placeGuidesOnMap) {
      if (this._placeGuidesOnMap.hasOwnProperty(placeGuideId)) {
        if (!placeGuides.hasOwnProperty(placeGuideId)) {
          // This placeGuide is not needed anymore.
          this.remove(placeGuideId);
        }
      }
    }
  }

  addNewPlaceGuidesToMap(placeGuides) {
    for (const placeGuideId in placeGuides) {
      if (placeGuides.hasOwnProperty(placeGuideId)) {
        if (!this._placeGuidesOnMap.hasOwnProperty(placeGuideId)) {
          // new placeGuide should be constructed.
          this.addNewMarker(placeGuideId, placeGuides[placeGuideId]);
        }
      }
    }
  }

  addNewMarker(placeGuideId, placeGuide) {
    this._placeGuidesOnMap[placeGuideId] =
      this.constructPlaceGuideOnMapFromPlaceGuide(
          placeGuide);
    this._markerClusterer
        .addMarker(this._placeGuidesOnMap[placeGuideId].marker);
  }

  constructPlaceGuideOnMapFromPlaceGuide(placeGuide) {
    let placeType;
    if (placeGuide.isPublic) {
      placeType = PlaceType.PUBLIC;
    } else {
      placeType = PlaceType.PRIVATE;
    }
    return new PlaceGuideOnMap(placeGuide.id,
        placeGuide.name,
        placeGuide.location.position,
        placeGuide.location.mapsPlace,
        placeGuide.creator,
        placeGuide.description,
        placeType);
  }

  remove(placeGuideId) {
    this._markerClusterer.removeMarker(
        this._placeGuidesOnMap[placeGuideId].marker);
    this._placeGuidesOnMap[placeGuideId].remove();
    delete this._placeGuidesOnMap[placeGuideId];
  }

  highlight(placeGuideId) {
    this._placeGuidesOnMap[placeGuideId].highlight();
  }

  unhighlight(placeGuideId) {
    this._placeGuidesOnMap[placeGuideId].unhighlight();
  }
}
