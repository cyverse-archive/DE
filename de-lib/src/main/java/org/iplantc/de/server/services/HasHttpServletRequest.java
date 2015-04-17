package org.iplantc.de.server.services;

import javax.servlet.http.HttpServletRequest;

/**
 * This interface is for wrapped RPC implementations which require access to the servlet's request.
 * @author jstroot
 */
public interface HasHttpServletRequest {

    void setRequest(HttpServletRequest request);
}
