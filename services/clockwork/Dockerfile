FROM discoenv/javabase

USER root
VOLUME ["/etc/iplant/de"]

COPY conf/main/logback.xml /home/iplant/
COPY target/clockwork-standalone.jar /home/iplant/
RUN chown -R iplant:iplant /home/iplant/

ARG git_commit=unknown
ARG buildenv_git_commit=unknown
ARG version=unknown
LABEL org.iplantc.de.clockwork.git-ref="$git_commit" \
      org.iplantc.de.clockwork.version="$version" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"

USER iplant
RUN ln -s "/opt/jdk/bin/java" "/home/iplant/bin/clockwork"
ENTRYPOINT ["clockwork", "-Dlogback.configurationFile=/etc/iplant/de/logging/clockwork-logging.xml", "-cp", ".:clockwork-standalone.jar", "clockwork.core"]
