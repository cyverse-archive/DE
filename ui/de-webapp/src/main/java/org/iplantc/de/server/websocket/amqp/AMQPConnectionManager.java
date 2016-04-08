package org.iplantc.de.server.websocket.amqp;

import org.iplantc.de.server.websocket.util.PropertiesUtil;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

/**
 * Created by sriram on 4/7/16.
 */
public class AMQPConnectionManager {

    public static AMQPConnectionManager instance;

    private final Logger LOG = LoggerFactory.getLogger(AMQPConnectionManager.class);

    private String amqpHost;

    private String amqpPort;

    private String user;

    private String password;

    Properties deprops = PropertiesUtil.getDEProperties();
    private Connection connection;

    private AMQPConnectionManager() {
        amqpHost = deprops.getProperty("org.iplantc.discoveryenvironment.notification.amqp.host");
        amqpPort = deprops.getProperty("org.iplantc.discoveryenvironment.notification.amqp.port");
        user = deprops.getProperty("org.iplantc.discoveryenvironment.notification.amqp.user");
        password = deprops.getProperty("org.iplantc.discoveryenvironment.notification.amqp.password");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(amqpHost);
        factory.setPort(Integer.parseInt(amqpPort));
        factory.setUsername(user);
        factory.setPassword(password);
        try {
            connection = factory.newConnection();
            LOG.error("**********amqp connection created!");
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("**********IOEException when creating amqp connection!");
        } catch (TimeoutException e) {
            e.printStackTrace();
            LOG.error("**********Timeout Exception when creating amqp connection!");
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Exception when creating AMQP connection!");
        }

    }

    public static AMQPConnectionManager getInstance() {
        if(instance == null) {
            instance = new AMQPConnectionManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
