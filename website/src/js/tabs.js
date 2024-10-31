function onTabClick(event) {
    let container = event.target.parentElement.parentElement.parentElement
    console.log(container)
    if (container.className === "nav-container") {
        let activeTabs = container.querySelectorAll('.active');

        // deactivate existing active tab and panel
        activeTabs.forEach((tab) => {
            tab.className = tab.className.replace('active', '');
        });

        // activate new tab and panel
        event.target.parentElement.className += ' active';
        let newActiveTab = container.querySelector('div[data-id="' + event.target.dataset.id + '"]')
        newActiveTab.className += ' active';
        // document.getElementById(event.target.dataset.id).className += ' active';
    }
}

const elements = document.getElementsByClassName('nav');

for (let element of elements) {
    element.addEventListener('click', onTabClick, false);
}