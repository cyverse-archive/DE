package org.iplantc.de.apps.widgets.client.view.editors;

import org.iplantc.de.apps.widgets.client.events.AppTemplateSelectedEvent;
import org.iplantc.de.apps.widgets.client.events.AppTemplateSelectedEvent.AppTemplateSelectedEventHandler;
import org.iplantc.de.apps.widgets.client.events.ArgumentGroupAddedEvent;
import org.iplantc.de.apps.widgets.client.events.ArgumentGroupAddedEvent.ArgumentGroupAddedEventHandler;
import org.iplantc.de.apps.widgets.client.events.ArgumentGroupSelectedEvent;
import org.iplantc.de.apps.widgets.client.events.ArgumentGroupSelectedEvent.ArgumentGroupSelectedEventHandler;
import org.iplantc.de.apps.widgets.client.events.ArgumentSelectedEvent;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm;
import org.iplantc.de.apps.widgets.client.view.LaunchAnalysisView;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.client.models.apps.integration.ArgumentGroup;

import com.google.common.collect.Lists;
import com.google.gwt.editor.client.adapters.EditorSource;
import com.google.gwt.editor.client.adapters.ListEditor;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;
import com.google.inject.Provider;

import static com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode.AUTOY;
import static com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.ExpandMode.SINGLE;

import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.CollapseHandler;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.ExpandHandler;

import java.util.List;

/**
 * A wizard for editing <code>AppTemplate</code>s.
 * 
 * @author jstroot
 * 
 */
public class AppTemplateFormImpl extends Composite implements AppTemplateForm {
    
    /**
     * The EditorSource class for adding indiv. ArgumentGroups to the ui.
     * 
     * Each ArgumentGroupEditor must be added/inserted into the accordion container using
     * asWidget(). The ArgumentGroupEditor.asWidget() method returns a ContentPanel, which
     * is the only widget that can be added to an accordion panel.
     * 
     * @author jstroot
     * 
     */
    private class ArgumentGroupEditorSource extends EditorSource<AppTemplateForm.ArgumentGroupEditor> {

        private final Provider<ArgumentGroupEditor> argGrpEditorProvider;
        private final List<CollapseHandler> collapseHandlers = Lists.newArrayList();

        private final AccordionLayoutContainer con;
        private final List<ExpandHandler> expandHandlers = Lists.newArrayList();

        public ArgumentGroupEditorSource(AccordionLayoutContainer con, Provider<ArgumentGroupEditor> argGrpEditorProvider) {
            this.con = con;
            this.argGrpEditorProvider = argGrpEditorProvider;
        }

        @Override
        public AppTemplateForm.ArgumentGroupEditor create(int index) {
            final AppTemplateForm.ArgumentGroupEditor subEditor = argGrpEditorProvider.get();
            subEditor.setCollapsible(true);
            for (CollapseHandler h : collapseHandlers) {
                subEditor.addCollapseHandler(h);
            }
            for (ExpandHandler h : expandHandlers) {
                subEditor.addExpandHandler(h);
            }
            con.insert(subEditor, index);

            if (index == 0) {
                // Ensure that the first container is expanded automatically
                con.setActiveWidget(subEditor.asWidget());
            }


            AppTemplateFormImpl.this.fireEvent(new ArgumentGroupAddedEvent(listEditor.getList().get(index), subEditor));
            return subEditor;
        }

        @Override
        public void dispose(AppTemplateForm.ArgumentGroupEditor subEditor) {
            subEditor.asWidget().removeFromParent();
        }

        @Override
        public void setIndex(AppTemplateForm.ArgumentGroupEditor editor, int index) {
            con.insert(editor, index);
        }

    }
    private final AppTemplateWizardAppearance appearance;

    private final AccordionLayoutContainer groupsContainer;
    private final ListEditor<ArgumentGroup, AppTemplateForm.ArgumentGroupEditor> listEditor;

    private final VerticalLayoutContainer vlc;

    @Inject
    public AppTemplateFormImpl(AppTemplateWizardAppearance appearance, Provider<AppTemplateForm.ArgumentGroupEditor> argGrpEditorProvider) {
        this.appearance = appearance;

        groupsContainer = new AccordionLayoutContainer();
        groupsContainer.setExpandMode(SINGLE);
        listEditor = ListEditor.of(new ArgumentGroupEditorSource(groupsContainer, argGrpEditorProvider));
        vlc = new VerticalLayoutContainer();
        vlc.setScrollMode(AUTOY);
        vlc.setAdjustForScroll(true);

        vlc.add(groupsContainer, new VerticalLayoutData(1.0, -1.0));
        initWidget(vlc);

    }

    @Override
    public HandlerRegistration addAppTemplateSelectedEventHandler(AppTemplateSelectedEventHandler handler) {
        return addHandler(handler, AppTemplateSelectedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addArgumentGroupAddedEventHandler(ArgumentGroupAddedEventHandler handler) {
        return addHandler(handler, ArgumentGroupAddedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addArgumentGroupSelectedHandler(ArgumentGroupSelectedEventHandler handler) {
        return addHandler(handler, ArgumentGroupSelectedEvent.TYPE);
    }

    @Override
    public ListEditor<ArgumentGroup, AppTemplateForm.ArgumentGroupEditor> argumentGroups() {
        return listEditor;
    }

    @Override
    public void collapseAllArgumentGroups() {
        for (Object aGroupsContainer : groupsContainer) {
            ContentPanel cp = (ContentPanel)aGroupsContainer;
            cp.collapse();
        }
    }

    @Override
    public AccordionLayoutContainer getDndContainer() {
        return groupsContainer;
    }

    @Override
    public void insertFirstInAccordion(LaunchAnalysisView law) {
        groupsContainer.insert(law, 0);
        groupsContainer.setActiveWidget(law.asWidget());
    }

    @Override
    public void onAppTemplateSelected(AppTemplateSelectedEvent event) {
        clearSelectionStyles(event.getSource());
    }

    @Override
    public void onArgumentGroupSelected(ArgumentGroupSelectedEvent event) {
        clearSelectionStyles(event.getSource());
    }

    @Override
    public void onArgumentSelected(ArgumentSelectedEvent event) {
        clearSelectionStyles(event.getSource());
    }

    @Override
    public void setAdjustForScroll(boolean adjustForScroll) {
        vlc.setAdjustForScroll(adjustForScroll);
    }

    void clearSelectionStyles(Object object) {
        if (listEditor == null) {
            return;
        }

        for (AppTemplateForm.ArgumentGroupEditor age : listEditor.getEditors()) {
            if (age == object) {
                age.getHeader().addStyleName(appearance.getStyle().appHeaderSelect());
            } else {
                age.getHeader().removeStyleName(appearance.getStyle().appHeaderSelect());
            }
        }

    }

}
