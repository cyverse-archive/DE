FROM ubuntu:15.04
LABEL org.iplantc.de.apps.git-ref="$git_commit" \
      org.iplantc.de.apps.version="$version" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"
COPY ./bin/* /bin/
COPY restart /bin/restart
ENTRYPOINT ["restart"]
CMD [ "/bin/sh" ]
