FROM discoenv/javabase

USER root
VOLUME ["/etc/iplant/de"]

COPY conf/main/logback.xml /
COPY target/iplant-groups-standalone.jar /

ARG git_commit=unknown
ARG buildenv_git_commit=unknown
ARG version=unknown
LABEL org.iplantc.de.iplant-groups.git-ref="$git_commit" \
      org.iplantc.de.iplant-groups.version="$version" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"

RUN ln -s "/opt/jdk/bin/java" "/bin/iplant-groups"
ENTRYPOINT ["iplant-groups", "-Dlogback.configurationFile=/etc/iplant/de/logging/iplant-groups-logging.xml", "-cp", ".:iplant-groups-standalone.jar:/", "iplant_groups.core"]
CMD ["--help"]

