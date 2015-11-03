/**
 * 
 */
package org.iplantc.de.diskResource.client;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.PermissionValue;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

/**
 * @author sriram
 *
 */
public interface DataSharingView extends IsWidget {

    interface Appearance {

        String chooseFromCollab();

        int dataSharingDlgNameColumnWidth();

        ImageResource deleteIcon();

        String done();

        String explain();

        ImageResource folderIcon();

        String groupByData();

        String groupByUser();

        ImageResource helpIcon();

        String loadingMask();

        String manageSharing();

        String nameColumnLabel();

        int nameColumnWidth();

        String permissionsColumnLabel();

        SafeStyles permissionsColumnStyle();

        int permissionsColumnWidth();

        SafeStyles removeColumnStyle();

        String removeColumnTextClass();

        int removeColumnWidth();

        String selfShareWarning();

        int shareBreakDownDlgHeight();

        int shareBreakDownDlgNameColumnWidth();

        int shareBreakDownDlgPermissionColumnWidth();

        int shareBreakDownDlgToolbarHeight();

        int shareBreakDownDlgWidth();

        ImageResource shareIcon();

        SafeHtml sharePermissionsHelp();

        SafeHtml unshare();

        String variablePermissionsNotice();

        String warning();

        String whoHasAccess();

        String selectFilesFolders();
    }

    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter {

        interface Appearance {

            String sharingCompleteMsg();
        }

        void loadDiskResources();

        void loadPermissions();

        PermissionValue getDefaultPermissions();

        void processRequest();
    }

    void addShareWidget(Widget widget);

    void setPresenter(Presenter dataSharingPresenter);

    void setSelectedDiskResources(List<DiskResource> models);

}
