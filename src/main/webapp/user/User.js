class User {
  constructor(id, email, name, publicPortfolio, selfIntroduction, imgKey) {
    this._id = id;
    this._email = email;
    this._name = name;
    this._publicPortfolio = publicPortfolio;
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
}