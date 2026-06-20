package polytech.aisw.eom.controller;

import java.security.Principal;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import polytech.aisw.eom.domain.BoardType;
import polytech.aisw.eom.service.MyPageService;
import polytech.aisw.eom.service.MyPageService.ProfileUpdateRequest;

@Controller
public class MyPageController {

    private final MyPageService myPageService;

    public MyPageController(MyPageService myPageService) {
        this.myPageService = myPageService;
    }

    @GetMapping({"/my-page", "/me"})
    public String myPage(Principal principal, Model model) {
        model.addAttribute("boards", BoardType.values());
        model.addAttribute("myPage", myPageService.findMyPage(principal.getName()));
        return "my-page";
    }

    @PostMapping("/my-page/profile")
    public String updateProfile(
            Principal principal,
            @RequestParam String displayName,
            @RequestParam(required = false) String crewName,
            @RequestParam(required = false) String primaryGenre,
            @RequestParam(required = false) String bio,
            @RequestParam(required = false) String instagramUrl,
            @RequestParam(required = false) String profileImageUrl,
            @RequestParam(required = false) String headerImageUrl,
            RedirectAttributes redirectAttributes
    ) {
        myPageService.updateProfile(principal.getName(), new ProfileUpdateRequest(
                displayName,
                crewName,
                primaryGenre,
                bio,
                instagramUrl,
                profileImageUrl,
                headerImageUrl
        ));
        redirectAttributes.addFlashAttribute("profileMessage", "프로필이 저장되었습니다.");
        return "redirect:/my-page";
    }

    @PostMapping("/my-page/portfolio/select")
    public String updatePortfolioSelection(
            Principal principal,
            @RequestParam Long postId,
            @RequestParam boolean selected
    ) {
        myPageService.updatePortfolioSelection(principal.getName(), postId, selected);
        return "redirect:/my-page#posts";
    }

    @PostMapping("/my-page/portfolio/pin")
    public String updatePortfolioPin(
            Principal principal,
            @RequestParam Long postId,
            @RequestParam boolean pinned,
            RedirectAttributes redirectAttributes
    ) {
        try {
            myPageService.updatePortfolioPin(principal.getName(), postId, pinned);
        } catch (IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("portfolioMessage", "상단 고정은 최대 3개까지 가능합니다.");
        }
        return "redirect:/my-page#portfolio";
    }

    @PostMapping("/my-page/joined-events")
    public String addJoinedEvent(
            Principal principal,
            @RequestParam LocalDate eventDate,
            @RequestParam String eventName,
            @RequestParam String result
    ) {
        myPageService.addJoinedEvent(principal.getName(), eventDate, eventName, result);
        return "redirect:/my-page#joined-events";
    }
}
