FROM amazoncorretto:17.0.8
ADD /build/libs/task.jar /task.jar
ENV JAVA_OPTS=""
ENTRYPOINT ["java", "-jar", "/task.jar"]