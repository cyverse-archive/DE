package org.iplantc.de.commons.client.views.dialogs;

import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.commons.client.validators.DiskResourceNameValidator;
import org.iplantc.de.commons.client.widgets.IPCFileUploadField;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTML;

import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.InvalidEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent;
import com.sencha.gxt.widget.core.client.event.ValidEvent;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.FormPanelHelper;
import com.sencha.gxt.widget.core.client.form.TextField;

import java.util.List;

/**
 *
 * @author aramsey
 */
public abstract class AbstractFileUploadDialog extends IPlantDialog {

    public interface AbstractFileUploadDialogAppearance {

        String confirmAction();

        String fileExist();

        String fileUploadMaxSizeWarning();

        ImageResource arrowUndoIcon();

        String fileUploadsFailed(List<String> strings);

        String closeConfirmMessage();

        SafeHtml renderDestinationPathLabel(String destPath, String parentPath);

        String reset();

        String upload();

        String fileUploadsSuccess(List<String> strings);
    }

    public static final String HDN_PARENT_ID_KEY = "dest";
    public static final String HDN_USER_ID_KEY = "user";
    public static final String FILE_TYPE = "type";
    public static final String URL_FIELD = "url";
    static final String FORM_WIDTH = "475";
    static final String FORM_HEIGHT = "28";
    final DiskResourceAutoBeanFactory FS_FACTORY = GWT.create(DiskResourceAutoBeanFactory.class);
    List<FormPanel> formList;
    List<IPCFileUploadField> fufList;
    List<TextButton> tbList;
    List<Status> statList;
    final List<FormPanel> submittedForms = Lists.newArrayList();
    final SafeUri fileUploadServlet;


    @UiField(provided = true) final AbstractFileUploadDialogAppearance appearance;
    @UiField HTML htmlDestText;
    @UiField FormPanel form0, form1, form2, form3, form4;
    @UiField IPCFileUploadField fuf0, fuf1, fuf2, fuf3, fuf4;
    @UiField TextButton btn0, btn1, btn2, btn3, btn4;
    @UiField Status status0, status1, status2, status3, status4;

    public AbstractFileUploadDialog(final SafeUri fileUploadServlet) {
        this.fileUploadServlet = fileUploadServlet;
        appearance = GWT.create(AbstractFileUploadDialogAppearance.class);

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
    }

    protected void afterBinding() {
        formList = Lists.newArrayList(form0, form1, form2, form3, form4);
        fufList = Lists.newArrayList(fuf0, fuf1, fuf2, fuf3, fuf4);
        tbList = Lists.newArrayList(btn0, btn1, btn2, btn3, btn4);
        statList = Lists.newArrayList(status0, status1, status2, status3, status4);
        addValidators();
        setModal(false);
    }

    protected abstract void onSubmitComplete(List<IPCFileUploadField> fufList,
                                             List<Status> statList,
                                             List<FormPanel> submittedForms,
                                             List<FormPanel> formList,
                                             SubmitCompleteEvent event);

    protected abstract void doUpload(List<IPCFileUploadField> fufList,
                                     List<Status> statList,
                                     List<FormPanel> submittedForms,
                                     List<FormPanel> formList);

    @UiHandler({ "form0", "form1", "form2", "form3", "form4" })
    void onSubmitComplete(SubmitCompleteEvent event) {
        onSubmitComplete(fufList, statList, submittedForms, formList, event);
    }

    @Override
    protected void onOkButtonClicked() {
        doUpload(fufList, statList, submittedForms, formList);
    }

    void addValidators() {
        for (IPCFileUploadField f : fufList) {
            f.addValidator(new DiskResourceNameValidator());
        }
    }

    @UiFactory
    FormPanel createFormPanel() {
        FormPanel form = new FormPanel();
        form.setAction(fileUploadServlet);
        form.setMethod(FormPanel.Method.POST);
        form.setEncoding(FormPanel.Encoding.MULTIPART);
        form.setSize(FORM_WIDTH, FORM_HEIGHT);
        return form;
    }

    @UiFactory
    HorizontalLayoutContainer createHLC() {
        return new HorizontalLayoutContainer();
    }

    @UiFactory
    Status createFormStatus() {
        Status status = new Status();
        status.setWidth(15);
        return status;
    }


    @UiHandler({ "btn0", "btn1", "btn2", "btn3", "btn4" })
    void onResetClicked(SelectEvent event) {
        IPCFileUploadField uField = fufList.get(tbList.indexOf(event.getSource()));
        uField.reset();
        uField.validate(true);
    }

    @UiHandler({ "fuf0", "fuf1", "fuf2", "fuf3", "fuf4" })
    void onFieldChanged(ChangeEvent event) {
        getOkButton().setEnabled(FormPanelHelper.isValid(this, true) && isValidForm());
    }

    @UiHandler({ "fuf0", "fuf1", "fuf2", "fuf3", "fuf4" })
    void onFieldValid(ValidEvent event) {
        getOkButton().setEnabled(FormPanelHelper.isValid(this, true) && isValidForm());
    }

    boolean isValidForm() {
        for (IPCFileUploadField f : fufList) {
            if (!Strings.isNullOrEmpty(f.getValue()) && f.isValid()) {
                return true;
            }
        }
        return false;

    }

    @UiHandler({ "fuf0", "fuf1", "fuf2", "fuf3", "fuf4" })
    void onFieldInvalid(InvalidEvent event) {
        getOkButton().setEnabled(false);
    }

    @Override
    public void hide() {
        if (submittedForms.size() > 0) {
            final ConfirmMessageBox cmb =
                    new ConfirmMessageBox(appearance.confirmAction(), appearance.closeConfirmMessage());

            cmb.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
                @Override
                public void onDialogHide(DialogHideEvent event) {
                    if (Dialog.PredefinedButton.YES.equals(event.getHideButton())) {
                        AbstractFileUploadDialog.super.hide();
                    }
                }
            });

            cmb.show();
        } else {
            super.hide();
        }
    }

    @UiHandler({ "fuf0", "fuf1", "fuf2", "fuf3", "fuf4" })
    void onFormKeyUp(KeyUpEvent event) {
        if ((event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE) || (event.getNativeKeyCode()
                                                                     == KeyCodes.KEY_DELETE)) {
            TextField tf = (TextField)event.getSource();
            for (IPCFileUploadField fuf : fufList) {
                String value = fuf.getValue();
                String currentValue = tf.getCurrentValue();
                if (value.equalsIgnoreCase(currentValue)) {
                    fuf.clear();
                    fuf.validate(true);
                    break;
                }
            }
        }
    }

}
