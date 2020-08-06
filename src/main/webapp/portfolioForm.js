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


/**
 * Handles setting up the portfolio form whenever the page is loaded. 
 */
function setUpPortfolioForm() {
    addBlobstoreUploadUrlToForm();
    fillFormInputsWithUserData();
}

/**
 * Sets th action of the form to the url to which blobs can be uploaded.
 */
function addBlobstoreUploadUrlToForm() {
  getBlobstoreUploadUrlFromServlet().then (uploadUrl => {
    setFormActionUrl(uploadUrl);
  });
}

/**
 * Fetches a url to which the files can be uploaded, relying on Blobstore API.
 */
function getBlobstoreUploadUrlFromServlet() {
    return fetch ('/blobstore-upload-portfolio')
    .catch (error => console.log("blobstore-upload-portfolio: failed to fetch: " + error))
    .then (uploadUrl => uploadUrl.text())
    .catch (error => console.log('blobstore-upload-portfolio: failed to convert to text: ' + error))
    .then (uploadUrl => {
    return uploadUrl;
    });
}

/** 
 * Sets the destination url of the post method for the portfolio form to uploadUrl. 
 */
function setFormActionUrl(uploadUrl) {
  var form = document.getElementById("portfolioForm");
  form.action = uploadUrl;
}

/** 
 * Gets the currently logged in user's data from the database
 * and fill's the form inputs with this data.
 */
function fillFormInputsWithUserData() {
    getUserDataFromServlet().then (user => {
        setFormInputValue(document.getElementById("name"), user.name);
        setFormInputValue(document.getElementById("selfIntroduction"), user.selfIntroduction);
        if (user.publicPortfolio) {
            document.getElementById("publicPortfolio").value = "public";
        } else {
            document.getElementById("publicPortfolio").value = "private";
        }
    });
}

/**
 * Sets one form input's value to the given value.
 */
function setFormInputValue(input, value) {
    if (value == undefined) {
        input.value = "";
    } else {
        input.value = value;
    }
}

/** 
 * Gets the currently logged in user's data from the servlet.
 */
function getUserDataFromServlet() {
   return fetch('/user-servlet')
        .catch (error => console.log("user-servlet: failed to fetch: " + error))
        .then (response => response.json())
        .catch (error => console.log('fillFormInputsWithData: failed to convert to json: ' + error))
        .then (response => {
            return response;
        });
}