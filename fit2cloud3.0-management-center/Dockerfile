FROM registry.fit2cloud.com/fit2cloud2/fabric8-java-alpine-openjdk8-jre:glibc

MAINTAINER FIT2CLOUD <support@fit2cloud.com>

RUN mkdir -p /opt/apps

RUN cd /tmp; wget https://get.helm.sh/helm-v3.0.0-linux-amd64.tar.gz ; tar zxvf helm-v3.0.0-linux-amd64.tar.gz; mv /tmp/linux-amd64/helm /usr/bin/helm ; rm -rf /tmp/helm-v3.0.0-linux-amd64.tar.gz /tmp/linux-amd64

ADD target/fit2cloud3.0-management-center-3.0.0.jar /opt/apps

ENV JAVA_APP_JAR=/opt/apps/fit2cloud3.0-management-center-3.0.0.jar

ENV AB_OFF=true

ENV JAVA_OPTIONS=-Dfile.encoding=utf-8

HEALTHCHECK --interval=15s --timeout=5s --retries=20 --start-period=30s CMD curl -f 127.0.0.1:6602

CMD ["/deployments/run-java.sh"]
