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

    updatePlaceGuides() {

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
            url.searchParams.append("bookmarkHandlingType", "REMOVE");
        }
        return fetch(url)
            .catch(error => console.log("BookmarkPlaceGuideServlet: failed to fetch: " + error));
    }
}