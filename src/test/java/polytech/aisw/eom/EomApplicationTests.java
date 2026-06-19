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
}
