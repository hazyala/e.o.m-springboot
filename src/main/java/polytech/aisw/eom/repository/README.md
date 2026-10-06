# JPA 조회와 사용자별 활동 연결

Spring Data JPA 인터페이스로 게시글, 사용자, 댓글, 좋아요·저장, 참여 행사를 조회한다.

- [PostRepository](PostRepository.java): 보드·정렬·태그와 통합 검색. 통합 검색은 제목·본문·태그·작성자 이름·크루 이름을 조회한다. 작성자 `EntityGraph`를 적용한다.
- [UserRepository](UserRepository.java): 로그인 아이디 조회와 차단되지 않은 USER 목록.
- [CommentRepository](CommentRepository.java): 게시글의 댓글, 사용자가 작성한 댓글과 게시글 삭제 시 댓글 정리.
- [PostLikeRepository](PostLikeRepository.java)·[PostSaveRepository](PostSaveRepository.java): 게시글/사용자 조합 조회와 사용자의 활동 목록. 활동 목록에는 게시글·작성자를 함께 로드한다.
- [JoinedEventRepository](JoinedEventRepository.java): 사용자의 참여 행사를 행사일·생성일 내림차순으로 조회한다.

조회 목록은 [DashboardService](../service/DashboardService.java)와 [MyPageService](../service/MyPageService.java)가 화면 데이터로 조립한다. 쓰기 흐름의 트랜잭션은 Service에서 관리한다.
