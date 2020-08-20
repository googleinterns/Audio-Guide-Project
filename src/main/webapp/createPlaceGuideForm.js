/**
 * Handles setting up the create place guide form whenever the page is loaded.
 */
function setUpCreatePlaceGuideForm() {
  addBlobstoreUploadUrlToForm("CREATE_PLACE_GUIDE_FORM", "formId");
  activatePreviewFeature();
}

function activatePreviewFeature() {
  setSrcToElementOnChangeEvent("imageKey", "imagePreview", true);
  setSrcToElementOnChangeEvent("audioKey", "audioPlayer", false);
}

function setSrcToElementOnChangeEvent(elementId, previewId, displayBlock) {
  const element = document.getElementById(elementId);
  const preview = document.getElementById(previewId);
  element.addEventListener("change", function() {
    const file = this.files[0];
    console.log(this.files);

    if (file) {
      const reader = new FileReader();
      if (displayBlock) {
        preview.style.display = "block"; 
      }

      reader.addEventListener("load", function() {
        preview.setAttribute("src", this.result);
      });

      reader.readAsDataURL(file);
    }
  });
}

// Just a test by fetching actual place guides' data from database 
// to see if image and audio previewing also works with files from blobstore.
function testExistingPlaceGuide() {
  getFetchedList().then(placeGuides => {
    if (placeGuides === undefined || placeGuides.length == 0) {
      console.log("place guide does not exist yet.");
    } else {
      fillFormWithPlaceGuideData(placeGuides[0]); 
    }
  });
}

// For testing.
async function getFetchedList() {
  return fetch('/place-guide-data?placeGuideType=ALL_PUBLIC', {method: 'GET'})
  .then((response) => {
      return response.json();
  });
}

// For testing.
function testGivenCoordinate() {
  document.getElementById("latitude").setAttribute("value", 3.14);
  document.getElementById("longitude").setAttribute("value", 2.56);
}

function fillFormWithPlaceGuideData(placeGuide) {
  setFormInputValue(document.getElementById("id"), placeGuide.id);
  setFormInputValue(document.getElementById("name"), placeGuide.name);
  setBlobKeySrcToElement("audioKey", placeGuide.audioKey, "audioPlayer", false);
  if (placeGuide.isPublic) {
    document.getElementById("isPublic").value = "public";
  } else {
    document.getElementById("isPublic").value = "private";
  }
  setFormInputValue(document.getElementById("latitude"), placeGuide.coordinate.latitude);
  setFormInputValue(document.getElementById("longitude"), placeGuide.coordinate.longitude);
  setFormInputValue(document.getElementById("length"), placeGuide.length);
  setFormInputValue(document.getElementById("description"), placeGuide.description);
  if (placeGuide.imageKey != undefined) {
    setBlobKeySrcToElement("imageKey", placeGuide.imageKey, "imagePreview", true);
  }
  setFormInputValue(document.getElementById("placeName"), placeGuide.placeName);
}

function setBlobKeySrcToElement(inputId, blobKey, previewId, displayBlock) {
  const input = document.getElementById(inputId);
  const preview = document.getElementById(previewId);
  if (displayBlock) {
    preview.style.display = "block";
  }
  const src = new URL("/serve-blob", document.URL);
  src.searchParams.append('blob-key', blobKey);
  preview.setAttribute("src", src);
}