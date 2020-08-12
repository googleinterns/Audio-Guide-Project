function displayPublicPlaceGuides(map) {
    var placeGuides = getPublicPlaceGuides();
    var markers = placeGuides.map(function(placeGuide) { 
        placeGuide.addMarkerToMap(map);
        return placeGuide._marker;
    });
}

// This is for demonstrational purposes only. data will be fetched from the server.
function getPublicPlaceGuides() {
    var publicPlaceGuides = [
        new PlaceGuide("id1", "PlaceGuide 1", "This is a placeguide 1", "audioKey1", 3, "imgKey1", 
            46.731686,23.604655, "placeId1", "creatorId1", "creatorName1",  PlaceType.PUBLIC),
        new PlaceGuide("id2", "PlaceGuide 2", "This is a placeguide 2", "audioKey2", 4, "imgKey2", 
            46.837006,23.527699, "placeId2", "creatorId2", "creatorName2",  PlaceType.PUBLIC),
        new PlaceGuide("id3", "PlaceGuide 3", "This is a placeguide 3", "audioKey3", 2, "imgKey3", 
            46.162006,25.469789, "placeId3", "creatorId3", "creatorName3",  PlaceType.PUBLIC),
        new PlaceGuide("id4", "PlaceGuide 4", "This is a placeguide 4", "audioKey4", 12, "imgKey4", 
            46.295773,2.465071, "placeId4", "creatorId4", "creatorName4",  PlaceType.PUBLIC),
        new PlaceGuide("id5", "PlaceGuide 5", "This is a placeguide 5", "audioKey5", 8, "imgKey5", 
            39.751611,-3.163685, "placeId4", "creatorId5", "creatorName5",  PlaceType.PUBLIC),
        new PlaceGuide("id6", "PlaceGuide 6", "This is a placeguide 6", "audioKey6", 9, "imgKey6", 
            31.257349,108.751543, "placeId5", "creatorId6", "creatorName6",  PlaceType.PUBLIC),
        new PlaceGuide("id7", "PlaceGuide 7", "This is a placeguide 7", "audioKey7", 9, "imgKey7", 
            53.119633,-88.482046, "placeId6", "creatorId7", "creatorName7",  PlaceType.PUBLIC),
        new PlaceGuide("id8", "PlaceGuide 8", "This is a placeguide 8", "audioKey8", 4, "imgKey8", 
            51.834982,-86.723060, "placeId7", "creatorId8", "creatorName8",  PlaceType.PUBLIC),
        new PlaceGuide("id9", "PlaceGuide 9", "This is a placeguide 9", "audioKey9", 6, "imgKey9", 
            46.770003,23.509997, "placeId8", "creatorId9", "creatorName9",  PlaceType.PUBLIC),
    ];
    return publicPlaceGuides;
}