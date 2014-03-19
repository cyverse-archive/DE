/**
 * 
 */
package org.iplantc.de.diskResource.client.sharing.views;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Permissions;
import org.iplantc.de.client.models.sharing.DataSharing.TYPE;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

/**
 * @author sriram
 *
 */
public interface DataSharingView extends IsWidget {

    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter {

        void loadDiskResources();

        void loadPermissions();

        TYPE getSharingResourceType(String path);

        Permissions getDefaultPermissions();

        void processRequest();

		List<DiskResource> getSelectedResources();
    }

    void addShareWidget(Widget widget);

    void setPresenter(Presenter dataSharingPresenter);

    void setSelectedDiskResources(List<DiskResource> models);


}
