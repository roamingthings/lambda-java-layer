docker build --progress=plain -t graalvm-al2-slim .
docker run -v $(pwd)/layer:/tmp -it graalvm-al2-slim sh -c "cp /opt/jre-17-slim.zip /tmp"