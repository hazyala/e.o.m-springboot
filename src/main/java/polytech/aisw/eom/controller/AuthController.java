package polytech.aisw.eom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import polytech.aisw.eom.service.AuthService;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public String signup(
            @RequestParam String displayName,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String passwordConfirm,
            RedirectAttributes redirectAttributes
    ) {
        try {
            authService.signup(displayName, username, password, passwordConfirm);
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("signupMessage", exception.getMessage());
            return "redirect:/login?signup";
        }

        redirectAttributes.addFlashAttribute("loginMessage", "회원가입이 완료되었습니다. 새 계정으로 로그인해주세요.");
        return "redirect:/login";
    }
}
