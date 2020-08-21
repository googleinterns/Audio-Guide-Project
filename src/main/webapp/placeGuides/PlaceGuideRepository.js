class PlaceGuideRepository {
    static QueryType = {
        ALL_PUBLIC_IN_MAP_AREA: "ALL_PUBLIC_IN_MAP_AREA",
        CREATED_ALL_IN_MAP_AREA: "CREATED_ALL_IN_MAP_AREA",
        CREATED_PUBLIC_IN_MAP_AREA: "CREATED_PUBLIC_IN_MAP_AREA",
        CREATED_PRIVATE_IN_MAP_AREA: "CREATED_PRIVATE_IN_MAP_AREA",
    };
    
    constructor(queryType) {
        this._queryType = queryType;
        this._placeGuides = {}; // Init dictionary. 
        console.log("PlaceGuideRepository constructed");
    }

    updatePlaceGuides(bounds, zoom) {
        // TODO add zoom to query.
        var url = new URL("/place-guide-data", document.URL);
        url.searchParams.append("placeGuideType", this._queryType);
        url.searchParams.append("regionCorners", bounds.toUrlValue());
        return fetch(url)
            .catch(error => console.log("PlaceGuideServlet: failed to fetch: " + error))
            .then(response => response.json())
            .catch(error => console.log('updatePlaceGuides: failed to convert response to JSON' + error))
            .then(placeGuideWithCreatorPairs => this._placeGudies = buildPlaceGuideDictionaryFromResponse(placeGuideWithCreatorPairs))
            .catch(error => "updatePlaceGuides: unable to build placeGuide dictionary: " + error);
    }

    get placeGuides() {
        return this._placeGuides;
    }

    removePlaceGuide(placeGuideId) {
        // Remove from in-memory dictionary.
        delete this._placeGuides[placeGuideId];
        // Remove from database.
        var url = new URL("/delete-place-guide-data", document.URL);
        url.searchParams.append('id', placeGuideId)
        return fetch(url)
            .catch(error => console.log("DeletePlaceGuideServlet: failed to fetch: " + error));
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
            .catch(error => console.log("BookmarkPlaceGuideServlet: failed to fetch: " + error));
    }

    static buildPlaceGuideDictionaryFromResponse(placeGuideWithCreatorPairs) {
        return new Promise(function (resolve, reject) {
            var placeGuidesDict = {};
            var promises = [];
            for (var i = 0; i < placeGuideWithCreatorPairs.length; i++) {
                promises.push(
                    getPlaceGuideFromPlaceGuideWithCreatorPair(placeGuideWithCreatorPairs[i])
                    .then(placeGuide => {
                            placeGuidesDict[placeGuide.id] = placeGuide;
                    }));
            }
            Promise.all(promises).then(() => {
                resolve(placeGuidesDict);
            });
        });
    }

    static getPlaceGuideFromPlaceGuideWithCreatorPair(placeGuideWithCreatorPair) {
        new Promise(function (resolve, reject) {
            var creator = this.getUserFromResponse(placeGuideWithCreatorPair.creator);
            var placeGuideResponse = placeGuideWithCreatorPair.placeGuide;
            if(placeGuideResponse.placeId !== undefined) {
                Location.constructLocationBasedOnPlaceId(placeGuideResponse.placeId)
                    .then(location => {
                    var placeGuide = new PlaceGuide(placeGuideResponse.id, 
                                                    placeGuideResponse.name, 
                                                    location, 
                                                    placeGuideResponse.audioKey, 
                                                    placeGuideResponse.length, 
                                                    placeGuideResponse.imageKey,
                                                    creator,
                                                    placeGuideResponse.isPublic, 
                                                    placeGuideWithCreatorPair.createdByCurrentUser, 
                                                    placeGuideWithCreatorPair.bookmarkedByCurrentUser);
                    resolve(placeGuide);
                    });
            }
             else {
                 // TODO: figure out how to get coordinates from GeoPt
                 var location = Location.constructLoctaionBasedOnCoordinates(10, 20);
                 var placeGuide = new PlaceGuide(placeGuideResponse.id, 
                                                placeGuideResponse.name, 
                                                location, 
                                                placeGuideResponse.audioKey, 
                                                placeGuideResponse.length, 
                                                placeGuideResponse.imageKey,
                                                creator,
                                                placeGuideResponse.isPublic, 
                                                placeGuideWithCreatorPair.createdByCurrentUser, 
                                                placeGuideWithCreatorPair.bookmarkedByCurrentUser);
                 resolve(placeGuide);
             }
        });

    }

    static getUserFromResponse(userResponse) {
        return new User(userResponse.id, 
                        userResponse.email,
                        userResponse.name,
                        userResponse.publicPortfolio,
                        userResponse.imgKey);
    }
}