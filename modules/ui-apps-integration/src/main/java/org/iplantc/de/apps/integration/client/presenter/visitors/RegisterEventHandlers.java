package org.iplantc.de.apps.integration.client.presenter.visitors;

import org.iplantc.de.apps.integration.client.events.DeleteArgumentEvent.DeleteArgumentEventHandler;
import org.iplantc.de.apps.integration.client.events.DeleteArgumentEvent.HasDeleteArgumentEventHandlers;
import org.iplantc.de.apps.integration.client.view.AppsEditorView;
import org.iplantc.de.apps.widgets.client.events.AppTemplateSelectedEvent.AppTemplateSelectedEventHandler;
import org.iplantc.de.apps.widgets.client.events.AppTemplateSelectedEvent.HasAppTemplateSelectedEventHandlers;
import org.iplantc.de.apps.widgets.client.events.ArgumentAddedEvent.ArgumentAddedEventHandler;
import org.iplantc.de.apps.widgets.client.events.ArgumentAddedEvent.HasArgumentAddedEventHandlers;
import org.iplantc.de.apps.widgets.client.events.ArgumentGroupAddedEvent.ArgumentGroupAddedEventHandler;
import org.iplantc.de.apps.widgets.client.events.ArgumentGroupAddedEvent.HasArgumentGroupAddedEventHandlers;
import org.iplantc.de.apps.widgets.client.events.ArgumentGroupSelectedEvent.ArgumentGroupSelectedEventHandler;
import org.iplantc.de.apps.widgets.client.events.ArgumentGroupSelectedEvent.HasArgumentGroupSelectedHandlers;
import org.iplantc.de.apps.widgets.client.events.ArgumentSelectedEvent.ArgumentSelectedEventHandler;
import org.iplantc.de.apps.widgets.client.events.ArgumentSelectedEvent.HasArgumentSelectedEventHandlers;

import com.google.common.collect.Lists;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorContext;
import com.google.gwt.editor.client.EditorVisitor;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import java.util.List;
import java.util.Map;

public class RegisterEventHandlers extends EditorVisitor {

    /**
     * Used to coordinate changes from different leaf editors with the same path.
     * 
     * @author jstroot
     * 
     * @param <T>
     */
    private class LeafEditorChangeHandler<T> implements ValueChangeHandler<T> {

        private final LeafValueEditor<T> leafEditor;

        LeafEditorChangeHandler(LeafValueEditor<T> leafEditor) {
            this.leafEditor = leafEditor;
        }

        @Override
        public void onValueChange(ValueChangeEvent<T> event) {
            // We are assuming that the leaf editor will not fire change events on this call.
            leafEditor.setValue(event.getValue());
        }

    }

    private final List<HasAppTemplateSelectedEventHandlers> hasAppTemplateSelectedHandlers;
    private final List<HasArgumentAddedEventHandlers> hasArgAddedHandlers;
    private final List<HasArgumentGroupAddedEventHandlers> hasArgGrpAddedHandlers;
    private final List<HasArgumentGroupSelectedHandlers> hasArgGrpSelectedHandlers;
    private final List<HasArgumentSelectedEventHandlers> hasArgSelectedHandlers;
    private final List<HasDeleteArgumentEventHandlers> hasDeleteArgHandlers;
    private final Map<String, List<HasValueChangeHandlers<?>>> hasValChangeHandlerMap;

    public RegisterEventHandlers(DeleteArgumentEventHandler deleteArgumentEventHandler,
            ArgumentGroupAddedEventHandler argumentGroupAddedEventHandler, ArgumentAddedEventHandler argumentAddedEventHandler, GatherAllEventProviders eventProviders) {

        this.hasAppTemplateSelectedHandlers = eventProviders.getHasAppTemplateSelectedHandlers();

        this.hasArgGrpAddedHandlers = eventProviders.getHasArgGrpAddedHandlers();
        this.hasArgGrpSelectedHandlers = eventProviders.getHasArgGrpSelectedHandlers();

        this.hasArgAddedHandlers = eventProviders.getHasArgumentAddedHandlers();
        this.hasArgSelectedHandlers = eventProviders.getHasArgSelectedHandlers();
        this.hasDeleteArgHandlers = eventProviders.getHasDeleteArgumentHandlers();
        this.hasValChangeHandlerMap = eventProviders.getHasValChangeHandlerMap();

        for (HasArgumentGroupAddedEventHandlers hasHandlers : hasArgGrpAddedHandlers) {
            hasHandlers.addArgumentGroupAddedEventHandler(argumentGroupAddedEventHandler);
        }
        for (HasArgumentAddedEventHandlers hasHandlers : hasArgAddedHandlers) {
            hasHandlers.addArgumentAddedEventHandler(argumentAddedEventHandler);
        }
        for (HasDeleteArgumentEventHandlers hasHandlers : hasDeleteArgHandlers) {
            hasHandlers.addDeleteArgumentEventHandler(deleteArgumentEventHandler);
        }
    }

