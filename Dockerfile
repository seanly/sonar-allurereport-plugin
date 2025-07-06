# syntax=docker/dockerfile:1.10.0
FROM openjdk:11-jdk-slim AS build

COPY ./ /code

WORKDIR /code

RUN <<EOF
./mvnw clean package -DskipTests
EOF

FROM seanly/scratch

COPY --from=build /code/target/sonar-allurereport-plugin-*.jar /dist/