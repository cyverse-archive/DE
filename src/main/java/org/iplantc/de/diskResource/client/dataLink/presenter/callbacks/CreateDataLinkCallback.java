package org.iplantc.de.diskResource.client.dataLink.presenter.callbacks;

import org.iplantc.de.client.models.dataLink.DataLink;
import org.iplantc.de.client.models.dataLink.DataLinkFactory;
import org.iplantc.de.client.models.dataLink.DataLinkList;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.diskResource.client.dataLink.view.DataLinkPanel;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckState;

import java.util.List;

public class CreateDataLinkCallback<M> implements AsyncCallback<String> {

    @SuppressWarnings("rawtypes")
    private final DataLinkPanel view;
    private final DataLinkFactory factory;
    private final Tree<M, M> tree;

    @SuppressWarnings("unchecked")
    public CreateDataLinkCallback(final DataLinkFactory factory,
            @SuppressWarnings("rawtypes") final DataLinkPanel view) {
        this.factory = factory;
        this.view = view;
        this.tree = this.view.getTree();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onSuccess(String result) {
        AutoBean<DataLinkList> tickets = AutoBeanCodex.decode(factory, DataLinkList.class, result);
        List<DataLink> dlList = tickets.as().getTickets();

        TreeStore<M> treeStore = tree.getStore();
        for (DataLink dl : dlList) {
            M parent = treeStore.findModelWithKey(dl.getPath());
            if (parent != null) {
                treeStore.add(parent, (M)dl);
                tree.setExpanded(parent, true);
                tree.setChecked((M)dl, CheckState.CHECKED);
            }
        }
        view.unmask();
    }

    @Override
    public void onFailure(Throwable caught) {
        ErrorHandler.post(I18N.ERROR.createDataLinksError(), caught);
        view.unmask();
    }

}
