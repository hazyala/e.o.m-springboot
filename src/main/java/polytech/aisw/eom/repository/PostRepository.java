package polytech.aisw.eom.repository;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import polytech.aisw.eom.domain.BoardType;
import polytech.aisw.eom.domain.MediaType;
import polytech.aisw.eom.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Override
    @EntityGraph(attributePaths = "author")
    List<Post> findAll();

    @EntityGraph(attributePaths = "author")
    List<Post> findTop6ByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = "author")
    List<Post> findTop6ByBoardTypeOrderByCreatedAtDesc(BoardType boardType);

    @EntityGraph(attributePaths = "author")
    List<Post> findTop6ByOrderByLikeCountDescViewCountDescCreatedAtDesc();

    @EntityGraph(attributePaths = "author")
    List<Post> findTop6ByBoardTypeOrderByEventDateAscCreatedAtDesc(BoardType boardType);

    @EntityGraph(attributePaths = "author")
    List<Post> findTop6ByBoardTypeAndMediaTypeInOrderByLikeCountDescCreatedAtDesc(
            BoardType boardType,
            List<MediaType> mediaTypes
    );

    @Query("select p.tags from Post p where p.tags is not null and p.tags <> ''")
    List<String> findTagTexts();
}
