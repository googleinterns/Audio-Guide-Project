/**
 * This file handles user authentication and saving and updating the user's data in the database through a form.
 */


/**
 * This function handles everything what needs to be done when the page loads.
 *
 * The user is authenticated first.
 * They can only access the page after logging in.
 * Otherwise, the site will prompt the user to Google login page.
 *
 * It is checked (the check is done by the servlet) if the user logs in for the first time to the website.
 * If yes, their data(id and email) will be atutomatically saved in the database.
 *
 * After having their data saved if needed, the user will see a form for editing portfolio data.
 * The portfolio shows the user's previously saved data, if any.
 *
 * The user is also provided a href link to log out and switch account.
 */
function setUpUserAndForm() {
  authenticateUser().then(userAuthenticationStatus => {
    if (!userAuthenticationStatus.isLoggedIn) {
      location.replace(userAuthenticationStatus.loginUrl);
    } else {
      addLinktoLogoutButton(userAuthenticationStatus.logoutUrl);
      saveUserInDatabase().then(setUpPortfolioForm());
    }
  });
}