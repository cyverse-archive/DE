package org.iplantc.de.diskResource.client.views.metadata.dialogs;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;
import org.iplantc.de.diskResource.client.MetadataView;
import org.iplantc.de.diskResource.client.events.TemplateDownloadClickedEvent;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;

/**
 * Created by sriram on 6/27/16.
 */
public class DownloadTemplateCell extends AbstractCell<MetadataTemplateInfo> {

    public interface DownloadTemplateCellAppearance {
        void render(SafeHtmlBuilder sb,
                    String debugId);
    }

    private final DownloadTemplateCellAppearance appearance;
    private final MetadataView.Presenter presenter;

    public DownloadTemplateCell(MetadataView.Presenter presenter) {
        super(CLICK);
        this.presenter = presenter;
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
        if(eventTarget.getId().equals(value.getId())){
            switch (Event.as(event).getTypeInt()) {
                case Event.ONCLICK:
                    doOnClick(eventTarget, value);
                    break;
                default:
                    break;
            }

        }
    }

    private void doOnClick(Element eventTarget, MetadataTemplateInfo value) {
        presenter.downloadTemplate(value.getId());
    }

    @Override
    public void render(Cell.Context context, MetadataTemplateInfo value, SafeHtmlBuilder sb) {
         appearance.render(sb,value.getId());
    }
}
