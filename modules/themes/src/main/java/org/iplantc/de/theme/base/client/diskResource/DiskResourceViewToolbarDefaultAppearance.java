package org.iplantc.de.theme.base.client.diskResource;

import org.iplantc.de.diskResource.client.views.DiskResourceView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public class DiskResourceViewToolbarDefaultAppearance implements DiskResourceView.DiskResourceViewToolbarAppearance {

    private final DiskResourceMessages messages;
    private final DiskResourceViewToolbarResources resources;

    public interface DiskResourceViewToolbarResources extends ClientBundle {
        @Source("org/iplantc/de/theme/base/client/diskResource/list-ingredients-16.png")
        ImageResource pathListMenuIcon16();

        @Source("org/iplantc/de/theme/base/client/diskResource/list-ingredients-24.png")
        ImageResource pathListMenuIcon24();
    }

    public DiskResourceViewToolbarDefaultAppearance() {
        this(GWT.<DiskResourceMessages> create(DiskResourceMessages.class),
             GWT.<DiskResourceViewToolbarResources> create(DiskResourceViewToolbarResources.class));
    }
    DiskResourceViewToolbarDefaultAppearance(final DiskResourceMessages messages,
                                             final DiskResourceViewToolbarResources resources){
        this.messages = messages;
        this.resources = resources;
    }
    @Override
    public String newPathListMenuText() {
        return messages.newPathListMenuText();
    }

    @Override
    public ImageResource newPathListMenuIcon() {
        return resources.pathListMenuIcon16();
    }
}
