FROM discoenv/javabase

USER root
VOLUME ["/etc/iplant/de"]

COPY conf/main/logback.xml /home/iplant/
COPY build/* /home/iplant/resources/
COPY target/kifshare-standalone.jar /home/iplant/
RUN chown -R iplant:iplant /home/iplant/

ARG git_commit=unknown
ARG buildenv_git_commit=unknown
ARG version=unknown
LABEL org.iplantc.de.kifshare.git-ref="$git_commit" \
      org.iplantc.de.kifshare.version="$version" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"

USER iplant
RUN ln -s "/opt/jdk/bin/java" "/home/iplant/bin/kifshare"
ENTRYPOINT ["kifshare", "-Dlogback.configurationFile=/etc/iplant/de/logging/kifshare-logging.xml", "-cp", ".:resources:kifshare-standalone.jar", "kifshare.core"]
CMD ["--help"]
