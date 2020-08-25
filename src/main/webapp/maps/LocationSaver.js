/**
 * This class is responsible for saving the chosen place when the user clicks the "Save location" button.
 * Remark that after the two markers get on top of each other,
 * the chosen location will be seen only when its position changes.
 */
class LocationSaver {
  static SAVE_LOCATION_TEXT = "Save location";
  static SAVE_LOCATION_ID = "locationSaverControl";
  static SAVED_LOCATION_MESSAGE = "Location saved!";
  static SAVED_LOCATION_TITLE = "Saved location";
  static SAVE_LOCATION_EVENT = "saveLocationEvent";

  constructor(map, chosenPlace) {
    this._map = map;
    this._savedPlace =
        Place.constructPlaceBasedOnCoordinates(map, 0, 0,
            LocationSaver.SAVED_LOCATION_TITLE, PlaceType.SAVED_LOCATION, true);
    this._savedPlace.map = this._map;
    this._savedPlace.visible = false;
    this._chosenPlace = chosenPlace;
    this._saveLocationControlButton =
        this.createControlButton(LocationSaver.SAVE_LOCATION_TEXT, LocationSaver.SAVE_LOCATION_ID);
    this._saveLocationControlButton.disabled = true;
    this._map.controls[google.maps.ControlPosition.BOTTOM_CENTER]
        .push(this._saveLocationControlButton);
    var saveLocationButton = this._saveLocationControlButton;
    var chosenPlace = this._chosenPlace;
    document.getElementById("map")
        .addEventListener(CHOSEN_LOCATION_CHANGE_EVENT, function () {
          saveLocationButton.disabled = false;
          chosenPlace.visible = true;
        });
  }

  get savedLocation() {
    return this._savedPlace;
  }

  // The LocationSaver starts listening to and handling "save chosen location" 
  // click events when init() is called. 
  init() {
    this._saveLocationControlButton.addEventListener("click",
        event => this.onSaveLocationControlEvent());
  }

  onSaveLocationControlEvent() {
    this._savedPlace.visible = true;
    if (this._chosenPlace.place != null) {
      this._savedPlace.place = this._chosenPlace.place;
    } else {
      this._savedPlace.position = this._chosenPlace.position;
    }
    this._chosenPlace.visible = false;
    this._saveLocationControlButton.disabled = true;
    document.getElementById("map").dispatchEvent(new Event(MapWidget.SAVE_LOCATION_EVENT));
    alert(LocationSaver.SAVED_LOCATION_MESSAGE);
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