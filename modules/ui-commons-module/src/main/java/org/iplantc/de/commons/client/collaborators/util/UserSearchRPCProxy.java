/**
 * 
 */
package org.iplantc.de.commons.client.collaborators.util;

import org.iplantc.de.client.models.collaborators.Collaborator;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.collaborators.util.UserSearchField.UsersLoadConfig;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;

/**
 * @author sriram
 *
 */
public class UserSearchRPCProxy extends
 RpcProxy<UsersLoadConfig, PagingLoadResult<Collaborator>> {

    private String lastQueryText = ""; //$NON-NLS-1$

    public UserSearchRPCProxy() {
    }

    public String getLastQueryText() {
        return lastQueryText;
    }

    @Override
    public void load(UsersLoadConfig loadConfig,
            final AsyncCallback<PagingLoadResult<Collaborator>> callback) {

        lastQueryText = loadConfig.getQuery();

        if (lastQueryText == null || lastQueryText.isEmpty()) {
            // nothing to search
            return;
        }

        CollaboratorsUtil.search(lastQueryText, new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                callback.onSuccess(new PagingLoadResultBean<Collaborator>(CollaboratorsUtil
                        .getSearchResutls(), CollaboratorsUtil.getSearchResutls().size(), 0));
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
                callback.onFailure(caught);
            }
        });

    }

}
