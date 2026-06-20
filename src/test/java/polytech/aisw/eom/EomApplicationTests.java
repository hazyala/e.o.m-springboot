package polytech.aisw.eom;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EomApplicationTests {

    private final MockMvc mockMvc;

    @Autowired
    EomApplicationTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
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
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/dashboard?board=HYPE")));

        mockMvc.perform(get("/activity").with(user("dancer1").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/all?sort=latest"));

        mockMvc.perform(get("/boards/all").with(user("dancer1").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("ALL")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("릴스 기반 코레오 쇼케이스")));

        mockMvc.perform(get("/events").with(user("dancer1").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("THIS MONTH EVENTS")));

        mockMvc.perform(get("/dancers").with(user("dancer1").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Street Genre Directory")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("GENRE FILTER")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("전체보기")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/dancers?genres=")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Instagram")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("프로필 보기")));
    }

    @Test
    void dancerDirectorySupportsMultiGenreFiltersAndProfileLinks() throws Exception {
        mockMvc.perform(get("/dancers")
                        .param("genres", "힙합")
                        .param("genres", "크럼프")
                        .with(user("dancer1").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("SHADOW_98")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("RAYA")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/dancers/")));

        mockMvc.perform(get("/dancers")
                        .param("genres", "보깅")
                        .with(user("dancer1").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("조건에 맞는 댄서가 없습니다.")));
    }
}
