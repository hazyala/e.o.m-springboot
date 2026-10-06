# 게시글·댓글 폼 입력

Thymeleaf 폼을 Java 객체로 바인딩하고 Bean Validation으로 필수 입력과 길이를 검사한다.

| 객체 | 전달하는 값과 검사 |
|---|---|
| [PostCreateRequest](PostCreateRequest.java) | 보드, 제목·본문, 태그·위치, 미디어 URL·첨부 파일, 행사일·마감일·관리자 승인 표시. 제목 120자, 본문 5,000자 제한 |
| [CommentCreateRequest](CommentCreateRequest.java) | 댓글 본문. 빈 값 금지, 500자 제한 |

`PostCreateRequest.from(Post)`는 수정 폼에 게시글 값을 채운다. 파일 자체는 `MultipartFile`로 받고 업로드 뒤 반환된 URL을 `Post`에 저장한다. [CommunityController](../controller/CommunityController.java)는 binding 오류를 폼 화면으로 돌려보내고 [CommunityService](../service/CommunityService.java)에 저장을 맡긴다.
