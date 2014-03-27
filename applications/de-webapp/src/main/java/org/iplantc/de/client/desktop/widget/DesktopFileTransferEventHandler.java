package org.iplantc.de.client.desktop.widget;

import org.iplantc.de.client.Constants;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.idroplite.util.IDropLiteUtil;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;
import org.iplantc.de.client.views.windows.configs.IDropLiteWindowConfig;
import org.iplantc.de.client.views.windows.configs.SimpleDownloadWindowConfig;
import org.iplantc.de.commons.client.util.WindowUtil;
import org.iplantc.de.diskResource.client.events.RequestBulkDownloadEvent;
import org.iplantc.de.diskResource.client.events.RequestBulkDownloadEvent.RequestBulkDownloadEventHandler;
import org.iplantc.de.diskResource.client.events.RequestBulkUploadEvent;
import org.iplantc.de.diskResource.client.events.RequestBulkUploadEvent.RequestBulkUploadEventHandler;
import org.iplantc.de.diskResource.client.events.RequestImportFromUrlEvent;
import org.iplantc.de.diskResource.client.events.RequestImportFromUrlEvent.RequestImportFromUrlEventHandler;
import org.iplantc.de.diskResource.client.events.RequestSimpleDownloadEvent;
import org.iplantc.de.diskResource.client.events.RequestSimpleDownloadEvent.RequestSimpleDownloadEventHandler;
import org.iplantc.de.diskResource.client.events.RequestSimpleUploadEvent;
import org.iplantc.de.diskResource.client.events.RequestSimpleUploadEvent.RequestSimpleUploadEventHandler;
import org.iplantc.de.diskResource.client.views.dialogs.FileUploadByUrlDialog;
import org.iplantc.de.diskResource.client.views.dialogs.SimpleFileUploadDialog;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.collect.Lists;
import com.google.gwt.safehtml.shared.UriUtils;

import com.sencha.gxt.widget.core.client.box.AlertMessageBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DesktopFileTransferEventHandler implements RequestBulkDownloadEventHandler,
        RequestBulkUploadEventHandler, RequestImportFromUrlEventHandler,
        RequestSimpleDownloadEventHandler, RequestSimpleUploadEventHandler {

    private final Desktop desktop;
    private final DiskResourceServiceFacade drService = ServicesInjector.INSTANCE.getDiskResourceServiceFacade();

    DesktopFileTransferEventHandler(Desktop desktop) {
        this.desktop = desktop;
    }

    @Override
    public void onRequestSimpleUpload(RequestSimpleUploadEvent event) {
        Folder uploadDest = event.getDestinationFolder();
        if (canUpload(uploadDest)) {
            SimpleFileUploadDialog dlg = new SimpleFileUploadDialog(uploadDest, drService,
                    EventBus.getInstance(), UriUtils.fromTrustedString(Constants.CLIENT
                            .fileUploadServlet()), UserInfo.getInstance().getUsername());
            dlg.show();
        }
    }

    @Override
    public void onRequestUploadFromUrl(RequestImportFromUrlEvent event) {
        Folder uploadDest = event.getDestinationFolder();

        if (canUpload(uploadDest)) {
            String userName = UserInfo.getInstance().getUsername();
            FileUploadByUrlDialog dlg = new FileUploadByUrlDialog(uploadDest, drService, userName);
            dlg.show();
        }
    }

    @Override
    public void onRequestBulkUpload(RequestBulkUploadEvent event) {
        Folder uploadDest = event.getDestinationFolder();
        if (canUpload(uploadDest)) {
            // Build window config
            IDropLiteWindowConfig idlwc = ConfigFactory.iDropLiteUploadWindowConfig();
            idlwc.setDisplayMode(IDropLiteUtil.DISPLAY_MODE_UPLOAD);
            idlwc.setUploadFolderDest(uploadDest);
            idlwc.setCurrentFolder(uploadDest);
            desktop.showWindow(idlwc);
        }
    }

    @Override
    public void onRequestSimpleDownload(RequestSimpleDownloadEvent event) {
        List<DiskResource> resources = Lists.newArrayList(event.getRequestedResources());
        if (isDownloadable(resources)) {
            if (resources.size() == 1) {
                // Download now. No folders possible here....
                final String encodedSimpleDownloadURL = ServicesInjector.INSTANCE.getDiskResourceServiceFacade().getEncodedSimpleDownloadURL(resources.get(0).getId());
                WindowUtil.open(encodedSimpleDownloadURL, "width=100,height=100");
            } else {
                SimpleDownloadWindowConfig sdwc = ConfigFactory.simpleDownloadWindowConfig();
                sdwc.setResourcesToDownload(filterFolders(resources));
                desktop.showWindow(sdwc);
            }
        } else {
            showErrorMsg();
        }

    }

    // remove folders from list to be displayed for simple download
    private List<DiskResource> filterFolders(List<DiskResource> listToFilter) {
        List<DiskResource> filteredList = new ArrayList<DiskResource>();
        for (DiskResource dr : listToFilter) {
            if (!(dr instanceof Folder)) {
                filteredList.add(dr);
            }
        }

        return filteredList;
    }

    @Override
    public void onRequestBulkDownload(RequestBulkDownloadEvent event) {
        List<DiskResource> resources = Lists.newArrayList(event.getRequestedResources());
        if (isDownloadable(resources)) {

            // Build window config
            IDropLiteWindowConfig idlwc = ConfigFactory.iDropLiteDownloadWindowConfig();
            idlwc.setDisplayMode(IDropLiteUtil.DISPLAY_MODE_DOWNLOAD);
            idlwc.setResourcesToDownload(resources);
            idlwc.setTypeMap(buildTypeMap(resources));
            idlwc.setCurrentFolder(event.getCurrentFolder());
            idlwc.setSelectAll(event.isSelectAll());
            desktop.showWindow(idlwc);
        } else {
            showErrorMsg();
        }
    }

    private Map<String, String> buildTypeMap(List<DiskResource> resources) {
        Map<String, String> map = new HashMap<String, String>();
        for (DiskResource dr : resources) {
            map.put(dr.getId(), dr instanceof Folder ? TYPE.FOLDER.toString() : TYPE.FILE.toString());
        }

        return map;

    }

    private boolean isDownloadable(List<DiskResource> resources) {
        if ((resources == null) || resources.isEmpty()) {
            return false;
        }

        for (DiskResource dr : resources) {
            if (!DiskResourceUtil.isReadable(dr)) {
                return false;
            }
        }
        return true;
    }

    private boolean canUpload(Folder uploadDest) {
        if (uploadDest != null && DiskResourceUtil.canUploadTo(uploadDest)) {
            return true;
        } else {
            showErrorMsg();
            return false;
        }
    }

    private void showErrorMsg() {
        new AlertMessageBox(I18N.DISPLAY.permissionErrorTitle(), I18N.DISPLAY.permissionErrorMessage())
                .show();
    }
}
