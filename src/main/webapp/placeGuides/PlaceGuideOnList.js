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

    this._placeGuideOnListDiv = createPlaceGuideOnListDiv(
        this._placeGuideProperties, 
        creator,
        createdByCurrentUser,
        bookmarkedByCurrentUser);
  }

  get placeGuideOnListDiv() {
    return this._placeGuideOnListDiv;
  }
}

function highlight(placeGuideId) {
    expand(placeGuideId);
}

function unhighlight(placeGuideId) {
    close(placeGuideId)
}

function createPlaceGuideOnListDiv(
    placeGuideProperties, 
    creator, 
    createdByCurrentUser, 
    bookmarkedByCurrentUser) {
  const placeGuideDiv = initiatePlaceGuideOnListDiv(
      placeGuideProperties.placeGuideId);
  appendChildren(
      placeGuideDiv, placeGuideProperties, creator, createdByCurrentUser, bookmarkedByCurrentUser);
  return placeGuideDiv;
}

function initiatePlaceGuideOnListDiv(placeGuideId) {
  const placeGuideDiv = document.createElement("div");
  const divId = "placeGuideOnList-" + "{" + placeGuideId + "}";
  placeGuideDiv.setAttribute('id', divId);
  placeGuideDiv.classList.add(
      "list-group-item", 
      "list-group-item-action",
      "flex-column",
      "align-items-start");
  return placeGuideDiv;
}

function appendChildren(
    placeGuideDiv, placeGuideProperties, creator, createdByCurrentUser, bookmarkedByCurrentUser) {
  placeGuideDiv.appendChild(createFoldedPlaceGuide(placeGuideProperties));
  placeGuideDiv.appendChild(
      createCardPlaceGuide(
          placeGuideProperties, creator, createdByCurrentUser, bookmarkedByCurrentUser));
}

function createFoldedPlaceGuide(placeGuideProperties) {
  const placeGuideAudioKey = placeGuideProperties.audioKey;
  const placeGuideId = placeGuideProperties.placeGuideId;
  const placeGuideName = placeGuideProperties.name;
  const placeName = placeGuideProperties.placeName;
  const foldedPlaceGuideDiv = document.createElement("div");
  foldedPlaceGuideDiv.style.display = "block";
  foldedPlaceGuideDiv.classList.add("folded-placeGuide");
  foldedPlaceGuideDiv.appendChild(foldedPlaceGuide_name(placeGuideName));
  if (placeName != undefined || placeName != "" || placeName != null) {
    foldedPlaceGuideDiv.appendChild(foldedPlaceGuide_placeName(placeName));
  }
  foldedPlaceGuideDiv.appendChild(foldedPlaceGuide_buttons(placeGuideId, placeGuideAudioKey));
  return foldedPlaceGuideDiv;
}

function foldedPlaceGuide_name(placeGuideName) {
  const placeGuideNameContainer = document.createElement("div");
  placeGuideNameContainer.classList.add(
      "d-flex",
      "w-100",
      "justify-content-between");
  const placeGuideNameElement = document.createElement("h5");
  placeGuideNameElement.classList.add("mb-1");
  placeGuideNameElement.innerText = placeGuideName;
  placeGuideNameContainer.appendChild(placeGuideNameElement);
  return placeGuideNameContainer;
}

function foldedPlaceGuide_placeName(placeName) {
  const placeNameElement = document.createElement("p");
  placeNameElement.classList.add("mb-1");
  placeNameElement.innerText = placeName;
  return placeNameElement;
}

function foldedPlaceGuide_buttons(placeGuideId, audioKey) {
  const buttonsContainer = document.createElement("div");
  buttonsContainer.classList.add("mdc-card__action-icons");
  createAudioButton(audioKey, buttonsContainer);
  const expandButton = getPlaceGuideButtonWithPreparedClasses();
  expandButton.setAttribute("title", "expand");
  expandButton.innerText = "open_in_full";
  expandButton.addEventListener("click", click => expand(placeGuideId));
  buttonsContainer.appendChild(expandButton);
  return buttonsContainer;
}

function expand(placeGuideId) {
    const divId = "placeGuideOnList-" + "{" + placeGuideId + "}";
    const placeGuideDiv = document.getElementById(divId);
    placeGuideDiv.querySelectorAll(".folded-placeGuide")[0].style.display = "none";
    placeGuideDiv.querySelectorAll(".card-placeGuide")[0].style.display = "block";
    placeGuideDiv.style.padding = "0px";
}

function createCardPlaceGuide(
    placeGuideProperties, creator, createdByCurrentUser, bookmarkedByCurrentUser) {
  const cardPlaceGuideDiv = createCardPlaceGuideDiv();
  const cardDiv = createCardDiv();

  const cardContentsContainer = 
      createAndPopulateCardContentsContainer(placeGuideProperties, creator);

  const buttonsContainer = 
      createAndPopulateButtonsContainer(
          placeGuideProperties, creator, createdByCurrentUser, bookmarkedByCurrentUser);

  cardDiv.appendChild(cardContentsContainer);
  cardDiv.appendChild(buttonsContainer);
  cardPlaceGuideDiv.appendChild(cardDiv);
  return cardPlaceGuideDiv;
}

