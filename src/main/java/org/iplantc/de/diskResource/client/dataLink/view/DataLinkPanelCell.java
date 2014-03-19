package org.iplantc.de.diskResource.client.dataLink.view;

import org.iplantc.de.client.models.dataLink.DataLink;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.resources.client.DataLinkPanelCellStyle;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.I18N;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Event;

final class DataLinkPanelCell<M extends DiskResource> extends AbstractCell<M> {

    interface Templates extends SafeHtmlTemplates {

        // TODO JDS The image which would be clicked on for copy to clipboard would be appended to the
        // following template definition.
        @SafeHtmlTemplates.Template("<span name=\"del\" class=\"{0}\" qtip=\"{1}\"></span><span style=\"float: left;\">&nbsp; {2} &nbsp;</span> <span id=\"{3}\" class=\"{4}\"></span>")
        SafeHtml dataLinkCellWithCopyIcon(String delImgClassName, String delImgToolTip,
                SafeHtml urlText, String copyToClipId, String copyImg);

        @SafeHtmlTemplates.Template("<span name=\"del\" class=\"{0}\" qtip=\"{1}\"></span><span style=\"float: left;\">&nbsp; {2} &nbsp;</span>")
        SafeHtml dataLinkCell(String delImgClassName, String delImgToolTip, SafeHtml urlText);

        @SafeHtmlTemplates.Template("<span name=\"fileIcon\" class=\"{0}\"></span> <span>&nbsp; {1}</span>")
        SafeHtml diskResCell(String fileIconImgClass, SafeHtml fileName);
    }

    private final Templates templates = GWT.create(Templates.class);
    private final DataLinkPanel.Presenter<M> presenter;
    private final DataLinkPanelCellStyle dataLinkCss;

    DataLinkPanelCell(DataLinkPanel.Presenter<M> presenter) {
        super(CLICK);
        this.presenter = presenter;
        dataLinkCss = IplantResources.RESOURCES.getDataLinkCss();
        dataLinkCss.ensureInjected();
    }

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, M value, SafeHtmlBuilder sb) {

        if (value instanceof DataLink) {
            SafeHtml dataLinkText = SafeHtmlUtils.fromString(((DataLink)value).getDownloadPageUrl());
            // sb.append(templates.dataLinkCellWithCopyIcon(dataLinkCss.dataLinkDelete(),
            // I18N.DISPLAY.deleteDataLinkToolTip(), dataLinkText, copyToClipId,
            // dataLinkCss.pasteIcon()));
            sb.append(templates.dataLinkCell(dataLinkCss.dataLinkDelete(),
                    I18N.DISPLAY.deleteDataLinkToolTip(), dataLinkText));

        } else if (value instanceof File) {
            sb.append(templates.diskResCell(dataLinkCss.dataLinkFileIcon(),
                    SafeHtmlUtils.fromString(value.getName())));
        } else {

        }

    }

    @Override
    public void onBrowserEvent(Cell.Context context, Element parent, M value, NativeEvent event,
            ValueUpdater<M> valueUpdater) {

        if (value == null) {
            return;
        }

        Element eventTarget = Element.as(event.getEventTarget());
        if (parent.isOrHasChild(eventTarget)) {

            switch (Event.as(event).getTypeInt()) {
                case Event.ONCLICK:
                    doOnClick(eventTarget, value, valueUpdater);
                    break;
                default:
                    break;
            }
        }
    }

    private void doOnClick(Element eventTarget, M value, ValueUpdater<M> valueUpdater) {
        if (eventTarget.getAttribute("name").equalsIgnoreCase("del")) {
            presenter.deleteDataLink((DataLink)value);
        }

    }

}