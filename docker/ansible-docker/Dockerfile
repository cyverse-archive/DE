FROM williamyeh/ansible:alpine3
MAINTAINER Jonathan Strootman <jstroot@iplantcollaborative.org>
ENV ANSIBLE_HASH_BEHAVIOUR="merge"
ADD ansible.cfg /etc/ansible/ansible.cfg
RUN apk --update add git openssh
CMD ["/bin/sh"]
