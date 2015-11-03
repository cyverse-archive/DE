package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.fileViewers.client.events.LineNumberCheckboxChangeEvent;
import org.iplantc.de.fileViewers.client.events.RefreshSelectedEvent;
import org.iplantc.de.fileViewers.client.events.SaveSelectedEvent;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;

import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.Status.StatusAppearance;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.CheckBox;

/**
 * @author jstroot
 */
public abstract class AbstractToolBar extends Composite {

    public interface AbstractToolBarAppearance {
        String editingStatusText();

        String notEditingStatusText();

        String saveButtonText();

        ImageResource saveButtonIcon();

        String refreshButtonText();

        ImageResource refreshButtonIcon();

        String lineNumberCheckboxLabel();

        StatusAppearance editStatusAppearance();

        String editStatusWidth();
    }

    protected boolean editing;

    AbstractToolBarAppearance appearance;

    @UiField TextButton refreshBtn;
    @UiField TextButton saveBtn;
    @UiField CheckBox lineNumberCheckbox;
    @UiField(provided = true) Status editStatus;

    public AbstractToolBar(boolean editing,
                           final AbstractToolBarAppearance appearance) {
        this.editing = editing;
        this.appearance = appearance;
        saveBtn = new TextButton(appearance.saveButtonText(), appearance.saveButtonIcon());
        refreshBtn = new TextButton(appearance.refreshButtonText(), appearance.refreshButtonIcon());
        lineNumberCheckbox = new CheckBox();
        lineNumberCheckbox.setBoxLabel(appearance.lineNumberCheckboxLabel());

        editStatus = new Status(appearance.editStatusAppearance());
        editStatus.setWidth(appearance.editStatusWidth());
    }

    public void setSaveEnabled(boolean enabled) {
        saveBtn.setEnabled(enabled);
    }

    @UiHandler("lineNumberCheckbox")
    void onLineNumberCheckboxValueChange(ValueChangeEvent<Boolean> event){
        fireEvent(new LineNumberCheckboxChangeEvent(event.getValue()));
    }

    @UiHandler("saveBtn")
    void onSaveSelect(SelectEvent event){
        fireEvent(new SaveSelectedEvent());
    }

    @UiHandler("refreshBtn")
    void onRefreshSelect(SelectEvent event){
        fireEvent(new RefreshSelectedEvent());
    }

    /**
     * @return the editing
     */
    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        saveBtn.setEnabled(editing);
        String editingText = editing ? appearance.editingStatusText() : appearance.notEditingStatusText();
        editStatus.setText(editingText);
    }

    public HandlerRegistration addRefreshHandler(final RefreshSelectedEvent.RefreshSelectedEventHandler handler) {
        return addHandler(handler, RefreshSelectedEvent.TYPE);
    }

    public HandlerRegistration addSaveHandler(final SaveSelectedEvent.SaveSelectedEventHandler handler) {
        return addHandler(handler, SaveSelectedEvent.TYPE);
    }

    public HandlerRegistration addLineNumberCheckboxChangeHandler(final LineNumberCheckboxChangeEvent.LineNumberCheckboxChangeEventHandler handler){
        return addHandler(handler, LineNumberCheckboxChangeEvent.TYPE);
    }

}
