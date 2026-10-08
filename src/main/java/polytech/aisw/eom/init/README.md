# 데모 계정과 커뮤니티 데이터 생성

[DataSeeder](DataSeeder.java)는 Spring Boot 시작 시 실행되는 `CommandLineRunner`다. `UserRepository.count()`가 0일 때 사용자와 SHOW·CAST·HYPE·LINK 게시글을 저장한다. 사용자가 이미 있으면 초기화를 건너뛴다.

`admin / admin`, `dancer1 / 1234`를 비롯한 데모 계정을 만들며 비밀번호는 `PasswordEncoder`로 인코딩한다. 게시글에는 태그·장소·외부 미디어 URL·썸네일과 행사 날짜를 넣는다. 행사 날짜는 실행일의 `LocalDate.now()`를 기준으로 계산한다.

별도 seed 명령이나 local 전용 `@Profile` 제한은 없다. 실행 profile의 DB가 비어 있으면 이 초기화가 적용된다. DB 선택과 실행은 [루트 README](../../../../../../../README.md)를 따른다.
