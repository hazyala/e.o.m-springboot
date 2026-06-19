package polytech.aisw.eom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import polytech.aisw.eom.domain.BoardType;
import polytech.aisw.eom.service.DashboardService;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("boards", BoardType.values());
        model.addAttribute("todayPick", dashboardService.findTodayPick());
        model.addAttribute("recentPosts", dashboardService.findRecentPosts());
        model.addAttribute("popularPosts", dashboardService.findPopularPosts());
        model.addAttribute("upcomingEvents", dashboardService.findUpcomingEvents());
        model.addAttribute("recommendedDancers", dashboardService.findRecommendedDancers());
        model.addAttribute("featuredMediaPosts", dashboardService.findFeaturedMediaPosts());
        model.addAttribute("tags", dashboardService.findTags());
        return "dashboard";
    }
}
