/**
 * This class is responsible for connecting all the placeGuide-related
 * objects: the two displayers and the repository.
 * When the map's bounds are changed, PlaceGuideManager
 * sends the needed commands to all classes involved in
 * executing it(PlaceGudieRepository, Map- and ListPlaceGuideDisplayer).
 */
class PlaceGuideManager {
  static PAGE = {
    DISCOVER: {
      query: PlaceGuideRepository.QUERY_TYPE.ALL_PUBLIC_IN_MAP_AREA,
      guideBookmarkStatusChanged: undefined,
      name: "DISCOVER"
    },
    MY_GUIDES: {
      query: PlaceGuideRepository.QUERY_TYPE.CREATED_ALL_IN_MAP_AREA,
      guideBookmarkStatusChanged: undefined,
      name: "MY_GUIDES"
    },
    CREATE_PLACE_GUIDE: {
      query: PlaceGuideRepository.QUERY_TYPE.CREATED_ALL_IN_MAP_AREA,
      guideBookmarkStatusChanged: undefined,
      name: "CREATE_PLACE_GUIDE"
    },
    BOOKMARKED_PLACEGUIDES: {
      query: PlaceGuideRepository.QUERY_TYPE.BOOKMARKED,
      guideBookmarkStatusChanged: PlaceGuideManager.removeGuideIfUnbookmarked,
      name: "BOOKMARKED_PLACEGUIDES"
    }
  };

  constructor(page, map) {
    this._page = page;
    this._placeGuideRepository = new PlaceGuideRepository();
    this._highlightedPlaceGuideId = null;
    this._mapPlaceGuideDisplayer = new MapPlaceGuideDisplayer();
    this._listPlaceGuideDisplayer = new ListPlaceGuideDisplayer(page);
    let thisManager = this;
    google.maps.event.addListenerOnce(map, 'idle', function () {
      thisManager.refreshPlaceGuides(map.getBounds(), map.getZoom());
    });
    if (this._page != PlaceGuideManager.PAGE.BOOKMARKED_PLACEGUIDES) {
      google.maps.event.addListener(map, 'idle', function () {
        thisManager.refreshPlaceGuides(map.getBounds(), map.getZoom());
      });
    }
  }

  refreshPlaceGuides(bounds, zoom) {
    this._placeGuideRepository.fetchPlaceGuides(this._page.query, bounds, zoom)
        .then((placeGuides) => {
          this._listPlaceGuideDisplayer.update(placeGuides);
          this._mapPlaceGuideDisplayer.update(placeGuides);
          if (this._page === PlaceGuideManager.PAGE.BOOKMARKED_PLACEGUIDES) {
            this._mapPlaceGuideDisplayer.adjustMapToShowAll();
          }
        });
  }

  removePlaceGuide(placeGuideId) {
    this._placeGuideRepository.removePlaceGuide(placeGuideId)
      .then((response) => {
        if(response) {
          this.removePlaceGuideRepresentation(placeGuideId);
        }
      });
  }

  removePlaceGuideRepresentation(placeGuideId) {
    this._mapPlaceGuideDisplayer.remove(placeGuideId);
    this._listPlaceGuideDisplayer.remove(placeGuideId);
  }

  highlightPlaceGuide(placeGuideId) {
    if (this._highlightedPlaceGuideId !== null) {
      this.unhighlightPlaceGuide();
    }
    this._highlightedPlaceGuideId = placeGuideId;
    this._mapPlaceGuideDisplayer.highlight(placeGuideId);
    this._listPlaceGuideDisplayer.highlight(placeGuideId);
  }

  unhighlightPlaceGuide() {
    this._mapPlaceGuideDisplayer.unhighlight(this._highlightedPlaceGuideId);
    this._listPlaceGuideDisplayer.unhighlight(this._highlightedPlaceGuideId);
    this._highlightedPlaceGuideId = null;
  }

  toggleBookmark(placeGuideId) {
    this._placeGuideRepository.togglePlaceGuideBookmarkStatus(placeGuideId)
      .then((response) => {
        if(response) {
          if (this._page.guideBookmarkStatusChanged != undefined) {
            this._page.guideBookmarkStatusChanged(this, placeGuideId);
          }
        }
      });
  }

  static removeGuideIfUnbookmarked(placeGuideManager, placeGuideId) {
    if(!placeGuideManager._placeGuideRepository.isBookmarked(placeGuideId)) {
      placeGuideManager.removePlaceGuideRepresentation(placeGuideId);
    }
  }
}
