# E.O.M Architecture

## 1. 기술 스택

- Java 21
- Spring Boot 3.x
- Spring MVC
- Thymeleaf
- Spring Security
- Spring Data JPA
- H2 local
- PostgreSQL prod
- Render Web Service

## 2. 핵심 구조

반드시 다음 흐름을 지킵니다.

```text
Controller -> Service -> Repository -> Domain
```

역할:
- Controller: 요청 파라미터 수집, 인증 정보 확인, Service 호출, 뷰 반환
- Service: 비즈니스 로직, 검증, 트랜잭션 처리
- Repository: JPA 데이터 접근
- Domain: 엔티티와 핵심 모델
- DTO: 폼 요청과 화면 전달 데이터
- Templates: Thymeleaf 화면
- Static: CSS, JS, 이미지 에셋

## 3. 패키지 구조

```text
src/main/java/polytech/aisw/eom
├─ config
├─ controller
├─ service
├─ repository
├─ domain
├─ dto
├─ security
└─ init
```

## 4. 리소스 구조

```text
src/main/resources
├─ application.yml
├─ templates
│  ├─ fragments
│  ├─ index.html
│  ├─ login.html
│  ├─ dashboard.html
│  ├─ post-list.html
│  ├─ post-detail.html
│  ├─ my-page.html
│  ├─ dancers.html
│  ├─ dancer-detail.html
│  └─ admin.html
└─ static
   ├─ css
   ├─ js
   └─ assets
      └─ source
```

## 5. 설계 원칙

- Controller에 비즈니스 로직을 두지 않습니다.
- Repository를 Controller에서 직접 호출하지 않습니다.
- Thymeleaf 템플릿은 화면 표현에 집중합니다.
- 공통 헤더, 푸터, 네비게이션은 fragments로 분리합니다.
- 인증/권한은 Spring Security 설정과 security 패키지에서 관리합니다.
- 게시글 조회와 후속 작성/삭제 같은 기능은 Service를 통해 처리합니다.
- 검색, 보드 탐색, Events, 마이페이지 데이터 조립은 Controller -> Service -> Repository 순서를 지킵니다.
- 빈 검색어는 Service에서 빈 리스트로 처리해 `/posts`가 전체 게시글 목록으로 흐르지 않게 합니다.
- `open-in-view=false`를 기준으로 지연 로딩 문제가 없도록 조회합니다.
- Render 배포를 위해 `server.port=${PORT:8080}` 설정을 유지합니다.

## 6. 도메인 초안

### AppUser

- username
- password
- displayName
- instagramUrl
- profileImageUrl
- headerImageUrl
- bio
- crewName
- primaryGenre
- role
- createdAt

### Post

- boardType
- title
- content
- instagramUrl
- imageUrl
- viewCount
- likeCount
- commentCount
- tags
- location
- eventDate
- deadline
- mediaType
- mediaUrl
- thumbnailUrl
- portfolioSelected
- portfolioPinned
- author
- createdAt

### MediaType

- IMAGE
- INSTAGRAM
- YOUTUBE
- VIDEO_LINK
- EXTERNAL_LINK

MVP에서는 직접 영상 파일 업로드를 구현하지 않고, 인스타그램 릴스/게시물 URL, 유튜브 URL, 외부 영상 URL을 `mediaUrl`로 저장합니다. 목록 화면은 `thumbnailUrl`을 카드 이미지로 사용하고, 상세 화면은 `thumbnailUrl` 중심 프리뷰와 새 탭 외부 링크만 제공합니다. 인스타그램 URL은 `https://www.instagram.com/hazyala?igsh=ZW1maGFzNHQzdzEx&utm_source=qr` 계정과 해당 계정 내 확인 가능한 실제 릴스/게시물 URL을 기준으로 하며, 정확한 릴스/게시물 URL을 확인한 경우 해당 URL을 사용하고 확인 전에는 프로필 URL을 fallback으로 사용한 뒤 실제 URL로 교체 예정입니다.

현재 `DataSeeder`는 제공받은 실제 인스타그램 릴스/게시물 URL 16개를 게시글 `mediaUrl`에 반영합니다. Instagram 미디어 게시글은 `instagramUrl`도 해당 게시물 URL을 사용하고, 아직 개별 URL이 없는 항목만 프로필 URL을 fallback으로 둡니다.

### PostSortOption

- LATEST: `createdAt desc`
- VIEWS: `viewCount desc, createdAt desc`
- COMMENTS: `commentCount desc, createdAt desc`
- LIKES: `likeCount desc, createdAt desc`

보드 목록, 검색 결과, 태그 검색, Events 목록은 같은 정렬 키 `sort=latest|views|comments|likes`를 공유합니다.

### Comment

- post
- author
- content
- createdAt

### JoinedEvent

- user
- eventDate
- eventName
- result
- createdAt

### PostLike

- post
- user
- createdAt

## 7. 인증/권한

권한:
- USER
- ADMIN

접근:
- `/`, `/login`, `/css/**`, `/js/**`, `/assets/**`: 공개
- `/dashboard`, `/posts`, `/posts/{id}`, `/posts?q={query}`, `/posts?tag={tag}`, `/boards/all`, `/boards/{board}`, `/events`, `/dancers`, `/my-page`, `/me`: 로그인 필요
- `/admin/**`: ADMIN 필요

