package polytech.aisw.eom.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import polytech.aisw.eom.domain.AppUser;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);
}

