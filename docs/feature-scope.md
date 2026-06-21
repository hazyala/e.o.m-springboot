# E.O.M Feature Scope

## MVP 필수

- 사용자 로그인
- 관리자 로그인
- 로그아웃
- 관리자 페이지
- SHOW/CAST/HYPE/LINK 게시판
- 게시글 목록
- 게시글 상세
- 대시보드
- 마이페이지
- 검색
- HYPE 관리자 승인 행사 필터
- 인스타그램 프로필 링크
- 게시글 인스타그램 릴스/게시물 URL
- 유튜브 URL 또는 외부 영상 URL 기반 미디어 첨부
- 목록용 `thumbnailUrl` 카드 이미지
- 라이트/다크 모드
- Render 배포 가능 설정

## 현재 완료

- Spring Boot 기본 구조
- 사용자/관리자 로그인 기본 흐름
- 관리자 페이지 기본 조회
- 라이트/다크 모드 기본 토글
- React 원본 기준 index 화면 이식
- React 원본 기준 login 화면 이식
- index fixed 배경 이미지와 주요 섹션 이미지 적용
- 디자인 후보 이미지 `assets/source` 보관
- 대시보드용 댄서/크루/행사/미디어 더미 데이터 보강
- `MediaType` 기반 URL/임베드 확장 구조 추가
- 제공받은 실제 인스타그램 릴스/게시물 URL 16개를 `DataSeeder` 미디어 더미 데이터에 반영
- Figma 목업 기반 대시보드 UI 개선: 검색형 헤더, Today Pick 히어로, Popular/Recent 리스트, Tags/Events/Dancers/Activity 사이드 컬럼
- 대시보드 전체 미리보기 개수 제한 및 Today Pick 히어로 배경 이미지 슬라이드 연출
- 대시보드 `SHOW/CAST/HYPE/LINK` Recent 탭 클라이언트 전환
- 대시보드 링크 목적지 연결: 게시글 상세, 태그 검색, 최신글 Activity, HYPE 관리자 승인 행사 필터, Dancers 디렉터리
- `/boards/all`, `/boards/SHOW`, `/boards/CAST`, `/boards/HYPE`, `/boards/LINK` 보드 탐색 목록
- `/posts/{id}` 게시글 상세와 외부 미디어 새 탭 링크
- `/posts/{id}` 댓글 작성/목록/본인 댓글 삭제
- `/posts?q=` 통합 검색: tags, title, content, author.displayName, author.crewName
- `/posts?tag=` 태그 검색 호환 경로
- 빈 `/posts`와 빈 검색어에서 전체 목록 노출 방지
- 검색 화면 목록형 결과와 추천 태그 사이드 컬럼
- HYPE 보드 관리자 승인 행사 필터(`/boards/HYPE?officialEvents=true`)
- `/dancers`와 `/dancers/{id}` 작성자/댄서 프로필 이동
- `/dancers` 다중 선택 장르 필터와 인재 카드형 댄서 비교 UI
- `/my-page`, `/me` 마이페이지
- 프로필 편집, 계정 아이디/비밀번호 변경, 로그아웃 후 재로그인 흐름
- 포트폴리오 선택과 최대 3개 상단 고정
- Joined Events 추가/수정/삭제
- 좋아요/댓글/활동 이력 목록 조회
- `/posts/new` 게시글 작성 폼
- `/posts/{id}/edit` 작성자 본인 게시글 수정
- `/posts/{id}/delete` 작성자 본인 또는 ADMIN 게시글 삭제
- 게시글 상세 권한별 Edit/Delete 액션 노출과 삭제 확인 UX
- 작성 폼 좌측 입력/우측 Live Preview 2컬럼 UI
- 보드, 제목, 본문, 태그, 위치, Instagram/외부 미디어 URL, `thumbnailUrl`, 일정 입력 저장
- admin 전용 HYPE 관리자 승인 행사 체크 저장

## 게시글 운영 정책

