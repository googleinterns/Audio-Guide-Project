/**
 * Handles setting up the create place guide form whenever the page is loaded.
 */
function setUpCreatePlaceGuideForm() {
  addBlobstoreUploadUrlToForm("CREATE_PLACE_GUIDE_FORM", "createPlaceGuideForm");
  activatePreviewFeature();
  fillFormWithPlaceGuideToEdit();
}

function activatePreviewFeature() {
  setSrcToElementOnChangeEvent("imageKey", "imagePreview", true);
  setSrcToElementOnChangeEvent("audioKey", "audioPlayer", false);
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

/**
 * Will fill the form if query string exists. Query string exists only when user wants 
 * to edit a place guide.
 */
function fillFormWithPlaceGuideToEdit() {
  if (window.location.search != "") {
    var GET = {};
    var queryString = decodeURI(window.location.search.replace(/^\?/, ''));
    queryString.split(/\&/).forEach(function(keyValuePair) {
        var paramName = keyValuePair.replace(/=.*$/, "");
        var paramValue = keyValuePair.replace(/^[^=]*\=/, "");
        GET[paramName] = paramValue;
    });
    document.getElementById("id").value = GET["placeGuideId"];
    document.getElementById("placeId").value = GET["placeId"];
    document.getElementById("name").value = GET["name"];
    document.getElementById("audioPlayer").src = getBlobSrc(GET["audioKey"]);
    document.getElementById("imagePreview").src = getBlobSrc(GET["imageKey"]);
    document.getElementById("imagePreview").style.display = "block";
    document.getElementById("description").value = GET["description"];
    document.getElementById("length").value = GET["length"];
    document.getElementById("isPublic").value = GET["isPublic"];
    document.getElementById("placeName").value = GET["placeName"];
    document.getElementById("latitude").value = GET["latitude"];
    document.getElementById("longitude").value = GET["longitude"];
  }
}