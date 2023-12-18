# Build stage
FROM openjdk:17-jdk-buster as build

WORKDIR /workspace/app

COPY . /workspace/app

RUN apt-get update && apt-get -y install findutils
RUN chmod +x ./gradlew
RUN ./gradlew clean build

# Run stage
FROM openjdk:17-jdk

WORKDIR /app

COPY --from=build /workspace/app/build/libs/cs_skin_system-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]