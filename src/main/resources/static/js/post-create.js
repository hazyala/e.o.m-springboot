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
    const fileInput = form.querySelector("[data-preview-file]");
    const mediaInput = form.querySelector("[data-preview-media]");
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
    const videoOutput = root.querySelector("[data-preview-video-output]");
    const mediaBadge = root.querySelector("[data-preview-media-badge]");
    const mediaPlay = root.querySelector(".post-preview-play");
    const linkCard = root.querySelector("[data-preview-link-card]");
    const linkThumbnail = root.querySelector("[data-preview-link-thumbnail]");
    const linkLabel = root.querySelector("[data-preview-link-label]");
    const linkTitle = root.querySelector("[data-preview-link-title]");
    const linkUrl = root.querySelector("[data-preview-link-url]");
    let filePreviewUrl = "";

    function selectedBoard() {
        return boardInputs.find((input) => input.checked)?.value || "SHOW";
    }

    function syncBoardTabs(board) {
        boardInputs.forEach((input) => {
            input.closest("label").classList.toggle("is-active", input.value === board);
        });
    }

    function setOptionalText(element, value) {
        const text = value.trim();
        element.hidden = !text;
        element.textContent = text;
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

    function selectedFile() {
        return fileInput && fileInput.files && fileInput.files.length > 0 ? fileInput.files[0] : null;
    }

    function isInstagramUrl(mediaUrl) {
        return mediaUrl.toLowerCase().includes("instagram.com/");
    }

    function normalizeInstagramUrl(mediaUrl) {
        let normalized = mediaUrl.trim();
        if (!normalized) {
            return "";
        }
        if (/^https?:\/\//i.test(normalized)) {
            return normalized;
        }
        normalized = normalized.replace(/^\/+/, "");
        if (/^(www\.)?instagram\.com\//i.test(normalized)) {
            return `https://${normalized}`;
        }
        return normalized;
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
        setOptionalText(titleOutput, titleInput.value);
        setOptionalText(contentOutput, contentInput.value);

        const currentTags = parseTags(tagsInput.value);
        tagsOutput.innerHTML = "";
        tagsOutput.hidden = currentTags.length === 0;
        currentTags.forEach((tag) => {
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

        const file = selectedFile();
        if (filePreviewUrl) {
            URL.revokeObjectURL(filePreviewUrl);
            filePreviewUrl = "";
        }
        if (file) {
            filePreviewUrl = URL.createObjectURL(file);
        }

        const thumbnailUrl = filePreviewUrl;
        const isVideoFile = file && file.type.startsWith("video/");
        mediaFrame.hidden = !thumbnailUrl;
        thumbnailOutput.hidden = !thumbnailUrl || isVideoFile;
        if (videoOutput) {
            videoOutput.hidden = !thumbnailUrl || !isVideoFile;
        }
        mediaFrame.classList.remove("has-image");
        if (thumbnailUrl) {
            if (isVideoFile && videoOutput) {
                thumbnailOutput.removeAttribute("src");
                videoOutput.src = thumbnailUrl;
                linkThumbnail.removeAttribute("src");
                linkThumbnail.hidden = true;
                linkCard.classList.add("is-text-only");
            } else {
                thumbnailOutput.src = thumbnailUrl;
                if (videoOutput) {
                    videoOutput.removeAttribute("src");
                }
                linkThumbnail.src = thumbnailUrl;
                linkThumbnail.hidden = false;
                linkCard.classList.remove("is-text-only");
            }
        } else {
            thumbnailOutput.removeAttribute("src");
            if (videoOutput) {
                videoOutput.removeAttribute("src");
            }
            linkThumbnail.removeAttribute("src");
            linkThumbnail.hidden = true;
            linkCard.classList.add("is-text-only");
        }

        const mediaUrl = normalizeInstagramUrl(mediaInput.value);
        const hasInstagram = Boolean(mediaUrl && isInstagramUrl(mediaUrl));
        mediaBadge.hidden = true;
        mediaBadge.textContent = "";
        mediaPlay.hidden = true;

        linkCard.hidden = !hasInstagram;
        if (hasInstagram) {
            linkCard.href = mediaUrl;
            linkThumbnail.removeAttribute("src");
            linkThumbnail.hidden = true;
            linkCard.classList.add("is-text-only");
            linkLabel.textContent = "Instagram Preview";
            linkTitle.textContent = titleInput.value.trim() || `${board} Instagram post`;
            linkUrl.textContent = mediaUrl;
        } else {
            linkCard.removeAttribute("href");
            linkTitle.textContent = "";
            linkUrl.textContent = "";
        }

        if (contentCount) {
            contentCount.textContent = String(contentInput.value.length);
        }
    }

    [
        titleInput,
        contentInput,
        tagsInput,
        locationInput,
        fileInput,
        mediaInput,
        eventDateInput,
        deadlineInput,
        ...boardInputs
    ].forEach((element) => {
        element.addEventListener("input", syncPreview);
        element.addEventListener("change", syncPreview);
    });

    syncPreview();
})();
