const MENU_AND_MARGIN_HEIGHT = 48;

function setContentHeight() {
  console.log(window.innerHeight);
  const availableHeight = window.innerHeight - MENU_AND_MARGIN_HEIGHT;
  console.log("available=" +availableHeight);
  document.getElementsByClassName("content-container")[0].style.height =
      availableHeight+ 'px';
}