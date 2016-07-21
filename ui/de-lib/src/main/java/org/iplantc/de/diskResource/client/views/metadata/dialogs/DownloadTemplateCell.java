package org.iplantc.de.diskResource.client.views.metadata.dialogs;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;
import org.iplantc.de.diskResource.client.MetadataView;
import org.iplantc.de.diskResource.client.events.TemplateDownloadEvent;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;

/**
 * Created by sriram on 6/27/16.
 */
public class DownloadTemplateCell extends AbstractCell<MetadataTemplateInfo> {

    public interface DownloadTemplateCellAppearance {
        void render(SafeHtmlBuilder sb,
                    String debugId);
        String download();
    }

    private final DownloadTemplateCellAppearance appearance;

    public DownloadTemplateCell() {
        super(CLICK);
        appearance = GWT.create(DownloadTemplateCellAppearance.class);
    }


    @Override
    public void onBrowserEvent(Cell.Context context,
                               Element parent,
                               MetadataTemplateInfo value,
                               NativeEvent event,
                               ValueUpdater<MetadataTemplateInfo> valueUpdater) {
        if (value == null) {
            return;
        }

        Element eventTarget = Element.as(event.getEventTarget());
        Element child = findAppNameElement(parent);
        if (child != null && child.isOrHasChild(eventTarget)) {
            switch (Event.as(event).getTypeInt()) {
                case Event.ONCLICK:
                    doOnClick(eventTarget, value);
                    break;
                default:
                    break;
            }

        }
    }


    private Element findAppNameElement(Element parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            Node childNode = parent.getChild(i);

            if (Element.is(childNode)) {
                Element child = Element.as(childNode);
                if (child.getAttribute("name").equalsIgnoreCase(appearance.download())) { //$NON-NLS-1$
                    return child;
                }
            }
        }
        return null;
    }

    private void doOnClick(Element eventTarget, MetadataTemplateInfo value) {
        EventBus.getInstance().fireEvent(new TemplateDownloadEvent(value.getId()));
    }

    @Override
    public void render(Cell.Context context, MetadataTemplateInfo value, SafeHtmlBuilder sb) {
         appearance.render(sb,value.getId());
    }
}
