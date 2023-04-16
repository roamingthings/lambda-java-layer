BUILD_JDK_VERSION=${JDK_VERSION:-17}
mkdir -p build/layer/$BUILD_JDK_VERSION
docker build --platform linux/amd64 --no-cache --progress=plain --build-arg JDK_VERSION=$BUILD_JDK_VERSION -t jre$BUILD_JDK_VERSION-al2-slim .
docker run -v $(pwd)/build/layer/$BUILD_JDK_VERSION:/tmp jre$BUILD_JDK_VERSION-al2-slim sh -c "unzip -d /tmp /opt/jre-$BUILD_JDK_VERSION-slim.zip"
