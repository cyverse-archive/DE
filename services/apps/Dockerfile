FROM discoenv/javabase

USER root

RUN mkdir -p /etc/iplant/de/crypto && \
    touch /etc/iplant/de/crypto/pubring.gpg && \
    touch /etc/iplant/de/crypto/random_seed && \
    touch /etc/iplant/de/crypto/secring.gpg && \
    touch /etc/iplant/de/crypto/trustdb.gpg && \
    chown -R iplant:iplant /etc/iplant/de/crypto
VOLUME ["/etc/iplant/de"]

COPY conf/main/logback.xml /home/iplant/
COPY target/apps-standalone.jar /home/iplant/
RUN chown -R iplant:iplant /home/iplant/

ARG git_commit=unknown
ARG buildenv_git_commit=unknown
LABEL org.iplantc.de.apps.git-ref="$git_commit" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"

USER iplant
ENTRYPOINT ["java", "-Dlogback.configurationFile=/etc/iplant/de/logging/apps-logging.xml", "-cp", ".:apps-standalone.jar:/home/iplant/", "apps.core"]
CMD ["--help"]
