function setUpCreatePlaceGuidePage() {
  authenticateUser().then(userAuthenticationStatus => {
    if (!userAuthenticationStatus.isLoggedIn) {
      location.replace(userAuthenticationStatus.loginUrl);
    } else {
      setApiKeyAndMapsCallback();
      addLinktoLogoutButton(userAuthenticationStatus.logoutUrl);
      setUpCreatePlaceGuideForm();
    }
  });
}