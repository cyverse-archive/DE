/**
 * 
 */
package org.iplantc.de.apps.client.views.dialogs;

import org.iplantc.de.apps.client.events.AppGroupCountUpdateEvent;
import org.iplantc.de.apps.client.events.AppGroupCountUpdateEvent.AppGroupType;
import org.iplantc.de.apps.client.gin.AppsInjector;
import org.iplantc.de.apps.client.views.SubmitAppForPublicUseView.Presenter;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * @author sriram
 * 
 */
public class SubmitAppForPublicDialog extends IPlantDialog {


    private final class SubmitAppForPublicCallbackImpl implements AsyncCallback<String> {
        @Override
        public void onSuccess(String url) {
            hide();

            IplantAnnouncer.getInstance().schedule(
                    new SuccessAnnouncementConfig(SafeHtmlUtils.fromTrustedString(I18N.DISPLAY.makePublicSuccessMessage(url))));

            // Create and fire event
            AppGroupCountUpdateEvent event = new AppGroupCountUpdateEvent(false,
                    AppGroupType.BETA);
            EventBus.getInstance().fireEvent(event);
        }

        @Override
        public void onFailure(Throwable caught) {
            hide();
            if (caught != null) {
                String errorMessage = getErrorMessage(caught);
                if(errorMessage.equals("")) {
                    ErrorHandler.post(I18N.DISPLAY.makePublicFail(), caught);
                } else {
                    ErrorHandler.post(I18N.DISPLAY.makePublicFail() + "Reason: " + errorMessage  , caught);
                }
            }
        }
    }

    private String getErrorMessage(Throwable caught) {
        JSONObject obj = JsonUtil.getObject(caught.getMessage());
        if(obj != null) {
            return JsonUtil.getString(obj, "reason");
        }
        return  "";
    }
    
    public SubmitAppForPublicDialog(final App selectedApp) {
        initDialog();
        final Presenter p = AppsInjector.INSTANCE.getSubmitAppForPublixUsePresenter();
        p.go(this, selectedApp, new SubmitAppForPublicCallbackImpl());
        setOkButtonText(I18N.DISPLAY.submit());
        addOkButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                p.onSubmit();
            }
        });
        addCancelButtonSelectHandler(new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                hide();
                
            }
        });
    }

    private void initDialog() {
        setHeadingText(I18N.DISPLAY.publicSubmissionForm()); //$NON-NLS-1$
        setPixelSize(615, 480);
        setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
        setHideOnButtonClick(false);
    }

}
