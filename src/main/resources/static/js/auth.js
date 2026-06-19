(function () {
    var page = document.querySelector('[data-auth-page]');
    var title = document.querySelector('[data-auth-title]');
    var subtitle = document.querySelector('[data-auth-subtitle]');
    var toggle = document.querySelector('[data-auth-toggle]');
    var toggleCopy = document.querySelector('[data-auth-toggle-copy]');
    var submitButton = document.querySelector('[data-submit-button]');
    var form = document.querySelector('[data-login-form]');
    var titleLink = document.querySelector('[data-login-title]');
    var passwordInputs = document.querySelectorAll('[data-password-input], [data-password-confirm]');

    var fullTitle = 'Echo of Movement';
    var index = 0;

    function typeTitle() {
        if (!titleLink) {
            return;
        }
        if (index <= fullTitle.length) {
            titleLink.textContent = fullTitle.slice(0, index);
            index += 1;
            window.setTimeout(typeTitle, 100);
        }
    }

    typeTitle();

    function setMode(isSignup) {
        page.classList.toggle('is-signup', isSignup);
        title.textContent = isSignup ? 'CREATE ACCOUNT' : 'LOGIN';
        subtitle.textContent = isSignup ? '새 계정을 만드세요' : '계정에 로그인하세요';
        submitButton.textContent = isSignup ? '회원가입' : '로그인';
        toggleCopy.textContent = isSignup ? '이미 계정이 있으신가요?' : '계정이 없으신가요?';
        toggle.textContent = isSignup ? '로그인' : '회원가입';
    }

    if (toggle && page) {
        toggle.addEventListener('click', function () {
            setMode(!page.classList.contains('is-signup'));
        });
    }

    document.querySelectorAll('[data-password-toggle]').forEach(function (button) {
        button.addEventListener('click', function () {
            passwordInputs.forEach(function (input) {
                input.type = input.type === 'password' ? 'text' : 'password';
            });
        });
    });

    if (form) {
        form.addEventListener('submit', function (event) {
            if (page.classList.contains('is-signup')) {
                event.preventDefault();
                window.alert('회원가입 기능은 다음 단계에서 연결됩니다. 지금은 데모 계정으로 로그인해주세요.');
            }
        });
    }
})();

