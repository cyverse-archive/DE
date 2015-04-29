FROM discoenv/javabase

ADD target/de.war /home/iplant/
USER root
RUN chown -R iplant:iplant /home/iplant/
USER iplant
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "de.war"]
