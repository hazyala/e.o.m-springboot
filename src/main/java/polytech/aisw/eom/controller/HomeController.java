package polytech.aisw.eom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import polytech.aisw.eom.repository.PostRepository;

@Controller
public class HomeController {

    private final PostRepository postRepository;

    public HomeController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("indexPosts", postRepository.findTop12ByHiddenByAdminFalseAndAuthor_BlockedFalseOrderByCreatedAtDesc());
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
