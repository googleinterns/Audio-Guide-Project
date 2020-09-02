class UserRepresentation {
  constructor(user) {
    this._userRepresentationDiv = this.createUserDiv(user);
  }

  get userRepresentationDiv() {
    return this._userRepresentationDiv;
  }

  createUserDiv(user) {
    const userDiv = document.createElement("div");
    const userDivClass = "userRepresentation-" + "{" + user.id() + "}";
    userDiv.setAttribute("class", userDivClass);
    if (user.imgKey != undefined) {
        userDiv.appendChild(this.createUserImg(user.imgKey));
    }
    if (user.name != undefined) {
        userDiv.appendChild(this.createUserNameText(user.name));
    } else {
        userDiv.appendChild(this.createUserNameText(user.email));
    }
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

  createUserImg(imgKey) {
    const userImg = document.createElement("img");
    userDiv.setAttribute("class", "user-img");
    const src = new URL("/serve-blob", document.URL);
    src.searchParams.append('blob-key', imgKey);
    userImg.setAttribute("src", src);
    return userImg;
  }

  createUserNameText(name) {
    const userText = document.createElement("p");
    // userDiv.setAttribute("class", "user-img");
    userText.innerHTML = name;
    return userText;
  }
}