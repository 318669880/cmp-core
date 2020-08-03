#!/bin/bash

mvn clean package -Dmaven.test.skip=true

docker build -t registry.fit2cloud.com/fit2cloud2/management-center:${branch} .
docker push registry.fit2cloud.com/fit2cloud2/management-center:${branch}
