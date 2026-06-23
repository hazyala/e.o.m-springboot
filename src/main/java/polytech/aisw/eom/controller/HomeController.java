package polytech.aisw.eom.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import polytech.aisw.eom.domain.Post;
import polytech.aisw.eom.repository.PostRepository;

@Controller
public class HomeController {

    private final PostRepository postRepository;

    public HomeController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Post> latestPosts = visiblePosts(postRepository.findTop12ByOrderByCreatedAtDesc());
        model.addAttribute("indexPosts", latestPosts);
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    private List<Post> visiblePosts(List<Post> posts) {
        return posts.stream()
                .filter(Post::isVisibleInCommunity)
                .toList();
    }
}

