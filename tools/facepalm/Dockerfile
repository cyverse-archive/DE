FROM postgres:9.2

ADD https://everdene.iplantcollaborative.org/jenkins/job/databases-dev/lastSuccessfulBuild/artifact/databases/de-database-schema/database.tar.gz /
ADD https://everdene.iplantcollaborative.org/jenkins/job/databases-dev/lastSuccessfulBuild/artifact/databases/jex-db/jex-db.tar.gz /
ADD https://everdene.iplantcollaborative.org/jenkins/job/databases-dev/lastSuccessfulBuild/artifact/databases/metadata/metadata-db.tar.gz /
ADD https://everdene.iplantcollaborative.org/jenkins/job/databases-dev/lastSuccessfulBuild/artifact/databases/notification-db/notification-db.tar.gz /

RUN apt-get update && apt-get install -y \
  openjdk-7-jre-headless \
  postgresql-client-9.3 \
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
