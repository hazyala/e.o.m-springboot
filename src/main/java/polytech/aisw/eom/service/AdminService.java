package polytech.aisw.eom.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import polytech.aisw.eom.domain.AppUser;
import polytech.aisw.eom.domain.BoardType;
import polytech.aisw.eom.domain.Post;
import polytech.aisw.eom.repository.PostRepository;
import polytech.aisw.eom.repository.UserRepository;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public AdminService(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public List<AppUser> findUsers() {
        return userRepository.findAll();
    }

    public List<Post> findPosts() {
        return postRepository.findAll();
    }

    public List<Post> findReportedPosts() {
        return postRepository.findAll().stream()
                .filter(post -> post.getReportCount() > 0)
                .toList();
    }

    @Transactional
    public void setUserBlocked(Long userId, boolean blocked) {
        AppUser user = userRepository.findById(userId).orElseThrow();
        if (user.getRole().name().equals("ADMIN")) {
            throw new IllegalArgumentException("관리자 계정은 차단할 수 없습니다.");
        }
        user.setBlocked(blocked);
    }

    @Transactional
    public void setPostHidden(Long postId, boolean hidden) {
        Post post = postRepository.findById(postId).orElseThrow();
        post.setHiddenByAdmin(hidden);
    }

    @Transactional
    public void setHypeEventApproved(Long postId, boolean approved) {
        Post post = postRepository.findById(postId).orElseThrow();
        if (post.getBoardType() != BoardType.HYPE || post.getEventDate() == null) {
            throw new IllegalArgumentException("행사일이 있는 HYPE 게시글만 승인할 수 있습니다.");
        }
        post.setAdminApprovedEvent(approved);
    }
}
