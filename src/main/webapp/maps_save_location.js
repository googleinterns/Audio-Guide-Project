class LocationSaver {
    static SAVE_LOCATION_TEXT = "Save location";
    static SAVE_LOCATION_ID = "locationSaverControl";

    constructor(chosenPlace) {
        this._savedPlace = null;
        this._chosenPlace = chosenPlace; 
        this._saveLocationControlDiv = this.createControlDiv(LocationSaver.SAVE_LOCATION_TEXT, LocationSaver.SAVE_LOCATION_ID);
    }

    /** Creates a div with the style of control elements and with the given text. */
    createControlDiv(text, id) {
        const controlDiv = document.createElement("div");
        controlDiv.setAttribute('class', 'control');
        controlDiv.setAttribute('id', id);
        controlDiv.innerText = text;
        return controlDiv;
    }
}