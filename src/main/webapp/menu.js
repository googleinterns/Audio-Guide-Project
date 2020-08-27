const tabBar = new mdc.tabBar.MDCTabBar(document.querySelector('.mdc-tab-bar'));
const tabs = document.querySelectorAll('.mdc-tab');

tabBar.listen('MDCTabBar:activated', function(event) {
  var url;
  switch(event.detail.index) {
    case 0:
        // The portfolio page is currently on index.html.
        url = new URL("/index.html", document.URL);
        break;
    case 1:
        var url = new URL("/discover.html", document.URL);
        break;
    case 2:
        var url = new URL("/myPlaceGuides.html", document.URL);
        break;
    case 3:
        var url = new URL("createPlaceGuide.html", document.URL);
        break;
    case 4:
        // The bookmarkedPlaceGuides page is not ready yet.
        var url = new URL("/index.html", document.URL);
        break;
    default:
        var url = new URL(document.url);
  }
  window.location = url;
});