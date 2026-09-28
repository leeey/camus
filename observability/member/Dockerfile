FROM amazoncorretto:21
COPY build/libs/observability-member.jar /member.jar
ENV JAVA_OPTS=""
ENTRYPOINT ["java", "-jar", "/member.jar"]