로그인 폼의 `remember-me` 체크박스는 기본 선택 상태이며, Spring Security remember-me 쿠키 유효기간은 14일입니다. 로그아웃 시 `JSESSIONID`와 `remember-me` 쿠키를 함께 삭제합니다.

## 8. 화면 라우트

대시보드와 커뮤니티 탐색 화면은 Spring MVC Controller -> Service -> Repository 흐름을 유지합니다.

- `/dashboard`: 로그인 후 첫 화면. Today Pick, Popular, Recent, Tags, Activity, Events, Dancers 미리보기를 렌더링합니다.
- `/`: 공개 index입니다. 인증 상태에 따라 Login/My Page 링크를 전환하고, SHOW/CAST/HYPE/LINK CTA는 비로그인 상태에서 `/login`, 로그인 상태에서 `/boards/{board}`로 이동합니다.
- `/dashboard?board=SHOW|CAST|HYPE|LINK`: Recent 기본 보드 선택값을 지정합니다. 화면에서는 네 보드 데이터를 모두 렌더링한 뒤 클라이언트 탭 전환으로 Recent 목록만 바꿉니다.
- `/boards/all`: SHOW, CAST, HYPE, LINK 전체 목록입니다. 대시보드 Activity `ALL` 목적지이며 `sort=latest|views|comments|likes` 정렬 쿼리를 지원합니다.
- `/boards/SHOW|CAST|HYPE|LINK`: 보드별 전체 탐색 목록입니다. 대시보드 헤더 보드 링크와 Recent의 `ALL` 목적지이며 `sort=latest|views|comments|likes` 정렬 쿼리를 지원합니다.
- `/posts`: 헤더 검색과 대시보드 Tags `ALL` 목적지입니다. `q` 쿼리는 `tags`, `title`, `content`, `author.displayName`, `author.crewName` 통합 검색으로 처리하고, 빈 검색어는 전체 목록으로 redirect하지 않고 검색 안내/추천 태그 상태를 렌더링합니다.
- `/posts` 검색어 정규화: `q`가 `#왁킹`처럼 들어오면 앞의 `#`를 제거합니다. 태그 클릭은 `/posts?tag={tag}`를 사용하고 `findByTagsContainingIgnoreCase`로 조회합니다.
- `/posts/{id}`: 대시보드 Today Pick, Popular, Recent 및 목록 카드의 내부 게시글 상세 목적지입니다.
- `/posts?tag={tag}`: Tags 클릭 시 이동하는 태그 검색 목록이며, 검색 결과 화면의 목록형 UI를 공유합니다.
- `/events`: 이번 달 HYPE 공식 행사 목록입니다. `eventDate`, `deadline`, `location`, `boardType`, `mediaType`, `thumbnailUrl`, 제목, 본문 미리보기, 작성자를 보여주며 외부 미디어는 상세 화면에서만 새 탭 링크로 제공합니다.
- `/dancers`: 장르별 댄서 탐색 목록입니다.
- `/my-page`, `/me`: 로그인한 사용자의 프로필, 포트폴리오, 작성 게시글, 참여 이벤트, 좋아요한 게시글, 작성 댓글, 자동 활동 이력을 렌더링합니다. admin도 본인 마이페이지에 접근할 수 있습니다.
- `/my-page/profile`: 프로필 히어로에 쓰는 이름, 크루, 주 장르, 소개, 인스타그램 URL, 프로필 이미지 URL, 헤더 이미지 URL을 저장합니다.
- `/my-page/account`: 아이디와 비밀번호를 변경합니다. 현재 비밀번호 검증을 통과해야 하며, 저장 후 로그아웃되어 `/login`으로 이동합니다.
- `/my-page/portfolio/select`: 내가 쓴 게시글을 포트폴리오 탭에 포함하거나 제외합니다.
- `/my-page/portfolio/pin`: 선택된 포트폴리오 중 상단 고정 상태를 저장하며 최대 3개로 제한합니다.
- `/my-page/joined-events`: 날짜, 행사명, 결과로 구성된 참여 이벤트 이력을 사용자가 직접 추가합니다.
- `/my-page/joined-events/update`, `/my-page/joined-events/delete`: 본인 참여 이벤트 이력만 수정/삭제합니다.
- 외부 인스타그램/미디어 URL: Follow, 프로필 보기, 게시글 상세의 `OPEN MEDIA`/`INSTAGRAM` 링크에서만 새 탭으로 열며, 대시보드와 목록 썸네일은 내부 게시글 미리보기로 취급합니다.
- 직접 영상 업로드와 실제 embed는 현재 MVC 범위에 포함하지 않습니다. 기존 `mediaType + mediaUrl + thumbnailUrl` 필드를 유지합니다.

## 9. 배포 구조

Docker 없이 Render Web Service로 배포합니다.

```text
Build Command: ./gradlew clean build
Start Command: java -jar build/libs/eom-springboot-0.0.1-SNAPSHOT.jar
```

환경변수:
- `SPRING_PROFILES_ACTIVE=prod`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
