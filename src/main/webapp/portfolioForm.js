function addBlobstoreUploadurlToForm() {
  fetch('/blobstore-upload-portfolio')
      .catch(error => console.log("blobstore-upload-portfolio: failed to fetch: " + error))
      .then(uploadUrl => uploadUrl.text())
      .catch(error => console.log('blobstore-upload-portfolio: failed to convert to text: ' + error))
      .then(uploadUrl => {
        console.log("the upload url is: " + uploadUrl);
        setFormActionUrl(uploadUrl);
      });
}

function setFormActionUrl(uploadUrl){
  var form = Document().getElementById("portfolioForm");
  form.action = uploadUrl;
}