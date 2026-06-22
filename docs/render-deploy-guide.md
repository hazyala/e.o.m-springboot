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
JAVA_VERSION=21
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://HOST/DB_NAME?sslmode=require
SPRING_DATASOURCE_USERNAME=...
SPRING_DATASOURCE_PASSWORD=...
CLOUDINARY_URL=cloudinary://API_KEY:API_SECRET@CLOUD_NAME
CLOUDINARY_FOLDER=eom-posts
MEDIA_MAX_FILE_SIZE_BYTES=52428800
MEDIA_MAX_PART_COUNT=50
```

`CLOUDINARY_URL` 대신 `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`를 각각 넣어도 됩니다.

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
- Neon PostgreSQL
- `prod` profile

Neon Connection string이 `postgresql://USER:PASSWORD@HOST/DB_NAME?sslmode=require` 형태라면 Render에는 `jdbc:postgresql://HOST/DB_NAME?sslmode=require`를 `SPRING_DATASOURCE_URL`로 넣고, `USER`와 `PASSWORD`는 각각 `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`로 분리합니다.

## 6. 미디어 업로드

- 이미지와 영상 첨부는 Cloudinary Free를 사용합니다.
- 파일 크기는 기본 50MB 이하로 제한합니다.
- Spring multipart 요청 크기는 60MB로 설정되어 있습니다.
- Tomcat multipart part count는 `MEDIA_MAX_PART_COUNT`로 조절하며 기본값은 50입니다. 이 값이 너무 낮으면 게시글 작성 시 `FileCountLimitExceededException`으로 500 오류가 날 수 있습니다.

## 7. 배포 전 체크리스트

- [ ] `./gradlew clean test` 성공
- [ ] `./gradlew clean build` 성공
- [ ] `JAVA_VERSION=21` 설정
- [ ] `SPRING_PROFILES_ACTIVE=prod` 설정
- [ ] Neon PostgreSQL 환경변수 설정
- [ ] Cloudinary 환경변수 설정
- [ ] 관리자 계정 시드 또는 초기 생성 방식 확인
- [ ] 정적 이미지 용량 확인
- [ ] 게시글 이미지/영상 첨부 후 상세 화면 확인
- [ ] 로그인, 대시보드, 관리자 페이지 확인

## 8. 무료 티어 주의

- 콜드 스타트가 발생할 수 있습니다.
- Neon과 Cloudinary 무료 플랜 정책 변경 가능성을 확인해야 합니다.
- 발표 전에는 로컬 실행 백업을 준비합니다.

