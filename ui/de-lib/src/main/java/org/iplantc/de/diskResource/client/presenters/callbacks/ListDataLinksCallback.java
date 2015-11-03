package org.iplantc.de.diskResource.client.presenters.callbacks;

import org.iplantc.de.client.models.dataLink.DataLink;
import org.iplantc.de.client.models.dataLink.DataLinkFactory;
import org.iplantc.de.client.models.dataLink.DataLinkList;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckState;

import java.util.List;

/**
 * @author jstroot
 */
public class ListDataLinksCallback<M> implements AsyncCallback<String> {

    private final Tree<M, M> tree;
    private final DataLinkFactory dlFactory;
    private final JsonUtil jsonUtil;
    private final DiskResourceCallbackAppearance appearance = GWT.create(DiskResourceCallbackAppearance.class);

    public ListDataLinksCallback(final Tree<M, M> tree,
                                 final DataLinkFactory dlFactory) {
        this.tree = tree;
        this.dlFactory = dlFactory;
        this.jsonUtil = JsonUtil.getInstance();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onSuccess(String result) {
        // Get tickets by resource id, add them to the tree.
        JSONObject response = jsonUtil.getObject(result);
        JSONObject tickets = jsonUtil.getObject(response, "tickets");

        Splittable placeHolder;
        for (String key : tickets.keySet()) {
            placeHolder = StringQuoter.createSplittable();
            M dr = null;
            // manually find the item since id's wont work
            for (M item : tree.getStore().getAll()) {
                if (((DiskResource)item).getPath().equals(key)) {
                    dr = item;
                    break;
                }
            }

            JSONArray dlIds = jsonUtil.getArray(tickets, key);
            Splittable splittable = StringQuoter.split(dlIds.toString());
            splittable.assign(placeHolder, "tickets");
            AutoBean<DataLinkList> ticketsAB = AutoBeanCodex.decode(dlFactory,
                                                                    DataLinkList.class,
                                                                    placeHolder);

            List<DataLink> dlList = ticketsAB.as().getTickets();

            for (DataLink dl : dlList) {
                tree.getStore().add(dr, (M)dl);
                tree.setChecked((M)dl, CheckState.CHECKED);
            }
        }
        // Select all roots automatically
        tree.setCheckedSelection(tree.getStore().getAll());
        for (M m : tree.getStore().getAll()) {
            tree.setExpanded(m, true);
        }
    }

    @Override
    public void onFailure(Throwable caught) {
        ErrorHandler.post(appearance.listDataLinksError(), caught);
    }
}
