FROM amazoncorretto:21
COPY build/libs/observability-mashup.jar /mashup.jar
ENV JAVA_OPTS=""
ENTRYPOINT ["java", "-jar", "/mashup.jar"]