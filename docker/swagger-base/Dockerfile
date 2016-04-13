FROM jeanblanchard/alpine-glibc
MAINTAINER Dennis Roberts <dennis@cyverse.org>
ARG git_commit=unknown
LABEL org.iplantc.de.swagger-base.git-ref="$git_commit"

RUN apk add --update git \
    && git clone https://github.com/swagger-api/swagger-ui.git /tmp/swagger-ui \
    && cd /tmp/swagger-ui \
    && git checkout v2.1.4 \
    && mkdir -p /docs \
    && cp -pr dist/* /docs/ \
    && cd / \
    && rm -rf /tmp/swagger-ui \
    && apk del git \
    && rm -rf /var/cache/apk/*

COPY index.html /docs/index.html
EXPOSE 60000
