package org.iplantc.de.apps.widgets.client.view.editors;

import org.iplantc.de.apps.widgets.client.events.AppTemplateSelectedEvent;
import org.iplantc.de.apps.widgets.client.events.ArgumentAddedEvent;
import org.iplantc.de.apps.widgets.client.events.ArgumentAddedEvent.ArgumentAddedEventHandler;
import org.iplantc.de.apps.widgets.client.events.ArgumentGroupSelectedEvent;
import org.iplantc.de.apps.widgets.client.events.ArgumentRequiredChangedEvent;
import org.iplantc.de.apps.widgets.client.events.ArgumentSelectedEvent;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm.ArgumentEditor;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm.HasDisabledOnNotVisible;
import org.iplantc.de.apps.widgets.client.view.HasLabelOnlyEditMode;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppWizardQuickTip;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.models.apps.integration.ArgumentGroup;
import org.iplantc.de.client.util.AppTemplateUtils;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.editor.client.adapters.EditorSource;
import com.google.gwt.editor.client.adapters.ListEditor;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Event;
import com.google.inject.Inject;
import com.google.inject.Provider;

import static com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode.AUTOY;

import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;

import java.util.List;

/**
 * @author jstroot
 */
public class ArgumentGroupEditorImpl extends ContentPanel implements AppTemplateForm.ArgumentGroupEditor {

    public class ArgGrpLabelLeafEditor implements LeafValueEditor<String> {
        protected final ArgumentGroupEditorImpl argGrpEditor;

        private final HasSafeHtml hasHtml;
        private String lleModel;

        public ArgGrpLabelLeafEditor(HasSafeHtml hasHtml, ArgumentGroupEditorImpl argEditor) {
            this.hasHtml = hasHtml;
            this.argGrpEditor = argEditor;
        }

        @Override
        public String getValue() {
            return lleModel;
        }

        public void refresh() {
            setValue(lleModel);
        }

        @Override
        public void setValue(String value) {
            this.lleModel = value;
            SafeHtml createArgumentLabel = createArgumentLabel(argGrpEditor, value);
            hasHtml.setHTML(createArgumentLabel);
        }

        private SafeHtml createArgumentLabel(ArgumentGroupEditorImpl argGrpEditor, String value) {
            SafeHtmlBuilder labelText = new SafeHtmlBuilder();
            labelText.append(appAppearance.createContentPanelHeaderLabel(SafeHtmlUtils.fromString(value), argGrpEditor.containsRequiredArguments()));
            return labelText.toSafeHtml();
        }

    }

    private class PropertyListEditorSource extends EditorSource<AppTemplateForm.ArgumentEditorFactory> {
        private static final int DEF_ARGUMENT_MARGIN = 10;
        private final Provider<AppTemplateForm.ArgumentEditorFactory> argumentEditorProvider;
        private final VerticalLayoutContainer con;
        private final HasDisabledOnNotVisible hasDisOnNotVis;
        private final HasLabelOnlyEditMode hasLabelOnlyEditMode;
    
        public PropertyListEditorSource(final VerticalLayoutContainer con, Provider<AppTemplateForm.ArgumentEditorFactory> argumentEditorProvider, HasDisabledOnNotVisible hasDisOnNotVis,
                HasLabelOnlyEditMode hasLabelOnlyEditMode) {
            this.con = con;
            this.argumentEditorProvider = argumentEditorProvider;
            this.hasDisOnNotVis = hasDisOnNotVis;
            this.hasLabelOnlyEditMode = hasLabelOnlyEditMode;
        }
    
