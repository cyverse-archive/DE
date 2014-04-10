package org.iplantc.de.analysis.client.views;

import org.iplantc.de.analysis.client.presenter.AnalysisParameterProperties;
import org.iplantc.de.analysis.client.views.cells.AnalysisParamNameCell;
import org.iplantc.de.analysis.client.views.cells.AnalysisParamValueCell;
import org.iplantc.de.client.models.analysis.AnalysisParameter;
import org.iplantc.de.client.models.apps.integration.ArgumentType;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.inject.Inject;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnalysisParamViewColumnModel extends ColumnModel<AnalysisParameter> {

    @Inject
    public AnalysisParamViewColumnModel(AnalysisParameterProperties props, IplantDisplayStrings displayStrings) {
        super(createColumnConfigList(props, displayStrings));
    }

    public static List<ColumnConfig<AnalysisParameter, ?>> createColumnConfigList(AnalysisParameterProperties props, IplantDisplayStrings displayStrings){


        ColumnConfig<AnalysisParameter, AnalysisParameter> param_name = new ColumnConfig<AnalysisParameter, AnalysisParameter>( new IdentityValueProvider<AnalysisParameter>(), 175);
        ColumnConfig<AnalysisParameter, ArgumentType> param_type = new ColumnConfig<AnalysisParameter, ArgumentType>( props.type(), 75);
        ColumnConfig<AnalysisParameter, AnalysisParameter> param_value = new ColumnConfig<AnalysisParameter, AnalysisParameter>( new IdentityValueProvider<AnalysisParameter>(), 325);

        param_name.setHeader(displayStrings.paramName());
        param_name.setCell(new AnalysisParamNameCell());

        param_type.setHeader(displayStrings.paramType());

        param_value.setHeader(displayStrings.paramValue());
        param_value.setCell(new AnalysisParamValueCell());

        List<ColumnConfig<AnalysisParameter, ?>> columns = new ArrayList<ColumnConfig<AnalysisParameter, ?>>();
        columns.addAll(Arrays.asList(param_name, param_type, param_value));

        return columns;
    }
}
