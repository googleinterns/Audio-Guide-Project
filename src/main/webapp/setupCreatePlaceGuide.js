function setUpCreatePlaceGuidePage() {
  authenticateUser().then(userAuthenticationStatus => {
    if (!userAuthenticationStatus.isLoggedIn) {
      location.replace(userAuthenticationStatus.loginUrl);
    } else {
      addLinktoLogoutButton(userAuthenticationStatus.logoutUrl);
      setUpCreatePlaceGuideForm();
    }
  });
}