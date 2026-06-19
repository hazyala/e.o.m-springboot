package polytech.aisw.eom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import polytech.aisw.eom.service.AdminService;

@Controller
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("users", adminService.findUsers());
        model.addAttribute("posts", adminService.findPosts());
        return "admin";
    }
}

