# E.O.M Docs

이 폴더는 E.O.M의 기획, 설계, 협업, 배포 문서를 둡니다.

운영 원칙:
- `docs/`는 `dev` 브랜치까지만 유지합니다.
- `main`에는 배포 가능한 코드와 루트 `README.md` 중심으로 둡니다.
- 구현이 바뀌면 관련 문서도 같은 작업 단위에서 업데이트합니다.

문서 목록:
- `PRD.md`: 제품 요구사항 명세서
- `architecture.md`: Spring Boot MVC 아키텍처와 설계 원칙
- `design-guide.md`: 디자인 시스템과 화면 구현 원칙
- `roadmap-3day.md`: 3일 완성/배포 로드맵
- `git-guide.md`: 브랜치, 커밋, PR 컨벤션
- `feature-scope.md`: MVP, SHOULD, 제외 기능 범위
- `render-deploy-guide.md`: Docker 없는 Render 배포 가이드

현재 문서 기준:
- Spring Boot MVC + Thymeleaf 구조를 유지합니다.
- Controller -> Service -> Repository -> Domain 흐름을 유지합니다.
- index/login 디자인 파일과 전용 CSS는 별도 요청 없이 수정하지 않습니다.
- 로그인 후 화면은 대시보드 헤더 톤을 공유합니다.
- 검색은 `/posts?q=` 통합 검색과 `/posts?tag=` 태그 검색을 지원하고, 빈 검색어는 전체 목록으로 흐르지 않습니다.
- `/events`는 기존 호환 경로이며 HYPE 관리자 승인 행사 필터(`/boards/HYPE?officialEvents=true`)로 이동합니다.
- 직접 영상 업로드, 직접 이미지 업로드, 외부 미디어 직접 embed는 제외합니다.
