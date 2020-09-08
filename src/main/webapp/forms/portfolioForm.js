/**
 * Handles setting up the portfolio form whenever the page is loaded.
 */
function setUpPortfolioForm() {
  addBlobstoreUploadUrlToForm("PORTFOLIO_FORM", "portfolioForm");
  styleInputs();
  activatePreviewFeature();
  fillPortfolioFormWithUserData();
}

/**
 * This function initialises the components 
 * managed by the Material Design library.
 */
function styleInputs() {
  const nameInput = new mdc.textField.MDCTextField(
      document.getElementById('nameInput'));
  const selfIntroductionInput = new mdc.textField.MDCTextField(
      document.getElementById('selfIntroductionInput'));
  const submitButtonRipple = new mdc.ripple.MDCRipple(
      document.getElementById("submitBtn"));
  const chooseFileButtonRipple = new mdc.ripple.MDCRipple(
      document.getElementById("chooseFileBtn"));
  const deletePrevImageCheckbox = new mdc.checkbox.MDCCheckbox(
      document.getElementById('deletePrevImageCheckbox'));
  const deletePrevImageFormField = new mdc.formField.MDCFormField(
      document.getElementById('deletePrevImageFormField'));
  deletePrevImageFormField.input = deletePrevImageCheckbox;
  const switchControl = new mdc.switchControl.MDCSwitch(
      document.getElementById("publicitySwitch"));
}

/**
 * This function enables the preview of the profile picture.
 */
function activatePreviewFeature() {
  handleFileInputChangeEvent(
      'imageKey', showImagePreview, removeImagePreview);
}

/**
 * Gets the currently logged in user's data from the database
 * and fill's the form inputs with this data.
 */
function fillPortfolioFormWithUserData() {
  getUserDataFromServlet().then((user) => {
    const nameInput = new mdc.textField.MDCTextField(
        document.getElementById('nameInput'));
    const selfIntroductionInput = new mdc.textField.MDCTextField(
        document.getElementById('selfIntroductionInput'));
    setFormInputValueOrEmpty(nameInput, user.name);
    setFormInputValueOrEmpty(selfIntroductionInput,
        user.selfIntroduction);
    const switchControl = new mdc.switchControl.MDCSwitch(
        document.querySelector('.mdc-switch'));
    if (user.publicPortfolio) {
      switchControl.checked = true;
    } else {
      switchControl.checked = false;
    }
    if (user.imgKey != undefined) {
      setBlobKeySrcToElement(user.imgKey, "imagePreview", true);
      document.getElementById("no-img-icon").style.display = "none";
      document.getElementById("clear-img-icon").style.display = "block";
      activateRemoveImageFeature("clear-img-icon", true);
    } else {
      activateRemoveImageFeature("clear-img-icon", false);
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

