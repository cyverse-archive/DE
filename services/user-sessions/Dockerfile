FROM discoenv/javabase

USER root
VOLUME ["/etc/iplant/de"]

COPY conf/main/logback.xml /
COPY target/user-sessions-standalone.jar /

ARG git_commit=unknown
ARG buildenv_git_commit=unknown
ARG version=unknown
LABEL org.iplantc.de.user-sessions.git-ref="$git_commit" \
      org.iplantc.de.user-sessions.version="$version" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"

RUN ln -s "/opt/jdk/bin/java" "/bin/user-sessions"
ENTRYPOINT ["user-sessions", "-Dlogback.configurationFile=/etc/iplant/de/logging/user-sessions-logging.xml", "-cp", ".:user-sessions-standalone.jar", "user_sessions.core"]
CMD ["--help"]

