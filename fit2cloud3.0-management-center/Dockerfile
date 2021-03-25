FROM registry.fit2cloud.com/fit2cloud3/fabric8-java-alpine-openjdk8-jre:helm-v3.5.2

ARG IMAGE_TAG

MAINTAINER FIT2CLOUD <support@fit2cloud.com>

RUN mkdir -p /opt/apps

ADD target/fit2cloud3.0-management-center-$IMAGE_TAG.jar /opt/apps

ENV JAVA_APP_JAR=/opt/apps/fit2cloud3.0-management-center-$IMAGE_TAG.jar

ENV AB_OFF=true

ENV JAVA_OPTIONS=-Dfile.encoding=utf-8

HEALTHCHECK --interval=15s --timeout=5s --retries=20 --start-period=30s CMD curl -f 127.0.0.1:6602

CMD ["/deployments/run-java.sh"]
