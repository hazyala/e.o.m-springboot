# Neon + Cloudinary Free Setup

## Cloudinary

1. Cloudinary Console에서 `API Environment variable` 또는 `CLOUDINARY_URL` 값을 복사합니다.
2. Render Web Service 환경변수에 아래 값을 추가합니다.

```text
CLOUDINARY_URL=cloudinary://API_KEY:API_SECRET@CLOUD_NAME
CLOUDINARY_FOLDER=eom-posts
```

`CLOUDINARY_URL` 대신 아래처럼 3개로 나눠 넣어도 됩니다.

```text
CLOUDINARY_CLOUD_NAME=...
CLOUDINARY_API_KEY=...
CLOUDINARY_API_SECRET=...
CLOUDINARY_FOLDER=eom-posts
```

업로드 파일은 기본 50MB 이하의 이미지/영상만 허용합니다.

선택 환경변수:

```text
MEDIA_MAX_FILE_SIZE_BYTES=52428800
MEDIA_MAX_PART_COUNT=50
```

`MEDIA_MAX_PART_COUNT`는 게시글 작성 폼의 multipart 파트 개수 제한입니다. 기본값 50이면 현재 작성 폼에는 충분합니다.

## Neon

1. Neon에서 새 PostgreSQL 프로젝트를 만듭니다.
2. Connection string에서 JDBC URL 형태로 사용할 값을 준비합니다.
3. Render Web Service 환경변수에 아래 값을 추가합니다.

```text
JAVA_VERSION=21
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://HOST/DB_NAME?sslmode=require
SPRING_DATASOURCE_USERNAME=...
SPRING_DATASOURCE_PASSWORD=...
```

Neon에서 복사한 URL이 `postgresql://USER:PASSWORD@HOST/DB_NAME?sslmode=require` 형태라면,
`USER`와 `PASSWORD`를 분리하고 URL 앞을 `jdbc:postgresql://`로 바꿔 넣습니다.

## Render

Build command:

```bash
./gradlew clean build
```

Start command:

```bash
java -jar build/libs/eom-springboot-0.0.1-SNAPSHOT.jar
```

배포 전 확인:

```bash
./gradlew clean test
./gradlew clean build
```

Render에서 게시글 작성 시 500 오류가 나면 먼저 `CLOUDINARY_URL`, Neon DB 환경변수, `MEDIA_MAX_PART_COUNT` 값을 확인합니다.
