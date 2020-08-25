class PlaceGuideOnList {
  constructor(
      placeGuideId, placeName, placeId, name, creator, description, 
      audioKey, audioLength, isPublic, imageKey, createdByCurrentUser, 
      bookmarkedByCurrentUser, latitude, longitude) {

    this.placeGuideProperties = {
      placeGuideId: placeGuideId,
      placeName: placeName,
      placeId: placeId,
      name: name,
      audioKey: audioKey,
      imageKey: imageKey,
      description: description,
      audioLength: audioLength,
      isPublic: isPublic,
      latitude: latitude,
      longitude: longitude
    }

    const placeGuideDiv = document.createElement("div");
    const divId = "placeGuideOnList-" + "{" + placeGuideId + "}";
    placeGuideDiv.setAttribute('id', divId);
    
    highlightOnInfoBoxClick(placeGuideDiv, placeGuideId);

    createBlobView(audioKey, "audio", placeGuideDiv);
    createBlobView(imageKey, "img", placeGuideDiv);
    createButtonsIfUserIsCreator(createdByCurrentUser);
    createBookmarkButton(bookmarkedByCurrentUser, placeGuideDiv);
    createDownloadButton(audioKey, placeGuideDiv);
  }
}

function displayPlaceGuideInfo(placeGuideDiv, placeGuideProperties) {
  const name = document.createElement("h3");
  name.setAttribute('id', 'placeGuideOnListName');
  name.innerText = placeGuideProperties.name;

  if (placeGuideProperties.description != undefined) {
    const description = document.createElement("h4");
    description.setAttribute('id', 'placeGuideOnListDescription');
    description.innerText = placeGuideProperties.description;
  }

  if (placeGuideProperties.audioLength != undefined) {
    const audioLength = document.createElement("h5");
    audioLength.setAttribute('id', "placeGuideOnListAudioLength");
    audioLength.innerText = placeGuideProperties.audioLength + "minutes";
  }
}

function highlightOnInfoBoxClick(placeGuideDiv, placeGuideId) {
  placeGuideDiv.addEventListener('click', function() {
    highlightOnInfoBoxClick(placeGuideId);
  });
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

function generateQueryString(placeGuideProperties) {
  var esc = encodeURIComponent;
  var query = Object.keys(placeGuideProperties)
      .map(function(k) {return esc(k) + '=' + esc(params[k]);})
      .join('&');
  return query;
}

/**
 * Creates delete and edit button.
 */
function createButtonsIfUserIsCreator(createdByCurrentUser, placeGuideDiv, placeGuideProperties) {
  if (createdByCurrentUser) {
    createDeleteButton(placeGuideDiv, placeGuideProperties.placeGuideId);
    createEditButton(placeGuideDiv, placeGuideProperties);
  }
}

function createDeleteButton(placeGuideDiv, placeGuideId) {
  const deleteButton = document.createElement("button");
  deleteButton.addEventListener("click", function() {
    if (window.confirm("Click ok if you want to delete the place guide")) {
      placeGuideManager.remove(placeGuideId);
    }
  });
  placeGuideDiv.appendChild(deleteButton);
}

function createEditButton(placeGuideDiv, placeGuideProperties) {
  const editButton = document.createElement("button");
  editButton.addEventListener("click", function() {
    const queryString = generateQueryString(placeGuideProperties);
    const url = "./createPlaceGuide.html?" + queryString;
    window.location = url;
  });
  placeGuideDiv.appendChild(editButton);
}

function createBookmarkButton(bookmarkedByCurrentUser, placeGuideDiv) {
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
    placeGuideManager.toggleBookmark(placeGuideId);
  });
  placeGuideDiv.appendChild(bookmarkButton);
}

function createDownloadButton(audioKey, placeGuideDiv) {
  const downloadButton = document.createElement("button");
  const downloadLink = document.createElement("a");
  downloadLink.setAttribute("href", getBlobSrc(audioKey));
  downloadLink.setAttribute("download", "audio_file");
  downloadLink.innerText = "Download audio file";
  downloadLink.appendChild(downloadButton);
  placeGuideDiv.appendChild(downloadLink);
}