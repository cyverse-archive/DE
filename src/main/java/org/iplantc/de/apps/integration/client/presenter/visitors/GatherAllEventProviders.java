package org.iplantc.de.apps.integration.client.presenter.visitors;

import org.iplantc.de.apps.integration.client.events.DeleteArgumentEvent.DeleteArgumentEventHandler;
import org.iplantc.de.apps.integration.client.events.DeleteArgumentEvent.HasDeleteArgumentEventHandlers;
import org.iplantc.de.apps.integration.client.presenter.dnd.ArgumentWYSIWYGDeleteHandler;
import org.iplantc.de.apps.widgets.client.events.AppTemplateSelectedEvent.HasAppTemplateSelectedEventHandlers;
import org.iplantc.de.apps.widgets.client.events.ArgumentAddedEvent.HasArgumentAddedEventHandlers;
import org.iplantc.de.apps.widgets.client.events.ArgumentGroupAddedEvent.HasArgumentGroupAddedEventHandlers;
import org.iplantc.de.apps.widgets.client.events.ArgumentGroupSelectedEvent.HasArgumentGroupSelectedHandlers;
import org.iplantc.de.apps.widgets.client.events.ArgumentRequiredChangedEvent.HasArgumentRequiredChangedHandlers;
import org.iplantc.de.apps.widgets.client.events.ArgumentSelectedEvent.HasArgumentSelectedEventHandlers;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm.ArgumentEditor;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm.ArgumentGroupEditor;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm.HasDisableValidations;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm.HasDisabledOnNotVisible;
import org.iplantc.de.apps.widgets.client.view.HasLabelOnlyEditMode;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.diskResource.client.views.widgets.DiskResourceSelector.HasDisableBrowseButtons;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorContext;
import com.google.gwt.editor.client.EditorVisitor;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.user.client.Event;

import java.util.List;
import java.util.Map;

/**
 * You will have to traverse twice. Once to get all the things that fire events, second to wire them up
 * to the handlers.
 */
public class GatherAllEventProviders extends EditorVisitor {


    private final AppTemplateWizardAppearance appearance;
    private final DeleteArgumentEventHandler delArgHandler;
    private final List<HasAppTemplateSelectedEventHandlers> hasAppTemplateSelectedHandlers = Lists.newArrayList();

    private final List<HasArgumentGroupAddedEventHandlers> hasArgGrpAddedHandlers = Lists.newArrayList();
    private final List<HasArgumentGroupSelectedHandlers> hasArgGrpSelectedHandlers = Lists.newArrayList();
    private final List<HasArgumentSelectedEventHandlers> hasArgSelectedHandlers = Lists.newArrayList();

    private final List<HasArgumentAddedEventHandlers> hasArgumentAddedHandlers = Lists.newArrayList();

    private final List<HasArgumentRequiredChangedHandlers> hasArgumentRequiredChangedHandlers = Lists.newArrayList();
    private final List<HasDeleteArgumentEventHandlers> hasDeleteArgumentHandlers = Lists.newArrayList();
    private final HasLabelOnlyEditMode hasLabelOnlyEditMode;
    private final Map<String, List<HasValueChangeHandlers<?>>> hasValChangeHandlerMap = Maps.newHashMap();

    public GatherAllEventProviders(AppTemplateWizardAppearance appearance, HasLabelOnlyEditMode hasLabelOnlyEditMode, DeleteArgumentEventHandler delArgHandler) {
        this.appearance = appearance;
        this.hasLabelOnlyEditMode = hasLabelOnlyEditMode;
        this.delArgHandler = delArgHandler;
    }

    public List<HasAppTemplateSelectedEventHandlers> getHasAppTemplateSelectedHandlers() {
        return hasAppTemplateSelectedHandlers;
    }

    public List<HasArgumentGroupAddedEventHandlers> getHasArgGrpAddedHandlers() {
        return hasArgGrpAddedHandlers;
    }

    public List<HasArgumentGroupSelectedHandlers> getHasArgGrpSelectedHandlers() {
        return hasArgGrpSelectedHandlers;
    }

    public List<HasArgumentSelectedEventHandlers> getHasArgSelectedHandlers() {
        return hasArgSelectedHandlers;
    }


    public List<HasArgumentAddedEventHandlers> getHasArgumentAddedHandlers() {
        return hasArgumentAddedHandlers;
    }

    public List<HasArgumentRequiredChangedHandlers> getHasArgumentRequiredChangedHandlers() {
        return hasArgumentRequiredChangedHandlers;
    }

    public List<HasDeleteArgumentEventHandlers> getHasDeleteArgumentHandlers() {
        return hasDeleteArgumentHandlers;
    }

