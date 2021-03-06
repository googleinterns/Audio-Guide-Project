class User {
  constructor(id, email, name, publicPortfolio, selfIntroduction, imgKey) {
    this._id = id;
    this._email = email;
    this._name = name;
    this._publicPortfolio = publicPortfolio;
    this._selfIntroduction = selfIntroduction;
    this._imgKey = imgKey;
  }

  get id() {
    return this._id;
  }

  get email() {
    return this._email;
  }

  get name() {
    return this._name;
  }

  get publicPortfolio() {
    return this._publicPortfolio;
  }

  get selfIntroduction() {
    return this._selfIntroduction;
  }

  get imgKey() {
    return this._imgKey;
  }

  get queryString() {
    const query = `id=${this._id}&name=${this._name}&email=${this._email}\
      &publicPortfolio=${this._publicPortfolio}&selfIntroduction=\
      ${this._selfIntroduction}&imgKey=${this._imgKey}`;
    return query;
  }

  static getUserFromQueryString() {
    if (window.location.search != '') {
      const GET = UrlQueryUtils.getParamsFromQueryString();
      const id = GET['id'];
      let name = GET['name'];
      if (name === "undefined") {
        name = undefined;
      }
      const email = GET['email'];
      let selfIntroduction = GET['selfIntroduction'];
      if (selfIntroduction === "undefined") {
        selfIntroduction = undefined;
      }
      let imgKey = GET['imgKey'];
      if (imgKey === "undefined") {
        imgKey = undefined;
      }
      let publicPortfolio = GET['publicPortfolio'];
      if (publicPortfolio === "true") {
        publicPortfolio = true;
      } else {
        publicPortfolio = false;
      }
      return new User(
          id, email, name, publicPortfolio, selfIntroduction, imgKey);
    }
  }
}
