const DUMMY_DATA_FOR_PLACE_NAME = 'placeName';

/**
 * Handles setting up the create place guide form whenever the page is loaded.
 */
function setUpCreatePlaceGuideForm() {
  addBlobstoreUploadUrlToForm(
      'CREATE_PLACE_GUIDE_FORM', 'createPlaceGuideForm');
  activatePreviewFeature();
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
  setFormInputValue(document.getElementById('name'), placeGuide.name);
  setBlobKeySrcToElement(
      placeGuide.audioKey, 'audioPlayer', false);
  if (placeGuide.isPublic) {
    document.getElementById('isPublic').value = 'public';
  } else {
    document.getElementById('isPublic').value = 'private';
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
      document.getElementById('description'),
      placeGuide.description);
  if (placeGuide.imageKey != undefined) {
    setBlobKeySrcToElement(
        placeGuide.imageKey,
        'imagePreview', true);
  }
  setFormInputValue(document.getElementById('placeName'),
      DUMMY_DATA_FOR_PLACE_NAME);
}