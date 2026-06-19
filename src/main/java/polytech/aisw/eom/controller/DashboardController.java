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
        model.addAttribute("recentPosts", dashboardService.findRecentPosts());
        return "dashboard";
    }
}

