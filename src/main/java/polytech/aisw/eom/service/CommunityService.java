package polytech.aisw.eom.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import polytech.aisw.eom.domain.AppUser;
import polytech.aisw.eom.domain.BoardType;
import polytech.aisw.eom.domain.Comment;
import polytech.aisw.eom.domain.MediaType;
import polytech.aisw.eom.domain.Post;
import polytech.aisw.eom.domain.UserRole;
import polytech.aisw.eom.dto.CommentCreateRequest;
import polytech.aisw.eom.dto.PostCreateRequest;
import polytech.aisw.eom.repository.CommentRepository;
import polytech.aisw.eom.repository.PostLikeRepository;
import polytech.aisw.eom.repository.PostRepository;
import polytech.aisw.eom.repository.UserRepository;

@Service
public class CommunityService {

    private static final List<String> DANCER_GENRES = List.of(
            "Hip-hop",
            "House",
            "Krump",
            "Popping",
            "Locking",
            "Breaking",
            "Waacking",
            "Voguing",
            "Dancehall"
    );

    private static final Map<String, List<String>> DANCER_GENRE_KEYWORDS = Map.of(
            "Hip-hop", List.of("힙합", "hiphop", "hip-hop", "hip hop", "choreo", "코레오"),
            "House", List.of("하우스", "house"),
            "Krump", List.of("크럼프", "krump"),
            "Popping", List.of("팝핑", "popping"),
            "Locking", List.of("락킹", "locking"),
            "Breaking", List.of("브레이킹", "breaking", "bboy", "b-boy", "bgirl", "b-girl"),
            "Waacking", List.of("왁킹", "waacking", "whacking"),
            "Voguing", List.of("보깅", "voguing", "vogue"),
            "Dancehall", List.of("댄스홀", "dancehall")
    );

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;

