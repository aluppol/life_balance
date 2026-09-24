FROM eclipse-temurin:21-jdk@sha256:92a2a4d7a928d057e7bd999c418d66c26a34eb9a0442f3ab67721c3f88110b2d AS build
WORKDIR /src
COPY . .
# Layout-agnostic: builds whichever module applies the Spring Boot plugin and keeps the one jar that carries BOOT-INF/.
RUN --mount=type=cache,target=/root/.gradle ./gradlew --no-daemon bootJar -x test \
 && for jar in $(find . -path '*/build/libs/*.jar'); do if jar tf "$jar" | grep -q '^BOOT-INF/'; then echo "$jar"; fi; done > /tmp/boot-jars \
 && test "$(wc -l < /tmp/boot-jars)" -eq 1 \
 && mkdir /out && cp "$(cat /tmp/boot-jars)" /out/app.jar \
 && if [ -f application.yml ]; then cp application.yml /out/; fi

FROM eclipse-temurin:21-jre@sha256:49e21e16e3c86eb7816a44a67549910ed090fbeb40c29c525d58bf5e02e91b0f
WORKDIR /app
# Spring reads ./application.yml from the working directory when the repo keeps one at its root.
COPY --from=build /out/ /app/
USER 10001:10001
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=70", "-jar", "/app/app.jar"]
