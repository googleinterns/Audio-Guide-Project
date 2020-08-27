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
        for (var i = 0; i < NO_TABS; i++) {
            var tab = this.createMenuTab(i,  i == pageName.index);
            document.querySelector('.mdc-tab-scroller__scroll-content').appendChild(tab);
        }
        const tabBar = new mdc.tabBar.MDCTabBar(document.querySelector('.mdc-tab-bar'));
        const tabs = document.querySelectorAll('.mdc-tab');
        tabs[pageName.index].focus();
        tabBar.listen('MDCTabBar:activated', function(event) {
            var url = new URL(Menu.PAGE_NUMBERS[event.detail.index].url, document.URL);
            window.location = url;
        });
    }

    createMenuTab(index, focused) {
        var tab = document.createElement("button");
        if (focused) {
            tab.setAttribute("class", "mdc-tab mdc-tab--active");
        } else {
            tab.setAttribute("class", "mdc-tab");
        }
        tab.setAttribute("role", "tab");
        tab.setAttribute("aria-selected", "true");
        tab.setAttribute("tabindex", "0");
        tab.appendChild(this.createTabContent(index));
        tab.appendChild(this.createTabIndicator(focused));
        tab.appendChild(this.createTabRipple());
        return tab;
    }

    createTabContent(index) {
        var content = document.createElement("span");
        content.setAttribute("class", "mdc-tab__content");
        content.appendChild(this.createIcon(index));
        content.appendChild(this.createLabel(index));
        return content;
    }

    createTabIndicator(focused) {
        var indicator = document.createElement("span");
        if (focused) {
            indicator.setAttribute("class", "mdc-tab-indicator mdc-tab-indicator--active")
        } else {
            indicator.setAttribute("class", "mdc-tab-indicator");
        }
        indicator.appendChild(this.createIndicatorContent());
        return indicator;
    }

    createTabRipple() {
        var ripple = document.createElement("span");
        ripple.setAttribute("class", "mdc-tab__ripple");
        return ripple;
    }

    createIndicatorContent() {
        var indicatorContent = document.createElement("span");
        indicatorContent.setAttribute("class", "mdc-tab-indicator__content mdc-tab-indicator__content--underline");
        return indicatorContent;
    }

    createLabel(index) {
        var label = document.createElement("span");
        label.setAttribute("class", "mdc-tab__text-label");
        label.innerText = Menu.PAGE_NUMBERS[index].label;
        return label;
    }

    createIcon(index) {
        var icon = document.createElement("span");
        icon.setAttribute("class", "mdc-tab__icon material-icons");
        icon.setAttribute("aria-hidden", true)
        icon.innerText = Menu.PAGE_NUMBERS[index].icon;
        return icon;
    }
}
