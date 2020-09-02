/**
 * This class is responsible for representing
 * the placeGuides on a scrollable list.
 */
class ListPlaceGuideDisplayer {
  constructor() {    
    this._listPlaceGuideDisplayerDiv = document.getElementById("listPlaceGuideDisplayer");
    this._listPlaceGuideDisplayerDiv.classList.add(
        "list-group", "my-place-guide-list", "list-place-guide-displayer", "form-card");
  }

  get listPlaceGuideDisplayerDiv() {
    return this._listPlaceGuideDisplayerDiv;
  }

  update(placeGuides) {
    this.removeAllPlaceGuidesFromList();
    this.addPlaceGuidesToList(placeGuides);
  }

  removeAllPlaceGuidesFromList() {
    while (this._listPlaceGuideDisplayerDiv.firstChild) {
      this._listPlaceGuideDisplayerDiv.removeChild(
          this._listPlaceGuideDisplayerDiv.lastChild);
    };
  }

  addPlaceGuidesToList(placeGuides) {
    for (var placeGuideId in placeGuides) {
      if (placeGuides.hasOwnProperty(placeGuideId)) {
        var constructedPlaceGuideOnListDiv = 
            this.constructPlaceGuideOnListDivFromPlaceGuide(placeGuides[placeGuideId]);
        this._listPlaceGuideDisplayerDiv.appendChild(constructedPlaceGuideOnListDiv);
      }
    }
  }

  remove(placeGuideId) {
    const placeGuideDivId = "placeGuideOnList-" + "{" + placeGuideId + "}";
    const placeGuideDiv = document.getElementById(placeGuideDivId);
    if (this._listPlaceGuideDisplayerDiv.contains(placeGuideDiv)) {
      this._listPlaceGuideDisplayerDiv.removeChild(placeGuideDiv);
    }
    return placeGuideDiv;
  }

  // Move the highlighted place guide to top of list.
  highlight(placeGuideId) {
    const placeGuideDiv = this.remove(placeGuideId);
    this._listPlaceGuideDisplayerDiv.insertBefore(
        placeGuideDiv, this._listPlaceGuideDisplayerDiv.firstChild);
    highlight(placeGuideId);
  }

  unhighlight(placeGuideId) {
    unhighlight(placeGuideId);
  }

  constructPlaceGuideOnListDivFromPlaceGuide(placeGuide) {
    const mapsPlace = placeGuide.location.mapsPlace;
    var placeName;
    var placeId;
    if (mapsPlace == null) {
      placeName = null;
      placeId = null;
    } else {
      placeName = mapsPlace.name;
      placeId = mapsPlace.place_id;
    }
    return new PlaceGuideOnList(
        placeGuide.id,
        placeName,
        placeId,
        placeGuide.name,
        placeGuide.creator,
        placeGuide.description,
        placeGuide.audioKey,
        placeGuide.audioLength,
        placeGuide.isPublic,
        placeGuide.imgKey,
        placeGuide.createdByCurrentUser,
        placeGuide.bookmarkedByCurrentUser,
        placeGuide.location.position.lat(),
        placeGuide.location.position.lng()).placeGuideOnListDiv;
  }

}
