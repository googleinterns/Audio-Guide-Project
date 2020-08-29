/**
 * Handles setting up the portfolio form whenever the page is loaded.
 */
function setUpPortfolioForm() {
  addBlobstoreUploadUrlToForm("PORTFOLIO_FORM", "portfolioForm");
  activatePreviewFeature();
  fillPortfolioFormWithUserData();
}

/**
 * Gets the currently logged in user's data from the database
 * and fill's the form inputs with this data.
 */
function fillPortfolioFormWithUserData() {
  getUserDataFromServlet().then((user) => {
    setFormInputValue(document.getElementById('name'), user.name);
    setFormInputValue(document.getElementById('selfIntroduction'),
        user.selfIntroduction);
    if (user.publicPortfolio) {
      document.getElementById('publicPortfolio').value = 'public';
    } else {
      document.getElementById('publicPortfolio').value = 'private';
    }
    if (user.imgKey != undefined) {
      const img = createImgElement(user.imgKey);
      document.getElementById('portfolioForm').appendChild(img);
    }
  });
}

/**
 * Creates an img element with the given blobKey,
 * relying on the blobstore API for serving the blob.
 */
function createImgElement(srcBlobKey) {
  const url = new URL('/serve-blob', document.URL);
  url.searchParams.append('blob-key', srcBlobKey);
  const img = document.createElement('img');
  img.src = url;
  return img;
}

/**
 * Gets the currently logged in user's data from the servlet.
 */
function getUserDataFromServlet() {
  return fetch('/user-data-servlet')
      .catch((error) => console.log('user-servlet: failed to fetch: ' + error))
      .then((response) => response.json())
      .catch((error) =>
        console.log(
            'fillFormInputsWithData: failed to convert to json: ' + error))
      .then((response) => {
        return response;
      });
}
