package org.iplantc.de.theme.base.client.diskResource.grid.cells;

import org.iplantc.de.diskResource.client.views.grid.cells.DiskResourcePathCell;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author jstroot
 */
public class DiskResourcePathCellDefaultAppearance implements DiskResourcePathCell.Appearance {

    public interface PathCellStyle extends CssResource {
        String pathCell();
    }

    public interface Resources extends ClientBundle {

        @Source("PathCellStyle.css")
        PathCellStyle css();
    }

    interface Templates extends SafeHtmlTemplates {

        @Template("<span name=\"drPath\" title='{0}' class='{1}' >{0}</span>")
        SafeHtml cell(String path, String className);

        @Template("<span id='{1}' title='{0}' name=\"drPath\" class='{2}'>{0}</span>")
        SafeHtml debugCell(String path,
                           String id,
                           String className);
    }

    private final Templates templates;
    private final PathCellStyle css;

    public DiskResourcePathCellDefaultAppearance() {
        this(GWT.<Templates> create(Templates.class),
             GWT.<Resources> create(Resources.class));
    }

    DiskResourcePathCellDefaultAppearance(final Templates templates,
                                          final Resources resources) {
        this.templates = templates;
        this.css = resources.css();

        this.css.ensureInjected();
    }


    @Override
    public void render(final SafeHtmlBuilder sb,
                       final String path,
                       final String baseID,
                       final String debugId) {

        if (DebugInfo.isDebugIdEnabled() && !Strings.isNullOrEmpty(baseID)) {
            sb.append(templates.debugCell(path, debugId, css.pathCell()));
        } else {
            sb.append(templates.cell(path, css.pathCell()));
        }
    }
}
