/**
 * This class is responsible for getting and displaying the places corresponding to a query.
 * For now, the places' data is hardcoded, but it needs to be fetched from the server in the future.
 */
class PlaceDisplayer {
  static displayPublicPlaceGuides(map) {
    PlaceDisplayer.getPublicPlaceGuides(map)
        .then(placeGuides => {
          var markers = placeGuides.map(function (placeGuide) {
            return placeGuide._place._marker;
          });
          var markerCluster = new MarkerClusterer(map, markers,
              {imagePath: './img/m'});
        });
  }

  static displayPlaceGuidesOfUser(map) {
    PlaceDisplayer.getPlaceGuidesOfUser(map)
        .then(placeGuides => {
          var markers = placeGuides.map(function (placeGuide) {
            return placeGuide._place._marker;
          });
          var markerCluster = new MarkerClusterer(map, markers,
              {imagePath: './img/m'});
        });
  }

  // This is for demonstrational purposes only. Data will be fetched from the server.
  // TODO(fbori): fetch all public placeGuides from servlet
  static getPublicPlaceGuides(map) {
    var placeGuidesData = [
      {
        id: "id1", name: "PlaceGuide 1", description: "This is a placeguide 1",
        audioKey: "audioKey1", audioLength: 3, imageKey: "imageKey1",
        positionLat: 46.731686, positionLng: 23.604655, placeId: null, creatorId: "creatorId1",
        creatorName: "creatorName1", placeType: PlaceType.PUBLIC
      },
      {
        id: "id2",
        name: "PlaceGuide 2",
        description: "This is a placeguide 2",
        audioKey: "audioKey2",
        audioLength: 2,
        imageKey: "imageKey2",
        positionLat: 0,
        positionLng: 0,
        placeId: "ChIJM3bYao8OSUcRpHGkCCTD9yM",
        creatorId: "creatorId2",
        creatorName: "creatorName2",
        placeType: PlaceType.PUBLIC
      },
      {
        id: "id3", name: "PlaceGuide 3", description: "This is a placeguide 3",
        audioKey: "audioKey3", audioLength: 5, imageKey: "imageKey3",
        positionLat: 46.295773, positionLng: 2.465071, placeId: null, creatorId: "creatorId3",
        creatorName: "creatorName3", placeType: PlaceType.PUBLIC
      },
      {
        id: "id4",
        name: "PlaceGuide 4",
        description: "This is a placeguide 4",
        audioKey: "audioKey4",
        audioLength: 5,
        imageKey: "imageKey4",
        positionLat: 0,
        positionLng: 0,
        placeId: "ChIJH-tBOc4EdkgRJ8aJ8P1CUxo",
        creatorId: "creatorId4",
        creatorName: "creatorName4",
        placeType: PlaceType.PUBLIC
      },
      {
        id: "id5", name: "PlaceGuide 5", description: "This is a placeguide 5",
        audioKey: "audioKey5", audioLength: 5, imageKey: "imageKey5",
        positionLat: 39.751611, positionLng: -3.163685, placeId: null, creatorId: "creatorId4",
        creatorName: "creatorName5", placeType: PlaceType.PUBLIC
      },
      {
        id: "id6", name: "PlaceGuide 6", description: "This is a placeguide 6",
        audioKey: "audioKey6", audioLength: 6, imageKey: "imageKey6",
        positionLat: 37.751611, positionLng: -3.163685, placeId: null, creatorId: "creatorId6",
        creatorName: "creatorName6", placeType: PlaceType.PUBLIC
      },
      {
        id: "id7", name: "PlaceGuide 7", description: "This is a placeguide 7",
        audioKey: "audioKey7", audioLength: 7, imageKey: "imageKey7",
        positionLat: 32.751711, positionLng: -5.173785, placeId: null, creatorId: "creatorId7",
        creatorName: "creatorName7", placeType: PlaceType.PUBLIC
      },
      {
        id: "id8",
        name: "PlaceGuide 8",
        description: "This is a placeguide 8",
        audioKey: "audioKey8",
        audioLength: 8,
        imageKey: "imageKey8",
        positionLat: 0,
        positionLng: 0,
        placeId: "ChIJC4Y5_qTBQUcRoZpDUKb2pEw",
        creatorId: "creatorId8",
        creatorName: "creatorName8",
        placeType: PlaceType.PUBLIC
      },
    ];

    return new Promise(function (resolve, reject) {
      var publicPlaceGuides = [];
      var promises = [];
      for (var i = 0; i < placeGuidesData.length; i++) {
        var pg = placeGuidesData[i];
        if (pg.placeId == null) {
          publicPlaceGuides.push(
              PlaceGuide.constructPlaceGuideBasedOnCoordinates(map, pg.id, pg.name, pg.description,
                  pg.audioKey, pg.audioLength, pg.imageKey, pg.positionLat, pg.positionLng,
                  pg.creatorId, pg.creatorName, pg.placeType));
        } else {
          promises.push(new Promise(function (resolve, reject) {
            PlaceGuide.constructPlaceGuideBasedOnPlaceId(map, pg.id, pg.name, pg.description,
                pg.audioKey, pg.audioLength,
                pg.imageKey, pg.placeId, pg.creatorId, pg.creatorName, pg.placeType)
                .then(placeGuide => {
                  publicPlaceGuides.push(placeGuide);
                  resolve()
                });
          }));
        }
      }
      Promise.all(promises).then(() => {
        resolve(publicPlaceGuides)
      });
    });
  }

