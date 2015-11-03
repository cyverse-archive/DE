/**
 * 
 */
package org.iplantc.de.client.models.collaborators;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * @author sriram
 * 
 */
public interface CollaboratorsList {

    @PropertyName("users")
    public List<Collaborator> getCollaborators();
}
