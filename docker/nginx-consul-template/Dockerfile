FROM nginx:alpine
MAINTAINER Ian McEwen <mian@cyverse.org>
ARG git_commit
LABEL org.iplantc.de.nginx-consul-template.git-ref="$git_commit"

ENV CONSUL_TEMPLATE_VERSION=0.15.0
ENV CONSUL_TEMPLATE_SHA256SUM=b7561158d2074c3c68ff62ae6fc1eafe8db250894043382fb31f0c78150c513a
ENV CONSUL_CONNECT=localhost:8500
ENV NGINX_TEMPLATE=/templates/nginx.conf.tmpl
ENV NGINX_CONF=/etc/nginx/nginx.conf
ENV ENTRYKIT_VERSION=0.4.0

ADD https://releases.hashicorp.com/consul-template/${CONSUL_TEMPLATE_VERSION}/consul-template_${CONSUL_TEMPLATE_VERSION}_linux_amd64.zip /
ADD https://github.com/progrium/entrykit/releases/download/v${ENTRYKIT_VERSION}/entrykit_${ENTRYKIT_VERSION}_Linux_x86_64.tgz /

RUN echo "${CONSUL_TEMPLATE_SHA256SUM}  consul-template_${CONSUL_TEMPLATE_VERSION}_linux_amd64.zip" | sha256sum -c - \
    && unzip consul-template_${CONSUL_TEMPLATE_VERSION}_linux_amd64.zip \
    && mkdir -p /usr/local/bin \
    && mv consul-template /usr/local/bin/consul-template

RUN tar -xzvf entrykit_${ENTRYKIT_VERSION}_Linux_x86_64.tgz \
    && mkdir -p /usr/local/bin \
    && mv entrykit /usr/local/bin/entrykit \
    && entrykit --symlink

COPY run-consul-template.sh /usr/local/bin/run-consul-template.sh
COPY run-nginx.sh /usr/local/bin/run-nginx.sh
COPY nginx.conf.tmpl /templates/nginx.conf.tmpl
COPY nginx.conf.dummy /etc/nginx/nginx.conf

ENTRYPOINT ["prehook", "/usr/local/bin/run-consul-template.sh -once", "--", "codep", "/usr/local/bin/run-consul-template.sh", "/usr/local/bin/run-nginx.sh"]
