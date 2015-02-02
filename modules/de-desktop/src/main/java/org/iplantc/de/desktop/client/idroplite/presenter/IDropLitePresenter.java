package org.iplantc.de.desktop.client.idroplite.presenter;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.desktop.client.util.IDropLiteUtil;
import org.iplantc.de.desktop.client.idroplite.views.IDropLiteView;
import org.iplantc.de.desktop.client.idroplite.views.IDropLiteView.Presenter;
import org.iplantc.de.client.models.HasPaths;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.sharing.DataSharing.TYPE;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.views.window.configs.IDropLiteWindowConfig;
import org.iplantc.de.diskResource.client.events.RequestSimpleDownloadEvent;
import org.iplantc.de.diskResource.client.events.RequestSimpleUploadEvent;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;

import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;

import java.util.List;
import java.util.Map;

/**
 * @author sriram
 * 
 */
public class IDropLitePresenter implements Presenter {

    private final DiskResourceAutoBeanFactory drFactory = GWT.create(DiskResourceAutoBeanFactory.class);
    private final IDropLiteView view;
    private final int CONTENT_PADDING = 12;
    private final IDropLiteWindowConfig idlwc;
    private final DiskResourceUtil diskResourceUtil;
    private final JsonUtil jsonUtil;

    public IDropLitePresenter(final IDropLiteView view,
                              final IDropLiteWindowConfig config) {
        this.view = view;
        view.setPresenter(this);
        this.idlwc = config;
        this.diskResourceUtil = DiskResourceUtil.getInstance();
        this.jsonUtil = JsonUtil.getInstance();
    }

    @Override
    public void buildUploadApplet() {
        view.mask();
        ServicesInjector.INSTANCE.getDiskResourceServiceFacade().upload(new IDropLiteServiceCallback() {
            @Override
            protected HtmlLayoutContainer buildAppletHtml(JSONObject appletData) {
                int adjustSize = CONTENT_PADDING * 2;

                appletData.put("uploadDest", new JSONString(idlwc.getUploadFolderDest().getPath())); //$NON-NLS-1$

                return IDropLiteUtil.getAppletForUpload(appletData, view.getViewWidth(),
                        view.getViewHeight() - adjustSize);
            }
        });

    }

    @Override
    public void buildDownloadApplet() {
        view.mask();

        if (idlwc.isSelectAll()) {
            ServicesInjector.INSTANCE.getDiskResourceServiceFacade().downloadContents(idlwc.getCurrentFolder().getPath(),
                    new IDropLiteServiceCallback() {
                        @Override
                        protected HtmlLayoutContainer buildAppletHtml(JSONObject appletData) {
                            int adjustSize = CONTENT_PADDING * 3;

                            return IDropLiteUtil.getAppletForDownload(appletData, view.getViewWidth(),
                                    view.getViewHeight() - adjustSize);
                        }
                    });

        } else {
            HasPaths request = drFactory.pathsList().as();
            request.setPaths(diskResourceUtil.asStringPathList(idlwc.getResourcesToDownload()));

            ServicesInjector.INSTANCE.getDiskResourceServiceFacade().download(request, new IDropLiteServiceCallback() {
                @Override
                protected HtmlLayoutContainer buildAppletHtml(JSONObject appletData) {
                    int adjustSize = CONTENT_PADDING * 3;

                    return IDropLiteUtil.getAppletForDownload(appletData, view.getViewWidth(),
                            view.getViewHeight() - adjustSize);
                }
            });

        }

    }

    /**
     * Common success and failure handling for upload and download service calls.
     * 
     * @author psarando
     * 
     */
    private abstract class IDropLiteServiceCallback implements AsyncCallback<String> {
        /**
         * Builds the Html for the idrop-lite applet from the given JSON applet data returned by the
         * service call.
         * 
         * @return Html applet with the given applet params.
         */
        protected abstract HtmlLayoutContainer buildAppletHtml(JSONObject appletData);

        @Override
        public void onSuccess(String response) {
            view.setApplet(buildAppletHtml(jsonUtil.getObject(jsonUtil.getObject(response), "data"))); //$NON-NLS-1$
            view.unmask();
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(caught);
        }
    }

    @Override
    public void onSimpleUploadClick() {
        EventBus.getInstance()
                .fireEvent(new RequestSimpleUploadEvent(this, idlwc.getUploadFolderDest()));
    }

    @Override
    public void onSimpleDownloadClick() {
        EventBus.getInstance().fireEvent(
                new RequestSimpleDownloadEvent(Lists.newArrayList(idlwc.getResourcesToDownload()),
                        idlwc.getCurrentFolder()));
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view.asWidget());
        int mode = idlwc.getDisplayMode();
        if (mode == IDropLiteUtil.DISPLAY_MODE_UPLOAD) {
            buildUploadApplet();
        } else if (mode == IDropLiteUtil.DISPLAY_MODE_DOWNLOAD) {
            buildDownloadApplet();
        }
        view.setToolBarButton(mode);

        // disable simple download if only one folder(s) are selected...
        List<DiskResource> resourcesToDownload = idlwc.getResourcesToDownload();
        boolean foldersOnly = true;
        if (mode == IDropLiteUtil.DISPLAY_MODE_DOWNLOAD
                && !diskResourceUtil.containsFile(Sets.newHashSet(resourcesToDownload))) {
            Map<String, String> typeMap = idlwc.getTypeMap();
            if (typeMap != null) {
                for (String id : typeMap.keySet()) {
                    String type = typeMap.get(id);
                    if (type.equalsIgnoreCase(TYPE.FILE.toString())) {
                        foldersOnly = false;
                        break;
                    }
                }
                if (foldersOnly) {
                    view.disableSimpleDownload();
                }
            }
        }
    }
}