  // This is for demonstrational purposes only. Data will be fetched from the server.
  // TODO(fbori): fetch all place guides of the currently logged in user
  static getPlaceGuidesOfUser(map) {
    var placeGuidesData = [
      {
        id: "id1", name: "PlaceGuide 1", description: "This is a placeguide 1",
        audioKey: "audioKey1", audioLength: 3, imageKey: "imageKey1",
        positionLat: 46.731686, positionLng: 23.604655, placeId: null, creatorId: "creatorId1",
        creatorName: "creatorName1", placeType: PlaceType.PUBLIC
      },
      {
        id: "id2",
        name: "PlaceGuide 2",
        description: "This is a placeguide 2",
        audioKey: "audioKey2",
        audioLength: 2,
        imageKey: "imageKey2",
        positionLat: 0,
        positionLng: 0,
        placeId: "ChIJM3bYao8OSUcRpHGkCCTD9yM",
        creatorId: "creatorId2",
        creatorName: "creatorName2",
        placeType: PlaceType.PRIVATE
      },
      {
        id: "id3", name: "PlaceGuide 3", description: "This is a placeguide 3",
        audioKey: "audioKey3", audioLength: 5, imageKey: "imageKey3",
        positionLat: 46.295773, positionLng: 2.465071, placeId: null, creatorId: "creatorId3",
        creatorName: "creatorName3", placeType: PlaceType.PRIVATE
      },
      {
        id: "id4",
        name: "PlaceGuide 4",
        description: "This is a placeguide 4",
        audioKey: "audioKey4",
        audioLength: 5,
        imageKey: "imageKey4",
        positionLat: 0,
        positionLng: 0,
        placeId: "ChIJH-tBOc4EdkgRJ8aJ8P1CUxo",
        creatorId: "creatorId4",
        creatorName: "creatorName4",
        placeType: PlaceType.PUBLIC
      },
      {
        id: "id5", name: "PlaceGuide 5", description: "This is a placeguide 5",
        audioKey: "audioKey5", audioLength: 5, imageKey: "imageKey5",
        positionLat: 39.751611, positionLng: -3.163685, placeId: null, creatorId: "creatorId4",
        creatorName: "creatorName5", placeType: PlaceType.PUBLIC
      },
    ];

    return new Promise(function (resolve, reject) {
      var placeGuidesOfUser = [];
      var promises = [];
      for (var i = 0; i < placeGuidesData.length; i++) {
        var pg = placeGuidesData[i];
        if (pg.placeId == null) {
          placeGuidesOfUser.push(
              PlaceGuide.constructPlaceGuideBasedOnCoordinates(map, pg.id, pg.name, pg.description,
                  pg.audioKey, pg.audioLength, pg.imageKey, pg.positionLat, pg.positionLng, pg.creatorId,
                  pg.creatorName, pg.placeType));
        } else {
          promises.push(new Promise(function (resolve, reject) {
            PlaceGuide.constructPlaceGuideBasedOnPlaceId(map, pg.id, pg.name, pg.description,
                pg.audioKey, pg.audioLength,
                pg.imageKey, pg.placeId, pg.creatorId, pg.creatorName, pg.placeType)
                .then(placeGuide => {
                  placeGuidesOfUser.push(placeGuide);
                  resolve()
                });
          }));
        }
      }
      Promise.all(promises).then(() => {
        resolve(placeGuidesOfUser)
      });
    });
  }
}