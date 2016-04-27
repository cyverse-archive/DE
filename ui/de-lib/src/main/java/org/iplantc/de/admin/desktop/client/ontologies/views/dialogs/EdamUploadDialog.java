package org.iplantc.de.admin.desktop.client.ontologies.views.dialogs;

import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.validators.DiskResourceNameValidator;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.commons.client.widgets.IPCFileUploadField;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.InvalidEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent;
import com.sencha.gxt.widget.core.client.event.SubmitEvent;
import com.sencha.gxt.widget.core.client.event.SubmitEvent.SubmitHandler;
import com.sencha.gxt.widget.core.client.event.ValidEvent;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.FormPanel.Encoding;
import com.sencha.gxt.widget.core.client.form.FormPanel.Method;
import com.sencha.gxt.widget.core.client.form.FormPanelHelper;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * @author jstroot
 */
public class EdamUploadDialog extends IPlantDialog {

    private static final String FORM_WIDTH = "475";
    private static final String FORM_HEIGHT = "28";
    @UiField(provided = true) final OntologiesView.OntologiesViewAppearance appearance;
    private final EdamUploadDialogUiBinder BINDER = GWT.create(EdamUploadDialogUiBinder.class);
    private final SafeUri ontologyUploadServlet;
    private final EventBus eventBus;
    @UiField HTML htmlDestText;
    @UiField FormPanel form;
    @UiField IPCFileUploadField fuf;
    @UiField TextButton btn;
    @UiField Status status;
    @Inject
    public EdamUploadDialog(final EventBus eventBus,
                            final SafeUri ontologyUploadServlet,
                            OntologiesView.OntologiesViewAppearance appearance) {
        this.eventBus = eventBus;
        this.ontologyUploadServlet = ontologyUploadServlet;
        this.appearance = appearance;
        setAutoHide(false);
        setHideOnButtonClick(false);
        // Reset the "OK" button text
        getOkButton().setText(appearance.upload());
        getOkButton().setEnabled(false);
        setHeadingText(appearance.upload());
        addCancelButtonSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        });

        add(BINDER.createAndBindUi(this));

        addValidators();
        setModal(false);

    }

    private void addValidators() {
        fuf.addValidator(new DiskResourceNameValidator());
    }

    @UiFactory
    FormPanel createFormPanel() {
        FormPanel form = new FormPanel();
        form.setAction(ontologyUploadServlet);
        form.setMethod(Method.POST);
        form.setEncoding(Encoding.MULTIPART);
        form.setSize(FORM_WIDTH, FORM_HEIGHT);
        return form;
    }

    @UiFactory
    Status createFormStatus() {
        Status status = new Status();
        status.setWidth(15);
        return status;
    }

    @UiHandler({ "btn" })
    void onResetClicked(SelectEvent event) {
        fuf.reset();
        fuf.validate(true);
    }

    @UiHandler({ "fuf" })
    void onFieldChanged(ChangeEvent event) {
        getOkButton().setEnabled(FormPanelHelper.isValid(this, true) && isValidForm());
    }

    @UiHandler({ "fuf" })
    void onFieldValid(ValidEvent event) {
        getOkButton().setEnabled(FormPanelHelper.isValid(this, true) && isValidForm());
    }

    private boolean isValidForm() {
        if (!Strings.isNullOrEmpty(fuf.getValue()) && fuf.isValid()) {
            return true;
        }

        return false;

    }

    @UiHandler({ "fuf" })
    void onFieldInvalid(InvalidEvent event) {
        getOkButton().setEnabled(false);
    }

    @Override
    public void hide() {

        final ConfirmMessageBox cmb =
                new ConfirmMessageBox(appearance.confirmAction(), appearance.closeConfirmMessage());

        cmb.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
                if (PredefinedButton.YES.equals(event.getHideButton())) {
                    EdamUploadDialog.super.hide();
                }
            }
        });

        cmb.show();

    }

    @UiHandler({ "form" })
    void onSubmitComplete(SubmitCompleteEvent event) {

        String results2 = event.getResults();
        if (Strings.isNullOrEmpty(results2)) {
            IplantAnnouncer.getInstance()
                           .schedule(new SuccessAnnouncementConfig(appearance.fileUploadSuccess(fuf.getValue())));
            hide();
        } else {
            IplantAnnouncer.getInstance()
                           .schedule(new ErrorAnnouncementConfig(appearance.fileUploadsFailed(fuf.getValue())));
        }

    }

    @UiHandler({ "fuf" })
    void onFormKeyUp(KeyUpEvent event) {
        if ((event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE) || (event.getNativeKeyCode()
                                                                     == KeyCodes.KEY_DELETE)) {
            TextField tf = (TextField)event.getSource();
            String value = fuf.getValue();
            String currentValue = tf.getCurrentValue();
            if (value.equalsIgnoreCase(currentValue)) {
                fuf.clear();
                fuf.validate(true);
            }
        }
    }

    @Override
    protected void onOkButtonClicked() {
        doUpload();
    }

    private void doUpload() {

        String fileName = fuf.getValue().replaceAll(".*[\\\\/]", "");
        fuf.setEnabled(!Strings.isNullOrEmpty(fileName) && !fileName.equalsIgnoreCase("null"));

        form.addSubmitHandler(new SubmitHandler() {

            @Override
            public void onSubmit(SubmitEvent event) {
                if (event.isCanceled()) {
                    IplantAnnouncer.getInstance()
                                   .schedule(new ErrorAnnouncementConfig(appearance.fileUploadsFailed(fuf.getValue())));
                }

                getOkButton().disable();
            }
        });
        try {
            form.submit();
        } catch (Exception e) {
            GWT.log("\nexception on submit\n" + e.getMessage());
            IplantAnnouncer.getInstance()
                           .schedule(new ErrorAnnouncementConfig(appearance.fileUploadsFailed(fuf.getValue())));
        }
    }

    @UiTemplate("EdamUploadPanel.ui.xml")
    interface EdamUploadDialogUiBinder extends UiBinder<Widget, EdamUploadDialog> {
    }
}
