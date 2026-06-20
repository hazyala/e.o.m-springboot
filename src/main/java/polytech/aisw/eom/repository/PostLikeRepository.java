package polytech.aisw.eom.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import polytech.aisw.eom.domain.PostLike;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    @EntityGraph(attributePaths = {"post", "post.author"})
    List<PostLike> findByUserUsernameOrderByCreatedAtDesc(String username);
}
