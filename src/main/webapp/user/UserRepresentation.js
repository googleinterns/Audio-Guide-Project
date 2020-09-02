class UserRepresentation {
  constructor(user) {
    this._userRepresentationDiv = createUserDiv(user);
  }

  get userRepresentationDiv() {
    return this._userRepresentationDiv;
  }
}

function createUserDiv(user) {
  const userDiv = document.createElement("div");
  const userDivClass = "userRepresentation-" + "{" + user.id() + "}";
  userDiv.setAttribute("class", userDivClass);
//   userDiv.addEventListener("click", function() {
//     if (user.publicPortfolio) {
//       var url = new URL("/portfolio.html", document.URL);
//       url.searchParams.append('user', user)
//       window.location = url;
//     } else {
//       alert("The creator of this place guide does not have a private place guide!");
//     }
//   });
  return userDiv;
}