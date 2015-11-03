package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.client.models.diskResources.File;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

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

    public ImageViewerImpl(final File file,
                           final String imageUrl) {
        super(file, null);
        img = new Image(imageUrl);
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        con.fireEvent(event);
    }

    @Override
    public String getEditorContent() {
        return null;
    }

    @Override
    public void setData(Object data) { /* Do nothing intentionally */ }

    @Override
    public boolean isDirty() {
        return false;
    }
}
