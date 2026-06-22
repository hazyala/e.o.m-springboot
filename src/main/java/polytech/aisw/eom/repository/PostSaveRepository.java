package polytech.aisw.eom.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import polytech.aisw.eom.domain.PostSave;

public interface PostSaveRepository extends JpaRepository<PostSave, Long> {

    @EntityGraph(attributePaths = {"post", "post.author"})
    List<PostSave> findByUserUsernameOrderByCreatedAtDesc(String username);

    Optional<PostSave> findByPostIdAndUserUsername(Long postId, String username);

    boolean existsByPostIdAndUserUsername(Long postId, String username);

    @Modifying
    @Query("delete from PostSave postSave where postSave.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
