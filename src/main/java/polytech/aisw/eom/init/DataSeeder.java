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
    private static final String REEL_CHOREO_SHOWCASE =
            "https://www.instagram.com/reel/C5frLClST0B/?utm_source=ig_web_copy_link&igsh=MzRlODBiNWFlZA==";
    private static final String REEL_OPEN_CLASS =
            "https://www.instagram.com/reel/DHGjIYxSGzP/?utm_source=ig_web_copy_link&igsh=MzRlODBiNWFlZA==";
    private static final String POST_SHOW_ARCHIVE =
            "https://www.instagram.com/p/DEvAdpRSYHL/?utm_source=ig_web_copy_link&igsh=MzRlODBiNWFlZA==";
    private static final String REEL_CYPHER_CUT =
            "https://www.instagram.com/reel/C8rY8bhSQGE/?utm_source=ig_web_copy_link&igsh=MzRlODBiNWFlZA==";
    private static final String REEL_KRUMP_LOG =
            "https://www.instagram.com/reel/C5vUUTJSeeJ/?utm_source=ig_web_copy_link&igsh=MzRlODBiNWFlZA==";
    private static final String REEL_TEAM_TEASER =
            "https://www.instagram.com/reel/C5LZponSDE1/?utm_source=ig_web_copy_link&igsh=MzRlODBiNWFlZA==";
    private static final String REEL_LOCKING_ARCHIVE =
            "https://www.instagram.com/reel/C4sR2wfSVZK/?utm_source=ig_web_copy_link&igsh=MzRlODBiNWFlZA==";
    private static final String POST_CAST_REFERENCE =
            "https://www.instagram.com/p/C4XXv-iragk/?utm_source=ig_web_copy_link&igsh=MzRlODBiNWFlZA==";
    private static final String POST_CAST_NOTICE =
            "https://www.instagram.com/p/C0ost6-pSRf/?utm_source=ig_web_copy_link&igsh=MzRlODBiNWFlZA==";
    private static final String REEL_POPUP_PERFORMANCE =
            "https://www.instagram.com/reel/Cyu3bF_p0QE/?utm_source=ig_web_copy_link&igsh=MzRlODBiNWFlZA==";
    private static final String REEL_EVENT_BATTLE =
            "https://www.instagram.com/reel/Cyk6lnspeFr/?utm_source=ig_web_copy_link&igsh=MzRlODBiNWFlZA==";
    private static final String POST_EVENT_NOTICE =
            "https://www.instagram.com/p/CydU59IrR3y/?utm_source=ig_web_copy_link&igsh=MzRlODBiNWFlZA==";
    private static final String POST_WORKSHOP_NOTICE =
            "https://www.instagram.com/p/ClLvP9HLTC_/?utm_source=ig_web_copy_link&igsh=MzRlODBiNWFlZA==";
    private static final String REEL_OPEN_SESSION =
            "https://www.instagram.com/reel/Ckaufd9pKtn/?utm_source=ig_web_copy_link&igsh=MzRlODBiNWFlZA==";
    private static final String REEL_FEEDBACK_SESSION =
            "https://www.instagram.com/reel/CVj4eMTpjlY/?utm_source=ig_web_copy_link&igsh=MzRlODBiNWFlZA==";
    private static final String POST_NETWORKING =
            "https://www.instagram.com/p/CKwgC-KrsFL/?utm_source=ig_web_copy_link&igsh=MzRlODBiNWFlZA==";

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
                MediaType.INSTAGRAM, REEL_CHOREO_SHOWCASE, "/assets/source/show.png", dancer);
        savePost(BoardType.SHOW, "오픈 클래스 하이라이트", "클래스 마지막 런스루를 영상 URL로 연결해 포트폴리오처럼 보여줍니다.",
                1210, 233, 21, "오픈클래스,힙합,영상", "홍대", null, null,
                MediaType.INSTAGRAM, REEL_OPEN_CLASS, "/assets/source/hero.jpg", mina);
        savePost(BoardType.SHOW, "프리스타일 사이퍼 컷", "배틀 전 몸을 푸는 사이퍼 장면을 외부 미디어 링크로 정리했습니다.",
                980, 190, 18, "프리스타일,사이퍼,배틀", "압구정", null, null,
                MediaType.INSTAGRAM, REEL_CYPHER_CUT, "/assets/source/background.jpg", jun);
        savePost(BoardType.SHOW, "크럼프 에너지 로그", "강한 에너지와 표정이 살아있는 연습 영상을 SHOW 카드에 묶었습니다.",
                870, 166, 14, "크럼프,연습영상,무브먼트", "이태원", null, null,
                MediaType.INSTAGRAM, REEL_KRUMP_LOG, "/assets/source/dwayne-joe-_Haw_PsHa5E-unsplash.jpg", raya);
        savePost(BoardType.SHOW, "팀 퍼포먼스 티저", "정식 영상 공개 전 썸네일과 링크만 먼저 올리는 티저 포스트입니다.",
                760, 142, 11, "팀퍼포먼스,티저,댄스필름", "문래", null, null,
                MediaType.INSTAGRAM, REEL_TEAM_TEASER, "/assets/source/jakob-owens-1ua8Ujmfez4-unsplash.jpg", admin);
        savePost(BoardType.SHOW, "락킹 솔로 무브 아카이브", "연습실에서 찍은 솔로 루틴을 릴스 링크 기반으로 공유합니다.",
                690, 128, 9, "락킹,솔로,아카이브", "합정", null, null,
                MediaType.INSTAGRAM, REEL_LOCKING_ARCHIVE, "/assets/source/ben-iwara-XoZ3P4Vl0LY-unsplash.jpg", mina);
        savePost(BoardType.SHOW, "하우스 풋워크 로그", "가볍게 반복한 풋워크 루틴을 짧은 영상으로 정리했습니다.",
                650, 119, 8, "하우스,풋워크,연습", "성수", null, null,
                MediaType.INSTAGRAM, REEL_OPEN_CLASS, "/assets/source/dan-duffey-7NaBBaRzfZ4-unsplash.jpg", jun);
        savePost(BoardType.SHOW, "왁킹 라인 체크", "촬영 전 팔 라인과 포즈 흐름을 점검한 SHOW 기록입니다.",
                620, 112, 7, "왁킹,라인,포즈", "망원", null, null,
                MediaType.INSTAGRAM, POST_SHOW_ARCHIVE, "/assets/source/renee-thompson-VdN2CGmvM88-unsplash.jpg", mina);
        savePost(BoardType.SHOW, "팝핑 아이솔레이션 노트", "기초 아이솔레이션을 음악 위에 얹어 본 짧은 클립입니다.",
                590, 104, 6, "팝핑,아이솔레이션,기초", "신림", null, null,
                MediaType.INSTAGRAM, REEL_CYPHER_CUT, "/assets/source/someofakind-WaTX4_2fD9k-unsplash.jpg", dancer);
        savePost(BoardType.SHOW, "힙합 베이직 무드보드", "기본 스텝과 그루브를 하나의 무드보드처럼 묶었습니다.",
                560, 98, 5, "힙합,베이직,그루브", "연남", null, null,
                MediaType.INSTAGRAM, REEL_TEAM_TEASER, "/assets/source/samuel-regan-asante-jRM3E-MG9sg-unsplash.jpg", raya);

        savePost(BoardType.CAST, "홍대 쇼케이스 백업댄서 모집", "이번 주말 무대에 함께 설 백업댄서를 찾습니다. 힙합/코레오 경험자 환영.",
                520, 88, 16, "백업댄서,쇼케이스,모집", "홍대", LocalDate.now().plusDays(8), LocalDate.now().plusDays(3),
                MediaType.INSTAGRAM, POST_CAST_REFERENCE, "/assets/source/cast1.jpg", admin);
        savePost(BoardType.CAST, "여성 코레오 팀원 2명 모집", "정기 촬영과 공연을 함께할 팀원을 찾습니다. 포트폴리오 링크를 함께 보내주세요.",
                430, 76, 19, "팀원모집,코레오,촬영", "강남", LocalDate.now().plusDays(14), LocalDate.now().plusDays(6),
                MediaType.INSTAGRAM, POST_CAST_NOTICE, "/assets/source/cast2.jpg", mina);
        savePost(BoardType.CAST, "팝업 브랜드 퍼포머 캐스팅", "패션 팝업 현장에서 짧은 루틴을 반복할 퍼포머를 모집합니다.",
                390, 63, 12, "퍼포머,팝업,브랜드", "성수", LocalDate.now().plusDays(18), LocalDate.now().plusDays(9),
                MediaType.INSTAGRAM, REEL_POPUP_PERFORMANCE, "/assets/source/cast3.jpg", host);
        savePost(BoardType.CAST, "입문반 보조 강사 구인", "토요일 입문반 수업을 함께 운영할 보조 강사를 찾습니다.",
                310, 45, 8, "강사,입문반,구인", "신촌", LocalDate.now().plusDays(21), LocalDate.now().plusDays(10),
                MediaType.IMAGE, HAZYALA_INSTAGRAM, "/assets/source/cast4.jpg", admin);
        savePost(BoardType.CAST, "뮤직비디오 프리스타일러 모집", "원테이크 촬영에 참여할 프리스타일 댄서를 찾습니다.",
                300, 44, 7, "뮤직비디오,프리스타일,캐스팅", "상암", LocalDate.now().plusDays(12), LocalDate.now().plusDays(4),
                MediaType.INSTAGRAM, REEL_POPUP_PERFORMANCE, "/assets/source/cast1.jpg", host);
        savePost(BoardType.CAST, "팝핑 쇼케이스 게스트 모집", "짧은 게스트 세션을 채워줄 팝핑 댄서를 모집합니다.",
                288, 42, 6, "팝핑,쇼케이스,게스트", "건대", LocalDate.now().plusDays(16), LocalDate.now().plusDays(6),
                MediaType.INSTAGRAM, POST_CAST_REFERENCE, "/assets/source/cast2.jpg", jun);
        savePost(BoardType.CAST, "하우스 클래스 대체 강사", "평일 저녁 하우스 입문 수업을 맡아줄 강사를 찾습니다.",
                270, 39, 6, "하우스,강사,클래스", "합정", LocalDate.now().plusDays(20), LocalDate.now().plusDays(8),
                MediaType.INSTAGRAM, POST_CAST_NOTICE, "/assets/source/cast3.jpg", mina);
        savePost(BoardType.CAST, "댄스필름 군무 멤버 모집", "야외 촬영 군무 파트에 참여할 댄서 4명을 모집합니다.",
                252, 36, 5, "댄스필름,군무,모집", "한강", LocalDate.now().plusDays(23), LocalDate.now().plusDays(9),
                MediaType.INSTAGRAM, REEL_TEAM_TEASER, "/assets/source/cast4.jpg", admin);
        savePost(BoardType.CAST, "브랜드 쇼룸 퍼포먼스 팀", "쇼룸 오프닝에서 짧은 퍼포먼스를 보여줄 팀을 찾습니다.",
                240, 33, 5, "브랜드,쇼룸,퍼포먼스", "청담", LocalDate.now().plusDays(27), LocalDate.now().plusDays(12),
                MediaType.INSTAGRAM, REEL_POPUP_PERFORMANCE, "/assets/source/jakob-owens-1ua8Ujmfez4-unsplash.jpg", host);
        savePost(BoardType.CAST, "청소년반 보조 코치 모집", "기초 루틴을 함께 봐줄 보조 코치를 모집합니다.",
                228, 31, 4, "보조코치,청소년반,구인", "노원", LocalDate.now().plusDays(29), LocalDate.now().plusDays(13),
                MediaType.IMAGE, HAZYALA_INSTAGRAM, "/assets/source/anna-frizen-S0335VS9MHE-unsplash.jpg", dancer);

        savePost(BoardType.HYPE, "Seoul Street Jam 참가자 모집", "배틀, 워크숍, 네트워킹이 함께 열리는 주말 스트릿 행사입니다.",
                1100, 210, 35, "배틀,워크숍,스트릿잼", "무신사 개러지", LocalDate.now().plusDays(12), LocalDate.now().plusDays(5),
                MediaType.IMAGE, HAZYALA_INSTAGRAM, "/assets/source/hype.jpg", host);
        savePost(BoardType.HYPE, "올장르 1on1 배틀 오픈", "힙합, 팝핑, 왁킹, 크럼프까지 모두 열어두는 올장르 배틀입니다.",
                840, 171, 28, "올장르,1on1,배틀", "마포", LocalDate.now().plusDays(19), LocalDate.now().plusDays(11),
                MediaType.INSTAGRAM, REEL_EVENT_BATTLE, "/assets/source/sergio-kian-BB48dwtKHBk-unsplash.jpg", host);
        savePost(BoardType.HYPE, "코레오 워크숍 위크", "평일 저녁 집중 워크숍으로 짧은 결과물 촬영까지 진행합니다.",
                640, 132, 17, "코레오,워크숍,클래스", "강남", LocalDate.now().plusDays(25), LocalDate.now().plusDays(17),
                MediaType.INSTAGRAM, POST_WORKSHOP_NOTICE, "/assets/source/anna-frizen-S0335VS9MHE-unsplash.jpg", mina);
        savePost(BoardType.HYPE, "루키 쇼케이스 관객 모집", "첫 무대를 준비하는 루키 댄서들의 쇼케이스에 놀러오세요.",
                580, 104, 13, "루키,쇼케이스,관객모집", "연남", LocalDate.now().plusDays(31), LocalDate.now().plusDays(24),
                MediaType.INSTAGRAM, POST_EVENT_NOTICE, "/assets/source/yeremia-krisnamurti-G4pK0cQAfU8-unsplash.jpg", admin);
        savePost(BoardType.HYPE, "월말 프리스타일 나이트", "이번 달 마지막 주에 열리는 소규모 프리스타일 파티입니다.",
                560, 99, 12, "프리스타일,파티,공식행사", "성수", LocalDate.now().plusDays(2), LocalDate.now().plusDays(1),
                MediaType.INSTAGRAM, REEL_EVENT_BATTLE, "/assets/source/hype.jpg", host);
        savePost(BoardType.HYPE, "팝핑 루키 배틀", "루키 팝퍼를 위한 1on1 배틀과 잼이 함께 열립니다.",
                540, 94, 11, "팝핑,루키,배틀", "신촌", LocalDate.now().plusDays(4), LocalDate.now().plusDays(2),
                MediaType.INSTAGRAM, POST_EVENT_NOTICE, "/assets/source/sergio-kian-BB48dwtKHBk-unsplash.jpg", jun);
        savePost(BoardType.HYPE, "왁킹 오픈 세션", "왁킹 댄서들이 모여 음악과 라인을 공유하는 공식 오픈 세션입니다.",
                510, 88, 10, "왁킹,오픈세션,공식행사", "망원", LocalDate.now().plusDays(7), LocalDate.now().plusDays(3),
                MediaType.INSTAGRAM, REEL_OPEN_SESSION, "/assets/source/renee-thompson-VdN2CGmvM88-unsplash.jpg", mina);
        savePost(BoardType.HYPE, "스트릿 쇼케이스 데이", "장르별 팀이 짧은 무대를 보여주는 월간 쇼케이스입니다.",
                490, 83, 9, "쇼케이스,공식행사,스트릿", "홍대", LocalDate.now().plusDays(9), LocalDate.now().plusDays(4),
                MediaType.INSTAGRAM, POST_WORKSHOP_NOTICE, "/assets/source/dwayne-joe-_Haw_PsHa5E-unsplash.jpg", admin);
        savePost(BoardType.HYPE, "힙합 베이직 워크숍", "기초 그루브와 루틴을 함께 다루는 입문 워크숍입니다.",
                470, 79, 8, "힙합,워크숍,클래스", "강남", LocalDate.now().plusDays(22), LocalDate.now().plusDays(14),
                MediaType.INSTAGRAM, POST_WORKSHOP_NOTICE, "/assets/source/anna-frizen-S0335VS9MHE-unsplash.jpg", dancer);
        savePost(BoardType.HYPE, "올스타일 크루 나이트", "크루 단위로 모여 쇼케이스와 배틀을 함께 여는 밤입니다.",
                450, 75, 8, "크루,쇼케이스,배틀", "문래", LocalDate.now().plusDays(26), LocalDate.now().plusDays(16),
                MediaType.INSTAGRAM, REEL_EVENT_BATTLE, "/assets/source/yeremia-krisnamurti-G4pK0cQAfU8-unsplash.jpg", host);

        savePost(BoardType.LINK, "왁킹 연습 파트너 구해요", "성수 근처에서 주 2회 같이 연습할 파트너를 찾습니다.",
                360, 54, 15, "왁킹,연습파트너,성수", "성수", null, null,
                MediaType.INSTAGRAM, POST_NETWORKING, "/assets/source/link.png", dancer);
        savePost(BoardType.LINK, "촬영 가능한 연습실 공유", "야간 촬영 가능한 공간을 같이 예약할 팀을 찾습니다.",
                280, 41, 9, "연습실,촬영,공유", "을지로", null, null,
                MediaType.INSTAGRAM, POST_SHOW_ARCHIVE, "/assets/source/frankie-cordoba-Y1kZT7S2aPI-unsplash.jpg", jun);
        savePost(BoardType.LINK, "배틀 영상 피드백 모임", "서로의 배틀 영상을 보고 루틴과 무브를 피드백하는 작은 모임입니다.",
                240, 38, 10, "피드백,배틀영상,네트워킹", "합정", null, null,
                MediaType.INSTAGRAM, REEL_FEEDBACK_SESSION, "/assets/source/link.png", raya);
        savePost(BoardType.LINK, "토요일 오픈 세션 같이 갈 분", "혼자 가기 어색한 오픈 세션, 같이 움직이고 네트워킹해요.",
                220, 31, 7, "오픈세션,네트워킹,토요일", "홍대", LocalDate.now().plusDays(6), null,
                MediaType.INSTAGRAM, REEL_OPEN_SESSION, "/assets/source/samuel-regan-asante-jRM3E-MG9sg-unsplash.jpg", dancer);
        savePost(BoardType.LINK, "하우스 음악 공유방", "하우스 연습곡과 셋리스트를 같이 모을 분들을 찾습니다.",
                210, 30, 6, "하우스,음악공유,네트워킹", "온라인", null, null,
                MediaType.INSTAGRAM, POST_NETWORKING, "/assets/source/link.png", jun);
        savePost(BoardType.LINK, "왁킹 촬영 메이트", "짧은 릴스 촬영을 번갈아 도와줄 메이트를 구합니다.",
                198, 28, 6, "왁킹,촬영,메이트", "망원", null, null,
                MediaType.INSTAGRAM, POST_SHOW_ARCHIVE, "/assets/source/frankie-cordoba-Y1kZT7S2aPI-unsplash.jpg", mina);
        savePost(BoardType.LINK, "배틀 동행 구해요", "이번 달 배틀에 같이 갈 동행을 찾습니다. 장르는 상관없어요.",
                186, 26, 5, "배틀,동행,네트워킹", "마포", LocalDate.now().plusDays(5), null,
                MediaType.INSTAGRAM, REEL_FEEDBACK_SESSION, "/assets/source/sergio-kian-BB48dwtKHBk-unsplash.jpg", raya);
        savePost(BoardType.LINK, "연습실 공동 예약", "평일 낮 시간대 연습실을 함께 예약할 팀을 찾습니다.",
                174, 24, 5, "연습실,공동예약,공유", "강남", null, null,
                MediaType.INSTAGRAM, POST_NETWORKING, "/assets/source/dan-duffey-7NaBBaRzfZ4-unsplash.jpg", admin);
        savePost(BoardType.LINK, "프리스타일 피드백 파트너", "서로 짧게 촬영하고 바로 피드백해줄 파트너를 찾습니다.",
                162, 22, 4, "프리스타일,피드백,파트너", "성수", null, null,
                MediaType.INSTAGRAM, REEL_FEEDBACK_SESSION, "/assets/source/someofakind-WaTX4_2fD9k-unsplash.jpg", dancer);
        savePost(BoardType.LINK, "크루 합동 연습 제안", "다른 크루와 합동으로 오픈 연습을 해보고 싶습니다.",
                150, 20, 4, "크루,합동연습,오픈세션", "문래", null, null,
                MediaType.INSTAGRAM, REEL_OPEN_SESSION, "/assets/source/link.png", host);
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
                mediaType == MediaType.INSTAGRAM ? mediaUrl : HAZYALA_INSTAGRAM,
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
