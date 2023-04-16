FROM amazonlinux:2 as packager

ARG JDK_VERSION=17

RUN yum -y update \
    && yum install -y tar zip unzip gzip bzip2-devel ed gcc gcc-c++ gcc-gfortran \
    less libcurl-devel openssl openssl-devel readline-devel xz-devel \
    zlib-devel glibc-static libcxx libcxx-devel llvm-toolset-7 zlib-static \
    && rm -rf /var/cache/yum

ENV JDK_FOLDERNAME jdk-${JDK_VERSION}
ENV JDK_FILENAME amazon-corretto-${JDK_VERSION}-x64-linux-jdk.tar.gz
RUN mkdir $JDK_FOLDERNAME && curl -4 -L https://corretto.aws/downloads/latest/${JDK_FILENAME} | tar -xvz --strip-components=1 -C $JDK_FOLDERNAME
RUN mv $JDK_FOLDERNAME /usr/lib/jdk${JDK_VERSION}
RUN yum install -y binutils
RUN rm -rf $JDK_FOLDERNAME
ENV PATH="/usr/lib/jdk${JDK_VERSION}/bin:$PATH"
RUN jlink --add-modules "$(java --list-modules | cut -f1 -d'@' | tr '\n' ',')" --compress 0 --no-man-pages --no-header-files --strip-debug --output /opt/jre-slim
RUN find /opt/jre-slim/lib -name *.so -exec strip -p --strip-unneeded {} \;
RUN java -Xshare:dump -version
RUN rm /opt/jre-slim/lib/classlist
RUN cp /usr/lib/jdk${JDK_VERSION}/lib/server/classes.jsa /opt/jre-slim/lib/server/classes.jsa
RUN cd /opt/ && zip -r jre-${JDK_VERSION}-slim.zip jre-slim
