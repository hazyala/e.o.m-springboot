package polytech.aisw.eom;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import polytech.aisw.eom.domain.AppUser;
import polytech.aisw.eom.domain.BoardType;
import polytech.aisw.eom.domain.Post;
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
    private final UserRepository userRepository;

    @Autowired
    EomApplicationTests(MockMvc mockMvc, PostRepository postRepository, UserRepository userRepository) {
        this.mockMvc = mockMvc;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Test
    void publicPagesLoad() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
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
                .andExpect(content().string(org.hamcrest.Matchers.containsString("홍대 쇼케이스 백업댄서 모집")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("LINK에 새 글")))
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
                .andExpect(content().string(containsString("DELETE")));
    }

    @Test
    void authorCanDeleteOwnPost() throws Exception {
        Post post = saveTestPost("owner delete target", "dancer1");

        mockMvc.perform(post("/posts/{id}/delete", post.getId())
                        .with(user("dancer1").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/all"));

        org.assertj.core.api.Assertions.assertThat(postRepository.findById(post.getId())).isEmpty();
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
    }

    private Post saveTestPost(String title, String authorUsername) {
        AppUser author = userRepository.findByUsername(authorUsername).orElseThrow();
        Post post = new Post(
                BoardType.SHOW,
                title,
                "테스트 게시글 본문입니다.",
                "",
                "",
                author
        );
        return postRepository.save(post);
    }
}
