/**
 * Handles setting up the create place guide form whenever the page is loaded.
 */
function setUpCreatePlaceGuideForm() {
  addBlobstoreUploadUrlToForm("CREATE_PLACE_GUIDE_FORM", "formId");
  activatePreviewFeature();
}

function activatePreviewFeature() {

  const imageInput = document.getElementById("imageKey");
  const imagePreview = document.getElementById("imagePreview");
  // Previewing files uploaded by user.
  imageInput.addEventListener("change", function() {
    const file = this.files[0];

    if (file) {
      const reader = new FileReader();
      imagePreview.style.display = "block";

      reader.addEventListener("load", function() {
        imagePreview.setAttribute("src", this.result);
      });

      reader.readAsDataURL(file);
    }
  });
  const audioInput = document.getElementById("audioKey");
  const audioPlayer = document.getElementById("audioPlayer");
  audioInput.addEventListener("change", function() {
    const file = this.files[0];
    if (file) {
      const reader = new FileReader();

      reader.addEventListener("load", function() {
        audioPlayer.setAttribute('src', this.result);
      });

      reader.readAsDataURL(file);
    }
  });
}

// Just a test by fetching actual place guides' data from database 
// to see if image and audio previewing also works with files from blobstore.
function testExistingPlaceGuide() {
  getFetchedList().then(placeGuides => {
    fillFormWithPlaceGuideData(placeGuides)
  });
}

async function getFetchedList() {
  return fetch('/place-guide-data?placeGuideType=ALL_PUBLIC', {method: 'GET'})
  .then((response) => {
      return response.json();
  });
}

function testGivenCoordinate() {
  document.getElementById("latitude").setAttribute("value", 3.14);
  document.getElementById("longitude").setAttribute("value", 2.56);
}

function fillFormWithPlaceGuideData(placeGuides) {
  if (placeGuides === undefined || placeGuides.length == 0) {
    console.log("place guide does not exist yet.");
  } else {
    const placeGuide = placeGuides[0];
    setFormInputValue(document.getElementById("id"), placeGuide.id);
    setFormInputValue(document.getElementById("name"), placeGuide.name);
    setBlobKeySrcToElement(placeGuide.audioKey, "audioPlayer", false);
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
      setBlobKeySrcToElement(placeGuide.imageKey, "imagePreview", true);
    }
  }
}

function setBlobKeySrcToElement(blobKey, elementId, displayBlock) {
  const element = document.getElementById(elementId);
  if (displayBlock) {
    element.style.display = "block";
  }
  const src = new URL("/serve-blob", document.URL);
  src.searchParams.append('blob-key', blobKey);
  element.setAttribute("src", src);
}