- 게시글 수정은 작성자 본인만 가능합니다. ADMIN도 작성자 본인이 아닌 글은 수정할 수 없습니다.
- 게시글 삭제는 작성자 본인 또는 ADMIN만 가능합니다.
- 상세 화면은 권한이 있는 사용자에게만 Edit/Delete 액션을 노출하고, 삭제는 확인 후 `POST /posts/{id}/delete`로 처리합니다.
- 수정/삭제 권한 정책은 Controller에 분산하지 않고 Service 계층에서 관리합니다.
- 댓글 목록은 오래된순으로 표시해 대화 흐름을 위에서 아래로 읽게 합니다.
- 댓글 작성은 로그인 사용자만 가능하며 1자 이상 500자 이하로 제한합니다.
- 댓글 삭제는 작성자 본인만 가능하고, ADMIN 삭제 예외는 이번 범위에서 제외합니다.
- 댓글 작성/삭제 시 게시글 `commentCount`를 증감하고, 상세 화면의 댓글 수는 실제 조회된 댓글 목록 기준으로 표시합니다.
- 좋아요는 로그인 사용자별로 게시글당 1개만 허용하며, 상세 화면에서 다시 누르면 취소합니다.
- 좋아요 생성/취소 시 `post.likeCount`를 증감하고, 취소 시 0 아래로 내려가지 않게 보호합니다.
- 저장은 사용자별 개인 북마크입니다. 공개 카운트는 노출하지 않고, 저장 여부만 상세 화면과 마이페이지 Saves 탭에 표시합니다.

## SHOULD

가능하면 구현합니다.

- 이미지 URL 유효성 검증
- 외부 미디어 URL 유효성 검증

## 나중에

- 인스타그램 게시물 embed.js 바로보기
- 게시글 작성 경험 고도화
- 알림
- 검색 하이라이트

3일 MVP에서는 제외합니다.

- 인스타그램 Graph API
- 실시간 채팅
- DM
- 알림
- 결제
- 파일 업로드 서버
- 직접 이미지 파일 업로드
- 직접 영상 파일 업로드
- 외부 미디어 직접 embed
- 고급 관리자 권한
- 추천 알고리즘

## 미디어 정책

