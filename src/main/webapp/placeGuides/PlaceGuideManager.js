/**
 * This class is responsible for connecting all the placeGuide-related 
 * objects: the two displayers and the repository. 
 * When an event occurs, PlaceGuideManager is triggered and it
 * sends the commands to the 
 * classes which are responsible for handling the events. 
 */
class PlaceGuideManager {

    constructor(placeGuideRepository) {
        this._placeGuideRepository = placeGuideRepository;
        this._highlightedPlaceGuideId = null;
        this._mapPlaceGuideDisplayer = new MapPlaceGuideDisplayer();
        this._listPlaceGuideDisplayer = new ListPlaceGuideDisplayer();
    }

    update(bounds, zoom) {
        this._placeGuideRepository.updatePlaceGuides(bounds, zoom)
            .then(response => {
                var placeGuides = this._placeGuideRepository.placeGuides;
                this._mapPlaceGuideDisplayer.update(placeGuides);
                this._mapPlaceGuideDisplayer.update(placeGuides);
            })
    }

    removePlaceGuide(placeGuideId) {
        this._placeGuideRepository.remove(placeGuideId);
        this._mapPlaceGuideDisplayer.remove(placeGuideId);
        this._listPlaceGuideDisplayer.remove(placeGuideId);
    }

    highlightPlaceGuide(placeGuideId) {
        if (this._highlightedPlaceGuideId !== null) {
            this.unhighlightPlaceGuide();
        }
        this._highlightedPlaceGuideId = placeGuideId;
        this._mapPlaceGuideDisplayer.highlight(placeGuideId);
        this._listPlaceGuideDisplayer.highlight(placeGuideId);
    }

    unhighlightPlaceGuide() {
        this._mapPlaceGuideDisplayer.unhighlight(this._highlightedPlaceGuideId);
        this._listPlaceGuideDisplayer.unhighlight(this._highlightedPlaceGuideId);
        this._highlightedPlaceGuideId = null;
    }
}