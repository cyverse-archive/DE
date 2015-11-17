---
layout: page
title: Ansible Commands
description: Collection of useful ansible ad-hoc commands
root: ../
---

Reboot all the `docker-ready` machines in the given inventory in 10 parallel forks

    ansible docker-ready -i [INVENTORY HERE] -K -a "/usr/bin/systemctl reboot" -f 10

Restart the `rsyslog` service
   
    ansible docker-ready -i [INVENTORY HERE] -K -m service -a "name=rsyslog.service state=restarted" -f 10

