FROM ubuntu:14.04

ARG git_commit=unknown
ARG buildenv_git_commit=unknown
LABEL org.iplantc.de.condor-log-monitor.git-ref="$git_commit" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"

RUN useradd -m -U -s /bin/bash iplant
ADD bin/condor-log-monitor /bin/
USER iplant
RUN mkdir -p /home/iplant/logs/
WORKDIR /home/iplant
EXPOSE 60000
ENTRYPOINT ["condor-log-monitor"]
CMD ["--help"]
