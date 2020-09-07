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
        query: PlaceGudieRepository.QUERTY_TYPE.ALL_PUBLIC_IN_MAP_AREA,
        listTitle: "Discover Guides",
        subtitle: "in selected map area"
    },
    MY_GUIDES: {
        query: PlaceGudieRepository.QUERTY_TYPE.CREATED_ALL_IN_MAP_AREA,
        listTitle: "My Guides",
        subtitle: "in selected map area"
    },
    CREATE_PLACE_GUIDE: {
        query: PlaceGudieRepository.QUERTY_TYPE.CREATED_ALL_IN_MAP_AREA,
        listTitle: "",
        subtitle: ""
    },
    BOOKMARKED_PLACEGUIDES: {
        query: PlaceGudieRepository.QUERTY_TYPE.BOOKMARKED,
        listTitle: "Bookmarked Guides",
        subtitle: ""
    }
  };

  constructor(page) {
    this._page = page;
    this._placeGuideRepository = new PlaceGuideRepository();
    this._highlightedPlaceGuideId = null;
    this._mapPlaceGuideDisplayer = new MapPlaceGuideDisplayer();
    this._listPlaceGuideDisplayer = new ListPlaceGuideDisplayer();
  }

  update(bounds, zoom, showAll) {
    this._placeGuideRepository.updatePlaceGuides(this._page.query, bounds, zoom)
        .then((response) => {
          const placeGuides = this._placeGuideRepository.placeGuides;
          this._mapPlaceGuideDisplayer.update(placeGuides);
          this._mapPlaceGuideDisplayer.update(placeGuides);
          if (showAll) {
              this._mapPlaceGuideDisplayer.adjustMapToShowAll();
          }
        });
  }

  removePlaceGuide(placeGuideId) {
    this._placeGuideRepository.remove(placeGuideId);
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
}
