(function () {
    var storageKey = 'eom-theme';
    var root = document.documentElement;
    var savedTheme = 'dark';
    try {
        savedTheme = localStorage.getItem(storageKey) === 'light' ? 'light' : 'dark';
    } catch (error) {
        // Keep the default theme when storage is unavailable.
    }

    root.setAttribute('data-theme', savedTheme);

    function syncButtons() {
        document.querySelectorAll('[data-theme-toggle]').forEach(function (button) {
            button.textContent = root.getAttribute('data-theme') === 'dark' ? '☀' : '☾';
        });
    }

    function initButtons() {
        document.querySelectorAll('[data-theme-toggle]').forEach(function (button) {
            button.addEventListener('click', function () {
                var nextTheme = root.getAttribute('data-theme') === 'dark' ? 'light' : 'dark';
                root.setAttribute('data-theme', nextTheme);
                root.style.setProperty('--text-color', nextTheme === 'dark' ? '#ffffff' : '#000000');
                root.style.setProperty('--sub-text-color', nextTheme === 'dark' ? '#cccccc' : '#555555');
                if (document.body.classList.contains('dashboard-page')) {
                    document.body.style.backgroundColor = nextTheme === 'dark' ? '#121212' : '#ffffff';
                    document.body.style.color = nextTheme === 'dark' ? '#ffffff' : '#050505';
                    var header = document.querySelector('.dashboard-header');
                    if (header) {
                        header.style.backgroundColor = nextTheme === 'dark' ? '#0b0b0b' : '#ffffff';
                        header.style.color = nextTheme === 'dark' ? '#ffffff' : '#050505';
                        header.querySelectorAll('.dashboard-logo, .dashboard-nav a, .dashboard-add-button, .dashboard-account-link, .dashboard-logout, .dashboard-theme-toggle').forEach(function (element) {
                            element.style.color = nextTheme === 'dark' ? '#ffffff' : '#050505';
                        });
                        var search = header.querySelector('.dashboard-search');
                        if (search) search.style.backgroundColor = nextTheme === 'dark' ? '#242424' : '#f0f0f5';
                        var searchInput = header.querySelector('.dashboard-search input');
                        if (searchInput) searchInput.style.color = nextTheme === 'dark' ? '#ffffff' : '#050505';
                    }
                }
                // WebKit can retain the old colors in sticky and image layers.
                var scrollTop = document.scrollingElement.scrollTop;
                button.blur();
                var previousDisplay = document.body.style.display;
                document.body.style.display = 'none';
                void document.body.offsetHeight;
                document.body.style.display = previousDisplay;
                requestAnimationFrame(function () {
                    document.scrollingElement.scrollTop = scrollTop;
                });
                try {
                    localStorage.setItem(storageKey, nextTheme);
                } catch (error) {
                    // The current page still switches themes without storage.
                }
                syncButtons();
            });
        });
        syncButtons();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initButtons, { once: true });
    } else {
        initButtons();
    }
})();
