package org.iplantc.de.apps.integration.client.presenter.dnd;

import org.iplantc.de.apps.widgets.client.view.AppTemplateForm;
import org.iplantc.de.apps.widgets.client.view.HasLabelOnlyEditMode;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.models.apps.integration.ArgumentType;
import org.iplantc.de.client.util.AppTemplateUtils;

import com.google.common.base.Strings;
import com.google.gwt.editor.client.adapters.ListEditor;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;

import java.util.List;

/**
 * A drop target class which handles the addition of new arguments to this editor's ListEditor.
 *
 * @author jstroot
 *
 */
public final class ArgListEditorDropTarget extends ContainerDropTarget<VerticalLayoutContainer> {

    private final AppTemplateWizardAppearance appearance = AppTemplateWizardAppearance.INSTANCE;
    private int argCountInt = 1;
    private final HasLabelOnlyEditMode hasLabelOnlyEditMode;
    private final ListEditor<Argument, AppTemplateForm.ArgumentEditorFactory> listEditor;

    public ArgListEditorDropTarget(HasLabelOnlyEditMode hasLabelOnlyEditMode, VerticalLayoutContainer container, ListEditor<Argument, AppTemplateForm.ArgumentEditorFactory> editor,
            XElement scrollElement) {
        super(container, scrollElement);
        this.hasLabelOnlyEditMode = hasLabelOnlyEditMode;
        this.listEditor = editor;
        setScrollDelay(appearance.getAutoScrollDelay());
        setScrollRegionHeight(appearance.getAutoScrollRegionHeight());
        setScrollRepeatDelay(appearance.getAutoScrollRepeatDelay());
    }

    @Override
    protected void onDragDrop(DndDropEvent event) {
        super.onDragDrop(event);

        List<Argument> list = listEditor.getList();
        Argument data = (Argument)event.getData();
        AutoBean<Argument> argumentBean = AutoBeanUtils.getAutoBean(data);
        boolean isNewArg = argumentBean.getTag(Argument.IS_NEW) != null;

        // Update new argument label if needed.
        if (isNewArg) {
            String label = data.getLabel() + " - " + argCountInt++; //$NON-NLS-1$
            data.setLabel(label);
            argumentBean.setTag(Argument.IS_NEW, null);
        }

        if (list != null) {
            // JDS Protect against OBOB issues (caused by argument delete button, which is actually a
            // child of the argumentsContainer
            if (insertIndex >= list.size()) {
                list.add(data);
            } else {
                list.add(insertIndex, data);
            }

            // JDS Remove placeholder, empty group argument on DnD add.
            if (list.size() > 1) {
                Argument argToRemove = null;
                for (Argument arg : list) {
                    if (!Strings.isNullOrEmpty(arg.getId()) && arg.getId().equalsIgnoreCase(AppTemplateUtils.EMPTY_GROUP_ARG_ID)) {
                        argToRemove = arg;
                        break;
                    }
                }
                if (argToRemove != null) {
                    list.remove(argToRemove);
                }
            }
        }
    }

    @Override
    protected boolean verifyDragData(Object dragData) {
        boolean labelOnlyEditMode = hasLabelOnlyEditMode.isLabelOnlyEditMode();
        // Only accept drag data which is an Argument
        return (dragData instanceof Argument) && super.verifyDragData(dragData)
 && (!labelOnlyEditMode || (labelOnlyEditMode && ((Argument)dragData).getType().equals(ArgumentType.Info)));
    }
}
