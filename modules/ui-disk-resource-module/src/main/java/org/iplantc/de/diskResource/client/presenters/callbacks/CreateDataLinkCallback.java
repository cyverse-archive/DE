package org.iplantc.de.diskResource.client.presenters.callbacks;

import org.iplantc.de.client.models.dataLink.DataLink;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.diskResource.client.DataLinkView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckState;

import java.util.List;

/**
 * @author jstroot
 */
public class CreateDataLinkCallback implements AsyncCallback<List<DataLink>> {

    private final Tree<DiskResource, DiskResource> tree;
    private final DataLinkView view;
    private final DiskResourceCallbackAppearance appearance = GWT.create(DiskResourceCallbackAppearance.class);

    public CreateDataLinkCallback(final DataLinkView view) {
        this.view = view;
        this.tree = this.view.getTree();
    }

    @Override
    public void onFailure(Throwable caught) {
        ErrorHandler.post(appearance.createDataLinksError(), caught);
        view.unmask();
    }

    @Override
    public void onSuccess(List<DataLink> dlList) {

        TreeStore<DiskResource> treeStore = tree.getStore();
        DiskResource parent = null;
        for (DataLink dl : dlList) {
            // manually find the item since id's wont work
            for (DiskResource item : tree.getStore().getAll()) {
                if (item.getPath().equals(dl.getPath())) {
                    parent = item;
                    break;
                }
            }
            if (parent != null) {
                treeStore.add(parent, dl);
                tree.setExpanded(parent, true);
                tree.setChecked(dl, CheckState.CHECKED);
            }
        }
        view.unmask();
    }

}
