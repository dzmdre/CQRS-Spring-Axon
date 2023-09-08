FROM eclipse-temurin:17-jdk-jammy
ADD target/zipkinService.jar zipkinService.jar
ENTRYPOINT ["java", "-jar", "/zipkinService.jar"]
EXPOSE 8761
