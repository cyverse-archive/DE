package org.iplantc.de.apps.widgets.client.view;

import org.iplantc.de.apps.widgets.client.events.AppTemplateSelectedEvent.AppTemplateSelectedEventHandler;
import org.iplantc.de.apps.widgets.client.events.AppTemplateSelectedEvent.HasAppTemplateSelectedEventHandlers;
import org.iplantc.de.apps.widgets.client.events.ArgumentAddedEvent.HasArgumentAddedEventHandlers;
import org.iplantc.de.apps.widgets.client.events.ArgumentGroupAddedEvent.HasArgumentGroupAddedEventHandlers;
import org.iplantc.de.apps.widgets.client.events.ArgumentGroupSelectedEvent.ArgumentGroupSelectedEventHandler;
import org.iplantc.de.apps.widgets.client.events.ArgumentGroupSelectedEvent.HasArgumentGroupSelectedHandlers;
import org.iplantc.de.apps.widgets.client.events.ArgumentRequiredChangedEvent.ArgumentRequiredChangedEventHandler;
import org.iplantc.de.apps.widgets.client.events.ArgumentRequiredChangedEvent.HasArgumentRequiredChangedHandlers;
import org.iplantc.de.apps.widgets.client.events.ArgumentSelectedEvent.ArgumentSelectedEventHandler;
import org.iplantc.de.apps.widgets.client.events.ArgumentSelectedEvent.HasArgumentSelectedEventHandlers;
import org.iplantc.de.client.models.apps.integration.AppTemplate;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.models.apps.integration.ArgumentGroup;
import org.iplantc.de.client.models.apps.integration.ArgumentType;
import org.iplantc.de.client.models.apps.integration.ArgumentValidator;
import org.iplantc.de.client.models.apps.integration.SelectionItem;

import com.google.gwt.editor.client.CompositeEditor;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.HasEditorErrors;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.editor.client.adapters.ListEditor;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.widget.core.client.Header;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.HasCollapseHandlers;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.HasExpandHandlers;
import com.sencha.gxt.widget.core.client.form.IsField;

import java.util.List;

/**
 */
public interface AppTemplateForm extends IsWidget, Editor<AppTemplate>, ArgumentGroupSelectedEventHandler, ArgumentSelectedEventHandler, AppTemplateSelectedEventHandler,
        HasAppTemplateSelectedEventHandlers, HasArgumentGroupSelectedHandlers, HasArgumentGroupAddedEventHandlers {

    public interface ArgumentEditor extends ValueAwareEditor<Argument>, IsWidget, HasLabelOnlyEditMode, HasDisabledOnNotVisible, HasDisableValidations, HasArgumentSelectedEventHandlers, HasEnabled,
            HasVisibility, HasArgumentRequiredChangedHandlers {

        LeafValueEditor<String> descriptionEditor();

        /**
         * Exposes the {@code EditorDelegate} which is used to bridge separate {@code EditorDriver}s
         * 
         * @return
         */
        EditorDelegate<Argument> getEditorDelegate();

        LeafValueEditor<String> idEditor();

        LeafValueEditor<String> labelEditor();

        LeafValueEditor<Boolean> requiredEditor();

        ValueAwareEditor<List<SelectionItem>> selectionItemsEditor();

        LeafValueEditor<ArgumentType> typeEditor();

        LeafValueEditor<List<ArgumentValidator>> validatorsEditor();

        IArgumentEditorConverter valueEditor();

        LeafValueEditor<Boolean> visibleEditor();

    }

    public interface ArgumentEditorFactory extends CompositeEditor<Argument, Argument, ArgumentEditor>, IsWidget {

        @Ignore
        AppTemplateForm.ArgumentEditor getSubEditor();
    }

    /**
     * By default, this class will not be shown if it does not contain any {@code Argument}s.
     * 
     * @author jstroot
     * 
     */
    public interface ArgumentGroupEditor extends ValueAwareEditor<ArgumentGroup>, IsWidget, HasDisabledOnNotVisible, HasEditorErrors<ArgumentGroup>, HasCollapseHandlers, HasExpandHandlers,
            ArgumentGroupSelectedEventHandler, ArgumentSelectedEventHandler, AppTemplateSelectedEventHandler, HasArgumentGroupSelectedHandlers, HasArgumentAddedEventHandlers,
            ArgumentRequiredChangedEventHandler, HasLabelOnlyEditMode {

        ListEditor<Argument, ArgumentEditorFactory> argumentsEditor();

        VerticalLayoutContainer getDndContainer();

        Header getHeader();

        LeafValueEditor<String> labelEditor();

        void setCollapsible(boolean b);

        void showWhenEmptyOrAllInvisible();

    }

    public interface HasDisabledOnNotVisible {
        void disableOnNotVisible();

        boolean isDisabledOnNotVisible();
    }

    public interface HasDisableValidations {
        void disableValidations();

        boolean isValidationDisabled();

    }

    public interface IArgumentEditorConverter extends IsField<Splittable>, ValueAwareEditor<Splittable>, HasEnabled, HasValueChangeHandlers<Splittable> {

        void applyValidators(List<ArgumentValidator> validators, boolean validationDisabled);

        void setRequired(boolean booleanValue, boolean validationDisabled);

    }

    ListEditor<ArgumentGroup, AppTemplateForm.ArgumentGroupEditor> argumentGroups();

    void collapseAllArgumentGroups();

    AccordionLayoutContainer getDndContainer();

    void insertFirstInAccordion(LaunchAnalysisView law);

    void setAdjustForScroll(boolean adjustForScroll);

}
