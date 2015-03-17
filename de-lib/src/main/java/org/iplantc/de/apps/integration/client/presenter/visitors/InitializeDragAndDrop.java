package org.iplantc.de.apps.integration.client.presenter.visitors;

import org.iplantc.de.apps.integration.client.presenter.dnd.ArgGrpListDragSource;
import org.iplantc.de.apps.integration.client.presenter.dnd.ArgGrpListEditorDropTarget;
import org.iplantc.de.apps.integration.client.presenter.dnd.ArgListEditorDragSource;
import org.iplantc.de.apps.integration.client.presenter.dnd.ArgListEditorDropTarget;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm.ArgumentEditorFactory;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm.ArgumentGroupEditor;
import org.iplantc.de.apps.widgets.client.view.HasLabelOnlyEditMode;
import org.iplantc.de.client.models.apps.integration.AppTemplate;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.models.apps.integration.ArgumentGroup;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorContext;
import com.google.gwt.editor.client.EditorVisitor;
import com.google.gwt.editor.client.adapters.ListEditor;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import static com.sencha.gxt.dnd.core.client.DND.Feedback.BOTH;

import com.sencha.gxt.core.client.dom.XElement;


/**
 * 
 * @author jstroot
 * 
 */
public class InitializeDragAndDrop extends EditorVisitor {

    private final HasLabelOnlyEditMode hasLabelOnlyEditMode;
    private XElement scrollElement = null;

    public InitializeDragAndDrop(HasLabelOnlyEditMode hasLabelOnlyEditMode) {
        this.hasLabelOnlyEditMode = hasLabelOnlyEditMode;
    }

    @Override
    public <T> boolean visit(EditorContext<T> ctx) {

        Editor<T> editor = ctx.getEditor();

        if (editor instanceof AppTemplateForm) {
            // Prevent DnD classes from being applied more than once.
            if (ctx.getFromModel() instanceof AppTemplate) {
                AutoBean<T> atAb = AutoBeanUtils.getAutoBean(ctx.getFromModel());
                Boolean hasDnd = atAb.getTag("HAS_DND");
                if ((hasDnd != null) && hasDnd) {
                    return ctx.asLeafValueEditor() == null;
                } else {
                    atAb.setTag("HAS_DND", true);
                }
            }
            AppTemplateForm appTemplateForm = (AppTemplateForm)editor;
            scrollElement = appTemplateForm.getDndContainer().getElement();
            ListEditor<ArgumentGroup, ArgumentGroupEditor> listEditor = appTemplateForm.argumentGroups();
            new ArgGrpListDragSource(appTemplateForm.getDndContainer(), listEditor, hasLabelOnlyEditMode);
            ArgGrpListEditorDropTarget argGrpListEditorDropTarget = new ArgGrpListEditorDropTarget(hasLabelOnlyEditMode, appTemplateForm.getDndContainer(), listEditor);
            argGrpListEditorDropTarget.setFeedback(BOTH);
            argGrpListEditorDropTarget.setAllowSelfAsSource(true);
        } else if (editor instanceof AppTemplateForm.ArgumentGroupEditor) {
            AppTemplateForm.ArgumentGroupEditor argumentListEditor = (AppTemplateForm.ArgumentGroupEditor)editor;
            ListEditor<Argument, ArgumentEditorFactory> asEditor = argumentListEditor.argumentsEditor();
            new ArgListEditorDragSource(argumentListEditor.getDndContainer(), asEditor, hasLabelOnlyEditMode);
            ArgListEditorDropTarget argListEditorDropTarget = new ArgListEditorDropTarget(hasLabelOnlyEditMode, argumentListEditor.getDndContainer(), asEditor, scrollElement);
            argListEditorDropTarget.setFeedback(BOTH);
            argListEditorDropTarget.setAllowSelfAsSource(true);
        }
        return ctx.asLeafValueEditor() == null;
    }


}
