package org.iplantc.de.apps.integration.client.presenter.dnd;

import org.iplantc.de.apps.widgets.client.view.AppTemplateForm;
import org.iplantc.de.apps.widgets.client.view.HasLabelOnlyEditMode;
import org.iplantc.de.client.models.apps.integration.ArgumentGroup;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.editor.client.adapters.ListEditor;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.dnd.core.client.DndDragCancelEvent;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.DragSource;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer;

import java.util.List;

public final class ArgGrpListDragSource extends DragSource {

    private final AccordionLayoutContainer container;
    private ArgumentGroup dragArgGrp = null;
    private int dragArgGrpIndex = -1;
    private final HasLabelOnlyEditMode hasLabelOnlyEditMode;
    private final ListEditor<ArgumentGroup, AppTemplateForm.ArgumentGroupEditor> listEditor;

    public ArgGrpListDragSource(AccordionLayoutContainer container, ListEditor<ArgumentGroup, AppTemplateForm.ArgumentGroupEditor> listEditor, HasLabelOnlyEditMode hasLabelOnlyEditMode) {
        super(container);
        this.container = container;
        this.listEditor = listEditor;
        this.hasLabelOnlyEditMode = hasLabelOnlyEditMode;
    }

    /*
     * -- Re-insert the ArgumentGroup back into the list at the stored index.
     */
    @Override
    protected void onDragCancelled(DndDragCancelEvent event) {

        // JDS Put the ArgumentGroup back
        listEditor.getList().add(dragArgGrpIndex, dragArgGrp);

        dragArgGrpIndex = -1;
        dragArgGrp = null;
    }

    /*
     * -- Clear local references of Argument and index
     */
    @Override
    protected void onDragDrop(DndDropEvent event) {
        dragArgGrpIndex = -1;
        dragArgGrp = null;
    }

    /*
     * -- Re-insert the ArgumentGroup back into the list at the stored index.
     */
    @Override
    protected void onDragFail(DndDropEvent event) {

        // JDS Put the ArgumentGroup back
        listEditor.getList().add(dragArgGrpIndex, dragArgGrp);

        dragArgGrpIndex = -1;
        dragArgGrp = null;
    }

    @Override
    protected void onDragStart(DndDragStartEvent event) {
        EventTarget target = event.getDragStartEvent().getNativeEvent().getEventTarget();
        Element as = Element.as(target);

        // Only want to allow drag start when we are over a child
        IsWidget findWidget = container.findWidget(as);
        List<AppTemplateForm.ArgumentGroupEditor> editors = listEditor.getEditors();
        boolean contains = editors.contains(findWidget);
        if ((findWidget != null) && contains && ((ContentPanel)findWidget.asWidget()).getHeader().getElement().isOrHasChild(as) && !hasLabelOnlyEditMode.isLabelOnlyEditMode()) {
            event.getStatusProxy().update(((ContentPanel)findWidget.asWidget()).getHeader().getElement().getString());
            event.setCancelled(false);

            dragArgGrpIndex = editors.indexOf(findWidget);
            // JDS For now, let's remove on drag start
            dragArgGrp = listEditor.getList().remove(dragArgGrpIndex);
            event.setData(dragArgGrp);

        } else {

            dragArgGrpIndex = -1;
            dragArgGrp = null;
            event.setCancelled(true);
            event.getStatusProxy().update("");
        }
    }

}
