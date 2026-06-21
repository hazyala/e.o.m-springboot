package polytech.aisw.eom.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import polytech.aisw.eom.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"author", "post", "post.author"})
    List<Comment> findByAuthorUsernameOrderByCreatedAtDesc(String username);

    @EntityGraph(attributePaths = {"author"})
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);

    @Modifying
    @Query("delete from Comment c where c.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
