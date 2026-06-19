package polytech.aisw.eom.repository;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import polytech.aisw.eom.domain.BoardType;
import polytech.aisw.eom.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Override
    @EntityGraph(attributePaths = "author")
    List<Post> findAll();

    @EntityGraph(attributePaths = "author")
    List<Post> findTop6ByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = "author")
    List<Post> findTop6ByBoardTypeOrderByCreatedAtDesc(BoardType boardType);
}
