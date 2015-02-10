package org.iplantc.de.diskResource.client;

import org.iplantc.de.client.models.dataLink.DataLink;
import org.iplantc.de.client.models.diskResources.DiskResource;

import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.List;

/**
 * Created by jstroot on 2/10/15.
 * @author jstroot
 */
public interface DataLinkView extends IsWidget {
    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter {

        void createDataLinks(List<DiskResource> selectedItems);

        void deleteDataLink(DataLink dataLink);

        void deleteDataLinks(List<DataLink> dataLinks);

        String getSelectedDataLinkDownloadPage();

        String getSelectedDataLinkDownloadUrl();

        void openSelectedDataLinkDownloadPage();
    }

    void addRoots(List<DiskResource> roots);

    Tree<DiskResource, DiskResource> getTree();

    void mask();

    void unmask();
}
