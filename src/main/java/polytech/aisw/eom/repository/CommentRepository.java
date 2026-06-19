package polytech.aisw.eom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import polytech.aisw.eom.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}

