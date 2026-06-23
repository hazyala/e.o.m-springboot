(function () {
    var doc = document.documentElement;
    var header = document.querySelector('[data-marketing-header]');
    var reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    var ticking = false;
    var scrollStatusTimer = 0;
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
                heroCube: element.querySelector('[data-hero-cube]'),
                cube: element.querySelector('[data-cube]'),
                cards: Array.prototype.slice.call(element.querySelectorAll('.board-motion-card')),
                showTitle: element.querySelector('[data-show-title]'),
                showMessage: element.querySelector('.show-message'),
                showRail: element.querySelector('[data-show-rail]'),
                showCards: Array.prototype.slice.call(element.querySelectorAll('.show-reel-card')),
                rail: element.querySelector('[data-reel-rail]')
            };
        });
        update();
    }

    function restartHeroTyping() {
        document.querySelectorAll('.hero-type-line span:not(.hero-type-gap)').forEach(function (letter) {
            var order = Number(letter.style.getPropertyValue('--n') || 0);
            var delay = 0.28 + order * 0.085;
            var rollDelay = 2.75 + order * 0.045;
            letter.style.animation = 'none';
            letter.style.opacity = '0';
            letter.offsetHeight;
            letter.style.animation = 'hero-type-in 0.24s steps(1, end) ' + delay + 's both, hero-letter-roll 3.6s ease-in-out ' + rollDelay + 's infinite';
            window.setTimeout(function () {
                letter.style.opacity = '1';
            }, (delay + 0.28) * 1000);
        });
    }

    function updateHeader() {
        if (!header) {
            return;
        }
        header.classList.toggle('is-solid', window.scrollY > window.innerHeight * 0.16);
    }

    function updateHero(scene, progress) {
        var element = scene.element;
        var rect = element.getBoundingClientRect();
        var viewportHeight = window.innerHeight || 1;
        var scrollable = Math.max(1, rect.height - viewportHeight);
        progress = clamp(-rect.top / scrollable, 0, 1);
        var navRise = clamp(progress / 0.26, 0, 1);
        var heroLift = clamp((progress - 0.16) / 0.34, 0, 1);
        var titleSettle = clamp((progress - 0.38) / 0.22, 0, 1);
        var cubeEnter = clamp((progress - 0.18) / 0.5, 0, 1);
        var finalSpin = clamp((progress - 0.68) / 0.32, 0, 1);

        doc.style.setProperty('--hero-nav-x', 'calc(' + lerp(-35.6, 0, navRise) + 'vw - ' + lerp(10, 0, navRise) + 'px)');
        doc.style.setProperty('--hero-nav-offset', 'calc(' + lerp(68, 0, navRise) + 'vh + ' + lerp(20, 0, navRise) + 'px)');
        doc.style.setProperty('--hero-nav-font-size', lerp(28, 14, navRise) + 'px');

        element.style.setProperty('--hero-ribbon-y', lerp(0, -viewportHeight * 0.92, heroLift) + 'px');
        element.style.setProperty('--hero-ribbon-opacity', String(1 - clamp((heroLift - 0.12) / 0.64, 0, 1)));
        element.style.setProperty('--line-dont-opacity', String(1 - titleSettle));
        element.style.setProperty('--line-move-opacity', String(1 - titleSettle));
        element.style.setProperty('--line-echo-opacity', '1');
        element.style.setProperty('--echo-top', lerp(52, 13.2, titleSettle) + 'vh');
        element.style.setProperty('--echo-left', lerp(10, 4.2, titleSettle) + 'vw');
        element.style.setProperty('--echo-size', lerp(8, 6.4, titleSettle) + 'vw');
        var echoIsAqua = cubeEnter > 0.58;
        element.style.setProperty('--echo-color', echoIsAqua ? 'var(--brand-aqua)' : '#ffffff');
        element.style.setProperty('--echo-glow-strong', echoIsAqua ? 'rgba(0, 180, 171, 0.72)' : 'rgba(255, 255, 255, 0.68)');
        element.style.setProperty('--echo-glow-mid', echoIsAqua ? 'rgba(0, 180, 171, 0.44)' : 'rgba(255, 255, 255, 0.38)');
        element.style.setProperty('--echo-glow-soft', echoIsAqua ? 'rgba(0, 180, 171, 0.26)' : 'rgba(255, 255, 255, 0.22)');

        scene.lines.forEach(function (line) {
            line.style.setProperty('--line-x', '0px');
            line.style.setProperty('--line-y', lerp(0, -viewportHeight * 0.46, heroLift) + 'px');
        });

        var echoLine = element.querySelector('.hero-type-line-echo');
        if (echoLine) {
            echoLine.style.setProperty('--line-y', lerp(0, 0, titleSettle) + 'px');
        }

        if (scene.heroCube) {
            scene.heroCube.style.setProperty('--cube-x', lerp(31, 0, cubeEnter) + 'vw');
            scene.heroCube.style.setProperty('--cube-y', lerp(25, 6, cubeEnter) + 'vh');
            scene.heroCube.style.setProperty('--cube-rx', (lerp(-18, 12, cubeEnter) + finalSpin * 96) + 'deg');
            scene.heroCube.style.setProperty('--cube-ry', (lerp(-38, 28, cubeEnter) + finalSpin * 300) + 'deg');
            scene.heroCube.style.setProperty('--cube-rz', (lerp(2, -4, cubeEnter) + finalSpin * 36) + 'deg');
            scene.heroCube.style.setProperty('--cube-scale', String(lerp(0.34, 0.88, cubeEnter)));
        }
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

    function updateShow(scene, progress) {
        var rect = scene.element.getBoundingClientRect();
        var viewportHeight = window.innerHeight || 1;
        var scrollable = Math.max(1, rect.height - viewportHeight);
        progress = clamp(-rect.top / scrollable, 0, 1);
        var titleHoldEnd = 0.32;
        var returnProgress = clamp((progress - 0.84) / 0.1, 0, 1);
        var titleLeave = lerp(clamp((progress - titleHoldEnd) / 0.2, 0, 1), 0, returnProgress);
        var cardsEnter = lerp(clamp((progress - titleHoldEnd) / 0.21, 0, 1), 0, returnProgress);
        var horizontal = clamp((progress - 0.56) / 0.22, 0, 1);
        var settle = lerp(clamp((progress - 0.7) / 0.1, 0, 1), 0, returnProgress);
        var isActiveLight = progress > 0.04 && progress < 1;

        doc.style.setProperty('--scroll-status-color', isActiveLight ? '#ffffff' : '#ffffff');
        doc.style.setProperty('--scroll-status-border', isActiveLight ? 'rgba(211, 15, 66, 0.94)' : 'rgba(255, 255, 255, 0.42)');
        doc.style.setProperty('--scroll-status-bg', isActiveLight ? 'rgba(211, 15, 66, 0.9)' : 'transparent');

        if (header) {
            header.classList.toggle('is-light-section', isActiveLight);
        }

        if (scene.showTitle) {
            scene.showTitle.style.setProperty('--show-title-y', 'calc(' + lerp(0, 46, titleLeave) + 'vh - ' + lerp(0, 30, titleLeave) + 'px)');
            scene.showTitle.style.setProperty('--show-title-scale', String(lerp(1, 0.84, titleLeave)));
            scene.showTitle.style.setProperty('--show-title-opacity', String(lerp(1, 0.18, cardsEnter)));
            scene.showTitle.style.setProperty('--show-title-detail-opacity', String(1 - clamp((titleLeave - 0.52) / 0.36, 0, 1)));
        }

        if (scene.showMessage) {
            scene.showMessage.style.setProperty('--show-message-opacity', String(lerp(1, 0, cardsEnter)));
            scene.showMessage.style.setProperty('--show-message-y', lerp(0, -28, cardsEnter) + 'px');
        }

        if (scene.showRail) {
            var overflow = Math.max(0, scene.showRail.scrollWidth - window.innerWidth + Math.min(window.innerWidth * 0.34, 420));
            var railEndInset = Math.min(window.innerWidth * 0.3, 420);
            var railDistance = Math.max(0, overflow - railEndInset);
            scene.showRail.style.setProperty('--show-rail-x', (-railDistance * horizontal) + 'px');
            scene.showRail.style.setProperty('--show-rail-y', lerp(96, -27, cardsEnter) + 'px');
        }

        scene.showCards.forEach(function (card, index) {
            var delay = index * 0.018;
            var cardEnter = clamp((cardsEnter - delay) / 0.42, 0, 1);
            var lane = index - (scene.showCards.length - 1) / 2;
            var cross = index % 2 === 0 ? 1 : -1;
            var lift = lerp(250 + (index % 3) * 58, (index % 2 === 0 ? 0 : 34), cardEnter);
            var drift = lerp(cross * (80 + index * 6), 0, cardEnter);
            var rotate = lerp(cross * (5 + index * 0.55), 0, cardEnter);

            if (settle > 0) {
                lift = lerp(lift, (index % 2 === 0 ? -8 : 26), settle);
                rotate = lerp(rotate, lane * -0.35, settle);
            }

            card.style.setProperty('--show-card-y', lift + 'px');
            card.style.setProperty('--show-card-x', drift + 'px');
            card.style.setProperty('--show-card-r', rotate + 'deg');
            card.style.setProperty('--show-card-opacity', String(cardEnter));
        });
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
        var viewportHeight = window.innerHeight || 1;

        if (reducedMotion) {
            return;
        }

        doc.style.setProperty('--scroll-status-color', '#ffffff');
        doc.style.setProperty('--scroll-status-border', 'rgba(255, 255, 255, 0.42)');
        doc.style.setProperty('--scroll-status-bg', 'transparent');
        if (header) {
            header.classList.remove('is-light-section');
        }

        sceneCache.forEach(function (scene) {
            var rect = scene.element.getBoundingClientRect();
            var progress = progressFor(rect, viewportHeight);
            if (progress <= 0 || progress >= 1 && rect.bottom < 0) {
                return;
            }

            if (scene.type === 'hero') {
                updateHero(scene, progress);
            } else if (scene.type === 'show') {
                updateShow(scene, progress);
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
        document.querySelectorAll('[data-scroll-status]').forEach(function (status) {
            status.textContent = 'Keep Scroll';
        });
        window.clearTimeout(scrollStatusTimer);
        scrollStatusTimer = window.setTimeout(function () {
            document.querySelectorAll('[data-scroll-status]').forEach(function (status) {
                status.textContent = 'Start Scroll';
            });
        }, 180);

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
    window.addEventListener('load', function () {
        cacheScenes();
        restartHeroTyping();
    });
    cacheScenes();
    restartHeroTyping();
})();
