# ---------- Stage 1: Build the JAR ----------
FROM maven:3.9.6-eclipse-temurin-21 AS build
#Create NON Root User
#RUN addgroup -r appgroup && useradd -r -g appgroup appuser
WORKDIR /mybuilds
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:resolve dependency:resolve-plugins
COPY src ./src
RUN mvn clean package -DskipTests

# Extract layered JAR structure
RUN java -Djarmode=layertools -jar target/*.jar extract

# ---------- Stage 2: Run the JAR ----------
#FROM eclipse-temurin:21-jre-alpine
FROM gcr.io/distroless/java21-debian12
WORKDIR /mybuilds
COPY --from=build /mybuilds/dependencies/ ./
COPY --from=build /mybuilds/snapshot-dependencies/ ./
COPY --from=build /mybuilds/spring-boot-loader/ ./
COPY --from=build /mybuilds/application/ ./

#Run as non root user
# Run as non-root user provided by Distroless
USER nonroot
#EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]

