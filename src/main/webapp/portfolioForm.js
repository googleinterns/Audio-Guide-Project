// Copyright 2020 Google LLC

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at

//     https://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

function addBlobstoreUploadUrlToForm() {
  fetch('/blobstore-upload-portfolio')
      .catch(error => console.log("blobstore-upload-portfolio: failed to fetch: " + error))
      .then(uploadUrl => uploadUrl.text())
      .catch(error => console.log('blobstore-upload-portfolio: failed to convert to text: ' + error))
      .then(uploadUrl => {
        console.log("the upload url is: " + uploadUrl);
        setFormActionUrl(uploadUrl);
      });
}

/** Set the destination url of the post method for the portfolio form t */
function setFormActionUrl(uploadUrl){
  var form = document.getElementById("portfolioForm");
  form.action = uploadUrl;
}