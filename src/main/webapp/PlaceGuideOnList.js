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
      const queryString = 
      editButton.addEventListener("click", function() {
        window.location = "./createPlaceGuide.html?";
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

function generateQueryString(placeGuideId, placeName, placeId, name, audioSrc, imageSrc, description, length, isPublic, latitude, longitude) {
  
}