/**
 * Handles setting up the create place guide form whenever the page is loaded.
 */
function setUpCreatePlaceGuideForm() {
  addBlobstoreUploadUrlToForm("CREATE_PLACE_GUIDE_FORM", "createPlaceGuideForm");
  fillCreatePlaceGuideFormWithPlaceGuideData();
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
 * Sets the destination url of the post method for the portfolio form to uploadUrl.
 */
function setFormActionUrl(uploadUrl, formId) {
  var form = document.getElementById(formId);
  form.action = uploadUrl;
}

/**
 * Gets the currently logged in user's data from the database
 * and fill's the form inputs with this data.
 */
function fillPortfolioFormWithUserData() {
  getUserDataFromServlet().then(user => {
    setFormInputValue(document.getElementById("name"), user.name);
    setFormInputValue(document.getElementById("selfIntroduction"), user.selfIntroduction);
    if (user.publicPortfolio) {
      document.getElementById("publicPortfolio").value = "public";
    } else {
      document.getElementById("publicPortfolio").value = "private";
    }
    if (user.imgKey != undefined) {
      var img = createImgElement(user.imgKey);
      document.getElementById("portfolioForm").appendChild(img);
    }
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


/**
 * Creates an img element with the given blobKey,
 * relying on the blobstore API for serving the blob.
 */
function createImgElement(srcBlobKey) {
  var url = new URL("/serve-blob", document.URL);
  url.searchParams.append('blob-key', srcBlobKey);
  var img = document.createElement("img");
  img.src = url;
  return img;
}

/**
 * Gets the currently logged in user's data from the servlet.
 */
function getUserDataFromServlet() {
  return fetch('/user-data-servlet')
      .catch(error => console.log("user-servlet: failed to fetch: " + error))
      .then(response => response.json())
      .catch(error => console.log('fillFormInputsWithData: failed to convert to json: ' + error))
      .then(response => {
        return response;
      });
}