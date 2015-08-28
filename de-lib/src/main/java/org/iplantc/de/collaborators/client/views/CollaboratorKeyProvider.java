/**
 * 
 */
package org.iplantc.de.collaborators.client.views;

import org.iplantc.de.client.models.collaborators.Collaborator;

import com.sencha.gxt.data.shared.ModelKeyProvider;

public class CollaboratorKeyProvider implements ModelKeyProvider<Collaborator> {

    @Override
    public String getKey(Collaborator item) {
        return item.getUserName();
    }

}
