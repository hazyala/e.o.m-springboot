(function () {
    var typewriter = document.querySelector('[data-typewriter-text]');
    var fullText = typewriter ? typewriter.getAttribute('data-typewriter-text') : '';
    var index = 0;

    function typeNext() {
        if (!typewriter) {
            return;
        }
        if (index <= fullText.length) {
            typewriter.textContent = fullText.slice(0, index);
            index += 1;
            window.setTimeout(typeNext, 100);
            return;
        }

        typewriter.classList.add('finished');
    }

    if (typewriter) {
        typeNext();
    }

    document.querySelectorAll('[data-section-link]').forEach(function (link) {
        link.addEventListener('click', function (event) {
            event.preventDefault();
            var section = document.getElementById(link.getAttribute('data-section-link'));
            if (section) {
                window.scrollTo({top: section.offsetTop - 70, behavior: 'smooth'});
            }
        });
    });

    var topLink = document.querySelector('[data-scroll-top]');
    if (topLink) {
        topLink.addEventListener('click', function (event) {
            if (window.location.pathname === '/') {
                event.preventDefault();
                window.scrollTo({top: 0, behavior: 'smooth'});
            }
        });
    }

    var revealObserver = new IntersectionObserver(function (entries) {
        entries.forEach(function (entry) {
            if (entry.isIntersecting) {
                entry.target.classList.add('is-visible');
                revealObserver.unobserve(entry.target);
            }
        });
    }, {threshold: 0.18});

    document.querySelectorAll('.reveal-section, .reveal-up, .reveal-left, .reveal-right').forEach(function (element) {
        revealObserver.observe(element);
    });
})();

