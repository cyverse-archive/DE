package org.iplantc.de.diskResource.client.views.widgets;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.UserSettings;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.models.errorHandling.ServiceErrorCode;
import org.iplantc.de.client.models.errorHandling.SimpleServiceError;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.events.LastSelectedPathChangedEvent;
import org.iplantc.de.commons.client.widgets.IPlantSideErrorHandler;
import org.iplantc.de.diskResource.client.model.DiskResourceModelKeyProvider;
import org.iplantc.de.diskResource.client.model.DiskResourceProperties;
import org.iplantc.de.diskResource.client.views.dialogs.FileFolderSelectDialog;
import org.iplantc.de.diskResource.client.views.dialogs.FileSelectDialog;
import org.iplantc.de.resources.client.constants.IplantValidationConstants;
import org.iplantc.de.shared.AsyncProviderWrapper;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.core.shared.FastMap;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.DndDragEnterEvent;
import com.sencha.gxt.dnd.core.client.DndDragEnterEvent.DndDragEnterHandler;
import com.sencha.gxt.dnd.core.client.DndDragMoveEvent;
import com.sencha.gxt.dnd.core.client.DndDragMoveEvent.DndDragMoveHandler;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
import com.sencha.gxt.dnd.core.client.DropTarget;
import com.sencha.gxt.dnd.core.client.StatusProxy;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.IsField;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.form.error.SideErrorHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author jstroot
 */
