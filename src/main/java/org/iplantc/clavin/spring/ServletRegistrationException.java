package org.iplantc.clavin.spring;

import org.iplantc.clavin.ClavinException;

/**
 * Thrown when a servlet can't be registered.
 *
 * @author Dennis Roberts
 */
public class ServletRegistrationException extends ClavinException {

    /**
     * The format string used to generate the default detail message.
     */
    private static final String MSG_FORMAT
            = "unable to register servlet, %s; please ensure that the servlet name is not already registered in "
            + "web.xml";

    /**
     * The name of the servlet that couldn't be registered.
     */
    private String servletName;

    /**
     * @return the name of the servlet that couldn't be registered.
     */
    public String getServletName() {
        return servletName;
    }

    /**
     * @param servletName the name of the servlet that couldn't be registered.
     */
    public ServletRegistrationException(String servletName) {
        super(String.format(MSG_FORMAT, servletName));
        this.servletName = servletName;
    }
}
