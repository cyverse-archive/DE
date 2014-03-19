package org.iplantc.de.client.services.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.GET;
import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.POST;

import org.iplantc.de.client.DEClientConstants;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.services.DEServiceFacade;
import org.iplantc.de.client.services.FileEditorServiceFacade;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.util.Format;

/**
 * Facade for file editors.
 */
public class FileEditorServiceFacadeImpl implements FileEditorServiceFacade {
    private final DEClientConstants constants;
    private final DEProperties deProperties;
    private final DEServiceFacade deServiceFacade;
    private final UserInfo userInfo;

    @Inject
    public FileEditorServiceFacadeImpl(final DEServiceFacade deServiceFacade, final DEProperties deProperties, final DEClientConstants constants, final UserInfo userInfo) {
        this.deServiceFacade = deServiceFacade;
        this.deProperties = deProperties;
        this.constants = constants;
        this.userInfo = userInfo;
    }

    @Override
    public void getManifest(String idFile, AsyncCallback<String> callback) {
        String address = deProperties.getDataMgmtBaseUrl() + "file/manifest?path=" //$NON-NLS-1$
                + URL.encodeQueryString(idFile);

        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        callService(wrapper, callback);
    }

    @Override
    public String getServletDownloadUrl(final String path) {
        String address = Format.substitute("{0}{1}?url=display-download&user={2}&path={3}", //$NON-NLS-1$
                GWT.getModuleBaseURL(), constants.fileDownloadServlet(), userInfo.getUsername(), path);

        return URL.encode(address);
    }

    @Override
    public void getData(String url, AsyncCallback<String> callback) {
        String address = deProperties.getDataMgmtBaseUrl() + url;

        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        callService(wrapper, callback);
    }

    @Override
    public void getDataChunk(String url, JSONObject body, AsyncCallback<String> callback) {
        String address = deProperties.getDataMgmtBaseUrl() + url;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, body.toString());
        callService(wrapper, callback);
    }

    @Override
    public void getTreeUrl(String idFile, boolean refresh, AsyncCallback<String> callback) {
        String address = "org.iplantc.services.buggalo.baseUrl?refresh=" + refresh + "&path=" + URL.encodeQueryString(idFile); //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        callService(wrapper, callback);
    }

    @Override
    public void uploadTextAsFile(String destination, String fileContents, boolean newFile,
            AsyncCallback<String> callback) {

        String fullAddress = deProperties.getFileIoBaseUrl()
                + (newFile ? "saveas" : "save"); //$NON-NLS-1$
        JSONObject obj = new JSONObject();
        obj.put("dest", new JSONString(destination)); //$NON-NLS-1$
        obj.put("content", new JSONString(fileContents));
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, fullAddress,
                obj.toString());
        callService(wrapper, callback);
    }

    /**
     * Performs the actual service call.
     * 
     * @param wrapper the wrapper used to get to the actual service via the service proxy.
     * @param callback executed when RPC call completes.
     */
    private void callService(ServiceCallWrapper wrapper, AsyncCallback<String> callback) {
        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void getGenomeVizUrl(String idFile, AsyncCallback<String> callback) {
        String address = deProperties.getMuleServiceBaseUrl()
                + "get-genomes-viz-url?path=" + idFile;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        callService(wrapper, callback);

    }

    @Override
    public void viewGenomes(JSONObject pathArray, AsyncCallback<String> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "coge/load-genomes";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address,
                pathArray.toString());
        callService(wrapper, callback);

    }
}
