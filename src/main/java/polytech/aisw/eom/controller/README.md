# HTTP 요청에서 Thymeleaf 화면까지

Spring MVC Controller가 폼·query·경로 변수를 받아 Service를 호출하고 HTML 뷰 이름 또는 redirect를 반환한다.

| Controller | 화면과 처리 |
|---|---|
| [HomeController](HomeController.java) | `/`, `/login` |
| [AuthController](AuthController.java) | `/signup` 폼 입력과 가입 오류 처리 |
| [DashboardController](DashboardController.java) | `/dashboard`의 추천·최근 게시글·행사·댄서 |
| [CommunityController](CommunityController.java) | 보드·검색·상세, 게시글 작성/수정/삭제, 댓글·좋아요·저장·신고, 행사·댄서 탐색 |
| [MyPageController](MyPageController.java) | 본인/작성자 프로필, 계정·프로필 수정, 포트폴리오·참여 행사 |
| [AdminController](AdminController.java) | 사용자 차단, 게시글 숨김, HYPE 행사 승인 |

`GlobalNavigationAdvice`는 공통 Model의 `currentPath`에 요청 URI를 넣는다. 게시글 작성·수정은 DTO binding 오류나 업로드 오류가 나면 입력 폼을 다시 렌더링한다. 성공한 폼 요청은 상세 또는 목록으로 redirect한다. 상세 endpoint는 [API 문서](../../../../../../../docs/API.md)에 있다.
