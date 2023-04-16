#!/bin/sh

# -4 IPv4 only
# -L follow redirect if the server responds with a redirect
BUILD_JDK_VERSION=${JDK_VERSION:-17}
mkdir -p build/layer/$BUILD_JDK_VERSION
curl -4 -L https://repo.maven.apache.org/maven2/com/amazonaws/aws-lambda-java-runtime-interface-client/2.3.1/aws-lambda-java-runtime-interface-client-2.3.1.jar -o build/layer/$BUILD_JDK_VERSION/aws-lambda-java-runtime-interface-client-2.3.1.jar
curl -4 -L https://repo.maven.apache.org/maven2/com/amazonaws/aws-lambda-java-core/1.2.2/aws-lambda-java-core-1.2.2.jar -o build/layer/$BUILD_JDK_VERSION/aws-lambda-java-core-1.2.2.jar
curl -4 -L https://repo.maven.apache.org/maven2/com/amazonaws/aws-lambda-java-serialization/1.1.2/aws-lambda-java-serialization-1.1.2.jar -o build/layer/$BUILD_JDK_VERSION/aws-lambda-java-serialization-1.1.2.jar
cp bootstrap build/layer/$BUILD_JDK_VERSION
chmod 755 build/layer/$BUILD_JDK_VERSION/bootstrap
