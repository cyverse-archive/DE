package org.iplantc.de.apps.client.views.submit.dialog;

import org.iplantc.de.apps.client.SubmitAppForPublicUseView;
import org.iplantc.de.apps.client.events.AppCategoryCountUpdateEvent;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * FIXME Apply appearance
 * @author sriram, jstroot
 */
public class SubmitAppForPublicDialog extends IPlantDialog implements SelectHandler {

    private final class SubmitAppForPublicCallbackImpl implements AsyncCallback<String> {
        @Override
        public void onFailure(Throwable caught) {
            hide();
            if (caught != null) {
                String errorMessage = getErrorMessage(caught);
                if (errorMessage.equals("")) {
                    ErrorHandler.post(appearance.makePublicFail(), caught);
                } else {
                    ErrorHandler.post(appearance.makePublicFail() + "Reason: " + errorMessage, caught);
                }
            }
        }

        @Override
        public void onSuccess(String appName) {
            hide();

            announcer.schedule(new SuccessAnnouncementConfig(SafeHtmlUtils.fromTrustedString(appearance.makePublicSuccessMessage(appName))));

            // Create and fire event
            AppCategoryCountUpdateEvent event = new AppCategoryCountUpdateEvent(false,
                                                                                AppCategoryCountUpdateEvent.AppCategoryType.BETA);
            eventBus.fireEvent(event);
        }
    }
    @Inject JsonUtil jsonUtil;
    @Inject EventBus eventBus;
    @Inject IplantAnnouncer announcer;
    @Inject SubmitAppForPublicUseView.Presenter presenter;
    private final SubmitAppForPublicUseView.SubmitAppAppearance appearance;

    @Inject
    SubmitAppForPublicDialog(final SubmitAppForPublicUseView.SubmitAppAppearance appearance) {
        this.appearance = appearance;

        setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
        setHeadingText(appearance.publicSubmissionForm());
        setOkButtonText(appearance.submit());
        setPixelSize(615, 480);
        setHideOnButtonClick(false);
        addCancelButtonSelectHandler(this);
    }

    /**
     * Handles Cancel button selections. Wired up in constructor.
     */
    @Override
    public void onSelect(SelectEvent event) {
        hide();
    }

    public void show(final App appToSubmit) {
        presenter.go(this, appToSubmit, new SubmitAppForPublicCallbackImpl());
        super.show();
    }

    @Override
    public void show() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This method is not supported. Use 'show(App)' instead.");
    }

    @Override
    protected void onOkButtonClicked() {
        presenter.onSubmit();
    }

    private String getErrorMessage(Throwable caught) {
        JSONObject obj = jsonUtil.getObject(caught.getMessage());
        if (obj != null) {
            return jsonUtil.getString(obj, "reason");
        }
        return "";
    }

}
