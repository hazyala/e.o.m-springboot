package polytech.aisw.eom.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import polytech.aisw.eom.domain.BoardType;
import polytech.aisw.eom.domain.UserRole;
import polytech.aisw.eom.dto.CommentCreateRequest;
import polytech.aisw.eom.dto.PostCreateRequest;
import polytech.aisw.eom.service.CommunityService;
import polytech.aisw.eom.service.MyPageService;
import polytech.aisw.eom.service.PostSortOption;

@Controller
public class CommunityController {

    private final CommunityService communityService;
    private final MyPageService myPageService;

    public CommunityController(CommunityService communityService, MyPageService myPageService) {
        this.communityService = communityService;
        this.myPageService = myPageService;
    }

    @GetMapping("/posts/new")
    public String newPost(
            @RequestParam(required = false) String board,
            Model model,
            Principal principal
    ) {
        if (!model.containsAttribute("postCreateRequest")) {
            PostCreateRequest postCreateRequest = new PostCreateRequest();
            resolveBoardType(board).ifPresent(postCreateRequest::setBoardType);
            model.addAttribute("postCreateRequest", postCreateRequest);
        }
        populatePostCreateModel(model, principal);
        return "post-create";
    }

    @PostMapping("/posts/new")
    public String createPost(
            @Valid @ModelAttribute PostCreateRequest postCreateRequest,
            BindingResult bindingResult,
            Model model,
            Principal principal
    ) {
        if (bindingResult.hasErrors()) {
            populatePostCreateModel(model, principal);
            return "post-create";
        }

        var post = communityService.createPost(postCreateRequest, principal.getName());
        return "redirect:/posts/" + post.getId();
    }

    @GetMapping("/posts/{id}/edit")
    public String editPost(
            @PathVariable Long id,
            Model model,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        try {
            var post = communityService.findEditablePost(id, principal.getName());
            if (!model.containsAttribute("postCreateRequest")) {
                model.addAttribute("postCreateRequest", PostCreateRequest.from(post));
            }
            populatePostCreateModel(model, principal);
            model.addAttribute("post", post);
            model.addAttribute("formMode", "edit");
            model.addAttribute("formAction", "/posts/" + id + "/edit");
            return "post-create";
        } catch (AccessDeniedException exception) {
            redirectAttributes.addFlashAttribute("postError", exception.getMessage());
            return "redirect:/posts/" + id;
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("postError", "게시글을 찾을 수 없습니다.");
            return "redirect:/boards/all";
        }
    }

    @PostMapping("/posts/{id}/edit")
    public String updatePost(
            @PathVariable Long id,
            @Valid @ModelAttribute PostCreateRequest postCreateRequest,
            BindingResult bindingResult,
            Model model,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            try {
                var post = communityService.findEditablePost(id, principal.getName());
                populatePostCreateModel(model, principal);
                model.addAttribute("post", post);
                model.addAttribute("formMode", "edit");
                model.addAttribute("formAction", "/posts/" + id + "/edit");
                return "post-create";
            } catch (AccessDeniedException exception) {
                redirectAttributes.addFlashAttribute("postError", exception.getMessage());
                return "redirect:/posts/" + id;
            } catch (RuntimeException exception) {
                redirectAttributes.addFlashAttribute("postError", "게시글을 찾을 수 없습니다.");
                return "redirect:/boards/all";
            }
        }

        try {
            var post = communityService.updatePost(id, postCreateRequest, principal.getName());
            redirectAttributes.addFlashAttribute("postNotice", "게시글이 수정되었습니다.");
            return "redirect:/posts/" + post.getId();
        } catch (AccessDeniedException exception) {
            redirectAttributes.addFlashAttribute("postError", exception.getMessage());
            return "redirect:/posts/" + id;
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("postError", "게시글을 찾을 수 없습니다.");
            return "redirect:/boards/all";
        }
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(
            @PathVariable Long id,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        try {
            communityService.deletePost(id, principal.getName());
            redirectAttributes.addFlashAttribute("postNotice", "게시글이 삭제되었습니다.");
            return "redirect:/boards/all";
        } catch (AccessDeniedException exception) {
            redirectAttributes.addFlashAttribute("postError", exception.getMessage());
            return "redirect:/posts/" + id;
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("postError", "게시글을 찾을 수 없습니다.");
            return "redirect:/boards/all";
        }
    }

