FROM discoenv/javabase

ARG git_commit=unknown
ARG buildenv_git_commit=unknown
ARG version=unknown
LABEL org.iplantc.de.sharkbait.git-ref="$git_commit" \
      org.iplantc.de.sharkbait.version="$version" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"

COPY target/sharkbait-standalone.jar /iplant/home/

ENTRYPOINT ["java", "-jar", "/iplant/home/sharkbait-standalone.jar"]