    public Map<String, List<HasValueChangeHandlers<?>>> getHasValChangeHandlerMap() {
        return hasValChangeHandlerMap;
    }

    @Override
    public <T> boolean visit(EditorContext<T> ctx) {
        Editor<T> editor = ctx.getEditor();

        if (editor instanceof HasDisabledOnNotVisible) {
            ((HasDisabledOnNotVisible)editor).disableOnNotVisible();
        }

        if (editor instanceof HasDisableValidations) {
            ((HasDisableValidations)editor).disableValidations();
        }

        if (editor instanceof HasDisableBrowseButtons) {
            ((HasDisableBrowseButtons)editor).disableBrowseButtons();
        }

        if (editor instanceof ArgumentEditor) {
            ArgumentEditor argumentEditor = (ArgumentEditor)editor;
            argumentEditor.asWidget().sinkEvents(Event.MOUSEEVENTS);
        }

        if (editor instanceof ArgumentGroupEditor) {
            ArgumentGroupEditor argumentGroupEditor = (ArgumentGroupEditor)editor;
            argumentGroupEditor.showWhenEmptyOrAllInvisible();
            argumentGroupEditor.asWidget().sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS);
            ArgumentWYSIWYGDeleteHandler argumentWYSIWYGDeleteHandler = new ArgumentWYSIWYGDeleteHandler(appearance, argumentGroupEditor.argumentsEditor(), argumentGroupEditor.getDndContainer(),
                    appearance.getArgListDeleteButton(), hasLabelOnlyEditMode);
            getHasDeleteArgumentHandlers().add(argumentWYSIWYGDeleteHandler);
            getHasArgSelectedHandlers().add(argumentWYSIWYGDeleteHandler);
            argumentWYSIWYGDeleteHandler.addDeleteArgumentEventHandler(delArgHandler);
        }

        // Store AppTemplate-related event providers
        if (editor instanceof HasAppTemplateSelectedEventHandlers) {
            HasAppTemplateSelectedEventHandlers hasAppTemplateSelHandlers = (HasAppTemplateSelectedEventHandlers)editor;
            hasAppTemplateSelectedHandlers.add(hasAppTemplateSelHandlers);
        }
        // Store ArgumentGroup-related event providers
        if (editor instanceof HasArgumentGroupAddedEventHandlers) {
            HasArgumentGroupAddedEventHandlers hasHandlers = (HasArgumentGroupAddedEventHandlers)editor;
            hasArgGrpAddedHandlers.add(hasHandlers);
        }
        if (editor instanceof HasArgumentGroupSelectedHandlers) {
            HasArgumentGroupSelectedHandlers hasArgumentGroupSelectHandlers = (HasArgumentGroupSelectedHandlers)editor;
            hasArgGrpSelectedHandlers.add(hasArgumentGroupSelectHandlers);
        }

        // Store Argument-related event providers
        if (editor instanceof HasArgumentAddedEventHandlers) {
            HasArgumentAddedEventHandlers hasHandlers = (HasArgumentAddedEventHandlers)editor;
            hasArgumentAddedHandlers.add(hasHandlers);
        }
        if (editor instanceof HasArgumentSelectedEventHandlers) {
            HasArgumentSelectedEventHandlers hasArgumentSelectedHandlers = (HasArgumentSelectedEventHandlers)editor;
            hasArgSelectedHandlers.add(hasArgumentSelectedHandlers);
        }
        if (editor instanceof HasDeleteArgumentEventHandlers) {
            HasDeleteArgumentEventHandlers hasHandlers = (HasDeleteArgumentEventHandlers)editor;
            hasDeleteArgumentHandlers.add(hasHandlers);
        }
        if (editor instanceof HasArgumentRequiredChangedHandlers) {
            HasArgumentRequiredChangedHandlers hasHandlers = (HasArgumentRequiredChangedHandlers)editor;
            hasArgumentRequiredChangedHandlers.add(hasHandlers);
        }

        // Grab all the value change event providers
        if(editor instanceof HasValueChangeHandlers){
            String absolutePath = ctx.getAbsolutePath();
            HasValueChangeHandlers<?> hasValChangeHndlr = (HasValueChangeHandlers<?>)editor;
            if(hasValChangeHandlerMap.containsKey(absolutePath)){
                hasValChangeHandlerMap.get(absolutePath).add(hasValChangeHndlr);
            } else {
                hasValChangeHandlerMap.put(absolutePath, Lists.<HasValueChangeHandlers<?>> newArrayList(hasValChangeHndlr));
            }
            
        }

        return ctx.asLeafValueEditor() == null;
    }

}
