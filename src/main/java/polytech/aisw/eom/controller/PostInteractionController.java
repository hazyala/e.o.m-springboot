package polytech.aisw.eom.controller;

import jakarta.validation.Valid;
import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import polytech.aisw.eom.domain.Comment;
import polytech.aisw.eom.dto.CommentCreateRequest;
import polytech.aisw.eom.service.CommunityService;

@RestController
@RequestMapping("/posts")
public class PostInteractionController {

    private static final String AJAX_HEADER = "X-Requested-With=XMLHttpRequest";
    private static final DateTimeFormatter COMMENT_DATE = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    private final CommunityService communityService;

    public PostInteractionController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @PostMapping(value = "/{id}/like", headers = AJAX_HEADER)
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Long id, Principal principal) {
        try {
            boolean liked = communityService.togglePostLike(id, principal.getName());
            int count = communityService.findPostForViewer(id, principal.getName()).getLikeCount();
            return ResponseEntity.ok(Map.of("active", liked, "count", count,
                    "message", liked ? "좋아요를 눌렀습니다." : "좋아요를 취소했습니다."));
        } catch (AccessDeniedException exception) {
            return error(HttpStatus.FORBIDDEN, exception.getMessage());
        } catch (RuntimeException exception) {
            return error(HttpStatus.BAD_REQUEST, "좋아요를 처리할 수 없습니다.");
        }
    }

    @PostMapping(value = "/{id}/save", headers = AJAX_HEADER)
    public ResponseEntity<Map<String, Object>> toggleSave(@PathVariable Long id, Principal principal) {
        try {
            boolean saved = communityService.togglePostSave(id, principal.getName());
            return ResponseEntity.ok(Map.of("active", saved,
                    "message", saved ? "게시글을 저장했습니다." : "저장을 취소했습니다."));
        } catch (AccessDeniedException exception) {
            return error(HttpStatus.FORBIDDEN, exception.getMessage());
        } catch (RuntimeException exception) {
            return error(HttpStatus.BAD_REQUEST, "저장을 처리할 수 없습니다.");
        }
    }

    @PostMapping(value = "/{id}/comments", headers = AJAX_HEADER)
    public ResponseEntity<Map<String, Object>> createComment(
            @PathVariable Long id,
            @Valid @ModelAttribute CommentCreateRequest request,
            BindingResult bindingResult,
            Principal principal
    ) {
        if (bindingResult.hasErrors()) {
            return error(HttpStatus.UNPROCESSABLE_ENTITY, "댓글은 1자 이상 500자 이하로 입력해주세요.");
        }
        try {
            Comment comment = communityService.createComment(id, request, principal.getName());
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("id", comment.getId());
            result.put("content", comment.getContent());
            result.put("authorId", comment.getAuthor().getId());
            result.put("authorName", comment.getAuthor().getDisplayName());
            result.put("authorImage", comment.getAuthor().getProfileImageUrl());
            result.put("createdAt", comment.getCreatedAt().format(COMMENT_DATE));
            result.put("count", communityService.findComments(id).size());
            result.put("message", "댓글이 등록되었습니다.");
            return ResponseEntity.ok(result);
        } catch (AccessDeniedException exception) {
            return error(HttpStatus.FORBIDDEN, exception.getMessage());
        } catch (RuntimeException exception) {
            return error(HttpStatus.BAD_REQUEST, "댓글을 등록할 수 없습니다.");
        }
    }

    @PostMapping(value = "/{postId}/comments/{commentId}/delete", headers = AJAX_HEADER)
    public ResponseEntity<Map<String, Object>> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            Principal principal
    ) {
        try {
            communityService.deleteComment(postId, commentId, principal.getName());
            return ResponseEntity.ok(Map.of("count", communityService.findComments(postId).size(),
                    "message", "댓글이 삭제되었습니다."));
        } catch (AccessDeniedException exception) {
            return error(HttpStatus.FORBIDDEN, exception.getMessage());
        } catch (RuntimeException exception) {
            return error(HttpStatus.BAD_REQUEST, "댓글을 삭제할 수 없습니다.");
        }
    }

    @PostMapping(value = "/{id}/report", headers = AJAX_HEADER)
    public ResponseEntity<Map<String, Object>> reportPost(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            Principal principal
    ) {
        try {
            communityService.reportPost(id, reason, principal.getName());
            return ResponseEntity.ok(Map.of("message", "신고가 접수되었습니다. 운영자가 확인합니다."));
        } catch (AccessDeniedException exception) {
            return error(HttpStatus.FORBIDDEN, exception.getMessage());
        } catch (RuntimeException exception) {
            return error(HttpStatus.BAD_REQUEST, "신고를 접수할 수 없습니다.");
        }
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("message", message));
    }
}
