# E.O.M HTTP 요청 계약

기준: Controller, DTO, SecurityConfig. JSON REST 서버가 아니라 Thymeleaf 페이지·form POST다. URL path의 id는 Long이며 form 본문은 URL-encoded 또는 파일이 있는 경우 multipart다.

## 인증·응답

홈·로그인·회원가입·정적 파일·H2 console을 제외한 화면에는 인증이 필요하다. `/admin/**`는 ADMIN이다. 일반 POST에는 CSRF token을 포함한다. Spring Security가 `POST /login`의 username/password/remember-me와 logout을 처리한다.

GET은 HTML 또는 redirect, POST는 대개 redirect다. 작성/수정의 binding·검증·미디어 오류는 폼 HTML로 반환한다. 작성자/관리자 검사는 Service에서도 수행한다.

## 페이지·검색

| Method | Endpoint | 입력 | 응답 / 역할 |
|---|---|---|---|
| GET | `/` | 없음 | 소개 HTML, 공개 |
| GET | `/login` | 없음 | 로그인 HTML, 공개 |
| POST | `/signup` | displayName, username, password, passwordConfirm | login redirect, 공개·CSRF |
| POST | `/login` | username, password, remember-me(선택) | Security 로그인 / dashboard redirect |
| POST | `/logout` | CSRF | 홈 redirect |
| GET | `/dashboard` | board=SHOW | 대시보드 HTML |
| GET | `/posts` | q, tag, sort=latest | 검색 HTML |
| GET | `/boards/{board}` | board=all 또는 SHOW/CAST/HYPE/LINK, sort, officialEvents=false | 보드 HTML |
| GET | `/posts/{id}` | id | 상세 HTML 또는 보드 redirect |
| GET | `/activity` | sort=latest | boards/all redirect |
| GET | `/events` | 없음 | boards/HYPE?officialEvents=true redirect |
| GET | `/dancers` | genres(반복 가능) | 댄서 목록 HTML |
| GET | `/dancers/{id}` | id | 프로필 HTML 또는 dancers redirect |
| GET | `/my-page`, `/me` | 없음 | 내 프로필 HTML |
| GET | `/admin` | 없음 | 관리자 HTML, ADMIN |

## 게시글·댓글·반응

| Method | Endpoint | 입력 | 처리 |
|---|---|---|---|
| GET | `/posts/new` | board(선택) | 작성 폼 |
| POST | `/posts/new` | PostCreateRequest | 생성 → 상세 redirect |
| GET | `/posts/{id}/edit` | id | 수정 폼, 작성자/관리자 검사 |
| POST | `/posts/{id}/edit` | id, PostCreateRequest | 수정 → 상세 redirect 또는 오류 폼 |
| POST | `/posts/{id}/delete` | id | 삭제 → boards/all redirect |
| POST | `/posts/{id}/comments` | content | 댓글 → 상세 redirect |
| POST | `/posts/{postId}/comments/{commentId}/delete` | 두 id | 작성자/관리자 댓글 삭제 |
| POST | `/posts/{id}/like` | id | 좋아요 토글 → 상세 redirect |
| POST | `/posts/{id}/save` | id | 저장 토글 → 상세 redirect |
| POST | `/posts/{id}/report` | reason(선택) | 신고 → 상세/보드 redirect |

PostCreateRequest 필드:

| 필드 | 규칙 |
|---|---|
| boardType | enum, 기본 SHOW, 필수 |
| title | 공백 불가, 최대 120자 |
| content | 공백 불가, 최대 5,000자 |
| tags | 최대 300자 |
| location | 최대 120자 |
| mediaUrl, thumbnailUrl | 각각 최대 300자 |
| mediaFile | MultipartFile, 선택 |
| eventDate, deadline | LocalDate |
| adminApprovedEvent | boolean, 실제 승인 권한은 Service 검사 |

CommentCreateRequest.content는 공백 불가·최대 500자다. 숨김/차단 상태와 소유권 때문에 로그인한 요청도 거부될 수 있다.

## 내 정보와 관리자

| POST Endpoint | 주요 form 필드 | 권한 / 응답 |
|---|---|---|
| `/my-page/profile` | displayName; crewName, primaryGenre, bio, instagramUrl, profileImageUrl, headerImageUrl(선택) | 본인 / my-page redirect |
| `/my-page/account` | username, currentPassword, newPassword(선택) | 본인 / my-page 또는 login redirect |
| `/my-page/portfolio/select` | postId, selected, returnTab=posts | 본인 게시글 / tab redirect |
| `/my-page/portfolio/pin` | postId, pinned, returnTab=portfolio | 본인 게시글 / tab redirect |
| `/my-page/joined-events` | eventDate, eventName, result | 본인 / joined-events redirect |
| `/my-page/joined-events/update` | eventId, eventDate, eventName, result | 본인 / joined-events redirect |
| `/my-page/joined-events/delete` | eventId | 본인 / joined-events redirect |
| `/admin/users/{id}/block` | blocked | ADMIN / admin redirect |
| `/admin/posts/{id}/visibility` | hidden | ADMIN / admin redirect |
| `/admin/posts/{id}/hype-approval` | approved | ADMIN / admin redirect |

## 근거

[Controller](../src/main/java/polytech/aisw/eom/controller/) · [DTO](../src/main/java/polytech/aisw/eom/dto/) · [SecurityConfig](../src/main/java/polytech/aisw/eom/config/SecurityConfig.java)