    @PostMapping("/posts/{id}/comments")
    public String createComment(
            @PathVariable Long id,
            @Valid @ModelAttribute CommentCreateRequest commentCreateRequest,
            BindingResult bindingResult,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("commentError", "댓글은 1자 이상 500자 이하로 입력해주세요.");
            return "redirect:/posts/" + id;
        }

        try {
            communityService.createComment(id, commentCreateRequest, principal.getName());
            redirectAttributes.addFlashAttribute("postNotice", "댓글이 등록되었습니다.");
        } catch (AccessDeniedException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("commentError", "댓글을 등록할 수 없습니다.");
        }
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/like")
    public String togglePostLike(
            @PathVariable Long id,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        try {
            boolean liked = communityService.togglePostLike(id, principal.getName());
            redirectAttributes.addFlashAttribute("postNotice", liked ? "좋아요를 눌렀습니다." : "좋아요를 취소했습니다.");
        } catch (AccessDeniedException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("postError", "좋아요를 처리할 수 없습니다.");
        }
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/save")
    public String togglePostSave(
            @PathVariable Long id,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        try {
            boolean saved = communityService.togglePostSave(id, principal.getName());
            redirectAttributes.addFlashAttribute("postNotice", saved ? "게시글을 저장했습니다." : "저장을 취소했습니다.");
        } catch (AccessDeniedException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("postError", "저장을 처리할 수 없습니다.");
        }
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/report")
    public String reportPost(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        try {
            communityService.reportPost(id, reason, principal.getName());
            redirectAttributes.addFlashAttribute("postNotice", "신고가 접수되었습니다. 운영자가 확인합니다.");
        } catch (AccessDeniedException exception) {
            redirectAttributes.addFlashAttribute("postError", exception.getMessage());
            return "redirect:/boards/all";
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("postError", "신고를 접수할 수 없습니다.");
        }
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/delete")
    public String deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        try {
            communityService.deleteComment(postId, commentId, principal.getName());
            redirectAttributes.addFlashAttribute("postNotice", "댓글이 삭제되었습니다.");
        } catch (AccessDeniedException exception) {
            redirectAttributes.addFlashAttribute("commentError", exception.getMessage());
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("commentError", "댓글을 삭제할 수 없습니다.");
        }
        return "redirect:/posts/" + postId;
    }

    @GetMapping("/posts/{id}")
    public String postDetail(
            @PathVariable Long id,
            HttpServletRequest request,
            Model model,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        try {
            String username = principal == null ? null : principal.getName();
            var post = communityService.findPostForViewer(id, username);
            var comments = communityService.findComments(id);
            Set<Long> deletableCommentIds = comments.stream()
                    .filter(comment -> communityService.canDeleteComment(comment, username))
                    .map(comment -> comment.getId())
                    .collect(Collectors.toSet());
            model.addAttribute("post", post);
            model.addAttribute("comments", comments);
            model.addAttribute("commentCount", comments.size());
            model.addAttribute("commentCreateRequest", new CommentCreateRequest());
            model.addAttribute("deletableCommentIds", deletableCommentIds);
            model.addAttribute("backUrl", resolveBackUrl(request));
            model.addAttribute("boards", BoardType.values());
            model.addAttribute("postTags", communityService.parseTags(post.getTags()));
            model.addAttribute("popularPosts", communityService.findPopularPosts());
            model.addAttribute("recentPosts", communityService.findRecentPostsByBoard(post.getBoardType()));
            model.addAttribute("canEditPost", communityService.canEditPost(post, username));
            model.addAttribute("canDeletePost", communityService.canDeletePost(post, username));
            model.addAttribute("likedByCurrentUser", communityService.hasLikedPost(id, username));
            model.addAttribute("savedByCurrentUser", communityService.hasSavedPost(id, username));
            return "post-detail";
        } catch (AccessDeniedException exception) {
            redirectAttributes.addFlashAttribute("postError", exception.getMessage());
            return "redirect:/boards/all";
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("postError", "게시글을 찾을 수 없습니다.");
            return "redirect:/boards/all";
        }
    }

    @GetMapping("/posts")
    public String posts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "latest") String sort,
            Model model
    ) {
        String searchQuery = normalizeSearchTerm(q);
        boolean hasTag = tag != null && !tag.isBlank();
        PostSortOption sortOption = PostSortOption.from(sort);

        String effectiveQuery = hasTag ? tag.trim() : searchQuery;
        model.addAttribute("title", "SEARCH");
        model.addAttribute("eyebrow", hasTag ? "TAG SEARCH" : "COMMUNITY SEARCH");
        model.addAttribute("summary", "태그, 제목, 내용, 작성자, 크루명에서 입력한 움직임을 찾아봅니다.");
        model.addAttribute("searchQuery", effectiveQuery);
        model.addAttribute("hasSearchTerm", !effectiveQuery.isBlank());
        model.addAttribute("selectedTag", hasTag ? tag : null);
        model.addAttribute("isSearchPage", true);
        populatePostListModel(model, sortOption);
        var posts = hasTag
                ? communityService.findPostsByTag(tag, sortOption)
                : communityService.searchPosts(searchQuery, sortOption);
        model.addAttribute("posts", posts);
        model.addAttribute("postCount", posts.size());
        model.addAttribute("sortLinks", buildSearchSortLinks(hasTag ? null : searchQuery, hasTag ? tag : null));
        return "post-list";
    }

