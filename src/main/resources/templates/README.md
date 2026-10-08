# E.O.M의 Thymeleaf 화면

Controller의 Model을 HTML로 렌더링한다. `fragments/`의 헤더·푸터를 화면들이 공유한다.

| 파일 | 사용자가 하는 일 |
|---|---|
| `index.html`, `login.html` | 커뮤니티 소개, 로그인·가입 진입 |
| `dashboard.html` | Today Pick·인기·최근 게시글과 행사·댄서 탐색 |
| `post-list.html` | SHOW·CAST·HYPE·LINK 목록, 정렬·태그·검색 |
| `post-create.html` | 게시글 작성·수정. 이미지/영상 파일과 Instagram 링크를 입력하고 Live Preview 확인 |
| `post-detail.html` | 본문·미디어 확인, 댓글·좋아요·저장·신고 |
| `my-page.html` | 본인 또는 작성자 프로필, 포트폴리오·참여 행사 |
| `dancers.html`, `dancer-detail.html` | 댄서 목록과 프로필 진입 |
| `admin.html` | 사용자·게시글·행사 관리 |

폼은 Spring MVC endpoint로 제출한다. Live Preview와 테마 같은 브라우저 동작은 [static](../static/README.md)에 있다. 화면의 저장·권한 검사는 [Controller](../../java/polytech/aisw/eom/controller/README.md)와 Service가 처리한다.
