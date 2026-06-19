package polytech.aisw.eom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import polytech.aisw.eom.domain.BoardType;
import polytech.aisw.eom.service.DashboardService;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(defaultValue = "SHOW") BoardType board, Model model) {
        model.addAttribute("boards", BoardType.values());
        model.addAttribute("selectedBoard", board);
        model.addAttribute("todayPick", dashboardService.findTodayPick());
        model.addAttribute("showRecentPosts", dashboardService.findRecentPostsByBoard(BoardType.SHOW));
        model.addAttribute("castRecentPosts", dashboardService.findRecentPostsByBoard(BoardType.CAST));
        model.addAttribute("hypeRecentPosts", dashboardService.findRecentPostsByBoard(BoardType.HYPE));
        model.addAttribute("linkRecentPosts", dashboardService.findRecentPostsByBoard(BoardType.LINK));
        model.addAttribute("recentPosts", dashboardService.findRecentPostsByBoard(board));
        model.addAttribute("popularPosts", dashboardService.findPopularPosts());
        model.addAttribute("upcomingEvents", dashboardService.findUpcomingEvents());
        model.addAttribute("recommendedDancers", dashboardService.findRecommendedDancers());
        model.addAttribute("featuredMediaPosts", dashboardService.findFeaturedMediaPosts());
        model.addAttribute("tags", dashboardService.findTags());
        return "dashboard";
    }
}