function createCardPlaceGuideDiv() {
  const cardPlaceGuideDiv = document.createElement("div");
  cardPlaceGuideDiv.classList.add("card-placeGuide");
  cardPlaceGuideDiv.style.display = "none";
  return cardPlaceGuideDiv;
}

function createCardDiv() {
  const cardDiv = document.createElement("div");
  cardDiv.classList.add(
      "mdc-card",
      "my-card");
  return cardDiv;
}

function createAndPopulateCardContentsContainer(placeGuideProperties, creator) {
  const cardContentsContainer = document.createElement("div");
  cardContentsContainer.classList.add(
      "mdc-card__media",
      "mdc-card__media--square");

  const cardContents = createCardContents();

  const placeGuideImage = 
      createPlaceGuideImageElement(placeGuideProperties.imageKey);
  const placeGuideTitle = 
      createPlaceGuideTitle(placeGuideProperties.name, creator);
  const placeGuideLength = 
      createPlaceGuideLengthElement(placeGuideProperties.audioLength);
  const placeGuideDescription = 
      createPlaceGuideDescriptionElement(placeGuideProperties.description);
  cardContents.appendChild(placeGuideImage);
  cardContents.appendChild(placeGuideTitle);
  cardContents.appendChild(placeGuideLength);
  cardContents.appendChild(placeGuideDescription);
  cardContentsContainer.appendChild(cardContents);
  return cardContentsContainer;
}

function createCardContents() {
  const cardContents = document.createElement("div");
  cardContents.classList.add(
      "mdc-card__media-content",
      "my-place-guide-image");
  cardContents.style.overflow = "hidden";
  return cardContents;
}

function createPlaceGuideImageElement(placeGuideImageKey) {
  var placeGuideImage = createBlobView(placeGuideImageKey, "img");
  if (placeGuideImageKey == undefined) {
    placeGuideImage.src = "/";
  }
  placeGuideImage.style.width = "100%";
  placeGuideImage.style.height = "180px";
  return placeGuideImage;
}

function createPlaceGuideTitle(placeGuideName, creator) {
  const placeGuideTitle = document.createElement("div");
  placeGuideTitle.classList.add("place-guide-title");
  const placeGuideNameElement = document.createElement("p");
  placeGuideNameElement.innerText = placeGuideName;
  const creatorDiv = UserRepresentation.createUserDiv(creator);
  placeGuideTitle.appendChild(placeGuideNameElement);
  placeGuideTitle.appendChild(creatorDiv);
  return placeGuideTitle;
}

function createPlaceGuideLengthElement(placeGuideAudioLength) {
  const placeGuideLength = document.createElement("p");
  placeGuideLength.classList.add("place-guide-length");
  placeGuideLength.innerText = placeGuideAudioLength + " minutes";
  return placeGuideLength;
}

function createPlaceGuideDescriptionElement(placeGuideDescription) {
  const description = document.createElement("p");
  description.classList.add("place-guide-description");
  if (placeGuideDescription != undefined) {
    description.innerText = placeGuideDescription;
  }
  return description;
}

function createAndPopulateButtonsContainer(
    placeGuideProperties, creator, createdByCurrentUser, bookmarkedByCurrentUser) {
  const buttonsContainer = document.createElement("div");
  buttonsContainer.classList.add("mdc-card__actions");

  const buttonsSubContainer = createAndPopulateButtonsSubContainer();
  
  createButtonsIfUserIsCreator(
      createdByCurrentUser, buttonsSubContainer, placeGuideProperties);

  createAudioButton(placeGuideProperties.audioKey, buttonsSubContainer);
  createDownloadButton(placeGuideProperties.audioKey, buttonsSubContainer);
  createBookmarkButton(
      placeGuideProperties.placeGuideId, bookmarkedByCurrentUser, buttonsSubContainer);
  createBackToListButton(
      placeGuideProperties.placeGuideId, buttonsSubContainer);

  buttonsContainer.appendChild(buttonsSubContainer);

  return buttonsContainer;
}

function createAudioButton(placeGuideAudioKey, parentDiv) {
  const audioPlayer = document.createElement("audio");
  audioPlayer.src = getBlobSrc(placeGuideAudioKey);
  const audioButton = getPlaceGuideButtonWithPreparedClasses();
  audioButton.setAttribute("title", "play/pause audio");
  audioButton.innerText = "play_arrow";
  audioButton.addEventListener("click", function() {
    if (audioButton.innerText == "play_arrow") {
      audioPlayer.play();
      audioButton.innerText = "pause";
    } else {
      audioPlayer.pause();
      audioButton.innerText = "play_arrow";
    }
  });
  parentDiv.appendChild(audioButton);
}

