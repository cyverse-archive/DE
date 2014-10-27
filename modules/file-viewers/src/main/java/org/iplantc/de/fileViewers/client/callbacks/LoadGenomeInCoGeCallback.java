package org.iplantc.de.fileViewers.client.callbacks;

import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IplantInfoBox;

import com.google.common.base.Strings;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class LoadGenomeInCoGeCallback implements AsyncCallback<String> {

    private final IsMaskable container;

    public LoadGenomeInCoGeCallback(IsMaskable container) {
        this.container = container;
    }

    @Override
    public void onFailure(Throwable caught) {
        if (container != null) {
            container.unmask();
        }
        ErrorHandler.post(org.iplantc.de.resources.client.messages.I18N.ERROR.cogeError(), caught);
    }

    @Override
    public void onSuccess(String result) {
        JSONObject resultObj = JsonUtil.getObject(result);
        String url = JsonUtil.getString(resultObj, "coge_genome_url");
        if (!Strings.isNullOrEmpty(url)) {
            IplantInfoBox iib = new IplantInfoBox(org.iplantc.de.resources.client.messages.I18N.DISPLAY.coge(), org.iplantc.de.resources.client.messages.I18N.DISPLAY.cogeResponse(url));
            iib.show();
        } else {
            onFailure(null);
        }
        if (container != null) {
            container.unmask();
        }

    }

}
