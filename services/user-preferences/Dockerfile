FROM discoenv/javabase

USER root
VOLUME ["/etc/iplant/de"]

COPY conf/main/logback.xml /home/iplant/
COPY target/user-preferences-standalone.jar /home/iplant/
RUN chown -R iplant:iplant /home/iplant/

ARG git_commit=unknown
ARG buildenv_git_commit=unknown
ARG version=unknown
LABEL org.iplantc.de.user-preferences.git-ref="$git_commit" \
      org.iplantc.de.user-preferences.version="$version" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"

USER iplant
RUN ln -s "/opt/jdk/bin/java" "/home/iplant/bin/user-preferences"
ENTRYPOINT ["user-preferences", "-Dlogback.configurationFile=/etc/iplant/de/logging/user-preferences-logging.xml", "-cp", ".:user-preferences-standalone.jar", "user_preferences.core"]
CMD ["--help"]
