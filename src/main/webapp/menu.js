class Menu {
    static PAGE_NAMES = {
        INDEX: {
            url: "index.html",
            index: 0,
            icon: "account_circle",
            label: "My Portfolio"
        },
        DISCOVER: {
            url: "discover.html",
            index: 1,
            icon: "search",
            label: "Discover"
        },
        MY_PLACEGUIDES: {
            url: "myPlaceGuides.html",
            index: 2,
            icon: "place",
            label: "My Guides"
        },
        CREATE_PLACEGUIDE: {
            url: "createPlaceGuide.html",
            index: 3,
            icon: "create",
            label: "Create Guide"
        },
        BOOKMARKED_PLACEGUIDES: {
            url: "index.html",
            index: 4,
            icon: "bookmark",
            label: "Bookmarked"
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
        const NO_TABS = 5;
        const tabBar = new mdc.tabBar.MDCTabBar(document.querySelector('.mdc-tab-bar'));
        var tabs = [];
        for (var i = 0; i < NO_TABS; i++) {
            var tab = this.createMenuTab(i);
            document.querySelector('.mdc-tab-scroller__scroll-content').appendChild(tab);
            tabs.push(tab);
        }
        tabBar.activateTab(pageName.index);
        tabBar.listen('MDCTabBar:activated', function(event) {
            console.log("activated tab: " + event.detail.index);
            var url = new URL(Menu.PAGE_NUMBERS[event.detail.index].url, document.URL);
            window.location = url;
        });
    }

    createMenuTab(index) {
        var tab = document.createElement("button");
        tab.setAttribute("class", "mdc-tab mdc-tab--active");
        tab.setAttribute("role", "tab");
        tab.setAttribute("aria-selected", "true");
        tab.setAttribute("tabindex", "0");
        tab.innerHTML = "<span class=\"mdc-tab__content\">\
                                <span class=\"mdc-tab__icon material-icons\" aria-hidden=\"true\">" + Menu.PAGE_NUMBERS[index].icon + "</span>\
                                <span class=\"mdc-tab__text-label\">" + Menu.PAGE_NUMBERS[index].label + "</span>\
                            </span>\
                            <span class=\"mdc-tab-indicator\">\
                                <span class=\"mdc-tab-indicator__content mdc-tab-indicator__content--underline\"></span>\
                            </span>\
                            <span class=\"mdc-tab__ripple\"></span>";
        return tab;
    }
}
