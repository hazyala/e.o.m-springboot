# 브라우저 스타일·스크립트·이미지

Thymeleaf가 렌더링한 페이지에서 실행하는 CSS와 JavaScript다. 별도 React/Vite 앱이나 npm 빌드 없이 Spring Boot의 정적 리소스로 제공한다.

- `css/app.css`: 로그인 후 커뮤니티 화면 스타일. `marketing.css`·`auth.css`는 랜딩·인증 화면 스타일이다.
- `js/post-create.js`: 보드 선택, 파일·Instagram 입력과 Live Preview를 동기화한다.
- `js/theme.js`: 테마 전환. `marketing.js`·`auth.js`: 해당 화면의 브라우저 동작.
- `assets/source/`: 랜딩·게시글·사용자 프로필에 쓰는 이미지. `DataSeeder`도 `/assets/source/...` URL을 사용한다.

미리보기는 브라우저에서 처리하고, 실제 파일 업로드와 게시글 저장은 서버 폼 제출 후 [Service](../../java/polytech/aisw/eom/service/README.md)가 처리한다.
