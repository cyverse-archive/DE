package org.iplantc.de.diskResource.client.views.metadata.dialogs;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;
import org.iplantc.de.diskResource.client.events.TemplateDownloadEvent;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;

import com.sencha.gxt.widget.core.client.Dialog;

/**
 * Created by sriram on 7/7/16.
 */
public class TemplateNameCell extends AbstractCell<MetadataTemplateInfo> {

    public interface TemplateNameCellAppearance {
        void render(SafeHtmlBuilder sb,
                    MetadataTemplateInfo value);

        String description();

        String background();
    }


    private TemplateNameCellAppearance appearance;

    public TemplateNameCell() {
       super(CLICK);
       appearance = GWT.create(TemplateNameCellAppearance.class);
    }

    @Override
    public void render(Context context, MetadataTemplateInfo value, SafeHtmlBuilder sb) {
        appearance.render(sb,value);
    }

    @Override
    public void onBrowserEvent(Cell.Context context,
                               Element parent,
                               MetadataTemplateInfo value,
                               NativeEvent event,
                               ValueUpdater<MetadataTemplateInfo> valueUpdater) {
        Element eventTarget = Element.as(event.getEventTarget());
        if ((value == null) || !parent.isOrHasChild(eventTarget)) {
            return;
        }


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
                if (child.getAttribute("name").equalsIgnoreCase(appearance.description())) { //$NON-NLS-1$
                    return child;
                }
            }
        }
        return null;
    }

    private void doOnClick(Element eventTarget, MetadataTemplateInfo value) {
        Dialog d = new Dialog();
        d.setSize("500","100");
        d.setHideOnButtonClick(true);
        d.setHeadingText(appearance.description());
        HTML desc = new HTML(value.getDescription());
        desc.setStylePrimaryName(appearance.background());
        d.add(desc);
        d.show();
    }
}
