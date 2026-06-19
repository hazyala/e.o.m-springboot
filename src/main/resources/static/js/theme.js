(function () {
    var storageKey = 'eom-theme';
    var root = document.documentElement;
    var savedTheme = localStorage.getItem(storageKey) || 'dark';

    root.setAttribute('data-theme', savedTheme);

    document.querySelectorAll('[data-theme-toggle]').forEach(function (button) {
        button.addEventListener('click', function () {
            var nextTheme = root.getAttribute('data-theme') === 'dark' ? 'light' : 'dark';
            root.setAttribute('data-theme', nextTheme);
            localStorage.setItem(storageKey, nextTheme);
        });
    });
})();

