package org.iplantc.de.diskResource.client.views;

import org.iplantc.de.client.models.diskResources.DiskResource;

public interface IsDiskResourceRoot {

    /**
     * @param dr
     * @return true if the given <code>DiskResource</code> is a root folder, false otherwise
     */
    boolean isRoot(final DiskResource dr);

}
