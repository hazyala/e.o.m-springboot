package polytech.aisw.eom.init;

import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import polytech.aisw.eom.domain.AppUser;
import polytech.aisw.eom.domain.BoardType;
import polytech.aisw.eom.domain.MediaType;
import polytech.aisw.eom.domain.Post;
import polytech.aisw.eom.domain.UserRole;
import polytech.aisw.eom.repository.PostRepository;
import polytech.aisw.eom.repository.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final String HAZYALA_INSTAGRAM =
            "https://www.instagram.com/hazyala?igsh=ZW1maGFzNHQzdzEx&utm_source=qr";

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
                HAZYALA_INSTAGRAM,
                "/assets/source/hero.jpg",
                "E.O.M 커뮤니티 운영자. 쇼케이스, 배틀, 워크숍 소식을 큐레이션합니다.",
                "E.O.M",
                UserRole.ADMIN
        ));

        AppUser dancer = userRepository.save(new AppUser(
                "dancer1",
                passwordEncoder.encode("1234"),
                "SHADOW_98",
                HAZYALA_INSTAGRAM,
                "/assets/source/show.png",
                "힙합과 코레오를 오가며 무대와 연습 기록을 남기는 댄서.",
                "Night Shift",
                UserRole.USER
        ));

        AppUser mina = saveUser("mina.flow", "MINA FLOW", "/assets/source/cast1.jpg",
                "락킹 기반으로 팀 퍼포먼스와 클래스 영상을 기록합니다.", "Ripple Crew");
        AppUser jun = saveUser("jun.wav", "JUN WAV", "/assets/source/cast2.jpg",
                "프리스타일 배틀과 오픈 세션을 즐기는 올라운더.", "Wave Room");
        AppUser raya = saveUser("raya.krump", "RAYA", "/assets/source/cast3.jpg",
                "크럼프와 힙합 에너지로 쇼케이스를 만드는 댄서.", "Raw Steps");
        AppUser host = saveUser("stage.host", "STAGE HOST", "/assets/source/hype.jpg",
                "서울 기반 배틀, 워크숍, 팝업 클래스를 연결하는 주최자.", "Open Floor");

        savePost(BoardType.SHOW, "릴스 기반 코레오 쇼케이스", "짧은 릴스로 무드와 동선을 먼저 보여주는 SHOW 포스트입니다.",
                1480, 284, 32, "코레오,릴스,쇼케이스", "성수", null, null,
                MediaType.INSTAGRAM, HAZYALA_INSTAGRAM, "/assets/source/show.png", dancer);
        savePost(BoardType.SHOW, "오픈 클래스 하이라이트", "클래스 마지막 런스루를 영상 URL로 연결해 포트폴리오처럼 보여줍니다.",
                1210, 233, 21, "오픈클래스,힙합,영상", "홍대", null, null,
                MediaType.INSTAGRAM, HAZYALA_INSTAGRAM, "/assets/source/hero.jpg", mina);
        savePost(BoardType.SHOW, "프리스타일 사이퍼 컷", "배틀 전 몸을 푸는 사이퍼 장면을 외부 미디어 링크로 정리했습니다.",
                980, 190, 18, "프리스타일,사이퍼,배틀", "압구정", null, null,
                MediaType.VIDEO_LINK, HAZYALA_INSTAGRAM, "/assets/source/background.jpg", jun);
        savePost(BoardType.SHOW, "크럼프 에너지 로그", "강한 에너지와 표정이 살아있는 연습 영상을 SHOW 카드에 묶었습니다.",
                870, 166, 14, "크럼프,연습영상,무브먼트", "이태원", null, null,
                MediaType.INSTAGRAM, HAZYALA_INSTAGRAM, "/assets/source/dwayne-joe-_Haw_PsHa5E-unsplash.jpg", raya);
        savePost(BoardType.SHOW, "팀 퍼포먼스 티저", "정식 영상 공개 전 썸네일과 링크만 먼저 올리는 티저 포스트입니다.",
                760, 142, 11, "팀퍼포먼스,티저,댄스필름", "문래", null, null,
                MediaType.EXTERNAL_LINK, HAZYALA_INSTAGRAM, "/assets/source/jakob-owens-1ua8Ujmfez4-unsplash.jpg", admin);
        savePost(BoardType.SHOW, "락킹 솔로 무브 아카이브", "연습실에서 찍은 솔로 루틴을 릴스 링크 기반으로 공유합니다.",
                690, 128, 9, "락킹,솔로,아카이브", "합정", null, null,
                MediaType.INSTAGRAM, HAZYALA_INSTAGRAM, "/assets/source/ben-iwara-XoZ3P4Vl0LY-unsplash.jpg", mina);

        savePost(BoardType.CAST, "홍대 쇼케이스 백업댄서 모집", "이번 주말 무대에 함께 설 백업댄서를 찾습니다. 힙합/코레오 경험자 환영.",
                520, 88, 16, "백업댄서,쇼케이스,모집", "홍대", LocalDate.now().plusDays(8), LocalDate.now().plusDays(3),
                MediaType.IMAGE, HAZYALA_INSTAGRAM, "/assets/source/cast1.jpg", admin);
        savePost(BoardType.CAST, "여성 코레오 팀원 2명 모집", "정기 촬영과 공연을 함께할 팀원을 찾습니다. 포트폴리오 링크를 함께 보내주세요.",
                430, 76, 19, "팀원모집,코레오,촬영", "강남", LocalDate.now().plusDays(14), LocalDate.now().plusDays(6),
                MediaType.IMAGE, HAZYALA_INSTAGRAM, "/assets/source/cast2.jpg", mina);
        savePost(BoardType.CAST, "팝업 브랜드 퍼포머 캐스팅", "패션 팝업 현장에서 짧은 루틴을 반복할 퍼포머를 모집합니다.",
                390, 63, 12, "퍼포머,팝업,브랜드", "성수", LocalDate.now().plusDays(18), LocalDate.now().plusDays(9),
                MediaType.EXTERNAL_LINK, HAZYALA_INSTAGRAM, "/assets/source/cast3.jpg", host);
        savePost(BoardType.CAST, "입문반 보조 강사 구인", "토요일 입문반 수업을 함께 운영할 보조 강사를 찾습니다.",
                310, 45, 8, "강사,입문반,구인", "신촌", LocalDate.now().plusDays(21), LocalDate.now().plusDays(10),
                MediaType.IMAGE, HAZYALA_INSTAGRAM, "/assets/source/cast4.jpg", admin);

        savePost(BoardType.HYPE, "Seoul Street Jam 참가자 모집", "배틀, 워크숍, 네트워킹이 함께 열리는 주말 스트릿 행사입니다.",
                1100, 210, 35, "배틀,워크숍,스트릿잼", "무신사 개러지", LocalDate.now().plusDays(12), LocalDate.now().plusDays(5),
                MediaType.IMAGE, HAZYALA_INSTAGRAM, "/assets/source/hype.jpg", host);
        savePost(BoardType.HYPE, "올장르 1on1 배틀 오픈", "힙합, 팝핑, 왁킹, 크럼프까지 모두 열어두는 올장르 배틀입니다.",
                840, 171, 28, "올장르,1on1,배틀", "마포", LocalDate.now().plusDays(19), LocalDate.now().plusDays(11),
                MediaType.INSTAGRAM, HAZYALA_INSTAGRAM, "/assets/source/sergio-kian-BB48dwtKHBk-unsplash.jpg", host);
        savePost(BoardType.HYPE, "코레오 워크숍 위크", "평일 저녁 집중 워크숍으로 짧은 결과물 촬영까지 진행합니다.",
                640, 132, 17, "코레오,워크숍,클래스", "강남", LocalDate.now().plusDays(25), LocalDate.now().plusDays(17),
                MediaType.EXTERNAL_LINK, HAZYALA_INSTAGRAM, "/assets/source/anna-frizen-S0335VS9MHE-unsplash.jpg", mina);
        savePost(BoardType.HYPE, "루키 쇼케이스 관객 모집", "첫 무대를 준비하는 루키 댄서들의 쇼케이스에 놀러오세요.",
                580, 104, 13, "루키,쇼케이스,관객모집", "연남", LocalDate.now().plusDays(31), LocalDate.now().plusDays(24),
                MediaType.IMAGE, HAZYALA_INSTAGRAM, "/assets/source/yeremia-krisnamurti-G4pK0cQAfU8-unsplash.jpg", admin);

        savePost(BoardType.LINK, "왁킹 연습 파트너 구해요", "성수 근처에서 주 2회 같이 연습할 파트너를 찾습니다.",
                360, 54, 15, "왁킹,연습파트너,성수", "성수", null, null,
                MediaType.IMAGE, HAZYALA_INSTAGRAM, "/assets/source/link.png", dancer);
        savePost(BoardType.LINK, "촬영 가능한 연습실 공유", "야간 촬영 가능한 공간을 같이 예약할 팀을 찾습니다.",
                280, 41, 9, "연습실,촬영,공유", "을지로", null, null,
                MediaType.EXTERNAL_LINK, HAZYALA_INSTAGRAM, "/assets/source/frankie-cordoba-Y1kZT7S2aPI-unsplash.jpg", jun);
        savePost(BoardType.LINK, "배틀 영상 피드백 모임", "서로의 배틀 영상을 보고 루틴과 무브를 피드백하는 작은 모임입니다.",
                240, 38, 10, "피드백,배틀영상,네트워킹", "합정", null, null,
                MediaType.VIDEO_LINK, HAZYALA_INSTAGRAM, "/assets/source/link.png", raya);
        savePost(BoardType.LINK, "토요일 오픈 세션 같이 갈 분", "혼자 가기 어색한 오픈 세션, 같이 움직이고 네트워킹해요.",
                220, 31, 7, "오픈세션,네트워킹,토요일", "홍대", LocalDate.now().plusDays(6), null,
                MediaType.IMAGE, HAZYALA_INSTAGRAM, "/assets/source/samuel-regan-asante-jRM3E-MG9sg-unsplash.jpg", dancer);
    }

    private AppUser saveUser(String username, String displayName, String profileImageUrl, String bio, String crewName) {
        return userRepository.save(new AppUser(
                username,
                passwordEncoder.encode("1234"),
                displayName,
                HAZYALA_INSTAGRAM,
                profileImageUrl,
                bio,
                crewName,
                UserRole.USER
        ));
    }

    private void savePost(
            BoardType boardType,
            String title,
            String content,
            int viewCount,
            int likeCount,
            int commentCount,
            String tags,
            String location,
            LocalDate eventDate,
            LocalDate deadline,
            MediaType mediaType,
            String mediaUrl,
            String thumbnailUrl,
            AppUser author
    ) {
        postRepository.save(new Post(
                boardType,
                title,
                content,
                HAZYALA_INSTAGRAM,
                thumbnailUrl,
                viewCount,
                likeCount,
                commentCount,
                tags,
                location,
                eventDate,
                deadline,
                mediaType,
                mediaUrl,
                thumbnailUrl,
                author
        ));
    }
}
