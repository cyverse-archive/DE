package org.iplantc.de.diskResource.client.views.widgets;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.HasPaths;
import org.iplantc.de.client.models.UserSettings;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceStatMap;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.errorHandling.ServiceErrorCode;
import org.iplantc.de.client.models.errorHandling.SimpleServiceError;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.events.UserSettingsUpdatedEvent;
import org.iplantc.de.commons.client.widgets.IPlantSideErrorHandler;
import org.iplantc.de.diskResource.client.views.DiskResourceModelKeyProvider;
import org.iplantc.de.diskResource.client.views.DiskResourceProperties;
import org.iplantc.de.diskResource.client.views.dialogs.FileSelectDialog;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;

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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;


/**
 *
 * @author jstroot
 *
 */
public class MultiFileSelectorField extends Composite implements IsField<List<HasId>>,
        ValueAwareEditor<List<HasId>>, HasValueChangeHandlers<List<HasId>>, DndDragEnterHandler,
 DndDragMoveHandler,
        DndDropHandler, DiskResourceSelector, DiskResourceSelector.HasDisableBrowseButtons {

    interface MultiFileSelectorFieldUiBinder extends UiBinder<Widget, MultiFileSelectorField> {
    }

    private final class FileSelectDialogHideHandler implements HideHandler {
        private final FileSelectDialog dlg;
        private final ListStore<DiskResource> store;

        public FileSelectDialogHideHandler(final FileSelectDialog dlg, final ListStore<DiskResource> store) {
            this.dlg = dlg;
            this.store = store;
        }

        @Override
        public void onHide(HideEvent event) {
            Set<File> files = DiskResourceUtil.filterFiles(dlg.getDiskResources());
            if (files.isEmpty()) {
                return;
            }
            store.addAll(files);
            if (userSettings.isRememberLastPath() && store.size() > 0) {
                userSettings.setLastPathId(DiskResourceUtil.parseParent(store.get(0).getId()));
                UserSettingsUpdatedEvent usue = new UserSettingsUpdatedEvent();
                EventBus.getInstance().fireEvent(usue);
            }
            ValueChangeEvent.fire(MultiFileSelectorField.this, Lists.<HasId> newArrayList(store.getAll()));
        }
    }

    private static MultiFileSelectorFieldUiBinder BINDER = GWT.create(MultiFileSelectorFieldUiBinder.class);

    protected List<EditorError> errors = Lists.newArrayList();

    protected List<EditorError> existsErrors = Lists.newArrayList();

    protected List<EditorError> permissionErrors = Lists.newArrayList();

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

    UserSettings userSettings = UserSettings.getInstance();
    private boolean addDeleteButtonsEnabled = true;
    private final DiskResourceServiceFacade drServiceFacade;

    private final SideErrorHandler errorSupport;

    private boolean required;

    // by default do not validate permissions
    private final boolean validatePermissions = false;

    public MultiFileSelectorField() {
        initWidget(BINDER.createAndBindUi(this));

        grid.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<DiskResource>() {
            @Override
            public void onSelectionChanged(SelectionChangedEvent<DiskResource> event) {
                List<DiskResource> selection = event.getSelection();
                deleteButton.setEnabled((selection != null) && !selection.isEmpty());
            }
        });

        grid.setBorders(true);

        drServiceFacade = ServicesInjector.INSTANCE.getDiskResourceServiceFacade();
        initDragAndDrop();
        this.errorSupport = new IPlantSideErrorHandler(this);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<HasId>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public void clear() {
        setValue(Collections.<HasId> emptyList());
        clearInvalid();
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
    public void flush() {
        validate(false);
    }

    @Override
    public List<EditorError> getErrors() {
        return errors;
    }

    @Override
    public List<HasId> getValue() {
        List<HasId> hasIdList = Lists.newArrayList();
        for (DiskResource dr : listStore.getAll()) {
            hasIdList.add(dr);
        }
        return hasIdList;
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
            for (DiskResource data : dropData) {
                if ((data instanceof File) && listStore.findModel(data) == null) {
                    listStore.add(data);
                }
            }
            ValueChangeEvent.fire(this, Lists.<HasId> newArrayList(dropData));
        }
    }

    @Override
    public void onPropertyChange(String... paths) {/* Do Nothing */}

    @Override
    public void reset() {
    }

    @Override
    public void setDelegate(EditorDelegate<List<HasId>> delegate) {/* Do Nothing */}

    public void setEmptyText(String emptyText) {
        gridView.setEmptyText(emptyText);
    }

    @Override
    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public void setValue(List<HasId> value) {
        if ((value == null) || value.isEmpty())
            return;

        doGetStat(value);
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
            errors.add(new DefaultEditorError(this, I18N.ERROR.requiredField(), ""));
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

        Set<DiskResource> dropData = null;
        dropData = Sets.newHashSet((Collection<DiskResource>)dataColl);

        return dropData;
    }

    protected boolean validateDropStatus(Set<DiskResource> dropData, StatusProxy status) {
        if (dropData == null || dropData.isEmpty()) {
            status.setStatus(false);
            return false;
        }

        // Reset status message
        status.setStatus(true);
        status.update(I18N.DISPLAY.dataDragDropStatusText(dropData.size()));

        return true;
    }

    @UiFactory
    ColumnModel<DiskResource> createColumnModel() {
        List<ColumnConfig<DiskResource, ?>> list = Lists.newArrayList();
        DiskResourceProperties props = GWT.create(DiskResourceProperties.class);

        ColumnConfig<DiskResource, String> name = new ColumnConfig<DiskResource, String>(props.name(), 130, I18N.DISPLAY.name());
        list.add(name);
        return new ColumnModel<DiskResource>(list);
    }

    @UiFactory
    ListStore<DiskResource> createListStore() {
        return new ListStore<DiskResource>(new DiskResourceModelKeyProvider());
    }

    @UiHandler("addButton")
    void onAddButtonSelected(SelectEvent event) {
        if (!addDeleteButtonsEnabled) {
            return;
        }
        // Open a multiselect file selector
        FileSelectDialog dlg = null;

        if (userSettings.isRememberLastPath()) {
            String id = userSettings.getLastPathId();
            dlg = FileSelectDialog.selectParentFolderById(id,false);
        } else {
            dlg = FileSelectDialog.selectParentFolderById(null,false);
        }
        dlg.addHideHandler(new FileSelectDialogHideHandler(dlg, listStore));
        dlg.show();
    }

    @UiHandler("deleteButton")
    void onDeleteButtonSelected(SelectEvent event) {
        if (!addDeleteButtonsEnabled) {
            return;
        }
        for (DiskResource dr : grid.getSelectionModel().getSelectedItems()) {
            listStore.remove(dr);
        }
    }

    private void doGetStat(final List<HasId> value) {
        // JDS Clear permissions and existence errors since we are about to recheck
        permissionErrors.clear();
        existsErrors.clear();
        HasPaths diskResourcePaths = drServiceFacade.getDiskResourceFactory().pathsList().as();
        diskResourcePaths.setPaths(DiskResourceUtil.asStringIdList(value));
        drServiceFacade.getStat(diskResourcePaths, new AsyncCallback<DiskResourceStatMap>() {

            @Override
            public void onFailure(Throwable caught) {
                // Assuming that if there are any non-existant files, that this will kick off.
                final IplantErrorStrings errorStrings = I18N.ERROR;
                SimpleServiceError serviceError = AutoBeanCodex.decode(drServiceFacade.getDiskResourceFactory(), SimpleServiceError.class, caught.getMessage()).as();
                if (serviceError.getErrorCode().equals(ServiceErrorCode.ERR_DOES_NOT_EXIST.toString())) {
                    String reason = serviceError.getReason();
                    GWT.log("The Reason: " + reason);
                    List<String> errorMessageValues = Lists.newArrayList(); 
                    String drErrList = DiskResourceUtil.asCommaSeperatedNameList(errorMessageValues);
                    DefaultEditorError existsErr = new DefaultEditorError(MultiFileSelectorField.this, errorStrings.diskResourceDoesNotExist(drErrList), null);
                    existsErrors.add(existsErr);
                    errors.add(existsErr);
                    ValueChangeEvent.fire(MultiFileSelectorField.this, value);
                }
            }

            @Override
            public void onSuccess(DiskResourceStatMap result) {
                if (result.getMap().isEmpty()) {
                    return;
                }

                Set<Entry<String, DiskResource>> entrySet = result.getMap().entrySet();
                for (Entry<String, DiskResource> entry : entrySet) {
                    DiskResource entryValue = entry.getValue();
                    DefaultEditorError permError = new DefaultEditorError(MultiFileSelectorField.this, I18N.DISPLAY.permissionSelectErrorMessage(), entryValue.getId());
                    if (validatePermissions) {
                        if (entryValue == null) {
                            permissionErrors.add(permError);
                            errors.add(permError);
                            errorSupport.markInvalid(errors);
                        } else if (!(entryValue.getPermissions().isWritable() || entryValue.getPermissions().isOwner())) {
                            permissionErrors.add(permError);
                            errors.add(permError);
                            errorSupport.markInvalid(errors);
                        }
                    } else if (listStore.findModelWithKey(entry.getKey()) == null) {
                        // JDS Add the items which are valid.
                        entryValue.setId(entry.getKey());
                        entryValue.setName(DiskResourceUtil.parseNameFromPath(entry.getKey()));
                        listStore.add(entryValue);
                    }
                }
            }
        });
    }

    private void initDragAndDrop() {
        DropTarget dataDrop = new DropTarget(this);
        dataDrop.setOperation(Operation.COPY);
        dataDrop.addDragEnterHandler(this);
        dataDrop.addDragMoveHandler(this);
        dataDrop.addDropHandler(this);
    }
}
