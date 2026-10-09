(() => {
    const toast = document.querySelector('[data-site-toast]');
    const commentList = document.querySelector('[data-comment-list]');
    const commentEmpty = document.querySelector('[data-comment-empty]');
    const commentError = document.querySelector('[data-comment-error]');
    const confirmDialog = document.querySelector('[data-post-confirm]');
    const pending = new Set();
    let toastTimer;

    document.querySelectorAll('form[data-confirm-message]').forEach((form) => {
        form.dataset.confirmHandled = 'true';
    });

    function confirmAction(message) {
        if (!confirmDialog || typeof confirmDialog.showModal !== 'function') {
            return Promise.resolve(window.confirm(message));
        }
        confirmDialog.querySelector('[data-post-confirm-message]').textContent = message;
        confirmDialog.returnValue = 'cancel';
        confirmDialog.showModal();
        return new Promise((resolve) => {
            confirmDialog.addEventListener('close', () => {
                resolve(confirmDialog.returnValue === 'confirm');
            }, { once: true });
        });
    }

    function notify(message, isError = false) {
        if (!toast) return;
        window.clearTimeout(toastTimer);
        toast.textContent = message;
        toast.classList.toggle('is-error', isError);
        toast.setAttribute('role', isError ? 'alert' : 'status');
        toast.hidden = false;
        toastTimer = window.setTimeout(() => { toast.hidden = true; }, 4000);
    }

    window.showPostToast = notify;
    if (toast && !toast.hidden) {
        toastTimer = window.setTimeout(() => { toast.hidden = true; }, 4000);
    }

    function showCommentError(message) {
        if (!commentError) return;
        commentError.textContent = message;
        commentError.hidden = !message;
    }

    async function send(form) {
        let response;
        try {
            response = await fetch(form.action, {
                method: 'POST',
                body: new FormData(form),
                credentials: 'same-origin',
                headers: { 'Accept': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }
            });
        } catch (error) {
            throw new Error('연결이 불안정합니다. 잠시 후 다시 시도해 주세요.');
        }
        if (response.redirected || !response.headers.get('content-type')?.includes('application/json')) {
            throw new Error('로그인 상태를 확인하고 다시 시도해 주세요.');
        }
        const data = await response.json();
        if (!response.ok) throw new Error(data.message || '처리 중 문제가 발생했습니다.');
        return data;
    }

    function updateToggle(type, active, count) {
        document.querySelectorAll(`[data-post-interaction="${type}"] button`).forEach((button) => {
            button.classList.toggle('is-active', active);
            button.setAttribute('aria-pressed', String(active));
            button.setAttribute('aria-label', type === 'like'
                ? (active ? '좋아요 취소' : '좋아요')
                : (active ? '저장 취소' : '게시글 저장'));
            const icon = button.querySelector('b');
            if (icon) icon.textContent = type === 'like' ? (active ? '♥' : '♡') : (active ? '▣' : '▢');
            if (type === 'save') {
                const label = button.querySelector('strong');
                if (label) label.textContent = active ? '저장됨' : '저장';
            }
            if (type === 'save' && !icon) button.textContent = active ? '▣' : '▢';
        });
        if (type === 'like') {
            document.querySelectorAll('[data-like-count]').forEach((element) => {
                element.textContent = String(count);
            });
        }
    }

    function updateCommentCount(count) {
        document.querySelectorAll('[data-comment-count]').forEach((element) => {
            element.textContent = String(count);
        });
        if (commentEmpty) commentEmpty.hidden = count > 0;
    }

    function appendComment(data, sourceForm) {
        if (!commentList) return;
        const article = document.createElement('article');
        article.dataset.commentId = String(data.id);

        const avatar = document.createElement('a');
        avatar.className = 'post-author-avatar-link';
        avatar.href = `/dancers/${data.authorId}`;
        avatar.setAttribute('aria-label', '댓글 작성자 프로필 보기');
        if (data.authorImage) {
            const image = document.createElement('img');
            image.src = data.authorImage;
            image.alt = data.authorName;
            avatar.append(image);
        } else {
            const placeholder = document.createElement('span');
            placeholder.setAttribute('aria-hidden', 'true');
            avatar.append(placeholder);
        }

        const body = document.createElement('div');
        const heading = document.createElement('header');
        const author = document.createElement('a');
        author.href = avatar.href;
        author.textContent = data.authorName;
        const time = document.createElement('time');
        time.textContent = data.createdAt;
        heading.append(author, time);
        const content = document.createElement('p');
        content.textContent = data.content;
        body.append(heading, content);

        const deleteForm = document.createElement('form');
        deleteForm.action = `${sourceForm.action}/${data.id}/delete`;
        deleteForm.method = 'post';
        deleteForm.dataset.postInteraction = 'delete-comment';
        deleteForm.dataset.confirmMessage = '댓글을 삭제할까요?';
        const token = sourceForm.querySelector('input[name="_csrf"]');
        if (token) deleteForm.append(token.cloneNode(true));
        const deleteButton = document.createElement('button');
        deleteButton.type = 'submit';
        deleteButton.textContent = '삭제';
        deleteForm.append(deleteButton);

        article.append(avatar, body, deleteForm);
        commentList.append(article);
    }

    document.addEventListener('submit', async (event) => {
        if (event.defaultPrevented) return;
        const form = event.target.closest('form[data-post-interaction], form[data-confirm-message]');
        if (!form) return;

        const type = form.dataset.postInteraction;
        if (form.dataset.confirmMessage) {
            event.preventDefault();
            if (!await confirmAction(form.dataset.confirmMessage)) return;
            if (!type) {
                HTMLFormElement.prototype.submit.call(form);
                return;
            }
        }
        if (type === 'comment') {
            const input = form.querySelector('input[name="content"]');
            if (!input) return;
            input.value = input.value.trim();
            if (!input.value) {
                event.preventDefault();
                showCommentError('댓글 내용을 입력해주세요.');
                input.focus();
                return;
            }
            showCommentError('');
        }

        event.preventDefault();
        const pendingKey = type === 'delete-comment' ? form.action : type;
        if (pending.has(pendingKey)) return;
        const button = form.querySelector('button[type="submit"]');
        if (button?.disabled) return;
        pending.add(pendingKey);
        if (button) button.disabled = true;
        form.classList.add('is-pending');
        try {
            const data = await send(form);
            if (type === 'like' || type === 'save') updateToggle(type, data.active, data.count);
            if (type === 'comment') {
                appendComment(data, form);
                form.reset();
                updateCommentCount(data.count);
            }
            if (type === 'delete-comment') {
                form.closest('[data-comment-id]')?.remove();
                updateCommentCount(data.count);
            }
            if (type === 'report') {
                const reportButton = form.querySelector('button');
                if (reportButton) {
                    reportButton.textContent = '신고 접수됨';
                    reportButton.disabled = true;
                }
                const menu = form.closest('[data-more-actions-menu]');
                const toggle = document.querySelector('[data-more-actions-toggle]');
                if (menu) menu.hidden = true;
                if (toggle) toggle.setAttribute('aria-expanded', 'false');
            }
            notify(data.message);
        } catch (error) {
            if (type === 'comment') showCommentError(error.message);
            else notify(error.message, true);
        } finally {
            pending.delete(pendingKey);
            form.classList.remove('is-pending');
            if (button && type !== 'report') button.disabled = false;
        }
    });
})();
