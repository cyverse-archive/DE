FROM ubuntu:15.04
ARG git_commit=unknown
ARG buildenv_git_commit=unknown
ARG version=unknown
LABEL org.iplantc.de.jobservices.git-ref="$git_commit" \
      org.iplantc.de.jobservices.version="$version" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"
COPY ./bin/road-runner-linux-amd64 /bin/road-runner
COPY ./bin/condor-launcher-linux-amd64 /bin/condor-launcher
COPY ./bin/job-status-recorder-linux-amd64 /bin/job-status-recorder
COPY ./bin/jex-adapter-linux-amd64 /bin/jex-adapter
COPY restart /bin/restart
ENTRYPOINT ["restart"]
CMD [ "/bin/sh" ]
