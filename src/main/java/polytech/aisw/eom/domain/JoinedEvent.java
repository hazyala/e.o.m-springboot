package polytech.aisw.eom.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "joined_events")
public class JoinedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppUser user;

    @Column(nullable = false)
    private LocalDate eventDate;

    @Column(nullable = false, length = 120)
    private String eventName;

    @Column(nullable = false, length = 120)
    private String result;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected JoinedEvent() {
    }

    public JoinedEvent(AppUser user, LocalDate eventDate, String eventName, String result) {
        this.user = user;
        this.eventDate = eventDate;
        this.eventName = eventName;
        this.result = result;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public AppUser getUser() {
        return user;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public String getEventName() {
        return eventName;
    }

    public String getResult() {
        return result;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
