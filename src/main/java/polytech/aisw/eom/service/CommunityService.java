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

    public List<Post> findLatestPosts() {
        return postRepository.findTop12ByOrderByCreatedAtDesc();
    }

    public List<Post> findPostsByTag(String tag) {
        return postRepository.findTop12ByTagsContainingIgnoreCaseOrderByCreatedAtDesc(tag);
    }

    public List<Post> findPostsByBoard(BoardType boardType) {
        return postRepository.findByBoardTypeOrderByCreatedAtDesc(boardType);
    }

    public List<Post> findPopularPosts() {
        return postRepository.findTop6ByOrderByLikeCountDescViewCountDescCreatedAtDesc();
    }

    public List<Post> findRecentPostsByBoard(BoardType boardType) {
        return postRepository.findTop10ByBoardTypeOrderByCreatedAtDesc(boardType);
    }

    public List<Post> findThisMonthEvents() {
        LocalDate today = LocalDate.now();
        return postRepository.findTop6ByBoardTypeAndEventDateBetweenOrderByEventDateAscCreatedAtDesc(
                BoardType.HYPE,
                today,
                today.withDayOfMonth(today.lengthOfMonth())
        );
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
