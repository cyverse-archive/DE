package org.iplantc.de.apps.integration.client.presenter.dnd;

import org.iplantc.de.apps.widgets.client.view.AppTemplateForm;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm.ArgumentEditorFactory;
import org.iplantc.de.apps.widgets.client.view.HasLabelOnlyEditMode;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.util.AppTemplateUtils;

import com.google.common.base.Strings;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.editor.client.adapters.ListEditor;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.dnd.core.client.DndDragCancelEvent;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.DragSource;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;

public final class ArgListEditorDragSource extends DragSource {

    private final VerticalLayoutContainer container;
    private Argument dragArgument = null;
    private int dragArgumentIndex = -1;
    private final HasLabelOnlyEditMode hasLabelOnlyEditMode;
    private final ListEditor<Argument, AppTemplateForm.ArgumentEditorFactory> listEditor;

    public ArgListEditorDragSource(VerticalLayoutContainer container, ListEditor<Argument, AppTemplateForm.ArgumentEditorFactory> listEditor, HasLabelOnlyEditMode hasLabelOnlyEditMode) {
        super(container);
        this.container = container;
        this.listEditor = listEditor;
        this.hasLabelOnlyEditMode = hasLabelOnlyEditMode;
    }

    /*
     * -- Re-insert the Argument back into the list at the stored index.
     */
    @Override
    protected void onDragCancelled(DndDragCancelEvent event) {

        // JDS Put the Argument back
        listEditor.getList().add(dragArgumentIndex, dragArgument);

        dragArgumentIndex = -1;
        dragArgument = null;

        removePlaceholderArgument();
    }

    /*
     * -- Clear local references of Argument and index
     */
    @Override
    protected void onDragDrop(DndDropEvent event) {
        dragArgumentIndex = -1;
        dragArgument = null;
    }

    /*
     * -- Re-insert the Argument back into the list at the stored index.
     */
    @Override
    protected void onDragFail(DndDropEvent event) {

        // JDS Put the Argument back
        listEditor.getList().add(dragArgumentIndex, dragArgument);

        dragArgumentIndex = -1;
        dragArgument = null;

        removePlaceholderArgument();
    }

    /*
     * -- Find the current drag child, and its index within the list
     * -- Store references to each.
     * -- Add the Argument as the drag data.
     * -- Using the corresponding editor, set the drag shadow
     * -- Remove the argument from this list.
     */
    @Override
    protected void onDragStart(DndDragStartEvent event) {
        EventTarget target = event.getDragStartEvent().getNativeEvent().getEventTarget();
        Element as = Element.as(target);

        // Only want to allow drag start when we are over a child
        IsWidget findWidget = container.findWidget(as);
        if ((findWidget instanceof SimpleContainer) && !hasLabelOnlyEditMode.isLabelOnlyEditMode()) {
            Widget widget2 = ((SimpleContainer)findWidget).getWidget();
            for (ArgumentEditorFactory aef : listEditor.getEditors()) {
                if (aef.getSubEditor() == widget2) {
                    
                    dragArgumentIndex = listEditor.getEditors().indexOf(aef);
                    Argument argument = listEditor.getList().get(dragArgumentIndex);
                    // Only allow drag if it's not the empty grp argument
                    if (Strings.isNullOrEmpty(argument.getId()) || !argument.getId().equalsIgnoreCase(AppTemplateUtils.EMPTY_GROUP_ARG_ID)) {
                        event.getStatusProxy().update(findWidget.asWidget().getElement().getString());
                        event.setCancelled(false);
                        
                        // JDS For now, let's remove on drag start
                        dragArgument = listEditor.getList().remove(dragArgumentIndex);

                        if (listEditor.getList().isEmpty()) {
                            // If it is empty, add the empty group argument
                            listEditor.getList().add(AppTemplateUtils.getEmptyGroupArgument());
                        }
                        event.setData(dragArgument);
                        return;
                    }

                    break;
                }
            }
        }

        // JDS In every other case, clean up and cancel drag.
        dragArgumentIndex = -1;
        dragArgument = null;
        event.setCancelled(true);
        event.getStatusProxy().update("");
    }

    private void removePlaceholderArgument() {
        // JDS Remove placeholder, empty group argument on DnD cancel.
        if (listEditor.getList().size() > 1) {
            Argument argToRemove = null;
            for (Argument arg : listEditor.getList()) {
                if (!Strings.isNullOrEmpty(arg.getId()) && arg.getId().equalsIgnoreCase(AppTemplateUtils.EMPTY_GROUP_ARG_ID)) {
                    argToRemove = arg;
                    break;
                }
            }
            if (argToRemove != null) {
                listEditor.getList().remove(argToRemove);
            }
        }
    }

}
