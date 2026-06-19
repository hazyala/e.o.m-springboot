package polytech.aisw.eom.service;

import java.time.LocalDate;
import java.util.List;
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

    public List<Post> findThisMonthEvents() {
        LocalDate today = LocalDate.now();
        return postRepository.findTop6ByBoardTypeAndEventDateBetweenOrderByEventDateAscCreatedAtDesc(
                BoardType.HYPE,
                today,
                today.withDayOfMonth(today.lengthOfMonth())
        );
    }

    public List<AppUser> findDancers() {
        return userRepository.findTop6ByRoleOrderByCreatedAtDesc(UserRole.USER);
    }
}
