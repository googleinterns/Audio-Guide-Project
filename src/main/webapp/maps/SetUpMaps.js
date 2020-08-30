function setApiKeyAndMapsCallback() {
  // Create the script tag, set the appropriate attributes
  var script = document.createElement('script');
  script.src = 'https://maps.googleapis.com/maps/api/js?key=AIzaSyB5r4xc4N3npOYW8bQQfFSOR2pCy-COL6I&callback=initPage';
  script.defer = true;

  // Append the 'script' element to 'head'
  document.head.appendChild(script);
}