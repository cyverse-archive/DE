FROM discoenv/javabase

ARG git_commit=unknown
ARG buildenv_git_commit=unknown
ARG version=unknown
LABEL org.iplantc.de.apps-beta-tagger.git-ref="$git_commit" \
      org.iplantc.de.apps-beta-tagger.version="$version" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"

COPY target/apps-beta-tagger-standalone.jar /iplant/home/

ENTRYPOINT [ "java", "-jar", "/iplant/home/apps-beta-tagger-standalone.jar" ]
CMD [ "--help" ]
