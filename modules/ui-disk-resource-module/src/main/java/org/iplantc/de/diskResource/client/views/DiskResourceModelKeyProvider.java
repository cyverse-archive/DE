package org.iplantc.de.diskResource.client.views;


import org.iplantc.de.client.models.diskResources.DiskResource;

import com.sencha.gxt.data.shared.ModelKeyProvider;

public class DiskResourceModelKeyProvider implements ModelKeyProvider<DiskResource> {
    @Override
    public String getKey(DiskResource item) {
        return item.getId();
    }
}
