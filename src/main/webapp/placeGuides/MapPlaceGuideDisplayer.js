class MapPlaceGuideDisplayer {
    constructor(map) {
        this._map = map;
        this._placeGuidesOnMap = {};
        console.log("MapPlaceGuideDisplayer constructed");
    }

    update(placeGuides) {

    }

    remove(placeGuideId) {
        this._placeGuidesOnMap[placeGuideId].remove();
        delete this._placeGuidesOnMap[placeGuideId];
    }

    highlight(placeGuideId) {
        this._placeGuidesOnMap[placeGuideId].highlight();
    }
}