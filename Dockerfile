FROM discoenv/javabase

ADD target/de.war /home/iplant/
USER root
RUN mkdir /home/iplant/config
RUN chown -R iplant:iplant /home/iplant/
USER iplant
EXPOSE 8080

VOLUME ["/etc/iplant/de/de-application.yaml", "de-application.yaml"]
ENTRYPOINT ["java", "-jar", "de.war", "--spring.config.location=file:de-application.yaml"]
