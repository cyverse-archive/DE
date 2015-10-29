package org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.client.models.tool.ToolAutoBeanFactory;
import org.iplantc.de.client.models.tool.ToolDevice;

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


public class ToolDeviceListEditor extends Composite implements IsEditor<Editor<List<ToolDevice>>> {

    private GridEditing<ToolDevice> editing;
    private Grid<ToolDevice> grid;
    private static final String TOOL_DEVICE_MODEL_KEY = "model_key";
    private int unique_device_id;


    @UiField ListStore<ToolDevice> listStore;
    @UiField(provided = true)
    ToolAdminView.ToolAdminViewAppearance appearance =
            GWT.create(ToolAdminView.ToolAdminViewAppearance.class);


    interface ToolDeviceProperties extends PropertyAccess<ToolDevice> {

        ValueProvider<ToolDevice, String> hostPath();

        ValueProvider<ToolDevice, String> containerPath();
    }

    private static final ToolDeviceProperties ToolDeviceProperties =
            GWT.create(ToolDeviceProperties.class);

    public ToolDeviceListEditor() {
        listStore = new ListStore<ToolDevice>(new ModelKeyProvider<ToolDevice>() {
            @Override
            public String getKey(ToolDevice item) {
                return getDeviceTag(item);
            }
        });

        ColumnConfig<ToolDevice, String> hostPath = new ColumnConfig<ToolDevice, String>(
                ToolDeviceProperties.hostPath(),
                appearance.containerDevicesHostPathWidth(),
                appearance.containerDevicesHostPathLabel());
        ColumnConfig<ToolDevice, String> containerPath = new ColumnConfig<ToolDevice, String>(
                ToolDeviceProperties.containerPath(),
                appearance.containerDevicesContainerPathWidth(),
                appearance.containerDevicesContainerPathLabel());

        List<ColumnConfig<ToolDevice, ?>> columns = new ArrayList<ColumnConfig<ToolDevice, ?>>();
        columns.add(hostPath);
        columns.add(containerPath);
        ColumnModel<ToolDevice> cm = new ColumnModel<ToolDevice>(columns);

        grid = new Grid<ToolDevice>(listStore, cm);

        editing = new GridInlineEditing<ToolDevice>(grid);
        editing.addEditor(hostPath, new TextField());
        editing.addEditor(containerPath, new TextField());

        initWidget(grid);
    }

    @Override
    public Editor<List<ToolDevice>> asEditor() {
        return new ListStoreEditor<ToolDevice>(listStore);
    }

    public void addDevice() {
        ToolAutoBeanFactory factory = GWT.create(ToolAutoBeanFactory.class);
        ToolDevice device = factory.getDevice().as();
        getDeviceTag(device);

        editing.cancelEditing();
        listStore.add(0, device);
        int row = listStore.indexOf(device);
        editing.startEditing(new Grid.GridCell(row, 0));
    }

    private String getDeviceTag(ToolDevice device){
        if (device != null){
            final AutoBean<ToolDevice> deviceAutoBean = AutoBeanUtils.getAutoBean(device);
            String currentTag = deviceAutoBean.getTag(TOOL_DEVICE_MODEL_KEY);
            if (currentTag == null){
                deviceAutoBean.setTag(TOOL_DEVICE_MODEL_KEY, String.valueOf(unique_device_id++));
            }
            return deviceAutoBean.getTag(TOOL_DEVICE_MODEL_KEY);
        }
        return "";
    }

    public void deleteDevice() {
        ToolDevice deleteDevice = grid.getSelectionModel().getSelectedItem();
        if (deleteDevice != null){
            listStore.remove(listStore.findModelWithKey(getDeviceTag(deleteDevice)));
        }
    }

    public List<ToolDevice> getDeviceList() {
        List<ToolDevice> devices = Lists.newArrayList();
        removeDisabledId(devices);
        return devices;
    }

    private void removeDisabledId(List<ToolDevice> devices) {
        for (ToolDevice device : listStore.getAll()) {
            device.setId(null);
            devices.add(device);
        }
    }
}
