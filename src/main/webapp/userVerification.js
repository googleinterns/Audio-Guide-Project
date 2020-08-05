// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
* This js file is to authenticate user and check if user 
* has data stored already on the database.
*/

/**
* Add a href link to the logout button if the user is logged in,
* and prompt the user to Google login page if user is not logged
* in.
*/
function authenticateUser() {
    const currentWindowLocation = window.location.href;
    const currentUrl = new URL(currentWindowLocation);
    queryAuthenticationServer(currentUrl).then((userAuthenticationStatus) => {
        if (!userAuthenticationStatus.isLoggedIn) {
            location.replace(userAuthenticationStatus.loginUrl);
        } else {
            addLinktoLogoutButton(userAuthenticationStatus.logoutUrl);
        }
    });
}

/**
* Send GET request to UserAuthenticationServlet with the current page url
* as the query parameters.
*/
function  queryAuthenticationServer(currentUrl) {
    const requestUrl = new URL('/user-authentication', currentUrl);
    const queryParams = new QueryParams(currentUrl);
    requestUrl.search = new URLSearchParams(queryParams).toString();

    return fetch(requestUrl, {method: 'GET'})
        .catch(error => console.log(error))
        .then((response) => {
            return response.json();
        });
}

class QueryParams{
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