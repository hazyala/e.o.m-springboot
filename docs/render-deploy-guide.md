# Render Deploy Guide

## 1. 배포 방식

Docker를 사용하지 않습니다. Render Web Service의 Java/Gradle 빌드 흐름을 사용합니다.

## 2. Render 서비스

서비스 유형:

```text
Web Service
```

빌드 명령:

```bash
./gradlew clean build
```

시작 명령:

```bash
java -jar build/libs/eom-springboot-0.0.1-SNAPSHOT.jar
```

## 3. 환경변수

```text
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=Render PostgreSQL external/internal URL
SPRING_DATASOURCE_USERNAME=...
SPRING_DATASOURCE_PASSWORD=...
```

## 4. 포트

Render는 `PORT` 환경변수를 제공합니다. 애플리케이션은 다음 설정을 유지합니다.

```yaml
server:
  port: ${PORT:8080}
```

로컬에서는 8080으로 실행되고, Render에서는 Render가 지정한 포트로 실행됩니다.

## 5. DB

로컬:
- H2
- `local` profile

배포:
- PostgreSQL
- `prod` profile

## 6. 배포 전 체크리스트

- [ ] `./gradlew clean test` 성공
- [ ] `./gradlew clean build` 성공
- [ ] `SPRING_PROFILES_ACTIVE=prod` 설정
- [ ] PostgreSQL 환경변수 설정
- [ ] 관리자 계정 시드 또는 초기 생성 방식 확인
- [ ] 정적 이미지 용량 확인
- [ ] 로그인, 대시보드, 관리자 페이지 확인

## 7. 무료 티어 주의

- 콜드 스타트가 발생할 수 있습니다.
- PostgreSQL 무료 플랜 정책 변경 가능성을 확인해야 합니다.
- 발표 전에는 로컬 실행 백업을 준비합니다.

