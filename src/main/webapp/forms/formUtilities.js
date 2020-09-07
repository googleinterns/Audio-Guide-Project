/**
 * Sets one form input's value to the given value.
 */
function setFormInputValue(input, value) {
  if (value === undefined) {
    input.value = '';
  } else {
    input.value = value;
  }
}

function handleFileInputChangeEvent(fileInputName, fileUploadedFunction, fileRemovedFunction) {
  const fileInput = document.getElementById(fileInputName);
  fileInput.addEventListener("change", function() {
    const file = this.files[0];
    if (file) {
      const reader = new FileReader();
      reader.addEventListener("load", function() {
        fileUploadedFunction(this.result);
      });
      reader.readAsDataURL(file);
    } else {
        fileRemovedFunction();
    }
  });
}

function activateRemoveImageFeature(hasPreviousImage) {
    document.getElementById("clear-img-icon").addEventListener("click", function() {
        if (hasPreviousImage) {
            const deletePrevImageCheckbox = new mdc.checkbox.MDCCheckbox(document.getElementById('deletePrevImageCheckbox'));
            const deletePrevImageFormField = new mdc.formField.MDCFormField(document.getElementById('deletePrevImageFormField'));
            deletePrevImageFormField.input = deletePrevImageCheckbox;
            deletePrevImageCheckbox.checked = true;
            hasPreviousImage = false;
        }
        removeImagePreview();
        document.getElementById("imageKey").value = "";
    });
}

function removeImagePreview() {
    document.getElementById("imagePreview").style.display="none";
    document.getElementById("no-img-icon").style.display="block";
    document.getElementById("clear-img-icon").style.display="none";
}

function showImagePreview(imgSrc) {
    let previewElement = document.getElementById("imagePreview");
    previewElement.setAttribute("src", imgSrc);
    previewElement.style.display="block";
    document.getElementById("no-img-icon").style.display="none";
    document.getElementById("clear-img-icon").style.display="block";
}

function setAudioPreviewSource(src) {
    document.getElementById('audioPlayer').setAttribute('src', src);
}