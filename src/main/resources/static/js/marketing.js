(function () {
    var doc = document.documentElement;
    var header = document.querySelector('[data-marketing-header]');
    var reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    var ticking = false;
    var sceneCache = [];

    function clamp(value, min, max) {
        return Math.max(min, Math.min(max, value));
    }

    function lerp(start, end, progress) {
        return start + (end - start) * progress;
    }

    function progressFor(rect, viewportHeight) {
        var total = rect.height + viewportHeight;
        return clamp((viewportHeight - rect.top) / total, 0, 1);
    }

    function cacheScenes() {
        sceneCache = Array.prototype.slice.call(document.querySelectorAll('[data-scene]')).map(function (element) {
            return {
                element: element,
                type: element.getAttribute('data-scene'),
                lines: Array.prototype.slice.call(element.querySelectorAll('[data-hero-line]')),
                cube: element.querySelector('[data-cube]'),
                cards: Array.prototype.slice.call(element.querySelectorAll('.board-motion-card')),
                rail: element.querySelector('[data-reel-rail]')
            };
        });
        update();
    }

    function updateHeader() {
        if (!header) {
            return;
        }
        header.classList.toggle('is-solid', window.scrollY > window.innerHeight * 0.82);
    }

    function updateHero(scene, progress) {
        var element = scene.element;
        element.style.setProperty('--hero-scale', String(lerp(1.04, 1.18, progress)));
        element.style.setProperty('--hero-y', lerp(0, 80, progress) + 'px');
        element.style.setProperty('--ghost-opacity', String(lerp(0.14, 0.38, progress)));
        element.style.setProperty('--ghost-x', lerp(0, -150, progress) + 'px');
        element.style.setProperty('--ghost-y', lerp(0, 52, progress) + 'px');

        scene.lines.forEach(function (line, index) {
            var direction = index % 2 === 0 ? -1 : 1;
            line.style.setProperty('--line-x', (direction * lerp(0, 120, progress)) + 'px');
            line.style.setProperty('--line-y', lerp(0, -38 * (index + 1), progress) + 'px');
            line.style.opacity = String(lerp(1, 0.55, progress));
        });
    }

    function updateCube(scene, progress) {
        if (!scene.cube) {
            return;
        }
        scene.cube.style.setProperty('--cube-rx', lerp(-24, 18, progress) + 'deg');
        scene.cube.style.setProperty('--cube-ry', lerp(-52, 308, progress) + 'deg');
        scene.cube.style.setProperty('--cube-rz', lerp(-5, 8, progress) + 'deg');
        scene.cube.style.setProperty('--cube-scale', String(lerp(0.78, 1.06, progress)));
        scene.cube.style.setProperty('--cube-y', lerp(80, -34, progress) + 'px');
    }

    function updateCards(scene, progress) {
        var spreadX = Math.min(window.innerWidth * 0.4, 520);
        var spreadY = Math.min(window.innerHeight * 0.24, 190);
        scene.cards.forEach(function (card, index) {
            var startSide = index % 2 === 0 ? 1 : -1;
            var lane = index - (scene.cards.length - 1) / 2;
            var enter = clamp((progress - index * 0.035) / 0.62, 0, 1);
            var settle = clamp((progress - 0.54) / 0.38, 0, 1);
            var x = lerp(startSide * (window.innerWidth * 0.72 + index * 34), lane * 104, enter);
            var y = lerp((index % 3 - 1) * 210, lane * 28, enter);
            var rotation = lerp(startSide * (18 + index * 4), lane * -5, enter);

            if (settle > 0) {
                x = lerp(x, Math.cos(index * 1.7) * spreadX, settle);
                y = lerp(y, Math.sin(index * 1.35) * spreadY, settle);
                rotation = lerp(rotation, (index % 2 === 0 ? 1 : -1) * 8, settle);
            }

            card.style.setProperty('--card-x', x + 'px');
            card.style.setProperty('--card-y', y + 'px');
            card.style.setProperty('--card-r', rotation + 'deg');
            card.style.setProperty('--card-scale', String(lerp(0.86, 1, enter)));
            card.style.zIndex = String(20 + index);
            card.style.opacity = String(lerp(0, 1, enter));
        });
    }

    function updateTypo(scene, progress) {
        var shift = -progress * Math.min(window.innerWidth * 0.72, 760);
        scene.element.style.setProperty('--typo-x', shift + 'px');
    }

    function updateReels(scene, progress) {
        if (!scene.rail) {
            return;
        }
        var overflow = Math.max(0, scene.rail.scrollWidth - window.innerWidth + 160);
        scene.rail.style.setProperty('--rail-x', (-overflow * progress) + 'px');
    }

    function updateFinal(scene, progress) {
        scene.element.style.setProperty('--final-scale', String(lerp(1.12, 1.02, progress)));
    }

    function update() {
        ticking = false;
        updateHeader();

        if (reducedMotion) {
            return;
        }

        var viewportHeight = window.innerHeight || 1;
        sceneCache.forEach(function (scene) {
            var rect = scene.element.getBoundingClientRect();
            var progress = progressFor(rect, viewportHeight);
            if (progress <= 0 || progress >= 1 && rect.bottom < 0) {
                return;
            }

            if (scene.type === 'hero') {
                updateHero(scene, progress);
            } else if (scene.type === 'cube') {
                updateCube(scene, progress);
            } else if (scene.type === 'cards') {
                updateCards(scene, progress);
            } else if (scene.type === 'typo') {
                updateTypo(scene, progress);
            } else if (scene.type === 'reels') {
                updateReels(scene, progress);
            } else if (scene.type === 'final') {
                updateFinal(scene, progress);
            }
        });
    }

    function requestUpdate() {
        if (!ticking) {
            ticking = true;
            window.requestAnimationFrame(update);
        }
    }

    document.querySelectorAll('[data-section-link]').forEach(function (link) {
        link.addEventListener('click', function (event) {
            var id = link.getAttribute('data-section-link');
            var section = document.getElementById(id);
            if (!section) {
                return;
            }
            event.preventDefault();
            window.scrollTo({ top: section.offsetTop, behavior: reducedMotion ? 'auto' : 'smooth' });
        });
    });

    document.querySelectorAll('[data-scroll-top]').forEach(function (link) {
        link.addEventListener('click', function (event) {
            if (window.location.pathname === '/') {
                event.preventDefault();
                window.scrollTo({ top: 0, behavior: reducedMotion ? 'auto' : 'smooth' });
            }
        });
    });

    window.addEventListener('scroll', requestUpdate, { passive: true });
    window.addEventListener('resize', cacheScenes);
    window.addEventListener('load', cacheScenes);
    cacheScenes();
})();
