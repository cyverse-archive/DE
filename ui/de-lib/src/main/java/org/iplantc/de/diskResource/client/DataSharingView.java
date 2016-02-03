/**
 * 
 */
package org.iplantc.de.diskResource.client;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.sharing.SharingPresenter;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

/**
 * @author sriram
 *
 */
public interface DataSharingView extends IsWidget {

    void addShareWidget(Widget widget);

    void setPresenter(SharingPresenter dataSharingPresenter);

    void setSelectedDiskResources(List<DiskResource> models);

}
