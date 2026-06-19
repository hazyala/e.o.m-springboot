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
- 게시글 작성, 삭제, 조회 같은 기능은 Service를 통해 처리합니다.
- `open-in-view=false`를 기준으로 지연 로딩 문제가 없도록 조회합니다.
- Render 배포를 위해 `server.port=${PORT:8080}` 설정을 유지합니다.

## 6. 도메인 초안

### AppUser

- username
- password
- displayName
- instagramUrl
- profileImageUrl
- bio
- crewName
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
- author
- createdAt

### MediaType

- IMAGE
- INSTAGRAM
- YOUTUBE
- VIDEO_LINK
- EXTERNAL_LINK

MVP에서는 직접 영상 파일 업로드를 구현하지 않고, 인스타그램 릴스/게시물 URL, 유튜브 URL, 외부 영상 URL을 `mediaUrl`로 저장합니다. 목록 화면은 `thumbnailUrl`을 카드 이미지로 사용하고, 상세 화면은 이후 `mediaType + mediaUrl` 기준 embed 또는 링크 폴백으로 확장합니다. 인스타그램 URL은 `https://www.instagram.com/hazyala?igsh=ZW1maGFzNHQzdzEx&utm_source=qr` 계정과 해당 계정 내 확인 가능한 실제 릴스/게시물 URL을 기준으로 하며, 정확한 릴스/게시물 URL을 확인한 경우 해당 URL을 사용하고 확인 전에는 프로필 URL을 fallback으로 사용한 뒤 실제 URL로 교체 예정입니다.

현재 `DataSeeder`는 제공받은 실제 인스타그램 릴스/게시물 URL 16개를 게시글 `mediaUrl`에 반영합니다. Instagram 미디어 게시글은 `instagramUrl`도 해당 게시물 URL을 사용하고, 아직 개별 URL이 없는 항목만 프로필 URL을 fallback으로 둡니다.

### Comment

- post
- author
- content
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
- `/dashboard`, `/posts`, `/posts/{id}`, `/activity`, `/events`, `/dancers`, 보드, 마이페이지: 로그인 필요
- `/admin/**`: ADMIN 필요

## 8. 화면 라우트

대시보드와 커뮤니티 탐색 화면은 Spring MVC Controller -> Service -> Repository 흐름을 유지합니다.

- `/dashboard`: 로그인 후 첫 화면. Today Pick, Popular, Recent, Tags, Activity, Events, Dancers 미리보기를 렌더링합니다.
- `/dashboard?board=SHOW|CAST|HYPE|LINK`: Recent 기본 보드 선택값을 지정합니다. 화면에서는 네 보드 데이터를 모두 렌더링한 뒤 클라이언트 탭 전환으로 Recent 목록만 바꿉니다.
- `/posts/{id}`: 대시보드 Today Pick, Popular, Recent의 내부 게시글 상세 목적지입니다.
- `/posts?tag={tag}`: Tags 클릭 시 이동하는 태그 검색 목록입니다.
- `/activity`: Recent 탭과 독립적인 전체 최신글 목록입니다.
- `/events`: 이번 달 HYPE 공식 행사 목록입니다.
- `/dancers`: 장르별 댄서 탐색 목록입니다.
- 외부 인스타그램/미디어 URL: Follow, 프로필 보기, 게시글 상세의 첨부 링크에서만 새 탭으로 열며, 대시보드 썸네일은 버튼이 아닌 게시글 미리보기로 취급합니다.

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
