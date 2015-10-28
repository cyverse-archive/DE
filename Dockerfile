FROM java:8

RUN useradd -m -U -s /bin/bash -u 1337 iplant
USER iplant
RUN mkdir -p /home/iplant/logs/
WORKDIR /home/iplant

ADD target/de.war /home/iplant/
USER root
RUN chown -R iplant:iplant /home/iplant/
USER iplant
EXPOSE 8080

ENTRYPOINT ["java", "-Dlogging.config=file:/etc/iplant/de/logging/de-ui.xml", "-jar", "de.war", "--spring.config.location=file:/etc/iplant/de/de-application.yaml"]
