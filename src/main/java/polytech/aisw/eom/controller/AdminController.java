package polytech.aisw.eom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
        model.addAttribute("reportedPosts", adminService.findReportedPosts());
        return "admin";
    }

    @PostMapping("/admin/users/{id}/block")
    public String setUserBlocked(
            @PathVariable Long id,
            @RequestParam boolean blocked,
            RedirectAttributes redirectAttributes
    ) {
        try {
            adminService.setUserBlocked(id, blocked);
            redirectAttributes.addFlashAttribute("adminNotice", blocked ? "사용자를 차단했습니다." : "사용자 차단을 해제했습니다.");
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("adminError", exception.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/admin/posts/{id}/visibility")
    public String setPostHidden(
            @PathVariable Long id,
            @RequestParam boolean hidden,
            RedirectAttributes redirectAttributes
    ) {
        adminService.setPostHidden(id, hidden);
        redirectAttributes.addFlashAttribute("adminNotice", hidden ? "게시글을 숨김 처리했습니다." : "게시글을 복구했습니다.");
        return "redirect:/admin";
    }

    @PostMapping("/admin/posts/{id}/hype-approval")
    public String setHypeEventApproved(
            @PathVariable Long id,
            @RequestParam boolean approved,
            RedirectAttributes redirectAttributes
    ) {
        try {
            adminService.setHypeEventApproved(id, approved);
            redirectAttributes.addFlashAttribute("adminNotice", approved ? "HYPE 행사를 승인했습니다." : "HYPE 행사 승인을 취소했습니다.");
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("adminError", exception.getMessage());
        }
        return "redirect:/admin";
    }
}
