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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppUser author;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Post() {
    }

    public Post(BoardType boardType, String title, String content, String instagramUrl, String imageUrl, AppUser author) {
        this.boardType = boardType;
        this.title = title;
        this.content = content;
        this.instagramUrl = instagramUrl;
        this.imageUrl = imageUrl;
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

    public AppUser getAuthor() {
        return author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

