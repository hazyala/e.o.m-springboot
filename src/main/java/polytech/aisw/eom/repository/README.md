# repository

Spring Data JPA 저장소 인터페이스를 둡니다. 데이터 조회와 저장 책임만 가집니다.

`PostRepository`는 `@EntityGraph(attributePaths = "author")`를 사용해 Thymeleaf 렌더링 중 작성자 지연 로딩 문제가 생기지 않게 합니다. 통합 검색은 tags, title, content, author.displayName, author.crewName을 조회하고, 태그 검색은 `findByTagsContainingIgnoreCase`를 사용합니다.
