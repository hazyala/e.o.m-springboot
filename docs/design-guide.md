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
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;800&family=Irish+Grover&family=Roboto+Mono&display=swap" rel="stylesheet">
```

용도:
- Irish Grover: 브랜드 로고, 큰 브랜드 타이틀
- Inter: 본문, UI, 버튼
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

### Login

- 기존 React 시안 최대한 동일
- 좌우 분할 또는 강한 이미지 배경
- 브랜드 무드와 로그인 폼의 대비

### Dashboard

- 로그인 후 댄서들의 아지트
- TODAY'S PICK
- POPULAR
- RECENT
- TAGS
- EVENTS
- 추천 댄서
- 활동 피드

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

