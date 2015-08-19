package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.client.events.FileSavedEvent;
import org.iplantc.de.client.models.diskResources.File;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * @author jstroot
 */
public class MarkDownRendererViewImpl extends AbstractFileViewer {

    public interface MarkdownRendererViewAppearance {
        String backgroundColor();

        ImageResource saveBtnIcon();

        String saveBtnText();
    }

    @UiTemplate("MarkDownRendererView.ui.xml")
    interface MarkDownRendererViewUiBinder extends UiBinder<Widget, MarkDownRendererViewImpl> { }

    @UiField HtmlLayoutContainer panel;
    @UiField ToolBar toolbar;
    @UiField TextButton saveBtn;
    @UiField VerticalLayoutContainer con;

    private static MarkDownRendererViewUiBinder uiBinder = GWT.create(MarkDownRendererViewUiBinder.class);
    @UiField(provided = true) MarkdownRendererViewAppearance appearance = GWT.create(MarkdownRendererViewAppearance.class);
    private final String previewData;
    private final Presenter presenter;
    private String renderHtml;

    public MarkDownRendererViewImpl(final File file,
                                    final String infoType,
                                    final String previewData,
                                    final Presenter presenter) {
        super(file, infoType);
        this.previewData = previewData;
        this.presenter = presenter;
        initWidget(uiBinder.createAndBindUi(this));
        panel.getElement().getStyle().setBackgroundColor(appearance.backgroundColor());
        panel.getElement().getStyle().setOverflow(Overflow.SCROLL);
        if (file == null) {
            saveBtn.disable();
        } else {
            saveBtn.enable();
        }
    }

    @UiHandler("saveBtn")
    void onSaveBtnSelect(SelectEvent event){
        presenter.saveFileWithExtension(this,
                                        renderHtml,
                                        ".html");
    }

    public static native String render(String val) /*-{
		var markdown = $wnd.Markdown.getSanitizingConverter();
		return markdown.makeHtml(val);
    }-*/;

    @Override
    public HandlerRegistration addFileSavedEventHandler(final FileSavedEvent.FileSavedEventHandler handler) {
        return asWidget().addHandler(handler, FileSavedEvent.TYPE);
    }

    @Override
    public String getEditorContent() {
        return null;
    }

    @Override
    public void setData(Object data) {/* Do nothing intentionally */}

    @UiFactory
    HtmlLayoutContainer buildHtmlContainer() {
        // FIXME Roll into appearance
        renderHtml = render(previewData);
        return new HtmlLayoutContainer("<link href=\"./markdown.css\" rel=\"stylesheet\"></link><div class=\"markdown\">"
                                           + renderHtml + "</div>");
    }

    @Override
    public boolean isDirty() {
        return false;
    }

}
