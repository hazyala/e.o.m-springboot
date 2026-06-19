package polytech.aisw.eom.service;

import java.util.List;
import org.springframework.stereotype.Service;
import polytech.aisw.eom.domain.AppUser;
import polytech.aisw.eom.domain.Post;
import polytech.aisw.eom.repository.PostRepository;
import polytech.aisw.eom.repository.UserRepository;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public AdminService(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public List<AppUser> findUsers() {
        return userRepository.findAll();
    }

    public List<Post> findPosts() {
        return postRepository.findAll();
    }
}

