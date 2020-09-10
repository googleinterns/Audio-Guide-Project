/**
 * This class is responsible for connecting all the placeGuide-related
 * objects: the two displayers and the repository.
 * When the map's bounds are changed, PlaceGuideManager
 * sends the needed commands to all classes involved in
 * executing it(PlaceGudieRepository, Map- and ListPlaceGuideDisplayer).
 */
class PlaceGuideManager {
  static MAX_NO_BOOKMARKED_GUIDES = 25;
  static PAGE = {
    DISCOVER: {
      query: PlaceGuideRepository.QUERY_TYPE.ALL_PUBLIC_IN_MAP_AREA,
      onGuideBookmarkStatusChanged: undefined,
    },
    MY_GUIDES: {
      query: PlaceGuideRepository.QUERY_TYPE.CREATED_ALL_IN_MAP_AREA,
      onGuideBookmarkStatusChanged: undefined,
    },
    CREATE_PLACE_GUIDE: {
      query: PlaceGuideRepository.QUERY_TYPE.CREATED_ALL_IN_MAP_AREA,
      onGuideBookmarkStatusChanged: undefined,
    },
    BOOKMARKED_PLACEGUIDES: {
      query: PlaceGuideRepository.QUERY_TYPE.BOOKMARKED,
      onGuideBookmarkStatusChanged: PlaceGuideManager.removeGuideIfUnbookmarked,
    }
  };

  constructor(page, map) {
    this._page = page;
    this._placeGuideRepository = new PlaceGuideRepository();
    this._highlightedPlaceGuideId = null;
    this._mapPlaceGuideDisplayer = new MapPlaceGuideDisplayer();
    this._listPlaceGuideDisplayer = new ListPlaceGuideDisplayer();
    let thisManager = this;
    google.maps.event.addListenerOnce(map, 'idle', function () {
      thisManager.refreshPlaceGuides(map.getBounds(), map.getZoom());
    });
    if (this._page != PlaceGuideManager.BOOKMARKED_PLACEGUIDES) {
      google.maps.event.addListener(map, 'idle', function () {
        thisManager.refreshPlaceGuides(map.getBounds(), map.getZoom());
      });
    }
  }

  refreshPlaceGuides(bounds, zoom) {
    this._placeGuideRepository.fetchPlaceGuides(this._page.query, bounds, zoom)
        .then((response) => {
          const placeGuides = this._placeGuideRepository.placeGuides;
          this._mapPlaceGuideDisplayer.update(placeGuides);
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
        if(response == PlaceGuideRepository.BOOKMARK_ACTION_RESULT_TYPE.SUCCESS) {
          if (this._page.onGuideBookmarkStatusChanged != undefined) {
            this._page.onGuideBookmarkStatusChanged(this, placeGuideId);
          }
        } else if(response == PlaceGuideRepository.BOOKMARK_ACTION_RESULT_TYPE.SUCCESS) {
          alert(`You can't bookmark more than ${MAX_NO_BOOKMARKED_GUIDES} gudies. Please unbookmark some of them before bookmarking a new one`);
        } else {
          alert("Failed to execute bookmarking/unbookmarking");
        }
      });
  }

  static removeGuideIfUnbookmarked(placeGuideManager, placeGuideId) {
    if(!placeGuideManager._placeGuideRepository.isBookmarked(placeGuideId)) {
      placeGuideManager.removePlaceGuideRepresentation(placeGuideId);
    }
  }
}