    @GetMapping("/activity")
    public String activity(@RequestParam(defaultValue = "latest") String sort) {
        PostSortOption sortOption = PostSortOption.from(sort);
        return "redirect:/boards/all?sort=" + sortOption.getKey();
    }

    @GetMapping("/events")
    public String events() {
        return "redirect:/boards/HYPE?officialEvents=true";
    }

    @GetMapping("/boards/{board}")
    public String board(
            @PathVariable String board,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(name = "officialEvents", defaultValue = "false") boolean officialEvents,
            Model model
    ) {
        PostSortOption sortOption = PostSortOption.from(sort);
        if ("all".equalsIgnoreCase(board)) {
            model.addAttribute("title", "ALL");
            model.addAttribute("eyebrow", "COMMUNITY");
            model.addAttribute("summary", "SHOW, CAST, HYPE, LINK 전체 게시글을 한 화면에서 탐색하고 원하는 기준으로 정렬합니다.");
            populatePostListModel(model, sortOption);
            var posts = communityService.findPosts(sortOption);
            model.addAttribute("posts", posts);
            model.addAttribute("postCount", posts.size());
            model.addAttribute("sortLinks", buildSortLinks("/boards/all", null, false));
            return "post-list";
        }

        BoardType boardType = BoardType.valueOf(board.toUpperCase());
        boolean officialEventsOnly = boardType == BoardType.HYPE && officialEvents;
        PostSortOption effectiveSortOption = officialEventsOnly ? PostSortOption.LATEST : sortOption;
        model.addAttribute("title", boardType.getLabel());
        model.addAttribute("eyebrow", "BOARD");
        model.addAttribute("summary", officialEventsOnly
                ? "관리자가 승인한 공식 행사, 배틀, 공연 HYPE 글만 모아봅니다."
                : boardType.getDescription());
        model.addAttribute("selectedBoard", boardType);
        model.addAttribute("officialEventsOnly", officialEventsOnly);
        model.addAttribute("officialEventsLink", buildOfficialEventsLink());
        populatePostListModel(model, effectiveSortOption);
        var posts = officialEventsOnly
                ? communityService.findOfficialEventPosts(PostSortOption.LATEST)
                : communityService.findPostsByBoard(boardType, effectiveSortOption);
        model.addAttribute("posts", posts);
        model.addAttribute("postCount", posts.size());
        model.addAttribute("sortLinks", buildSortLinks("/boards/" + boardType.name(), null, false));
        return "post-list";
    }

    private void populatePostListModel(Model model, PostSortOption sortOption) {
        model.addAttribute("boards", BoardType.values());
        model.addAttribute("tags", communityService.findTags());
        model.addAttribute("sortOptions", PostSortOption.values());
        model.addAttribute("selectedSort", sortOption);
    }

    private void populatePostCreateModel(Model model, Principal principal) {
        model.addAttribute("boards", BoardType.values());
        var user = communityService.findUser(principal.getName());
        model.addAttribute("currentUser", user);
        model.addAttribute("canApproveOfficialEvent", user.getRole() == UserRole.ADMIN);
    }

    private Map<PostSortOption, String> buildSortLinks(String basePath, String tag) {
        return buildSortLinks(basePath, tag, false);
    }

