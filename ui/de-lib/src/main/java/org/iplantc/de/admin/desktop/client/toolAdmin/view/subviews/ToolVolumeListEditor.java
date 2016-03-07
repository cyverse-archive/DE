package org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.admin.desktop.client.toolAdmin.model.ToolVolumeProperties;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.client.models.tool.ToolAutoBeanFactory;
import org.iplantc.de.client.models.tool.ToolVolume;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.uibinder.client.UiField;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import com.sencha.gxt.data.client.editor.ListStoreEditor;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.event.CancelEditEvent;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.editing.AbstractGridEditing;
import com.sencha.gxt.widget.core.client.grid.editing.ClicksToEdit;
import com.sencha.gxt.widget.core.client.grid.editing.GridEditing;
import com.sencha.gxt.widget.core.client.grid.editing.GridRowEditing;

import java.util.ArrayList;
import java.util.List;


public class ToolVolumeListEditor extends Composite implements IsEditor<Editor<List<ToolVolume>>> {

    private GridEditing<ToolVolume> editing;
    private Grid<ToolVolume> grid;
    private static final String TOOL_VOLUME_MODEL_KEY = "model_key";
    private int unique_volume_id;
    private ListStoreEditor<ToolVolume> listStoreEditor;

    @UiField
    ListStore<ToolVolume> listStore;
    @UiField(provided = true)
    ToolAdminView.ToolAdminViewAppearance appearance;

    @Inject
    public ToolVolumeListEditor(ToolVolumeProperties toolVolumeProperties, ToolAdminView.ToolAdminViewAppearance appearance) {
        this.appearance = appearance;
        listStore = new ListStore<>(new ModelKeyProvider<ToolVolume>() {
            @Override
            public String getKey(ToolVolume item) {
                return getVolumeTag(item);
            }
        });
        listStoreEditor = new ListStoreEditor<>(listStore);
        listStore.setAutoCommit(true);

        ColumnConfig<ToolVolume, String> hostPath = new ColumnConfig<>(toolVolumeProperties.hostPath(),
                                                                       appearance.containerVolumesHostPathWidth(),
                                                                       appearance.containerVolumesHostPathLabel());
        ColumnConfig<ToolVolume, String> containerPath = new ColumnConfig<>(toolVolumeProperties.containerPath(),
                                                                            appearance.containerVolumesContainerPathWidth(),
                                                                            appearance.containerVolumesContainerPathLabel());

        List<ColumnConfig<ToolVolume, ?>> columns = new ArrayList<>();
        columns.add(hostPath);
        columns.add(containerPath);
        ColumnModel<ToolVolume> cm = new ColumnModel<>(columns);

        grid = new Grid<>(listStore, cm);
        grid.setHeight(100);

        editing = new GridRowEditing<>(grid);
        enableGridEditing(hostPath, containerPath);
        editing.addCancelEditHandler(getCancelHandler());
        ((AbstractGridEditing<ToolVolume>)editing).setClicksToEdit(ClicksToEdit.TWO);

        initWidget(grid);
    }

    private CancelEditEvent.CancelEditHandler<ToolVolume> getCancelHandler() {
        return new CancelEditEvent.CancelEditHandler<ToolVolume>() {
            @Override
            public void onCancelEdit(CancelEditEvent<ToolVolume> event) {
                int cancelRow = event.getEditCell().getRow();
                if (listStore.get(cancelRow).getHostPath() == null &&
                    listStore.get(cancelRow).getContainerPath() == null) {
                    listStore.remove(cancelRow);
                }
            }
        };
    }

    private void enableGridEditing(ColumnConfig<ToolVolume, String> hostPath,
                                   ColumnConfig<ToolVolume, String> containerPath) {
        TextField hostPathTextField = new TextField();
        hostPathTextField.setAllowBlank(false);
        editing.addEditor(hostPath, hostPathTextField);

        TextField containerPathTextField = new TextField();
        containerPathTextField.setAllowBlank(false);
        editing.addEditor(containerPath, containerPathTextField);
    }

    @Override
    public Editor<List<ToolVolume>> asEditor() {
        return listStoreEditor;
    }

    public void addVolume() {
        ToolAutoBeanFactory factory = GWT.create(ToolAutoBeanFactory.class);
        ToolVolume volume = factory.getVolume().as();
        getVolumeTag(volume);

        editing.cancelEditing();
        listStore.add(0, volume);
        int row = listStore.indexOf(volume);
        editing.startEditing(new Grid.GridCell(row, 0));
    }


    private String getVolumeTag(ToolVolume volume){
        if (volume != null){
            final AutoBean<ToolVolume> volumeAutoBean = AutoBeanUtils.getAutoBean(volume);
            String currentTag = volumeAutoBean.getTag(TOOL_VOLUME_MODEL_KEY);
            if (currentTag == null){
                volumeAutoBean.setTag(TOOL_VOLUME_MODEL_KEY, String.valueOf(unique_volume_id++));
            }
            return volumeAutoBean.getTag(TOOL_VOLUME_MODEL_KEY);
        }
        return "";
    }

    public void deleteVolume() {
        ToolVolume deleteVolume = grid.getSelectionModel().getSelectedItem();
        if (deleteVolume != null) {
            listStore.remove(listStore.findModelWithKey(getVolumeTag(deleteVolume)));
        }
    }

    public boolean isValid(){
        for (ToolVolume volume : listStore.getAll()){
            if (Strings.isNullOrEmpty(volume.getHostPath()) || Strings.isNullOrEmpty(volume.getContainerPath())){
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        grid.ensureDebugId(baseID + Belphegor.ToolAdminIds.CONTAINER_VOLUMES_GRID);
    }
}
