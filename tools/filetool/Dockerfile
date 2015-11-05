FROM irods/icommands:4.0.3
ARG git_commit=unknown
ARG buildenv_git_commit=unknown
ARG version=unknown
LABEL org.iplantc.de.porklock.git-ref="$git_commit" \
      org.iplantc.de.porklock.version="$version" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"

ADD target/porklock-standalone.jar /porklock-standalone.jar

RUN apt-get update && apt-get install -y \
  openjdk-7-jre-headless \
  && rm -rf /var/lib/apt/lists/*

ENTRYPOINT ["java", "-jar", "/porklock-standalone.jar"]

CMD ["--help"]
