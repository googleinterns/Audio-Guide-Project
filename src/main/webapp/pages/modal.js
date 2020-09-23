class Modal {
  constructor() {
    this.hidden = true;
    this.modal = document.getElementById("myModal");
    console.log("constructed");
  }

  show(text, timeLimit) {
    this.hidden = false;
    this.modal.style.display = "block";
    this.setModalText(text);
    this.closeModalOnButtonClick();
    if (timeLimit !== undefined) {
      const thisModal = this;
      setTimeout(function() {
        thisModal.hide();
      }, timeLimit);
    }
  }

  setModalText(text) {
    const modalText = document.getElementById("modalText");
    modalText.innerText = text;
  }

  closeModalOnButtonClick() {
    const btn = document.getElementById("closeModalBtn");
    const thisModal = this;
    btn.addEventListener("click", function() {
      thisModal.hide();
    });
  }

  hide() {
    if (!this.hidden) {
      this.hidden = true;
      this.modal.style.display = "none";
    }
  }
}