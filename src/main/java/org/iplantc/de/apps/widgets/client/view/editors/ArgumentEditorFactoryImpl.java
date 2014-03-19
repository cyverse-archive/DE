package org.iplantc.de.apps.widgets.client.view.editors;

import org.iplantc.de.apps.widgets.client.view.AppTemplateForm;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm.ArgumentEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.DoubleInputEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.DoubleSelectionEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.EnvironmentVariableEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.FileInputEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.FileOutputEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.FlagEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.FolderInputEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.FolderOutputEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.InfoEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.IntegerInputEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.IntegerSelectionEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.MultiFileInputEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.MultiFileOutputEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.MultiLineTextEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.ReferenceAnnotationEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.ReferenceGenomeEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.ReferenceSequenceEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.SampleArgumentEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.TextInputEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.TextSelectionEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.tree.TreeSelectionEditor;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.models.apps.integration.ReferenceGenome;
import org.iplantc.de.client.services.AppMetadataServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsPropertyPanelLabels;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.widget.core.client.container.ResizeContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

import java.util.List;

/**
 */
public class ArgumentEditorFactoryImpl implements AppTemplateForm.ArgumentEditorFactory {
    protected AppTemplateForm.ArgumentEditor subEditor;

    @Inject
    private AppTemplateWizardAppearance appearance;
    @Inject
    private AppMetadataServiceFacade appMetadataService;

    @Inject
    private AppsWidgetsPropertyPanelLabels appsWidgetsLabels;
    private EditorChain<Argument, AppTemplateForm.ArgumentEditor> chain;
    private final SimpleContainer con;
    private ListStore<ReferenceGenome> refGenomeListStore;
    @Inject
    private ReferenceGenomeProperties refGenomeProps;

    @Inject
    private SelectionItemProperties selectionItemProps;

    private final ReferenceGenomeProperties referenceGenomeProperties;

    @Inject
    public ArgumentEditorFactoryImpl() {
    	con = new SimpleContainer();
        referenceGenomeProperties = GWT.create(ReferenceGenomeProperties.class);
    }

    @Override
    public Widget asWidget() {
        return con;
    }

    @Override
    public AppTemplateForm.ArgumentEditor createEditorForTraversal() {
        // JDS This should let the editor hierarchy know what the bound paths should be.
        return new SampleArgumentEditor();
    }


    @Override
    public void flush() {

    }

    @Override
    public String getPathElement(AppTemplateForm.ArgumentEditor subEditor) {
        return "";
    }

    @Override
    public ArgumentEditor getSubEditor() {
        return subEditor;
    }

    @Override
    public void onPropertyChange(String... paths) {/* Do Nothing */}

    @Override
    public void setDelegate(EditorDelegate<Argument> delegate) {
    }

    @Override
    public void setEditorChain(EditorChain<Argument, AppTemplateForm.ArgumentEditor> chain) {
        this.chain = chain;
    }

    @Override
    public void setValue(Argument value) {

        if (subEditor != null) {
            return;
        }
        chain.detach(subEditor);

        switch (value.getType()) {
            case FileInput:
                subEditor = new FileInputEditor(appearance);
                break;
            case FolderInput:
                subEditor = new FolderInputEditor(appearance);
                break;
            case MultiFileSelector:
                subEditor = new MultiFileInputEditor(appearance);
                break;
            case EnvironmentVariable:
                subEditor = new EnvironmentVariableEditor(appearance, appsWidgetsLabels);
                break;
            case Flag:
                subEditor = new FlagEditor(appearance);
                break;
            case Info:
                subEditor = new InfoEditor(appearance);
                break;
            case MultiLineText:
                subEditor = new MultiLineTextEditor(appearance, appsWidgetsLabels);
                break;
            case Integer:
                subEditor = new IntegerInputEditor(appearance, appsWidgetsLabels);
                break;
            case Double:
                subEditor = new DoubleInputEditor(appearance, appsWidgetsLabels);
                break;
            case Text:
                subEditor = new TextInputEditor(appearance, appsWidgetsLabels);
                break;
            case TextSelection:
                subEditor = new TextSelectionEditor(appearance);
                break;
            case IntegerSelection:
                subEditor = new IntegerSelectionEditor(appearance);
                break;
            case DoubleSelection:
                subEditor = new DoubleSelectionEditor(appearance);
                break;
            case TreeSelection:
                subEditor = new TreeSelectionEditor(appearance, selectionItemProps);
                break;
            case FileOutput:
                subEditor = new FileOutputEditor(appearance);
                break;
            case FolderOutput:
                subEditor = new FolderOutputEditor(appearance);
                break;
            case MultiFileOutput:
                subEditor = new MultiFileOutputEditor(appearance);
                break;
            case ReferenceGenome:
                subEditor = new ReferenceGenomeEditor(appearance, getReferenceGenomeStore(), refGenomeProps);
                break;
            case ReferenceSequence:
                subEditor = new ReferenceSequenceEditor(appearance, getReferenceGenomeStore(), refGenomeProps);
                break;
            case ReferenceAnnotation:
                subEditor = new ReferenceAnnotationEditor(appearance, getReferenceGenomeStore(), refGenomeProps);
                break;
            default:
                throw new IllegalStateException("Argument type must be known");
        }
        con.add(subEditor);
        Widget parent = con.getParent();
        if ((parent != null) && (parent instanceof ResizeContainer)) {
            ((ResizeContainer)parent).forceLayout();
        }
        chain.attach(value, subEditor);
    }

    private ListStore<ReferenceGenome> getReferenceGenomeStore() {
        if (refGenomeListStore == null) {
            refGenomeListStore = new ListStore<ReferenceGenome>(referenceGenomeProperties.id());

            appMetadataService.getReferenceGenomes(new AsyncCallback<List<ReferenceGenome>>() {

                @Override
                public void onFailure(Throwable caught) {
                    ErrorHandler.post(caught);
                }

                @Override
                public void onSuccess(List<ReferenceGenome> result) {
                    if (refGenomeListStore.getAll().isEmpty()) {
                        refGenomeListStore.addAll(result);
                        refGenomeListStore.addSortInfo(new Store.StoreSortInfo<ReferenceGenome>(referenceGenomeProperties.nameValue(), SortDir.ASC));
                    }
                }
            });
        }
        return refGenomeListStore;
    }
}
