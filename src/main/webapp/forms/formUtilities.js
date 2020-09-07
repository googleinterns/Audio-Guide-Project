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

function setSrcToElementOnChangeEvent(elementId, previewId, displayBlock) {
  const element = document.getElementById(elementId);
  const preview = document.getElementById(previewId);
  element.addEventListener("change", function() {
    const file = this.files[0];

    if (file) {
      const reader = new FileReader();
      if (displayBlock) {
        preview.style.display = "block"; 
      }

      reader.addEventListener("load", function() {
        preview.setAttribute("src", this.result);
      });

      reader.readAsDataURL(file);
    } else {
        preview.style.display = "none";
    }
  });
}

function handleIconVisibilityOnFileChangeEvent(elementId, icon, showWhenFilePresent) {
  const element = document.getElementById(elementId);
  element.addEventListener("change", function() {
    const file = this.files[0];

    if (file) {
      if (showWhenFilePresent) {
        icon.style.display = "block";
      } else {
        icon.style.display = "none";
      }
    } else {
      if (showWhenFilePresent) {
        icon.style.display = "none";
      } else {
        icon.style.display = "block";
      }
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
        document.getElementById("imagePreview").style.display="none";
        document.getElementById("imageKey").value = "";
    });
}