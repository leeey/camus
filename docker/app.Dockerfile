# 공통 애플리케이션 이미지 (spring boot layered jar)
#
#   ./gradlew :hexagonal:bootJar
#   docker build -f docker/app.Dockerfile --build-arg JAR_FILE=hexagonal/build/libs/hexagonal.jar -t camus/task-service .
#
# 의존성 layer 와 애플리케이션 layer 를 나눠서, 코드만 바뀌면 application layer 만 다시 받는다.

FROM eclipse-temurin:21-jre-alpine AS extract
WORKDIR /workspace
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --launcher --destination extracted

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S -g 10001 app && adduser -S -u 10001 -G app app
WORKDIR /app
COPY --from=extract /workspace/extracted/dependencies/ ./
COPY --from=extract /workspace/extracted/spring-boot-loader/ ./
COPY --from=extract /workspace/extracted/snapshot-dependencies/ ./
COPY --from=extract /workspace/extracted/application/ ./
USER 10001:10001
# 컨테이너 메모리 한도의 75% 를 힙으로 쓰고, OOM 이면 바로 종료해 재시작되게 한다.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
