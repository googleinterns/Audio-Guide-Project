/**
 * This class is responsible for getting and displaying the places corresponding to a query.
 * For now, the places' data is hardcoded, but it needs to be fetched from the server in the future.
 */
class PlaceDisplayer {
  static displayPublicPlaceGuides(map) {
    PlaceDisplayer.getPublicPlaceGuides(map)
        .then(placeGuides => {
            var markers = placeGuides.map(function (placeGuide) {
                console.log("Next placeguide: ");
                console.log(placeGuide);
                return placeGuide._marker;
            });
            console.log("marker1: ");
            console.log(markers[1]);
            console.log(markers[1].getPosition().toString());
            var markerCluster = new MarkerClusterer(map, markers,
                {imagePath: './img/m'});
       });     
  }

  static displayPlaceGuidesOfUser(map, userId) {
    var placeGuides = PlaceDisplayer.getPlaceGuidesOfUser(userId);
    placeGuides.forEach(placeGuide => placeGuide.map = map);
    var markers = placeGuides.map(function (placeGuide) {
      return placeGuide._marker;
    });
    var markerCluster = new MarkerClusterer(map, markers,
        {imagePath: './img/m'});
  }

  // This is for demonstrational purposes only. Data will be fetched from the server.
  static getPublicPlaceGuides(map) {
      var placeGuidesData = [
      { id: "id1", name: "PlaceGuide 1", description: "This is a placeguide 1",
          audioKey: "audioKey1", audioLength: 3, imgKey: "imgKey1",
          positionLat: 46.731686, positionLng: 23.604655, placeId: null, creatorId: "creatorId1",
          creatorName: "creatorName1", placeType: PlaceType.PUBLIC},
     { id: "id2", name: "PlaceGuide 2", description: "This is a placeguide 2",
          audioKey: "audioKey2", audioLength: 2, imgKey: "imgKey2",
          positionLat: 0, positionLng: 0, placeId: "ChIJM3bYao8OSUcRpHGkCCTD9yM", creatorId: "creatorId2",
          creatorName: "creatorName2", placeType: PlaceType.PUBLIC},
    //   new PlaceGuide("id2", "PlaceGuide 2", "This is a placeguide 2",
    //       "audioKey2", 4, "imgKey2",
    //       0, 0, "ChIJM3bYao8OSUcRpHGkCCTD9yM", "creatorId2",
    //       "creatorName2", PlaceType.PUBLIC),
    //   new PlaceGuide("id3", "PlaceGuide 3", "This is a placeguide 3",
    //       "audioKey3", 2, "imgKey3",
    //       0, 0,  "ChIJH-tBOc4EdkgRJ8aJ8P1CUxo", "creatorId3",
    //       "creatorName3", PlaceType.PUBLIC),
    //   new PlaceGuide("id4", "PlaceGuide 4", "This is a placeguide 4",
    //       "audioKey4", 12, "imgKey4",
    //       46.295773, 2.465071, null, "creatorId4",
    //       "creatorName4", PlaceType.PUBLIC),
    //   new PlaceGuide("id5", "PlaceGuide 5", "This is a placeguide 5",
    //       "audioKey5", 8, "imgKey5",
    //       39.751611, -3.163685, null, "creatorId5",
    //       "creatorName5", PlaceType.PUBLIC),
    //   new PlaceGuide("id6", "PlaceGuide 6", "This is a placeguide 6",
    //       "audioKey6", 9, "imgKey6",
    //       31.257349, 108.751543, null, "creatorId6",
    //       "creatorName6", PlaceType.PUBLIC),
    //   new PlaceGuide("id7", "PlaceGuide 7", "This is a placeguide 7",
    //       "audioKey7", 9, "imgKey7",
    //       53.119633, -88.482046, null, "creatorId7",
    //       "creatorName7", PlaceType.PUBLIC),
    //   new PlaceGuide("id8", "PlaceGuide 8", "This is a placeguide 8",
    //       "audioKey8", 4, "imgKey8",
    //       51.834982, -86.723060, null, "creatorId8",
    //       "creatorName8", PlaceType.PUBLIC),
    //   new PlaceGuide("id9", "PlaceGuide 9", "This is a placeguide 9",
    //       "audioKey9", 6, "imgKey9",
    //       46.770003, 23.509997, null, "creatorId9",
    //       "creatorName9", PlaceType.PUBLIC),
    ];

    return new Promise(function(resolve, reject) {
        var publicPlaceGuides = []
        //var p = new Promise();
        var promises = [];
        for (var i=0; i<placeGuidesData.length; i++) {
            var pg = placeGuidesData[i];
            if (pg.placeId == null) {
                publicPlaceGuides.push(PlaceGuide.constructPlaceGuideBasedOnCoordinates(map, pg.id, pg.name, pg.description, 
                    pg.audioKey, pg.audioLength, pg.imgKey, pg.positionLat, pg.positionLng, pg.creatorId, pg.creatorName, pg.placeType));
            } else {
                promises.push(new Promise(function(resolve, reject) {
                    PlaceGuide.constructPlaceGuideBasedOnPlaceId(map, pg.id, pg.name, pg.description, pg.audioKey, pg.audioLength, 
                            pg.imgKey, pg.placeId, pg.creatorId, pg.creatorName, pg.placeType).then(placeGuide => {
                                console.log("pushing placeGuide");
                                console.log(placeGuide)
                                publicPlaceGuides.push(placeGuide)
                                resolve()});
                    }));
            }
        }
        Promise.all(promises).then(() => {
            console.log("returned array of placeGuides: " + publicPlaceGuides);
            resolve(publicPlaceGuides)
        });
        // resolve(PlaceGuide.constructPlaceGuideBasedOnPlaceId(map,
        //   "id2", "PlaceGuide 2", "This is a placeguide 2",
        //   "audioKey2", 4, "imgKey2",
        //   "ChIJM3bYao8OSUcRpHGkCCTD9yM", "creatorId2",
        //   "creatorName2", PlaceType.PUBLIC)
        //    .then(newPlaceGuide => publicPlaceGuides.push(newPlaceGuide))
        //    .then(() => {return publicPlaceGuides}));
    });
  }
}
//     return publicPlaceGuides;
//   }

