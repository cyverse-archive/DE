package org.iplantc.de.diskResource.client.model;

import org.iplantc.de.client.models.diskResources.DiskResource;

import java.util.Comparator;

/**
 * A {@link java.util.Comparator} that sorts {@link org.iplantc.de.client.models.diskResources.DiskResource} names, case-insensitive.
 *
 * @author psarando
 *
 */
public class DiskResourceNameComparator implements Comparator<DiskResource> {

    @Override
    public int compare(DiskResource dr1, DiskResource dr2) {
        return dr1.getName().compareToIgnoreCase(dr2.getName());
    }
}
