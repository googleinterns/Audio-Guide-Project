/**
 * This class holds a place guide's data.
 */
class PlaceGuide {
  constructor(id, name, location, audioKey, audioLength, imgKey, creator,
      description, isPublic,
      createdByCurrentUser, bookmarkedByCurrentUser) {
    this._id = id;
    this._name = name;
    this._location = location;
    this._audioKey = audioKey;
    this._audioLength = audioLength;
    this._imgKey = imgKey;
    this._creator = creator;
    this._description = description;
    this._isPublic = isPublic;
    this._createdByCurrentuser = createdByCurrentUser;
    this._bookmarkedBuCurrentUser = bookmarkedByCurrentUser;
  }

  get id() {
    return this._id;
  }

  get name() {
    return this._name;
  }

  get location() {
    return this._location;
  }

  get audioKey() {
    return this._audioKey;
  }

  get audioLength() {
    return this._audioLength;
  }

  get imgKey() {
    return this._imgKey;
  }

  get creator() {
    return this._creator;
  }

  get description() {
    return this._description;
  }

  get isPublic() {
    return this._isPublic;
  }

  get createdByCurrentUser() {
    return this._createdByCurrentuser;
  }

  get bookmarkedByCurrentUser() {
    return this._bookmarkedBuCurrentUser;
  }

  set bookmarkedByCurrentUser(newValue) {
    this._bookmarkedBuCurrentUser = newValue;
  }
}