function createAndPopulateButtonsSubContainer() {
  const buttonsSubContainer = document.createElement("div");
  buttonsSubContainer.classList.add("mdc-card__action-icons");
  return buttonsSubContainer;
}

function getPlaceGuideButtonWithPreparedClasses() {
  const button = document.createElement("button");
  button.classList.add(
      "material-icons",
      "mdc-icon-button",
      "mdc-card__action",
      "mdc-card__action--icon", 
      "specialButton");
  return button;
}

function highlightOnInfoBoxClick(placeGuideDiv, placeGuideId) {
  placeGuideDiv.addEventListener('click', function() {
    highlightOnInfoBoxClick(placeGuideId);
  });
}

function createBlobView(blobKey, elementType) {
  const element = document.createElement(elementType);
  const src = getBlobSrc(blobKey);
  element.setAttribute("src", src);
  return element;
}

function getBlobSrc(blobKey) {
  const src = new URL("/serve-blob", document.URL);
  src.searchParams.append('blob-key', blobKey);
  return src;
}

function generateQueryString(placeGuideProperties) {
  var esc = encodeURIComponent;
  var query = Object.keys(placeGuideProperties)
      .map(function(k) {return esc(k) + '=' + esc(placeGuideProperties[k]);})
      .join('&');
  return query;
}

/**
 * Creates delete and edit button.
 */
function createButtonsIfUserIsCreator(createdByCurrentUser, parentDiv, placeGuideProperties) {
  if (createdByCurrentUser) {
    createDeleteButton(parentDiv, placeGuideProperties.placeGuideId);
    createEditButton(parentDiv, placeGuideProperties);
  }
}

function createDeleteButton(parentDiv, placeGuideId) {

  const deleteButton = getPlaceGuideButtonWithPreparedClasses();
  deleteButton.setAttribute("title", "delete place guide");
  deleteButton.innerText = "delete";

  deleteButton.addEventListener("click", function() {
    if (window.confirm("Click ok if you want to delete the place guide")) {
      placeGuideManager.removePlaceGuide(placeGuideId);
    }
  });
  parentDiv.appendChild(deleteButton);
}

function createEditButton(parentDiv, placeGuideProperties) {

  const editButton = getPlaceGuideButtonWithPreparedClasses();
  editButton.setAttribute("title", "edit place guide");
  editButton.innerText = "edit";

  editButton.addEventListener("click", function() {
    const queryString = generateQueryString(placeGuideProperties);
    const url = "./createPlaceGuide.html?" + queryString;
    window.location = url;
  });
  parentDiv.appendChild(editButton);
}

function createBookmarkButton(placeGuideId, bookmarkedByCurrentUser, parentDiv) {
  const bookmarkButton = getPlaceGuideButtonWithPreparedClasses();
  bookmarkButton.setAttribute("title", "bookmark place guide");
  bookmarkButton.innerText = "bookmark";
  if (bookmarkedByCurrentUser) {
    bookmarkButton.innerText = "bookmark_border";
    bookmarkButton.setAttribute("title", "unbookmark place guide");
  }
  bookmarkButton.addEventListener("click", function() {
    console.log(bookmarkButton.innerText);
    if (bookmarkButton.innerText == "bookmark_border") {
      bookmarkButton.innerText = "bookmark";
      bookmarkButton.setAttribute("title", "bookmark place guide");
    } else {
      bookmarkButton.innerText = "bookmark_border";
      bookmarkButton.setAttribute("title", "unbookmark place guide");
    }
    placeGuideManager.toggleBookmark(placeGuideId);
  });
  parentDiv.appendChild(bookmarkButton);
}

function createDownloadButton(audioKey, parentDiv) {
  const downloadButton = getPlaceGuideButtonWithPreparedClasses();
  downloadButton.setAttribute("title", "download audio");
  downloadButton.innerText = "get_app";
  downloadButton.addEventListener("click", function() {
    window.location.href = getBlobSrc(audioKey);
  });
  parentDiv.appendChild(downloadButton);
}

function createBackToListButton(placeGuideId, parentDiv) {
  const backToListButton = getPlaceGuideButtonWithPreparedClasses();
  backToListButton.setAttribute("title", "back to list");
  backToListButton.innerText = "close_fullscreen";
  backToListButton.addEventListener("click", click => close(placeGuideId));
  parentDiv.appendChild(backToListButton);
}

function close(placeGuideId) {
    const divId = "placeGuideOnList-" + "{" + placeGuideId + "}";
    const placeGuideDiv = document.getElementById(divId);
    placeGuideDiv.querySelectorAll(".folded-placeGuide")[0].style.display = "block";
    placeGuideDiv.querySelectorAll(".card-placeGuide")[0].style.display = "none";
    placeGuideDiv.style.removeProperty("padding");
}