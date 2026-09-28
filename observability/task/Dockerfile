FROM amazoncorretto:21
COPY build/libs/observability-task.jar /task.jar
ENV JAVA_OPTS=""
ENTRYPOINT ["java", "-jar", "/task.jar"]