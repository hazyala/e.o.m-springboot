# E.O.M (Echo of Movement)

E.O.M은 인스타그램, DM, 오픈채팅, 지인 추천에 흩어진 댄서들의 활동과 기회를 한곳에 모으는 스트릿 댄서 커뮤니티입니다. 로그인하면 댄서들의 아지트가 열리고, 사용자는 나를 어필하고, 강사를 구하고, 팀원을 찾고, 행사를 알리고, 함께 연습할 사람을 만날 수 있습니다.

## 주요 섹션

- **SHOW**: 포트폴리오, 공연 기록, 연습 영상, 안무 쉐어
- **CAST**: 강사, 백업댄서, 크루, 팀원, 오디션 모집
- **HYPE**: 배틀, 워크숍, 스트릿 행사 홍보
- **LINK**: 연습 파트너, 연습실 공유, 네트워킹, 정보 공유
- **Dashboard**: 로그인 후 열리는 댄서들의 아지트
- **My Page**: 개인 활동 기록과 인스타그램 링크
- **Admin**: 사용자와 게시글 관리

## 대표 기능

- 사용자/관리자 로그인
- 관리자 페이지
- 보드별 게시글 목록, 상세, 작성
- 인스타그램 프로필 링크와 게시물 URL 첨부
- 라이트/다크 모드
- Render 무료 티어 배포 대응

## 기술 스택

| 영역 | 기술 |
| --- | --- |
| 언어 | Java 21 |
| 프레임워크 | Spring Boot 3.x |
| 뷰 | Thymeleaf |
| 인증 | Spring Security |
| 데이터 | Spring Data JPA |
| 로컬 DB | H2 |
| 배포 DB | PostgreSQL |
| 배포 | Render Web Service |

## 프로젝트 구조

```text
src/main/java/polytech/aisw/eom
├─ config/       Spring Security 등 설정
├─ controller/   요청 처리와 Thymeleaf 뷰 반환
├─ service/      비즈니스 로직
├─ repository/   Spring Data JPA 저장소
├─ domain/       JPA 엔티티와 enum
├─ dto/          폼/뷰 데이터 객체
├─ security/     인증 사용자 로딩
└─ init/         로컬 시드 데이터

src/main/resources
├─ templates/    Thymeleaf 화면
└─ static/       css, js, assets
```

## 로컬 실행

요구사항:
- JDK 21
- IntelliJ IDEA 권장

IntelliJ에서 프로젝트 폴더를 열고 Gradle 프로젝트로 인식시킨 뒤 `EomApplication`을 실행합니다.

기본 접속:
- `http://localhost:8080`
- H2 콘솔: `http://localhost:8080/h2-console`

데모 계정:

| 아이디 | 비밀번호 | 권한 |
| --- | --- | --- |
| `admin` | `admin` | ADMIN |
| `dancer1` | `1234` | USER |

## 배포 방향

Docker 없이 Render Web Service로 배포합니다.

```text
Build Command: ./gradlew clean build
Start Command: java -jar build/libs/eom-springboot-0.0.1-SNAPSHOT.jar
Environment:
- SPRING_PROFILES_ACTIVE=prod
- SPRING_DATASOURCE_URL
- SPRING_DATASOURCE_USERNAME
- SPRING_DATASOURCE_PASSWORD
```

현재 `server.port=${PORT:8080}` 설정으로 Render의 `PORT` 환경변수를 받을 수 있습니다.

