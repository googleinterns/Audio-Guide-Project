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
    this.updateMarkerClusters();
  }

  updateMarkerClusters() {
    // if (this._markerClusterer != undefined) {
    //   this._markerClusterer.clearMarkers();
    // }
    // const markers = [];
    // for (const placeGuideId in this._placeGuidesOnMap) {
    //   if (this._placeGuidesOnMap.hasOwnProperty(placeGuideId)) {
    //     markers.push(this._placeGuidesOnMap[placeGuideId].marker);
    //   }
    // }
    // this._markerClusterer = new MarkerClusterer(map, markers,
    //     {imagePath: './img/m'});
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
          this._placeGuidesOnMap[placeGuideId] =
              this.constructPlaceGuideOnMapFromPlaceGuide(
                  placeGuides[placeGuideId]);
          this._markerClusterer
            .add(this._placeGuidesOnMap[placeGuideId].marker);
        }
      }
    }
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
    this._markerClusterer.removeMarker(this._placeGuidesOnMap[placeGuideId].marker)
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
