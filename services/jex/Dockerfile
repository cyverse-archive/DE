FROM ubuntu:15.04
ARG git_commit=unknown
ARG buildenv_git_commit=unknown
ARG version=unknown
LABEL org.iplantc.de.jobservices.git-ref="$git_commit" \
      org.iplantc.de.jobservices.version="$version" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"
COPY ./bin/* /bin/
COPY restart /bin/restart
ENTRYPOINT ["restart"]
CMD [ "/bin/sh" ]
