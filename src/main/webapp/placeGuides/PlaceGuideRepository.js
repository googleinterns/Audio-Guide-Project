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
    }

    updatePlaceGuides(bounds, zoom) {
        // TODO add zoom to query.
        var url = new URL("/place-guide-data", document.URL);
        url.searchParams.append("placeGuideType", this._queryType);
        url.searchParams.append("regionCorners", bounds.toUrlValue());
        var thisPlaceGuides = this._placeGuides;
        var thisQueryType = this._queryType;
        return new Promise(function (resolve, reject) {
            var isAllPublic = thisQueryType == PlaceGuideRepository.QueryType.ALL_PUBLIC_IN_MAP_AREA || thisQueryType == PlaceGuideRepository.QueryType.CREATED_PUBLIC_IN_MAP_AREA;
            console.log(isAllPublic);
            var location = Location.constructLocationBasedOnCoordinates(10, 20);
            var user = new User("id1", "email", "name", true, "I am user", "imgkey");
            var placeGuide = new PlaceGuide(111, "placeGuide1", location, "audioKey", 3, "imgKey", user, "This is a placeGuide", isAllPublic, true, true);
            thisPlaceGuides[placeGuide.id] = placeGuide;
            var location2 = Location.constructLocationBasedOnCoordinates(46.792268,23.537362);
            var user2 = new User("id2", "email2", "name2", true, "I am user2", "imgkey2");
            var placeGuide2 = new PlaceGuide(222, "placeGuide2", location2, "audioKey2", 5, "imgKey2", user2, "This is a placeGuide2", true, false, false);
            thisPlaceGuides[placeGuide2.id] = placeGuide2;
            Location.constructLocationBasedOnPlaceId("ChIJe-ff-71RqEcRqvy8lRR4PHo").then(location3 => {
                var user3 = new User("id3", "email3", "name3", true, "I am user3", "imgkey3");
                var placeGuide3 = new PlaceGuide(333, "placeGuide3", location3, "audioKey3", 4, "imgKey3", user3, "This is a placeGuide3", true, true, false);
                thisPlaceGuides[placeGuide3.id] = placeGuide3;
            }).then(response => {
                Location.constructLocationBasedOnPlaceId("ChIJM3bYao8OSUcRpHGkCCTD9yM").then(location4 => {
                    var user4 = new User("id4", "email4", "name4", true, "I am user4", "imgkey4");
                    var placeGuide4 = new PlaceGuide(444, "placeGuide4", location4, "audioKey4", 4, "imgKey4", user4, "This is a placeGuide4", isAllPublic, true, false);
                    thisPlaceGuides[placeGuide4.id] = placeGuide4;
                }).then(response => resolve());
            });
        })
        // return fetch(url)
        //     .catch(error => console.log("PlaceGuideServlet: failed to fetch: " + error))
        //     .then(response => response.json())
        //     .catch(error => console.log('updatePlaceGuides: failed to convert response to JSON' + error))
        //     .then(placeGuideWithCreatorPairs => this._placeGudies = buildPlaceGuideDictionaryFromResponse(placeGuideWithCreatorPairs))
        //     .catch(error => "updatePlaceGuides: unable to build placeGuide dictionary: " + error);
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
                 // TODO: figure out how to get coordinates from GeoPt.
                 var location = Location.constructLocationBasedOnCoordinates(10, 20);
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