FROM clojure:alpine

ARG git_commit=unknown
ARG buildenv_git_commit=unknown
ARG version=unknown
LABEL org.iplantc.de.integrationator.git-ref="$git_commit" \
      org.iplantc.de.integrationator.version="$version" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"

COPY target/integrationator-standalone.jar /

ENTRYPOINT ["java", "-jar", "/integrationator-standalone.jar"]
