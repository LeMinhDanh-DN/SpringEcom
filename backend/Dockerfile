FROM openjdk:27-ea-trixie
ADD target/SpringEcom.jar SpringEcom.jar
ENTRYPOINT ["java","-jar","SpringEcom.jar"]

# docker build -t spring-ecom -p 8081:8081 .