package polytech.aisw.eom.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BoardType boardType;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 300)
    private String instagramUrl;

    @Column(length = 300)
    private String imageUrl;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private int likeCount;

    @Column(nullable = false)
    private int commentCount;

    @Column(length = 300)
    private String tags;

    @Column(length = 120)
    private String location;

    private LocalDate eventDate;

    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MediaType mediaType;

    @Column(length = 500)
    private String mediaUrl;

    @Column(length = 300)
    private String thumbnailUrl;

    @Column(nullable = false)
    private boolean portfolioSelected;

    @Column(nullable = false)
    private boolean portfolioPinned;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppUser author;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Post() {
    }

    public Post(BoardType boardType, String title, String content, String instagramUrl, String imageUrl, AppUser author) {
        this(
                boardType,
                title,
                content,
                instagramUrl,
                imageUrl,
                0,
                0,
                0,
                "",
                "",
                null,
                null,
                MediaType.IMAGE,
                instagramUrl,
                imageUrl,
                author
        );
    }

    public Post(
            BoardType boardType,
            String title,
            String content,
            String instagramUrl,
            String imageUrl,
            int viewCount,
            int likeCount,
            int commentCount,
            String tags,
            String location,
            LocalDate eventDate,
            LocalDate deadline,
            MediaType mediaType,
            String mediaUrl,
            String thumbnailUrl,
            AppUser author
    ) {
        this.boardType = boardType;
        this.title = title;
        this.content = content;
        this.instagramUrl = instagramUrl;
        this.imageUrl = imageUrl;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.tags = tags;
        this.location = location;
        this.eventDate = eventDate;
        this.deadline = deadline;
        this.mediaType = mediaType;
        this.mediaUrl = mediaUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.portfolioSelected = boardType == BoardType.SHOW;
        this.portfolioPinned = false;
        this.author = author;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public BoardType getBoardType() {
        return boardType;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getInstagramUrl() {
        return instagramUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getViewCount() {
        return viewCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public String getTags() {
        return tags;
    }

    public String getLocation() {
        return location;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public boolean isPortfolioSelected() {
        return portfolioSelected;
    }

    public boolean isPortfolioPinned() {
        return portfolioPinned;
    }

    public AppUser getAuthor() {
        return author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isAuthoredBy(String username) {
        return author.getUsername().equals(username);
    }

    public void setPortfolioSelected(boolean portfolioSelected) {
        this.portfolioSelected = portfolioSelected;
        if (!portfolioSelected) {
            this.portfolioPinned = false;
        }
    }

    public void setPortfolioPinned(boolean portfolioPinned) {
        if (portfolioPinned) {
            this.portfolioSelected = true;
        }
        this.portfolioPinned = portfolioPinned;
    }
}
