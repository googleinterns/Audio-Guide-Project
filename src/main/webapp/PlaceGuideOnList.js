class PlaceGuideOnList {
  constructor(
      placeGuideId, placeName, placeId, name, creator, description, 
      audioKey, audioLength, isPublic, imageKey, createdByCurrentUser, 
      bookmarkedByCurrentUser, latitude, longitude) {

    this._placeGuideProperties = {
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

    fillPlaceGuideOnList(
        createPlaceGuideDiv(placeGuideId, creator), 
        this._placeGuideProperties, 
        createdByCurrentUser, 
        bookmarkedByCurrentUser);
  }
  get placeGuideProperties() {
    return this._placeGuideProperties;
  }
}

function createPlaceGuideDiv(placeGuideId, creator) {
  const placeGuideDiv = document.createElement("div");
  const divId = "placeGuideOnList-" + "{" + placeGuideId + "}";
  placeGuideDiv.setAttribute('id', divId);
  placeGuideDiv.appendChild(new User(creator).userRepresentationDiv);
  return placeGuideDiv;
}

function fillPlaceGuideOnList(
    placeGuideDiv, placeGuideProperties, createdByCurrentUser, bookmarkedByCurrentUser) {
  createPlaceGuideInfoDiv(placeGuideDiv, placeGuideProperties);    
  highlightOnInfoBoxClick(placeGuideDiv, placeGuideProperties.placeGuideId);

  createBlobView(placeGuideProperties.audioKey, "audio", placeGuideDiv);
  createBlobView(placeGuideProperties.imageKey, "img", placeGuideDiv);
  createButtonsIfUserIsCreator(createdByCurrentUser, placeGuideDiv, placeGuideProperties);
  createBookmarkButton(bookmarkedByCurrentUser, placeGuideDiv);
  createDownloadButton(placeGuideProperties.audioKey, placeGuideDiv);
}

function createPlaceGuideInfoDiv(placeGuideDiv, placeGuideProperties) {
  const placeGuideInfoDiv = document.createElement("div");
  placeGuideInfoDiv.setAttribute("class", "placeGuideOnListInfo");
  const name = document.createElement("h3");
  name.setAttribute('id', 'placeGuideOnListName');
  name.innerText = placeGuideProperties.name;
  placeGuideDiv.appendChild(name);

  if (placeGuideProperties.description != undefined) {
    const description = document.createElement("h4");
    description.setAttribute('id', 'placeGuideOnListDescription');
    description.innerText = placeGuideProperties.description;
    placeGuideDiv.appendChild(description);
  }

  if (placeGuideProperties.audioLength != undefined) {
    const audioLength = document.createElement("h5");
    audioLength.setAttribute('id', "placeGuideOnListAudioLength");
    audioLength.innerText = placeGuideProperties.audioLength + "minutes";
    placeGuideDiv.appendChild(audioLength);
  }
}

function highlightOnInfoBoxClick(placeGuideDiv, placeGuideId) {
  placeGuideDiv.addEventListener('click', function() {
    highlightOnInfoBoxClick(placeGuideId);
  });
}

function createBlobView(blobKey, elementType, placeGuideDiv) {
  const element = document.createElement(elementType);
  const src = getBlobSrc(blobKey);
  element.setAttribute("src", src);
  placeGuideDiv.appendChild(element);
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