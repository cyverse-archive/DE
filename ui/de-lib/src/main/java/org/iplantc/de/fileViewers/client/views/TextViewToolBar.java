package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.fileViewers.client.events.WrapTextCheckboxChangeEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.toolbar.SeparatorToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * @author jstroot
 */
public class TextViewToolBar extends AbstractToolBar {

    public interface TextViewToolBarAppearance extends AbstractToolBarAppearance {
        String cbxWrapBoxLabel();

        String previewMarkdownBtnText();
    }

    interface TextViewToolBarUiBinder extends UiBinder<ToolBar, TextViewToolBar> { }

    @UiField(provided = true) TextViewToolBarAppearance appearance;
    @UiField CheckBox cbxWrap;
    // for markdown preview
    @UiField TextButton previewMDBtn;
    @UiField SeparatorToolItem previewSeparator;

    private static final TextViewToolBarUiBinder BINDER = GWT.create(TextViewToolBarUiBinder.class);

    TextViewToolBar(final boolean editing,
                    final boolean preview,
                    final TextViewToolBarAppearance appearance) {
        super(editing, appearance);
        this.appearance = appearance;
        initWidget(BINDER.createAndBindUi(this));

        previewSeparator.setVisible(preview);
        previewMDBtn.setVisible(preview);
        setEditing(editing);
    }

    public TextViewToolBar(final boolean editing,
                           final boolean preview) {
        this(editing,
             preview,
             GWT.<TextViewToolBarAppearance>create(TextViewToolBarAppearance.class));
    }

    public HandlerRegistration addPreviewHandler(SelectHandler handler) {
        if (previewMDBtn == null) {
            return null;
        }
        return previewMDBtn.addSelectHandler(handler);
    }

    public HandlerRegistration addWrapCbxChangeHandler(WrapTextCheckboxChangeEvent.WrapTextCheckboxChangeEventHandler handler) {
        return addHandler(handler, WrapTextCheckboxChangeEvent.TYPE);
    }

    public boolean isWrapText() {
        return cbxWrap.getValue();
    }

    @UiHandler("cbxWrap")
    void onCbxWrapChanged(ValueChangeEvent<Boolean> event) {
        fireEvent(new WrapTextCheckboxChangeEvent(event.getValue()));
    }

}
