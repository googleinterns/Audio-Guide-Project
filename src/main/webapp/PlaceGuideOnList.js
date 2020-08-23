class PlaceGuideOnList {
  constructor(placeGuideId, place, name, creator, description, audioKey, audioLength, imageKey, createdByCurrentUser) {
    const placeGuideDiv = document.createElement("div");
    placeGuideDiv.setAttribute('id', placeGuideId);
    
    // Set onclick event to highlight the place guide on the list.
    placeGuideDiv.addEventListener('click', function() {
      PlaceGuideManager.highlightPlaceGuide(placeGuideId);
    });

    const placeGuideAudio = document.createElement("audio");
    if (displayBlock) {
      preview.style.display = "block";
    }
    const src = new URL("/serve-blob", document.URL);
    src.searchParams.append('blob-key', blobKey);
    preview.setAttribute("src", src);
  }
}