        @Override
        public AppTemplateForm.ArgumentEditorFactory create(int index) {
            final AppTemplateForm.ArgumentEditorFactory subEditor = argumentEditorProvider.get();

    
            con.insert(subEditor, index, new VerticalLayoutData(1, -1, new Margins(DEF_ARGUMENT_MARGIN)));
            con.forceLayout();
            subEditor.asWidget().fireEvent(new ArgumentSelectedEvent(null));
            Scheduler.get().scheduleFinally(new ScheduledCommand() {


                @Override
                public void execute() {
                    AppTemplateForm.ArgumentEditorFactory subEditorCopy = subEditor;
                    ArgumentEditor subEditor2 = subEditorCopy.getSubEditor();
                    ArgumentGroupEditorImpl.this.fireEvent(new ArgumentAddedEvent(subEditor2));
                    subEditor2.addArgumentRequiredChangedEventHandler(ArgumentGroupEditorImpl.this);
                    subEditor2.setLabelOnlyEditMode(hasLabelOnlyEditMode.isLabelOnlyEditMode());
                    if (hasDisOnNotVis.isDisabledOnNotVisible()) {
                        subEditor2.disableOnNotVisible();
                    }
                }
            });
    
            return subEditor;
        }
    
        @Override
        public void dispose(AppTemplateForm.ArgumentEditorFactory subEditor) {
            subEditor.asWidget().removeFromParent();
        }
    
        @Override
        public void setIndex(AppTemplateForm.ArgumentEditorFactory editor, int index) {
            con.insert(editor, index, new VerticalLayoutData(1, -1, new Margins(DEF_ARGUMENT_MARGIN)));
        }
    }

    private final AppTemplateWizardAppearance appAppearance;

    private final VerticalLayoutContainer argumentsContainer;

    private EditorDelegate<ArgumentGroup> delegate;

    private boolean disableOnNotVisible = false;

    private final ListEditor<Argument, AppTemplateForm.ArgumentEditorFactory> editor;

    private final ArgGrpLabelLeafEditor labelLeafEditor;

    private boolean labelOnlyEditMode;
    
    private ArgumentGroup model;

    private boolean visibleWhenEmptyOrNoChildVisible = false;

    @Inject
    public ArgumentGroupEditorImpl(ContentPanelAppearance cpAppearance, AppTemplateWizardAppearance appearance, Provider<AppTemplateForm.ArgumentEditorFactory> argumentEditorProvider) {
        super(cpAppearance);
        this.appAppearance = appearance;
        argumentsContainer = new VerticalLayoutContainer();
        argumentsContainer.setAdjustForScroll(true);
        argumentsContainer.setScrollMode(AUTOY);
        labelLeafEditor = new ArgGrpLabelLeafEditor(getHeader(), this);
        editor = ListEditor.of(new PropertyListEditorSource(argumentsContainer, argumentEditorProvider, this, this));

        add(argumentsContainer);
        new AppWizardQuickTip(header);
    }

