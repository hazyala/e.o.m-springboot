package polytech.aisw.eom.service;

import java.util.List;
import org.springframework.stereotype.Service;
import polytech.aisw.eom.domain.BoardType;
import polytech.aisw.eom.domain.Post;
import polytech.aisw.eom.repository.PostRepository;

@Service
public class DashboardService {

    private final PostRepository postRepository;

    public DashboardService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> findRecentPosts() {
        return postRepository.findTop6ByOrderByCreatedAtDesc();
    }

    public List<Post> findRecentPostsByBoard(BoardType boardType) {
        return postRepository.findTop6ByBoardTypeOrderByCreatedAtDesc(boardType);
    }
}

