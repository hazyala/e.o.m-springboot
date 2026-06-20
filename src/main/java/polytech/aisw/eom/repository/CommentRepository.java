package polytech.aisw.eom.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import polytech.aisw.eom.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"author", "post", "post.author"})
    List<Comment> findByAuthorUsernameOrderByCreatedAtDesc(String username);
}
