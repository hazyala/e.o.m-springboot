# E.O.M Design Guide

## 1. 디자인 방향

E.O.M은 스트릿 댄서 커뮤니티입니다. 로그인 전 화면은 브랜드의 첫인상을 강하게 보여주고, 로그인 후 화면은 댄서들의 아지트처럼 느껴져야 합니다.

키워드:
- street
- dynamic
- hideout
- image-heavy
- bold typography
- Instagram culture
- dark first

## 2. 절대 원칙

- index와 login은 기존 React 시안에 최대한 가깝게 구현합니다.
- 대시보드 이후 화면은 index/login의 폰트, 컬러, 이미지 톤을 가져갑니다.
- Figma는 레이아웃 구조 참고용입니다.
- 기본값은 다크 모드입니다.
- 라이트/다크 모드는 필수입니다.
- 새 폰트와 새 컬러를 임의로 늘리지 않습니다.

## 3. 폰트

Google Fonts:

```html
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&family=Irish+Grover&family=Roboto:wght@700&family=Roboto+Mono&display=swap" rel="stylesheet">
```

용도:
- Irish Grover: 브랜드 로고, 큰 브랜드 타이틀
- Inter: 본문, UI, 버튼
- Roboto: SHOW/CAST/HYPE/LINK 대형 섹션 헤드라인
- Roboto Mono: 숫자, 메타 정보, 카운트, 타임스탬프

## 4. 컬러 시스템

색상은 CSS 변수로만 관리합니다.

### Dark

```css
--bg-color:#201E1E;
--main-bg-color:#121212;
--section-bg-color:#1a1a1a;
--header-bg:rgba(0,0,0,0.3);
--text-color:#ffffff;
--sub-text-color:#cccccc;
--primary-color:#ffffff;
--secondary-color:#ff4d6d;
--border-color:rgba(255,255,255,0.2);
--shadow-color:rgba(255,255,255,0.1);
```

### Light

```css
--bg-color:#ffffff;
--main-bg-color:#f0f0f5;
--section-bg-color:#f5f5f5;
--header-bg:rgba(255,255,255,0.7);
--text-color:#000000;
--sub-text-color:#555555;
--primary-color:#121212;
--secondary-color:#f33a6a;
--border-color:rgba(0,0,0,0.2);
--shadow-color:rgba(0,0,0,0.1);
```

### Section Accent

```css
--accent-show:#68a063;
--accent-cast:#a06380;
--accent-hype:#a08263;
--accent-link:#6370a0;
--accent-shadow-show:#68a063;
--accent-shadow-cast:#a06380;
--accent-shadow-hype:#a08263;
--accent-shadow-link:#6370a0;
```

## 5. 이미지 운영

이미지 원본 위치:

```text
src/main/resources/static/assets/source
```

사용 기준:
- 히어로와 로그인은 강한 첫인상을 주는 이미지를 사용합니다.
- 대시보드는 썸네일, 대표 이미지, 랭킹 카드 등 이미지 비중을 높입니다.
- 보드 목록은 텍스트 리스트보다 이미지 카드 중심으로 구성합니다.
- 인스타그램 문화와 이어지도록 프로필/게시글에 시각적 이미지 영역을 둡니다.
- E.O.M은 댄서 플랫폼이므로 영상, 릴스, 인스타그램 게시물 URL 기반 콘텐츠를 핵심으로 둡니다.
- MVP에서는 직접 영상 파일 업로드를 지원하지 않고 URL/임베드 기반으로 처리합니다.
- 목록 화면은 `thumbnailUrl`을 카드 이미지로 사용합니다.
- 상세 화면은 이후 `mediaType + mediaUrl`로 embed 또는 링크 폴백을 구현합니다.
- 인스타그램 URL은 `https://www.instagram.com/hazyala?igsh=ZW1maGFzNHQzdzEx&utm_source=qr` 계정과 해당 계정 내 확인 가능한 실제 릴스/게시물 URL을 기준으로 하며, 정확한 릴스/게시물 URL을 확인한 경우 해당 URL을 사용하고 확인 전에는 프로필 URL을 fallback으로 사용한 뒤 실제 URL로 교체 예정입니다.

