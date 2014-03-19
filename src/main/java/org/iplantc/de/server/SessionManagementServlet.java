package org.iplantc.de.server;

import org.iplantc.de.shared.services.SessionManagementService;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

/**
 * Provides management of client session data.
 * 
 * @author sriram
 */
public class SessionManagementServlet extends RemoteServiceServlet implements SessionManagementService {

    /**
     * Generated Unique Identifier for serialization
     */
    private static final long serialVersionUID = 1L;

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> getAttributes() throws SerializationException {
        Map<String, String> attributes = new HashMap<String, String>();
        HttpSession session = getSession();
        Enumeration<String> enumeration = session.getAttributeNames();
        while (enumeration.hasMoreElements()) {
            String attributeName = enumeration.nextElement();
            String attributeValue = session.getAttribute(attributeName).toString();
            attributes.put(attributeName, attributeValue);
        }
        return attributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAttribute(String key) throws SerializationException {
        Object attr = getSession().getAttribute(key);
        if (attr == null) {
            return null;
        } else {
            return attr.toString();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void invalidate() throws SerializationException {
        getSession().invalidate();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAttribute(String key) throws SerializationException {
        getSession().removeAttribute(key);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAttribute(String key, String value) throws SerializationException {
        getSession().setAttribute(key, value);
    }

    private HttpSession getSession() {
        return this.getThreadLocalRequest().getSession();
    }
}
