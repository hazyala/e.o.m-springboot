package polytech.aisw.eom;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import polytech.aisw.eom.domain.AppUser;
import polytech.aisw.eom.domain.BoardType;
import polytech.aisw.eom.domain.Comment;
import polytech.aisw.eom.domain.Post;
import polytech.aisw.eom.repository.CommentRepository;
import polytech.aisw.eom.repository.PostRepository;
import polytech.aisw.eom.repository.UserRepository;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EomApplicationTests {

    private final MockMvc mockMvc;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    EomApplicationTests(
            MockMvc mockMvc,
            PostRepository postRepository,
            CommentRepository commentRepository,
            UserRepository userRepository,
            JdbcTemplate jdbcTemplate
    ) {
        this.mockMvc = mockMvc;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    void publicPagesLoad() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-signup-action=\"/signup\"")));
    }

    @Test
    void signupCreatesUserAndAllowsLogin() throws Exception {
        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .param("displayName", "New Dancer")
                        .param("username", "new.dancer")
                        .param("password", "1234")
                        .param("passwordConfirm", "1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(formLogin().user("new.dancer").password("1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void protectedPagesRedirectAnonymousUser() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void seededAdminCanLoginAndOpenAdminPage() throws Exception {
        mockMvc.perform(formLogin().user("admin").password("admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));

        mockMvc.perform(get("/admin").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void seededDancerCanLoginAndSeeDashboardData() throws Exception {
        mockMvc.perform(formLogin().user("dancer1").password("1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));

        mockMvc.perform(get("/dashboard").with(user("dancer1").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Featured Media")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("릴스 기반 코레오 쇼케이스")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("https://www.instagram.com/reel/C5frLClST0B/")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("SHADOW_98")));
    }

    @Test
    void dashboardBoardTabsAndLinkedPagesRender() throws Exception {
        mockMvc.perform(get("/dashboard?board=CAST").with(user("dancer1").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("data-board=\"CAST\" class=\" is-active\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("type-cast")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("SHOW에 새 글")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/dashboard?board=HYPE")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/boards/HYPE?officialEvents=true")));

        mockMvc.perform(get("/activity").with(user("dancer1").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/all?sort=latest"));

        mockMvc.perform(get("/boards/all").with(user("dancer1").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("ALL")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("릴스 기반 코레오 쇼케이스")));

        mockMvc.perform(get("/events").with(user("dancer1").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/HYPE?officialEvents=true"));

        mockMvc.perform(get("/boards/HYPE")
                        .param("officialEvents", "true")
                        .param("sort", "views")
                        .with(user("dancer1").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("관리자 승인 행사")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Seoul Street Jam 참가자 모집")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("officialEvents=true")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("class=\" is-active\">관리자 승인 행사")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"/boards/HYPE?sort=views\"")))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("class=\" is-active\">최신순"))))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("officialEvents=true&amp;sort=views"))));

        mockMvc.perform(get("/dancers").with(user("dancer1").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Street Genre Directory")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Dancers")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("All")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/dancers?genres=")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Instagram")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("visit profile")));
    }

    @Test
    void dancerDirectorySupportsMultiGenreFiltersAndProfileLinks() throws Exception {
        mockMvc.perform(get("/dancers")
                        .param("genres", "Hip-hop")
                        .param("genres", "Krump")
                        .with(user("dancer1").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("SHADOW_98")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("RAYA")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/dancers/")));

        mockMvc.perform(get("/dancers")
                        .param("genres", "Voguing")
                        .with(user("dancer1").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("No dancers match these filters.")));
    }

    @Test
    void authorCanEditOwnPost() throws Exception {
        Post post = saveTestPost("owner edit target", "dancer1");

        mockMvc.perform(get("/posts/{id}/edit", post.getId()).with(user("dancer1").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("UPDATE")));

        mockMvc.perform(post("/posts/{id}/edit", post.getId())
                        .with(user("dancer1").roles("USER"))
                        .with(csrf())
                        .param("boardType", "SHOW")
                        .param("title", "owner edited title")
                        .param("content", "owner edited content")
                        .param("tags", "edit,owner")
                        .param("location", "서울")
                        .param("mediaUrl", "")
                        .param("thumbnailUrl", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post.getId()));

        mockMvc.perform(get("/posts/{id}", post.getId()).with(user("dancer1").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("owner edited title")))
                .andExpect(content().string(containsString("EDIT")))
                .andExpect(content().string(containsString("DELETE")))
                .andExpect(content().string(containsString("data-more-actions-toggle")))
                .andExpect(content().string(containsString("링크 복사")))
                .andExpect(content().string(containsString("게시판으로 이동")));
    }

    @Test
    void authorCanDeleteOwnPost() throws Exception {
        Post post = saveTestPost("owner delete target", "dancer1");
        attachCommentAndLike(post, "mina.flow");

        mockMvc.perform(post("/posts/{id}/delete", post.getId())
                        .with(user("dancer1").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/all"));

        org.assertj.core.api.Assertions.assertThat(postRepository.findById(post.getId())).isEmpty();
        assertNoChildEngagement(post.getId());
    }

    @Test
    void loggedInUserCanCreateCommentAndDetailShowsCommentList() throws Exception {
        Post post = saveTestPost("comment create target", "dancer1");

        mockMvc.perform(post("/posts/{id}/comments", post.getId())
                        .with(user("mina.flow").roles("USER"))
                        .with(csrf())
                        .param("content", "첫 댓글입니다."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post.getId()));

        Post commentedPost = postRepository.findById(post.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(commentedPost.getCommentCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(commentRepository.findByPostIdOrderByCreatedAtAsc(post.getId()))
                .extracting(Comment::getContent)
                .containsExactly("첫 댓글입니다.");

        mockMvc.perform(get("/posts/{id}", post.getId()).with(user("dancer1").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("첫 댓글입니다.")))
                .andExpect(content().string(containsString("href=\"/dancers/")))
                .andExpect(content().string(containsString("COMMENTS <b>1</b>")));
    }

    @Test
    void emptyOrTooLongCommentIsRejected() throws Exception {
        Post post = saveTestPost("comment validation target", "dancer1");

        mockMvc.perform(post("/posts/{id}/comments", post.getId())
                        .with(user("dancer1").roles("USER"))
                        .with(csrf())
                        .param("content", "   "))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post.getId()));

        mockMvc.perform(post("/posts/{id}/comments", post.getId())
                        .with(user("dancer1").roles("USER"))
                        .with(csrf())
                        .param("content", "a".repeat(501)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post.getId()));

        Post unchangedPost = postRepository.findById(post.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(unchangedPost.getCommentCount()).isZero();
        org.assertj.core.api.Assertions.assertThat(commentRepository.findByPostIdOrderByCreatedAtAsc(post.getId())).isEmpty();
    }

    @Test
    void commentAuthorCanDeleteOwnCommentAndCountDecreases() throws Exception {
        Post post = saveTestPost("comment delete target", "dancer1");
        createComment(post, "mina.flow", "삭제할 댓글입니다.");
        Long commentId = commentRepository.findByPostIdOrderByCreatedAtAsc(post.getId()).get(0).getId();

        mockMvc.perform(post("/posts/{postId}/comments/{commentId}/delete", post.getId(), commentId)
                        .with(user("mina.flow").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post.getId()));

        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(updatedPost.getCommentCount()).isZero();
        org.assertj.core.api.Assertions.assertThat(commentRepository.findByPostIdOrderByCreatedAtAsc(post.getId())).isEmpty();
    }

    @Test
    void otherUserAndAdminCannotDeleteSomeoneElsesComment() throws Exception {
        Post post = saveTestPost("comment delete blocked target", "dancer1");
        createComment(post, "dancer1", "남이 지울 수 없는 댓글입니다.");
        Long commentId = commentRepository.findByPostIdOrderByCreatedAtAsc(post.getId()).get(0).getId();

        mockMvc.perform(post("/posts/{postId}/comments/{commentId}/delete", post.getId(), commentId)
                        .with(user("mina.flow").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post.getId()));

        mockMvc.perform(post("/posts/{postId}/comments/{commentId}/delete", post.getId(), commentId)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post.getId()));

        Post unchangedPost = postRepository.findById(post.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(unchangedPost.getCommentCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(commentRepository.findByPostIdOrderByCreatedAtAsc(post.getId())).hasSize(1);
    }

    @Test
    void otherUserCannotEditOrDeletePost() throws Exception {
        Post post = saveTestPost("other blocked target", "dancer1");

        mockMvc.perform(get("/posts/{id}", post.getId()).with(user("mina.flow").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("EDIT"))))
                .andExpect(content().string(not(containsString("DELETE"))));

        mockMvc.perform(get("/posts/{id}/edit", post.getId()).with(user("mina.flow").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post.getId()));

        mockMvc.perform(post("/posts/{id}/edit", post.getId())
                        .with(user("mina.flow").roles("USER"))
                        .with(csrf())
                        .param("boardType", "SHOW")
                        .param("title", "blocked edit title")
                        .param("content", "blocked edit content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post.getId()));

        mockMvc.perform(post("/posts/{id}/delete", post.getId())
                        .with(user("mina.flow").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post.getId()));

        Post unchangedPost = postRepository.findById(post.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(unchangedPost.getTitle()).isEqualTo("other blocked target");
    }

    @Test
    void adminCanDeleteButCannotEditOtherUsersPost() throws Exception {
        Post post = saveTestPost("admin policy target", "dancer1");
        attachCommentAndLike(post, "mina.flow");

        mockMvc.perform(get("/posts/{id}", post.getId()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("EDIT"))))
                .andExpect(content().string(containsString("DELETE")));

        mockMvc.perform(post("/posts/{id}/edit", post.getId())
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("boardType", "SHOW")
                        .param("title", "admin blocked edit title")
                        .param("content", "admin blocked edit content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post.getId()));

        org.assertj.core.api.Assertions.assertThat(postRepository.findById(post.getId()).orElseThrow().getTitle())
                .isEqualTo("admin policy target");

        mockMvc.perform(post("/posts/{id}/delete", post.getId())
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/all"));

        org.assertj.core.api.Assertions.assertThat(postRepository.findById(post.getId())).isEmpty();
        assertNoChildEngagement(post.getId());
    }

    @Test
    void loggedInUserCanToggleLikeAndCountChangesWithoutDuplicates() throws Exception {
        Post post = saveTestPost("reaction like target", "dancer1");

        mockMvc.perform(post("/posts/{id}/like", post.getId())
                        .with(user("mina.flow").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post.getId()));

        Post likedPost = postRepository.findById(post.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(likedPost.getLikeCount()).isEqualTo(1);
        assertTableCount("post_likes", post.getId(), 1);

        mockMvc.perform(get("/posts/{id}", post.getId()).with(user("mina.flow").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("♥")))
                .andExpect(content().string(containsString("reaction like target")));

        mockMvc.perform(post("/posts/{id}/like", post.getId())
                        .with(user("mina.flow").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post.getId()));

        Post unlikedPost = postRepository.findById(post.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(unlikedPost.getLikeCount()).isZero();
        assertTableCount("post_likes", post.getId(), 0);
    }

    @Test
    void likeCancelDoesNotMakeCountNegative() throws Exception {
        Post post = saveTestPost("reaction negative guard target", "dancer1");
        AppUser user = userRepository.findByUsername("mina.flow").orElseThrow();
        jdbcTemplate.update(
                "insert into post_likes (post_id, user_id, created_at) values (?, ?, current_timestamp)",
                post.getId(),
                user.getId()
        );

        mockMvc.perform(post("/posts/{id}/like", post.getId())
                        .with(user("mina.flow").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post.getId()));

        Post unlikedPost = postRepository.findById(post.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(unlikedPost.getLikeCount()).isZero();
        assertTableCount("post_likes", post.getId(), 0);
    }

    @Test
    void loggedInUserCanToggleSaveAndMyPageReflectsIt() throws Exception {
        Post post = saveTestPost("reaction save target", "dancer1");

        mockMvc.perform(post("/posts/{id}/save", post.getId())
                        .with(user("mina.flow").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post.getId()));

        assertTableCount("post_saves", post.getId(), 1);
        mockMvc.perform(get("/posts/{id}", post.getId()).with(user("mina.flow").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("저장됨")));

        mockMvc.perform(get("/my-page").with(user("mina.flow").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Saves")))
                .andExpect(content().string(containsString("reaction save target")));

        mockMvc.perform(post("/posts/{id}/save", post.getId())
                        .with(user("mina.flow").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post.getId()));

        assertTableCount("post_saves", post.getId(), 0);
        mockMvc.perform(get("/my-page").with(user("mina.flow").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("reaction save target"))));
    }

    @Test
    void savedPostsAreHiddenOnOtherUsersPublicProfile() throws Exception {
        Post post = saveTestPost("private saved bookmark target", "dancer1");
        Long minaId = userRepository.findByUsername("mina.flow").orElseThrow().getId();

        mockMvc.perform(post("/posts/{id}/save", post.getId())
                        .with(user("mina.flow").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post.getId()));

        mockMvc.perform(get("/dancers/{id}", minaId).with(user("dancer1").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Saves"))))
                .andExpect(content().string(not(containsString("private saved bookmark target"))));

        mockMvc.perform(get("/dancers/{id}", minaId).with(user("mina.flow").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Saves")))
                .andExpect(content().string(containsString("private saved bookmark target")));
    }

    @Test
    void myPageShowsOwnerTabsAndPostCardMeta() throws Exception {
        Post post = saveTestPost("owner posts tab meta target", "mina.flow");
        createComment(post, "dancer1", "메타 댓글입니다.");

        mockMvc.perform(get("/my-page").with(user("mina.flow").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Portfolio")))
                .andExpect(content().string(containsString("Posts")))
                .andExpect(content().string(containsString("Events")))
                .andExpect(content().string(containsString("Likes")))
                .andExpect(content().string(containsString("Saves")))
                .andExpect(content().string(containsString("Comments")))
                .andExpect(content().string(containsString("owner posts tab meta target")))
                .andExpect(content().string(containsString("SHOW")))
                .andExpect(content().string(containsString("댓글 1")))
                .andExpect(content().string(containsString("좋아요 0")))
                .andExpect(content().string(containsString("/posts/" + post.getId())));
    }

    @Test
    void otherUsersPublicProfileHidesPrivateTabsAndOwnerActions() throws Exception {
        Post post = saveTestPost("public profile visible post target", "mina.flow");
        Long minaId = userRepository.findByUsername("mina.flow").orElseThrow().getId();

        mockMvc.perform(post("/posts/{id}/like", post.getId())
                        .with(user("mina.flow").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post.getId()));

        mockMvc.perform(post("/posts/{id}/save", post.getId())
                        .with(user("mina.flow").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post.getId()));

        mockMvc.perform(get("/dancers/{id}", minaId).with(user("dancer1").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Portfolio")))
                .andExpect(content().string(containsString("Posts")))
                .andExpect(content().string(containsString("Events")))
                .andExpect(content().string(containsString("Comments")))
                .andExpect(content().string(containsString("public profile visible post target")))
                .andExpect(content().string(containsString("/posts/" + post.getId())))
                .andExpect(content().string(not(containsString(">Likes<"))))
                .andExpect(content().string(not(containsString(">Saves<"))))
                .andExpect(content().string(not(containsString("프로필 편집"))))
                .andExpect(content().string(not(containsString("회원정보 수정"))))
                .andExpect(content().string(not(containsString("포트폴리오에 추가"))))
                .andExpect(content().string(not(containsString("대표 고정"))))
                .andExpect(content().string(not(containsString("Add</button>"))));
    }

    @Test
    void myPageLikesReflectPostLikeRows() throws Exception {
        Post post = saveTestPost("reaction my likes target", "dancer1");

        mockMvc.perform(post("/posts/{id}/like", post.getId())
                        .with(user("mina.flow").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post.getId()));

        mockMvc.perform(get("/my-page").with(user("mina.flow").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Likes")))
                .andExpect(content().string(containsString("reaction my likes target")));

        mockMvc.perform(post("/posts/{id}/like", post.getId())
                        .with(user("mina.flow").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post.getId()));

        mockMvc.perform(get("/my-page").with(user("mina.flow").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("reaction my likes target"))));
    }

    @Test
    void portfolioSelectionAndPinningOnlyAllowShowPosts() throws Exception {
        Post showPost = saveTestPost("show portfolio candidate target", "mina.flow", BoardType.SHOW);
        Post castPost = saveTestPost("cast portfolio blocked target", "mina.flow", BoardType.CAST);

        mockMvc.perform(post("/my-page/portfolio/select")
                        .with(user("mina.flow").roles("USER"))
                        .with(csrf())
                        .param("postId", castPost.getId().toString())
                        .param("selected", "true")
                        .param("returnTab", "posts"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-page#posts"));

        mockMvc.perform(post("/my-page/portfolio/pin")
                        .with(user("mina.flow").roles("USER"))
                        .with(csrf())
                        .param("postId", castPost.getId().toString())
                        .param("pinned", "true")
                        .param("returnTab", "posts"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-page#posts"));

        Post unchangedCastPost = postRepository.findById(castPost.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(unchangedCastPost.isPortfolioSelected()).isFalse();
        org.assertj.core.api.Assertions.assertThat(unchangedCastPost.isPortfolioPinned()).isFalse();

        mockMvc.perform(post("/my-page/portfolio/pin")
                        .with(user("mina.flow").roles("USER"))
                        .with(csrf())
                        .param("postId", showPost.getId().toString())
                        .param("pinned", "true")
                        .param("returnTab", "portfolio"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-page#portfolio"));

        Post pinnedShowPost = postRepository.findById(showPost.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(pinnedShowPost.isPortfolioSelected()).isTrue();
        org.assertj.core.api.Assertions.assertThat(pinnedShowPost.isPortfolioPinned()).isTrue();
    }

    @Test
    void portfolioPinLimitStillAllowsOnlyThreePinnedShowPosts() throws Exception {
        Post firstPost = saveTestPost("portfolio pin first target", "mina.flow", BoardType.SHOW);
        Post secondPost = saveTestPost("portfolio pin second target", "mina.flow", BoardType.SHOW);
        Post thirdPost = saveTestPost("portfolio pin third target", "mina.flow", BoardType.SHOW);
        Post fourthPost = saveTestPost("portfolio pin fourth target", "mina.flow", BoardType.SHOW);

        firstPost.setPortfolioPinned(true);
        secondPost.setPortfolioPinned(true);
        thirdPost.setPortfolioPinned(true);
        postRepository.save(firstPost);
        postRepository.save(secondPost);
        postRepository.save(thirdPost);

        mockMvc.perform(post("/my-page/portfolio/pin")
                        .with(user("mina.flow").roles("USER"))
                        .with(csrf())
                        .param("postId", fourthPost.getId().toString())
                        .param("pinned", "true")
                        .param("returnTab", "portfolio"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-page#portfolio"));

        Post unpinnedFourthPost = postRepository.findById(fourthPost.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(unpinnedFourthPost.isPortfolioPinned()).isFalse();
    }

    @Test
    void forgedAdminApprovalParameterDoesNotApproveNonHypePostOnEdit() throws Exception {
        Post post = saveTestPost("admin own show target", "admin");

        mockMvc.perform(post("/posts/{id}/edit", post.getId())
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("boardType", "SHOW")
                        .param("title", "admin own show edited")
                        .param("content", "admin own show edited content")
                        .param("adminApprovedEvent", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post.getId()));

        Post editedPost = postRepository.findById(post.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(editedPost.isAdminApprovedEvent()).isFalse();
    }

    @Test
    void adminApprovalParameterRequiresHypePostWithEventDateOnEdit() throws Exception {
        Post post = saveTestPost("admin own hype target", "admin");

        mockMvc.perform(post("/posts/{id}/edit", post.getId())
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("boardType", "HYPE")
                        .param("title", "admin own hype edited")
                        .param("content", "admin own hype edited content")
                        .param("adminApprovedEvent", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post.getId()));

        Post editedPost = postRepository.findById(post.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(editedPost.isAdminApprovedEvent()).isFalse();
    }

    @Test
    void anonymousUserCannotAccessEditOrDeleteActions() throws Exception {
        Post post = saveTestPost("anonymous blocked target", "dancer1");

        mockMvc.perform(get("/posts/{id}/edit", post.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(post("/posts/{id}/edit", post.getId())
                        .with(csrf())
                        .param("boardType", "SHOW")
                        .param("title", "anonymous edit title")
                        .param("content", "anonymous edit content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(post("/posts/{id}/delete", post.getId()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(post("/posts/{id}/comments", post.getId())
                        .with(csrf())
                        .param("content", "anonymous comment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(post("/posts/{id}/like", post.getId()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(post("/posts/{id}/save", post.getId()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        createComment(post, "dancer1", "anonymous delete blocked comment");
        Long commentId = commentRepository.findByPostIdOrderByCreatedAtAsc(post.getId()).get(0).getId();
        mockMvc.perform(post("/posts/{postId}/comments/{commentId}/delete", post.getId(), commentId).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    private Post saveTestPost(String title, String authorUsername) {
        return saveTestPost(title, authorUsername, BoardType.SHOW);
    }

    private Post saveTestPost(String title, String authorUsername, BoardType boardType) {
        AppUser author = userRepository.findByUsername(authorUsername).orElseThrow();
        Post post = new Post(
                boardType,
                title,
                "테스트 게시글 본문입니다.",
                "",
                "",
                author
        );
        return postRepository.save(post);
    }

    private void attachCommentAndLike(Post post, String username) {
        AppUser user = userRepository.findByUsername(username).orElseThrow();
        jdbcTemplate.update(
                "insert into comments (post_id, author_id, content, created_at) values (?, ?, ?, current_timestamp)",
                post.getId(),
                user.getId(),
                "삭제 정리 검증 댓글"
        );
        jdbcTemplate.update(
                "insert into post_likes (post_id, user_id, created_at) values (?, ?, current_timestamp)",
                post.getId(),
                user.getId()
        );
        jdbcTemplate.update(
                "insert into post_saves (post_id, user_id, created_at) values (?, ?, current_timestamp)",
                post.getId(),
                user.getId()
        );
    }

    private Comment createComment(Post post, String username, String content) {
        AppUser user = userRepository.findByUsername(username).orElseThrow();
        post.increaseCommentCount();
        postRepository.save(post);
        return commentRepository.save(new Comment(post, user, content));
    }

    private void assertNoChildEngagement(Long postId) {
        Integer commentCount = jdbcTemplate.queryForObject(
                "select count(*) from comments where post_id = ?",
                Integer.class,
                postId
        );
        Integer likeCount = jdbcTemplate.queryForObject(
                "select count(*) from post_likes where post_id = ?",
                Integer.class,
                postId
        );
        Integer saveCount = jdbcTemplate.queryForObject(
                "select count(*) from post_saves where post_id = ?",
                Integer.class,
                postId
        );
        org.assertj.core.api.Assertions.assertThat(commentCount).isZero();
        org.assertj.core.api.Assertions.assertThat(likeCount).isZero();
        org.assertj.core.api.Assertions.assertThat(saveCount).isZero();
    }

    private void assertTableCount(String tableName, Long postId, int expectedCount) {
        Integer rowCount = jdbcTemplate.queryForObject(
                "select count(*) from " + tableName + " where post_id = ?",
                Integer.class,
                postId
        );
        org.assertj.core.api.Assertions.assertThat(rowCount).isEqualTo(expectedCount);
    }
}
