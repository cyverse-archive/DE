FROM ubuntu:14.04

ARG git_commit=unknown
ARG buildenv_git_commit=unknown
ARG version=unknown
LABEL org.iplantc.de.condor-log-monitor.git-ref="$git_commit" \
      org.iplantc.de.condor-log-monitor.version="$version" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"

ADD bin/condor-log-monitor /bin/
EXPOSE 60000
ENTRYPOINT ["condor-log-monitor"]
CMD ["--help"]

