FROM discoenv/javabase

USER root
VOLUME ["/etc/iplant/de"]

COPY conf/main/logback.xml /home/iplant/
COPY conf/*.st /home/iplant/
COPY target/iplant-email-standalone.jar /home/iplant/

ARG git_commit=unknown
ARG buildenv_git_commit=unknown
ARG version=unknown
LABEL org.iplantc.de.iplant-email.git-ref="$git_commit" \
      org.iplantc.de.iplant-email.version="$version" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"

RUN ln -s "/opt/jdk/bin/java" "/home/iplant/bin/iplant-email"
ENTRYPOINT ["iplant-email", "-Dlogback.configurationFile=/etc/iplant/de/logging/iplant-email-logging.xml", "-cp", ".:iplant-email-standalone.jar", "iplant_email.core"]
CMD ["--help"]

