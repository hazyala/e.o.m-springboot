# 커뮤니티 작업과 화면 데이터 조립

Controller에서 전달한 로그인 사용자·폼 값을 받아 DB 조회·수정과 미디어 업로드를 처리한다.

| Service | 구현 내용 |
|---|---|
| [AuthService](AuthService.java) | 가입 필수 입력·아이디 중복·비밀번호 확인 검사, BCrypt 저장 |
| [CommunityService](CommunityService.java) | 게시글·댓글 CRUD, 좋아요·저장·신고, 작성자/관리자 검사, 숨김·차단 필터, 태그·통합 검색 |
| [DashboardService](DashboardService.java) | Today Pick, 인기·최근 게시글, 행사·댄서·태그 목록 |
| [CloudinaryMediaStorageService](CloudinaryMediaStorageService.java) | 이미지·영상 파일 업로드, 게시글에 쓸 미디어 URL·썸네일 URL 반환 |
| [MyPageService](MyPageService.java) | 프로필·계정 변경, 포트폴리오 선택·고정, 참여 행사 CRUD, 활동 이력 |
| [AdminService](AdminService.java) | 사용자 차단/해제, 게시글 숨김/복구, HYPE 행사 승인/취소 |

## 게시글을 저장하는 흐름

`CommunityService`는 로그인 사용자를 작성자로 연결하고 `PostCreateRequest`의 보드·본문·일정 값을 `Post`에 반영한다. 첨부 파일은 Cloudinary 업로드 URL로 저장하며 Instagram 링크는 별도 미디어 입력으로 처리한다. HYPE 관리자 승인 표시에는 ADMIN 작성자 조건을 적용한다.

조회·변경 메서드에 `@Transactional`을 사용한다. 빈 검색어는 빈 목록으로 반환한다. DTO 검사와 Service의 사용자·권한 검사가 함께 폼 요청을 처리한다.
