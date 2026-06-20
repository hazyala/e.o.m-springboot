package polytech.aisw.eom.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import polytech.aisw.eom.domain.AppUser;
import polytech.aisw.eom.domain.BoardType;
import polytech.aisw.eom.domain.MediaType;
import polytech.aisw.eom.domain.Post;
import polytech.aisw.eom.domain.UserRole;
import polytech.aisw.eom.repository.PostRepository;
import polytech.aisw.eom.repository.UserRepository;

@Service
public class DashboardService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public DashboardService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Post findTodayPick() {
        return postRepository.findTop6ByBoardTypeAndMediaTypeInOrderByLikeCountDescCreatedAtDesc(
                        BoardType.SHOW,
                        List.of(MediaType.INSTAGRAM, MediaType.YOUTUBE, MediaType.VIDEO_LINK)
                )
                .stream()
                .findFirst()
                .orElseGet(() -> postRepository.findTop6ByOrderByLikeCountDescViewCountDescCreatedAtDesc()
                        .stream()
                        .findFirst()
                        .orElse(null));
    }

    public List<Post> findRecentPosts() {
        return postRepository.findTop6ByOrderByCreatedAtDesc();
    }

    public List<Post> findPopularPosts() {
        return postRepository.findTop6ByOrderByLikeCountDescViewCountDescCreatedAtDesc();
    }

    public List<Post> findUpcomingEvents() {
        LocalDate today = LocalDate.now();
        LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());
        return postRepository.findTop6ByBoardTypeAndAdminApprovedEventTrueAndEventDateBetweenOrderByEventDateAscCreatedAtDesc(
                BoardType.HYPE,
                today,
                monthEnd
        );
    }

    public List<AppUser> findRecommendedDancers() {
        return userRepository.findTop6ByRoleOrderByCreatedAtDesc(UserRole.USER);
    }

    public List<Post> findFeaturedMediaPosts() {
        return postRepository.findTop6ByBoardTypeAndMediaTypeInOrderByLikeCountDescCreatedAtDesc(
                BoardType.SHOW,
                List.of(MediaType.INSTAGRAM, MediaType.YOUTUBE, MediaType.VIDEO_LINK)
        );
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

    public List<Post> findRecentPostsByBoard(BoardType boardType) {
        return postRepository.findTop10ByBoardTypeOrderByCreatedAtDesc(boardType);
    }
}
