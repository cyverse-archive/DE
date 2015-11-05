FROM discoenv/javabase

USER root

ADD target/de.war /home/iplant/
RUN chown -R iplant:iplant /home/iplant/

ARG git_commit=unknown
ARG buildenv_git_commit=unknown
LABEL org.iplantc.de.ui.git-ref="$git_commit" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"

USER iplant
EXPOSE 8080

ENTRYPOINT ["java", "-Dlogging.config=file:/etc/iplant/de/logging/de-ui.xml", "-jar", "de.war", "--spring.config.location=file:/etc/iplant/de/de-application.yaml"]
