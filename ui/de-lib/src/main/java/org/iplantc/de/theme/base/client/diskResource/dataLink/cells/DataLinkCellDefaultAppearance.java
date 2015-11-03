package org.iplantc.de.theme.base.client.diskResource.dataLink.cells;

import org.iplantc.de.client.models.dataLink.DataLink;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.diskResource.client.views.dataLink.cells.DataLinkPanelCell;
import org.iplantc.de.theme.base.client.diskResource.dataLink.DataLinkMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * @author jstroot
 */
public class DataLinkCellDefaultAppearance implements DataLinkPanelCell.Appearance {

    public interface DataLinkCellStyle extends CssResource {

        String dataLinkDelete();

        String dataLinkFileIcon();
    }

    public interface Resources extends ClientBundle {
        @Source("DataLinkCellStyle.css")
        DataLinkCellStyle css();

        @Source("../../link_delete.png")
        ImageResource linkDelete();

        @Source("../../file.gif")
        ImageResource file();
    }

    interface Templates extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("<span name='del' class='{0}' qtip='{1}'></span><span style='float: left;'>&nbsp; {2} &nbsp;</span>")
        SafeHtml dataLinkCell(String delImgClassName, String delImgToolTip, SafeHtml urlText);

        @SafeHtmlTemplates.Template("<span name='fileIcon' class='{0}'></span> <span>&nbsp; {1}</span>")
        SafeHtml diskResCell(String fileIconImgClass, SafeHtml fileName);
    }

    private final Templates templates;
    private final DataLinkMessages displayMessages;
    private final DataLinkCellStyle css;

    public DataLinkCellDefaultAppearance() {
        this(GWT.<Templates> create(Templates.class),
             GWT.<DataLinkMessages> create(DataLinkMessages.class),
             GWT.<Resources> create(Resources.class));
    }

    DataLinkCellDefaultAppearance(final Templates templates,
                                  final DataLinkMessages displayMessages,
                                  final Resources resources) {
        this.templates = templates;
        this.displayMessages = displayMessages;
        this.css = resources.css();
        css.ensureInjected();
    }


    @Override
    public void render(SafeHtmlBuilder sb, DiskResource value) {
          if (value instanceof DataLink) {
            SafeHtml dataLinkText = SafeHtmlUtils.fromString(((DataLink) value).getDownloadPageUrl());
            sb.append(templates.dataLinkCell(css.dataLinkDelete(),
                                             displayMessages.deleteDataLinkToolTip(), dataLinkText));

        } else if (value instanceof File) {
            sb.append(templates.diskResCell(css.dataLinkFileIcon(),
                                            SafeHtmlUtils.fromString(value.getName())));
        }

    }
}
