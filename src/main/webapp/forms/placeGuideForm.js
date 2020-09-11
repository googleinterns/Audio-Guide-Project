const DUMMY_DATA_FOR_PLACE_NAME = 'placeName';

/**
 * Handles setting up the create place guide form whenever the page is loaded.
 */
function setUpCreatePlaceGuideForm() {
  addBlobstoreUploadUrlToForm(
      'CREATE_PLACE_GUIDE_FORM', 'createPlaceGuideForm');
  activatePreviewFeature();
  activateRemoveImageFeature("clear-img-icon", false);
  styleInputs();
  fillFormWithPlaceGuideToEdit();
}

/**
 * This function initializes the components
 * managed by the Material Design library.
 */
function styleInputs() {
  const nameInput = new mdc.textField.MDCTextField(
      document.getElementById('nameInput'));
  const lengthInput = new mdc.textField.MDCTextField(
      document.getElementById('lengthInput'));
  const descriptionInput = new mdc.textField.MDCTextField(
      document.getElementById('descriptionInput'));
  const submitButtonRipple = new mdc.ripple.MDCRipple(
      document.getElementById("submitBtn"));
  const chooseAudioFileButtonRipple = new mdc.ripple.MDCRipple(
      document.getElementById("chooseAudioFileBtn"));
  const chooseImageFileButtonRipple = new mdc.ripple.MDCRipple(
      document.getElementById("chooseImageFileBtn"));
  const deletePrevImageCheckbox = new mdc.checkbox.MDCCheckbox(
      document.getElementById('deletePrevImageCheckbox'));
  const deletePrevImageFormField = new mdc.formField.MDCFormField(
      document.getElementById('deletePrevImageFormField'));
  deletePrevImageFormField.input = deletePrevImageCheckbox;
  const publicitySwitchControl = new mdc.switchControl.MDCSwitch(
      document.getElementById("publicitySwitch"));
}

/**
 * This function enables the preview of the picture and audio.
 */
function activatePreviewFeature() {
  handleFileInputChangeEvent(
      'imageKey', showImagePreview, removeImagePreview);
  handleFileInputChangeEvent(
      'audioKey', setAudioPreviewSource, removeAudioPreviewSource);
}

function enableSubmission() {
  document.getElementById("submitBtn").disabled = false;
}

/**
 * This function writes in the hidden form inputs the
 * data of the newly chosen location for the placeguide.
 */
function updateLocation(position, placeId, placeName) {
  if (placeName != null) {
    document.getElementById(
        'placeName').setAttribute('value', placeName);
    document.getElementById(
        'placeId').setAttribute('value', placeId);
  } else {
    document.getElementById(
        'placeName').setAttribute('value', '-');
    document.getElementById(
        'placeId').setAttribute('value', '');
  }
  document.getElementById(
      'latitude').setAttribute('value', position.lat());
  document.getElementById(
      'longitude').setAttribute('value', position.lng());
}

function fillFormWithPlaceGuideData(placeGuide) {
  // Set required attribute to false since there must be a previous audio key
  // from the previous place guide data.
  document.getElementById('audioKey').required = false;
  setFormInputValueOrEmpty(document.getElementById('id'), placeGuide.id);
  setFormInputValueOrEmpty(
      new mdc.textField.MDCTextField(document.getElementById('nameInput')),
      placeGuide.name);
  setBlobKeySrcToElement(
      placeGuide.audioKey, 'audioPlayer', false);
  const publicitySwitchControl =
      new mdc.switchControl.MDCSwitch(document.getElementById("publicitySwitch"));
  if (placeGuide.isPublic) {
    publicitySwitchControl.checked = true;
  } else {
    publicitySwitchControl.checked = false;
  }
  setFormInputValueOrEmpty(
      document.getElementById('latitude'),
      placeGuide.coordinate.latitude);
  setFormInputValueOrEmpty(
      document.getElementById('longitude'),
      placeGuide.coordinate.longitude);
  setFormInputValueOrEmpty(
      document.getElementById('length'),
      placeGuide.length);
  setFormInputValueOrEmpty(
      new mdc.textField.MDCTextField(
          document.getElementById('descriptionInput')),
      placeGuide.description);
  if (placeGuide.imageKey != undefined) {
    setBlobKeySrcToElement(placeGuide.imageKey, "imagePreview", true);
    document.getElementById("no-img-icon").style.display = "none";
    document.getElementById("clear-img-icon").style.display = "block";
    activateRemoveImageFeature(true);
  } else {
    activateRemoveImageFeature(false);
  }
  setFormInputValueOrEmpty(document.getElementById('placeName'),
      DUMMY_DATA_FOR_PLACE_NAME);
}

/**
 * Will fill the form if query string exists. Query string exists only when user wants
 * to edit a place guide.
 */
function fillFormWithPlaceGuideToEdit() {
  if (window.location.search != '') {
    enableSubmission();
    document.getElementById('audioKey').required = false;
    const GET = {};
    const queryString = decodeURI(window.location.search.replace(/^\?/, ''));
    queryString.split(/\&/).forEach(function(keyValuePair) {
      const paramName = keyValuePair.replace(/=.*$/, '');
      const paramValue = keyValuePair.replace(/^[^=]*\=/, '');
      GET[paramName] = paramValue;
    });
    document.getElementById('id').value = GET['placeGuideId'];
    setFormInputValue(
        document.getElementById('placeId').value,
        GET['placeId']);
    setFormInputValue(
        new mdc.textField.MDCTextField(document.getElementById('nameInput')),
        GET['name']);
    document.getElementById('audioPlayer').src = PlaceGuideOnList.getBlobSrc(GET['audioKey']);
    if (GET['imageKey'] != 'undefined') {
      document.getElementById('imagePreview').style.display = 'block';
      document.getElementById('imagePreview').src =
        PlaceGuideOnList.getBlobSrc(GET['imageKey']);
      document.getElementById('no-img-icon')
          .style.display = 'none';
    }
    if (GET['description'] != 'undefined') {
      setFormInputValue(
          new mdc.textField.MDCTextField(document.getElementById('descriptionInput')),
          GET['description']);
    }
    setFormInputValue(
        document.getElementById('latitude'),
        GET['latitude']);
    setFormInputValue(
        document.getElementById('longitude'),
        GET['longitude']);
    setFormInputValue(
        new mdc.textField.MDCTextField(document.getElementById('lengthInput')),
        GET['audioLength']);
    const publicitySwitchControl =
    new mdc.switchControl.MDCSwitch(document.getElementById('publicitySwitch'));
    if (GET['isPublic'] == 'true') {
      publicitySwitchControl.checked = true;
    } else {
      publicitySwitchControl.checked = false;
    }
  }
}
