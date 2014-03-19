package org.iplantc.de.diskResource.client.presenters.callbacks;

import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.DiskResourceInfo;
import org.iplantc.de.client.models.errors.diskResources.DiskResourceErrorAutoBeanFactory;
import org.iplantc.de.client.models.errors.diskResources.ErrorGetManifest;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.diskResource.client.views.DiskResourceView;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

public final class GetDiskResourceDetailsCallback implements AsyncCallback<String> {
    private final DiskResourceView.Presenter presenter;
    private final String path;
    private final DiskResourceAutoBeanFactory factory;

    public GetDiskResourceDetailsCallback(DiskResourceView.Presenter presenter, String path, DiskResourceAutoBeanFactory factory) {
        this.presenter = presenter;
        this.path = path;
        this.factory = factory;
        presenter.getView().maskDetailsPanel();
    }

    @Override
    public void onFailure(Throwable caught) {
        DiskResourceErrorAutoBeanFactory errFactory = GWT.create(DiskResourceErrorAutoBeanFactory.class);
        String errMessage = caught.getMessage();
        presenter.getView().unmaskDetailsPanel();
        if (JsonUtils.safeToEval(errMessage)) {
            AutoBean<ErrorGetManifest> errorBean = AutoBeanCodex.decode(errFactory,
                    ErrorGetManifest.class, errMessage);

            ErrorHandler.post(errorBean.as(), caught);
        } else {
            ErrorHandler.post(I18N.ERROR.retrieveStatFailed(), caught);
        }
    }

    @Override
    public void onSuccess(String result) {
        JSONObject json = JsonUtil.getObject(result);
        JSONObject pathsObj = JsonUtil.getObject(json, "paths");
        JSONObject details = JsonUtil.getObject(pathsObj, path);
        AutoBean<DiskResourceInfo> bean = AutoBeanCodex.decode(factory, DiskResourceInfo.class,
                details.toString());
        presenter.getView().updateDetails(path, bean.as());
        presenter.getView().unmaskDetailsPanel();
    }
}