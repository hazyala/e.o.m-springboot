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
    var displayNameInput = document.querySelector('input[name="displayName"]');
    var passwordConfirmInput = document.querySelector('input[name="passwordConfirm"]');

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
        form.action = isSignup ? form.dataset.signupAction : form.dataset.loginAction;
        if (displayNameInput) {
            displayNameInput.required = isSignup;
        }
        if (passwordConfirmInput) {
            passwordConfirmInput.required = isSignup;
        }
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
        setMode(window.location.search.indexOf('signup') !== -1);
        form.addEventListener('submit', function (event) {
            if (page.classList.contains('is-signup')) {
                var password = document.querySelector('[data-password-input]');
                if (password && passwordConfirmInput && password.value !== passwordConfirmInput.value) {
                    event.preventDefault();
                    window.alert('비밀번호 확인이 일치하지 않습니다.');
                }
            }
        });
    }
})();
