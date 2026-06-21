# service

게시글, 대시보드, 관리자 기능 등 핵심 비즈니스 로직을 처리합니다.

현재 주요 Service:
- `DashboardService`: Today Pick, Popular, Recent, Tags, Events, Dancers 미리보기 조립
- `CommunityService`: 보드 목록, 게시글 작성, 신고 접수, 숨김/차단 콘텐츠 필터링, HYPE 관리자 승인 행사 필터, 태그 검색, 통합 검색, Dancers 조회
- `MyPageService`: 프로필/계정 수정, 포트폴리오 선택/고정, 참여 이벤트 CRUD, 활동 이력 조립
- `AdminService`: 관리자 조회 데이터 조립, 사용자 차단/해제, 게시글 숨김/복구, HYPE 행사 승인/취소

검색은 빈 검색어를 빈 리스트로 처리해 전체 목록으로 흐르지 않게 합니다.

게시글 작성은 `PostCreateRequest`를 받아 로그인 사용자를 작성자로 연결하고, Instagram/외부 미디어 URL과 `thumbnailUrl`만 저장합니다. HYPE 관리자 승인 행사는 ADMIN 작성자일 때만 반영합니다.
