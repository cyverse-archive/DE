FROM jeanblanchard/java:jdk-7
MAINTAINER Dennis Roberts <dennis@cyverse.org>
ARG git_commit=unknown
LABEL org.iplantc.de.grouper.git-ref="$git_commit"

# Copy the configuration files to temporary locations.
COPY configs /tmp/configs

# Environment variables for ant.
ENV ANT_HOME /opt/apache-ant
ENV PATH $PATH:$ANT_HOME/bin
ENV ANT_BASE https://www.apache.org/dist/ant/binaries
ENV ANT_VERSION 1.9.6
ENV ANT_URL $ANT_BASE/apache-ant-$ANT_VERSION-bin.tar.gz

# Environment variables for tomcat.
ENV CATALINA_HOME /opt/tomcat
ENV TOMCAT_CONF /etc/tomcat
ENV PATH $CATALINA_HOME/bin:$PATH
ENV TOMCAT_MAJOR 7
ENV TOMCAT_VERSION 7.0.65
ENV TOMCAT_BASE https://archive.apache.org/dist/tomcat
ENV TOMCAT_TGZ apache-tomcat-$TOMCAT_VERSION.tar.gz
ENV TOMCAT_TGZ_URL $TOMCAT_BASE/tomcat-$TOMCAT_MAJOR/v$TOMCAT_VERSION/bin/$TOMCAT_TGZ

# Grouper environment variables.
ENV GROUPER_VERSION 2.2.2
ENV GROUPER_BASE_URL http://software.internet2.edu/grouper/release/$GROUPER_VERSION
ENV GROUPER_API_TGZ grouper.apiBinary-$GROUPER_VERSION.tar.gz
ENV GROUPER_UI_TGZ grouper.ui-$GROUPER_VERSION.tar.gz
ENV GROUPER_WS_TGZ grouper.ws-$GROUPER_VERSION.tar.gz
ENV GROUPER_BASE /opt/grouper
ENV GROUPER_LOGS /var/log/grouper
ENV GROUPER_CONF /etc/grouper
ENV GROUPER_TEMP /tmp/grouper
ENV GROUPER_HOME $GROUPER_BASE/api
ENV GROUPER_UI_HOME $GROUPER_BASE/ui
ENV GROUPER_WS_HOME $GROUPER_BASE/ws
ENV PATH $GROUPER_HOME/bin:$PATH

