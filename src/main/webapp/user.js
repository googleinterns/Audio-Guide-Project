/**
 * This file handles user authentication and saving and updating the user's data in the database. 
 */


 /**
 * This function handles everything what needs to be done when the page loads. 
 *
 * The user is authenticated first. 
 * They can only access the page after logging in. 
 * Otherwise, the site will prompt the user to Google login page.
 *
 * It is checked if the user logs in for the first time to the website. 
 * If yes, their data(id and email) will be atutomatically saved in the database.
 *
 * After having their data saved if needed, the user will see a form for editing portfolio data.
 * The portfolio shows the user's previously saved data, if any.
 *
 * The user is also provided a href link to log out and switch account.
 */
function setUpUserAndForm(){
    authenticateUser().then(userAuthenticationStatus => {
        if (!userAuthenticationStatus.isLoggedIn) {
            location.replace(userAuthenticationStatus.loginUrl);
        } else {
            addLinktoLogoutButton(userAuthenticationStatus.logoutUrl);
            userPresentInDatabase().then(present => {
                if (!present) {
                    saveUserInDatabase().
                        then(setUpPortfolioForm())
                } else {
                    setUpPortfolioForm();
                }
            });
        }
    });
}

/**
* Check if the user is logged in.
*/
function authenticateUser() {
  const currentWindowLocation = window.location.href;
  const currentUrl = new URL(currentWindowLocation);
  return queryAuthenticationServer(currentUrl).then((userAuthenticationStatus) => {
    return userAuthenticationStatus;
  });
}

/**
* Send GET request to UserAuthenticationServlet with the current page url
* as the query parameters.
*/
function queryAuthenticationServer(currentUrl) {
  const requestUrl = new URL('/user-authentication', currentUrl);
  const queryParams = new QueryParams(currentUrl);
  requestUrl.search = new URLSearchParams(queryParams).toString();

  return fetch(requestUrl, {method: 'GET'})
    .catch(error => console.log(error))
    .then((response) => {
      return response.json();
    });
}

class QueryParams{
  constructor(currentUrl) {
    this.currentUrl = currentUrl;
  }
}

function addLinktoLogoutButton(logoutUrl) {
  const logoutButton = document.getElementById('logout');
  logoutButton.addEventListener('click', () => {
  window.location.replace(logoutUrl);
  });
}

/**
 * Checks if the user is present in the database. If not, saves them.
 */
function saveUserInDatabase(){
    fetch('/new-user-servlet', {method: 'POST'})
            .catch(error => "user-servlet: failed to post new user: " + error); 
}

/** 
 * Checks if the currently logged in user is already saved in the database.
 */
function userPresentInDatabase() {
   return fetch('/new-user-servlet')
        .catch (error => console.log("user-servlet: failed to fetch: " + error))
        .then (response => response.json())
        .catch (error => console.log('fillFormInputsWithData: failed to convert to json: ' + error))
        .then (response => {
            return response;
        });
}

/**
 * Handles setting up the portfolio form whenever the page is loaded. 
 */
function setUpPortfolioForm() {
    addBlobstoreUploadUrlToForm("PORTFOLIO_FORM");
    fillFormWithUserData();
}

/**
 * Sets th action of the form to the url to which blobs can be uploaded.
 */
function addBlobstoreUploadUrlToForm(formType) {
  getBlobstoreUploadUrlFromServlet(formType).then (uploadUrl => {
    setFormActionUrl(uploadUrl);
  });
}

/**
 * Fetches a url to which the files can be uploaded, relying on Blobstore API.
 */
function getBlobstoreUploadUrlFromServlet(formType) {
    var url = new URL("/blobstore-upload-url", document.URL);
    url.searchParams.append('formType', formType)
    return fetch (url)
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
function fillFormWithUserData() {
    getUserDataFromServlet().then (user => {
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
    if (value == undefined) {
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
    url.searchParams.append('blob-key', srcBlobKey)
    var img = document.createElement("img");
    img.src = url;
    return img;
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