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


/*
 * Fetches a url to which the files can be uploaded, relying on blobstore.
 * Sets th action of the form to the fetche url.
 */
function addBlobstoreUploadUrlToForm() {
  fetch ('/blobstore-upload-portfolio')
      .catch (error => console.log("blobstore-upload-portfolio: failed to fetch: " + error))
      .then (uploadUrl => uploadUrl.text())
      .catch (error => console.log('blobstore-upload-portfolio: failed to convert to text: ' + error))
      .then (uploadUrl => {
        setFormActionUrl(uploadUrl);
      });
}

/** Set the destination url of the post method for the portfolio form to uploadUrl. */
function setFormActionUrl(uploadUrl) {
  var form = document.getElementById("portfolioForm");
  form.action = uploadUrl;
}

function fillFormInputsWithUserData() {
    getUserDataFromServlet().then (response => {
        document.getElementById("name").value = response.name;
        document.getElementById("selfIntroduction").value = response.selfIntroduction;
        console.log(response.selfIntroduction);
        console.log(response.publicPortfolio);
        if (response.publicPortfolio) {
            document.getElementById("publicPortfolio").value = "public";
        } else {
            document.getElementById("publicPortfolio").value = "private";
        }
    });
}

function getUserDataFromServlet() {
   return fetch('/user-servlet')
        .catch (error => console.log("user-servlet: failed to fetch: " + error))
        .then (response => response.json())
        .catch (error => console.log('fillFormInputsWithData: failed to convert to json: ' + error))
        .then (response => {
            return response;
        });
}