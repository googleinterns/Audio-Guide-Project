class PlaceGuideRepository {
    static QueryType = {
        ALL_PUBLIC_IN_MAP_AREA: "ALL_PUBLIC_IN_MAP_AREA",
        CREATED_ALL_IN_MAP_AREA: "CREATED_ALL_IN_MAP_AREA",
        CREATED_PUBLIC_IN_MAP_AREA: "CREATED_PUBLIC_IN_MAP_AREA",
        CREATED_PRIVATE_IN_MAP_AREA: "CREATED_PRIVATE_IN_MAP_AREA",
        BOOKMARKED: "BOOKMARKED_IN_MAP_AREA",
    };
    
    constructor(queryType) {
        this._queryType = queryType;
        this._placeGuides = {}; // Init dictionary. 
    }

}