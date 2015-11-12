FROM java:8

ARG git_commit=unknown
ARG buildenv_git_commit=unknown
ARG version=unknown
LABEL org.iplantc.de.jex.git-ref="$git_commit" \
      org.iplantc.de.jex.version="$version" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"

COPY target/jex-standalone.jar /jex-standalone.jar
COPY conf/main/logback.xml /logback.xml
COPY submitnerator.sh /submitnerator.sh
ENTRYPOINT ["java", "-Dlogback.configurationFile=/etc/iplant/de/logging/jex-logging.xml", "-cp", ".:jex-standalone.jar", "jex.core"]
CMD ["--help"]
