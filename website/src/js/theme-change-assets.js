(() => {
  const htmlElement = document.querySelector('html');
  const systemMode = window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';

  function changeAssets(theme) {
    if (!theme) return;
    const themeAssets = document.querySelectorAll('img[data-theme-mode]');

    themeAssets.forEach((el) => {
      el.src = el.getAttribute(`data-${theme}-asset`);
    });
  }

  changeAssets(htmlElement.getAttribute('data-theme-mode') === 'system' ? systemMode : htmlElement.getAttribute('data-theme-mode'));

  const observer = new MutationObserver(() => {
    changeAssets(htmlElement.getAttribute('data-theme-mode') === 'system' ? systemMode : htmlElement.getAttribute('data-theme-mode'));
  });

  observer.observe(htmlElement, { attributes: true });
})();
