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
    console.log(file);

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
        audioPlayer.play();

      });

      reader.readAsDataURL(file);
      console.log(reader);
    }
  });
}

/**
 * Sets the action of the form to the url to which blobs can be uploaded.
 */
function addBlobstoreUploadUrlToForm(formType, formId) {
  getBlobstoreUploadUrlFromServlet(formType).then(uploadUrl => {
    setFormActionUrl(uploadUrl, formId);
  });
}

/**
 * Fetches a url to which the files can be uploaded, relying on Blobstore API.
 */
function getBlobstoreUploadUrlFromServlet(formType) {
  var url = new URL("/blobstore-upload-url", document.URL);
  url.searchParams.append('formType', formType)
  return fetch(url)
      .catch(error => console.log("getBlobstoreUploadUrlFromServlet: failed to fetch: " + error))
      .then(uploadUrl => uploadUrl.text())
      .catch(error =>
          console.log('getBlobstoreUploadUrlFromServlet: failed to convert to text: ' + error))
      .then(uploadUrl => {
        return uploadUrl;
      });
}

/**
 * Sets the destination url of the post method for the createPlaceGuide form to uploadUrl.
 */
function setFormActionUrl(uploadUrl, formId) {
  var form = document.getElementById("formId");
  form.action = uploadUrl;
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

/**
 * Sets one form input's value to the given value.
 */
function setFormInputValue(input, value) {
  if (value === undefined) {
    input.value = "";
  } else {
    input.value = value;
  }
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
    const audioSrc = new URL("/serve-blob", document.URL);
    audioSrc.searchParams.append('blob-key', placeGuide.audioKey);
    const audioPlayer = document.getElementById("audioPlayer");
    audioPlayer.setAttribute("src", audioSrc);
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
      const imagePreview = document.getElementById("imagePreview");
      imagePreview.style.display = "block";
      const imageSrc = new URL("/serve-blob", document.URL);
      imageSrc.searchParams.append('blob-key', placeGuide.imageKey);
      imagePreview.setAttribute("src", imageSrc);
    }
  }
}