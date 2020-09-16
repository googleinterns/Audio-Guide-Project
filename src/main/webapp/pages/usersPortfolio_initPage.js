/**
 * This function initialises the map and adds some functionalities to it:
 * Geolocation: to track the user's current location,
 * display it and center the map around it.
 * Searching: lets the user search for places and center the map around it.
 * Display place guides: displays the place guides created
 * by the user with the given creatorId.
 */
let map;
let placeGuideManager;

function initPage() {
  authenticateUser().then((userAuthenticationStatus) => {
    if (!userAuthenticationStatus.isLoggedIn) {
      location.replace(userAuthenticationStatus.loginUrl);
    } else {
      const menu = new Menu(undefined);
      const mapWidget = new MapWidget();
      mapWidget.addGeolocationFunctionality();
      mapWidget.addSearchingFunctionality();
      map = mapWidget.map;
      const user = User.getUserFromQueryString();
      fillPortfolioDiv(user);
      placeGuideManager = new PlaceGuideManager(
          PlaceGuideManager.PAGE.USERS_PORTFOLIO, map, user.id);
      setListHeight();
      window.addEventListener('resize', function() {
        setMapWidth();
        setListHeight();
      });
    }
  });
}

function fillPortfolioDiv(user) {
  if (user.imgKey != undefined) {
    setBlobKeyBackgroundToElement(user.imgKey, "portfolioImg");
  } else {
    const icon = document.createElement("icon");
    icon.classList.add("mdc-tab__icon", "material-icons", "no-portfolio-img-icon");
    icon.setAttribute("aria-hidden", true);
    icon.innerText = "no_photography";
    document.getElementById("portfolioImg").appendChild(icon);
  }
  if (user.name != undefined) {
    document.getElementById("portfolioName").innerText = user.name;
  } else {
    document.getElementById("portfolioName").innerText = user.email;
  }
  if (user.selfIntroduction != undefined) {
    document.getElementById("portfolioSelfIntroduction").innerText = user.selfIntroduction;
  }
}

function setMapWidth() {
  const availableWidth = window.innerWidth - 370;
  document.getElementById('mapDisplayer').style.width = availableWidth.toString() + 'px';
}

function setListHeight() {
  const availableHeight= window.innerHeight - 520;
  document.getElementById('listPlaceGuideDisplayer').style.height = availableHeight.toString() + 'px';
}




