/**
 * Handles setting up the create place guide form whenever the page is loaded.
 */
function setUpCreatePlaceGuideForm() {
  addBlobstoreUploadUrlToForm(
      'CREATE_PLACE_GUIDE_FORM', 'createPlaceGuideForm');
  activatePreviewFeature();
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
      document.getElementById('submitBtn'));
  const chooseAudioFileButtonRipple = new mdc.ripple.MDCRipple(
      document.getElementById('chooseAudioFileBtn'));
  const chooseImageFileButtonRipple = new mdc.ripple.MDCRipple(
      document.getElementById('chooseImageFileBtn'));
  const deletePrevImageCheckbox = new mdc.checkbox.MDCCheckbox(
      document.getElementById('deletePrevImageCheckbox'));
  const deletePrevImageFormField = new mdc.formField.MDCFormField(
      document.getElementById('deletePrevImageFormField'));
  deletePrevImageFormField.input = deletePrevImageCheckbox;
  const publicitySwitchControl = new mdc.switchControl.MDCSwitch(
      document.getElementById('publicitySwitch'));
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
  document.getElementById('submitBtn').disabled = false;
}

/**
 * This function writes in the hidden form inputs the
 * data of the newly chosen location for the placeguide.
 */
function updateLocation(position, placeId) {
  if (placeId !== null) {
    document.getElementById(
        'placeId').value = placeId;
  } else {
    document.getElementById(
        'placeId').value = '';
  }
  document.getElementById(
      'latitude').value = position.lat();
  document.getElementById(
      'longitude').value = position.lng();
}

/**
 * Will fill the form if query string exists. Query string exists only when user wants
 * to edit a place guide.
 */
function fillFormWithPlaceGuideToEdit() {
  if (window.location.search != '') {
    const GET = UrlQueryUtils.getParamsFromQueryString();
    var url = new URL('/place-guide-data', document.URL);
    url.searchParams.append('placeGuideType', "PLACE_GUIDE_WITH_ID");
    url.searchParams.append('placeGuideId', GET['placeGuideId']);
    return fetch(url)
        .catch(error => {
          console.log("PlaceGuideServlet: failed to fetch: " + error);
          alert("Failed to load the data of the guide to edit");
        })
        .then(response => response.json())
        .catch(error => {
          console.log('fillFormWithPlaceGuideToEdit: failed to convert response to JSON'
              + error);
          alert("Failed to process the data of the guide to edit");
        })
        .then(placeGuideWithCreatorPair => {
          console.log("fetching finished");
          enableSubmission();
          document.getElementById('audioKey').required = false;
        });
  }

  //   if (GET['placeId'] !== 'null') {
  //     setFormInputValueOrEmpty(
  //         document.getElementById('placeId'),
  //         GET['placeId']);
  //   }
  //   setFormInputValueOrEmpty(
  //       new mdc.textField.MDCTextField(document.getElementById('nameInput')),
  //       GET['name']);
  //   document.getElementById('audioPlayer').src = PlaceGuideOnList.getBlobSrc(GET['audioKey']);
  //   if (GET['imageKey'] !== 'undefined') {
  //     document.getElementById('imagePreview').style.display = 'block';
  //     document.getElementById('imagePreview').src =
  //       PlaceGuideOnList.getBlobSrc(GET['imageKey']);
  //     document.getElementById('no-img-icon')
  //         .style.display = 'none';
  //     document.getElementById('clear-img-icon').style.display = 'block';
  //     activateRemoveImageFeature('clear-img-icon', true);
  //   } else {
  //     activateRemoveImageFeature('clear-img-icon', false);
  //   }
  //   if (GET['description'] !== 'undefined') {
  //     setFormInputValueOrEmpty(
  //         new mdc.textField.MDCTextField(document.getElementById('descriptionInput')),
  //         GET['description']);
  //   }
  //   setFormInputValueOrEmpty(
  //       document.getElementById('latitude'),
  //       GET['latitude']);
  //   setFormInputValueOrEmpty(
  //       document.getElementById('longitude'),
  //       GET['longitude']);
  //   setFormInputValueOrEmpty(
  //       new mdc.textField.MDCTextField(document.getElementById('lengthInput')),
  //       GET['audioLength']);
  //   const publicitySwitchControl =
  //   new mdc.switchControl.MDCSwitch(document.getElementById('publicitySwitch'));
  //   if (GET['isPublic'] === 'true') {
  //     publicitySwitchControl.checked = true;
  //   } else {
  //     publicitySwitchControl.checked = false;
  //   }
  // } else {
  //   activateRemoveImageFeature('clear-img-icon', false);
  // }
}
