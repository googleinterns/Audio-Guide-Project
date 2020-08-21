class PlaceGuideManager {

    constructor(map, placeGuideRepository) {
        this._map = map;
        this._placeGuideRepository = placeGuideRepository;
        this._mapPlaceGuideDisplayer = new MapPlaceGuideDisplayer(this._map);
        this._listPlaceGuideDisplayer = new ListPlaceGuideDisplayer(this._map);
        console.log("PlaceGuideManager constructed");
    }
}