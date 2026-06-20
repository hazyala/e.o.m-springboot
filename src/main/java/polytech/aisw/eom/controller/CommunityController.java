package polytech.aisw.eom.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import polytech.aisw.eom.domain.BoardType;
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

    @GetMapping("/posts/{id}")
    public String postDetail(@PathVariable Long id, HttpServletRequest request, Model model) {
        var post = communityService.findPost(id);
        model.addAttribute("post", post);
        model.addAttribute("backUrl", resolveBackUrl(request));
        model.addAttribute("boards", BoardType.values());
        model.addAttribute("postTags", communityService.parseTags(post.getTags()));
        model.addAttribute("popularPosts", communityService.findPopularPosts());
        model.addAttribute("recentPosts", communityService.findRecentPostsByBoard(post.getBoardType()));
        return "post-detail";
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
    public String events(@RequestParam(defaultValue = "latest") String sort, Model model) {
        PostSortOption sortOption = PostSortOption.from(sort);
        model.addAttribute("title", "THIS MONTH EVENTS");
        model.addAttribute("eyebrow", "HYPE EVENTS");
        model.addAttribute("summary", "이번 달 안에 열리는 HYPE 공식 행사와 모집 일정을 모았습니다.");
        model.addAttribute("selectedBoard", BoardType.HYPE);
        model.addAttribute("isEventsPage", true);
        populatePostListModel(model, sortOption);
        var posts = communityService.findThisMonthEvents(sortOption);
        model.addAttribute("posts", posts);
        model.addAttribute("postCount", posts.size());
        model.addAttribute("sortLinks", buildSortLinks("/events", null));
        return "post-list";
    }

    @GetMapping("/boards/{board}")
    public String board(
            @PathVariable String board,
            @RequestParam(defaultValue = "latest") String sort,
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
            model.addAttribute("sortLinks", buildSortLinks("/boards/all", null));
            return "post-list";
        }

        BoardType boardType = BoardType.valueOf(board.toUpperCase());
        model.addAttribute("title", boardType.getLabel());
        model.addAttribute("eyebrow", "BOARD");
        model.addAttribute("summary", boardType.getDescription());
        model.addAttribute("selectedBoard", boardType);
        populatePostListModel(model, sortOption);
        var posts = communityService.findPostsByBoard(boardType, sortOption);
        model.addAttribute("posts", posts);
        model.addAttribute("postCount", posts.size());
        model.addAttribute("sortLinks", buildSortLinks("/boards/" + boardType.name(), null));
        return "post-list";
    }

    private void populatePostListModel(Model model, PostSortOption sortOption) {
        model.addAttribute("boards", BoardType.values());
        model.addAttribute("tags", communityService.findTags());
        model.addAttribute("sortOptions", PostSortOption.values());
        model.addAttribute("selectedSort", sortOption);
    }

    private Map<PostSortOption, String> buildSortLinks(String basePath, String tag) {
        Map<PostSortOption, String> links = new LinkedHashMap<>();
        for (PostSortOption option : PostSortOption.values()) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromPath(basePath);
            if (tag != null && !tag.isBlank()) {
                builder.queryParam("tag", tag);
            }
            builder.queryParam("sort", option.getKey());
            links.put(option, builder.build().toUriString());
        }
        return links;
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
        var myPage = myPageService.findProfilePage(id);
        model.addAttribute("boards", BoardType.values());
        model.addAttribute("myPage", myPage);
        model.addAttribute("isOwner", principal != null && myPage.user().getUsername().equals(principal.getName()));
        return "my-page";
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
