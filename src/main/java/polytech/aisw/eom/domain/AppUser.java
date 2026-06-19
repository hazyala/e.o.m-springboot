package polytech.aisw.eom.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 80)
    private String displayName;

    @Column(length = 300)
    private String instagramUrl;

    @Column(length = 300)
    private String profileImageUrl;

    @Column(length = 500)
    private String bio;

    @Column(length = 80)
    private String crewName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected AppUser() {
    }

    public AppUser(String username, String password, String displayName, String instagramUrl, UserRole role) {
        this(username, password, displayName, instagramUrl, null, null, null, role);
    }

    public AppUser(
            String username,
            String password,
            String displayName,
            String instagramUrl,
            String profileImageUrl,
            String bio,
            String crewName,
            UserRole role
    ) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.instagramUrl = instagramUrl;
        this.profileImageUrl = profileImageUrl;
        this.bio = bio;
        this.crewName = crewName;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getInstagramUrl() {
        return instagramUrl;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getBio() {
        return bio;
    }

    public String getCrewName() {
        return crewName;
    }

    public UserRole getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
