class PlaceGuideManager {
    static MIN_ZOOM = 10;

    constructor(map, placeGuideRepository) {
        this._map = map;
        this._placeGuideRepository = placeGuideRepository;
        this._mapPlaceGuideDisplayer = new MapPlaceGuideDisplayer(this._map);
        this._listPlaceGuideDisplayer = new ListPlaceGuideDisplayer(this._map);
        console.log("PlaceGuideManager constructed");
    }

    update(bounds, zoom) {

    }

    removePlaceGuide(placeGuideId) {
        this._placeGuideRepository.remove(placeGuideId);
        this._mapPlaceGuideDisplayer.remove(placeGuideId);
        this._listPlaceGuideDisplayer.remove(placeGuideId);
    }

    highlightPlaceGuide(placeGuideId) {
        this._mapPlaceGuideDisplayer.highlight(placeGuideId);
        this._listPlaceGuideDisplayer.highlight(placeGuideId);
    }
}