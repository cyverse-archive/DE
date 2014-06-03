package org.iplantc.de.client.desktop.widget;

import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.client.viewer.callbacks.EnsemblUtil;
import org.iplantc.de.client.viewer.callbacks.LoadGenomeInCoGeCallback;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.commons.client.views.window.configs.FileViewerWindowConfig;
import org.iplantc.de.diskResource.client.events.RequestSendToCoGeEvent;
import org.iplantc.de.diskResource.client.events.RequestSendToCoGeEvent.RequestSendToCoGeEventHandler;
import org.iplantc.de.diskResource.client.events.RequestSendToEnsemblEvent;
import org.iplantc.de.diskResource.client.events.RequestSendToEnsemblEvent.RequestSendToEnsemblEventHandler;
import org.iplantc.de.diskResource.client.events.RequestSendToTreeViewerEvent;
import org.iplantc.de.diskResource.client.events.RequestSendToTreeViewerEvent.RequestSendToTreeViewerEventHandler;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class ExternalVizURLRequestHandler implements RequestSendToCoGeEventHandler, RequestSendToEnsemblEventHandler, RequestSendToTreeViewerEventHandler {

    private final Desktop desktop;

    public ExternalVizURLRequestHandler(Desktop desktop) {
        this.desktop = desktop;
    }

    @Override
    public void onRequestSendToTreeViewer(RequestSendToTreeViewerEvent event) {
        if (event.getFile() != null) {
            showFile(event.getFile(), true);
        }

    }

    @Override
    public void onRequestSendToEnsembl(RequestSendToEnsemblEvent event) {
        File file = event.getFile();
        if (file != null) {
            showFile(file, true);
            EnsemblUtil util = new EnsemblUtil(file, event.getInfoType().toString(), null);
            util.sendToEnsembl();
        }
    }

    @Override
    public void onRequestSendToCoGe(RequestSendToCoGeEvent event) {
        File file = event.getFile();
        if (file != null) {
            showFile(file, true);
            JSONObject obj = new JSONObject();
            JSONArray pathArr = new JSONArray();
            pathArr.set(0, new JSONString(file.getPath()));
            obj.put("paths", pathArr);
            ServicesInjector.INSTANCE.getFileEditorServiceFacade().viewGenomes(obj, new LoadGenomeInCoGeCallback(null));
        }


    }

    private void showFile(File file, boolean vizTabFirst) {
        FileViewerWindowConfig fileViewerWindowConfig = null;
        fileViewerWindowConfig = ConfigFactory.fileViewerWindowConfig(file);
        fileViewerWindowConfig.setVizTabFirst(true);
        fileViewerWindowConfig.setEditing(DiskResourceUtil.isWritable(file));
        desktop.showWindow(fileViewerWindowConfig);
    }

}
