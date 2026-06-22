package polytech.aisw.eom.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.web.multipart.MultipartFile;
import polytech.aisw.eom.domain.BoardType;
import polytech.aisw.eom.domain.Post;

public class PostCreateRequest {

    @NotNull
    private BoardType boardType = BoardType.SHOW;

    @NotBlank
    @Size(max = 120)
    private String title;

    @NotBlank
    @Size(max = 5000)
    private String content;

    @Size(max = 300)
    private String tags;

    @Size(max = 120)
    private String location;

    @Size(max = 300)
    private String mediaUrl;

    @Size(max = 300)
    private String thumbnailUrl;

    private MultipartFile mediaFile;

    private LocalDate eventDate;

    private LocalDate deadline;

    private boolean adminApprovedEvent;

    public static PostCreateRequest from(Post post) {
        PostCreateRequest request = new PostCreateRequest();
        request.setBoardType(post.getBoardType());
        request.setTitle(post.getTitle());
        request.setContent(post.getContent());
        request.setTags(post.getTags());
        request.setLocation(post.getLocation());
        request.setMediaUrl(post.getMediaUrl());
        request.setThumbnailUrl(post.getThumbnailUrl());
        request.setEventDate(post.getEventDate());
        request.setDeadline(post.getDeadline());
        request.setAdminApprovedEvent(post.isAdminApprovedEvent());
        return request;
    }

    public BoardType getBoardType() {
        return boardType;
    }

    public void setBoardType(BoardType boardType) {
        this.boardType = boardType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public MultipartFile getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(MultipartFile mediaFile) {
        this.mediaFile = mediaFile;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public boolean isAdminApprovedEvent() {
        return adminApprovedEvent;
    }

    public void setAdminApprovedEvent(boolean adminApprovedEvent) {
        this.adminApprovedEvent = adminApprovedEvent;
    }
}
