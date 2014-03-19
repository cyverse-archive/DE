package org.iplantc.de.client.idroplite.views;

import org.iplantc.de.client.idroplite.util.IDropLiteUtil;
import org.iplantc.de.commons.client.CommonUiConstants;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.SeparatorToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

public class IDropLiteViewImpl implements IDropLiteView {

    private static IDropLiteViewUiBinder uiBinder = GWT.create(IDropLiteViewUiBinder.class);
    private final CommonUiConstants CONSTANTS = GWT.create(CommonUiConstants.class);

    private final Widget widget;

    @UiTemplate("IDropLiteView.ui.xml")
    interface IDropLiteViewUiBinder extends UiBinder<Widget, IDropLiteViewImpl> {
    }

    @UiField
    VerticalLayoutContainer contents;

    private HtmlLayoutContainer htmlApplet;

    @UiField
    ToolBar toolbar;

    @UiField
    TextButton btnSimpleUpld;

    @UiField
    TextButton btnSimpleDwld;

    private Presenter presenter;

    public IDropLiteViewImpl() {
        widget = uiBinder.createAndBindUi(this);
        toolbar.add(new SeparatorToolItem());
        toolbar.add(new LabelToolItem(I18N.DISPLAY.idropJavaInfo(CONSTANTS.idropUrl())));
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @UiHandler("btnSimpleUpld")
    void simpleUploadClicked(SelectEvent event) {
        presenter.onSimpleUploadClick();
    }

    @UiHandler("btnSimpleDwld")
    void simpleDownloadClicked(SelectEvent event) {
        presenter.onSimpleDownloadClick();
    }

    @Override
    public void setPresenter(Presenter p) {
        this.presenter = p;
    }

    @Override
    public void setApplet(HtmlLayoutContainer container) {
        if (htmlApplet != null) {
            contents.remove(htmlApplet);
        }
        this.htmlApplet = container;
        contents.add(htmlApplet);
    }

    @Override
    public int getViewHeight() {
        return contents.getOffsetHeight(true);
    }

    @Override
    public int getViewWidth() {
        return contents.getOffsetWidth(true);
    }

    @Override
    public void setToolBarButton(int mode) {
        if (mode == IDropLiteUtil.DISPLAY_MODE_DOWNLOAD) {
            btnSimpleDwld.setVisible(true);
        } else if (mode == IDropLiteUtil.DISPLAY_MODE_UPLOAD) {
            btnSimpleUpld.setVisible(true);
        }
    }

    @Override
    public void mask() {
        contents.mask(I18N.DISPLAY.loadingMask());
    }

    @Override
    public void unmask() {
        contents.unmask();

    }

    @Override
    public void disableSimpleDownload() {
        btnSimpleDwld.setEnabled(false);
    }
}
