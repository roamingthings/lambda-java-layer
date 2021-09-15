docker build --progress=plain -t jre17-al2-slim .
docker run -v $(pwd)/layer:/tmp -it jre17-al2-slim sh -c "cp /opt/jre-17-slim.zip /tmp"