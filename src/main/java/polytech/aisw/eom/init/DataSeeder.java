package polytech.aisw.eom.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import polytech.aisw.eom.domain.AppUser;
import polytech.aisw.eom.domain.BoardType;
import polytech.aisw.eom.domain.Post;
import polytech.aisw.eom.domain.UserRole;
import polytech.aisw.eom.repository.PostRepository;
import polytech.aisw.eom.repository.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PostRepository postRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        AppUser admin = userRepository.save(new AppUser(
                "admin",
                passwordEncoder.encode("admin"),
                "E.O.M Admin",
                "https://www.instagram.com/",
                UserRole.ADMIN
        ));

        AppUser dancer = userRepository.save(new AppUser(
                "dancer1",
                passwordEncoder.encode("1234"),
                "SHADOW_98",
                "https://www.instagram.com/",
                UserRole.USER
        ));

        postRepository.save(new Post(
                BoardType.SHOW,
                "서울 스트릿잼 후기",
                "무대, 배틀, 사이퍼까지 에너지가 넘쳤던 하루. 내 움직임을 기록하고 다음 기회를 연결합니다.",
                "https://www.instagram.com/",
                "",
                dancer
        ));
        postRepository.save(new Post(
                BoardType.CAST,
                "홍대 공연 백업댄서 모집",
                "이번 주말 쇼케이스 무대에 함께 설 백업댄서를 찾습니다. 힙합/코레오 경험자 환영.",
                "https://www.instagram.com/",
                "",
                admin
        ));
        postRepository.save(new Post(
                BoardType.HYPE,
                "2026 Seoul Street Jam 참가자 모집",
                "배틀, 워크숍, 네트워킹이 함께 열리는 주말 스트릿 행사.",
                "https://www.instagram.com/",
                "",
                admin
        ));
        postRepository.save(new Post(
                BoardType.LINK,
                "왁킹 연습 파트너 구해요",
                "성수 근처에서 주 2회 같이 연습할 파트너를 찾습니다.",
                "https://www.instagram.com/",
                "",
                dancer
        ));
    }
}
