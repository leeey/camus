FROM amazoncorretto:17.0.8
ADD /build/libs/mashup.jar /mashup.jar
ENV JAVA_OPTS=""
ENTRYPOINT ["java", "-jar", "/mashup.jar"]