class Modal {
  static show(text) {
    const modal = document.getElementById("myModal");
    modal.style.display = "block";
    const modalText = document.getElementById("modalText");
    modalText.innerText = text;
    var btn = document.getElementById("closeModalBtn");
    btn.addEventListener("click", function() {
      Modal.hide();
    });
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