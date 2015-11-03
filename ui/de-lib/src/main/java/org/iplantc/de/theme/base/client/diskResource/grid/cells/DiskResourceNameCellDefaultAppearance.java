package org.iplantc.de.theme.base.client.diskResource.grid.cells;

import org.iplantc.de.diskResource.client.views.grid.cells.DiskResourceNameCell;
import org.iplantc.de.theme.base.client.diskResource.grid.GridViewDisplayStrings;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;

import com.sencha.gxt.widget.core.client.Popup;

/**
 * @author jstroot
 */
public class DiskResourceNameCellDefaultAppearance implements DiskResourceNameCell.Appearance {

    public static interface CellStyle extends CssResource {

        String drFile();

        String drFileTrash();

        String drFolder();

        String drFolderTrash();

        String nameDisabledStyle();

        String nameStyle();

        String nameStyleNoPointer();

        String pathList();
    }

    public interface Resources extends ClientBundle {
        @Source("../../file.gif")
        ImageResource file();

        @Source("../../folder.gif")
        ImageResource folder();

        @Source("../../file_delete.gif")
        ImageResource fileDelete();

        @Source("../../folder_delete.gif")
        ImageResource folderDelete();

        @Source("../../list-ingredients-16.png")
        ImageResource pathList();

        @Source("DiskResourceNameCell.css")
        CellStyle css();
    }

    interface Templates extends SafeHtmlTemplates {

        @Template("<span></span><span class='{0}'> </span>&nbsp;<span name=\"drName\" title='{2}' class='{1}' >{2}</span></span>")
        SafeHtml cell(String imgClassName, String diskResourceClassName, String diskResourceName);

        @Template("<span><span class='{0}'> </span>&nbsp;<span id='{3}' name=\"drName\" title='{2}' class='{1}' >{2}</span></span>")
        SafeHtml debugCell(String imgClassName,
                           String diskResourceClassName,
                           String diskResourceName,
                           String id);
    }

    private final CellStyle css;
    private final GridViewDisplayStrings displayStrings;
    private final Templates templates;

    public DiskResourceNameCellDefaultAppearance() {
        this(GWT.<GridViewDisplayStrings>create(GridViewDisplayStrings.class),
             GWT.<Resources>create(Resources.class),
             GWT.<Templates>create(Templates.class));
    }

    DiskResourceNameCellDefaultAppearance(final GridViewDisplayStrings displayStrings,
                                          final Resources resources,
                                          final Templates templates) {
        this.displayStrings = displayStrings;
        this.css = resources.css();
        this.templates = templates;

        this.css.ensureInjected();
    }

    @Override
    public String diskResourceNotAvailable() {
        return displayStrings.diskResourceNotAvailable();
    }

    @Override
    public String drFileClass() {
        return css.drFile();
    }

    @Override
    public String drFileTrashClass() {
        return css.drFileTrash();
    }

    @Override
    public String drFolderClass() {
        return css.drFolder();
    }

    @Override
    public String drFolderTrashClass() {
        return css.drFolderTrash();
    }

    @Override
    public Popup getFilteredDiskResourcePopup() {
        Popup popup = new Popup();
        popup.setBorders(true);
        popup.getElement().getStyle().setBackgroundColor("#F8F8F8");
        popup.getElement().getStyle().setFontSize(11, Style.Unit.PX);
        popup.add(new HTML(displayStrings.diskResourceNotAvailable()));
        popup.setSize("300px", "150px");
        return popup;
    }

    @Override
    public String nameDisabledStyle() {
        return css.nameDisabledStyle();
    }

    @Override
    public String nameStyle() {
        return css.nameStyle();
    }

    @Override
    public String nameStyleNoPointer() {
        return css.nameStyleNoPointer();
    }

    @Override
    public String pathListClass() {
        return css.pathList();
    }

    @Override
    public void render(SafeHtmlBuilder sb, String imgClassName, String nameStyle, String name,
                       String baseID, String debugId) {

        if (DebugInfo.isDebugIdEnabled() && !Strings.isNullOrEmpty(baseID)) {
            sb.append(templates.debugCell(imgClassName, nameStyle, name, debugId));
        } else {
            sb.append(templates.cell(imgClassName, nameStyle, name));
        }
    }
}
