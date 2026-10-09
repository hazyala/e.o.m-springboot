(function () {
    'use strict';
    window.scrollTo({ top: 0, behavior: 'instant' });
    var root = document.documentElement;
    var header = document.querySelector('[data-marketing-header]');
    var preference = matchMedia('(prefers-reduced-motion: reduce)');
    var paused = preference.matches;
    var opening = document.querySelector('[data-opening]');
    var lines = Array.from(document.querySelectorAll('[data-intro-line]'));
    var orbit = document.querySelector('[data-orbit]');
    var cards = Array.from(document.querySelectorAll('[data-orbit-card]'));
    var steps = Array.from(document.querySelectorAll('[data-orbit-step]'));
    var count = document.querySelector('[data-orbit-count]');
    var button = document.querySelector('[data-motion-toggle]');
    var videos = Array.from(document.querySelectorAll('video'));
    var visibleVideos = new WeakMap();
    var pending = false;
    var introReady = paused;
    var clamp = function (n, a, b) { return Math.max(a, Math.min(b, n)); };
    function progress(section) {
        var rect = section.getBoundingClientRect();
        return clamp(-rect.top / Math.max(1, rect.height - innerHeight), 0, 1);
    }
    function update() {
        pending = false;
        var intro = introReady ? progress(opening) : 0;
        var rise = paused ? 1 : clamp(intro / .38, 0, 1);
        // Preserve the original lower-left navigation-to-header choreography.
        root.style.setProperty('--hero-nav-x', (-(.356 * innerWidth + 10) * (1 - rise)) + 'px');
        root.style.setProperty('--hero-nav-offset', ((.68 * innerHeight + 20) * (1 - rise)) + 'px');
        root.style.setProperty('--hero-nav-font-size', (28 - 14 * rise) + 'px');
        root.style.setProperty('--mobile-nav-y', (55 * (1 - rise)) + 'vh');
        var film = document.querySelector('.hero-cinema').getBoundingClientRect();
        var ending = document.querySelector('.show-cinema').getBoundingClientRect();
        var overFilm = film.top < 90 && film.bottom > 90;
        var overEnding = ending.top < 90 && ending.bottom > 90;
        header.classList.toggle('is-over-film', overFilm);
        header.classList.toggle('is-over-feature', overEnding);
        root.style.setProperty('--control-color', overEnding ? '#666' : overFilm ? '#fff' : 'var(--scene-muted)');
        if (paused) { orbit.classList.remove('is-card-crossing'); return; }
        opening.style.setProperty('--intro-progress', intro);
        opening.style.setProperty('--ribbon-x', (-intro * 40) + 'vw');
        opening.style.setProperty('--intro-note', 1 - clamp((intro - .65) / .2, 0, 1));
        lines.forEach(function (line, index) {
            var local = clamp((intro - index * .07) / .8, 0, 1);
            line.style.setProperty('--intro-x', ((index % 2 ? -1 : 1) * local * (innerWidth < 700 ? 14 : 22)) + 'vw');
            line.style.setProperty('--intro-y', (-local * innerHeight * .28) + 'px');
            line.style.setProperty('--intro-r', ((index - 1) * local * 3) + 'deg');
            line.style.setProperty('--intro-opacity', 1 - clamp((local - .66) / .34, 0, 1));
        });
        var position = progress(orbit) * 3.6 - .3;
        var active = clamp(Math.round(position), 0, cards.length - 1);
        var mobile = innerWidth < 700;
        cards.forEach(function (card, index) {
            var q = index - position;
            // A parabola with a diagonal tangent: bottom-right entry, rising left exit.
            var x = (mobile ? 0 : innerWidth * .15) + q * innerWidth * (mobile ? .94 : .54);
            var y = (mobile ? innerHeight * .02 : innerHeight * .08) + innerHeight * (.18 * q + .11 * q * q);
            var rotation = q * (mobile ? 15 : 27);
            var fit = Math.min(1, Math.max(360, innerHeight - 250) / card.offsetHeight);
            var scale = fit * (1 - Math.min(Math.abs(q) * .045, .12));
            card.style.transform = 'translate(-50%,-50%) translate3d(' + x + 'px,' + y + 'px,0) rotate(' + rotation + 'deg) scale(' + scale + ')';
            card.style.zIndex = String(10 - Math.round(Math.abs(q) * 2));
            card.style.visibility = Math.abs(q) > 2 ? 'hidden' : 'visible';
            card.inert = Math.abs(q) > (mobile ? .55 : 1.15);
        });
        var orbitRect = orbit.getBoundingClientRect();
        var headerBottom = header.getBoundingClientRect().bottom + (mobile ? 48 : 10);
        orbit.classList.toggle('is-card-crossing', orbitRect.top < innerHeight && orbitRect.bottom > 0 && cards.some(function (card) {
            var rect = card.getBoundingClientRect();
            return card.style.visibility !== 'hidden' && rect.top < headerBottom && rect.bottom > 0 && rect.right > 0 && rect.left < innerWidth;
        }));
        steps.forEach(function (step, index) { step.setAttribute('aria-current', String(index === active)); });
        count.textContent = String(active + 1).padStart(2, '0');
        orbit.style.setProperty('--orbit-title-x', (-progress(orbit) * 8) + 'vw');
    }
    function requestUpdate() {
        if (!pending) { pending = true; requestAnimationFrame(update); }
    }
    function goToCard(index) {
        if (paused) { cards[index].scrollIntoView({ behavior: 'auto', block: 'center' }); return; }
        var top = orbit.getBoundingClientRect().top + scrollY;
        var fraction = (index + .3) / 3.6;
        scrollTo({ top: top + fraction * (orbit.offsetHeight - innerHeight), behavior: 'smooth' });
    }
    steps.forEach(function (step, index) { step.addEventListener('click', function () { goToCard(index); }); });
    window.addEventListener('eom:orbit-navigate', function (event) {
        var index = cards.indexOf(event.detail.target);
        if (index >= 0) goToCard(index);
    });
    var monitorVideo = document.querySelector('[data-monitor] video');
    var monitorPlay = document.querySelector('[data-monitor-play]');
    if (monitorVideo && monitorPlay) {
        var monitorLastTime = -1;
        var monitorLastAdvance = performance.now();
        function showMonitorPlay() { monitorPlay.hidden = false; }
        monitorVideo.addEventListener('timeupdate', function () {
            if (monitorVideo.currentTime !== monitorLastTime) {
                monitorLastTime = monitorVideo.currentTime;
                monitorLastAdvance = performance.now();
                monitorPlay.hidden = !monitorVideo.paused;
            }
        });
        ['pause', 'waiting', 'stalled', 'error', 'ended'].forEach(function (event) {
            monitorVideo.addEventListener(event, showMonitorPlay);
        });
        // Some browsers leave play() pending without rejecting it or advancing frames.
        window.setInterval(function () {
            if (document.hidden || !visibleVideos.get(monitorVideo)) return;
            if (monitorVideo.paused || performance.now() - monitorLastAdvance > 1800) showMonitorPlay();
        }, 600);
        monitorPlay.addEventListener('click', function () {
            monitorVideo.muted = true;
            if (monitorVideo.error) monitorVideo.load();
            monitorVideo.play().catch(showMonitorPlay);
        });
    }
    function syncVideo(video) {
        if (paused || document.hidden || !visibleVideos.get(video)) { video.pause(); return; }
        video.muted = true;
        video.defaultMuted = true;
        var attempt = video.play();
        if (attempt) attempt.catch(function () {
            if (video === monitorVideo && video.paused && visibleVideos.get(video)) monitorPlay.hidden = false;
        });
    }
    document.addEventListener('pointerdown', function () { videos.forEach(syncVideo); }, { passive: true });
    document.addEventListener('keydown', function () { videos.forEach(syncVideo); });
    function setPaused(value) {
        var anchor = [opening, document.querySelector('.hero-cinema'), orbit, document.querySelector('.show-cinema')].find(function (el) {
            var rect = el.getBoundingClientRect(); return rect.top <= 110 && rect.bottom > 110;
        });
        paused = value;
        root.classList.toggle('motion-paused', value);
        if (button) {
            button.setAttribute('aria-pressed', String(value));
            button.textContent = value ? '모션 재생' : '모션 정지';
        }
        cards.forEach(function (card) { card.inert = false; });
        window.dispatchEvent(new CustomEvent('eom:motion', { detail: { paused: value } }));
        if (anchor && scrollY > 10) scrollTo({ top: anchor.getBoundingClientRect().top + scrollY, behavior: 'instant' });
        videos.forEach(syncVideo);
        update();
    }
    if ('IntersectionObserver' in window) {
        // Observe the shared stage: rotated 3D faces can have empty intersection boxes.
        var mediaGroups = new Map();
        videos.forEach(function (video) {
            var target = video.closest('.hero-sticky-stage, .opening-stage') || video;
            if (!mediaGroups.has(target)) mediaGroups.set(target, []);
            mediaGroups.get(target).push(video);
        });
        var media = new IntersectionObserver(function (entries) {
            entries.forEach(function (entry) {
                mediaGroups.get(entry.target).forEach(function (video) {
                    visibleVideos.set(video, entry.isIntersecting);
                    syncVideo(video);
                });
            });
        }, { threshold: .01 });
        mediaGroups.forEach(function (_, target) { media.observe(target); });
    }
    if (button) button.addEventListener('click', function () { setPaused(!paused); });
    preference.addEventListener('change', function (event) { setPaused(event.matches); });
    window.addEventListener('scroll', requestUpdate, { passive: true });
    window.addEventListener('resize', requestUpdate);
    window.addEventListener('load', requestUpdate);
    document.addEventListener('visibilitychange', function () { videos.forEach(syncVideo); });
    // The shared theme script owns persistence; keep the landing control's label in sync.
    function syncThemeLabel() {
        document.querySelector('[data-theme-toggle]').setAttribute('aria-label', root.dataset.theme === 'dark' ? '화이트 테마로 변경' : '다크 테마로 변경');
    }
    new MutationObserver(syncThemeLabel).observe(root, { attributes: true, attributeFilter: ['data-theme'] });
    syncThemeLabel();
    setPaused(paused);
    if (!introReady) {
        opening.classList.add('intro-entering');
        var bouncing = false;
        var finishIntro = function () {
            introReady = true;
            opening.classList.remove('intro-entering', 'intro-bouncing');
            requestUpdate();
        };
        var bounceIntro = function () {
            if (bouncing || introReady) return;
            bouncing = true;
            opening.classList.remove('intro-entering');
            opening.classList.add('intro-bouncing');
            lines[2].querySelector('.intro-word').addEventListener('animationend', finishIntro, { once: true });
            window.setTimeout(finishIntro, 2700);
        };
        lines[2].firstElementChild.addEventListener('animationend', bounceIntro, { once: true });
        window.setTimeout(bounceIntro, 1550);
    }
    var monitor = document.querySelector('[data-monitor]');
    if (monitor && 'IntersectionObserver' in window) {
        new IntersectionObserver(function (entries) {
            monitor.classList.toggle('is-offscreen', !entries[0].isIntersecting);
        }).observe(monitor);
    }
})();