    @Override
    public <T> boolean visit(EditorContext<T> ctx) {

        T fromModel = ctx.getFromModel();
        AutoBean<T> autoBean = AutoBeanUtils.getAutoBean(fromModel);

        // Find out which events the current editor handles, and register them
        /*
         * If the current editor handles any events we have gathered, add the current editor to the
         * Has*Handlers, and save the registration on the current autobean.
         */

        Editor<T> editorAsHandler = ctx.getEditor();
        // Register handler with AppTemplate-related event providers
        if (editorAsHandler instanceof AppTemplateSelectedEventHandler) {
            for (HasAppTemplateSelectedEventHandlers hasHandlers : hasAppTemplateSelectedHandlers) {
                HandlerRegistration hr = hasHandlers.addAppTemplateSelectedEventHandler((AppTemplateSelectedEventHandler)editorAsHandler);
                getAutobeanHandlers(autoBean).add(hr);
            }
        }
        
        // Register handler with ArgumentGroup-related event providers
        if (editorAsHandler instanceof ArgumentGroupAddedEventHandler) {
            for(HasArgumentGroupAddedEventHandlers hasHandlers : hasArgGrpAddedHandlers){
                HandlerRegistration hr = hasHandlers.addArgumentGroupAddedEventHandler((ArgumentGroupAddedEventHandler)editorAsHandler);
                getAutobeanHandlers(autoBean).add(hr);
            }
        }
        if (editorAsHandler instanceof ArgumentGroupSelectedEventHandler) {
            for (HasArgumentGroupSelectedHandlers hasArgGrpSelHndlr : hasArgGrpSelectedHandlers) {
                HandlerRegistration argumentGroupSelectedReg = hasArgGrpSelHndlr.addArgumentGroupSelectedHandler((ArgumentGroupSelectedEventHandler)editorAsHandler);
                getAutobeanHandlers(autoBean).add(argumentGroupSelectedReg);
            }
        }

        // Register handler with Argument-related event providers
        if (editorAsHandler instanceof ArgumentAddedEventHandler) {
            for (HasArgumentAddedEventHandlers hasHandlers : hasArgAddedHandlers) {
                HandlerRegistration hr = hasHandlers.addArgumentAddedEventHandler((ArgumentAddedEventHandler)editorAsHandler);
                getAutobeanHandlers(autoBean).add(hr);
            }
        }
        if (editorAsHandler instanceof ArgumentSelectedEventHandler) {
            for (HasArgumentSelectedEventHandlers hasArgSelHndlr : hasArgSelectedHandlers) {
                HandlerRegistration argumentSelectedReg = hasArgSelHndlr.addArgumentSelectedEventHandler((ArgumentSelectedEventHandler)editorAsHandler);
                getAutobeanHandlers(autoBean).add(argumentSelectedReg);
            }
        } 
        if (editorAsHandler instanceof DeleteArgumentEventHandler) {
            for (HasDeleteArgumentEventHandlers hasHandlers : hasDeleteArgHandlers) {
                HandlerRegistration hr = hasHandlers.addDeleteArgumentEventHandler((DeleteArgumentEventHandler)editorAsHandler);
                getAutobeanHandlers(autoBean).add(hr);
            }
        }

        if (editorAsHandler instanceof ValueChangeHandler<?>) {
            List<HasValueChangeHandlers<?>> list = hasValChangeHandlerMap.get(ctx.getAbsolutePath());
            for (@SuppressWarnings("rawtypes")
            HasValueChangeHandlers hvch : list) {
                if (editorAsHandler != hvch) {
                    @SuppressWarnings("unchecked")
                    HandlerRegistration valueChangeReg = hvch.addValueChangeHandler(new LeafEditorChangeHandler<T>(ctx.asLeafValueEditor()));
                    getAutobeanHandlers(autoBean).add(valueChangeReg);
                }
            }
        }
        return ctx.asLeafValueEditor() == null;
    }

    private List<HandlerRegistration> getAutobeanHandlers(AutoBean<?> autobean) {
        if (autobean.getTag(AppsEditorView.Presenter.HANDLERS) == null) {
            autobean.setTag(AppsEditorView.Presenter.HANDLERS, Lists.<HandlerRegistration> newArrayList());
        }
        return autobean.getTag(AppsEditorView.Presenter.HANDLERS);
    }

}
