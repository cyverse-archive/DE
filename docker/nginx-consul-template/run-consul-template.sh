#!/bin/sh

exec consul-template "$@" -consul $CONSUL_CONNECT -template "$NGINX_TEMPLATE:$NGINX_CONF:nginx -s reload || true"
