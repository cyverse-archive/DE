FROM discoenv/javabase

USER root
VOLUME ["/etc/iplant/de"]

COPY conf/main/logback.xml /home/iplant/
COPY target/saved-searches-standalone.jar /home/iplant/
RUN chown -R iplant:iplant /home/iplant/

ARG git_commit=unknown
ARG buildenv_git_commit=unknown
LABEL org.iplantc.de.saved-searches.git-ref="$git_commit" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"

USER iplant
ENTRYPOINT ["java", "-Dlogback.configurationFile=/etc/iplant/de/logging/saved-searches-logging.xml", "-cp", ".:saved-searches-standalone.jar", "saved_searches.core"]
CMD ["--help"]
