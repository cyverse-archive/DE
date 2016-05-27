package org.iplantc.de.client.models.diskResources;

import java.util.List;

import org.iplantc.de.client.models.HasId;

public interface DiskResourceUserMetadata extends HasId {

    List<DiskResourceMetadata> getAvus();

    void setAvus(List<DiskResourceMetadata> avus);
}
