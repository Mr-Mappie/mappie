(() => {
  let activeElement = null;
  const siteWrapper = document.querySelector('.site-wrapper');
  const button = document.querySelector('[data-action="open-search"]');
  const input = document.querySelector('.pagefind-ui__search-input');
  const modal = document.querySelector('.modal-backdrop');

  if (!button || !modal) return;

  function openModal() {
    activeElement = document.activeElement;
    siteWrapper.setAttribute('inert', '');
    modal.classList.add('modal-backdrop--open');
    input.focus();
  }

  function closeModal() {
    siteWrapper.removeAttribute('inert');
    modal.classList.remove('modal-backdrop--open');
    activeElement.focus();
  }

  function handleKeyDown(e) {
    if (e.code === 'Escape') {
      closeModal();
    }

    if (e.ctrlKey && e.code === 'KeyK') {
      e.preventDefault();
      openModal();
    }
  }

  modal.addEventListener('click', (e) => {
    if (e.target === e.currentTarget) {
      closeModal();
    }
  });

  button.addEventListener('click', () => {
    openModal();
  });

  window.addEventListener('keydown', handleKeyDown);
})();
