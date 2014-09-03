package org.iplantc.admin.belphegor.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BelphegorProperties {
    private final Properties properties;

    public BelphegorProperties(Properties properties) {
        this.properties = properties;
    }

    public static BelphegorProperties get() throws IOException {

        BelphegorProperties belphegorProperties;
        try {
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("belphegor.properties");
            Properties properties = new Properties();
            properties.load(in);
            in.close();
            belphegorProperties = new BelphegorProperties(properties);
        } catch (IOException e) {
            throw e;
        }
        return belphegorProperties;
    }

    public Properties getProperties() {
        return properties;
    }
}
