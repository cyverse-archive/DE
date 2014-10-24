package org.iplantc.de.pipelines.client.views;

import org.iplantc.de.client.models.pipelines.PipelineTask;
import org.iplantc.de.resources.client.messages.I18N;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A ColumnModel for the "Select & Order Apps" grid.
 * 
 * @author psarando
 * 
 */
public class AppColumnModel extends ColumnModel<PipelineTask> {

    public AppColumnModel(PipelineAppProperties props) {
        super(createColumnConfigList(props));
    }

    public static List<ColumnConfig<PipelineTask, ?>>
            createColumnConfigList(PipelineAppProperties props) {
        ColumnConfig<PipelineTask, Integer> step = new ColumnConfig<PipelineTask, Integer>(new ValueProvider<PipelineTask, Integer>() {

                                                                                               @Override
                                                                                               public Integer
                                                                                                       getValue(PipelineTask object) {
                                                                                                   return object.getStep() + 1;
                                                                                               }

                                                                                               @Override
                                                                                               public void
                                                                                                       setValue(PipelineTask object,
                                                                                                                Integer value) {
                                                                                                   object.setStep(value);

                                                                                               }

                                                                                               @Override
                                                                                               public String
                                                                                                       getPath() {
                                                                                                   // TODO
                                                                                                   // Auto-generated
                                                                                                   // method
                                                                                                   // stub
                                                                                                   return null;
                                                                                               }
                                                                                           },
                                                                                           50,
                                                                                           I18N.DISPLAY.step());
        step.setFixed(true);
        step.setHideable(false);
        step.setSortable(false);
        step.setMenuDisabled(true);

        ColumnConfig<PipelineTask, String> name = new ColumnConfig<PipelineTask, String>(props.name(),
                                                                                         180,
                                                                                         I18N.DISPLAY.name());
        name.setSortable(false);

        ColumnConfig<PipelineTask, String> description = new ColumnConfig<PipelineTask, String>(props.description(),
                                                                                                180,
                                                                                                I18N.DISPLAY.description());
        description.setSortable(false);

        List<ColumnConfig<PipelineTask, ?>> list = new ArrayList<ColumnConfig<PipelineTask, ?>>();
        list.add(step);
        list.add(name);
        list.add(description);

        return list;
    }
}
