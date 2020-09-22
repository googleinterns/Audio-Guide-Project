/**
 * PlaceGudieRepository is responsible for getting the placeGuides'
 * data from the database and updating it if needed.
 * The data is stored in memory until the next query is sent.
 */
class PlaceGuideRepository {
  static MIN_ZOOM = 8;
  static QUERY_TYPE = {
    ALL_PUBLIC_IN_MAP_AREA: "ALL_PUBLIC_IN_MAP_AREA",
    CREATED_ALL_IN_MAP_AREA: "CREATED_ALL_IN_MAP_AREA",
    CREATED_PUBLIC_IN_MAP_AREA: "CREATED_PUBLIC_IN_MAP_AREA",
    CREATED_PRIVATE_IN_MAP_AREA: "CREATED_PRIVATE_IN_MAP_AREA",
    BOOKMARKED: "BOOKMARKED",
    CREATED_BY_GIVEN_USER_PUBLIC_IN_MAP_AREA: "CREATED_BY_GIVEN_USER_PUBLIC_IN_MAP_AREA"
  };

  static BOOKMARK_ACTION_RESULT_TYPE = {
      SUCCESS: {},
      NOT_ALLOWED: {},
      SERVER_FAILURE: {},
  }

  constructor() {
    this._placeGuides = {};
  }

  // get placeGuides() {
  //   for (const placeGuideId in this._placeGuides) {
  //     if (this._placeGuides.hasOwnProperty(placeGuideId)) {
  //       console.log(this._placeGuides[placeGuideId]);
  //     }
  //   }
  //   return this._placeGuides;
  // }

  static buildPlaceGuideDictionaryFromResponse(placeGuideWithCreatorPairs) {
    var placeGuidesDict = {};
    for (var i = 0; i < placeGuideWithCreatorPairs.length; i++) {
      const placeGuide = PlaceGuideRepository
          .getPlaceGuideFromPlaceGuideWithCreatorPair(
            placeGuideWithCreatorPairs[i]);
      placeGuidesDict[placeGuide.id] = placeGuide;
    }
    return placeGuidesDict;
  }

  static getPlaceGuideFromPlaceGuideWithCreatorPair(placeGuideInfo) {
    const creator = PlaceGuideRepository.getUserFromResponse(placeGuideInfo.creator);
    const placeGuideResponse = placeGuideInfo.placeGuide;
    const location = new Location(placeGuideResponse.coordinate.latitude,
                                  placeGuideResponse.coordinate.longitude,
                                  placeGuideResponse.placeId);
    return new PlaceGuide(placeGuideResponse.id,
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
  }

  static getUserFromResponse(userResponse) {
    return new User(userResponse.id,
        userResponse.email,
        userResponse.name,
        userResponse.publicPortfolio,
        userResponse.selfIntroduction,
        userResponse.imgKey);
  }

  fetchPlaceGuides(queryType, bounds, zoom, portfolioUserId) {
    if (PlaceGuideRepository.MIN_ZOOM <= zoom ||
        queryType == PlaceGuideRepository.QUERY_TYPE.BOOKMARKED) {
      // As the number of bookmarked placeGuides will be restricted,
      // we are not limiting the number of
      // displayed PlaceGuides based on the zoom level/map area.
      // We display all of them at once.
      var url = new URL("/place-guide-data", document.URL);
      url.searchParams.append("placeGuideType", queryType);
      if (queryType != PlaceGuideRepository.QUERY_TYPE.BOOKMARKED) {
        url.searchParams.append("regionCorners", bounds.toUrlValue());
      }
      if (queryType === PlaceGuideRepository.QUERY_TYPE.CREATED_BY_GIVEN_USER_PUBLIC_IN_MAP_AREA) {
        url.searchParams.append("creatorId", portfolioUserId);
      }
      var thisRepository = this;
      return fetch(url)
          .catch(error => {
            console.log("PlaceGuideServlet: failed to fetch: " + error);
            alert("Failed to load the data of guides");
          })
          .then(response => response.json())
          .catch(error => {
            console.log('updatePlaceGuides: failed to convert response to JSON'
                  + error);
            alert("Failed to process the data of guides");
          })
          .then(placeGuideWithCreatorPairs => {
            thisRepository._placeGuides = PlaceGuideRepository
                .buildPlaceGuideDictionaryFromResponse(
                    placeGuideWithCreatorPairs);
            return thisRepository._placeGuides;
          });
    } else {
      var thisRepository = this;
      return new Promise(function (resolve, reject) {
        thisRepository._placeGuides = {};
        resolve(thisRepository._placeGuides);
      });
    }
  }

  removePlaceGuide(placeGuideId) {
    const thisRepository = this;
    return new Promise(function (resolve, reject) {
      // Remove from database.
      const url = new URL("/delete-place-guide-data", document.URL);
      url.searchParams.append('id', placeGuideId);
      fetch(url)
        .catch(error => {
          console.log("DeletePlaceGuideServlet: failed to fetch: "
            + error);
          alert("Failed to delete guide");
          resolve(false);
        })
        .then(response => {
          // Remove from in-memory dictionary.
          delete thisRepository._placeGuides[placeGuideId];
          resolve(true);
        });
    });
  }

  togglePlaceGuideBookmarkStatus(placeGuideId) {
    const thisRepository = this;
    return new Promise(function (resolve, reject) {
      const isBookmarked = thisRepository._placeGuides[placeGuideId].bookmarkedByCurrentUser;
      // Toogle in database.
      const url = new URL("bookmark-place-guide", document.URL);
      url.searchParams.append("placeGuideId", placeGuideId);
      if (isBookmarked) {
        url.searchParams.append("bookmarkHandlingType", "UNBOOKMARK");
      } else {
        url.searchParams.append("bookmarkHandlingType", "BOOKMARK");
      }
      fetch(url)
        .catch(error => {
          console.log("BookmarkPlaceGuideServlet: failed to fetch: "
            + error);
          resolve(PlaceGuideRepository.BOOKMARK_ACTION_RESULT_TYPE.SERVER_FAILURE);
        })
        .then(response => response.json())
        .catch(error => {
            console.log('updatePlaceGuides: failed to convert response to JSON'
                  + error);
            alert("Failed to process the response from the server");
        })
        .then(response => {
          if (response) {
            thisRepository._placeGuides[placeGuideId].bookmarkedByCurrentUser = !isBookmarked;
            // Toggle in in-memory dictionary.
            resolve(PlaceGuideRepository.BOOKMARK_ACTION_RESULT_TYPE.SUCCESS);
          } else{
            resolve(PlaceGuideRepository.BOOKMARK_ACTION_RESULT_TYPE.NOT_ALLOWED);
          }
        });
    });
  }

  isBookmarked(placeGuideId) {
    return this._placeGuides[placeGuideId].bookmarkedByCurrentUser;
  }

  location(placeGuideId) {
    return this._placeGuides[placeGuideId].location;
  }
}