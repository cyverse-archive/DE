package org.iplantc.de.diskResource.client.gin.appearance;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.diskResource.client.gin.TreeCell;
import org.iplantc.de.diskResource.client.search.events.DeleteSavedSearchEvent;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.widget.core.client.tree.TreeSelectionModel;

public class TreeCellDefaultAppearance implements TreeCell.TreeCellAppearance, BeforeSelectionHandler<Folder> {
    interface TreeCellTempates extends XTemplates {
        @XTemplate("<span class='{style.treeCell}'>{name}&nbsp;</span><span class='{style.deleteBtn}' qtip='{imgToolTip}'>&nbsp;x</span>")
        SafeHtml savedQuery(String name, TreeCellStyle style, String imgToolTip);
    }

    public interface TreeCellStyle extends CssResource {
        String deleteBtn();

        String treeCell();
    }

    public interface TreeCellResources extends ClientBundle {
        @Source("TreeCellStyle.css")
        TreeCellStyle css();

        @Source("delete_icon.png")
        ImageResource deleteButton();
    }

    private final TreeCellTempates templates;
    private final TreeCellMessages messages;
    private final TreeCellResources resources;
    private final TreeCellStyle style;
    private HasHandlers hasHandlers;
    private TreeSelectionModel<Folder> selectionModel;
    private boolean suppressSelectionCancel = false;

    public TreeCellDefaultAppearance() {
        this(GWT.<TreeCellResources> create(TreeCellResources.class));
    }

    public TreeCellDefaultAppearance(TreeCellResources resources) {
        this.resources = resources;
        this.style = this.resources.css();
        this.style.ensureInjected();
        templates = GWT.create(TreeCellTempates.class);
        messages = GWT.create(TreeCellMessages.class);
    }

    @Override
    public void render(Context context, Folder value, SafeHtmlBuilder sb) {
        if (value instanceof DiskResourceQueryTemplate) {
            sb.append(templates.savedQuery(value.getName(), style, messages.deleteBtnToolTip()));
        } else {
            // Normal folder
            sb.append(SafeHtmlUtils.fromString(value.getName()));
        }

    }

    @Override
    public void onBrowserEvent(Context context, Element parent, final Folder value, NativeEvent event, ValueUpdater<Folder> valueUpdater) {
        final Element eventTarget = Element.as(event.getEventTarget());
        if (eventTarget.getClassName().equals(style.deleteBtn())) {
            if ((hasHandlers != null) && (value instanceof DiskResourceQueryTemplate)) {
                event.stopPropagation();
                event.preventDefault();
                hasHandlers.fireEvent(new DeleteSavedSearchEvent((DiskResourceQueryTemplate)value));
            }
        } else {
            suppressSelectionCancel = true;
            selectionModel.setSelection(java.util.Collections.singletonList(value));
            suppressSelectionCancel = false;
        }
    }

    @Override
    public void setHasHandlers(HasHandlers hasHandlers) {
        this.hasHandlers = hasHandlers;
    }

    @Override
    public void setSelectionModel(TreeSelectionModel<Folder> selectionModel) {
        this.selectionModel = selectionModel;
        this.selectionModel.addBeforeSelectionHandler(this);

    }

    @Override
    public void onBeforeSelection(BeforeSelectionEvent<Folder> event) {
        if ((event.getItem() instanceof DiskResourceQueryTemplate) && !suppressSelectionCancel) {
            event.cancel();
        }
    }

}
