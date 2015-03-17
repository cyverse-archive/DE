package org.iplantc.de.pipelines.client.views;

import org.iplantc.de.client.models.pipelines.PipelineTask;
import org.iplantc.de.client.models.pipelines.PipelineAppData;
import org.iplantc.de.client.models.pipelines.PipelineAppMapping;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.ListField;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.form.error.SideErrorHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A PipelineAppMappingView that displays input mappings as FieldLabels with a ComboBox for mapping an
 * output to an input.
 *
 * @author psarando
 *
 */
public class PipelineAppMappingForm implements PipelineAppMappingView {
    List<PipelineTask> apps;

    private Presenter presenter;
    private EditorDelegate<List<PipelineTask>> delegate;

    private final List<MappingFieldSet> mappingFields;
    private final VerticalLayoutContainer container;
    private final LabelProvider<PipelineMappingOutputWrapper> labelProvider;
    private final ModelKeyProvider<PipelineMappingOutputWrapper> outputsKeyProvider;
    private final ValueProvider<PipelineMappingOutputWrapper, String> outputsValueProvider;

    public PipelineAppMappingForm() {
        container = new VerticalLayoutContainer();
        container.setScrollMode(ScrollMode.AUTO);

        labelProvider = new OutputComboLabelProvider();
        outputsKeyProvider = new OutputWrapperKeyProvider();
        outputsValueProvider = new OutputWrapperValueProvider();

        mappingFields = new ArrayList<MappingFieldSet>();
    }

