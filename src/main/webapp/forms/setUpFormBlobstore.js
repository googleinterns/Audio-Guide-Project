/**
 * Sets the action of the form to the url to which blobs can be uploaded.
 */
function addBlobstoreUploadUrlToForm(formType, formId) {
  getBlobstoreUploadUrlFromServlet(formType).then((uploadUrl) => {
    setFormActionUrl(uploadUrl, formId);
  });
}

/**
 * Fetches a url to which the files can be uploaded, relying on Blobstore API.
 */
function getBlobstoreUploadUrlFromServlet(formType) {
  const url = new URL('/blobstore-upload-url', document.URL);
  url.searchParams.append('formType', formType);
  return fetch(url)
      .catch((error) =>
        console.log(
            'getBlobstoreUploadUrlFromServlet: failed to fetch: ' + error))
      .then((uploadUrl) => uploadUrl.text())
      .catch((error) =>
        console.log(
            'getBlobstoreUploadUrlFromServlet: failed to convert to text: ' +
              error))
      .then((uploadUrl) => {
        return uploadUrl;
      });
}

/**
 * Sets the destination url of the post method
 * for the createPlaceGuide form to uploadUrl.
 */
function setFormActionUrl(uploadUrl, formId) {
  const form = document.getElementById(formId);
  form.action = uploadUrl;
}

function setBlobKeySrcToElement(blobKey, previewId, displayBlock) {
  const preview = document.getElementById(previewId);
  if (displayBlock) {
    preview.style.display = 'block';
  }
  const src = new URL('/serve-blob', document.URL);
  src.searchParams.append('blob-key', blobKey);
  preview.setAttribute("src", src);
}

function setBlobKeyBackgroundToElement(blobKey, element) {
  const htmlElement = document.getElementById(element);
  const src = new URL("/serve-blob", document.URL);
  src.searchParams.append('blob-key', blobKey);
  htmlElement.style.backgroundImage = src;
}
