mvn clean package -Dmaven.test.skip=true

docker build -t registry.fit2cloud.com/fit2cloud3/gateway:3.0.0 .
docker push registry.fit2cloud.com/fit2cloud3/gateway:3.0.0
