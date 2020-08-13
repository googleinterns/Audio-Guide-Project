class LocationSaver {
    static SAVE_LOCATION_TEXT = "Save location";
    static SAVE_LOCATION_ID = "locationSaverControl";

    constructor(map, chosenPlace) {
        this._map = map;
        this._savedPlace = new Place(0, 0, "Saved location", null, PlaceType.SAVED_LOCATION, true);
        this._savedPlace.map = this._map;
        this._savedPlace.visible = false;
        this._chosenPlace = chosenPlace; 
        this._saveLocationControlDiv = this.createControlDiv(LocationSaver.SAVE_LOCATION_TEXT, LocationSaver.SAVE_LOCATION_ID);
        this._map.controls[google.maps.ControlPosition.BOTTOM_CENTER].push(this._saveLocationControlDiv);
    }

    init() {
        this._saveLocationControlDiv.addEventListener("click",
                event => this.onSaveLocationControlEvent());
    }

    onSaveLocationControlEvent() {
        if (this._chosenPlace.place != null) {
            this._savedPlace.place = this._chosenPlace.place;
            this._chosenPlace.place = this._chosenPlace.place;
        } else {
            this._savedPlace.position = this._chosenPlace.position;
            this._chosenPlace.posiiton = this._chosenPlace.position;
        }
        this._savedPlace.visible = true;
    }

    /** Creates a div with the style of control elements and with the given text. */
    createControlDiv(text, id) {
        const controlDiv = document.createElement("div");
        controlDiv.setAttribute('class', 'map-button');
        controlDiv.setAttribute('id', id);
        controlDiv.innerText = text;
        return controlDiv;
    }
}