FROM discoenv/javabase

USER root
VOLUME ["/etc/iplant/de"]

COPY conf/main/logback.xml /home/iplant/
COPY target/monkey-standalone.jar /home/iplant/

ARG git_commit=unknown
ARG buildenv_git_commit=unknown
ARG version=unknown
LABEL org.iplantc.de.monkey.git-ref="$git_commit" \
      org.iplantc.de.monkey.version="$version" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"

RUN ln -s "/opt/jdk/bin/java" "/home/iplant/bin/monkey"
ENTRYPOINT ["monkey", "-Dlogback.configurationFile=/etc/iplant/de/logging/monkey-logging.xml", "-cp", ".:monkey-standalone.jar", "monkey.core"]
CMD ["--help"]

