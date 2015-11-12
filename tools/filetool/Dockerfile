FROM alpine:3.2

ARG git_commit=unknown
ARG buildenv_git_commit=unknown
ARG version=unknown
LABEL org.iplantc.de.porklock.git-ref="$git_commit" \
      org.iplantc.de.porklock.version="$version" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"

RUN apk --update add openjdk7-jre

ADD target/porklock-standalone.jar /porklock-standalone.jar

ENTRYPOINT ["java", "-jar", "/porklock-standalone.jar"]

CMD ["--help"]