RUN apk add --update curl \
    && mkdir -p /tmp/jce \
    && curl -jksSLH "Cookie: oraclelicense=accept-securebackup-cookie" -o /tmp/jce/jce.zip \
           http://download.oracle.com/otn-pub/java/jce/7/UnlimitedJCEPolicyJDK7.zip \
    && unzip -d /tmp/jce -o /tmp/jce/jce.zip \
    && cp /tmp/jce/UnlimitedJCEPolicy/*.jar /opt/jdk/jre/lib/security/ \
    && rm -rf /tmp/jce \
    \
    && apk add gnupg \
    && curl -fSL https://www.apache.org/dist/tomcat/tomcat-7/KEYS -o tomcat-keys \
    && curl -fSL https://www.apache.org/dist/ant/KEYS -o ant-keys \
    && gpg --import tomcat-keys \
    && gpg --import ant-keys \
    && rm -f tomcat-keys ant-keys \
    \
    && mkdir -p /tmp/tarballs \
    && cd /tmp/tarballs \
    \
    && curl -fSL "$ANT_URL" -o apache-ant.tar.gz \
    && curl -fSL "$ANT_URL.asc" -o apache-ant.tar.gz.asc \
    && gpg --verify apache-ant.tar.gz.asc \
    \
    && curl -fSL "$TOMCAT_TGZ_URL" -o tomcat.tar.gz \
    && curl -fSL "$TOMCAT_TGZ_URL.asc" -o tomcat.tar.gz.asc \
    && gpg --verify tomcat.tar.gz.asc \
    \
    && curl -fSL "$GROUPER_BASE_URL/$GROUPER_API_TGZ" -o grouper-api.tar.gz \
    && curl -fSL "$GROUPER_BASE_URL/$GROUPER_UI_TGZ" -o grouper-ui.tar.gz \
    && curl -fSL "$GROUPER_BASE_URL/$GROUPER_WS_TGZ" -o grouper-ws.tar.gz \
    \
    && cd /opt \
    && tar -xzvf /tmp/tarballs/apache-ant.tar.gz \
    && mv "apache-ant-$ANT_VERSION" "$ANT_HOME" \
    \
    && cd /opt \
    && tar -xzvf /tmp/tarballs/tomcat.tar.gz \
    && mv "apache-tomcat-$TOMCAT_VERSION" "$CATALINA_HOME" \
    && cd "$CATALINA_HOME" \
    && rm bin/*.bat \
    && mkdir -p "$TOMCAT_CONF" \
    && mv "$CATALINA_HOME/conf/server.xml" "$TOMCAT_CONF/server.xml" \
    && ln -s "$TOMCAT_CONF/server.xml" "$CATALINA_HOME/conf/server.xml" \
    && mv "$CATALINA_HOME/conf/tomcat-users.xml" "$TOMCAT_CONF/tomcat-users.xml" \
    && ln -s "$TOMCAT_CONF/tomcat-users.xml" "$CATALINA_HOME/conf/tomcat-users.xml" \
    && rm -f "$CATALINA_HOME/bin/setenv.sh" \
    && touch "$TOMCAT_CONF/setenv.sh" \
    && ln -s "$TOMCAT_CONF/setenv.sh" "$CATALINA_HOME/bin/setenv.sh" \
    \
    && mkdir -p "$GROUPER_BASE" \
        "$GROUPER_LOGS" \
        "$GROUPER_CONF" \
        "$GROUPER_TEMP" \
    \
    && cd "$GROUPER_BASE" \
    && tar -xzvf /tmp/tarballs/grouper-api.tar.gz \
    && mv "grouper.apiBinary-$GROUPER_VERSION" "$GROUPER_HOME" \
    && mv /tmp/configs/api/grouper.properties "$GROUPER_HOME/conf/" \
    && mv /tmp/configs/api/grouper.hibernate.properties "$GROUPER_HOME/conf/" \
    && mv /tmp/configs/api/grouper-loader.properties "$GROUPER_HOME/conf/" \
    && mv /tmp/configs/api/subject.properties "$GROUPER_HOME/conf" \
    && mv /tmp/configs/api/log4j.properties "$GROUPER_HOME/conf" \
    \
    && cd "$GROUPER_BASE" \
    && tar -xzvf /tmp/tarballs/grouper-ui.tar.gz \
    && mv "grouper.ui-$GROUPER_VERSION" "$GROUPER_UI_HOME" \
    && mv /tmp/configs/ui/build.properties "$GROUPER_UI_HOME" \
    && cd "$GROUPER_UI_HOME" \
    && ant war \
    && cp dist/grouper.war "$CATALINA_HOME/webapps" \
    && cd "$GROUPER_BASE" \
    && rm -rf "$GROUPER_UI_HOME" \
    \
    && cd "$GROUPER_BASE" \
    && tar -xzvf /tmp/tarballs/grouper-ws.tar.gz \
    && mv "grouper.ws-$GROUPER_VERSION" "$GROUPER_WS_HOME" \
    && mv /tmp/configs/ws/build.properties "$GROUPER_WS_HOME/grouper-ws/" \
    && mv /tmp/configs/ws/grouper-ws.properties "$GROUPER_WS_HOME/grouper-ws/conf/" \
    && cd "$GROUPER_WS_HOME/grouper-ws" \
    && ant dist \
    && cp build/dist/grouper-ws.war "$CATALINA_HOME/webapps" \
    && cd "$GROUPER_BASE" \
    && rm -rf "$GROUPER_WS_HOME" \
    \
    && apk del gnupg \
    && apk del curl \
    && rm -rf /var/cache/apk/* \
    && rm -rf /tmp/configs \
    && rm -rf /tmp/tarballs

WORKDIR $CATALINA_HOME

EXPOSE 8080
CMD ["catalina.sh", "run"]
