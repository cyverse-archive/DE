package org.iplantc.de.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Used to generate a landing page.
 */
public interface LandingPage {
    void display(HttpServletRequest req, HttpServletResponse res) throws IOException;
}
