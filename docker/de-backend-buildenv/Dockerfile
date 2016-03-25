FROM centos:centos7
MAINTAINER John Wregglesworth <wregglej@gmail.com>
ARG git_commit
LABEL org.iplantc.de.buildenv.git-ref="$git_commit"

ENV GOOS=linux GOARCH=amd64 GOVERSION=1.6
ENV MVNVERSION=3.3.3
ENV MVNURL=http://apache.claz.org/maven/maven-3/$MVNVERSION/binaries/apache-maven-$MVNVERSION-bin.tar.gz

##RUN rpm -Uvh http://ftp.linux.ncsu.edu/pub/epel/5/i386/epel-release-5-4.noarch.rpm
RUN yum install -y \
    git-core \
    java-1.7.0-openjdk-devel \
    java-1.7.0-openjdk mercurial \
    curl \
    wget \
    ruby \
    ruby-devel \
    rubygems \
    gcc \
    rpm-build \
    gcc \
    g++ \
    make \
    tar \
    which \
    && yum -y clean all
RUN mkdir -p /opt/go && \
    curl -O https://storage.googleapis.com/golang/go$GOVERSION.$GOOS-$GOARCH.tar.gz && \
    tar xzf go$GOVERSION.$GOOS-$GOARCH.tar.gz -C /opt/go --strip-components=1 && \
    rm -rf go$GOVERSION.$GOOS-$GOARCH.tar.gz
RUN mkdir -p /opt/maven && \
    curl -O $MVNURL && \
    tar xzf apache-maven-$MVNVERSION-bin.tar.gz -C /opt/maven --strip-components=1 && \
    rm -rf apache-maven-$MVNVERSION-bin.tar.gz
RUN mkdir -p /opt/nodejs && \
    curl -O http://nodejs.org/dist/v0.10.32/node-v0.10.32-linux-x64.tar.gz && \
    tar xzf node-v0.10.32-linux-x64.tar.gz -C /opt/nodejs --strip-components=1 && \
    rm -rf node-v0.10.32-linux-x64.tar.gz
RUN mkdir -p /opt/gopath
ENV GOPATH /opt/gopath
ENV GOROOT /opt/go
ENV LEIN_ROOT 1
ENV PATH /bin:/usr/bin:/usr/local/bin:/sbin/:/usr/sbin:/opt/go/bin:/opt/gopath/bin:/opt/maven/bin:/opt/nodejs/bin
RUN go get github.com/tools/godep
RUN go get github.com/constabulary/gb/...
RUN go get github.com/jstemmer/go-junit-report
ADD https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein /usr/bin/lein
ADD build_profiles.clj /root/.lein/profiles.clj

RUN chmod a+x /usr/bin/lein
RUN npm install -g grunt-cli
RUN lein help
RUN git clone https://github.com/cyverse/DE.git backend && \
    cd backend && \
    git checkout $git_commit && \
    lein exec build-all.clj lein-plugins libs
