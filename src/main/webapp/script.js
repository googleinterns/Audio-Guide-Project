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
*  
*/
function authenticateUser() {
    const currentWindowLocation = window.location.href;
    const currentUrl = new URL(currentWindowLocation);
    queryAuthenticationServer(currentUrl).then((userAuthenticationStatus) => {
        if (!userAuthenticationStatus.isLoggedIn) {
            location.replace(userAuthenticationStatus.url);
        }
        else {
            const logoutButton = document.getElementById('logout');
            logoutButton.onclick = location.replace(userAuthenticationStatus.url);
        }
    });
}

function  queryAuthenticationServer(currentUrl) {
    return fetch('/user-authentication', {method: 'GET'})
        .then((response) => {
            return response.json();
        });
}