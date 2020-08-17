/**
 * This class is responsible for getting and displaying the places corresponding to a query.
 * For now, the places' data is hardcoded, but it needs to be fetched from the server in the future.
 */
class PlaceDisplayer {
  static displayPublicPlaceGuides(map) {
    PlaceDisplayer.getPublicPlaceGuides()
        .then(placeGuides => {
            console.log("public place guides: ");
            console.log(placeGuides);
            placeGuides.forEach(placeGuide => placeGuide.map = map);
            var markers = placeGuides.map(function (placeGuide) {
                console.log("marker" + placeGuide._marker);
                return placeGuide._marker;
            });
            console.log("Markers");
            console.log(markers);
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
  static getPublicPlaceGuides() {
    return new Promise(function(resolve, reject) {
        var publicPlaceGuides = []
        resolve(PlaceGuide.constructPlaceGuideBasedOnPlaceId(
          "id2", "PlaceGuide 2", "This is a placeguide 2",
          "audioKey2", 4, "imgKey2",
          "ChIJM3bYao8OSUcRpHGkCCTD9yM", "creatorId2",
          "creatorName2", PlaceType.PUBLIC)
          .then(newPlaceGuide => publicPlaceGuides.push(newPlaceGuide))
          .then(() => {return publicPlaceGuides}));
    });
  }
}
//     var publicPlaceGuides = [
//     //   new PlaceGuide("id1", "PlaceGuide 1", "This is a placeguide 1",
//     //       "audioKey1", 3, "imgKey1",
//     //       46.731686, 23.604655, null, "creatorId1",
//     //       "creatorName1", PlaceType.PUBLIC),
//       PlaceGuide.constructPlaceGuideBasedOnPlaceId("id2", "PlaceGuide 2", "This is a placeguide 2",
//           "audioKey2", 4, "imgKey2",
//           "ChIJM3bYao8OSUcRpHGkCCTD9yM", "creatorId2",
//           "creatorName2", PlaceType.PUBLIC)
//     //   new PlaceGuide("id2", "PlaceGuide 2", "This is a placeguide 2",
//     //       "audioKey2", 4, "imgKey2",
//     //       0, 0, "ChIJM3bYao8OSUcRpHGkCCTD9yM", "creatorId2",
//     //       "creatorName2", PlaceType.PUBLIC),
//     //   new PlaceGuide("id3", "PlaceGuide 3", "This is a placeguide 3",
//     //       "audioKey3", 2, "imgKey3",
//     //       0, 0,  "ChIJH-tBOc4EdkgRJ8aJ8P1CUxo", "creatorId3",
//     //       "creatorName3", PlaceType.PUBLIC),
//     //   new PlaceGuide("id4", "PlaceGuide 4", "This is a placeguide 4",
//     //       "audioKey4", 12, "imgKey4",
//     //       46.295773, 2.465071, null, "creatorId4",
//     //       "creatorName4", PlaceType.PUBLIC),
//     //   new PlaceGuide("id5", "PlaceGuide 5", "This is a placeguide 5",
//     //       "audioKey5", 8, "imgKey5",
//     //       39.751611, -3.163685, null, "creatorId5",
//     //       "creatorName5", PlaceType.PUBLIC),
//     //   new PlaceGuide("id6", "PlaceGuide 6", "This is a placeguide 6",
//     //       "audioKey6", 9, "imgKey6",
//     //       31.257349, 108.751543, null, "creatorId6",
//     //       "creatorName6", PlaceType.PUBLIC),
//     //   new PlaceGuide("id7", "PlaceGuide 7", "This is a placeguide 7",
//     //       "audioKey7", 9, "imgKey7",
//     //       53.119633, -88.482046, null, "creatorId7",
//     //       "creatorName7", PlaceType.PUBLIC),
//     //   new PlaceGuide("id8", "PlaceGuide 8", "This is a placeguide 8",
//     //       "audioKey8", 4, "imgKey8",
//     //       51.834982, -86.723060, null, "creatorId8",
//     //       "creatorName8", PlaceType.PUBLIC),
//     //   new PlaceGuide("id9", "PlaceGuide 9", "This is a placeguide 9",
//     //       "audioKey9", 6, "imgKey9",
//     //       46.770003, 23.509997, null, "creatorId9",
//     //       "creatorName9", PlaceType.PUBLIC),
//     ];
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