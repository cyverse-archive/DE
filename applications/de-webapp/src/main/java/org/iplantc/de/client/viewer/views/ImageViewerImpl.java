package org.iplantc.de.client.viewer.views;

import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.diskResources.File;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;

/**
 * @author sriram, jstroot
 */
public class ImageViewerImpl extends AbstractFileViewer {

    @UiTemplate("ImageViewer.ui.xml")
    interface ImageViewerUiBinder extends UiBinder<Widget, ImageViewerImpl> { }

    @UiField
    VerticalLayoutContainer con;
    @UiField(provided = true)
    Image img;

    private static ImageViewerUiBinder uiBinder = GWT.create(ImageViewerUiBinder.class);
    private final Widget widget;

    public ImageViewerImpl(File file) {
        super(file, null);
        img = new Image(ServicesInjector.INSTANCE.getFileEditorServiceFacade()
                                                 .getServletDownloadUrl(this.file.getPath()));
        widget = uiBinder.createAndBindUi(this);
        con.setScrollMode(ScrollMode.AUTO);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void loadData() {  /* Do nothing intentionally */ }

    @Override
    public void refresh() { /* Do nothing intentionally */ }

    @Override
    public void setData(Object data) { /* Do nothing intentionally */ }
}
