package polytech.aisw.eom.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import polytech.aisw.eom.domain.PostLike;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    @EntityGraph(attributePaths = {"post", "post.author"})
    List<PostLike> findByUserUsernameOrderByCreatedAtDesc(String username);

    Optional<PostLike> findByPostIdAndUserUsername(Long postId, String username);

    boolean existsByPostIdAndUserUsername(Long postId, String username);

    @Modifying
    @Query("delete from PostLike postLike where postLike.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
