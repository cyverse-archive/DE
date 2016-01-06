FROM discoenv/javabase

USER root
VOLUME ["/etc/iplant/de"]

COPY conf/main/logback.xml /
COPY target/data-info-standalone.jar /

ARG git_commit=unknown
ARG buildenv_git_commit=unknown
ARG version=unknown
LABEL org.iplantc.de.data-info.git-ref="$git_commit" \
      org.iplantc.de.data-info.version="$version" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"

RUN ln -s "/opt/jdk/bin/java" "/bin/data-info"
ENTRYPOINT ["data-info", "-Dlogback.configurationFile=/etc/iplant/de/logging/data-info-logging.xml", "-cp", ".:data-info-standalone.jar", "data_info.core"]
CMD ["--help"]

