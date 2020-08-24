class MapPlaceGuideDisplayer {
    constructor(map) {
        this._map = map;
        this._placeGuidesOnMap = {};
        console.log("MapPlaceGuideDisplayer constructed");
    }

    update(placeGuides) {
        this.removePreviousPlaceGuidesFromMap(placeGuides);
        this.addNewPlaceGuidesToMap(placeGuides);
    }

    removePreviousPlaceGuidesFromMap(placeGuides) {
        for (var placeGuideId in this._placeGuidesOnMap) {
            if (this._placeGuidesOnMap.hasOwnProperty(placeGuideId)) {           
                if(!placeGuides.hasOwnProperty(placeGuideId)) {
                    // This placeGuide is not needed anymore.
                    this._placeGuidesOnMap[placeGuideId].remove();
                }
            }
        }
    }

    addNewPlaceGuidesToMap(placeGuides) {
        for (var placeGuideId in placeGuides) {
            if (placeGuides.hasOwnProperty(placeGuideId)) {           
                if(!this._placeGuidesOnMap.hasOwnProperty(placeGuideId)) {
                    // new placeGuide should be constructed.
                    this._placeGuidesOnMap[placeGuideId] = this.constructPlaceGuideOnMapFromPlaceGuide(placeGuides[placeGuideId]);
                    console.log("create placeGuideOnMap for id = " + placeGuideId);
                    console.log(this._placeGuidesOnMap[placeGuideId]);
                }
            }
        }
    }

    constructPlaceGuideOnMapFromPlaceGuide(placeGuide) {
        var placeType;
        if (placeGuide.isPublic) {
            placeType = PlaceType.PUBLIC;
        } else {
            placeType = PlaceType.PRIVATE;
        }
        return new PlaceGuideOnMap(this._map, 
                                   placeGuide.id, 
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
        console.log("searching for placeGuideOnMap with id = " + placeGuideId);
         for (var existingPlaceGuideId in this._placeGuidesOnMap) {
            if (this._placeGuidesOnMap.hasOwnProperty(existingPlaceGuideId)) {           
                console.log("existing placeGuideOnMap with id " + existingPlaceGuideId);
            }
        }
        console.log("found placeGuideOnMap: ");
        console.log( this._placeGuidesOnMap[placeGuideId]);
        this._placeGuidesOnMap[placeGuideId].highlight();
    }
}