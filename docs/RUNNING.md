# 실행 환경과 미디어 설정

## local / prod

`application.yml`은 기본 local profile을 선택한다. H2 in-memory DB를 만들고 시드 데이터를 넣는다. local 종료 시 schema가 제거된다. prod에서는 PostgreSQL과 아래 변수가 필요하다.

| 변수 | 역할 / 기본값 |
|---|---|
| `SPRING_PROFILES_ACTIVE` | prod DB를 사용할 때 `prod` |
| `SPRING_DATASOURCE_URL` | prod JDBC PostgreSQL URL |
| `SPRING_DATASOURCE_USERNAME` | prod DB 사용자 |
| `SPRING_DATASOURCE_PASSWORD` | prod DB 비밀번호 |
| `PORT` | 서버 포트, 기본 8080 |
| `CLOUDINARY_URL` | Cloudinary credential URL |
| `CLOUDINARY_CLOUD_NAME` | URL 대신 사용하는 cloud name |
| `CLOUDINARY_API_KEY` | URL 대신 사용하는 API key |
| `CLOUDINARY_API_SECRET` | URL 대신 사용하는 API secret |
| `CLOUDINARY_FOLDER` | 업로드 폴더, 기본 eom-posts |
| `MEDIA_MAX_FILE_SIZE_BYTES` | 서비스 파일 한도, 기본 52,428,800 bytes |
| `MEDIA_MAX_PART_COUNT` | Tomcat multipart part 한도, 기본 50 |

Cloudinary는 URL 또는 개별 세 값으로 설정한다. 업로드 요청이 없는 local 탐색과 파일 업로드를 구분한다. `.env`를 자동 로드하는 라이브러리는 선언되어 있지 않으므로 shell·IDE·배포 서비스에서 환경변수를 전달한다.

Servlet 설정의 max-file-size/max-request-size는 각각 60MB이고 서비스 단일 파일 한도는 기본 50MiB다. 같은 제한값이 아니다. 미디어 MIME·용량·credential 검사는 `CloudinaryMediaStorageService`가 수행한다.

## JAR

```bash
bash gradlew bootJar
java -jar build/libs/eom-springboot-0.0.1-SNAPSHOT.jar
```

JDK 21과 의존성 다운로드가 필요하다. 현재 `build.gradle`의 repository 누락으로 깨끗한 환경에서 resolve 실패가 발생할 수 있다. 소스를 변경하지 않는 범위에서 이 조건을 문서에 남긴다.

`main`에는 Java 21 multi-stage [Dockerfile](../Dockerfile)이 있다. 기존 README의 “Docker 없이 배포”는 배포 선택지였으며 저장소 구성과 구분한다.

prod 연결은 외부 PostgreSQL·Cloudinary 계정이 준비된 환경에서 확인해야 한다. Render/Neon 사용 기록은 배포 방식의 문서이며 현재 서비스 가동 여부를 증명하지 않는다.
