package org.iplantc.de.analysis.client.views;

import org.iplantc.de.analysis.client.AnalysesView;
import org.iplantc.de.analysis.client.events.selection.AnalysisAppSelectedEvent;
import org.iplantc.de.analysis.client.events.selection.AnalysisCommentSelectedEvent;
import org.iplantc.de.analysis.client.events.selection.AnalysisNameSelectedEvent;
import org.iplantc.de.analysis.client.events.HTAnalysisExpandEvent;
import org.iplantc.de.analysis.client.views.cells.AnalysisAppNameCell;
import org.iplantc.de.analysis.client.views.cells.AnalysisCommentCell;
import org.iplantc.de.analysis.client.views.cells.AnalysisNameCell;
import org.iplantc.de.analysis.client.views.cells.EndDateTimeCell;
import org.iplantc.de.analysis.client.views.cells.StartDateTimeCell;
import org.iplantc.de.client.models.analysis.Analysis;

import com.google.common.collect.Lists;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import java.util.List;

/**
 * @author jstroot
 */
public class AnalysisColumnModel extends ColumnModel<Analysis> implements AnalysisNameSelectedEvent.HasAnalysisNameSelectedEventHandlers,
                                                                          AnalysisAppSelectedEvent.HasAnalysisAppSelectedEventHandlers,
                                                                          AnalysisCommentSelectedEvent.HasAnalysisCommentSelectedEventHandlers,
                                                                          HTAnalysisExpandEvent.HasHTAnalysisExpandEventHandlers {

    @Inject
    AnalysisColumnModel(final CheckBoxSelectionModel<Analysis> checkBoxSelectionModel,
                        final AnalysesView.Appearance appearance) {
        super(createColumnConfigList(checkBoxSelectionModel, appearance));

        // Set handler managers on appropriate cells so they can fire events.
        for (ColumnConfig<Analysis, ?> cc : configs) {
            if (cc.getCell() instanceof AnalysisNameCell) {
                ((AnalysisNameCell)cc.getCell()).setHasHandlers(ensureHandlers());
            } else if (cc.getCell() instanceof AnalysisAppNameCell) {
                ((AnalysisAppNameCell)cc.getCell()).setHasHandlers(ensureHandlers());
            } else if (cc.getCell() instanceof AnalysisCommentCell) {
                ((AnalysisCommentCell)cc.getCell()).setHasHandlers(ensureHandlers());
            }
        }
    }

    public static List<ColumnConfig<Analysis, ?>> createColumnConfigList(final CheckBoxSelectionModel<Analysis> checkBoxSelectionModel,
                                                                         final AnalysesView.Appearance appearance) {
        ColumnConfig<Analysis, Analysis> colCheckBox = checkBoxSelectionModel.getColumn();
        ColumnConfig<Analysis, Analysis> name = new ColumnConfig<>(new IdentityValueProvider<Analysis>("name"),
                                                                                     150);
        ColumnConfig<Analysis, Analysis> comment = new ColumnConfig<>(new IdentityValueProvider<Analysis>("description"), 30);
        ColumnConfig<Analysis, Analysis> app = new ColumnConfig<>(new IdentityValueProvider<Analysis>("app_name"), 100);
        ColumnConfig<Analysis, Analysis> startDate = new ColumnConfig<>(new IdentityValueProvider<Analysis>("startdate"), 125);
        ColumnConfig<Analysis, Analysis> endDate = new ColumnConfig<>(new IdentityValueProvider<Analysis>("enddate"), 125);
        ColumnConfig<Analysis, String> status = new ColumnConfig<>(new ValueProvider<Analysis, String>() {

            @Override
            public String getValue(Analysis object) {
                return object.getStatus();
            }

            @Override
            public void setValue(Analysis object, String value) {
                object.setStatus(value);
            }

            @Override
            public String getPath() {
                return "status";
            }
        }, 75);

        name.setHeader(appearance.name());
        name.setCell(new AnalysisNameCell());

        comment.setMenuDisabled(true);
        comment.setCell(new AnalysisCommentCell());
        comment.setSortable(false);
        comment.setHeader("");
        comment.setHideable(false);

        app.setHeader(appearance.appName());
        app.setCell(new AnalysisAppNameCell());

        startDate.setCell(new StartDateTimeCell());
        startDate.setHeader(appearance.startDate());

        endDate.setCell(new EndDateTimeCell());
        endDate.setHeader(appearance.endDate());

        status.setHeader(appearance.status());

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

    @Override
    public HandlerRegistration addHTAnalysisExpandEventHandler(HTAnalysisExpandEvent.HTAnalysisExpandEventHandler handler) {
        return ensureHandlers().addHandler(HTAnalysisExpandEvent.TYPE, handler);
    }
}
