FROM amazoncorretto:21-alpine-jdk

RUN apk update; \
	apk add --no-cache procps; \
	apk add --no-cache net-tools; \
	apk add --no-cache iputils; \
	apk add --no-cache bash; \
	apk add --no-cache util-linux; \
	apk add --no-cache dpkg; \
	apk add --no-cache gzip; \
	apk add --no-cache curl; \
	apk add --no-cache tar; \
	apk add --no-cache binutils; \
	apk add --no-cache freetype; \
	apk add --no-cache fontconfig; \
	apk add --no-cache git; \
	apk add --no-cache nano; \ 
	apk add --no-cache openrc; \
	apk add --no-cache apache2



#####################################################################
# The following is pulled from the official maven dockerfile:
# https://github.com/carlossg/docker-maven/blob/26ba49149787c85b9c51222b47c00879b2a0afde/openjdk-14/Dockerfile
#####################################################################

ARG MAVEN_VERSION=3.8.8
ARG USER_HOME_DIR="/root"
ARG SHA=332088670d14fa9ff346e6858ca0acca304666596fec86eea89253bd496d3c90deae2be5091be199f48e09d46cec817c6419d5161fb4ee37871503f472765d00
ARG BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries

RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
  && curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
  && echo "${SHA}  /tmp/apache-maven.tar.gz" | sha512sum -c - \
  && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
  && rm -f /tmp/apache-maven.tar.gz \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

# Enables the JRE remote debugging; perhaps comment this out in a production build
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=*:8000,server=y,suspend=n


#####################################################################
# Build the web jar

RUN mkdir -p /src/gemp-module
COPY gemp-module/ /src/gemp-module/

RUN mvn -f /src/gemp-module/pom.xml install -DskipTests
	
RUN touch /nohup.out
