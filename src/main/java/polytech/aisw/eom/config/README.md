# 인증과 multipart 설정

Spring Security의 접근 규칙과 게시글 첨부 요청의 Tomcat 제한을 설정한다.

- [SecurityConfig](SecurityConfig.java): `/`, `/login`, `/signup`, 정적 파일과 H2 console을 공개하고 `/admin/**`에 ADMIN 권한을 요구한다. 나머지는 로그인 후 접근한다. form login 성공 시 `/dashboard`, 로그아웃 후 `/`로 이동한다.
- 비밀번호는 BCrypt로 인코딩한다. remember-me 기간은 14일이며 로그아웃 때 `JSESSIONID`·`remember-me` 쿠키를 삭제한다. CSRF는 H2 console만 예외로 둔다.
- [TomcatMultipartConfig](TomcatMultipartConfig.java): `app.media.max-part-count`를 Tomcat Connector에 적용한다. 기본값은 50이다.

사용자 로딩은 [security](../security/README.md), 첨부 업로드는 [service](../service/README.md)에서 처리한다.
