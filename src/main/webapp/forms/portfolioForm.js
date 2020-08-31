/**
 * Handles setting up the portfolio form whenever the page is loaded.
 */
function setUpPortfolioForm() {
  addBlobstoreUploadUrlToForm("PORTFOLIO_FORM", "portfolioForm");
  activatePreviewFeature();
  fillPortfolioFormWithUserData();
}

function activatePreviewFeature() {
  setSrcToElementOnChangeEvent("imgKey", "imagePreview", true);
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
      setBlobKeySrcToElement(user.imgKey, "imagePreview", true);
    }
  });
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
