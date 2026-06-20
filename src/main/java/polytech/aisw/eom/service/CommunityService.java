package polytech.aisw.eom.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import polytech.aisw.eom.domain.AppUser;
import polytech.aisw.eom.domain.BoardType;
import polytech.aisw.eom.domain.Post;
import polytech.aisw.eom.domain.UserRole;
import polytech.aisw.eom.repository.PostRepository;
import polytech.aisw.eom.repository.UserRepository;

@Service
public class CommunityService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommunityService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Post findPost(Long id) {
        return postRepository.findById(id).orElseThrow();
    }

    public List<Post> findPosts(PostSortOption sortOption) {
        return postRepository.findAll(sortOption.getSort());
    }

    public List<Post> findLatestPosts() {
        return findPosts(PostSortOption.LATEST);
    }

    public List<Post> findPostsByTag(String tag, PostSortOption sortOption) {
        return postRepository.findByTagsContainingIgnoreCase(tag, sortOption.getSort());
    }

    public List<Post> findPostsByTag(String tag) {
        return findPostsByTag(tag, PostSortOption.LATEST);
    }

    public List<Post> findPostsByBoard(BoardType boardType, PostSortOption sortOption) {
        return postRepository.findByBoardType(boardType, sortOption.getSort());
    }

    public List<Post> findPostsByBoard(BoardType boardType) {
        return findPostsByBoard(boardType, PostSortOption.LATEST);
    }

    public List<Post> findPopularPosts() {
        return postRepository.findTop6ByOrderByLikeCountDescViewCountDescCreatedAtDesc();
    }

    public List<Post> findRecentPostsByBoard(BoardType boardType) {
        return postRepository.findTop10ByBoardTypeOrderByCreatedAtDesc(boardType);
    }

    public List<Post> findThisMonthEvents(PostSortOption sortOption) {
        LocalDate today = LocalDate.now();
        return postRepository.findByBoardTypeAndEventDateBetween(
                BoardType.HYPE,
                today,
                today.withDayOfMonth(today.lengthOfMonth()),
                sortOption.getSort()
        );
    }

    public List<Post> findThisMonthEvents() {
        return findThisMonthEvents(PostSortOption.LATEST);
    }

    public List<AppUser> findDancers() {
        return userRepository.findByRoleOrderByCreatedAtDesc(UserRole.USER);
    }

    public AppUser findDancer(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    public List<String> findTags() {
        Set<String> tags = new LinkedHashSet<>();
        postRepository.findTagTexts().forEach(tagText ->
                Arrays.stream(tagText.split(","))
                        .map(String::trim)
                        .filter(tag -> !tag.isBlank())
                        .forEach(tags::add)
        );
        return List.copyOf(tags);
    }

    public List<String> parseTags(String tagText) {
        if (tagText == null || tagText.isBlank()) {
            return List.of();
        }
        return Arrays.stream(tagText.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isBlank())
                .toList();
    }
}
