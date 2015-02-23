package org.iplantc.de.analysis.client.views.parameters;

import org.iplantc.de.analysis.client.AnalysesView;
import org.iplantc.de.analysis.client.events.selection.AnalysisParamValueSelectedEvent;
import org.iplantc.de.analysis.client.models.AnalysisParameterProperties;
import org.iplantc.de.analysis.client.views.parameters.cells.AnalysisParamNameCell;
import org.iplantc.de.analysis.client.views.parameters.cells.AnalysisParamValueCell;
import org.iplantc.de.client.models.analysis.AnalysisParameter;
import org.iplantc.de.client.models.apps.integration.ArgumentType;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author jstroot
 */
public class AnalysisParamViewColumnModel extends ColumnModel<AnalysisParameter> implements AnalysisParamValueSelectedEvent.HasAnalysisParamValueSelectedEventHandlers {

    @Inject
    AnalysisParamViewColumnModel(final AnalysisParameterProperties props,
                                 final AnalysesView.Appearance appearance) {
        super(createColumnConfigList(props, appearance));
       for(ColumnConfig<AnalysisParameter, ?> cc : configs){
           if(cc.getCell() instanceof AnalysisParamValueCell){
               ((AnalysisParamValueCell)cc.getCell()).setHasHandlers(this);
           }
       }
    }

    public static List<ColumnConfig<AnalysisParameter, ?>> createColumnConfigList(final AnalysisParameterProperties props,
                                                                                  final AnalysesView.Appearance appearance){
        ColumnConfig<AnalysisParameter, String> param_name = new ColumnConfig<>( props.name(), 175);
        ColumnConfig<AnalysisParameter, ArgumentType> param_type = new ColumnConfig<>( props.type(), 75);
        ColumnConfig<AnalysisParameter, AnalysisParameter> param_value = new ColumnConfig<>( new IdentityValueProvider<AnalysisParameter>(), 325);

        param_name.setHeader(appearance.paramName());
        param_name.setCell(new AnalysisParamNameCell());

        param_type.setHeader(appearance.paramType());

        param_value.setHeader(appearance.paramValue());
        param_value.setCell(new AnalysisParamValueCell());

        List<ColumnConfig<AnalysisParameter, ?>> columns = new ArrayList<>();
        columns.addAll(Arrays.asList(param_name, param_type, param_value));

        return columns;
    }

    @Override
    public HandlerRegistration addAnalysisParamValueSelectedEventHandler(AnalysisParamValueSelectedEvent.AnalysisParamValueSelectedEventHandler handler) {
        return ensureHandlers().addHandler(AnalysisParamValueSelectedEvent.TYPE, handler);
    }

}
