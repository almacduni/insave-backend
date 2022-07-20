#Build stage
FROM maven:3.6.3-jdk-11-slim AS build
COPY src /home/app/src
COPY lombok.config /home/app
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package


# Package stage
FROM adoptopenjdk/openjdk11:alpine-jre
RUN apk upgrade && apk add --no-cache \
  alpine-sdk \
  linux-headers \
  eudev-dev \
  autoconf \
  automake \
  libusb-dev \
  libtool \
  python3 py3-pip \
  nodejs npm
RUN npm -g config set user $USER
RUN npm i -g npm@6
RUN npm i -g @tatumio/tatum-kms
COPY --from=build /home/app/target/save-backend-release-1.0-SNAPSHOT.jar /usr/local/lib/demo.jar
EXPOSE 5020
ENTRYPOINT ["java","-jar","/usr/local/lib/demo.jar"]
