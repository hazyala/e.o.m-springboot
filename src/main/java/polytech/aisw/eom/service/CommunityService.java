package polytech.aisw.eom.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import polytech.aisw.eom.domain.AppUser;
import polytech.aisw.eom.domain.BoardType;
import polytech.aisw.eom.domain.MediaType;
import polytech.aisw.eom.domain.Post;
import polytech.aisw.eom.domain.UserRole;
import polytech.aisw.eom.dto.PostCreateRequest;
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

    public CommunityService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Post findPost(Long id) {
        return postRepository.findById(id).orElseThrow();
    }

    public AppUser findUser(String username) {
        return userRepository.findByUsername(username).orElseThrow();
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

        if (request.isAdminApprovedEvent() && author.getRole() == UserRole.ADMIN) {
            post.approveAsOfficialEvent();
        }

        return postRepository.save(post);
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
