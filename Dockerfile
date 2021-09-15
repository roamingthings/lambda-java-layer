FROM amazonlinux:2 as packager

RUN yum -y update \
    && yum install -y tar zip gzip bzip2-devel ed gcc gcc-c++ gcc-gfortran \
    less libcurl-devel openssl openssl-devel readline-devel xz-devel \
    zlib-devel glibc-static libcxx libcxx-devel llvm-toolset-7 zlib-static \
    && rm -rf /var/cache/yum

ENV JDK_FOLDERNAME jdk-17
ENV JDK_FILENAME openjdk-17_linux-x64_bin.tar.gz
RUN curl -4 -L https://download.java.net/java/GA/jdk17/0d483333a00540d886896bac774ff48b/35/GPL/${JDK_FILENAME} | tar -xvz
RUN mv $JDK_FOLDERNAME /usr/lib/jdk17
RUN yum install -y binutils
RUN rm -rf $JDK_FOLDERNAME
ENV PATH="/usr/lib/jdk17/bin:$PATH"
RUN jlink --add-modules "$(java --list-modules | cut -f1 -d'@' | tr '\n' ',')" --compress 0 --no-man-pages --no-header-files --strip-debug --output /opt/jre17-slim
RUN find /opt/jre17-slim/lib -name *.so -exec strip -p --strip-unneeded {} \;
RUN java -Xshare:dump -version
RUN rm /opt/jre17-slim/lib/classlist
RUN cp /usr/lib/jdk17/lib/server/classes.jsa /opt/jre17-slim/lib/server/classes.jsa
RUN cd /opt/ && zip -r jre-17-slim.zip jre17-slim