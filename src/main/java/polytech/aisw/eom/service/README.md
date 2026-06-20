# service

게시글, 대시보드, 관리자 기능 등 핵심 비즈니스 로직을 처리합니다.

현재 주요 Service:
- `DashboardService`: Today Pick, Popular, Recent, Tags, Events, Dancers 미리보기 조립
- `CommunityService`: 보드 목록, 태그 검색, 통합 검색, Events, Dancers 조회
- `MyPageService`: 프로필/계정 수정, 포트폴리오 선택/고정, 참여 이벤트 CRUD, 활동 이력 조립
- `AdminService`: 관리자 조회 데이터 조립

검색은 빈 검색어를 빈 리스트로 처리해 전체 목록으로 흐르지 않게 합니다.
