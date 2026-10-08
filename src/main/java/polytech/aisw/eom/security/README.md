# DB 사용자와 Spring Security 연결

[CustomUserDetailsService](CustomUserDetailsService.java)가 로그인 아이디를 받아 `UserRepository.findByUsername`으로 `AppUser`를 찾는다. DB의 비밀번호 hash와 USER/ADMIN 역할을 Spring Security `UserDetails`에 전달한다. 차단된 계정은 `accountLocked`로 표시하며 없는 아이디는 `UsernameNotFoundException`으로 처리한다.

가입 시 비밀번호를 인코딩하는 코드는 [AuthService](../service/AuthService.java), URL별 접근·로그인·로그아웃 설정은 [SecurityConfig](../config/SecurityConfig.java)에 있다. 이 패키지에는 별도 실행 entry가 없고 Spring Boot가 Service bean을 주입한다.
