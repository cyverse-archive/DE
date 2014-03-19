package org.iplantc.de.diskResource.client.search.views.cells;

import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.commons.client.validators.DiskResourceNameValidator;
import org.iplantc.de.diskResource.client.search.events.SaveDiskResourceQueryEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.core.client.util.BaseEventPreview;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.TextField;

public class DiskResourceQueryFormNamePrompt extends Composite implements Editor<DiskResourceQueryTemplate>, SaveDiskResourceQueryEvent.HasSaveDiskResourceQueryEventHandlers {

    interface DiskResourceQueryFormNamePromptUiBinder extends UiBinder<Widget, DiskResourceQueryFormNamePrompt> {}

    interface QueryFormNamePromptEditorDriver extends SimpleBeanEditorDriver<DiskResourceQueryTemplate, DiskResourceQueryFormNamePrompt> {}

    private static DiskResourceQueryFormNamePromptUiBinder uiBinder = GWT.create(DiskResourceQueryFormNamePromptUiBinder.class);

    protected BaseEventPreview eventPreview;

    @UiField
    TextField name;

    @Ignore
    @UiField
    TextButton saveFilterBtn;

    @Ignore
    @UiField
    Label saveLabel;

    private final QueryFormNamePromptEditorDriver editorDriver = GWT.create(QueryFormNamePromptEditorDriver.class);
    private boolean showing;

    String originalName;

    public DiskResourceQueryFormNamePrompt() {
        initWidget(uiBinder.createAndBindUi(this));
        setSize("330", "90");
        name.addValidator(new DiskResourceNameValidator());
        name.setAutoValidate(true);
        saveLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        editorDriver.initialize(this);
        eventPreview = new BaseEventPreview();
        eventPreview.getIgnoreList().add(getElement());
        eventPreview.setAutoHide(false);
    }

    @Override
    public HandlerRegistration addSaveDiskResourceQueryEventHandler(SaveDiskResourceQueryEvent.SaveDiskResourceQueryEventHandler handler) {
        return addHandler(handler, SaveDiskResourceQueryEvent.TYPE);
    }

    @Override
    public void hide() {
        if (showing) {
            editorDriver.flush();
            onHide();
            RootPanel.get().remove(this);
            eventPreview.remove();
            showing = false;
            hidden = true;
        }
    }

    public boolean isShowing() {
        return showing;
    }

    public void show(DiskResourceQueryTemplate filter, Element element, AnchorAlignment alignment) {
        editorDriver.edit(filter);
        originalName = filter.getName();
        show(element, alignment);
    }

    public void show(Element element, AnchorAlignment anchorAlignment) {
        getElement().makePositionable(true);
        RootPanel.get().add(this);
        onShow();
        getElement().updateZIndex(0);

        showing = true;

        getElement().setWidth(element.getOffsetWidth());
        getElement().alignTo(element, anchorAlignment, new int[] {0, 0});

        getElement().show();
        if (!eventPreview.getIgnoreList().contains(element)) {
            eventPreview.getIgnoreList().add(element);
        }
        eventPreview.add();

        focus();
    }

    @UiHandler("cancelSaveFilterBtn")
    void onCancelSaveFilter(SelectEvent event) {
        // Reset name of filter
        name.setValue(originalName);
        hide();
    }

    @UiHandler("saveFilterBtn")
    void onSaveFilterSelected(SelectEvent event) {
        final DiskResourceQueryTemplate flushedQueryTemplate = editorDriver.flush();
        if (editorDriver.hasErrors() || DiskResourceQueryForm.isEmptyQuery(flushedQueryTemplate)) {
            return;
        }

        // Set the filter name field to allow blank values when hidden.
        fireEvent(new SaveDiskResourceQueryEvent(flushedQueryTemplate, originalName));
        hide();
    }

}
