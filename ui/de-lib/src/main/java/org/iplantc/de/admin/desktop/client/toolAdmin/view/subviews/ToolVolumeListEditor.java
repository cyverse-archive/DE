package org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.client.models.tool.ToolAutoBeanFactory;
import org.iplantc.de.client.models.tool.ToolVolume;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.uibinder.client.UiField;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.client.editor.ListStoreEditor;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.editing.GridEditing;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;

import java.util.ArrayList;
import java.util.List;


public class ToolVolumeListEditor extends Composite implements IsEditor<Editor<List<ToolVolume>>> {

    private GridEditing<ToolVolume> editing;
    private Grid<ToolVolume> grid;
    private static final String TOOL_VOLUME_MODEL_KEY = "model_key";
    private int unique_volume_id;

    @UiField
    ListStore<ToolVolume> listStore;
    @UiField(provided = true)
    ToolAdminView.ToolAdminViewAppearance appearance =
            GWT.create(ToolAdminView.ToolAdminViewAppearance.class);


    interface ToolVolumeProperties extends PropertyAccess<ToolVolume> {
        ValueProvider<ToolVolume, String> hostPath();

        ValueProvider<ToolVolume, String> containerPath();
    }

    private static final ToolVolumeProperties toolVolumeProperties =
            GWT.create(ToolVolumeProperties.class);

    public ToolVolumeListEditor() {
        listStore = new ListStore<ToolVolume>(new ModelKeyProvider<ToolVolume>() {
            @Override
            public String getKey(ToolVolume item) {
                return getVolumeTag(item);
            }
        });

        ColumnConfig<ToolVolume, String> hostPath = new ColumnConfig<ToolVolume, String>(
                toolVolumeProperties.hostPath(),
                appearance.containerVolumesHostPathWidth(),
                appearance.containerVolumesHostPathLabel());
        ColumnConfig<ToolVolume, String> containerPath = new ColumnConfig<ToolVolume, String>(
                toolVolumeProperties.containerPath(),
                appearance.containerVolumesContainerPathWidth(),
                appearance.containerVolumesContainerPathLabel());

        List<ColumnConfig<ToolVolume, ?>> columns = new ArrayList<ColumnConfig<ToolVolume, ?>>();
        columns.add(hostPath);
        columns.add(containerPath);
        ColumnModel<ToolVolume> cm = new ColumnModel<ToolVolume>(columns);

        grid = new Grid<ToolVolume>(listStore, cm);

        editing = new GridInlineEditing<ToolVolume>(grid);
        editing.addEditor(hostPath, new TextField());
        editing.addEditor(containerPath, new TextField());

        initWidget(grid);
    }

    @Override
    public Editor<List<ToolVolume>> asEditor() {
        return new ListStoreEditor<ToolVolume>(listStore);
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


    public List<ToolVolume> getVolumeList() {
        List<ToolVolume> volumeList = Lists.newArrayList();
        removeDisabledId(volumeList);
        return volumeList;
    }

    private void removeDisabledId(List<ToolVolume> volumeList) {
        for (ToolVolume volume : listStore.getAll()) {
            volume.setId(null);
            volumeList.add(volume);
        }
    }

}
