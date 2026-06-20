# E.O.M Feature Scope

## MVP 필수

- 사용자 로그인
- 관리자 로그인
- 로그아웃
- 관리자 페이지
- SHOW/CAST/HYPE/LINK 게시판
- 게시글 목록
- 게시글 상세
- 게시글 작성
- 게시글 삭제
- 대시보드
- 마이페이지
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
- 대시보드 링크 목적지 연결: 게시글 상세, 태그 검색, 최신글 Activity, 이번 달 HYPE Events, Dancers 디렉터리

## SHOULD

가능하면 구현합니다.

- 댓글
- 좋아요
- 태그
- 검색
- 인기글 정렬
- 추천 댄서
- 행사 날짜 필드
- 상세 화면의 `mediaType + mediaUrl` 기반 새 탭 링크

## 나중에

- 인스타그램 게시물 embed.js 바로보기
3일 MVP에서는 제외합니다.

- 인스타그램 Graph API
- 실시간 채팅
- DM
- 알림
- 결제
- 파일 업로드 서버
- 직접 영상 파일 업로드
- 고급 관리자 권한
- 추천 알고리즘

## 미디어 정책

- E.O.M은 댄서 플랫폼이므로 영상/릴스 중심 콘텐츠를 핵심으로 둡니다.
- MVP에서는 직접 영상 업로드를 지원하지 않습니다.
- 게시글은 인스타그램 릴스/게시물 URL, 유튜브 URL, 외부 영상 URL을 첨부하는 방식으로 처리합니다.
- 목록은 `thumbnailUrl`을 카드 이미지로 사용하고, 상세는 `thumbnailUrl` 중심 프리뷰와 외부 미디어 표시로 구성합니다.
- 현재 대시보드와 커뮤니티 탐색의 미디어 표시는 `thumbnailUrl`을 썸네일로 사용하고 `mediaType`, boardType, 작성자/크루, 위치, 행사일, 조회/좋아요/댓글 수를 리스트 메타로 노출합니다.
- 히어로 배경 슬라이드는 현재 `assets/source` 이미지로 연출하며, 이후 게시글 미디어 기반 슬라이드로 확장 가능합니다.
- 대시보드와 목록의 게시글 썸네일은 외부 링크 버튼이 아니라 내부 게시글 미리보기이며, 외부 미디어 링크는 상세 화면에서만 새 탭으로 엽니다.
- 보드별 탐색 URL은 `/boards/SHOW`, `/boards/CAST`, `/boards/HYPE`, `/boards/LINK`입니다.
- 전체/보드/태그/Activity/Events 목록은 기본 최신순이며 `sort=latest|views|comments|likes`로 최신순, 조회순, 댓글순, 좋아요순 카드 정렬을 지원합니다.
- Recent는 선택된 보드의 최신 글 10개를 보여주고, Tags 8개, Activity 5개, Events 4개, Dancers 4개로 대시보드 미리보기 개수를 제한합니다.
- Activity 미리보기는 Recent 탭 선택과 무관하게 전체 최신글 기준으로 유지합니다.
- Events는 HYPE 중 `eventDate`가 오늘부터 이번 달 말까지인 공식 행사성 글을 보여주는 목록으로 둡니다.
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
