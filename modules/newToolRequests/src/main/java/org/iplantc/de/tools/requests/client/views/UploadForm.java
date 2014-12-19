package org.iplantc.de.tools.requests.client.views;

import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.commons.client.widgets.IPCFileUploadField;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.event.InvalidEvent.HasInvalidHandlers;
import com.sencha.gxt.widget.core.client.event.InvalidEvent.InvalidHandler;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent.SubmitCompleteHandler;
import com.sencha.gxt.widget.core.client.event.ValidEvent.HasValidHandlers;
import com.sencha.gxt.widget.core.client.event.ValidEvent.ValidHandler;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.validator.AbstractValidator;

import java.util.List;

/**
 * This class manages a single upload for the DE backend services. On submission, it posts a
 * multipart document. The first part of the document is a form containing three fields.
 * 
 * <lu> <li>'file' the name of the file being uploaded</li> <li>'user' the name of the DE user</li> <li>
 * 'dest' the path to the DE user's home collection in iRODS</li> </lu>
 * 
 * The second part of the document contains the contents of the file.
 * 
 * TODO move this class to ui-commons and consider converting the simple upload form to using it.
 */
public final class UploadForm extends Composite implements Uploader, HasChangeHandlers, HasKeyUpHandlers, HasValidHandlers, HasInvalidHandlers {

    interface Binder extends UiBinder<Widget, UploadForm> {
    }

    private static final Binder BINDER = GWT.create(Binder.class);

    @UiField
    FormPanel form;

    @UiField
    IPCFileUploadField fileField;

    @UiField
    Hidden userField;

    @UiField
    Hidden destinationField;

    /**
     * the constructor
     */
    public UploadForm() {
        initWidget(BINDER.createAndBindUi(this));
        userField.setValue(UserInfo.getInstance().getUsername());
        destinationField.setValue(UserInfo.getInstance().getHomePath());
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return fileField.addValueChangeHandler(handler);
    }

    @UiHandler("form")
    void onSubmintComplete(final SubmitCompleteEvent event) {
        fireEvent(new SubmitCompleteEvent(event.getResults()));
    }

    /**
     * @see Composite#setEnabled(boolean)
     */
    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        form.setEnabled(enabled);
        fileField.setEnabled(enabled);
    }

    /**
     * @see Uploader#addSubmitCompleteHandler(SubmitCompleteHandler)
     */
    @Override
    public HandlerRegistration addSubmitCompleteHandler(final SubmitCompleteHandler handler) {
        return addHandler(handler, SubmitCompleteEvent.getType());
    }

    /**
     * @see Uploader#clear()
     */
    @Override
    public void clear() {
        fileField.clear();
    }

    /**
     * @see Uploader#reset()
     */
    @Override
    public void reset() {
        fileField.reset();
    }

    /**
     * @see Uploader#isValid(boolean)
     */
    @Override
    public boolean isValid(final boolean preventMark) {
        return fileField.isValid(preventMark);
    }

    /**
     * @see Uploader#validate(boolean)
     */
    @Override
    public boolean validate(final boolean preventMark) {
        return fileField.validate(preventMark);
    }

    /**
     * @see Uploader#clearInvalid()
     */
    @Override
    public void clearInvalid() {
        fileField.clearInvalid();
    }

    @Override
    public void finishEditing() {
        fileField.finishEditing();
    }

    @Override
    public List<EditorError> getErrors() {
        return fileField.getErrors();
    }

    /**
     * @see Uploader#getValue()
     */
    @Override
    public String getValue() {
        return fileField.getValue();
    }

    @Override
    public void setValue(final String value) {
        fileField.setValue(value);
    }

    /**
     * @see Uploader#markInvalid(String)
     */
    @Override
    public void markInvalid(final String reason) {
        fileField.markInvalid(reason);
    }

    /**
     * @see Uploader#submit()
     */
    @Override
    public void submit() {
        form.submit();
    }

    @Override
    public HandlerRegistration addChangeHandler(final ChangeHandler handler) {
        return fileField.addChangeHandler(handler);
    }

    /**
     * @see HasKeyUpHandlers#addKeyUpHandler(KeyUpHandler)
     */
    @Override
    public HandlerRegistration addKeyUpHandler(final KeyUpHandler handler) {
        return fileField.addKeyUpHandler(handler);
    }

    /**
     * @see HasValidHandlers#addValidHandler(ValidHandler)
     */
    @Override
    public HandlerRegistration addValidHandler(final ValidHandler handler) {
        return fileField.addValidHandler(handler);
    }

    @Override
    public HandlerRegistration addInvalidHandler(final InvalidHandler handler) {
        return fileField.addInvalidHandler(handler);
    }

    /**
     * Attaches a validator to the file name field
     */
    public void addValidator(final AbstractValidator<String> validator) {
        fileField.addValidator(validator);
    }

    /**
     * Forces validation of the file name and returns whether or not it passed validation.
     */
    public boolean isValid() {
        return form.isValid();
    }

    /**
     * Sets whether or not the file name may be blank.
     * 
     * @param allowBlank - whether or not the file name may be blank
     */
    public void setAllowBlank(final boolean allowBlank) {
        fileField.setAllowBlank(allowBlank);
    }

}
