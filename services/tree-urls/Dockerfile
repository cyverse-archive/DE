FROM discoenv/javabase

USER root
VOLUME ["/etc/iplant/de"]

COPY conf/main/logback.xml /home/iplant/
COPY target/tree-urls-standalone.jar /home/iplant/

ARG git_commit=unknown
ARG buildenv_git_commit=unknown
ARG version=unknown
LABEL org.iplantc.de.tree-urls.git-ref="$git_commit" \
      org.iplantc.de.tree-urls.version="$version" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"

RUN ln -s "/opt/jdk/bin/java" "/home/iplant/bin/tree-urls"
ENTRYPOINT ["tree-urls", "-Dlogback.configurationFile=/etc/iplant/de/logging/tree-urls-logging.xml", "-cp", ".:tree-urls-standalone.jar", "tree_urls.core"]
CMD ["--help"]

