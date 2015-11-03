package org.iplantc.de.diskResource.client;

import org.iplantc.de.client.models.dataLink.DataLink;
import org.iplantc.de.client.models.diskResources.DiskResource;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.List;

/**
 * Created by jstroot on 2/10/15.
 * @author jstroot
 */
public interface DataLinkView extends IsWidget {
    interface Appearance {

        String copy();

        String copyDataLinkDlgHeight();

        int copyDataLinkDlgTextBoxWidth();

        String copyDataLinkDlgWidth();

        String copyPasteInstructions();

        String loadingMask();

        String create();

        ImageResource linkAddIcon();

        String expandAll();

        ImageResource treeExpandIcon();

        String collapseAll();

        ImageResource treeCollapseIcon();

        String copyLink();

        ImageResource pasteIcon();

        String advancedSharing();

        ImageResource infoIcon();

        ImageResource exclamationIcon();

        String dataLinkInfoImgClass();

        String dataLinkWarning();

        String backgroundClass();

        String dataLinkWarningClass();
    }

    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter {

        void createDataLinks(List<DiskResource> selectedItems);

        void deleteDataLink(DataLink dataLink);

        String getSelectedDataLinkDownloadUrl();

        void openSelectedDataLinkDownloadPage();
    }

    void addRoots(List<DiskResource> roots);

    Tree<DiskResource, DiskResource> getTree();

    void mask();

    void unmask();
}
