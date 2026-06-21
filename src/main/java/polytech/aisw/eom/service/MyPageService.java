package polytech.aisw.eom.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import polytech.aisw.eom.domain.AppUser;
import polytech.aisw.eom.domain.Comment;
import polytech.aisw.eom.domain.JoinedEvent;
import polytech.aisw.eom.domain.Post;
import polytech.aisw.eom.repository.CommentRepository;
import polytech.aisw.eom.repository.JoinedEventRepository;
import polytech.aisw.eom.repository.PostLikeRepository;
import polytech.aisw.eom.repository.PostRepository;
import polytech.aisw.eom.repository.PostSaveRepository;
import polytech.aisw.eom.repository.UserRepository;

@Service
public class MyPageService {

    private static final int MAX_PINNED_PORTFOLIO = 3;

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostSaveRepository postSaveRepository;
    private final CommentRepository commentRepository;
    private final JoinedEventRepository joinedEventRepository;
    private final PasswordEncoder passwordEncoder;

    public MyPageService(
            UserRepository userRepository,
            PostRepository postRepository,
            PostLikeRepository postLikeRepository,
            PostSaveRepository postSaveRepository,
            CommentRepository commentRepository,
            JoinedEventRepository joinedEventRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.postLikeRepository = postLikeRepository;
        this.postSaveRepository = postSaveRepository;
        this.commentRepository = commentRepository;
        this.joinedEventRepository = joinedEventRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public MyPageView findMyPage(String username) {
        AppUser user = findUser(username);
        return buildMyPageView(user);
    }

    @Transactional(readOnly = true)
    public MyPageView findProfilePage(Long userId) {
        AppUser user = userRepository.findById(userId).orElseThrow();
        return buildMyPageView(user);
    }

    private MyPageView buildMyPageView(AppUser user) {
        String username = user.getUsername();
        List<Post> posts = postRepository.findByAuthorUsernameOrderByCreatedAtDesc(username);
        List<Post> portfolioPosts = postRepository
                .findByAuthorUsernameAndPortfolioSelectedTrueOrderByPortfolioPinnedDescCreatedAtDesc(username);
        List<Post> likedPosts = postLikeRepository.findByUserUsernameOrderByCreatedAtDesc(username).stream()
                .map(like -> like.getPost())
                .toList();
        List<Post> savedPosts = postSaveRepository.findByUserUsernameOrderByCreatedAtDesc(username).stream()
                .map(save -> save.getPost())
                .toList();
        List<Comment> comments = commentRepository.findByAuthorUsernameOrderByCreatedAtDesc(username);
        List<JoinedEvent> joinedEvents = joinedEventRepository.findByUserUsernameOrderByEventDateDescCreatedAtDesc(username);
        List<ActivityItem> activityItems = buildActivity(posts, comments);

        return new MyPageView(
                user,
                posts,
                portfolioPosts,
                likedPosts,
                savedPosts,
                comments,
                joinedEvents,
                activityItems
        );
    }

    @Transactional
    public AccountUpdateResult updateAccount(String username, AccountUpdateRequest request) {
        AppUser user = findUser(username);
        String nextUsername = cleanRequired(request.username(), username);
        if (!nextUsername.equals(username) && userRepository.findByUsername(nextUsername).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (!passwordEncoder.matches(cleanRequired(request.currentPassword(), ""), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
        }

        String nextPassword = user.getPassword();
        String newPassword = clean(request.newPassword());
        if (newPassword != null) {
            nextPassword = passwordEncoder.encode(newPassword);
        }
        user.updateAccount(nextUsername, nextPassword);
        return new AccountUpdateResult(!nextUsername.equals(username));
    }

    @Transactional
    public void updateProfile(String username, ProfileUpdateRequest request) {
        AppUser user = findUser(username);
        user.updateProfile(
                cleanRequired(request.displayName(), user.getDisplayName()),
                clean(request.crewName()),
                clean(request.primaryGenre()),
                clean(request.bio()),
                clean(request.instagramUrl()),
                clean(request.profileImageUrl()),
                clean(request.headerImageUrl())
        );
    }

    @Transactional
    public void updatePortfolioSelection(String username, Long postId, boolean selected) {
        Post post = findOwnedPost(username, postId);
        post.setPortfolioSelected(selected);
    }

    @Transactional
    public void updatePortfolioPin(String username, Long postId, boolean pinned) {
        Post post = findOwnedPost(username, postId);
        if (pinned && !post.isPortfolioPinned()
                && postRepository.countByAuthorUsernameAndPortfolioPinnedTrue(username) >= MAX_PINNED_PORTFOLIO) {
            throw new IllegalStateException("Pinned portfolio limit exceeded");
        }
        post.setPortfolioPinned(pinned);
    }

    @Transactional
    public void addJoinedEvent(String username, LocalDate eventDate, String eventName, String result) {
        AppUser user = findUser(username);
        joinedEventRepository.save(new JoinedEvent(
                user,
                eventDate,
                cleanRequired(eventName, "Untitled event"),
                cleanRequired(result, "참여")
        ));
    }

    @Transactional
    public void updateJoinedEvent(String username, Long eventId, LocalDate eventDate, String eventName, String result) {
        JoinedEvent joinedEvent = findOwnedJoinedEvent(username, eventId);
        joinedEvent.update(
                eventDate,
                cleanRequired(eventName, "Untitled event"),
                cleanRequired(result, "참여")
        );
    }

    @Transactional
    public void deleteJoinedEvent(String username, Long eventId) {
        JoinedEvent joinedEvent = findOwnedJoinedEvent(username, eventId);
        joinedEventRepository.delete(joinedEvent);
    }

    private AppUser findUser(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    private Post findOwnedPost(String username, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow();
        if (!post.isAuthoredBy(username)) {
            throw new IllegalArgumentException("Cannot edit another user's portfolio");
        }
        return post;
    }

    private JoinedEvent findOwnedJoinedEvent(String username, Long eventId) {
        JoinedEvent joinedEvent = joinedEventRepository.findById(eventId).orElseThrow();
        if (!joinedEvent.isOwnedBy(username)) {
            throw new IllegalArgumentException("Cannot edit another user's joined event");
        }
        return joinedEvent;
    }

    private List<ActivityItem> buildActivity(List<Post> posts, List<Comment> comments) {
        return Stream.concat(
                        posts.stream().map(post -> new ActivityItem(
                                post.getCreatedAt(),
                                post.getBoardType().getLabel() + " 게시글 작성",
                                post.getTitle(),
                                "/posts/" + post.getId()
                        )),
                        comments.stream().map(comment -> new ActivityItem(
                                comment.getCreatedAt(),
                                "댓글 작성",
                                comment.getPost().getTitle(),
                                "/posts/" + comment.getPost().getId()
                        ))
                )
                .sorted(Comparator.comparing(ActivityItem::createdAt).reversed())
                .limit(5)
                .toList();
    }

    private String clean(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String cleanRequired(String value, String fallback) {
        String cleaned = clean(value);
        return cleaned == null ? fallback : cleaned;
    }

    public record MyPageView(
            AppUser user,
            List<Post> posts,
            List<Post> portfolioPosts,
            List<Post> likedPosts,
            List<Post> savedPosts,
            List<Comment> comments,
            List<JoinedEvent> joinedEvents,
            List<ActivityItem> activityItems
    ) {
    }

    public record ActivityItem(
            java.time.LocalDateTime createdAt,
            String action,
            String title,
            String href
    ) {
    }

    public record ProfileUpdateRequest(
            String displayName,
            String crewName,
            String primaryGenre,
            String bio,
            String instagramUrl,
            String profileImageUrl,
            String headerImageUrl
    ) {
    }

    public record AccountUpdateRequest(
            String username,
            String currentPassword,
            String newPassword
    ) {
    }

    public record AccountUpdateResult(
            boolean usernameChanged
    ) {
    }
}
