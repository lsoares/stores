FROM gradle:6.8.2-openj9 as cache
RUN mkdir -p /home/gradle/cache_home
RUN mkdir -p /usr/src/app
ENV GRADLE_USER_HOME /home/gradle/cache_home
COPY ./build.gradle.kts /usr/src/app
WORKDIR /usr/src/app
RUN gradle clean build -i --stacktrace

FROM gradle:6.8.2-openj9 as build
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
WORKDIR /usr/src/app
COPY . .
RUN gradle shadowJar

FROM openjdk:15.0-slim as run
WORKDIR /usr/src/app
COPY --from=build /usr/src/app/build/libs ./
CMD [ "java", "-jar", " store-1.0-SNAPSHOT-all.jar" ]
