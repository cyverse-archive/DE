package org.iplantc.de.analysis.client.views;

import org.iplantc.de.analysis.client.events.AnalysisAppSelectedEvent;
import org.iplantc.de.analysis.client.events.AnalysisCommentSelectedEvent;
import org.iplantc.de.analysis.client.events.AnalysisNameSelectedEvent;
import org.iplantc.de.analysis.client.views.cells.*;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.views.windows.AnalysisProperties;

import com.google.common.collect.Lists;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import java.util.List;

public class AnalysisColumnModel extends ColumnModel<Analysis> implements AnalysisNameSelectedEvent.HasAnalysisNameSelectedEventHandlers, AnalysisAppSelectedEvent.HasAnalysisAppSelectedEventHandlers, AnalysisCommentSelectedEvent.HasAnalysisCommentSelectedEventHandlers {

    @Inject
    public AnalysisColumnModel(final AnalysisProperties props, final CheckBoxSelectionModel<Analysis> checkBoxSelectionModel) {
        super(createColumnConfigList(props, checkBoxSelectionModel));

        // Set handler managers on appropriate cells so they can fire events.
        for(ColumnConfig<Analysis,?> cc : configs){
            if(cc.getCell() instanceof AnalysisNameCell){
                ((AnalysisNameCell)cc.getCell()).setHasHandlers(ensureHandlers());
            } else if(cc.getCell() instanceof AnalysisAppNameCell){
                ((AnalysisAppNameCell)cc.getCell()).setHasHandlers(ensureHandlers());
            } else if(cc.getCell() instanceof AnalysisCommentCell){
                ((AnalysisCommentCell)cc.getCell()).setHasHandlers(ensureHandlers());
            }
        }
    }

    public static List<ColumnConfig<Analysis, ?>> createColumnConfigList(AnalysisProperties props, CheckBoxSelectionModel<Analysis> checkBoxSelectionModel){

        IdentityValueProvider<Analysis> valueProvider = new IdentityValueProvider<Analysis>();

        ColumnConfig<Analysis, Analysis> colCheckBox = checkBoxSelectionModel.getColumn();
        ColumnConfig<Analysis, Analysis> name = new ColumnConfig<Analysis, Analysis>(valueProvider, 100);
        ColumnConfig<Analysis, Analysis> comment = new ColumnConfig<Analysis, Analysis>(valueProvider, 30);
        ColumnConfig<Analysis, Analysis> app = new ColumnConfig<Analysis, Analysis>(valueProvider, 100);
        ColumnConfig<Analysis, Analysis> startDate = new ColumnConfig<Analysis, Analysis>(valueProvider, 150);
        ColumnConfig<Analysis, Analysis> endDate = new ColumnConfig<Analysis, Analysis>(valueProvider, 150);
        ColumnConfig<Analysis, String> status = new ColumnConfig<Analysis, String>(props.status(), 100);


        name.setHeader(org.iplantc.de.resources.client.messages.I18N.DISPLAY.name());
        name.setMenuDisabled(true);
        name.setCell(new AnalysisNameCell());

        comment.setMenuDisabled(true);
        comment.setCell(new AnalysisCommentCell());

        app.setHeader(org.iplantc.de.resources.client.messages.I18N.DISPLAY.appName());
        app.setMenuDisabled(true);
        app.setCell(new AnalysisAppNameCell());

        startDate.setCell(new StartDateTimeCell());
        startDate.setHeader(org.iplantc.de.resources.client.messages.I18N.DISPLAY.startDate());

        endDate.setCell(new EndDateTimeCell());
        endDate.setHeader(org.iplantc.de.resources.client.messages.I18N.DISPLAY.endDate());

        status.setHeader(org.iplantc.de.resources.client.messages.I18N.DISPLAY.status());
        status.setMenuDisabled(true);

        List<ColumnConfig<Analysis, ?>> ret = Lists.newArrayList();
        ret.add(colCheckBox);
        ret.add(name);
        ret.add(comment);
        ret.add(app);
        ret.add(startDate);
        ret.add(endDate);
        ret.add(status);
        return ret;
    }

    @Override
    public HandlerRegistration addAnalysisAppSelectedEventHandler(AnalysisAppSelectedEvent.AnalysisAppSelectedEventHandler handler) {
        return ensureHandlers().addHandler(AnalysisAppSelectedEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration addAnalysisCommentSelectedEventHandler(AnalysisCommentSelectedEvent.AnalysisCommentSelectedEventHandler handler) {
        return ensureHandlers().addHandler(AnalysisCommentSelectedEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration addAnalysisNameSelectedEventHandler(AnalysisNameSelectedEvent.AnalysisNameSelectedEventHandler handler) {
        return ensureHandlers().addHandler(AnalysisNameSelectedEvent.TYPE, handler);
    }
}
