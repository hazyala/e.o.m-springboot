package polytech.aisw.eom.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

    @Override
    @EntityGraph(attributePaths = "author")
    Optional<Post> findById(Long id);

    @EntityGraph(attributePaths = "author")
    List<Post> findTop6ByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = "author")
    List<Post> findTop6ByBoardTypeOrderByCreatedAtDesc(BoardType boardType);

    @EntityGraph(attributePaths = "author")
    List<Post> findTop10ByBoardTypeOrderByCreatedAtDesc(BoardType boardType);

    @EntityGraph(attributePaths = "author")
    List<Post> findByBoardTypeOrderByCreatedAtDesc(BoardType boardType);

    @EntityGraph(attributePaths = "author")
    List<Post> findTop6ByOrderByLikeCountDescViewCountDescCreatedAtDesc();

    @EntityGraph(attributePaths = "author")
    List<Post> findTop6ByBoardTypeOrderByEventDateAscCreatedAtDesc(BoardType boardType);

    @EntityGraph(attributePaths = "author")
    List<Post> findTop6ByBoardTypeAndEventDateBetweenOrderByEventDateAscCreatedAtDesc(
            BoardType boardType,
            LocalDate startDate,
            LocalDate endDate
    );

    @EntityGraph(attributePaths = "author")
    List<Post> findTop12ByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = "author")
    List<Post> findTop12ByTagsContainingIgnoreCaseOrderByCreatedAtDesc(String tag);

    @EntityGraph(attributePaths = "author")
    List<Post> findTop6ByBoardTypeAndMediaTypeInOrderByLikeCountDescCreatedAtDesc(
            BoardType boardType,
            List<MediaType> mediaTypes
    );

    @Query("select p.tags from Post p where p.tags is not null and p.tags <> ''")
    List<String> findTagTexts();
}
