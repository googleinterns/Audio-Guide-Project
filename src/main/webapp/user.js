/**
 * Check if the user is logged in.
 */
function authenticateUser() {
  const currentWindowLocation = window.location.href;
  const currentUrl = new URL(currentWindowLocation);
  return queryAuthenticationServer(currentUrl).then((userAuthenticationStatus) => {
    return userAuthenticationStatus;
  });
}

/**
 * Send GET request to UserAuthenticationServlet with the current page url
 * as the query parameters.
 */
function queryAuthenticationServer(currentUrl) {
  const requestUrl = new URL('/user-authentication', currentUrl);
  const queryParams = new QueryParams(currentUrl);
  requestUrl.search = new URLSearchParams(queryParams).toString();

  return fetch(requestUrl, {method: 'GET'})
      .catch(error => console.log(error))
      .then((response) => {
        return response.json();
      });
}

class QueryParams {
  constructor(currentUrl) {
    this.currentUrl = currentUrl;
  }
}

function addLinktoLogoutButton(logoutUrl) {
  const logoutButton = document.getElementById('logout');
  logoutButton.addEventListener('click', () => {
    window.location.replace(logoutUrl);
  });
}

/**
 * Saves the currently logged in user in the database.
 */
function saveUserInDatabase() {
  return fetch('/user-creation-servlet', {method: 'POST'})
      .catch(error => "saveUserInDatabase: failed to post new user: " + error);
}