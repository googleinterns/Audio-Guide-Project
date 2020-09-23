class Modal {
  static show(text, timeLimit) {
    const modal = document.getElementById("myModal");
    modal.style.display = "block";
    Modal.setModalText()
    Modal.closeModalOnButtonClick();
    Modal.closeModalOnWindowClick();
    if (timeLimit !== undefined) {
      setTimeout(Modal.hide, timeLimit);
    }
  }

  static setModalText(text) {
    const modalText = document.getElementById("modalText");
    modalText.innerText = text;
  }

  static closeModalOnButtonClick() {
    const btn = document.getElementById("closeModalBtn");
    btn.addEventListener("click", function() {
      Modal.hide();
    });
  }

  static closeModalOnWindowClick() {
    const modal = document.getElementById("myModal");
    window.addEventListener("click", function(event) {
      if (event.target === modal) {
        Modal.hide();
      }
    });
  }

  static hide() {
    const modal = document.getElementById("myModal");
    modal.style.display = "none";
  }
}