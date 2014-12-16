package org.iplantc.de.diskResource.client.dataLink.presenter.callbacks;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.diskResource.client.dataLink.view.DataLinkPanel;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.widget.core.client.tree.Tree;

public class DeleteDataLinksCallback implements AsyncCallback<String> {

    private final Tree<DiskResource, DiskResource> tree;
    private DataLinkPanel view;
    private final JsonUtil jsonUtil;

    public DeleteDataLinksCallback(DataLinkPanel view) {
        this.view = view;
        this.tree = view.getTree();
        this.jsonUtil = JsonUtil.getInstance();
    }

    @Override
    public void onFailure(Throwable caught) {
        ErrorHandler.post(I18N.ERROR.deleteDataLinksError(), caught);
        view.unmask();
    }

    @Override
    public void onSuccess(String result) {
        JSONObject response = jsonUtil.getObject(result);
        JSONArray tickets = jsonUtil.getArray(response, "tickets");

        for (int i = 0; i < tickets.size(); i++) {
            String ticketId = tickets.get(i).isString().toString().replace("\"", "");
            DiskResource m = tree.getStore().findModelWithKey(ticketId);
            if (m != null) {
                tree.getStore().remove(m);
            }
        }

        view.unmask();

    }
}
