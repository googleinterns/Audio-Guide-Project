const MENU_AND_MARGIN_HEIGHT = 48;
const LIST_WIDTH = 375;
const MENU_AND_USER_PORTFOLIO_HEIGHT = 520;

function setContentHeight() {
  console.log(window.innerHeight);
  const availableHeight = window.innerHeight - MENU_AND_MARGIN_HEIGHT;
  console.log("available=" +availableHeight);
  document.getElementsByClassName("content-container")[0].style.height =
      availableHeight+ 'px';
}

function setMapWidth() {
  const availableWidth = window.innerWidth - LIST_WIDTH;
  document.getElementById('mapDisplayer').style.width = availableWidth + 'px';
}

function setListHeight() {
  const availableHeight = window.innerHeight - MENU_AND_USER_PORTFOLIO_HEIGHT;
  document.getElementById('listPlaceGuideDisplayer').style.height =
      availableHeight+ 'px';
}