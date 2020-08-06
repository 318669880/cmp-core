#!/bin/bash

mvn clean package -Dmaven.test.skip=true

docker build -t registry.fit2cloud.com/fit2cloud3/management-center:3.0.0 .
docker push registry.fit2cloud.com/fit2cloud3/management-center:3.0.0
