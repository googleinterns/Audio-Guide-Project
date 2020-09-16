class UserRepresentation {
  constructor(user) {
    this._userRepresentationDiv = UserRepresentation.createUserDiv(user);
  }

  get div() {
    return this._userRepresentationDiv;
  }

  static createUserDiv(user) {
    const userDiv = document.createElement("div");
    userDiv.style.height = "5px";
    userDiv.style.width = "50px";
    const userDivClass = "userRepresentation-" + "{" + user.id + "}";
    userDiv.setAttribute("class", userDivClass);
    if (user.imgKey != undefined) {
      if (user.name != undefined) {
        userDiv.appendChild(
            UserRepresentation.createUserImg(user.imgKey, user.name));
      } else {
        userDiv.appendChild(
            UserRepresentation.createUserImg(user.imgKey, user.email));
      }
    } else {
      if (user.name != undefined) {
        userDiv.appendChild(
            UserRepresentation.createUserIcon(user.name));
      } else {
        userDiv.appendChild(
            UserRepresentation.createUserIcon(user.email));
      }
    }
    userDiv.addEventListener("click", function() {
      if (user.publicPortfolio) {
        const queryString = user.queryString;
        const url = './usersPortfolio.html?' + queryString;
        window.location = url;
      } else {
        alert("The portfolio of this user is not public");
      }
    });
    return userDiv;
  }

  static createUserImg(imgKey, name) {
    const userImg = document.createElement("img");
    userImg.setAttribute("class", "user-img");
    const src = new URL("/serve-blob", document.URL);
    src.searchParams.append('blob-key', imgKey);
    userImg.setAttribute("src", src);
    userImg.setAttribute("title", "Owner: " + name);
    return userImg;
  }

  static createUserIcon(name) {
    const userIcon = document.createElement("i");
    userIcon.classList.add("material-icons",
        "mdc-icon-button",
        "mdc-card__action",
        "mdc-card__action--icon");
    userIcon.innerText = "account_circle";
    userIcon.setAttribute("title", "Owner: " + name);
    return userIcon;
  }
}