    private Map<PostSortOption, String> buildSortLinks(String basePath, String tag, boolean officialEventsOnly) {
        Map<PostSortOption, String> links = new LinkedHashMap<>();
        for (PostSortOption option : PostSortOption.values()) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromPath(basePath);
            if (tag != null && !tag.isBlank()) {
                builder.queryParam("tag", tag);
            }
            if (officialEventsOnly) {
                builder.queryParam("officialEvents", true);
            }
            builder.queryParam("sort", option.getKey());
            links.put(option, builder.build().toUriString());
        }
        return links;
    }

    private String buildOfficialEventsLink() {
        return UriComponentsBuilder.fromPath("/boards/HYPE")
                .queryParam("officialEvents", true)
                .build()
                .toUriString();
    }

    private String normalizeSearchTerm(String query) {
        if (query == null || query.isBlank()) {
            return "";
        }
        String normalized = query.trim();
        while (normalized.startsWith("#")) {
            normalized = normalized.substring(1).trim();
        }
        return normalized;
    }

    private Optional<BoardType> resolveBoardType(String board) {
        if (board == null || board.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(BoardType.valueOf(board.trim().toUpperCase()));
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

    private Map<PostSortOption, String> buildSearchSortLinks(String query, String tag) {
        Map<PostSortOption, String> links = new LinkedHashMap<>();
        for (PostSortOption option : PostSortOption.values()) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/posts");
            if (query != null && !query.isBlank()) {
                builder.queryParam("q", query);
            }
            if (tag != null && !tag.isBlank()) {
                builder.queryParam("tag", tag);
            }
            builder.queryParam("sort", option.getKey());
            links.put(option, builder.build().toUriString());
        }
        return links;
    }

    @GetMapping("/dancers")
    public String dancers(@RequestParam(name = "genres", required = false) List<String> genres, Model model) {
        List<String> selectedGenres = normalizeSelectedGenres(genres);
        model.addAttribute("boards", BoardType.values());
        model.addAttribute("dancers", communityService.findDancers(selectedGenres));
        model.addAttribute("dancerGenres", communityService.findDancerGenres());
        model.addAttribute("selectedGenres", selectedGenres);
        model.addAttribute("genreLinks", buildDancerGenreLinks(selectedGenres));
        return "dancers";
    }

    @GetMapping("/dancers/{id}")
    public String dancerDetail(@PathVariable Long id, Principal principal, Model model) {
        try {
            communityService.findDancer(id);
            String viewerUsername = principal == null ? null : principal.getName();
            var myPage = myPageService.findProfilePage(id, viewerUsername);
            model.addAttribute("boards", BoardType.values());
            model.addAttribute("myPage", myPage);
            model.addAttribute("isOwner", myPage.user().getUsername().equals(viewerUsername));
            return "my-page";
        } catch (AccessDeniedException exception) {
            return "redirect:/dancers";
        }
    }

    private String resolveBackUrl(HttpServletRequest request) {
        String fallback = "/dashboard";
        String referer = request.getHeader("Referer");
        if (referer == null || referer.isBlank()) {
            return fallback;
        }

        try {
            URI refererUri = URI.create(referer);
            String refererHost = refererUri.getHost();
            if (refererHost != null && !refererHost.equalsIgnoreCase(request.getServerName())) {
                return fallback;
            }

            String path = refererUri.getPath();
            if (path == null || path.isBlank() || path.startsWith("/login") || path.startsWith("/posts/")) {
                return fallback;
            }

            String query = refererUri.getQuery();
            return query == null || query.isBlank() ? path : path + "?" + query;
        } catch (IllegalArgumentException exception) {
            return fallback;
        }
    }

    private List<String> normalizeSelectedGenres(List<String> genres) {
        if (genres == null || genres.isEmpty()) {
            return List.of();
        }

        return genres.stream()
                .filter(genre -> genre != null && !genre.isBlank())
                .map(String::trim)
                .filter(communityService.findDancerGenres()::contains)
                .distinct()
                .toList();
    }

    private Map<String, String> buildDancerGenreLinks(List<String> selectedGenres) {
        Map<String, String> links = new LinkedHashMap<>();
        for (String genre : communityService.findDancerGenres()) {
            List<String> nextGenres = new ArrayList<>(selectedGenres);
            if (nextGenres.contains(genre)) {
                nextGenres.remove(genre);
            } else {
                nextGenres.add(genre);
            }

            UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/dancers");
            nextGenres.forEach(selectedGenre -> builder.queryParam("genres", selectedGenre));
            links.put(genre, builder.build().encode().toUriString());
        }
        return links;
    }
}