현재 index/login에서 사용하는 이미지:
- `hero.jpg`: index 첫 히어로 배경
- `background.jpg`: index 전역 fixed 배경 이미지
- `show.png`: SHOW 섹션 원형 이미지
- `cast1.jpg` ~ `cast4.jpg`: CAST 섹션 롤링 카드
- `hype.jpg`: HYPE 섹션 이미지
- `link.png`: LINK 섹션 중앙 배경 이미지
- `login.jpg`: 로그인 화면 좌측 비주얼 배경

주의:
- 현재 원본 이미지는 해상도가 큽니다.
- 실제 배포 전에는 화면별 대표 이미지를 선별하고 압축본을 따로 만드는 것이 좋습니다.
- `.DS_Store`는 복사하지 않습니다.

## 6. 화면별 방향

### Index

- 기존 React 시안 최대한 동일
- 강한 브랜드 타이포
- 큰 히어로 카피
- SHOW/CAST/HYPE/LINK 섹션 소개
- 현재 Spring Thymeleaf 구조로 이식 완료
- 히어로 타이핑 효과, 섹션 스크롤, CAST 롤링 카드, fixed 배경 이미지 적용
- 푸터는 임시 문구 대신 E.O.M 서비스 링크 구조로 정리

### Login

- 기존 React 시안 최대한 동일
- 좌우 분할 또는 강한 이미지 배경
- 브랜드 무드와 로그인 폼의 대비
- 현재 Spring Security 로그인 폼과 연결 완료
- 회원가입 슬라이드 UI는 시각만 유지하며 실제 가입 기능은 다음 단계에서 연결

### Dashboard

- 로그인 후 댄서들의 아지트
- 현재 구현은 `dashboard.html` + `app.css` 대시보드 전용 클래스로 구성합니다.
- Figma 목업 기준의 라이트 대시보드로 구성합니다.
- Header는 index의 로고/네비 톤을 따르되 중앙 검색창, Add New, Logout, 테마 토글을 배치합니다.
- TODAY'S PICK은 전체 폭 이미지 히어로와 큰 E.O.M 타이포를 중심으로 보여줍니다.
- 히어로에는 `assets/source`의 감도 높은 이미지를 우측에서 좌측으로 밀리는 슬라이드 배경 레이어로 사용하며, 이후 실제 게시글 미디어 셔플로 교체할 수 있습니다.
- 히어로 CTA는 외부 미디어 링크가 아니라 내부 게시글 상세로 진입하는 `VIEW POST` 성격으로 둡니다.
- 본문은 Popular/Recent 리스트와 Tags/Events/Dancers/Activity 사이드 컬럼으로 구성합니다.
- Recent는 `SHOW/CAST/HYPE/LINK` 탭을 클라이언트에서 전환하며 선택된 보드의 최신 글 10개를 보여줍니다.
- 모든 대시보드 리스트는 미리보기 개수를 제한해 데이터 수와 무관하게 화면 끝선이 흐트러지지 않게 합니다. 현재 기준은 Popular 5, Recent 10, Tags 8, Activity 5, Events 4, Dancers 4입니다.
- Tags는 태그 검색 결과, Activity는 최신글 목록, Events는 이번 달 HYPE 공식 행사 목록, Dancers는 장르별 댄서 탐색 페이지로 이동합니다.
- 미디어는 게시글의 `mediaType + mediaUrl + thumbnailUrl`을 유지하고, 대시보드에서는 작은 썸네일과 영상 표시 아이콘으로만 노출합니다.
- Instagram/외부 미디어 링크가 상세/프로필 영역에 노출될 경우 새 탭으로 열고, 직접 영상 업로드 UI는 제공하지 않습니다.

### Board

- 큰 섹션 타이틀
- 최신순/인기순/댓글순 정렬
- 대표 이미지 카드 그리드
- 보드별 액센트 컬러

### My Page

- 프로필 헤더
- 인스타그램 링크
- 활동 통계
- 내가 쓴 글
- 포트폴리오 그리드

### Post Detail

- 태그
- 제목
- 작성자
- 대표 이미지
- 본문
- 인스타그램 바로보기 또는 링크
- 댓글/좋아요 영역

### Write Post

- 보드 선택
- 제목/본문/인스타그램 URL
- 대표 이미지 URL
- 우측 라이브 프리뷰

### Admin

- 디자인은 기능성이 우선입니다.
- 다크 톤은 유지하되 읽기 쉬운 테이블 중심으로 구성합니다.
