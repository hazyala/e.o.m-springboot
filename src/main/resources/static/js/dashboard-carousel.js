(() => {
    const hero = document.querySelector('[data-dashboard-pick]');
    if (!hero) return;

    const slides = Array.from(hero.querySelectorAll('[data-dashboard-slide]'));
    if (slides.length < 2) return;

    const current = hero.querySelector('[data-dashboard-current]');
    const motionPreference = window.matchMedia('(prefers-reduced-motion: reduce)');
    let activeIndex = 0;
    let timer;

    slides.forEach((slide, index) => {
        slide.inert = index !== activeIndex;
    });

    function show(index) {
        slides[activeIndex].classList.remove('is-active');
        slides[activeIndex].setAttribute('aria-hidden', 'true');
        slides[activeIndex].inert = true;

        activeIndex = (index + slides.length) % slides.length;
        slides[activeIndex].classList.add('is-active');
        slides[activeIndex].setAttribute('aria-hidden', 'false');
        slides[activeIndex].inert = false;
        current.textContent = String(activeIndex + 1).padStart(2, '0');
    }

    function stop() {
        window.clearInterval(timer);
        timer = undefined;
    }

    function start() {
        stop();
        if (!motionPreference.matches && !hero.contains(document.activeElement)) {
            timer = window.setInterval(() => show(activeIndex + 1), 5000);
        }
    }

    hero.querySelector('[data-dashboard-prev]').addEventListener('click', () => {
        show(activeIndex - 1);
        start();
    });
    hero.querySelector('[data-dashboard-next]').addEventListener('click', () => {
        show(activeIndex + 1);
        start();
    });

    hero.addEventListener('focusin', stop);
    hero.addEventListener('focusout', () => window.setTimeout(start, 0));
    motionPreference.addEventListener('change', start);
    start();
})();
