package org.iplantc.de.client.models;

/**
 * Implemented by objects which contain a description.
 * 
 * This can be implemented by autobeans which have a "description" property.
 * @author jstroot
 *
 */
public interface HasDescription {

    String getDescription();
    
    void setDescription(String description);
}
