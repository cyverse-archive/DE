FROM discoenv/javabase

USER root
VOLUME ["/etc/iplant/de"]

COPY conf/main/logback.xml /
COPY target/info-typer-standalone.jar /

ARG git_commit=unknown
ARG buildenv_git_commit=unknown
ARG version=unknown
LABEL org.iplantc.de.info-typer.git-ref="$git_commit" \
      org.iplantc.de.info-typer.version="$version" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"

RUN ln -s "/opt/jdk/bin/java" "/bin/info-typer"
ENTRYPOINT ["info-typer", "-Dlogback.configurationFile=/etc/iplant/de/logging/info-typer-logging.xml", "-cp", ".:info-typer-standalone.jar", "info_typer.core"]
CMD ["--help"]

