package polytech.aisw.eom.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import polytech.aisw.eom.domain.BoardType;
import polytech.aisw.eom.domain.MediaType;
import polytech.aisw.eom.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Override
    @EntityGraph(attributePaths = "author")
    List<Post> findAll();

    @Override
    @EntityGraph(attributePaths = "author")
    List<Post> findAll(Sort sort);

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
    List<Post> findByBoardType(BoardType boardType, Sort sort);

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
    List<Post> findByTagsContainingIgnoreCase(String tag, Sort sort);

    @EntityGraph(attributePaths = "author")
    @Query("""
            select p from Post p
            join p.author a
            where lower(p.tags) like lower(concat('%', :query, '%'))
               or lower(p.title) like lower(concat('%', :query, '%'))
               or lower(p.content) like lower(concat('%', :query, '%'))
               or lower(a.displayName) like lower(concat('%', :query, '%'))
               or lower(a.crewName) like lower(concat('%', :query, '%'))
            """)
    List<Post> searchPosts(@Param("query") String query, Sort sort);

    @EntityGraph(attributePaths = "author")
    List<Post> findByBoardTypeAndEventDateBetween(
            BoardType boardType,
            LocalDate startDate,
            LocalDate endDate,
            Sort sort
    );

    @EntityGraph(attributePaths = "author")
    List<Post> findByAuthorUsernameOrderByCreatedAtDesc(String username);

    @EntityGraph(attributePaths = "author")
    List<Post> findByAuthorUsernameAndPortfolioSelectedTrueOrderByPortfolioPinnedDescCreatedAtDesc(String username);

    long countByAuthorUsernameAndPortfolioPinnedTrue(String username);

    @EntityGraph(attributePaths = "author")
    List<Post> findTop6ByBoardTypeAndMediaTypeInOrderByLikeCountDescCreatedAtDesc(
            BoardType boardType,
            List<MediaType> mediaTypes
    );

    @Query("select p.tags from Post p where p.tags is not null and p.tags <> ''")
    List<String> findTagTexts();
}
