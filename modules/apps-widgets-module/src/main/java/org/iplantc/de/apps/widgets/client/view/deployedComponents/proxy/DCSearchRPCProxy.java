package org.iplantc.de.apps.widgets.client.view.deployedComponents.proxy;

import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.deployedComps.DeployedComponent;
import org.iplantc.de.client.services.DeployedComponentServices;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.base.Strings;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;

import java.util.List;

public class DCSearchRPCProxy extends
        RpcProxy<FilterPagingLoadConfig, PagingLoadResult<DeployedComponent>> {

    DeployedComponentServices dcService = ServicesInjector.INSTANCE.getDeployedComponentServices();

    private String lastQueryText = ""; //$NON-NLS-1$

    public String getLastQuery() {
        return lastQueryText;
    }

    @Override
    public void load(FilterPagingLoadConfig loadConfig,
            final AsyncCallback<PagingLoadResult<DeployedComponent>> callback) {
        // Get the proxy's search params.
        List<FilterConfig> filterConfigs = loadConfig.getFilters();
        if (filterConfigs != null && !filterConfigs.isEmpty()) {
            lastQueryText = filterConfigs.get(0).getValue();
        }

        if (!Strings.isNullOrEmpty(lastQueryText)) {
            dcService.searchDeployedComponents(lastQueryText,
                    new AsyncCallback<List<DeployedComponent>>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            ErrorHandler.post(caught);
                        }

                        @Override
                        public void onSuccess(List<DeployedComponent> result) {
                            callback.onSuccess(new PagingLoadResultBean<DeployedComponent>(result,
                                    result.size(), 0));
                        }
                    });
        } else {

            dcService.getDeployedComponents(new AsyncCallback<List<DeployedComponent>>() {

                @Override
                public void onFailure(Throwable caught) {
                    ErrorHandler.post(I18N.ERROR.dcLoadError(), caught);

                }

                @Override
                public void onSuccess(List<DeployedComponent> result) {
                    callback.onSuccess(new PagingLoadResultBean<DeployedComponent>(result,
                            result.size(), 0));
                }

            });
        }

    }

}