    public CommunityService(
            PostRepository postRepository,
            UserRepository userRepository,
            CommentRepository commentRepository,
            PostLikeRepository postLikeRepository
    ) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.postLikeRepository = postLikeRepository;
    }

    public Post findPost(Long id) {
        return postRepository.findById(id).orElseThrow();
    }

    public AppUser findUser(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    public boolean canEditPost(Post post, String username) {
        return username != null && post.isAuthoredBy(username);
    }

    public boolean canDeletePost(Post post, String username) {
        if (username == null) {
            return false;
        }
        AppUser user = findUser(username);
        return post.isAuthoredBy(username) || user.getRole() == UserRole.ADMIN;
    }

    public boolean canDeleteComment(Comment comment, String username) {
        return username != null && comment.isAuthoredBy(username);
    }

    public Post findEditablePost(Long id, String username) {
        Post post = findPost(id);
        assertCanEdit(post, username);
        return post;
    }

    @Transactional
    public Post createPost(PostCreateRequest request, String username) {
        AppUser author = findUser(username);
        String mediaUrl = normalizeText(request.getMediaUrl());
        String thumbnailUrl = normalizeText(request.getThumbnailUrl());
        MediaType mediaType = resolveMediaType(mediaUrl);
        Post post = new Post(
                request.getBoardType(),
                request.getTitle().trim(),
                request.getContent().trim(),
                mediaType == MediaType.INSTAGRAM ? mediaUrl : "",
                thumbnailUrl,
                0,
                0,
                0,
                normalizeTags(request.getTags()),
                normalizeText(request.getLocation()),
                request.getEventDate(),
                request.getDeadline(),
                mediaType,
                mediaUrl,
                thumbnailUrl,
                author
        );

        if (canApproveOfficialEvent(request, author)) {
            post.approveAsOfficialEvent();
        }

        return postRepository.save(post);
    }

    @Transactional
    public Post updatePost(Long id, PostCreateRequest request, String username) {
        Post post = findPost(id);
        assertCanEdit(post, username);
        AppUser editor = findUser(username);
        applyPostDetails(post, request, editor);
        return post;
    }

    @Transactional
    public void deletePost(Long id, String username) {
        Post post = findPost(id);
        if (!canDeletePost(post, username)) {
            throw new AccessDeniedException("게시글을 삭제할 권한이 없습니다.");
        }
        commentRepository.deleteByPostId(post.getId());
        postLikeRepository.deleteByPostId(post.getId());
        postRepository.delete(post);
    }

    public List<Comment> findComments(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }

    @Transactional
    public Comment createComment(Long postId, CommentCreateRequest request, String username) {
        Post post = findPost(postId);
        AppUser author = findUser(username);
        Comment comment = commentRepository.save(new Comment(post, author, request.getContent().trim()));
        post.increaseCommentCount();
        return comment;
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("게시글과 댓글이 일치하지 않습니다.");
        }
        if (!canDeleteComment(comment, username)) {
            throw new AccessDeniedException("댓글을 삭제할 권한이 없습니다.");
        }
        comment.getPost().decreaseCommentCount();
        commentRepository.delete(comment);
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

    public List<Post> searchPosts(String query, PostSortOption sortOption) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return postRepository.searchPosts(query.trim(), sortOption.getSort());
    }

    public List<Post> findPostsByTag(String tag) {
        return findPostsByTag(tag, PostSortOption.LATEST);
    }

    public List<Post> findPostsByBoard(BoardType boardType, PostSortOption sortOption) {
        return postRepository.findByBoardType(boardType, sortOption.getSort());
    }

    public List<Post> findOfficialEventPosts(PostSortOption sortOption) {
        return postRepository.findByBoardTypeAndAdminApprovedEventTrue(BoardType.HYPE, sortOption.getSort());
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

    public List<AppUser> findDancers() {
        return userRepository.findByRoleOrderByCreatedAtDesc(UserRole.USER);
    }

    public List<AppUser> findDancers(List<String> selectedGenres) {
        List<String> normalizedGenres = normalizeDancerGenres(selectedGenres);
        if (normalizedGenres.isEmpty()) {
            return findDancers();
        }

        return userRepository.findByRoleOrderByCreatedAtDesc(UserRole.USER)
                .stream()
                .filter(dancer -> matchesAnyGenre(dancer.getPrimaryGenre(), normalizedGenres))
                .toList();
    }

    public List<String> findDancerGenres() {
        return DANCER_GENRES;
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

    private MediaType resolveMediaType(String mediaUrl) {
        if (mediaUrl.isBlank()) {
            return MediaType.IMAGE;
        }

        String lowerUrl = mediaUrl.toLowerCase();
        if (lowerUrl.contains("instagram.com/")) {
            return MediaType.INSTAGRAM;
        }
        if (lowerUrl.contains("youtube.com/") || lowerUrl.contains("youtu.be/")) {
            return MediaType.YOUTUBE;
        }
        if (lowerUrl.endsWith(".mp4") || lowerUrl.endsWith(".mov") || lowerUrl.endsWith(".webm")) {
            return MediaType.VIDEO_LINK;
        }
        return MediaType.EXTERNAL_LINK;
    }

    private void applyPostDetails(Post post, PostCreateRequest request, AppUser editor) {
        String mediaUrl = normalizeText(request.getMediaUrl());
        String thumbnailUrl = normalizeText(request.getThumbnailUrl());
        MediaType mediaType = resolveMediaType(mediaUrl);
        boolean adminApprovedEvent = canApproveOfficialEvent(request, editor);
        post.updateDetails(
                request.getBoardType(),
                request.getTitle().trim(),
                request.getContent().trim(),
                mediaType == MediaType.INSTAGRAM ? mediaUrl : "",
                thumbnailUrl,
                normalizeTags(request.getTags()),
                normalizeText(request.getLocation()),
                request.getEventDate(),
                request.getDeadline(),
                mediaType,
                mediaUrl,
                thumbnailUrl,
                adminApprovedEvent
        );
    }

    private boolean canApproveOfficialEvent(PostCreateRequest request, AppUser user) {
        return request.isAdminApprovedEvent()
                && user.getRole() == UserRole.ADMIN
                && request.getBoardType() == BoardType.HYPE
                && request.getEventDate() != null;
    }

    private void assertCanEdit(Post post, String username) {
        if (!canEditPost(post, username)) {
            throw new AccessDeniedException("게시글을 수정할 권한이 없습니다.");
        }
    }

    private String normalizeTags(String tagText) {
        if (tagText == null || tagText.isBlank()) {
            return "";
        }

        return Arrays.stream(tagText.split("[,#]"))
                .map(String::trim)
                .filter(tag -> !tag.isBlank())
                .distinct()
                .reduce((left, right) -> left + "," + right)
                .orElse("");
    }

    private String normalizeText(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        return text.trim();
    }

    private List<String> normalizeDancerGenres(List<String> genres) {
        if (genres == null || genres.isEmpty()) {
            return List.of();
        }

        Set<String> normalizedGenres = new LinkedHashSet<>();
        genres.stream()
                .filter(genre -> genre != null && !genre.isBlank())
                .map(String::trim)
                .filter(DANCER_GENRES::contains)
                .forEach(normalizedGenres::add);
        return new ArrayList<>(normalizedGenres);
    }

    private boolean matchesAnyGenre(String primaryGenre, List<String> selectedGenres) {
        if (primaryGenre == null || primaryGenre.isBlank()) {
            return false;
        }

        String searchableGenre = primaryGenre.toLowerCase();
        return selectedGenres.stream()
                .map(DANCER_GENRE_KEYWORDS::get)
                .anyMatch(keywords -> keywords != null
                        && keywords.stream().anyMatch(keyword -> searchableGenre.contains(keyword.toLowerCase())));
    }
}
