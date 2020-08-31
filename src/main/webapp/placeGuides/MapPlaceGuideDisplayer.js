/**
 * This class is responsible for representing the placeGuides on the map.
 */
class MapPlaceGuideDisplayer {
  constructor() {
    this._placeGuidesOnMap = {};
  }

  update(placeGuides) {
    this.removePreviousPlaceGuidesFromMap(placeGuides);
    this.addNewPlaceGuidesToMap(placeGuides);
    this.updateMarkerClusters();
  }

  adjustMapToShowAll() {
    let minLat = 90;
    let maxLat = -90;
    let maxLngDifference = 0;
    let maxLngDifferenceWestCorner = 0;
    let nextMarkerLng;
    let placeGuidesOnMap = Object.values(this._placeGuidesOnMap);
    placeGuidesOnMap.sort(MapPlaceGuideDisplayer.comparePlaceGuidesOnMap);
    if (placeGuidesOnMap.length > 0) {
        for (let i = 0; i < placeGuidesOnMap.length; i++) {
            var position = placeGuidesOnMap[i].marker.getPosition();
            minLat = Math.min(position.lat(), minLat);
            maxLat = Math.max(position.lat(), maxLat);
            if (i + 1 < placeGuidesOnMap.length) {
                nextMarkerLng = placeGuidesOnMap[i+1].marker.getPosition().lng();
            } else {
                nextMarkerLng = placeGuidesOnMap[0].marker.getPosition().lng();
            }
            if (MapPlaceGuideDisplayer.lngDistance(position.lng(), nextMarkerLng) > maxLngDifference) {
                maxLngDifference = MapPlaceGuideDisplayer.lngDistance(position.lng(), nextMarkerLng)
                maxLngDifferenceWestCorner = position.lng();
            }
        }
        let westLng = maxLngDifferenceWestCorner + maxLngDifference;
        if (westLng > 180) westLng -= 360;
        let eastLng = maxLngDifferenceWestCorner;
        let southWestCorner = new google.maps.LatLng(minLat, westLng);
        let northEastCorner = new google.maps.LatLng(maxLat, eastLng);
        map.setZoom(15);
        map.fitBounds(new google.maps.LatLngBounds(southWestCorner, northEastCorner), 10);
    }
  }

  static lngDistance(lngWest, lngEast) {
      if(lngWest < lngEast) {
          return lngEast - lngWest;
      } else {
          // West points distance from IDL + east points distance to IDL;
          // (180 - lngWest) + (180 + lngEast);
          return 360 - lngWest + lngEast;
      }
  }

  // Sorts placeGuides in an ascending order based on their 
  // longitude coordinates.
  static comparePlaceGuidesOnMap(placeGuideA, placeGuideB) {
    let positionA = placeGuideA.marker.getPosition();
    let positionB = placeGuideB.marker.getPosition();
    return positionA.lng()-positionB.lng();
  }

  updateMarkerClusters() {
    if (this._markerClusterer != undefined) {
      this._markerClusterer.clearMarkers();
    }
    const markers = [];
    for (const placeGuideId in this._placeGuidesOnMap) {
      if (this._placeGuidesOnMap.hasOwnProperty(placeGuideId)) {
        markers.push(this._placeGuidesOnMap[placeGuideId].marker);
      }
    }
    this._markerClusterer = new MarkerClusterer(map, markers,
        {imagePath: './img/m'});
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
