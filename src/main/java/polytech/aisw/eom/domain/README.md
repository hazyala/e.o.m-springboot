# domain

JPA 엔티티와 enum을 둡니다. E.O.M의 사용자, 게시글, 댓글, 좋아요 같은 핵심 모델을 표현합니다.

`Post`는 SHOW/CAST/HYPE/LINK 보드, URL 기반 미디어, `thumbnailUrl`, 일정, 포트폴리오 선택, HYPE 관리자 승인 행사 상태, 신고 수/사유, 관리자 숨김 상태를 보관합니다. `AppUser`는 USER/ADMIN 권한과 차단 상태를 함께 보관합니다. 직접 이미지/영상 파일 업로드 데이터는 도메인에 두지 않습니다.
