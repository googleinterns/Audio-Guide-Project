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