//   // This is for demonstrational purposes only. Data will be fetched from the server.
//   static getPlaceGuidesOfUser(userId) {
//     var publicPlaceGuides = [
//     //   new PlaceGuide("id1", "PlaceGuide 1", "This is a placeguide 1",
//     //       "audioKey1", 3, "imgKey1",
//     //       46.731686, 23.604655, null, userId,
//     //       "creatorName1", PlaceType.PUBLIC),
//     //   new PlaceGuide("id2", "PlaceGuide 2", "This is a placeguide 2",
//     //       "audioKey2", 4, "imgKey2",
//     //       0, 0, "ChIJM3bYao8OSUcRpHGkCCTD9yM", userId,
//     //       "creatorName2", PlaceType.PUBLIC),
//     //   new PlaceGuide("id3", "PlaceGuide 3", "This is a placeguide 3",
//     //       "audioKey3", 2, "imgKey3",
//     //       0, 0,  "ChIJH-tBOc4EdkgRJ8aJ8P1CUxo", userId,
//     //       "creatorName3", PlaceType.PRIVATE),
//     //   new PlaceGuide("id4", "PlaceGuide 4", "This is a placeguide 4",
//     //       "audioKey4", 12, "imgKey4",
//     //       46.295773, 2.465071, null, userId,
//     //       "creatorName4", PlaceType.PRIVATE),
//     //   new PlaceGuide("id5", "PlaceGuide 5", "This is a placeguide 5",
//     //       "audioKey5", 8, "imgKey5",
//     //       39.751611, -3.163685, null, userId,
//     //       "creatorName5", PlaceType.PRIVATE),
//     //   new PlaceGuide("id8", "PlaceGuide 8", "This is a placeguide 8",
//     //       "audioKey8", 4, "imgKey8",
//     //       51.834982, -86.723060, null, userId,
//     //       "creatorName8", PlaceType.PUBLIC),
//     //   new PlaceGuide("id9", "PlaceGuide 9", "This is a placeguide 9",
//     //       "audioKey9", 6, "imgKey9",
//     //       46.770003, 23.509997, null, userId,
//     //       "creatorName9", PlaceType.PRIVATE),
//     ];
    //return publicPlaceGuides;
//   }
// }