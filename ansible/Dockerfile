FROM discoenv/de-ansible:dev

RUN apt-get update -y && apt-get install -y git
COPY ssh-configs.tar.gz /root/
COPY requirements.yaml /
RUN cd root && tar xzf ssh-configs.tar.gz && chown -R root:root /root/.ssh
RUN ansible-galaxy install --force -r requirements.yaml

CMD [ "/bin/bash" ]
