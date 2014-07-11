package org.iplantc.de.client.viewer.views;

import org.iplantc.de.client.callbacks.FileSaveCallback;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

public class MarkDownRendererViewImpl extends AbstractFileViewer {

    private VerticalLayoutContainer vlc;

    @UiField
    HtmlLayoutContainer panel;

    @UiField
    ToolBar toolbar;

    private static MarkDownRendererViewUiBinder uiBinder = GWT.create(MarkDownRendererViewUiBinder.class);

    @UiTemplate("MarkDownRendererView.ui.xml")
    interface MarkDownRendererViewUiBinder extends UiBinder<Widget, MarkDownRendererViewImpl> {
    }
    
    private final Widget widget;

    private final TextButton saveBtn;

    private String renderHtml;

    private final String previewData;

    public MarkDownRendererViewImpl(File file, String infoType, String previewData) {
        super(file, infoType);
        this.previewData = previewData;
        widget = uiBinder.createAndBindUi(this);
        panel.getElement().getStyle().setBackgroundColor("#ffffff");
        panel.getElement().getStyle().setOverflow(Overflow.SCROLL);
        saveBtn = new TextButton(I18N.DISPLAY.save(), IplantResources.RESOURCES.save());
        saveBtn.addSelectHandler(new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                panel.mask();
                String destination = MarkDownRendererViewImpl.this.file.getPath() + ".html";
                ServicesInjector.INSTANCE.getFileEditorServiceFacade()
                                         .uploadTextAsFile(destination,
                                                           renderHtml,
                                                           true,
                                                           new FileSaveCallback(destination,
                                                                                true,
                                                                                MarkDownRendererViewImpl.this.panel));
                
            }
        });
        toolbar.add(saveBtn);
        if (file == null) {
            saveBtn.disable();
        } else {
            saveBtn.enable();
        }
    }

    @Override
    public void refresh() {
        // TODO Auto-generated method stub

    }

    @UiFactory
    HtmlLayoutContainer buildHtmlContainer() {
        renderHtml = render(previewData);
        return new HtmlLayoutContainer(renderHtml);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setData(Object data) {

    }

    @Override
    public void loadData() {
        // TODO Auto-generated method stub

    }

    public static native String render(String val) /*-{
		var markdown = $wnd.Markdown.getSanitizingConverter();
		return markdown.makeHtml(val);
    }-*/;

}
