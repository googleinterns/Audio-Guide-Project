class Modal {
  static MODAL_ELEMENT = document.getElementById("myModal");
  static CLOSE_BTN = document.getElementById("closeModalBtn");
  static MODAL_TEXT = document.getElementById("modalText");
  static hidden = true;
  static timeOutId = undefined;
  static hasCloseButtonListener = false;

  static show(text, timeLimit) {
    Modal.destroy();
    Modal.hidden = false;
    Modal.setText(text);
    Modal.MODAL_ELEMENT.style.display = "block";
    Modal.closeModalOnButtonClick();
    if (timeLimit !== undefined) {
      Modal.timeOutId = setTimeout(function() {
        Modal.hide();
      }, timeLimit);
    }
  }

  static setText(text) {
    Modal.MODAL_TEXT.innerText = text;
  }

  static closeModalOnButtonClick() {
    if (!Modal.hasCloseButtonListener) {
      Modal.CLOSE_BTN.addEventListener("click", Modal.hide);
      Modal.hasCloseButtonListener = true;
    }
  }

  static hide() {
    if (!Modal.hidden) {
      Modal.hidden = true;
      Modal.MODAL_ELEMENT.style.display = "none";
    }
  }

  static destroy() {
    if (Modal.timeOutId !== undefined) {
      clearTimeout(Modal.timeOutId);
      Modal.timeOutId = undefined;
    }
  }
}