    @Override
    public Widget asWidget() {
        return container;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void clearInvalid() {
        for (MappingFieldSet step : mappingFields) {
            step.clearInvalid();
        }
    }

    @Override
    public void setDelegate(EditorDelegate<List<PipelineTask>> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void flush() {
        if (delegate != null) {
            // A pipline needs at least 2 apps and each app after the first one should have at least one
            // output-to-input mapping
            if (mappingFields.size() > 1) {
                for (MappingFieldSet mappingStep : mappingFields) {
                    PipelineTask app = mappingStep.getApp();
                    mappingStep.clearInvalid();
                    if (!mappingStep.isValid()) {
                        String err = I18N.ERROR.mappingStepError();
                        delegate.recordError(err, app, this);

                        DefaultEditorError editorError = new DefaultEditorError(this, err, app);
                        mappingStep.markInvalid(Collections.<EditorError> singletonList(editorError));
                    }
                }
            } else {
                delegate.recordError(I18N.ERROR.mappingStepError(), apps, this);
            }
        }
    }

    @Override
    public void onPropertyChange(String... paths) {
        // no-op
    }

    @Override
    public void setValue(List<PipelineTask> value) {
        apps = value;
        mappingFields.clear();
        container.clear();

        if (apps == null) {
            return;
        }

        // Keep a list of all previous steps' outputs.
        List<PipelineMappingOutputWrapper> outputs = new ArrayList<PipelineMappingOutputWrapper>();

        // Build a FieldSet for each step.
        for (PipelineTask app : apps) {
            FieldSet step = buildStepFieldSet(app);
            container.add(step);

            VerticalLayoutContainer panel = new VerticalLayoutContainer();
            step.add(panel);

            // Add this step's available inputs each with a ComboBox of all previous steps' outputs.
            List<PipelineAppData> appInputs = app.getInputs();
            if (appInputs != null) {
                for (PipelineAppData input : appInputs) {
                    ComboBox<PipelineMappingOutputWrapper> combo = buildOutputCombo(app, input, outputs);

                    FieldLabel inputField = new FieldLabel(combo, input.getLabel());
                    panel.add(inputField);
                }
            }

            // Add a list of this step's available outputs.
            List<PipelineMappingOutputWrapper> outputWrappers = wrapOutputs(app);
            if (outputWrappers != null) {
                outputs.addAll(outputWrappers);

                FieldLabel outputList = new FieldLabel(buildOutputsListField(outputWrappers),
                        I18N.DISPLAY.outputs());
                panel.add(outputList);
            }
        }
    }

    private List<PipelineMappingOutputWrapper> wrapOutputs(PipelineTask app) {
        List<PipelineMappingOutputWrapper> outputWrappers = null;

        List<PipelineAppData> appOutputs = app.getOutputs();
        if (appOutputs != null) {
            outputWrappers = new ArrayList<PipelineMappingOutputWrapper>();

            for (PipelineAppData output : appOutputs) {
                outputWrappers.add(new PipelineMappingOutputWrapper(app, output));
            }
        }

        return outputWrappers;
    }

    private FieldSet buildStepFieldSet(PipelineTask app) {
        MappingFieldSet step = new MappingFieldSet(app);
        mappingFields.add(step);

        return step;
    }

    private ComboBox<PipelineMappingOutputWrapper> buildOutputCombo(PipelineTask app,
            PipelineAppData input, List<PipelineMappingOutputWrapper> outputs) {
        String targetInputId = input.getId();

        ListStore<PipelineMappingOutputWrapper> store = new ListStore<PipelineMappingOutputWrapper>(
                outputsKeyProvider);

        store.addAll(outputs);

        ComboBox<PipelineMappingOutputWrapper> combo = new ComboBox<PipelineMappingOutputWrapper>(store,
                labelProvider);
        combo.setEmptyText(I18N.DISPLAY.userProvided());
        combo.setAllowBlank(true);
        combo.setForceSelection(true);
        combo.setTriggerAction(TriggerAction.ALL);
        combo.setWidth(200);
        combo.addSelectionHandler(new OutputComboSelectionHandler(presenter, app, targetInputId));

        List<PipelineAppMapping> appMappings = app.getMappings();
        if (appMappings != null) {
            PipelineMappingOutputWrapper outputWrapper = null;
            for (PipelineAppMapping mapping : appMappings) {
                String outputId = mapping.getMap().get(targetInputId);

                if (outputId != null) {
                    String wrapperKey = buildOutputWrapperKey(mapping.getStep(), outputId);
                    outputWrapper = store.findModelWithKey(wrapperKey);
                }
            }

            if (outputWrapper != null) {
                combo.setValue(outputWrapper, false);
            }
        }

        return combo;
    }

    private ListField<PipelineMappingOutputWrapper, String> buildOutputsListField(
            List<PipelineMappingOutputWrapper> outputs) {
        ListStore<PipelineMappingOutputWrapper> outputListStore = new ListStore<PipelineMappingOutputWrapper>(
                outputsKeyProvider);
        outputListStore.addAll(outputs);

        ListView<PipelineMappingOutputWrapper, String> outputsView = new ListView<PipelineMappingOutputWrapper, String>(
                outputListStore, outputsValueProvider);

        ListField<PipelineMappingOutputWrapper, String> outputListField = new ListField<PipelineMappingOutputWrapper, String>(outputsView);
        outputListField.setWidth(200);

        return outputListField;
    }

    private String buildOutputWrapperKey(int step, String outputId) {
        return step + "-" + outputId; //$NON-NLS-1$
    }

    /**
     * A convenience wrapper to hold a reference to a Pipeline App and one of its Outputs.
     *
     * @author psarando
     *
     */
    private class PipelineMappingOutputWrapper {
        private final PipelineTask sourceApp;
        private final PipelineAppData sourceOutput;

        public PipelineMappingOutputWrapper(PipelineTask sourceApp, PipelineAppData sourceOutput) {
            this.sourceApp = sourceApp;
            this.sourceOutput = sourceOutput;
        }

        /**
         * @return the sourceApp
         */
        public PipelineTask getApp() {
            return sourceApp;
        }

        /**
         * @return the sourceOutput
         */
        public PipelineAppData getOutput() {
            return sourceOutput;
        }
    }

    /**
     * A SelectionHandler for the output ComboBoxes that sets the mapping when an output is selected.
     *
     * @author psarando
     *
     */
    private class OutputComboSelectionHandler implements SelectionHandler<PipelineMappingOutputWrapper> {

        private final Presenter presenter;
        private final PipelineTask targetApp;
        private final String targetInputId;

        OutputComboSelectionHandler(Presenter presenter, PipelineTask targetApp, String targetInputId) {
            this.presenter = presenter;
            this.targetApp = targetApp;
            this.targetInputId = targetInputId;
        }

        @Override
        public void onSelection(SelectionEvent<PipelineMappingOutputWrapper> event) {
            PipelineMappingOutputWrapper selectedWrapper = event.getSelectedItem();

            if (selectedWrapper != null) {
                PipelineTask sourceApp = selectedWrapper.getApp();
                String sourceOutputId = selectedWrapper.getOutput().getId();

                presenter.setInputOutputMapping(targetApp, targetInputId, sourceApp, sourceOutputId);
            }
        }
    }

    private class OutputComboLabelProvider implements LabelProvider<PipelineMappingOutputWrapper> {

        @Override
        public String getLabel(PipelineMappingOutputWrapper item) {
            String stepLabel = I18N.DISPLAY.stepWithValue(item.getApp().getStep()+1);
            return Format.substitute("{0}: {1}", stepLabel, item.getOutput().getLabel()); //$NON-NLS-1$
        }
    }

    private class OutputWrapperKeyProvider implements ModelKeyProvider<PipelineMappingOutputWrapper> {

        @Override
        public String getKey(PipelineMappingOutputWrapper item) {
            return buildOutputWrapperKey(item.getApp().getStep(), item.getOutput().getId());
        }
    }

    private class OutputWrapperValueProvider implements ValueProvider<PipelineMappingOutputWrapper, String> {

        @Override
        public String getValue(PipelineMappingOutputWrapper object) {
            return object.getOutput().getLabel();
        }

        @Override
        public void setValue(PipelineMappingOutputWrapper object, String value) {
        }

        @Override
        public String getPath() {
            return null;
        }
    }

    /**
     * A FieldSet wrapper for App mapping that can mark itself as invalid
     *
     * @author psarando
     *
     */
    public class MappingFieldSet extends FieldSet {
        private final SideErrorHandler errorHandler;
        private final PipelineTask app;

        public MappingFieldSet(PipelineTask app) {
            this.app = app;
            errorHandler = new SideErrorHandler(this);

            init();
        }

        private void init() {
            String stepLabel = I18N.DISPLAY.stepWithValue(app.getStep()+1);
            setHeadingText(Format.substitute("{0}: {1}", stepLabel, app.getName())); //$NON-NLS-1$
            setCollapsible(true);
            setWidth(400);
        }

        public PipelineTask getApp() {
            return app;
        }

        public void clearInvalid() {
            errorHandler.clearInvalid();
        }

        public void markInvalid(List<EditorError> errors) {
            errorHandler.markInvalid(errors);
        }

        public boolean isValid() {
            return presenter.isMappingValid(app);
        }
    }
}
