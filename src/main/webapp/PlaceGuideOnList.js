class PlaceGuideOnList {
  constructor(
      placeGuideId, placeName, placeId, name, creator, description, 
      audioKey, audioLength, isPublic, imageKey, createdByCurrentUser, 
      bookmarkedByCurrentUser, latitude, longitude) {
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
      placeGuideDiv.appendChild(deleteButton);

      const editButton = document.createElement("button");
      const queryString = 
      editButton.addEventListener("click", function() {
        const queryString = 
            generateQueryString(
                placeGuideId, placeName, placeId, name, 
                getBlobSrc(audioKey), getBlobSrc(imageKey), 
                description, audioLength, isPublic, latitude, longitude);
        const url = "./createPlaceGuide.html?" + queryString;
        window.location = url;
      });
      placeGuideDiv.appendChild(editButton);
    }

    const bookmarkButton = document.createElement("button");
    if (bookmarkedByCurrentUser) {
      bookmarkButton.innerText = "unbookmark";
    } else {
      bookmarkButton.innerText = "bookmark";
    }
    bookmarkButton.addEventListener("click", function() {
      if (bookmarkButton.innerText == "unbookmark") {
        bookmarkButton.innerText == "bookmark";
      } else {
        bookmarkButton.innerText == "unbookmark";
      }
      PlaceGuideManager.toggleBookmark(placeGuideId);
    });
    placeGuideDiv.appendChild(bookmarkButton);

    const downloadLink = document.createElement("a");
    downloadLink.setAttribute("href", getBlobSrc(audioKey));
    downloadLink.setAttribute("download", "audio_file");
    downloadLink.innerText = "Download audio file";
    placeGuideDiv.appendChild(downloadLink);

  }
}

function createBlobView(blobKey, elementType, parentDiv) {
  const element = document.createElement(elementType);
  const src = getBlobSrc(blobKey);
  element.setAttribute("src", src);
  parentDiv.appendChild(element);
}

function getBlobSrc(blobKey) {
  const src = new URL("/serve-blob", document.URL);
  src.searchParams.append('blob-key', blobKey);
  return src;
}

function generateQueryString(placeGuideId, placeName, placeId, name, audioSrc, imageSrc, description, length, isPublic, latitude, longitude) {
  var params = {
    placeGuideId: placeGuideId,
    placeName: placeName,
    placeId: placeId,
    name: name,
    audioSrc: audioSrc,
    imageSrc: imageSrc,
    description: description,
    length: length,
    isPublic: isPublic,
    latitude: latitude,
    longitude: longitude 
  };

  var esc = encodeURIComponent;
  var query = Object.keys(params)
      .map(function(k) {return esc(k) + '=' + esc(params[k]);})
      .join('&');
  return query;
}