package polytech.aisw.eom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import polytech.aisw.eom.domain.PostLike;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
}
