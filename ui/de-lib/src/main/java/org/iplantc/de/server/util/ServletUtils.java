package org.iplantc.de.server.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * Some utility methods for use within servlets.
 *
 * @author Dennis Roberts
 */
public class ServletUtils {

    /**
     * Prevent instantiation.
     */
    private ServletUtils() {
    }

    /**
     * Gets the property name prefix from a servlet initialization parameter.
     *
     * @servletConfig the servlet configuration.
     * @return the property name prefix.
     * @throws ServletException if the property name prefix isn't defined.
     */
    public static String getPropertyPrefix(ServletConfig servletConfig) throws ServletException {
        String prefix = servletConfig.getInitParameter("propertyNamePrefix");
        if (prefix == null) {
            throw new ServletException("init parameter, propertyNamePrefix, is required");
        }
        return prefix;
    }

    /**
     * Gets a required property from a set of properties.
     *
     * @param props the properties.
     * @param name the name of the required property.
     * @return the value of the required property.
     * @throws IllegalStateException if the required property is missing or empty.
     */
    public static String getRequiredProp(Properties props, String name) {
        String value = props.getProperty(name);
        if (StringUtils.isBlank(value)) {
            String msg = "configuration property, " + name + ", is missing or empty";
            throw new IllegalStateException(msg);
        }
        return value;
    }

    /**
     * Loads a resource from someplace on the classpath.
     *
     * @param resourceName the name of the resource to load.
     * @return the template text.
     * @throws RuntimeException if the template doesn't exist or can't be loaded.
     */
    public static String loadResource(String resourceName) {
        try {
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
            if (in == null) {
                throw new RuntimeException(resourceName + " not found");
            }
            return IOUtils.toString(in);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
