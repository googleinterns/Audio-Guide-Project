class PlaceGuideOnList {
  constructor(
      placeGuideId, location, name, creator, description,
      audioKey, audioLength, isPublic, imageKey, createdByCurrentUser,
      bookmarkedByCurrentUser, latitude, longitude) {
    this._placeGuideProperties = {
      placeGuideId: placeGuideId,
      location: location,
      name: name,
      audioKey: audioKey,
      imageKey: imageKey,
      description: description,
      audioLength: audioLength,
      isPublic: isPublic,
      latitude: latitude,
      longitude: longitude,
    };

    this._placeGuideOnListDiv = this.createPlaceGuideOnListDiv(
        this._placeGuideProperties,
        creator,
        createdByCurrentUser,
        bookmarkedByCurrentUser);
  }

  get placeGuideOnListDiv() {
    return this._placeGuideOnListDiv;
  }

  static highlight(placeGuideId) {
    PlaceGuideOnList.expand(placeGuideId);
  }

  static unhighlight(placeGuideId) {
    PlaceGuideOnList.close(placeGuideId);
  }

  createPlaceGuideOnListDiv(
      placeGuideProperties,
      creator,
      createdByCurrentUser,
      bookmarkedByCurrentUser) {
    const placeGuideDiv = this.initiatePlaceGuideOnListDiv(
        placeGuideProperties.placeGuideId);
    this.appendChildren(
        placeGuideDiv, placeGuideProperties, creator, createdByCurrentUser, bookmarkedByCurrentUser);
    return placeGuideDiv;
  }

  initiatePlaceGuideOnListDiv(placeGuideId) {
    const placeGuideDiv = document.createElement('div');
    const divId = 'placeGuideOnList-' + '{' + placeGuideId + '}';
    placeGuideDiv.setAttribute('id', divId);
    placeGuideDiv.classList.add(
        'list-group-item',
        'list-group-item-action',
        'flex-column',
        'align-items-start');
    return placeGuideDiv;
  }

  appendChildren(
      placeGuideDiv, placeGuideProperties, creator, createdByCurrentUser, bookmarkedByCurrentUser) {
    placeGuideDiv.appendChild(this.createFoldedPlaceGuide(placeGuideProperties));
    placeGuideDiv.appendChild(
        this.createCardPlaceGuide(
            placeGuideProperties, creator, createdByCurrentUser, bookmarkedByCurrentUser));
  }

  createFoldedPlaceGuide(placeGuideProperties) {
    const placeGuideAudioKey = placeGuideProperties.audioKey;
    const placeGuideId = placeGuideProperties.placeGuideId;
    const placeGuideName = placeGuideProperties.name;
    const placeName = placeGuideProperties.placeName;
    const foldedPlaceGuideDiv = document.createElement('div');
    foldedPlaceGuideDiv.style.display = 'block';
    foldedPlaceGuideDiv.classList.add('folded-placeGuide');
    foldedPlaceGuideDiv.appendChild(this.foldedPlaceGuide_name(placeGuideName));
    foldedPlaceGuideDiv.appendChild(this.foldedPlaceGuide_buttons(placeGuideId, placeGuideAudioKey));
    return foldedPlaceGuideDiv;
  }

  foldedPlaceGuide_name(placeGuideName) {
    const placeGuideNameContainer = document.createElement('div');
    placeGuideNameContainer.classList.add(
        'd-flex',
        'w-100',
        'justify-content-between');
    const placeGuideNameElement = document.createElement('h5');
    placeGuideNameElement.classList.add('mb-1');
    placeGuideNameElement.innerText = placeGuideName;
    placeGuideNameContainer.appendChild(placeGuideNameElement);
    return placeGuideNameContainer;
  }

  foldedPlaceGuide_buttons(placeGuideId, audioKey) {
    const buttonsContainer = document.createElement('div');
    buttonsContainer.classList.add('mdc-card__action-icons');
    this.createAudioButton(audioKey, buttonsContainer);
    const expandButton = this.getPlaceGuideButtonWithPreparedClasses();
    expandButton.setAttribute('title', 'expand');
    expandButton.innerText = 'open_in_full';
    expandButton.addEventListener('click', function() {
      PlaceGuideOnList.expand(placeGuideId);
    });
    buttonsContainer.appendChild(expandButton);
    return buttonsContainer;
  }

  static expand(placeGuideId) {
    const divId = 'placeGuideOnList-' + '{' + placeGuideId + '}';
    const placeGuideDiv = document.getElementById(divId);
    placeGuideDiv.querySelectorAll('.folded-placeGuide')[0].style.display = 'none';
    placeGuideDiv.querySelectorAll('.card-placeGuide')[0].style.display = 'block';
    placeGuideDiv.style.padding = '0px';
  }

  createCardPlaceGuide(
      placeGuideProperties, creator, createdByCurrentUser, bookmarkedByCurrentUser) {
    const cardPlaceGuideDiv = this.createCardPlaceGuideDiv();
    const cardDiv = this.createCardDiv();

    const cardContentsContainer =
        this.createAndPopulateCardContentsContainer(placeGuideProperties, creator);

    const buttonsContainer =
        this.createAndPopulateButtonsContainer(
            placeGuideProperties, creator, createdByCurrentUser, bookmarkedByCurrentUser);

    cardDiv.appendChild(cardContentsContainer);
    cardDiv.appendChild(buttonsContainer);
    cardPlaceGuideDiv.appendChild(cardDiv);
    return cardPlaceGuideDiv;
  }

  createCardPlaceGuideDiv() {
    const cardPlaceGuideDiv = document.createElement('div');
    cardPlaceGuideDiv.classList.add('card-placeGuide');
    cardPlaceGuideDiv.style.display = 'none';
    return cardPlaceGuideDiv;
  }

  createCardDiv() {
    const cardDiv = document.createElement('div');
    cardDiv.classList.add(
        'mdc-card',
        'my-card');
    return cardDiv;
  }

  createAndPopulateCardContentsContainer(placeGuideProperties, creator) {
    const cardContentsContainer = document.createElement('div');
    cardContentsContainer.classList.add(
        'mdc-card__media',
        'mdc-card__media--square');

    const cardContents = this.createCardContents();

    const placeGuideImage =
        this.createPlaceGuideImageElement(placeGuideProperties.imageKey);
    const placeGuideTitle =
        this.createPlaceGuideTitle(placeGuideProperties.name, creator.email);
    const placeGuideLength =
        this.createPlaceGuideLengthElement(placeGuideProperties.audioLength);
    const placeGuidePlaceName =
        this.createPlaceGuidePlaceNameElement();
    const placeGuideDescription =
        this.createPlaceGuideDescriptionElement(placeGuideProperties.description);

    cardContents.appendChild(placeGuideImage);
    cardContents.appendChild(placeGuideTitle);
    cardContents.appendChild(placeGuidePlaceName);
    cardContents.appendChild(placeGuideLength);
    cardContents.appendChild(placeGuideDescription);
    cardContentsContainer.appendChild(cardContents);
    return cardContentsContainer;
  }

  createCardContents() {
    const cardContents = document.createElement('div');
    cardContents.classList.add(
        'mdc-card__media-content',
        'my-place-guide-image');
    cardContents.style.overflow = 'hidden';
    return cardContents;
  }

  createPlaceGuideImageElement(placeGuideImageKey) {
    let placeGuideImage;
    if (placeGuideImageKey != undefined) {
      placeGuideImage = this.createBlobView(placeGuideImageKey, 'img');
    } else {
      placeGuideImage = document.createElement('i');
      placeGuideImage.classList.add('material-icons', 'md-48');
      placeGuideImage.style.textAlign = 'center';
      placeGuideImage.style.paddingTop = '90px';
      placeGuideImage.innerText = 'tour';
    }
    placeGuideImage.style.width = '100%';
    placeGuideImage.style.height = '180px';
    return placeGuideImage;
  }

  createPlaceGuideTitle(placeGuideName, creatorEmail) {
    const placeGuideTitle = document.createElement('div');
    placeGuideTitle.classList.add('place-guide-title');
    const placeGuideNameElement = document.createElement('h5');
    placeGuideNameElement.innerText = placeGuideName;
    const creatorButton = this.getPlaceGuideButtonWithPreparedClasses();
    creatorButton.setAttribute('title', creatorEmail);
    creatorButton.innerText = 'account_circle';
    placeGuideTitle.appendChild(placeGuideNameElement);
    placeGuideTitle.appendChild(creatorButton);
    placeGuideTitle.style.paddingTop = '10px';
    placeGuideTitle.style.marginTop = '10px';
    return placeGuideTitle;
  }

  createPlaceGuideLengthElement(placeGuideAudioLength) {
    const placeGuideLength = document.createElement('p');
    placeGuideLength.classList.add('place-guide-length');
    placeGuideLength.innerText = placeGuideAudioLength + ' minutes';
    return placeGuideLength;
  }

  createPlaceGuidePlaceNameElement() {
    const placeNameElement = document.createElement('h5');
    placeNameElement.classList.add('place-guide-place-name');
    placeNameElement.innerText = "Random Name";
    return placeNameElement;
  }

  createPlaceGuideDescriptionElement(placeGuideDescription) {
    const description = document.createElement('p');
    description.classList.add('place-guide-description');
    if (placeGuideDescription != undefined) {
      description.innerText = placeGuideDescription;
    }
    return description;
  }

  createAndPopulateButtonsContainer(
      placeGuideProperties, creator, createdByCurrentUser, bookmarkedByCurrentUser) {
    const buttonsContainer = document.createElement('div');
    buttonsContainer.classList.add('mdc-card__actions');

    const buttonsSubContainer = this.createAndPopulateButtonsSubContainer();

    this.createButtonsIfUserIsCreator(
        createdByCurrentUser, buttonsSubContainer, placeGuideProperties);

    this.createAudioButton(placeGuideProperties.audioKey, buttonsSubContainer);
    this.createDownloadButton(placeGuideProperties.audioKey, buttonsSubContainer);
    this.createBookmarkButton(
        placeGuideProperties.placeGuideId, bookmarkedByCurrentUser, buttonsSubContainer);
    this.createBackToListButton(
        placeGuideProperties.placeGuideId, buttonsSubContainer);

    buttonsContainer.appendChild(buttonsSubContainer);
    buttonsContainer.style.marginTop = '30px';

    return buttonsContainer;
  }

  createAudioButton(placeGuideAudioKey, parentDiv) {
    const audioPlayer = document.createElement('audio');
    audioPlayer.src = PlaceGuideOnList.getBlobSrc(placeGuideAudioKey);
    const audioButton = this.getPlaceGuideButtonWithPreparedClasses();
    audioButton.setAttribute('title', 'play/pause audio');
    audioButton.innerText = 'play_arrow';
    audioButton.addEventListener('click', function() {
      if (audioButton.innerText == 'play_arrow') {
        audioPlayer.play();
        audioButton.innerText = 'pause';
      } else {
        audioPlayer.pause();
        audioButton.innerText = 'play_arrow';
      }
    });
    parentDiv.appendChild(audioButton);
  }

  createAndPopulateButtonsSubContainer() {
    const buttonsSubContainer = document.createElement('div');
    buttonsSubContainer.classList.add('mdc-card__action-icons');
    return buttonsSubContainer;
  }

  getPlaceGuideButtonWithPreparedClasses() {
    const button = document.createElement('button');
    button.classList.add(
        'material-icons',
        'mdc-icon-button',
        'mdc-card__action',
        'mdc-card__action--icon',
        'specialButton');
    return button;
  }

  highlightOnInfoBoxClick(placeGuideDiv, placeGuideId) {
    placeGuideDiv.addEventListener('click', function() {
      highlightOnInfoBoxClick(placeGuideId);
    });
  }

  createBlobView(blobKey, elementType) {
    const element = document.createElement(elementType);
    const src = PlaceGuideOnList.getBlobSrc(blobKey);
    element.setAttribute('src', src);
    return element;
  }

  static getBlobSrc(blobKey) {
    const src = new URL('/serve-blob', document.URL);
    src.searchParams.append('blob-key', blobKey);
    return src;
  }

  static generateQueryString(placeGuideProperties) {
    const esc = encodeURIComponent;
    const query = Object.keys(placeGuideProperties)
        .map(function(k) {
          return esc(k) + '=' + esc(placeGuideProperties[k]);
        })
        .join('&');
    return query;
  }

  createButtonsIfUserIsCreator(createdByCurrentUser, parentDiv, placeGuideProperties) {
    if (createdByCurrentUser) {
      this.createDeleteButton(parentDiv, placeGuideProperties.placeGuideId);
      this.createEditButton(parentDiv, placeGuideProperties);
    }
  }

  createDeleteButton(parentDiv, placeGuideId) {
    const deleteButton = this.getPlaceGuideButtonWithPreparedClasses();
    deleteButton.setAttribute('title', 'delete place guide');
    deleteButton.innerText = 'delete';

    deleteButton.addEventListener('click', function() {
      if (window.confirm('Click ok if you want to delete the place guide')) {
        placeGuideManager.removePlaceGuide(placeGuideId);
      }
    });
    parentDiv.appendChild(deleteButton);
  }

  createEditButton(parentDiv, placeGuideProperties) {
    const editButton = this.getPlaceGuideButtonWithPreparedClasses();
    editButton.setAttribute('title', 'edit place guide');
    editButton.innerText = 'edit';

    editButton.addEventListener('click', function() {
      const queryString = PlaceGuideOnList.generateQueryString(placeGuideProperties);
      const url = './createPlaceGuide.html?' + queryString;
      window.location = url;
    });
    parentDiv.appendChild(editButton);
  }

  createBookmarkButton(placeGuideId, bookmarkedByCurrentUser, parentDiv) {
    const bookmarkButton = this.getPlaceGuideButtonWithPreparedClasses();
    bookmarkButton.setAttribute('title', 'bookmark place guide');
    bookmarkButton.innerText = 'bookmark_border';
    if (bookmarkedByCurrentUser) {
      bookmarkButton.innerText = 'bookmark';
      bookmarkButton.setAttribute('title', 'unbookmark place guide');
    }
    bookmarkButton.addEventListener('click', function() {
      if (bookmarkButton.innerText == 'bookmark') {
        bookmarkButton.innerText = 'bookmark_border';
        bookmarkButton.setAttribute('title', 'bookmark place guide');
      } else {
        bookmarkButton.innerText = 'bookmark';
        bookmarkButton.setAttribute('title', 'unbookmark place guide');
      }
      placeGuideManager.toggleBookmark(placeGuideId);
    });
    parentDiv.appendChild(bookmarkButton);
  }

  createDownloadButton(audioKey, parentDiv) {
    const downloadButton = this.getPlaceGuideButtonWithPreparedClasses();
    downloadButton.setAttribute('title', 'download audio');
    downloadButton.innerText = 'get_app';
    downloadButton.addEventListener('click', function() {
      window.location.href = PlaceGuideOnList.getBlobSrc(audioKey);
    });
    parentDiv.appendChild(downloadButton);
  }

  createBackToListButton(placeGuideId, parentDiv) {
    const backToListButton = this.getPlaceGuideButtonWithPreparedClasses();
    backToListButton.setAttribute('title', 'back to list');
    backToListButton.innerText = 'close_fullscreen';
    backToListButton.addEventListener('click', function() {
      PlaceGuideOnList.close(placeGuideId);
    });

    parentDiv.appendChild(backToListButton);
  }

  static close(placeGuideId) {
    const divId = 'placeGuideOnList-' + '{' + placeGuideId + '}';
    const placeGuideDiv = document.getElementById(divId);
    if (placeGuideDiv !== null) {
      placeGuideDiv
          .querySelectorAll('.folded-placeGuide')[0].style.display = 'block';
      placeGuideDiv
          .querySelectorAll('.card-placeGuide')[0].style.display = 'none';
      placeGuideDiv.style.removeProperty('padding');
    }
  }
}
