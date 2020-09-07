/**
 * This class is responsible for connecting all the placeGuide-related
 * objects: the two displayers and the repository.
 * When an event occurs, PlaceGuideManager is triggered and it
 * sends the commands to the
 * classes which are responsible for handling the events.
 */
class PlaceGuideManager {
  static PAGE = {
    DISCOVER: {
        query: PlaceGuideRepository.QUERY_TYPE.ALL_PUBLIC_IN_MAP_AREA,
    },
    MY_GUIDES: {
        query: PlaceGuideRepository.QUERY_TYPE.CREATED_ALL_IN_MAP_AREA,
    },
    CREATE_PLACE_GUIDE: {
        query: PlaceGuideRepository.QUERY_TYPE.CREATED_ALL_IN_MAP_AREA,
    },
    BOOKMARKED_PLACEGUIDES: {
        query: PlaceGuideRepository.QUERY_TYPE.BOOKMARKED,
    }
  };

  constructor(page, map) {
    this._page = page;
    this._placeGuideRepository = new PlaceGuideRepository();
    this._highlightedPlaceGuideId = null;
    this._mapPlaceGuideDisplayer = new MapPlaceGuideDisplayer();
    this._listPlaceGuideDisplayer = new ListPlaceGuideDisplayer();
    this.update(map.getBounds(), 
                map.getZoom(), 
                this._page === PlaceGuideManager.BOOKMARKED_PLACEGUIDES);
    if (this._page != PlaceGuideManager.BOOKMARKED_PLACEGUIDES) {
      let thisManager = this;
      google.maps.event.addListener(map, 'idle', function () {
        thisManager.update(map.getBounds(), map.getZoom());
      });
    }
  }

  update(bounds, zoom) {
    this._placeGuideRepository.updatePlaceGuides(this._page.query, bounds, zoom)
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
    this._placeGuideRepository.remove(placeGuideId);
    this.removePlaceGuideRepresentation(placeGuideId);
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
    this._placeGuideRepository.togglePlaceGuideBookmarkStatus(placeGuideId);
    if (this._page === PlaceGuideManager.PAGE.BOOKMARKED_PLACEGUIDES &&
        !this._placeGuideRepository.isBookmarked(placeGuideId)) {
        this.removePlaceGuideRepresentation(placeGuideId);
    }
  }
}
