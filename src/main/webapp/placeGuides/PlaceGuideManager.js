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
      onGuideBookmarkStatusChanged: PlaceGuideManager.toogleBookmarkIcon,
      name: "DISCOVER"
    },
    MY_GUIDES: {
      query: PlaceGuideRepository.QUERY_TYPE.CREATED_ALL_IN_MAP_AREA,
      onGuideBookmarkStatusChanged: PlaceGuideManager.toogleBookmarkIcon,
      name: "MY_GUIDES"
    },
    CREATE_PLACE_GUIDE: {
      query: PlaceGuideRepository.QUERY_TYPE.CREATED_ALL_IN_MAP_AREA,
      onGuideBookmarkStatusChanged: PlaceGuideManager.toogleBookmarkIcon,
      name: "CREATE_PLACE_GUIDE"
    },
    BOOKMARKED_PLACEGUIDES: {
      query: PlaceGuideRepository.QUERY_TYPE.BOOKMARKED,
      onGuideBookmarkStatusChanged: PlaceGuideManager.removeGuideIfUnbookmarked,
      name: "BOOKMARKED_PLACEGUIDES"
    },
    USERS_PORTFOLIO: {
      query: PlaceGuideRepository.QUERY_TYPE.CREATED_BY_GIVEN_USER_PUBLIC_IN_MAP_AREA,
      guideBookmarkStatusChanged: undefined,
      name: "USERS_PORTFOLIO"
    }
  };

  constructor(page, map, portfolioUserId) {
    this._page = page;
    this._placeGuideRepository = new PlaceGuideRepository();
    this._highlightedPlaceGuideId = null;
    this._mapPlaceGuideDisplayer = new MapPlaceGuideDisplayer();
    this._listPlaceGuideDisplayer = new ListPlaceGuideDisplayer(page);
    let thisManager = this;
    google.maps.event.addListenerOnce(map, 'idle', function () {
      thisManager.refreshPlaceGuides(map.getBounds(), map.getZoom(), portfolioUserId);
    });
    if (this._page != PlaceGuideManager.PAGE.BOOKMARKED_PLACEGUIDES) {
      google.maps.event.addListener(map, 'idle', function () {
        thisManager.refreshPlaceGuides(map.getBounds(), map.getZoom(), portfolioUserId);
      });
    }
  }

  refreshPlaceGuides(bounds, zoom, portfolioUserId) {
    this._placeGuideRepository.fetchPlaceGuides(this._page.query, bounds, zoom, portfolioUserId)
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
    this._listPlaceGuideDisplayer.highlight(placeGuideId,
        this._placeGuideRepository.location(placeGuideId));
  }

  unhighlightPlaceGuide() {
    this._mapPlaceGuideDisplayer.unhighlight(this._highlightedPlaceGuideId);
    this._listPlaceGuideDisplayer.unhighlight(this._highlightedPlaceGuideId);
    this._highlightedPlaceGuideId = null;
  }

  toggleBookmark(placeGuideId) {
    this._placeGuideRepository.togglePlaceGuideBookmarkStatus(placeGuideId)
      .then((response) => {
        if(response === PlaceGuideRepository.BOOKMARK_ACTION_RESULT_TYPE.SUCCESS) {
          if (this._page.onGuideBookmarkStatusChanged != undefined) {
            this._page.onGuideBookmarkStatusChanged(this, placeGuideId);
          }
        } else if(response === PlaceGuideRepository.BOOKMARK_ACTION_RESULT_TYPE.NOT_ALLOWED) {
          alert(`You can't bookmark more than ${PlaceGuideManager.MAX_NO_BOOKMARKED_GUIDES} gudies. Please unbookmark some of them before bookmarking a new one`);
        } else {
          alert("Failed to execute bookmarking/unbookmarking");
        }
      });
  }

  static removeGuideIfUnbookmarked(placeGuideManager, placeGuideId) {
    if (!placeGuideManager._placeGuideRepository.isBookmarked(placeGuideId)) {
      placeGuideManager.removePlaceGuideRepresentation(placeGuideId);
    }
  }

  static toogleBookmarkIcon(placeGuideManager, placeGuideId) {
    if (placeGuideManager._placeGuideRepository.isBookmarked(placeGuideId)) {
      placeGuideManager.setBookmarked(placeGuideId);
    } else {
      placeGuideManager.setUnBookmarked((placeGuideId));
    }
  }

  setBookmarked(placeGuideId) {
    this._listPlaceGuideDisplayer.bookmark(placeGuideId);
  }

  setUnBookmarked(placeGuideId) {
    this._listPlaceGuideDisplayer.unbookmark(placeGuideId);
  }
}
