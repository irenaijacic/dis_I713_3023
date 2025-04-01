FROM openjdk:21 

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} reviewservice.jar

ENTRYPOINT ["java","-jar","/reviewservice.jar"]

EXPOSE 8084