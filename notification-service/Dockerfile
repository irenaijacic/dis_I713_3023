FROM openjdk:21 

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} notificationservice.jar

ENTRYPOINT ["java","-jar","/notificationservice.jar"]

EXPOSE 8091