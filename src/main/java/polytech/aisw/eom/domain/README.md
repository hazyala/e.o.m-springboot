# 사용자·게시글과 참여 데이터

커뮤니티 데이터를 JPA entity로 표현한다. `BoardType`은 SHOW·CAST·HYPE·LINK, `UserRole`은 USER·ADMIN을 구분한다.

| 모델 | 보관하는 데이터와 관계 |
|---|---|
| [AppUser](AppUser.java) | 로그인 아이디·비밀번호 hash, 표시 이름·프로필·장르·크루, 권한·차단 상태 |
| [Post](Post.java) | 작성자, 보드·제목·본문·태그·장소, 미디어 URL·썸네일, 행사일·마감일, 조회·좋아요·댓글 수, 포트폴리오 선택·고정, 승인·숨김·신고 상태 |
| [Comment](Comment.java) | 게시글과 작성자를 참조하는 댓글·작성 시각 |
| [PostLike](PostLike.java), [PostSave](PostSave.java) | 사용자와 게시글을 연결하는 좋아요·저장. 사용자/게시글 조합의 unique 제약 |
| [JoinedEvent](JoinedEvent.java) | 사용자별 참여 행사 이름·날짜·결과 |

게시글·댓글·좋아요·저장 모델은 사용자 또는 게시글을 `ManyToOne`으로 참조한다. 첨부 바이너리는 DB entity에 넣지 않고 [Cloudinary 업로드](../service/CloudinaryMediaStorageService.java) 결과 URL을 보관한다.
