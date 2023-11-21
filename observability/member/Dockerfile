FROM amazoncorretto:17.0.8
ADD /build/libs/member.jar /member.jar
ENV JAVA_OPTS=""
ENTRYPOINT ["java", "-jar", "/member.jar"]