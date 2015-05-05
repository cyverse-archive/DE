package org.iplantc.de.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;

/**
 * Async counterpart of <code>UUIDService</code>
 * 
 * @author jstroot
 *
 */
public interface UUIDServiceAsync {

    void getUUIDs(int num, AsyncCallback<ArrayList<String>> callback);
}
