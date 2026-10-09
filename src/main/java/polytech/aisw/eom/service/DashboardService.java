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

    public List<Post> findRecentPosts() {
        return postRepository.findTop6ByHiddenByAdminFalseAndAuthor_BlockedFalseOrderByCreatedAtDesc();
    }

    public List<Post> findPopularPosts() {
        return postRepository.findTop5ByHiddenByAdminFalseAndAuthor_BlockedFalseOrderByLikeCountDescViewCountDescCreatedAtDesc();
    }

    public List<Post> findUpcomingEvents() {
        LocalDate today = LocalDate.now();
        LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());
        return postRepository.findTop6ByBoardTypeAndAdminApprovedEventTrueAndHiddenByAdminFalseAndAuthor_BlockedFalseAndEventDateBetweenOrderByEventDateAscCreatedAtDesc(
                BoardType.HYPE,
                today,
                monthEnd
        );
    }

    public List<AppUser> findRecommendedDancers() {
        return userRepository.findTop6ByRoleAndBlockedFalseOrderByCreatedAtDesc(UserRole.USER);
    }

    public List<Post> findFeaturedMediaPosts() {
        return postRepository.findTop6ByBoardTypeAndMediaTypeInAndHiddenByAdminFalseAndAuthor_BlockedFalseOrderByLikeCountDescCreatedAtDesc(
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
        return postRepository.findTop10ByBoardTypeAndHiddenByAdminFalseAndAuthor_BlockedFalseOrderByCreatedAtDesc(boardType);
    }
}
