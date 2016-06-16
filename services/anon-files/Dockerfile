FROM discoenv/javabase

USER root
VOLUME ["/etc/iplant/de"]

COPY conf/main/logback.xml /
COPY target/anon-files-standalone.jar /

ARG git_commit=unknown
ARG buildenv_git_commit=unknown
ARG version=unknown
LABEL org.iplantc.de.anon-files.git-ref="$git_commit" \
      SERVICE_GITREF="$git_commit" \
      org.iplantc.de.anon-files.version="$version" \
      SERVICE_VERSION="$version" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit" \
      SERVICE_BUILDENV_GITREF="$buildenv_git_commit"

RUN ln -s "/opt/jdk/bin/java" "/bin/anon-files"
ENTRYPOINT ["anon-files", "-Dlogback.configurationFile=/etc/iplant/de/logging/anon-files-logging.xml", "-cp", ".:anon-files-standalone.jar", "anon_files.core"]

