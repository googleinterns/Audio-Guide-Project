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
  document.getElementById("portfolioSelfIntroduction").innerText = user.selfIntroduction;
}



