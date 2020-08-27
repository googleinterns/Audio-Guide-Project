const tabBar = new mdc.tabBar.MDCTabBar(document.querySelector('.mdc-tab-bar'));
const tabs = document.querySelectorAll('.mdc-tab');

tabBar.listen('MDCTabBar:activated', function(event) {
  //let tab = tabs[event.detail.index];
  console.log(event.detail.index, 'tab activated');
});