package org.iplantc.de.fileViewers.client.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.toolbar.SeparatorToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

public class TextViewToolBar extends AbstractToolBar {

    public interface TextViewToolBarAppearance extends AbstractToolBarAppearance {
        String cbxWrapBoxLabel();

        String previewMarkdownBtnText();
    }

    interface TextViewToolBarUiBinder extends UiBinder<ToolBar, TextViewToolBar> {
    }

    @UiField(provided = true)
    TextViewToolBarAppearance appearance;
    @UiField
    CheckBox cbxWrap;
    // for markdown preview
    @UiField
    TextButton previewMDBtn;
    @UiField
    SeparatorToolItem previewSeparator;
    private static final TextViewToolBarUiBinder BINDER = GWT.create(TextViewToolBarUiBinder.class);
    private final AbstractFileViewer view;

    TextViewToolBar(final AbstractFileViewer view,
                    final boolean editing,
                    final boolean preview,
                    final TextViewToolBarAppearance appearance) {
        super(editing, appearance);
        this.appearance = appearance;
        this.view = view;
        initWidget(BINDER.createAndBindUi(this));

        previewSeparator.setVisible(preview);
        previewMDBtn.setVisible(preview);
    }

    public TextViewToolBar(final AbstractFileViewer view,
                           final boolean editing,
                           final boolean preview) {
        this(view,
             editing,
             preview,
             GWT.<TextViewToolBarAppearance>create(TextViewToolBarAppearance.class));
    }

    public void addPreviewHandler(SelectHandler handler) {
        if (previewMDBtn != null) {
            previewMDBtn.addSelectHandler(handler);
        }
    }

    public void addWrapCbxChangeHandler(ValueChangeHandler<Boolean> changeHandler) {
        cbxWrap.addValueChangeHandler(changeHandler);
    }

    public boolean isWrapText() {
        return cbxWrap.getValue();
    }

}
