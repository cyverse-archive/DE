FROM jeanblanchard/alpine-glibc

ADD bin/templeton-linux-amd64 /bin/templeton

ARG git_commit=unknown
ARG buildenv_git_commit=unknown
ARG version=unknown
LABEL org.iplantc.de.templeton.git-ref="$git_commit" \
      org.iplantc.de.templeton.version="$version" \
      org.iplantc.de.buildenv.git-ref="$buildenv_git_commit"

EXPOSE 60000
ENTRYPOINT ["templeton"]
CMD ["--help"]

