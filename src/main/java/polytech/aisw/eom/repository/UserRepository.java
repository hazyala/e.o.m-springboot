package polytech.aisw.eom.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import polytech.aisw.eom.domain.AppUser;
import polytech.aisw.eom.domain.UserRole;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);

    List<AppUser> findTop6ByRoleOrderByCreatedAtDesc(UserRole role);

    List<AppUser> findByRoleOrderByCreatedAtDesc(UserRole role);
}