- E.O.M은 댄서 플랫폼이므로 영상/릴스 중심 콘텐츠를 핵심으로 둡니다.
- MVP에서는 직접 영상 업로드를 지원하지 않습니다.
- 게시글은 인스타그램 릴스/게시물 URL, 유튜브 URL, 외부 영상 URL을 첨부하는 방식으로 처리합니다.
- 작성 폼은 `/posts/new`이며 헤더 `Add New` 또는 보드 목록 우측 `New Post`에서 진입합니다. 보드별 `New Post`는 현재 보드를 자동 선택하고, 저장 후 생성된 `/posts/{id}` 상세로 이동하며 해당 보드 목록에서도 작성 글을 확인할 수 있습니다.
- 작성 폼은 파일 입력 없이 Instagram 게시물 링크 또는 외부 미디어 URL과 `thumbnailUrl` 문자열만 받습니다.
- SHOW 외 보드는 `eventDate`/`deadline` 입력 영역을 제공하고, HYPE 관리자 승인 행사 체크는 admin에게만 노출/저장됩니다.
- Live Preview는 입력값만 보여주며 실제 embed는 하지 않습니다. 입력 전 가짜 미디어/placeholder는 노출하지 않고, `thumbnailUrl`이 있을 때만 큰 미디어 프레임을, Instagram/외부 `mediaUrl`이 있을 때만 새 탭 링크 카드를 보여줍니다.
- 목록은 `thumbnailUrl`을 카드 이미지로 사용하고, 상세는 `thumbnailUrl` 중심 프리뷰와 외부 미디어 표시로 구성합니다.
- 현재 대시보드와 커뮤니티 탐색의 미디어 표시는 `thumbnailUrl`을 썸네일로 사용하고 `mediaType`, boardType, 작성자/크루, 위치, 행사일, 조회/좋아요/댓글 수를 리스트 메타로 노출합니다.
- 히어로 배경 슬라이드는 현재 `assets/source` 이미지로 연출하며, 이후 게시글 미디어 기반 슬라이드로 확장 가능합니다.
- 대시보드와 목록의 게시글 썸네일은 외부 링크 버튼이 아니라 내부 게시글 미리보기이며, 외부 미디어 링크는 상세 화면에서만 새 탭으로 엽니다.
- 전체 탐색 URL은 `/boards/all`, 보드별 탐색 URL은 `/boards/SHOW`, `/boards/CAST`, `/boards/HYPE`, `/boards/LINK`입니다.
- 공개 index의 SHOW/CAST/HYPE/LINK CTA는 비로그인 상태에서는 `/login`, 로그인 상태에서는 각 `/boards/{board}`로 이동합니다.
- Header 검색은 `/posts?q={query}`에서 `tags`, `title`, `content`, `author.displayName`, `author.crewName`을 통합 검색합니다. `/posts?tag={tag}`는 태그 클릭 호환 경로로 유지하며 같은 검색 결과 화면을 사용합니다.
- 대시보드 Tags의 `ALL`은 `/posts` 검색 화면으로 이동합니다.
- 빈 `/posts` 또는 빈 검색어는 전체 목록으로 흐르지 않고 검색 입력과 추천 태그를 보여줍니다.
- 전체/보드/검색/태그 목록은 기본 최신순이며 `sort=latest|views|comments|likes`로 최신순, 조회순, 댓글순, 좋아요순 정렬을 지원합니다. HYPE 보드는 같은 정렬 줄에서 최신순 고정 `관리자 승인 행사` 뷰를 함께 제공합니다.
- Recent는 선택된 보드의 최신 글 10개를 보여주고, Tags 8개, Activity 5개, Events 4개, Dancers 4개로 대시보드 미리보기 개수를 제한합니다.
- Activity 미리보기는 Recent 탭 선택과 무관하게 전체 최신글 기준으로 유지하고, `ALL`은 `/boards/all`로 이동합니다.
- Events 미리보기는 관리자 승인 행사 중 이번 달 예정 HYPE 글을 보여주고, `ALL`은 `/boards/HYPE?officialEvents=true`로 이동합니다. 목록에는 `eventDate`, `deadline`, `location`, `boardType`, `mediaType`, `thumbnailUrl`, 제목, 본문 미리보기, `author.displayName`을 노출합니다.
- Dancers는 USER 역할 사용자를 댄서 카드로 보여주는 탐색 화면으로 둡니다. `All`, `Hip-hop`, `House`, `Krump`, `Popping`, `Locking`, `Breaking`, `Waacking`, `Voguing`, `Dancehall` 필터를 제공하고, 여러 장르를 선택하면 선택 장르 중 하나라도 `primaryGenre`에 매칭되는 댄서를 보여줍니다.
- Dancers 카드에는 동일 비율 프로필 이미지, displayName, username, primaryGenre, crewName, bio, Instagram 새 탭 링크, 기존 `/dancers/{id}` 프로필 보기 링크를 노출합니다.
- 마이페이지 기본 URL은 `/my-page`이며 `/me`도 같은 화면으로 연결합니다.
- 공통 헤더는 index 제외 화면에서 대시보드 톤을 유지하고, 인증 사용자의 버튼은 일반 화면에서 외곽선 없는 `My Page`, 마이페이지에서 같은 크기의 붉은 `Logout`으로 전환합니다.
- 로그인 상태 유지는 기본 체크 상태이며 remember-me 쿠키로 유지하고, 로그아웃 시 세션 쿠키와 remember-me 쿠키를 함께 삭제합니다.
- 마이페이지는 displayName, crewName, bio, instagramUrl, profileImageUrl, headerImageUrl, 주 장르를 보여주고 admin도 본인 마이페이지에 접근할 수 있습니다.
- 포트폴리오는 내가 쓴 게시글 중 선택된 항목으로 구성하며, 상단 고정은 최대 3개입니다.
- 탭 순서는 Portfolio, Posts, Events, Likes, Saves, Comments입니다.
- Posts 탭은 내가 쓴 전체 게시글을 카드형으로 보여주고 별 버튼으로 포트폴리오 포함 여부를 바꿉니다.
- Events 탭은 Joined Events 전체 목록과 입력 폼을 카드형으로 제공하고, 사이드바 Joined Events는 최신 참여 행사 일부만 보여줍니다.
- Likes, Saves, Comments 탭은 각각 좋아요한 게시글, 저장한 게시글, 내가 작성한 댓글을 목록형으로 제공합니다.
- Activity는 게시글/댓글 기반 최근 활동을 자동 표시합니다.
- 마이페이지 편집은 URL 기반 프로필/헤더 이미지 갱신만 제공하며 새 이미지 원본 추가, 직접 이미지 업로드, 직접 영상 업로드는 제외합니다.
- Instagram/외부 미디어 링크가 상세/프로필 영역에 노출될 경우 새 탭으로 열며, 파일 업로드나 직접 영상 저장 흐름은 제공하지 않습니다.
- 인스타그램 URL은 `https://www.instagram.com/hazyala?igsh=ZW1maGFzNHQzdzEx&utm_source=qr` 계정과 해당 계정 내 확인 가능한 실제 릴스/게시물 URL을 기준으로 합니다.
- 정확한 릴스/게시물 URL을 확인한 경우 해당 URL을 사용하고, 확인 전에는 프로필 URL을 fallback으로 사용한 뒤 실제 릴스/게시물 URL로 교체 예정입니다.
- 현재 시드 데이터는 확인된 실제 URL을 우선 사용하고, 개별 URL이 없는 항목만 프로필 URL을 fallback으로 사용합니다.

## 우선순위 기준

1. 발표 시나리오에 필요한가
2. 댄서 커뮤니티 문제 해결에 직접 연결되는가
3. 관리자/사용자 흐름에 영향을 주는가
4. Render 배포 가능성에 영향을 주는가
5. 디자인 완성도를 크게 높이는가
