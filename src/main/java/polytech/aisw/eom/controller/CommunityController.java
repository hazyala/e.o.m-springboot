package polytech.aisw.eom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import polytech.aisw.eom.domain.BoardType;
import polytech.aisw.eom.service.CommunityService;

@Controller
public class CommunityController {

    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @GetMapping("/posts/{id}")
    public String postDetail(@PathVariable Long id, Model model) {
        var post = communityService.findPost(id);
        model.addAttribute("post", post);
        model.addAttribute("boards", BoardType.values());
        model.addAttribute("postTags", communityService.parseTags(post.getTags()));
        model.addAttribute("popularPosts", communityService.findPopularPosts());
        model.addAttribute("recentPosts", communityService.findRecentPostsByBoard(post.getBoardType()));
        return "post-detail";
    }

    @GetMapping("/posts")
    public String posts(@RequestParam(required = false) String tag, Model model) {
        boolean hasTag = tag != null && !tag.isBlank();
        model.addAttribute("title", hasTag ? "# " + tag : "LATEST POSTS");
        model.addAttribute("eyebrow", hasTag ? "TAG SEARCH" : "COMMUNITY");
        model.addAttribute("summary", hasTag
                ? "선택한 태그와 연결된 게시글을 최신순으로 모았습니다."
                : "SHOW, CAST, HYPE, LINK의 최신 움직임을 한 화면에서 탐색합니다.");
        model.addAttribute("selectedTag", hasTag ? tag : null);
        model.addAttribute("boards", BoardType.values());
        model.addAttribute("tags", communityService.findTags());
        model.addAttribute("posts", tag == null || tag.isBlank()
                ? communityService.findLatestPosts()
                : communityService.findPostsByTag(tag));
        return "post-list";
    }

    @GetMapping("/activity")
    public String activity(Model model) {
        model.addAttribute("title", "LATEST ACTIVITY");
        model.addAttribute("eyebrow", "ACTIVITY");
        model.addAttribute("summary", "보드 선택과 무관하게 커뮤니티 전체 최신글을 시간순으로 확인합니다.");
        model.addAttribute("boards", BoardType.values());
        model.addAttribute("tags", communityService.findTags());
        model.addAttribute("posts", communityService.findLatestPosts());
        return "post-list";
    }

    @GetMapping("/events")
    public String events(Model model) {
        model.addAttribute("title", "THIS MONTH EVENTS");
        model.addAttribute("eyebrow", "HYPE EVENTS");
        model.addAttribute("summary", "이번 달 안에 열리는 HYPE 공식 행사와 모집 일정을 모았습니다.");
        model.addAttribute("selectedBoard", BoardType.HYPE);
        model.addAttribute("boards", BoardType.values());
        model.addAttribute("tags", communityService.findTags());
        model.addAttribute("posts", communityService.findThisMonthEvents());
        return "post-list";
    }

    @GetMapping("/boards/{board}")
    public String board(@PathVariable String board, Model model) {
        BoardType boardType = BoardType.valueOf(board.toUpperCase());
        model.addAttribute("title", boardType.getLabel());
        model.addAttribute("eyebrow", "BOARD");
        model.addAttribute("summary", boardType.getDescription());
        model.addAttribute("selectedBoard", boardType);
        model.addAttribute("boards", BoardType.values());
        model.addAttribute("tags", communityService.findTags());
        model.addAttribute("posts", communityService.findPostsByBoard(boardType));
        return "post-list";
    }

    @GetMapping("/dancers")
    public String dancers(Model model) {
        model.addAttribute("boards", BoardType.values());
        model.addAttribute("dancers", communityService.findDancers());
        return "dancers";
    }

    @GetMapping("/dancers/{id}")
    public String dancerDetail(@PathVariable Long id, Model model) {
        model.addAttribute("boards", BoardType.values());
        model.addAttribute("dancer", communityService.findDancer(id));
        return "dancer-detail";
    }
}
