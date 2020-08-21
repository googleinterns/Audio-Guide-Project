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
        url.searchParams.append('id', formType)
        return fetch(url)
            .catch(error => console.log("DeletePlaceGuideServlet: failed to fetch: " + error));
    }
}