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
 * Sets the destination url of the post method for the form to uploadUrl.
 */
function setFormActionUrl(uploadUrl, formId) {
  var form = document.getElementById(formId);
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