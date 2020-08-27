class Menu {
    static PAGE_NAMES = {
        INDEX: {
            url: index.html,
            index: 0
        },
        DISCOVER: {
            url: discover.html,
            index: 1
        },
        MY_PLACEGUIDES: {
            url: myPlaceGuides.html,
            index: 2
        },
        CREATE_PLACEGUIDE: {
            url: createPlaceGudie.html,
            index: 3
        },
        BOOKMARKED_PLACEGUIDES: {
            url: index.html,
            index: 4
        }
    }

    static PAGE_NUMBERS = {
        0: Menu.PAGE_NAMES.INDEX,
        1: Menu.PAGE_NAMES.DISCOVER,
        2: Menu.PAGE_NAMES.MY_PLACEGUIDES,
        3: Menu.PAGE_NAMES.CREATE_PLACEGUIDE,
        4: Menu.PAGE_NAMES.BOOKMARKED_PLACEGUIDES
    }

    constructor(pageName) {
        const tabBar = new mdc.tabBar.MDCTabBar(document.querySelector('.mdc-tab-bar'));
        const tabs = document.querySelectorAll('.mdc-tab');
        tabBar.activate(pageName.index);
        tabBar.listen('MDCTabBar:activated', function(event) {
            var url = new Url(Menu.PAGE_NUMBERS[event.detail.index].url, document.URL);
            window.location = url;
        });
    }
}
