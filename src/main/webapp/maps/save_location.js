/**
 * This class is responsible for saving the chosen place when the user clicks the "Save location" button.
 * Remark that there would be 2 markers on top of each other, 
 * so instead savedPlace's marker is set visible only when the other one changes its position.
 */
class LocationSaver {
    static SAVE_LOCATION_TEXT = "Save location";
    static SAVE_LOCATION_ID = "locationSaverControl";
    static SAVED_LOCATION_MESSAGE = "Location saved!";

    constructor(map, chosenPlace) {
        this._map = map;
        this._savedPlace = new Place(0, 0, "Saved location", null, PlaceType.SAVED_LOCATION, true);
        this._savedPlace.map = this._map;
        this._savedPlace.visible = false;
        this._chosenPlace = chosenPlace; 
        this._saveLocationControlButton = this.createControlButton(LocationSaver.SAVE_LOCATION_TEXT, LocationSaver.SAVE_LOCATION_ID);
        this._saveLocationControlButton.disabled = true;
        this._map.controls[google.maps.ControlPosition.BOTTOM_CENTER].push(this._saveLocationControlButton);
        this._chosenPlace.enableElementOnPositionChange(this._saveLocationControlButton);
    }

    init() {
        this._saveLocationControlButton.addEventListener("click",
                event => this.onSaveLocationControlEvent());
    }

    get savedLocation() {
        return this._savedPlace;
    }

    onSaveLocationControlEvent() {
        this._savedPlace.visible = false;
        if (this._chosenPlace.place != null) {
            this._savedPlace.place = this._chosenPlace.place;
        } else {
            this._savedPlace.position = this._chosenPlace.position;
        }
        alert(LocationSaver.SAVED_LOCATION_MESSAGE);
        this._chosenPlace.attachToSavePlace(this._savedPlace);
    }

    /** Creates a button with the style of control elements and with the given text. */
    createControlButton(text, id) {
        const controlDiv = document.createElement("button");
        controlDiv.setAttribute('class', 'map-button');
        controlDiv.setAttribute('id', id);
        controlDiv.innerText = text;
        return controlDiv;
    }
}