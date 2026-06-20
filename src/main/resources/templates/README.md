# templates

Thymeleaf 화면 파일을 둡니다. 공통 헤더와 푸터는 `fragments/`로 분리합니다.

현재 화면:
- `index.html`, `login.html`: 공개 랜딩/로그인 화면. 전용 디자인 톤을 유지합니다.
- `dashboard.html`: 로그인 후 첫 화면.
- `post-list.html`: `/boards/*`, `/posts` 공용 탐색 화면. `/events`는 HYPE 관리자 승인 행사 필터로 리다이렉트합니다.
- `post-detail.html`: 게시글 상세와 외부 미디어 새 탭 링크.
- `my-page.html`: 본인 마이페이지와 공개 작성자 프로필 화면.
- `dancers.html`, `dancer-detail.html`: 댄서 탐색/프로필 진입.
- `admin.html`: 관리자 조회 화면.
