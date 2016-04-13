FROM nginx:alpine
MAINTAINER Ian McEwen <mian@cyverse.org>
ARG git_commit
LABEL org.iplantc.de.nginx-consul-template.git-ref="$git_commit"

ENV CONSUL_TEMPLATE_VERSION=0.14.0
ENV CONSUL_CONNECT=localhost:8500
ENV NGINX_TEMPLATE=/templates/nginx.conf.tmpl
ENV NGINX_CONF=/etc/nginx/nginx.conf

ADD https://releases.hashicorp.com/consul-template/${CONSUL_TEMPLATE_VERSION}/consul-template_${CONSUL_TEMPLATE_VERSION}_linux_amd64.zip /

RUN unzip consul-template_${CONSUL_TEMPLATE_VERSION}_linux_amd64.zip \
    && mkdir -p /usr/local/bin \
    && mv consul-template /usr/local/bin/consul-template

COPY run.sh /usr/local/bin/run.sh
COPY nginx.conf.tmpl /templates/nginx.conf.tmpl
COPY nginx.conf.dummy /etc/nginx/nginx.conf

CMD ["/usr/local/bin/run.sh"]
