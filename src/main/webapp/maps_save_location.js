class LocationSaver {
    static SAVE_LOCATION_TEXT = "Save location";
    static SAVE_LOCATION_ID = "locationSaverControl";

    constructor(map, chosenPlace) {
        this._map = map;
        this._savedPlace = null;
        this._chosenPlace = chosenPlace; 
        this._saveLocationControlDiv = this.createControlDiv(LocationSaver.SAVE_LOCATION_TEXT, LocationSaver.SAVE_LOCATION_ID);
        this._map.controls[google.maps.ControlPosition.BOTTOM_CENTER].push(this._saveLocationControlDiv);
    }

    init() {

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