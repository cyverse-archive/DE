FROM debian:jessie

MAINTAINER Dennis Roberts <dennis@cyverse.org>

ENV OPENLDAP_VERSION 2.4.40

RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install --no-install-recommends -y \
        slapd=${OPENLDAP_VERSION}* ldap-utils=${OPENLDAP_VERSION}* && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN mv /etc/ldap /etc/ldap.dist && \
    mkdir -p /etc/ldap /var/lib/ldap

EXPOSE 389

ENV SLAPD_PASSWORD notprod
ENV SLAPD_DOMAIN example.org
ENV SLAPD_ADDITIONAL_SCHEMAS openldap

COPY modules/ /etc/ldap.dist/modules
COPY ./entrypoint.sh /entrypoint.sh
COPY ./test-de.ldif /tmp/test-de.ldif
COPY ./init-ldap.sh /tmp/init-ldap.sh

RUN /tmp/init-ldap.sh /tmp/test-de.ldif

ENTRYPOINT ["/entrypoint.sh"]

CMD ["slapd", "-d", "32768", "-u", "openldap", "-g", "openldap"]
