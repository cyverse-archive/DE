package org.iplantc.de.client.models;

import com.google.web.bindery.autobean.shared.AutoBean;

import java.util.List;

/**
 * An interface for an {@link AutoBean} which has a "paths" key.
 * 
 * @author jstroot
 * 
 */
public interface HasPaths {

    List<String> getPaths();

    void setPaths(List<String> paths);
}