public class MultiFileSelectorField extends Composite implements
                                                     IsField<List<HasPath>>,
                                                     ValueAwareEditor<List<HasPath>>,
                                                     HasValueChangeHandlers<List<HasPath>>,
                                                     DndDragEnterHandler,
                                                     DndDragMoveHandler,
                                                     DndDropHandler,
                                                     DiskResourceSelector,
                                                     DiskResourceSelector.HasDisableBrowseButtons,
                                                     SelectionChangedHandler<DiskResource> {

    public interface MultiFileSelectorFieldAppearance {

        String analysisFailureWarning(String s);

        SafeHtml dataDragDropStatusText(int size);

        String diskResourceDoesNotExist(String drErrList);

        String nameColumnLabel();

        String permissionSelectErrorMessage();

        String requiredField();

        String selectMultipleInputs();

        String add();

        ImageResource addIcon();

        String delete();

        ImageResource deleteIcon();
    }

    private final class FileSelectDialogHideHandler implements HideHandler {
        private final FileSelectDialog dlg;
        private final ListStore<DiskResource> store;

        public FileSelectDialogHideHandler(final FileSelectDialog dlg,
                                           final ListStore<DiskResource> store) {
            this.dlg = dlg;
            this.store = store;
        }

        @Override
        public void onHide(HideEvent event) {
            Set<File> files = diskResourceUtil.filterFiles(dlg.getDiskResources());
            if (files.isEmpty()) {
                return;
            }
            store.addAll(files);
            if (userSettings.isRememberLastPath() && store.size() > 0) {
                userSettings.setLastPath(diskResourceUtil.parseParent(store.get(0).getPath()));
                eventBus.fireEvent(new LastSelectedPathChangedEvent(true));
            }
            ValueChangeEvent.fire(MultiFileSelectorField.this,
                                  Lists.<HasPath> newArrayList(store.getAll()));
            setValue(Lists.<HasPath> newArrayList(store.getAll()));
        }
    }

    private final class FileFolderSelectDailogHideHandler implements HideHandler {

        private final FileFolderSelectDialog dlg;
        private final ListStore<DiskResource> store;

        public FileFolderSelectDailogHideHandler(final FileFolderSelectDialog dlg,
                                                 final ListStore<DiskResource> store) {
            this.dlg = dlg;
            this.store = store;
        }

        @Override
        public void onHide(HideEvent event) {
            store.addAll(dlg.getValue());
            if (userSettings.isRememberLastPath() && store.size() > 0) {
                userSettings.setLastPath(diskResourceUtil.parseParent(store.get(0).getPath()));
                eventBus.fireEvent(new LastSelectedPathChangedEvent(true));
            }
            ValueChangeEvent.fire(MultiFileSelectorField.this,
                                  Lists.<HasPath> newArrayList(store.getAll()));
            setValue(Lists.<HasPath> newArrayList(store.getAll()));

        }

    }

    interface MultiFileSelectorFieldUiBinder extends UiBinder<Widget, MultiFileSelectorField> {
    }

    private static final MultiFileSelectorFieldUiBinder BINDER = GWT.create(MultiFileSelectorFieldUiBinder.class);
    protected final List<EditorError> errors = Lists.newArrayList();
    protected final List<EditorError> existsErrors = Lists.newArrayList();
    protected final List<EditorError> permissionErrors = Lists.newArrayList();

    @UiField
    TextButton addButton;
    @UiField
    ColumnModel<DiskResource> cm;
    @UiField
    TextButton deleteButton;
    @UiField
    Grid<DiskResource> grid;
    @UiField
    GridView<DiskResource> gridView;
    @UiField
    ListStore<DiskResource> listStore;
    @UiField
    ToolBar toolbar;
    @UiField
    HTML warnInfo;
    @UiField(provided = true)
    final MultiFileSelectorFieldAppearance appearance;
    private final SideErrorHandler errorSupport;
    // by default do not validate permissions
    private final boolean validatePermissions = false;
    private boolean addDeleteButtonsEnabled = true;
    private boolean required;

    @Inject
    UserSettings userSettings;
    @Inject
    DiskResourceAutoBeanFactory factory;
    @Inject
    DiskResourceServiceFacade drServiceFacade;
    @Inject
    IplantValidationConstants validationConstants;
    @Inject
    EventBus eventBus;
    @Inject AsyncProviderWrapper<FileSelectDialog> fileSelectDialogProvider;
    @Inject
    AsyncProviderWrapper<FileFolderSelectDialog> fileFolderSelectDialogProvider;
    @Inject
    DiskResourceUtil diskResourceUtil;
    private final boolean allowFolderSelect;

    @Inject
    MultiFileSelectorField(final MultiFileSelectorFieldAppearance appearance,
                           @Assisted boolean allowFolderSelect) {
        this.appearance = appearance;
        this.errorSupport = new IPlantSideErrorHandler(this);
        this.allowFolderSelect = allowFolderSelect;
        initWidget(BINDER.createAndBindUi(this));
        grid.getSelectionModel().addSelectionChangedHandler(this);
        grid.setBorders(true);
        gridView.setEmptyText(appearance.selectMultipleInputs());
        initDragAndDrop();
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<HasPath>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public void clear() {
        grid.getStore().clear();
        clearInvalid();
        setInfoErrorText("");
    }

    @Override
    public void clearInvalid() {
        errorSupport.clearInvalid();
    }

    @Override
    public void disableBrowseButtons() {
        addDeleteButtonsEnabled = false;
    }

    @Override
    public void finishEditing() {
    }

    @Override
    public void flush() {
        validate(false);
    }

    @Override
    public List<EditorError> getErrors() {
        return errors;
    }

    @Override
    public List<HasPath> getValue() {
        List<HasPath> hasIdList = Lists.newArrayList();
        for (DiskResource dr : listStore.getAll()) {
            hasIdList.add(dr);
        }
        return hasIdList;
    }

    @Override
    public void setValue(List<HasPath> value) {
        if ((value == null) || value.isEmpty())
            return;

        doGetStat(value);
    }

    @Override
    public boolean isValid(boolean preventMark) {
        return validate(preventMark);
    }

    @Override
    public void onDragEnter(DndDragEnterEvent event) {
        Set<DiskResource> dropData = getDropData(event.getDragSource().getData());

        if (!validateDropStatus(dropData, event.getStatusProxy())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onDragMove(DndDragMoveEvent event) {
        Set<DiskResource> dropData = getDropData(event.getDragSource().getData());

        if (!validateDropStatus(dropData, event.getStatusProxy())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onDrop(DndDropEvent event) {
        Set<DiskResource> dropData = getDropData(event.getData());

        if (validateDropStatus(dropData, event.getStatusProxy())) {
            List<HasPath> validateList = new ArrayList<>();
            for (DiskResource data : dropData) {
                if (listStore.findModel(data) == null) {
                    if (allowFolderSelect) {
                        listStore.add(data);
                        validateList.add(data);
                    } else {
                        if (data instanceof File) {
                            listStore.add(data);
                            validateList.add(data);
                        }
                    }
                }
            }

            if (checkForSplChar(validateList).length() > 0) {
                setInfoErrorText(getSplCharWarning());
            }

            ValueChangeEvent.fire(this, Lists.<HasPath> newArrayList(dropData));
        }
    }

    @Override
    public void onPropertyChange(String... paths) {/* Do Nothing */
    }

    @Override
    public void onSelectionChanged(SelectionChangedEvent<DiskResource> event) {
        List<DiskResource> selection = event.getSelection();
        deleteButton.setEnabled((selection != null) && !selection.isEmpty());
    }

    @Override
    public void reset() {/* Do Nothing */
    }

    @Override
    public void hideResetButton() {
        /* Do Nothing */
    }

    @Override
    public void setDelegate(EditorDelegate<List<HasPath>> delegate) {/* Do Nothing */
    }

    public void setEmptyText(String emptyText) {
        gridView.setEmptyText(emptyText);
    }

    @Override
    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public boolean validate(boolean preventMark) {
        errors.clear();
        if (disabled) {
            clearInvalid();
            return true;
        }

        errors.addAll(permissionErrors);
        errors.addAll(existsErrors);
        if (required && listStore.getAll().isEmpty()) {
            errors.add(new DefaultEditorError(this, appearance.requiredField(), ""));
        }
        errorSupport.markInvalid(errors);
        return !errors.isEmpty();
    }

    @SuppressWarnings("unchecked")
    protected Set<DiskResource> getDropData(Object data) {
        if (!(data instanceof Collection<?>)) {
            return null;
        }
        Collection<?> dataColl = (Collection<?>)data;
        if (dataColl.isEmpty() || !(dataColl.iterator().next() instanceof DiskResource)) {
            return null;
        }

        return Sets.newHashSet((Collection<DiskResource>)dataColl);
    }

    protected void setInfoErrorText(String analysisFailureWarning) {
        warnInfo.setHTML(analysisFailureWarning);
    }

    protected boolean validateDropStatus(Set<DiskResource> dropData, StatusProxy status) {
        if (dropData == null || dropData.isEmpty()) {
            status.setStatus(false);
            return false;
        }

        // Reset status message
        status.setStatus(true);
        status.update(appearance.dataDragDropStatusText(dropData.size()));

        return true;
    }

    @UiFactory
    ColumnModel<DiskResource> createColumnModel() {
        List<ColumnConfig<DiskResource, ?>> list = Lists.newArrayList();
        DiskResourceProperties props = GWT.create(DiskResourceProperties.class);

        ColumnConfig<DiskResource, String> name = new ColumnConfig<>(props.name(),
                                                                     130,
                                                                     appearance.nameColumnLabel());
        list.add(name);
        return new ColumnModel<>(list);
    }

    @UiFactory
    ListStore<DiskResource> createListStore() {
        return new ListStore<>(new DiskResourceModelKeyProvider());
    }

    @UiHandler("addButton")
    void onAddButtonSelected(SelectEvent event) {
        if (!addDeleteButtonsEnabled) {
            return;
        }
        // Open a multi-select file selector
        final String path = userSettings.isRememberLastPath() ? userSettings.getLastPath() : null;
        if (!allowFolderSelect) {
            fileSelectDialogProvider.get(new AsyncCallback<FileSelectDialog>() {
                @Override
                public void onFailure(Throwable caught) {
                    ErrorHandler.post(caught);
                }

                @Override
                public void onSuccess(FileSelectDialog result) {
                    HasPath hasPath = CommonModelUtils.getInstance().createHasPathFromString(path);
                    result.addHideHandler(new FileSelectDialogHideHandler(result, listStore));
                    result.show(false, hasPath, null, Collections.<InfoType> emptyList());
                }
            });
        } else {
            fileFolderSelectDialogProvider.get(new AsyncCallback<FileFolderSelectDialog>() {

                @Override
                public void onFailure(Throwable caught) {
                    ErrorHandler.post(caught);

                }

                @Override
                public void onSuccess(FileFolderSelectDialog result) {
                    HasPath hasPath = CommonModelUtils.getInstance().createHasPathFromString(path);
                    result.addHideHandler(new FileFolderSelectDailogHideHandler(result, listStore));
                    result.show(hasPath, null, null, false);
                }

            });
        }

    }

    @UiHandler("deleteButton")
    void onDeleteButtonSelected(SelectEvent event) {
        if (!addDeleteButtonsEnabled) {
            return;
        }
        for (DiskResource dr : grid.getSelectionModel().getSelectedItems()) {
            listStore.remove(dr);
        }

        if (checkForSplChar(Lists.<HasPath> newArrayList(grid.getStore().getAll())).length() > 0) {
            setInfoErrorText(getSplCharWarning());
        } else {
            setInfoErrorText("");
        }
    }

    private StringBuilder checkForSplChar(List<HasPath> idSet) {
        char[] restrictedChars = (validationConstants.warnedDiskResourceNameChars()).toCharArray(); //$NON-NLS-1$
        StringBuilder restrictedFound = new StringBuilder();

        for (HasPath path : idSet) {
            String diskResourceId = path.getPath();
            for (char restricted : restrictedChars) {
                for (char next : diskResourceId.toCharArray()) {
                    if (next == restricted && next != '/') {
                        restrictedFound.append(restricted);
                    }
                }
            }
            // validate '/' only on label
            for (char next : diskResourceUtil.parseNameFromPath(diskResourceId).toCharArray()) {
                if (next == '/') {
                    restrictedFound.append('/');
                }
            }

        }
        return restrictedFound;
    }

    private void doGetStat(final List<HasPath> value) {
        // JDS Clear permissions and existence errors since we are about to recheck
        permissionErrors.clear();
        existsErrors.clear();
        drServiceFacade.getStat(diskResourceUtil.asStringPathTypeMap(value, TYPE.FILE),
                                new AsyncCallback<FastMap<DiskResource>>() {

                                    @Override
                                    public void onFailure(Throwable caught) {
                                        // Assuming that if there are any non-existent files, that this
                                        // will kick off.
                                        SimpleServiceError serviceError = AutoBeanCodex.decode(factory,
                                                                                               SimpleServiceError.class,
                                                                                               caught.getMessage())
                                                                                       .as();
                                        if (serviceError.getErrorCode()
                                                        .equals(ServiceErrorCode.ERR_DOES_NOT_EXIST.toString())) {
                                            String reason = serviceError.getReason();
                                            GWT.log("The Reason: " + reason);
                                            List<String> errorMessageValues = Lists.newArrayList();
                                            String drErrList = diskResourceUtil.asCommaSeparatedNameList(errorMessageValues);
                                            DefaultEditorError existsErr = new DefaultEditorError(MultiFileSelectorField.this,
                                                                                                  appearance.diskResourceDoesNotExist(drErrList),
                                                                                                  null);
                                            existsErrors.add(existsErr);
                                            errors.add(existsErr);
                                            ValueChangeEvent.fire(MultiFileSelectorField.this, value);
                                        }
                                    }

                                    @Override
                                    public void onSuccess(FastMap<DiskResource> result) {
                                        if (result.isEmpty()) {
                                            return;
                                        }

                                        Set<Entry<String, DiskResource>> entrySet = result.entrySet();
                                        for (Entry<String, DiskResource> entry : entrySet) {
                                            DiskResource entryValue = entry.getValue();
                                            DefaultEditorError permError = new DefaultEditorError(MultiFileSelectorField.this,
                                                                                                  appearance.permissionSelectErrorMessage(),
                                                                                                  entryValue.getId());
                                            if (validatePermissions) {
                                                if (entryValue == null) {
                                                    permissionErrors.add(permError);
                                                    errors.add(permError);
                                                    errorSupport.markInvalid(errors);
                                                } else if (!(diskResourceUtil.isWritable(entryValue) || diskResourceUtil.isOwner(entryValue))) {
                                                    permissionErrors.add(permError);
                                                    errors.add(permError);
                                                    errorSupport.markInvalid(errors);
                                                }
                                            } else if (listStore.findModelWithKey(entryValue.getId()) == null) {
                                                // JDS If item does not exist in store, add it.
                                                listStore.add(entryValue);
                                            }
                                        }
                                        if (checkForSplChar(value).length() > 0) {
                                            setInfoErrorText(getSplCharWarning());
                                        } else {
                                            setInfoErrorText("");
                                        }
                                    }
                                });
    }

    private String getSplCharWarning() {
        return "<span style='color:red;width:65%;font-size:9px;'>"
                + appearance.analysisFailureWarning(validationConstants.warnedDiskResourceNameChars())
                + "</span>";
    }

    private void initDragAndDrop() {
        DropTarget dataDrop = new DropTarget(this);
        dataDrop.setOperation(Operation.COPY);
        dataDrop.addDragEnterHandler(this);
        dataDrop.addDragMoveHandler(this);
        dataDrop.addDropHandler(this);
    }
}
