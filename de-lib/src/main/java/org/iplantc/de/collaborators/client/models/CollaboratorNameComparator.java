package org.iplantc.de.collaborators.client.models;

import org.iplantc.de.client.models.collaborators.Collaborator;

import java.util.Comparator;

/**
 * @author jstroot
 */
public final class CollaboratorNameComparator implements Comparator<Collaborator> {
    @Override
    public int compare(Collaborator o1, Collaborator o2) {

        if (o1.getFirstName() == null && o2.getFirstName() == null) {
            return 0;
        }

        if (o1.getFirstName() == null) {
            return -1;
        }

        if (o2.getFirstName() == null) {
            return 1;
        }

        return o1.getFirstName().compareTo(o2.getFirstName());
    }
}
