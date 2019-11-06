FROM maven:3-jdk-8-alpine AS BUILD

RUN apk add --no-cache git

WORKDIR /usr/src/app

COPY pom.xml .

RUN mvn --batch-mode --errors --fail-fast \
  --define maven.javadoc.skip=true \
  --define skipTests=true package \
  --define spring-boot.repackage.skip=true \
  && rm -rf target

COPY . /usr/src/app

RUN mvn --batch-mode --errors --fail-fast \
  --define maven.javadoc.skip=true \
  --define skipTests=true install

FROM java:8-jre-alpine

ARG VERSION=0.0.1-SNAPSHOT

COPY --from=BUILD /usr/src/app/target/qad-${VERSION}.jar /app.jar

ENV QAD_SIMPLIFY_LENGTH_CALCULATION=false \
    QAD_MAX_ANGLE_DEVIATION=90 \
    QAD_MAX_LENGTH_DEVIATION=0.30 \
    QAD_SNAPPING_TOLERANCE=20 \
    QAD_STOP_START_THRESHOLD=5.0 \
    QAD_STOP_END_THRESHOLD=10.0 \
    QAD_SEGMENT_BUFFER_SIZE=20 \
    QAD_DENSIFY_NUM_POINTS=4 \
    QAD_MAPMATCHING_ENABLE=true \
    QAD_MAPMATCHING_ENDPOINT="https://processing.envirocar.org" \
    QAD_OUTPUT_PATH=/tmp

CMD ["java", "-jar", "/app.jar"]