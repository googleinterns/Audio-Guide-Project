/**
 * This class is responsible for representing the placeGuides on a scrollable list.
 */
class ListPlaceGuideDisplayer {
  constructor() {
    this._placeGuidesOnList = {};
  }

  update(placeGuides) {
    this.removePreviousPlaceGuidesFromList(placeGuides);
    this.addNewPlaceGuidesToList(placeGuides);
  }

  removeFromList(placeGuideId) {

  }

  addToList(placeGuideId) {

  }

  removePreviousPlaceGuidesFromList(placeGuides) {
    for (var placeGuideId in this._placeGuidesOnList) {
      if (this._placeGuidesOnList.hasOwnProperty(placeGuideId)) {
        if (!placeGuides.hasOwnProperty(placeGuideId)) {
          // This placeGuide is not needed anymore.
          this.remove(placeGuideId);
        }
      }
    }
  }

  addNewPlaceGuidesToList(placeGuides) {
    for (var placeGuideId in placeGuides) {
      if (placeGuides.hasOwnProperty(placeGuideId)) {
        if (!this._placeGuidesOnList.hasOwnProperty(placeGuideId)) {
          // new placeGuide should be constructed.
          this._placeGuidesOnList[placeGuideId] =
              this.constructPlaceGuideOnListFromPlaceGuide(placeGuides[placeGuideId]);
          this.addToList(placeGuideId);
        }
      }
    }
  }

  remove(placeGuideId) {
    // Delete from list.
    this.removeFromList(placeGuideIde);
    // Delete from memory.
    delete this._placeGuidesOnList[placeGuideId];
  }

  highlight(placeGuideId) {

  }

  unhighlight(placeGuideId) {

  }

  constructPlaceGuideOnListFromPlaceGuide(placeGuide) {
    return null;
    // new PlaceGuideOnList(placeGuide.id,
    //                            placeGuide.location,
    //                            placeGuide.name,
    //                            placeGuide.creator,
    //                            placeGuide.description,
    //                            placeGuide.audioKey,
    //                            placeGuide.audioLength,
    //                            placeGuide.imgKey,
    //                            placeGuide.createdByCurrentUser);
  }
}