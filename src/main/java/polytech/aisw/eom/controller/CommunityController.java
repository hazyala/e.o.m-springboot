package polytech.aisw.eom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import polytech.aisw.eom.service.CommunityService;

@Controller
public class CommunityController {

    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @GetMapping("/posts/{id}")
    public String postDetail(@PathVariable Long id, Model model) {
        model.addAttribute("post", communityService.findPost(id));
        return "post-detail";
    }

    @GetMapping("/posts")
    public String posts(@RequestParam(required = false) String tag, Model model) {
        model.addAttribute("title", tag == null || tag.isBlank() ? "LATEST POSTS" : "# " + tag);
        model.addAttribute("posts", tag == null || tag.isBlank()
                ? communityService.findLatestPosts()
                : communityService.findPostsByTag(tag));
        return "post-list";
    }

    @GetMapping("/activity")
    public String activity(Model model) {
        model.addAttribute("title", "LATEST ACTIVITY");
        model.addAttribute("posts", communityService.findLatestPosts());
        return "post-list";
    }

    @GetMapping("/events")
    public String events(Model model) {
        model.addAttribute("title", "THIS MONTH EVENTS");
        model.addAttribute("posts", communityService.findThisMonthEvents());
        return "post-list";
    }

    @GetMapping("/dancers")
    public String dancers(Model model) {
        model.addAttribute("dancers", communityService.findDancers());
        return "dancers";
    }
}
