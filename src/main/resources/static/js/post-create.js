(function () {
    const root = document.querySelector(".post-create-shell");
    const form = document.querySelector(".post-create-form");

    if (!root || !form) {
        return;
    }

    const boardInputs = Array.from(form.querySelectorAll("input[name='boardType']"));
    const titleInput = form.querySelector("[data-preview-title]");
    const contentInput = form.querySelector("[data-preview-content]");
    const tagsInput = form.querySelector("[data-preview-tags]");
    const locationInput = form.querySelector("[data-preview-location]");
    const mediaInput = form.querySelector("[data-preview-media]");
    const thumbnailInput = form.querySelector("[data-preview-thumbnail]");
    const eventDateInput = form.querySelector("[data-preview-event-date]");
    const deadlineInput = form.querySelector("[data-preview-deadline]");
    const dateFields = form.querySelector("[data-date-fields]");
    const officialCheck = form.querySelector("[data-official-check]");
    const contentCount = form.querySelector("[data-content-count]");

    const boardOutput = root.querySelector("[data-preview-board]");
    const titleOutput = root.querySelector("[data-preview-title-output]");
    const contentOutput = root.querySelector("[data-preview-content-output]");
    const tagsOutput = root.querySelector("[data-preview-tags-output]");
    const locationOutput = root.querySelector("[data-preview-location-output]");
    const eventOutput = root.querySelector("[data-preview-event-output]");
    const deadlineOutput = root.querySelector("[data-preview-deadline-output]");
    const mediaFrame = root.querySelector("[data-preview-media-frame]");
    const thumbnailOutput = root.querySelector("[data-preview-thumbnail-output]");
    const mediaLink = root.querySelector("[data-preview-media-link]");

    const fallback = {
        title: "서울 스트릿잼 후기",
        content: "지난 주말 다녀온 서울 스트릿잼. 정말 에너지 넘치는 하루였습니다.",
        tags: ["스트릿잼", "배틀", "서울"]
    };

    function selectedBoard() {
        return boardInputs.find((input) => input.checked)?.value || "SHOW";
    }

    function syncBoardTabs(board) {
        boardInputs.forEach((input) => {
            input.closest("label").classList.toggle("is-active", input.value === board);
        });
    }

    function setTextOrFallback(element, value, fallbackText) {
        element.textContent = value.trim() || fallbackText;
    }

    function formatDate(value, prefix) {
        if (!value) {
            return "";
        }
        const parts = value.split("-");
        if (parts.length !== 3) {
            return `${prefix} ${value}`;
        }
        return `${prefix} ${parts[1]}.${parts[2]}`;
    }

    function parseTags(value) {
        return value
            .split(/[#,]/)
            .map((tag) => tag.trim())
            .filter(Boolean)
            .slice(0, 6);
    }

    function syncOptionalFields(board) {
        const needsDate = board !== "SHOW";
        if (dateFields) {
            dateFields.hidden = !needsDate;
        }
        if (officialCheck) {
            officialCheck.closest("label").hidden = board !== "HYPE";
            if (board !== "HYPE") {
                officialCheck.checked = false;
            }
        }
    }

    function syncPreview() {
        const board = selectedBoard();
        syncBoardTabs(board);
        syncOptionalFields(board);

        boardOutput.textContent = board;
        boardOutput.className = `post-board-pill type-${board.toLowerCase()}`;
        setTextOrFallback(titleOutput, titleInput.value, fallback.title);
        setTextOrFallback(contentOutput, contentInput.value, fallback.content);

        const currentTags = parseTags(tagsInput.value);
        tagsOutput.innerHTML = "";
        (currentTags.length ? currentTags : fallback.tags).forEach((tag) => {
            const tagElement = document.createElement("span");
            tagElement.textContent = `# ${tag}`;
            tagsOutput.appendChild(tagElement);
        });

        const location = locationInput.value.trim();
        locationOutput.hidden = !location;
        locationOutput.textContent = location;

        const eventDate = formatDate(eventDateInput.value, "EVENT");
        eventOutput.hidden = !eventDate;
        eventOutput.textContent = eventDate;

        const deadline = formatDate(deadlineInput.value, "DUE");
        deadlineOutput.hidden = !deadline;
        deadlineOutput.textContent = deadline;

        const thumbnailUrl = thumbnailInput.value.trim();
        thumbnailOutput.hidden = !thumbnailUrl;
        mediaFrame.classList.toggle("has-image", Boolean(thumbnailUrl));
        if (thumbnailUrl) {
            thumbnailOutput.src = thumbnailUrl;
        } else {
            thumbnailOutput.removeAttribute("src");
        }

        const mediaUrl = mediaInput.value.trim();
        mediaLink.hidden = !mediaUrl;
        mediaLink.href = mediaUrl || "#";

        if (contentCount) {
            contentCount.textContent = String(contentInput.value.length);
        }
    }

    [
        titleInput,
        contentInput,
        tagsInput,
        locationInput,
        mediaInput,
        thumbnailInput,
        eventDateInput,
        deadlineInput,
        ...boardInputs
    ].forEach((element) => {
        element.addEventListener("input", syncPreview);
        element.addEventListener("change", syncPreview);
    });

    syncPreview();
})();
