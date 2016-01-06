FROM discoenv/javabase

USER root

RUN mkdir -p /etc/iplant/de/crypto && \
    touch /etc/iplant/de/crypto/pubring.gpg && \
    touch /etc/iplant/de/crypto/random_seed && \
    touch /etc/iplant/de/crypto/secring.gpg && \
    touch /etc/iplant/de/crypto/trustdb.gpg
VOLUME ["/etc/iplant/de"]

COPY conf/main/logback.xml /
COPY target/terrain-standalone.jar /

ARG git_commit=unknown
ARG buildenv_git_commit=unknown
ARG version=unknown
LABEL org.iplantc.de.terrain.git-ref="$git_commit" \
      org.iplantc.de.terrain.version="$version" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"

RUN ln -s "/opt/jdk/bin/java" "/bin/terrain"
ENTRYPOINT ["terrain", "-Dlogback.configurationFile=/etc/iplant/de/logging/terrain-logging.xml", "-cp", ".:terrain-standalone.jar", "terrain.core"]
CMD ["--help"]