    @Override
    public HandlerRegistration addArgumentAddedEventHandler(ArgumentAddedEventHandler handler) {
        return addHandler(handler, ArgumentAddedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addArgumentGroupSelectedHandler(ArgumentGroupSelectedEvent.ArgumentGroupSelectedEventHandler handler) {
        return addHandler(handler, ArgumentGroupSelectedEvent.TYPE);
    }

    @Override
    public ListEditor<Argument, AppTemplateForm.ArgumentEditorFactory> argumentsEditor() {
        return editor;
    }

    @Override
    public void disableOnNotVisible() {
        this.disableOnNotVisible = true;
    }

    @Override
    public void flush() {/* Do Nothing */}

    @Override
    public VerticalLayoutContainer getDndContainer() {
        return argumentsContainer;
    }

    @Override
    public boolean isDisabledOnNotVisible() {
        return disableOnNotVisible;
    }

    @Override
    public boolean isLabelOnlyEditMode() {
        return labelOnlyEditMode;
    }

    @Override
    public LeafValueEditor<String> labelEditor() {
        return labelLeafEditor;
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
    public void onArgumentRequiredChanged(ArgumentRequiredChangedEvent event) {
        // Update labelEditor
        labelLeafEditor.refresh();
    }

    @Override
    public void onArgumentSelected(ArgumentSelectedEvent event) {
        clearSelectionStyles(event.getSource());
    }

    @Override
    public void onPropertyChange(String... paths) {/* Do Nothing */}

    @Override
    public void setDelegate(EditorDelegate<ArgumentGroup> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setLabelOnlyEditMode(boolean labelOnlyEditMode) {
        this.labelOnlyEditMode = labelOnlyEditMode;
    }

    @Override
    public void setValue(ArgumentGroup value) {
        this.model = value;
        SafeHtmlBuilder labelText = new SafeHtmlBuilder();
        if (value.getArguments().isEmpty()) {
            /* JDS If the argument group has no arguments, add special Empty group argument.
             * This argument should be removed if it still exists when this app gets saved.
             */
            if (visibleWhenEmptyOrNoChildVisible) {
                value.getArguments().add(AppTemplateUtils.getEmptyGroupArgument());
            } else {
                setVisible(false);
            }
        } else if (!visibleWhenEmptyOrNoChildVisible && allChildrenInvisible(value.getArguments())) {
            setVisible(false);
        }

        boolean isRequired = containsRequiredArguments(value);
        // When the value is set, update the FieldSet header text
        labelText.append(appAppearance.createContentPanelHeaderLabel(SafeHtmlUtils.fromString(value.getLabel()), isRequired));
        setHeadingHtml(labelText.toSafeHtml());
    }

    @Override
    public void showErrors(List<EditorError> errors) {
        SafeHtmlBuilder labelText = new SafeHtmlBuilder();
        if (!errors.isEmpty()) {
            ImageElement errImg = appAppearance.getErrorIconImgWithErrQTip(errors);
            labelText.appendHtmlConstant(errImg.getString());
        }
        labelText.append(appAppearance.createContentPanelHeaderLabel(SafeHtmlUtils.fromString(model.getLabel()), containsRequiredArguments(model)));
        setHeadingHtml(labelText.toSafeHtml());
    }

    @Override
    public void showWhenEmptyOrAllInvisible() {
        visibleWhenEmptyOrNoChildVisible = true;
        setVisible(true);

        if ((model != null) && model.getArguments().isEmpty()) {
            editor.getList().add(AppTemplateUtils.getEmptyGroupArgument());
        }
    }

    @Override
    protected void assertPreRender() {
        // KLUDGE JDS Do nothing. This is a workaround for the following bug (which was submitted to the
        // GXT forums);
        // http://www.sencha.com/forum/showthread.php?261470-Adding-new-ContentPanel-to-AccordionLayoutContainer-at-runtime-issue
    }

    @Override
    protected void onClick(Event ce) {
        super.onClick(ce);
        if (header.getElement().isOrHasChild(ce.getEventTarget().<Element> cast())) {
            fireEvent(new ArgumentGroupSelectedEvent(model, delegate.getPath()));
        }
    }

    void clearSelectionStyles(Object object) {
        if (editor == null) {
            return;
        }

        for (AppTemplateForm.ArgumentEditorFactory ae : editor.getEditors()) {
            if (ae.getSubEditor() == object) {
                ae.asWidget().addStyleName(appAppearance.getStyle().argumentSelect());
            } else {
                ae.asWidget().removeStyleName(appAppearance.getStyle().argumentSelect());
            }
        }
    }

    boolean containsRequiredArguments() {
        if (model == null) {
            return false;
        }

        return containsRequiredArguments(model);
    }

    private boolean allChildrenInvisible(List<Argument> list) {
        boolean allChildrenInvisible = true;
        for (Argument arg : list) {
            if (arg.isVisible()) {
                allChildrenInvisible = false;
                break;
            }
        }
        return allChildrenInvisible;
    }

    private boolean containsRequiredArguments(ArgumentGroup ag) {
        boolean isRequired = false;
        List<Argument> arguments = (editor.getList() == null) ? ag.getArguments() : editor.getList();
        for (Argument property : arguments) {
            if (property.getRequired()) {
                // If any field is required, mark header as required.
                isRequired = true;
                break;
            }
        }
        return isRequired;
    }

}
