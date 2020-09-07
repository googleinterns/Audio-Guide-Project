/**
 * PlaceGudieRepository is responsible for getting the placeGuides'
 * data from the database and updating it if needed.
 * The data is stored in memory until the next query is sent.
 */
class PlaceGuideRepository {
  static MIN_ZOOM = 8;
  static QueryType = {
    ALL_PUBLIC_IN_MAP_AREA: "ALL_PUBLIC_IN_MAP_AREA",
    CREATED_ALL_IN_MAP_AREA: "CREATED_ALL_IN_MAP_AREA",
    CREATED_PUBLIC_IN_MAP_AREA: "CREATED_PUBLIC_IN_MAP_AREA",
    CREATED_PRIVATE_IN_MAP_AREA: "CREATED_PRIVATE_IN_MAP_AREA",
    BOOKMARKED: "BOOKMARKED",
  };

  constructor(queryType) {
    this._queryType = queryType;
    this._placeGuides = {};
  }

  get placeGuides() {
    return this._placeGuides;
  }

  static buildPlaceGuideDictionaryFromResponse(placeGuideWithCreatorPairs) {
    var placeGuidesDict = {};
    var promises = [];
    return new Promise(function (resolve, reject) {
          for (var i = 0; i < placeGuideWithCreatorPairs.length; i++) {
            promises.push(new Promise(function (resolve, reject) {
              PlaceGuideRepository
                  .getPlaceGuideFromPlaceGuideWithCreatorPair(placeGuideWithCreatorPairs[i])
                  .then(placeGuide => {
                    placeGuidesDict[placeGuide.id] = placeGuide;
                  })
                  .then(finished => resolve())
            }));
          }
          Promise.all(promises).then(() => {
            resolve(placeGuidesDict);
          });
        }
    );
  }

  static getPlaceGuideFromPlaceGuideWithCreatorPair(placeGuideInfo) {
    var thisRepository = this;
    return new Promise(function (resolve, reject) {
      var creator = thisRepository.getUserFromResponse(placeGuideInfo.creator);
      var placeGuideResponse = placeGuideInfo.placeGuide;
      if (placeGuideResponse.placeId !== undefined) {
        Location.constructLocationBasedOnPlaceId(placeGuideResponse.placeId)
            .then(location => {
              var placeGuide = new PlaceGuide(placeGuideResponse.id,
                  placeGuideResponse.name,
                  location,
                  placeGuideResponse.audioKey,
                  placeGuideResponse.length,
                  placeGuideResponse.imageKey,
                  creator,
                  placeGuideResponse.description,
                  placeGuideResponse.isPublic,
                  placeGuideInfo.createdByCurrentUser,
                  placeGuideInfo.bookmarkedByCurrentUser);
              resolve(placeGuide);
            });
      } else {
        var location = Location.constructLocationBasedOnCoordinates(placeGuideResponse.coordinate.latitude,
            placeGuideResponse.coordinate.longitude);
        var placeGuide = new PlaceGuide(placeGuideResponse.id,
            placeGuideResponse.name,
            location,
            placeGuideResponse.audioKey,
            placeGuideResponse.length,
            placeGuideResponse.imageKey,
            creator,
            placeGuideResponse.description,
            placeGuideResponse.isPublic,
            placeGuideInfo.createdByCurrentUser,
            placeGuideInfo.bookmarkedByCurrentUser);
        resolve(placeGuide);
      }
    });

  }

  static getUserFromResponse(userResponse) {
    return new User(userResponse.id,
        userResponse.email,
        userResponse.name,
        userResponse.publicPortfolio,
        userResponse.selfIntroduction,
        userResponse.imgKey);
  }

  updatePlaceGuides(bounds, zoom) {
    if (PlaceGuideRepository.MIN_ZOOM <= zoom || 
        this._queryType == PlaceGuideRepository.QueryType.BOOKMARKED) {
      // As the number of bookmarked placeGuides will be restricted, 
      // we are not limiting the number of 
      // displayed PlaceGuides based on the zoom level/map area. 
      // We display all of them at once.
      var url = new URL("/place-guide-data", document.URL);
      url.searchParams.append("placeGuideType", this._queryType);
      if (this._queryType != PlaceGuideRepository.QueryType.BOOKMARKED) {
        url.searchParams.append("regionCorners", bounds.toUrlValue());
      }
      var thisRepository = this;
      return fetch(url)
          .catch(error =>
              console.log("PlaceGuideServlet: failed to fetch: " + error))
          .then(response => response.json())
          .catch(error =>
              console.log('updatePlaceGuides: failed to convert response to JSON'
                  + error))
          .then(placeGuideWithCreatorPairs =>
              PlaceGuideRepository
                  .buildPlaceGuideDictionaryFromResponse(
                      placeGuideWithCreatorPairs))
          .catch(error =>
              console.log("updatePlaceGuides: unable to build placeGuide dictionary: "
                  + error))
          .then(placeGuides => thisRepository._placeGuides = placeGuides);
    } else {
      var thisRepository = this;
      return new Promise(function (resolve, reject) {
        thisRepository._placeGuides = {};
        resolve();
      });
    }
  }

  removePlaceGuide(placeGuideId) {
    // Remove from in-memory dictionary.
    delete this._placeGuides[placeGuideId];
    // Remove from database.
    var url = new URL("/delete-place-guide-data", document.URL);
    url.searchParams.append('id', placeGuideId);
    return fetch(url)
        .catch(error => console.log("DeletePlaceGuideServlet: failed to fetch: "
            + error));
  }

  togglePlaceGuideBookmarkStatus(placeGuideId) {
    // Toggle in in-memory dictionary.
    var isBookmarked = this._placeGuides.bookmarkedByCurrentUser;
    this._placeGuides.bookmarkedByCurrentUser = !isBookmarked;
    // Toogle in database.
    var url = new URL("bookmark-place-guide", document.URL);
    url.searchParams.append("placeGuideId", placeGuideId);
    if (this._placeGuides.bookmarkedByCurrentUser) {
      url.searchParams.append("bookmarkHandlingType", "BOOKMARK");
    } else {
      url.searchParams.append("bookmarkHandlingType", "REMOVE");
    }
    return fetch(url)
        .catch(error => console.log("BookmarkPlaceGuideServlet: failed to fetch: "
            + error));
  }
}