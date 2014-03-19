package org.iplantc.de.apps.integration.client.presenter.dnd;

import org.iplantc.de.apps.widgets.client.view.AppTemplateForm;
import org.iplantc.de.apps.widgets.client.view.HasLabelOnlyEditMode;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.models.apps.integration.ArgumentGroup;
import org.iplantc.de.client.util.AppTemplateUtils;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsPropertyPanelLabels;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.editor.client.adapters.ListEditor;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Header;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer;

import java.util.List;

/**
 * This <code>DropTarget</code> is responsible for handling
 * {@link org.iplantc.de.client.models.apps.integration.ArgumentGroup} additions to the
 * given {@link com.google.gwt.editor.client.adapters.ListEditor}, as well as handling the auto-expansion
 * of a child <code>ContentPanel</code>s when a drag move containing an
 * {@link org.iplantc.de.client.models.apps.integration.Argument} is detected over a
 * <code>ContentPanel</code>'s header.
 * 
 * TODO JDS Handle DnD Argument additions when drop occurs on a ContentPanel header.
 * 
 * @author jstroot
 * 
 */
public final class ArgGrpListEditorDropTarget extends ContainerDropTarget<AccordionLayoutContainer> {
    private final AppTemplateWizardAppearance appearance = AppTemplateWizardAppearance.INSTANCE;
    private final AppsWidgetsPropertyPanelLabels appsWidgetsDisplay = I18N.APPS_LABELS;
    private int grpCountInt = 2;
    private final HasLabelOnlyEditMode hasLabelOnlyEditMode;
    private Header header;
    private final ListEditor<ArgumentGroup, AppTemplateForm.ArgumentGroupEditor> listEditor;

    public ArgGrpListEditorDropTarget(HasLabelOnlyEditMode hasLabelOnlyEditMode, AccordionLayoutContainer container, ListEditor<ArgumentGroup, AppTemplateForm.ArgumentGroupEditor> editor) {
        super(container);
        this.hasLabelOnlyEditMode = hasLabelOnlyEditMode;
        this.listEditor = editor;
    }

    @Override
    protected void onDragDrop(DndDropEvent event) {
        super.onDragDrop(event);
        List<ArgumentGroup> list = listEditor.getList();
        boolean isNewArgGrp = AutoBeanUtils.getAutoBean((ArgumentGroup) event.getData()).getTag(ArgumentGroup.IS_NEW) != null;
        ArgumentGroup newArgGrp = AppTemplateUtils.copyArgumentGroup((ArgumentGroup) event.getData());

        // Update new group label, if needed
        if (isNewArgGrp) {
            String defaultGroupLabel = appsWidgetsDisplay.groupDefaultLabel(grpCountInt++);
            newArgGrp.setLabel(defaultGroupLabel);
        }

        if (list != null) {
            list.add(insertIndex, newArgGrp);
        }

    }

    @Override
    protected boolean verifyDragData(Object dragData) {
        return (dragData instanceof ArgumentGroup) && super.verifyDragData(dragData) && !hasLabelOnlyEditMode.isLabelOnlyEditMode();
    }

    @Override
    protected boolean verifyDragMove(EventTarget target, Object dragData) {
        XElement conElement = container.getElement();
        Element as = Element.as(target);
        if (Element.is(target) && conElement.isOrHasChild(as)) {
            if (verifyDragData(dragData)) {
                header = null;
                return true;
            } else if (dragData instanceof Argument) {
                IsWidget findWidget = container.findWidget(as);
                if ((findWidget != null)) {
                    boolean isCp = findWidget instanceof ContentPanel;
                    if (isCp && ((ContentPanel)findWidget).getHeader().getElement().isOrHasChild(as)) {
                        final ContentPanel cp = (ContentPanel)findWidget;
                        if(cp.isCollapsed()){
                            header = cp.getHeader();
                            // JDS Kick off timer for autoExpand of ArgumentGroup content panel
                            Timer t = new Timer() {
                                @Override
                                public void run() {
                                    if ((cp.getHeader() == header) && cp.isCollapsed()) {
                                        container.setActiveWidget(cp);
                                    }
                                }
                            };
                            t.schedule(appearance.getAutoExpandOnHoverDelay());

                        }
                    } else {
                        header = null;
                    }
                } else {
                    header = null;
                }
            }
        }

        return super.verifyDragMove(target, dragData);
    }
}
