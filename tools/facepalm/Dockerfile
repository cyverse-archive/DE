FROM postgres:9.5

COPY database.tar.gz /database.tar.gz
COPY jex-db.tar.gz /jex-db.tar.gz
COPY metadata-db.tar.gz /metadata-db.tar.gz
COPY notification-db.tar.gz /notification-db.tar.gz
COPY permissions-db.tar.gz /permissions-db.tar.gz

RUN apt-get update && apt-get install -y \
  openjdk-7-jre-headless \
  postgresql-client-9.5 \
  && rm -rf /var/lib/apt/lists/*

ARG git_commit=unknown
ARG buildenv_git_commit=unknown
ARG version=unknown
LABEL org.iplantc.de.facepalm.git-ref="$git_commit" \
      org.iplantc.de.facepalm.version="$version" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"

COPY target/facepalm-standalone.jar /

ENTRYPOINT ["java", "-jar", "facepalm-standalone.jar"]
CMD [ "--help" ]
