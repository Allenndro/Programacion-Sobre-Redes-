FROM eclipse-temurin:17-jdk AS compilacion
WORKDIR /proyecto
COPY src/ ./src/
RUN mkdir clases && javac --release 17 -encoding UTF-8 -d clases src/batallanaval/*.java

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=compilacion /proyecto/clases ./clases
USER 10001:10001
EXPOSE 7777/tcp
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-cp", "clases", "batallanaval.MainServidor"]
