const DUMMY_DATA_FOR_PLACE_NAME = 'placeName';

/**
 * Handles setting up the create place guide form whenever the page is loaded.
 */
function setUpCreatePlaceGuideForm() {
  addBlobstoreUploadUrlToForm(
      'CREATE_PLACE_GUIDE_FORM', 'createPlaceGuideForm');
  activatePreviewFeature();
  styleInputs();
}

function styleInputs() {
    const nameInput = new mdc.textField.MDCTextField(document.getElementById('nameInput'));
    const lengthInput = new mdc.textField.MDCTextField(document.getElementById('lengthInput'));
    const descriptionInput = new mdc.textField.MDCTextField(document.getElementById('descriptionInput'));
    // const submitButtonRipple = new mdc.ripple.MDCRipple(document.getElementById("submitBtn"));
    const chooseAudioFileButtonRipple = new mdc.ripple.MDCRipple(document.getElementById("chooseAudioFileBtn"));
    const chooseImageFileButtonRipple = new mdc.ripple.MDCRipple(document.getElementById("chooseImageFileBtn"));
    const deletePrevImageCheckbox = new mdc.checkbox.MDCCheckbox(document.getElementById('deletePrevImageCheckbox'));
    const deletePrevImageFormField = new mdc.formField.MDCFormField(document.getElementById('deletePrevImageFormField'));
    deletePrevImageFormField.input = deletePrevImageCheckbox;
    const publicitySwitchControl = new mdc.switchControl.MDCSwitch(document.getElementById("publicitySwitch"));
}

function activatePreviewFeature() {
  setSrcToElementOnChangeEvent(
      'imageKey', 'imagePreview', true);
  setSrcToElementOnChangeEvent(
      'audioKey', 'audioPlayer', false);
}

// Just a test by fetching actual place guides' data from database 
// to see if image and audio previewing also works with files from blobstore.
function testExistingPlaceGuide() {
  getFetchedList().then(placeGuideCreatorPairs => {
    if (placeGuideCreatorPairs === undefined || placeGuideCreatorPairs.length == 0) {
      console.log("place guide does not exist yet.");
    } else {
      fillFormWithPlaceGuideData(placeGuideCreatorPairs[0].placeGuide); 
    }
  });
}

// For testing.
async function getFetchedList() {
  return fetch(
      '/place-guide-data?placeGuideType=ALL_PUBLIC', {method: 'GET'})
      .then((response) => {
        return response.json();
      });
}

function enableSubmission() {
  document.getElementById("submitBtn").disabled = false;
}

// For testing.
function updateLocation(position, placeId, placeName) {
  document.getElementById(
      'placeId').setAttribute('value', placeId);
  if (placeName != null) {
    document.getElementById(
        'placeName').setAttribute('value', placeName);
  } else {
    document.getElementById(
        'placeName').setAttribute('value', '-');
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
  setFormInputValue(document.getElementById('id'), placeGuide.id);
  setFormInputValue(
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
  setFormInputValue(
      document.getElementById('latitude'),
      placeGuide.coordinate.latitude);
  setFormInputValue(
      document.getElementById('longitude'),
      placeGuide.coordinate.longitude);
  setFormInputValue(
      document.getElementById('length'),
      placeGuide.length);
  setFormInputValue(
      new mdc.textField.MDCTextField(
          document.getElementById('descriptionInput')),
      placeGuide.description);
  if (placeGuide.imageKey != undefined) {
    setBlobKeySrcToElement(
        placeGuide.imageKey,
        'imagePreview', true);
  }
  setFormInputValue(document.getElementById('placeName'),
      DUMMY_DATA_FOR_PLACE_NAME);
}