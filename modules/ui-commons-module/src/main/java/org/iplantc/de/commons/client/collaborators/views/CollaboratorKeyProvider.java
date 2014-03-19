/**
 * 
 */
package org.iplantc.de.commons.client.collaborators.views;

import org.iplantc.de.client.models.collaborators.Collaborator;

import com.sencha.gxt.data.shared.ModelKeyProvider;

public class CollaboratorKeyProvider implements ModelKeyProvider<Collaborator> {

    @Override
    public String getKey(Collaborator item) {
        return item.getId();
    }

}
