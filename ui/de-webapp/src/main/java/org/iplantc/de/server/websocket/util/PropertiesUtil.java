package org.iplantc.de.server.websocket.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by sriram on 4/6/16.
 */
public class PropertiesUtil {

    private PropertiesUtil() {

    }

    public static Properties getDEProperties() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("/etc/iplant/de/de.properties"));
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
