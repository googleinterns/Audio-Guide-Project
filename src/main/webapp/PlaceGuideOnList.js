class PlaceGuideOnList {
  constructor(placeGuideId, place, name, creator, description, audioKey, audioLength, imageKey, createdByCurrentUser) {
    const placeGuideDiv = document.createElement("div");
    placeGuideDiv.setAttribute('id', placeGuideId);
    
    placeGuideDiv.addEventListener('click', function() {
      PlaceGuideManager.highlightPlaceGuide(placeGuideId);
    });

    createBlobView(audioKey, "audio", placeGuideDiv);
    createBlobView(imageKey, "img", placeGuideDiv);

    if (createdByCurrentUser) {
      const deleteButton = document.createElement("button");
      deleteButton.addEventListener("click", function() {
        if (window.confirm("Click ok if you want to delete the place guide")) {
          PlaceGuideManager.remove(placeGuideId);
        }
      });

      const editButton = document.createElement("button");
      editButton.addEventListener("click", function() {
        // Prompt to createPlaceGuide.html.
        setupCreatePlaceGuide();
      });
    }

  }
}

function createBlobView(blobKey, elementType, parentDiv) {
  const element = document.createElement(elementType);
  const src = new URL("/serve-blob", document.URL);
  src.searchParams.append('blob-key', blobKey);
  element.setAttribute("src", src);
  parentDiv.appendChild(element);
}