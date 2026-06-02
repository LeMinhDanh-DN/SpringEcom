FROM openjdk:27-ea-trixie
ADD target/SpringEcom.jar SpringEcom.jar
ENTRYPOINT ["java","-jar","SpringEcom.jar"]

# To build the Docker image, run the following command in the terminal:
# docker build -t spring-ecom -p 8081:8081 .