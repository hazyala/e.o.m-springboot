package polytech.aisw.eom.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import polytech.aisw.eom.domain.JoinedEvent;

public interface JoinedEventRepository extends JpaRepository<JoinedEvent, Long> {

    List<JoinedEvent> findByUserUsernameOrderByEventDateDescCreatedAtDesc(String username);
}
