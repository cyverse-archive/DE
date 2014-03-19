package org.iplantc.de.apps.integration.client.presenter.visitors;

import org.iplantc.de.apps.widgets.client.events.ArgumentGroupSelectedEvent;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm.ArgumentGroupEditor;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.client.models.apps.integration.ArgumentGroup;

import com.google.gwt.editor.client.EditorContext;
import com.google.gwt.editor.client.EditorVisitor;
import com.google.gwt.editor.client.adapters.ListEditor;

/**
 * This class is responsible for completely removing a given ArgumentGroup from an editor hierarchy.
 * 
 * @author jstroot
 * 
 */
public class DeleteArgumentGroup extends EditorVisitor {

    private final AppTemplateWizardAppearance appearance;
    private final ArgumentGroup argGrp;

    public DeleteArgumentGroup(ArgumentGroup argGrp, AppTemplateWizardAppearance appearance) {
        this.argGrp = argGrp;
        this.appearance = appearance;
    }

    @Override
    public <T> void endVisit(EditorContext<T> ctx) {
        if(ctx.getEditor() instanceof AppTemplateForm){
            AppTemplateForm appTemplateForm = (AppTemplateForm)ctx.getEditor();
            ListEditor<ArgumentGroup, ArgumentGroupEditor> argumentGroups = appTemplateForm.argumentGroups();
            int indexRemoved = argumentGroups.getList().indexOf(argGrp);
            argumentGroups.getList().remove(argGrp);
            if (argumentGroups.getList().isEmpty()) {
                appTemplateForm.asWidget().fireEvent(new ArgumentGroupSelectedEvent(null));
            } else {
                int index = (indexRemoved > 0) ? indexRemoved - 1 : 0;
                AppTemplateForm.ArgumentGroupEditor toBeSelected = argumentGroups.getEditors().get(index);
                appTemplateForm.asWidget().fireEvent(new ArgumentGroupSelectedEvent(argGrp));
                toBeSelected.asWidget().addStyleName(appearance.getStyle().appHeaderSelect());
            }
        }
    }
}
